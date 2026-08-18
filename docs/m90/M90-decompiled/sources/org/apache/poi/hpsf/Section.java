package org.apache.poi.hpsf;

import java.util.Map;
import org.apache.poi.hpsf.wellknown.SectionIDMap;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class Section {
    protected Map dictionary;
    protected ClassID formatID;
    protected long offset;
    protected Property[] properties;
    protected int propertyCount;
    protected int size;
    private boolean wasNull;

    public ClassID getFormatID() {
        return this.formatID;
    }

    public long getOffset() {
        return this.offset;
    }

    public int getSize() {
        return this.size;
    }

    public int getPropertyCount() {
        return this.propertyCount;
    }

    public Property[] getProperties() {
        return this.properties;
    }

    protected Section() {
    }

    public Section(byte[] bArr, int i) {
        int uInt;
        this.formatID = new ClassID(bArr, i);
        long uInt2 = LittleEndian.getUInt(bArr, i + 16);
        this.offset = uInt2;
        int i2 = (int) uInt2;
        this.size = (int) LittleEndian.getUInt(bArr, i2);
        int i3 = i2 + 4;
        int uInt3 = (int) LittleEndian.getUInt(bArr, i3);
        this.propertyCount = uInt3;
        int i4 = i3 + 4;
        this.properties = new Property[uInt3];
        int i5 = i4;
        int uShort = -1;
        int i6 = 0;
        while (true) {
            int i7 = 1;
            if (i6 < this.properties.length) {
                int uInt4 = (int) LittleEndian.getUInt(bArr, i5);
                int i8 = i5 + 4;
                int uInt5 = (int) LittleEndian.getUInt(bArr, i8);
                i5 = i8 + 4;
                if (i6 == this.properties.length - 1) {
                    int length = bArr.length;
                } else {
                    LittleEndian.getUInt(bArr, i5 + 4);
                }
                if (uInt4 == 1) {
                    int i9 = (int) (this.offset + ((long) uInt5));
                    long uInt6 = LittleEndian.getUInt(bArr, i9);
                    int i10 = i9 + 4;
                    if (uInt6 != 2) {
                        throw new HPSFRuntimeException(new StringBuffer().append("Value type of property ID 1 is not VT_I2 but ").append(uInt6).append(".").toString());
                    }
                    uShort = LittleEndian.getUShort(bArr, i10);
                }
                i6++;
            } else {
                int i11 = 0;
                while (i11 < this.properties.length) {
                    int uInt7 = (int) LittleEndian.getUInt(bArr, i4);
                    int i12 = i4 + 4;
                    int uInt8 = (int) LittleEndian.getUInt(bArr, i12);
                    int i13 = i12 + 4;
                    if (i11 == this.properties.length - i7) {
                        uInt = (int) ((((long) bArr.length) - this.offset) - ((long) uInt8));
                    } else {
                        uInt = ((int) LittleEndian.getUInt(bArr, i13 + 4)) - uInt8;
                    }
                    int i14 = i11;
                    this.properties[i14] = new Property(uInt7, bArr, this.offset + ((long) uInt8), uInt, uShort);
                    i11 = i14 + 1;
                    i4 = i13;
                    i7 = 1;
                }
                this.dictionary = (Map) getProperty(0);
                return;
            }
        }
    }

    public Object getProperty(int i) {
        int i2 = 0;
        this.wasNull = false;
        while (true) {
            Property[] propertyArr = this.properties;
            if (i2 < propertyArr.length) {
                if (i == propertyArr[i2].getID()) {
                    return this.properties[i2].getValue();
                }
                i2++;
            } else {
                this.wasNull = true;
                return null;
            }
        }
    }

    protected int getPropertyIntValue(int i) {
        Long l = (Long) getProperty(i);
        if (l != null) {
            return l.intValue();
        }
        return 0;
    }

    protected boolean getPropertyBooleanValue(int i) {
        Boolean bool = (Boolean) getProperty(i);
        if (bool != null) {
            return bool.booleanValue();
        }
        return false;
    }

    public boolean wasNull() {
        return this.wasNull;
    }

    public String getPIDString(int i) {
        Map map = this.dictionary;
        String pIDString = map != null ? (String) map.get(new Integer(i)) : null;
        if (pIDString == null) {
            pIDString = SectionIDMap.getPIDString(getFormatID().getBytes(), i);
        }
        return pIDString == null ? SectionIDMap.UNDEFINED : pIDString;
    }
}
