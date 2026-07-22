package org.apache.tools.bzip2;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;

/* JADX INFO: loaded from: classes3.dex */
public class CBZip2InputStream extends InputStream implements BZip2Constants {
    private static final int EOF = 0;
    private static final int NO_RAND_PART_A_STATE = 5;
    private static final int NO_RAND_PART_B_STATE = 6;
    private static final int NO_RAND_PART_C_STATE = 7;
    private static final int RAND_PART_A_STATE = 2;
    private static final int RAND_PART_B_STATE = 3;
    private static final int RAND_PART_C_STATE = 4;
    private static final int START_BLOCK_STATE = 1;
    private boolean blockRandomised;
    private int blockSize100k;
    private int bsBuff;
    private int bsLive;
    private int computedBlockCRC;
    private int computedCombinedCRC;
    private final CRC crc;
    private int currentChar;
    private int currentState;
    private Data data;
    private final boolean decompressConcatenated;
    private InputStream in;
    private int last;
    private int nInUse;
    private int origPtr;
    private int storedBlockCRC;
    private int storedCombinedCRC;
    private int su_ch2;
    private int su_chPrev;
    private int su_count;
    private int su_i2;
    private int su_j2;
    private int su_rNToGo;
    private int su_rTPos;
    private int su_tPos;
    private char su_z;

    public CBZip2InputStream(InputStream inputStream) throws IOException {
        this(inputStream, false);
    }

    public CBZip2InputStream(InputStream inputStream, boolean z) throws IOException {
        this.crc = new CRC();
        this.currentChar = -1;
        this.currentState = 1;
        this.in = inputStream;
        this.decompressConcatenated = z;
        init(true);
        initBlock();
        setupBlock();
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        if (this.in != null) {
            return read0();
        }
        throw new IOException("stream closed");
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (i < 0) {
            throw new IndexOutOfBoundsException("offs(" + i + ") < 0.");
        }
        if (i2 < 0) {
            throw new IndexOutOfBoundsException("len(" + i2 + ") < 0.");
        }
        int i3 = i + i2;
        if (i3 > bArr.length) {
            throw new IndexOutOfBoundsException("offs(" + i + ") + len(" + i2 + ") > dest.length(" + bArr.length + ").");
        }
        if (this.in == null) {
            throw new IOException("stream closed");
        }
        int i4 = i;
        while (i4 < i3) {
            int i5 = read0();
            if (i5 < 0) {
                break;
            }
            bArr[i4] = (byte) i5;
            i4++;
        }
        if (i4 == i) {
            return -1;
        }
        return i4 - i;
    }

    private void makeMaps() {
        boolean[] zArr = this.data.inUse;
        byte[] bArr = this.data.seqToUnseq;
        int i = 0;
        for (int i2 = 0; i2 < 256; i2++) {
            if (zArr[i2]) {
                bArr[i] = (byte) i2;
                i++;
            }
        }
        this.nInUse = i;
    }

    private int read0() throws IOException {
        int i = this.currentChar;
        switch (this.currentState) {
            case 0:
                return -1;
            case 1:
                throw new IllegalStateException();
            case 2:
                throw new IllegalStateException();
            case 3:
                setupRandPartB();
                return i;
            case 4:
                setupRandPartC();
                return i;
            case 5:
                throw new IllegalStateException();
            case 6:
                setupNoRandPartB();
                return i;
            case 7:
                setupNoRandPartC();
                return i;
            default:
                throw new IllegalStateException();
        }
    }

