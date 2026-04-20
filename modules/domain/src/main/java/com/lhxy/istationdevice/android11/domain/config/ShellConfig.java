package com.lhxy.istationdevice.android11.domain.config;

import com.lhxy.istationdevice.android11.deviceapi.DeviceMode;
import com.lhxy.istationdevice.android11.deviceapi.SerialMode;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortConfig;
import com.lhxy.istationdevice.android11.deviceapi.SocketEndpointConfig;
import com.lhxy.istationdevice.android11.deviceapi.SocketMode;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 终端配置
 * <p>
 * 统一承接串口、Socket、GPIO、Camera、RFID、SystemOps 和调试入口。
 * 后面不管是换真口、换 GPIO 路径还是切厂商命令，都优先改这里，不要把设备细节散回页面代码。
 */
public final class ShellConfig {
    private final String deviceProfile;
    private final String configVersion;
    private final String configSource;
    private final Map<String, SerialChannel> serialChannels;
    private final Map<String, SocketChannel> socketChannels;
    private final GpioConfig gpioConfig;
    private final CameraConfig cameraConfig;
    private final RfidConfig rfidConfig;
    private final SystemConfig systemConfig;
    private final DebugReplay debugReplay;
    private final BasicSetupConfig basicSetupConfig;

    public ShellConfig(
            String deviceProfile,
            String configVersion,
            String configSource,
            Map<String, SerialChannel> serialChannels,
            Map<String, SocketChannel> socketChannels,
            GpioConfig gpioConfig,
            CameraConfig cameraConfig,
            RfidConfig rfidConfig,
            SystemConfig systemConfig,
                DebugReplay debugReplay
    ) {
            this(
                deviceProfile,
                configVersion,
                configSource,
                serialChannels,
                socketChannels,
                gpioConfig,
                cameraConfig,
                rfidConfig,
                systemConfig,
                debugReplay,
                BasicSetupConfig.defaults()
            );
            }

            public ShellConfig(
                String deviceProfile,
                String configVersion,
                String configSource,
                Map<String, SerialChannel> serialChannels,
                Map<String, SocketChannel> socketChannels,
                GpioConfig gpioConfig,
                CameraConfig cameraConfig,
                RfidConfig rfidConfig,
                SystemConfig systemConfig,
                DebugReplay debugReplay,
                BasicSetupConfig basicSetupConfig
            ) {
        this.deviceProfile = deviceProfile;
        this.configVersion = configVersion;
        this.configSource = configSource;
        this.serialChannels = Collections.unmodifiableMap(new LinkedHashMap<>(serialChannels));
        this.socketChannels = Collections.unmodifiableMap(new LinkedHashMap<>(socketChannels));
        this.gpioConfig = gpioConfig == null ? GpioConfig.empty() : gpioConfig;
        this.cameraConfig = cameraConfig == null ? CameraConfig.empty() : cameraConfig;
        this.rfidConfig = rfidConfig == null ? RfidConfig.stub() : rfidConfig;
        this.systemConfig = systemConfig == null ? SystemConfig.stub() : systemConfig;
        this.debugReplay = debugReplay == null ? DebugReplay.defaultReplay() : debugReplay;
        this.basicSetupConfig = basicSetupConfig == null ? BasicSetupConfig.defaults() : basicSetupConfig;
    }

    /**
     * 终端配置名称。
     */
    public String getDeviceProfile() {
        return deviceProfile;
    }

    /**
     * 当前配置版本。
     */
    public String getConfigVersion() {
        return configVersion;
    }

    /**
     * 当前配置来源。
     */
    public String getConfigSource() {
        return configSource;
    }

    /**
     * 返回所有串口配置。
     */
    public Map<String, SerialChannel> getSerialChannels() {
        return serialChannels;
    }

    /**
     * 返回所有 Socket 配置。
     */
    public Map<String, SocketChannel> getSocketChannels() {
        return socketChannels;
    }

