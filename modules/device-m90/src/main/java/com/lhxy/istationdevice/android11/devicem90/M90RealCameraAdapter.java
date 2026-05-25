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

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

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
    private final Map<String, String> previewOwners = new ConcurrentHashMap<>();
    private final Map<String, Long> previewGenerations = new ConcurrentHashMap<>();
    private final AtomicLong previewRequestCounter = new AtomicLong();
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
        openPreview(cameraId, surface, width, height, null, traceId);
    }

    @Override
    public void openPreview(String cameraId, Surface surface, int width, int height, String ownerToken, String traceId) {
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
        String channelKey = cameraChannel.getKey();
        String normalizedOwnerToken = normalizeOwnerToken(ownerToken);
        long generation = registerPreviewRequest(channelKey, normalizedOwnerToken);
        closeCurrentCamera(channelKey, traceId + "-restart");

        CameraManager cameraManager = context.getSystemService(CameraManager.class);
        if (cameraManager == null) {
            throw new IllegalStateException("鎷夸笉鍒?CameraManager");
        }

        try {
            cameraManager.openCamera(cameraChannel.getCameraId(), context.getMainExecutor(), new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice cameraDevice) {
                    if (!isPreviewRequestCurrent(channelKey, normalizedOwnerToken, generation)) {
                        cameraDevice.close();
                        AppLogCenter.log(LogCategory.DEVICE, LogLevel.DEBUG, TAG, "ignore stale camera open: " + channelKey, traceId);
                        return;
                    }
                    replaceOpenedDevice(channelKey, cameraDevice);
                    createPreviewSession(cameraChannel, cameraDevice, surface, width, height, normalizedOwnerToken, generation, traceId);
                }

                @Override
                public void onDisconnected(CameraDevice cameraDevice) {
                    removeOpenedDeviceIfSame(channelKey, cameraDevice);
                    closeSession(channelKey);
                    cameraDevice.close();
                    AppLogCenter.log(LogCategory.DEVICE, LogLevel.WARN, TAG, "camera disconnected: " + channelKey, traceId);
                }

                @Override
                public void onError(CameraDevice cameraDevice, int error) {
                    removeOpenedDeviceIfSame(channelKey, cameraDevice);
                    closeSession(channelKey);
                    cameraDevice.close();
                    AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "camera preview open failed " + channelKey + " / error=" + error, traceId);
                }
            });
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "camera preview open failed " + cameraChannel.getKey() + ": " + e.getMessage(), traceId);
            throw new IllegalStateException("鎵撳紑 Camera 棰勮澶辫触: " + cameraChannel.getKey() + " / " + e.getMessage(), e);
        }
    }

    @Override
    public void close(String cameraId, String traceId) {
        close(cameraId, null, traceId);
    }

    @Override
    public void close(String cameraId, String ownerToken, String traceId) {
        ShellConfig.CameraChannel cameraChannel = requireChannel(cameraId);
        String channelKey = cameraChannel.getKey();
        String normalizedOwnerToken = normalizeOwnerToken(ownerToken);
        if (!isCloseAllowed(channelKey, normalizedOwnerToken)) {
            AppLogCenter.log(LogCategory.DEVICE, LogLevel.DEBUG, TAG, "ignore stale camera close: " + channelKey + " / owner=" + normalizedOwnerToken, traceId);
            return;
        }
        clearPreviewRequest(channelKey);
        CameraDevice cameraDevice = closeCurrentCamera(channelKey, traceId);
        if (cameraDevice == null) {
            return;
        }
        AppLogCenter.log(LogCategory.DEVICE, LogLevel.INFO, TAG, "camera closed: " + channelKey, traceId);
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
            String ownerToken,
            long generation,
            String traceId
    ) {
        String channelKey = cameraChannel.getKey();
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
                            if (!isPreviewRequestCurrent(channelKey, ownerToken, generation)) {
                                session.close();
                                removeOpenedDeviceIfSame(channelKey, cameraDevice);
                                cameraDevice.close();
                                AppLogCenter.log(LogCategory.DEVICE, LogLevel.DEBUG, TAG, "ignore stale preview session: " + channelKey, traceId);
                                return;
                            }
                            try {
                                replacePreviewSession(channelKey, session);
                                session.setRepeatingRequest(requestBuilder.build(), null, mainHandler);
                                AppLogCenter.log(
                                        LogCategory.DEVICE,
                                        LogLevel.INFO,
                                        TAG,
                                        "camera preview started: " + channelKey
                                                + " -> " + cameraChannel.getCameraId()
                                                + " / surface=" + width + "x" + height,
                                        traceId
                                );
                            } catch (Exception e) {
                                AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "camera preview request failed " + channelKey + ": " + e.getMessage(), traceId);
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "camera preview configure failed: " + channelKey, traceId);
                        }
                    },
                    mainHandler
            );
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "camera preview session failed " + channelKey + ": " + e.getMessage(), traceId);
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

    private CameraDevice closeCurrentCamera(String channelKey, String traceId) {
        closeSession(channelKey);
        CameraDevice cameraDevice = openedDevices.remove(channelKey);
        if (cameraDevice == null) {
            AppLogCenter.log(LogCategory.DEVICE, LogLevel.DEBUG, TAG, "camera already closed: " + channelKey, traceId);
            return null;
        }
        cameraDevice.close();
        return cameraDevice;
    }

    private long registerPreviewRequest(String channelKey, String ownerToken) {
        long generation = previewRequestCounter.incrementAndGet();
        previewGenerations.put(channelKey, generation);
        if (ownerToken == null) {
            previewOwners.remove(channelKey);
        } else {
            previewOwners.put(channelKey, ownerToken);
        }
        return generation;
    }

    private void clearPreviewRequest(String channelKey) {
        previewGenerations.put(channelKey, previewRequestCounter.incrementAndGet());
        previewOwners.remove(channelKey);
    }

    private boolean isPreviewRequestCurrent(String channelKey, String ownerToken, long generation) {
        Long currentGeneration = previewGenerations.get(channelKey);
        if (currentGeneration == null || currentGeneration.longValue() != generation) {
            return false;
        }
        String currentOwner = previewOwners.get(channelKey);
        if (currentOwner == null) {
            return ownerToken == null;
        }
        return currentOwner.equals(ownerToken);
    }

    private boolean isCloseAllowed(String channelKey, String ownerToken) {
        if (ownerToken == null) {
            return true;
        }
        String currentOwner = previewOwners.get(channelKey);
        return currentOwner == null || currentOwner.equals(ownerToken);
    }

    private void replaceOpenedDevice(String channelKey, CameraDevice nextDevice) {
        CameraDevice previousDevice = openedDevices.put(channelKey, nextDevice);
        if (previousDevice != null && previousDevice != nextDevice) {
            previousDevice.close();
        }
    }

    private void removeOpenedDeviceIfSame(String channelKey, CameraDevice cameraDevice) {
        CameraDevice currentDevice = openedDevices.get(channelKey);
        if (currentDevice == cameraDevice) {
            openedDevices.remove(channelKey);
        }
    }

    private void replacePreviewSession(String channelKey, CameraCaptureSession nextSession) {
        CameraCaptureSession previousSession = previewSessions.put(channelKey, nextSession);
        if (previousSession != null && previousSession != nextSession) {
            try {
                previousSession.stopRepeating();
            } catch (Exception ignore) {
            }
            previousSession.close();
        }
    }

    private String normalizeOwnerToken(String ownerToken) {
        if (ownerToken == null) {
            return null;
        }
        String trimmed = ownerToken.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private ShellConfig.CameraChannel requireChannel(String key) {
        ShellConfig.CameraChannel channel = channelMap.get(key);
        if (channel != null) {
            return channel;
        }
        throw new IllegalArgumentException("Camera 未配置: " + key);
    }
}
