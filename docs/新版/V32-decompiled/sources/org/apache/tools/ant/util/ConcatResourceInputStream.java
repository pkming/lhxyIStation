package org.apache.tools.ant.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

/* JADX INFO: loaded from: classes3.dex */
public class ConcatResourceInputStream extends InputStream {
    private static final int EOF = -1;
    private InputStream currentStream;
    private boolean eof = false;
    private boolean ignoreErrors = false;
    private Iterator<Resource> iter;
    private ProjectComponent managingPc;

    public ConcatResourceInputStream(ResourceCollection resourceCollection) {
        this.iter = resourceCollection.iterator();
    }

    public void setIgnoreErrors(boolean z) {
        this.ignoreErrors = z;
    }

    public boolean isIgnoreErrors() {
        return this.ignoreErrors;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        closeCurrent();
        this.eof = true;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        if (this.eof) {
            return -1;
        }
        int current = readCurrent();
        if (current != -1) {
            return current;
        }
        nextResource();
        return readCurrent();
    }

    public void setManagingComponent(ProjectComponent projectComponent) {
        this.managingPc = projectComponent;
    }

    public void log(String str, int i) {
        ProjectComponent projectComponent = this.managingPc;
        if (projectComponent != null) {
            projectComponent.log(str, i);
        } else {
            (i > 1 ? System.out : System.err).println(str);
        }
    }

    private int readCurrent() throws IOException {
        InputStream inputStream;
        if (this.eof || (inputStream = this.currentStream) == null) {
            return -1;
        }
        return inputStream.read();
    }

    private void nextResource() throws IOException {
        closeCurrent();
        while (this.iter.hasNext()) {
            Resource next = this.iter.next();
            if (next.isExists()) {
                log("Concating " + next.toLongString(), 3);
                try {
                    this.currentStream = new BufferedInputStream(next.getInputStream());
                    return;
                } catch (IOException e) {
                    if (!this.ignoreErrors) {
                        log("Failed to get input stream for " + next, 0);
                        throw e;
                    }
                }
            }
        }
        this.eof = true;
    }

    private void closeCurrent() {
        FileUtils.close(this.currentStream);
        this.currentStream = null;
    }
}
