package com.lhxy.istationdevice.android11.domain.passenger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.lhxy.istationdevice.android11.deviceapi.DeviceMode;
import com.lhxy.istationdevice.android11.deviceapi.JhySerialPortAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialMode;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortConfig;
import com.lhxy.istationdevice.android11.deviceapi.SerialReceiveListener;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JhyPassengerCounterMonitorTest {
    @Test
    public void requestCurrentCount_retriesAfterPortOpens() throws Exception {
        FakeSerialPortAdapter serialPortAdapter = new FakeSerialPortAdapter();
        JhyPassengerCounterMonitor monitor = new JhyPassengerCounterMonitor(serialPortAdapter, null, 20L, 20L);

        monitor.updateConfig(buildJhyConfig());

        Thread.sleep(120L);

        assertTrue(serialPortAdapter.openCallCount >= 1);
        assertEquals(1, serialPortAdapter.sentPayloads.size());
        assertArrayEquals(JhyPassengerCounterProtocol.createCurrentCount(), serialPortAdapter.sentPayloads.get(0));
    }

    @Test
    public void requestCurrentCount_insideThrottleWindow_defersInsteadOfDropping() throws Exception {
        FakeSerialPortAdapter serialPortAdapter = new FakeSerialPortAdapter();
        serialPortAdapter.isOpen = true;
        JhyPassengerCounterMonitor monitor = new JhyPassengerCounterMonitor(serialPortAdapter, null, 80L, 20L);

        monitor.updateConfig(buildJhyConfig());
        Thread.sleep(40L);
        monitor.requestCurrentCount("manual-arrival");

        Thread.sleep(30L);
        assertEquals(1, serialPortAdapter.sentPayloads.size());

        Thread.sleep(90L);
        assertEquals(2, serialPortAdapter.sentPayloads.size());
        assertArrayEquals(JhyPassengerCounterProtocol.createCurrentCount(), serialPortAdapter.sentPayloads.get(1));
    }

    @Test
    public void requestCurrentCountAfterStationDisplay_waitsForDisplayDelay() throws Exception {
        FakeSerialPortAdapter serialPortAdapter = new FakeSerialPortAdapter();
        serialPortAdapter.isOpen = true;
        JhyPassengerCounterMonitor monitor = new JhyPassengerCounterMonitor(serialPortAdapter, null, 80L, 20L);

        monitor.updateConfig(buildJhyConfig());
        Thread.sleep(40L);
        assertEquals(1, serialPortAdapter.sentPayloads.size());

        monitor.requestCurrentCountAfterStationDisplay("station-display");

        Thread.sleep(40L);
        assertEquals(1, serialPortAdapter.sentPayloads.size());

        Thread.sleep(70L);
        assertEquals(2, serialPortAdapter.sentPayloads.size());
        assertArrayEquals(JhyPassengerCounterProtocol.createCurrentCount(), serialPortAdapter.sentPayloads.get(1));
    }

    @Test
    public void requestCurrentCount_prefersNativeJhyPortWhenAvailable() throws Exception {
        FakeSerialPortAdapter serialPortAdapter = new FakeSerialPortAdapter();
        FakeJhySerialPortAdapter jhySerialPortAdapter = new FakeJhySerialPortAdapter();
        JhyPassengerCounterMonitor monitor = new JhyPassengerCounterMonitor(serialPortAdapter, jhySerialPortAdapter, 20L, 20L);

        monitor.updateConfig(buildJhyConfig());

        Thread.sleep(120L);

        assertEquals(0, serialPortAdapter.openCallCount);
        assertTrue(jhySerialPortAdapter.openCallCount >= 1);
        assertEquals(1, jhySerialPortAdapter.sentPayloads.size());
        assertArrayEquals(JhyPassengerCounterProtocol.createCurrentCount(), jhySerialPortAdapter.sentPayloads.get(0));
    }

    private static ShellConfig buildJhyConfig() {
        Map<String, ShellConfig.SerialChannel> serialChannels = new LinkedHashMap<>();
        serialChannels.put("rs485_2", new ShellConfig.SerialChannel("rs485_2", "ttyS9", 9600, SerialMode.REAL, "RS485-2"));
        return new ShellConfig(
                "M90",
                "test",
                "test",
                serialChannels,
                new LinkedHashMap<>(),
                ShellConfig.GpioConfig.empty(),
                ShellConfig.CameraConfig.empty(),
            ShellConfig.RfidConfig.stub(),
            ShellConfig.SystemConfig.stub(),
            ShellConfig.LocationConfig.defaults(),
            ShellConfig.CanConfig.empty(),
            ShellConfig.KeyboardConfig.stub(),
            ShellConfig.DebugReplay.defaultReplay(),
                new ShellConfig.BasicSetupConfig(
                ShellConfig.NewspaperSettings.defaults(),
                ShellConfig.NetworkSettings.defaults(),
                        new ShellConfig.SerialSettings("无", "无", "无", "JHY"),
                ShellConfig.TtsSettings.defaults(),
                ShellConfig.LanguageSettings.defaults(),
                ShellConfig.OtherSettings.defaults(),
                ShellConfig.WirelessSettings.defaults(),
                ShellConfig.ResourceImportSettings.defaults(),
                ShellConfig.ProtocolLinkageSettings.defaults()
                )
        );
    }

    private static final class FakeSerialPortAdapter implements SerialPortAdapter {
        private final List<byte[]> sentPayloads = new ArrayList<>();
        private boolean isOpen;
        private int openCallCount;

        @Override
        public void open(SerialPortConfig config, String traceId) {
            openCallCount++;
            isOpen = true;
        }

        @Override
        public void close(String portName, String traceId) {
            isOpen = false;
        }

        @Override
        public boolean isOpen(String portName) {
            return isOpen;
        }

        @Override
        public void send(String portName, byte[] payload, String traceId) {
            sentPayloads.add(payload.clone());
        }

        @Override
        public void setReceiveListener(String portName, SerialReceiveListener listener) {
        }

        @Override
        public void removeReceiveListener(String portName) {
        }
    }

    private static final class FakeJhySerialPortAdapter implements JhySerialPortAdapter {
        private final List<byte[]> sentPayloads = new ArrayList<>();
        private boolean open;
        private int openCallCount;

        @Override
        public boolean isSupported() {
            return true;
        }

        @Override
        public void open(String portName, int baudRate, SerialReceiveListener listener, String traceId) {
            openCallCount++;
            open = true;
        }

        @Override
        public void close(String portName, String traceId) {
            open = false;
        }

        @Override
        public boolean isOpen(String portName) {
            return open;
        }

        @Override
        public void send(String portName, byte[] payload, String traceId) {
            sentPayloads.add(payload.clone());
        }
    }
}
