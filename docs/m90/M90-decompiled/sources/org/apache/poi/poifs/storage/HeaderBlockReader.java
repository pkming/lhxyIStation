package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.IntegerField;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LongField;

/* JADX INFO: loaded from: classes3.dex */
public class HeaderBlockReader implements HeaderBlockConstants {
    private IntegerField _bat_count;
    private byte[] _data;
    private IntegerField _property_start;
    private IntegerField _sbat_start;
    private IntegerField _xbat_count;
    private IntegerField _xbat_start;

    public HeaderBlockReader(InputStream inputStream) throws IOException {
        byte[] bArr = new byte[512];
        this._data = bArr;
        int fully = IOUtils.readFully(inputStream, bArr);
        if (fully != 512) {
            throw new IOException(new StringBuffer().append("Unable to read entire header; ").append(fully).append(new StringBuffer().append(" byte").append(fully == 1 ? "" : "s").toString()).append(" read; expected ").append(512).append(" bytes").toString());
        }
        LongField longField = new LongField(0, this._data);
        if (longField.get() != HeaderBlockConstants._signature) {
            throw new IOException(new StringBuffer().append("Invalid header signature; read ").append(longField.get()).append(", expected ").append(HeaderBlockConstants._signature).toString());
        }
        this._bat_count = new IntegerField(44, this._data);
        this._property_start = new IntegerField(48, this._data);
        this._sbat_start = new IntegerField(60, this._data);
        this._xbat_start = new IntegerField(68, this._data);
        this._xbat_count = new IntegerField(72, this._data);
    }

    public int getPropertyStart() {
        return this._property_start.get();
    }

    public int getSBATStart() {
        return this._sbat_start.get();
    }

    public int getBATCount() {
        return this._bat_count.get();
    }

    public int[] getBATArray() {
        int[] iArr = new int[109];
        int i = 76;
        for (int i2 = 0; i2 < 109; i2++) {
            iArr[i2] = LittleEndian.getInt(this._data, i);
            i += 4;
        }
        return iArr;
    }

    public int getXBATCount() {
        return this._xbat_count.get();
    }

    public int getXBATIndex() {
        return this._xbat_start.get();
    }
}
