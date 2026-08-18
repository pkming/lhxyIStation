package de.innosystec.unrar.rarfile;

import de.innosystec.unrar.io.Raw;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: classes2.dex */
public class MarkHeader extends BaseBlock {
    private Log logger;
    private boolean oldFormat;

    public MarkHeader(BaseBlock baseBlock) {
        super(baseBlock);
        this.logger = LogFactory.getLog(MarkHeader.class.getName());
        this.oldFormat = false;
    }

    public boolean isValid() {
        return getHeadCRC() == 24914 && getHeaderType() == UnrarHeadertype.MarkHeader && getFlags() == 6689 && getHeaderSize() == 7;
    }

    public boolean isSignature() {
        byte[] bArr = new byte[7];
        Raw.writeShortLittleEndian(bArr, 0, this.headCRC);
        bArr[2] = this.headerType;
        Raw.writeShortLittleEndian(bArr, 3, this.flags);
        Raw.writeShortLittleEndian(bArr, 5, this.headerSize);
        if (bArr[0] != 82) {
            return false;
        }
        if (bArr[1] == 69 && bArr[2] == 126 && bArr[3] == 94) {
            this.oldFormat = true;
        } else {
            if (bArr[1] != 97 || bArr[2] != 114 || bArr[3] != 33 || bArr[4] != 26 || bArr[5] != 7 || bArr[6] != 0) {
                return false;
            }
            this.oldFormat = false;
        }
        return true;
    }

    public boolean isOldFormat() {
        return this.oldFormat;
    }

    @Override // de.innosystec.unrar.rarfile.BaseBlock
    public void print() {
        super.print();
        this.logger.info("valid: " + isValid());
    }
}
