package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.poi.util.IntegerField;

/* JADX INFO: loaded from: classes3.dex */
public class BATBlock extends BigBlock {
    private static final byte _default_value = -1;
    private static final int _entries_per_block = 128;
    private static final int _entries_per_xbat_block = 127;
    private static final int _xbat_chain_offset = 508;
    private byte[] _data;
    private IntegerField[] _fields;

    public static final int entriesPerBlock() {
        return 128;
    }

    public static final int entriesPerXBATBlock() {
        return 127;
    }

    public static final int getXBATChainOffset() {
        return _xbat_chain_offset;
    }

    private BATBlock() {
        byte[] bArr = new byte[512];
        this._data = bArr;
        Arrays.fill(bArr, _default_value);
        this._fields = new IntegerField[128];
        int i = 0;
        for (int i2 = 0; i2 < 128; i2++) {
            this._fields[i2] = new IntegerField(i);
            i += 4;
        }
    }

    public static BATBlock[] createBATBlocks(int[] iArr) {
        BATBlock[] bATBlockArr = new BATBlock[calculateStorageRequirements(iArr.length)];
        int length = iArr.length;
        int i = 0;
        int i2 = 0;
        while (i < iArr.length) {
            int i3 = i2 + 1;
            bATBlockArr[i2] = new BATBlock(iArr, i, length > 128 ? i + 128 : iArr.length);
            length -= 128;
            i += 128;
            i2 = i3;
        }
        return bATBlockArr;
    }

    public static BATBlock[] createXBATBlocks(int[] iArr, int i) {
        int iCalculateXBATStorageRequirements = calculateXBATStorageRequirements(iArr.length);
        BATBlock[] bATBlockArr = new BATBlock[iCalculateXBATStorageRequirements];
        int length = iArr.length;
        if (iCalculateXBATStorageRequirements != 0) {
            int i2 = 0;
            int i3 = 0;
            int i4 = 0;
            while (i3 < iArr.length) {
                int i5 = i4 + 1;
                bATBlockArr[i4] = new BATBlock(iArr, i3, length > 127 ? i3 + 127 : iArr.length);
                length -= 127;
                i3 += 127;
                i4 = i5;
            }
            while (i2 < iCalculateXBATStorageRequirements - 1) {
                bATBlockArr[i2].setXBATChain(i + i2 + 1);
                i2++;
            }
            bATBlockArr[i2].setXBATChain(-2);
        }
        return bATBlockArr;
    }

    public static int calculateStorageRequirements(int i) {
        return ((i + 128) - 1) / 128;
    }

    public static int calculateXBATStorageRequirements(int i) {
        return ((i + 127) - 1) / 127;
    }

    private void setXBATChain(int i) {
        this._fields[127].set(i, this._data);
    }

    private BATBlock(int[] iArr, int i, int i2) {
        this();
        for (int i3 = i; i3 < i2; i3++) {
            this._fields[i3 - i].set(iArr[i3], this._data);
        }
    }

    @Override // org.apache.poi.poifs.storage.BigBlock
    void writeData(OutputStream outputStream) throws IOException {
        doWriteData(outputStream, this._data);
    }
}
