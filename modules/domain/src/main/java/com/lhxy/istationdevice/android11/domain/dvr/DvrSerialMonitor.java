package com.lhxy.istationdevice.android11.domain.dvr;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.Hexs;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialReceiveListener;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.module.state.DispatchState;
import com.lhxy.istationdevice.android11.domain.module.state.SignInState;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * DVR 串口接收监视器。
 * <p>
 * 这里统一承接：
 * 1. 握手在线状态
 * 2. DVR 入站调度/公告/考勤关键帧
 * 3. 周末联调期需要的原始协议日志
 */
public final class DvrSerialMonitor {
    private static final String TAG = "DvrSerialMonitor";
    private static final Charset GB2312 = Charset.forName("GB2312");
    private static final int MAX_FRAME_SIZE = 4096;
    private static final int INITIAL_RAW_LOG_LIMIT = 3;
    private static final long RAW_LOG_INTERVAL_MS = 10_000L;
    private static final long OFFLINE_TIMEOUT_MS = 42_000L;
    private static final long HANDSHAKE_INTERVAL_OFFLINE_MS = 8_000L;
    private static final long HANDSHAKE_INTERVAL_ONLINE_MS = 20_000L;

    private final DispatchState dispatchState;
    private final SignInState signInState;
    private final Object bufferLock = new Object();
    private final ByteArrayOutputStream receivedBuffer = new ByteArrayOutputStream();

    private volatile String attachedChannelKey;
    private volatile String attachedPortName;
    private volatile boolean attached;
    private volatile boolean online;
    private volatile long lastHandshakeAckTimeMs;
    private volatile long lastFrameTimeMs;
    private volatile long rxFrameCount;
    private volatile long rawPacketCount;
    private volatile int lastCommand = -1;
    private volatile int lastPayloadLength;
    private volatile String lastEventSummary = "-";
    private volatile String lastRawHex = "-";
    private volatile long lastRawLogTimeMs;

    private volatile HandshakeLoop handshakeLoop;

    public DvrSerialMonitor(DispatchState dispatchState, SignInState signInState) {
        this.dispatchState = dispatchState;
        this.signInState = signInState;
    }

    public void sync(SerialPortAdapter serialPortAdapter, ShellConfig shellConfig, String traceId) {
        if (!shouldUse(shellConfig)) {
            detach(serialPortAdapter, traceId + "-detach");
            return;
        }
        ShellConfig.SerialChannel channel = shellConfig.requireSerialChannel("rs232_1");
        attach(serialPortAdapter, channel, traceId + "-attach");
    }

