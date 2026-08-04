package com.lhxy.istationdevice.android11.protocol.gps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GpsNmeaParserTest {
    private final GpsNmeaParser parser = new GpsNmeaParser();

    @Test
    public void rmcRequiresActiveStatusAndCoordinates() {
        GpsFixSnapshot voidFix = parser.parseSentence(
                "$GNRMC,103618.00,V,2240.56605,N,11403.35383,E,0.03,0.0,150726,,,N*00",
                null
        );
        GpsFixSnapshot missingCoordinates = parser.parseSentence(
                "$GNRMC,103618.00,A,,,,,0.03,0.0,150726,,,A*00",
                null
        );

        assertNotNull(voidFix);
        assertFalse(voidFix.isValid());
        assertFalse(missingCoordinates.isValid());
    }

    @Test
    public void validRmcProducesExpectedDecimalCoordinates() {
        GpsFixSnapshot snapshot = parser.parseSentence(
                "$GNRMC,103618.00,A,2240.56605,N,11403.35383,E,0.03,0.0,150726,,,A*00",
                null
        );

        assertTrue(snapshot.isValid());
        assertEquals("22.676101", snapshot.getLatitudeDecimal());
        assertEquals("114.055897", snapshot.getLongitudeDecimal());
    }

    @Test
    public void gsaCannotCreateAValidFixWithoutCoordinates() {
        GpsFixSnapshot snapshot = parser.parseSentence(
                "$GNGSA,A,3,01,02,03,04,,,,,,,,1.2,0.8,0.9*00",
                null
        );

        assertNotNull(snapshot);
        assertFalse(snapshot.isValid());
        assertEquals(3, snapshot.getFixType());
    }

    @Test
    public void anomalousGsaFixTypeDoesNotReplaceTrustedMetadata() {
        GpsFixSnapshot rmc = parser.parseSentence(
                "$GNRMC,103618.00,A,2240.56605,N,11403.35383,E,0.03,0.0,150726,,,A*00",
                null
        );
        GpsFixSnapshot gsa = parser.parseSentence(
                "$GNGSA,A,179,01,02,03,04,,,,,,,,1.2,0.8,0.9*00",
                rmc
        );

        assertTrue(gsa.isValid());
        assertEquals(rmc.getFixType(), gsa.getFixType());
    }

    @Test
    public void noFixGgaClearsCoordinatesInsteadOfRetainingOldFix() {
        GpsFixSnapshot rmc = parser.parseSentence(
                "$GNRMC,103618.00,A,2240.56605,N,11403.35383,E,0.03,0.0,150726,,,A*00",
                null
        );
        GpsFixSnapshot gga = parser.parseSentence(
                "$GNGGA,103619.00,,,,,0,00,25.5,0.0,M,0.0,M,,*00",
                rmc
        );

        assertFalse(gga.isValid());
        assertEquals("", gga.getLatitudeDecimal());
        assertEquals("", gga.getLongitudeDecimal());
    }
}
