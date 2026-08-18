package org.apache.poi.ddf;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class EscherSimpleProperty extends EscherProperty {
    protected int propertyValue;

    @Override // org.apache.poi.ddf.EscherProperty
    public int serializeComplexPart(byte[] bArr, int i) {
        return 0;
    }

    public EscherSimpleProperty(short s, int i) {
        super(s);
        this.propertyValue = i;
    }

    public EscherSimpleProperty(short s, boolean z, boolean z2, int i) {
        super(s, z, z2);
        this.propertyValue = i;
    }

    @Override // org.apache.poi.ddf.EscherProperty
    public int serializeSimplePart(byte[] bArr, int i) {
        LittleEndian.putShort(bArr, i, getId());
        LittleEndian.putInt(bArr, i + 2, this.propertyValue);
        return 6;
    }

    public int getPropertyValue() {
        return this.propertyValue;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EscherSimpleProperty)) {
            return false;
        }
        EscherSimpleProperty escherSimpleProperty = (EscherSimpleProperty) obj;
        return this.propertyValue == escherSimpleProperty.propertyValue && getId() == escherSimpleProperty.getId();
    }

    public int hashCode() {
        return this.propertyValue;
    }

    public String toString() {
        return new StringBuffer().append("propNum: ").append((int) getPropertyNumber()).append(", propName: ").append(EscherProperties.getPropertyName(getPropertyNumber())).append(", complex: ").append(isComplex()).append(", blipId: ").append(isBlipId()).append(", value: ").append(this.propertyValue).append(" (0x").append(HexDump.toHex(this.propertyValue)).append(")").toString();
    }
}
