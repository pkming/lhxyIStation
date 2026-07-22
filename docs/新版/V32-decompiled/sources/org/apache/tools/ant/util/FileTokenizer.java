package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.ProjectComponent;

/* JADX INFO: loaded from: classes3.dex */
public class FileTokenizer extends ProjectComponent implements Tokenizer {
    @Override // org.apache.tools.ant.util.Tokenizer
    public String getPostToken() {
        return "";
    }

    @Override // org.apache.tools.ant.util.Tokenizer
    public String getToken(Reader reader) throws IOException {
        return FileUtils.readFully(reader);
    }
}
