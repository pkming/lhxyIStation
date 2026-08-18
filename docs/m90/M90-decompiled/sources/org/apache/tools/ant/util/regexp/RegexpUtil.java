package org.apache.tools.ant.util.regexp;

/* JADX INFO: loaded from: classes3.dex */
public class RegexpUtil {
    public static int asOptions(boolean z, boolean z2, boolean z3) {
        int i = !z ? 256 : 0;
        if (z2) {
            i |= 4096;
        }
        return z3 ? i | 65536 : i;
    }

    public static boolean hasFlag(int i, int i2) {
        return (i & i2) > 0;
    }

    public static int removeFlag(int i, int i2) {
        return i & ((-1) - i2);
    }

    public static int asOptions(String str) {
        if (str == null) {
            return 0;
        }
        int iAsOptions = asOptions(str.indexOf(105) == -1, str.indexOf(109) != -1, str.indexOf(115) != -1);
        return str.indexOf(103) != -1 ? iAsOptions | 16 : iAsOptions;
    }

    public static int asOptions(boolean z) {
        return asOptions(z, false, false);
    }
}
