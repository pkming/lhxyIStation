package com.lhxy.istationdevice.android11.domain.module.state;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 调度业务状态。
 * <p>
 * 统一收口当前调度提示、班次计划和运营加入状态，
 * 后面首页、模块页和正式业务页都直接复用这份状态。
 */
public final class DispatchState {
    private String activeProtocol = "JT808/AL808";
    private String dispatchMessage = "等待调度消息";
    private String scheduleNo = "-";
    private String plannedDepartureTime = "-";
    private String plannedArrivalTime = "-";
    private boolean joinedOperation;
    private boolean dispatchedConfirmed;
    private boolean startedBus;
    private boolean requestedCharge;
    private boolean reportedVehicleFailure;
    private int replayCount;
    private long lineGuid = 1001L;
    private int timesNo = 1;
    private long lastMsgSerialNo = 1L;
    private long pendingNoticeMsgSerialNo;
    private String pendingNoticeMessage = "-";
    private boolean pendingNoticeAcked = true;
    private long lastUpdateTimeMillis;

    /**
     * 标记收到一轮调度样例。
     */
    public void markReplay(String protocolLabel, boolean fullReplay, int count) {
        activeProtocol = emptyAsDash(protocolLabel);
        replayCount = count;
        lineGuid = Math.max(1001L, 1000L + count);
        timesNo = 1;
        lastMsgSerialNo = System.currentTimeMillis() / 1000L;
        joinedOperation = true;
        scheduleNo = fullReplay ? "FULL-" + count : "SCH-" + count;
        dispatchMessage = fullReplay ? "已回放全量调度协议，等待人工联调确认" : "收到新的调度信息，请按计划时间发车";
        long now = System.currentTimeMillis();
        plannedDepartureTime = formatTime(now + 3 * 60_000L);
        plannedArrivalTime = formatTime(now + 18 * 60_000L);
        dispatchedConfirmed = false;
        startedBus = false;
        requestedCharge = false;
        reportedVehicleFailure = false;
        markNoticeReceived("收到新的调度公告，请司机确认并按计划发车", lastMsgSerialNo + 1);
        lastUpdateTimeMillis = now;
    }

    /**
     * 标记运营加入或退出。
     */
    public void markOperation(boolean joined, String message) {
        joinedOperation = joined;
        dispatchMessage = emptyAsDash(message);
        lastUpdateTimeMillis = System.currentTimeMillis();
    }

    public void confirmDispatch() {
        dispatchedConfirmed = true;
        dispatchMessage = "已确认调度消息，等待到点发车";
        lastUpdateTimeMillis = System.currentTimeMillis();
    }

    public void markStartBus() {
        startedBus = true;
        joinedOperation = true;
        dispatchedConfirmed = true;
        dispatchMessage = "已执行发车，车辆进入运营";
        lastUpdateTimeMillis = System.currentTimeMillis();
    }

    public void requestCharge() {
        requestedCharge = true;
        dispatchMessage = "已提交充电请求，等待平台确认";
        lastUpdateTimeMillis = System.currentTimeMillis();
    }

    public void reportVehicleFailure() {
        reportedVehicleFailure = true;
        dispatchMessage = "已上报车辆故障，等待调度处置";
        lastUpdateTimeMillis = System.currentTimeMillis();
    }

    public void markNoticeReceived(String message, long msgSerialNo) {
        pendingNoticeMessage = emptyAsDash(message);
        pendingNoticeMsgSerialNo = Math.max(1L, msgSerialNo);
        pendingNoticeAcked = false;
        lastUpdateTimeMillis = System.currentTimeMillis();
    }

