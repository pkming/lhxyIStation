package org.apache.tools.bzip2;

import android.os.Trace;
import java.util.BitSet;
import org.apache.tools.bzip2.CBZip2OutputStream;

/* JADX INFO: loaded from: classes3.dex */
class BlockSort {
    private static final int CLEARMASK = -2097153;
    private static final int DEPTH_THRESH = 10;
    private static final int FALLBACK_QSORT_SMALL_THRESH = 10;
    private static final int FALLBACK_QSORT_STACK_SIZE = 100;
    private static final int[] INCS = {1, 4, 13, 40, 121, 364, 1093, 3280, 9841, 29524, 88573, 265720, 797161, 2391484};
    private static final int QSORT_STACK_SIZE = 1000;
    private static final int SETMASK = 2097152;
    private static final int SMALL_THRESH = 20;
    private static final int STACK_SIZE = 1000;
    private static final int WORK_FACTOR = 30;
    private int[] eclass;
    private boolean firstAttempt;
    private final char[] quadrant;
    private int workDone;
    private int workLimit;
    private final int[] stack_ll = new int[1000];
    private final int[] stack_hh = new int[1000];
    private final int[] stack_dd = new int[1000];
    private final int[] mainSort_runningOrder = new int[256];
    private final int[] mainSort_copy = new int[256];
    private final boolean[] mainSort_bigDone = new boolean[256];
    private final int[] ftab = new int[65537];

    private int fmin(int i, int i2) {
        return i < i2 ? i : i2;
    }

    private static byte med3(byte b, byte b2, byte b3) {
        if (b < b2) {
            if (b2 >= b3) {
                if (b >= b3) {
                    return b;
                }
                return b3;
            }
            return b2;
        }
        if (b2 <= b3) {
            if (b <= b3) {
                return b;
            }
            return b3;
        }
        return b2;
    }

    BlockSort(CBZip2OutputStream.Data data) {
        this.quadrant = data.sfmap;
    }

    void blockSort(CBZip2OutputStream.Data data, int i) {
        this.workLimit = i * 30;
        this.workDone = 0;
        this.firstAttempt = true;
        if (i + 1 < 10000) {
            fallbackSort(data, i);
        } else {
            mainSort(data, i);
            if (this.firstAttempt && this.workDone > this.workLimit) {
                fallbackSort(data, i);
            }
        }
        int[] iArr = data.fmap;
        data.origPtr = -1;
        for (int i2 = 0; i2 <= i; i2++) {
            if (iArr[i2] == 0) {
                data.origPtr = i2;
                return;
            }
        }
    }

    final void fallbackSort(CBZip2OutputStream.Data data, int i) {
        int i2 = i + 1;
        data.block[0] = data.block[i2];
        fallbackSort(data.fmap, data.block, i2);
        for (int i3 = 0; i3 < i2; i3++) {
            data.fmap[i3] = r1[i3] - 1;
        }
        for (int i4 = 0; i4 < i2; i4++) {
            if (data.fmap[i4] == -1) {
                data.fmap[i4] = i;
                return;
            }
        }
    }

    private void fallbackSimpleSort(int[] iArr, int[] iArr2, int i, int i2) {
        if (i == i2) {
            return;
        }
        if (i2 - i > 3) {
            for (int i3 = i2 - 4; i3 >= i; i3--) {
                int i4 = iArr[i3];
                int i5 = iArr2[i4];
                int i6 = i3 + 4;
                while (i6 <= i2 && i5 > iArr2[iArr[i6]]) {
                    iArr[i6 - 4] = iArr[i6];
                    i6 += 4;
                }
                iArr[i6 - 4] = i4;
            }
        }
        for (int i7 = i2 - 1; i7 >= i; i7--) {
            int i8 = iArr[i7];
            int i9 = iArr2[i8];
            int i10 = i7 + 1;
            while (i10 <= i2 && i9 > iArr2[iArr[i10]]) {
                iArr[i10 - 1] = iArr[i10];
                i10++;
            }
            iArr[i10 - 1] = i8;
        }
    }

    private void fswap(int[] iArr, int i, int i2) {
        int i3 = iArr[i];
        iArr[i] = iArr[i2];
        iArr[i2] = i3;
    }

    private void fvswap(int[] iArr, int i, int i2, int i3) {
        while (i3 > 0) {
            fswap(iArr, i, i2);
            i++;
            i2++;
            i3--;
        }
    }

    private void fpush(int i, int i2, int i3) {
        this.stack_ll[i] = i2;
        this.stack_hh[i] = i3;
    }

