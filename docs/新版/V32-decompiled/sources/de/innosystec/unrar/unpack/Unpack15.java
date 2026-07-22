package de.innosystec.unrar.unpack;

import android.mtp.MtpConstants;
import android.os.BatteryStats;
import de.innosystec.unrar.exception.RarException;
import de.innosystec.unrar.unpack.decode.Compress;
import de.innosystec.unrar.unpack.vm.BitInput;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.net.telnet.TelnetCommand;
import org.apache.tools.zip.UnixStat;

/* JADX INFO: loaded from: classes2.dex */
public abstract class Unpack15 extends BitInput {
    private static int[] DecHf3 = null;
    private static int[] DecHf4 = null;
    private static int[] PosHf2 = null;
    private static int[] PosHf3 = null;
    private static int[] PosHf4 = null;
    private static final int STARTHF0 = 4;
    private static final int STARTHF1 = 5;
    private static final int STARTHF2 = 5;
    private static final int STARTHF3 = 6;
    private static final int STARTHF4 = 8;
    private static final int STARTL1 = 2;
    private static final int STARTL2 = 3;
    static int[] ShortLen1;
    static int[] ShortLen2;
    static int[] ShortXor1;
    static int[] ShortXor2;
    protected int AvrLn1;
    protected int AvrLn2;
    protected int AvrLn3;
    protected int AvrPlc;
    protected int AvrPlcB;
    protected int Buf60;
    protected int FlagBuf;
    protected int FlagsCnt;
    protected int LCount;
    protected int MaxDist3;
    protected int Nhfb;
    protected int Nlzb;
    protected int NumHuf;
    protected int StMode;
    protected long destUnpSize;
    protected int lastDist;
    protected int lastLength;
    protected int oldDistPtr;
    protected int readBorder;
    protected int readTop;
    protected boolean suspended;
    protected boolean unpAllBuf;
    protected ComprDataIO unpIO;
    protected int unpPtr;
    protected boolean unpSomeRead;
    protected byte[] window;
    protected int wrPtr;
    private static int[] DecL1 = {32768, UnixStat.LINK_FLAG, 49152, 53248, 57344, 59904, 60928, BatteryStats.HistoryItem.STATE_DATA_CONNECTION_MASK, 61952, 61952, 65535};
    private static int[] PosL1 = {0, 0, 0, 2, 3, 5, 7, 11, 16, 20, 24, 32, 32};
    private static int[] DecL2 = {UnixStat.LINK_FLAG, 49152, 53248, 57344, 59904, 60928, BatteryStats.HistoryItem.STATE_DATA_CONNECTION_MASK, 61952, 62016, 65535};
    private static int[] PosL2 = {0, 0, 0, 0, 5, 7, 9, 13, 18, 22, 26, 34, 36};
    private static int[] DecHf0 = {32768, 49152, 57344, 61952, 61952, 61952, 61952, 61952, 65535};
    private static int[] PosHf0 = {0, 0, 0, 0, 0, 8, 16, 24, 33, 33, 33, 33, 33};
    private static int[] DecHf1 = {8192, 49152, 57344, BatteryStats.HistoryItem.STATE_DATA_CONNECTION_MASK, 61952, 61952, 63456, 65535};
    private static int[] PosHf1 = {0, 0, 0, 0, 0, 0, 4, 44, 60, 76, 80, 80, 127};
    private static int[] DecHf2 = {4096, 9216, 32768, 49152, 64000, 65535, 65535, 65535};
    protected int[] oldDist = new int[4];
    protected int[] ChSet = new int[256];
    protected int[] ChSetA = new int[256];
    protected int[] ChSetB = new int[256];
    protected int[] ChSetC = new int[256];
    protected int[] Place = new int[256];
    protected int[] PlaceA = new int[256];
    protected int[] PlaceB = new int[256];
    protected int[] PlaceC = new int[256];
    protected int[] NToPl = new int[256];
    protected int[] NToPlB = new int[256];
    protected int[] NToPlC = new int[256];

    protected abstract void unpInitData(boolean z);

