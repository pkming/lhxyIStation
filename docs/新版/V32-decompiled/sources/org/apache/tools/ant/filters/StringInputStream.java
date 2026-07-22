package org.apache.tools.ant.filters;

import java.io.StringReader;
import org.apache.tools.ant.util.ReaderInputStream;

/* JADX INFO: loaded from: classes3.dex */
public class StringInputStream extends ReaderInputStream {
    public StringInputStream(String str) {
        super(new StringReader(str));
    }

    public StringInputStream(String str, String str2) {
        super(new StringReader(str), str2);
    }
}
