package com.lhxy.istationdevice.android11.domain.config;

import android.content.Context;

import com.lhxy.istationdevice.android11.deviceapi.DeviceMode;
import com.lhxy.istationdevice.android11.deviceapi.SerialMode;
import com.lhxy.istationdevice.android11.deviceapi.SocketMode;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * 终端配置加载器
 * <p>
 * 负责按“运行期覆盖文件 -> assets -> 默认配置”的顺序加载配置，
 * 保证新壳在没有真机配置时也能先跑起来。
 */
public final class ShellConfigLoader {
    public static final String CONFIG_ASSET_NAME = "terminal-config.json";
    public static final String CONFIG_RUNTIME_DIR_NAME = "config";
    public static final String CONFIG_RUNTIME_FILE_NAME = "terminal-config.runtime.json";

    private ShellConfigLoader() {
    }

    /**
     * 加载当前配置。
     */
    public static ShellConfig loadFromAssets(Context context) {
        if (context == null) {
            return createDefault();
        }
        try {
            File runtimeConfigFile = getRuntimeConfigFile(context);
            if (runtimeConfigFile.exists()) {
                try (InputStream inputStream = new FileInputStream(runtimeConfigFile)) {
                    return parse(readText(inputStream), "runtime:" + runtimeConfigFile.getAbsolutePath());
                }
            }
        } catch (Exception ignore) {
            // 运行期覆盖文件异常时继续回退到 assets。
        }
        try (InputStream inputStream = context.getAssets().open(CONFIG_ASSET_NAME)) {
            return parse(readText(inputStream), "assets:" + CONFIG_ASSET_NAME);
        } catch (Exception ignore) {
            return createDefault();
        }
    }

    /**
     * 解析 JSON 配置文本。
     */
    public static ShellConfig parse(String jsonText) throws Exception {
        return parse(jsonText, "unknown");
    }

