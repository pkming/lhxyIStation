package org.apache.tools.ant.util;

/* JADX INFO: loaded from: classes3.dex */
public class DeweyDecimal implements Comparable<DeweyDecimal> {
    private final int[] components;

    public DeweyDecimal(int[] iArr) {
        int[] iArr2 = new int[iArr.length];
        this.components = iArr2;
        System.arraycopy(iArr, 0, iArr2, 0, iArr.length);
    }

    public DeweyDecimal(String str) throws NumberFormatException {
        java.util.StringTokenizer stringTokenizer = new java.util.StringTokenizer(str, ".", true);
        this.components = new int[(stringTokenizer.countTokens() + 1) / 2];
        for (int i = 0; i < this.components.length; i++) {
            String strNextToken = stringTokenizer.nextToken();
            if (strNextToken.length() == 0) {
                throw new NumberFormatException("Empty component in string");
            }
            this.components[i] = Integer.parseInt(strNextToken);
            if (stringTokenizer.hasMoreTokens()) {
                stringTokenizer.nextToken();
                if (!stringTokenizer.hasMoreTokens()) {
                    throw new NumberFormatException("DeweyDecimal ended in a '.'");
                }
            }
        }
    }

    public int getSize() {
        return this.components.length;
    }

    public int get(int i) {
        return this.components[i];
    }

    public boolean isEqual(DeweyDecimal deweyDecimal) {
        int iMax = Math.max(deweyDecimal.components.length, this.components.length);
        int i = 0;
        while (i < iMax) {
            int[] iArr = this.components;
            int i2 = i < iArr.length ? iArr[i] : 0;
            int[] iArr2 = deweyDecimal.components;
            if ((i < iArr2.length ? iArr2[i] : 0) != i2) {
                return false;
            }
            i++;
        }
        return true;
    }

    public boolean isLessThan(DeweyDecimal deweyDecimal) {
        return !isGreaterThanOrEqual(deweyDecimal);
    }

    public boolean isLessThanOrEqual(DeweyDecimal deweyDecimal) {
        return !isGreaterThan(deweyDecimal);
    }

    public boolean isGreaterThan(DeweyDecimal deweyDecimal) {
        int iMax = Math.max(deweyDecimal.components.length, this.components.length);
        int i = 0;
        while (i < iMax) {
            int[] iArr = this.components;
            int i2 = i < iArr.length ? iArr[i] : 0;
            int[] iArr2 = deweyDecimal.components;
            int i3 = i < iArr2.length ? iArr2[i] : 0;
            if (i3 > i2) {
                return false;
            }
            if (i3 < i2) {
                return true;
            }
            i++;
        }
        return false;
    }

    public boolean isGreaterThanOrEqual(DeweyDecimal deweyDecimal) {
        int iMax = Math.max(deweyDecimal.components.length, this.components.length);
        int i = 0;
        while (i < iMax) {
            int[] iArr = this.components;
            int i2 = i < iArr.length ? iArr[i] : 0;
            int[] iArr2 = deweyDecimal.components;
            int i3 = i < iArr2.length ? iArr2[i] : 0;
            if (i3 > i2) {
                return false;
            }
            if (i3 < i2) {
                return true;
            }
            i++;
        }
        return true;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < this.components.length; i++) {
            if (i != 0) {
                stringBuffer.append('.');
            }
            stringBuffer.append(this.components[i]);
        }
        return stringBuffer.toString();
    }

    @Override // java.lang.Comparable
    public int compareTo(DeweyDecimal deweyDecimal) {
        int iMax = Math.max(deweyDecimal.components.length, this.components.length);
        int i = 0;
        while (i < iMax) {
            int[] iArr = this.components;
            int i2 = i < iArr.length ? iArr[i] : 0;
            int[] iArr2 = deweyDecimal.components;
            int i3 = i < iArr2.length ? iArr2[i] : 0;
            if (i2 != i3) {
                return i2 - i3;
            }
            i++;
        }
        return 0;
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public boolean equals(Object obj) {
        return (obj instanceof DeweyDecimal) && isEqual((DeweyDecimal) obj);
    }
}
