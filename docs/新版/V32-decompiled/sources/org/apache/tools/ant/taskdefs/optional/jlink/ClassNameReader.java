package org.apache.tools.ant.taskdefs.optional.jlink;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes3.dex */
public class ClassNameReader {
    private static final int CLASS_MAGIC_NUMBER = -889275714;

    public static String getClassName(InputStream inputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        if (dataInputStream.readInt() != CLASS_MAGIC_NUMBER) {
            return null;
        }
        dataInputStream.readInt();
        Object[] objArr = new ConstantPool(dataInputStream).values;
        dataInputStream.readUnsignedShort();
        return (String) objArr[((Integer) objArr[dataInputStream.readUnsignedShort()]).intValue()];
    }
}
