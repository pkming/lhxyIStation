package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.ProjectComponent;

/* JADX INFO: loaded from: classes3.dex */
public class StringTokenizer extends ProjectComponent implements Tokenizer {
    private static final int NOT_A_CHAR = -2;
    private String intraString = "";
    private int pushed = -2;
    private char[] delims = null;
    private boolean delimsAreTokens = false;
    private boolean suppressDelims = false;
    private boolean includeDelims = false;

    public void setDelims(String str) {
        this.delims = StringUtils.resolveBackSlash(str).toCharArray();
    }

    public void setDelimsAreTokens(boolean z) {
        this.delimsAreTokens = z;
    }

    public void setSuppressDelims(boolean z) {
        this.suppressDelims = z;
    }

    public void setIncludeDelims(boolean z) {
        this.includeDelims = z;
    }

    @Override // org.apache.tools.ant.util.Tokenizer
    public String getToken(Reader reader) throws IOException {
        int i = this.pushed;
        if (i != -2) {
            this.pushed = -2;
        } else {
            i = reader.read();
        }
        if (i == -1) {
            return null;
        }
        boolean z = true;
        this.intraString = "";
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer stringBuffer2 = new StringBuffer();
        while (true) {
            if (i == -1) {
                break;
            }
            char c = (char) i;
            boolean zIsDelim = isDelim(c);
            if (!z) {
                if (zIsDelim) {
                    stringBuffer2.append(c);
                    i = reader.read();
                } else {
                    this.pushed = i;
                    break;
                }
            } else {
                if (zIsDelim) {
                    if (this.delimsAreTokens) {
                        if (stringBuffer.length() == 0) {
                            stringBuffer.append(c);
                        } else {
                            this.pushed = i;
                        }
                    } else {
                        stringBuffer2.append(c);
                        z = false;
                    }
                } else {
                    stringBuffer.append(c);
                }
                i = reader.read();
            }
        }
        String string = stringBuffer2.toString();
        this.intraString = string;
        if (this.includeDelims) {
            stringBuffer.append(string);
        }
        return stringBuffer.toString();
    }

    @Override // org.apache.tools.ant.util.Tokenizer
    public String getPostToken() {
        return (this.suppressDelims || this.includeDelims) ? "" : this.intraString;
    }

    private boolean isDelim(char c) {
        if (this.delims == null) {
            return Character.isWhitespace(c);
        }
        int i = 0;
        while (true) {
            char[] cArr = this.delims;
            if (i >= cArr.length) {
                return false;
            }
            if (cArr[i] == c) {
                return true;
            }
            i++;
        }
    }
}
