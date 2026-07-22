package android.hardware.camera2.impl;

import android.hardware.camera2.Size;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes.dex */
public class MetadataMarshalSize implements MetadataMarshalClass<Size> {
    private static final int SIZE = 8;

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public int getNativeSize(int i) {
        return 8;
    }

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public boolean isNativeTypeSupported(int i) {
        return i == 1;
    }

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public int marshal(Size size, ByteBuffer byteBuffer, int i, boolean z) {
        if (z) {
            return 8;
        }
        byteBuffer.putInt(size.getWidth());
        byteBuffer.putInt(size.getHeight());
        return 8;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public Size unmarshal(ByteBuffer byteBuffer, int i) {
        return new Size(byteBuffer.getInt(), byteBuffer.getInt());
    }

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public Class<Size> getMarshalingClass() {
        return Size.class;
    }
}
