package com.lhxy.istationdevice.android11.devicem90;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.GpioAdapter;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * M90 真 GPIO 适配器
 * <p>
 * 这版先按配置里的 `valuePath` 直接读写 value 文件。
 * 真机如果后面改成厂商 JNI，也只需要替这层。
 */
public final class M90RealGpioAdapter implements GpioAdapter {
    private static final String TAG = "M90RealGpio";

    private final Map<String, ShellConfig.GpioPin> pinMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> cachedValues = new ConcurrentHashMap<>();

    /**
     * 更新当前 GPIO 配置。
     */
    public void updateConfig(ShellConfig.GpioConfig gpioConfig) {
        pinMap.clear();
        cachedValues.clear();
        if (gpioConfig == null) {
            return;
        }
        for (Map.Entry<String, ShellConfig.GpioPin> entry : gpioConfig.getPins().entrySet()) {
            pinMap.put(entry.getKey(), entry.getValue());
            cachedValues.put(entry.getKey(), entry.getValue().getDefaultValue());
        }
    }

    @Override
    public void write(String pinId, int value, String traceId) {
        ShellConfig.GpioPin gpioPin = requirePin(pinId);
        String valuePath = requireValuePath(gpioPin);
        try (FileOutputStream outputStream = new FileOutputStream(valuePath, false)) {
            outputStream.write(String.valueOf(value).getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            cachedValues.put(gpioPin.getKey(), value);
            AppLogCenter.log(LogCategory.DEVICE, LogLevel.INFO, TAG, "real write " + gpioPin.getKey() + "=" + value + " -> " + valuePath, traceId);
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "real write failed " + gpioPin.getKey() + ": " + e.getMessage(), traceId);
            throw new IllegalStateException("GPIO 写失败: " + gpioPin.getKey() + " / " + e.getMessage(), e);
        }
    }

    @Override
    public int read(String pinId, String traceId) {
        ShellConfig.GpioPin gpioPin = requirePin(pinId);
        String valuePath = requireValuePath(gpioPin);
        try {
            File valueFile = new File(valuePath);
            String rawValue = M90CommandSupport.readFileText(valueFile);
            int value = Integer.parseInt(rawValue.isEmpty() ? "0" : rawValue.substring(0, 1));
            cachedValues.put(gpioPin.getKey(), value);
            AppLogCenter.log(LogCategory.DEVICE, LogLevel.DEBUG, TAG, "real read " + gpioPin.getKey() + "=" + value + " <- " + valuePath, traceId);
            return value;
        } catch (Exception e) {
            int fallbackValue = cachedValues.containsKey(gpioPin.getKey()) ? cachedValues.get(gpioPin.getKey()) : gpioPin.getDefaultValue();
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "real read failed " + gpioPin.getKey() + ": " + e.getMessage() + "，回退到 " + fallbackValue, traceId);
            return fallbackValue;
        }
    }

    private ShellConfig.GpioPin requirePin(String pinKey) {
        ShellConfig.GpioPin gpioPin = pinMap.get(pinKey);
        if (gpioPin != null) {
            return gpioPin;
        }
        throw new IllegalArgumentException("GPIO 未配置: " + pinKey);
    }

    private String requireValuePath(ShellConfig.GpioPin gpioPin) {
        if (gpioPin.getValuePath() == null || gpioPin.getValuePath().trim().isEmpty()) {
            throw new IllegalStateException("GPIO 没有配置 valuePath: " + gpioPin.getKey());
        }
        return gpioPin.getValuePath().trim();
    }
}
