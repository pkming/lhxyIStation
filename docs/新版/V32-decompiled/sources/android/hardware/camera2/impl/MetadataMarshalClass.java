package android.hardware.camera2.impl;

import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes.dex */
public interface MetadataMarshalClass<T> {
    public static final int NATIVE_SIZE_DYNAMIC = -1;

    Class<T> getMarshalingClass();

    int getNativeSize(int i);

    boolean isNativeTypeSupported(int i);

    int marshal(T t, ByteBuffer byteBuffer, int i, boolean z);

    T unmarshal(ByteBuffer byteBuffer, int i);
}
