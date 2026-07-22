package org.apache.tools.ant.types.resources;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes3.dex */
public interface Appendable {
    OutputStream getAppendOutputStream() throws IOException;
}
