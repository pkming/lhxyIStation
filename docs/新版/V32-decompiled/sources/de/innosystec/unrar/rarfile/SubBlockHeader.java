package de.innosystec.unrar.rarfile;

import de.innosystec.unrar.io.Raw;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: classes2.dex */
public class SubBlockHeader extends BlockHeader {
    public static final short SubBlockHeaderSize = 3;
    private byte level;
    private Log logger;
    private short subType;

    public SubBlockHeader(SubBlockHeader subBlockHeader) {
        super(subBlockHeader);
        this.logger = LogFactory.getLog(getClass());
        this.subType = subBlockHeader.getSubType().getSubblocktype();
        this.level = subBlockHeader.getLevel();
    }

    public SubBlockHeader(BlockHeader blockHeader, byte[] bArr) {
        super(blockHeader);
        this.logger = LogFactory.getLog(getClass());
        this.subType = Raw.readShortLittleEndian(bArr, 0);
        this.level = (byte) (this.level | (bArr[2] & 255));
    }

    public byte getLevel() {
        return this.level;
    }

    public SubBlockHeaderType getSubType() {
        return SubBlockHeaderType.findSubblockHeaderType(this.subType);
    }

    @Override // de.innosystec.unrar.rarfile.BlockHeader, de.innosystec.unrar.rarfile.BaseBlock
    public void print() {
        super.print();
        this.logger.info("subtype: " + getSubType());
        this.logger.info("level: " + ((int) this.level));
    }
}