    private int[] fpop(int i) {
        return new int[]{this.stack_ll[i], this.stack_hh[i]};
    }

    private void fallbackQSort3(int[] iArr, int[] iArr2, int i, int i2) {
        int i3;
        int i4;
        char c = 0;
        fpush(0, i, i2);
        long j = 0;
        int i5 = 1;
        long j2 = 0;
        int i6 = 1;
        while (i6 > 0) {
            i6--;
            int[] iArrFpop = fpop(i6);
            int i7 = iArrFpop[c];
            int i8 = iArrFpop[i5];
            if (i8 - i7 < 10) {
                fallbackSimpleSort(iArr, iArr2, i7, i8);
            } else {
                j2 = ((j2 * 7621) + 1) % Trace.TRACE_TAG_RS;
                long j3 = j2 % 3;
                if (j3 == j) {
                    i3 = iArr2[iArr[i7]];
                } else if (j3 == 1) {
                    i3 = iArr2[iArr[(i7 + i8) >>> i5]];
                } else {
                    i3 = iArr2[iArr[i8]];
                }
                long j4 = i3;
                int i9 = i8;
                int i10 = i9;
                int i11 = i7;
                int i12 = i11;
                while (true) {
                    if (i12 <= i9) {
                        int i13 = iArr2[iArr[i12]] - ((int) j4);
                        if (i13 == 0) {
                            fswap(iArr, i12, i11);
                            i11++;
                        } else if (i13 <= 0) {
                        }
                        i12++;
                    }
                    i4 = i10;
                    while (i12 <= i9) {
                        int i14 = iArr2[iArr[i9]] - ((int) j4);
                        if (i14 == 0) {
                            fswap(iArr, i9, i4);
                            i4--;
                            i9--;
                        } else if (i14 < 0) {
                            break;
                        } else {
                            i9--;
                        }
                    }
                    if (i12 > i9) {
                        break;
                    }
                    fswap(iArr, i12, i9);
                    i12++;
                    i9--;
                    i10 = i4;
                    i5 = 1;
                }
                if (i4 < i11) {
                    c = 0;
                    j = 0;
                    i5 = 1;
                } else {
                    int iFmin = fmin(i11 - i7, i12 - i11);
                    fvswap(iArr, i7, i12 - iFmin, iFmin);
                    int i15 = i8 - i4;
                    int i16 = i4 - i9;
                    int iFmin2 = fmin(i15, i16);
                    fvswap(iArr, i9 + 1, (i8 - iFmin2) + 1, iFmin2);
                    int i17 = ((i12 + i7) - i11) - 1;
                    int i18 = (i8 - i16) + 1;
                    if (i17 - i7 > i8 - i18) {
                        int i19 = i6 + 1;
                        fpush(i6, i7, i17);
                        fpush(i19, i18, i8);
                        i6 = i19 + 1;
                    } else {
                        int i20 = i6 + 1;
                        fpush(i6, i18, i8);
                        fpush(i20, i7, i17);
                        i6 = i20 + 1;
                    }
                    i5 = 1;
                    c = 0;
                    j = 0;
                }
            }
        }
    }

    private int[] getEclass() {
        int[] iArr = this.eclass;
        if (iArr != null) {
            return iArr;
        }
        int[] iArr2 = new int[this.quadrant.length / 2];
        this.eclass = iArr2;
        return iArr2;
    }

