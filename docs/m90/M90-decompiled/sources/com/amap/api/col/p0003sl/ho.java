package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import com.amap.api.col.p0003sl.ip;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.interfaces.IInputtipsSearch;
import java.util.ArrayList;

/* JADX INFO: compiled from: InputtipsSearchCore.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ho implements IInputtipsSearch {
    private Context a;
    private Inputtips.InputtipsListener b;
    private Handler c;
    private InputtipsQuery d;

    public ho(Context context, Inputtips.InputtipsListener inputtipsListener) throws AMapException {
        iq iqVarA = ip.a(context, fo.a(false));
        if (iqVarA.a != ip.c.SuccessCode) {
            throw new AMapException(iqVarA.b, 1, iqVarA.b, iqVarA.a.a());
        }
        this.a = context.getApplicationContext();
        this.b = inputtipsListener;
        this.c = ga.a();
    }

    public ho(Context context, InputtipsQuery inputtipsQuery) {
        this.a = context.getApplicationContext();
        this.d = inputtipsQuery;
        this.c = ga.a();
    }

    @Override // com.amap.api.services.interfaces.IInputtipsSearch
    public final InputtipsQuery getQuery() {
        return this.d;
    }

    @Override // com.amap.api.services.interfaces.IInputtipsSearch
    public final void setQuery(InputtipsQuery inputtipsQuery) {
        this.d = inputtipsQuery;
    }

    @Override // com.amap.api.services.interfaces.IInputtipsSearch
    public final void setInputtipsListener(Inputtips.InputtipsListener inputtipsListener) {
        this.b = inputtipsListener;
    }

    @Override // com.amap.api.services.interfaces.IInputtipsSearch
    public final void requestInputtipsAsyn() {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.ho.1
                @Override // java.lang.Runnable
                public final void run() {
                    Message messageObtainMessage = ga.a().obtainMessage();
                    messageObtainMessage.obj = ho.this.b;
                    messageObtainMessage.arg1 = 5;
                    try {
                        try {
                            ho hoVar = ho.this;
                            ArrayList<? extends Parcelable> arrayListA = hoVar.a(hoVar.d);
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList("result", arrayListA);
                            messageObtainMessage.setData(bundle);
                            messageObtainMessage.what = 1000;
                        } catch (AMapException e) {
                            messageObtainMessage.what = e.getErrorCode();
                        }
                    } finally {
                        ho.this.c.sendMessage(messageObtainMessage);
                    }
                }
            });
        } catch (Throwable th) {
            fp.a(th, "Inputtips", "requestInputtipsAsynThrowable");
        }
    }

    @Override // com.amap.api.services.interfaces.IInputtipsSearch
    public final ArrayList<Tip> requestInputtips() throws AMapException {
        return a(this.d);
    }

    @Override // com.amap.api.services.interfaces.IInputtipsSearch
    public final void requestInputtips(String str, String str2) throws AMapException {
        requestInputtips(str, str2, null);
    }

    @Override // com.amap.api.services.interfaces.IInputtipsSearch
    public final void requestInputtips(String str, String str2, String str3) throws AMapException {
        if (str == null || str.equals("")) {
            throw new AMapException("无效的参数 - IllegalArgumentException");
        }
        InputtipsQuery inputtipsQuery = new InputtipsQuery(str, str2);
        this.d = inputtipsQuery;
        inputtipsQuery.setType(str3);
        requestInputtipsAsyn();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<Tip> a(InputtipsQuery inputtipsQuery) throws AMapException {
        try {
            fy.a(this.a);
            if (inputtipsQuery == null) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            if (inputtipsQuery.getKeyword() == null || inputtipsQuery.getKeyword().equals("")) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            return new fw(this.a, inputtipsQuery).d();
        } catch (Throwable th) {
            fp.a(th, "Inputtips", "requestInputtips");
            if (th instanceof AMapException) {
                throw th;
            }
            return null;
        }
    }
}
