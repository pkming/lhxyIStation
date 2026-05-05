package com.lhxy.istationdevice.android11.domain.module.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;

public class StationStateTest {

    @Test
    public void quickStep_respectsStartAndTerminalBoundaries() {
        StationState state = createState();

        assertFalse(state.quickStepBackward());
        assertEquals(0, state.getCurrentStationNo());
        assertFalse(state.isPreviewingNext());

        assertTrue(state.quickStepForward());
        assertEquals(1, state.getCurrentStationNo());
        assertTrue(state.isPreviewingNext());
        assertEquals("B站", state.getCurrentStation());

        assertTrue(state.quickStepForward());
        assertEquals(1, state.getCurrentStationNo());
        assertFalse(state.isPreviewingNext());

        assertTrue(state.quickStepForward());
        assertTrue(state.quickStepForward());
        assertTrue(state.quickStepForward());
        assertTrue(state.quickStepForward());
        assertEquals(3, state.getCurrentStationNo());
        assertFalse(state.isPreviewingNext());
        assertEquals("D站", state.getCurrentStation());

        assertFalse(state.quickStepForward());

        assertTrue(state.quickStepBackward());
        assertEquals(3, state.getCurrentStationNo());
        assertTrue(state.isPreviewingNext());

        assertTrue(state.quickStepBackward());
        assertEquals(2, state.getCurrentStationNo());
        assertFalse(state.isPreviewingNext());
        assertEquals("C站", state.getCurrentStation());
    }

    @Test
    public void advanceStation_keepsTerminalArrivalStableAfterQuickStepArrival() {
        StationState state = createState();

        assertTrue(state.quickStepForward());
        assertTrue(state.quickStepForward());
        assertTrue(state.quickStepForward());
        assertTrue(state.quickStepForward());
        assertTrue(state.quickStepForward());
        assertTrue(state.quickStepForward());
        assertEquals(3, state.getCurrentStationNo());
        assertFalse(state.isPreviewingNext());

        state.advanceStation();

        assertEquals(3, state.getCurrentStationNo());
        assertFalse(state.isPreviewingNext());
        assertEquals("D站", state.getCurrentStation());
        assertEquals("终点到站", state.getReportPhase());
    }

    @Test
    public void advanceStation_matchesLegacyTwoPhaseFlowAtTerminal() {
        StationState state = createState();

        state.advanceStation();
        assertEquals(1, state.getCurrentStationNo());
        assertTrue(state.isPreviewingNext());
        assertEquals("B站", state.getCurrentStation());

        state.advanceStation();
        assertEquals(1, state.getCurrentStationNo());
        assertFalse(state.isPreviewingNext());
        assertEquals("B站", state.getCurrentStation());

        state.advanceStation();
        state.advanceStation();
        state.advanceStation();
        assertEquals(3, state.getCurrentStationNo());
        assertTrue(state.isPreviewingNext());
        assertEquals("D站", state.getCurrentStation());

        state.advanceStation();
        assertEquals(3, state.getCurrentStationNo());
        assertFalse(state.isPreviewingNext());
        assertEquals("终点到站", state.getReportPhase());
    }

    @Test
    public void repeatCurrentStation_isNoOpWhilePreviewingNext() {
        StationState state = createState();

        assertTrue(state.quickStepForward());
        assertTrue(state.isPreviewingNext());
        assertEquals("起点发车", state.getReportPhase());

        assertFalse(state.repeatCurrentStation());
        assertTrue(state.isPreviewingNext());
        assertEquals(1, state.getCurrentStationNo());
        assertEquals("起点发车", state.getReportPhase());
    }

    @Test
    public void firstDeparturePreview_matchesLegacyFirstStationLeaveState() {
        StationState state = createState();

        state.advanceStation();
        assertTrue(state.isFirstDeparturePreview());
        assertEquals(1, state.getCurrentStationNo());
        assertTrue(state.isPreviewingNext());
        assertEquals("起点发车", state.getReportPhase());

        state.advanceStation();
        assertFalse(state.isFirstDeparturePreview());
    }

    @Test
    public void autoStation_leaveFromFirstStation_isFirstDeparturePreview() {
        StationState state = createState();

        state.recordAutoStation(1, "B站", 1);

        assertTrue(state.isFirstDeparturePreview());
        assertEquals(1, state.getCurrentStationNo());
        assertTrue(state.isPreviewingNext());
    }

    @Test
    public void recordDirectionSwitch_resetsToNewDirectionStartState() {
        StationState state = createState();

        state.advanceStation();
        state.advanceStation();
        assertEquals(1, state.getCurrentStationNo());
        assertFalse(state.isPreviewingNext());

        state.recordDirectionSwitch("下行", Arrays.asList("D站", "C站", "B站", "A站"));

        assertEquals("下行", state.getDirectionText());
        assertEquals(0, state.getCurrentStationNo());
        assertFalse(state.isPreviewingNext());
        assertEquals("D站", state.getCurrentStation());
        assertEquals("C站", state.getNextStation());
        assertEquals("自动切向", state.getReportPhase());
    }

    private StationState createState() {
        StationState state = new StationState();
        state.applyLineProfile("测试线", "上行", Arrays.asList("A站", "B站", "C站", "D站"));
        return state;
    }
}