    /**
     * 解析 JSON 配置文本并记录来源。
     */
    public static ShellConfig parse(String jsonText, String source) throws Exception {
        JSONObject root = new JSONObject(jsonText);
        String deviceProfile = root.optString("deviceProfile", "M90-STUB");
        String configVersion = root.optString("configVersion", "0.0.0");

        Map<String, ShellConfig.SerialChannel> serialChannels = new LinkedHashMap<>();
        JSONObject serialObject = root.optJSONObject("serial");
        if (serialObject != null) {
            for (Iterator<String> iterator = serialObject.keys(); iterator.hasNext(); ) {
                String key = iterator.next();
                JSONObject item = serialObject.optJSONObject(key);
                if (item == null) {
                    continue;
                }
                serialChannels.put(key, new ShellConfig.SerialChannel(
                        key,
                        item.optString("portName", ""),
                        item.optInt("baudRate", 9600),
                        SerialMode.fromConfig(item.optString("mode", "stub")),
                        item.optString("note", "")
                ));
            }
        }

        Map<String, ShellConfig.SocketChannel> socketChannels = new LinkedHashMap<>();
        JSONObject socketObject = root.optJSONObject("socket");
        if (socketObject != null) {
            for (Iterator<String> iterator = socketObject.keys(); iterator.hasNext(); ) {
                String key = iterator.next();
                JSONObject item = socketObject.optJSONObject(key);
                if (item == null) {
                    continue;
                }
                socketChannels.put(key, new ShellConfig.SocketChannel(
                        key,
                        item.optString("channelName", ""),
                        item.optString("host", ""),
                        item.optInt("port", 0),
                        SocketMode.fromConfig(item.optString("mode", "stub")),
                        item.optString("note", "")
                ));
            }
        }

        JSONObject gpioObject = root.optJSONObject("gpio");
        DeviceMode gpioMode = gpioObject == null ? DeviceMode.STUB : DeviceMode.fromConfig(gpioObject.optString("mode", "stub"));
        Map<String, ShellConfig.GpioPin> gpioPins = new LinkedHashMap<>();
        if (gpioObject != null) {
            JSONObject pinObject = gpioObject.optJSONObject("pins");
            if (pinObject != null) {
                for (Iterator<String> iterator = pinObject.keys(); iterator.hasNext(); ) {
                    String key = iterator.next();
                    JSONObject item = pinObject.optJSONObject(key);
                    if (item == null) {
                        continue;
                    }
                    gpioPins.put(key, new ShellConfig.GpioPin(
                            key,
                            item.optString("pinId", key),
                            item.optString("valuePath", ""),
                            item.optInt("defaultValue", 0),
                            item.optString("note", "")
                    ));
                }
            }
        }
        ShellConfig.GpioConfig gpioConfig = new ShellConfig.GpioConfig(
                gpioMode,
                gpioPins,
                gpioObject == null ? "" : gpioObject.optString("note", "")
        );

        JSONObject cameraObject = root.optJSONObject("camera");
        DeviceMode cameraMode = cameraObject == null ? DeviceMode.STUB : DeviceMode.fromConfig(cameraObject.optString("mode", "stub"));
        Map<String, ShellConfig.CameraChannel> cameraChannels = new LinkedHashMap<>();
        if (cameraObject != null) {
            JSONObject channelObject = cameraObject.optJSONObject("channels");
            if (channelObject != null) {
                for (Iterator<String> iterator = channelObject.keys(); iterator.hasNext(); ) {
                    String key = iterator.next();
                    JSONObject item = channelObject.optJSONObject(key);
                    if (item == null) {
                        continue;
                    }
                    cameraChannels.put(key, new ShellConfig.CameraChannel(
                            key,
                            item.optString("cameraId", ""),
                            item.optString("note", "")
                    ));
                }
            }
        }
        ShellConfig.CameraConfig cameraConfig = new ShellConfig.CameraConfig(
                cameraMode,
                cameraChannels,
                cameraObject == null ? "" : cameraObject.optString("note", "")
        );

        JSONObject rfidObject = root.optJSONObject("rfid");
        ShellConfig.RfidConfig rfidConfig = new ShellConfig.RfidConfig(
                rfidObject == null ? DeviceMode.STUB : DeviceMode.fromConfig(rfidObject.optString("mode", "stub")),
                rfidObject == null ? "RFID-DEMO-001" : rfidObject.optString("mockCardNo", "RFID-DEMO-001"),
                rfidObject == null ? "" : rfidObject.optString("inputFilePath", ""),
                rfidObject == null ? "" : rfidObject.optString("readCommand", ""),
            rfidObject == null ? "" : rfidObject.optString("i2cDevicePath", ""),
            rfidObject == null ? "0x00" : rfidObject.optString("i2cAddress", "0x00"),
                rfidObject == null ? "" : rfidObject.optString("note", "")
        );

        JSONObject systemObject = root.optJSONObject("systemOps");
        ShellConfig.SystemConfig systemConfig = new ShellConfig.SystemConfig(
                systemObject == null ? DeviceMode.STUB : DeviceMode.fromConfig(systemObject.optString("mode", "stub")),
                systemObject != null && systemObject.optBoolean("supportSilentInstall", false),
                systemObject != null && systemObject.optBoolean("allowReboot", false),
                systemObject != null && systemObject.optBoolean("allowSetTime", false),
                systemObject == null ? "" : systemObject.optString("silentInstallCommand", ""),
                systemObject == null ? "" : systemObject.optString("rebootCommand", ""),
                systemObject == null ? "" : systemObject.optString("setTimeCommand", ""),
                systemObject == null ? "" : systemObject.optString("note", "")
        );

            JSONObject locationObject = root.optJSONObject("location");
            ShellConfig.LocationConfig locationConfig = new ShellConfig.LocationConfig(
                locationObject == null ? DeviceMode.STUB : DeviceMode.fromConfig(locationObject.optString("mode", "stub")),
                locationObject != null && locationObject.optBoolean("enabled", false),
                locationObject == null ? "gps" : locationObject.optString("provider", "gps"),
                locationObject == null ? 1000L : locationObject.optLong("minTimeMs", 1000L),
                (float) (locationObject == null ? 0D : locationObject.optDouble("minDistanceMeters", 0D)),
                locationObject == null ? "" : locationObject.optString("note", "")
            );

            JSONObject canObject = root.optJSONObject("can");
            DeviceMode canMode = canObject == null ? DeviceMode.STUB : DeviceMode.fromConfig(canObject.optString("mode", "stub"));
            Map<String, ShellConfig.CanChannel> canChannels = new LinkedHashMap<>();
            if (canObject != null) {
                JSONObject channelObject = canObject.optJSONObject("channels");
                if (channelObject != null) {
                for (Iterator<String> iterator = channelObject.keys(); iterator.hasNext(); ) {
                    String key = iterator.next();
                    JSONObject item = channelObject.optJSONObject(key);
                    if (item == null) {
                    continue;
                    }
                    canChannels.put(key, new ShellConfig.CanChannel(
                        key,
                        item.optString("interfaceName", key),
                        item.optString("devicePath", ""),
                        item.optString("readCommand", ""),
                        item.optString("writeCommand", ""),
                        item.optString("note", "")
                    ));
                }
                }
            }
            ShellConfig.CanConfig canConfig = new ShellConfig.CanConfig(
                canMode,
                canChannels,
                canObject == null ? "" : canObject.optString("note", "")
            );

            JSONObject keyboardObject = root.optJSONObject("keyboard");
            ShellConfig.KeyboardConfig keyboardConfig = new ShellConfig.KeyboardConfig(
                keyboardObject == null ? DeviceMode.STUB : DeviceMode.fromConfig(keyboardObject.optString("mode", "stub")),
                keyboardObject == null ? "keyboard" : keyboardObject.optString("serialKey", "keyboard"),
                keyboardObject == null ? "serial" : keyboardObject.optString("protocol", "serial"),
                keyboardObject == null ? "" : keyboardObject.optString("note", "")
            );

        JSONObject debugReplayObject = root.optJSONObject("debugReplay");
        ShellConfig.DebugReplay debugReplay = new ShellConfig.DebugReplay(
                debugReplayObject == null ? "rs485_1" : debugReplayObject.optString("displaySerialKey", "rs485_1"),
                debugReplayObject == null ? "gps" : debugReplayObject.optString("gpsSerialKey", "gps"),
                debugReplayObject == null ? "jt808" : debugReplayObject.optString("jt808SocketKey", "jt808"),
                debugReplayObject == null ? "al808" : debugReplayObject.optString("al808SocketKey", "al808"),
                debugReplayObject == null ? "inner_audio" : debugReplayObject.optString("gpioPinKey", "inner_audio"),
            debugReplayObject == null ? "io1" : debugReplayObject.optString("monitorPrimaryGpioKey", "io1"),
            debugReplayObject == null ? "io2" : debugReplayObject.optString("monitorSecondaryGpioKey", "io2"),
                debugReplayObject == null ? "av_out" : debugReplayObject.optString("cameraChannelKey", "av_out")
        );

            JSONObject basicSetupObject = root.optJSONObject("basicSetup");
            JSONObject newspaperObject = basicSetupObject == null ? null : basicSetupObject.optJSONObject("newspaper");
            JSONObject networkSettingsObject = basicSetupObject == null ? null : basicSetupObject.optJSONObject("network");
            JSONObject serialSettingsObject = basicSetupObject == null ? null : basicSetupObject.optJSONObject("serialSettings");
            JSONObject ttsObject = basicSetupObject == null ? null : basicSetupObject.optJSONObject("tts");
            JSONObject languageObject = basicSetupObject == null ? null : basicSetupObject.optJSONObject("language");
            JSONObject otherObject = basicSetupObject == null ? null : basicSetupObject.optJSONObject("other");
            JSONObject wirelessObject = basicSetupObject == null ? null : basicSetupObject.optJSONObject("wireless");
            JSONObject resourceImportObject = basicSetupObject == null ? null : basicSetupObject.optJSONObject("resourceImport");
            JSONObject protocolLinkageObject = basicSetupObject == null ? null : basicSetupObject.optJSONObject("protocolLinkage");

            ShellConfig.SerialSettings serialSettings = normalizeLegacySerialSettings(new ShellConfig.SerialSettings(
                    serialSettingsObject == null ? "无" : serialSettingsObject.optString("rs2321Protocol", "无"),
                    serialSettingsObject == null ? "无" : serialSettingsObject.optString("rs2322Protocol", "无"),
                    serialSettingsObject == null ? "无" : serialSettingsObject.optString("rs485Protocol", "无"),
                    serialSettingsObject == null ? "无" : serialSettingsObject.optString("rs4852Protocol", "无")
            ));

            ShellConfig.BasicSetupConfig basicSetupConfig = new ShellConfig.BasicSetupConfig(
                new ShellConfig.NewspaperSettings(
                    newspaperObject == null ? 7 : newspaperObject.optInt("innerVolume", 7),
                    newspaperObject == null ? 7 : newspaperObject.optInt("outerVolume", 7),
                    newspaperObject == null ? "up_down" : newspaperObject.optString("lineProperty", "up_down"),
                    newspaperObject == null || newspaperObject.optBoolean("angleEnabled", true),
                    newspaperObject != null && newspaperObject.optBoolean("dialectEnabled", false),
                    newspaperObject != null && newspaperObject.optBoolean("englishEnabled", false),
                    newspaperObject == null || newspaperObject.optBoolean("externalSoundEnabled", true),
                    newspaperObject == null || newspaperObject.optBoolean("nowTimeEnabled", true),
                    newspaperObject == null || newspaperObject.optBoolean("speedingWarningEnabled", true)
                ),
                new ShellConfig.NetworkSettings(
                    networkSettingsObject == null ? "1" : networkSettingsObject.optString("dispatchId", "1"),
                    networkSettingsObject == null ? 30 : networkSettingsObject.optInt("longInterval", 30),
                    networkSettingsObject == null ? 5 : networkSettingsObject.optInt("infoInterval", 5),
                    networkSettingsObject == null ? 10 : networkSettingsObject.optInt("speedingInterval", 10),
                    networkSettingsObject == null || networkSettingsObject.optBoolean("adwordsEnabled", true),
                    networkSettingsObject == null ? "1" : networkSettingsObject.optString("adwordsId", "1"),
                    networkSettingsObject == null ? "admin" : networkSettingsObject.optString("adwordsUser", "admin"),
                    networkSettingsObject == null ? 10 : networkSettingsObject.optInt("adwordsInterval", 10)
                ),
                serialSettings,
                new ShellConfig.TtsSettings(
                    ttsObject == null || ttsObject.optBoolean("enabled", true),
                    ttsObject == null ? 8 : ttsObject.optInt("innerVolume", 8),
                    ttsObject == null ? 8 : ttsObject.optInt("outerVolume", 8)
                ),
                new ShellConfig.LanguageSettings(languageObject == null ? "auto" : languageObject.optString("languageCode", "auto")),
                new ShellConfig.OtherSettings(
                    otherObject == null ? 50 : otherObject.optInt("shoutingVolume", 50),
                    otherObject == null ? 7 : otherObject.optInt("dispatchVolume", 7),
                    otherObject == null ? "" : otherObject.optString("vehicleNumber", ""),
                    otherObject == null ? "shouting_outer" : otherObject.optString("shoutingPrimaryGpioKey", "shouting_outer"),
                    otherObject == null ? "shouting_inner" : otherObject.optString("shoutingSecondaryGpioKey", "shouting_inner")
                ),
                new ShellConfig.WirelessSettings(wirelessObject == null || wirelessObject.optBoolean("systemEntryEnabled", true)),
                new ShellConfig.ResourceImportSettings(
                    resourceImportObject != null && resourceImportObject.optBoolean("stationResourceImported", false),
                    resourceImportObject == null ? "-" : resourceImportObject.optString("source", "-"),
                    resourceImportObject == null ? "-" : resourceImportObject.optString("lineName", "-"),
                    resourceImportObject == null ? "-" : resourceImportObject.optString("directionText", "-"),
                    resourceImportObject == null ? "-" : resourceImportObject.optString("lineAttribute", "-"),
                    resourceImportObject == null ? 0L : resourceImportObject.optLong("updatedAt", 0L)
                ),
                new ShellConfig.ProtocolLinkageSettings(
                    protocolLinkageObject == null
                        ? ShellConfig.ProtocolLinkageSettings.DISPATCH_OWNER_NETWORK
                        : protocolLinkageObject.optString("dispatchOwner", ShellConfig.ProtocolLinkageSettings.DISPATCH_OWNER_NETWORK),
                    protocolLinkageObject == null ? 0L : protocolLinkageObject.optLong("updatedAt", 0L)
                )
            );

        return new ShellConfig(
                deviceProfile,
                configVersion,
                source,
                serialChannels,
                socketChannels,
                gpioConfig,
                cameraConfig,
                rfidConfig,
                systemConfig,
                locationConfig,
                canConfig,
                keyboardConfig,
                debugReplay,
                basicSetupConfig
        );
    }

