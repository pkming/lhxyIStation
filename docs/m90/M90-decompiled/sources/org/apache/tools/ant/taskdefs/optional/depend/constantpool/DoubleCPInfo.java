package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public class DoubleCPInfo extends ConstantCPInfo {
    public DoubleCPInfo() {
        super(6, 2);
    }

    @Override // org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry
    public void read(DataInputStream dataInputStream) throws IOException {
        setValue(new Double(dataInputStream.readDouble()));
    }

    public String toString() {
        return "Double Constant Pool Entry: " + getValue();
    }
}
