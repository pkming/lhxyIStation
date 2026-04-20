package com.lhxy.istationdevice.android11.domain.module;

import com.lhxy.istationdevice.android11.deviceapi.CameraAdapter;
import com.lhxy.istationdevice.android11.deviceapi.GpioAdapter;
import com.lhxy.istationdevice.android11.domain.DeviceFoundationUseCase;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.dispatch.DvrSerialDispatchUseCase;

/**
 * 摄像头 / DVR 模块
 * <p>
 * 这一层承接默认 Camera、GPIO、DVR 键位和触摸协议发送。
 * 触摸动作统一通过 dvr_touch_{phase}_{x}_{y} 进入，避免页面直接拼串口帧。
 */
public final class CameraDvrBusinessModule extends AbstractTerminalBusinessModule {
    private final DeviceFoundationUseCase deviceFoundationUseCase;
    private final CameraAdapter cameraAdapter;
    private final GpioAdapter gpioAdapter;
    private final DvrSerialDispatchUseCase dvrSerialDispatchUseCase;
    private String lastCameraKey = "-";
    private String lastDvrKey = "-";
    private String lastTouchPoint = "-";
    private String lastGpioKey = "-";
    private int lastGpioValue = -1;
    private boolean defaultCameraOpened;

    public CameraDvrBusinessModule(
            DeviceFoundationUseCase deviceFoundationUseCase,
            CameraAdapter cameraAdapter,
            GpioAdapter gpioAdapter,
            DvrSerialDispatchUseCase dvrSerialDispatchUseCase
    ) {
        this.deviceFoundationUseCase = deviceFoundationUseCase;
        this.cameraAdapter = cameraAdapter;
        this.gpioAdapter = gpioAdapter;
        this.dvrSerialDispatchUseCase = dvrSerialDispatchUseCase;
    }

    @Override
    public String getKey() {
        return "camera_dvr";
    }

    @Override
    public String getTitle() {
        return "摄像头/DVR";
    }

    @Override
    public String describePurpose() {
        return "承接 Camera 通道、DVR 开关 IO、DVR 键位和触摸事件发送。";
    }

    @Override
    public String describeStatus() {
        try {
            ShellConfig shellConfig = requireShellConfig();
            ShellConfig.CameraChannel cameraChannel = shellConfig.getCameraConfig().requireChannel(shellConfig.getDebugReplay().getCameraChannelKey());
            ShellConfig.GpioPin gpioPin = shellConfig.getGpioConfig().requirePin(shellConfig.getDebugReplay().getGpioPinKey());
            return "默认 Camera=" + cameraChannel.getKey()
                    + " / cameraId=" + cameraChannel.getCameraId()
                    + "\n- 默认 GPIO=" + gpioPin.getKey()
                    + " / pinId=" + gpioPin.getPinId()
                    + "\n- DVR 串口主链=" + yesNo(dvrSerialDispatchUseCase.canUse(shellConfig)) + " / lastDvrKey=" + lastDvrKey
                    + " / lastTouch=" + lastTouchPoint
                    + "\n- Camera available=" + yesNo(cameraAdapter.isAvailable())
                    + "\n- lastCamera=" + lastCameraKey + " / opened=" + yesNo(defaultCameraOpened) + " / lastGpioValue=" + lastGpioValue
                    + "\n- " + describeActionMemory();
        } catch (Exception e) {
            return "当前还没拿到完整摄像头/DVR 配置: " + emptyAsDash(e.getMessage());
        }
    }

    @Override
    public ModuleRunResult runSample(String traceId) {
        return runDefaultCameraChain(traceId);
    }

    @Override
    public ModuleRunResult runAction(String actionKey, String traceId) {
        if ("open_default_camera".equals(actionKey)) {
            return openOrCloseDefaultCamera(traceId, true);
        }
        if ("close_default_camera".equals(actionKey)) {
            return openOrCloseDefaultCamera(traceId, false);
        }
        if (actionKey != null && actionKey.startsWith("dvr_key_")) {
            return sendDvrKey(actionKey, traceId);
        }
        if (actionKey != null && actionKey.startsWith("dvr_touch_")) {
            return sendDvrTouch(actionKey, traceId);
        }
        return unsupportedAction(actionKey);
    }

