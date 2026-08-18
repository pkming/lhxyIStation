package com.unisound.sdk;

import android.content.Context;
import android.os.Message;
import com.unisound.client.SpeechConstants;
import com.unisound.client.TextUnderstanderListener;

/* JADX INFO: loaded from: classes2.dex */
public class bp extends com.unisound.common.u {
    private static final int a = 100;
    private static final int b = 101;
    private static final int c = 1000;
    private static final int d = 1001;
    private TextUnderstanderListener e;
    private ca f;
    private bz g;
    private by h = new bq(this);

    protected bp(Context context, String str, String str2) {
        com.unisound.common.k.a(context);
        bz bzVar = new bz(str, str2);
        this.g = bzVar;
        bzVar.e(com.unisound.common.k.x);
        this.g.g(com.unisound.common.k.s);
    }

    private void b(String str) {
        ca caVar = this.f;
        if (caVar != null) {
            caVar.c();
        }
        ca caVar2 = new ca(this.g);
        this.f = caVar2;
        caVar2.a(str);
        this.f.a(this.h);
        this.f.start();
    }

    protected void a(int i) {
        TextUnderstanderListener textUnderstanderListener = this.e;
        if (textUnderstanderListener != null) {
            textUnderstanderListener.onEvent(1001);
        }
    }

    protected void a(String str) {
        TextUnderstanderListener textUnderstanderListener = this.e;
        if (textUnderstanderListener != null) {
            textUnderstanderListener.onResult(1000, str);
        }
    }

    protected void cancel() {
        ca caVar = this.f;
        if (caVar != null) {
            caVar.c();
            this.f = null;
        }
    }

    protected Object getOption(int i) {
        if (i == 1036) {
            return com.unisound.common.k.x;
        }
        switch (i) {
            case 1021:
                return this.g.n();
            case 1022:
                return this.g.s();
            case 1023:
                return this.g.w();
            default:
                switch (i) {
                    case 1030:
                        return this.g.i();
                    case SpeechConstants.GENERAL_CITY /* 1031 */:
                        return this.g.j();
                    case 1032:
                        return this.g.m();
                    case SpeechConstants.GENERAL_GPS /* 1033 */:
                        return this.g.f();
                    default:
                        return null;
                }
        }
    }

    @Override // com.unisound.common.u, android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 100) {
            a((String) message.obj);
        } else {
            if (i != 101) {
                return;
            }
            a(((Integer) message.obj).intValue());
        }
    }

    protected int init(String str) {
        return 0;
    }

    protected void setListener(TextUnderstanderListener textUnderstanderListener) {
        this.e = textUnderstanderListener;
    }

    protected void setOption(int i, Object obj) {
        String str;
        switch (i) {
            case 1021:
                try {
                    this.g.l((String) obj);
                    return;
                } catch (Exception unused) {
                    str = "set nlu_scenario Error.";
                }
                break;
            case 1022:
                try {
                    String str2 = (String) obj;
                    if (str2 != null && str2.contains(":")) {
                        String[] strArrSplit = str2.split(":");
                        String str3 = strArrSplit[0];
                        try {
                            this.g.a(str3, Integer.parseInt(strArrSplit[1]));
                            return;
                        } catch (NumberFormatException unused2) {
                        }
                    }
                    com.unisound.common.r.e("nlu server set Error.");
                    return;
                } catch (Exception unused3) {
                    str = "set nlu_server_address Error.";
                }
                break;
            case 1023:
                try {
                    this.g.a((bz) obj);
                    return;
                } catch (Exception unused4) {
                    str = "set nlu_params Error.";
                }
                break;
            case 1024:
                try {
                    this.g.d(String.valueOf(obj));
                    return;
                } catch (Exception unused5) {
                    str = "set nlu_ver Error.";
                }
                break;
            case 1025:
                try {
                    this.g.g(String.valueOf(obj));
                    return;
                } catch (Exception unused6) {
                    str = "set nlu_appver Error.";
                }
                break;
            case 1026:
            case 1027:
            case 1028:
            case 1029:
            default:
                return;
            case 1030:
                try {
                    this.g.i((String) obj);
                    return;
                } catch (Exception unused7) {
                    str = "set history Error.";
                }
                break;
            case SpeechConstants.GENERAL_CITY /* 1031 */:
                try {
                    this.g.j((String) obj);
                    return;
                } catch (Exception unused8) {
                    str = "set city Error.";
                }
                break;
            case 1032:
                try {
                    this.g.k((String) obj);
                    return;
                } catch (Exception unused9) {
                    str = "set voiceID Error.";
                }
                break;
            case SpeechConstants.GENERAL_GPS /* 1033 */:
                try {
                    this.g.f((String) obj);
                    return;
                } catch (Exception unused10) {
                    str = "set gps Error.";
                }
                break;
        }
        com.unisound.common.r.e(str);
    }

    protected void setText(String str) {
        if (str == null || str.length() == 0) {
            sendMessage(100, "");
        } else {
            b(str);
        }
    }
}
