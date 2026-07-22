package com.unisound.sdk;

import com.unisound.client.ErrorCode;

/* JADX INFO: loaded from: classes2.dex */
class ak implements ac {
    final /* synthetic */ aj a;

    ak(aj ajVar) {
        this.a = ajVar;
    }

    @Override // com.unisound.sdk.ac
    public void a() {
        com.unisound.common.r.e("Recognizer timeout(" + this.a.q.a() + ")");
        com.unisound.common.r.c("Ontimer:cancelRecognition()");
        this.a.d(true);
        if (this.a.i != null) {
            this.a.i.b(ErrorCode.RECOGNITION_TIMEOUT);
        }
    }
}
