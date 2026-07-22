package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public class Utf8CPInfo extends ConstantPoolEntry {
    private String value;

    public Utf8CPInfo() {
        super(1, 1);
    }

    @Override // org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry
    public void read(DataInputStream dataInputStream) throws IOException {
        this.value = dataInputStream.readUTF();
    }

    public String toString() {
        return "UTF8 Value = " + this.value;
    }

    public String getValue() {
        return this.value;
    }
}
