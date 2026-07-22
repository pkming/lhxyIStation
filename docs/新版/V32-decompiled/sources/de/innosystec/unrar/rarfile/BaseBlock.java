package de.innosystec.unrar.rarfile;

import de.innosystec.unrar.io.Raw;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: classes2.dex */
public class BaseBlock {
    public static final short BaseBlockSize = 7;
    public static final short EARC_DATACRC = 2;
    public static final short EARC_NEXT_VOLUME = 1;
    public static final short EARC_REVSPACE = 4;
    public static final short EARC_VOLNUMBER = 8;
    public static final short LHD_COMMENT = 8;
    public static final short LHD_DIRECTORY = 224;
    public static final short LHD_EXTFLAGS = 8192;
    public static final short LHD_EXTTIME = 4096;
    public static final short LHD_LARGE = 256;
    public static final short LHD_PASSWORD = 4;
    public static final short LHD_SALT = 1024;
    public static final short LHD_SOLID = 16;
    public static final short LHD_SPLIT_AFTER = 2;
    public static final short LHD_SPLIT_BEFORE = 1;
    public static final short LHD_UNICODE = 512;
    public static final short LHD_VERSION = 2048;
    public static final short LHD_WINDOW1024 = 128;
    public static final short LHD_WINDOW128 = 32;
    public static final short LHD_WINDOW2048 = 160;
    public static final short LHD_WINDOW256 = 64;
    public static final short LHD_WINDOW4096 = 192;
    public static final short LHD_WINDOW512 = 96;
    public static final short LHD_WINDOW64 = 0;
    public static final short LHD_WINDOWMASK = 224;
    public static final short LONG_BLOCK = Short.MIN_VALUE;
    public static final short MHD_AV = 32;
    public static final short MHD_COMMENT = 2;
    public static final short MHD_ENCRYPTVER = 512;
    public static final short MHD_FIRSTVOLUME = 256;
    public static final short MHD_LOCK = 4;
    public static final short MHD_NEWNUMBERING = 16;
    public static final short MHD_PACK_COMMENT = 16;
    public static final short MHD_PASSWORD = 128;
    public static final short MHD_PROTECT = 64;
    public static final short MHD_SOLID = 8;
    public static final short MHD_VOLUME = 1;
    public static final short SKIP_IF_UNKNOWN = 16384;
    protected short flags;
    protected short headCRC;
    protected short headerSize;
    protected byte headerType;
    Log logger;
    protected long positionInFile;

    public BaseBlock() {
        this.logger = LogFactory.getLog(BaseBlock.class.getName());
        this.headCRC = (short) 0;
        this.headerType = (byte) 0;
        this.flags = (short) 0;
        this.headerSize = (short) 0;
    }

    public BaseBlock(BaseBlock baseBlock) {
        this.logger = LogFactory.getLog(BaseBlock.class.getName());
        this.headCRC = (short) 0;
        this.headerType = (byte) 0;
        this.flags = (short) 0;
        this.headerSize = (short) 0;
        this.flags = baseBlock.getFlags();
        this.headCRC = baseBlock.getHeadCRC();
        this.headerType = baseBlock.getHeaderType().getHeaderByte();
        this.headerSize = baseBlock.getHeaderSize();
        this.positionInFile = baseBlock.getPositionInFile();
    }

    public BaseBlock(byte[] bArr) {
        this.logger = LogFactory.getLog(BaseBlock.class.getName());
        this.headCRC = (short) 0;
        this.headerType = (byte) 0;
        this.flags = (short) 0;
        this.headerSize = (short) 0;
        this.headCRC = Raw.readShortLittleEndian(bArr, 0);
        this.headerType = (byte) (this.headerType | (bArr[2] & 255));
        this.flags = Raw.readShortLittleEndian(bArr, 3);
        this.headerSize = Raw.readShortLittleEndian(bArr, 5);
    }

    public boolean hasArchiveDataCRC() {
        return (this.flags & 2) != 0;
    }

    public boolean hasVolumeNumber() {
        return (this.flags & 8) != 0;
    }

    public boolean hasEncryptVersion() {
        return (this.flags & 512) != 0;
    }

    public boolean isSubBlock() {
        if (UnrarHeadertype.SubHeader.equals(this.headerType)) {
            return true;
        }
        return UnrarHeadertype.NewSubHeader.equals(this.headerType) && (this.flags & 16) != 0;
    }

    public long getPositionInFile() {
        return this.positionInFile;
    }

    public short getFlags() {
        return this.flags;
    }

    public short getHeadCRC() {
        return this.headCRC;
    }

    public short getHeaderSize() {
        return this.headerSize;
    }

    public UnrarHeadertype getHeaderType() {
        return UnrarHeadertype.findType(this.headerType);
    }

    public void setPositionInFile(long j) {
        this.positionInFile = j;
    }

    public void print() {
        StringBuilder sb = new StringBuilder();
        sb.append("HeaderType: " + getHeaderType());
        sb.append("\nHeadCRC: " + Integer.toHexString(getHeadCRC()));
        sb.append("\nFlags: " + Integer.toHexString(getFlags()));
        sb.append("\nHeaderSize: " + ((int) getHeaderSize()));
        sb.append("\nPosition in file: " + getPositionInFile());
        this.logger.info(sb.toString());
    }
}
