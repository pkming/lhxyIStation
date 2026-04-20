package com.lhxy.istationdevice.android11.domain.socket;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.Hexs;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SocketReceiveListener;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808FrameInspector;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808FrameStreamParser;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JT808 / AL808 Socket 监视器
 * <p>
 * 负责把 Socket 回包切成完整帧，并输出更适合现场看的解析摘要。
 */
public final class Jt808SocketMonitor {
    private static final String TAG = "Jt808SocketMonitor";

    private final Map<String, Jt808FrameStreamParser> parsers = new ConcurrentHashMap<>();
    private final Map<String, String> channelKeyByName = new ConcurrentHashMap<>();
    private final Map<String, String> latestInspectByName = new ConcurrentHashMap<>();
    private final Set<String> attachedChannelNames = ConcurrentHashMap.newKeySet();

    /**
     * 绑定一条 Socket 通道。
     */
    public void attach(SocketClientAdapter socketClientAdapter, ShellConfig.SocketChannel socketChannel, String traceId) {
        if (socketClientAdapter == null || socketChannel == null) {
            return;
        }
        parsers.put(socketChannel.getChannelName(), new Jt808FrameStreamParser());
        channelKeyByName.put(socketChannel.getChannelName(), socketChannel.getKey());
        attachedChannelNames.add(socketChannel.getChannelName());
        socketClientAdapter.setReceiveListener(socketChannel.getChannelName(), buildListener(socketChannel, traceId));
        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.INFO,
                TAG,
                "已绑定 Socket 协议监听: " + socketChannel.getKey() + "/" + socketChannel.getChannelName(),
                traceId
        );
    }

    /**
     * 按当前配置同步默认调度通道监听。
     */
    public void syncDefaultChannels(SocketClientAdapter socketClientAdapter, ShellConfig shellConfig, String traceId) {
        if (socketClientAdapter == null || shellConfig == null || shellConfig.getDebugReplay() == null) {
            return;
        }

        Set<String> expectedChannelNames = new LinkedHashSet<>();
        bindIfPresent(socketClientAdapter, shellConfig, shellConfig.getDebugReplay().getJt808SocketKey(), expectedChannelNames, traceId);
        bindIfPresent(socketClientAdapter, shellConfig, shellConfig.getDebugReplay().getAl808SocketKey(), expectedChannelNames, traceId);

        for (String channelName : new LinkedHashSet<>(attachedChannelNames)) {
            if (!expectedChannelNames.contains(channelName)) {
                detach(socketClientAdapter, channelName, traceId);
            }
        }
    }

    private void bindIfPresent(
            SocketClientAdapter socketClientAdapter,
            ShellConfig shellConfig,
            String channelKey,
            Set<String> expectedChannelNames,
            String traceId
    ) {
        try {
            ShellConfig.SocketChannel socketChannel = shellConfig.requireSocketChannel(channelKey);
            expectedChannelNames.add(socketChannel.getChannelName());
            attach(socketClientAdapter, socketChannel, traceId);
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "绑定默认 Socket 监听失败: " + e.getMessage(), traceId);
        }
    }

    /**
     * 解绑一条 Socket 通道。
     */
    public void detach(SocketClientAdapter socketClientAdapter, String channelName, String traceId) {
        if (socketClientAdapter == null || channelName == null || channelName.trim().isEmpty()) {
            return;
        }
        socketClientAdapter.removeReceiveListener(channelName);
        attachedChannelNames.remove(channelName);
        parsers.remove(channelName);
        latestInspectByName.remove(channelName);
        String channelKey = channelKeyByName.remove(channelName);
        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.INFO,
                TAG,
                "已解绑 Socket 协议监听: " + (channelKey == null ? channelName : channelKey + "/" + channelName),
                traceId
        );
    }

    /**
     * 当前通道是否已绑定协议监听。
     */
    public boolean isAttached(String channelName) {
        return channelName != null && attachedChannelNames.contains(channelName);
    }

    /**
     * 返回当前监听状态摘要。
     */
    public String describeStatus() {
        StringBuilder builder = new StringBuilder("Socket 协议监听:");
        if (attachedChannelNames.isEmpty()) {
            builder.append("\n- 当前还没绑定任何调度通道");
            return builder.toString();
        }

        Map<String, String> orderedInspect = new LinkedHashMap<>();
        for (String channelName : attachedChannelNames) {
            orderedInspect.put(channelName, latestInspectByName.get(channelName));
        }

        for (Map.Entry<String, String> entry : orderedInspect.entrySet()) {
            String channelName = entry.getKey();
            String channelKey = channelKeyByName.get(channelName);
            builder.append("\n- ")
                    .append(channelKey == null ? channelName : channelKey + "/" + channelName)
                    .append(" -> 已绑定");
            String inspect = entry.getValue();
            if (inspect == null || inspect.trim().isEmpty()) {
                builder.append("，还没有完整帧");
            } else {
                builder.append("\n  最近一帧: ").append(compactInspect(inspect));
            }
        }
        return builder.toString();
    }

    private SocketReceiveListener buildListener(ShellConfig.SocketChannel socketChannel, String traceId) {
        return (channelName, payload) -> {
            Jt808FrameStreamParser parser = parsers.computeIfAbsent(channelName, key -> new Jt808FrameStreamParser());
            List<byte[]> frames = parser.accept(payload);
            if (frames.isEmpty()) {
                AppLogCenter.log(
                        LogCategory.BIZ,
                        LogLevel.DEBUG,
                        TAG,
                        "收到一段未成帧 Socket 数据: " + socketChannel.getKey() + "/" + channelName + " -> " + Hexs.toHex(payload),
                        traceId + "-socket-chunk"
                );
                return;
            }

            for (byte[] frame : frames) {
                String inspect = Jt808FrameInspector.inspect(frame);
                latestInspectByName.put(channelName, inspect);
                AppLogCenter.log(
                        LogCategory.BIZ,
                        LogLevel.INFO,
                        TAG,
                        socketChannel.getKey() + "/" + channelName + "\n" + inspect,
                        traceId + "-socket-frame"
                );
            }
        };
    }

    private String compactInspect(String inspectText) {
        String[] lines = inspectText.split("\\n");
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (String line : lines) {
            String text = line == null ? "" : line.trim();
            if (text.isEmpty() || "解析结果:".equals(text)) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(" | ");
            }
            builder.append(text.replace("- ", ""));
            count++;
            if (count >= 3) {
                break;
            }
        }
        return builder.length() == 0 ? "还没有可展示的解析摘要" : builder.toString();
    }
}
