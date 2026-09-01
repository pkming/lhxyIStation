package com.lhxy.istationdevice.android11.domain.module.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DispatchStateTest {

    @Test
    public void defaultsToNoPlannedTripLikeLegacyPreferences() {
        assertEquals(0, new DispatchState().getTimesNo());
    }

    @Test
    public void confirmDispatch_marksConfirmedWithoutStartingBus() {
        DispatchState state = new DispatchState();

        state.markOperation(true, "已加入运营");
        state.confirmDispatch();

        assertTrue(state.isJoinedOperation());
        assertTrue(state.isDispatchedConfirmed());
        assertFalse(state.isStartedBus());
        assertEquals("已确认调度消息，等待到点发车", state.getDispatchMessage());
    }

    @Test
    public void acknowledgeNotice_clearsPendingNotice() {
        DispatchState state = new DispatchState();

        state.markNoticeReceived("收到新的调度公告", 12L);
        assertFalse(state.isPendingNoticeAcked());

        state.acknowledgeNotice();

        assertTrue(state.isPendingNoticeAcked());
        assertEquals("已确认下发公告，等待后续调度", state.getDispatchMessage());
    }

    @Test
    public void startBus_marksOperationRunning() {
        DispatchState state = new DispatchState();

        state.confirmDispatch();
        state.markStartBus();

        assertTrue(state.isJoinedOperation());
        assertTrue(state.isDispatchedConfirmed());
        assertTrue(state.isStartedBus());
        assertEquals("已执行发车，车辆进入运营", state.getDispatchMessage());
    }

    @Test
    public void leaveOperation_clearsCurrentDispatchRound() {
        DispatchState state = new DispatchState();

        state.markOperation(true, "已加入运营");
        state.confirmDispatch();
        state.requestCharge();
        state.reportVehicleFailure();
        state.markNoticeReceived("收到新的调度公告", 12L);

        state.markOperation(false, "已退出运营，等待重新签到或调度恢复");

        assertFalse(state.isJoinedOperation());
        assertFalse(state.isDispatchedConfirmed());
        assertFalse(state.isStartedBus());
        assertFalse(state.isRequestedCharge());
        assertFalse(state.isReportedVehicleFailure());
        assertTrue(state.isPendingNoticeAcked());
        assertEquals("-", state.getPendingNoticeMessage());
        assertEquals(0L, state.getPendingNoticeMsgSerialNo());
        assertEquals("已退出运营，等待重新签到或调度恢复", state.getDispatchMessage());
    }

    @Test
    public void applyPlatformDispatchPlan_updatesTripTimeAndReminderSettings() {
        DispatchState state = new DispatchState();

        state.applyPlatformDispatchPlan(40, 3, "090807", 10, 2, 3, "计划第3趟09点08分07秒发车");

        assertTrue(state.isJoinedOperation());
        assertTrue(state.isDispatchedConfirmed());
        assertFalse(state.isStartedBus());
        assertEquals(3, state.getTimesNo());
        assertEquals("09:08", state.getPlannedDepartureTime());
        assertEquals(10, state.getOvertimeMinutes());
        assertEquals(2, state.getOvertimeSpeakIntervalMinutes());
        assertEquals(3, state.getPrepareSpeakIntervalMinutes());
    }

    @Test
    public void cancelPlatformPlan_clearsDepartureAndConfirmation() {
        DispatchState state = new DispatchState();
        state.applyPlatformDispatchPlan(40, 3, "090807", 10, 2, 3, "第3趟09点08分07秒");

        state.cancelPlatformPlan();

        assertFalse(state.isDispatchedConfirmed());
        assertFalse(state.isStartedBus());
        assertEquals("-", state.getPlannedDepartureTime());
        assertEquals("取消计划成功", state.getDispatchMessage());
    }

    @Test
    public void updateTripMessages_keepsMissingValuesAndUpdatesPresentValues() {
        DispatchState state = new DispatchState();

        state.updateTripMessages("下趟10:00", "本趟09:00", "-");
        state.updateTripMessages("-", "-", "明日8趟");

        assertEquals("下趟10:00", state.getNextTrip());
        assertEquals("本趟09:00", state.getThisTrip());
        assertEquals("明日8趟", state.getTomorrow());
    }
}
