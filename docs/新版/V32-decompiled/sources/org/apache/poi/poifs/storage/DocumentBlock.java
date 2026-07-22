package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.poi.util.IOUtils;

/* JADX INFO: loaded from: classes3.dex */
public class DocumentBlock extends BigBlock {
    private static final byte _default_value = -1;
    private int _bytes_read;
    private byte[] _data;

    public static byte getFillByte() {
        return _default_value;
    }

    public DocumentBlock(RawDataBlock rawDataBlock) throws IOException {
        byte[] data = rawDataBlock.getData();
        this._data = data;
        this._bytes_read = data.length;
    }

    public DocumentBlock(InputStream inputStream) throws IOException {
        this();
        int fully = IOUtils.readFully(inputStream, this._data);
        this._bytes_read = fully == -1 ? 0 : fully;
    }

    private DocumentBlock() {
        byte[] bArr = new byte[512];
        this._data = bArr;
        Arrays.fill(bArr, _default_value);
    }

    public int size() {
        return this._bytes_read;
    }

    public boolean partiallyRead() {
        return this._bytes_read != 512;
    }

    public static DocumentBlock[] convert(byte[] bArr, int i) {
        int i2 = ((i + 512) - 1) / 512;
        DocumentBlock[] documentBlockArr = new DocumentBlock[i2];
        int i3 = 0;
        for (int i4 = 0; i4 < i2; i4++) {
            documentBlockArr[i4] = new DocumentBlock();
            if (i3 < bArr.length) {
                int iMin = Math.min(512, bArr.length - i3);
                System.arraycopy(bArr, i3, documentBlockArr[i4]._data, 0, iMin);
                if (iMin != 512) {
                    Arrays.fill(documentBlockArr[i4]._data, iMin, 512, _default_value);
                }
            } else {
                Arrays.fill(documentBlockArr[i4]._data, _default_value);
            }
            i3 += 512;
        }
        return documentBlockArr;
    }

    public static void read(DocumentBlock[] documentBlockArr, byte[] bArr, int i) {
        int i2 = i / 512;
        int i3 = i % 512;
        int length = ((i + bArr.length) - 1) / 512;
        if (i2 == length) {
            System.arraycopy(documentBlockArr[i2]._data, i3, bArr, 0, bArr.length);
            return;
        }
        int i4 = 512 - i3;
        System.arraycopy(documentBlockArr[i2]._data, i3, bArr, 0, i4);
        int i5 = i4 + 0;
        while (true) {
            i2++;
            if (i2 < length) {
                System.arraycopy(documentBlockArr[i2]._data, 0, bArr, i5, 512);
                i5 += 512;
            } else {
                System.arraycopy(documentBlockArr[length]._data, 0, bArr, i5, bArr.length - i5);
                return;
            }
        }
    }

    @Override // org.apache.poi.poifs.storage.BigBlock
    void writeData(OutputStream outputStream) throws IOException {
        doWriteData(outputStream, this._data);
    }
}
