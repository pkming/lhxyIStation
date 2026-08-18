package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.SymbolicLinkUtils;

/* JADX INFO: loaded from: classes3.dex */
public class TokenizedPath {
    private final String path;
    private final String[] tokenizedPath;
    public static final TokenizedPath EMPTY_PATH = new TokenizedPath("", new String[0]);
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final SymbolicLinkUtils SYMLINK_UTILS = SymbolicLinkUtils.getSymbolicLinkUtils();
    private static final boolean[] CS_SCAN_ONLY = {true};
    private static final boolean[] CS_THEN_NON_CS = {true, false};

    public TokenizedPath(String str) {
        this(str, SelectorUtils.tokenizePathAsArray(str));
    }

    /* JADX WARN: Removed duplicated region for block: B:7:0x0037  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public TokenizedPath(org.apache.tools.ant.types.selectors.TokenizedPath r5, java.lang.String r6) {
        /*
            r4 = this;
            r4.<init>()
            java.lang.String r0 = r5.path
            int r0 = r0.length()
            if (r0 <= 0) goto L37
            java.lang.String r0 = r5.path
            int r1 = r0.length()
            int r1 = r1 + (-1)
            char r0 = r0.charAt(r1)
            char r1 = java.io.File.separatorChar
            if (r0 == r1) goto L37
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = r5.path
            java.lang.StringBuilder r0 = r0.append(r1)
            char r1 = java.io.File.separatorChar
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.StringBuilder r0 = r0.append(r6)
            java.lang.String r0 = r0.toString()
            r4.path = r0
            goto L4c
        L37:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = r5.path
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.StringBuilder r0 = r0.append(r6)
            java.lang.String r0 = r0.toString()
            r4.path = r0
        L4c:
            java.lang.String[] r0 = r5.tokenizedPath
            int r0 = r0.length
            int r0 = r0 + 1
            java.lang.String[] r0 = new java.lang.String[r0]
            r4.tokenizedPath = r0
            java.lang.String[] r1 = r5.tokenizedPath
            int r2 = r1.length
            r3 = 0
            java.lang.System.arraycopy(r1, r3, r0, r3, r2)
            java.lang.String[] r5 = r5.tokenizedPath
            int r5 = r5.length
            r0[r5] = r6
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.types.selectors.TokenizedPath.<init>(org.apache.tools.ant.types.selectors.TokenizedPath, java.lang.String):void");
    }

    TokenizedPath(String str, String[] strArr) {
        this.path = str;
        this.tokenizedPath = strArr;
    }

    public String toString() {
        return this.path;
    }

    public int depth() {
        return this.tokenizedPath.length;
    }

    String[] getTokens() {
        return this.tokenizedPath;
    }

    public File findFile(File file, boolean z) {
        String[] strArr = this.tokenizedPath;
        if (FileUtils.isAbsolutePath(this.path)) {
            if (file == null) {
                String[] strArrDissect = FILE_UTILS.dissect(this.path);
                File file2 = new File(strArrDissect[0]);
                strArr = SelectorUtils.tokenizePathAsArray(strArrDissect[1]);
                file = file2;
            } else {
                FileUtils fileUtils = FILE_UTILS;
                File fileNormalize = fileUtils.normalize(this.path);
                String strRemoveLeadingPath = fileUtils.removeLeadingPath(file, fileNormalize);
                if (strRemoveLeadingPath.equals(fileNormalize.getAbsolutePath())) {
                    return null;
                }
                strArr = SelectorUtils.tokenizePathAsArray(strRemoveLeadingPath);
            }
        }
        return findFile(file, strArr, z);
    }

    public boolean isSymlink(File file) {
        int i = 0;
        while (true) {
            String[] strArr = this.tokenizedPath;
            if (i >= strArr.length) {
                return false;
            }
            if (file != null) {
                try {
                    if (SYMLINK_UTILS.isSymbolicLink(file, strArr[i])) {
                        return true;
                    }
                    if (file != null && SYMLINK_UTILS.isSymbolicLink(this.tokenizedPath[i])) {
                        return true;
                    }
                    file = new File(file, this.tokenizedPath[i]);
                } catch (IOException unused) {
                    System.err.println("IOException caught while checking for links, couldn't get canonical path!");
                }
            } else {
                if (file != null) {
                }
                file = new File(file, this.tokenizedPath[i]);
            }
            i++;
        }
    }

    public boolean equals(Object obj) {
        return (obj instanceof TokenizedPath) && this.path.equals(((TokenizedPath) obj).path);
    }

    public int hashCode() {
        return this.path.hashCode();
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x0040  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static java.io.File findFile(java.io.File r11, java.lang.String[] r12, boolean r13) {
        /*
            r0 = 0
            r1 = r0
        L2:
            int r2 = r12.length
            r3 = 0
            if (r1 >= r2) goto L74
            boolean r2 = r11.isDirectory()
            if (r2 != 0) goto Ld
            return r3
        Ld:
            java.lang.String[] r2 = r11.list()
            if (r2 == 0) goto L57
            if (r13 == 0) goto L18
            boolean[] r4 = org.apache.tools.ant.types.selectors.TokenizedPath.CS_SCAN_ONLY
            goto L1a
        L18:
            boolean[] r4 = org.apache.tools.ant.types.selectors.TokenizedPath.CS_THEN_NON_CS
        L1a:
            r5 = r0
            r6 = r5
        L1c:
            if (r5 != 0) goto L51
            int r7 = r4.length
            if (r6 >= r7) goto L51
            r7 = r0
        L22:
            if (r5 != 0) goto L4e
            int r8 = r2.length
            if (r7 >= r8) goto L4e
            boolean r8 = r4[r6]
            if (r8 == 0) goto L36
            r8 = r2[r7]
            r9 = r12[r1]
            boolean r8 = r8.equals(r9)
            if (r8 == 0) goto L4b
            goto L40
        L36:
            r8 = r2[r7]
            r9 = r12[r1]
            boolean r8 = r8.equalsIgnoreCase(r9)
            if (r8 == 0) goto L4b
        L40:
            java.io.File r5 = new java.io.File
            r8 = r2[r7]
            r5.<init>(r11, r8)
            r11 = 1
            r10 = r5
            r5 = r11
            r11 = r10
        L4b:
            int r7 = r7 + 1
            goto L22
        L4e:
            int r6 = r6 + 1
            goto L1c
        L51:
            if (r5 != 0) goto L54
            return r3
        L54:
            int r1 = r1 + 1
            goto L2
        L57:
            org.apache.tools.ant.BuildException r12 = new org.apache.tools.ant.BuildException
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r0 = "IO error scanning directory "
            java.lang.StringBuilder r13 = r13.append(r0)
            java.lang.String r11 = r11.getAbsolutePath()
            java.lang.StringBuilder r11 = r13.append(r11)
            java.lang.String r11 = r11.toString()
            r12.<init>(r11)
            throw r12
        L74:
            int r12 = r12.length
            if (r12 != 0) goto L7e
            boolean r12 = r11.isDirectory()
            if (r12 != 0) goto L7e
            r11 = r3
        L7e:
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.types.selectors.TokenizedPath.findFile(java.io.File, java.lang.String[], boolean):java.io.File");
    }

    public TokenizedPattern toPattern() {
        return new TokenizedPattern(this.path, this.tokenizedPath);
    }
}
