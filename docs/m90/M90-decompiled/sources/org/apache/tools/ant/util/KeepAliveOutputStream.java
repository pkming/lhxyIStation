package org.apache.tools.ant.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/* JADX INFO: loaded from: classes3.dex */
public class KeepAliveOutputStream extends FilterOutputStream {
    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
    }

    public KeepAliveOutputStream(OutputStream outputStream) {
        super(outputStream);
    }

    public static PrintStream wrapSystemOut() {
        return wrap(System.out);
    }

    public static PrintStream wrapSystemErr() {
        return wrap(System.err);
    }

    private static PrintStream wrap(PrintStream printStream) {
        return new PrintStream(new KeepAliveOutputStream(printStream));
    }
}
