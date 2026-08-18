package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.types.Parameter;

/* JADX INFO: loaded from: classes3.dex */
public final class SuffixLines extends BaseParamFilterReader implements ChainableReader {
    private static final String SUFFIX_KEY = "suffix";
    private String queuedData;
    private String suffix;

    public SuffixLines() {
        this.suffix = null;
        this.queuedData = null;
    }

    public SuffixLines(Reader reader) {
        super(reader);
        this.suffix = null;
        this.queuedData = null;
    }

    @Override // java.io.FilterReader, java.io.Reader
    public int read() throws IOException {
        if (!getInitialized()) {
            initialize();
            setInitialized(true);
        }
        String str = this.queuedData;
        if (str != null && str.length() == 0) {
            this.queuedData = null;
        }
        String str2 = this.queuedData;
        if (str2 != null) {
            char cCharAt = str2.charAt(0);
            String strSubstring = this.queuedData.substring(1);
            this.queuedData = strSubstring;
            if (strSubstring.length() != 0) {
                return cCharAt;
            }
            this.queuedData = null;
            return cCharAt;
        }
        String line = readLine();
        this.queuedData = line;
        if (line == null) {
            return -1;
        }
        if (this.suffix != null) {
            String str3 = "\r\n";
            if (!line.endsWith("\r\n")) {
                str3 = this.queuedData.endsWith("\n") ? "\n" : "";
            }
            StringBuilder sb = new StringBuilder();
            String str4 = this.queuedData;
            this.queuedData = sb.append(str4.substring(0, str4.length() - str3.length())).append(this.suffix).append(str3).toString();
        }
        return read();
    }

    public void setSuffix(String str) {
        this.suffix = str;
    }

    private String getSuffix() {
        return this.suffix;
    }

    @Override // org.apache.tools.ant.filters.ChainableReader
    public Reader chain(Reader reader) {
        SuffixLines suffixLines = new SuffixLines(reader);
        suffixLines.setSuffix(getSuffix());
        suffixLines.setInitialized(true);
        return suffixLines;
    }

    private void initialize() {
        Parameter[] parameters = getParameters();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                if (SUFFIX_KEY.equals(parameters[i].getName())) {
                    this.suffix = parameters[i].getValue();
                    return;
                }
            }
        }
    }
}
