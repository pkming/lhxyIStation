package org.apache.tools.zip;

import java.io.IOException;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes3.dex */
public interface ZipEncoding {
    boolean canEncode(String str);

    String decode(byte[] bArr) throws IOException;

    ByteBuffer encode(String str) throws IOException;
}
