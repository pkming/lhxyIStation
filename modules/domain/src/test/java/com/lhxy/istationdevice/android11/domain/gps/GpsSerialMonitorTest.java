package com.lhxy.istationdevice.android11.domain.gps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.lhxy.istationdevice.android11.deviceapi.SerialMode;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortConfig;
import com.lhxy.istationdevice.android11.deviceapi.SerialReceiveListener;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

public class GpsSerialMonitorTest {
    @Test
    public void stateExpiresFixAndRejectsCachedCoordinates() {
        AtomicLong now = new AtomicLong(1_000L);
        FakeSerialAdapter adapter = new FakeSerialAdapter();
        GpsSerialMonitor monitor = new GpsSerialMonitor(now::get);
        monitor.attach(
                adapter,
                new ShellConfig.SerialChannel("gps", "ttyS5", 115200, SerialMode.REAL, "test"),
                "gps-test"
        );

        assertEquals(GpsSerialMonitor.GpsConnectionState.SEARCHING, monitor.getConnectionState());

        adapter.emit("$GNRMC,103618.00,A,2240.56605,N,11403.35383,E,0.03,0.0,150726,,,A*00\r\n");
        assertEquals(GpsSerialMonitor.GpsConnectionState.FIXED, monitor.getConnectionState());
        assertNotNull(monitor.getLatestSnapshot());

        now.set(5_000L);
        adapter.emit("$GPTXT,01,01,01,ANTENNA OPEN*25\r\n");
        now.set(6_001L);
        assertEquals(GpsSerialMonitor.GpsConnectionState.EXPIRED, monitor.getConnectionState());
        assertNull(monitor.getLatestSnapshot());

        adapter.open = false;
        assertEquals(GpsSerialMonitor.GpsConnectionState.RECONNECTING, monitor.getConnectionState());
        assertNull(monitor.getLatestSnapshot());
    }

    @Test
    public void incompleteSentenceDoesNotBlankLatestUsableFix() {
        AtomicLong now = new AtomicLong(1_000L);
        FakeSerialAdapter adapter = new FakeSerialAdapter();
        GpsSerialMonitor monitor = new GpsSerialMonitor(now::get);
        monitor.attach(
                adapter,
                new ShellConfig.SerialChannel("gps", "ttyS5", 115200, SerialMode.REAL, "test"),
                "gps-test"
        );

        adapter.emit("$GNRMC,103618.00,A,2240.56605,N,11403.35383,E,0.03,99.63,150726,,,A*00\r\n");
        GpsFixSnapshot validFix = monitor.getLatestSnapshot();
        assertNotNull(validFix);
        assertEquals("22.676101", validFix.getLatitudeDecimal());
        assertEquals("114.055897", validFix.getLongitudeDecimal());

        now.set(2_000L);
        adapter.emit("$GNGGA,103619.00,,,,,0,00,25.5,0.0,M,0.0,M,,*00\r\n");

        GpsFixSnapshot stableFix = monitor.getLatestSnapshot();
        assertEquals(GpsSerialMonitor.GpsConnectionState.FIXED, monitor.getConnectionState());
        assertNotNull(stableFix);
        assertEquals(validFix.getLatitudeDecimal(), stableFix.getLatitudeDecimal());
        assertEquals(validFix.getLongitudeDecimal(), stableFix.getLongitudeDecimal());
        assertEquals(validFix.getCourse(), stableFix.getCourse());
    }

    @Test
    public void physicalNoFixDoesNotExpireActiveSyntheticFix() {
        AtomicLong now = new AtomicLong(1_000L);
        FakeSerialAdapter adapter = new FakeSerialAdapter();
        GpsSerialMonitor monitor = new GpsSerialMonitor(now::get);
        monitor.attach(
                adapter,
                new ShellConfig.SerialChannel("gps", "ttyS5", 115200, SerialMode.REAL, "test"),
                "gps-test"
        );

        adapter.emit("$GNRMC,103618.00,A,2240.56605,N,11403.35383,E,0.03,99.63,150726,,,A*00\r\n");
        GpsFixSnapshot syntheticFix = monitor.getLatestSnapshot();
        assertNotNull(syntheticFix);

        now.set(2_000L);
        monitor.publishSyntheticSnapshot(syntheticFix, "gps-synthetic-test");
        now.set(8_000L);
        adapter.emit("$GNGGA,103624.00,,,,,0,00,25.5,0.0,M,0.0,M,,*00\r\n");

        assertEquals(GpsSerialMonitor.GpsConnectionState.FIXED, monitor.getConnectionState());
        assertEquals(syntheticFix.getLatitudeDecimal(), monitor.getLatestSnapshot().getLatitudeDecimal());

        monitor.finishSyntheticSnapshot();
        now.set(8_001L);
        adapter.emit("$GNGGA,103625.00,,,,,0,00,25.5,0.0,M,0.0,M,,*00\r\n");
        assertEquals(GpsSerialMonitor.GpsConnectionState.EXPIRED, monitor.getConnectionState());
    }

    private static final class FakeSerialAdapter implements SerialPortAdapter {
        private boolean open = true;
        private SerialReceiveListener listener;

        @Override
        public void open(SerialPortConfig config, String traceId) {
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
        }

        @Override
        public void setReceiveListener(String portName, SerialReceiveListener listener) {
            this.listener = listener;
        }

        @Override
        public void removeReceiveListener(String portName) {
            listener = null;
        }

        private void emit(String nmea) {
            listener.onReceive("ttyS5", nmea.getBytes(StandardCharsets.US_ASCII));
        }
    }
}
