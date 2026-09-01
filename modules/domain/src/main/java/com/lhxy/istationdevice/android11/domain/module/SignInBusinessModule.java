package com.lhxy.istationdevice.android11.domain.module;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.DeviceMode;
import com.lhxy.istationdevice.android11.deviceapi.RfidAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.domain.ProtocolReplayUseCase;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.dispatch.DvrSerialDispatchUseCase;
import com.lhxy.istationdevice.android11.domain.module.state.SignInState;
import com.lhxy.istationdevice.android11.protocol.jt808.Cc808LegacyMessages;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808Frame;

/**
 * 签到模块
 * <p>
 * 先把 RFID 读取和司机考勤样例绑在一起，后面再补真正的签到签退状态机。
 * <p>
 * 查找关键字：RFID 刷卡、签到签退、司机考勤、DVR 考勤帧。
 */
public final class SignInBusinessModule extends AbstractTerminalBusinessModule {
    private static final String TAG = "SignInBusinessModule";
    private static final long AUTO_POLL_INTERVAL_MS = 400L;
    private static final long WAIT_CARD_REMOVED_TIMEOUT_MS = 5000L;
    private static final long WAIT_CARD_REMOVED_POLL_MS = 150L;

    private final ProtocolReplayUseCase protocolReplayUseCase;
    private final SocketClientAdapter socketClientAdapter;
    private final RfidAdapter rfidAdapter;
    private final DvrSerialDispatchUseCase dvrSerialDispatchUseCase;
    private final Cc808LegacyMessages cc808Messages = new Cc808LegacyMessages();
    private final Object signInLock = new Object();
    private final Object autoPollLock = new Object();
    private final long autoPollIntervalMs;
    private final long waitCardRemovedTimeoutMs;
    private final long waitCardRemovedPollMs;
    private int lastAttendanceReplayCount;
    private final SignInState signInState = new SignInState();
    private volatile boolean autoPolling;
    private volatile Thread autoPollThread;
    private int exclusiveRfidHoldCount;

    public SignInBusinessModule(
            ProtocolReplayUseCase protocolReplayUseCase,
            SocketClientAdapter socketClientAdapter,
            RfidAdapter rfidAdapter,
            DvrSerialDispatchUseCase dvrSerialDispatchUseCase
    ) {
        this(
                protocolReplayUseCase,
                socketClientAdapter,
                rfidAdapter,
                dvrSerialDispatchUseCase,
                AUTO_POLL_INTERVAL_MS,
                WAIT_CARD_REMOVED_TIMEOUT_MS,
                WAIT_CARD_REMOVED_POLL_MS
        );
    }

    SignInBusinessModule(
            ProtocolReplayUseCase protocolReplayUseCase,
            SocketClientAdapter socketClientAdapter,
            RfidAdapter rfidAdapter,
            DvrSerialDispatchUseCase dvrSerialDispatchUseCase,
            long autoPollIntervalMs,
            long waitCardRemovedTimeoutMs,
            long waitCardRemovedPollMs
    ) {
        this.protocolReplayUseCase = protocolReplayUseCase;
        this.socketClientAdapter = socketClientAdapter;
        this.rfidAdapter = rfidAdapter;
        this.dvrSerialDispatchUseCase = dvrSerialDispatchUseCase;
        this.autoPollIntervalMs = Math.max(50L, autoPollIntervalMs);
        this.waitCardRemovedTimeoutMs = Math.max(0L, waitCardRemovedTimeoutMs);
        this.waitCardRemovedPollMs = Math.max(50L, waitCardRemovedPollMs);
    }

    @Override
    public String getKey() {
        return "signin";
    }

    @Override
    public String getTitle() {
        return "签到";
    }

    @Override
    public String describePurpose() {
        return "承接 RFID 刷卡、司机考勤和后续签到签退业务。";
    }

    public SignInState getSignInState() {
        return signInState;
    }

