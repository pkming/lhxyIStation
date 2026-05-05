package com.lhxy.istationdevice.android11.domain.module.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DispatchStateTest {

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
}