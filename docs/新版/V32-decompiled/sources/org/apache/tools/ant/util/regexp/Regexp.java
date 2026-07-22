package org.apache.tools.ant.util.regexp;

import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public interface Regexp extends RegexpMatcher {
    public static final int REPLACE_ALL = 16;
    public static final int REPLACE_FIRST = 1;

    String substitute(String str, String str2, int i) throws BuildException;
}
