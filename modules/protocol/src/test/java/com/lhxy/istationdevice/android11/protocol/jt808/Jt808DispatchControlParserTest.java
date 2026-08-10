package com.lhxy.istationdevice.android11.protocol.jt808;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.nio.charset.Charset;

public class Jt808DispatchControlParserTest {

    @Test
    public void parse_cancelPlan_readsLineAndType() {
        Jt808DispatchControlCommand command = Jt808DispatchControlCommandParser.parse(
                new Jt808Frame(
                        Jt808Variant.JT808,
                        0x8B02,
                        "018612345678",
                        12,
                        new byte[]{0x00, 0x00, 0x27, 0x10, (byte) 0x8C}
                )
        );

        assertEquals(10000L, command.getLineNumber());
        assertEquals(0x8C, command.getType());
    }

    @Test
    public void parse_tripUpdate_decodesGbkJson() {
        String json = "{\"nextTrip\":\"下趟10:00\",\"thisTrip\":\"本趟09:00\",\"tomorrow\":\"明日8趟\"}";
        byte[] jsonBytes = json.getBytes(Charset.forName("GBK"));
        byte[] body = new byte[jsonBytes.length + 5];
        body[4] = (byte) 0x8D;
        System.arraycopy(jsonBytes, 0, body, 5, jsonBytes.length);

        Jt808DispatchControlCommand command = Jt808DispatchControlCommandParser.parse(
                new Jt808Frame(Jt808Variant.JT808, 0x8B02, "018612345678", 13, body)
        );

        assertEquals("下趟10:00", command.getNextTrip());
        assertEquals("本趟09:00", command.getThisTrip());
        assertEquals("明日8趟", command.getTomorrow());
    }

    @Test
    public void parse_professionResponse_marksResultOneAccepted() {
        Jt808ProfessionResponse response = Jt808ProfessionResponseParser.parse(
                new Jt808Frame(
                        Jt808Variant.JT808,
                        0x8B09,
                        "018612345678",
                        14,
                        new byte[]{0x12, 0x34, 0x01}
                )
        );

        assertEquals(0x1234, response.getRequestSerialNumber());
        assertTrue(response.isAccepted());
    }
}
