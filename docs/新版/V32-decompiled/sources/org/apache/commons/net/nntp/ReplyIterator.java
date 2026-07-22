package org.apache.commons.net.nntp;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.net.io.DotTerminatedMessageReader;
import org.apache.commons.net.io.Util;

/* JADX INFO: loaded from: classes3.dex */
class ReplyIterator implements Iterator<String>, Iterable<String> {
    private String line;
    private final BufferedReader reader;
    private Exception savedException;

    @Override // java.lang.Iterable
    public Iterator<String> iterator() {
        return this;
    }

    ReplyIterator(BufferedReader bufferedReader, boolean z) throws IOException {
        bufferedReader = z ? new DotTerminatedMessageReader(bufferedReader) : bufferedReader;
        this.reader = bufferedReader;
        String line = bufferedReader.readLine();
        this.line = line;
        if (line == null) {
            Util.closeQuietly(bufferedReader);
        }
    }

    ReplyIterator(BufferedReader bufferedReader) throws IOException {
        this(bufferedReader, true);
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        if (this.savedException == null) {
            return this.line != null;
        }
        throw new NoSuchElementException(this.savedException.toString());
    }

    @Override // java.util.Iterator
    public String next() throws NoSuchElementException {
        if (this.savedException != null) {
            throw new NoSuchElementException(this.savedException.toString());
        }
        String str = this.line;
        if (str == null) {
            throw new NoSuchElementException();
        }
        try {
            String line = this.reader.readLine();
            this.line = line;
            if (line == null) {
                Util.closeQuietly(this.reader);
            }
        } catch (IOException e) {
            this.savedException = e;
            Util.closeQuietly(this.reader);
        }
        return str;
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