    /**
     * 返回 GPIO 配置。
     */
    public GpioConfig getGpioConfig() {
        return gpioConfig;
    }

    /**
     * 返回 Camera 配置。
     */
    public CameraConfig getCameraConfig() {
        return cameraConfig;
    }

    /**
     * 返回 RFID 配置。
     */
    public RfidConfig getRfidConfig() {
        return rfidConfig;
    }

    /**
     * 返回系统能力配置。
     */
    public SystemConfig getSystemConfig() {
        return systemConfig;
    }

    /**
     * 返回调试回放入口配置。
     */
    public DebugReplay getDebugReplay() {
        return debugReplay;
    }

    /**
     * 返回系统设置页统一配置。
     */
    public BasicSetupConfig getBasicSetupConfig() {
        return basicSetupConfig;
    }

    /**
     * 根据 key 获取串口配置。
     */
    public SerialChannel requireSerialChannel(String key) {
        SerialChannel channel = serialChannels.get(key);
        if (channel == null) {
            throw new IllegalArgumentException("缺少串口配置: " + key);
        }
        return channel;
    }

    /**
     * 根据 key 获取 Socket 配置。
     */
    public SocketChannel requireSocketChannel(String key) {
        SocketChannel channel = socketChannels.get(key);
        if (channel == null) {
            throw new IllegalArgumentException("缺少 Socket 配置: " + key);
        }
        return channel;
    }

    /**
     * 首页和调试页统一展示的配置摘要。
     */
    public String describe() {
        StringBuilder builder = new StringBuilder();
        builder.append("当前配置: ").append(deviceProfile)
                .append(" / 版本 ").append(configVersion)
                .append(" / 来源 ").append(configSource);

        if (!serialChannels.isEmpty()) {
            builder.append("\n串口:");
            for (Map.Entry<String, SerialChannel> entry : serialChannels.entrySet()) {
                builder.append("\n- ").append(entry.getKey())
                        .append(" -> ").append(entry.getValue().getPortName())
                        .append(" @").append(entry.getValue().getBaudRate())
                        .append(" [").append(entry.getValue().getMode().toConfigValue()).append("]");
            }
        }

        if (!socketChannels.isEmpty()) {
            builder.append("\nSocket:");
            for (Map.Entry<String, SocketChannel> entry : socketChannels.entrySet()) {
                builder.append("\n- ").append(entry.getKey())
                        .append(" -> ").append(entry.getValue().getHost())
                        .append(":").append(entry.getValue().getPort())
                        .append(" [").append(entry.getValue().getMode().toConfigValue()).append("]");
            }
        }

        builder.append("\nGPIO: [").append(gpioConfig.getMode().toConfigValue()).append("]");
        for (Map.Entry<String, GpioPin> entry : gpioConfig.getPins().entrySet()) {
            builder.append("\n- ").append(entry.getKey())
                    .append(" -> ").append(entry.getValue().getPinId())
                    .append(entry.getValue().getValuePath().isEmpty() ? "" : " / " + entry.getValue().getValuePath());
        }

        builder.append("\nCamera: [").append(cameraConfig.getMode().toConfigValue()).append("]");
        for (Map.Entry<String, CameraChannel> entry : cameraConfig.getChannels().entrySet()) {
            builder.append("\n- ").append(entry.getKey())
                    .append(" -> cameraId ").append(entry.getValue().getCameraId());
        }

        builder.append("\nRFID: [").append(rfidConfig.getMode().toConfigValue()).append("]")
                .append(" / 命令=").append(emptyAsDash(rfidConfig.getReadCommand()))
                .append(" / 文件=").append(emptyAsDash(rfidConfig.getInputFilePath()));

        builder.append("\nSystemOps: [").append(systemConfig.getMode().toConfigValue()).append("]")
                .append(" / 静默安装=").append(systemConfig.isSupportSilentInstall())
                .append(" / 重启=").append(systemConfig.isAllowReboot())
                .append(" / 校时=").append(systemConfig.isAllowSetTime());

        builder.append("\n回放入口:")
                .append("\n- 屏显走 ").append(debugReplay.getDisplaySerialKey())
                .append("\n- GPS 走 ").append(debugReplay.getGpsSerialKey())
                .append("\n- JT808 走 ").append(debugReplay.getJt808SocketKey())
                .append("\n- AL808 走 ").append(debugReplay.getAl808SocketKey())
                .append("\n- 默认 GPIO 走 ").append(debugReplay.getGpioPinKey())
                .append("\n- 默认 Camera 走 ").append(debugReplay.getCameraChannelKey());

        builder.append("\n系统设置:")
            .append("\n- 报站: 内音=").append(basicSetupConfig.getNewspaperSettings().getInnerVolume())
            .append(" / 外音=").append(basicSetupConfig.getNewspaperSettings().getOuterVolume())
            .append(" / 属性=").append(basicSetupConfig.getNewspaperSettings().getLineProperty())
            .append(" / 外音开关=").append(basicSetupConfig.getNewspaperSettings().isExternalSoundEnabled())
            .append("\n- 资源: 导入=").append(basicSetupConfig.getResourceImportSettings().isStationResourceImported())
            .append(" / 线路=").append(basicSetupConfig.getResourceImportSettings().getLineName())
            .append(" / 来源=").append(basicSetupConfig.getResourceImportSettings().getSource())
            .append("\n- TTS: 开关=").append(basicSetupConfig.getTtsSettings().isEnabled())
            .append(" / 内音=").append(basicSetupConfig.getTtsSettings().getInnerVolume())
            .append(" / 外音=").append(basicSetupConfig.getTtsSettings().getOuterVolume())
            .append("\n- 语言=").append(basicSetupConfig.getLanguageSettings().getLanguageCode())
            .append("\n- 其他: 喊话=").append(basicSetupConfig.getOtherSettings().getShoutingVolume())
            .append(" / 调度=").append(basicSetupConfig.getOtherSettings().getDispatchVolume())
            .append("\n- 协议互斥: dispatchOwner=").append(basicSetupConfig.getProtocolLinkageSettings().getDispatchOwner());

        return builder.toString();
    }

