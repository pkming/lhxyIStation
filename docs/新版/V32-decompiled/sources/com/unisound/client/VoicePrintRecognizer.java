package com.unisound.client;

import android.content.Context;
import com.unisound.sdk.ci;

/* JADX INFO: loaded from: classes2.dex */
public class VoicePrintRecognizer extends ci {
    public VoicePrintRecognizer(Context context, String str, String str2) {
        super(context, str, str2);
    }

    @Override // com.unisound.sdk.ci
    public void cancel() {
        super.cancel();
    }

    @Override // com.unisound.sdk.ci
    public Object getOption(int i) {
        return super.getOption(i);
    }

    @Override // com.unisound.sdk.ci
    public int init(String str) {
        return super.init(str);
    }

    @Override // com.unisound.sdk.ci
    public int setAudioSource(IAudioSource iAudioSource) {
        return super.setAudioSource(iAudioSource);
    }

    @Override // com.unisound.sdk.ci
    public void setListener(VoicePrintRecognizerListener voicePrintRecognizerListener) {
        super.setListener(voicePrintRecognizerListener);
    }

    @Override // com.unisound.sdk.ci
    public void setOption(int i, Object obj) {
        super.setOption(i, obj);
    }

    @Override // com.unisound.sdk.ci
    public void start(String str, int i) {
        super.start(str, i);
    }

    @Override // com.unisound.sdk.ci
    public void stop() {
        super.stop();
    }
}
