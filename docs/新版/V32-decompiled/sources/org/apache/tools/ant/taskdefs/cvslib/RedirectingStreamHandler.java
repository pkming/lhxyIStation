package org.apache.tools.ant.taskdefs.cvslib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;

/* JADX INFO: loaded from: classes3.dex */
class RedirectingStreamHandler extends PumpStreamHandler {
    RedirectingStreamHandler(ChangeLogParser changeLogParser) {
        super(new RedirectingOutputStream(changeLogParser), new ByteArrayOutputStream());
    }

    String getErrors() {
        try {
            return ((ByteArrayOutputStream) getErr()).toString("ASCII");
        } catch (Exception unused) {
            return null;
        }
    }

    @Override // org.apache.tools.ant.taskdefs.PumpStreamHandler, org.apache.tools.ant.taskdefs.ExecuteStreamHandler
    public void stop() {
        super.stop();
        try {
            getErr().close();
            getOut().close();
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }
}
