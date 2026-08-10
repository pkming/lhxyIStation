package com.lhxy.istationdevice.android11.protocol.jt808;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

public class Jt808UpgradeCommandParserTest {
    private static final Charset GBK = Charset.forName("GBK");

    @Test
    public void parse_immediateCommand_readsTransferFields() {
        Jt808UpgradeCommand command = parseCommand(
                "升级服务器",
                8080,
                0,
                "driver",
                "secret",
                "/upgrade/M90-v33.apk",
                Jt808UpgradeCommand.UPGRADE_TYPE_IMMEDIATE,
                new byte[0]
        );

        assertEquals("升级服务器", command.getServerAddress());
        assertEquals(8080, command.getServerAddressPort());
        assertEquals("driver", command.getLoginName());
        assertEquals("secret", command.getLoginPwd());
        assertEquals(Jt808UpgradeCommand.DOWNLOAD_TYPE_APK, command.resolveDownloadType());
        assertTrue(command.isSupportedUpgradeType());
        assertFalse(command.isRestartCommand());
    }

    @Test
    public void parse_scheduledCommand_readsBcdTime() {
        Jt808UpgradeCommand command = parseCommand(
                "10.0.0.8",
                21,
                1,
                "u",
                "p",
                "/resource.zip",
                Jt808UpgradeCommand.UPGRADE_TYPE_SCHEDULED,
                new byte[]{0x26, 0x08, 0x10, 0x19, 0x30, 0x45}
        );

        assertTrue(command.isScheduledCommand());
        assertEquals("260810193045", command.getScheduleTimeBcd());
        assertEquals(Jt808UpgradeCommand.DOWNLOAD_TYPE_SOURCE_FILE, command.resolveDownloadType());
    }

    @Test
    public void parse_cancelCommand_readsTargetSerial() {
        Jt808UpgradeCommand command = parseCommand(
                "",
                0,
                0,
                "",
                "",
                "",
                Jt808UpgradeCommand.UPGRADE_TYPE_CANCEL,
                new byte[]{0x12, 0x34}
        );

        assertTrue(command.isCancelCommand());
        assertEquals("1234", command.getCancelSerialHex());
    }

    @Test
    public void parse_restartCommand_isDeferredType() {
        Jt808UpgradeCommand command = parseCommand(
                "10.0.0.8",
                80,
                0,
                "",
                "",
                "/upgrade/M90-v33.apk",
                Jt808UpgradeCommand.UPGRADE_TYPE_RESTART,
                new byte[0]
        );

        assertTrue(command.isRestartCommand());
        assertTrue(command.isSupportedUpgradeType());
    }

    @Test
    public void unknownUpgradeType_isNotSupported() {
        Jt808UpgradeCommand command = parseCommand(
                "10.0.0.8",
                80,
                0,
                "",
                "",
                "/upgrade/M90-v33.apk",
                9,
                new byte[0]
        );

        assertFalse(command.isSupportedUpgradeType());
    }

    private static Jt808UpgradeCommand parseCommand(
            String address,
            int port,
            int protocol,
            String username,
            String password,
            String url,
            int upgradeType,
            byte[] tail
    ) {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        writeText(body, address);
        body.write((port >>> 8) & 0xFF);
        body.write(port & 0xFF);
        body.write(protocol & 0xFF);
        writeText(body, username);
        writeText(body, password);
        writeText(body, url);
        body.write(upgradeType & 0xFF);
        if (tail != null) {
            body.write(tail, 0, tail.length);
        }
        return Jt808UpgradeCommandParser.parse(
                new Jt808Frame(Jt808Variant.JT808, 0x8B0A, "018612345678", 0x20, body.toByteArray())
        );
    }

    private static void writeText(ByteArrayOutputStream output, String value) {
        byte[] encoded = value == null ? new byte[0] : value.getBytes(GBK);
        output.write(encoded.length & 0xFF);
        output.write(encoded, 0, encoded.length);
    }
}
