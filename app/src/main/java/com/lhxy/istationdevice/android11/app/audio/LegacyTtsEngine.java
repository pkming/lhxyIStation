package com.lhxy.istationdevice.android11.app.audio;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;

import java.util.Locale;

/** Shared wrapper for the M90 built-in Android TTS engine. */
public final class LegacyTtsEngine {
    private static final String TAG = "LegacyTtsEngine";
    private static final String PREFERRED_ENGINE = "com.iflytek.speechsuite";
    private static LegacyTtsEngine instance;

    private final Context appContext;
    private TextToSpeech textToSpeech;
    private boolean ready;
    private boolean defaultEngineTried;
    private PendingSpeech activeSpeech;

    public interface Listener {
        void onDone();

        void onError();

        void onStop();
    }

    private LegacyTtsEngine(Context context) {
        appContext = context.getApplicationContext();
        create(PREFERRED_ENGINE);
    }

    public static LegacyTtsEngine get(Context context) {
        synchronized (LegacyTtsEngine.class) {
            if (instance == null) {
                instance = new LegacyTtsEngine(context);
            }
            return instance;
        }
    }

    public static void preload(Context context) {
        get(context);
    }

    public synchronized boolean speak(String text, String utteranceId, Listener listener) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        PendingSpeech request = new PendingSpeech(text, utteranceId, listener);
        if (!ready || textToSpeech == null) {
            log(LogLevel.WARN, "TTS not ready; speech ignored: " + text, "tts-speak");
            return false;
        }
        return speakNow(request);
    }

    public synchronized void stop() {
        activeSpeech = null;
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }

    private void create(String engine) {
        boolean specific = engine != null && !engine.trim().isEmpty();
        log(LogLevel.INFO, "initializing TTS engine=" + (specific ? engine : "default"), "tts-init");
        TextToSpeech.OnInitListener listener = status -> onInitialized(status, engine, specific);
        textToSpeech = specific
                ? new TextToSpeech(appContext, listener, engine)
                : new TextToSpeech(appContext, listener);
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
            }

            @Override
            public void onDone(String utteranceId) {
                dispatch(utteranceId, 0);
            }

            @Override
            public void onError(String utteranceId) {
                dispatch(utteranceId, 1);
            }

            @Override
            public void onStop(String utteranceId, boolean interrupted) {
                dispatch(utteranceId, 2);
            }
        });
    }

    private synchronized void onInitialized(int status, String engine, boolean specific) {
        ready = status == TextToSpeech.SUCCESS;
        if (!ready) {
            log(LogLevel.WARN, "TTS init failed status=" + status + " engine=" + (specific ? engine : "default"), "tts-init");
            if (specific && !defaultEngineTried) {
                defaultEngineTried = true;
                if (textToSpeech != null) {
                    textToSpeech.shutdown();
                }
                textToSpeech = null;
                create(null);
            }
            return;
        }
        int language = textToSpeech.setLanguage(Locale.CHINA);
        textToSpeech.setPitch(1.0f);
        textToSpeech.setSpeechRate(1.0f);
        if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {
            log(LogLevel.WARN, "TTS Chinese language unavailable result=" + language, "tts-init");
        }
        log(LogLevel.INFO, "TTS init success engine=" + (specific ? engine : "default"), "tts-init");
    }

    private boolean speakNow(PendingSpeech request) {
        textToSpeech.stop();
        activeSpeech = request;
        int result = textToSpeech.speak(request.text, TextToSpeech.QUEUE_FLUSH, (Bundle) null, request.utteranceId);
        log(result == TextToSpeech.SUCCESS ? LogLevel.INFO : LogLevel.WARN,
                "TTS speak result=" + result + " text=" + request.text, "tts-speak");
        return result == TextToSpeech.SUCCESS;
    }

    private synchronized void dispatch(String utteranceId, int event) {
        PendingSpeech request = activeSpeech;
        if (request == null || !request.utteranceId.equals(utteranceId)) {
            return;
        }
        activeSpeech = null;
        if (request.listener == null) {
            return;
        }
        if (event == 0) {
            request.listener.onDone();
        } else if (event == 1) {
            request.listener.onError();
        } else {
            request.listener.onStop();
        }
    }

    private void log(LogLevel level, String message, String traceId) {
        AppLogCenter.log(LogCategory.BIZ, level, TAG, message, traceId);
    }

    private static final class PendingSpeech {
        private final String text;
        private final String utteranceId;
        private final Listener listener;

        private PendingSpeech(String text, String utteranceId, Listener listener) {
            this.text = text;
            this.utteranceId = utteranceId;
            this.listener = listener;
        }
    }
}
