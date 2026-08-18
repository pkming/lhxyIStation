package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.types.Parameter;

/* JADX INFO: loaded from: classes3.dex */
public final class PrefixLines extends BaseParamFilterReader implements ChainableReader {
    private static final String PREFIX_KEY = "prefix";
    private String prefix;
    private String queuedData;

    public PrefixLines() {
        this.prefix = null;
        this.queuedData = null;
    }

    public PrefixLines(Reader reader) {
        super(reader);
        this.prefix = null;
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
        if (this.prefix != null) {
            this.queuedData = this.prefix + this.queuedData;
        }
        return read();
    }

    public void setPrefix(String str) {
        this.prefix = str;
    }

    private String getPrefix() {
        return this.prefix;
    }

    @Override // org.apache.tools.ant.filters.ChainableReader
    public Reader chain(Reader reader) {
        PrefixLines prefixLines = new PrefixLines(reader);
        prefixLines.setPrefix(getPrefix());
        prefixLines.setInitialized(true);
        return prefixLines;
    }

    private void initialize() {
        Parameter[] parameters = getParameters();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                if (PREFIX_KEY.equals(parameters[i].getName())) {
                    this.prefix = parameters[i].getValue();
                    return;
                }
            }
        }
    }
}