    private String emptyAsDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    /**
     * 串口配置项。
     */
    public static final class SerialChannel {
        private final String key;
        private final String portName;
        private final int baudRate;
        private final SerialMode mode;
        private final String note;

        public SerialChannel(String key, String portName, int baudRate, SerialMode mode, String note) {
            this.key = key;
            this.portName = portName;
            this.baudRate = baudRate;
            this.mode = mode == null ? SerialMode.STUB : mode;
            this.note = note == null ? "" : note;
        }

        public String getKey() {
            return key;
        }

        public String getPortName() {
            return portName;
        }

        public int getBaudRate() {
            return baudRate;
        }

        public SerialMode getMode() {
            return mode;
        }

        public String getNote() {
            return note;
        }

        /**
         * 转成设备层可直接使用的串口配置对象。
         */
        public SerialPortConfig toSerialPortConfig() {
            return new SerialPortConfig(portName, baudRate, mode);
        }
    }

    /**
     * Socket 配置项。
     */
    public static final class SocketChannel {
        private final String key;
        private final String channelName;
        private final String host;
        private final int port;
        private final SocketMode mode;
        private final String note;

        public SocketChannel(String key, String channelName, String host, int port, SocketMode mode, String note) {
            this.key = key;
            this.channelName = channelName;
            this.host = host;
            this.port = port;
            this.mode = mode == null ? SocketMode.STUB : mode;
            this.note = note == null ? "" : note;
        }

        public String getKey() {
            return key;
        }

