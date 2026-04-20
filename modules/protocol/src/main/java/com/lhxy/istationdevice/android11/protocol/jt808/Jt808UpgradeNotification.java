package com.lhxy.istationdevice.android11.protocol.jt808;

/**
 * 升级通知参数
 */
public final class Jt808UpgradeNotification {
    private final int responseSerialNumber;
    private final int upgradeStatus;
    private final int upgradeProgress;

    public Jt808UpgradeNotification(int responseSerialNumber, int upgradeStatus, int upgradeProgress) {
        this.responseSerialNumber = responseSerialNumber;
        this.upgradeStatus = upgradeStatus;
        this.upgradeProgress = upgradeProgress;
    }

    public int getResponseSerialNumber() {
        return responseSerialNumber;
    }

    public int getUpgradeStatus() {
        return upgradeStatus;
    }

    public int getUpgradeProgress() {
        return upgradeProgress;
    }
}
