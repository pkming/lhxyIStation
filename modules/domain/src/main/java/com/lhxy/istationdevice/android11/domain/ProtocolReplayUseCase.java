package com.lhxy.istationdevice.android11.domain;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.Hexs;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.protocol.ProtocolDebugFormatter;
import com.lhxy.istationdevice.android11.protocol.ProtocolEnvelope;
import com.lhxy.istationdevice.android11.protocol.ProtocolMockCatalog;
import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;
import com.lhxy.istationdevice.android11.protocol.gps.GpsNmeaParser;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808ProtocolDemo;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808FrameInspector;
import com.lhxy.istationdevice.android11.protocol.legacy.LegacyProtocolDemo;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

import java.util.List;

/**
 * 协议回放用例
 * <p>
 * 统一承接首页和调试页的样例链路，后面换成真实业务参数时也先走这里。
 */
public final class ProtocolReplayUseCase {
    private static final String TAG = "ProtocolReplay";
    private final GpsNmeaParser gpsNmeaParser = new GpsNmeaParser();

    /**
     * 回放屏显协议样例。
     */
    public int replayDisplayDemo(SerialPortAdapter serialPortAdapter, ShellConfig shellConfig, String traceId) {
        return sendSerialEnvelopes(
                serialPortAdapter,
                shellConfig.requireSerialChannel(shellConfig.getDebugReplay().getDisplaySerialKey()),
                LegacyProtocolDemo.generateDemoEnvelopes(),
                "屏显样例",
                traceId
        );
    }

    /**
     * 回放报站样例。
     */
    public int replayStationDemo(SerialPortAdapter serialPortAdapter, ShellConfig shellConfig, String traceId) {
        return sendSerialEnvelopes(
                serialPortAdapter,
                shellConfig.requireSerialChannel(shellConfig.getDebugReplay().getDisplaySerialKey()),
                LegacyProtocolDemo.generateStationDemoEnvelopes(),
                "报站样例",
                traceId
        );
    }

    /**
     * 回放 JT808 / AL808 样例。
     */
    public int replayJt808Demo(SocketClientAdapter socketClientAdapter, ShellConfig shellConfig, String traceId) {
        return sendSocketEnvelopes(
                socketClientAdapter,
                shellConfig,
                Jt808ProtocolDemo.generateDemoEnvelopes(),
                "808/AL808 样例",
                traceId
        );
    }

    /**
     * 回放调度主链样例。
     */
    public int replayDispatchDemo(SocketClientAdapter socketClientAdapter, ShellConfig shellConfig, String traceId) {
        return sendSocketEnvelopes(
                socketClientAdapter,
                shellConfig,
                Jt808ProtocolDemo.generateDispatchDemoEnvelopes(),
                "调度样例",
                traceId
        );
    }

    /**
     * 回放签到样例。
     */
    public int replaySignInDemo(SocketClientAdapter socketClientAdapter, ShellConfig shellConfig, String traceId) {
        return sendSocketEnvelopes(
                socketClientAdapter,
                shellConfig,
                Jt808ProtocolDemo.generateSignInDemoEnvelopes(),
                "签到样例",
                traceId
        );
    }

    /**
     * 回放升级样例。
     */
    public int replayUpgradeDemo(SocketClientAdapter socketClientAdapter, ShellConfig shellConfig, String traceId) {
        return sendSocketEnvelopes(
                socketClientAdapter,
                shellConfig,
                Jt808ProtocolDemo.generateUpgradeDemoEnvelopes(),
                "升级样例",
                traceId
        );
    }

    /**
     * 手工发送一段 HEX 到屏显串口。
     */
    public int sendManualHexToDisplay(SerialPortAdapter serialPortAdapter, ShellConfig shellConfig, String hexSource, String traceId) {
        return sendManualHexToSerial(
                serialPortAdapter,
                shellConfig.requireSerialChannel(shellConfig.getDebugReplay().getDisplaySerialKey()),
                hexSource,
                traceId
        );
    }

    /**
     * 手工发送一段 HEX 到 JT808 socket。
     */
    public int sendManualHexToJt808(SocketClientAdapter socketClientAdapter, ShellConfig shellConfig, String hexSource, String traceId) {
        return sendManualHexToSocket(
                socketClientAdapter,
                shellConfig.requireSocketChannel(shellConfig.getDebugReplay().getJt808SocketKey()),
                hexSource,
                traceId
        );
    }

    /**
     * 手工发送一段 HEX 到 AL808 socket。
     */
    public int sendManualHexToAl808(SocketClientAdapter socketClientAdapter, ShellConfig shellConfig, String hexSource, String traceId) {
        return sendManualHexToSocket(
                socketClientAdapter,
                shellConfig.requireSocketChannel(shellConfig.getDebugReplay().getAl808SocketKey()),
                hexSource,
                traceId
        );
    }

