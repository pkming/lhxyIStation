package com.lhxy.istationdevice.android11.devicem90;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.os.HandlerThread;
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
    private final HandlerThread cameraThread;
    private final Handler cameraHandler;
    private volatile Context appContext;

    public M90RealCameraAdapter() {
        cameraThread = new HandlerThread("m90-camera");
        cameraThread.start();
        cameraHandler = new Handler(cameraThread.getLooper());
    }

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
        CameraManager cameraManager = context.getSystemService(CameraManager.class);
        if (cameraManager == null) {
            throw new IllegalStateException("拿不到 CameraManager");
        }
        cameraHandler.post(() -> openInternal(cameraManager, cameraChannel, traceId));
    }

    private void openInternal(CameraManager cameraManager, ShellConfig.CameraChannel cameraChannel, String traceId) {
        String channelKey = cameraChannel.getKey();
        if (openedDevices.containsKey(channelKey)) {
            AppLogCenter.log(LogCategory.DEVICE, LogLevel.DEBUG, TAG, "camera already open: " + channelKey, traceId);
            return;
        }
        try {
            cameraManager.openCamera(cameraChannel.getCameraId(), new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice cameraDevice) {
                    openedDevices.put(channelKey, cameraDevice);
                    AppLogCenter.log(LogCategory.DEVICE, LogLevel.INFO, TAG, "camera opened: " + channelKey + " -> " + cameraChannel.getCameraId(), traceId);
                }

                @Override
                public void onDisconnected(CameraDevice cameraDevice) {
                    openedDevices.remove(channelKey);
                    closeCameraQuietly(cameraDevice);
                    AppLogCenter.log(LogCategory.DEVICE, LogLevel.WARN, TAG, "camera disconnected: " + channelKey, traceId);
                }

                @Override
                public void onError(CameraDevice cameraDevice, int error) {
                    openedDevices.remove(channelKey);
                    closeCameraQuietly(cameraDevice);
                    AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "camera open failed " + channelKey + " / error=" + error, traceId);
                }
            }, cameraHandler);
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "camera open failed " + channelKey + ": " + e.getMessage(), traceId);
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

        CameraManager cameraManager = context.getSystemService(CameraManager.class);
        if (cameraManager == null) {
            throw new IllegalStateException("鎷夸笉鍒?CameraManager");
        }
        cameraHandler.post(() -> openPreviewInternal(
                cameraManager,
                cameraChannel,
                surface,
                width,
                height,
                normalizedOwnerToken,
                generation,
                traceId
        ));
    }

    private void openPreviewInternal(
            CameraManager cameraManager,
            ShellConfig.CameraChannel cameraChannel,
            Surface surface,
            int width,
            int height,
            String ownerToken,
            long generation,
            String traceId
    ) {
        String channelKey = cameraChannel.getKey();
        closeCurrentCamera(channelKey, traceId + "-restart");
        try {
            cameraManager.openCamera(cameraChannel.getCameraId(), new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice cameraDevice) {
                    if (!isPreviewRequestCurrent(channelKey, ownerToken, generation)) {
                        closeCameraQuietly(cameraDevice);
                        AppLogCenter.log(LogCategory.DEVICE, LogLevel.DEBUG, TAG, "ignore stale camera open: " + channelKey, traceId);
                        return;
                    }
                    replaceOpenedDevice(channelKey, cameraDevice);
                    createPreviewSession(cameraChannel, cameraDevice, surface, width, height, ownerToken, generation, traceId);
                }

                @Override
                public void onDisconnected(CameraDevice cameraDevice) {
                    removeOpenedDeviceIfSame(channelKey, cameraDevice);
                    closeSession(channelKey);
                    closeCameraQuietly(cameraDevice);
                    AppLogCenter.log(LogCategory.DEVICE, LogLevel.WARN, TAG, "camera disconnected: " + channelKey, traceId);
                }

                @Override
                public void onError(CameraDevice cameraDevice, int error) {
                    removeOpenedDeviceIfSame(channelKey, cameraDevice);
                    closeSession(channelKey);
                    closeCameraQuietly(cameraDevice);
                    AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "camera preview open failed " + channelKey + " / error=" + error, traceId);
                }
            }, cameraHandler);
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "camera preview open failed " + channelKey + ": " + e.getMessage(), traceId);
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
        cameraHandler.post(() -> closeInternal(channelKey, normalizedOwnerToken, traceId));
    }

    private void closeInternal(String channelKey, String ownerToken, String traceId) {
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
            cameraDevice.createCaptureSession(
                    Collections.singletonList(surface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            if (!isPreviewRequestCurrent(channelKey, ownerToken, generation)) {
                                session.close();
                                removeOpenedDeviceIfSame(channelKey, cameraDevice);
                                closeCameraQuietly(cameraDevice);
                                AppLogCenter.log(LogCategory.DEVICE, LogLevel.DEBUG, TAG, "ignore stale preview session: " + channelKey, traceId);
                                return;
                            }
                            try {
                                replacePreviewSession(channelKey, session);
                                session.setRepeatingRequest(requestBuilder.build(), null, cameraHandler);
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
                    cameraHandler
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
        closeCameraQuietly(cameraDevice);
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
            closeCameraQuietly(previousDevice);
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

    private void closeCameraQuietly(CameraDevice cameraDevice) {
        if (cameraDevice == null) {
            return;
        }
        try {
            cameraDevice.close();
        } catch (Exception ignore) {
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
