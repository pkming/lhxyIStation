package org.apache.tools.ant.taskdefs;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class GZip extends Pack {
    @Override // org.apache.tools.ant.taskdefs.Pack
    protected void pack() throws Throwable {
        GZIPOutputStream gZIPOutputStream;
        Throwable th;
        IOException e;
        try {
            gZIPOutputStream = new GZIPOutputStream(new FileOutputStream(this.zipFile));
            try {
                try {
                    zipResource(getSrcResource(), gZIPOutputStream);
                    FileUtils.close(gZIPOutputStream);
                } catch (IOException e2) {
                    e = e2;
                    throw new BuildException("Problem creating gzip " + e.getMessage(), e, getLocation());
                }
            } catch (Throwable th2) {
                th = th2;
                FileUtils.close(gZIPOutputStream);
                throw th;
            }
        } catch (IOException e3) {
            gZIPOutputStream = null;
            e = e3;
        } catch (Throwable th3) {
            gZIPOutputStream = null;
            th = th3;
            FileUtils.close(gZIPOutputStream);
            throw th;
        }
    }

    @Override // org.apache.tools.ant.taskdefs.Pack
    protected boolean supportsNonFileResources() {
        return getClass().equals(GZip.class);
    }
}
