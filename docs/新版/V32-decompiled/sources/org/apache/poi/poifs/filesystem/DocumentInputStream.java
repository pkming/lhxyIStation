package org.apache.poi.poifs.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/* JADX INFO: loaded from: classes3.dex */
public class DocumentInputStream extends InputStream {
    private static final int EOD = -1;
    private POIFSDocument _document;
    private int _document_size;
    private int _current_offset = 0;
    private int _marked_offset = 0;
    private boolean _closed = false;
    private byte[] _tiny_buffer = null;

    @Override // java.io.InputStream
    public boolean markSupported() {
        return true;
    }

    public DocumentInputStream(DocumentEntry documentEntry) throws IOException {
        this._document_size = documentEntry.getSize();
        if (documentEntry instanceof DocumentNode) {
            this._document = ((DocumentNode) documentEntry).getDocument();
            return;
        }
        throw new IOException("Cannot open internal document storage");
    }

    public DocumentInputStream(POIFSDocument pOIFSDocument) throws IOException {
        this._document_size = pOIFSDocument.getSize();
        this._document = pOIFSDocument;
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        dieIfClosed();
        return this._document_size - this._current_offset;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this._closed = true;
    }

    @Override // java.io.InputStream
    public void mark(int i) {
        this._marked_offset = this._current_offset;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        dieIfClosed();
        if (atEOD()) {
            return -1;
        }
        if (this._tiny_buffer == null) {
            this._tiny_buffer = new byte[1];
        }
        POIFSDocument pOIFSDocument = this._document;
        byte[] bArr = this._tiny_buffer;
        int i = this._current_offset;
        this._current_offset = i + 1;
        pOIFSDocument.read(bArr, i);
        return this._tiny_buffer[0] & 255;
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr) throws IOException, NullPointerException {
        return read(bArr, 0, bArr.length);
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IndexOutOfBoundsException, IOException, NullPointerException {
        dieIfClosed();
        Objects.requireNonNull(bArr, "buffer is null");
        if (i < 0 || i2 < 0 || bArr.length < i + i2) {
            throw new IndexOutOfBoundsException("can't read past buffer boundaries");
        }
        if (i2 == 0) {
            return 0;
        }
        if (atEOD()) {
            return -1;
        }
        int iMin = Math.min(available(), i2);
        if (i == 0 && iMin == bArr.length) {
            this._document.read(bArr, this._current_offset);
        } else {
            byte[] bArr2 = new byte[iMin];
            this._document.read(bArr2, this._current_offset);
            System.arraycopy(bArr2, 0, bArr, i, iMin);
        }
        this._current_offset += iMin;
        return iMin;
    }

    @Override // java.io.InputStream
    public void reset() {
        this._current_offset = this._marked_offset;
    }

    @Override // java.io.InputStream
    public long skip(long j) throws IOException {
        dieIfClosed();
        if (j < 0) {
            return 0L;
        }
        int i = this._current_offset;
        int i2 = ((int) j) + i;
        if (i2 < i) {
            i2 = this._document_size;
        } else {
            int i3 = this._document_size;
            if (i2 > i3) {
                i2 = i3;
            }
        }
        long j2 = i2 - i;
        this._current_offset = i2;
        return j2;
    }

    private void dieIfClosed() throws IOException {
        if (this._closed) {
            throw new IOException("cannot perform requested operation on a closed stream");
        }
    }

    private boolean atEOD() {
        return this._current_offset == this._document_size;
    }
}