    /**
     * 手工发送一段 HEX 到指定串口。
     */
    public int sendManualHexToSerial(
            SerialPortAdapter serialPortAdapter,
            ShellConfig.SerialChannel serialChannel,
            String hexSource,
            String traceId
    ) {
        byte[] payload = parseManualHex(hexSource);
        if (!serialPortAdapter.isOpen(serialChannel.getPortName())) {
            serialPortAdapter.open(serialChannel.toSerialPortConfig(), traceId);
        }
        AppLogCenter.log(
                LogCategory.PROTOCOL_TX,
                LogLevel.INFO,
                TAG,
                "手工 HEX -> " + serialChannel.getKey() + "/" + serialChannel.getPortName() + " : " + Hexs.toHex(payload),
                traceId
        );
        serialPortAdapter.send(serialChannel.getPortName(), payload, traceId);
        return payload.length;
    }

    /**
     * 解析手工输入的 HEX。
     */
    private byte[] parseManualHex(String hexSource) {
        byte[] payload = Hexs.fromHex(hexSource);
        if (payload.length == 0) {
            throw new IllegalArgumentException("HEX 内容为空，先输入报文再发");
        }
        return payload;
    }

    /**
     * 发送手工 HEX 到指定 socket。
     */
    public int sendManualHexToSocket(
            SocketClientAdapter socketClientAdapter,
            ShellConfig.SocketChannel socketChannel,
            String hexSource,
            String traceId
    ) {
        byte[] payload = parseManualHex(hexSource);
        if (!socketClientAdapter.isConnected(socketChannel.getChannelName())) {
            socketClientAdapter.connect(socketChannel.toSocketEndpointConfig(), traceId);
        }
        AppLogCenter.log(
                LogCategory.PROTOCOL_TX,
                LogLevel.INFO,
                TAG,
                "手工 HEX -> " + socketChannel.getKey() + "/" + socketChannel.getChannelName() + " : " + Hexs.toHex(payload),
                traceId
        );
        socketClientAdapter.send(socketChannel.getChannelName(), payload, traceId);
        return payload.length;
    }

    /**
     * 解析一段 JT808 / AL808 HEX。
     */
    public String inspectJt808Hex(String hexSource) {
        return Jt808FrameInspector.inspect(parseManualHex(hexSource));
    }

    /**
     * 解析一段 GPS NMEA 文本。
     */
    public String inspectGpsNmea(String rawText) {
        GpsFixSnapshot snapshot = gpsNmeaParser.parseBlock(rawText);
        if (snapshot == null) {
            throw new IllegalArgumentException("没有识别到 RMC / GGA 语句");
        }
        return snapshot.describe() + "\n- source=" + snapshot.getSourceSentence();
    }

    /**
     * 输出当前内置协议 mock 目录。
     */
    public String describeMockCatalog() {
        return ProtocolMockCatalog.describeCatalog();
    }

    /**
     * 输出适合页面摘要展示的 mock 概况。
     */
    public String describeMockCatalogCompact() {
        return ProtocolMockCatalog.describeCompact();
    }

    private int sendSerialEnvelopes(
            SerialPortAdapter serialPortAdapter,
            ShellConfig.SerialChannel serialChannel,
            List<ProtocolEnvelope> envelopes,
            String label,
            String traceId
    ) {
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "开始回放" + label + "，共 " + envelopes.size() + " 条", traceId);
        if (!serialPortAdapter.isOpen(serialChannel.getPortName())) {
            serialPortAdapter.open(serialChannel.toSerialPortConfig(), traceId);
        }
        for (ProtocolEnvelope envelope : envelopes) {
            AppLogCenter.log(LogCategory.PROTOCOL_TX, LogLevel.DEBUG, TAG, ProtocolDebugFormatter.describe(envelope), traceId);
            serialPortAdapter.send(serialChannel.getPortName(), envelope.getPayload(), traceId);
        }
        return envelopes.size();
    }

    private int sendSocketEnvelopes(
            SocketClientAdapter socketClientAdapter,
            ShellConfig shellConfig,
            List<ProtocolEnvelope> envelopes,
            String label,
            String traceId
    ) {
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "开始回放" + label + "，共 " + envelopes.size() + " 条", traceId);
        ShellConfig.SocketChannel jt808Socket = shellConfig.requireSocketChannel(shellConfig.getDebugReplay().getJt808SocketKey());
        ShellConfig.SocketChannel al808Socket = shellConfig.requireSocketChannel(shellConfig.getDebugReplay().getAl808SocketKey());

        for (ProtocolEnvelope envelope : envelopes) {
            ShellConfig.SocketChannel socketChannel = jt808Socket.getChannelName().equals(envelope.getChannelName())
                    ? jt808Socket
                    : al808Socket.getChannelName().equals(envelope.getChannelName()) ? al808Socket : null;

            if (socketChannel == null) {
                AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, envelope.getChannelName() + " 没有对应的 socket 配置", traceId);
                continue;
            }

            if (!socketClientAdapter.isConnected(socketChannel.getChannelName())) {
                socketClientAdapter.connect(socketChannel.toSocketEndpointConfig(), traceId);
            }
            AppLogCenter.log(LogCategory.PROTOCOL_TX, LogLevel.DEBUG, TAG, ProtocolDebugFormatter.describe(envelope), traceId);
            socketClientAdapter.send(socketChannel.getChannelName(), envelope.getPayload(), traceId);
        }
        return envelopes.size();
    }
}