        public String getChannelName() {
            return channelName;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public SocketMode getMode() {
            return mode;
        }

        public String getNote() {
            return note;
        }

        /**
         * 转成设备层可直接使用的 Socket 配置对象。
         */
        public SocketEndpointConfig toSocketEndpointConfig() {
            return new SocketEndpointConfig(channelName, host, port, mode);
        }
    }

    /**
     * GPIO 总配置。
     */
    public static final class GpioConfig {
        private final DeviceMode mode;
        private final Map<String, GpioPin> pins;
        private final String note;

        public GpioConfig(DeviceMode mode, Map<String, GpioPin> pins, String note) {
            this.mode = mode == null ? DeviceMode.STUB : mode;
            this.pins = Collections.unmodifiableMap(new LinkedHashMap<>(pins));
            this.note = note == null ? "" : note;
        }

        public static GpioConfig empty() {
            return new GpioConfig(DeviceMode.STUB, new LinkedHashMap<>(), "");
        }

        public DeviceMode getMode() {
            return mode;
        }

        public Map<String, GpioPin> getPins() {
            return pins;
        }

        public String getNote() {
            return note;
        }

        public GpioPin requirePin(String key) {
            GpioPin gpioPin = pins.get(key);
            if (gpioPin == null) {
                throw new IllegalArgumentException("缺少 GPIO 配置: " + key);
            }
            return gpioPin;
        }
    }

    /**
     * 单个 GPIO 定义。
     */
    public static final class GpioPin {
        private final String key;
        private final String pinId;
        private final String valuePath;
        private final int defaultValue;
        private final String note;

        public GpioPin(String key, String pinId, String valuePath, int defaultValue, String note) {
            this.key = key;
            this.pinId = pinId == null ? "" : pinId;
            this.valuePath = valuePath == null ? "" : valuePath;
            this.defaultValue = defaultValue;
            this.note = note == null ? "" : note;
        }

        public String getKey() {
            return key;
        }

        public String getPinId() {
            return pinId;
        }

        public String getValuePath() {
            return valuePath;
        }

        public int getDefaultValue() {
            return defaultValue;
        }

        public String getNote() {
            return note;
        }
    }

    /**
     * Camera 总配置。
     */
    public static final class CameraConfig {
        private final DeviceMode mode;
        private final Map<String, CameraChannel> channels;
        private final String note;

        public CameraConfig(DeviceMode mode, Map<String, CameraChannel> channels, String note) {
            this.mode = mode == null ? DeviceMode.STUB : mode;
            this.channels = Collections.unmodifiableMap(new LinkedHashMap<>(channels));
            this.note = note == null ? "" : note;
        }

        public static CameraConfig empty() {
            return new CameraConfig(DeviceMode.STUB, new LinkedHashMap<>(), "");
        }

        public DeviceMode getMode() {
            return mode;
        }

        public Map<String, CameraChannel> getChannels() {
            return channels;
        }

        public String getNote() {
            return note;
        }

        public CameraChannel requireChannel(String key) {
            CameraChannel channel = channels.get(key);
            if (channel == null) {
                throw new IllegalArgumentException("缺少 Camera 配置: " + key);
            }
            return channel;
        }
    }

    /**
     * 单个 Camera 逻辑通道。
     */
    public static final class CameraChannel {
        private final String key;
        private final String cameraId;
        private final String note;

        public CameraChannel(String key, String cameraId, String note) {
            this.key = key;
            this.cameraId = cameraId == null ? "" : cameraId;
            this.note = note == null ? "" : note;
        }

        public String getKey() {
            return key;
        }

        public String getCameraId() {
            return cameraId;
        }

        public String getNote() {
            return note;
        }
    }

    /**
     * RFID 配置。
     */
    public static final class RfidConfig {
        private final DeviceMode mode;
        private final String mockCardNo;
        private final String inputFilePath;
        private final String readCommand;
        private final String note;

