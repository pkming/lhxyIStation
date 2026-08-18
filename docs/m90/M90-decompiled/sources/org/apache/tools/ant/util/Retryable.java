package org.apache.tools.ant.util;

import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public interface Retryable {
    public static final int RETRY_FOREVER = -1;

    void execute() throws IOException;
}
