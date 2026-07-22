package org.apache.poi.poifs.storage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class SmallDocumentBlock implements BlockWritable, ListManagedBlock {
    private static final int _block_size = 64;
    private static final int _blocks_per_big_block = 8;
    private static final byte _default_fill = -1;
    private byte[] _data;

    public static int calcSize(int i) {
        return i * 64;
    }

    private SmallDocumentBlock(byte[] bArr, int i) {
        this();
        System.arraycopy(bArr, i * 64, this._data, 0, 64);
    }

    private SmallDocumentBlock() {
        this._data = new byte[64];
    }

    public static SmallDocumentBlock[] convert(byte[] bArr, int i) {
        int i2 = ((i + 64) - 1) / 64;
        SmallDocumentBlock[] smallDocumentBlockArr = new SmallDocumentBlock[i2];
        int i3 = 0;
        for (int i4 = 0; i4 < i2; i4++) {
            smallDocumentBlockArr[i4] = new SmallDocumentBlock();
            if (i3 < bArr.length) {
                int iMin = Math.min(64, bArr.length - i3);
                System.arraycopy(bArr, i3, smallDocumentBlockArr[i4]._data, 0, iMin);
                if (iMin != 64) {
                    Arrays.fill(smallDocumentBlockArr[i4]._data, iMin, 64, _default_fill);
                }
            } else {
                Arrays.fill(smallDocumentBlockArr[i4]._data, _default_fill);
            }
            i3 += 64;
        }
        return smallDocumentBlockArr;
    }

    public static int fill(List list) {
        int size = list.size();
        int i = ((size + 8) - 1) / 8;
        int i2 = i * 8;
        while (size < i2) {
            list.add(makeEmptySmallDocumentBlock());
            size++;
        }
        return i;
    }

    public static SmallDocumentBlock[] convert(BlockWritable[] blockWritableArr, int i) throws IOException, ArrayIndexOutOfBoundsException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (BlockWritable blockWritable : blockWritableArr) {
            blockWritable.writeBlocks(byteArrayOutputStream);
        }
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        int iConvertToBlockCount = convertToBlockCount(i);
        SmallDocumentBlock[] smallDocumentBlockArr = new SmallDocumentBlock[iConvertToBlockCount];
        for (int i2 = 0; i2 < iConvertToBlockCount; i2++) {
            smallDocumentBlockArr[i2] = new SmallDocumentBlock(byteArray, i2);
        }
        return smallDocumentBlockArr;
    }

    public static List extract(ListManagedBlock[] listManagedBlockArr) throws IOException {
        ArrayList arrayList = new ArrayList();
        for (ListManagedBlock listManagedBlock : listManagedBlockArr) {
            byte[] data = listManagedBlock.getData();
            for (int i = 0; i < 8; i++) {
                arrayList.add(new SmallDocumentBlock(data, i));
            }
        }
        return arrayList;
    }

    public static void read(BlockWritable[] blockWritableArr, byte[] bArr, int i) {
        int i2 = i / 64;
        int i3 = i % 64;
        int length = ((i + bArr.length) - 1) / 64;
        if (i2 == length) {
            System.arraycopy(((SmallDocumentBlock) blockWritableArr[i2])._data, i3, bArr, 0, bArr.length);
            return;
        }
        int i4 = 64 - i3;
        System.arraycopy(((SmallDocumentBlock) blockWritableArr[i2])._data, i3, bArr, 0, i4);
        int i5 = i4 + 0;
        while (true) {
            i2++;
            if (i2 < length) {
                System.arraycopy(((SmallDocumentBlock) blockWritableArr[i2])._data, 0, bArr, i5, 64);
                i5 += 64;
            } else {
                System.arraycopy(((SmallDocumentBlock) blockWritableArr[length])._data, 0, bArr, i5, bArr.length - i5);
                return;
            }
        }
    }

    private static SmallDocumentBlock makeEmptySmallDocumentBlock() {
        SmallDocumentBlock smallDocumentBlock = new SmallDocumentBlock();
        Arrays.fill(smallDocumentBlock._data, _default_fill);
        return smallDocumentBlock;
    }

    private static int convertToBlockCount(int i) {
        return ((i + 64) - 1) / 64;
    }

    @Override // org.apache.poi.poifs.storage.BlockWritable
    public void writeBlocks(OutputStream outputStream) throws IOException {
        outputStream.write(this._data);
    }

    @Override // org.apache.poi.poifs.storage.ListManagedBlock
    public byte[] getData() throws IOException {
        return this._data;
    }
}
