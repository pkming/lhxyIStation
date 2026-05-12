package com.lhxy.istationdevice.android11.domain.station;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.tts.TextToSpeech;

import com.lhxy.istationdevice.android11.deviceapi.GpioAdapter;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.file.StationResourceArchiveUseCase;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsAutoReportEngine;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsRouteResource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 旧版报站音频用例。
 * <p>
 * 负责手动报站、自动报站、提醒音、服务音、整点报时和调度语音的统一播放。
 * <p>
 * 查找关键字：报站音频、服务音、TTS、内外音、调度语音。
 */
public final class LegacyStationAudioUseCase {
    private static final int BROADCAST_TYPE_START = 0;
    private static final int BROADCAST_TYPE_ENTER = 1;
    private static final int BROADCAST_TYPE_LEAVE = 2;
    private static final int BROADCAST_TYPE_TERMINAL_PREVIEW = 3;
    private static final int BROADCAST_TYPE_TERMINAL_ENTER = 4;
    private static final int VOLUME_MODE_NEWSPAPER = 0;
    private static final int VOLUME_MODE_TTS = 1;
    private static final int VOLUME_MODE_DISPATCH = 2;
    private static final String LANGUAGE_MANDARIN = "voiceD";
    private static final String LANGUAGE_ENGLISH = "voiceE";
    private static final String LANGUAGE_DIALECT = "voiceF";
    private static final String MAJOR_STATION_FLAG = "大站";

    private static final Object GLOBAL_LOCK = new Object();
    private final GpioAdapter gpioAdapter;

    private static MediaPlayer mediaPlayer;
    private static TextToSpeech textToSpeech;
    private static boolean ttsReady;
    private static String pendingSpeechText;
    private static PlaybackPlan activePlan;

    public LegacyStationAudioUseCase(GpioAdapter gpioAdapter) {
        this.gpioAdapter = gpioAdapter;
    }

    /**
     * 手动报站播放入口。
     */
    public void playManualStation(
            Context context,
            ShellConfig shellConfig,
            LegacyGpsRouteResource route,
            LegacyGpsRouteResource.StationPoint station,
            int stationType
    ) {
        if (context == null || shellConfig == null || route == null || station == null) {
            return;
        }
        int broadcastType = resolveManualBroadcastType(route, station, stationType);
        if (broadcastType < 0) {
            return;
        }
        PlaybackPlan plan = createStationPlan(context, shellConfig, route, station, broadcastType);
        play(context, shellConfig, plan, shellConfig.getBasicSetupConfig().getTtsSettings().isEnabled());
    }

    /**
     * 自动报站播放入口。
     */
    public void playAutoStation(
            Context context,
            ShellConfig shellConfig,
            LegacyGpsRouteResource route,
            LegacyGpsRouteResource.StationPoint station,
            int stationType
    ) {
        if (context == null || shellConfig == null || route == null || station == null) {
            return;
        }
        if (stationType != LegacyGpsAutoReportEngine.STATION_TYPE_ENTER) {
            return;
        }
        if (station.getStationNo() <= 0) {
            return;
        }
        int broadcastType = station.getStationNo() >= route.getStations().size() - 1
                ? BROADCAST_TYPE_TERMINAL_ENTER
                : BROADCAST_TYPE_ENTER;
        PlaybackPlan plan = createStationPlan(context, shellConfig, route, station, broadcastType);
        play(context, shellConfig, plan, shellConfig.getBasicSetupConfig().getTtsSettings().isEnabled());
    }

    /**
     * 播放提醒点语音。
     */
    public void playReminder(
            Context context,
            ShellConfig shellConfig,
            LegacyGpsRouteResource route,
            LegacyGpsRouteResource.ReminderPoint reminder
    ) {
        if (context == null || shellConfig == null || route == null || reminder == null) {
            return;
        }
        PlaybackPlan plan = createReminderPlan(context, shellConfig, reminder);
        play(context, shellConfig, plan, false);
    }