    static {
        int[] iArr = new int[13];
        iArr[6] = 2;
        iArr[7] = 7;
        iArr[8] = 53;
        iArr[9] = 117;
        iArr[10] = 233;
        PosHf2 = iArr;
        DecHf3 = new int[]{2048, 9216, 60928, 65152, 65535, 65535, 65535};
        int[] iArr2 = new int[13];
        iArr2[7] = 2;
        iArr2[8] = 16;
        iArr2[9] = 218;
        iArr2[10] = 251;
        PosHf3 = iArr2;
        DecHf4 = new int[]{65280, 65535, 65535, 65535, 65535, 65535};
        int[] iArr3 = new int[13];
        iArr3[9] = 255;
        PosHf4 = iArr3;
        ShortLen1 = new int[]{1, 3, 4, 4, 5, 6, 7, 8, 8, 4, 4, 5, 6, 6, 4, 0};
        ShortXor1 = new int[]{0, 160, 208, 224, 240, TelnetCommand.EL, 252, 254, 255, 192, 128, 144, 152, 156, 176};
        ShortLen2 = new int[]{2, 3, 3, 3, 4, 4, 5, 6, 6, 4, 4, 5, 6, 6, 4, 0};
        ShortXor2 = new int[]{0, 64, 96, 160, 208, 224, 240, TelnetCommand.EL, 252, 192, 128, 144, 152, 156, 176};
    }

    protected void unpack15(boolean z) throws RarException, IOException {
        if (this.suspended) {
            this.unpPtr = this.wrPtr;
        } else {
            unpInitData(z);
            oldUnpInitData(z);
            unpReadBuf();
            if (!z) {
                initHuff();
                this.unpPtr = 0;
            } else {
                this.unpPtr = this.wrPtr;
            }
            this.destUnpSize--;
        }
        if (this.destUnpSize >= 0) {
            getFlagsBuf();
            this.FlagsCnt = 8;
        }
        while (this.destUnpSize >= 0) {
            this.unpPtr &= Compress.MAXWINMASK;
            if (this.inAddr > this.readTop - 30 && !unpReadBuf()) {
                break;
            }
            int i = this.wrPtr;
            int i2 = this.unpPtr;
            if ((4194303 & (i - i2)) < 270 && i != i2) {
                oldUnpWriteBuf();
                if (this.suspended) {
                    return;
                }
            }
            if (this.StMode != 0) {
                huffDecode();
            } else {
                int i3 = this.FlagsCnt - 1;
                this.FlagsCnt = i3;
                if (i3 < 0) {
                    getFlagsBuf();
                    this.FlagsCnt = 7;
                }
                int i4 = this.FlagBuf;
                if ((i4 & 128) != 0) {
                    this.FlagBuf = i4 << 1;
                    if (this.Nlzb > this.Nhfb) {
                        longLZ();
                    } else {
                        huffDecode();
                    }
                } else {
                    this.FlagBuf = i4 << 1;
                    int i5 = this.FlagsCnt - 1;
                    this.FlagsCnt = i5;
                    if (i5 < 0) {
                        getFlagsBuf();
                        this.FlagsCnt = 7;
                    }
                    int i6 = this.FlagBuf;
                    if ((i6 & 128) != 0) {
                        this.FlagBuf = i6 << 1;
                        if (this.Nlzb > this.Nhfb) {
                            huffDecode();
                        } else {
                            longLZ();
                        }
                    } else {
                        this.FlagBuf = i6 << 1;
                        shortLZ();
                    }
                }
            }
        }
        oldUnpWriteBuf();
    }

    protected boolean unpReadBuf() throws RarException, IOException {
        int i = this.readTop - this.inAddr;
        if (i < 0) {
            return false;
        }
        if (this.inAddr > 16384) {
            if (i > 0) {
                System.arraycopy(this.inBuf, this.inAddr, this.inBuf, 0, i);
            }
            this.inAddr = 0;
            this.readTop = i;
        } else {
            i = this.readTop;
        }
        int iUnpRead = this.unpIO.unpRead(this.inBuf, i, (32768 - i) & (-16));
        if (iUnpRead > 0) {
            this.readTop += iUnpRead;
        }
        this.readBorder = this.readTop - 30;
        return iUnpRead != -1;
    }

    private int getShortLen1(int i) {
        return i == 1 ? this.Buf60 + 3 : ShortLen1[i];
    }

    private int getShortLen2(int i) {
        return i == 3 ? this.Buf60 + 3 : ShortLen2[i];
    }