    final void fallbackSort(int[] iArr, byte[] bArr, int i) {
        int i2;
        int[] iArr2 = new int[257];
        int[] eclass = getEclass();
        for (int i3 = 0; i3 < i; i3++) {
            eclass[i3] = 0;
        }
        for (int i4 = 0; i4 < i; i4++) {
            int i5 = bArr[i4] & 255;
            iArr2[i5] = iArr2[i5] + 1;
        }
        for (int i6 = 1; i6 < 257; i6++) {
            iArr2[i6] = iArr2[i6] + iArr2[i6 - 1];
        }
        for (int i7 = 0; i7 < i; i7++) {
            int i8 = bArr[i7] & 255;
            int i9 = iArr2[i8] - 1;
            iArr2[i8] = i9;
            iArr[i9] = i7;
        }
        BitSet bitSet = new BitSet(i + 64);
        for (int i10 = 0; i10 < 256; i10++) {
            bitSet.set(iArr2[i10]);
        }
        for (int i11 = 0; i11 < 32; i11++) {
            int i12 = (i11 * 2) + i;
            bitSet.set(i12);
            bitSet.clear(i12 + 1);
        }
        int i13 = 1;
        do {
            int i14 = 0;
            for (int i15 = 0; i15 < i; i15++) {
                if (bitSet.get(i15)) {
                    i14 = i15;
                }
                int i16 = iArr[i15] - i13;
                if (i16 < 0) {
                    i16 += i;
                }
                eclass[i16] = i14;
            }
            int iNextSetBit = -1;
            i2 = 0;
            while (true) {
                int iNextClearBit = bitSet.nextClearBit(iNextSetBit + 1);
                int i17 = iNextClearBit - 1;
                if (i17 >= i || (iNextSetBit = bitSet.nextSetBit(iNextClearBit + 1) - 1) >= i) {
                    break;
                }
                if (iNextSetBit > i17) {
                    i2 += (iNextSetBit - i17) + 1;
                    fallbackQSort3(iArr, eclass, i17, iNextSetBit);
                    int i18 = -1;
                    while (i17 <= iNextSetBit) {
                        int i19 = eclass[iArr[i17]];
                        if (i18 != i19) {
                            bitSet.set(i17);
                            i18 = i19;
                        }
                        i17++;
                    }
                }
            }
            i13 *= 2;
            if (i13 > i) {
                return;
            }
        } while (i2 != 0);
    }

