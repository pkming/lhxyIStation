package de.innosystec.unrar.unpack;

import android.media.AudioSystem;
import android.opengl.GLES30;
import android.view.Menu;
import android.view.SurfaceControl;
import de.innosystec.unrar.exception.RarException;
import de.innosystec.unrar.unpack.decode.AudioVariables;
import de.innosystec.unrar.unpack.decode.BitDecode;
import de.innosystec.unrar.unpack.decode.Compress;
import de.innosystec.unrar.unpack.decode.Decode;
import de.innosystec.unrar.unpack.decode.DistDecode;
import de.innosystec.unrar.unpack.decode.LitDecode;
import de.innosystec.unrar.unpack.decode.LowDistDecode;
import de.innosystec.unrar.unpack.decode.MultDecode;
import de.innosystec.unrar.unpack.decode.RepDecode;
import java.io.IOException;
import java.util.Arrays;
import org.apache.poi.hssf.usermodel.HSSFErrorConstants;

/* JADX INFO: loaded from: classes2.dex */
public abstract class Unpack20 extends Unpack15 {
    protected int UnpAudioBlock;
    protected int UnpChannelDelta;
    protected int UnpChannels;
    protected int UnpCurChannel;
    public static final int[] LDecode = {0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 12, 14, 16, 20, 24, 28, 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224};
    public static final byte[] LBits = {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5};
    public static final int[] DDecode = {0, 1, 2, 3, 4, 6, 8, 12, 16, 24, 32, 48, 64, 96, 128, 192, 256, 384, 512, 768, 1024, 1536, 2048, 3072, 4096, GLES30.GL_COLOR, 8192, 12288, 16384, AudioSystem.DEVICE_OUT_ALL_USB, 32768, 49152, 65536, 98304, 131072, Menu.CATEGORY_SECONDARY, 262144, 327680, 393216, 458752, 524288, 589824, 655360, 720896, 786432, 851968, 917504, SurfaceControl.FX_SURFACE_MASK};
    public static final int[] DBits = {0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13, 14, 14, 15, 15, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16};
    public static final int[] SDDecode = {0, 4, 8, 16, 32, 64, 128, 192};
    public static final int[] SDBits = {2, 2, 3, 4, 5, 6, 6, 6};
    protected MultDecode[] MD = new MultDecode[4];
    protected byte[] UnpOldTable20 = new byte[1028];
    protected AudioVariables[] AudV = new AudioVariables[4];
    protected LitDecode LD = new LitDecode();
    protected DistDecode DD = new DistDecode();
    protected LowDistDecode LDD = new LowDistDecode();
    protected RepDecode RD = new RepDecode();
    protected BitDecode BD = new BitDecode();

