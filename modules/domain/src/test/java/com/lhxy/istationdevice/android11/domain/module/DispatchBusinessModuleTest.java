package com.lhxy.istationdevice.android11.domain.module;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.lhxy.istationdevice.android11.core.Hexs;
import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SocketEndpointConfig;
import com.lhxy.istationdevice.android11.deviceapi.SocketReceiveListener;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigLoader;
import com.lhxy.istationdevice.android11.domain.module.state.StationState;
import com.lhxy.istationdevice.android11.domain.socket.Jt808SocketMonitor;
import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808Codec;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808Frame;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808FrameDecoder;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808LegacyMessages;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808PositionSnapshot;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808ReportStationSnapshot;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808Variant;

import org.json.JSONObject;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DispatchBusinessModuleTest {
    @Test
    public void al808ManualSequenceMatchesV7WireBytesInBothDirections() {
        for (String direction : Arrays.asList("上行", "下行")) {
            StationState station = station(direction);
            assertFalse(DispatchBusinessModule.shouldReportStation(Jt808Variant.AL808, 0, 0));
            String[] prefixes = {
                    "00 00 01 19 02 01 00 00 00 00 01 00",
                    "00 00 01 19 01 01 00 00 00 00 02 00",
                    "00 00 01 19 02 01 00 00 00 00 02 00",
                    "00 00 01 19 01 01 00 00 00 00 03 00"
            };
            for (String prefix : prefixes) {
                station.advanceStation();
                Jt808ReportStationSnapshot report = DispatchBusinessModule.buildReportStationSnapshot(
                        Jt808Variant.AL808, 281, station, gps("10", "176.9"));
                Jt808Frame frame = new Jt808LegacyMessages().createReportStation(Jt808Variant.AL808, "18612345677", report);
                byte[] expected = Hexs.fromHex(prefix);
                expected[5] = (byte) (direction.equals("上行") ? 1 : 2);
                assertEquals(0x0B02, frame.getMessageId());
                assertEquals(35, frame.getBody().length);
                assertArrayEquals(expected, Arrays.copyOf(frame.getBody(), 12));
                assertEquals(185, report.getSpeed());
                assertEquals(176, report.getAngle());
            }
        }
    }

    @Test
    public void al808GpsEventsUseTheSameMappingAsManualEvents() {
        StationState station = station("上行");
        int[][] events = {{1, 1, 2, 1}, {1, 0, 1, 2}, {2, 1, 2, 2}, {2, 0, 1, 3}};
        for (int[] event : events) {
            station.recordAutoStation(event[0], "test", event[1]);
            Jt808ReportStationSnapshot report = DispatchBusinessModule.buildReportStationSnapshot(Jt808Variant.AL808, 281, station, null);
            assertEquals(event[2], report.getStatus());
            assertEquals(event[3], report.getBusNo());
        }
    }

    @Test
    public void cc808StationMappingRemainsSeparate() {
        StationState station = station("下行");
        assertTrue(DispatchBusinessModule.shouldReportStation(Jt808Variant.CC808, 0, 0));
        station.advanceStation();
        Jt808ReportStationSnapshot preview = DispatchBusinessModule.buildReportStationSnapshot(Jt808Variant.CC808, 281, station, null);
        assertEquals(1, preview.getStatus());
        assertEquals(2, preview.getBusNo());
        station.advanceStation();
        Jt808ReportStationSnapshot arrival = DispatchBusinessModule.buildReportStationSnapshot(Jt808Variant.CC808, 281, station, null);
        assertEquals(0, arrival.getStatus());
        assertEquals(2, arrival.getBusNo());
        assertEquals(2, arrival.getDirection());
        assertEquals(4, DispatchBusinessModule.positionStationNumber(4));
    }

    @Test
    public void al808PositionUsesV7StatusSpeedAndCourseUnits() {
        Jt808PositionSnapshot position = DispatchBusinessModule.buildPositionSnapshot(Jt808Variant.AL808, gps("10", "176.9"));
        assertEquals(0x00080013L, position.getStatusFlag());
        assertEquals(185, position.getSpeed());
        assertEquals(176, position.getDirection());
        assertEquals(0, DispatchBusinessModule.buildPositionSnapshot(Jt808Variant.AL808, gps("0.9", "0")).getSpeed());
        assertEquals(0, DispatchBusinessModule.buildPositionSnapshot(Jt808Variant.AL808, gps("1", "0")).getDirection());
        assertEquals(361, DispatchBusinessModule.buildPositionSnapshot(Jt808Variant.AL808, gps("1", "")).getDirection());
        assertEquals(0, DispatchBusinessModule.buildPositionSnapshot(Jt808Variant.AL808, null).getStatusFlag());
        Jt808ReportStationSnapshot station = DispatchBusinessModule.buildReportStationSnapshot(Jt808Variant.AL808, 281, station("上行"), gps("1", "0"));
        assertEquals(361, station.getAngle());
        Jt808PositionSnapshot cc808 = DispatchBusinessModule.buildPositionSnapshot(Jt808Variant.CC808, gps("10", "176.9"));
        assertEquals(19, cc808.getSpeed());
        assertEquals(2, cc808.getStatusFlag());
    }

    @Test
    public void protocolRoundTripsWithoutAliasesOrSocketNameInference() throws Exception {
        for (String protocol : Arrays.asList("AL808", "ALINK", "CC808", "")) {
            ShellConfig config = config(protocol);
            ShellConfig reloaded = ShellConfigLoader.parse(ShellConfigLoader.toJson(config));
            assertEquals(protocol, reloaded.getBasicSetupConfig().getNetworkSettings().getDispatchProtocol());
        }
        assertEquals("CC808", ShellConfigLoader.parse("{}").getBasicSetupConfig().getNetworkSettings().getDispatchProtocol());
        assertEquals("AL808", ShellConfig.NetworkSettings.normalizeDispatchProtocol(" al808 "));
        assertEquals("", ShellConfig.NetworkSettings.normalizeDispatchProtocol("NONE"));
        JSONObject json = new JSONObject(ShellConfigLoader.toJson(config("AL808")));
        json.getJSONObject("debugReplay").put("al808SocketKey", "custom-dispatch").put("jt808SocketKey", "separate-jt");
        ShellConfig separate = ShellConfigLoader.parse(json.toString());
        assertEquals("custom-dispatch", separate.getDispatchSocketKey("AL808"));
        assertEquals("custom-dispatch", separate.getDispatchSocketKey("CC808"));
        assertEquals("separate-jt", separate.getDispatchSocketKey("ALINK"));
    }

    @Test
    public void registrationAndMatchingAuthenticationMustPrecedeLineAndStation() throws Exception {
        Harness h = new Harness("AL808");
        h.station.advanceStation();
        h.module.sendLineSwitchReport("281", 1, 1, "281", 1, 1, 1, "offline-line");
        h.module.reportStationProgress("offline-station");
        assertTrue(h.socket.frames.isEmpty());
        h.tick();
        Jt808Frame register = h.socket.last();
        assertEquals(0x0100, register.getMessageId());
        assertEquals(34, register.getBody().length);
        assertEquals("ALINK", new String(Arrays.copyOfRange(register.getBody(), 4, 9), StandardCharsets.US_ASCII));
        assertEquals("M90V702", new String(Arrays.copyOfRange(register.getBody(), 9, 16), StandardCharsets.US_ASCII));
        h.registerResponse(register.getSerialNumber() + 1, 1);
        h.tick();
        assertEquals(1, h.socket.frames.size());
        h.registerResponse(register.getSerialNumber(), 0);
        Jt808Frame authority = h.socket.last();
        assertEquals(0x0102, authority.getMessageId());
        assertArrayEquals(new byte[]{'t', 'e', 's', 't'}, authority.getBody());
        h.ack(authority.getSerialNumber(), 0x0002, 0);
        h.ack(authority.getSerialNumber() + 1, 0x0102, 0);
        h.tick();
        h.module.reportStationProgress("still-waiting");
        assertEquals(2, h.socket.frames.size());
        h.ack(authority.getSerialNumber(), 0x0102, 0);
        h.tick();
        assertEquals(Arrays.asList(0x0100, 0x0102, 0x0002, 0xDB0E, 0x0B02), h.socket.ids());
        assertEquals(2, h.socket.last().getBody()[4]);
        assertEquals(1, h.socket.last().getBody()[10]);
        h.module.reportStationProgress("same-event");
        h.tick();
        assertEquals(1, h.socket.count(0x0B02));
        h.station.advanceStation();
        h.module.reportStationProgress("arrival");
        assertEquals(1, h.socket.last().getBody()[4]);
        assertEquals(2, h.socket.last().getBody()[10]);
    }

    @Test
    public void authenticationFailureAndTimeoutRetryWithoutBusinessReports() throws Exception {
        Harness h = new Harness("AL808");
        h.tick();
        h.registerResponse(h.socket.last().getSerialNumber(), 0);
        h.ack(h.socket.last().getSerialNumber(), 0x0102, 1);
        h.tick();
        assertEquals(2, h.socket.count(0x0100));
        field(DispatchBusinessModule.class, "socketHandshakeSentAtMs").setLong(h.module, System.currentTimeMillis() - 20_000L);
        h.tick();
        assertFalse(h.socket.connected);
        h.tick();
        assertEquals(3, h.socket.count(0x0100));
        assertEquals(0, h.socket.count(0xDB0E));
        assertEquals(0, h.socket.count(0x0B02));
    }

    @Test
    public void reconnectReauthenticatesAndRestoresCurrentStationOnce() throws Exception {
        Harness h = new Harness("AL808");
        h.station.advanceStation();
        h.station.advanceStation();
        h.authenticate();
        h.tick();
        assertEquals(1, h.socket.count(0x0B02));
        h.socket.connected = false;
        h.authenticate();
        h.tick();
        assertEquals(2, h.socket.count(0xDB0E));
        assertEquals(2, h.socket.count(0x0B02));
        Jt808Frame line = h.socket.frames.stream().filter(f -> f.getMessageId() == 0xDB0E).reduce((a, b) -> b).get();
        assertEquals(2, line.getBody()[12]);
        assertEquals(2, h.socket.last().getBody()[10]);
        h.tick();
        assertEquals(2, h.socket.count(0x0B02));
    }

    @Test
    public void startupSkipsAl808OriginAndInvalidGpsPosition() throws Exception {
        Harness h = new Harness("AL808");
        h.authenticate();
        h.tick();
        h.module.reportStationProgress("origin");
        assertEquals(1, h.socket.count(0xDB0E));
        assertEquals(0, h.socket.count(0x0B02));
        assertEquals(0, h.socket.count(0x0200));
    }

    @Test
    public void cc808KeepsItsRegisterLineAndStationMessageIds() throws Exception {
        Harness h = new Harness("CC808");
        h.station.advanceStation();
        h.authenticate();
        h.tick();
        assertEquals(46, h.socket.frames.get(0).getBody().length);
        assertEquals(1, h.socket.count(0x0B0B));
        assertEquals(0, h.socket.count(0xDB0E));
        assertEquals(1, h.socket.count(0x0900));
        assertEquals(112, h.socket.last().getBody().length);
        assertEquals(7, h.socket.last().getBody()[0]);
    }

    @Test
    public void disablingOrChangingProtocolCannotReuseAuthenticatedSession() throws Exception {
        Harness h = new Harness("AL808");
        h.authenticate();
        h.station.advanceStation();
        h.configure("CC808");
        h.module.reportStationProgress("changed-protocol");
        assertEquals(2, h.socket.frames.size());
        h.tick();
        assertFalse(h.socket.connected);
        h.tick();
        assertEquals(46, h.socket.last().getBody().length);
        for (String protocol : Arrays.asList("", "ALINK")) {
            Harness disabled = new Harness(protocol);
            disabled.tick();
            disabled.module.reportStationProgress("unsupported");
            assertTrue(disabled.socket.frames.isEmpty());
        }
    }

    @Test
    public void runningLineSwitchUsesSelectedDirectionAndResendsAfterStationReset() throws Exception {
        Harness h = new Harness("AL808");
        h.station.advanceStation();
        h.authenticate();
        h.tick();
        h.station.applyLineProfile("282", "下行", Arrays.asList("A", "B", "C"));
        h.module.sendLineSwitchReport("281", 1, 1, "282", 2, 1, 1, "switch");
        assertEquals(0xDB0E, h.socket.last().getMessageId());
        assertEquals(2, h.socket.last().getBody()[11]);
        h.station.advanceStation();
        h.module.reportStationProgress("new-line-departure");
        assertEquals(2, h.socket.count(0x0B02));
        assertArrayEquals(Hexs.fromHex("00 00 01 1A 02 02"), Arrays.copyOf(h.socket.last().getBody(), 6));
    }

    private static StationState station(String direction) {
        StationState station = new StationState();
        station.applyLineProfile("281", direction, Arrays.asList("A", "B", "C", "D"));
        return station;
    }

    private static GpsFixSnapshot gps(String speed, String course) {
        return new GpsFixSnapshot("test", true, 1, 3, "120000", "060926", "", "N", "22.708998", "", "E",
                "114.097777", speed, course, "0", 12);
    }

    private static ShellConfig config(String protocol) throws Exception {
        JSONObject json = new JSONObject(ShellConfigLoader.toJson(ShellConfigLoader.createDefault()));
        json.getJSONObject("basicSetup").getJSONObject("network").put("dispatchProtocol", protocol).put("dispatchId", "18612345677");
        return ShellConfigLoader.parse(json.toString());
    }

    private static Field field(Class<?> type, String name) throws Exception {
        Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    private static final class Harness {
        final FakeSocket socket = new FakeSocket();
        final StationState station = station("上行");
        final Jt808SocketMonitor monitor = new Jt808SocketMonitor();
        final DispatchBusinessModule module = new DispatchBusinessModule(null, socket, null, null, monitor, null, null);
        ShellConfig config;
        String channel;

        Harness(String protocol) throws Exception {
            module.attachStateProviders(null, () -> station);
            configure(protocol);
        }

        void configure(String protocol) throws Exception {
            config = config(protocol);
            // Keep the real scheduler and Android resource loading out of this deterministic socket test.
            field(AbstractTerminalBusinessModule.class, "shellConfig").set(module, config);
            ShellConfig.SocketChannel endpoint = config.requireSocketChannel(config.getDispatchSocketKey(protocol));
            channel = endpoint.getChannelName();
            monitor.attach(socket, endpoint, "test");
        }

        void tick() throws Exception {
            Method method = DispatchBusinessModule.class.getDeclaredMethod("sendPeriodicSocketReport", String.class);
            method.setAccessible(true);
            method.invoke(module, "test-periodic");
        }

        void authenticate() throws Exception {
            tick();
            registerResponse(socket.last().getSerialNumber(), 0);
            ack(socket.last().getSerialNumber(), 0x0102, 0);
        }

        void registerResponse(int serial, int result) {
            receive(0x8100, new byte[]{(byte) (serial >> 8), (byte) serial, (byte) result, 't', 'e', 's', 't'});
        }

        void ack(int serial, int message, int result) {
            receive(0x8001, new byte[]{(byte) (serial >> 8), (byte) serial, (byte) (message >> 8), (byte) message, (byte) result});
        }

        void receive(int id, byte[] body) {
            socket.listener.onSocketReceive(channel, new Jt808Codec().encode(new Jt808Frame(Jt808Variant.AL808, id, "018612345677", 123, body)));
        }
    }

    private static final class FakeSocket implements SocketClientAdapter {
        boolean connected;
        SocketReceiveListener listener;
        final List<Jt808Frame> frames = new ArrayList<>();
        public void connect(SocketEndpointConfig config, String traceId) { connected = true; }
        public void disconnect(String channel, String traceId) { connected = false; }
        public boolean isConnected(String channel) { return connected; }
        public void send(String channel, byte[] payload, String traceId) {
            assertTrue("Socket must be connected before send", connected);
            frames.add(Jt808FrameDecoder.decode(payload, traceId));
        }
        public void setReceiveListener(String channel, SocketReceiveListener listener) { this.listener = listener; }
        public void removeReceiveListener(String channel) { listener = null; }
        Jt808Frame last() { return frames.get(frames.size() - 1); }
        long count(int id) { return frames.stream().filter(frame -> frame.getMessageId() == id).count(); }
        List<Integer> ids() {
            List<Integer> ids = new ArrayList<>();
            for (Jt808Frame frame : frames) { ids.add(frame.getMessageId()); }
            return ids;
        }
    }
}