    public void applyDvrDispatchRequest(
            long msgSerialNo,
            long lineGuid,
            int directionCode,
            int scheduleNo,
            int timesNo,
            String departureTime,
            String lineName
    ) {
        activeProtocol = "RS232-1/DVR";
        lastMsgSerialNo = Math.max(1L, msgSerialNo);
        this.lineGuid = Math.max(0L, lineGuid);
        this.timesNo = Math.max(0, timesNo);
        joinedOperation = true;
        dispatchedConfirmed = false;
        startedBus = false;
        requestedCharge = false;
        reportedVehicleFailure = false;
        this.scheduleNo = scheduleNo <= 0 ? "-" : String.valueOf(scheduleNo);
        plannedDepartureTime = normalizeCompactTime(departureTime);
        dispatchMessage = "收到新的调度信息，请按计划时间发车"
                + ("-".equals(emptyAsDash(lineName)) ? "" : " / " + emptyAsDash(lineName))
                + " / dir=" + directionCode;
        lastUpdateTimeMillis = System.currentTimeMillis();
    }

    public void acknowledgeNotice() {
        pendingNoticeAcked = true;
        dispatchMessage = "已确认下发公告，等待后续调度";
        lastUpdateTimeMillis = System.currentTimeMillis();
    }

    public String getActiveProtocol() {
        return activeProtocol;
    }

    public String getDispatchMessage() {
        return dispatchMessage;
    }

    public String getScheduleNo() {
        return scheduleNo;
    }

    public String getPlannedDepartureTime() {
        return plannedDepartureTime;
    }

    public String getPlannedArrivalTime() {
        return plannedArrivalTime;
    }

    public boolean isJoinedOperation() {
        return joinedOperation;
    }

    public boolean isDispatchedConfirmed() {
        return dispatchedConfirmed;
    }

    public boolean isStartedBus() {
        return startedBus;
    }

    public boolean isRequestedCharge() {
        return requestedCharge;
    }

    public boolean isReportedVehicleFailure() {
        return reportedVehicleFailure;
    }

    public int getReplayCount() {
        return replayCount;
    }

    public long getLineGuid() {
        return lineGuid;
    }

    public int getTimesNo() {
        return timesNo;
    }

    public long getLastMsgSerialNo() {
        return lastMsgSerialNo;
    }

    public long getPendingNoticeMsgSerialNo() {
        return pendingNoticeMsgSerialNo;
    }

    public String getPendingNoticeMessage() {
        return pendingNoticeMessage;
    }

    public boolean isPendingNoticeAcked() {
        return pendingNoticeAcked;
    }

    public int getScheduleNoValue() {
        if (scheduleNo == null) {
            return 0;
        }
        String digits = scheduleNo.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(digits);
        } catch (Exception ignore) {
            return 0;
        }
    }

    public long getLastUpdateTimeMillis() {
        return lastUpdateTimeMillis;
    }

    public String describe() {
        return "protocol=" + emptyAsDash(activeProtocol)
                + "\n- joinedOperation=" + (joinedOperation ? "是" : "否")
                + "\n- confirmed=" + (dispatchedConfirmed ? "是" : "否")
                + "\n- startedBus=" + (startedBus ? "是" : "否")
                + "\n- requestedCharge=" + (requestedCharge ? "是" : "否")
                + "\n- vehicleFailure=" + (reportedVehicleFailure ? "是" : "否")
                + "\n- scheduleNo=" + emptyAsDash(scheduleNo)
                + "\n- departure=" + emptyAsDash(plannedDepartureTime)
                + "\n- arrival=" + emptyAsDash(plannedArrivalTime)
                + "\n- pendingNotice=" + emptyAsDash(pendingNoticeMessage)
                + " / acked=" + (pendingNoticeAcked ? "是" : "否")
                + "\n- message=" + emptyAsDash(dispatchMessage);
    }

    private String formatTime(long timeMillis) {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(timeMillis));
    }

    private String normalizeCompactTime(String value) {
        String safeValue = emptyAsDash(value);
        if ("-".equals(safeValue) || safeValue.length() < 4) {
            return "-";
        }
        return safeValue.substring(0, 2) + ":" + safeValue.substring(2, 4);
    }

    private String emptyAsDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }
}