    /* JADX WARN: Code restructure failed: missing block: B:90:0x0166, code lost:
    
        r20 = r4;
        r6 = r24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:92:0x0172, code lost:
    
        r4 = r19;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean mainSimpleSort(org.apache.tools.bzip2.CBZip2OutputStream.Data r30, int r31, int r32, int r33, int r34) {
        /*
            Method dump skipped, instruction units count: 551
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.bzip2.BlockSort.mainSimpleSort(org.apache.tools.bzip2.CBZip2OutputStream$Data, int, int, int, int):boolean");
    }

    private static void vswap(int[] iArr, int i, int i2, int i3) {
        int i4 = i3 + i;
        while (i < i4) {
            int i5 = iArr[i];
            iArr[i] = iArr[i2];
            iArr[i2] = i5;
            i2++;
            i++;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x006e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void mainQSort3(org.apache.tools.bzip2.CBZip2OutputStream.Data r20, int r21, int r22, int r23, int r24) {
        /*
            Method dump skipped, instruction units count: 267
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.bzip2.BlockSort.mainQSort3(org.apache.tools.bzip2.CBZip2OutputStream$Data, int, int, int, int):void");
    }

    final void mainSort(CBZip2OutputStream.Data data, int i) {
        int i2;
        int i3;
        int[] iArr;
        int i4;
        int i5;
        int i6;
        int[] iArr2 = this.mainSort_runningOrder;
        int[] iArr3 = this.mainSort_copy;
        boolean[] zArr = this.mainSort_bigDone;
        int[] iArr4 = this.ftab;
        byte[] bArr = data.block;
        int[] iArr5 = data.fmap;
        char[] cArr = this.quadrant;
        int i7 = this.workLimit;
        boolean z = this.firstAttempt;
        int i8 = 65537;
        while (true) {
            i8--;
            if (i8 < 0) {
                break;
            } else {
                iArr4[i8] = 0;
            }
        }
        for (int i9 = 0; i9 < 20; i9++) {
            bArr[i + i9 + 2] = bArr[(i9 % (i + 1)) + 1];
        }
        int i10 = i + 20 + 1;
        while (true) {
            i10--;
            if (i10 < 0) {
                break;
            } else {
                cArr[i10] = 0;
            }
        }
        int i11 = i + 1;
        bArr[0] = bArr[i11];
        int i12 = 255;
        int i13 = bArr[0] & 255;
        int i14 = 0;
        while (i14 <= i) {
            i14++;
            int i15 = bArr[i14] & 255;
            int i16 = (i13 << 8) + i15;
            iArr4[i16] = iArr4[i16] + 1;
            i13 = i15;
        }
        for (int i17 = 1; i17 <= 65536; i17++) {
            iArr4[i17] = iArr4[i17] + iArr4[i17 - 1];
        }
        boolean z2 = true;
        int i18 = bArr[1] & 255;
        int i19 = 0;
        while (i19 < i) {
            int i20 = bArr[i19 + 2] & 255;
            int i21 = (i18 << 8) + i20;
            int i22 = iArr4[i21] - 1;
            iArr4[i21] = i22;
            iArr5[i22] = i19;
            i19++;
            i18 = i20;
            z2 = true;
        }
        int i23 = ((bArr[i11] & 255) << 8) + (bArr[z2 ? 1 : 0] & 255);
        int i24 = iArr4[i23] - 1;
        iArr4[i23] = i24;
        iArr5[i24] = i;
        int i25 = 256;
        while (true) {
            i25--;
            if (i25 < 0) {
                break;
            }
            zArr[i25] = false;
            iArr2[i25] = i25;
        }
        int i26 = 364;
        while (i26 != 1) {
            i26 /= 3;
            int i27 = i26;
            while (i27 <= i12) {
                int i28 = iArr2[i27];
                int i29 = iArr4[(i28 + 1) << 8] - iArr4[i28 << 8];
                int i30 = i26 - 1;
                int i31 = iArr2[i27 - i26];
                int i32 = i27;
                while (true) {
                    i6 = i7;
                    if (iArr4[(i31 + 1) << 8] - iArr4[i31 << 8] <= i29) {
                        break;
                    }
                    iArr2[i32] = i31;
                    int i33 = i32 - i26;
                    if (i33 <= i30) {
                        i32 = i33;
                        break;
                    } else {
                        i31 = iArr2[i33 - i26];
                        i32 = i33;
                        i7 = i6;
                    }
                }
                iArr2[i32] = i28;
                i27++;
                i7 = i6;
                i12 = 255;
            }
        }
        int i34 = i7;
        int i35 = 0;
        while (i35 <= i12) {
            int i36 = iArr2[i35];
            int i37 = 0;
            while (i37 <= i12) {
                int i38 = (i36 << 8) + i37;
                int i39 = iArr4[i38];
                if ((i39 & 2097152) != 2097152) {
                    int i40 = i39 & CLEARMASK;
                    int i41 = (iArr4[i38 + 1] & CLEARMASK) - 1;
                    if (i41 > i40) {
                        i5 = 2097152;
                        i2 = i37;
                        i3 = i34;
                        iArr = iArr2;
                        i4 = i35;
                        mainQSort3(data, i40, i41, 2, i);
                        if (z && this.workDone > i3) {
                            return;
                        }
                    } else {
                        i2 = i37;
                        i3 = i34;
                        i5 = 2097152;
                        iArr = iArr2;
                        i4 = i35;
                    }
                    iArr4[i38] = i39 | i5;
                } else {
                    i2 = i37;
                    i3 = i34;
                    iArr = iArr2;
                    i4 = i35;
                }
                i37 = i2 + 1;
                i35 = i4;
                iArr2 = iArr;
                i12 = 255;
                i34 = i3;
            }
            int i42 = i34;
            int[] iArr6 = iArr2;
            int i43 = i35;
            int i44 = 0;
            for (int i45 = i12; i44 <= i45; i45 = 255) {
                iArr3[i44] = iArr4[(i44 << 8) + i36] & CLEARMASK;
                i44++;
            }
            int i46 = i36 << 8;
            int i47 = iArr4[i46] & CLEARMASK;
            int i48 = (i36 + 1) << 8;
            int i49 = iArr4[i48] & CLEARMASK;
            while (i47 < i49) {
                int i50 = iArr5[i47];
                int i51 = i49;
                int i52 = bArr[i50] & 255;
                if (!zArr[i52]) {
                    iArr5[iArr3[i52]] = i50 == 0 ? i : i50 - 1;
                    iArr3[i52] = iArr3[i52] + 1;
                }
                i47++;
                i49 = i51;
            }
            int i53 = 256;
            while (true) {
                i53--;
                if (i53 < 0) {
                    break;
                }
                int i54 = (i53 << 8) + i36;
                iArr4[i54] = iArr4[i54] | 2097152;
            }
            zArr[i36] = true;
            if (i43 < 255) {
                int i55 = iArr4[i46] & CLEARMASK;
                int i56 = (CLEARMASK & iArr4[i48]) - i55;
                int i57 = 0;
                while ((i56 >> i57) > 65534) {
                    i57++;
                }
                int i58 = 0;
                while (i58 < i56) {
                    int i59 = iArr5[i55 + i58];
                    char c = (char) (i58 >> i57);
                    cArr[i59] = c;
                    int i60 = i55;
                    if (i59 < 20) {
                        cArr[i59 + i + 1] = c;
                    }
                    i58++;
                    i55 = i60;
                }
            }
            i35 = i43 + 1;
            iArr2 = iArr6;
            i12 = 255;
            i34 = i42;
        }
    }
}
