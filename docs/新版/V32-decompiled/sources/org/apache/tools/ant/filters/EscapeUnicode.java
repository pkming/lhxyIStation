package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.util.UnicodeUtil;

/* JADX INFO: loaded from: classes3.dex */
public class EscapeUnicode extends BaseParamFilterReader implements ChainableReader {
    private StringBuffer unicodeBuf;

    private void initialize() {
    }

    public EscapeUnicode() {
        this.unicodeBuf = new StringBuffer();
    }

    public EscapeUnicode(Reader reader) {
        super(reader);
        this.unicodeBuf = new StringBuffer();
    }

    @Override // java.io.FilterReader, java.io.Reader
    public final int read() throws IOException {
        char c;
        if (!getInitialized()) {
            initialize();
            setInitialized(true);
        }
        if (this.unicodeBuf.length() == 0) {
            int i = this.in.read();
            if (i == -1 || (c = (char) i) < 128) {
                return i;
            }
            this.unicodeBuf = UnicodeUtil.EscapeUnicode(c);
            return 92;
        }
        char cCharAt = this.unicodeBuf.charAt(0);
        this.unicodeBuf.deleteCharAt(0);
        return cCharAt;
    }

    @Override // org.apache.tools.ant.filters.ChainableReader
    public final Reader chain(Reader reader) {
        EscapeUnicode escapeUnicode = new EscapeUnicode(reader);
        escapeUnicode.setInitialized(true);
        return escapeUnicode;
    }
}