        public RfidConfig(DeviceMode mode, String mockCardNo, String inputFilePath, String readCommand, String note) {
            this.mode = mode == null ? DeviceMode.STUB : mode;
            this.mockCardNo = mockCardNo == null ? "" : mockCardNo;
            this.inputFilePath = inputFilePath == null ? "" : inputFilePath;
            this.readCommand = readCommand == null ? "" : readCommand;
            this.note = note == null ? "" : note;
        }

        public static RfidConfig stub() {
            return new RfidConfig(DeviceMode.STUB, "RFID-DEMO-001", "", "", "");
        }

        public DeviceMode getMode() {
            return mode;
        }

        public String getMockCardNo() {
            return mockCardNo;
        }

        public String getInputFilePath() {
            return inputFilePath;
        }

        public String getReadCommand() {
            return readCommand;
        }

        public String getNote() {
            return note;
        }
    }

    /**
     * 系统能力配置。
     */
    public static final class SystemConfig {
        private final DeviceMode mode;
        private final boolean supportSilentInstall;
        private final boolean allowReboot;
        private final boolean allowSetTime;
        private final String silentInstallCommand;
        private final String rebootCommand;
        private final String setTimeCommand;
        private final String note;

        public SystemConfig(
                DeviceMode mode,
                boolean supportSilentInstall,
                boolean allowReboot,
                boolean allowSetTime,
                String silentInstallCommand,
                String rebootCommand,
                String setTimeCommand,
                String note
        ) {
            this.mode = mode == null ? DeviceMode.STUB : mode;
            this.supportSilentInstall = supportSilentInstall;
            this.allowReboot = allowReboot;
            this.allowSetTime = allowSetTime;
            this.silentInstallCommand = silentInstallCommand == null ? "" : silentInstallCommand;
            this.rebootCommand = rebootCommand == null ? "" : rebootCommand;
            this.setTimeCommand = setTimeCommand == null ? "" : setTimeCommand;
            this.note = note == null ? "" : note;
        }

        public static SystemConfig stub() {
            return new SystemConfig(DeviceMode.STUB, false, false, false, "", "", "", "");
        }

        public DeviceMode getMode() {
            return mode;
        }

        public boolean isSupportSilentInstall() {
            return supportSilentInstall;
        }

        public boolean isAllowReboot() {
            return allowReboot;
        }

        public boolean isAllowSetTime() {
            return allowSetTime;
        }

        public String getSilentInstallCommand() {
            return silentInstallCommand;
        }

        public String getRebootCommand() {
            return rebootCommand;
        }

        public String getSetTimeCommand() {
            return setTimeCommand;
        }

        public String getNote() {
            return note;
        }
    }

    /**
     * 调试页和首页示例链路使用的回放入口。
     */
    public static final class DebugReplay {
        private final String displaySerialKey;
        private final String gpsSerialKey;
        private final String jt808SocketKey;
        private final String al808SocketKey;
        private final String gpioPinKey;
        private final String cameraChannelKey;

        public DebugReplay(
                String displaySerialKey,
                String gpsSerialKey,
                String jt808SocketKey,
                String al808SocketKey,
                String gpioPinKey,
                String cameraChannelKey
        ) {
            this.displaySerialKey = displaySerialKey;
            this.gpsSerialKey = gpsSerialKey;
            this.jt808SocketKey = jt808SocketKey;
            this.al808SocketKey = al808SocketKey;
            this.gpioPinKey = gpioPinKey;
            this.cameraChannelKey = cameraChannelKey;
        }

        public static DebugReplay defaultReplay() {
            return new DebugReplay("rs485_1", "gps", "jt808", "al808", "inner_audio", "av_out");
        }

        public String getDisplaySerialKey() {
            return displaySerialKey;
        }

        public String getGpsSerialKey() {
            return gpsSerialKey;
        }

        public String getJt808SocketKey() {
            return jt808SocketKey;
        }

        public String getAl808SocketKey() {
            return al808SocketKey;
        }

        public String getGpioPinKey() {
            return gpioPinKey;
        }

