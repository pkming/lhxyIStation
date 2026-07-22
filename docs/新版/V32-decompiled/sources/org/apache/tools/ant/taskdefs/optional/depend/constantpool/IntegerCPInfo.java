package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public class IntegerCPInfo extends ConstantCPInfo {
    public IntegerCPInfo() {
        super(3, 1);
    }

    @Override // org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry
    public void read(DataInputStream dataInputStream) throws IOException {
        setValue(new Integer(dataInputStream.readInt()));
    }

    public String toString() {
        return "Integer Constant Pool Entry: " + getValue();
    }
}
