package org.apache.tools.ant.types.selectors.modifiedselector;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Locale;
import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public class DigestAlgorithm implements Algorithm {
    private static final int BUFFER_SIZE = 8192;
    private static final int BYTE_MASK = 255;
    private String algorithm = "MD5";
    private String provider = null;
    private MessageDigest messageDigest = null;
    private int readBufferSize = 8192;

    public void setAlgorithm(String str) {
        this.algorithm = str != null ? str.toUpperCase(Locale.ENGLISH) : null;
    }

    public void setProvider(String str) {
        this.provider = str;
    }

    public void initMessageDigest() {
        if (this.messageDigest != null) {
            return;
        }
        String str = this.provider;
        if (str != null && !"".equals(str) && !"null".equals(this.provider)) {
            try {
                this.messageDigest = MessageDigest.getInstance(this.algorithm, this.provider);
                return;
            } catch (NoSuchAlgorithmException e) {
                throw new BuildException(e);
            } catch (NoSuchProviderException e2) {
                throw new BuildException(e2);
            }
        }
        try {
            this.messageDigest = MessageDigest.getInstance(this.algorithm);
        } catch (NoSuchAlgorithmException e3) {
            throw new BuildException(e3);
        }
    }

    @Override // org.apache.tools.ant.types.selectors.modifiedselector.Algorithm
    public boolean isValid() {
        return "SHA".equals(this.algorithm) || "MD5".equals(this.algorithm);
    }

    @Override // org.apache.tools.ant.types.selectors.modifiedselector.Algorithm
    public String getValue(File file) {
        int i;
        initMessageDigest();
        try {
            if (!file.canRead()) {
                return null;
            }
            byte[] bArr = new byte[this.readBufferSize];
            this.messageDigest.reset();
            FileInputStream fileInputStream = new FileInputStream(file);
            DigestInputStream digestInputStream = new DigestInputStream(fileInputStream, this.messageDigest);
            do {
            } while (digestInputStream.read(bArr, 0, this.readBufferSize) != -1);
            digestInputStream.close();
            fileInputStream.close();
            byte[] bArrDigest = this.messageDigest.digest();
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : bArrDigest) {
                String hexString = Integer.toHexString(b & 255);
                if (hexString.length() < 2) {
                    stringBuffer.append("0");
                }
                stringBuffer.append(hexString);
            }
            return stringBuffer.toString();
        } catch (Exception unused) {
            return null;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<DigestAlgorithm:");
        sb.append("algorithm=").append(this.algorithm);
        sb.append(";provider=").append(this.provider);
        sb.append(">");
        return sb.toString();
    }
}