    private boolean init(boolean z) throws IOException {
        InputStream inputStream = this.in;
        if (inputStream == null) {
            throw new IOException("No InputStream");
        }
        if (z) {
            if (inputStream.available() == 0) {
                throw new IOException("Empty InputStream");
            }
        } else {
            int i = inputStream.read();
            if (i == -1) {
                return false;
            }
            int i2 = this.in.read();
            if (i != 66 || i2 != 90) {
                throw new IOException("Garbage after a valid BZip2 stream");
            }
        }
        if (this.in.read() != 104) {
            throw new IOException(z ? "Stream is not in the BZip2 format" : "Garbage after a valid BZip2 stream");
        }
        int i3 = this.in.read();
        if (i3 < 49 || i3 > 57) {
            throw new IOException("Stream is not BZip2 formatted: illegal blocksize " + ((char) i3));
        }
        this.blockSize100k = i3 - 48;
        this.bsLive = 0;
        this.computedCombinedCRC = 0;
        return true;
    }

    private void initBlock() throws IOException {
        do {
            char cBsGetUByte = bsGetUByte();
            char cBsGetUByte2 = bsGetUByte();
            char cBsGetUByte3 = bsGetUByte();
            char cBsGetUByte4 = bsGetUByte();
            char cBsGetUByte5 = bsGetUByte();
            char cBsGetUByte6 = bsGetUByte();
            if (cBsGetUByte != 23 || cBsGetUByte2 != 'r' || cBsGetUByte3 != 'E' || cBsGetUByte4 != '8' || cBsGetUByte5 != 'P' || cBsGetUByte6 != 144) {
                if (cBsGetUByte != '1' || cBsGetUByte2 != 'A' || cBsGetUByte3 != 'Y' || cBsGetUByte4 != '&' || cBsGetUByte5 != 'S' || cBsGetUByte6 != 'Y') {
                    this.currentState = 0;
                    throw new IOException("bad block header");
                }
                this.storedBlockCRC = bsGetInt();
                this.blockRandomised = bsR(1) == 1;
                if (this.data == null) {
                    this.data = new Data(this.blockSize100k);
                }
                getAndMoveToFrontDecode();
                this.crc.initialiseCRC();
                this.currentState = 1;
                return;
            }
        } while (!complete());
    }

    private void endBlock() throws IOException {
        int finalCRC = this.crc.getFinalCRC();
        this.computedBlockCRC = finalCRC;
        int i = this.storedBlockCRC;
        if (i != finalCRC) {
            int i2 = this.storedCombinedCRC;
            int i3 = (i2 >>> 31) | (i2 << 1);
            this.computedCombinedCRC = i3;
            this.computedCombinedCRC = i3 ^ i;
            reportCRCError();
        }
        int i4 = this.computedCombinedCRC;
        int i5 = (i4 >>> 31) | (i4 << 1);
        this.computedCombinedCRC = i5;
        this.computedCombinedCRC = i5 ^ this.computedBlockCRC;
    }

    private boolean complete() throws IOException {
        int iBsGetInt = bsGetInt();
        this.storedCombinedCRC = iBsGetInt;
        this.currentState = 0;
        this.data = null;
        if (iBsGetInt != this.computedCombinedCRC) {
            reportCRCError();
        }
        return (this.decompressConcatenated && init(false)) ? false : true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        InputStream inputStream = this.in;
        if (inputStream != null) {
            try {
                if (inputStream != System.in) {
                    inputStream.close();
                }
            } finally {
                this.data = null;
                this.in = null;
            }
        }
    }

    private int bsR(int i) throws IOException {
        int i2 = this.bsLive;
        int i3 = this.bsBuff;
        if (i2 < i) {
            InputStream inputStream = this.in;
            do {
                int i4 = inputStream.read();
                if (i4 < 0) {
                    throw new IOException("unexpected end of stream");
                }
                i3 = (i3 << 8) | i4;
                i2 += 8;
            } while (i2 < i);
            this.bsBuff = i3;
        }
        int i5 = i2 - i;
        this.bsLive = i5;
        return ((1 << i) - 1) & (i3 >> i5);
    }

    private boolean bsGetBit() throws IOException {
        int i = this.bsLive;
        int i2 = this.bsBuff;
        if (i < 1) {
            int i3 = this.in.read();
            if (i3 < 0) {
                throw new IOException("unexpected end of stream");
            }
            i2 = (i2 << 8) | i3;
            i += 8;
            this.bsBuff = i2;
        }
        int i4 = i - 1;
        this.bsLive = i4;
        return ((i2 >> i4) & 1) != 0;
    }

