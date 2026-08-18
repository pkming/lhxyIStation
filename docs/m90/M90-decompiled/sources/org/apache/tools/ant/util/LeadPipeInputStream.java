package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;

/* JADX INFO: loaded from: classes3.dex */
public class LeadPipeInputStream extends PipedInputStream {
    private static final int BYTE_MASK = 255;
    private ProjectComponent managingPc;

    public LeadPipeInputStream() {
    }

    public LeadPipeInputStream(int i) {
        setBufferSize(i);
    }

    public LeadPipeInputStream(PipedOutputStream pipedOutputStream) throws IOException {
        super(pipedOutputStream);
    }

    public LeadPipeInputStream(PipedOutputStream pipedOutputStream, int i) throws IOException {
        super(pipedOutputStream);
        setBufferSize(i);
    }

    @Override // java.io.PipedInputStream, java.io.InputStream
    public synchronized int read() throws IOException {
        int i;
        try {
            i = super.read();
        } catch (IOException e) {
            String message = e.getMessage();
            if ("write end dead".equalsIgnoreCase(message) || "pipe broken".equalsIgnoreCase(message)) {
                if (((PipedInputStream) this).in > 0 && ((PipedInputStream) this).out < ((PipedInputStream) this).buffer.length && ((PipedInputStream) this).out > ((PipedInputStream) this).in) {
                    byte[] bArr = ((PipedInputStream) this).buffer;
                    int i2 = ((PipedInputStream) this).out;
                    ((PipedInputStream) this).out = i2 + 1;
                    i = bArr[i2] & 255;
                }
            } else {
                log("error at LeadPipeInputStream.read():  " + message, 2);
            }
            i = -1;
        }
        return i;
    }

    public synchronized void setBufferSize(int i) {
        if (i > this.buffer.length) {
            byte[] bArr = new byte[i];
            if (this.in >= 0) {
                if (this.in > this.out) {
                    System.arraycopy(this.buffer, this.out, bArr, this.out, this.in - this.out);
                } else {
                    int length = this.buffer.length - this.out;
                    System.arraycopy(this.buffer, this.out, bArr, 0, length);
                    System.arraycopy(this.buffer, 0, bArr, length, this.in);
                    this.in += length;
                    this.out = 0;
                }
            }
            this.buffer = bArr;
        }
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
}