        public String getCameraChannelKey() {
            return cameraChannelKey;
        }
    }

    /**
     * 系统设置统一配置。
     */
    public static final class BasicSetupConfig {
        private final NewspaperSettings newspaperSettings;
        private final NetworkSettings networkSettings;
        private final SerialSettings serialSettings;
        private final TtsSettings ttsSettings;
        private final LanguageSettings languageSettings;
        private final OtherSettings otherSettings;
        private final WirelessSettings wirelessSettings;
        private final ResourceImportSettings resourceImportSettings;
        private final ProtocolLinkageSettings protocolLinkageSettings;

        public BasicSetupConfig(
                NewspaperSettings newspaperSettings,
                NetworkSettings networkSettings,
                SerialSettings serialSettings,
                TtsSettings ttsSettings,
                LanguageSettings languageSettings,
                OtherSettings otherSettings,
            WirelessSettings wirelessSettings,
            ResourceImportSettings resourceImportSettings,
            ProtocolLinkageSettings protocolLinkageSettings
        ) {
            this.newspaperSettings = newspaperSettings == null ? NewspaperSettings.defaults() : newspaperSettings;
            this.networkSettings = networkSettings == null ? NetworkSettings.defaults() : networkSettings;
            this.serialSettings = serialSettings == null ? SerialSettings.defaults() : serialSettings;
            this.ttsSettings = ttsSettings == null ? TtsSettings.defaults() : ttsSettings;
            this.languageSettings = languageSettings == null ? LanguageSettings.defaults() : languageSettings;
            this.otherSettings = otherSettings == null ? OtherSettings.defaults() : otherSettings;
            this.wirelessSettings = wirelessSettings == null ? WirelessSettings.defaults() : wirelessSettings;
            this.resourceImportSettings = resourceImportSettings == null ? ResourceImportSettings.defaults() : resourceImportSettings;
            this.protocolLinkageSettings = protocolLinkageSettings == null ? ProtocolLinkageSettings.defaults() : protocolLinkageSettings;
        }

        public static BasicSetupConfig defaults() {
            return new BasicSetupConfig(
                    NewspaperSettings.defaults(),
                    NetworkSettings.defaults(),
                    SerialSettings.defaults(),
                    TtsSettings.defaults(),
                    LanguageSettings.defaults(),
                    OtherSettings.defaults(),
                    WirelessSettings.defaults(),
                    ResourceImportSettings.defaults(),
                    ProtocolLinkageSettings.defaults()
            );
        }

        public NewspaperSettings getNewspaperSettings() {
            return newspaperSettings;
        }

        public NetworkSettings getNetworkSettings() {
            return networkSettings;
        }

        public SerialSettings getSerialSettings() {
            return serialSettings;
        }

        public TtsSettings getTtsSettings() {
            return ttsSettings;
        }

        public LanguageSettings getLanguageSettings() {
            return languageSettings;
        }

        public OtherSettings getOtherSettings() {
            return otherSettings;
        }

        public WirelessSettings getWirelessSettings() {
            return wirelessSettings;
        }

        public ResourceImportSettings getResourceImportSettings() {
            return resourceImportSettings;
        }

        public ProtocolLinkageSettings getProtocolLinkageSettings() {
            return protocolLinkageSettings;
        }
    }

    public static final class ResourceImportSettings {
        private final boolean stationResourceImported;
        private final String source;
        private final String lineName;
        private final long updatedAt;

        public ResourceImportSettings(boolean stationResourceImported, String source, String lineName, long updatedAt) {
            this.stationResourceImported = stationResourceImported;
            this.source = source == null || source.trim().isEmpty() ? "-" : source.trim();
            this.lineName = lineName == null || lineName.trim().isEmpty() ? "-" : lineName.trim();
            this.updatedAt = updatedAt;
        }

        public static ResourceImportSettings defaults() {
            return new ResourceImportSettings(false, "-", "-", 0L);
        }

