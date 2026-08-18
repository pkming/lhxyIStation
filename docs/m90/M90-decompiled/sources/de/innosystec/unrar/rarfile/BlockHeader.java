package de.innosystec.unrar.rarfile;

import de.innosystec.unrar.io.Raw;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: classes2.dex */
public class BlockHeader extends BaseBlock {
    public static final short blockHeaderSize = 4;
    private int dataSize;
    private Log logger;
    private int packSize;

    public BlockHeader() {
        this.logger = LogFactory.getLog(BlockHeader.class.getName());
    }

    public BlockHeader(BlockHeader blockHeader) {
        super(blockHeader);
        this.logger = LogFactory.getLog(BlockHeader.class.getName());
        int dataSize = blockHeader.getDataSize();
        this.packSize = dataSize;
        this.dataSize = dataSize;
        this.positionInFile = blockHeader.getPositionInFile();
    }

    public BlockHeader(BaseBlock baseBlock, byte[] bArr) {
        super(baseBlock);
        this.logger = LogFactory.getLog(BlockHeader.class.getName());
        int intLittleEndian = Raw.readIntLittleEndian(bArr, 0);
        this.packSize = intLittleEndian;
        this.dataSize = intLittleEndian;
    }

    public int getDataSize() {
        return this.dataSize;
    }

    public int getPackSize() {
        return this.packSize;
    }

    @Override // de.innosystec.unrar.rarfile.BaseBlock
    public void print() {
        super.print();
        this.logger.info("DataSize: " + getDataSize() + " packSize: " + getPackSize());
    }
}
