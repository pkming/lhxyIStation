package com.lhxy.istationdevice.android11.domain.dispatch;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;

import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsRouteResource;
import com.lhxy.istationdevice.android11.domain.module.state.SignInState;
import com.lhxy.istationdevice.android11.domain.module.state.StationState;

public class Jt808OverspeedInfoPacketFactoryTest {
    private static final Charset GB2312 = Charset.forName("GB2312");

    @Test
    public void buildCrossing_encodesM90CrossingOverspeedBody() {
        Jt808OverspeedInfoPacketFactory factory = new Jt808OverspeedInfoPacketFactory();
        StationState stationState = new StationState();
        stationState.setLineName("M90路");
        stationState.recordReminder("转弯提醒", "UID-12", "1", 12, "20", 0);
        SignInState signInState = new SignInState();
        signInState.applyCard("11223344");
        LegacyGpsRouteResource route = new LegacyGpsRouteResource(
                "M90路",
                LegacyGpsRouteResource.ATTRIBUTE_UP_DOWN,
                "上行",
                Collections.emptyList(),
                Arrays.asList(
                        reminder(0, "UID-0"),
                    reminder(11, "UID-11"),
                        reminder(12, "UID-12")
                )
        );

        byte[] frame = factory.buildCrossing(
                createShellConfig(),
                stationState,
                signInState,
                route,
                66,
                6543,
                5L,
                "114.123456",
                "22.654321",
                "260508120102"
        );
        byte[] raw = unescape(frame);

        assertEquals(0x7E, raw[0] & 0xFF);
        assertEquals(0x09, raw[1] & 0xFF);
        assertEquals(0x00, raw[2] & 0xFF);
        assertEquals(154, raw[4] & 0xFF);
        assertEquals(0xC8, raw[13] & 0xFF);
        assertEquals(22, raw[16] & 0xFF);
        assertArrayEquals(textBytes("M90路", 36), slice(raw, 19, 36));
        assertArrayEquals(textBytes("UID-11", 36), slice(raw, 55, 36));
        assertArrayEquals(new byte[]{0x26, 0x05, 0x08, 0x12, 0x01, 0x02}, slice(raw, 91, 6));
        assertEquals(20, ((raw[109] & 0xFF) << 8) | (raw[110] & 0xFF));
        assertEquals(1, raw[111] & 0xFF);
        assertEquals(12, raw[112] & 0xFF);
        assertEquals(0x7E, raw[raw.length - 1] & 0xFF);
    }

    private LegacyGpsRouteResource.ReminderPoint reminder(int reminderNo, String crossCode) {
        return new LegacyGpsRouteResource.ReminderPoint(
                reminderNo,
                "转弯提醒",
                "114.000000",
                "22.000000",
                114.0d,
                22.0d,
                "",
                "",
                30d,
                crossCode,
                "",
                "",
                "",
                "",
                "20",
                "1",
                ""
        );
    }

    private ShellConfig createShellConfig() {
        return new ShellConfig(
                "test",
                "1",
                "unit-test",
                Collections.emptyMap(),
                Collections.emptyMap(),
                null,
                null,
                null,
                null,
                ShellConfig.DebugReplay.defaultReplay(),
                ShellConfig.BasicSetupConfig.defaults()
        );
    }

    private byte[] unescape(byte[] source) {
        byte[] buffer = new byte[source.length];
        int offset = 0;
        for (int index = 0; index < source.length; index++) {
            byte current = source[index];
            if (index > 0 && index < source.length - 1 && current == 0x7D && index + 1 < source.length - 1) {
                byte next = source[index + 1];
                if (next == 0x01) {
                    buffer[offset++] = 0x7D;
                    index++;
                    continue;
                }
                if (next == 0x02) {
                    buffer[offset++] = 0x7E;
                    index++;
                    continue;
                }
            }
            buffer[offset++] = current;
        }
        byte[] raw = new byte[offset];
        System.arraycopy(buffer, 0, raw, 0, offset);
        return raw;
    }

    private byte[] textBytes(String value, int maxLength) {
        byte[] raw = value.getBytes(GB2312);
        byte[] padded = new byte[maxLength];
        System.arraycopy(raw, 0, padded, 0, Math.min(raw.length, maxLength));
        return padded;
    }

    private byte[] slice(byte[] source, int offset, int length) {
        byte[] value = new byte[length];
        System.arraycopy(source, offset, value, 0, length);
        return value;
    }
}