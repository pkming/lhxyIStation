package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.io.FilenameFilter;

/* JADX INFO: loaded from: classes3.dex */
public class InnerClassFilenameFilter implements FilenameFilter {
    private String baseClassName;

    InnerClassFilenameFilter(String str) {
        int iLastIndexOf = str.lastIndexOf(".class");
        this.baseClassName = str.substring(0, iLastIndexOf == -1 ? str.length() - 1 : iLastIndexOf);
    }

    @Override // java.io.FilenameFilter
    public boolean accept(File file, String str) {
        return str.lastIndexOf(".") == str.lastIndexOf(".class") && str.indexOf(new StringBuilder().append(this.baseClassName).append("$").toString()) == 0;
    }
}
