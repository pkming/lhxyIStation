package org.apache.poi.poifs.storage;

import android.content.pm.PackageManager;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.poi.util.IntegerField;
import org.apache.poi.util.LongField;
import org.apache.poi.util.ShortField;

/* JADX INFO: loaded from: classes3.dex */
public class HeaderBlockWriter extends BigBlock implements HeaderBlockConstants {
    private static final byte _default_value = -1;
    private IntegerField _bat_count;
    private byte[] _data;
    private IntegerField _property_start;
    private IntegerField _sbat_block_count;
    private IntegerField _sbat_start;
    private IntegerField _xbat_count;
    private IntegerField _xbat_start;

    public HeaderBlockWriter() {
        byte[] bArr = new byte[512];
        this._data = bArr;
        Arrays.fill(bArr, _default_value);
        new LongField(0, HeaderBlockConstants._signature, this._data);
        new IntegerField(8, 0, this._data);
        new IntegerField(12, 0, this._data);
        new IntegerField(16, 0, this._data);
        new IntegerField(20, 0, this._data);
        new ShortField(24, (short) 59, this._data);
        new ShortField(26, (short) 3, this._data);
        new ShortField(28, (short) -2, this._data);
        new ShortField(30, (short) 9, this._data);
        new IntegerField(32, 6, this._data);
        new IntegerField(36, 0, this._data);
        new IntegerField(40, 0, this._data);
        this._bat_count = new IntegerField(44, 0, this._data);
        this._property_start = new IntegerField(48, -2, this._data);
        new IntegerField(52, 0, this._data);
        new IntegerField(56, 4096, this._data);
        this._sbat_start = new IntegerField(60, -2, this._data);
        this._sbat_block_count = new IntegerField(64, 0, this._data);
        this._xbat_start = new IntegerField(68, -2, this._data);
        this._xbat_count = new IntegerField(72, 0, this._data);
    }

    public BATBlock[] setBATBlocks(int i, int i2) {
        BATBlock[] bATBlockArrCreateXBATBlocks;
        this._bat_count.set(i, this._data);
        int iMin = Math.min(i, 109);
        int i3 = 76;
        for (int i4 = 0; i4 < iMin; i4++) {
            new IntegerField(i3, i2 + i4, this._data);
            i3 += 4;
        }
        if (i > 109) {
            int i5 = i + PackageManager.INSTALL_PARSE_FAILED_MANIFEST_EMPTY;
            int[] iArr = new int[i5];
            for (int i6 = 0; i6 < i5; i6++) {
                iArr[i6] = i2 + i6 + 109;
            }
            int i7 = i2 + i;
            bATBlockArrCreateXBATBlocks = BATBlock.createXBATBlocks(iArr, i7);
            this._xbat_start.set(i7, this._data);
        } else {
            bATBlockArrCreateXBATBlocks = BATBlock.createXBATBlocks(new int[0], 0);
            this._xbat_start.set(-2, this._data);
        }
        this._xbat_count.set(bATBlockArrCreateXBATBlocks.length, this._data);
        return bATBlockArrCreateXBATBlocks;
    }

    public void setPropertyStart(int i) {
        this._property_start.set(i, this._data);
    }

    public void setSBATStart(int i) {
        this._sbat_start.set(i, this._data);
    }

    public void setSBATBlockCount(int i) {
        this._sbat_block_count.set(i, this._data);
    }

    static int calculateXBATStorageRequirements(int i) {
        if (i > 109) {
            return BATBlock.calculateXBATStorageRequirements(i - 109);
        }
        return 0;
    }

    @Override // org.apache.poi.poifs.storage.BigBlock
    void writeData(OutputStream outputStream) throws IOException {
        doWriteData(outputStream, this._data);
    }
}