    protected void unpack20(boolean z) throws RarException, IOException {
        if (this.suspended) {
            this.unpPtr = this.wrPtr;
        } else {
            unpInitData(z);
            if (!unpReadBuf()) {
                return;
            }
            if (!z && !ReadTables20()) {
                return;
            } else {
                this.destUnpSize--;
            }
        }
        while (this.destUnpSize >= 0) {
            this.unpPtr &= Compress.MAXWINMASK;
            if (this.inAddr > this.readTop - 30 && !unpReadBuf()) {
                break;
            }
            if (((this.wrPtr - this.unpPtr) & Compress.MAXWINMASK) < 270 && this.wrPtr != this.unpPtr) {
                oldUnpWriteBuf();
                if (this.suspended) {
                    return;
                }
            }
            if (this.UnpAudioBlock != 0) {
                int iDecodeNumber = decodeNumber(this.MD[this.UnpCurChannel]);
                if (iDecodeNumber == 256) {
                    if (!ReadTables20()) {
                        break;
                    }
                } else {
                    byte[] bArr = this.window;
                    int i = this.unpPtr;
                    this.unpPtr = i + 1;
                    bArr[i] = DecodeAudio(iDecodeNumber);
                    int i2 = this.UnpCurChannel + 1;
                    this.UnpCurChannel = i2;
                    if (i2 == this.UnpChannels) {
                        this.UnpCurChannel = 0;
                    }
                    this.destUnpSize--;
                }
            } else {
                int iDecodeNumber2 = decodeNumber(this.LD);
                if (iDecodeNumber2 < 256) {
                    byte[] bArr2 = this.window;
                    int i3 = this.unpPtr;
                    this.unpPtr = i3 + 1;
                    bArr2[i3] = (byte) iDecodeNumber2;
                    this.destUnpSize--;
                } else if (iDecodeNumber2 > 269) {
                    int i4 = iDecodeNumber2 - 270;
                    int i5 = LDecode[i4] + 3;
                    byte b = LBits[i4];
                    if (b > 0) {
                        i5 += getbits() >>> (16 - b);
                        addbits(b);
                    }
                    int iDecodeNumber3 = decodeNumber(this.DD);
                    int i6 = DDecode[iDecodeNumber3] + 1;
                    int i7 = DBits[iDecodeNumber3];
                    if (i7 > 0) {
                        i6 += getbits() >>> (16 - i7);
                        addbits(i7);
                    }
                    if (i6 >= 8192) {
                        i5++;
                        if (i6 >= 262144) {
                            i5++;
                        }
                    }
                    CopyString20(i5, i6);
                } else if (iDecodeNumber2 == 269) {
                    if (!ReadTables20()) {
                        break;
                    }
                } else if (iDecodeNumber2 == 256) {
                    CopyString20(this.lastLength, this.lastDist);
                } else if (iDecodeNumber2 < 261) {
                    int i8 = this.oldDist[(this.oldDistPtr - (iDecodeNumber2 - 256)) & 3];
                    int iDecodeNumber4 = decodeNumber(this.RD);
                    int i9 = LDecode[iDecodeNumber4] + 2;
                    byte b2 = LBits[iDecodeNumber4];
                    if (b2 > 0) {
                        i9 += getbits() >>> (16 - b2);
                        addbits(b2);
                    }
                    if (i8 >= 257) {
                        i9++;
                        if (i8 >= 8192) {
                            i9++;
                            if (i8 >= 262144) {
                                i9++;
                            }
                        }
                    }
                    CopyString20(i9, i8);
                } else if (iDecodeNumber2 < 270) {
                    int i10 = iDecodeNumber2 - 261;
                    int i11 = SDDecode[i10] + 1;
                    int i12 = SDBits[i10];
                    if (i12 > 0) {
                        i11 += getbits() >>> (16 - i12);
                        addbits(i12);
                    }
                    CopyString20(2, i11);
                }
            }
        }
        ReadLastTables();
        oldUnpWriteBuf();
    }

    protected void CopyString20(int i, int i2) {
        int[] iArr = this.oldDist;
        int i3 = this.oldDistPtr;
        this.oldDistPtr = i3 + 1;
        iArr[i3 & 3] = i2;
        this.lastDist = i2;
        this.lastLength = i;
        this.destUnpSize -= (long) i;
        int i4 = this.unpPtr - i2;
        if (i4 < 4194004 && this.unpPtr < 4194004) {
            byte[] bArr = this.window;
            int i5 = this.unpPtr;
            this.unpPtr = i5 + 1;
            int i6 = i4 + 1;
            bArr[i5] = this.window[i4];
            byte[] bArr2 = this.window;
            int i7 = this.unpPtr;
            this.unpPtr = i7 + 1;
            int i8 = i6 + 1;
            bArr2[i7] = this.window[i6];
            while (i > 2) {
                i--;
                byte[] bArr3 = this.window;
                int i9 = this.unpPtr;
                this.unpPtr = i9 + 1;
                bArr3[i9] = this.window[i8];
                i8++;
            }
            return;
        }
        while (true) {
            int i10 = i - 1;
            if (i == 0) {
                return;
            }
            this.window[this.unpPtr] = this.window[i4 & Compress.MAXWINMASK];
            this.unpPtr = (this.unpPtr + 1) & Compress.MAXWINMASK;
            i = i10;
            i4++;
        }
    }

