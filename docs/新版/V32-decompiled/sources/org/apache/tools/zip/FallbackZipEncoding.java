package org.apache.tools.zip;

import java.io.IOException;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes3.dex */
class FallbackZipEncoding implements ZipEncoding {
    private final String charset;

    @Override // org.apache.tools.zip.ZipEncoding
    public boolean canEncode(String str) {
        return true;
    }

    public FallbackZipEncoding() {
        this.charset = null;
    }

    public FallbackZipEncoding(String str) {
        this.charset = str;
    }

    @Override // org.apache.tools.zip.ZipEncoding
    public ByteBuffer encode(String str) throws IOException {
        String str2 = this.charset;
        if (str2 == null) {
            return ByteBuffer.wrap(str.getBytes());
        }
        return ByteBuffer.wrap(str.getBytes(str2));
    }

    @Override // org.apache.tools.zip.ZipEncoding
    public String decode(byte[] bArr) throws IOException {
        if (this.charset == null) {
            return new String(bArr);
        }
        return new String(bArr, this.charset);
    }
}
