package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes3.dex */
public class ConstantPool {
    private final List<ConstantPoolEntry> entries;
    private final Map<String, Integer> utf8Indexes;

    public ConstantPool() {
        ArrayList arrayList = new ArrayList();
        this.entries = arrayList;
        this.utf8Indexes = new HashMap();
        arrayList.add(null);
    }

    public void read(DataInputStream dataInputStream) throws IOException {
        int unsignedShort = dataInputStream.readUnsignedShort();
        int numEntries = 1;
        while (numEntries < unsignedShort) {
            ConstantPoolEntry entry = ConstantPoolEntry.readEntry(dataInputStream);
            numEntries += entry.getNumEntries();
            addEntry(entry);
        }
    }

    public int size() {
        return this.entries.size();
    }

    public int addEntry(ConstantPoolEntry constantPoolEntry) {
        int size = this.entries.size();
        this.entries.add(constantPoolEntry);
        int numEntries = constantPoolEntry.getNumEntries();
        for (int i = 0; i < numEntries - 1; i++) {
            this.entries.add(null);
        }
        if (constantPoolEntry instanceof Utf8CPInfo) {
            this.utf8Indexes.put(((Utf8CPInfo) constantPoolEntry).getValue(), new Integer(size));
        }
        return size;
    }

    public void resolve() {
        for (ConstantPoolEntry constantPoolEntry : this.entries) {
            if (constantPoolEntry != null && !constantPoolEntry.isResolved()) {
                constantPoolEntry.resolve(this);
            }
        }
    }

    public ConstantPoolEntry getEntry(int i) {
        return this.entries.get(i);
    }

    public int getUTF8Entry(String str) {
        Integer num = this.utf8Indexes.get(str);
        if (num != null) {
            return num.intValue();
        }
        return -1;
    }

    public int getClassEntry(String str) {
        int size = this.entries.size();
        int i = -1;
        for (int i2 = 0; i2 < size && i == -1; i2++) {
            ConstantPoolEntry constantPoolEntry = this.entries.get(i2);
            if ((constantPoolEntry instanceof ClassCPInfo) && ((ClassCPInfo) constantPoolEntry).getClassName().equals(str)) {
                i = i2;
            }
        }
        return i;
    }

    public int getConstantEntry(Object obj) {
        int size = this.entries.size();
        int i = -1;
        for (int i2 = 0; i2 < size && i == -1; i2++) {
            ConstantPoolEntry constantPoolEntry = this.entries.get(i2);
            if ((constantPoolEntry instanceof ConstantCPInfo) && ((ConstantCPInfo) constantPoolEntry).getValue().equals(obj)) {
                i = i2;
            }
        }
        return i;
    }

    public int getMethodRefEntry(String str, String str2, String str3) {
        int size = this.entries.size();
        int i = -1;
        for (int i2 = 0; i2 < size && i == -1; i2++) {
            ConstantPoolEntry constantPoolEntry = this.entries.get(i2);
            if (constantPoolEntry instanceof MethodRefCPInfo) {
                MethodRefCPInfo methodRefCPInfo = (MethodRefCPInfo) constantPoolEntry;
                if (methodRefCPInfo.getMethodClassName().equals(str) && methodRefCPInfo.getMethodName().equals(str2) && methodRefCPInfo.getMethodType().equals(str3)) {
                    i = i2;
                }
            }
        }
        return i;
    }

    public int getInterfaceMethodRefEntry(String str, String str2, String str3) {
        int size = this.entries.size();
        int i = -1;
        for (int i2 = 0; i2 < size && i == -1; i2++) {
            ConstantPoolEntry constantPoolEntry = this.entries.get(i2);
            if (constantPoolEntry instanceof InterfaceMethodRefCPInfo) {
                InterfaceMethodRefCPInfo interfaceMethodRefCPInfo = (InterfaceMethodRefCPInfo) constantPoolEntry;
                if (interfaceMethodRefCPInfo.getInterfaceMethodClassName().equals(str) && interfaceMethodRefCPInfo.getInterfaceMethodName().equals(str2) && interfaceMethodRefCPInfo.getInterfaceMethodType().equals(str3)) {
                    i = i2;
                }
            }
        }
        return i;
    }

    public int getFieldRefEntry(String str, String str2, String str3) {
        int size = this.entries.size();
        int i = -1;
        for (int i2 = 0; i2 < size && i == -1; i2++) {
            ConstantPoolEntry constantPoolEntry = this.entries.get(i2);
            if (constantPoolEntry instanceof FieldRefCPInfo) {
                FieldRefCPInfo fieldRefCPInfo = (FieldRefCPInfo) constantPoolEntry;
                if (fieldRefCPInfo.getFieldClassName().equals(str) && fieldRefCPInfo.getFieldName().equals(str2) && fieldRefCPInfo.getFieldType().equals(str3)) {
                    i = i2;
                }
            }
        }
        return i;
    }

    public int getNameAndTypeEntry(String str, String str2) {
        int size = this.entries.size();
        int i = -1;
        for (int i2 = 0; i2 < size && i == -1; i2++) {
            ConstantPoolEntry constantPoolEntry = this.entries.get(i2);
            if (constantPoolEntry instanceof NameAndTypeCPInfo) {
                NameAndTypeCPInfo nameAndTypeCPInfo = (NameAndTypeCPInfo) constantPoolEntry;
                if (nameAndTypeCPInfo.getName().equals(str) && nameAndTypeCPInfo.getType().equals(str2)) {
                    i = i2;
                }
            }
        }
        return i;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("\n");
        int size = this.entries.size();
        for (int i = 0; i < size; i++) {
            sb.append("[" + i + "] = " + getEntry(i) + "\n");
        }
        return sb.toString();
    }
}