    private char bsGetUByte() throws IOException {
        return (char) bsR(8);
    }

    private int bsGetInt() throws IOException {
        return bsR(8) | (((((bsR(8) << 8) | bsR(8)) << 8) | bsR(8)) << 8);
    }

    private static void hbCreateDecodeTables(int[] iArr, int[] iArr2, int[] iArr3, char[] cArr, int i, int i2, int i3) {
        int i4 = 0;
        int i5 = 0;
        for (int i6 = i; i6 <= i2; i6++) {
            for (int i7 = 0; i7 < i3; i7++) {
                if (cArr[i7] == i6) {
                    iArr3[i5] = i7;
                    i5++;
                }
            }
        }
        int i8 = 23;
        while (true) {
            i8--;
            if (i8 <= 0) {
                break;
            }
            iArr2[i8] = 0;
            iArr[i8] = 0;
        }
        for (int i9 = 0; i9 < i3; i9++) {
            int i10 = cArr[i9] + 1;
            iArr2[i10] = iArr2[i10] + 1;
        }
        int i11 = iArr2[0];
        for (int i12 = 1; i12 < 23; i12++) {
            i11 += iArr2[i12];
            iArr2[i12] = i11;
        }
        int i13 = iArr2[i];
        int i14 = i;
        while (i14 <= i2) {
            int i15 = i14 + 1;
            int i16 = iArr2[i15];
            int i17 = i4 + (i16 - i13);
            iArr[i14] = i17 - 1;
            i4 = i17 << 1;
            i14 = i15;
            i13 = i16;
        }
        for (int i18 = i + 1; i18 <= i2; i18++) {
            iArr2[i18] = ((iArr[i18 - 1] + 1) << 1) - iArr2[i18];
        }
    }

    private void recvDecodingTables() throws IOException {
        Data data = this.data;
        boolean[] zArr = data.inUse;
        byte[] bArr = data.recvDecodingTables_pos;
        byte[] bArr2 = data.selector;
        byte[] bArr3 = data.selectorMtf;
        int i = 0;
        for (int i2 = 0; i2 < 16; i2++) {
            if (bsGetBit()) {
                i |= 1 << i2;
            }
        }
        int i3 = 256;
        while (true) {
            i3--;
            if (i3 < 0) {
                break;
            } else {
                zArr[i3] = false;
            }
        }
        for (int i4 = 0; i4 < 16; i4++) {
            if (((1 << i4) & i) != 0) {
                int i5 = i4 << 4;
                for (int i6 = 0; i6 < 16; i6++) {
                    if (bsGetBit()) {
                        zArr[i5 + i6] = true;
                    }
                }
            }
        }
        makeMaps();
        int i7 = this.nInUse + 2;
        int iBsR = bsR(3);
        int iBsR2 = bsR(15);
        for (int i8 = 0; i8 < iBsR2; i8++) {
            int i9 = 0;
            while (bsGetBit()) {
                i9++;
            }
            bArr3[i8] = (byte) i9;
        }
        int i10 = iBsR;
        while (true) {
            i10--;
            if (i10 < 0) {
                break;
            } else {
                bArr[i10] = (byte) i10;
            }
        }
        for (int i11 = 0; i11 < iBsR2; i11++) {
            int i12 = bArr3[i11] & 255;
            byte b = bArr[i12];
            while (i12 > 0) {
                bArr[i12] = bArr[i12 - 1];
                i12--;
            }
            bArr[0] = b;
            bArr2[i11] = b;
        }
        char[][] cArr = data.temp_charArray2d;
        for (int i13 = 0; i13 < iBsR; i13++) {
            int iBsR3 = bsR(5);
            char[] cArr2 = cArr[i13];
            for (int i14 = 0; i14 < i7; i14++) {
                while (bsGetBit()) {
                    iBsR3 += bsGetBit() ? -1 : 1;
                }
                cArr2[i14] = (char) iBsR3;
            }
        }
        createHuffmanDecodingTables(i7, iBsR);
    }

