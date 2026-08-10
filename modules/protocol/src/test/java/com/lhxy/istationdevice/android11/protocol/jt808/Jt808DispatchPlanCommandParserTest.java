package com.lhxy.istationdevice.android11.protocol.jt808;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.nio.charset.Charset;

public class Jt808DispatchPlanCommandParserTest {

    @Test
    public void parse_gbkJsonWithLegacyTail_extractsSchedule() {
        String json = "{\"ot\":\"10\",\"ots\":\"2\",\"sc\":\"计划第3趟09点08分07秒发车\",\"ts\":\"3\"}";
        byte[] jsonBytes = json.getBytes(Charset.forName("GBK"));
        byte[] body = new byte[jsonBytes.length + 6];
        System.arraycopy(jsonBytes, 0, body, 0, jsonBytes.length);

        Jt808DispatchPlanCommand command = Jt808DispatchPlanCommandParser.parse(
                new Jt808Frame(Jt808Variant.JT808, 0x8B01, "018612345678", 0x28, body)
        );

        assertEquals(0x28, command.getRequestSerialNumber());
        assertEquals(3, command.getTimesNo());
        assertEquals("090807", command.getDepartureTime());
        assertEquals(10, command.getOvertimeMinutes());
        assertEquals(2, command.getOvertimeSpeakIntervalMinutes());
        assertEquals(3, command.getPrepareSpeakIntervalMinutes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_invalidScheduleTime_rejectsCommand() {
        String json = "{\"sc\":\"第1趟25点00分00秒\"}";
        Jt808DispatchPlanCommandParser.parse(
                new Jt808Frame(
                        Jt808Variant.JT808,
                        0x8B01,
                        "018612345678",
                        1,
                        json.getBytes(Charset.forName("GBK"))
                )
        );
    }

    @Test
    public void parse_numericTimingAndEscapedSchedule_supported() {
        String json = "{\"ot\":10,\"ots\":3,\"ts\":5,"
                + "\"sc\":\"\\u7b2c12\\u8d9f 08\\u70b905\\u520606\\u79d2\"}";

        Jt808DispatchPlanCommand command = Jt808DispatchPlanCommandParser.parse(
                new Jt808Frame(
                        Jt808Variant.JT808,
                        0x8B01,
                        "018612345678",
                        0x29,
                        json.getBytes(Charset.forName("GBK"))
                )
        );

        assertEquals(12, command.getTimesNo());
        assertEquals("080506", command.getDepartureTime());
        assertEquals(10, command.getOvertimeMinutes());
        assertEquals(3, command.getOvertimeSpeakIntervalMinutes());
        assertEquals(5, command.getPrepareSpeakIntervalMinutes());
    }
}
