package org.apache.tools.ant.util;

import java.io.File;

/* JADX INFO: loaded from: classes3.dex */
public class PackageNameMapper extends GlobPatternMapper {
    @Override // org.apache.tools.ant.util.GlobPatternMapper
    protected String extractVariablePart(String str) {
        String strSubstring = str.substring(this.prefixLength, str.length() - this.postfixLength);
        if (getHandleDirSep()) {
            strSubstring = strSubstring.replace('/', '.').replace('\\', '.');
        }
        return strSubstring.replace(File.separatorChar, '.');
    }
}
