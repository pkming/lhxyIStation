package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public final class SelectorUtils {
    public static final String DEEP_TREE_MATCH = "**";
    private static final SelectorUtils instance = new SelectorUtils();
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();

    private SelectorUtils() {
    }

    public static SelectorUtils getInstance() {
        return instance;
    }

    public static boolean matchPatternStart(String str, String str2) {
        return matchPatternStart(str, str2, true);
    }

    public static boolean matchPatternStart(String str, String str2, boolean z) {
        if (str2.startsWith(File.separator) != str.startsWith(File.separator)) {
            return false;
        }
        return matchPatternStart(tokenizePathAsArray(str), tokenizePathAsArray(str2), z);
    }

    static boolean matchPatternStart(String[] strArr, String[] strArr2, boolean z) {
        int length = strArr.length - 1;
        int length2 = strArr2.length - 1;
        int i = 0;
        int i2 = 0;
        while (i <= length && i2 <= length2) {
            String str = strArr[i];
            if (str.equals(DEEP_TREE_MATCH)) {
                break;
            }
            if (!match(str, strArr2[i2], z)) {
                return false;
            }
            i++;
            i2++;
        }
        return i2 > length2 || i <= length;
    }

    public static boolean matchPath(String str, String str2) {
        return matchPath(tokenizePathAsArray(str), tokenizePathAsArray(str2), true);
    }

    public static boolean matchPath(String str, String str2, boolean z) {
        return matchPath(tokenizePathAsArray(str), tokenizePathAsArray(str2), z);
    }

    /* JADX WARN: Multi-variable type inference failed */
    static boolean matchPath(String[] strArr, String[] strArr2, boolean z) {
        int i;
        int i2 = 1;
        int length = strArr.length - 1;
        int length2 = strArr2.length - 1;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        while (i4 <= length && i5 <= length2) {
            String str = strArr[i4];
            if (str.equals(DEEP_TREE_MATCH)) {
                break;
            }
            if (!match(str, strArr2[i5], z)) {
                return false;
            }
            i4++;
            i5++;
        }
        if (i5 > length2) {
            while (i4 <= length) {
                if (!strArr[i4].equals(DEEP_TREE_MATCH)) {
                    return false;
                }
                i4++;
            }
            return true;
        }
        if (i4 > length) {
            return false;
        }
        while (i4 <= length && i5 <= length2) {
            String str2 = strArr[length];
            if (str2.equals(DEEP_TREE_MATCH)) {
                break;
            }
            if (!match(str2, strArr2[length2], z)) {
                return false;
            }
            length--;
            length2--;
        }
        if (i5 > length2) {
            while (i4 <= length) {
                if (!strArr[i4].equals(DEEP_TREE_MATCH)) {
                    return false;
                }
                i4++;
            }
            return true;
        }
        while (i4 != length && i5 <= length2) {
            int i6 = i4 + 1;
            int i7 = i6;
            while (true) {
                if (i7 > length) {
                    i7 = -1;
                    break;
                }
                if (strArr[i7].equals(DEEP_TREE_MATCH)) {
                    break;
                }
                i7++;
            }
            if (i7 == i6) {
                i4 = i6;
            } else {
                int i8 = (i7 - i4) - i2;
                int i9 = (length2 - i5) + i2;
                int i10 = i3;
                while (true) {
                    if (i10 > i9 - i8) {
                        i = -1;
                        break;
                    }
                    for (int i11 = i3; i11 < i8; i11++) {
                        if (!match(strArr[i4 + i11 + 1], strArr2[i5 + i10 + i11], z)) {
                            break;
                        }
                    }
                    i = i5 + i10;
                    break;
                    i10++;
                    i3 = 0;
                }
                if (i == -1) {
                    return false;
                }
                i5 = i + i8;
                i3 = 0;
                i4 = i7;
                i2 = 1;
            }
        }
        boolean z2 = i3;
        while (i4 <= length) {
            if (!strArr[i4].equals(DEEP_TREE_MATCH)) {
                return z2;
            }
            i4++;
        }
        return true;
    }

    public static boolean match(String str, String str2) {
        return match(str, str2, true);
    }

    /* JADX WARN: Code restructure failed: missing block: B:47:0x0075, code lost:
    
        if (r10 <= r5) goto L50;
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x007b, code lost:
    
        return allStars(r1, r7, r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x007c, code lost:
    
        if (r7 == r3) goto L96;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x007e, code lost:
    
        if (r10 > r5) goto L97;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x0080, code lost:
    
        r11 = r7 + 1;
        r12 = r11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x0084, code lost:
    
        if (r12 > r3) goto L104;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x0088, code lost:
    
        if (r1[r12] != r9) goto L58;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x008b, code lost:
    
        r12 = r12 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x008e, code lost:
    
        r12 = -1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x008f, code lost:
    
        if (r12 != r11) goto L98;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x0091, code lost:
    
        r7 = r11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x0093, code lost:
    
        r11 = (r12 - r7) - r4;
        r14 = (r5 - r10) + r4;
        r15 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x009c, code lost:
    
        if (r15 > (r14 - r11)) goto L106;
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x009e, code lost:
    
        r9 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x009f, code lost:
    
        if (r9 >= r11) goto L107;
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x00a1, code lost:
    
        r4 = r1[(r7 + r9) + 1];
     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x00a7, code lost:
    
        if (r4 == r8) goto L109;
     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x00b3, code lost:
    
        if (different(r19, r4, r2[(r10 + r15) + r9]) == false) goto L110;
     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x00b5, code lost:
    
        r15 = r15 + 1;
        r8 = '?';
     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x00bb, code lost:
    
        r9 = r9 + 1;
        r8 = '?';
     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x00c1, code lost:
    
        r10 = r10 + r15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x00c3, code lost:
    
        r10 = -1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x00c4, code lost:
    
        if (r10 != (-1)) goto L77;
     */
    /* JADX WARN: Code restructure failed: missing block: B:76:0x00c6, code lost:
    
        return false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x00c7, code lost:
    
        r10 = r10 + r11;
        r7 = r12;
        r4 = 1;
        r8 = '?';
        r9 = '*';
     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x00d3, code lost:
    
        return allStars(r1, r7, r3);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean match(java.lang.String r17, java.lang.String r18, boolean r19) {
        /*
            Method dump skipped, instruction units count: 212
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.types.selectors.SelectorUtils.match(java.lang.String, java.lang.String, boolean):boolean");
    }

    private static boolean allStars(char[] cArr, int i, int i2) {
        while (i <= i2) {
            if (cArr[i] != '*') {
                return false;
            }
            i++;
        }
        return true;
    }

    private static boolean different(boolean z, char c, char c2) {
        if (z) {
            if (c != c2) {
                return true;
            }
        } else if (Character.toUpperCase(c) != Character.toUpperCase(c2)) {
            return true;
        }
        return false;
    }

    public static Vector<String> tokenizePath(String str) {
        return tokenizePath(str, File.separator);
    }

    public static Vector<String> tokenizePath(String str, String str2) {
        Vector<String> vector = new Vector<>();
        if (FileUtils.isAbsolutePath(str)) {
            String[] strArrDissect = FILE_UTILS.dissect(str);
            vector.add(strArrDissect[0]);
            str = strArrDissect[1];
        }
        StringTokenizer stringTokenizer = new StringTokenizer(str, str2);
        while (stringTokenizer.hasMoreTokens()) {
            vector.addElement(stringTokenizer.nextToken());
        }
        return vector;
    }

    static String[] tokenizePathAsArray(String str) {
        String str2;
        int i = 1;
        if (FileUtils.isAbsolutePath(str)) {
            String[] strArrDissect = FILE_UTILS.dissect(str);
            str2 = strArrDissect[0];
            str = strArrDissect[1];
        } else {
            str2 = null;
        }
        char c = File.separatorChar;
        int length = str.length();
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < length; i4++) {
            if (str.charAt(i4) == c) {
                if (i4 != i2) {
                    i3++;
                }
                i2 = i4 + 1;
            }
        }
        if (length != i2) {
            i3++;
        }
        String[] strArr = new String[i3 + (str2 == null ? 0 : 1)];
        if (str2 != null) {
            strArr[0] = str2;
        } else {
            i = 0;
        }
        int i5 = 0;
        for (int i6 = 0; i6 < length; i6++) {
            if (str.charAt(i6) == c) {
                if (i6 != i5) {
                    strArr[i] = str.substring(i5, i6);
                    i++;
                }
                i5 = i6 + 1;
            }
        }
        if (length != i5) {
            strArr[i] = str.substring(i5);
        }
        return strArr;
    }

    public static boolean isOutOfDate(File file, File file2, int i) {
        if (file.exists()) {
            return !file2.exists() || file.lastModified() - ((long) i) > file2.lastModified();
        }
        return false;
    }

    public static boolean isOutOfDate(Resource resource, Resource resource2, int i) {
        return isOutOfDate(resource, resource2, i);
    }

    public static boolean isOutOfDate(Resource resource, Resource resource2, long j) {
        long lastModified = resource.getLastModified();
        long lastModified2 = resource2.getLastModified();
        return resource.isExists() && (lastModified == 0 || lastModified2 == 0 || lastModified - j > lastModified2);
    }

    public static String removeWhitespace(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        if (str != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(str);
            while (stringTokenizer.hasMoreTokens()) {
                stringBuffer.append(stringTokenizer.nextToken());
            }
        }
        return stringBuffer.toString();
    }

    public static boolean hasWildcards(String str) {
        return (str.indexOf(42) == -1 && str.indexOf(63) == -1) ? false : true;
    }

    public static String rtrimWildcardTokens(String str) {
        return new TokenizedPattern(str).rtrimWildcardTokens().toString();
    }
}
