package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.Reader;

/* JADX INFO: loaded from: classes3.dex */
public interface Tokenizer {
    String getPostToken();

    String getToken(Reader reader) throws IOException;
}
