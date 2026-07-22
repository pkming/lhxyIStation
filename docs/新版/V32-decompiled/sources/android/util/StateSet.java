package android.util;

import android.R;

/* JADX INFO: loaded from: classes.dex */
public class StateSet {
    public static final int[] WILD_CARD = new int[0];
    public static final int[] NOTHING = {0};

    public static boolean isWildCard(int[] iArr) {
        return iArr.length == 0 || iArr[0] == 0;
    }

    /* JADX WARN: Code restructure failed: missing block: B:29:0x0033, code lost:
    
        r5 = false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean stateSetMatches(int[] r9, int[] r10) {
        /*
            r0 = 1
            r1 = 0
            if (r10 != 0) goto Lf
            if (r9 == 0) goto Le
            boolean r9 = isWildCard(r9)
            if (r9 == 0) goto Ld
            goto Le
        Ld:
            r0 = r1
        Le:
            return r0
        Lf:
            int r2 = r9.length
            int r3 = r10.length
            r4 = r1
        L12:
            if (r4 >= r2) goto L3c
            r5 = r9[r4]
            if (r5 != 0) goto L19
            return r0
        L19:
            if (r5 <= 0) goto L1d
            r6 = r0
            goto L1f
        L1d:
            int r5 = -r5
            r6 = r1
        L1f:
            r7 = r1
        L20:
            if (r7 >= r3) goto L33
            r8 = r10[r7]
            if (r8 != 0) goto L29
            if (r6 == 0) goto L33
            return r1
        L29:
            if (r8 != r5) goto L30
            if (r6 == 0) goto L2f
            r5 = r0
            goto L34
        L2f:
            return r1
        L30:
            int r7 = r7 + 1
            goto L20
        L33:
            r5 = r1
        L34:
            if (r6 == 0) goto L39
            if (r5 != 0) goto L39
            return r1
        L39:
            int r4 = r4 + 1
            goto L12
        L3c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.util.StateSet.stateSetMatches(int[], int[]):boolean");
    }

    public static boolean stateSetMatches(int[] iArr, int i) {
        int i2;
        int length = iArr.length;
        for (int i3 = 0; i3 < length && (i2 = iArr[i3]) != 0; i3++) {
            if (i2 > 0) {
                if (i != i2) {
                    return false;
                }
            } else if (i == (-i2)) {
                return false;
            }
        }
        return true;
    }

    public static int[] trimStateSet(int[] iArr, int i) {
        if (iArr.length == i) {
            return iArr;
        }
        int[] iArr2 = new int[i];
        System.arraycopy(iArr, 0, iArr2, 0, i);
        return iArr2;
    }

    public static String dump(int[] iArr) {
        StringBuilder sb = new StringBuilder();
        for (int i : iArr) {
            if (i != 16842913) {
                if (i == 16842919) {
                    sb.append("P ");
                } else {
                    switch (i) {
                        case R.attr.state_focused /* 16842908 */:
                            sb.append("F ");
                            break;
                        case R.attr.state_window_focused /* 16842909 */:
                            sb.append("W ");
                            break;
                        case R.attr.state_enabled /* 16842910 */:
                            sb.append("E ");
                            break;
                    }
                }
            } else {
                sb.append("S ");
            }
        }
        return sb.toString();
    }
}