    /**
     * 默认配置
     * <p>
     * 文件缺失时，先回退到这一份，避免应用启动就挂。
     */
    public static ShellConfig createDefault() {
        Map<String, ShellConfig.SerialChannel> serialChannels = new LinkedHashMap<>();
        serialChannels.put("keyboard", new ShellConfig.SerialChannel("keyboard", "ttyS0", 9600, SerialMode.STUB, "Keyboard / ttyS0"));
        serialChannels.put("debug", new ShellConfig.SerialChannel("debug", "ttyS2", 115200, SerialMode.STUB, "debug / ttyS2"));
        serialChannels.put("rs232_1", new ShellConfig.SerialChannel("rs232_1", "ttyS3", 9600, SerialMode.STUB, "DVR / RS232-1"));
        serialChannels.put("rs232_2", new ShellConfig.SerialChannel("rs232_2", "ttyS4", 9600, SerialMode.STUB, "RS232-2"));
        serialChannels.put("gps", new ShellConfig.SerialChannel("gps", "ttyS5", 115200, SerialMode.STUB, "GPS"));
        serialChannels.put("rs485_1", new ShellConfig.SerialChannel("rs485_1", "ttyS7", 9600, SerialMode.STUB, "RS485-1"));
        serialChannels.put("rs485_2", new ShellConfig.SerialChannel("rs485_2", "ttyS9", 9600, SerialMode.STUB, "RS485-2"));

        Map<String, ShellConfig.SocketChannel> socketChannels = new LinkedHashMap<>();
        socketChannels.put("jt808", new ShellConfig.SocketChannel("jt808", "JT808_SOCKET", "211.154.159.34", 7000, SocketMode.STUB, "JT808 调度"));
        socketChannels.put("al808", new ShellConfig.SocketChannel("al808", "AL808_SOCKET", "211.154.159.34", 7000, SocketMode.STUB, "AL808 调度"));

        Map<String, ShellConfig.GpioPin> gpioPins = new LinkedHashMap<>();
        gpioPins.put("inner_audio", new ShellConfig.GpioPin("inner_audio", "GPIO1_B1", "/proc/rp_gpio/gpio1b1", 0, "内音"));
        gpioPins.put("outer_audio", new ShellConfig.GpioPin("outer_audio", "GPIO1_B2", "/proc/rp_gpio/gpio1b2", 0, "外音"));
        gpioPins.put("inner_speaker", new ShellConfig.GpioPin("inner_speaker", "GPIO0_D6", "/proc/rp_gpio/gpio0d6", 0, "内喇叭"));
        gpioPins.put("io1", new ShellConfig.GpioPin("io1", "GPIO1_D0", "/proc/rp_gpio/gpio1d0", 1, "IO1 / 倒车"));
        gpioPins.put("io2", new ShellConfig.GpioPin("io2", "GPIO1_D1", "/proc/rp_gpio/gpio1d1", 1, "IO2 / 中门开"));
        gpioPins.put("io3", new ShellConfig.GpioPin("io3", "GPIO1_D2", "/proc/rp_gpio/gpio1d2", 1, "IO3"));
        gpioPins.put("io4", new ShellConfig.GpioPin("io4", "GPIO1_D3", "/proc/rp_gpio/gpio1d3", 1, "IO4 / 默认高"));
        gpioPins.put("io5", new ShellConfig.GpioPin("io5", "GPIO1_D4", "/proc/rp_gpio/gpio1d4", 0, "IO5"));
        gpioPins.put("shouting_inner", new ShellConfig.GpioPin("shouting_inner", "GPIO3_A3", "/proc/rp_gpio/gpio3a3", 0, "喊话器内"));
        gpioPins.put("shouting_outer", new ShellConfig.GpioPin("shouting_outer", "GPIO3_A4", "/proc/rp_gpio/gpio3a4", 0, "喊话器外"));
        gpioPins.put("headphone_detect_power", new ShellConfig.GpioPin("headphone_detect_power", "GPIO3_A6", "/proc/rp_gpio/gpio3a6", 1, "耳机检测控制 / 默认高"));
        gpioPins.put("headphone_detect", new ShellConfig.GpioPin("headphone_detect", "GPIO1_B0", "/proc/rp_gpio/gpio1b0", 0, "耳机检测 IO"));

        Map<String, ShellConfig.CameraChannel> cameraChannels = new LinkedHashMap<>();
        cameraChannels.put("av_out", new ShellConfig.CameraChannel("av_out", "100", "AV-OUT / DVR"));
        cameraChannels.put("reverse", new ShellConfig.CameraChannel("reverse", "101", "倒车"));
        cameraChannels.put("middle_door", new ShellConfig.CameraChannel("middle_door", "102", "中门"));
        cameraChannels.put("monitor", new ShellConfig.CameraChannel("monitor", "103", "监控"));

        Map<String, ShellConfig.CanChannel> canChannels = new LinkedHashMap<>();
        canChannels.put("can0", new ShellConfig.CanChannel("can0", "can0", "", "", "", "CAN0 / JNI 或 SocketCAN 待真机确认"));
        canChannels.put("can1", new ShellConfig.CanChannel("can1", "can1", "", "", "", "CAN1 / JNI 或 SocketCAN 待真机确认"));

        return new ShellConfig(
                "M90-STUB",
                "default-fallback",
                "fallback:code-default",
                serialChannels,
                socketChannels,
                new ShellConfig.GpioConfig(DeviceMode.STUB, gpioPins, "M90 关键 GPIO"),
                new ShellConfig.CameraConfig(DeviceMode.STUB, cameraChannels, "M90 预置 Camera 通道"),
                new ShellConfig.RfidConfig(DeviceMode.STUB, "RFID-DEMO-001", "", "", "/dev/i2c-3", "0x00", "RFID 默认走 stub，I2C-3 待真机确认地址"),
                new ShellConfig.SystemConfig(DeviceMode.STUB, false, false, false, "", "", "", "系统能力默认走 stub"),
                new ShellConfig.LocationConfig(DeviceMode.STUB, false, "gps", 1000L, 0F, "LocationManager 默认关闭，主链仍走 ttyS5"),
                new ShellConfig.CanConfig(DeviceMode.STUB, canChannels, "CAN 默认走 stub，支持 can0/can1 配置化自检"),
                new ShellConfig.KeyboardConfig(DeviceMode.STUB, "keyboard", "serial", "ttyS0 Keyboard 默认关闭"),
                new ShellConfig.DebugReplay("rs485_1", "gps", "jt808", "al808", "inner_audio", "io1", "io2", "av_out"),
                ShellConfig.BasicSetupConfig.defaults()
        );
    }