    protected void makeDecodeTables(byte[] bArr, int i, Decode decode, int i2) {
        int i3;
        int[] iArr = new int[16];
        int[] iArr2 = new int[16];
        Arrays.fill(iArr, 0);
        Arrays.fill(decode.getDecodeNum(), 0);
        int i4 = 0;
        while (true) {
            if (i4 >= i2) {
                break;
            }
            int i5 = bArr[i + i4] & HSSFErrorConstants.ERROR_VALUE;
            iArr[i5] = iArr[i5] + 1;
            i4++;
        }
        iArr[0] = 0;
        iArr2[0] = 0;
        decode.getDecodePos()[0] = 0;
        decode.getDecodeLen()[0] = 0;
        long j = 0;
        for (i3 = 1; i3 < 16; i3++) {
            j = (j + ((long) iArr[i3])) * 2;
            long j2 = j << (15 - i3);
            if (j2 > 65535) {
                j2 = 65535;
            }
            decode.getDecodeLen()[i3] = (int) j2;
            int[] decodePos = decode.getDecodePos();
            int i6 = i3 - 1;
            int i7 = decode.getDecodePos()[i6] + iArr[i6];
            decodePos[i3] = i7;
            iArr2[i3] = i7;
        }
        for (int i8 = 0; i8 < i2; i8++) {
            int i9 = i + i8;
            if (bArr[i9] != 0) {
                int[] decodeNum = decode.getDecodeNum();
                int i10 = bArr[i9] & HSSFErrorConstants.ERROR_VALUE;
                int i11 = iArr2[i10];
                iArr2[i10] = i11 + 1;
                decodeNum[i11] = i8;
            }
        }
        decode.setMaxNum(i2);
    }

    protected int decodeNumber(Decode decode) {
        Unpack20 unpack20;
        long j = getbits() & 65534;
        int[] decodeLen = decode.getDecodeLen();
        int i = 8;
        if (j < decodeLen[8]) {
            if (j < decodeLen[4]) {
                if (j < decodeLen[2]) {
                    if (j < decodeLen[1]) {
                        unpack20 = this;
                        i = 1;
                    } else {
                        unpack20 = this;
                        i = 2;
                    }
                } else if (j < decodeLen[3]) {
                    unpack20 = this;
                    i = 3;
                } else {
                    unpack20 = this;
                    i = 4;
                }
            } else if (j < decodeLen[6]) {
                if (j < decodeLen[5]) {
                    unpack20 = this;
                    i = 5;
                } else {
                    unpack20 = this;
                    i = 6;
                }
            } else if (j < decodeLen[7]) {
                unpack20 = this;
                i = 7;
            } else {
                unpack20 = this;
            }
        } else if (j < decodeLen[12]) {
            if (j < decodeLen[10]) {
                if (j < decodeLen[9]) {
                    unpack20 = this;
                    i = 9;
                } else {
                    unpack20 = this;
                    i = 10;
                }
            } else if (j < decodeLen[11]) {
                unpack20 = this;
                i = 11;
            } else {
                unpack20 = this;
                i = 12;
            }
        } else if (j >= decodeLen[14]) {
            i = 15;
            unpack20 = this;
        } else if (j < decodeLen[13]) {
            unpack20 = this;
            i = 13;
        } else {
            unpack20 = this;
            i = 14;
        }
        unpack20.addbits(i);
        int i2 = decode.getDecodePos()[i] + ((((int) j) - decodeLen[i - 1]) >>> (16 - i));
        if (i2 >= decode.getMaxNum()) {
            i2 = 0;
        }
        return decode.getDecodeNum()[i2];
    }

