package com.lhxy.istationdevice.android11.devicem90;

import android.content.Context;

import com.lhxy.istationdevice.android11.deviceapi.CameraAdapter;
import com.lhxy.istationdevice.android11.deviceapi.DeviceMode;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

/**
 * M90 Camera 管理适配器
 * <p>
 * 统一处理 stub / real 切换和 Context 注入。
 */
public final class M90ManagedCameraAdapter implements CameraAdapter {
    private final M90StubCameraAdapter stubAdapter = new M90StubCameraAdapter();
    private final M90RealCameraAdapter realAdapter = new M90RealCameraAdapter();
    private volatile ShellConfig.CameraConfig cameraConfig = ShellConfig.CameraConfig.empty();
    private volatile Context appContext;

    /**
     * 更新 Context 和 Camera 配置。
     */
    public void updateConfig(Context context, ShellConfig.CameraConfig cameraConfig) {
        this.appContext = context == null ? null : context.getApplicationContext();
        this.cameraConfig = cameraConfig == null ? ShellConfig.CameraConfig.empty() : cameraConfig;
        stubAdapter.applyConfig(this.cameraConfig);
        realAdapter.updateConfig(this.appContext, this.cameraConfig);
    }

    @Override
    public void open(String cameraId, String traceId) {
        delegate().open(cameraId, traceId);
    }

    @Override
    public void close(String cameraId, String traceId) {
        delegate().close(cameraId, traceId);
    }

    /**
     * 输出 Camera 当前状态摘要。
     */
    public String describeStatus() {
        int openedCount = cameraConfig.getMode() == DeviceMode.REAL ? realAdapter.getOpenedCount() : stubAdapter.getOpenedCount();
        return "Camera -> mode=" + cameraConfig.getMode().toConfigValue()
                + " / channels=" + cameraConfig.getChannels().size()
                + " / opened=" + openedCount
                + " / ctx=" + (appContext == null ? "missing" : "ready");
    }

    private CameraAdapter delegate() {
        return cameraConfig.getMode() == DeviceMode.REAL ? realAdapter : stubAdapter;
    }
}
