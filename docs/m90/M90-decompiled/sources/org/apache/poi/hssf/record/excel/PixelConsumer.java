package org.apache.poi.hssf.record.excel;

import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Hashtable;

/* JADX INFO: loaded from: classes3.dex */
public class PixelConsumer implements ImageConsumer, Serializable {
    private boolean complete;
    public int height;
    public int iheight;
    private Image image;
    private boolean init;
    public int iwidth;
    public int[][] pix;
    private boolean region;
    private int sx1;
    private int sx2;
    private int sy1;
    private int sy2;
    private int trycnt;
    public int width;

    private int getInt(byte b) {
        return b & 255;
    }

    public void setColorModel(ColorModel colorModel) {
    }

    public void setHints(int i) {
    }

    public void setProperties(Hashtable hashtable) {
    }

    static class Key {
        Image image;
        int sx1;
        int sx2;
        int sy1;
        int sy2;

        public int hashCode() {
            return this.image.hashCode() + this.sx1 + this.sy1 + this.sx2 + this.sy2;
        }

        public boolean equals(Object obj) {
            try {
                Key key = (Key) obj;
                if (this.image == key.image && this.sx1 == key.sx1 && this.sy1 == key.sy1 && this.sx2 == key.sx2) {
                    return this.sy2 == key.sy2;
                }
                return false;
            } catch (Exception unused) {
                return false;
            }
        }

        public Key(PixelConsumer pixelConsumer) {
            this.image = pixelConsumer.image;
            this.sx1 = pixelConsumer.sx1;
            this.sx2 = pixelConsumer.sx2;
            this.sy1 = pixelConsumer.sy1;
            this.sy2 = pixelConsumer.sy2;
        }
    }

    public PixelConsumer(Image image) {
        this.complete = false;
        this.region = false;
        this.trycnt = 0;
        this.init = false;
        this.image = image;
    }

    public PixelConsumer(Image image, int i, int i2, int i3, int i4) {
        this(image);
        this.region = true;
        this.sx1 = i;
        this.sy1 = i2;
        this.sx2 = i3;
        this.sy2 = i4;
    }

    public Object getKey() {
        return new Key(this);
    }

    public void produce() {
        if (this.init) {
            return;
        }
        this.init = true;
        produce(this.image);
    }

    void produce(Image image) {
        ImageProducer source = image.getSource();
        source.removeConsumer(this);
        source.startProduction(this);
        synchronized (this) {
            while (!this.complete) {
                try {
                    wait();
                } catch (Exception unused) {
                }
            }
        }
        source.removeConsumer(this);
    }

    public synchronized void imageComplete(int i) {
        if (i == 1 || i == 4) {
            int i2 = this.trycnt;
            if (i2 < 3) {
                this.trycnt = i2 + 1;
                produce(this.image);
            } else {
                i = 3;
            }
            if (i != 3 || i == 2) {
                this.complete = true;
                notifyAll();
            }
        } else {
            if (i != 3) {
            }
            this.complete = true;
            notifyAll();
        }
    }

    public void setDimensions(int i, int i2) {
        if (this.region) {
            i = (this.sx2 - this.sx1) + 1;
            i2 = (this.sy2 - this.sy1) + 1;
        }
        if (this.pix == null || i != this.width || i2 != this.height) {
            this.width = i;
            this.height = i2;
            this.pix = (int[][]) Array.newInstance((Class<?>) int.class, i, i2);
        }
        this.iwidth = this.width;
        this.iheight = this.height;
    }

    public void setPixels(int i, int i2, int i3, int i4, ColorModel colorModel, byte[] bArr, int i5, int i6) {
        int i7;
        int i8 = i3 + i;
        int i9 = i4 + i2;
        while (i2 < i9) {
            int i10 = i5;
            for (int i11 = i; i11 < i8 && i10 < bArr.length; i11++) {
                if (this.region) {
                    int i12 = this.sx1;
                    if (i11 >= i12 && i11 <= this.sx2 && i2 >= (i7 = this.sy1) && i2 <= this.sy2) {
                        int i13 = i10 + 1;
                        this.pix[i11 - i12][i2 - i7] = getInt(bArr[i10]);
                        if (colorModel != null) {
                            int[][] iArr = this.pix;
                            int i14 = this.sx1;
                            int[] iArr2 = iArr[i11 - i14];
                            int i15 = this.sy1;
                            iArr2[i2 - i15] = colorModel.getRGB(iArr[i11 - i14][i2 - i15]);
                        }
                        i10 = i13;
                    }
                } else {
                    int i16 = i10 + 1;
                    this.pix[i11][i2] = getInt(bArr[i10]);
                    if (colorModel != null) {
                        int[][] iArr3 = this.pix;
                        iArr3[i11][i2] = colorModel.getRGB(iArr3[i11][i2]);
                    }
                    i10 = i16;
                }
            }
            i5 += i6;
            i2++;
        }
    }

    public void setPixels(int i, int i2, int i3, int i4, ColorModel colorModel, int[] iArr, int i5, int i6) {
        int i7;
        int i8 = i3 + i;
        int i9 = i4 + i2;
        while (i2 < i9) {
            int i10 = i5;
            for (int i11 = i; i11 < i8 && i10 < iArr.length; i11++) {
                if (this.region) {
                    int i12 = this.sx1;
                    if (i11 >= i12 && i11 <= this.sx2 && i2 >= (i7 = this.sy1) && i2 <= this.sy2) {
                        this.pix[i11 - i12][i2 - i7] = (colorModel.getRGB(iArr[i10]) & 16777215) | (-16777216);
                        i10++;
                    }
                } else {
                    this.pix[i11][i2] = (colorModel.getRGB(iArr[i10]) & 16777215) | (-16777216);
                    i10++;
                }
            }
            i5 += i6;
            i2++;
        }
    }

    public int hashCode() {
        return this.image.hashCode() + this.sx1 + this.sy1 + this.sx2 + this.sy2;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PixelConsumer)) {
            return false;
        }
        PixelConsumer pixelConsumer = (PixelConsumer) obj;
        return this.image == pixelConsumer.image && this.sx1 == pixelConsumer.sx1 && this.sy1 == pixelConsumer.sy1 && this.sx2 == pixelConsumer.sx2 && this.sy2 == pixelConsumer.sy2;
    }
}
