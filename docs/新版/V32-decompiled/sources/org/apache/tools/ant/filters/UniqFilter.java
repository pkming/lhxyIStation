package org.apache.tools.ant.filters;

import org.apache.tools.ant.filters.TokenFilter;

/* JADX INFO: loaded from: classes3.dex */
public class UniqFilter extends TokenFilter.ChainableReaderFilter {
    private String lastLine = null;

    @Override // org.apache.tools.ant.filters.TokenFilter.Filter
    public String filter(String str) {
        String str2 = this.lastLine;
        if (str2 != null && str2.equals(str)) {
            return null;
        }
        this.lastLine = str;
        return str;
    }
}
