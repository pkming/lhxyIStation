package org.apache.commons.net.smtp;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import org.apache.commons.net.util.Base64;

/* JADX INFO: loaded from: classes3.dex */
public class AuthenticatingSMTPClient extends SMTPSClient {
    public AuthenticatingSMTPClient() throws NoSuchAlgorithmException {
    }

    public AuthenticatingSMTPClient(String str) throws NoSuchAlgorithmException {
        super(str);
    }

    public AuthenticatingSMTPClient(String str, boolean z) {
        super(str, z);
    }

    public AuthenticatingSMTPClient(String str, boolean z, String str2) {
        super(str, z, str2);
    }

    public AuthenticatingSMTPClient(boolean z, SSLContext sSLContext) {
        super(z, sSLContext);
    }

    public AuthenticatingSMTPClient(String str, String str2) throws NoSuchAlgorithmException {
        super(str, false, str2);
    }

    public int ehlo(String str) throws IOException {
        return sendCommand(15, str);
    }

    public boolean elogin(String str) throws IOException {
        return SMTPReply.isPositiveCompletion(ehlo(str));
    }

    public boolean elogin() throws IOException {
        String hostName = getLocalAddress().getHostName();
        if (hostName == null) {
            return false;
        }
        return SMTPReply.isPositiveCompletion(ehlo(hostName));
    }

    public int[] getEnhancedReplyCode() {
        String strSubstring = getReplyString().substring(4);
        String[] strArrSplit = strSubstring.substring(0, strSubstring.indexOf(32)).split("\\.");
        int[] iArr = new int[strArrSplit.length];
        for (int i = 0; i < strArrSplit.length; i++) {
            iArr[i] = Integer.parseInt(strArrSplit[i]);
        }
        return iArr;
    }

    public boolean auth(AUTH_METHOD auth_method, String str, String str2) throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        if (!SMTPReply.isPositiveIntermediate(sendCommand(14, AUTH_METHOD.getAuthName(auth_method)))) {
            return false;
        }
        if (auth_method.equals(AUTH_METHOD.PLAIN)) {
            return SMTPReply.isPositiveCompletion(sendCommand(Base64.encodeBase64StringUnChunked(("\u0000" + str + "\u0000" + str2).getBytes(getCharsetName()))));
        }
        if (auth_method.equals(AUTH_METHOD.CRAM_MD5)) {
            byte[] bArrDecodeBase64 = Base64.decodeBase64(getReplyString().substring(4).trim());
            Mac mac = Mac.getInstance("HmacMD5");
            mac.init(new SecretKeySpec(str2.getBytes(getCharsetName()), "HmacMD5"));
            byte[] bytes = _convertToHexString(mac.doFinal(bArrDecodeBase64)).getBytes(getCharsetName());
            byte[] bytes2 = str.getBytes(getCharsetName());
            byte[] bArr = new byte[bytes2.length + 1 + bytes.length];
            System.arraycopy(bytes2, 0, bArr, 0, bytes2.length);
            bArr[bytes2.length] = 32;
            System.arraycopy(bytes, 0, bArr, bytes2.length + 1, bytes.length);
            return SMTPReply.isPositiveCompletion(sendCommand(Base64.encodeBase64StringUnChunked(bArr)));
        }
        if (auth_method.equals(AUTH_METHOD.LOGIN)) {
            if (SMTPReply.isPositiveIntermediate(sendCommand(Base64.encodeBase64StringUnChunked(str.getBytes(getCharsetName()))))) {
                return SMTPReply.isPositiveCompletion(sendCommand(Base64.encodeBase64StringUnChunked(str2.getBytes(getCharsetName()))));
            }
            return false;
        }
        if (auth_method.equals(AUTH_METHOD.XOAUTH)) {
            return SMTPReply.isPositiveIntermediate(sendCommand(Base64.encodeBase64StringUnChunked(str.getBytes(getCharsetName()))));
        }
        return false;
    }

    private String _convertToHexString(byte[] bArr) {
        StringBuilder sb = new StringBuilder(bArr.length * 2);
        for (byte b : bArr) {
            int i = b & 255;
            if (i <= 15) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(i));
        }
        return sb.toString();
    }

    public enum AUTH_METHOD {
        PLAIN,
        CRAM_MD5,
        LOGIN,
        XOAUTH;

        public static final String getAuthName(AUTH_METHOD auth_method) {
            if (auth_method.equals(PLAIN)) {
                return "PLAIN";
            }
            if (auth_method.equals(CRAM_MD5)) {
                return "CRAM-MD5";
            }
            if (auth_method.equals(LOGIN)) {
                return "LOGIN";
            }
            if (auth_method.equals(XOAUTH)) {
                return "XOAUTH";
            }
            return null;
        }
    }
}
