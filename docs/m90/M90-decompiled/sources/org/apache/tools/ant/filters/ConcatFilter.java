package org.apache.tools.ant.filters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.types.Parameter;

/* JADX INFO: loaded from: classes3.dex */
public final class ConcatFilter extends BaseParamFilterReader implements ChainableReader {
    private File append;
    private Reader appendReader;
    private File prepend;
    private Reader prependReader;

    public ConcatFilter() {
        this.prependReader = null;
        this.appendReader = null;
    }

    public ConcatFilter(Reader reader) {
        super(reader);
        this.prependReader = null;
        this.appendReader = null;
    }

    @Override // java.io.FilterReader, java.io.Reader
    public int read() throws IOException {
        int i;
        Reader reader;
        if (!getInitialized()) {
            initialize();
            setInitialized(true);
        }
        Reader reader2 = this.prependReader;
        if (reader2 != null) {
            i = reader2.read();
            if (i == -1) {
                this.prependReader.close();
                this.prependReader = null;
            }
        } else {
            i = -1;
        }
        if (i == -1) {
            i = super.read();
        }
        if (i == -1 && (reader = this.appendReader) != null && (i = reader.read()) == -1) {
            this.appendReader.close();
            this.appendReader = null;
        }
        return i;
    }

    public void setPrepend(File file) {
        this.prepend = file;
    }

    public File getPrepend() {
        return this.prepend;
    }

    public void setAppend(File file) {
        this.append = file;
    }

    public File getAppend() {
        return this.append;
    }

    @Override // org.apache.tools.ant.filters.ChainableReader
    public Reader chain(Reader reader) {
        ConcatFilter concatFilter = new ConcatFilter(reader);
        concatFilter.setPrepend(getPrepend());
        concatFilter.setAppend(getAppend());
        return concatFilter;
    }

    private void initialize() throws IOException {
        Parameter[] parameters = getParameters();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                if ("prepend".equals(parameters[i].getName())) {
                    setPrepend(new File(parameters[i].getValue()));
                } else if ("append".equals(parameters[i].getName())) {
                    setAppend(new File(parameters[i].getValue()));
                }
            }
        }
        File file = this.prepend;
        if (file != null) {
            if (!file.isAbsolute()) {
                this.prepend = new File(getProject().getBaseDir(), this.prepend.getPath());
            }
            this.prependReader = new BufferedReader(new FileReader(this.prepend));
        }
        File file2 = this.append;
        if (file2 != null) {
            if (!file2.isAbsolute()) {
                this.append = new File(getProject().getBaseDir(), this.append.getPath());
            }
            this.appendReader = new BufferedReader(new FileReader(this.append));
        }
    }
}