    @Override
    public String describeStatus() {
        try {
            ShellConfig shellConfig = requireShellConfig();
            if (dvrSerialDispatchUseCase.canUse(shellConfig)) {
                return "RFID=" + (rfidAdapter.isAvailable() ? "可读" : "未就绪")
                        + " / autoPoll=" + (autoPolling ? "运行中" : "已停止")
                        + "\n- 调度考勤主链 -> RS232-1/DVR"
                        + "\n- " + signInState.describe()
                        + "\n- replayCount=" + lastAttendanceReplayCount
                        + "\n- " + describeActionMemory();
            }
            ShellConfig.SocketChannel jt808 = shellConfig.requireSocketChannel(shellConfig.getDebugReplay().getJt808SocketKey());
            return "RFID=" + (rfidAdapter.isAvailable() ? "可读" : "未就绪")
                    + " / autoPoll=" + (autoPolling ? "运行中" : "已停止")
                    + "\n- 调度上报码通道 -> " + jt808.getKey()
                    + " / connected=" + yesNo(socketClientAdapter.isConnected(jt808.getChannelName()))
                    + "\n- " + signInState.describe()
                    + "\n- replayCount=" + lastAttendanceReplayCount
                    + "\n- " + describeActionMemory();
        } catch (Exception e) {
            return "当前还没拿到完整签到配置: " + emptyAsDash(e.getMessage());
        }
    }

    @Override
    public ModuleRunResult runSample(String traceId) {
        return handleReadAndReplay(traceId, true);
    }

    @Override
    protected void onContextUpdated() {
        ShellConfig shellConfig = requireShellConfig();
        if (shouldAutoPoll(shellConfig) && !isAutoPollingSuspended()) {
            startAutoPolling();
            return;
        }
        stopAutoPolling("signin-auto-poll-stop");
    }

    /**
     * 调度中心刷卡测试页需要独占 RFID 时，先暂停后台签到轮询，避免两个线程抢同一张卡。
     */
    public void pauseAutoPollingForExclusiveRfid(String traceId) {
        synchronized (autoPollLock) {
            exclusiveRfidHoldCount++;
        }
        stopAutoPolling(traceId);
        safeLog(LogCategory.BIZ, LogLevel.INFO, "RFID auto poll paused for exclusive reader", traceId);
    }

    /**
     * 释放 RFID 独占测试占用，并按当前配置恢复后台签到轮询。
     */
    public void resumeAutoPollingAfterExclusiveRfid(String traceId) {
        boolean shouldResume;
        synchronized (autoPollLock) {
            if (exclusiveRfidHoldCount > 0) {
                exclusiveRfidHoldCount--;
            }
            shouldResume = exclusiveRfidHoldCount == 0;
        }
        if (!shouldResume) {
            return;
        }
        try {
            ShellConfig shellConfig = requireShellConfig();
            if (shouldAutoPoll(shellConfig)) {
                startAutoPolling();
            }
        } catch (Exception e) {
            safeLog(LogCategory.ERROR, LogLevel.WARN, "RFID auto poll resume skipped: " + emptyAsDash(e.getMessage()), traceId);
        }
    }

    /**
     * 签到模块动作总入口。
     */
    @Override
    public ModuleRunResult runAction(String actionKey, String traceId) {
        if ("read_card".equals(actionKey)) {
            return handleReadAndReplay(traceId, false);
        }
        if ("manual_sign_out".equals(actionKey)) {
            signInState.manualSignOut();
            return sendAttendanceAfterStateChange(traceId, "已手动签退", "当前司机状态已切到签退");
        }
        return unsupportedAction(actionKey);
    }

