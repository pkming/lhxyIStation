package com.lhxy.istationdevice.android11.app.setup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.lhxy.istationdevice.android11.app.AppLanguageManager;
import com.lhxy.istationdevice.android11.app.R;
import com.lhxy.istationdevice.android11.app.station.LegacyStationResourceStateRepository;
import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.Hexs;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.core.TraceIds;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigRepository;
import com.lhxy.istationdevice.android11.deviceapi.SerialMode;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortConfig;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 旧版系统设置右侧内容区宿主。
 * <p>
 * 对照旧项目时保留页面行为和保存语义，不继承旧实现里分散的状态保存方式。
 * 持久化和设备动作统一走新壳配置与运行时。
 */
public final class LegacyBasicSetupSectionFragment extends Fragment {
    private static final String ARG_LAYOUT = "layout";
    private static final String ARG_SECTION = "section";

    private enum Section {
        NEWSPAPER,
        NETWORK,
        SERIAL_PORT,
        TTS,
        LANGUAGE,
        OTHER,
        WIRELESS
    }

    public static LegacyBasicSetupSectionFragment newInstance(@LayoutRes int layoutRes, String sectionName) {
        LegacyBasicSetupSectionFragment fragment = new LegacyBasicSetupSectionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT, layoutRes);
        args.putString(ARG_SECTION, sectionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutRes = requireArguments().getInt(ARG_LAYOUT, 0);
        return inflater.inflate(layoutRes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switch (requireSection()) {
            case NEWSPAPER:
                bindNewspaper(view);
                break;
            case NETWORK:
                bindNetwork(view);
                break;
            case SERIAL_PORT:
                bindSerialPort(view);
                break;
            case TTS:
                bindTts(view);
                break;
            case LANGUAGE:
                bindLanguage(view);
                break;
            case OTHER:
                bindOther(view);
                break;
            case WIRELESS:
                bindWireless(view);
                break;
        }
    }

    private void bindNewspaper(View view) {
        ShellConfig.NewspaperSettings settings = requireConfig().getBasicSetupConfig().getNewspaperSettings();
        setSeek(view, R.id.sbInnerVolume, settings.getInnerVolume());
        setSeek(view, R.id.sbOutsideVolume, settings.getOuterVolume());
        checkRadio(view, R.id.rgLineProperty, mapLinePropertyToId(settings.getLineProperty()));
        setSwitch(view, R.id.sAngle, settings.isAngleEnabled());
        setSwitch(view, R.id.sBroadcastDialect, settings.isDialectEnabled());
        setSwitch(view, R.id.sBroadcastEnglish, settings.isEnglishEnabled());
        setSwitch(view, R.id.sExternalSound, settings.isExternalSoundEnabled());
        setSwitch(view, R.id.sNowTime, settings.isNowTimeEnabled());
        setSwitch(view, R.id.sOpenSpeeding, settings.isSpeedingWarningEnabled());
        Button save = view.findViewById(R.id.butAffirm);
        if (save != null) {
            save.setOnClickListener(v -> saveNewspaperConfig(view));
        }
    }

    private void bindNetwork(View view) {
        ShellConfig config = ShellRuntime.get().getActiveConfig();
        List<ShellConfig.SocketChannel> channels = config == null
                ? new ArrayList<>()
                : new ArrayList<>(config.getSocketChannels().values());

        ShellConfig.NetworkSettings settings = requireConfig().getBasicSetupConfig().getNetworkSettings();
        bindText(view, R.id.etDispatchID, settings.getDispatchId());
        bindText(view, R.id.etLongInterval, String.valueOf(settings.getLongInterval()));
        bindText(view, R.id.etInfoInterval, String.valueOf(settings.getInfoInterval()));
        bindText(view, R.id.etSpeedingInterval, String.valueOf(settings.getSpeedingInterval()));
        setSwitch(view, R.id.sAdwordsSwitch, settings.isAdwordsEnabled());
        bindText(view, R.id.etAdwordsID, settings.getAdwordsId());
        bindText(view, R.id.etAdwordsUser, settings.getAdwordsUser());
        bindText(view, R.id.etAdwordsInterval, String.valueOf(settings.getAdwordsInterval()));

        // 旧页面只有一套网络表单，这里继续复用旧交互，但底层映射到新壳 Socket 配置。
        if (!channels.isEmpty()) {
            ShellConfig.SocketChannel first = channels.get(0);
            bindText(view, R.id.etDispatchIP, first.getHost());
            bindText(view, R.id.etDispatchPort, String.valueOf(first.getPort()));
        }
        if (channels.size() > 1) {
            ShellConfig.SocketChannel second = channels.get(1);
            bindText(view, R.id.etAdwordsIP, second.getHost());
            bindText(view, R.id.etAdwordsPort, String.valueOf(second.getPort()));
        } else if (!channels.isEmpty()) {
            ShellConfig.SocketChannel first = channels.get(0);
            bindText(view, R.id.etAdwordsIP, first.getHost());
            bindText(view, R.id.etAdwordsPort, String.valueOf(first.getPort()));
        }

        bindSpinner(view, R.id.spDispatch, extractSocketKeys(channels), channels.isEmpty() ? null : channels.get(0).getKey());
        Button save = view.findViewById(R.id.butNetWorkAffirm);
        if (save != null) {
            save.setOnClickListener(v -> saveNetworkConfig(view));
        }
    }

    private void bindSerialPort(View view) {
        ShellConfig config = ShellRuntime.get().getActiveConfig();
        Map<String, ShellConfig.SerialChannel> channels = config == null ? null : config.getSerialChannels();
        List<String> baudOptions = Arrays.asList("9600", "19200", "38400", "51200", "57600", "115200");
        List<String> gpsBaudOptions = Arrays.asList("9600", "115200");
        List<String> protocol232Options = Arrays.asList("无", "DVR", "POS");
        List<String> protocol485Options = Arrays.asList("无", "通达", "恒舞", "武汉乐的", "海梁", "LED导程牌", "LHXY", "HW3", "JHY");
        List<String> dvrChannelOptions = Arrays.asList("VIN1-AHD", "VIN4-AUTO");
        List<String> portOptions = Arrays.asList("RS232-1", "RS232-2", "RS485");

        ShellConfig.SerialChannel serial2321 = channels == null ? null : channels.get("rs232_1");
        ShellConfig.SerialChannel serial2322 = channels == null ? null : channels.get("rs232_2");
        ShellConfig.SerialChannel serial485 = channels == null ? null : channels.get("rs485_1");
        ShellConfig.SerialChannel serial4852 = channels == null ? null : channels.get("rs485_2");
        ShellConfig.SerialChannel gpsSerial = channels == null || config == null ? null : channels.get(config.getDebugReplay().getGpsSerialKey());

        bindSpinner(view, R.id.spPortBaud2321, baudOptions, serial2321 == null ? "9600" : String.valueOf(serial2321.getBaudRate()));
        bindSpinner(view, R.id.spPortBaud2322, baudOptions, serial2322 == null ? "9600" : String.valueOf(serial2322.getBaudRate()));
        bindSpinner(view, R.id.spPortBaud485, baudOptions, serial485 == null ? "9600" : String.valueOf(serial485.getBaudRate()));
        bindSpinner(view, R.id.spPortBaud4852, baudOptions, serial4852 == null ? "9600" : String.valueOf(serial4852.getBaudRate()));
        bindSpinner(view, R.id.gpsChannel, gpsBaudOptions, gpsSerial == null ? "115200" : String.valueOf(gpsSerial.getBaudRate()));
        ShellConfig.SerialSettings settings = requireConfig().getBasicSetupConfig().getSerialSettings();
        bindSpinner(view, R.id.spPortProtocol2321, protocol232Options, settings.getRs2321Protocol());
        bindSpinner(view, R.id.spPortProtocol2322, protocol232Options, settings.getRs2322Protocol());
        bindSpinner(view, R.id.spPortProtocol485, protocol485Options, settings.getRs485Protocol());
        bindSpinner(view, R.id.spPortProtocol4852, protocol485Options, settings.getRs4852Protocol());
        bindSpinner(view, R.id.spChannel, dvrChannelOptions, mapDvrCameraKeyToOption(requireConfig().getDebugReplay().getCameraChannelKey()));
        bindSpinner(view, R.id.spPortProtocol, protocol485Options, "无");
        bindSpinner(view, R.id.spPortNumber1, portOptions, "RS485");
        checkRadio(view, R.id.rgDataType, R.id.rbTxtType);
        bindText(view, R.id.etPortData, "TEST-485");

        View send = view.findViewById(R.id.butSend);
        if (send != null) {
            send.setOnClickListener(v -> sendSerialTest(view));
        }
        Button save = view.findViewById(R.id.butPortAffirm);
        if (save != null) {
            save.setOnClickListener(v -> saveSerialConfig(view));
        }
    }

    private void bindTts(View view) {
        ShellConfig.TtsSettings settings = requireConfig().getBasicSetupConfig().getTtsSettings();
        setSwitch(view, R.id.sTTS, settings.isEnabled());
        setSeek(view, R.id.sbTTSInnerVolume, settings.getInnerVolume());
        setSeek(view, R.id.sbTTSOutsideVolume, settings.getOuterVolume());
        Button save = view.findViewById(R.id.butAffirm);
        if (save != null) {
            save.setOnClickListener(v -> saveTtsConfig(view));
        }
    }

    private void bindLanguage(View view) {
        checkRadio(view, R.id.rg_radio_language, mapLanguageToId(requireConfig().getBasicSetupConfig().getLanguageSettings().getLanguageCode()));
        Button save = view.findViewById(R.id.butAffirm);
        if (save != null) {
            save.setOnClickListener(v -> saveLanguageConfig(view));
        }
    }

    private void bindOther(View view) {
        ShellConfig.OtherSettings settings = requireConfig().getBasicSetupConfig().getOtherSettings();
        setSeek(view, R.id.sbShoutingVolume, settings.getShoutingVolume());
        setSeek(view, R.id.sbDispatchVolume, settings.getDispatchVolume());
        Button save = view.findViewById(R.id.butAffirm);
        if (save != null) {
            save.setOnClickListener(v -> saveOtherConfig(view));
        }
    }

    private void bindWireless(View view) {
        View gotoSystem = view.findViewById(R.id.rlGotoSystemUp);
        if (gotoSystem != null) {
            gotoSystem.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                } catch (Exception e) {
                    toast("打开系统设置失败: " + safeMessage(e));
                }
            });
        }
    }

    private void bindConfirm(View root, int buttonId, String message) {
        Button button = root.findViewById(buttonId);
        if (button == null) {
            return;
        }
        button.setOnClickListener(v -> {
            AppLogCenter.log(
                    LogCategory.UI,
                    LogLevel.INFO,
                    "LegacyBasicSetupSection",
                    message,
                    TraceIds.next("legacy-basic-affirm")
            );
            toast(message);
        });
    }

    private void bindSpinner(View root, int spinnerId, List<String> values, @Nullable String selectedValue) {
        Spinner spinner = root.findViewById(spinnerId);
        if (spinner == null || getContext() == null) {
            return;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, values);
        adapter.setDropDownViewResource(R.layout.dropdown_stytle);
        spinner.setAdapter(adapter);
        if (selectedValue == null) {
            return;
        }
        int index = values.indexOf(selectedValue);
        if (index >= 0) {
            spinner.setSelection(index);
        }
    }

    private void bindText(View root, int editTextId, String value) {
        TextView textView = root.findViewById(editTextId);
        if (textView == null) {
            return;
        }
        textView.setText(value == null ? "" : value);
    }

    private void setSeek(View root, int seekBarId, int progress) {
        SeekBar seekBar = root.findViewById(seekBarId);
        if (seekBar != null) {
            seekBar.setProgress(progress);
        }
    }

    private void setSwitch(View root, int switchId, boolean checked) {
        Switch switchView = root.findViewById(switchId);
        if (switchView != null) {
            switchView.setChecked(checked);
        }
    }

    private void checkRadio(View root, int groupId, int checkedId) {
        RadioGroup radioGroup = root.findViewById(groupId);
        RadioButton radioButton = root.findViewById(checkedId);
        if (radioButton != null) {
            radioButton.setChecked(true);
        } else if (radioGroup != null) {
            radioGroup.check(checkedId);
        }
    }

    private List<String> extractSocketKeys(List<ShellConfig.SocketChannel> channels) {
        List<String> values = new ArrayList<>();
        for (ShellConfig.SocketChannel channel : channels) {
            values.add(channel.getKey());
        }
        if (values.isEmpty()) {
            values.add("jt808");
        }
        return values;
    }

    private Section requireSection() {
        String value = requireArguments().getString(ARG_SECTION, Section.NEWSPAPER.name());
        return Section.valueOf(value);
    }

    private void saveNetworkConfig(View root) {
        if (!requireStationResourceImported()) {
            return;
        }
        confirmAction(R.string.network_dialog_tip, R.string.restart, () -> {
            String traceId = TraceIds.next("legacy-basic-network-save");
            try {
                ShellConfig current = requireConfig();
                List<ShellConfig.SocketChannel> orderedChannels = new ArrayList<>(current.getSocketChannels().values());
                if (orderedChannels.isEmpty()) {
                    throw new IllegalStateException("当前没有可保存的 Socket 配置");
                }

                String selectedKey = readSpinnerValue(root, R.id.spDispatch, orderedChannels.get(0).getKey());
                Map<String, ShellConfig.SocketChannel> updatedChannels = new LinkedHashMap<>(current.getSocketChannels());

                ShellConfig.SocketChannel selectedChannel = current.requireSocketChannel(selectedKey);
                updatedChannels.put(selectedKey, new ShellConfig.SocketChannel(
                        selectedChannel.getKey(),
                        selectedChannel.getChannelName(),
                        readRequiredText(root, R.id.etDispatchIP, "调度 IP"),
                        parseNumber(root, R.id.etDispatchPort, "调度端口", selectedChannel.getPort()),
                        selectedChannel.getMode(),
                        selectedChannel.getNote()
                ));

                if (updatedChannels.containsKey("al808")) {
                    ShellConfig.SocketChannel second = current.requireSocketChannel("al808");
                    updatedChannels.put("al808", new ShellConfig.SocketChannel(
                            second.getKey(),
                            second.getChannelName(),
                            readOptionalText(root, R.id.etAdwordsIP, second.getHost()),
                            parseNumber(root, R.id.etAdwordsPort, "广告端口", second.getPort()),
                            second.getMode(),
                            second.getNote()
                    ));
                }

                // 旧项目这里会把 RS232-1 协议清成“无”，先保留这条互斥语义。
                ShellConfig.SerialSettings serialSettings = current.getBasicSetupConfig().getSerialSettings();
                ShellConfig updated = new ShellConfig(
                        current.getDeviceProfile(),
                        current.getConfigVersion(),
                        "runtime:" + ShellConfigRepository.getRuntimeConfigFile(requireContext()).getAbsolutePath(),
                        current.getSerialChannels(),
                        updatedChannels,
                        current.getGpioConfig(),
                        current.getCameraConfig(),
                        current.getRfidConfig(),
                        current.getSystemConfig(),
                        new ShellConfig.DebugReplay(
                                current.getDebugReplay().getDisplaySerialKey(),
                                current.getDebugReplay().getGpsSerialKey(),
                                selectedKey,
                                current.getDebugReplay().getAl808SocketKey(),
                                current.getDebugReplay().getGpioPinKey(),
                            current.getDebugReplay().getMonitorPrimaryGpioKey(),
                            current.getDebugReplay().getMonitorSecondaryGpioKey(),
                                current.getDebugReplay().getCameraChannelKey()
                        ),
                        new ShellConfig.BasicSetupConfig(
                                current.getBasicSetupConfig().getNewspaperSettings(),
                                new ShellConfig.NetworkSettings(
                                        readRequiredText(root, R.id.etDispatchID, "调度 ID"),
                                        parseNumber(root, R.id.etLongInterval, "心跳间隔", current.getBasicSetupConfig().getNetworkSettings().getLongInterval()),
                                        parseNumber(root, R.id.etInfoInterval, "信息间隔", current.getBasicSetupConfig().getNetworkSettings().getInfoInterval()),
                                    parseNumber(root, R.id.etSpeedingInterval, "超速间隔", current.getBasicSetupConfig().getNetworkSettings().getSpeedingInterval()),
                                        readSwitch(root, R.id.sAdwordsSwitch, current.getBasicSetupConfig().getNetworkSettings().isAdwordsEnabled()),
                                        readRequiredText(root, R.id.etAdwordsID, "广告 ID"),
                                        readRequiredText(root, R.id.etAdwordsUser, "广告用户"),
                                        parseNumber(root, R.id.etAdwordsInterval, "广告间隔", current.getBasicSetupConfig().getNetworkSettings().getAdwordsInterval())
                                ),
                                new ShellConfig.SerialSettings(
                                        "无",
                                        serialSettings.getRs2322Protocol(),
                                        serialSettings.getRs485Protocol()
                                ),
                                current.getBasicSetupConfig().getTtsSettings(),
                                current.getBasicSetupConfig().getLanguageSettings(),
                                current.getBasicSetupConfig().getOtherSettings(),
                                current.getBasicSetupConfig().getWirelessSettings(),
                                current.getBasicSetupConfig().getResourceImportSettings(),
                                new ShellConfig.ProtocolLinkageSettings(
                                    ShellConfig.ProtocolLinkageSettings.DISPATCH_OWNER_NETWORK,
                                    System.currentTimeMillis()
                                )
                        )
                );
                applyAndPersist(updated);
                AppLogCenter.log(LogCategory.UI, LogLevel.INFO, "LegacyBasicSetupSection", "网络配置已写入运行期文件。", traceId);
                        toast("网络配置已写入运行期文件，已切换为网络调度，正在请求重启。", true);
                requestReboot("legacy-basic-network-save", traceId);
            } catch (Exception e) {
                AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, "LegacyBasicSetupSection", "保存网络配置失败: " + e.getMessage(), traceId);
                toast("保存网络配置失败: " + safeMessage(e));
            }
        });
    }

    private void saveSerialConfig(View root) {
        if (!requireStationResourceImported()) {
            return;
        }
        confirmAction(R.string.port_ok_tip, R.string.restart, () -> {
            String traceId = TraceIds.next("legacy-basic-serial-save");
            try {
                ShellConfig current = requireConfig();
                Map<String, ShellConfig.SerialChannel> updatedChannels = new LinkedHashMap<>(current.getSerialChannels());

                updatedChannels.computeIfPresent("rs232_1", (key, channel) -> new ShellConfig.SerialChannel(
                        channel.getKey(),
                        channel.getPortName(),
                        parseSpinnerNumber(root, R.id.spPortBaud2321, channel.getBaudRate()),
                        channel.getMode() == null ? SerialMode.STUB : channel.getMode(),
                        channel.getNote()
                ));
                updatedChannels.computeIfPresent("rs232_2", (key, channel) -> new ShellConfig.SerialChannel(
                        channel.getKey(),
                        channel.getPortName(),
                        parseSpinnerNumber(root, R.id.spPortBaud2322, channel.getBaudRate()),
                        channel.getMode() == null ? SerialMode.STUB : channel.getMode(),
                        channel.getNote()
                ));
                updatedChannels.computeIfPresent("rs485_1", (key, channel) -> new ShellConfig.SerialChannel(
                        channel.getKey(),
                        channel.getPortName(),
                        parseSpinnerNumber(root, R.id.spPortBaud485, channel.getBaudRate()),
                        channel.getMode() == null ? SerialMode.STUB : channel.getMode(),
                        channel.getNote()
                ));
                updatedChannels.computeIfPresent("rs485_2", (key, channel) -> new ShellConfig.SerialChannel(
                    channel.getKey(),
                    channel.getPortName(),
                    parseSpinnerNumber(root, R.id.spPortBaud4852, channel.getBaudRate()),
                    channel.getMode() == null ? SerialMode.STUB : channel.getMode(),
                    channel.getNote()
                ));
                String gpsSerialKey = current.getDebugReplay().getGpsSerialKey();
                updatedChannels.computeIfPresent(gpsSerialKey, (key, channel) -> new ShellConfig.SerialChannel(
                    channel.getKey(),
                    channel.getPortName(),
                    parseSpinnerNumber(root, R.id.gpsChannel, channel.getBaudRate()),
                    channel.getMode() == null ? SerialMode.STUB : channel.getMode(),
                    channel.getNote()
                ));

                // 旧项目串口和网络协议是互斥关系，但新壳当前还没有“禁用网络协议”的正式建模。
                // 这里改成在 basicSetup 里显式记录“当前由串口还是网络负责调度”，不再直接破坏 Socket 运行时约束。
                String rs2321Protocol = readSpinnerValue(root, R.id.spPortProtocol2321, current.getBasicSetupConfig().getSerialSettings().getRs2321Protocol());
                String dispatchOwner = resolveDispatchOwnerAfterSerialSave(current, rs2321Protocol);
                String cameraChannelKey = mapDvrOptionToCameraKey(readSpinnerValue(root, R.id.spChannel, mapDvrCameraKeyToOption(current.getDebugReplay().getCameraChannelKey())));
                ShellConfig updated = new ShellConfig(
                        current.getDeviceProfile(),
                        current.getConfigVersion(),
                        "runtime:" + ShellConfigRepository.getRuntimeConfigFile(requireContext()).getAbsolutePath(),
                        updatedChannels,
                        current.getSocketChannels(),
                        current.getGpioConfig(),
                        current.getCameraConfig(),
                        current.getRfidConfig(),
                        current.getSystemConfig(),
                        new ShellConfig.DebugReplay(
                            current.getDebugReplay().getDisplaySerialKey(),
                            current.getDebugReplay().getGpsSerialKey(),
                            current.getDebugReplay().getJt808SocketKey(),
                            current.getDebugReplay().getAl808SocketKey(),
                            current.getDebugReplay().getGpioPinKey(),
                            current.getDebugReplay().getMonitorPrimaryGpioKey(),
                            current.getDebugReplay().getMonitorSecondaryGpioKey(),
                            cameraChannelKey
                        ),
                        new ShellConfig.BasicSetupConfig(
                                current.getBasicSetupConfig().getNewspaperSettings(),
                                current.getBasicSetupConfig().getNetworkSettings(),
                                new ShellConfig.SerialSettings(
                                        rs2321Protocol,
                                        readSpinnerValue(root, R.id.spPortProtocol2322, current.getBasicSetupConfig().getSerialSettings().getRs2322Protocol()),
                                readSpinnerValue(root, R.id.spPortProtocol485, current.getBasicSetupConfig().getSerialSettings().getRs485Protocol()),
                                readSpinnerValue(root, R.id.spPortProtocol4852, current.getBasicSetupConfig().getSerialSettings().getRs4852Protocol())
                                ),
                                current.getBasicSetupConfig().getTtsSettings(),
                                current.getBasicSetupConfig().getLanguageSettings(),
                                current.getBasicSetupConfig().getOtherSettings(),
                                current.getBasicSetupConfig().getWirelessSettings(),
                                current.getBasicSetupConfig().getResourceImportSettings(),
                                new ShellConfig.ProtocolLinkageSettings(dispatchOwner, System.currentTimeMillis())
                        )
                );
                applyAndPersist(updated);
                AppLogCenter.log(LogCategory.UI, LogLevel.INFO, "LegacyBasicSetupSection", "串口配置已写入运行期文件。", traceId);
                if (!"无".equals(rs2321Protocol)) {
                    toast("串口配置已写入运行期文件，已切换为串口调度。", true);
                } else {
                    toast("串口配置已写入运行期文件，已恢复网络调度，正在请求重启。", true);
                }
                requestReboot("legacy-basic-serial-save", traceId);
            } catch (Exception e) {
                AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, "LegacyBasicSetupSection", "保存串口配置失败: " + e.getMessage(), traceId);
                toast("保存串口配置失败: " + safeMessage(e));
            }
        });
    }

    /**
     * 把设置页新配置同时写入运行期文件和共享运行时。
     * <p>
     * 后续排查“设置是否立即生效”时，先看这里，再看请求重启的调用点。
     */
    private void applyAndPersist(ShellConfig updated) throws Exception {
        ShellConfigRepository.save(requireContext(), updated);
        ShellRuntime.get().applyConfig(requireContext(), updated);
    }

    private void sendSerialTest(View root) {
        String traceId = TraceIds.next("legacy-basic-serial-test");
        try {
            ShellConfig shellConfig = requireConfig();
            ShellConfig.SerialChannel channel = resolveSerialTestChannel(
                    shellConfig,
                    readSpinnerValue(root, R.id.spPortNumber1, "RS485")
            );
            int baudRate = resolveSerialTestBaud(root, channel);
            byte[] payload = readSerialTestPayload(root);
            SerialPortConfig serialPortConfig = new SerialPortConfig(
                    channel.getPortName(),
                    baudRate,
                    channel.getMode()
            );
            if (!ShellRuntime.get().getSerialPortAdapter().isOpen(channel.getPortName())) {
                ShellRuntime.get().getSerialPortAdapter().open(serialPortConfig, traceId);
            }
            ShellRuntime.get().getSerialPortAdapter().send(channel.getPortName(), payload, traceId);
            AppLogCenter.log(
                    LogCategory.UI,
                    LogLevel.INFO,
                    "LegacyBasicSetupSection",
                    "串口测试发送 -> " + channel.getKey()
                            + "/" + channel.getPortName()
                            + " @" + baudRate
                            + " [" + (isHexDataType(root) ? "HEX" : "TXT") + "] "
                            + Hexs.toHex(payload),
                    traceId
            );
            toast(
                    "已发送串口测试数据到 "
                            + mapSerialChoiceLabel(channel.getKey())
                            + "，字节数="
                            + payload.length
                            + "，模式="
                            + channel.getMode().toConfigValue(),
                    true
            );
        } catch (Exception e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.ERROR,
                    "LegacyBasicSetupSection",
                    "串口测试发送失败: " + e.getMessage(),
                    traceId
            );
            toast("串口测试发送失败: " + safeMessage(e));
        }
    }

    private ShellConfig requireConfig() {
        ShellConfig config = ShellRuntime.get().getActiveConfig();
        if (config == null) {
            throw new IllegalStateException("当前配置未初始化");
        }
        return config;
    }

    private void saveNewspaperConfig(View root) {
        if (!requireStationResourceImported()) {
            return;
        }
        confirmAction(R.string.newspaper_ok_tip, 0, () -> persistBasicSetup("legacy-basic-newspaper-save", buildUpdatedConfig(requireConfig(), new ShellConfig.BasicSetupConfig(
            new ShellConfig.NewspaperSettings(
                readSeek(root, R.id.sbInnerVolume, requireConfig().getBasicSetupConfig().getNewspaperSettings().getInnerVolume()),
                readSeek(root, R.id.sbOutsideVolume, requireConfig().getBasicSetupConfig().getNewspaperSettings().getOuterVolume()),
                readLineProperty(root),
                readSwitch(root, R.id.sAngle, requireConfig().getBasicSetupConfig().getNewspaperSettings().isAngleEnabled()),
                readSwitch(root, R.id.sBroadcastDialect, requireConfig().getBasicSetupConfig().getNewspaperSettings().isDialectEnabled()),
                readSwitch(root, R.id.sBroadcastEnglish, requireConfig().getBasicSetupConfig().getNewspaperSettings().isEnglishEnabled()),
                readSwitch(root, R.id.sExternalSound, requireConfig().getBasicSetupConfig().getNewspaperSettings().isExternalSoundEnabled()),
                readSwitch(root, R.id.sNowTime, requireConfig().getBasicSetupConfig().getNewspaperSettings().isNowTimeEnabled()),
                readSwitch(root, R.id.sOpenSpeeding, requireConfig().getBasicSetupConfig().getNewspaperSettings().isSpeedingWarningEnabled())
            ),
            requireConfig().getBasicSetupConfig().getNetworkSettings(),
            requireConfig().getBasicSetupConfig().getSerialSettings(),
            requireConfig().getBasicSetupConfig().getTtsSettings(),
            requireConfig().getBasicSetupConfig().getLanguageSettings(),
            requireConfig().getBasicSetupConfig().getOtherSettings(),
            requireConfig().getBasicSetupConfig().getWirelessSettings(),
            requireConfig().getBasicSetupConfig().getResourceImportSettings(),
            requireConfig().getBasicSetupConfig().getProtocolLinkageSettings()
        )), "报站设置已写入运行期文件。"));
    }

    private void saveTtsConfig(View root) {
        if (!requireStationResourceImported()) {
            return;
        }
        confirmAction(R.string.tts_ok_tip, 0, () -> persistBasicSetup("legacy-basic-tts-save", buildUpdatedConfig(requireConfig(), new ShellConfig.BasicSetupConfig(
            requireConfig().getBasicSetupConfig().getNewspaperSettings(),
            requireConfig().getBasicSetupConfig().getNetworkSettings(),
            requireConfig().getBasicSetupConfig().getSerialSettings(),
            new ShellConfig.TtsSettings(
                readSwitch(root, R.id.sTTS, requireConfig().getBasicSetupConfig().getTtsSettings().isEnabled()),
                readSeek(root, R.id.sbTTSInnerVolume, requireConfig().getBasicSetupConfig().getTtsSettings().getInnerVolume()),
                readSeek(root, R.id.sbTTSOutsideVolume, requireConfig().getBasicSetupConfig().getTtsSettings().getOuterVolume())
            ),
            requireConfig().getBasicSetupConfig().getLanguageSettings(),
            requireConfig().getBasicSetupConfig().getOtherSettings(),
            requireConfig().getBasicSetupConfig().getWirelessSettings(),
            requireConfig().getBasicSetupConfig().getResourceImportSettings(),
            requireConfig().getBasicSetupConfig().getProtocolLinkageSettings()
        )), "TTS 设置已写入运行期文件。"));
    }

    private void saveLanguageConfig(View root) {
        if (!requireStationResourceImported()) {
            return;
        }
        confirmAction(R.string.language_tip, R.string.restart, () -> {
            String traceId = TraceIds.next("legacy-basic-language-save");
            try {
                // 旧项目语言切换后立即重启；这里沿用语义，但动作走 systemOps。
            ShellConfig updated = buildUpdatedConfig(requireConfig(), new ShellConfig.BasicSetupConfig(
                requireConfig().getBasicSetupConfig().getNewspaperSettings(),
                requireConfig().getBasicSetupConfig().getNetworkSettings(),
                requireConfig().getBasicSetupConfig().getSerialSettings(),
                requireConfig().getBasicSetupConfig().getTtsSettings(),
                new ShellConfig.LanguageSettings(readLanguage(root)),
                requireConfig().getBasicSetupConfig().getOtherSettings(),
                requireConfig().getBasicSetupConfig().getWirelessSettings(),
                requireConfig().getBasicSetupConfig().getResourceImportSettings(),
                requireConfig().getBasicSetupConfig().getProtocolLinkageSettings()
            ));
            applyAndPersist(updated);
            AppLanguageManager.apply(updated.getBasicSetupConfig().getLanguageSettings().getLanguageCode());
            AppLogCenter.log(LogCategory.UI, LogLevel.INFO, "LegacyBasicSetupSection", "语言设置已写入运行期文件。", traceId);
            toast("语言设置已写入运行期文件，正在请求重启。", true);
            requestReboot("legacy-basic-language-save", traceId);
            } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, "LegacyBasicSetupSection", "保存语言设置失败: " + e.getMessage(), traceId);
            toast("保存语言设置失败: " + safeMessage(e));
            }
        });
    }

    private void saveOtherConfig(View root) {
        if (!requireStationResourceImported()) {
            return;
        }
        ShellConfig.OtherSettings currentSettings = requireConfig().getBasicSetupConfig().getOtherSettings();
        confirmAction(R.string.other_ok_tip, 0, () -> persistBasicSetup("legacy-basic-other-save", buildUpdatedConfig(requireConfig(), new ShellConfig.BasicSetupConfig(
            requireConfig().getBasicSetupConfig().getNewspaperSettings(),
            requireConfig().getBasicSetupConfig().getNetworkSettings(),
            requireConfig().getBasicSetupConfig().getSerialSettings(),
            requireConfig().getBasicSetupConfig().getTtsSettings(),
            requireConfig().getBasicSetupConfig().getLanguageSettings(),
            new ShellConfig.OtherSettings(
                readSeek(root, R.id.sbShoutingVolume, currentSettings.getShoutingVolume()),
                readSeek(root, R.id.sbDispatchVolume, currentSettings.getDispatchVolume()),
                currentSettings.getVehicleNumber(),
                currentSettings.getShoutingPrimaryGpioKey(),
                currentSettings.getShoutingSecondaryGpioKey()
            ),
            requireConfig().getBasicSetupConfig().getWirelessSettings(),
            requireConfig().getBasicSetupConfig().getResourceImportSettings(),
            requireConfig().getBasicSetupConfig().getProtocolLinkageSettings()
        )), "其他设置已写入运行期文件。"));
    }

        private void confirmAction(int messageResId, int positiveResId, Runnable action) {
        if (getContext() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
            .setMessage(messageResId)
            .setPositiveButton(positiveResId == 0 ? android.R.string.ok : positiveResId, (dialog, which) -> action.run())
            .setNegativeButton(android.R.string.cancel, null);
        builder.show();
        }

        private void requestReboot(String reason, String traceId) {
        ShellConfig.SystemConfig systemConfig = requireConfig().getSystemConfig();
        if (!canRequestSystemReboot(systemConfig)) {
            AppLogCenter.log(LogCategory.UI, LogLevel.WARN, "LegacyBasicSetupSection", "系统重启能力未开启，改为重启应用: " + reason, traceId);
            relaunchApplication(traceId);
            return;
        }
        try {
            ShellRuntime.get().getSystemOps().reboot(reason, traceId);
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, "LegacyBasicSetupSection", "请求重启失败，改为重启应用: " + e.getMessage(), traceId);
            relaunchApplication(traceId);
        }
        }

        private boolean canRequestSystemReboot(ShellConfig.SystemConfig systemConfig) {
        return systemConfig != null
            && systemConfig.isAllowReboot()
            && systemConfig.getRebootCommand() != null
            && !systemConfig.getRebootCommand().trim().isEmpty();
        }

        private void relaunchApplication(String traceId) {
        if (getContext() == null) {
            return;
        }
        Context appContext = requireContext().getApplicationContext();
        Intent launchIntent = new Intent(appContext, com.lhxy.istationdevice.android11.app.home.LegacyMainActivity.class);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent restartIntent = PendingIntent.getActivity(
            appContext,
            1001,
            launchIntent,
            PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        AlarmManager alarmManager = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 300L, restartIntent);
        } else {
            appContext.startActivity(launchIntent);
        }
        AppLogCenter.log(LogCategory.UI, LogLevel.INFO, "LegacyBasicSetupSection", "已安排应用重启", traceId);
        toast("配置已保存，正在重启应用。", true);
        if (getActivity() != null) {
            getActivity().finishAffinity();
        }
        Process.killProcess(Process.myPid());
        System.exit(0);
        }

        private boolean requireStationResourceImported() {
            if (getContext() == null) {
                return false;
            }
            if (LegacyStationResourceStateRepository.isImported(requireContext())) {
                return true;
            }
            toast(getString(R.string.legacy_station_resource_required));
            return false;
        }

        private String resolveDispatchOwnerAfterSerialSave(ShellConfig current, String rs2321Protocol) {
            if (!"无".equals(rs2321Protocol)) {
                return ShellConfig.ProtocolLinkageSettings.DISPATCH_OWNER_SERIAL_RS2321;
            }
            if (current.getBasicSetupConfig().getProtocolLinkageSettings().isSerialDispatchEnabled()) {
                return ShellConfig.ProtocolLinkageSettings.DISPATCH_OWNER_NETWORK;
            }
            return current.getBasicSetupConfig().getProtocolLinkageSettings().getDispatchOwner();
        }

    private void persistBasicSetup(String traceKey, ShellConfig updated, String successMessage) {
        String traceId = TraceIds.next(traceKey);
        try {
            applyAndPersist(updated);
            AppLogCenter.log(LogCategory.UI, LogLevel.INFO, "LegacyBasicSetupSection", successMessage, traceId);
            toast(successMessage);
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, "LegacyBasicSetupSection", "保存基础设置失败: " + e.getMessage(), traceId);
            toast("保存基础设置失败: " + safeMessage(e));
        }
    }

    private ShellConfig buildUpdatedConfig(ShellConfig current, ShellConfig.BasicSetupConfig basicSetupConfig) {
        return new ShellConfig(
                current.getDeviceProfile(),
                current.getConfigVersion(),
                "runtime:" + ShellConfigRepository.getRuntimeConfigFile(requireContext()).getAbsolutePath(),
                current.getSerialChannels(),
                current.getSocketChannels(),
                current.getGpioConfig(),
                current.getCameraConfig(),
                current.getRfidConfig(),
                current.getSystemConfig(),
                current.getDebugReplay(),
                basicSetupConfig
        );
    }

    private String readSpinnerValue(View root, int spinnerId, String defaultValue) {
        Spinner spinner = root.findViewById(spinnerId);
        if (spinner == null || spinner.getSelectedItem() == null) {
            return defaultValue;
        }
        return String.valueOf(spinner.getSelectedItem()).trim();
    }

    private int parseSpinnerNumber(View root, int spinnerId, int defaultValue) {
        try {
            return Integer.parseInt(readSpinnerValue(root, spinnerId, String.valueOf(defaultValue)));
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    private ShellConfig.SerialChannel resolveSerialTestChannel(ShellConfig config, String selectedPort) {
        String normalized = selectedPort == null ? "" : selectedPort.trim().toUpperCase();
        if (normalized.contains("232") && normalized.contains("1")) {
            return config.requireSerialChannel("rs232_1");
        }
        if (normalized.contains("232") && normalized.contains("2")) {
            return config.requireSerialChannel("rs232_2");
        }
        return config.requireSerialChannel("rs485_1");
    }

    private int resolveSerialTestBaud(View root, ShellConfig.SerialChannel channel) {
        if ("rs232_1".equals(channel.getKey())) {
            return parseSpinnerNumber(root, R.id.spPortBaud2321, channel.getBaudRate());
        }
        if ("rs232_2".equals(channel.getKey())) {
            return parseSpinnerNumber(root, R.id.spPortBaud2322, channel.getBaudRate());
        }
        return parseSpinnerNumber(root, R.id.spPortBaud485, channel.getBaudRate());
    }

    private byte[] readSerialTestPayload(View root) {
        String source = readRequiredText(root, R.id.etPortData, "发送数据");
        if (isHexDataType(root)) {
            byte[] payload = Hexs.fromHex(source);
            if (payload.length == 0) {
                throw new IllegalArgumentException("HEX 内容为空");
            }
            return payload;
        }
        return source.getBytes(StandardCharsets.UTF_8);
    }

    private boolean isHexDataType(View root) {
        RadioButton hexButton = root.findViewById(R.id.rbHexType);
        return hexButton != null && hexButton.isChecked();
    }

    private String mapSerialChoiceLabel(String channelKey) {
        if ("rs232_1".equals(channelKey)) {
            return "RS232-1";
        }
        if ("rs232_2".equals(channelKey)) {
            return "RS232-2";
        }
        return "RS485";
    }

    private String mapDvrCameraKeyToOption(String cameraChannelKey) {
        if ("monitor".equalsIgnoreCase(cameraChannelKey)) {
            return "VIN4-AUTO";
        }
        return "VIN1-AHD";
    }

    private String mapDvrOptionToCameraKey(String dvrOption) {
        if ("VIN4-AUTO".equalsIgnoreCase(dvrOption)) {
            return "monitor";
        }
        return "av_out";
    }

    private void showControlRow(@Nullable View target) {
        if (target == null) {
            return;
        }
        target.setVisibility(View.VISIBLE);
        if (target.getParent() instanceof View) {
            ((View) target.getParent()).setVisibility(View.VISIBLE);
        }
    }

    private int readSeek(View root, int seekBarId, int defaultValue) {
        SeekBar seekBar = root.findViewById(seekBarId);
        return seekBar == null ? defaultValue : seekBar.getProgress();
    }

    private boolean readSwitch(View root, int switchId, boolean defaultValue) {
        Switch switchView = root.findViewById(switchId);
        return switchView == null ? defaultValue : switchView.isChecked();
    }

    private String readLineProperty(View root) {
        RadioGroup radioGroup = root.findViewById(R.id.rgLineProperty);
        if (radioGroup == null) {
            return requireConfig().getBasicSetupConfig().getNewspaperSettings().getLineProperty();
        }
        int checkedId = radioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.rbLoopLine) {
            return "loop";
        }
        if (checkedId == R.id.rbAntiReverse) {
            return "anti_reverse";
        }
        return "up_down";
    }

    private int mapLinePropertyToId(String lineProperty) {
        if ("loop".equalsIgnoreCase(lineProperty)) {
            return R.id.rbLoopLine;
        }
        if ("anti_reverse".equalsIgnoreCase(lineProperty)) {
            return R.id.rbAntiReverse;
        }
        return R.id.rbUpAndDown;
    }

    private String readLanguage(View root) {
        RadioGroup group = root.findViewById(R.id.rg_radio_language);
        if (group == null) {
            return requireConfig().getBasicSetupConfig().getLanguageSettings().getLanguageCode();
        }
        int checkedId = group.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_radio_language_ch) {
            return "zh_cn";
        }
        if (checkedId == R.id.rb_radio_language_tr) {
            return "zh_tw";
        }
        if (checkedId == R.id.rb_radio_language_en) {
            return "en";
        }
        if (checkedId == R.id.rb_radio_language_ko) {
            return "ko";
        }
        if (checkedId == R.id.rb_radio_language_es) {
            return "es";
        }
        return "auto";
    }

    private int mapLanguageToId(String languageCode) {
        if ("zh_cn".equalsIgnoreCase(languageCode)) {
            return R.id.rb_radio_language_ch;
        }
        if ("zh_tw".equalsIgnoreCase(languageCode)) {
            return R.id.rb_radio_language_tr;
        }
        if ("en".equalsIgnoreCase(languageCode)) {
            return R.id.rb_radio_language_en;
        }
        if ("ko".equalsIgnoreCase(languageCode)) {
            return R.id.rb_radio_language_ko;
        }
        if ("es".equalsIgnoreCase(languageCode)) {
            return R.id.rb_radio_language_es;
        }
        return R.id.rb_radio_language_auto;
    }

    private int parseNumber(View root, int editTextId, String fieldName, int defaultValue) {
        String value = readOptionalText(root, editTextId, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(fieldName + "格式不正确");
        }
    }

    private String readRequiredText(View root, int editTextId, String fieldName) {
        String value = readOptionalText(root, editTextId, "");
        if (value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + "不能为空");
        }
        return value;
    }

    private String readOptionalText(View root, int editTextId, String defaultValue) {
        TextView textView = root.findViewById(editTextId);
        if (textView == null) {
            return defaultValue == null ? "" : defaultValue;
        }
        String value = textView.getText() == null ? "" : textView.getText().toString().trim();
        return value.isEmpty() ? (defaultValue == null ? "" : defaultValue) : value;
    }

    private String safeMessage(Exception e) {
        String message = e == null ? null : e.getMessage();
        return message == null || message.trim().isEmpty() ? "未知错误" : message.trim();
    }

    private void toast(String message) {
        toast(message, false);
    }

    private void toast(String message, boolean longDuration) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
        }
    }
}
