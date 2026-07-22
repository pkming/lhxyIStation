package com.unisound.common;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/* JADX INFO: loaded from: classes2.dex */
public class u extends Handler {
    private v a;

    public u() {
    }

    public u(Looper looper) {
        super(looper);
    }

    protected boolean a(Message message) {
        return true;
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        v vVar;
        super.handleMessage(message);
        if (a(message) || (vVar = this.a) == null) {
            return;
        }
        vVar.a(message);
    }

    public void removeSendMessage() {
        removeCallbacksAndMessages(null);
    }

    public void sendMessage(int i) {
        sendEmptyMessage(i);
    }

    public void sendMessage(int i, Object obj) {
        obtainMessage(i, obj).sendToTarget();
    }

    public void setMessageLisenter(v vVar) {
        this.a = vVar;
    }
}