    private static ShellConfig.SerialSettings normalizeLegacySerialSettings(ShellConfig.SerialSettings serialSettings) {
        if (serialSettings == null) {
            return ShellConfig.SerialSettings.defaults();
        }
        if ("JT808".equalsIgnoreCase(serialSettings.getRs2321Protocol())
                && "AL808".equalsIgnoreCase(serialSettings.getRs2322Protocol())
                && "通达".equals(serialSettings.getRs485Protocol())
                && "无".equals(serialSettings.getRs4852Protocol())) {
            return ShellConfig.SerialSettings.defaults();
        }
        return serialSettings;
    }

    /**
     * 初始化运行期配置文件。
     * <p>
     * 第一次启动时把 assets 里的配置复制出来，后面方便直接替换或调试。
     */
    public static File bootstrapRuntimeConfig(Context context) throws Exception {
        File runtimeConfigFile = getRuntimeConfigFile(context);
        File parentFile = runtimeConfigFile.getParentFile();
        if (parentFile != null && !parentFile.exists() && !parentFile.mkdirs()) {
            throw new IllegalStateException("无法创建运行配置目录: " + parentFile.getAbsolutePath());
        }
        if (!runtimeConfigFile.exists()) {
            writeRuntimeConfig(context, loadAssetConfigText(context));
        }
        return runtimeConfigFile;
    }