    protected void shortLZ() {
        int i;
        this.NumHuf = 0;
        int iFgetbits = fgetbits();
        if (this.LCount == 2) {
            faddbits(1);
            if (iFgetbits >= 32768) {
                oldCopyString(this.lastDist, this.lastLength);
                return;
            } else {
                iFgetbits <<= 1;
                this.LCount = 0;
            }
        }
        int i2 = iFgetbits >>> 8;
        if (this.AvrLn1 < 37) {
            i = 0;
            while (((ShortXor1[i] ^ i2) & (~(255 >>> getShortLen1(i)))) != 0) {
                i++;
            }
            faddbits(getShortLen1(i));
        } else {
            i = 0;
            while (((ShortXor2[i] ^ i2) & (~(255 >> getShortLen2(i)))) != 0) {
                i++;
            }
            faddbits(getShortLen2(i));
        }
        if (i < 9) {
            this.LCount = 0;
            int i3 = this.AvrLn1 + i;
            this.AvrLn1 = i3;
            this.AvrLn1 = i3 - (i3 >> 4);
            int iDecodeNum = decodeNum(fgetbits(), 5, DecHf2, PosHf2) & 255;
            int[] iArr = this.ChSetA;
            int i4 = iArr[iDecodeNum];
            int i5 = iDecodeNum - 1;
            if (i5 != -1) {
                int[] iArr2 = this.PlaceA;
                iArr2[i4] = iArr2[i4] - 1;
                int i6 = iArr[i5];
                iArr2[i6] = iArr2[i6] + 1;
                iArr[i5 + 1] = i6;
                iArr[i5] = i4;
            }
            int i7 = i + 2;
            int[] iArr3 = this.oldDist;
            int i8 = this.oldDistPtr;
            int i9 = i8 + 1;
            this.oldDistPtr = i9;
            int i10 = i4 + 1;
            iArr3[i8] = i10;
            this.oldDistPtr = i9 & 3;
            this.lastLength = i7;
            this.lastDist = i10;
            oldCopyString(i10, i7);
            return;
        }
        if (i == 9) {
            this.LCount++;
            oldCopyString(this.lastDist, this.lastLength);
            return;
        }
        if (i == 14) {
            this.LCount = 0;
            int iDecodeNum2 = decodeNum(fgetbits(), 3, DecL2, PosL2) + 5;
            int iFgetbits2 = (fgetbits() >> 1) | 32768;
            faddbits(15);
            this.lastLength = iDecodeNum2;
            this.lastDist = iFgetbits2;
            oldCopyString(iFgetbits2, iDecodeNum2);
            return;
        }
        this.LCount = 0;
        int i11 = this.oldDist[(this.oldDistPtr - (i - 9)) & 3];
        int iDecodeNum3 = decodeNum(fgetbits(), 2, DecL1, PosL1) + 2;
        if (iDecodeNum3 == 257 && i == 10) {
            this.Buf60 ^= 1;
            return;
        }
        if (i11 > 256) {
            iDecodeNum3++;
        }
        if (i11 >= this.MaxDist3) {
            iDecodeNum3++;
        }
        int[] iArr4 = this.oldDist;
        int i12 = this.oldDistPtr;
        int i13 = i12 + 1;
        this.oldDistPtr = i13;
        iArr4[i12] = i11;
        this.oldDistPtr = i13 & 3;
        this.lastLength = iDecodeNum3;
        this.lastDist = i11;
        oldCopyString(i11, iDecodeNum3);
    }

