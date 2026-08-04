package com.lhxy.istationdevice.android11.app.dispatch;

import android.content.Context;
import android.media.AudioManager;
import com.lhxy.istationdevice.android11.app.audio.LegacyTtsEngine;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

final class LegacyCardSpeechController {
    enum VolumeSource {
        TTS_INNER,
        DISPATCH
    }

    private final Context appContext;
    private final AudioManager audioManager;
    private final String tag;
    private final String logTraceId;
    private final String gpioTracePrefix;
    private final String utteranceId;
    private final VolumeSource volumeSource;

    private LegacyTtsEngine ttsEngine;

    LegacyCardSpeechController(
            Context context,
            String tag,
            String logTraceId,
            String gpioTracePrefix,
            String utteranceId,
            VolumeSource volumeSource
    ) {
        this.appContext = context.getApplicationContext();
        this.audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
        this.tag = tag;
        this.logTraceId = logTraceId;
        this.gpioTracePrefix = gpioTracePrefix;
        this.utteranceId = utteranceId;
        this.volumeSource = volumeSource == null ? VolumeSource.TTS_INNER : volumeSource;
    }

    void init() {
        ttsEngine = LegacyTtsEngine.get(appContext);
    }

    void speakCard(String cardNo) {
        String displayId = normalizeCardForDisplay(cardNo);
        if (displayId.isEmpty()) {
            disableInnerHorn();
            return;
        }
        String speechText = "ID号 " + spellOut(displayId);
        init();
        enableInnerHorn();
        applyConfiguredVolume();
        boolean spoken = ttsEngine.speak(speechText, utteranceId, new LegacyTtsEngine.Listener() {
            @Override
            public void onDone() {
                disableInnerHorn();
            }

            @Override
            public void onError() {
                disableInnerHorn();
            }

            @Override
            public void onStop() {
                disableInnerHorn();
            }
        });
        if (!spoken) {
            AppLogCenter.log(LogCategory.BIZ, LogLevel.WARN, tag,
                    "卡号播报失败: TTS 未就绪 card=" + displayId, logTraceId);
            disableInnerHorn();
            return;
        }
    }

    void stop() {
        if (ttsEngine != null) {
            ttsEngine.stop();
        }
        disableInnerHorn();
    }

    void shutdown() {
        // The shared engine is owned by the application and remains warm across pages.
    }

    static String normalizeCardForDisplay(String cardNo) {
        String normalized = cardNo == null ? "" : cardNo.trim();
        if (normalized.isEmpty() || "-".equals(normalized)) {
            return "";
        }
        String hex = normalized.replaceAll("[^0-9A-Fa-f]", "");
        if (hex.length() >= 2 && hex.length() % 2 == 0 && hex.length() == normalized.replaceAll("\\s+", "").length()) {
            String decoded = decodeAsciiHex(hex);
            if (!decoded.isEmpty()) {
                return decoded;
            }
        }
        return normalized.replaceAll("\\s+", "");
    }

    private static String decodeAsciiHex(String hex) {
        StringBuilder builder = new StringBuilder(hex.length() / 2);
        for (int index = 0; index + 1 < hex.length(); index += 2) {
            int value;
            try {
                value = Integer.parseInt(hex.substring(index, index + 2), 16);
            } catch (NumberFormatException e) {
                return "";
            }
            if (value == 0) {
                continue;
            }
            if (value < 0x20 || value > 0x7E) {
                return "";
            }
            builder.append((char) value);
        }
        return builder.toString().trim();
    }

    private static String spellOut(String value) {
        String compact = value == null ? "" : value.trim().replaceAll("\\s+", "");
        StringBuilder builder = new StringBuilder(compact.length() * 2);
        for (int index = 0; index < compact.length(); index++) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(compact.charAt(index));
        }
        return builder.toString();
    }

    private void applyConfiguredVolume() {
        if (audioManager == null) {
            return;
        }
        ShellConfig shellConfig = ShellRuntime.get().getActiveConfig();
        if (shellConfig == null) {
            return;
        }
        int targetVolume = volumeSource == VolumeSource.DISPATCH
                ? shellConfig.getBasicSetupConfig().getOtherSettings().getDispatchVolume()
                : shellConfig.getBasicSetupConfig().getTtsSettings().getInnerVolume();
        int bounded = Math.max(0, Math.min(targetVolume, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)));
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, bounded, 0);
        AppLogCenter.log(
                LogCategory.DEVICE,
                LogLevel.INFO,
                tag,
                "刷卡播报音量 source=" + volumeSource + " / target=" + targetVolume + " / bounded=" + bounded,
                logTraceId
        );
    }

    private void enableInnerHorn() {
        writePin("inner_audio", 0);
        writePin("outer_audio", 0);
        writePin("headphone_detect_power", 0);
        writePin("inner_speaker", 1);
    }

    private void disableInnerHorn() {
        writePin("inner_audio", 0);
        writePin("outer_audio", 0);
        writePin("headphone_detect_power", 1);
        writePin("inner_speaker", 0);
    }

    private void writePin(String pinKey, int value) {
        ShellConfig shellConfig = ShellRuntime.get().getActiveConfig();
        if (shellConfig == null || !shellConfig.getGpioConfig().getPins().containsKey(pinKey)) {
            return;
        }
        try {
            ShellRuntime.get().getGpioAdapter().write(pinKey, value, gpioTracePrefix + "-" + pinKey + "-" + value);
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, tag,
                    "卡号播报 GPIO 写入失败 pin=" + pinKey + " / value=" + value + " / error=" + e.getMessage(), logTraceId);
        }
    }
}
