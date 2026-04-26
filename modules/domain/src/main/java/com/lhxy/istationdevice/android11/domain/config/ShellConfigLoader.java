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

        JSONObject debugReplayObject = root.optJSONObject("debugReplay");
        ShellConfig.DebugReplay debugReplay = new ShellConfig.DebugReplay(
                debugReplayObject == null ? "rs485_1" : debugReplayObject.optString("displaySerialKey", "rs485_1"),
                debugReplayObject == null ? "gps" : debugReplayObject.optString("gpsSerialKey", "gps"),
                debugReplayObject == null ? "jt808" : debugReplayObject.optString("jt808SocketKey", "jt808"),
                debugReplayObject == null ? "al808" : debugReplayObject.optString("al808SocketKey", "al808"),
                debugReplayObject == null ? "inner_audio" : debugReplayObject.optString("gpioPinKey", "inner_audio"),
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
                    networkSettingsObject == null || networkSettingsObject.optBoolean("adwordsEnabled", true),
                    networkSettingsObject == null ? "1" : networkSettingsObject.optString("adwordsId", "1"),
                    networkSettingsObject == null ? "admin" : networkSettingsObject.optString("adwordsUser", "admin"),
                    networkSettingsObject == null ? 10 : networkSettingsObject.optInt("adwordsInterval", 10)
                ),
                new ShellConfig.SerialSettings(
                    serialSettingsObject == null ? "JT808" : serialSettingsObject.optString("rs2321Protocol", "JT808"),
                    serialSettingsObject == null ? "AL808" : serialSettingsObject.optString("rs2322Protocol", "AL808"),
                    serialSettingsObject == null ? "通达" : serialSettingsObject.optString("rs485Protocol", "通达")
                ),
                new ShellConfig.TtsSettings(
                    ttsObject == null || ttsObject.optBoolean("enabled", true),
                    ttsObject == null ? 8 : ttsObject.optInt("innerVolume", 8),
                    ttsObject == null ? 8 : ttsObject.optInt("outerVolume", 8)
                ),
                new ShellConfig.LanguageSettings(languageObject == null ? "auto" : languageObject.optString("languageCode", "auto")),
                new ShellConfig.OtherSettings(
                    otherObject == null ? 50 : otherObject.optInt("shoutingVolume", 50),
                    otherObject == null ? 7 : otherObject.optInt("dispatchVolume", 7)
                ),
                new ShellConfig.WirelessSettings(wirelessObject == null || wirelessObject.optBoolean("systemEntryEnabled", true)),
                new ShellConfig.ResourceImportSettings(
                    resourceImportObject != null && resourceImportObject.optBoolean("stationResourceImported", false),
                    resourceImportObject == null ? "-" : resourceImportObject.optString("source", "-"),
                    resourceImportObject == null ? "-" : resourceImportObject.optString("lineName", "-"),
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
        serialChannels.put("rs232_1", new ShellConfig.SerialChannel("rs232_1", "ttyS3", 9600, SerialMode.STUB, "DVR / RS232-1"));
        serialChannels.put("rs232_2", new ShellConfig.SerialChannel("rs232_2", "ttyS4", 9600, SerialMode.STUB, "RS232-2"));
        serialChannels.put("gps", new ShellConfig.SerialChannel("gps", "ttyS5", 9600, SerialMode.STUB, "GPS"));
        serialChannels.put("rs485_1", new ShellConfig.SerialChannel("rs485_1", "ttyS7", 9600, SerialMode.STUB, "RS485-1"));
        serialChannels.put("rs485_2", new ShellConfig.SerialChannel("rs485_2", "ttyS9", 9600, SerialMode.STUB, "RS485-2"));

        Map<String, ShellConfig.SocketChannel> socketChannels = new LinkedHashMap<>();
        socketChannels.put("jt808", new ShellConfig.SocketChannel("jt808", "JT808_SOCKET", "211.154.159.34", 7000, SocketMode.STUB, "JT808 调度"));
        socketChannels.put("al808", new ShellConfig.SocketChannel("al808", "AL808_SOCKET", "211.154.159.34", 7000, SocketMode.STUB, "AL808 调度"));

        Map<String, ShellConfig.GpioPin> gpioPins = new LinkedHashMap<>();
        gpioPins.put("inner_audio", new ShellConfig.GpioPin("inner_audio", "GPIO1_B1", "", 0, "内音"));
        gpioPins.put("outer_audio", new ShellConfig.GpioPin("outer_audio", "GPIO1_B2", "", 0, "外音"));
        gpioPins.put("inner_speaker", new ShellConfig.GpioPin("inner_speaker", "GPIO0_D6", "", 0, "内喇叭"));
        gpioPins.put("io1", new ShellConfig.GpioPin("io1", "GPIO1_D0", "", 1, "IO1"));
        gpioPins.put("io2", new ShellConfig.GpioPin("io2", "GPIO1_D1", "", 0, "IO2 / 中门开"));
        gpioPins.put("io5", new ShellConfig.GpioPin("io5", "GPIO1_D4", "", 0, "IO5"));

        Map<String, ShellConfig.CameraChannel> cameraChannels = new LinkedHashMap<>();
        cameraChannels.put("av_out", new ShellConfig.CameraChannel("av_out", "0", "AV-OUT / DVR"));
        cameraChannels.put("reverse", new ShellConfig.CameraChannel("reverse", "1", "倒车"));
        cameraChannels.put("middle_door", new ShellConfig.CameraChannel("middle_door", "2", "中门"));
        cameraChannels.put("monitor", new ShellConfig.CameraChannel("monitor", "3", "监控"));

        return new ShellConfig(
                "M90-STUB",
                "default-fallback",
                "fallback:code-default",
                serialChannels,
                socketChannels,
                new ShellConfig.GpioConfig(DeviceMode.STUB, gpioPins, "M90 关键 GPIO"),
                new ShellConfig.CameraConfig(DeviceMode.STUB, cameraChannels, "M90 预置 Camera 通道"),
                new ShellConfig.RfidConfig(DeviceMode.STUB, "RFID-DEMO-001", "", "", "RFID 默认走 stub"),
                new ShellConfig.SystemConfig(DeviceMode.STUB, false, false, false, "", "", "", "系统能力默认走 stub"),
                new ShellConfig.DebugReplay("rs485_1", "gps", "jt808", "al808", "inner_audio", "av_out"),
                ShellConfig.BasicSetupConfig.defaults()
        );
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

        JSONObject debugReplayObject = new JSONObject();
        debugReplayObject.put("displaySerialKey", shellConfig.getDebugReplay().getDisplaySerialKey());
        debugReplayObject.put("gpsSerialKey", shellConfig.getDebugReplay().getGpsSerialKey());
        debugReplayObject.put("jt808SocketKey", shellConfig.getDebugReplay().getJt808SocketKey());
        debugReplayObject.put("al808SocketKey", shellConfig.getDebugReplay().getAl808SocketKey());
        debugReplayObject.put("gpioPinKey", shellConfig.getDebugReplay().getGpioPinKey());
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
        networkSettingsObject.put("adwordsEnabled", shellConfig.getBasicSetupConfig().getNetworkSettings().isAdwordsEnabled());
        networkSettingsObject.put("adwordsId", shellConfig.getBasicSetupConfig().getNetworkSettings().getAdwordsId());
        networkSettingsObject.put("adwordsUser", shellConfig.getBasicSetupConfig().getNetworkSettings().getAdwordsUser());
        networkSettingsObject.put("adwordsInterval", shellConfig.getBasicSetupConfig().getNetworkSettings().getAdwordsInterval());
        basicSetupObject.put("network", networkSettingsObject);

        JSONObject serialSettingsObject = new JSONObject();
        serialSettingsObject.put("rs2321Protocol", shellConfig.getBasicSetupConfig().getSerialSettings().getRs2321Protocol());
        serialSettingsObject.put("rs2322Protocol", shellConfig.getBasicSetupConfig().getSerialSettings().getRs2322Protocol());
        serialSettingsObject.put("rs485Protocol", shellConfig.getBasicSetupConfig().getSerialSettings().getRs485Protocol());
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
        basicSetupObject.put("other", otherObject);

        JSONObject wirelessObject = new JSONObject();
        wirelessObject.put("systemEntryEnabled", shellConfig.getBasicSetupConfig().getWirelessSettings().isSystemEntryEnabled());
        basicSetupObject.put("wireless", wirelessObject);

        JSONObject resourceImportObject = new JSONObject();
        resourceImportObject.put("stationResourceImported", shellConfig.getBasicSetupConfig().getResourceImportSettings().isStationResourceImported());
        resourceImportObject.put("source", shellConfig.getBasicSetupConfig().getResourceImportSettings().getSource());
        resourceImportObject.put("lineName", shellConfig.getBasicSetupConfig().getResourceImportSettings().getLineName());
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
