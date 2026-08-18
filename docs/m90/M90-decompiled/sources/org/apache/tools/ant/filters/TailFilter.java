package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.util.LineTokenizer;

/* JADX INFO: loaded from: classes3.dex */
public final class TailFilter extends BaseParamFilterReader implements ChainableReader {
    private static final int DEFAULT_NUM_LINES = 10;
    private static final String LINES_KEY = "lines";
    private static final String SKIP_KEY = "skip";
    private boolean completedReadAhead;
    private String line;
    private LinkedList<String> lineList;
    private int linePos;
    private LineTokenizer lineTokenizer;
    private long lines;
    private long skip;

    public TailFilter() {
        this.lines = 10L;
        this.skip = 0L;
        this.completedReadAhead = false;
        this.lineTokenizer = null;
        this.line = null;
        this.linePos = 0;
        this.lineList = new LinkedList<>();
    }

    public TailFilter(Reader reader) {
        super(reader);
        this.lines = 10L;
        this.skip = 0L;
        this.completedReadAhead = false;
        this.lineTokenizer = null;
        this.line = null;
        this.linePos = 0;
        this.lineList = new LinkedList<>();
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
                String strTailFilter = tailFilter(token);
                this.line = strTailFilter;
                if (strTailFilter == null) {
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
        TailFilter tailFilter = new TailFilter(reader);
        tailFilter.setLines(getLines());
        tailFilter.setSkip(getSkip());
        tailFilter.setInitialized(true);
        return tailFilter;
    }

    private void initialize() {
        Parameter[] parameters = getParameters();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                if (LINES_KEY.equals(parameters[i].getName())) {
                    setLines(Long.parseLong(parameters[i].getValue()));
                } else if ("skip".equals(parameters[i].getName())) {
                    this.skip = Long.parseLong(parameters[i].getValue());
                }
            }
        }
    }

    private String tailFilter(String str) {
        if (!this.completedReadAhead) {
            if (str != null) {
                this.lineList.add(str);
                long j = this.lines;
                if (j == -1) {
                    return ((long) this.lineList.size()) > this.skip ? this.lineList.removeFirst() : "";
                }
                long j2 = this.skip;
                if (j + (j2 > 0 ? j2 : 0L) >= this.lineList.size()) {
                    return "";
                }
                this.lineList.removeFirst();
                return "";
            }
            this.completedReadAhead = true;
            if (this.skip > 0) {
                for (int i = 0; i < this.skip; i++) {
                    this.lineList.removeLast();
                }
            }
            if (this.lines > -1) {
                while (this.lineList.size() > this.lines) {
                    this.lineList.removeFirst();
                }
            }
        }
        if (this.lineList.size() > 0) {
            return this.lineList.removeFirst();
        }
        return null;
    }
}
