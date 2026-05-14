package com.lhxy.istationdevice.android11.domain.station;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
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
    private static final String TAG = "LegacyStationAudio";
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
    private static final int STEP_TYPE_TTS = 0;
    private static final int STEP_TYPE_FILE = 1;
    private static final String UTTERANCE_ID_STATION = "station-audio";
    private static final String UTTERANCE_ID_PENDING = "station-audio-pending";

    private static final Object GLOBAL_LOCK = new Object();
    private final GpioAdapter gpioAdapter;

    private static MediaPlayer mediaPlayer;
    private static TextToSpeech textToSpeech;
    private static boolean ttsReady;
    private static boolean ttsInitFailed;
    private static boolean ttsFailureHandledDuringEnsure;
    private static String pendingSpeechText;
    private static PlaybackPlan activePlan;
    private static long requestSerial;

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
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "手动报站音频跳过: 参数不完整", "station-audio-manual-skip");
            return;
        }
        int broadcastType = resolveManualBroadcastType(route, station, stationType);
        if (broadcastType < 0) {
            AppLogCenter.log(LogCategory.BIZ, LogLevel.WARN, TAG, "手动报站音频跳过: broadcastType=" + broadcastType + " / stationType=" + stationType + " / station=" + station.getStationName(), "station-audio-manual-skip");
            return;
        }
        PlaybackPlan plan = createStationPlan(context, shellConfig, route, station, broadcastType);
        plan.trigger = "manual-station";
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
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "自动报站音频跳过: 参数不完整", "station-audio-auto-skip");
            return;
        }
        if (stationType != LegacyGpsAutoReportEngine.STATION_TYPE_ENTER) {
            AppLogCenter.log(LogCategory.BIZ, LogLevel.DEBUG, TAG, "自动报站音频跳过: 非进站类型 stationType=" + stationType, "station-audio-auto-skip");
            return;
        }
        if (station.getStationNo() <= 0) {
            AppLogCenter.log(LogCategory.BIZ, LogLevel.WARN, TAG, "自动报站音频跳过: stationNo=" + station.getStationNo() + " / station=" + station.getStationName(), "station-audio-auto-skip");
            return;
        }
        int broadcastType = station.getStationNo() >= route.getStations().size() - 1
                ? BROADCAST_TYPE_TERMINAL_ENTER
                : BROADCAST_TYPE_ENTER;
        PlaybackPlan plan = createStationPlan(context, shellConfig, route, station, broadcastType);
            plan.trigger = "auto-station";
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
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "提醒点音频跳过: 参数不完整", "station-audio-reminder-skip");
            return;
        }
        PlaybackPlan plan = createReminderPlan(context, shellConfig, reminder);
        plan.trigger = "reminder";
        play(context, shellConfig, plan, false);
    }

    /**
     * 播放调度公告语音。
     */
    public void playDispatchNotice(Context context, ShellConfig shellConfig, String message) {
        if (context == null || shellConfig == null || message == null || message.trim().isEmpty() || "-".equals(message.trim())) {
            AppLogCenter.log(LogCategory.BIZ, LogLevel.WARN, TAG, "调度语音跳过: message=" + compactText(message), "station-audio-dispatch-skip");
            return;
        }
        Context appContext = context.getApplicationContext();
        synchronized (GLOBAL_LOCK) {
            stopLocked();
            disablePinsLocked();
            enablePinsLocked(shellConfig, true, false, true);
            applyAudioVolume(appContext, shellConfig, VOLUME_MODE_DISPATCH, false);
            PlaybackPlan plan = new PlaybackPlan();
            plan.appContext = appContext;
            plan.shellConfig = shellConfig;
            plan.trigger = "dispatch-notice";
            plan.ttsText = message;
            activePlan = plan;
            ensureRequestIdLocked(plan);
            logPlanMessage(LogCategory.BIZ, LogLevel.INFO, plan, "调度语音准备 TTS: " + compactText(message), "station-audio-dispatch");
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
            AppLogCenter.log(LogCategory.BIZ, LogLevel.WARN, TAG, "超速提示音跳过: 未找到提示音资源", "station-audio-speed-skip");
            return;
        }
        plan.trigger = "speed-warning";
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
            AppLogCenter.log(LogCategory.BIZ, LogLevel.WARN, TAG, "整点报时跳过: hour=" + hour + " / 未找到报时资源", "station-audio-clock-skip");
            return;
        }
        plan.trigger = "now-time";
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
            AppLogCenter.log(LogCategory.BIZ, LogLevel.WARN, TAG, "服务音跳过: no=" + serviceNo + " / 未找到服务音资源", "station-audio-service-skip");
            return false;
        }
        plan.trigger = "service-tone-" + serviceNo;
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
                AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "音频播放跳过: plan 为空", "station-audio-plan");
                return;
            }
            plan.shellConfig = shellConfig;
            activePlan = plan;
                ensureRequestIdLocked(plan);
                logPlanMessage(
                    LogCategory.BIZ,
                    LogLevel.INFO,
                    plan,
                    "音频播放计划 trigger=" + plan.trigger
                        + " / preferTts=" + preferTts
                        + " / station=" + plan.stationSummary
                        + " / resource=" + plan.resourceSummary
                        + " / source=" + plan.sourceSummary
                        + " / innerCount=" + plan.innerPlaylist.size()
                        + " / outerCount=" + plan.outerPlaylist.size()
                    + " / mixedCount=" + plan.mixedSteps.size()
                        + " / firstInner=" + firstPath(plan.innerPlaylist)
                        + " / firstOuter=" + firstPath(plan.outerPlaylist)
                        + " / tts=" + compactText(plan.ttsText),
                    "station-audio-plan"
                );
                plan.appContext = appContext;
                if (preferTts && !plan.mixedSteps.isEmpty()) {
                startMixedPlaybackLocked(appContext, shellConfig, plan);
                return;
                }
            if (!preferTts && !plan.innerPlaylist.isEmpty()) {
                enablePinsLocked(shellConfig, true, false, false);
                applyAudioVolume(appContext, shellConfig, plan.volumeMode, false);
                playLocked(appContext, shellConfig, plan, false, 0);
                return;
            }
            if (!preferTts && !plan.outerPlaylist.isEmpty()) {
                enablePinsLocked(shellConfig, false, true, false);
                applyAudioVolume(appContext, shellConfig, plan.volumeMode, true);
                playLocked(appContext, shellConfig, plan, true, 0);
                return;
            }
            boolean externalEnabled = shellConfig.getBasicSetupConfig().getNewspaperSettings().isExternalSoundEnabled();
            enablePinsLocked(shellConfig, true, externalEnabled, false);
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
        plan.stationSummary = buildStationSummary(route, station, broadcastType);
        plan.resourceSummary = buildStationResourceSummary(route, station);
        plan.sourceSummary = "sourceRoot=" + sourceRoot.getAbsolutePath()
            + " exists=" + sourceRoot.exists()
            + " / common=" + commonSoundsRoot.exists()
            + " / lineVoice=" + lineVoiceRoot.getAbsolutePath()
            + " exists=" + lineVoiceRoot.exists();
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
        buildStationMixedSteps(plan, route, station, broadcastType, commonSoundsRoot, lineVoiceRoot);
        plan.ttsText = buildStationTtsText(route, station, broadcastType);
        return plan;
    }

    private void buildStationMixedSteps(
            PlaybackPlan plan,
            LegacyGpsRouteResource route,
            LegacyGpsRouteResource.StationPoint station,
            int broadcastType,
            File commonSoundsRoot,
            File lineVoiceRoot
    ) {
        if (plan == null || route == null || station == null) {
            return;
        }
        LegacyGpsRouteResource.StationPoint firstStation = route.firstStation();
        LegacyGpsRouteResource.StationPoint terminalStation = route.lastStation();
        switch (broadcastType) {
            case BROADCAST_TYPE_START:
                addMixedTextStep(plan, "乘客朋友们，欢迎您乘坐");
                addMixedNamedVoiceOrText(plan, lineVoiceRoot, "", LANGUAGE_MANDARIN, route.getLineName(), route.getLineName());
                addMixedTextStep(plan, "路公共汽车，本车由");
                addMixedStationVoiceOrText(plan, lineVoiceRoot, LANGUAGE_MANDARIN, firstStation);
                addMixedTextStep(plan, "，开往");
                addMixedStationVoiceOrText(plan, lineVoiceRoot, LANGUAGE_MANDARIN, terminalStation);
                addMixedTextStep(plan, "，途径");
                addMixedMajorStations(plan, route, lineVoiceRoot, LANGUAGE_MANDARIN);
                addMixedTextStep(plan, "等站，请您按次序乘车，谢谢合作,下一站是");
                addMixedStationVoiceOrText(plan, lineVoiceRoot, LANGUAGE_MANDARIN, station);
                addMixedNamedVoiceOrText(plan, commonSoundsRoot, "Extend", LANGUAGE_MANDARIN, station.getDepartureExpansion(), station.getDepartureExpansion());
                return;
            case BROADCAST_TYPE_ENTER:
                addMixedTextStep(plan, "乘客朋友们，");
                addMixedNamedVoiceOrText(plan, commonSoundsRoot, "Adv", LANGUAGE_MANDARIN, station.getStationAdvert(), station.getStationAdvert());
                addMixedStationVoiceOrText(plan, lineVoiceRoot, LANGUAGE_MANDARIN, station);
                addMixedTextStep(plan, "到了,");
                addMixedNamedVoiceOrText(plan, commonSoundsRoot, "Other", LANGUAGE_MANDARIN, station.getStationPrompt(), station.getStationPrompt());
                addMixedTextStep(plan, ",需要下车的乘客,请从后门下车");
                addMixedNamedVoiceOrText(plan, commonSoundsRoot, "Extend", LANGUAGE_MANDARIN, station.getStationExpansion(), station.getStationExpansion());
                return;
            case BROADCAST_TYPE_LEAVE:
                addMixedNamedVoiceOrText(plan, commonSoundsRoot, "Adv", LANGUAGE_MANDARIN, station.getDepartureAdvert(), station.getDepartureAdvert());
                addMixedTextStep(plan, "刚上车的乘客，请您往里走，请坐好站稳，下一站是");
                addMixedStationVoiceOrText(plan, lineVoiceRoot, LANGUAGE_MANDARIN, station);
                addMixedNamedVoiceOrText(plan, commonSoundsRoot, "Other", LANGUAGE_MANDARIN, station.getDeparturePrompt(), station.getDeparturePrompt());
                addMixedTextStep(plan, "，需要下车的乘客，请做好下车准备");
                addMixedNamedVoiceOrText(plan, commonSoundsRoot, "Extend", LANGUAGE_MANDARIN, station.getDepartureExpansion(), station.getDepartureExpansion());
                return;
            case BROADCAST_TYPE_TERMINAL_PREVIEW:
                addMixedTextStep(plan, "下一站是终点站，");
                addMixedStationVoiceOrText(plan, lineVoiceRoot, LANGUAGE_MANDARIN, station);
                addMixedTextStep(plan, "，请您携带好自己行李物品，准备下车");
                return;
            case BROADCAST_TYPE_TERMINAL_ENTER:
                addMixedTextStep(plan, "终点站");
                addMixedStationVoiceOrText(plan, lineVoiceRoot, LANGUAGE_MANDARIN, station);
                addMixedTextStep(plan, "到了，请您携带好自己行李物品，准备下车");
                return;
            default:
                return;
        }
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
        plan.sourceSummary = "sourceRoot=" + sourceRoot.getAbsolutePath()
            + " exists=" + sourceRoot.exists()
            + " / common=" + commonSoundsRoot.exists();
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
        plan.sourceSummary = "sourceRoot=" + sourceRoot.getAbsolutePath()
            + " exists=" + sourceRoot.exists()
            + " / systemAlert=" + alertTone.exists()
            + " / common=" + commonSoundsRoot.exists();
        addIfExists(plan.innerPlaylist, new File(commonSoundsRoot, "Common/" + LANGUAGE_MANDARIN + "/超速语音.mp3"));
        plan.ttsText = "请注意控制车速";
        return plan;
    }

    private PlaybackPlan createNowTimePlan(Context context, ShellConfig shellConfig, int hour) {
        PlaybackPlan plan = new PlaybackPlan();
        plan.volumeMode = VOLUME_MODE_NEWSPAPER;
        File sourceRoot = new StationResourceArchiveUseCase().resolveManagedSourceRoot(context.getApplicationContext());
        File commonSoundsRoot = new File(sourceRoot, "CommonSounds");
        plan.sourceSummary = "sourceRoot=" + sourceRoot.getAbsolutePath()
            + " exists=" + sourceRoot.exists()
            + " / common=" + commonSoundsRoot.exists();
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
        plan.sourceSummary = "sourceRoot=" + sourceRoot.getAbsolutePath()
            + " exists=" + sourceRoot.exists()
            + " / common=" + commonSoundsRoot.exists();
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

    private void addMixedMajorStations(
            PlaybackPlan plan,
            LegacyGpsRouteResource route,
            File lineVoiceRoot,
            String languageFolder
    ) {
        if (plan == null || route == null || route.getStations().isEmpty()) {
            return;
        }
        for (LegacyGpsRouteResource.StationPoint point : route.getStations()) {
            if (point == null || !MAJOR_STATION_FLAG.equals(point.getMajorStation())) {
                continue;
            }
            addMixedStationVoiceOrText(plan, lineVoiceRoot, languageFolder, point);
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

    private void addMixedTextStep(PlaybackPlan plan, String text) {
        if (plan == null) {
            return;
        }
        String normalized = normalizeSegmentText(text);
        if (normalized.isEmpty()) {
            return;
        }
        plan.mixedSteps.add(new PlaybackStep(STEP_TYPE_TTS, null, normalized));
    }

    private void addMixedNamedVoiceOrText(
            PlaybackPlan plan,
            File root,
            String category,
            String languageFolder,
            String voiceName,
            String fallbackText
    ) {
        if (plan == null) {
            return;
        }
        File voiceFile = resolveNamedVoiceFile(root, category, languageFolder, voiceName);
        if (voiceFile != null) {
            plan.mixedSteps.add(new PlaybackStep(STEP_TYPE_FILE, voiceFile, null));
            return;
        }
        addMixedTextStep(plan, fallbackText);
    }

    private void addMixedStationVoiceOrText(
            PlaybackPlan plan,
            File lineVoiceRoot,
            String languageFolder,
            LegacyGpsRouteResource.StationPoint station
    ) {
        if (plan == null || station == null) {
            return;
        }
        File stationVoice = resolveStationVoiceFile(lineVoiceRoot, languageFolder, station);
        if (stationVoice != null) {
            plan.mixedSteps.add(new PlaybackStep(STEP_TYPE_FILE, stationVoice, null));
            return;
        }
        addMixedTextStep(plan, station.getStationName());
    }

    private File resolveNamedVoiceFile(File root, String category, String languageFolder, String voiceName) {
        String normalized = normalizeSegmentText(voiceName);
        if (root == null || languageFolder == null || normalized.isEmpty() || "-".equals(normalized)) {
            return null;
        }
        String basePath = category == null || category.isEmpty()
                ? languageFolder + "/" + normalized + ".mp3"
                : category + "/" + languageFolder + "/" + normalized + ".mp3";
        File file = new File(root, basePath);
        return file.isFile() ? file : null;
    }

    private File resolveStationVoiceFile(File lineVoiceRoot, String languageFolder, LegacyGpsRouteResource.StationPoint station) {
        if (station == null) {
            return null;
        }
        return resolveNamedVoiceFile(lineVoiceRoot, "", languageFolder, station.getStationSound());
    }

    private String normalizeSegmentText(String text) {
        if (text == null) {
            return "";
        }
        String normalized = text.trim();
        return "-".equals(normalized) ? "" : normalized;
    }

    private String buildStationSummary(
            LegacyGpsRouteResource route,
            LegacyGpsRouteResource.StationPoint station,
            int broadcastType
    ) {
        String lineName = route == null ? "-" : safeLogValue(route.getLineName());
        String stationNo = station == null ? "-" : String.valueOf(station.getStationNo());
        String stationName = station == null ? "-" : safeLogValue(station.getStationName());
        return "line=" + lineName
                + " / stationNo=" + stationNo
                + " / stationName=" + stationName
                + " / broadcastType=" + describeBroadcastType(broadcastType);
    }

    private String buildStationResourceSummary(
            LegacyGpsRouteResource route,
            LegacyGpsRouteResource.StationPoint station
    ) {
        return "lineVoice=" + (route == null ? "-" : safeLogValue(route.getLineName()))
                + " / stationSound=" + (station == null ? "-" : safeLogValue(station.getStationSound()))
                + " / stationAdvert=" + (station == null ? "-" : safeLogValue(station.getStationAdvert()))
                + " / departureAdvert=" + (station == null ? "-" : safeLogValue(station.getDepartureAdvert()))
                + " / stationPrompt=" + (station == null ? "-" : safeLogValue(station.getStationPrompt()))
                + " / departurePrompt=" + (station == null ? "-" : safeLogValue(station.getDeparturePrompt()))
                + " / stationExpansion=" + (station == null ? "-" : safeLogValue(station.getStationExpansion()))
                + " / departureExpansion=" + (station == null ? "-" : safeLogValue(station.getDepartureExpansion()));
    }

    private String describeBroadcastType(int broadcastType) {
        switch (broadcastType) {
            case BROADCAST_TYPE_START:
                return "start";
            case BROADCAST_TYPE_ENTER:
                return "enter";
            case BROADCAST_TYPE_LEAVE:
                return "leave";
            case BROADCAST_TYPE_TERMINAL_PREVIEW:
                return "terminal-preview";
            case BROADCAST_TYPE_TERMINAL_ENTER:
                return "terminal-enter";
            default:
                return String.valueOf(broadcastType);
        }
    }

    private String safeLogValue(String value) {
        String normalized = normalizeSegmentText(value);
        return normalized.isEmpty() ? "-" : normalized;
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
                enablePinsLocked(shellConfig, false, true, false);
                applyAudioVolume(context, shellConfig, plan.volumeMode, true);
                playLocked(context, shellConfig, plan, true, 0);
                return;
            }
            logPlanMessage(LogCategory.BIZ, LogLevel.INFO, plan, "音频播放列表结束 trigger=" + plan.trigger + " / outerPhase=" + outerPhase, "station-audio-player");
            stopLocked();
            disablePinsLocked();
            return;
        }
        File target = playlist.get(index);
        MediaPlayer player = MediaPlayer.create(context, Uri.fromFile(target));
        if (player == null) {
            logPlanMessage(LogCategory.ERROR, LogLevel.WARN, plan, "音频文件无法创建播放器: " + target.getAbsolutePath(), "station-audio-player");
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
                logPlanMessage(LogCategory.ERROR, LogLevel.WARN, plan, "MediaPlayer 播放错误 what=" + what + " / extra=" + extra + " / file=" + target.getAbsolutePath(), "station-audio-player-error");
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
        logPlanMessage(
            LogCategory.BIZ,
            LogLevel.INFO,
            plan,
            "开始播放" + (outerPhase ? "外音" : "内音") + " index=" + index + "/" + playlist.size()
                + " / file=" + target.getAbsolutePath(),
            "station-audio-player"
        );
        try {
            player.start();
        } catch (Exception e) {
            logPlanMessage(LogCategory.ERROR, LogLevel.ERROR, plan, "MediaPlayer start 失败: " + e.getMessage() + " / file=" + target.getAbsolutePath(), "station-audio-player-error");
            try {
                player.release();
            } catch (Exception ignore) {
                // Ignore release failures.
            }
            if (mediaPlayer == player) {
                mediaPlayer = null;
            }
            playLocked(context, shellConfig, plan, outerPhase, index + 1);
        }
    }

    private void speakLocked(Context context, String text) {
        String normalized = text == null ? "" : text.trim();
        if (normalized.isEmpty() || "-".equals(normalized)) {
            return;
        }
        ttsFailureHandledDuringEnsure = false;
        ensureTextToSpeechLocked(context);
        if (ttsFailureHandledDuringEnsure) {
            return;
        }
        if (ttsInitFailed) {
            handleUnavailableTtsLocked(context, activePlan, "init-failed");
            return;
        }
        if (!ttsReady || textToSpeech == null) {
            pendingSpeechText = normalized;
            logPlanMessage(LogCategory.BIZ, LogLevel.WARN, activePlan, "TTS 尚未就绪，已暂存: " + compactText(normalized), "station-audio-tts-pending");
            return;
        }
        pendingSpeechText = null;
        logPlanMessage(LogCategory.BIZ, LogLevel.INFO, activePlan, "开始 TTS: " + compactText(normalized), "station-audio-tts");
        textToSpeech.speak(normalized, TextToSpeech.QUEUE_FLUSH, (Bundle) null, UTTERANCE_ID_STATION);
    }

    private String firstPath(List<File> playlist) {
        if (playlist == null || playlist.isEmpty() || playlist.get(0) == null) {
            return "-";
        }
        return playlist.get(0).getAbsolutePath();
    }

    private String compactText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "-";
        }
        String normalized = text.replace('\n', ' ').replace('\r', ' ').replaceAll("\\s+", " ").trim();
        return normalized.length() <= 80 ? normalized : normalized.substring(0, 80) + "...";
    }

    private void ensureTextToSpeechLocked(Context context) {
        if (ttsInitFailed) {
            return;
        }
        if (textToSpeech != null) {
            return;
        }
        textToSpeech = new TextToSpeech(context.getApplicationContext(), status -> {
            synchronized (GLOBAL_LOCK) {
                ttsReady = status == TextToSpeech.SUCCESS;
                if (!ttsReady || textToSpeech == null) {
                    ttsInitFailed = true;
                    logPlanMessage(LogCategory.ERROR, LogLevel.WARN, activePlan, "TTS 初始化失败 status=" + status, "station-audio-tts-init");
                    TextToSpeech failedTts = textToSpeech;
                    textToSpeech = null;
                    pendingSpeechText = null;
                    if (failedTts != null) {
                        try {
                            failedTts.shutdown();
                        } catch (Exception ignore) {
                            logPlanMessage(LogCategory.ERROR, LogLevel.WARN, activePlan, "TTS 释放失败: " + ignore.getMessage(), "station-audio-tts-init");
                        }
                    }
                    ttsFailureHandledDuringEnsure = true;
                    handleUnavailableTtsLocked(context.getApplicationContext(), activePlan, "init-status-" + status);
                    return;
                }
                ttsInitFailed = false;
                try {
                    textToSpeech.setLanguage(Locale.CHINA);
                } catch (Exception ignore) {
                    logPlanMessage(LogCategory.ERROR, LogLevel.WARN, activePlan, "TTS 设置中文失败: " + ignore.getMessage(), "station-audio-tts-init");
                }
                try {
                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            synchronized (GLOBAL_LOCK) {
                                handleTtsFinishedLocked(utteranceId, false);
                            }
                        }

                        @Override
                        public void onError(String utteranceId) {
                            synchronized (GLOBAL_LOCK) {
                                handleTtsFinishedLocked(utteranceId, true);
                            }
                        }
                    });
                } catch (Exception ignore) {
                    logPlanMessage(LogCategory.ERROR, LogLevel.WARN, activePlan, "TTS 设置回调失败: " + ignore.getMessage(), "station-audio-tts-init");
                }
                if (pendingSpeechText != null && !pendingSpeechText.trim().isEmpty()) {
                    logPlanMessage(LogCategory.BIZ, LogLevel.INFO, activePlan, "播放暂存 TTS: " + compactText(pendingSpeechText), "station-audio-tts-pending");
                    textToSpeech.speak(pendingSpeechText, TextToSpeech.QUEUE_FLUSH, (Bundle) null, UTTERANCE_ID_PENDING);
                    pendingSpeechText = null;
                }
            }
        });
    }

    private void handleUnavailableTtsLocked(Context context, PlaybackPlan plan, String reason) {
        if (plan == null) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "TTS 不可用处理跳过: 播放计划为空 / reason=" + reason, "station-audio-tts-fallback");
            return;
        }
        pendingSpeechText = null;
        if (plan.mixedPlaybackActive) {
            if (!plan.mixedFilePlayed && !hasRemainingMixedFileStepLocked(plan, plan.mixedStepIndex + 1)) {
                fallbackToPlaylistLocked(context, plan, reason + " / no-remaining-mp3");
                return;
            }
            logPlanMessage(
                LogCategory.BIZ,
                LogLevel.WARN,
                plan,
                "TTS 不可用，跳过混播 TTS 片段 trigger=" + plan.trigger
                    + " / reason=" + reason
                    + " / step=" + plan.mixedStepIndex,
                "station-audio-tts-fallback"
            );
            playNextMixedStepLocked();
            return;
        }
        fallbackToPlaylistLocked(context, plan, reason);
    }

    private void startMixedPlaybackLocked(Context context, ShellConfig shellConfig, PlaybackPlan plan) {
        boolean externalEnabled = shellConfig.getBasicSetupConfig().getNewspaperSettings().isExternalSoundEnabled();
        plan.mixedPlaybackActive = true;
        plan.mixedStepIndex = -1;
        enablePinsLocked(shellConfig, true, externalEnabled, false);
        applyAudioVolume(context, shellConfig, VOLUME_MODE_TTS, externalEnabled);
        logPlanMessage(LogCategory.BIZ, LogLevel.INFO, plan, "开始混合报站 trigger=" + plan.trigger + " / steps=" + plan.mixedSteps.size(), "station-audio-mixed");
        playNextMixedStepLocked();
    }

    private void playNextMixedStepLocked() {
        PlaybackPlan plan = activePlan;
        if (plan == null || !plan.mixedPlaybackActive) {
            return;
        }
        int nextIndex = plan.mixedStepIndex + 1;
        if (nextIndex >= plan.mixedSteps.size()) {
            logPlanMessage(LogCategory.BIZ, LogLevel.INFO, plan, "混合报站结束 trigger=" + plan.trigger, "station-audio-mixed");
            stopLocked();
            disablePinsLocked();
            return;
        }
        plan.mixedStepIndex = nextIndex;
        PlaybackStep step = plan.mixedSteps.get(nextIndex);
        if (step.type == STEP_TYPE_TTS) {
            logPlanMessage(LogCategory.BIZ, LogLevel.INFO, plan, "混合报站 TTS step=" + nextIndex + " / text=" + compactText(step.text), "station-audio-mixed");
            speakLocked(plan.appContext, step.text);
            return;
        }
        if (step.file == null || !step.file.isFile()) {
            logPlanMessage(LogCategory.ERROR, LogLevel.WARN, plan, "混合报站文件缺失 step=" + nextIndex + " / file=" + (step.file == null ? "-" : step.file.getAbsolutePath()), "station-audio-mixed");
            playNextMixedStepLocked();
            return;
        }
        logPlanMessage(LogCategory.BIZ, LogLevel.INFO, plan, "混合报站 MP3 step=" + nextIndex + " / file=" + step.file.getAbsolutePath(), "station-audio-mixed");
        plan.mixedFilePlayed = true;
        playMixedStepLocked(plan.appContext, step.file);
    }

    private boolean hasRemainingMixedFileStepLocked(PlaybackPlan plan, int startIndex) {
        if (plan == null || plan.mixedSteps.isEmpty()) {
            return false;
        }
        int safeStart = Math.max(0, startIndex);
        for (int index = safeStart; index < plan.mixedSteps.size(); index++) {
            PlaybackStep step = plan.mixedSteps.get(index);
            if (step.type == STEP_TYPE_FILE && step.file != null && step.file.isFile()) {
                return true;
            }
        }
        return false;
    }

    private void playMixedStepLocked(Context context, File target) {
        PlaybackPlan plan = activePlan;
        MediaPlayer player = MediaPlayer.create(context, Uri.fromFile(target));
        if (player == null) {
            logPlanMessage(LogCategory.ERROR, LogLevel.WARN, plan, "混合报站播放器创建失败: " + target.getAbsolutePath(), "station-audio-player-error");
            playNextMixedStepLocked();
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
                playNextMixedStepLocked();
            }
        });
        player.setOnErrorListener((failed, what, extra) -> {
            synchronized (GLOBAL_LOCK) {
                logPlanMessage(LogCategory.ERROR, LogLevel.WARN, activePlan, "混合报站播放错误 what=" + what + " / extra=" + extra + " / file=" + target.getAbsolutePath(), "station-audio-player-error");
                try {
                    failed.release();
                } catch (Exception ignore) {
                    // Ignore release failures.
                }
                if (mediaPlayer == failed) {
                    mediaPlayer = null;
                }
                playNextMixedStepLocked();
            }
            return true;
        });
        try {
            player.start();
        } catch (Exception e) {
            logPlanMessage(LogCategory.ERROR, LogLevel.ERROR, plan, "混合报站 start 失败: " + e.getMessage() + " / file=" + target.getAbsolutePath(), "station-audio-player-error");
            try {
                player.release();
            } catch (Exception ignore) {
                // Ignore release failures.
            }
            if (mediaPlayer == player) {
                mediaPlayer = null;
            }
            playNextMixedStepLocked();
        }
    }

    private void handleTtsFinishedLocked(String utteranceId, boolean error) {
        PlaybackPlan plan = activePlan;
        if (plan == null) {
            return;
        }
        if (error) {
            logPlanMessage(LogCategory.ERROR, LogLevel.WARN, plan, "TTS 播放失败 utteranceId=" + utteranceId + " / trigger=" + plan.trigger, "station-audio-tts");
        }
        if (plan.mixedPlaybackActive) {
            playNextMixedStepLocked();
            return;
        }
        logPlanMessage(LogCategory.BIZ, LogLevel.INFO, plan, "TTS 播放结束 trigger=" + plan.trigger + " / utteranceId=" + utteranceId, "station-audio-tts");
        disablePinsLocked();
    }

    private void fallbackToPlaylistLocked(Context context, PlaybackPlan plan, String reason) {
        if (plan == null || plan.shellConfig == null) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "TTS 回退 MP3 跳过: 播放计划为空 / reason=" + reason, "station-audio-tts-fallback");
            return;
        }
        plan.mixedPlaybackActive = false;
        plan.mixedStepIndex = -1;
        if (plan.innerPlaylist.isEmpty() && plan.outerPlaylist.isEmpty()) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "TTS 回退 MP3 失败: 无可播音频 / trigger=" + plan.trigger + " / reason=" + reason, "station-audio-tts-fallback");
            return;
        }
        pendingSpeechText = null;
        logPlanMessage(
            LogCategory.BIZ,
            LogLevel.WARN,
            plan,
            "TTS 不可用，回退 MP3 trigger=" + plan.trigger
                + " / reason=" + reason
                + " / innerCount=" + plan.innerPlaylist.size()
                + " / outerCount=" + plan.outerPlaylist.size(),
            "station-audio-tts-fallback"
        );
        if (!plan.innerPlaylist.isEmpty()) {
            enablePinsLocked(plan.shellConfig, true, false, false);
            applyAudioVolume(context, plan.shellConfig, plan.volumeMode, false);
            playLocked(context, plan.shellConfig, plan, false, 0);
            return;
        }
        enablePinsLocked(plan.shellConfig, false, true, false);
        applyAudioVolume(context, plan.shellConfig, plan.volumeMode, true);
        playLocked(context, plan.shellConfig, plan, true, 0);
    }

    private void applyAudioVolume(Context context, ShellConfig shellConfig, int volumeMode, boolean outerEnabled) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null || shellConfig == null) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "设置音量跳过: AudioManager 或配置为空", "station-audio-volume");
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
        AppLogCenter.log(
            LogCategory.DEVICE,
            LogLevel.INFO,
            TAG,
            "设置音量 mode=" + volumeMode + " / outer=" + outerEnabled + " / target=" + targetVolume
                + " / bounded=" + bounded + " / max=" + audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
            "station-audio-volume"
        );
    }

    private void enablePinsLocked(ShellConfig shellConfig, boolean innerEnabled, boolean outerEnabled, boolean innerSpeakerEnabled) {
        writePinIfPresent(shellConfig, "inner_audio", innerEnabled ? 1 : 0);
        writePinIfPresent(shellConfig, "outer_audio", outerEnabled ? 1 : 0);
        writePinIfPresent(shellConfig, "inner_speaker", innerSpeakerEnabled ? 1 : 0);
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

    private void logPlanMessage(LogCategory category, LogLevel level, PlaybackPlan plan, String message, String event) {
        AppLogCenter.log(category, level, TAG, appendPlanTraceLocked(plan, message), event);
    }

    private String appendPlanTraceLocked(PlaybackPlan plan, String message) {
        if (plan == null) {
            return message;
        }
        ensureRequestIdLocked(plan);
        plan.logSequence += 1;
        return message + " / requestId=" + plan.requestId + " / seq=" + plan.logSequence;
    }

    private void ensureRequestIdLocked(PlaybackPlan plan) {
        if (plan == null || (plan.requestId != null && !plan.requestId.trim().isEmpty())) {
            return;
        }
        requestSerial += 1;
        String trigger = safeLogValue(plan.trigger);
        plan.requestId = trigger + "-" + System.currentTimeMillis() + "-" + requestSerial;
    }

    private void writePinIfPresent(ShellConfig shellConfig, String pinKey, int value) {
        if (gpioAdapter == null || shellConfig == null || !shellConfig.getGpioConfig().getPins().containsKey(pinKey)) {
            AppLogCenter.log(LogCategory.DEVICE, LogLevel.WARN, TAG, "音频 GPIO 跳过 pin=" + pinKey + " / value=" + value + " / configured=" + (shellConfig != null && shellConfig.getGpioConfig().getPins().containsKey(pinKey)), "station-audio-gpio");
            return;
        }
        try {
            gpioAdapter.write(pinKey, value, "station-audio-" + pinKey + "-" + value);
            AppLogCenter.log(LogCategory.DEVICE, LogLevel.INFO, TAG, "音频 GPIO 写入 pin=" + pinKey + " / value=" + value, "station-audio-gpio");
        } catch (Exception ignore) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "音频 GPIO 写入失败 pin=" + pinKey + " / value=" + value + " / error=" + ignore.getMessage(), "station-audio-gpio");
        }
    }

    private void stopLocked() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (Exception ignore) {
                AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "MediaPlayer stop 失败: " + ignore.getMessage(), "station-audio-stop");
            }
            try {
                mediaPlayer.release();
            } catch (Exception ignore) {
                AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "MediaPlayer release 失败: " + ignore.getMessage(), "station-audio-stop");
            }
            mediaPlayer = null;
        }
        pendingSpeechText = null;
        if (activePlan != null) {
            activePlan.mixedPlaybackActive = false;
            activePlan.mixedStepIndex = -1;
            activePlan.mixedFilePlayed = false;
        }
        if (textToSpeech != null) {
            try {
                textToSpeech.stop();
            } catch (Exception ignore) {
                AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "TTS stop 失败: " + ignore.getMessage(), "station-audio-stop");
            }
        }
    }

    private static final class PlaybackPlan {
        private final List<File> innerPlaylist = new ArrayList<>();
        private final List<File> outerPlaylist = new ArrayList<>();
        private final List<PlaybackStep> mixedSteps = new ArrayList<>();
        private String ttsText = "";
        private String trigger = "-";
        private String requestId = "";
        private int logSequence;
        private String stationSummary = "-";
        private String resourceSummary = "-";
        private String sourceSummary = "-";
        private ShellConfig shellConfig;
        private Context appContext;
        private int volumeMode = VOLUME_MODE_NEWSPAPER;
        private int mixedStepIndex = -1;
        private boolean mixedPlaybackActive;
        private boolean mixedFilePlayed;
    }

    private static final class PlaybackStep {
        private final int type;
        private final File file;
        private final String text;

        private PlaybackStep(int type, File file, String text) {
            this.type = type;
            this.file = file;
            this.text = text == null ? "" : text;
        }
    }
}