package com.lhxy.istationdevice.android11.devicem90;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.CameraAdapter;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

import java.util.Map;
import java.util.Collections;
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
    private final Map<String, CameraCaptureSession> previewSessions = new ConcurrentHashMap<>();
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
    public void openPreview(String cameraId, Surface surface, int width, int height, String traceId) {
        Context context = appContext;
        if (surface == null || !surface.isValid()) {
            throw new IllegalStateException("Camera preview surface is not ready");
        }
        if (context == null) {
            throw new IllegalStateException("Camera context 杩樻病鍒濆鍖?");
        }
        if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            throw new IllegalStateException("娌℃湁 CAMERA 鏉冮檺");
        }

        ShellConfig.CameraChannel cameraChannel = requireChannel(cameraId);
        close(cameraChannel.getKey(), traceId + "-restart");

        CameraManager cameraManager = context.getSystemService(CameraManager.class);
        if (cameraManager == null) {
            throw new IllegalStateException("鎷夸笉鍒?CameraManager");
        }

        try {
            cameraManager.openCamera(cameraChannel.getCameraId(), context.getMainExecutor(), new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice cameraDevice) {
                    openedDevices.put(cameraChannel.getKey(), cameraDevice);
                    createPreviewSession(cameraChannel, cameraDevice, surface, width, height, traceId);
                }

                @Override
                public void onDisconnected(CameraDevice cameraDevice) {
                    openedDevices.remove(cameraChannel.getKey());
                    closeSession(cameraChannel.getKey());
                    cameraDevice.close();
                    AppLogCenter.log(LogCategory.DEVICE, LogLevel.WARN, TAG, "camera disconnected: " + cameraChannel.getKey(), traceId);
                }

                @Override
                public void onError(CameraDevice cameraDevice, int error) {
                    openedDevices.remove(cameraChannel.getKey());
                    closeSession(cameraChannel.getKey());
                    cameraDevice.close();
                    AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "camera preview open failed " + cameraChannel.getKey() + " / error=" + error, traceId);
                }
            });
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "camera preview open failed " + cameraChannel.getKey() + ": " + e.getMessage(), traceId);
            throw new IllegalStateException("鎵撳紑 Camera 棰勮澶辫触: " + cameraChannel.getKey() + " / " + e.getMessage(), e);
        }
    }

    @Override
    public void close(String cameraId, String traceId) {
        ShellConfig.CameraChannel cameraChannel = requireChannel(cameraId);
        closeSession(cameraChannel.getKey());
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

    private void createPreviewSession(
            ShellConfig.CameraChannel cameraChannel,
            CameraDevice cameraDevice,
            Surface surface,
            int width,
            int height,
            String traceId
    ) {
        try {
            CaptureRequest.Builder requestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            requestBuilder.addTarget(surface);
            requestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
            Handler mainHandler = new Handler(Looper.getMainLooper());
            cameraDevice.createCaptureSession(
                    Collections.singletonList(surface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            try {
                                previewSessions.put(cameraChannel.getKey(), session);
                                session.setRepeatingRequest(requestBuilder.build(), null, mainHandler);
                                AppLogCenter.log(
                                        LogCategory.DEVICE,
                                        LogLevel.INFO,
                                        TAG,
                                        "camera preview started: " + cameraChannel.getKey()
                                                + " -> " + cameraChannel.getCameraId()
                                                + " / surface=" + width + "x" + height,
                                        traceId
                                );
                            } catch (Exception e) {
                                AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "camera preview request failed " + cameraChannel.getKey() + ": " + e.getMessage(), traceId);
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "camera preview configure failed: " + cameraChannel.getKey(), traceId);
                        }
                    },
                    mainHandler
            );
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "camera preview session failed " + cameraChannel.getKey() + ": " + e.getMessage(), traceId);
        }
    }

    private void closeSession(String channelKey) {
        CameraCaptureSession session = previewSessions.remove(channelKey);
        if (session != null) {
            try {
                session.stopRepeating();
            } catch (Exception ignore) {
            }
            session.close();
        }
    }

    private ShellConfig.CameraChannel requireChannel(String key) {
        ShellConfig.CameraChannel channel = channelMap.get(key);
        if (channel != null) {
            return channel;
        }
        throw new IllegalArgumentException("Camera 未配置: " + key);
    }
}
