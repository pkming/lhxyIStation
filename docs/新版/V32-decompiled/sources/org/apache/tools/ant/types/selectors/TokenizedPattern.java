package org.apache.tools.ant.types.selectors;

import java.io.File;

/* JADX INFO: loaded from: classes3.dex */
public class TokenizedPattern {
    public static final TokenizedPattern EMPTY_PATTERN = new TokenizedPattern("", new String[0]);
    private final String pattern;
    private final String[] tokenizedPattern;

    public TokenizedPattern(String str) {
        this(str, SelectorUtils.tokenizePathAsArray(str));
    }

    TokenizedPattern(String str, String[] strArr) {
        this.pattern = str;
        this.tokenizedPattern = strArr;
    }

    public boolean matchPath(TokenizedPath tokenizedPath, boolean z) {
        return SelectorUtils.matchPath(this.tokenizedPattern, tokenizedPath.getTokens(), z);
    }

    public boolean matchStartOf(TokenizedPath tokenizedPath, boolean z) {
        return SelectorUtils.matchPatternStart(this.tokenizedPattern, tokenizedPath.getTokens(), z);
    }

    public String toString() {
        return this.pattern;
    }

    public String getPattern() {
        return this.pattern;
    }

    public boolean equals(Object obj) {
        return (obj instanceof TokenizedPattern) && this.pattern.equals(((TokenizedPattern) obj).pattern);
    }

    public int hashCode() {
        return this.pattern.hashCode();
    }

    public int depth() {
        return this.tokenizedPattern.length;
    }

    public boolean containsPattern(String str) {
        int i = 0;
        while (true) {
            String[] strArr = this.tokenizedPattern;
            if (i >= strArr.length) {
                return false;
            }
            if (strArr[i].equals(str)) {
                return true;
            }
            i++;
        }
    }

    public TokenizedPath rtrimWildcardTokens() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (true) {
            String[] strArr = this.tokenizedPattern;
            if (i >= strArr.length || SelectorUtils.hasWildcards(strArr[i])) {
                break;
            }
            if (i > 0 && sb.charAt(sb.length() - 1) != File.separatorChar) {
                sb.append(File.separator);
            }
            sb.append(this.tokenizedPattern[i]);
            i++;
        }
        if (i == 0) {
            return TokenizedPath.EMPTY_PATH;
        }
        String[] strArr2 = new String[i];
        System.arraycopy(this.tokenizedPattern, 0, strArr2, 0, i);
        return new TokenizedPath(sb.toString(), strArr2);
    }

    public boolean endsWith(String str) {
        String[] strArr = this.tokenizedPattern;
        return strArr.length > 0 && strArr[strArr.length - 1].equals(str);
    }

    public TokenizedPattern withoutLastToken() {
        String[] strArr = this.tokenizedPattern;
        if (strArr.length == 0) {
            throw new IllegalStateException("cant strip a token from nothing");
        }
        if (strArr.length == 1) {
            return EMPTY_PATTERN;
        }
        int iLastIndexOf = this.pattern.lastIndexOf(strArr[strArr.length - 1]);
        String[] strArr2 = this.tokenizedPattern;
        String[] strArr3 = new String[strArr2.length - 1];
        System.arraycopy(strArr2, 0, strArr3, 0, strArr2.length - 1);
        return new TokenizedPattern(this.pattern.substring(0, iLastIndexOf), strArr3);
    }
}