    /**
     * 读取卡号，并按当前链路决定是回放考勤样例还是发 DVR 考勤帧。
     */
    private ModuleRunResult handleReadAndReplay(String traceId, boolean replayAttendance) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            String cardNo = null;
            if (rfidAdapter.isAvailable()) {
                cardNo = rfidAdapter.readCard(traceId + "-rfid");
            }
            if (cardNo == null || cardNo.trim().isEmpty()) {
                if (!replayAttendance) {
                    return failure("读取卡号失败", new IllegalStateException("未读取到有效卡号"));
                }
                cardNo = "DRIVER0001";
            }
            ProcessReadCardResult result = processReadCard(shellConfig, cardNo, replayAttendance, traceId);
            if (result.sentDvrAttendance) {
                return success(
                        replayAttendance ? "已执行签到主链并发送 DVR 考勤帧" : "已读取卡号并发送 DVR 考勤帧",
                        result.detail + " / RS232-1"
                );
            }
            if (replayAttendance) {
                return success("已回放签到样例 " + result.replayCount + " 条", result.detail);
            }
            return success("已读取一次卡号", result.detail);
        } catch (Exception e) {
            return failure("签到样例执行失败", e);
        }
    }

    /**
     * 在司机状态变化后补发一次考勤信息。
     */
    private ModuleRunResult sendAttendanceAfterStateChange(String traceId, String successSummary, String successDetail) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            if (isCc808(shellConfig)) {
                sendCc808Attendance(shellConfig, traceId + "-socket-attendance");
                return success(successSummary, successDetail + "，并已发送 CC808 0x0B05 考勤帧");
            }
            if (dvrSerialDispatchUseCase.canUse(shellConfig)) {
                dvrSerialDispatchUseCase.sendDriverAttendance(
                        shellConfig,
                        shellConfig.getBasicSetupConfig().getResourceImportSettings().getLineName(),
                        signInState,
                        traceId + "-serial-attendance"
                );
                return success(successSummary, successDetail + "，并已发送 DVR 考勤帧");
            }
            return success(successSummary, successDetail);
        } catch (Exception e) {
            return failure("司机考勤发送失败", e);
        }
    }

    private ProcessReadCardResult processReadCard(
            ShellConfig shellConfig,
            String cardNo,
            boolean replayAttendance,
            String traceId
    ) {
        synchronized (signInLock) {
            signInState.applyCard(cardNo);
            if (isCc808(shellConfig)) {
                lastAttendanceReplayCount = 0;
                sendCc808Attendance(shellConfig, traceId + "-socket-attendance");
                return new ProcessReadCardResult(true, 0, detailText() + " / CC808 0x0B05");
            }
            if (dvrSerialDispatchUseCase.canUse(shellConfig)) {
                lastAttendanceReplayCount = 0;
                dvrSerialDispatchUseCase.sendDriverAttendance(
                        shellConfig,
                        shellConfig.getBasicSetupConfig().getResourceImportSettings().getLineName(),
                        signInState,
                        traceId + "-serial-attendance"
                );
                return new ProcessReadCardResult(true, 0, detailText());
            }
            int replayCount = replayAttendance ? protocolReplayUseCase.replaySignInDemo(socketClientAdapter, shellConfig, traceId) : 0;
            if (replayAttendance) {
                lastAttendanceReplayCount = replayCount;
            }
            return new ProcessReadCardResult(false, replayCount, detailText());
        }
    }

    private String detailText() {
        return "RFID 卡号=" + signInState.getCardNo() + " / " + signInState.getAttendanceMode();
    }

    private boolean isCc808(ShellConfig shellConfig) {
        return shellConfig != null
                && "CC808".equalsIgnoreCase(shellConfig.getBasicSetupConfig().getNetworkSettings().getDispatchProtocol());
    }

    private void sendCc808Attendance(ShellConfig shellConfig, String traceId) {
        ShellConfig.SocketChannel channel = shellConfig.requireSocketChannel(
                shellConfig.getDebugReplay().getJt808SocketKey()
        );
        if (!socketClientAdapter.isConnected(channel.getChannelName())) {
            socketClientAdapter.connect(channel.toSocketEndpointConfig(), traceId + "-connect");
        }
        String lineName = shellConfig.getBasicSetupConfig().getResourceImportSettings().getLineName();
        int lineNumber = parseLineNumber(lineName);
        int driverStatus = signInState.isSignedIn() ? 0 : 1;
        Jt808Frame frame = cc808Messages.createDriverAttendance(
                shellConfig.getBasicSetupConfig().getNetworkSettings().getDispatchId(),
                lineNumber,
                signInState.getCardNo(),
                compactNowTime(),
                driverStatus,
                0
        );
        socketClientAdapter.send(channel.getChannelName(), cc808Messages.encode(frame), traceId + "-send");
        safeLog(LogCategory.BIZ, LogLevel.INFO,
                "CC808 driver attendance sent line=" + lineNumber + " / status=" + driverStatus, traceId);
    }

    private int parseLineNumber(String lineName) {
        if (lineName == null) {
            return 0;
        }
        String digits = lineName.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException ignore) {
            return 0;
        }
    }

    private String compactNowTime() {
        return new java.text.SimpleDateFormat("yyMMddHHmmss", java.util.Locale.getDefault())
                .format(new java.util.Date());
    }

    private boolean shouldAutoPoll(ShellConfig shellConfig) {
        return shellConfig != null
                && shellConfig.getRfidConfig() != null
                && shellConfig.getRfidConfig().getMode() == DeviceMode.REAL
                && rfidAdapter.isAvailable();
    }

    private boolean isAutoPollingSuspended() {
        synchronized (autoPollLock) {
            return exclusiveRfidHoldCount > 0;
        }
    }

    private void startAutoPolling() {
        synchronized (autoPollLock) {
            if (autoPolling && autoPollThread != null && autoPollThread.isAlive()) {
                return;
            }
            autoPolling = true;
            Thread thread = new Thread(this::runAutoPollLoop, "signin-rfid-poll");
            thread.setDaemon(true);
            autoPollThread = thread;
            thread.start();
        }
        safeLog(LogCategory.BIZ, LogLevel.INFO, "RFID auto poll started", "signin-auto-poll");
    }

    private void stopAutoPolling(String traceId) {
        Thread thread;
        synchronized (autoPollLock) {
            autoPolling = false;
            thread = autoPollThread;
            autoPollThread = null;
        }
        if (thread != null) {
            thread.interrupt();
        }
        safeLog(LogCategory.BIZ, LogLevel.INFO, "RFID auto poll stopped", traceId);
    }

    private void runAutoPollLoop() {
        Thread currentThread = Thread.currentThread();
        while (autoPolling && currentThread == autoPollThread) {
            String traceId = "signin-auto-poll-" + System.currentTimeMillis();
            try {
                pollCardOnce(traceId);
            } catch (Throwable throwable) {
                safeLog(LogCategory.ERROR, LogLevel.WARN, "RFID auto poll failed: " + emptyAsDash(throwable.getMessage()), traceId);
            }
            if (!sleepQuietly(autoPollIntervalMs) || !autoPolling || currentThread != autoPollThread) {
                break;
            }
        }
    }

    private void pollCardOnce(String traceId) {
        if (!rfidAdapter.isAvailable()) {
            return;
        }
        String cardNo = rfidAdapter.readCard(traceId + "-rfid");
        if (cardNo == null || cardNo.trim().isEmpty()) {
            return;
        }
        ShellConfig shellConfig = requireShellConfig();
        ProcessReadCardResult result = processReadCard(shellConfig, cardNo, false, traceId);
        safeLog(LogCategory.BIZ, LogLevel.INFO, "RFID auto read success / " + result.detail, traceId);
        boolean removed = rfidAdapter.waitCardRemoved(traceId + "-wait-off", waitCardRemovedTimeoutMs, waitCardRemovedPollMs);
        safeLog(LogCategory.DEVICE, LogLevel.INFO, "RFID wait card off -> " + yesNo(removed), traceId);
    }

    private boolean sleepQuietly(long delayMs) {
        try {
            Thread.sleep(delayMs);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private void safeLog(LogCategory category, LogLevel level, String message, String traceId) {
        try {
            AppLogCenter.log(category, level, TAG, message, traceId);
        } catch (RuntimeException ignore) {
            // Keep background polling alive in local tests even if logging is unavailable.
        }
    }

    private static final class ProcessReadCardResult {
        private final boolean sentDvrAttendance;
        private final int replayCount;
        private final String detail;

        private ProcessReadCardResult(boolean sentDvrAttendance, int replayCount, String detail) {
            this.sentDvrAttendance = sentDvrAttendance;
            this.replayCount = replayCount;
            this.detail = detail;
        }
    }
}
