package com.lhxy.istationdevice.android11.deviceapi;

/**
 * 系统能力抽象接口
 * <p>
 * 这里专门收静默安装、重启、校时这类系统级动作，
 * 业务和页面层不直接执行 shell 命令。
 */
public interface SystemOps {
    /**
     * 当前是否支持静默安装。
     */
    boolean supportsSilentInstall();

    /**
     * 请求系统重启。
     */
    void reboot(String reason, String traceId);

    /**
     * 请求设置系统时间。
     */
    void setSystemTime(long timeMillis, String traceId);
}
