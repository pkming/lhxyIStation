package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.util.LineTokenizer;

/* JADX INFO: loaded from: classes3.dex */
public final class HeadFilter extends BaseParamFilterReader implements ChainableReader {
    private static final int DEFAULT_NUM_LINES = 10;
    private static final String LINES_KEY = "lines";
    private static final String SKIP_KEY = "skip";
    private boolean eof;
    private String line;
    private int linePos;
    private LineTokenizer lineTokenizer;
    private long lines;
    private long linesRead;
    private long skip;

    public HeadFilter() {
        this.linesRead = 0L;
        this.lines = 10L;
        this.skip = 0L;
        this.lineTokenizer = null;
        this.line = null;
        this.linePos = 0;
    }

    public HeadFilter(Reader reader) {
        super(reader);
        this.linesRead = 0L;
        this.lines = 10L;
        this.skip = 0L;
        this.lineTokenizer = null;
        this.line = null;
        this.linePos = 0;
        LineTokenizer lineTokenizer = new LineTokenizer();
        this.lineTokenizer = lineTokenizer;
        lineTokenizer.setIncludeDelims(true);
    }

    @Override // java.io.FilterReader, java.io.Reader
    public int read() throws IOException {
        if (!getInitialized()) {
            initialize();
            setInitialized(true);
        }
        while (true) {
            String str = this.line;
            if (str == null || str.length() == 0) {
                String token = this.lineTokenizer.getToken(this.in);
                this.line = token;
                if (token == null) {
                    return -1;
                }
                this.line = headFilter(token);
                if (this.eof) {
                    return -1;
                }
                this.linePos = 0;
            } else {
                char cCharAt = this.line.charAt(this.linePos);
                int i = this.linePos + 1;
                this.linePos = i;
                if (i == this.line.length()) {
                    this.line = null;
                }
                return cCharAt;
            }
        }
    }

    public void setLines(long j) {
        this.lines = j;
    }

    private long getLines() {
        return this.lines;
    }

    public void setSkip(long j) {
        this.skip = j;
    }

    private long getSkip() {
        return this.skip;
    }

    @Override // org.apache.tools.ant.filters.ChainableReader
    public Reader chain(Reader reader) {
        HeadFilter headFilter = new HeadFilter(reader);
        headFilter.setLines(getLines());
        headFilter.setSkip(getSkip());
        headFilter.setInitialized(true);
        return headFilter;
    }

    private void initialize() {
        Parameter[] parameters = getParameters();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                if (LINES_KEY.equals(parameters[i].getName())) {
                    this.lines = Long.parseLong(parameters[i].getValue());
                } else if ("skip".equals(parameters[i].getName())) {
                    this.skip = Long.parseLong(parameters[i].getValue());
                }
            }
        }
    }

    private String headFilter(String str) {
        long j = this.linesRead + 1;
        this.linesRead = j;
        long j2 = this.skip;
        if (j2 > 0 && j - 1 < j2) {
            return null;
        }
        long j3 = this.lines;
        if (j3 <= 0 || j <= j3 + j2) {
            return str;
        }
        this.eof = true;
        return null;
    }
}
