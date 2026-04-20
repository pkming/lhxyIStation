package com.lhxy.istationdevice.android11.devicem90;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.Hexs;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortConfig;
import com.lhxy.istationdevice.android11.deviceapi.SerialReceiveListener;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * M90 串口 stub 适配器
 * <p>
 * 真串口还没接之前，先把开关口、发包和基本回包日志链路跑通。
 */
public final class M90StubSerialPortAdapter implements SerialPortAdapter {
    private static final String TAG = "M90StubSerial";
    private final Set<String> openedPorts = new HashSet<>();
    private final Map<String, SerialReceiveListener> listeners = new ConcurrentHashMap<>();

    @Override
    public void open(SerialPortConfig config, String traceId) {
        openedPorts.add(config.getPortName());
        AppLogCenter.log(
                LogCategory.DEVICE,
                LogLevel.INFO,
                TAG,
                "stub open " + config.getPortName() + " @" + config.getBaudRate(),
                traceId
        );
    }

    @Override
    public void close(String portName, String traceId) {
        openedPorts.remove(portName);
        AppLogCenter.log(LogCategory.DEVICE, LogLevel.INFO, TAG, "stub close " + portName, traceId);
    }

    @Override
    public boolean isOpen(String portName) {
        return openedPorts.contains(portName);
    }

    @Override
    public void send(String portName, byte[] payload, String traceId) {
        AppLogCenter.log(
                LogCategory.PROTOCOL_TX,
                LogLevel.DEBUG,
                TAG,
                "stub send on " + portName + ": " + Hexs.toHex(payload),
                traceId
        );
        AppLogCenter.log(
                LogCategory.PROTOCOL_RX,
                LogLevel.DEBUG,
                TAG,
                "stub recv echo on " + portName + ": " + Hexs.toHex(payload),
                traceId + "-rx"
        );
        SerialReceiveListener listener = listeners.get(portName);
        if (listener != null) {
            listener.onReceive(portName, payload.clone());
        }
    }

    @Override
    public void setReceiveListener(String portName, SerialReceiveListener listener) {
        if (listener == null) {
            listeners.remove(portName);
            return;
        }
        listeners.put(portName, listener);
    }

    @Override
    public void removeReceiveListener(String portName) {
        listeners.remove(portName);
    }
}
