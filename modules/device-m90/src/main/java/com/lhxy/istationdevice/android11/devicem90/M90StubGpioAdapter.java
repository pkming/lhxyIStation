package com.lhxy.istationdevice.android11.devicem90;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.GpioAdapter;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

import java.util.HashMap;
import java.util.Map;

public final class M90StubGpioAdapter implements GpioAdapter {
    private static final String TAG = "M90StubGpio";
    private final Map<String, Integer> values = new HashMap<>();

    /**
     * 应用默认 GPIO 值。
     */
    public void applyConfig(ShellConfig.GpioConfig gpioConfig) {
        values.clear();
        if (gpioConfig == null) {
            return;
        }
        for (Map.Entry<String, ShellConfig.GpioPin> entry : gpioConfig.getPins().entrySet()) {
            values.put(entry.getKey(), entry.getValue().getDefaultValue());
        }
    }

    @Override
    public void write(String pinId, int value, String traceId) {
        values.put(pinId, value);
        AppLogCenter.log(LogCategory.DEVICE, LogLevel.DEBUG, TAG, "stub write " + pinId + "=" + value, traceId);
    }

    @Override
    public int read(String pinId, String traceId) {
        int value = values.containsKey(pinId) ? values.get(pinId) : 0;
        AppLogCenter.log(LogCategory.DEVICE, LogLevel.DEBUG, TAG, "stub read " + pinId + "=" + value, traceId);
        return value;
    }
}
