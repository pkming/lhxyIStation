package com.lhxy.istationdevice.android11.debugtools;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.Hexs;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.devicem90.M90ManagedJhySerialPortAdapter;
import com.lhxy.istationdevice.android11.domain.passenger.JhyPassengerCounterProtocol;
import com.lhxy.istationdevice.android11.domain.passenger.JhyPassengerCounterState;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * JHY native 独立进程探测服务。
 * <p>
 * 只在 :jhy_probe 进程里加载和调用原厂 native，避免污染主进程的 shared 基线测试。
 */
public final class DebugJhyNativeProbeService extends Service {
    public static final String EXTRA_TRACE_ID = "trace_id";
    public static final String EXTRA_PORT_NAME = "port_name";
    public static final String EXTRA_BAUD_RATE = "baud_rate";
    public static final String EXTRA_RESULT_FILE = "result_file";

    private static final String TAG = "DebugJhyNativeProbe";
    private static final long OPEN_WAIT_MS = 2500L;
    private static final long RX_WAIT_MS = 5000L;

    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "debug-jhy-native-probe");
        thread.setDaemon(true);
        return thread;
    });

    public static Intent createIntent(
            Context context,
            String traceId,
            String portName,
            int baudRate,
            File resultFile
    ) {
        Intent intent = new Intent(context, DebugJhyNativeProbeService.class);
        intent.putExtra(EXTRA_TRACE_ID, traceId);
        intent.putExtra(EXTRA_PORT_NAME, portName);
        intent.putExtra(EXTRA_BAUD_RATE, baudRate);
        intent.putExtra(EXTRA_RESULT_FILE, resultFile == null ? "" : resultFile.getAbsolutePath());
        return intent;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        executor.execute(() -> {
            try {
                runProbe(intent, startId);
            } finally {
                stopSelf(startId);
            }
        });
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }

    private void runProbe(Intent intent, int startId) {
        String traceId = readStringExtra(intent, EXTRA_TRACE_ID, "jhy-native-probe-" + startId);
        String portName = readStringExtra(intent, EXTRA_PORT_NAME, "ttyS9");
        int baudRate = intent == null ? 9600 : intent.getIntExtra(EXTRA_BAUD_RATE, 9600);
        File resultFile = new File(readStringExtra(intent, EXTRA_RESULT_FILE, ""));
        ProbeResult result = new ProbeResult(traceId, portName, baudRate);

        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "AB_PHASE_NATIVE_START port=" + portName + " baud=" + baudRate, traceId);
        M90ManagedJhySerialPortAdapter adapter = new M90ManagedJhySerialPortAdapter();
        CountDownLatch receivedLatch = new CountDownLatch(1);
        result.nativeSupported = adapter.isSupported();

        try {
            if (!result.nativeSupported) {
                result.error = "JHY native library unsupported";
                AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "AB_PHASE_NATIVE_UNSUPPORTED", traceId);
                return;
            }

            adapter.open(portName, baudRate, (actualPortName, payload) -> {
                if (payload == null || payload.length == 0 || result.received) {
                    return;
                }
                result.received = true;
                result.receivedHex = Hexs.toHex(payload);
                JhyPassengerCounterState state = JhyPassengerCounterProtocol.parseCurrentCountFrame(payload);
                if (state != null) {
                    result.parsed = true;
                    result.passengerSummary = describePassengerState(state);
                }
                AppLogCenter.log(
                        LogCategory.PROTOCOL_RX,
                        LogLevel.INFO,
                        TAG,
                        "AB_PHASE_NATIVE_RX port=" + actualPortName + " hex=" + result.receivedHex + " parsed=" + result.parsed,
                        traceId
                );
                receivedLatch.countDown();
            }, traceId + "-open");

            result.open = waitUntilOpen(adapter, portName, OPEN_WAIT_MS);
            if (!result.open) {
                result.error = "native port not open within " + OPEN_WAIT_MS + "ms";
                AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "AB_PHASE_NATIVE_OPEN_TIMEOUT", traceId);
                return;
            }

            byte[] payload = JhyPassengerCounterProtocol.createCurrentCount();
            result.sentHex = Hexs.toHex(payload);
            adapter.send(portName, payload, traceId + "-send");
            result.sent = true;
            AppLogCenter.log(LogCategory.PROTOCOL_TX, LogLevel.INFO, TAG, "AB_PHASE_NATIVE_TX " + result.sentHex, traceId);

            boolean receivedInTime = receivedLatch.await(RX_WAIT_MS, TimeUnit.MILLISECONDS);
            if (!receivedInTime) {
                result.error = "native rx timeout " + RX_WAIT_MS + "ms";
                AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "AB_PHASE_NATIVE_TIMEOUT", traceId);
            }
        } catch (Throwable e) {
            result.error = e.getClass().getSimpleName() + ": " + (e.getMessage() == null ? "-" : e.getMessage());
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "AB_PHASE_NATIVE_FAILED " + result.error, traceId);
        } finally {
            try {
                adapter.close(portName, traceId + "-close");
            } catch (Throwable ignore) {
                // The probe process is isolated; preserve the original result.
            }
            result.endedAt = nowText();
            writeResult(resultFile, result);
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "AB_PHASE_NATIVE_DONE\n" + result.describe(), traceId);
        }
    }

    private boolean waitUntilOpen(M90ManagedJhySerialPortAdapter adapter, String portName, long timeoutMs) throws InterruptedException {
        long deadline = System.currentTimeMillis() + Math.max(timeoutMs, 0L);
        while (System.currentTimeMillis() < deadline) {
            if (adapter.isOpen(portName)) {
                return true;
            }
            TimeUnit.MILLISECONDS.sleep(100L);
        }
        return adapter.isOpen(portName);
    }

    private void writeResult(File resultFile, ProbeResult result) {
        if (resultFile == null || resultFile.getPath().trim().isEmpty()) {
            return;
        }
        File parent = resultFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            return;
        }
        try (FileOutputStream outputStream = new FileOutputStream(resultFile, false)) {
            outputStream.write(result.describe().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "write native probe result failed: " + e.getMessage(), result.traceId);
        }
    }

    private String readStringExtra(Intent intent, String key, String defaultValue) {
        if (intent == null) {
            return defaultValue;
        }
        String value = intent.getStringExtra(key);
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim();
    }

    private static String describePassengerState(JhyPassengerCounterState state) {
        if (state == null || !state.isAvailable()) {
            return "unavailable";
        }
        return "FIN=" + state.getFrontIn()
                + " FOUT=" + state.getFrontOut()
                + " BIN=" + state.getBackIn()
                + " BOUT=" + state.getBackOut()
                + " ALL=" + state.getTotal();
    }

    private static String nowText() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date());
    }

    private static final class ProbeResult {
        private final String traceId;
        private final String portName;
        private final int baudRate;
        private final String startedAt = nowText();
        private String endedAt = "-";
        private boolean nativeSupported;
        private boolean open;
        private boolean sent;
        private boolean received;
        private boolean parsed;
        private String sentHex = "-";
        private String receivedHex = "-";
        private String passengerSummary = "-";
        private String error = "-";

        private ProbeResult(String traceId, String portName, int baudRate) {
            this.traceId = traceId;
            this.portName = portName;
            this.baudRate = baudRate;
        }

        private String describe() {
            return "JHY native probe:"
                    + "\n- traceId=" + traceId
                    + "\n- startedAt=" + startedAt
                    + "\n- endedAt=" + endedAt
                    + "\n- port=" + portName
                    + "\n- baudRate=" + baudRate
                    + "\n- nativeSupported=" + nativeSupported
                    + "\n- open=" + open
                    + "\n- sent=" + sent
                    + "\n- sentHex=" + sentHex
                    + "\n- received=" + received
                    + "\n- receivedHex=" + receivedHex
                    + "\n- parsed=" + parsed
                    + "\n- passenger=" + passengerSummary
                    + "\n- error=" + error;
        }
    }
}
