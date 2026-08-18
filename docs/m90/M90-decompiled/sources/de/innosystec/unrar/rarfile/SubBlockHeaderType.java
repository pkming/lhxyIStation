package de.innosystec.unrar.rarfile;

import org.apache.poi.ddf.EscherProperties;

/* JADX INFO: loaded from: classes2.dex */
public enum SubBlockHeaderType {
    EA_HEAD(256),
    UO_HEAD(EscherProperties.BLIP__CROPFROMBOTTOM),
    MAC_HEAD(EscherProperties.BLIP__CROPFROMLEFT),
    BEEA_HEAD(EscherProperties.BLIP__CROPFROMRIGHT),
    NTACL_HEAD(EscherProperties.BLIP__BLIPTODISPLAY),
    STREAM_HEAD(EscherProperties.BLIP__BLIPFILENAME);

    private short subblocktype;

    /* JADX INFO: renamed from: values, reason: to resolve conflict with enum method */
    public static SubBlockHeaderType[] valuesCustom() {
        SubBlockHeaderType[] subBlockHeaderTypeArrValuesCustom = values();
        int length = subBlockHeaderTypeArrValuesCustom.length;
        SubBlockHeaderType[] subBlockHeaderTypeArr = new SubBlockHeaderType[length];
        System.arraycopy(subBlockHeaderTypeArrValuesCustom, 0, subBlockHeaderTypeArr, 0, length);
        return subBlockHeaderTypeArr;
    }

    SubBlockHeaderType(short s) {
        this.subblocktype = s;
    }

    public boolean equals(short s) {
        return this.subblocktype == s;
    }

    public static SubBlockHeaderType findSubblockHeaderType(short s) {
        SubBlockHeaderType subBlockHeaderType = EA_HEAD;
        if (subBlockHeaderType.equals(s)) {
            return subBlockHeaderType;
        }
        SubBlockHeaderType subBlockHeaderType2 = UO_HEAD;
        if (subBlockHeaderType2.equals(s)) {
            return subBlockHeaderType2;
        }
        SubBlockHeaderType subBlockHeaderType3 = MAC_HEAD;
        if (subBlockHeaderType3.equals(s)) {
            return subBlockHeaderType3;
        }
        SubBlockHeaderType subBlockHeaderType4 = BEEA_HEAD;
        if (subBlockHeaderType4.equals(s)) {
            return subBlockHeaderType4;
        }
        SubBlockHeaderType subBlockHeaderType5 = NTACL_HEAD;
        if (subBlockHeaderType5.equals(s)) {
            return subBlockHeaderType5;
        }
        SubBlockHeaderType subBlockHeaderType6 = STREAM_HEAD;
        if (subBlockHeaderType6.equals(s)) {
            return subBlockHeaderType6;
        }
        return null;
    }

    public short getSubblocktype() {
        return this.subblocktype;
    }
}
