package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.types.Parameter;

/* JADX INFO: loaded from: classes3.dex */
public final class StripLineBreaks extends BaseParamFilterReader implements ChainableReader {
    private static final String DEFAULT_LINE_BREAKS = "\r\n";
    private static final String LINE_BREAKS_KEY = "linebreaks";
    private String lineBreaks;

    public StripLineBreaks() {
        this.lineBreaks = "\r\n";
    }

    public StripLineBreaks(Reader reader) {
        super(reader);
        this.lineBreaks = "\r\n";
    }

    @Override // java.io.FilterReader, java.io.Reader
    public int read() throws IOException {
        if (!getInitialized()) {
            initialize();
            setInitialized(true);
        }
        int i = this.in.read();
        while (i != -1 && this.lineBreaks.indexOf(i) != -1) {
            i = this.in.read();
        }
        return i;
    }

    public void setLineBreaks(String str) {
        this.lineBreaks = str;
    }

    private String getLineBreaks() {
        return this.lineBreaks;
    }

    @Override // org.apache.tools.ant.filters.ChainableReader
    public Reader chain(Reader reader) {
        StripLineBreaks stripLineBreaks = new StripLineBreaks(reader);
        stripLineBreaks.setLineBreaks(getLineBreaks());
        stripLineBreaks.setInitialized(true);
        return stripLineBreaks;
    }

    private void initialize() {
        String value;
        Parameter[] parameters = getParameters();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                if (LINE_BREAKS_KEY.equals(parameters[i].getName())) {
                    value = parameters[i].getValue();
                    break;
                }
            }
            value = null;
        } else {
            value = null;
        }
        if (value != null) {
            this.lineBreaks = value;
        }
    }
}
