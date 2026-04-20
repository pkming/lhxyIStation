package com.lhxy.istationdevice.android11.deviceapi;

/**
 * Camera 抽象接口
 * <p>
 * 这里先只收口逻辑通道的开关，预览和多路画面后面再往上接。
 */
public interface CameraAdapter {
    /**
     * 当前 Camera 能力是否可用。
     * <p>
     * 默认先返回 true，具体终端实现可以按权限、上下文、硬件状态再细化。
     */
    default boolean isAvailable() {
        return true;
    }

    /**
     * 打开一个逻辑 Camera 通道。
     */
    void open(String cameraId, String traceId);

    /**
     * 关闭一个逻辑 Camera 通道。
     */
    void close(String cameraId, String traceId);
}
