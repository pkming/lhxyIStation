package com.unisound.client;

import com.unisound.common.VoiceprintResult;

/* JADX INFO: loaded from: classes2.dex */
public interface VoicePrintRecognizerListener {
    void onError(int i, String str);

    int onEvent(int i, int i2);

    void onResult(int i, VoiceprintResult voiceprintResult);
}
