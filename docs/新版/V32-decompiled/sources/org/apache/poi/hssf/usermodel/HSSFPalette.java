package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.PaletteRecord;
import org.apache.poi.hssf.util.HSSFColor;

/* JADX INFO: loaded from: classes3.dex */
public class HSSFPalette {
    private PaletteRecord palette;

    /* JADX INFO: renamed from: org.apache.poi.hssf.usermodel.HSSFPalette$1, reason: invalid class name */
    class AnonymousClass1 {
    }

    protected HSSFPalette(PaletteRecord paletteRecord) {
        this.palette = paletteRecord;
    }

    public HSSFColor getColor(short s) {
        byte[] color = this.palette.getColor(s);
        AnonymousClass1 anonymousClass1 = null;
        if (color != null) {
            return new CustomColor(s, color, anonymousClass1);
        }
        return null;
    }

    public HSSFColor findColor(byte b, byte b2, byte b3) {
        short s = 8;
        byte[] color = this.palette.getColor((short) 8);
        while (true) {
            AnonymousClass1 anonymousClass1 = null;
            if (color == null) {
                return null;
            }
            if (color[0] != b || color[1] != b2 || color[2] != b3) {
                s = (short) (s + 1);
                color = this.palette.getColor(s);
            } else {
                return new CustomColor(s, color, anonymousClass1);
            }
        }
    }

    public HSSFColor findSimilarColor(byte b, byte b2, byte b3) {
        short s = 8;
        byte[] color = this.palette.getColor((short) 8);
        HSSFColor color2 = null;
        while (color != null) {
            if (((((b - color[0]) + b2) - color[1]) + b3) - color[2] < Integer.MAX_VALUE) {
                color2 = getColor(s);
            }
            s = (short) (s + 1);
            color = this.palette.getColor(s);
        }
        return color2;
    }

    public void setColorAtIndex(short s, byte b, byte b2, byte b3) {
        this.palette.setColor(s, b, b2, b3);
    }

    public HSSFColor addColor(byte b, byte b2, byte b3) {
        short s = 8;
        byte[] color = this.palette.getColor((short) 8);
        while (s < 64) {
            if (color != null) {
                s = (short) (s + 1);
                color = this.palette.getColor(s);
            } else {
                setColorAtIndex(s, b, b2, b3);
                return getColor(s);
            }
        }
        throw new RuntimeException("Could not find free color index");
    }

    private static class CustomColor extends HSSFColor {
        private byte blue;
        private short byteOffset;
        private byte green;
        private byte red;

        /* synthetic */ CustomColor(short s, byte[] bArr, AnonymousClass1 anonymousClass1) {
            this(s, bArr);
        }

        private CustomColor(short s, byte[] bArr) {
            this(s, bArr[0], bArr[1], bArr[2]);
        }

        private CustomColor(short s, byte b, byte b2, byte b3) {
            this.byteOffset = s;
            this.red = b;
            this.green = b2;
            this.blue = b3;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return this.byteOffset;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return new short[]{(short) (this.red & 255), (short) (this.green & 255), (short) (this.blue & 255)};
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(getGnumericPart(this.red));
            stringBuffer.append(':');
            stringBuffer.append(getGnumericPart(this.green));
            stringBuffer.append(':');
            stringBuffer.append(getGnumericPart(this.blue));
            return stringBuffer.toString();
        }

        private String getGnumericPart(byte b) {
            if (b == 0) {
                return "0";
            }
            int i = b & 255;
            String upperCase = Integer.toHexString(i | (i << 8)).toUpperCase();
            while (upperCase.length() < 4) {
                upperCase = new StringBuffer().append("0").append(upperCase).toString();
            }
            return upperCase;
        }
    }
}
