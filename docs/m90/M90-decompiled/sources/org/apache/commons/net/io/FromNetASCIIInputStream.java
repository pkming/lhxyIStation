package org.apache.commons.net.io;

import android.media.MediaPlayer;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;

/* JADX INFO: loaded from: classes3.dex */
public final class FromNetASCIIInputStream extends PushbackInputStream {
    static final String _lineSeparator;
    static final byte[] _lineSeparatorBytes;
    static final boolean _noConversionRequired;
    private int __length;

    static {
        String property = System.getProperty("line.separator");
        _lineSeparator = property;
        _noConversionRequired = property.equals("\r\n");
        try {
            _lineSeparatorBytes = property.getBytes(MediaPlayer.CHARSET_US_ASCII);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Broken JVM - cannot find US-ASCII charset!", e);
        }
    }

    public static final boolean isConversionRequired() {
        return !_noConversionRequired;
    }

    public FromNetASCIIInputStream(InputStream inputStream) {
        super(inputStream, _lineSeparatorBytes.length + 1);
        this.__length = 0;
    }

    private int __read() throws IOException {
        int i = super.read();
        if (i != 13) {
            return i;
        }
        int i2 = super.read();
        if (i2 != 10) {
            if (i2 != -1) {
                unread(i2);
            }
            return 13;
        }
        unread(_lineSeparatorBytes);
        this.__length--;
        return super.read();
    }

    @Override // java.io.PushbackInputStream, java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        if (_noConversionRequired) {
            return super.read();
        }
        return __read();
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] bArr) throws IOException {
        return read(bArr, 0, bArr.length);
    }

    @Override // java.io.PushbackInputStream, java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        int i3;
        if (_noConversionRequired) {
            return super.read(bArr, i, i2);
        }
        if (i2 < 1) {
            return 0;
        }
        int iAvailable = available();
        if (i2 > iAvailable) {
            i2 = iAvailable;
        }
        this.__length = i2;
        if (i2 < 1) {
            this.__length = 1;
        }
        int i__read = __read();
        if (i__read == -1) {
            return -1;
        }
        int i4 = i;
        while (true) {
            i3 = i4 + 1;
            bArr[i4] = (byte) i__read;
            int i5 = this.__length - 1;
            this.__length = i5;
            if (i5 <= 0 || (i__read = __read()) == -1) {
                break;
            }
            i4 = i3;
        }
        return i3 - i;
    }

    @Override // java.io.PushbackInputStream, java.io.FilterInputStream, java.io.InputStream
    public int available() throws IOException {
        if (this.in == null) {
            throw new IOException("Stream closed");
        }
        return (this.buf.length - this.pos) + this.in.available();
    }
}
