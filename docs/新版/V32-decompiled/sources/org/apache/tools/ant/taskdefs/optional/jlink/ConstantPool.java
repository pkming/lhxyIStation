package org.apache.tools.ant.taskdefs.optional.jlink;

import java.io.DataInput;
import java.io.IOException;

/* JADX INFO: compiled from: ClassNameReader.java */
/* JADX INFO: loaded from: classes3.dex */
class ConstantPool {
    static final byte CLASS = 7;
    static final byte DOUBLE = 6;
    static final byte FIELDREF = 9;
    static final byte FLOAT = 4;
    static final byte INTEGER = 3;
    static final byte INTERFACEMETHODREF = 11;
    static final byte LONG = 5;
    static final byte METHODREF = 10;
    static final byte NAMEANDTYPE = 12;
    static final byte STRING = 8;
    static final byte UNUSED = 2;
    static final byte UTF8 = 1;
    byte[] types;
    Object[] values;

    ConstantPool(DataInput dataInput) throws IOException {
        int unsignedShort = dataInput.readUnsignedShort();
        this.types = new byte[unsignedShort];
        this.values = new Object[unsignedShort];
        int i = 1;
        while (i < unsignedShort) {
            byte b = dataInput.readByte();
            this.types[i] = b;
            switch (b) {
                case 1:
                    this.values[i] = dataInput.readUTF();
                    continue;
                    i++;
                    break;
                case 2:
                default:
                    i++;
                    break;
                case 3:
                    this.values[i] = new Integer(dataInput.readInt());
                    continue;
                    i++;
                    break;
                case 4:
                    this.values[i] = new Float(dataInput.readFloat());
                    continue;
                    i++;
                    break;
                case 5:
                    this.values[i] = new Long(dataInput.readLong());
                    break;
                case 6:
                    this.values[i] = new Double(dataInput.readDouble());
                    break;
                case 7:
                case 8:
                    this.values[i] = new Integer(dataInput.readUnsignedShort());
                    continue;
                    i++;
                    break;
                case 9:
                case 10:
                case 11:
                case 12:
                    this.values[i] = new Integer(dataInput.readInt());
                    continue;
                    i++;
                    break;
            }
            i++;
            i++;
        }
    }
}
