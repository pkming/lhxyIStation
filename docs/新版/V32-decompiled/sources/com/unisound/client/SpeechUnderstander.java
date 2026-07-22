package com.unisound.client;

import android.content.Context;
import com.unisound.sdk.bb;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class SpeechUnderstander extends bb {
    public SpeechUnderstander(Context context, String str, String str2) {
        super(context, str, str2);
    }

    @Override // com.unisound.sdk.bb, com.unisound.sdk.m
    public void cancel() {
        super.cancel();
    }

    @Override // com.unisound.sdk.bb, com.unisound.sdk.m
    public Object getOption(int i) {
        return super.getOption(i);
    }

    @Override // com.unisound.sdk.bb, com.unisound.sdk.m
    public String getVersion() {
        return super.getVersion();
    }

    @Override // com.unisound.sdk.bb
    public int init(String str) {
        return super.init(str);
    }

    @Override // com.unisound.sdk.bb, com.unisound.sdk.m
    public int setAudioSource(IAudioSource iAudioSource) {
        return super.setAudioSource(iAudioSource);
    }

    @Override // com.unisound.sdk.bb
    public void setListener(SpeechUnderstanderListener speechUnderstanderListener) {
        super.setListener(speechUnderstanderListener);
    }

    @Override // com.unisound.sdk.bb
    public String setOnlineWakeupWord(List<String> list) {
        return super.setOnlineWakeupWord(list);
    }

    @Override // com.unisound.sdk.bb, com.unisound.sdk.m
    public void setOption(int i, Object obj) {
        super.setOption(i, obj);
    }

    @Override // com.unisound.sdk.bb, com.unisound.sdk.m
    public void start() {
        super.start();
    }

    @Override // com.unisound.sdk.bb, com.unisound.sdk.m
    public void stop() {
        super.stop();
    }

    @Override // com.unisound.sdk.bb
    public void uploadUserData(Map<Integer, List<String>> map) {
        super.uploadUserData(map);
    }
}