    protected boolean ReadTables20() throws RarException, IOException {
        int i;
        int i2;
        byte[] bArr = new byte[19];
        byte[] bArr2 = new byte[1028];
        int i3 = 0;
        if (this.inAddr > this.readTop - 25 && !unpReadBuf()) {
            return false;
        }
        int i4 = getbits();
        this.UnpAudioBlock = 32768 & i4;
        if ((i4 & 16384) == 0) {
            Arrays.fill(this.UnpOldTable20, (byte) 0);
        }
        addbits(2);
        if (this.UnpAudioBlock != 0) {
            int i5 = ((i4 >>> 12) & 3) + 1;
            this.UnpChannels = i5;
            if (this.UnpCurChannel >= i5) {
                this.UnpCurChannel = 0;
            }
            addbits(2);
            i = this.UnpChannels * 257;
        } else {
            i = 374;
        }
        for (int i6 = 0; i6 < 19; i6++) {
            bArr[i6] = (byte) (getbits() >>> 12);
            addbits(4);
        }
        makeDecodeTables(bArr, 0, this.BD, 19);
        int i7 = 0;
        while (i7 < i) {
            if (this.inAddr > this.readTop - 5 && !unpReadBuf()) {
                return false;
            }
            int iDecodeNumber = decodeNumber(this.BD);
            if (iDecodeNumber < 16) {
                bArr2[i7] = (byte) ((iDecodeNumber + this.UnpOldTable20[i7]) & 15);
                i7++;
            } else if (iDecodeNumber == 16) {
                int i8 = (getbits() >>> 14) + 3;
                addbits(2);
                while (true) {
                    int i9 = i8 - 1;
                    if (i8 <= 0 || i7 >= i) {
                        break;
                    }
                    bArr2[i7] = bArr2[i7 - 1];
                    i7++;
                    i8 = i9;
                }
            } else {
                if (iDecodeNumber == 17) {
                    i2 = (getbits() >>> 13) + 3;
                    addbits(3);
                } else {
                    i2 = (getbits() >>> 9) + 11;
                    addbits(7);
                }
                while (true) {
                    int i10 = i2 - 1;
                    if (i2 <= 0 || i7 >= i) {
                        break;
                    }
                    bArr2[i7] = 0;
                    i7++;
                    i2 = i10;
                }
            }
        }
        if (this.inAddr > this.readTop) {
            return true;
        }
        if (this.UnpAudioBlock != 0) {
            for (int i11 = 0; i11 < this.UnpChannels; i11++) {
                makeDecodeTables(bArr2, i11 * 257, this.MD[i11], 257);
            }
        } else {
            makeDecodeTables(bArr2, 0, this.LD, Compress.NC20);
            makeDecodeTables(bArr2, Compress.NC20, this.DD, 48);
            makeDecodeTables(bArr2, 346, this.RD, 28);
        }
        while (true) {
            byte[] bArr3 = this.UnpOldTable20;
            if (i3 >= bArr3.length) {
                return true;
            }
            bArr3[i3] = bArr2[i3];
            i3++;
        }
    }

    protected void unpInitData20(boolean z) {
        if (z) {
            return;
        }
        this.UnpCurChannel = 0;
        this.UnpChannelDelta = 0;
        this.UnpChannels = 1;
        Arrays.fill(this.AudV, new AudioVariables());
        Arrays.fill(this.UnpOldTable20, (byte) 0);
    }

    protected void ReadLastTables() throws RarException, IOException {
        if (this.readTop >= this.inAddr + 5) {
            if (this.UnpAudioBlock != 0) {
                if (decodeNumber(this.MD[this.UnpCurChannel]) == 256) {
                    ReadTables20();
                }
            } else if (decodeNumber(this.LD) == 269) {
                ReadTables20();
            }
        }
    }

