package org.apache.tools.ant.taskdefs;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.bzip2.CBZip2InputStream;

/* JADX INFO: loaded from: classes3.dex */
public class BUnzip2 extends Unpack {
    private static final int BUFFER_SIZE = 8192;
    private static final String DEFAULT_EXTENSION = ".bz2";

    @Override // org.apache.tools.ant.taskdefs.Unpack
    protected String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Override // org.apache.tools.ant.taskdefs.Unpack
    protected void extract() throws Throwable {
        InputStream inputStream;
        BufferedInputStream bufferedInputStream;
        CBZip2InputStream cBZip2InputStream;
        Throwable th;
        FileOutputStream fileOutputStream;
        IOException e;
        if (this.source.lastModified() <= this.dest.lastModified()) {
            return;
        }
        log("Expanding " + this.source.getAbsolutePath() + " to " + this.dest.getAbsolutePath());
        try {
            fileOutputStream = new FileOutputStream(this.dest);
            try {
                inputStream = this.srcResource.getInputStream();
                try {
                    bufferedInputStream = new BufferedInputStream(inputStream);
                } catch (IOException e2) {
                    cBZip2InputStream = null;
                    e = e2;
                    bufferedInputStream = null;
                } catch (Throwable th2) {
                    cBZip2InputStream = null;
                    th = th2;
                    bufferedInputStream = null;
                }
            } catch (IOException e3) {
                bufferedInputStream = null;
                cBZip2InputStream = null;
                e = e3;
                inputStream = null;
            } catch (Throwable th3) {
                bufferedInputStream = null;
                cBZip2InputStream = null;
                th = th3;
                inputStream = null;
            }
        } catch (IOException e4) {
            inputStream = null;
            bufferedInputStream = null;
            cBZip2InputStream = null;
            e = e4;
            fileOutputStream = null;
        } catch (Throwable th4) {
            inputStream = null;
            bufferedInputStream = null;
            cBZip2InputStream = null;
            th = th4;
            fileOutputStream = null;
        }
        try {
            if (bufferedInputStream.read() != 66) {
                throw new BuildException("Invalid bz2 file.", getLocation());
            }
            if (bufferedInputStream.read() != 90) {
                throw new BuildException("Invalid bz2 file.", getLocation());
            }
            cBZip2InputStream = new CBZip2InputStream(bufferedInputStream, true);
            try {
                try {
                    byte[] bArr = new byte[8192];
                    int i = 0;
                    do {
                        fileOutputStream.write(bArr, 0, i);
                        i = cBZip2InputStream.read(bArr, 0, 8192);
                    } while (i != -1);
                    FileUtils.close(bufferedInputStream);
                    FileUtils.close(inputStream);
                    FileUtils.close(fileOutputStream);
                    FileUtils.close(cBZip2InputStream);
                } catch (IOException e5) {
                    e = e5;
                    throw new BuildException("Problem expanding bzip2 " + e.getMessage(), e, getLocation());
                }
            } catch (Throwable th5) {
                th = th5;
                FileUtils.close(bufferedInputStream);
                FileUtils.close(inputStream);
                FileUtils.close(fileOutputStream);
                FileUtils.close(cBZip2InputStream);
                throw th;
            }
        } catch (IOException e6) {
            cBZip2InputStream = null;
            e = e6;
        } catch (Throwable th6) {
            cBZip2InputStream = null;
            th = th6;
            FileUtils.close(bufferedInputStream);
            FileUtils.close(inputStream);
            FileUtils.close(fileOutputStream);
            FileUtils.close(cBZip2InputStream);
            throw th;
        }
    }

    @Override // org.apache.tools.ant.taskdefs.Unpack
    protected boolean supportsNonFileResources() {
        return getClass().equals(BUnzip2.class);
    }
}
