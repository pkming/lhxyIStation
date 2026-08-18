package de.innosystec.unrar.rarfile;

import de.innosystec.unrar.io.Raw;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: classes2.dex */
public class EAHeader extends SubBlockHeader {
    public static final short EAHeaderSize = 10;
    private int EACRC;
    private Log logger;
    private byte method;
    private int unpSize;
    private byte unpVer;

    public EAHeader(SubBlockHeader subBlockHeader, byte[] bArr) {
        super(subBlockHeader);
        this.logger = LogFactory.getLog(getClass());
        this.unpSize = Raw.readIntLittleEndian(bArr, 0);
        this.unpVer = (byte) (this.unpVer | (bArr[4] & 255));
        this.method = (byte) (this.method | (bArr[5] & 255));
        this.EACRC = Raw.readIntLittleEndian(bArr, 6);
    }

    public int getEACRC() {
        return this.EACRC;
    }

    public byte getMethod() {
        return this.method;
    }

    public int getUnpSize() {
        return this.unpSize;
    }

    public byte getUnpVer() {
        return this.unpVer;
    }

    @Override // de.innosystec.unrar.rarfile.SubBlockHeader, de.innosystec.unrar.rarfile.BlockHeader, de.innosystec.unrar.rarfile.BaseBlock
    public void print() {
        super.print();
        this.logger.info("unpSize: " + this.unpSize);
        this.logger.info("unpVersion: " + ((int) this.unpVer));
        this.logger.info("method: " + ((int) this.method));
        this.logger.info("EACRC:" + this.EACRC);
    }
}