    protected byte DecodeAudio(int i) {
        AudioVariables audioVariables = this.AudV[this.UnpCurChannel];
        audioVariables.setByteCount(audioVariables.getByteCount() + 1);
        audioVariables.setD4(audioVariables.getD3());
        audioVariables.setD3(audioVariables.getD2());
        audioVariables.setD2(audioVariables.getLastDelta() - audioVariables.getD1());
        audioVariables.setD1(audioVariables.getLastDelta());
        int lastChar = ((((((audioVariables.getLastChar() * 8) + (audioVariables.getK1() * audioVariables.getD1())) + ((audioVariables.getK2() * audioVariables.getD2()) + (audioVariables.getK3() * audioVariables.getD3()))) + ((audioVariables.getK4() * audioVariables.getD4()) + (audioVariables.getK5() * this.UnpChannelDelta))) >>> 3) & 255) - i;
        int i2 = ((byte) i) << 3;
        int[] dif = audioVariables.getDif();
        dif[0] = dif[0] + Math.abs(i2);
        int[] dif2 = audioVariables.getDif();
        dif2[1] = dif2[1] + Math.abs(i2 - audioVariables.getD1());
        int[] dif3 = audioVariables.getDif();
        dif3[2] = dif3[2] + Math.abs(audioVariables.getD1() + i2);
        int[] dif4 = audioVariables.getDif();
        dif4[3] = dif4[3] + Math.abs(i2 - audioVariables.getD2());
        int[] dif5 = audioVariables.getDif();
        dif5[4] = dif5[4] + Math.abs(audioVariables.getD2() + i2);
        int[] dif6 = audioVariables.getDif();
        dif6[5] = dif6[5] + Math.abs(i2 - audioVariables.getD3());
        int[] dif7 = audioVariables.getDif();
        dif7[6] = dif7[6] + Math.abs(audioVariables.getD3() + i2);
        int[] dif8 = audioVariables.getDif();
        dif8[7] = dif8[7] + Math.abs(i2 - audioVariables.getD4());
        int[] dif9 = audioVariables.getDif();
        dif9[8] = dif9[8] + Math.abs(audioVariables.getD4() + i2);
        int[] dif10 = audioVariables.getDif();
        dif10[9] = dif10[9] + Math.abs(i2 - this.UnpChannelDelta);
        int[] dif11 = audioVariables.getDif();
        dif11[10] = dif11[10] + Math.abs(i2 + this.UnpChannelDelta);
        audioVariables.setLastDelta((byte) (lastChar - audioVariables.getLastChar()));
        this.UnpChannelDelta = audioVariables.getLastDelta();
        audioVariables.setLastChar(lastChar);
        if ((audioVariables.getByteCount() & 31) == 0) {
            int i3 = audioVariables.getDif()[0];
            audioVariables.getDif()[0] = 0;
            int i4 = 0;
            for (int i5 = 1; i5 < audioVariables.getDif().length; i5++) {
                if (audioVariables.getDif()[i5] < i3) {
                    i3 = audioVariables.getDif()[i5];
                    i4 = i5;
                }
                audioVariables.getDif()[i5] = 0;
            }
            switch (i4) {
                case 1:
                    if (audioVariables.getK1() >= -16) {
                        audioVariables.setK1(audioVariables.getK1() - 1);
                    }
                    break;
                case 2:
                    if (audioVariables.getK1() < 16) {
                        audioVariables.setK1(audioVariables.getK1() + 1);
                    }
                    break;
                case 3:
                    if (audioVariables.getK2() >= -16) {
                        audioVariables.setK2(audioVariables.getK2() - 1);
                    }
                    break;
                case 4:
                    if (audioVariables.getK2() < 16) {
                        audioVariables.setK2(audioVariables.getK2() + 1);
                    }
                    break;
                case 5:
                    if (audioVariables.getK3() >= -16) {
                        audioVariables.setK3(audioVariables.getK3() - 1);
                    }
                    break;
                case 6:
                    if (audioVariables.getK3() < 16) {
                        audioVariables.setK3(audioVariables.getK3() + 1);
                    }
                    break;
                case 7:
                    if (audioVariables.getK4() >= -16) {
                        audioVariables.setK4(audioVariables.getK4() - 1);
                    }
                    break;
                case 8:
                    if (audioVariables.getK4() < 16) {
                        audioVariables.setK4(audioVariables.getK4() + 1);
                    }
                    break;
                case 9:
                    if (audioVariables.getK5() >= -16) {
                        audioVariables.setK5(audioVariables.getK5() - 1);
                    }
                    break;
                case 10:
                    if (audioVariables.getK5() < 16) {
                        audioVariables.setK5(audioVariables.getK5() + 1);
                    }
                    break;
            }
        }
        return (byte) lastChar;
    }
}
