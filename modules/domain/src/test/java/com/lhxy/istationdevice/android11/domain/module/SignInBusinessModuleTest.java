package com.lhxy.istationdevice.android11.domain.module;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.lhxy.istationdevice.android11.deviceapi.DeviceMode;
import com.lhxy.istationdevice.android11.deviceapi.RfidAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortConfig;
import com.lhxy.istationdevice.android11.deviceapi.SerialReceiveListener;
import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SocketEndpointConfig;
import com.lhxy.istationdevice.android11.deviceapi.SocketReceiveListener;
import com.lhxy.istationdevice.android11.domain.ProtocolReplayUseCase;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.dispatch.DvrSerialDispatchUseCase;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class SignInBusinessModuleTest {
    @Test
    public void updateContext_startsAutoPollingAndReadsCard() throws Exception {
        FakeRfidAdapter rfidAdapter = new FakeRfidAdapter("", "38383838323233431000000000000000");
        SignInBusinessModule module = new SignInBusinessModule(
                new ProtocolReplayUseCase(),
                new FakeSocketClientAdapter(),
                rfidAdapter,
                new DvrSerialDispatchUseCase(new FakeSerialPortAdapter()),
                20L,
                50L,
                20L
        );

        setShellConfig(module, buildRealRfidConfig());
        invokeNoArg(module, "startAutoPolling");

        Thread.sleep(140L);

        assertEquals("38383838323233431000000000000000", module.getSignInState().getCardNo());
        assertTrue(module.getSignInState().isSignedIn());
        assertEquals(1, rfidAdapter.waitCardRemovedCount);

        setShellConfig(module, buildStubRfidConfig());
        invokeWithTraceId(module, "stopAutoPolling", "signin-auto-poll-test-stop");
    }

    private static void setShellConfig(SignInBusinessModule module, ShellConfig shellConfig) throws Exception {
        Field shellConfigField = AbstractTerminalBusinessModule.class.getDeclaredField("shellConfig");
        shellConfigField.setAccessible(true);
        shellConfigField.set(module, shellConfig);
    }

    private static void invokeNoArg(SignInBusinessModule module, String methodName) throws Exception {
        Method method = SignInBusinessModule.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(module);
    }

    private static void invokeWithTraceId(SignInBusinessModule module, String methodName, String traceId) throws Exception {
        Method method = SignInBusinessModule.class.getDeclaredMethod(methodName, String.class);
        method.setAccessible(true);
        method.invoke(module, traceId);
    }

    private static ShellConfig buildRealRfidConfig() {
        return buildConfig(new ShellConfig.RfidConfig(DeviceMode.REAL, "", "", "", "/dev/i2c-3", "0x01", ""));
    }

    private static ShellConfig buildStubRfidConfig() {
        return buildConfig(ShellConfig.RfidConfig.stub());
    }

    private static ShellConfig buildConfig(ShellConfig.RfidConfig rfidConfig) {
        Map<String, ShellConfig.SerialChannel> serialChannels = new LinkedHashMap<>();
        return new ShellConfig(
                "M90",
                "test",
                "test",
                serialChannels,
                new LinkedHashMap<>(),
                ShellConfig.GpioConfig.empty(),
                ShellConfig.CameraConfig.empty(),
                rfidConfig,
                ShellConfig.SystemConfig.stub(),
                ShellConfig.LocationConfig.defaults(),
                ShellConfig.CanConfig.empty(),
                ShellConfig.KeyboardConfig.stub(),
                ShellConfig.DebugReplay.defaultReplay(),
                new ShellConfig.BasicSetupConfig(
                        ShellConfig.NewspaperSettings.defaults(),
                        ShellConfig.NetworkSettings.defaults(),
                        new ShellConfig.SerialSettings("无", "无", "无", "无"),
                        ShellConfig.TtsSettings.defaults(),
                        ShellConfig.LanguageSettings.defaults(),
                        ShellConfig.OtherSettings.defaults(),
                        ShellConfig.WirelessSettings.defaults(),
                        ShellConfig.ResourceImportSettings.defaults(),
                        ShellConfig.ProtocolLinkageSettings.defaults()
                )
        );
    }

    private static final class FakeRfidAdapter implements RfidAdapter {
        private final ConcurrentLinkedQueue<String> readResults = new ConcurrentLinkedQueue<>();
        private int waitCardRemovedCount;

        private FakeRfidAdapter(String... readResults) {
            Collections.addAll(this.readResults, readResults);
        }

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public String readCard(String traceId) {
            String value = readResults.poll();
            return value == null ? "" : value;
        }

        @Override
        public boolean waitCardRemoved(String traceId, long timeoutMs, long pollIntervalMs) {
            waitCardRemovedCount++;
            return true;
        }
    }

    private static final class FakeSocketClientAdapter implements SocketClientAdapter {
        @Override
        public void connect(SocketEndpointConfig config, String traceId) {
        }

        @Override
        public void disconnect(String channelName, String traceId) {
        }

        @Override
        public boolean isConnected(String channelName) {
            return false;
        }

        @Override
        public void send(String channelName, byte[] payload, String traceId) {
        }

        @Override
        public void setReceiveListener(String channelName, SocketReceiveListener listener) {
        }

        @Override
        public void removeReceiveListener(String channelName) {
        }
    }

    private static final class FakeSerialPortAdapter implements SerialPortAdapter {
        @Override
        public void open(SerialPortConfig config, String traceId) {
        }

        @Override
        public void close(String portName, String traceId) {
        }

        @Override
        public boolean isOpen(String portName) {
            return false;
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
    }
}