    private void createHuffmanDecodingTables(int i, int i2) {
        Data data = this.data;
        char[][] cArr = data.temp_charArray2d;
        int[] iArr = data.minLens;
        int[][] iArr2 = data.limit;
        int[][] iArr3 = data.base;
        int[][] iArr4 = data.perm;
        for (int i3 = 0; i3 < i2; i3++) {
            char c = ' ';
            char[] cArr2 = cArr[i3];
            int i4 = i;
            char c2 = 0;
            while (true) {
                i4--;
                if (i4 >= 0) {
                    char c3 = cArr2[i4];
                    if (c3 > c2) {
                        c2 = c3;
                    }
                    if (c3 < c) {
                        c = c3;
                    }
                }
            }
            hbCreateDecodeTables(iArr2[i3], iArr3[i3], iArr4[i3], cArr[i3], c, c2, i);
            iArr[i3] = c;
        }
    }

    private void getAndMoveToFrontDecode() throws IOException {
        int i;
        int i2;
        char c;
        int i3;
        CBZip2InputStream cBZip2InputStream = this;
        cBZip2InputStream.origPtr = cBZip2InputStream.bsR(24);
        recvDecodingTables();
        InputStream inputStream = cBZip2InputStream.in;
        Data data = cBZip2InputStream.data;
        byte[] bArr = data.ll8;
        int[] iArr = data.unzftab;
        byte[] bArr2 = data.selector;
        byte[] bArr3 = data.seqToUnseq;
        char[] cArr = data.getAndMoveToFrontDecode_yy;
        int[] iArr2 = data.minLens;
        int[][] iArr3 = data.limit;
        int[][] iArr4 = data.base;
        int[][] iArr5 = data.perm;
        int i4 = cBZip2InputStream.blockSize100k * 100000;
        int i5 = 256;
        while (true) {
            i5--;
            if (i5 < 0) {
                break;
            }
            cArr[i5] = (char) i5;
            iArr[i5] = 0;
        }
        int i6 = cBZip2InputStream.nInUse + 1;
        int andMoveToFrontDecode0 = cBZip2InputStream.getAndMoveToFrontDecode0(0);
        int i7 = cBZip2InputStream.bsBuff;
        int i8 = cBZip2InputStream.bsLive;
        int i9 = bArr2[0] & 255;
        int[] iArr6 = iArr4[i9];
        int[] iArr7 = iArr3[i9];
        int[] iArr8 = iArr5[i9];
        int i10 = 0;
        int i11 = i8;
        int i12 = andMoveToFrontDecode0;
        int i13 = 49;
        int i14 = -1;
        int i15 = iArr2[i9];
        int i16 = i7;
        while (i12 != i6) {
            int i17 = i6;
            int i18 = i16;
            if (i12 == 0 || i12 == 1) {
                int i19 = 1;
                int i20 = -1;
                while (true) {
                    if (i12 == 0) {
                        i20 += i19;
                        i = i14;
                    } else {
                        i = i14;
                        if (i12 == 1) {
                            i20 += i19 << 1;
                        } else {
                            int[][] iArr9 = iArr5;
                            byte[] bArr4 = bArr2;
                            byte b = bArr3[cArr[0]];
                            int i21 = b & 255;
                            iArr[i21] = iArr[i21] + i20 + 1;
                            i14 = i;
                            while (true) {
                                int i22 = i20 - 1;
                                if (i20 < 0) {
                                    break;
                                }
                                i14++;
                                bArr[i14] = b;
                                i20 = i22;
                            }
                            if (i14 >= i4) {
                                throw new IOException("block overrun");
                            }
                            cBZip2InputStream = this;
                            i6 = i17;
                            i16 = i18;
                            iArr5 = iArr9;
                            bArr2 = bArr4;
                        }
                    }
                    if (i13 == 0) {
                        i10++;
                        int i23 = bArr2[i10] & 255;
                        iArr6 = iArr4[i23];
                        iArr7 = iArr3[i23];
                        iArr8 = iArr5[i23];
                        i2 = iArr2[i23];
                        i13 = 49;
                    } else {
                        i13--;
                        i2 = i15;
                    }
                    int i24 = i11;
                    while (i24 < i2) {
                        int i25 = inputStream.read();
                        if (i25 < 0) {
                            throw new IOException("unexpected end of stream");
                        }
                        i18 = (i18 << 8) | i25;
                        i24 += 8;
                    }
                    int i26 = i24 - i2;
                    int[][] iArr10 = iArr5;
                    i11 = i26;
                    int i27 = (i18 >> i26) & ((1 << i2) - 1);
                    int i28 = i2;
                    while (i27 > iArr7[i28]) {
                        int i29 = i28 + 1;
                        byte[] bArr5 = bArr2;
                        int i30 = i11;
                        while (i30 < 1) {
                            int i31 = inputStream.read();
                            if (i31 < 0) {
                                throw new IOException("unexpected end of stream");
                            }
                            i18 = (i18 << 8) | i31;
                            i30 += 8;
                        }
                        i11 = i30 - 1;
                        i27 = (i27 << 1) | ((i18 >> i11) & 1);
                        i28 = i29;
                        bArr2 = bArr5;
                    }
                    int i32 = iArr8[i27 - iArr6[i28]];
                    i19 <<= 1;
                    i15 = i2;
                    i14 = i;
                    i12 = i32;
                    iArr5 = iArr10;
                }
            } else {
                i14++;
                if (i14 >= i4) {
                    throw new IOException("block overrun");
                }
                int i33 = i12 - 1;
                char c2 = cArr[i33];
                int i34 = bArr3[c2] & 255;
                iArr[i34] = iArr[i34] + 1;
                bArr[i14] = bArr3[c2];
                if (i12 <= 16) {
                    while (i33 > 0) {
                        int i35 = i33 - 1;
                        cArr[i33] = cArr[i35];
                        i33 = i35;
                    }
                    c = 0;
                } else {
                    c = 0;
                    System.arraycopy(cArr, 0, cArr, 1, i33);
                }
                cArr[c] = c2;
                if (i13 == 0) {
                    i10++;
                    int i36 = bArr2[i10] & 255;
                    int[] iArr11 = iArr4[i36];
                    int[] iArr12 = iArr3[i36];
                    int[] iArr13 = iArr5[i36];
                    i3 = iArr2[i36];
                    iArr6 = iArr11;
                    iArr7 = iArr12;
                    iArr8 = iArr13;
                    i13 = 49;
                } else {
                    i13--;
                    i3 = i15;
                }
                int i37 = i11;
                while (i37 < i3) {
                    int i38 = inputStream.read();
                    if (i38 < 0) {
                        throw new IOException("unexpected end of stream");
                    }
                    i18 = (i18 << 8) | i38;
                    i37 += 8;
                }
                int i39 = i37 - i3;
                int i40 = (i18 >> i39) & ((1 << i3) - 1);
                i11 = i39;
                int i41 = i3;
                while (i40 > iArr7[i41]) {
                    i41++;
                    int i42 = i3;
                    int i43 = i11;
                    while (i43 < 1) {
                        int i44 = inputStream.read();
                        if (i44 < 0) {
                            throw new IOException("unexpected end of stream");
                        }
                        i18 = (i18 << 8) | i44;
                        i43 += 8;
                    }
                    i11 = i43 - 1;
                    i40 = (i40 << 1) | ((i18 >> i11) & 1);
                    i3 = i42;
                }
                int i45 = i3;
                i12 = iArr8[i40 - iArr6[i41]];
                cBZip2InputStream = this;
                i6 = i17;
                i16 = i18;
                i15 = i45;
            }
        }
        cBZip2InputStream.last = i14;
        cBZip2InputStream.bsLive = i11;
        cBZip2InputStream.bsBuff = i16;
    }

