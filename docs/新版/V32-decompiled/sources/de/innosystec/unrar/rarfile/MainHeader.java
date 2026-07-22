package de.innosystec.unrar.rarfile;

import de.innosystec.unrar.io.Raw;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: classes2.dex */
public class MainHeader extends BaseBlock {
    public static final short mainHeaderSize = 6;
    public static final short mainHeaderSizeWithEnc = 7;
    private byte encryptVersion;
    private short highPosAv;
    private Log logger;
    private int posAv;

    public MainHeader(BaseBlock baseBlock, byte[] bArr) {
        super(baseBlock);
        this.logger = LogFactory.getLog(MainHeader.class.getName());
        this.highPosAv = Raw.readShortLittleEndian(bArr, 0);
        this.posAv = Raw.readIntLittleEndian(bArr, 2);
        if (hasEncryptVersion()) {
            this.encryptVersion = (byte) (this.encryptVersion | (bArr[6] & 255));
        }
    }

    public boolean hasArchCmt() {
        return (this.flags & 2) != 0;
    }

    public byte getEncryptVersion() {
        return this.encryptVersion;
    }

    public short getHighPosAv() {
        return this.highPosAv;
    }

    public int getPosAv() {
        return this.posAv;
    }

    public boolean isEncrypted() {
        return (this.flags & 128) != 0;
    }

    public boolean isMultiVolume() {
        return (this.flags & 1) != 0;
    }

    public boolean isFirstVolume() {
        return (this.flags & 256) != 0;
    }

    @Override // de.innosystec.unrar.rarfile.BaseBlock
    public void print() {
        super.print();
        StringBuilder sb = new StringBuilder();
        sb.append("posav: " + getPosAv());
        sb.append("\nhighposav: " + ((int) getHighPosAv()));
        sb.append("\nhasencversion: " + hasEncryptVersion() + (hasEncryptVersion() ? Byte.valueOf(getEncryptVersion()) : ""));
        sb.append("\nhasarchcmt: " + hasArchCmt());
        sb.append("\nisEncrypted: " + isEncrypted());
        sb.append("\nisMultivolume: " + isMultiVolume());
        sb.append("\nisFirstvolume: " + isFirstVolume());
        sb.append("\nisSolid: " + isSolid());
        sb.append("\nisLocked: " + isLocked());
        sb.append("\nisProtected: " + isProtected());
        sb.append("\nisAV: " + isAV());
        this.logger.info(sb.toString());
    }

    public boolean isSolid() {
        return (this.flags & 8) != 0;
    }

    public boolean isLocked() {
        return (this.flags & 4) != 0;
    }

    public boolean isProtected() {
        return (this.flags & 64) != 0;
    }

    public boolean isAV() {
        return (this.flags & 32) != 0;
    }

    public boolean isNewNumbering() {
        return (this.flags & 16) != 0;
    }
}
