package com.lhxy.istationdevice.android11.domain.dispatch;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsRouteResource;
import com.lhxy.istationdevice.android11.domain.module.state.StationState;

import java.nio.charset.Charset;
import java.util.Collections;

public class Jt808CrossInfoPacketFactoryTest {
    private static final Charset GB2312 = Charset.forName("GB2312");

    @Test
    public void build_encodesM90CrossInfoBody() {
        Jt808CrossInfoPacketFactory factory = new Jt808CrossInfoPacketFactory();
        StationState stationState = new StationState();
        stationState.setLineName("M90路");
        LegacyGpsRouteResource.ReminderPoint reminderPoint = new LegacyGpsRouteResource.ReminderPoint(
                12,
                "转弯提醒",
                "114.123456",
                "22.654321",
                114.123456d,
                22.654321d,
                "",
                "",
                30d,
            "UID-12",
            "进路口提示",
            "出路口提示",
            "进扩展",
            "出扩展",
                "20",
            "1",
                ""
        );

        byte[] frame = factory.build(
                createShellConfig(),
                stationState,
                reminderPoint,
                "260508120102",
                "000000000000",
                270,
                "114.123456",
                "22.654321"
        );
        byte[] raw = unescape(frame);

        assertEquals(0x7E, raw[0] & 0xFF);
        assertEquals(0x09, raw[1] & 0xFF);
        assertEquals(0x00, raw[2] & 0xFF);
        assertEquals(95, raw[4] & 0xFF);
        assertEquals(0x32, raw[13] & 0xFF);
        assertArrayEquals(textBytes("M90路", 36), slice(raw, 14, 36));
        assertArrayEquals(textBytes("UID-12", 36), slice(raw, 50, 36));
        assertArrayEquals(new byte[]{0x26, 0x05, 0x08, 0x12, 0x01, 0x02}, slice(raw, 86, 6));
        assertArrayEquals(new byte[6], slice(raw, 92, 6));
        assertEquals(0x01, raw[98] & 0xFF);
        assertEquals(0x0E, raw[99] & 0xFF);
        assertEquals(0x7E, raw[raw.length - 1] & 0xFF);
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