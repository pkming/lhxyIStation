package android.hardware.camera2.impl;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/* JADX INFO: loaded from: classes.dex */
public class MetadataMarshalString implements MetadataMarshalClass<String> {
    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public int getNativeSize(int i) {
        return -1;
    }

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public boolean isNativeTypeSupported(int i) {
        return i == 0;
    }

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public int marshal(String str, ByteBuffer byteBuffer, int i, boolean z) {
        byte[] bytes = str.getBytes(UTF8_CHARSET);
        if (!z) {
            byteBuffer.put(bytes);
            byteBuffer.put((byte) 0);
        }
        return bytes.length + 1;
    }

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public String unmarshal(ByteBuffer byteBuffer, int i) {
        boolean z;
        byteBuffer.mark();
        int i2 = 0;
        while (true) {
            if (!byteBuffer.hasRemaining()) {
                z = false;
                break;
            }
            if (byteBuffer.get() == 0) {
                z = true;
                break;
            }
            i2++;
        }
        if (!z) {
            throw new IllegalArgumentException("Strings must be null-terminated");
        }
        byteBuffer.reset();
        int i3 = i2 + 1;
        byte[] bArr = new byte[i3];
        byteBuffer.get(bArr, 0, i3);
        return new String(bArr, 0, i2, UTF8_CHARSET);
    }

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public Class<String> getMarshalingClass() {
        return String.class;
    }
}
