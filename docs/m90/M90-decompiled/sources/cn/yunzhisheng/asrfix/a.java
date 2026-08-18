package cn.yunzhisheng.asrfix;

import com.unisound.client.SpeechConstants;
import com.unisound.common.r;
import com.unisound.sdk.cn;
import com.unisound.sdk.s;
import com.unisound.sdk.u;

/* JADX INFO: loaded from: classes2.dex */
class a extends Thread {
    final /* synthetic */ JniAsrFix a;
    private u b;

    public a(JniAsrFix jniAsrFix, u uVar) {
        this.a = jniAsrFix;
        this.b = uVar;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        s sVar;
        long jCurrentTimeMillis;
        r.g("Recognizer.loadModel Reset thread start");
        r.c("Recognizer.loadModel Reset thread start");
        this.a.Q = true;
        synchronized (JniAsrFix.C) {
            this.a.P = false;
            while (!this.a.P) {
                if (!this.a.K) {
                    this.a.L = SpeechConstants.ASR_STATUS_LOCAL_RESET;
                    this.a.M.a(this.a.L);
                    while (!this.a.A.isEmpty()) {
                        String strPoll = this.a.A.poll();
                        String strPoll2 = this.a.A.poll();
                        String strPoll3 = this.a.A.poll();
                        r.c("Recognizer.loadModel reseting " + strPoll + " " + strPoll2);
                        int iReset = this.a.reset(strPoll, "");
                        this.a.L = SpeechConstants.ASR_STATUS_LOCAL_IDEL;
                        int i = SpeechConstants.ASR_EVENT_LOADGRAMMAR_DONE;
                        if (iReset == 0) {
                            this.a.M.a(this.a.L);
                            if (cn.a == strPoll2) {
                                sVar = this.a.M;
                                i = SpeechConstants.WAKEUP_EVENT_SET_WAKEUPWORD_DONE;
                                jCurrentTimeMillis = System.currentTimeMillis();
                            } else if ("command" == strPoll2) {
                                this.b.a(strPoll3, true);
                                sVar = this.a.M;
                                jCurrentTimeMillis = System.currentTimeMillis();
                            } else {
                                r.e("Recognizer.loadModel no cmd type error");
                                r.c("Recognizer.loadModel reset ok");
                            }
                            sVar.a(i, (int) jCurrentTimeMillis);
                            r.c("Recognizer.loadModel reset ok");
                        } else {
                            if ("command" == strPoll2) {
                                this.b.a(strPoll3, false);
                                this.a.M.a(SpeechConstants.ASR_EVENT_LOADGRAMMAR_DONE, (int) System.currentTimeMillis());
                            }
                            this.a.M.a(SpeechConstants.ASR_ERROR_LOADMODEL_FAIL, "error code = " + iReset);
                            r.e("Recognizer.loadModel reset error:" + iReset);
                        }
                    }
                    this.a.Q = false;
                    this.a.P = true;
                    this.a.L = SpeechConstants.ASR_STATUS_LOCAL_IDEL;
                }
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        r.c("Recognizer.loadModel Reset thread stop");
        r.g("Recognizer.loadModel Reset thread stop");
    }
}