    /**
     * 返回运行期配置文件。
     */
    public static File getRuntimeConfigFile(Context context) {
        File externalDir = context.getExternalFilesDir(CONFIG_RUNTIME_DIR_NAME);
        File baseDir = externalDir == null ? new File(context.getFilesDir(), CONFIG_RUNTIME_DIR_NAME) : externalDir;
        return new File(baseDir, CONFIG_RUNTIME_FILE_NAME);
    }

    /**
     * 输出当前配置文件位置，方便页面展示和导出留档。
     */
    public static String describeConfigLocations(Context context) {
        File runtimeConfigFile = getRuntimeConfigFile(context);
        return "配置位置:"
                + "\n- 运行期文件: " + runtimeConfigFile.getAbsolutePath() + (runtimeConfigFile.exists() ? " [已存在]" : " [未生成]")
                + "\n- 打包配置: assets/" + CONFIG_ASSET_NAME;
    }

    /**
     * 读取 assets 里的原始配置文本。
     */
    public static String loadAssetConfigText(Context context) throws Exception {
        try (InputStream inputStream = context.getAssets().open(CONFIG_ASSET_NAME)) {
            return readText(inputStream);
        }
    }

    /**
     * 读取当前生效的原始配置文本。
     */
    public static String loadCurrentConfigText(Context context) throws Exception {
        File runtimeConfigFile = getRuntimeConfigFile(context);
        if (runtimeConfigFile.exists()) {
            try (InputStream inputStream = new FileInputStream(runtimeConfigFile)) {
                return readText(inputStream);
            }
        }
        return loadAssetConfigText(context);
    }