    /**
     * 播放调度公告语音。
     */
    public void playDispatchNotice(Context context, ShellConfig shellConfig, String message) {
        if (context == null || shellConfig == null || message == null || message.trim().isEmpty() || "-".equals(message.trim())) {
            return;
        }
        Context appContext = context.getApplicationContext();
        synchronized (GLOBAL_LOCK) {
            stopLocked();
            disablePinsLocked();
            enablePinsLocked(shellConfig, true, false);
            applyAudioVolume(appContext, shellConfig, VOLUME_MODE_DISPATCH, false);
            PlaybackPlan plan = new PlaybackPlan();
            plan.shellConfig = shellConfig;
            activePlan = plan;
            speakLocked(appContext, message);
        }
    }

    /**
     * 播放超速提示音。
     */
    public void playSpeedWarning(Context context, ShellConfig shellConfig) {
        if (context == null || shellConfig == null) {
            return;
        }
        PlaybackPlan plan = createSpeedWarningPlan(context, shellConfig);
        if (plan == null || plan.innerPlaylist.isEmpty()) {
            return;
        }
        play(context, shellConfig, plan, false);
    }

    /**
     * 播放整点报时。
     */
    public void playNowTime(Context context, ShellConfig shellConfig, int hour) {
        if (context == null || shellConfig == null || hour < 0 || hour > 23) {
            return;
        }
        PlaybackPlan plan = createNowTimePlan(context, shellConfig, hour);
        if (plan == null || plan.innerPlaylist.isEmpty()) {
            return;
        }
        play(context, shellConfig, plan, false);
    }

    /**
     * 播放 0-9 服务音。
     * <p>
     * 0/9 走外音，其余走内音，行为按旧 M90 收口。
     */
    public boolean playServiceTone(Context context, ShellConfig shellConfig, int serviceNo) {
        if (context == null || shellConfig == null || serviceNo < 0 || serviceNo > 9) {
            return false;
        }
        PlaybackPlan plan = createServiceTonePlan(context, shellConfig, serviceNo);
        if (plan == null || (plan.innerPlaylist.isEmpty() && plan.outerPlaylist.isEmpty())) {
            return false;
        }
        play(context, shellConfig, plan, false);
        return true;
    }

    public boolean isBusy() {
        synchronized (GLOBAL_LOCK) {
            boolean mediaPlaying = mediaPlayer != null && mediaPlayer.isPlaying();
            boolean ttsSpeaking = textToSpeech != null && textToSpeech.isSpeaking();
            boolean hasPendingSpeech = pendingSpeechText != null && !pendingSpeechText.trim().isEmpty();
            return mediaPlaying || ttsSpeaking || hasPendingSpeech;
        }
    }

    public void stop() {
        synchronized (GLOBAL_LOCK) {
            stopLocked();
            disablePinsLocked();
            if (textToSpeech != null) {
                try {
                    textToSpeech.stop();
                } catch (Exception ignore) {
                    // Ignore player shutdown failures to keep station flow moving.
                }
            }
        }
    }

    /**
     * 统一决定本次播放是走内音、外音还是 TTS。
     */
    private void play(Context context, ShellConfig shellConfig, PlaybackPlan plan, boolean preferTts) {
        Context appContext = context.getApplicationContext();
        synchronized (GLOBAL_LOCK) {
            stopLocked();
            disablePinsLocked();
            if (plan == null) {
                return;
            }
            plan.shellConfig = shellConfig;
            activePlan = plan;
            if (!preferTts && !plan.innerPlaylist.isEmpty()) {
                enablePinsLocked(shellConfig, true, false);
                applyAudioVolume(appContext, shellConfig, plan.volumeMode, false);
                playLocked(appContext, shellConfig, plan, false, 0);
                return;
            }
            if (!preferTts && !plan.outerPlaylist.isEmpty()) {
                enablePinsLocked(shellConfig, false, true);
                applyAudioVolume(appContext, shellConfig, plan.volumeMode, true);
                playLocked(appContext, shellConfig, plan, true, 0);
                return;
            }
            boolean externalEnabled = shellConfig.getBasicSetupConfig().getNewspaperSettings().isExternalSoundEnabled();
            enablePinsLocked(shellConfig, true, externalEnabled);
            applyAudioVolume(appContext, shellConfig, plan.volumeMode == VOLUME_MODE_DISPATCH ? VOLUME_MODE_DISPATCH : VOLUME_MODE_TTS, externalEnabled);
            speakLocked(appContext, plan.ttsText);
        }
    }

