package org.apache.tools.ant.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;

/* JADX INFO: loaded from: classes3.dex */
public class ConcatFileInputStream extends InputStream {
    private static final int EOF = -1;
    private InputStream currentStream;
    private File[] file;
    private ProjectComponent managingPc;
    private int currentIndex = -1;
    private boolean eof = false;

    public ConcatFileInputStream(File[] fileArr) throws IOException {
        this.file = fileArr;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        closeCurrent();
        this.eof = true;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        int current = readCurrent();
        if (current != -1 || this.eof) {
            return current;
        }
        int i = this.currentIndex + 1;
        this.currentIndex = i;
        openFile(i);
        return readCurrent();
    }

    public void setManagingTask(Task task) {
        setManagingComponent(task);
    }

    public void setManagingComponent(ProjectComponent projectComponent) {
        this.managingPc = projectComponent;
    }

    public void log(String str, int i) {
        ProjectComponent projectComponent = this.managingPc;
        if (projectComponent != null) {
            projectComponent.log(str, i);
        } else if (i > 1) {
            System.out.println(str);
        } else {
            System.err.println(str);
        }
    }

    private int readCurrent() throws IOException {
        InputStream inputStream;
        if (this.eof || (inputStream = this.currentStream) == null) {
            return -1;
        }
        return inputStream.read();
    }

    private void openFile(int i) throws IOException {
        closeCurrent();
        File[] fileArr = this.file;
        if (fileArr != null && i < fileArr.length) {
            log("Opening " + this.file[i], 3);
            try {
                this.currentStream = new BufferedInputStream(new FileInputStream(this.file[i]));
                return;
            } catch (IOException e) {
                log("Failed to open " + this.file[i], 0);
                throw e;
            }
        }
        this.eof = true;
    }

    private void closeCurrent() {
        FileUtils.close(this.currentStream);
        this.currentStream = null;
    }
}
