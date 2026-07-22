package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/* JADX INFO: compiled from: HeaderAddStrategy.java */
/* JADX INFO: loaded from: classes2.dex */
public final class lp extends lt {
    private Context a;
    private String b;
    private kn e;
    private Object[] f;

    public lp(Context context, lt ltVar, kn knVar, String str, Object... objArr) {
        super(ltVar);
        this.a = context;
        this.b = str;
        this.e = knVar;
        this.f = objArr;
    }

    @Override // com.amap.api.col.p0003sl.lt
    protected final byte[] a(byte[] bArr) throws BadPaddingException, NoSuchPaddingException, InvalidKeySpecException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException, InvalidKeyException, CertificateException {
        String strA = it.a(bArr);
        if (TextUtils.isEmpty(strA)) {
            return null;
        }
        String strA2 = it.a(this.e.b(it.a(b())));
        StringBuilder sb = new StringBuilder();
        sb.append("{\"pinfo\":\"").append(strA2).append("\",\"els\":[");
        sb.append(strA);
        sb.append("]}");
        return it.a(sb.toString());
    }

    private String b() {
        try {
            return String.format(it.c(this.b), this.f);
        } catch (Throwable th) {
            th.printStackTrace();
            jw.c(th, "ofm", "gpj");
            return "";
        }
    }
}
