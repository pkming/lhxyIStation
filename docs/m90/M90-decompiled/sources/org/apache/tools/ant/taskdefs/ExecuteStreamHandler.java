package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes3.dex */
public interface ExecuteStreamHandler {
    void setProcessErrorStream(InputStream inputStream) throws IOException;

    void setProcessInputStream(OutputStream outputStream) throws IOException;

    void setProcessOutputStream(InputStream inputStream) throws IOException;

    void start() throws IOException;

    void stop();
}