    private int getAndMoveToFrontDecode0(int i) throws IOException {
        InputStream inputStream = this.in;
        Data data = this.data;
        int i2 = data.selector[i] & 255;
        int[] iArr = data.limit[i2];
        int i3 = data.minLens[i2];
        int iBsR = bsR(i3);
        int i4 = this.bsLive;
        int i5 = this.bsBuff;
        while (iBsR > iArr[i3]) {
            i3++;
            while (i4 < 1) {
                int i6 = inputStream.read();
                if (i6 < 0) {
                    throw new IOException("unexpected end of stream");
                }
                i5 = (i5 << 8) | i6;
                i4 += 8;
            }
            i4--;
            iBsR = (iBsR << 1) | (1 & (i5 >> i4));
        }
        this.bsLive = i4;
        this.bsBuff = i5;
        return data.perm[i2][iBsR - data.base[i2][i3]];
    }

    private void setupBlock() throws IOException {
        Data data = this.data;
        if (data == null) {
            return;
        }
        int[] iArr = data.cftab;
        int[] iArrInitTT = this.data.initTT(this.last + 1);
        byte[] bArr = this.data.ll8;
        iArr[0] = 0;
        System.arraycopy(this.data.unzftab, 0, iArr, 1, 256);
        int i = iArr[0];
        for (int i2 = 1; i2 <= 256; i2++) {
            i += iArr[i2];
            iArr[i2] = i;
        }
        int i3 = this.last;
        for (int i4 = 0; i4 <= i3; i4++) {
            int i5 = bArr[i4] & 255;
            int i6 = iArr[i5];
            iArr[i5] = i6 + 1;
            iArrInitTT[i6] = i4;
        }
        int i7 = this.origPtr;
        if (i7 < 0 || i7 >= iArrInitTT.length) {
            throw new IOException("stream corrupted");
        }
        this.su_tPos = iArrInitTT[i7];
        this.su_count = 0;
        this.su_i2 = 0;
        this.su_ch2 = 256;
        if (this.blockRandomised) {
            this.su_rNToGo = 0;
            this.su_rTPos = 0;
            setupRandPartA();
            return;
        }
        setupNoRandPartA();
    }

