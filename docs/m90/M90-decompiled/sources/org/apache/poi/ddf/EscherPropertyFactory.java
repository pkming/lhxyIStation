package org.apache.poi.ddf;

import de.innosystec.unrar.rarfile.BaseBlock;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class EscherPropertyFactory {
    public List createProperties(byte[] bArr, int i, short s) {
        int length;
        ArrayList<EscherProperty> arrayList = new ArrayList();
        for (int i2 = 0; i2 < s; i2++) {
            short s2 = LittleEndian.getShort(bArr, i);
            int i3 = LittleEndian.getInt(bArr, i + 2);
            short s3 = (short) (s2 & 16383);
            boolean z = (s2 & BaseBlock.LONG_BLOCK) != 0;
            int i4 = s2 & BaseBlock.SKIP_IF_UNKNOWN;
            byte propertyType = EscherProperties.getPropertyType(s3);
            if (propertyType == 1) {
                arrayList.add(new EscherBoolProperty(s3, i3));
            } else if (propertyType == 2) {
                arrayList.add(new EscherRGBProperty(s3, i3));
            } else if (propertyType == 3) {
                arrayList.add(new EscherShapePathProperty(s3, i3));
            } else if (!z) {
                arrayList.add(new EscherSimpleProperty(s3, i3));
            } else if (propertyType == 5) {
                arrayList.add(new EscherArrayProperty(s2, new byte[i3]));
            } else {
                arrayList.add(new EscherComplexProperty(s2, new byte[i3]));
            }
            i += 6;
        }
        for (EscherProperty escherProperty : arrayList) {
            if (escherProperty instanceof EscherComplexProperty) {
                if (escherProperty instanceof EscherArrayProperty) {
                    length = ((EscherArrayProperty) escherProperty).setArrayData(bArr, i);
                } else {
                    byte[] complexData = ((EscherComplexProperty) escherProperty).getComplexData();
                    System.arraycopy(bArr, i, complexData, 0, complexData.length);
                    length = complexData.length;
                }
                i += length;
            }
        }
        return arrayList;
    }
}