    /**
     * 用当前打包配置覆盖运行期配置。
     */
    public static File resetRuntimeConfig(Context context) throws Exception {
        writeRuntimeConfig(context, loadAssetConfigText(context));
        return getRuntimeConfigFile(context);
    }

    /**
     * 把当前配置对象直接写回运行期配置文件。
     */
    public static File saveRuntimeConfig(Context context, ShellConfig shellConfig) throws Exception {
        writeRuntimeConfig(context, toJson(shellConfig));
        return getRuntimeConfigFile(context);
    }

    /**
     * 把配置对象序列化成 JSON 文本。
     */
    public static String toJson(ShellConfig shellConfig) throws Exception {
        if (shellConfig == null) {
            throw new IllegalArgumentException("shellConfig 不能为空");
        }
        JSONObject root = new JSONObject();
        root.put("deviceProfile", shellConfig.getDeviceProfile());
        root.put("configVersion", shellConfig.getConfigVersion());

        JSONObject serialObject = new JSONObject();
        for (Map.Entry<String, ShellConfig.SerialChannel> entry : shellConfig.getSerialChannels().entrySet()) {
            ShellConfig.SerialChannel channel = entry.getValue();
            JSONObject item = new JSONObject();
            item.put("portName", channel.getPortName());
            item.put("baudRate", channel.getBaudRate());
            item.put("mode", channel.getMode().toConfigValue());
            item.put("note", channel.getNote());
            serialObject.put(entry.getKey(), item);
        }
        root.put("serial", serialObject);

        JSONObject socketObject = new JSONObject();
        for (Map.Entry<String, ShellConfig.SocketChannel> entry : shellConfig.getSocketChannels().entrySet()) {
            ShellConfig.SocketChannel channel = entry.getValue();
            JSONObject item = new JSONObject();
            item.put("channelName", channel.getChannelName());
            item.put("host", channel.getHost());
            item.put("port", channel.getPort());
            item.put("mode", channel.getMode().toConfigValue());
            item.put("note", channel.getNote());
            socketObject.put(entry.getKey(), item);
        }
        root.put("socket", socketObject);

        JSONObject gpioObject = new JSONObject();
        gpioObject.put("mode", shellConfig.getGpioConfig().getMode().toConfigValue());
        gpioObject.put("note", shellConfig.getGpioConfig().getNote());
        JSONObject gpioPins = new JSONObject();
        for (Map.Entry<String, ShellConfig.GpioPin> entry : shellConfig.getGpioConfig().getPins().entrySet()) {
            ShellConfig.GpioPin pin = entry.getValue();
            JSONObject item = new JSONObject();
            item.put("pinId", pin.getPinId());
            item.put("valuePath", pin.getValuePath());
            item.put("defaultValue", pin.getDefaultValue());
            item.put("note", pin.getNote());
            gpioPins.put(entry.getKey(), item);
        }
        gpioObject.put("pins", gpioPins);
        root.put("gpio", gpioObject);

        JSONObject cameraObject = new JSONObject();
        cameraObject.put("mode", shellConfig.getCameraConfig().getMode().toConfigValue());
        cameraObject.put("note", shellConfig.getCameraConfig().getNote());
        JSONObject cameraChannels = new JSONObject();
        for (Map.Entry<String, ShellConfig.CameraChannel> entry : shellConfig.getCameraConfig().getChannels().entrySet()) {
            ShellConfig.CameraChannel channel = entry.getValue();
            JSONObject item = new JSONObject();
            item.put("cameraId", channel.getCameraId());
            item.put("note", channel.getNote());
            cameraChannels.put(entry.getKey(), item);
        }
        cameraObject.put("channels", cameraChannels);
        root.put("camera", cameraObject);

        JSONObject rfidObject = new JSONObject();
        rfidObject.put("mode", shellConfig.getRfidConfig().getMode().toConfigValue());
        rfidObject.put("mockCardNo", shellConfig.getRfidConfig().getMockCardNo());
        rfidObject.put("inputFilePath", shellConfig.getRfidConfig().getInputFilePath());
        rfidObject.put("readCommand", shellConfig.getRfidConfig().getReadCommand());
        rfidObject.put("i2cDevicePath", shellConfig.getRfidConfig().getI2cDevicePath());
        rfidObject.put("i2cAddress", shellConfig.getRfidConfig().getI2cAddress());
        rfidObject.put("note", shellConfig.getRfidConfig().getNote());
        root.put("rfid", rfidObject);

        JSONObject systemObject = new JSONObject();
        systemObject.put("mode", shellConfig.getSystemConfig().getMode().toConfigValue());
        systemObject.put("supportSilentInstall", shellConfig.getSystemConfig().isSupportSilentInstall());
        systemObject.put("allowReboot", shellConfig.getSystemConfig().isAllowReboot());
        systemObject.put("allowSetTime", shellConfig.getSystemConfig().isAllowSetTime());
        systemObject.put("silentInstallCommand", shellConfig.getSystemConfig().getSilentInstallCommand());
        systemObject.put("rebootCommand", shellConfig.getSystemConfig().getRebootCommand());
        systemObject.put("setTimeCommand", shellConfig.getSystemConfig().getSetTimeCommand());
        systemObject.put("note", shellConfig.getSystemConfig().getNote());
        root.put("systemOps", systemObject);

        JSONObject locationObject = new JSONObject();
        locationObject.put("mode", shellConfig.getLocationConfig().getMode().toConfigValue());
        locationObject.put("enabled", shellConfig.getLocationConfig().isEnabled());
        locationObject.put("provider", shellConfig.getLocationConfig().getProvider());
        locationObject.put("minTimeMs", shellConfig.getLocationConfig().getMinTimeMs());
        locationObject.put("minDistanceMeters", shellConfig.getLocationConfig().getMinDistanceMeters());
        locationObject.put("note", shellConfig.getLocationConfig().getNote());
        root.put("location", locationObject);

        JSONObject canObject = new JSONObject();
        canObject.put("mode", shellConfig.getCanConfig().getMode().toConfigValue());
        canObject.put("note", shellConfig.getCanConfig().getNote());
        JSONObject canChannelsObject = new JSONObject();
        for (Map.Entry<String, ShellConfig.CanChannel> entry : shellConfig.getCanConfig().getChannels().entrySet()) {
            ShellConfig.CanChannel channel = entry.getValue();
            JSONObject item = new JSONObject();
            item.put("interfaceName", channel.getInterfaceName());
            item.put("devicePath", channel.getDevicePath());
            item.put("readCommand", channel.getReadCommand());
            item.put("writeCommand", channel.getWriteCommand());
            item.put("note", channel.getNote());
            canChannelsObject.put(entry.getKey(), item);
        }
        canObject.put("channels", canChannelsObject);
        root.put("can", canObject);

        JSONObject keyboardObject = new JSONObject();
        keyboardObject.put("mode", shellConfig.getKeyboardConfig().getMode().toConfigValue());
        keyboardObject.put("serialKey", shellConfig.getKeyboardConfig().getSerialKey());
        keyboardObject.put("protocol", shellConfig.getKeyboardConfig().getProtocol());
        keyboardObject.put("note", shellConfig.getKeyboardConfig().getNote());
        root.put("keyboard", keyboardObject);

        JSONObject debugReplayObject = new JSONObject();
        debugReplayObject.put("displaySerialKey", shellConfig.getDebugReplay().getDisplaySerialKey());
        debugReplayObject.put("gpsSerialKey", shellConfig.getDebugReplay().getGpsSerialKey());
        debugReplayObject.put("jt808SocketKey", shellConfig.getDebugReplay().getJt808SocketKey());
        debugReplayObject.put("al808SocketKey", shellConfig.getDebugReplay().getAl808SocketKey());
        debugReplayObject.put("gpioPinKey", shellConfig.getDebugReplay().getGpioPinKey());
        debugReplayObject.put("monitorPrimaryGpioKey", shellConfig.getDebugReplay().getMonitorPrimaryGpioKey());
        debugReplayObject.put("monitorSecondaryGpioKey", shellConfig.getDebugReplay().getMonitorSecondaryGpioKey());
        debugReplayObject.put("cameraChannelKey", shellConfig.getDebugReplay().getCameraChannelKey());
        root.put("debugReplay", debugReplayObject);

        JSONObject basicSetupObject = new JSONObject();
        JSONObject newspaperObject = new JSONObject();
        newspaperObject.put("innerVolume", shellConfig.getBasicSetupConfig().getNewspaperSettings().getInnerVolume());
        newspaperObject.put("outerVolume", shellConfig.getBasicSetupConfig().getNewspaperSettings().getOuterVolume());
        newspaperObject.put("lineProperty", shellConfig.getBasicSetupConfig().getNewspaperSettings().getLineProperty());
        newspaperObject.put("angleEnabled", shellConfig.getBasicSetupConfig().getNewspaperSettings().isAngleEnabled());
        newspaperObject.put("dialectEnabled", shellConfig.getBasicSetupConfig().getNewspaperSettings().isDialectEnabled());
        newspaperObject.put("englishEnabled", shellConfig.getBasicSetupConfig().getNewspaperSettings().isEnglishEnabled());
        newspaperObject.put("externalSoundEnabled", shellConfig.getBasicSetupConfig().getNewspaperSettings().isExternalSoundEnabled());
        newspaperObject.put("nowTimeEnabled", shellConfig.getBasicSetupConfig().getNewspaperSettings().isNowTimeEnabled());
        newspaperObject.put("speedingWarningEnabled", shellConfig.getBasicSetupConfig().getNewspaperSettings().isSpeedingWarningEnabled());
        basicSetupObject.put("newspaper", newspaperObject);

        JSONObject networkSettingsObject = new JSONObject();
        networkSettingsObject.put("dispatchId", shellConfig.getBasicSetupConfig().getNetworkSettings().getDispatchId());
        networkSettingsObject.put("longInterval", shellConfig.getBasicSetupConfig().getNetworkSettings().getLongInterval());
        networkSettingsObject.put("infoInterval", shellConfig.getBasicSetupConfig().getNetworkSettings().getInfoInterval());
        networkSettingsObject.put("speedingInterval", shellConfig.getBasicSetupConfig().getNetworkSettings().getSpeedingInterval());
        networkSettingsObject.put("adwordsEnabled", shellConfig.getBasicSetupConfig().getNetworkSettings().isAdwordsEnabled());
        networkSettingsObject.put("adwordsId", shellConfig.getBasicSetupConfig().getNetworkSettings().getAdwordsId());
        networkSettingsObject.put("adwordsUser", shellConfig.getBasicSetupConfig().getNetworkSettings().getAdwordsUser());
        networkSettingsObject.put("adwordsInterval", shellConfig.getBasicSetupConfig().getNetworkSettings().getAdwordsInterval());
        basicSetupObject.put("network", networkSettingsObject);

        JSONObject serialSettingsObject = new JSONObject();
        serialSettingsObject.put("rs2321Protocol", shellConfig.getBasicSetupConfig().getSerialSettings().getRs2321Protocol());
        serialSettingsObject.put("rs2322Protocol", shellConfig.getBasicSetupConfig().getSerialSettings().getRs2322Protocol());
        serialSettingsObject.put("rs485Protocol", shellConfig.getBasicSetupConfig().getSerialSettings().getRs485Protocol());
        serialSettingsObject.put("rs4852Protocol", shellConfig.getBasicSetupConfig().getSerialSettings().getRs4852Protocol());
        basicSetupObject.put("serialSettings", serialSettingsObject);

        JSONObject ttsObject = new JSONObject();
        ttsObject.put("enabled", shellConfig.getBasicSetupConfig().getTtsSettings().isEnabled());
        ttsObject.put("innerVolume", shellConfig.getBasicSetupConfig().getTtsSettings().getInnerVolume());
        ttsObject.put("outerVolume", shellConfig.getBasicSetupConfig().getTtsSettings().getOuterVolume());
        basicSetupObject.put("tts", ttsObject);

        JSONObject languageObject = new JSONObject();
        languageObject.put("languageCode", shellConfig.getBasicSetupConfig().getLanguageSettings().getLanguageCode());
        basicSetupObject.put("language", languageObject);

        JSONObject otherObject = new JSONObject();
        otherObject.put("shoutingVolume", shellConfig.getBasicSetupConfig().getOtherSettings().getShoutingVolume());
        otherObject.put("dispatchVolume", shellConfig.getBasicSetupConfig().getOtherSettings().getDispatchVolume());
        otherObject.put("vehicleNumber", shellConfig.getBasicSetupConfig().getOtherSettings().getVehicleNumber());
        otherObject.put("shoutingPrimaryGpioKey", shellConfig.getBasicSetupConfig().getOtherSettings().getShoutingPrimaryGpioKey());
        otherObject.put("shoutingSecondaryGpioKey", shellConfig.getBasicSetupConfig().getOtherSettings().getShoutingSecondaryGpioKey());
        basicSetupObject.put("other", otherObject);

        JSONObject wirelessObject = new JSONObject();
        wirelessObject.put("systemEntryEnabled", shellConfig.getBasicSetupConfig().getWirelessSettings().isSystemEntryEnabled());
        basicSetupObject.put("wireless", wirelessObject);

        JSONObject resourceImportObject = new JSONObject();
        resourceImportObject.put("stationResourceImported", shellConfig.getBasicSetupConfig().getResourceImportSettings().isStationResourceImported());
        resourceImportObject.put("source", shellConfig.getBasicSetupConfig().getResourceImportSettings().getSource());
        resourceImportObject.put("lineName", shellConfig.getBasicSetupConfig().getResourceImportSettings().getLineName());
        resourceImportObject.put("directionText", shellConfig.getBasicSetupConfig().getResourceImportSettings().getDirectionText());
        resourceImportObject.put("lineAttribute", shellConfig.getBasicSetupConfig().getResourceImportSettings().getLineAttribute());
        resourceImportObject.put("updatedAt", shellConfig.getBasicSetupConfig().getResourceImportSettings().getUpdatedAt());
        basicSetupObject.put("resourceImport", resourceImportObject);

        JSONObject protocolLinkageObject = new JSONObject();
        protocolLinkageObject.put("dispatchOwner", shellConfig.getBasicSetupConfig().getProtocolLinkageSettings().getDispatchOwner());
        protocolLinkageObject.put("updatedAt", shellConfig.getBasicSetupConfig().getProtocolLinkageSettings().getUpdatedAt());
        basicSetupObject.put("protocolLinkage", protocolLinkageObject);

        root.put("basicSetup", basicSetupObject);

        return root.toString(2);
    }

    private static void writeRuntimeConfig(Context context, String configText) throws Exception {
        File runtimeConfigFile = getRuntimeConfigFile(context);
        File parentFile = runtimeConfigFile.getParentFile();
        if (parentFile != null && !parentFile.exists() && !parentFile.mkdirs()) {
            throw new IllegalStateException("无法创建运行配置目录: " + parentFile.getAbsolutePath());
        }
        try (FileOutputStream outputStream = new FileOutputStream(runtimeConfigFile, false)) {
            outputStream.write(configText.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
    }

    private static String readText(InputStream inputStream) throws Exception {
        try (InputStream source = inputStream; ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = source.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
            return output.toString(StandardCharsets.UTF_8.name());
        }
    }
}
