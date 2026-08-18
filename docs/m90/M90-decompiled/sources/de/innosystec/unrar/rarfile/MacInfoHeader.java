package de.innosystec.unrar.rarfile;

import de.innosystec.unrar.io.Raw;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: classes2.dex */
public class MacInfoHeader extends SubBlockHeader {
    public static final short MacInfoHeaderSize = 8;
    private int fileCreator;
    private int fileType;
    private Log logger;

    public MacInfoHeader(SubBlockHeader subBlockHeader, byte[] bArr) {
        super(subBlockHeader);
        this.logger = LogFactory.getLog(getClass());
        this.fileType = Raw.readIntLittleEndian(bArr, 0);
        this.fileCreator = Raw.readIntLittleEndian(bArr, 4);
    }

    public int getFileCreator() {
        return this.fileCreator;
    }

    public void setFileCreator(int i) {
        this.fileCreator = i;
    }

    public int getFileType() {
        return this.fileType;
    }

    public void setFileType(int i) {
        this.fileType = i;
    }

    @Override // de.innosystec.unrar.rarfile.SubBlockHeader, de.innosystec.unrar.rarfile.BlockHeader, de.innosystec.unrar.rarfile.BaseBlock
    public void print() {
        super.print();
        this.logger.info("filetype: " + this.fileType);
        this.logger.info("creator :" + this.fileCreator);
    }
}