    /**
     * 构造站点播报播放计划。
     */
    private PlaybackPlan createStationPlan(
            Context context,
            ShellConfig shellConfig,
            LegacyGpsRouteResource route,
            LegacyGpsRouteResource.StationPoint station,
            int broadcastType
    ) {
        PlaybackPlan plan = new PlaybackPlan();
        plan.volumeMode = VOLUME_MODE_NEWSPAPER;
        File sourceRoot = new StationResourceArchiveUseCase().resolveManagedSourceRoot(context.getApplicationContext());
        File commonSoundsRoot = new File(sourceRoot, "CommonSounds");
        File lineVoiceRoot = new File(new File(sourceRoot, "Bus"), route.getLineName());
        LegacyGpsRouteResource.StationPoint firstStation = route.getStations().isEmpty() ? null : route.getStations().get(0);
        LegacyGpsRouteResource.StationPoint terminalStation = route.lastStation();

        for (String languageFolder : resolveLanguageFolders(shellConfig)) {
            switch (broadcastType) {
                case BROADCAST_TYPE_START:
                    addCommonVoiceIfExists(plan.innerPlaylist, commonSoundsRoot, languageFolder, "起点音1", "Start1");
                    addNamedVoiceIfExists(plan.innerPlaylist, lineVoiceRoot, "", languageFolder, route.getLineName());
                    addCommonVoiceIfExists(plan.innerPlaylist, commonSoundsRoot, languageFolder, "起点音2", "Start2");
                    addStationVoiceIfExists(plan.innerPlaylist, lineVoiceRoot, languageFolder, firstStation);
                    addCommonVoiceIfExists(plan.innerPlaylist, commonSoundsRoot, languageFolder, "起点音3", "Start3");
                    addStationVoiceIfExists(plan.innerPlaylist, lineVoiceRoot, languageFolder, terminalStation);
                    addCommonVoiceIfExists(plan.innerPlaylist, commonSoundsRoot, languageFolder, "起点音4", "Start4");
                    addMajorStationsIfExists(plan.innerPlaylist, route, lineVoiceRoot, languageFolder);
                    addCommonVoiceIfExists(plan.innerPlaylist, commonSoundsRoot, languageFolder, "起点音5", "Start5");
                    addCommonVoiceIfExists(plan.innerPlaylist, commonSoundsRoot, languageFolder, "起点音6", "Start6");
                    addStationVoiceIfExists(plan.innerPlaylist, lineVoiceRoot, languageFolder, station);
                    break;
                case BROADCAST_TYPE_ENTER:
                    addCommonVoiceIfExists(plan.innerPlaylist, commonSoundsRoot, languageFolder, "进站音1", "Pitted1");
                    addNamedVoiceIfExists(plan.innerPlaylist, commonSoundsRoot, "Adv", languageFolder, station.getStationAdvert());
                    addStationVoiceIfExists(plan.innerPlaylist, lineVoiceRoot, languageFolder, station);
                    addCommonVoiceIfExists(plan.innerPlaylist, commonSoundsRoot, languageFolder, "进站音2", "Pitted2");
                    addCommonVoiceIfExists(plan.innerPlaylist, commonSoundsRoot, languageFolder, "进站音3", "Pitted3");
                    addNamedVoiceIfExists(plan.innerPlaylist, commonSoundsRoot, "Other", languageFolder, station.getStationPrompt());
                    if (shellConfig.getBasicSetupConfig().getNewspaperSettings().isExternalSoundEnabled()) {
                        addCommonVoiceIfExists(plan.outerPlaylist, commonSoundsRoot, languageFolder, "外音1", "External1");
                        addNamedVoiceIfExists(plan.outerPlaylist, lineVoiceRoot, "", languageFolder, route.getLineName());
                        addCommonVoiceIfExists(plan.outerPlaylist, commonSoundsRoot, languageFolder, "外音2", "External2");
                        addCommonVoiceIfExists(plan.outerPlaylist, commonSoundsRoot, languageFolder, "外音3", "External3");
                        addStationVoiceIfExists(plan.outerPlaylist, lineVoiceRoot, languageFolder, terminalStation);
                        addCommonVoiceIfExists(plan.outerPlaylist, commonSoundsRoot, languageFolder, "外音4", "External4");
                    }
                    break;
                case BROADCAST_TYPE_LEAVE:
                    addNamedVoiceIfExists(plan.innerPlaylist, commonSoundsRoot, "Adv", languageFolder, station.getDepartureAdvert());
                    addCommonVoiceIfExists(plan.innerPlaylist, commonSoundsRoot, languageFolder, "出站音1", "Out1");
                    addStationVoiceIfExists(plan.innerPlaylist, lineVoiceRoot, languageFolder, station);
                    addNamedVoiceIfExists(plan.innerPlaylist, commonSoundsRoot, "Other", languageFolder, station.getDeparturePrompt());
                    addCommonVoiceIfExists(plan.innerPlaylist, commonSoundsRoot, languageFolder, "出站音2", "Out2");
                    addNamedVoiceIfExists(plan.innerPlaylist, commonSoundsRoot, "Extend", languageFolder, station.getDepartureExpansion());
                    break;
                case BROADCAST_TYPE_TERMINAL_PREVIEW:
                    addCommonVoiceIfExists(plan.innerPlaylist, commonSoundsRoot, languageFolder, "终点音1", "End1");
                    addCommonVoiceIfExists(plan.innerPlaylist, commonSoundsRoot, languageFolder, "终点音2", "End2");
                    addStationVoiceIfExists(plan.innerPlaylist, lineVoiceRoot, languageFolder, station);
                    addCommonVoiceIfExists(plan.innerPlaylist, commonSoundsRoot, languageFolder, "终点音3", "End3");
                    break;
                case BROADCAST_TYPE_TERMINAL_ENTER:
                    addCommonVoiceIfExists(plan.innerPlaylist, commonSoundsRoot, languageFolder, "终点音2", "End2");
                    addStationVoiceIfExists(plan.innerPlaylist, lineVoiceRoot, languageFolder, station);
                    addCommonVoiceIfExists(plan.innerPlaylist, commonSoundsRoot, languageFolder, "终点音4", "End4");
                    break;
                default:
                    break;
            }
        }
        plan.ttsText = buildStationTtsText(route, station, broadcastType);
        return plan;
    }