        public boolean isStationResourceImported() { return stationResourceImported; }
        public String getSource() { return source; }
        public String getLineName() { return lineName; }
        public long getUpdatedAt() { return updatedAt; }
    }

    public static final class ProtocolLinkageSettings {
        public static final String DISPATCH_OWNER_NETWORK = "network";
        public static final String DISPATCH_OWNER_SERIAL_RS2321 = "serial_rs232_1";
        public static final String DISPATCH_OWNER_NONE = "none";

        private final String dispatchOwner;
        private final long updatedAt;

        public ProtocolLinkageSettings(String dispatchOwner, long updatedAt) {
            this.dispatchOwner = normalizeDispatchOwner(dispatchOwner);
            this.updatedAt = updatedAt;
        }

        public static ProtocolLinkageSettings defaults() {
            return new ProtocolLinkageSettings(DISPATCH_OWNER_NETWORK, 0L);
        }

        public String getDispatchOwner() { return dispatchOwner; }
        public long getUpdatedAt() { return updatedAt; }
        public boolean isNetworkDispatchEnabled() { return DISPATCH_OWNER_NETWORK.equals(dispatchOwner); }
        public boolean isSerialDispatchEnabled() { return DISPATCH_OWNER_SERIAL_RS2321.equals(dispatchOwner); }

        private static String normalizeDispatchOwner(String dispatchOwner) {
            if (DISPATCH_OWNER_SERIAL_RS2321.equals(dispatchOwner)) {
                return DISPATCH_OWNER_SERIAL_RS2321;
            }
            if (DISPATCH_OWNER_NONE.equals(dispatchOwner)) {
                return DISPATCH_OWNER_NONE;
            }
            return DISPATCH_OWNER_NETWORK;
        }
    }

    public static final class NewspaperSettings {
        private final int innerVolume;
        private final int outerVolume;
        private final String lineProperty;
        private final boolean angleEnabled;
        private final boolean dialectEnabled;
        private final boolean englishEnabled;
        private final boolean externalSoundEnabled;
        private final boolean nowTimeEnabled;
        private final boolean speedingWarningEnabled;

        public NewspaperSettings(
                int innerVolume,
                int outerVolume,
                String lineProperty,
                boolean angleEnabled,
                boolean dialectEnabled,
                boolean englishEnabled,
                boolean externalSoundEnabled,
                boolean nowTimeEnabled,
                boolean speedingWarningEnabled
        ) {
            this.innerVolume = innerVolume;
            this.outerVolume = outerVolume;
            this.lineProperty = lineProperty == null || lineProperty.trim().isEmpty() ? "up_down" : lineProperty.trim();
            this.angleEnabled = angleEnabled;
            this.dialectEnabled = dialectEnabled;
            this.englishEnabled = englishEnabled;
            this.externalSoundEnabled = externalSoundEnabled;
            this.nowTimeEnabled = nowTimeEnabled;
            this.speedingWarningEnabled = speedingWarningEnabled;
        }

        public static NewspaperSettings defaults() {
            return new NewspaperSettings(7, 7, "up_down", true, false, false, true, true, true);
        }

        public int getInnerVolume() { return innerVolume; }
        public int getOuterVolume() { return outerVolume; }
        public String getLineProperty() { return lineProperty; }
        public boolean isAngleEnabled() { return angleEnabled; }
        public boolean isDialectEnabled() { return dialectEnabled; }
        public boolean isEnglishEnabled() { return englishEnabled; }
        public boolean isExternalSoundEnabled() { return externalSoundEnabled; }
        public boolean isNowTimeEnabled() { return nowTimeEnabled; }
        public boolean isSpeedingWarningEnabled() { return speedingWarningEnabled; }
    }

    public static final class NetworkSettings {
        private final String dispatchId;
        private final int longInterval;
        private final int infoInterval;
        private final boolean adwordsEnabled;
        private final String adwordsId;
        private final String adwordsUser;
        private final int adwordsInterval;

