package com.lhxy.istationdevice.android11.app.dispatch;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

import java.util.Locale;

final class LegacyCardSpeechController {
    private static final String PREFERRED_TTS_ENGINE = "Test";

    private final Context appContext;
    private final AudioManager audioManager;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final String tag;
    private final String logTraceId;
    private final String gpioTracePrefix;
    private final String utteranceId;

    private TextToSpeech textToSpeech;
    private volatile boolean ttsReady;
    private boolean ttsDefaultEngineTried;
    private volatile String pendingSpeechText = "";

    LegacyCardSpeechController(Context context, String tag, String logTraceId, String gpioTracePrefix, String utteranceId) {
        this.appContext = context.getApplicationContext();
        this.audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
        this.tag = tag;
        this.logTraceId = logTraceId;
        this.gpioTracePrefix = gpioTracePrefix;
        this.utteranceId = utteranceId;
    }

    void init() {
        if (textToSpeech != null) {
            return;
        }
        createTts(PREFERRED_TTS_ENGINE);
    }

    void speakCard(String cardNo) {
        String displayId = normalizeCardForDisplay(cardNo);
        if (displayId.isEmpty()) {
            disableInnerHorn();
            return;
        }
        String speechText = "ID号 " + spellOut(displayId);
        pendingSpeechText = speechText;
        init();
        enableInnerHorn();
        applyInnerTtsVolume();
        if (!ttsReady || textToSpeech == null) {
            AppLogCenter.log(LogCategory.BIZ, LogLevel.WARN, tag,
                    "卡号播报暂无声: TTS 未就绪(已暂存=" + displayId + ")", logTraceId);
            return;
        }
        pendingSpeechText = "";
        textToSpeech.stop();
        int result = textToSpeech.speak(speechText, TextToSpeech.QUEUE_FLUSH, (Bundle) null, utteranceId);
        AppLogCenter.log(LogCategory.BIZ, result == TextToSpeech.SUCCESS ? LogLevel.INFO : LogLevel.WARN, tag,
                "卡号播报开始 card=" + displayId + " / text=" + speechText + " / result=" + result, logTraceId);
        if (result != TextToSpeech.SUCCESS) {
            disableInnerHorn();
        }
    }

    void stop() {
        pendingSpeechText = "";
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        disableInnerHorn();
    }

    void shutdown() {
        ttsReady = false;
        if (textToSpeech == null) {
            return;
        }
        textToSpeech.stop();
        textToSpeech.shutdown();
        textToSpeech = null;
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

    private void createTts(String engine) {
        boolean useSpecificEngine = engine != null && !engine.trim().isEmpty();
        String engineLabel = useSpecificEngine ? engine : "默认";
        TextToSpeech.OnInitListener listener = status -> {
            ttsReady = status == TextToSpeech.SUCCESS;
            if (!ttsReady) {
                TextToSpeech failed = textToSpeech;
                textToSpeech = null;
                if (failed != null) {
                    try {
                        failed.shutdown();
                    } catch (Exception ignore) {
                        // Ignore shutdown failures.
                    }
                }
                if (useSpecificEngine && !ttsDefaultEngineTried) {
                    ttsDefaultEngineTried = true;
                    AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, tag,
                            "卡号播报 TTS 引擎(" + engineLabel + ")初始化失败 status=" + status + "，回退默认引擎重试", logTraceId);
                    createTts(null);
                    return;
                }
                AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, tag,
                        "卡号播报 TTS 初始化失败 status=" + status + " 引擎=" + engineLabel, logTraceId);
                return;
            }
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, tag,
                    "卡号播报 TTS 初始化成功 引擎=" + engineLabel, logTraceId);
            if (textToSpeech != null) {
                textToSpeech.setLanguage(Locale.CHINA);
                String pending = pendingSpeechText == null ? "" : pendingSpeechText.trim();
                if (!pending.isEmpty()) {
                    textToSpeech.speak(pending, TextToSpeech.QUEUE_FLUSH, (Bundle) null, utteranceId);
                    pendingSpeechText = "";
                }
            }
        };
        textToSpeech = useSpecificEngine
                ? new TextToSpeech(appContext, listener, engine)
                : new TextToSpeech(appContext, listener);
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
            }

            @Override
            public void onDone(String utteranceId) {
                mainHandler.post(LegacyCardSpeechController.this::disableInnerHorn);
            }

            @Override
            public void onError(String utteranceId) {
                mainHandler.post(LegacyCardSpeechController.this::disableInnerHorn);
            }

            @Override
            public void onStop(String utteranceId, boolean interrupted) {
                mainHandler.post(LegacyCardSpeechController.this::disableInnerHorn);
            }
        });
    }

    private void applyInnerTtsVolume() {
        if (audioManager == null) {
            return;
        }
        ShellConfig shellConfig = ShellRuntime.get().getActiveConfig();
        if (shellConfig == null) {
            return;
        }
        int targetVolume = shellConfig.getBasicSetupConfig().getTtsSettings().getInnerVolume();
        int bounded = Math.max(0, Math.min(targetVolume, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)));
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, bounded, 0);
    }

    private void enableInnerHorn() {
        writePin("inner_audio", 0);
        writePin("outer_audio", 0);
        writePin("headphone_detect_power", 1);
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