    private PlaybackPlan createReminderPlan(
            Context context,
            ShellConfig shellConfig,
            LegacyGpsRouteResource.ReminderPoint reminder
    ) {
        PlaybackPlan plan = new PlaybackPlan();
        plan.volumeMode = VOLUME_MODE_NEWSPAPER;
        File sourceRoot = new StationResourceArchiveUseCase().resolveManagedSourceRoot(context.getApplicationContext());
        File commonSoundsRoot = new File(sourceRoot, "CommonSounds");
        for (String languageFolder : resolveLanguageFolders(shellConfig)) {
            addNamedVoiceIfExists(plan.innerPlaylist, commonSoundsRoot, "Common", languageFolder, reminder.getReminderName());
        }
        plan.ttsText = reminder.getReminderName();
        return plan;
    }

    private PlaybackPlan createSpeedWarningPlan(Context context, ShellConfig shellConfig) {
        PlaybackPlan plan = new PlaybackPlan();
        plan.volumeMode = VOLUME_MODE_NEWSPAPER;
        File alertTone = new File("/system/media/audio/notifications/Altair.ogg");
        addIfExists(plan.innerPlaylist, alertTone);
        File sourceRoot = new StationResourceArchiveUseCase().resolveManagedSourceRoot(context.getApplicationContext());
        File commonSoundsRoot = new File(sourceRoot, "CommonSounds");
        addIfExists(plan.innerPlaylist, new File(commonSoundsRoot, "Common/" + LANGUAGE_MANDARIN + "/超速语音.mp3"));
        plan.ttsText = "请注意控制车速";
        return plan;
    }

