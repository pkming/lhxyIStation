package com.lianhexinye.m90.tts;

/* JADX INFO: loaded from: classes2.dex */
public interface TTS {
    void destroy();

    void init();

    boolean isPlaying();

    void playText(String str);

    void setCallback(ICallBack iCallBack);

    void stopSpeak();
}