    private ModuleRunResult runDefaultCameraChain(String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            String cameraKey = shellConfig.getDebugReplay().getCameraChannelKey();
            String gpioKey = shellConfig.getDebugReplay().getGpioPinKey();
            int gpioValue = deviceFoundationUseCase.readGpio(gpioAdapter, shellConfig, gpioKey, traceId + "-gpio");
            deviceFoundationUseCase.openCamera(cameraAdapter, shellConfig, cameraKey, traceId + "-camera-open");
            deviceFoundationUseCase.closeCamera(cameraAdapter, shellConfig, cameraKey, traceId + "-camera-close");
            lastCameraKey = cameraKey;
            lastGpioKey = gpioKey;
            lastGpioValue = gpioValue;
            defaultCameraOpened = false;
            return success("已验证默认 Camera 开关链路", "GPIO " + gpioKey + "=" + gpioValue + "，Camera=" + cameraKey);
        } catch (Exception e) {
            return failure("摄像头/DVR 样例执行失败", e);
        }
    }

    private ModuleRunResult openOrCloseDefaultCamera(String traceId, boolean open) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            String cameraKey = shellConfig.getDebugReplay().getCameraChannelKey();
            String gpioKey = shellConfig.getDebugReplay().getGpioPinKey();
            lastCameraKey = cameraKey;
            lastGpioKey = gpioKey;
            lastGpioValue = deviceFoundationUseCase.readGpio(gpioAdapter, shellConfig, gpioKey, traceId + "-gpio");
            if (open) {
                deviceFoundationUseCase.openCamera(cameraAdapter, shellConfig, cameraKey, traceId);
            } else {
                deviceFoundationUseCase.closeCamera(cameraAdapter, shellConfig, cameraKey, traceId);
            }
            defaultCameraOpened = open;
            return success(open ? "已打开默认 Camera" : "已关闭默认 Camera", "Camera=" + cameraKey + " / GPIO " + gpioKey + "=" + lastGpioValue);
        } catch (Exception e) {
            return failure("摄像头/DVR 动作执行失败", e);
        }
    }

    private ModuleRunResult sendDvrKey(String actionKey, String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            if (!dvrSerialDispatchUseCase.canUse(shellConfig)) {
                return failure("DVR 键位发送失败", new IllegalStateException("当前不是 RS232-1/DVR 模式"));
            }
            byte keyCode = parseKeyCode(actionKey);
            dvrSerialDispatchUseCase.sendKey(shellConfig, keyCode, traceId + "-dvr-key");
            lastDvrKey = String.format("0x%02X", keyCode & 0xFF);
            return success("已发送 DVR 键位", "key=" + lastDvrKey + " / RS232-1");
        } catch (Exception e) {
            return failure("DVR 键位发送失败", e);
        }
    }

    private byte parseKeyCode(String actionKey) {
        String hex = actionKey.substring("dvr_key_".length());
        return (byte) (Integer.parseInt(hex, 16) & 0xFF);
    }

    private ModuleRunResult sendDvrTouch(String actionKey, String traceId) {
        try {
            ShellConfig shellConfig = requireShellConfig();
            if (!dvrSerialDispatchUseCase.canUse(shellConfig)) {
                return failure("DVR 触摸发送失败", new IllegalStateException("当前不是 RS232-1/DVR 模式"));
            }
            String[] parts = actionKey.split("_");
            if (parts.length != 5) {
                return failure("DVR 触摸发送失败", new IllegalArgumentException("触摸动作格式无效"));
            }
            boolean pressed = "down".equals(parts[2]) || "move".equals(parts[2]);
            int x = Integer.parseInt(parts[3]);
            int y = Integer.parseInt(parts[4]);
            dvrSerialDispatchUseCase.sendTouchEvent(shellConfig, x, y, pressed, traceId + "-dvr-touch");
            lastTouchPoint = parts[2] + "(" + x + "," + y + ")" + " / pressed=" + yesNo(pressed);
            return success("已发送 DVR 触摸事件", lastTouchPoint + " / RS232-1");
        } catch (Exception e) {
            return failure("DVR 触摸发送失败", e);
        }
    }
}