    private PlaybackPlan createNowTimePlan(Context context, ShellConfig shellConfig, int hour) {
        PlaybackPlan plan = new PlaybackPlan();
        plan.volumeMode = VOLUME_MODE_NEWSPAPER;
        File sourceRoot = new StationResourceArchiveUseCase().resolveManagedSourceRoot(context.getApplicationContext());
        File commonSoundsRoot = new File(sourceRoot, "CommonSounds");
        for (String languageFolder : resolveLanguageFolders(shellConfig)) {
            addIfExists(plan.innerPlaylist, new File(commonSoundsRoot, "Clock/" + languageFolder + "/" + hour + ".mp3"));
        }
        plan.ttsText = hour + "点整";
        return plan;
    }

    /**
     * 构造服务音播放计划。
     */
    private PlaybackPlan createServiceTonePlan(Context context, ShellConfig shellConfig, int serviceNo) {
        PlaybackPlan plan = new PlaybackPlan();
        plan.volumeMode = VOLUME_MODE_NEWSPAPER;
        File sourceRoot = new StationResourceArchiveUseCase().resolveManagedSourceRoot(context.getApplicationContext());
        File commonSoundsRoot = new File(sourceRoot, "CommonSounds");
        List<File> targetPlaylist = (serviceNo == 0 || serviceNo == 9) ? plan.outerPlaylist : plan.innerPlaylist;
        for (String languageFolder : resolveLanguageFolders(shellConfig)) {
            if (LANGUAGE_ENGLISH.equals(languageFolder)) {
                addIfExists(targetPlaylist, new File(commonSoundsRoot, "Common/" + languageFolder + "/Service" + serviceNo + ".mp3"));
            } else {
                addIfExists(targetPlaylist, new File(commonSoundsRoot, "Common/" + languageFolder + "/服务音" + serviceNo + ".mp3"));
            }
        }
        plan.ttsText = "服务音" + serviceNo;
        return plan;
    }

    private List<String> resolveLanguageFolders(ShellConfig shellConfig) {
        List<String> folders = new ArrayList<>();
        folders.add(LANGUAGE_MANDARIN);
        if (shellConfig.getBasicSetupConfig().getNewspaperSettings().isEnglishEnabled()) {
            folders.add(LANGUAGE_ENGLISH);
        }
        if (shellConfig.getBasicSetupConfig().getNewspaperSettings().isDialectEnabled()) {
            folders.add(LANGUAGE_DIALECT);
        }
        return folders;
    }

    private void addCommonVoiceIfExists(List<File> playlist, File commonSoundsRoot, String languageFolder, String chineseName, String englishAlias) {
        String fileName = LANGUAGE_ENGLISH.equals(languageFolder) ? englishAlias : chineseName;
        if (fileName == null || fileName.trim().isEmpty()) {
            return;
        }
        addIfExists(playlist, new File(commonSoundsRoot, "Common/" + languageFolder + "/" + fileName + ".mp3"));
    }

    private void addStationVoiceIfExists(
            List<File> playlist,
            File lineVoiceRoot,
            String languageFolder,
            LegacyGpsRouteResource.StationPoint station
    ) {
        if (station == null || station.getStationSound() == null || station.getStationSound().trim().isEmpty()) {
            return;
        }
        addIfExists(playlist, new File(lineVoiceRoot, languageFolder + "/" + station.getStationSound().trim() + ".mp3"));
    }

    private void addMajorStationsIfExists(
            List<File> playlist,
            LegacyGpsRouteResource route,
            File lineVoiceRoot,
            String languageFolder
    ) {
        if (route == null || route.getStations().isEmpty()) {
            return;
        }
        for (LegacyGpsRouteResource.StationPoint point : route.getStations()) {
            if (point == null || !MAJOR_STATION_FLAG.equals(point.getMajorStation())) {
                continue;
            }
            addStationVoiceIfExists(playlist, lineVoiceRoot, languageFolder, point);
        }
    }

