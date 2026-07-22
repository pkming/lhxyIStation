package org.apache.commons.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/* JADX INFO: loaded from: classes3.dex */
final class ExtendedBufferedReader extends BufferedReader {
    private boolean closed;
    private long eolCounter;
    private int lastChar;
    private long position;

    ExtendedBufferedReader(Reader reader) {
        super(reader);
        this.lastChar = -2;
    }

    @Override // java.io.BufferedReader, java.io.Reader
    public int read() throws IOException {
        int i = super.read();
        if (i == 13 || (i == 10 && this.lastChar != 13)) {
            this.eolCounter++;
        }
        this.lastChar = i;
        this.position++;
        return i;
    }

    int getLastChar() {
        return this.lastChar;
    }

    @Override // java.io.BufferedReader, java.io.Reader
    public int read(char[] cArr, int i, int i2) throws IOException {
        int i3;
        if (i2 == 0) {
            return 0;
        }
        int i4 = super.read(cArr, i, i2);
        if (i4 > 0) {
            int i5 = i;
            while (true) {
                i3 = i + i4;
                if (i5 >= i3) {
                    break;
                }
                char c = cArr[i5];
                if (c == '\n') {
                    if (13 != (i5 > 0 ? cArr[i5 - 1] : this.lastChar)) {
                        this.eolCounter++;
                    }
                } else if (c == '\r') {
                    this.eolCounter++;
                }
                i5++;
            }
            this.lastChar = cArr[i3 - 1];
        } else if (i4 == -1) {
            this.lastChar = -1;
        }
        this.position += (long) i4;
        return i4;
    }

    @Override // java.io.BufferedReader
    public String readLine() throws IOException {
        String line = super.readLine();
        if (line != null) {
            this.lastChar = 10;
            this.eolCounter++;
        } else {
            this.lastChar = -1;
        }
        return line;
    }

    int lookAhead() throws IOException {
        super.mark(1);
        int i = super.read();
        super.reset();
        return i;
    }

    long getCurrentLineNumber() {
        int i = this.lastChar;
        if (i == 13 || i == 10 || i == -2 || i == -1) {
            return this.eolCounter;
        }
        return this.eolCounter + 1;
    }

    long getPosition() {
        return this.position;
    }

    public boolean isClosed() {
        return this.closed;
    }

    @Override // java.io.BufferedReader, java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.closed = true;
        this.lastChar = -1;
        super.close();
    }
}
