package com.lhxy.istationdevice.android11.protocol.jt808;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.lhxy.istationdevice.android11.core.Hexs;

import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class Jt808TextMessageCommandParserTest {

    @Test
    public void parse_fieldFrame_decodesDisplayAndSpeakMessage() {
        byte[] rawFrame = Hexs.fromHex("7E 83 00 00 03 01 86 12 34 56 78 00 0B 0C 68 69 09 7E");

        Jt808TextMessageCommand command = Jt808TextMessageCommandParser.parse(Jt808FrameDecoder.decode(rawFrame));

        assertEquals(0x000B, command.getRequestSerialNumber());
        assertEquals("018612345678", command.getTerminalId());
        assertEquals(0x0C, command.getFlag());
        assertEquals("hi", command.getContent());
        assertTrue(command.shouldDisplay());
        assertTrue(command.shouldSpeak());
    }

    @Test
    public void parse_gbkChinese_preservesTextAndFlagSemantics() {
        byte[] text = "平台测试消息".getBytes(Charset.forName("GBK"));
        byte[] body = new byte[text.length + 1];
        body[0] = 0x04;
        System.arraycopy(text, 0, body, 1, text.length);

        Jt808TextMessageCommand command = Jt808TextMessageCommandParser.parse(
                new Jt808Frame(Jt808Variant.JT808, 0x8300, "018612345678", 28, body)
        );

        assertEquals("平台测试消息", command.getContent());
        assertTrue(command.shouldDisplay());
        assertFalse(command.shouldSpeak());
    }

    @Test
    public void parse_secondFieldFrame_decodesFullAsciiContent() {
        byte[] rawFrame = Hexs.fromHex(
                "7E 83 00 00 07 01 86 12 34 56 78 00 1C 0C 6E 69 20 68 61 6F 5A 7E"
        );

        Jt808TextMessageCommand command = Jt808TextMessageCommandParser.parse(Jt808FrameDecoder.decode(rawFrame));

        assertEquals(0x001C, command.getRequestSerialNumber());
        assertEquals("ni hao", command.getContent());
        assertTrue(command.shouldDisplay());
        assertTrue(command.shouldSpeak());
    }

    @Test
    public void streamReplay_fragmentedFieldFrame_dispatchesAfterCompletePacket() {
        byte[] rawFrame = Hexs.fromHex("7E 83 00 00 03 01 86 12 34 56 78 00 0B 0C 68 69 09 7E");
        Jt808FrameStreamParser streamParser = new Jt808FrameStreamParser();

        List<byte[]> firstResult = streamParser.accept(Arrays.copyOfRange(rawFrame, 0, 7));
        List<byte[]> secondResult = streamParser.accept(Arrays.copyOfRange(rawFrame, 7, rawFrame.length));

        assertTrue(firstResult.isEmpty());
        assertEquals(1, secondResult.size());
        Jt808TextMessageCommand command = Jt808TextMessageCommandParser.parse(
                Jt808FrameDecoder.decode(secondResult.get(0))
        );
        assertEquals("hi", command.getContent());
    }

    @Test
    public void speakOnlyFlag_doesNotRequestDisplay() {
        Jt808TextMessageCommand command = Jt808TextMessageCommandParser.parse(
                new Jt808Frame(
                        Jt808Variant.JT808,
                        0x8300,
                        "018612345678",
                        1,
                        new byte[]{0x08, 0x68, 0x69}
                )
        );

        assertFalse(command.shouldDisplay());
        assertTrue(command.shouldSpeak());
        assertTrue(command.hasSupportedAction());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_emptyText_rejectsMalformedMessage() {
        Jt808TextMessageCommandParser.parse(
                new Jt808Frame(Jt808Variant.JT808, 0x8300, "018612345678", 1, new byte[]{0x0C})
        );
    }

    @Test
    public void generalResponse_forFieldFrameReferences8300Request() {
        Jt808LegacyMessages messages = new Jt808LegacyMessages();
        Jt808Frame response = messages.createGeneralResponse(
                Jt808Variant.JT808,
                "018612345678",
                new Jt808GeneralResponse(0x000B, 0x8300, 0)
        );

        assertArrayEquals(new byte[]{0x00, 0x0B, (byte) 0x83, 0x00, 0x00}, response.getBody());
        Jt808Frame decoded = Jt808FrameDecoder.decode(messages.encode(response));
        assertEquals(0x0001, decoded.getMessageId());
        assertArrayEquals(response.getBody(), decoded.getBody());
    }
}