    private void addNamedVoiceIfExists(List<File> playlist, File root, String category, String languageFolder, String voiceName) {
        if (voiceName == null || voiceName.trim().isEmpty() || "-".equals(voiceName.trim())) {
            return;
        }
        String basePath = category == null || category.isEmpty()
                ? languageFolder + "/" + voiceName.trim() + ".mp3"
                : category + "/" + languageFolder + "/" + voiceName.trim() + ".mp3";
        addIfExists(playlist, new File(root, basePath));
    }

    private void addIfExists(List<File> playlist, File file) {
        if (file != null && file.isFile()) {
            playlist.add(file);
        }
    }

    private String buildStationTtsText(LegacyGpsRouteResource route, LegacyGpsRouteResource.StationPoint station, int broadcastType) {
        switch (broadcastType) {
            case BROADCAST_TYPE_START:
                return "乘客朋友们，欢迎您乘坐" + route.getLineName() + "路公共汽车，下一站是" + station.getStationName();
            case BROADCAST_TYPE_LEAVE:
                return "刚上车的乘客，请您往里走，请坐好站稳，下一站是" + station.getStationName() + "，需要下车的乘客，请做好下车准备";
            case BROADCAST_TYPE_TERMINAL_PREVIEW:
                return "下一站是终点站，" + station.getStationName() + "，请您携带好自己行李物品，准备下车";
            case BROADCAST_TYPE_TERMINAL_ENTER:
                return "终点站" + station.getStationName() + "到了，请您携带好自己行李物品，准备下车";
            case BROADCAST_TYPE_ENTER:
            default:
                return station.getStationName() + "到了,需要下车的乘客,请从后门下车";
        }
    }

    private int resolveManualBroadcastType(
            LegacyGpsRouteResource route,
            LegacyGpsRouteResource.StationPoint station,
            int stationType
    ) {
        if (station == null || route == null) {
            return -1;
        }
        boolean terminal = station.getStationNo() >= route.getStations().size() - 1;
        if (stationType == LegacyGpsAutoReportEngine.STATION_TYPE_LEAVE) {
            if (station.getStationNo() == 1) {
                return BROADCAST_TYPE_START;
            }
            return terminal ? BROADCAST_TYPE_TERMINAL_PREVIEW : BROADCAST_TYPE_LEAVE;
        }
        if (station.getStationNo() <= 0) {
            return -1;
        }
        return terminal ? BROADCAST_TYPE_TERMINAL_ENTER : BROADCAST_TYPE_ENTER;
    }

    private void playLocked(Context context, ShellConfig shellConfig, PlaybackPlan plan, boolean outerPhase, int index) {
        List<File> playlist = outerPhase ? plan.outerPlaylist : plan.innerPlaylist;
        if (index < 0 || index >= playlist.size()) {
            if (!outerPhase && !plan.outerPlaylist.isEmpty()) {
                enablePinsLocked(shellConfig, false, true);
                applyAudioVolume(context, shellConfig, plan.volumeMode, true);
                playLocked(context, shellConfig, plan, true, 0);
                return;
            }
            stopLocked();
            disablePinsLocked();
            return;
        }
        File target = playlist.get(index);
        MediaPlayer player = MediaPlayer.create(context, Uri.fromFile(target));
        if (player == null) {
            playLocked(context, shellConfig, plan, outerPhase, index + 1);
            return;
        }
        mediaPlayer = player;
        player.setOnCompletionListener(completed -> {
            synchronized (GLOBAL_LOCK) {
                try {
                    completed.release();
                } catch (Exception ignore) {
                    // Ignore release failures.
                }
                if (mediaPlayer == completed) {
                    mediaPlayer = null;
                }
                playLocked(context, shellConfig, plan, outerPhase, index + 1);
            }
        });
        player.setOnErrorListener((failed, what, extra) -> {
            synchronized (GLOBAL_LOCK) {
                try {
                    failed.release();
                } catch (Exception ignore) {
                    // Ignore release failures.
                }
                if (mediaPlayer == failed) {
                    mediaPlayer = null;
                }
                playLocked(context, shellConfig, plan, outerPhase, index + 1);
            }
            return true;
        });
        player.start();
    }

