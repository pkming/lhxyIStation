package de.innosystec.unrar.rarfile;

import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes2.dex */
public enum UnrarHeadertype {
    MainHeader((byte) 115),
    MarkHeader((byte) 114),
    FileHeader((byte) 116),
    CommHeader((byte) 117),
    AvHeader((byte) 118),
    SubHeader((byte) 119),
    ProtectHeader(TarConstants.LF_PAX_EXTENDED_HEADER_LC),
    SignHeader((byte) 121),
    NewSubHeader((byte) 122),
    EndArcHeader((byte) 123);

    private byte headerByte;

    /* JADX INFO: renamed from: values, reason: to resolve conflict with enum method */
    public static UnrarHeadertype[] valuesCustom() {
        UnrarHeadertype[] unrarHeadertypeArrValuesCustom = values();
        int length = unrarHeadertypeArrValuesCustom.length;
        UnrarHeadertype[] unrarHeadertypeArr = new UnrarHeadertype[length];
        System.arraycopy(unrarHeadertypeArrValuesCustom, 0, unrarHeadertypeArr, 0, length);
        return unrarHeadertypeArr;
    }

    public static UnrarHeadertype findType(byte b) {
        UnrarHeadertype unrarHeadertype = MarkHeader;
        if (unrarHeadertype.equals(b)) {
            return unrarHeadertype;
        }
        UnrarHeadertype unrarHeadertype2 = MainHeader;
        if (unrarHeadertype2.equals(b)) {
            return unrarHeadertype2;
        }
        UnrarHeadertype unrarHeadertype3 = FileHeader;
        if (unrarHeadertype3.equals(b)) {
            return unrarHeadertype3;
        }
        UnrarHeadertype unrarHeadertype4 = EndArcHeader;
        if (unrarHeadertype4.equals(b)) {
            return unrarHeadertype4;
        }
        UnrarHeadertype unrarHeadertype5 = NewSubHeader;
        if (unrarHeadertype5.equals(b)) {
            return unrarHeadertype5;
        }
        UnrarHeadertype unrarHeadertype6 = SubHeader;
        if (unrarHeadertype6.equals(b)) {
            return unrarHeadertype6;
        }
        UnrarHeadertype unrarHeadertype7 = SignHeader;
        if (unrarHeadertype7.equals(b)) {
            return unrarHeadertype7;
        }
        UnrarHeadertype unrarHeadertype8 = ProtectHeader;
        if (unrarHeadertype8.equals(b)) {
            return unrarHeadertype8;
        }
        if (unrarHeadertype.equals(b)) {
            return unrarHeadertype;
        }
        if (unrarHeadertype2.equals(b)) {
            return unrarHeadertype2;
        }
        if (unrarHeadertype3.equals(b)) {
            return unrarHeadertype3;
        }
        if (unrarHeadertype4.equals(b)) {
            return unrarHeadertype4;
        }
        UnrarHeadertype unrarHeadertype9 = CommHeader;
        if (unrarHeadertype9.equals(b)) {
            return unrarHeadertype9;
        }
        UnrarHeadertype unrarHeadertype10 = AvHeader;
        if (unrarHeadertype10.equals(b)) {
            return unrarHeadertype10;
        }
        return null;
    }

    UnrarHeadertype(byte b) {
        this.headerByte = b;
    }

    public boolean equals(byte b) {
        return this.headerByte == b;
    }

    public byte getHeaderByte() {
        return this.headerByte;
    }
}
