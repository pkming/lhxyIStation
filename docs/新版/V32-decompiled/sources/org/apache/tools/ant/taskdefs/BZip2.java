package org.apache.tools.ant.taskdefs;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.bzip2.CBZip2OutputStream;

/* JADX INFO: loaded from: classes3.dex */
public class BZip2 extends Pack {
    @Override // org.apache.tools.ant.taskdefs.Pack
    protected void pack() throws Throwable {
        Throwable th;
        IOException e;
        try {
            try {
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(this.zipFile));
                bufferedOutputStream.write(66);
                bufferedOutputStream.write(90);
                CBZip2OutputStream cBZip2OutputStream = new CBZip2OutputStream(bufferedOutputStream);
                try {
                    zipResource(getSrcResource(), cBZip2OutputStream);
                    FileUtils.close(cBZip2OutputStream);
                } catch (IOException e2) {
                    e = e2;
                    throw new BuildException("Problem creating bzip2 " + e.getMessage(), e, getLocation());
                }
            } catch (Throwable th2) {
                th = th2;
                FileUtils.close((OutputStream) null);
                throw th;
            }
        } catch (IOException e3) {
            e = e3;
        } catch (Throwable th3) {
            th = th3;
            FileUtils.close((OutputStream) null);
            throw th;
        }
    }

    @Override // org.apache.tools.ant.taskdefs.Pack
    protected boolean supportsNonFileResources() {
        return getClass().equals(BZip2.class);
    }
}
