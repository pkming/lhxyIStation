package com.lhxy.istationdevice.android11.devicem90;

import com.lhxy.istationdevice.android11.deviceapi.DeviceMode;
import com.lhxy.istationdevice.android11.deviceapi.GpioAdapter;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

/**
 * M90 GPIO 管理适配器
 * <p>
 * 统一接收配置，再根据 mode 决定走 stub 还是 real。
 */
public final class M90ManagedGpioAdapter implements GpioAdapter {
    private final M90StubGpioAdapter stubAdapter = new M90StubGpioAdapter();
    private final M90RealGpioAdapter realAdapter = new M90RealGpioAdapter();
    private volatile ShellConfig.GpioConfig gpioConfig = ShellConfig.GpioConfig.empty();

    /**
     * 应用最新 GPIO 配置。
     */
    public void updateConfig(ShellConfig.GpioConfig gpioConfig) {
        this.gpioConfig = gpioConfig == null ? ShellConfig.GpioConfig.empty() : gpioConfig;
        stubAdapter.applyConfig(this.gpioConfig);
        realAdapter.updateConfig(this.gpioConfig);
    }

    @Override
    public void write(String pinId, int value, String traceId) {
        delegate().write(pinId, value, traceId);
    }

    @Override
    public int read(String pinId, String traceId) {
        return delegate().read(pinId, traceId);
    }

    /**
     * 输出 GPIO 当前状态摘要。
     */
    public String describeStatus() {
        return "GPIO -> mode=" + gpioConfig.getMode().toConfigValue()
                + " / pins=" + gpioConfig.getPins().size()
                + " / note=" + (gpioConfig.getNote().isEmpty() ? "-" : gpioConfig.getNote());
    }

    private GpioAdapter delegate() {
        return gpioConfig.getMode() == DeviceMode.REAL ? realAdapter : stubAdapter;
    }
}