    protected void longLZ() {
        int iDecodeNum;
        int[] iArr;
        int i;
        int i2;
        int i3 = 0;
        this.NumHuf = 0;
        int i4 = this.Nlzb + 16;
        this.Nlzb = i4;
        if (i4 > 255) {
            this.Nlzb = 144;
            this.Nhfb >>>= 1;
        }
        int i5 = this.AvrLn2;
        int iFgetbits = fgetbits();
        int i6 = this.AvrLn2;
        if (i6 >= 122) {
            iFgetbits = decodeNum(iFgetbits, 3, DecL2, PosL2);
        } else if (i6 >= 64) {
            iFgetbits = decodeNum(iFgetbits, 2, DecL1, PosL1);
        } else if (iFgetbits < 256) {
            faddbits(16);
        } else {
            while (((iFgetbits << i3) & 32768) == 0) {
                i3++;
            }
            faddbits(i3 + 1);
            iFgetbits = i3;
        }
        int i7 = this.AvrLn2 + iFgetbits;
        this.AvrLn2 = i7;
        this.AvrLn2 = i7 - (i7 >>> 5);
        int iFgetbits2 = fgetbits();
        int i8 = this.AvrPlcB;
        if (i8 > 10495) {
            iDecodeNum = decodeNum(iFgetbits2, 5, DecHf2, PosHf2);
        } else if (i8 > 1791) {
            iDecodeNum = decodeNum(iFgetbits2, 5, DecHf1, PosHf1);
        } else {
            iDecodeNum = decodeNum(iFgetbits2, 4, DecHf0, PosHf0);
        }
        int i9 = this.AvrPlcB + iDecodeNum;
        this.AvrPlcB = i9;
        this.AvrPlcB = i9 - (i9 >> 8);
        while (true) {
            iArr = this.ChSetB;
            int i10 = iArr[iDecodeNum & 255];
            int[] iArr2 = this.NToPlB;
            i = i10 + 1;
            int i11 = i10 & 255;
            i2 = iArr2[i11];
            iArr2[i11] = i2 + 1;
            if ((i & 255) != 0) {
                break;
            } else {
                corrHuff(iArr, iArr2);
            }
        }
        iArr[iDecodeNum] = iArr[i2];
        iArr[i2] = i;
        int iFgetbits3 = ((65280 & i) | (fgetbits() >>> 8)) >>> 1;
        faddbits(7);
        int i12 = this.AvrLn3;
        if (iFgetbits != 1 && iFgetbits != 4) {
            if (iFgetbits == 0 && iFgetbits3 <= this.MaxDist3) {
                int i13 = i12 + 1;
                this.AvrLn3 = i13;
                this.AvrLn3 = i13 - (i13 >> 8);
            } else if (i12 > 0) {
                this.AvrLn3 = i12 - 1;
            }
        }
        int i14 = iFgetbits + 3;
        if (iFgetbits3 >= this.MaxDist3) {
            i14++;
        }
        if (iFgetbits3 <= 256) {
            i14 += 8;
        }
        if (i12 > 176 || (this.AvrPlc >= 10752 && i5 < 64)) {
            this.MaxDist3 = 32512;
        } else {
            this.MaxDist3 = MtpConstants.RESPONSE_OK;
        }
        int[] iArr3 = this.oldDist;
        int i15 = this.oldDistPtr;
        int i16 = i15 + 1;
        this.oldDistPtr = i16;
        iArr3[i15] = iFgetbits3;
        this.oldDistPtr = i16 & 3;
        this.lastLength = i14;
        this.lastDist = iFgetbits3;
        oldCopyString(iFgetbits3, i14);
    }

    protected void huffDecode() {
        int iDecodeNum;
        int iFgetbits = fgetbits();
        int i = this.AvrPlc;
        if (i > 30207) {
            iDecodeNum = decodeNum(iFgetbits, 8, DecHf4, PosHf4);
        } else if (i > 24063) {
            iDecodeNum = decodeNum(iFgetbits, 6, DecHf3, PosHf3);
        } else if (i > 13823) {
            iDecodeNum = decodeNum(iFgetbits, 5, DecHf2, PosHf2);
        } else if (i > 3583) {
            iDecodeNum = decodeNum(iFgetbits, 5, DecHf1, PosHf1);
        } else {
            iDecodeNum = decodeNum(iFgetbits, 4, DecHf0, PosHf0);
        }
        int i2 = iDecodeNum & 255;
        if (this.StMode != 0) {
            if (i2 == 0 && iFgetbits > 4095) {
                i2 = 256;
            }
            i2--;
            if (i2 == -1) {
                int iFgetbits2 = fgetbits();
                faddbits(1);
                if ((32768 & iFgetbits2) != 0) {
                    this.StMode = 0;
                    this.NumHuf = 0;
                    return;
                }
                int i3 = (iFgetbits2 & 16384) == 0 ? 3 : 4;
                faddbits(1);
                int iDecodeNum2 = (decodeNum(fgetbits(), 5, DecHf2, PosHf2) << 5) | (fgetbits() >>> 11);
                faddbits(5);
                oldCopyString(iDecodeNum2, i3);
                return;
            }
        } else {
            int i4 = this.NumHuf;
            this.NumHuf = i4 + 1;
            if (i4 >= 16 && this.FlagsCnt == 0) {
                this.StMode = 1;
            }
        }
        int i5 = this.AvrPlc + i2;
        this.AvrPlc = i5;
        this.AvrPlc = i5 - (i5 >>> 8);
        int i6 = this.Nhfb + 16;
        this.Nhfb = i6;
        if (i6 > 255) {
            this.Nhfb = 144;
            this.Nlzb >>>= 1;
        }
        byte[] bArr = this.window;
        int i7 = this.unpPtr;
        this.unpPtr = i7 + 1;
        bArr[i7] = (byte) (this.ChSet[i2] >>> 8);
        this.destUnpSize--;
        while (true) {
            int[] iArr = this.ChSet;
            int i8 = iArr[i2];
            int[] iArr2 = this.NToPl;
            int i9 = i8 + 1;
            int i10 = i8 & 255;
            int i11 = iArr2[i10];
            iArr2[i10] = i11 + 1;
            if ((i9 & 255) > 161) {
                corrHuff(iArr, iArr2);
            } else {
                iArr[i2] = iArr[i11];
                iArr[i11] = i9;
                return;
            }
        }
    }

