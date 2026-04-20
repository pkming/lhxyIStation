package com.lhxy.istationdevice.android11.devicem90;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.CameraAdapter;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class M90StubCameraAdapter implements CameraAdapter {
    private static final String TAG = "M90StubCamera";
    private final Map<String, Boolean> openedStates = new ConcurrentHashMap<>();

    /**
     * 应用默认 Camera 配置。
     */
    public void applyConfig(ShellConfig.CameraConfig cameraConfig) {
        openedStates.clear();
        if (cameraConfig == null) {
            return;
        }
        for (Map.Entry<String, ShellConfig.CameraChannel> entry : cameraConfig.getChannels().entrySet()) {
            openedStates.put(entry.getKey(), false);
        }
    }

    @Override
    public void open(String cameraId, String traceId) {
        openedStates.put(cameraId, true);
        AppLogCenter.log(LogCategory.DEVICE, LogLevel.INFO, TAG, "stub open camera " + cameraId, traceId);
    }

    @Override
    public void close(String cameraId, String traceId) {
        openedStates.put(cameraId, false);
        AppLogCenter.log(LogCategory.DEVICE, LogLevel.INFO, TAG, "stub close camera " + cameraId, traceId);
    }

    /**
     * 返回当前打开的 Camera 数量。
     */
    public int getOpenedCount() {
        int count = 0;
        for (Boolean opened : openedStates.values()) {
            if (Boolean.TRUE.equals(opened)) {
                count++;
            }
        }
        return count;
    }
}
