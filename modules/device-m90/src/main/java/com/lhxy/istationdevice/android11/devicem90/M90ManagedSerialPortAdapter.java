package com.lhxy.istationdevice.android11.devicem90;

import com.lhxy.istationdevice.android11.deviceapi.SerialMode;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortConfig;
import com.lhxy.istationdevice.android11.deviceapi.SerialReceiveListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * M90 串口管理适配器
 * <p>
 * 根据配置里的 mode 决定走 stub 还是真串口。
 */
public final class M90ManagedSerialPortAdapter implements SerialPortAdapter {
    private final M90StubSerialPortAdapter stubAdapter = new M90StubSerialPortAdapter();
    private final M90RealSerialPortAdapter realAdapter = new M90RealSerialPortAdapter();
    private final Map<String, SerialMode> portModes = new ConcurrentHashMap<>();

    /**
     * 打开串口。
     */
    @Override
    public void open(SerialPortConfig config, String traceId) {
        portModes.put(config.getPortName(), config.getMode());
        delegate(config.getMode()).open(config, traceId);
    }

    /**
     * 关闭串口。
     */
    @Override
    public void close(String portName, String traceId) {
        SerialMode mode = portModes.remove(portName);
        if (mode == null) {
            stubAdapter.close(portName, traceId);
            realAdapter.close(portName, traceId);
            return;
        }
        delegate(mode).close(portName, traceId);
    }

    /**
     * 判断串口是否已打开。
     */
    @Override
    public boolean isOpen(String portName) {
        SerialMode mode = portModes.get(portName);
        if (mode == null) {
            return stubAdapter.isOpen(portName) || realAdapter.isOpen(portName);
        }
        return delegate(mode).isOpen(portName);
    }

    /**
     * 发送串口数据。
     */
    @Override
    public void send(String portName, byte[] payload, String traceId) {
        SerialMode mode = portModes.get(portName);
        delegate(mode == null ? SerialMode.STUB : mode).send(portName, payload, traceId);
    }

    @Override
    public void setReceiveListener(String portName, SerialReceiveListener listener) {
        stubAdapter.setReceiveListener(portName, listener);
        realAdapter.setReceiveListener(portName, listener);
    }

    @Override
    public void removeReceiveListener(String portName) {
        stubAdapter.removeReceiveListener(portName);
        realAdapter.removeReceiveListener(portName);
    }

    private SerialPortAdapter delegate(SerialMode mode) {
        return mode == SerialMode.REAL ? realAdapter : stubAdapter;
    }
}
