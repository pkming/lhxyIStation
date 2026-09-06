package com.lhxy.istationdevice.android11.domain.module;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DispatchBusinessModuleTest {
    @Test
    public void positionReportKeepsZeroBasedStationNumber() {
        assertEquals(4, DispatchBusinessModule.positionStationNumber(4));
        assertEquals(0, DispatchBusinessModule.positionStationNumber(-1));
    }

    @Test
    public void reportStationUsesOneBasedNumberForBothPreviewAndArrival() {
        // Internal station 0 is the first station; both preview and arrival report busNo=1.
        assertEquals(1, DispatchBusinessModule.reportStationBusNumber(0, 1));
        assertEquals(1, DispatchBusinessModule.reportStationBusNumber(0, 0));
        
        // Internal station 3 is the fourth station; both preview and arrival report busNo=4.
        assertEquals(4, DispatchBusinessModule.reportStationBusNumber(3, 1));
        assertEquals(4, DispatchBusinessModule.reportStationBusNumber(3, 0));
    }

    @Test
    public void reportStationFollowsCorrectStationProgression() {
        // Station 0 (first station): busNo=1 for both phases
        assertEquals(1, DispatchBusinessModule.reportStationBusNumber(0, 0));
        assertEquals(1, DispatchBusinessModule.reportStationBusNumber(0, 1));
        
        // Station 1 (second station): busNo=2 for both phases
        assertEquals(2, DispatchBusinessModule.reportStationBusNumber(1, 0));
        assertEquals(2, DispatchBusinessModule.reportStationBusNumber(1, 1));
        
        // Station 2 (third station): busNo=3 for both phases
        assertEquals(3, DispatchBusinessModule.reportStationBusNumber(2, 0));
        assertEquals(3, DispatchBusinessModule.reportStationBusNumber(2, 1));
    }

    @Test
    public void stationReportKeyKeepsSeparateEventsWithSameStationState() {
        String first = DispatchBusinessModule.stationReportKey(3, 0, 1, 7);
        String repeated = DispatchBusinessModule.stationReportKey(3, 0, 1, 8);

        org.junit.Assert.assertNotEquals(first, repeated);
    }

    @Test
    public void cc808PeriodicReportRequiresValidGps() {
        org.junit.Assert.assertFalse(DispatchBusinessModule.shouldSendPeriodicStationReport(true, false));
        org.junit.Assert.assertTrue(DispatchBusinessModule.shouldSendPeriodicStationReport(true, true));
        org.junit.Assert.assertTrue(DispatchBusinessModule.shouldSendPeriodicStationReport(false, false));
    }

    @Test
    public void cc808PassesLegacyOneTwoRouteDirectionToEncoders() {
        assertEquals(1, DispatchBusinessModule.cc808RouteDirectionValue("上行"));
        assertEquals(2, DispatchBusinessModule.cc808RouteDirectionValue("下行"));
    }

    @Test
    public void jq808KeepsOneTwoDirectionEncoding() {
        assertEquals(1, DispatchBusinessModule.jq808DirectionValue("上行"));
        assertEquals(2, DispatchBusinessModule.jq808DirectionValue("下行"));
    }
}
