package android.hardware.camera2.impl;

import android.graphics.Rect;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes.dex */
public class MetadataMarshalRect implements MetadataMarshalClass<Rect> {
    private static final int SIZE = 16;

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public int getNativeSize(int i) {
        return 16;
    }

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public boolean isNativeTypeSupported(int i) {
        return i == 1;
    }

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public int marshal(Rect rect, ByteBuffer byteBuffer, int i, boolean z) {
        if (z) {
            return 16;
        }
        byteBuffer.putInt(rect.left);
        byteBuffer.putInt(rect.top);
        byteBuffer.putInt(rect.width());
        byteBuffer.putInt(rect.height());
        return 16;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public Rect unmarshal(ByteBuffer byteBuffer, int i) {
        int i2 = byteBuffer.getInt();
        int i3 = byteBuffer.getInt();
        return new Rect(i2, i3, byteBuffer.getInt() + i2, byteBuffer.getInt() + i3);
    }

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public Class<Rect> getMarshalingClass() {
        return Rect.class;
    }
}
