package org.apache.poi.hssf.record.excel;

import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public class ImdataBiffElement extends OBJBiffElement {
    private static int BITMAPCELLLENGTH = 2068;
    private static int BITMAPCOREINFO = 12;
    private static int HEADERBITS = 20;
    private int bcBitCount;
    private int bcHeight;
    private int bcPlanes;
    private int bcSize;
    private int bcWidth;
    private int env;
    private byte[] imageData;
    private Image img;
    private int imgFormat;
    private boolean isBackground;
    private int lcb;

    private ImdataBiffElement(Image image) {
        super(127, 0);
        this.imageData = null;
        this.imgFormat = 9;
        this.env = 1;
        this.bcSize = 12;
        this.bcWidth = 0;
        this.bcHeight = 0;
        this.bcPlanes = 1;
        this.bcBitCount = 24;
        this.lcb = 0;
        this.img = null;
        this.isBackground = true;
        setObjectType(OBJBiffElement.PICTURE);
        setGrbit(1556);
        setContent(image);
    }

    private ImdataBiffElement(byte[] bArr) {
        this((Image) null);
        this.imageData = bArr;
    }

    public static ImdataBiffElement createImdataBiffElement(Image image) {
        return new ImdataBiffElement(image);
    }

    public static ImdataBiffElement createImdataBiffElement(byte[] bArr) {
        return new ImdataBiffElement(bArr);
    }

    @Override // org.apache.poi.hssf.record.excel.AbstractBiffElement, org.apache.poi.hssf.record.excel.BiffElement
    public byte[] toBinary() throws IOException {
        byte[] imageByteData = getImageByteData();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStreamLfirst dataOutputStreamLfirst = new DataOutputStreamLfirst(byteArrayOutputStream);
        dataOutputStreamLfirst.writeShort(93);
        if (this.isBackground) {
            dataOutputStreamLfirst.writeShort(70);
        } else {
            dataOutputStreamLfirst.writeShort(60);
        }
        dataOutputStreamLfirst.write(super.toHeadBinary());
        dataOutputStreamLfirst.write(9);
        dataOutputStreamLfirst.write(9);
        dataOutputStreamLfirst.write(0);
        dataOutputStreamLfirst.write(0);
        dataOutputStreamLfirst.write(8);
        dataOutputStreamLfirst.write(-1);
        dataOutputStreamLfirst.write(1);
        dataOutputStreamLfirst.write(0);
        dataOutputStreamLfirst.writeShort(0);
        dataOutputStreamLfirst.writeShort(this.imgFormat);
        dataOutputStreamLfirst.writeInt(0);
        dataOutputStreamLfirst.writeShort(0);
        dataOutputStreamLfirst.writeShort(0);
        dataOutputStreamLfirst.writeShort(0);
        dataOutputStreamLfirst.writeInt(0);
        if (this.isBackground) {
            dataOutputStreamLfirst.write(9);
            dataOutputStreamLfirst.writeBytes("_BkgndObj");
        }
        dataOutputStreamLfirst.writeShort(getType());
        dataOutputStreamLfirst.writeShort(getLength());
        dataOutputStreamLfirst.writeShort(this.imgFormat);
        dataOutputStreamLfirst.writeShort(this.env);
        dataOutputStreamLfirst.writeInt(this.lcb);
        dataOutputStreamLfirst.writeInt(this.bcSize);
        dataOutputStreamLfirst.writeShort(this.bcWidth);
        dataOutputStreamLfirst.writeShort(this.bcHeight);
        dataOutputStreamLfirst.writeShort(this.bcPlanes);
        dataOutputStreamLfirst.writeShort(this.bcBitCount);
        dataOutputStreamLfirst.write(imageByteData);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        dataOutputStreamLfirst.close();
        byteArrayOutputStream.close();
        return byteArray;
    }

    public void setContent(Image image) {
        this.img = image;
    }

    public Image getContent() {
        return this.img;
    }

    public void setBackground(boolean z) {
        this.isBackground = z;
    }

    private byte[] getImageByteData() {
        int i;
        if (this.img == null) {
            return this.imageData;
        }
        PixelConsumer pixelConsumer = new PixelConsumer(this.img);
        pixelConsumer.produce();
        if (pixelConsumer.width == 0 || pixelConsumer.height == 0) {
            return null;
        }
        int i2 = pixelConsumer.width;
        int i3 = pixelConsumer.height;
        int i4 = i2 * 3;
        int i5 = i4 % 4;
        int i6 = i5 != 0 ? 4 - i5 : 0;
        int i7 = (i4 + i6) * i3;
        byte[] bArr = new byte[i7];
        for (int i8 = 0; i8 < i7; i8++) {
            bArr[i8] = 0;
        }
        this.bcWidth = i2;
        this.bcHeight = i3;
        int[] iArr = new int[i2 * i3];
        try {
            new PixelGrabber(this.img, 0, 0, i2, i3, iArr, 0, i2).grabPixels();
            int i9 = -1;
            for (int i10 = i3 - 1; i10 >= 0; i10--) {
                for (int i11 = 0; i11 < i2; i11++) {
                    int i12 = iArr[(i10 * i2) + i11];
                    if ((i12 >>> 24) == 0) {
                        i12 = -1;
                    }
                    int i13 = i9 + 1;
                    bArr[i13] = (byte) (i12 & 255);
                    int i14 = i13 + 1;
                    bArr[i14] = (byte) ((i12 >> 8) & 255);
                    i9 = i14 + 1;
                    bArr[i9] = (byte) ((i12 >> 16) & 255);
                }
                i9 += i6;
            }
            int i15 = BITMAPCOREINFO;
            int i16 = i7 + i15;
            this.lcb = i16;
            int i17 = BITMAPCELLLENGTH;
            int i18 = HEADERBITS;
            if (i7 < i17 - i18) {
                setLength((i16 + i18) - i15);
                return bArr;
            }
            setLength(i17);
            int i19 = HEADERBITS;
            byte[] bArr2 = new byte[(((i7 + i19) / BITMAPCELLLENGTH) * 4) + i7];
            int i20 = 0;
            for (int i21 = 0; i21 < i7; i21++) {
                int i22 = i20 + 1;
                bArr2[i20] = bArr[i21];
                i19++;
                int i23 = BITMAPCELLLENGTH;
                if (i19 == i23) {
                    int i24 = i22 + 1;
                    bArr2[i22] = 60;
                    int i25 = i24 + 1;
                    bArr2[i24] = 0;
                    int i26 = i7 - i21;
                    if (i26 > i23) {
                        int i27 = i25 + 1;
                        bArr2[i25] = (byte) i23;
                        i = i27 + 1;
                        bArr2[i27] = (byte) (i23 >>> 8);
                    } else {
                        int i28 = i26 - 1;
                        int i29 = i25 + 1;
                        bArr2[i25] = (byte) (i28 & 255);
                        i = i29 + 1;
                        bArr2[i29] = (byte) ((i28 & 65280) >>> 8);
                    }
                    i20 = i;
                    i19 = 0;
                } else {
                    i20 = i22;
                }
            }
            return bArr2;
        } catch (InterruptedException unused) {
            return null;
        }
    }
}
