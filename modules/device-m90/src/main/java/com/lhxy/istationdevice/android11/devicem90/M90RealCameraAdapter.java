package com.lhxy.istationdevice.android11.devicem90;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.CameraAdapter;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * M90 真 Camera 适配器
 * <p>
 * 这版先走 Camera2 打开/关闭物理通道，用来确认 cameraId 和权限通路。
 * 真正预览、切流和多路画面后面再往上层接。
 */
public final class M90RealCameraAdapter implements CameraAdapter {
    private static final String TAG = "M90RealCamera";

    private final Map<String, ShellConfig.CameraChannel> channelMap = new ConcurrentHashMap<>();
    private final Map<String, CameraDevice> openedDevices = new ConcurrentHashMap<>();
    private volatile Context appContext;

    /**
     * 更新 Context 和 Camera 配置。
     */
    public void updateConfig(Context context, ShellConfig.CameraConfig cameraConfig) {
        this.appContext = context == null ? null : context.getApplicationContext();
        channelMap.clear();
        if (cameraConfig == null) {
            return;
        }
        channelMap.putAll(cameraConfig.getChannels());
    }

    @Override
    public void open(String cameraId, String traceId) {
        Context context = appContext;
        if (context == null) {
            throw new IllegalStateException("Camera context 还没初始化");
        }
        if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            throw new IllegalStateException("没有 CAMERA 权限");
        }

        ShellConfig.CameraChannel cameraChannel = requireChannel(cameraId);
        if (openedDevices.containsKey(cameraChannel.getKey())) {
            AppLogCenter.log(LogCategory.DEVICE, LogLevel.DEBUG, TAG, "camera already open: " + cameraChannel.getKey(), traceId);
            return;
        }

        CameraManager cameraManager = context.getSystemService(CameraManager.class);
        if (cameraManager == null) {
            throw new IllegalStateException("拿不到 CameraManager");
        }

        try {
            cameraManager.openCamera(cameraChannel.getCameraId(), context.getMainExecutor(), new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice cameraDevice) {
                    openedDevices.put(cameraChannel.getKey(), cameraDevice);
                    AppLogCenter.log(LogCategory.DEVICE, LogLevel.INFO, TAG, "camera opened: " + cameraChannel.getKey() + " -> " + cameraChannel.getCameraId(), traceId);
                }

                @Override
                public void onDisconnected(CameraDevice cameraDevice) {
                    openedDevices.remove(cameraChannel.getKey());
                    cameraDevice.close();
                    AppLogCenter.log(LogCategory.DEVICE, LogLevel.WARN, TAG, "camera disconnected: " + cameraChannel.getKey(), traceId);
                }

                @Override
                public void onError(CameraDevice cameraDevice, int error) {
                    openedDevices.remove(cameraChannel.getKey());
                    cameraDevice.close();
                    AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "camera open failed " + cameraChannel.getKey() + " / error=" + error, traceId);
                }
            });
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "camera open failed " + cameraChannel.getKey() + ": " + e.getMessage(), traceId);
            throw new IllegalStateException("打开 Camera 失败: " + cameraChannel.getKey() + " / " + e.getMessage(), e);
        }
    }

    @Override
    public void close(String cameraId, String traceId) {
        ShellConfig.CameraChannel cameraChannel = requireChannel(cameraId);
        CameraDevice cameraDevice = openedDevices.remove(cameraChannel.getKey());
        if (cameraDevice == null) {
            AppLogCenter.log(LogCategory.DEVICE, LogLevel.DEBUG, TAG, "camera already closed: " + cameraChannel.getKey(), traceId);
            return;
        }
        cameraDevice.close();
        AppLogCenter.log(LogCategory.DEVICE, LogLevel.INFO, TAG, "camera closed: " + cameraChannel.getKey(), traceId);
    }

    /**
     * 返回当前已打开的 Camera 数量。
     */
    public int getOpenedCount() {
        return openedDevices.size();
    }

    private ShellConfig.CameraChannel requireChannel(String key) {
        ShellConfig.CameraChannel channel = channelMap.get(key);
        if (channel != null) {
            return channel;
        }
        throw new IllegalArgumentException("Camera 未配置: " + key);
    }
}
