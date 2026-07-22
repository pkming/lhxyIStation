package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import java.util.Vector;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Parameter;

/* JADX INFO: loaded from: classes3.dex */
public final class LineContains extends BaseParamFilterReader implements ChainableReader {
    private static final String CONTAINS_KEY = "contains";
    private static final String NEGATE_KEY = "negate";
    private Vector<String> contains;
    private String line;
    private boolean negate;

    public LineContains() {
        this.contains = new Vector<>();
        this.line = null;
        this.negate = false;
    }

    public LineContains(Reader reader) {
        super(reader);
        this.contains = new Vector<>();
        this.line = null;
        this.negate = false;
    }

    @Override // java.io.FilterReader, java.io.Reader
    public int read() throws IOException {
        boolean z;
        if (!getInitialized()) {
            initialize();
            setInitialized(true);
        }
        String str = this.line;
        if (str != null) {
            char cCharAt = str.charAt(0);
            if (this.line.length() == 1) {
                this.line = null;
                return cCharAt;
            }
            this.line = this.line.substring(1);
            return cCharAt;
        }
        int size = this.contains.size();
        do {
            this.line = readLine();
            if (this.line == null) {
                break;
            }
            z = true;
            for (int i = 0; z && i < size; i++) {
                z = this.line.indexOf(this.contains.elementAt(i)) >= 0;
            }
        } while (!(z ^ isNegated()));
        if (this.line != null) {
            return read();
        }
        return -1;
    }

    public void addConfiguredContains(Contains contains) {
        this.contains.addElement(contains.getValue());
    }

    public void setNegate(boolean z) {
        this.negate = z;
    }

    public boolean isNegated() {
        return this.negate;
    }

    private void setContains(Vector<String> vector) {
        this.contains = vector;
    }

    private Vector<String> getContains() {
        return this.contains;
    }

    @Override // org.apache.tools.ant.filters.ChainableReader
    public Reader chain(Reader reader) {
        LineContains lineContains = new LineContains(reader);
        lineContains.setContains(getContains());
        lineContains.setNegate(isNegated());
        return lineContains;
    }

    private void initialize() {
        Parameter[] parameters = getParameters();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                if (CONTAINS_KEY.equals(parameters[i].getType())) {
                    this.contains.addElement(parameters[i].getValue());
                } else if ("negate".equals(parameters[i].getType())) {
                    setNegate(Project.toBoolean(parameters[i].getValue()));
                }
            }
        }
    }

    public static class Contains {
        private String value;

        public final void setValue(String str) {
            this.value = str;
        }

        public final String getValue() {
            return this.value;
        }
    }
}