    protected void getFlagsBuf() {
        int iDecodeNum = decodeNum(fgetbits(), 5, DecHf2, PosHf2);
        while (true) {
            int[] iArr = this.ChSetC;
            int i = iArr[iDecodeNum];
            this.FlagBuf = i >>> 8;
            int[] iArr2 = this.NToPlC;
            int i2 = i + 1;
            int i3 = i & 255;
            int i4 = iArr2[i3];
            iArr2[i3] = i4 + 1;
            if ((i2 & 255) == 0) {
                corrHuff(iArr, iArr2);
            } else {
                iArr[iDecodeNum] = iArr[i4];
                iArr[i4] = i2;
                return;
            }
        }
    }

    protected void oldUnpInitData(boolean z) {
        if (!z) {
            this.Buf60 = 0;
            this.NumHuf = 0;
            this.AvrLn3 = 0;
            this.AvrLn2 = 0;
            this.AvrLn1 = 0;
            this.AvrPlcB = 0;
            this.AvrPlc = 13568;
            this.MaxDist3 = MtpConstants.RESPONSE_OK;
            this.Nlzb = 128;
            this.Nhfb = 128;
        }
        this.FlagsCnt = 0;
        this.FlagBuf = 0;
        this.StMode = 0;
        this.LCount = 0;
        this.readTop = 0;
    }

    protected void initHuff() {
        for (int i = 0; i < 256; i++) {
            int[] iArr = this.Place;
            int[] iArr2 = this.PlaceA;
            this.PlaceB[i] = i;
            iArr2[i] = i;
            iArr[i] = i;
            int[] iArr3 = this.PlaceC;
            int i2 = ((~i) + 1) & 255;
            iArr3[i] = i2;
            int[] iArr4 = this.ChSet;
            int i3 = i << 8;
            this.ChSetB[i] = i3;
            iArr4[i] = i3;
            this.ChSetA[i] = i;
            this.ChSetC[i] = i2 << 8;
        }
        Arrays.fill(this.NToPl, 0);
        Arrays.fill(this.NToPlB, 0);
        Arrays.fill(this.NToPlC, 0);
        corrHuff(this.ChSetB, this.NToPlB);
    }

    protected void corrHuff(int[] iArr, int[] iArr2) {
        int i = 0;
        for (int i2 = 7; i2 >= 0; i2--) {
            int i3 = 0;
            while (i3 < 32) {
                iArr[i] = (iArr[i] & (-256)) | i2;
                i3++;
                i++;
            }
        }
        Arrays.fill(iArr2, 0);
        for (int i4 = 6; i4 >= 0; i4--) {
            iArr2[i4] = (7 - i4) * 32;
        }
    }

    protected void oldCopyString(int i, int i2) {
        this.destUnpSize -= (long) i2;
        while (true) {
            int i3 = i2 - 1;
            if (i2 == 0) {
                return;
            }
            byte[] bArr = this.window;
            int i4 = this.unpPtr;
            bArr[i4] = bArr[(i4 - i) & Compress.MAXWINMASK];
            this.unpPtr = (i4 + 1) & Compress.MAXWINMASK;
            i2 = i3;
        }
    }

    protected int decodeNum(int i, int i2, int[] iArr, int[] iArr2) {
        int i3 = i & 65520;
        int i4 = 0;
        while (iArr[i4] <= i3) {
            i2++;
            i4++;
        }
        faddbits(i2);
        return ((i3 - (i4 != 0 ? iArr[i4 - 1] : 0)) >>> (16 - i2)) + iArr2[i2];
    }

    protected void oldUnpWriteBuf() throws IOException {
        int i = this.unpPtr;
        int i2 = this.wrPtr;
        if (i != i2) {
            this.unpSomeRead = true;
        }
        if (i < i2) {
            this.unpIO.unpWrite(this.window, i2, (-i2) & Compress.MAXWINMASK);
            this.unpIO.unpWrite(this.window, 0, this.unpPtr);
            this.unpAllBuf = true;
        } else {
            this.unpIO.unpWrite(this.window, i2, i - i2);
        }
        this.wrPtr = this.unpPtr;
    }
}
