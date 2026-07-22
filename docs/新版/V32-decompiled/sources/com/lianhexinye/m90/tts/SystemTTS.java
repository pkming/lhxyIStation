package com.lianhexinye.m90.tts;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import java.util.HashMap;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class SystemTTS extends UtteranceProgressListener implements TTS {
    private static SystemTTS singleton;
    ICallBack callBack = null;
    private boolean isSuccess = true;
    private Context mContext;
    private TextToSpeech textToSpeech;

    @Override // com.lianhexinye.m90.tts.TTS
    public void init() {
    }

    @Override // android.speech.tts.UtteranceProgressListener
    public void onStart(String str) {
    }

    public static SystemTTS getInstance(Context context) {
        if (singleton == null) {
            synchronized (SystemTTS.class) {
                if (singleton == null) {
                    singleton = new SystemTTS(context);
                }
            }
        }
        return singleton;
    }

    private SystemTTS(Context context) {
        this.mContext = context.getApplicationContext();
        this.textToSpeech = new TextToSpeech(this.mContext, new TextToSpeech.OnInitListener() { // from class: com.lianhexinye.m90.tts.SystemTTS.1
            @Override // android.speech.tts.TextToSpeech.OnInitListener
            public void onInit(int i) {
                if (i == 0) {
                    int language = SystemTTS.this.textToSpeech.setLanguage(Locale.CHINA);
                    SystemTTS.this.textToSpeech.setPitch(1.0f);
                    SystemTTS.this.textToSpeech.setSpeechRate(1.0f);
                    SystemTTS.this.textToSpeech.setOnUtteranceProgressListener(SystemTTS.this);
                    if (language == -1 || language == -2) {
                        SystemTTS.this.isSuccess = false;
                    }
                }
            }
        }, "Test");
    }

    @Override // com.lianhexinye.m90.tts.TTS
    public void destroy() {
        stopSpeak();
        TextToSpeech textToSpeech = this.textToSpeech;
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
        singleton = null;
    }

    @Override // com.lianhexinye.m90.tts.TTS
    public void playText(String str) {
        if (this.isSuccess && this.textToSpeech != null) {
            HashMap<String, String> map = new HashMap<>();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utterance");
            this.textToSpeech.speak(str, 1, map);
        }
    }

    @Override // com.lianhexinye.m90.tts.TTS
    public void stopSpeak() {
        TextToSpeech textToSpeech = this.textToSpeech;
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }

    @Override // com.lianhexinye.m90.tts.TTS
    public boolean isPlaying() {
        return this.textToSpeech.isSpeaking();
    }

    @Override // com.lianhexinye.m90.tts.TTS
    public void setCallback(ICallBack iCallBack) {
        this.callBack = iCallBack;
    }

    @Override // android.speech.tts.UtteranceProgressListener
    public void onDone(String str) {
        this.callBack.onCompleted(0);
    }

    @Override // android.speech.tts.UtteranceProgressListener
    public void onError(String str) {
        LogUtils.d("SystemTTS", "onError:" + str);
    }
}