    private void setupRandPartA() throws IOException {
        if (this.su_i2 <= this.last) {
            this.su_chPrev = this.su_ch2;
            int i = this.data.ll8[this.su_tPos] & 255;
            this.su_tPos = this.data.tt[this.su_tPos];
            int i2 = this.su_rNToGo;
            if (i2 == 0) {
                int[] iArr = BZip2Constants.rNums;
                int i3 = this.su_rTPos;
                this.su_rNToGo = iArr[i3] - 1;
                int i4 = i3 + 1;
                this.su_rTPos = i4;
                if (i4 == 512) {
                    this.su_rTPos = 0;
                }
            } else {
                this.su_rNToGo = i2 - 1;
            }
            int i5 = i ^ (this.su_rNToGo == 1 ? 1 : 0);
            this.su_ch2 = i5;
            this.su_i2++;
            this.currentChar = i5;
            this.currentState = 3;
            this.crc.updateCRC(i5);
            return;
        }
        endBlock();
        initBlock();
        setupBlock();
    }

    private void setupNoRandPartA() throws IOException {
        if (this.su_i2 <= this.last) {
            this.su_chPrev = this.su_ch2;
            int i = this.data.ll8[this.su_tPos] & 255;
            this.su_ch2 = i;
            this.su_tPos = this.data.tt[this.su_tPos];
            this.su_i2++;
            this.currentChar = i;
            this.currentState = 6;
            this.crc.updateCRC(i);
            return;
        }
        this.currentState = 5;
        endBlock();
        initBlock();
        setupBlock();
    }