        public NetworkSettings(
                String dispatchId,
                int longInterval,
                int infoInterval,
                boolean adwordsEnabled,
                String adwordsId,
                String adwordsUser,
                int adwordsInterval
        ) {
            this.dispatchId = dispatchId == null ? "1" : dispatchId;
            this.longInterval = longInterval;
            this.infoInterval = infoInterval;
            this.adwordsEnabled = adwordsEnabled;
            this.adwordsId = adwordsId == null ? "1" : adwordsId;
            this.adwordsUser = adwordsUser == null ? "admin" : adwordsUser;
            this.adwordsInterval = adwordsInterval;
        }

        public static NetworkSettings defaults() {
            return new NetworkSettings("1", 30, 5, true, "1", "admin", 10);
        }

        public String getDispatchId() { return dispatchId; }
        public int getLongInterval() { return longInterval; }
        public int getInfoInterval() { return infoInterval; }
        public boolean isAdwordsEnabled() { return adwordsEnabled; }
        public String getAdwordsId() { return adwordsId; }
        public String getAdwordsUser() { return adwordsUser; }
        public int getAdwordsInterval() { return adwordsInterval; }
    }

    public static final class SerialSettings {
        private final String rs2321Protocol;
        private final String rs2322Protocol;
        private final String rs485Protocol;

        public SerialSettings(String rs2321Protocol, String rs2322Protocol, String rs485Protocol) {
            this.rs2321Protocol = rs2321Protocol == null ? "JT808" : rs2321Protocol;
            this.rs2322Protocol = rs2322Protocol == null ? "AL808" : rs2322Protocol;
            this.rs485Protocol = rs485Protocol == null ? "通达" : rs485Protocol;
        }

        public static SerialSettings defaults() {
            return new SerialSettings("JT808", "AL808", "通达");
        }

        public String getRs2321Protocol() { return rs2321Protocol; }
        public String getRs2322Protocol() { return rs2322Protocol; }
        public String getRs485Protocol() { return rs485Protocol; }
    }

    public static final class TtsSettings {
        private final boolean enabled;
        private final int innerVolume;
        private final int outerVolume;

        public TtsSettings(boolean enabled, int innerVolume, int outerVolume) {
            this.enabled = enabled;
            this.innerVolume = innerVolume;
            this.outerVolume = outerVolume;
        }

        public static TtsSettings defaults() {
            return new TtsSettings(true, 8, 8);
        }

        public boolean isEnabled() { return enabled; }
        public int getInnerVolume() { return innerVolume; }
        public int getOuterVolume() { return outerVolume; }
    }

    public static final class LanguageSettings {
        private final String languageCode;

        public LanguageSettings(String languageCode) {
            this.languageCode = languageCode == null || languageCode.trim().isEmpty() ? "auto" : languageCode.trim();
        }

        public static LanguageSettings defaults() {
            return new LanguageSettings("auto");
        }

        public String getLanguageCode() { return languageCode; }
    }

    public static final class OtherSettings {
        private final int shoutingVolume;
        private final int dispatchVolume;

        public OtherSettings(int shoutingVolume, int dispatchVolume) {
            this.shoutingVolume = shoutingVolume;
            this.dispatchVolume = dispatchVolume;
        }

        public static OtherSettings defaults() {
            return new OtherSettings(50, 7);
        }

        public int getShoutingVolume() { return shoutingVolume; }
        public int getDispatchVolume() { return dispatchVolume; }
    }

    public static final class WirelessSettings {
        private final boolean systemEntryEnabled;

        public WirelessSettings(boolean systemEntryEnabled) {
            this.systemEntryEnabled = systemEntryEnabled;
        }

        public static WirelessSettings defaults() {
            return new WirelessSettings(true);
        }

        public boolean isSystemEntryEnabled() { return systemEntryEnabled; }
    }
}
