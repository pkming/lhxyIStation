package com.lhxy.istationdevice.android11.app.media;

import android.os.Bundle;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lhxy.istationdevice.android11.app.R;
import com.lhxy.istationdevice.android11.app.common.LegacyBaseActivity;
import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LegacyHomeStatusRepository;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.core.TraceIds;
import com.lhxy.istationdevice.android11.deviceapi.DeviceMode;
import com.lhxy.istationdevice.android11.deviceapi.SocketEndpointConfig;
import com.lhxy.istationdevice.android11.deviceapi.SocketMode;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigRepository;
import com.lhxy.istationdevice.android11.protocol.intercom.IntercomPacketFactory;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

import java.nio.charset.StandardCharsets;

/**
 * 旧版语音通话页。
 * <p>
 * 工作日先走 socket stub/real 和对讲 mock 包，
 * 真机阶段再把真实语音流和麦克风权限接到同一入口。
 */
public final class LegacyVoiceCallActivity extends LegacyBaseActivity {
    private static final String VOICE_CHANNEL = "VOICE_CALL";
    private static final String IP_PHONE_STATE = "IP PHONE...";
    private static final long SHOUTING_POLL_INTERVAL_MS = 100L;
    private static final int SHOUTING_ROUTE_OUTER = 1;
    private static final int SHOUTING_ROUTE_INNER = 2;
    private static final int SHOUTING_ROUTE_BOTH = 3;
    private static final int SHOUTING_ROUTE_IDLE = 4;

    private final ShellRuntime shellRuntime = ShellRuntime.get();
    private final IntercomPacketFactory packetFactory = new IntercomPacketFactory();
    private boolean connected;
    private String activeHost = "-";
    private int activePort;
    private volatile boolean shoutingMonitorRunning;
    private volatile int lastShoutingRoute = Integer.MIN_VALUE;
    private Thread shoutingMonitorThread;

    @Override
    protected int getLayoutId() {
        return R.layout.act_voice_call;
    }

    @Override
    protected int getTitleResId() {
        return R.string.voicecall_title;
    }

