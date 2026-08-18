package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;

/* JADX INFO: loaded from: classes3.dex */
public final class StripJavaComments extends BaseFilterReader implements ChainableReader {
    private boolean inString;
    private boolean quoted;
    private int readAheadCh;

    public StripJavaComments() {
        this.readAheadCh = -1;
        this.inString = false;
        this.quoted = false;
    }

    public StripJavaComments(Reader reader) {
        super(reader);
        this.readAheadCh = -1;
        this.inString = false;
        this.quoted = false;
    }

    @Override // java.io.FilterReader, java.io.Reader
    public int read() throws IOException {
        int i = this.readAheadCh;
        if (i != -1) {
            this.readAheadCh = -1;
            return i;
        }
        int i2 = this.in.read();
        if (i2 == 34 && !this.quoted) {
            this.inString = !this.inString;
            this.quoted = false;
            return i2;
        }
        if (i2 == 92) {
            this.quoted = !this.quoted;
            return i2;
        }
        this.quoted = false;
        if (this.inString || i2 != 47) {
            return i2;
        }
        int i3 = this.in.read();
        if (i3 == 47) {
            while (i3 != 10 && i3 != -1 && i3 != 13) {
                i3 = this.in.read();
            }
            return i3;
        }
        if (i3 != 42) {
            this.readAheadCh = i3;
            return 47;
        }
        while (i3 != -1) {
            i3 = this.in.read();
            if (i3 == 42) {
                i3 = this.in.read();
                while (i3 == 42) {
                    i3 = this.in.read();
                }
                if (i3 == 47) {
                    return read();
                }
            }
        }
        return i3;
    }

    @Override // org.apache.tools.ant.filters.ChainableReader
    public Reader chain(Reader reader) {
        return new StripJavaComments(reader);
    }
}
