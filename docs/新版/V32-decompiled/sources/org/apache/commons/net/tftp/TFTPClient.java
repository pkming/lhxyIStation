package org.apache.commons.net.tftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;

/* JADX INFO: loaded from: classes3.dex */
public class TFTPClient extends TFTP {
    public static final int DEFAULT_MAX_TIMEOUTS = 5;
    private int __maxTimeouts = 5;

    public void setMaxTimeouts(int i) {
        if (i < 1) {
            this.__maxTimeouts = 1;
        } else {
            this.__maxTimeouts = i;
        }
    }

    public int getMaxTimeouts() {
        return this.__maxTimeouts;
    }

    /* JADX WARN: Code restructure failed: missing block: B:41:0x00e8, code lost:
    
        bufferedSend(new org.apache.commons.net.tftp.TFTPErrorPacket(r13.getAddress(), r13.getPort(), 5, "Unexpected host or port."));
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x0134, code lost:
    
        if (r12 == 512) goto L57;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x0136, code lost:
    
        bufferedSend(r8);
        endBufferedOps();
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x013c, code lost:
    
        return r11;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int receiveFile(java.lang.String r17, int r18, java.io.OutputStream r19, java.net.InetAddress r20, int r21) throws java.io.IOException {
        /*
            Method dump skipped, instruction units count: 329
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.net.tftp.TFTPClient.receiveFile(java.lang.String, int, java.io.OutputStream, java.net.InetAddress, int):int");
    }

    public int receiveFile(String str, int i, OutputStream outputStream, String str2, int i2) throws IOException {
        return receiveFile(str, i, outputStream, InetAddress.getByName(str2), i2);
    }

    public int receiveFile(String str, int i, OutputStream outputStream, InetAddress inetAddress) throws IOException {
        return receiveFile(str, i, outputStream, inetAddress, 69);
    }

    public int receiveFile(String str, int i, OutputStream outputStream, String str2) throws IOException {
        return receiveFile(str, i, outputStream, InetAddress.getByName(str2), 69);
    }

    /* JADX WARN: Code restructure failed: missing block: B:41:0x00e9, code lost:
    
        bufferedSend(new org.apache.commons.net.tftp.TFTPErrorPacket(r13.getAddress(), r13.getPort(), 5, "Unexpected host or port."));
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x0133, code lost:
    
        if (r12 > 0) goto L68;
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x0135, code lost:
    
        if (r11 != false) goto L69;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void sendFile(java.lang.String r17, int r18, java.io.InputStream r19, java.net.InetAddress r20, int r21) throws java.io.IOException {
        /*
            Method dump skipped, instruction units count: 324
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.net.tftp.TFTPClient.sendFile(java.lang.String, int, java.io.InputStream, java.net.InetAddress, int):void");
    }

    public void sendFile(String str, int i, InputStream inputStream, String str2, int i2) throws IOException {
        sendFile(str, i, inputStream, InetAddress.getByName(str2), i2);
    }

    public void sendFile(String str, int i, InputStream inputStream, InetAddress inetAddress) throws IOException {
        sendFile(str, i, inputStream, inetAddress, 69);
    }

    public void sendFile(String str, int i, InputStream inputStream, String str2) throws IOException {
        sendFile(str, i, inputStream, InetAddress.getByName(str2), 69);
    }
}
