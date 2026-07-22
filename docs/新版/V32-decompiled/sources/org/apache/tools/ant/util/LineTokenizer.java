package org.apache.tools.ant.util;

import org.apache.tools.ant.ProjectComponent;

/* JADX INFO: loaded from: classes3.dex */
public class LineTokenizer extends ProjectComponent implements Tokenizer {
    private static final int NOT_A_CHAR = -2;
    private String lineEnd = "";
    private int pushed = -2;
    private boolean includeDelims = false;

    public void setIncludeDelims(boolean z) {
        this.includeDelims = z;
    }

    /* JADX WARN: Code restructure failed: missing block: B:25:0x0047, code lost:
    
        r3 = r4;
     */
    @Override // org.apache.tools.ant.util.Tokenizer
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.lang.String getToken(java.io.Reader r10) throws java.io.IOException {
        /*
            r9 = this;
            int r0 = r9.pushed
            r1 = -2
            if (r0 == r1) goto L8
            r9.pushed = r1
            goto Lc
        L8:
            int r0 = r10.read()
        Lc:
            r1 = -1
            if (r0 != r1) goto L11
            r10 = 0
            return r10
        L11:
            java.lang.String r2 = ""
            r9.lineEnd = r2
            java.lang.StringBuffer r2 = new java.lang.StringBuffer
            r2.<init>()
            r3 = 0
            r4 = r3
        L1c:
            java.lang.String r5 = "\r"
            r6 = 1
            if (r0 == r1) goto L47
            r7 = 10
            if (r4 != 0) goto L3b
            r8 = 13
            if (r0 != r8) goto L2b
            r4 = r6
            goto L36
        L2b:
            if (r0 != r7) goto L32
            java.lang.String r10 = "\n"
            r9.lineEnd = r10
            goto L47
        L32:
            char r0 = (char) r0
            r2.append(r0)
        L36:
            int r0 = r10.read()
            goto L1c
        L3b:
            if (r0 != r7) goto L42
            java.lang.String r10 = "\r\n"
            r9.lineEnd = r10
            goto L48
        L42:
            r9.pushed = r0
            r9.lineEnd = r5
            goto L48
        L47:
            r3 = r4
        L48:
            if (r0 != r1) goto L4e
            if (r3 != r6) goto L4e
            r9.lineEnd = r5
        L4e:
            boolean r10 = r9.includeDelims
            if (r10 == 0) goto L57
            java.lang.String r10 = r9.lineEnd
            r2.append(r10)
        L57:
            java.lang.String r10 = r2.toString()
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.util.LineTokenizer.getToken(java.io.Reader):java.lang.String");
    }

    @Override // org.apache.tools.ant.util.Tokenizer
    public String getPostToken() {
        return this.includeDelims ? "" : this.lineEnd;
    }
}
