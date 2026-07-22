package org.apache.tools.ant.util;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

/* JADX INFO: loaded from: classes3.dex */
public final class SplitClassLoader extends AntClassLoader {
    private final String[] splitClasses;

    public SplitClassLoader(ClassLoader classLoader, Path path, Project project, String[] strArr) {
        super(classLoader, project, path, true);
        this.splitClasses = strArr;
    }

    @Override // org.apache.tools.ant.AntClassLoader, java.lang.ClassLoader
    protected synchronized Class loadClass(String str, boolean z) throws ClassNotFoundException {
        Class clsFindLoadedClass = findLoadedClass(str);
        if (clsFindLoadedClass != null) {
            return clsFindLoadedClass;
        }
        if (isSplit(str)) {
            Class<?> clsFindClass = findClass(str);
            if (z) {
                resolveClass(clsFindClass);
            }
            return clsFindClass;
        }
        return super.loadClass(str, z);
    }

    private boolean isSplit(String str) {
        String strSubstring = str.substring(str.lastIndexOf(46) + 1);
        int i = 0;
        while (true) {
            String[] strArr = this.splitClasses;
            if (i >= strArr.length) {
                return false;
            }
            if (strSubstring.equals(strArr[i]) || strSubstring.startsWith(this.splitClasses[i] + '$')) {
                break;
            }
            i++;
        }
        return true;
    }
}