    private void speakLocked(Context context, String text) {
        String normalized = text == null ? "" : text.trim();
        if (normalized.isEmpty() || "-".equals(normalized)) {
            return;
        }
        ensureTextToSpeechLocked(context);
        if (!ttsReady || textToSpeech == null) {
            pendingSpeechText = normalized;
            return;
        }
        pendingSpeechText = null;
        textToSpeech.speak(normalized, TextToSpeech.QUEUE_FLUSH, null, "station-audio");
    }

    private void ensureTextToSpeechLocked(Context context) {
        if (textToSpeech != null) {
            return;
        }
        textToSpeech = new TextToSpeech(context.getApplicationContext(), status -> {
            synchronized (GLOBAL_LOCK) {
                ttsReady = status == TextToSpeech.SUCCESS;
                if (!ttsReady || textToSpeech == null) {
                    return;
                }
                try {
                    textToSpeech.setLanguage(Locale.CHINA);
                } catch (Exception ignore) {
                    // Keep the default voice when locale switching fails.
                }
                if (pendingSpeechText != null && !pendingSpeechText.trim().isEmpty()) {
                    textToSpeech.speak(pendingSpeechText, TextToSpeech.QUEUE_FLUSH, null, "station-audio-pending");
                    pendingSpeechText = null;
                }
            }
        });
    }

    private void applyAudioVolume(Context context, ShellConfig shellConfig, int volumeMode, boolean outerEnabled) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null || shellConfig == null) {
            return;
        }
        int targetVolume;
        if (volumeMode == VOLUME_MODE_TTS) {
            targetVolume = outerEnabled
                    ? shellConfig.getBasicSetupConfig().getTtsSettings().getOuterVolume()
                    : shellConfig.getBasicSetupConfig().getTtsSettings().getInnerVolume();
        } else if (volumeMode == VOLUME_MODE_DISPATCH) {
            targetVolume = shellConfig.getBasicSetupConfig().getOtherSettings().getDispatchVolume();
        } else {
            targetVolume = outerEnabled
                    ? shellConfig.getBasicSetupConfig().getNewspaperSettings().getOuterVolume()
                    : shellConfig.getBasicSetupConfig().getNewspaperSettings().getInnerVolume();
        }
        int bounded = Math.max(0, Math.min(targetVolume, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)));
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, bounded, 0);
    }

    private void enablePinsLocked(ShellConfig shellConfig, boolean innerEnabled, boolean outerEnabled) {
        writePinIfPresent(shellConfig, "inner_audio", innerEnabled ? 1 : 0);
        writePinIfPresent(shellConfig, "outer_audio", outerEnabled ? 1 : 0);
        writePinIfPresent(shellConfig, "inner_speaker", innerEnabled ? 1 : 0);
    }

    private void disablePinsLocked() {
        PlaybackPlan plan = activePlan;
        if (plan == null || plan.shellConfig == null) {
            return;
        }
        writePinIfPresent(plan.shellConfig, "inner_audio", 0);
        writePinIfPresent(plan.shellConfig, "outer_audio", 0);
        writePinIfPresent(plan.shellConfig, "inner_speaker", 0);
        activePlan = null;
    }

    private void writePinIfPresent(ShellConfig shellConfig, String pinKey, int value) {
        if (gpioAdapter == null || shellConfig == null || !shellConfig.getGpioConfig().getPins().containsKey(pinKey)) {
            return;
        }
        try {
            gpioAdapter.write(pinKey, value, "station-audio-" + pinKey + "-" + value);
        } catch (Exception ignore) {
            // Keep audio playback moving when GPIO toggling is unavailable.
        }
    }

    private void stopLocked() {
        if (mediaPlayer == null) {
            return;
        }
        try {
            mediaPlayer.stop();
        } catch (Exception ignore) {
            // Ignore stop failures and continue releasing.
        }
        try {
            mediaPlayer.release();
        } catch (Exception ignore) {
            // Ignore release failures.
        }
        mediaPlayer = null;
    }

    private static final class PlaybackPlan {
        private final List<File> innerPlaylist = new ArrayList<>();
        private final List<File> outerPlaylist = new ArrayList<>();
        private String ttsText = "";
        private ShellConfig shellConfig;
        private int volumeMode = VOLUME_MODE_NEWSPAPER;
    }
}