    private void setupRandPartB() throws IOException {
        if (this.su_ch2 != this.su_chPrev) {
            this.currentState = 2;
            this.su_count = 1;
            setupRandPartA();
            return;
        }
        int i = this.su_count + 1;
        this.su_count = i;
        if (i >= 4) {
            this.su_z = (char) (this.data.ll8[this.su_tPos] & 255);
            this.su_tPos = this.data.tt[this.su_tPos];
            int i2 = this.su_rNToGo;
            if (i2 == 0) {
                int[] iArr = BZip2Constants.rNums;
                int i3 = this.su_rTPos;
                this.su_rNToGo = iArr[i3] - 1;
                int i4 = i3 + 1;
                this.su_rTPos = i4;
                if (i4 == 512) {
                    this.su_rTPos = 0;
                }
            } else {
                this.su_rNToGo = i2 - 1;
            }
            this.su_j2 = 0;
            this.currentState = 4;
            if (this.su_rNToGo == 1) {
                this.su_z = (char) (this.su_z ^ 1);
            }
            setupRandPartC();
            return;
        }
        this.currentState = 2;
        setupRandPartA();
    }

    private void setupRandPartC() throws IOException {
        if (this.su_j2 < this.su_z) {
            int i = this.su_ch2;
            this.currentChar = i;
            this.crc.updateCRC(i);
            this.su_j2++;
            return;
        }
        this.currentState = 2;
        this.su_i2++;
        this.su_count = 0;
        setupRandPartA();
    }

    private void setupNoRandPartB() throws IOException {
        if (this.su_ch2 != this.su_chPrev) {
            this.su_count = 1;
            setupNoRandPartA();
            return;
        }
        int i = this.su_count + 1;
        this.su_count = i;
        if (i >= 4) {
            this.su_z = (char) (this.data.ll8[this.su_tPos] & 255);
            this.su_tPos = this.data.tt[this.su_tPos];
            this.su_j2 = 0;
            setupNoRandPartC();
            return;
        }
        setupNoRandPartA();
    }

    private void setupNoRandPartC() throws IOException {
        if (this.su_j2 < this.su_z) {
            int i = this.su_ch2;
            this.currentChar = i;
            this.crc.updateCRC(i);
            this.su_j2++;
            this.currentState = 7;
            return;
        }
        this.su_i2++;
        this.su_count = 0;
        setupNoRandPartA();
    }

    private static final class Data {
        byte[] ll8;
        int[] tt;
        final boolean[] inUse = new boolean[256];
        final byte[] seqToUnseq = new byte[256];
        final byte[] selector = new byte[BZip2Constants.MAX_SELECTORS];
        final byte[] selectorMtf = new byte[BZip2Constants.MAX_SELECTORS];
        final int[] unzftab = new int[256];
        final int[][] limit = (int[][]) Array.newInstance((Class<?>) int.class, 6, 258);
        final int[][] base = (int[][]) Array.newInstance((Class<?>) int.class, 6, 258);
        final int[][] perm = (int[][]) Array.newInstance((Class<?>) int.class, 6, 258);
        final int[] minLens = new int[6];
        final int[] cftab = new int[257];
        final char[] getAndMoveToFrontDecode_yy = new char[256];
        final char[][] temp_charArray2d = (char[][]) Array.newInstance((Class<?>) char.class, 6, 258);
        final byte[] recvDecodingTables_pos = new byte[6];

        Data(int i) {
            this.ll8 = new byte[i * 100000];
        }

        final int[] initTT(int i) {
            int[] iArr = this.tt;
            if (iArr != null && iArr.length >= i) {
                return iArr;
            }
            int[] iArr2 = new int[i];
            this.tt = iArr2;
            return iArr2;
        }
    }

    private static void reportCRCError() throws IOException {
        System.err.println("BZip2 CRC error");
    }
}
