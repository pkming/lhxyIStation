package com.lhxy.istationdevice.android11.domain.module;

import static org.junit.Assert.assertEquals;

import com.lhxy.istationdevice.android11.deviceapi.CameraAdapter;
import com.lhxy.istationdevice.android11.deviceapi.DeviceMode;
import com.lhxy.istationdevice.android11.deviceapi.GpioAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortConfig;
import com.lhxy.istationdevice.android11.deviceapi.SerialReceiveListener;
import com.lhxy.istationdevice.android11.domain.DeviceFoundationUseCase;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.dispatch.DvrSerialDispatchUseCase;
import com.lhxy.istationdevice.android11.domain.dvr.DvrSerialMonitor;
import com.lhxy.istationdevice.android11.domain.module.state.DispatchState;
import com.lhxy.istationdevice.android11.domain.module.state.SignInState;

import org.junit.Test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class CameraDvrBusinessModuleTest {
    @Test
    public void resolveMonitorCameraKey_prefersReverseWhenPrimaryHighSecondaryLow() {
        CameraDvrBusinessModule module = createModule(createGpioAdapter(1, 0));

        assertEquals("reverse", module.resolveMonitorCameraKey(createShellConfig(), "camera-dvr-test"));
    }

    @Test
    public void resolveMonitorCameraKey_prefersMiddleDoorWhenSecondaryHigh() {
        CameraDvrBusinessModule module = createModule(createGpioAdapter(0, 1));

        assertEquals("middle_door", module.resolveMonitorCameraKey(createShellConfig(), "camera-dvr-test"));
    }

    @Test
    public void resolveMonitorCameraKey_usesReversePriorityWhenBothLow() {
        CameraDvrBusinessModule module = createModule(createGpioAdapter(0, 0));

        assertEquals("reverse", module.resolveMonitorCameraKey(createShellConfig(), "camera-dvr-test"));
    }

    @Test
    public void resolveMonitorCameraKey_fallsBackToConfiguredCameraWhenGpioUnavailable() {
        CameraDvrBusinessModule module = createModule(new GpioAdapter() {
            @Override
            public void write(String pinId, int value, String traceId) {
            }

            @Override
            public int read(String pinId, String traceId) {
                throw new IllegalStateException("gpio unavailable");
            }
        });

        assertEquals("av_out", module.resolveMonitorCameraKey(createShellConfig(), "camera-dvr-test"));
    }

    private CameraDvrBusinessModule createModule(GpioAdapter gpioAdapter) {
        return new CameraDvrBusinessModule(
                new DeviceFoundationUseCase(),
                new CameraAdapter() {
                    @Override
                    public void open(String cameraId, String traceId) {
                    }

                    @Override
                    public void close(String cameraId, String traceId) {
                    }
                },
                gpioAdapter,
                new SerialPortAdapter() {
                    @Override
                    public void open(SerialPortConfig config, String traceId) {
                    }

                    @Override
                    public void close(String portName, String traceId) {
                    }

                    @Override
                    public boolean isOpen(String portName) {
                        return true;
                    }

                    @Override
                    public void send(String portName, byte[] payload, String traceId) {
                    }

                    @Override
                    public void setReceiveListener(String portName, SerialReceiveListener listener) {
                    }

                    @Override
                    public void removeReceiveListener(String portName) {
                    }
                },
                new DvrSerialDispatchUseCase(new SerialPortAdapter() {
                    @Override
                    public void open(SerialPortConfig config, String traceId) {
                    }

                    @Override
                    public void close(String portName, String traceId) {
                    }

                    @Override
                    public boolean isOpen(String portName) {
                        return true;
                    }

                    @Override
                    public void send(String portName, byte[] payload, String traceId) {
                    }

                    @Override
                    public void setReceiveListener(String portName, SerialReceiveListener listener) {
                    }

                    @Override
                    public void removeReceiveListener(String portName) {
                    }
                }),
                new DvrSerialMonitor(new DispatchState(), new SignInState(), (traceId) -> {
                }, (notice, traceId) -> {
                })
        );
    }

    private GpioAdapter createGpioAdapter(int primaryValue, int secondaryValue) {
        return new GpioAdapter() {
            @Override
            public void write(String pinId, int value, String traceId) {
            }

            @Override
            public int read(String pinId, String traceId) {
                if ("monitor_primary".equals(pinId)) {
                    return primaryValue;
                }
                if ("monitor_secondary".equals(pinId)) {
                    return secondaryValue;
                }
                throw new IllegalArgumentException("unexpected pin: " + pinId);
            }
        };
    }

    private ShellConfig createShellConfig() {
        Map<String, ShellConfig.GpioPin> gpioPins = new LinkedHashMap<>();
        gpioPins.put("monitor_primary", new ShellConfig.GpioPin("monitor_primary", "monitor_primary", "/sys/monitor_primary", 0, ""));
        gpioPins.put("monitor_secondary", new ShellConfig.GpioPin("monitor_secondary", "monitor_secondary", "/sys/monitor_secondary", 0, ""));

        Map<String, ShellConfig.CameraChannel> cameraChannels = new LinkedHashMap<>();
        cameraChannels.put("av_out", new ShellConfig.CameraChannel("av_out", "0", ""));
        cameraChannels.put("middle_door", new ShellConfig.CameraChannel("middle_door", "1", ""));
        cameraChannels.put("reverse", new ShellConfig.CameraChannel("reverse", "2", ""));

        return new ShellConfig(
                "test",
                "1",
                "unit",
                Collections.emptyMap(),
                Collections.emptyMap(),
                new ShellConfig.GpioConfig(DeviceMode.REAL, gpioPins, ""),
                new ShellConfig.CameraConfig(DeviceMode.REAL, cameraChannels, ""),
                ShellConfig.RfidConfig.stub(),
                ShellConfig.SystemConfig.stub(),
                new ShellConfig.DebugReplay("", "", "", "", "", "monitor_primary", "monitor_secondary", "av_out")
        );
    }
}