package org.apache.tools.ant.filters;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public abstract class BaseFilterReader extends FilterReader {
    private static final int BUFFER_SIZE = 8192;
    private boolean initialized;
    private Project project;

    public BaseFilterReader() {
        super(new StringReader(""));
        this.initialized = false;
        this.project = null;
        FileUtils.close(this);
    }

    public BaseFilterReader(Reader reader) {
        super(reader);
        this.initialized = false;
        this.project = null;
    }

    @Override // java.io.FilterReader, java.io.Reader
    public final int read(char[] cArr, int i, int i2) throws IOException {
        for (int i3 = 0; i3 < i2; i3++) {
            int i4 = read();
            if (i4 == -1) {
                if (i3 == 0) {
                    return -1;
                }
                return i3;
            }
            cArr[i + i3] = (char) i4;
        }
        return i2;
    }

    @Override // java.io.FilterReader, java.io.Reader
    public final long skip(long j) throws IOException, IllegalArgumentException {
        if (j < 0) {
            throw new IllegalArgumentException("skip value is negative");
        }
        for (long j2 = 0; j2 < j; j2++) {
            if (read() == -1) {
                return j2;
            }
        }
        return j;
    }

    protected final void setInitialized(boolean z) {
        this.initialized = z;
    }

    protected final boolean getInitialized() {
        return this.initialized;
    }

    public final void setProject(Project project) {
        this.project = project;
    }

    protected final Project getProject() {
        return this.project;
    }

    protected final String readLine() throws IOException {
        int i = this.in.read();
        if (i == -1) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        while (i != -1) {
            stringBuffer.append((char) i);
            if (i == 10) {
                break;
            }
            i = this.in.read();
        }
        return stringBuffer.toString();
    }

    protected final String readFully() throws IOException {
        return FileUtils.readFully(this.in, 8192);
    }
}