    @Override
    protected void onPageReady(Bundle savedInstanceState) {
        TextView stateView = findViewById(R.id.etVoiceState);
        EditText ipInput = findViewById(R.id.etVoiceIP);
        Button actionButton = findViewById(R.id.butCollectionOperation);
        ensureRuntimeConfig();
        bindDefaultHost(ipInput);
        if (stateView != null) {
            stateView.setText(R.string.legacy_voice_idle_state);
        }
        if (actionButton != null) {
            actionButton.setOnClickListener(v -> {
                if (connected) {
                    disconnectVoice(stateView, actionButton);
                } else {
                    connectVoice(ipInput, stateView, actionButton);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        shellRuntime.getSocketClientAdapter().removeReceiveListener(VOICE_CHANNEL);
        if (connected) {
            shellRuntime.getSocketClientAdapter().disconnect(VOICE_CHANNEL, TraceIds.next("legacy-voice-destroy"));
        }
        stopShoutingMonitor();
        applyShoutingMicState(false);
        LegacyHomeStatusRepository.clearShouting(this);
        super.onDestroy();
    }

    private void ensureRuntimeConfig() {
        if (shellRuntime.getActiveConfig() != null) {
            return;
        }
        shellRuntime.applyConfig(this, ShellConfigRepository.get(this));
    }

    private void bindDefaultHost(EditText ipInput) {
        if (ipInput == null || (ipInput.getText() != null && ipInput.getText().length() > 0)) {
            return;
        }
        ShellConfig.SocketChannel channel = resolveVoiceChannel();
        if (channel == null) {
            ipInput.setText(R.string.legacy_voice_default_ip);
            return;
        }
        ipInput.setText(channel.getHost());
    }

    private void connectVoice(EditText ipInput, TextView stateView, Button actionButton) {
        try {
            ShellConfig.SocketChannel channel = resolveVoiceChannel();
            String host = readVoiceHost(ipInput, channel);
            int port = channel == null ? 7000 : channel.getPort();
            SocketMode mode = channel == null ? SocketMode.STUB : channel.getMode();
            String traceId = TraceIds.next("legacy-voice-connect");

            shellRuntime.getSocketClientAdapter().setReceiveListener(VOICE_CHANNEL, (channelName, payload) -> runOnUiThread(() -> {
                if (stateView != null) {
                    stateView.setText(getString(
                            R.string.legacy_voice_echo_state,
                            activeHost,
                            String.valueOf(activePort),
                            String.valueOf(payload == null ? 0 : payload.length)
                    ));
                }
            }));
            shellRuntime.getSocketClientAdapter().connect(new SocketEndpointConfig(VOICE_CHANNEL, host, port, mode), traceId);

            byte[] payload = packetFactory.encode(
                    resolveTerminalId(channel),
                    1,
                    "VOICE-MOCK-CALL".getBytes(StandardCharsets.UTF_8)
            );
            shellRuntime.getSocketClientAdapter().send(VOICE_CHANNEL, payload, traceId);

            connected = true;
            activeHost = host;
            activePort = port;
            applyShoutingMicState(true);
            LegacyHomeStatusRepository.setShouting(this, IP_PHONE_STATE);
            if (stateView != null) {
                stateView.setText(getString(
                        R.string.legacy_voice_connected_state,
                        host,
                        String.valueOf(port),
                        mode.toConfigValue()
                ));
            }
            if (actionButton != null) {
                actionButton.setText(R.string.legacy_voice_disconnect_button);
            }
            AppLogCenter.log(
                    LogCategory.UI,
                    LogLevel.INFO,
                    "LegacyVoiceCallActivity",
                    "语音通话已连接，mock 对讲包已发到 " + host + ":" + port + " [" + mode.toConfigValue() + "]",
                    traceId
            );
            Toast.makeText(this, R.string.legacy_voice_connect_sent, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.ERROR,
                    "LegacyVoiceCallActivity",
                    "语音通话连接失败: " + safeMessage(e),
                    TraceIds.next("legacy-voice-connect-error")
            );
            if (stateView != null) {
                stateView.setText(getString(R.string.legacy_voice_failed_state, safeMessage(e)));
            }
            Toast.makeText(this, "语音通话连接失败: " + safeMessage(e), Toast.LENGTH_SHORT).show();
        }
    }

    private void disconnectVoice(TextView stateView, Button actionButton) {
        String traceId = TraceIds.next("legacy-voice-disconnect");
        shellRuntime.getSocketClientAdapter().removeReceiveListener(VOICE_CHANNEL);
        shellRuntime.getSocketClientAdapter().disconnect(VOICE_CHANNEL, traceId);
        connected = false;
        activeHost = "-";
        activePort = 0;
        applyShoutingMicState(false);
        LegacyHomeStatusRepository.clearShouting(this);
        if (stateView != null) {
            stateView.setText(R.string.legacy_voice_idle_state);
        }
        if (actionButton != null) {
            actionButton.setText(R.string.voice_call_connect_but);
        }
        AppLogCenter.log(
                LogCategory.UI,
                LogLevel.INFO,
                "LegacyVoiceCallActivity",
                "语音通话已断开",
                traceId
        );
        Toast.makeText(this, R.string.legacy_voice_disconnected_toast, Toast.LENGTH_SHORT).show();
    }

    private ShellConfig.SocketChannel resolveVoiceChannel() {
        ShellConfig config = shellRuntime.getActiveConfig();
        if (config == null) {
            return null;
        }
        try {
            return config.requireSocketChannel(config.getDebugReplay().getAl808SocketKey());
        } catch (Exception ignore) {
            for (ShellConfig.SocketChannel channel : config.getSocketChannels().values()) {
                return channel;
            }
            return null;
        }
    }

    private String readVoiceHost(EditText ipInput, ShellConfig.SocketChannel channel) {
        String value = ipInput == null || ipInput.getText() == null
                ? ""
                : ipInput.getText().toString().trim();
        if (!value.isEmpty()) {
            return value;
        }
        if (channel != null && channel.getHost() != null && !channel.getHost().trim().isEmpty()) {
            return channel.getHost().trim();
        }
        return getString(R.string.legacy_voice_default_ip);
    }

    private String resolveTerminalId(ShellConfig.SocketChannel channel) {
        ShellConfig config = shellRuntime.getActiveConfig();
        if (config != null) {
            String dispatchId = config.getBasicSetupConfig().getNetworkSettings().getDispatchId();
            if (dispatchId != null && !dispatchId.trim().isEmpty()) {
                return dispatchId.trim();
            }
        }
        if (channel != null && channel.getChannelName() != null && !channel.getChannelName().trim().isEmpty()) {
            return channel.getChannelName().replaceAll("\\D", "");
        }
        return "1";
    }

    private String safeMessage(Exception e) {
        String message = e == null ? null : e.getMessage();
        return message == null || message.trim().isEmpty() ? "未知错误" : message.trim();
    }

    private void applyShoutingMicState(boolean enabled) {
        ShellConfig shellConfig = shellRuntime.getActiveConfig();
        if (shellConfig == null) {
            return;
        }
        if (!enabled) {
            stopShoutingMonitor();
            applyHornRoute(shellConfig, SHOUTING_ROUTE_IDLE);
            return;
        }
        startShoutingMonitor();
    }

    private void startShoutingMonitor() {
        ShellConfig shellConfig = shellRuntime.getActiveConfig();
        if (shellConfig == null
                || shellConfig.getGpioConfig().getMode() != DeviceMode.REAL
                || !hasPin(shellConfig, resolveShoutingOuterKey(shellConfig))
                || !hasPin(shellConfig, resolveShoutingInnerKey(shellConfig))) {
            applyHornRoute(shellConfig, SHOUTING_ROUTE_IDLE);
            return;
        }
        stopShoutingMonitor();
        shoutingMonitorRunning = true;
        lastShoutingRoute = Integer.MIN_VALUE;
        Thread monitorThread = new Thread(() -> {
            while (shoutingMonitorRunning) {
                try {
                    pollShoutingRoute();
                } catch (Exception ignore) {
                    // GPIO 不可读时保留当前通话，等待下一轮轮询恢复。
                }
                try {
                    Thread.sleep(SHOUTING_POLL_INTERVAL_MS);
                } catch (InterruptedException ignore) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "legacy-shouting-monitor");
        monitorThread.setDaemon(true);
        shoutingMonitorThread = monitorThread;
        monitorThread.start();
    }

    private void stopShoutingMonitor() {
        shoutingMonitorRunning = false;
        Thread monitorThread = shoutingMonitorThread;
        shoutingMonitorThread = null;
        if (monitorThread != null) {
            monitorThread.interrupt();
        }
        lastShoutingRoute = Integer.MIN_VALUE;
    }

    private void pollShoutingRoute() {
        ShellConfig shellConfig = shellRuntime.getActiveConfig();
        if (shellConfig == null) {
            return;
        }
        int shoutingRoute = resolveShoutingRoute(shellConfig);
        if (shoutingRoute == lastShoutingRoute) {
            return;
        }
        lastShoutingRoute = shoutingRoute;
        applyHornRoute(shellConfig, shoutingRoute);
    }

    private int resolveShoutingRoute(ShellConfig shellConfig) {
        String outerKey = resolveShoutingOuterKey(shellConfig);
        String innerKey = resolveShoutingInnerKey(shellConfig);
        int outer = shellRuntime.getGpioAdapter().read(outerKey, TraceIds.next("legacy-voice-shouting-outer"));
        int inner = shellRuntime.getGpioAdapter().read(innerKey, TraceIds.next("legacy-voice-shouting-inner"));
        if (outer == 1 && inner == 0) {
            return SHOUTING_ROUTE_OUTER;
        }
        if (outer == 0 && inner == 1) {
            return SHOUTING_ROUTE_INNER;
        }
        if (outer == 0 && inner == 0) {
            return SHOUTING_ROUTE_BOTH;
        }
        return SHOUTING_ROUTE_IDLE;
    }

    private void applyHornRoute(ShellConfig shellConfig, int shoutingRoute) {
        if (shellConfig == null) {
            return;
        }
        boolean innerEnabled = shoutingRoute == SHOUTING_ROUTE_INNER || shoutingRoute == SHOUTING_ROUTE_BOTH;
        boolean outerEnabled = shoutingRoute == SHOUTING_ROUTE_OUTER || shoutingRoute == SHOUTING_ROUTE_BOTH;
        writePin("inner_audio", innerEnabled ? 1 : 0);
        writePin("outer_audio", outerEnabled ? 1 : 0);
        writePin("headphone_detect_power", 1);
        writePin("inner_speaker", 0);
    }

    private String resolveShoutingOuterKey(ShellConfig shellConfig) {
        if (shellConfig == null) {
            return "shouting_outer";
        }
        String configured = shellConfig.getBasicSetupConfig().getOtherSettings().getShoutingPrimaryGpioKey();
        return configured == null || configured.trim().isEmpty() ? "shouting_outer" : configured.trim();
    }

    private String resolveShoutingInnerKey(ShellConfig shellConfig) {
        if (shellConfig == null) {
            return "shouting_inner";
        }
        String configured = shellConfig.getBasicSetupConfig().getOtherSettings().getShoutingSecondaryGpioKey();
        return configured == null || configured.trim().isEmpty() ? "shouting_inner" : configured.trim();
    }

    private boolean hasPin(ShellConfig shellConfig, String pinKey) {
        return shellConfig != null
                && pinKey != null
                && !pinKey.trim().isEmpty()
                && shellConfig.getGpioConfig().getPins().containsKey(pinKey.trim());
    }

    private void writePin(String pinKey, int value) {
        try {
            shellRuntime.getGpioAdapter().write(pinKey, value, "legacy-voice-" + pinKey + "-" + value);
        } catch (Exception ignore) {
            // Keep voice-call flow available even when GPIO is not writable.
        }
    }
}
