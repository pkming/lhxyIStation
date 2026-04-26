package com.lhxy.istationdevice.android11.domain.module.state;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 签到业务状态。
 * <p>
 * 统一保存司机、卡号和当前签到/签退状态。
 */
public final class SignInState {
    private String cardNo = "-";
    private String driverName = "未签到";
    private String attendanceMode = "待签到";
    private boolean signedIn;
    private int attendanceCount;
    private long lastAttendanceTimeMillis;

    /**
     * 处理一次刷卡结果。
     * <p>
     * 参考旧项目的习惯：同一张卡再次刷卡时切到相反状态。
     */
    public void applyCard(String rawCardNo) {
        String card = normalizeCard(rawCardNo);
        if (cardNo.equals(card) && signedIn) {
            markSignedOut();
        } else {
            signedIn = true;
            attendanceMode = "上班签到";
        }
        cardNo = card;
        driverName = "司机 " + suffix(card);
        attendanceCount++;
        lastAttendanceTimeMillis = System.currentTimeMillis();
    }

    public void manualSignOut() {
        markSignedOut();
        attendanceCount++;
        lastAttendanceTimeMillis = System.currentTimeMillis();
    }

    public void applyDriverIdentity(String rawCardNo, String rawDriverName) {
        String normalizedCard = normalizeCard(rawCardNo);
        if (!"00000000".equals(normalizedCard)) {
            cardNo = normalizedCard;
        }
        if (rawDriverName != null && !rawDriverName.trim().isEmpty() && !"-".equals(rawDriverName.trim())) {
            driverName = rawDriverName.trim();
        }
        if (!signedIn) {
            signedIn = true;
            attendanceMode = "上班签到";
        }
        lastAttendanceTimeMillis = System.currentTimeMillis();
    }

    public String getCardNo() {
        return cardNo;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getAttendanceMode() {
        return attendanceMode;
    }

    public boolean isSignedIn() {
        return signedIn;
    }

    public int getAttendanceCount() {
        return attendanceCount;
    }

    public long getLastAttendanceTimeMillis() {
        return lastAttendanceTimeMillis;
    }

    public String describe() {
        return "driverName=" + driverName
                + "\n- cardNo=" + cardNo
                + "\n- signedIn=" + (signedIn ? "是" : "否")
                + "\n- attendanceMode=" + attendanceMode
                + "\n- attendanceCount=" + attendanceCount
                + "\n- lastAttendanceTime=" + formatTime(lastAttendanceTimeMillis);
    }

    private String normalizeCard(String rawCardNo) {
        if (rawCardNo == null || rawCardNo.trim().isEmpty()) {
            return "00000000";
        }
        String card = rawCardNo.trim();
        return card.length() <= 8 ? card : card.substring(card.length() - 8);
    }

    private void markSignedOut() {
        signedIn = false;
        attendanceMode = "下班签到";
    }

    private String suffix(String card) {
        if (card == null || card.trim().isEmpty()) {
            return "----";
        }
        String value = card.trim();
        return value.length() <= 4 ? value : value.substring(value.length() - 4);
    }

    private String formatTime(long timeMillis) {
        if (timeMillis <= 0) {
            return "-";
        }
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(timeMillis));
    }
}
