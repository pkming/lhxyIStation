package com.amap.api.col.p0003sl;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/* JADX INFO: compiled from: RSAAESEncryptProcessor.java */
/* JADX INFO: loaded from: classes2.dex */
public final class kp extends kn {
    public kp() {
    }

    public kp(kn knVar) {
        super(knVar);
    }

    @Override // com.amap.api.col.p0003sl.kn
    protected final byte[] a(byte[] bArr) throws BadPaddingException, NoSuchPaddingException, InvalidKeySpecException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException, InvalidKeyException, CertificateException {
        return il.a(bArr);
    }
}