    public void attach(SerialPortAdapter serialPortAdapter, ShellConfig.SerialChannel serialChannel, String traceId) {
        String portName = serialChannel.getPortName();
        if (!serialPortAdapter.isOpen(portName)) {
            serialPortAdapter.open(serialChannel.toSerialPortConfig(), traceId + "-open");
        }
        boolean portChanged = attachedPortName != null && !attachedPortName.equals(portName);
        if (portChanged) {
            serialPortAdapter.removeReceiveListener(attachedPortName);
            resetBuffer();
        }
        attachedChannelKey = serialChannel.getKey();
        attachedPortName = portName;
        attached = true;
        serialPortAdapter.setReceiveListener(portName, buildListener(traceId));
        ensureHandshakeLoop(serialPortAdapter, portName, traceId);
        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.INFO,
                TAG,
                "已绑定 DVR 串口监听: " + serialChannel.getKey() + "/" + portName,
                traceId
        );
    }

    public void detach(SerialPortAdapter serialPortAdapter, String traceId) {
        if (attachedPortName != null) {
            serialPortAdapter.removeReceiveListener(attachedPortName);
        }
        stopHandshakeLoop();
        resetBuffer();
        attached = false;
        online = false;
        attachedChannelKey = null;
        attachedPortName = null;
        lastHandshakeAckTimeMs = 0L;
        lastFrameTimeMs = 0L;
        lastCommand = -1;
        lastPayloadLength = 0;
        lastEventSummary = "-";
        lastRawHex = "-";
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "已解绑 DVR 串口监听", traceId);
    }

    public boolean isAttached() {
        return attached && attachedPortName != null && !attachedPortName.trim().isEmpty();
    }

    public boolean isOnline() {
        return online;
    }

    public String describeStatus() {
        StringBuilder builder = new StringBuilder("DVR 监听:");
        builder.append("\n- attached=").append(isAttached() ? "是" : "否");
        builder.append("\n- online=").append(online ? "是" : "否");
        builder.append("\n- channel=").append(valueOrDash(attachedChannelKey)).append(" / ").append(valueOrDash(attachedPortName));
        builder.append("\n- lastCommand=").append(lastCommand < 0 ? "-" : String.format("0x%02X", lastCommand));
        builder.append("\n- lastLength=").append(lastPayloadLength);
        builder.append("\n- rxFrames=").append(rxFrameCount).append(" / rawPackets=").append(rawPacketCount);
        builder.append("\n- lastEvent=").append(valueOrDash(lastEventSummary));
        return builder.toString();
    }

    private boolean shouldUse(ShellConfig shellConfig) {
        return shellConfig != null
                && "DVR".equalsIgnoreCase(shellConfig.getBasicSetupConfig().getSerialSettings().getRs2321Protocol());
    }

    private SerialReceiveListener buildListener(String traceId) {
        return (portName, payload) -> {
            if (payload == null || payload.length == 0) {
                return;
            }
            logRawSampleIfNeeded(portName, payload, traceId);
            appendAndParse(payload, traceId);
        };
    }

    private void ensureHandshakeLoop(SerialPortAdapter serialPortAdapter, String portName, String traceId) {
        HandshakeLoop loop = handshakeLoop;
        if (loop != null && loop.matches(serialPortAdapter, portName)) {
            return;
        }
        stopHandshakeLoop();
        HandshakeLoop newLoop = new HandshakeLoop(serialPortAdapter, portName, traceId);
        handshakeLoop = newLoop;
        newLoop.start();
    }

    private void stopHandshakeLoop() {
        HandshakeLoop loop = handshakeLoop;
        handshakeLoop = null;
        if (loop != null) {
            loop.shutdown();
        }
    }

    private void appendAndParse(byte[] chunk, String traceId) {
        synchronized (bufferLock) {
            try {
                receivedBuffer.write(chunk);
            } catch (Exception ignore) {
                return;
            }
            byte[] buffer = receivedBuffer.toByteArray();
            int offset = 0;
            while (true) {
                int frameStart = findFrameStart(buffer, offset);
                if (frameStart < 0) {
                    replaceBuffer(new byte[0]);
                    return;
                }
                if (frameStart > 0) {
                    buffer = Arrays.copyOfRange(buffer, frameStart, buffer.length);
                    offset = 0;
                }
                if (buffer.length < 6) {
                    replaceBuffer(buffer);
                    return;
                }
                int payloadLength = readLittleEndianShort(buffer, 2);
                if (payloadLength < 0 || payloadLength > MAX_FRAME_SIZE) {
                    buffer = Arrays.copyOfRange(buffer, 1, buffer.length);
                    offset = 0;
                    continue;
                }
                int frameLength = payloadLength + 6;
                if (buffer.length < frameLength) {
                    replaceBuffer(buffer);
                    return;
                }
                byte[] rawFrame = Arrays.copyOfRange(buffer, 0, frameLength);
                buffer = Arrays.copyOfRange(buffer, frameLength, buffer.length);
                offset = 0;
                if ((rawFrame[frameLength - 1] & 0xFF) != 0xAA) {
                    continue;
                }
                if (!verifyChecksum(rawFrame)) {
                    AppLogCenter.log(
                            LogCategory.ERROR,
                            LogLevel.WARN,
                            TAG,
                            "DVR 帧校验失败: " + Hexs.toHex(rawFrame),
                            traceId + "-checksum"
                    );
                    continue;
                }
                byte[] payload = new byte[payloadLength];
                if (payloadLength > 0) {
                    System.arraycopy(rawFrame, 4, payload, 0, payloadLength);
                }
                handleFrame(new DvrSerialFrame(rawFrame[1] & 0xFF, payloadLength, rawFrame[frameLength - 2], payload, rawFrame), traceId);
            }
        }
    }

    private void handleFrame(DvrSerialFrame frame, String traceId) {
        lastFrameTimeMs = System.currentTimeMillis();
        rxFrameCount++;
        lastCommand = frame.getCommand();
        lastPayloadLength = frame.getPayloadLength();
        lastRawHex = Hexs.toHex(frame.getRawFrame());

        if (frame.getCommand() == 0x02 && frame.getPayloadLength() == 0) {
            markOnline(traceId + "-online");
            return;
        }

        String summary = describeFrame(frame);
        lastEventSummary = summary;
        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.INFO,
                TAG,
                summary + " / raw=" + lastRawHex,
                traceId + "-frame"
        );
    }

    private String describeFrame(DvrSerialFrame frame) {
        switch (frame.getCommand()) {
            case 0x07:
                return "DVR 收到站点信息应答";
            case 0x0C:
                return "DVR 收到报警应答";
            case 0x0E:
                return applyAttendanceAck(frame.getPayload());
            case 0x0F:
                return applyDispatchRequest(frame.getPayload());
            case 0x12:
                return applyNoticeRequest(frame.getPayload());
            default:
                return "DVR 入站未知帧 cmd=0x"
                        + twoHex(frame.getCommand())
                        + " len=" + frame.getPayloadLength();
        }
    }

    private String applyAttendanceAck(byte[] payload) {
        if (payload == null || payload.length < 40) {
            return "DVR 司机考勤应答长度不足";
        }
        long status = readIntBigEndian(payload, 0) & 0xFFFFFFFFL;
        long driverId = readIntBigEndian(payload, 4) & 0xFFFFFFFFL;
        String driverName = decodeGbk(payload, 8, Math.min(32, payload.length - 8));
        if (status >= 0) {
            signInState.applyDriverIdentity(String.valueOf(driverId), driverName);
        }
        return "DVR 司机考勤应答 status=" + status
                + " / driverId=" + driverId
                + " / driverName=" + valueOrDash(driverName);
    }

    private String applyDispatchRequest(byte[] payload) {
        if (payload == null || payload.length < 68) {
            return "DVR 调度下发长度不足";
        }
        long msgSerialNo = readIntBigEndian(payload, 0) & 0xFFFFFFFFL;
        String lineName = decodeGbk(payload, 4, 20);
        long lineGuid = readIntBigEndian(payload, 24) & 0xFFFFFFFFL;
        int direction = payload[28] & 0xFF;
        int scheduleNo = payload[29] & 0xFF;
        int timesNo = payload[30] & 0xFF;
        String departureTime = decodeGbk(payload, 64, 4);
        dispatchState.applyDvrDispatchRequest(
                msgSerialNo,
                lineGuid,
                direction,
                scheduleNo,
                timesNo,
                departureTime,
                lineName
        );
        return "DVR 调度下发 msgSn=" + msgSerialNo
                + " / line=" + valueOrDash(lineName)
                + " / direction=" + direction
                + " / scheduleNo=" + scheduleNo
                + " / timesNo=" + timesNo
                + " / departure=" + valueOrDash(departureTime);
    }

    private String applyNoticeRequest(byte[] payload) {
        if (payload == null || payload.length < 4) {
            return "DVR 公告下发长度不足";
        }
        long msgSerialNo = readIntBigEndian(payload, 0) & 0xFFFFFFFFL;
        String notice = decodeGbk(payload, 4, payload.length - 4);
        dispatchState.markNoticeReceived(notice, msgSerialNo);
        return "DVR 公告下发 msgSn=" + msgSerialNo + " / body=" + valueOrDash(notice);
    }

    private void markOnline(String traceId) {
        lastHandshakeAckTimeMs = System.currentTimeMillis();
        if (online) {
            return;
        }
        online = true;
        lastEventSummary = "DVR 握手成功";
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "DVR 在线", traceId);
    }

    private void markOffline(String traceId) {
        if (!online) {
            return;
        }
        online = false;
        lastEventSummary = "DVR 握手超时";
        AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "DVR 离线，握手超时", traceId);
    }

    private void logRawSampleIfNeeded(String portName, byte[] payload, String traceId) {
        rawPacketCount++;
        long now = System.currentTimeMillis();
        boolean initial = rawPacketCount <= INITIAL_RAW_LOG_LIMIT;
        boolean intervalReached = now - lastRawLogTimeMs >= RAW_LOG_INTERVAL_MS;
        if (!initial && !intervalReached) {
            return;
        }
        lastRawLogTimeMs = now;
        AppLogCenter.log(
                LogCategory.PROTOCOL_RX,
                LogLevel.DEBUG,
                TAG,
                "dvr raw " + portName + " packet=" + rawPacketCount
                        + " bytes=" + payload.length
                        + " hex=" + Hexs.toHex(payload),
                traceId + "-raw"
        );
    }

    private void resetBuffer() {
        synchronized (bufferLock) {
            receivedBuffer.reset();
        }
    }

    private void replaceBuffer(byte[] buffer) {
        receivedBuffer.reset();
        if (buffer != null && buffer.length > 0) {
            receivedBuffer.write(buffer, 0, buffer.length);
        }
    }

    private int findFrameStart(byte[] buffer, int offset) {
        if (buffer == null) {
            return -1;
        }
        for (int index = Math.max(0, offset); index < buffer.length; index++) {
            if ((buffer[index] & 0xFF) == 0x55) {
                return index;
            }
        }
        return -1;
    }

    private boolean verifyChecksum(byte[] frame) {
        if (frame == null || frame.length < 6) {
            return false;
        }
        int sum = 0;
        for (int index = 0; index < frame.length - 2; index++) {
            sum += frame[index] & 0xFF;
        }
        return ((byte) (sum & 0xFF)) == frame[frame.length - 2];
    }

    private int readLittleEndianShort(byte[] payload, int offset) {
        return (payload[offset] & 0xFF) | ((payload[offset + 1] & 0xFF) << 8);
    }

    private int readIntBigEndian(byte[] payload, int offset) {
        return ((payload[offset] & 0xFF) << 24)
                | ((payload[offset + 1] & 0xFF) << 16)
                | ((payload[offset + 2] & 0xFF) << 8)
                | (payload[offset + 3] & 0xFF);
    }

    private String decodeGbk(byte[] payload, int offset, int length) {
        if (payload == null || payload.length <= offset || length <= 0) {
            return "-";
        }
        int safeLength = Math.min(length, payload.length - offset);
        String value = new String(payload, offset, safeLength, GB2312).replace('\u0000', ' ').trim();
        return valueOrDash(value);
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    private String twoHex(int value) {
        String hex = Integer.toHexString(value & 0xFF).toUpperCase();
        return hex.length() >= 2 ? hex : "0" + hex;
    }

    private final class HandshakeLoop extends Thread {
        private final SerialPortAdapter serialPortAdapter;
        private final String portName;
        private final String traceId;
        private volatile boolean closed;

        private HandshakeLoop(SerialPortAdapter serialPortAdapter, String portName, String traceId) {
            super("dvr-handshake-" + portName);
            this.serialPortAdapter = serialPortAdapter;
            this.portName = portName;
            this.traceId = traceId;
            setDaemon(true);
        }

        private boolean matches(SerialPortAdapter candidateAdapter, String candidatePortName) {
            return this.serialPortAdapter == candidateAdapter && this.portName.equals(candidatePortName);
        }

        @Override
        public void run() {
            while (!closed) {
                try {
                    sendHandshake();
                    if (lastHandshakeAckTimeMs > 0L
                            && System.currentTimeMillis() - lastHandshakeAckTimeMs > OFFLINE_TIMEOUT_MS) {
                        markOffline(traceId + "-offline");
                    }
                    Thread.sleep(online ? HANDSHAKE_INTERVAL_ONLINE_MS : HANDSHAKE_INTERVAL_OFFLINE_MS);
                } catch (InterruptedException e) {
                    interrupt();
                    return;
                } catch (Exception e) {
                    AppLogCenter.log(
                            LogCategory.ERROR,
                            LogLevel.WARN,
                            TAG,
                            "DVR 握手发送失败: " + e.getMessage(),
                            traceId + "-handshake-error"
                    );
                    try {
                        Thread.sleep(HANDSHAKE_INTERVAL_OFFLINE_MS);
                    } catch (InterruptedException interruptedException) {
                        interrupt();
                        return;
                    }
                }
            }
        }

        private void sendHandshake() {
            if (!serialPortAdapter.isOpen(portName)) {
                return;
            }
            byte[] payload = new byte[6];
            payload[0] = 0x55;
            payload[1] = 0x01;
            payload[2] = 0x00;
            payload[3] = 0x00;
            int sum = 0;
            for (int index = 0; index < 4; index++) {
                sum += payload[index] & 0xFF;
            }
            payload[4] = (byte) (sum & 0xFF);
            payload[5] = (byte) 0xAA;
            serialPortAdapter.send(portName, payload, traceId + "-handshake");
        }

        private void shutdown() {
            closed = true;
            interrupt();
        }
    }
}
