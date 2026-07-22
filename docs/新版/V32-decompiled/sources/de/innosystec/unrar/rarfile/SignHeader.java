package de.innosystec.unrar.rarfile;

import de.innosystec.unrar.io.Raw;

/* JADX INFO: loaded from: classes2.dex */
public class SignHeader extends BaseBlock {
    public static final short signHeaderSize = 8;
    private short arcNameSize;
    private int creationTime;
    private short userNameSize;

    public SignHeader(BaseBlock baseBlock, byte[] bArr) {
        super(baseBlock);
        this.creationTime = 0;
        this.arcNameSize = (short) 0;
        this.userNameSize = (short) 0;
        this.creationTime = Raw.readIntLittleEndian(bArr, 0);
        this.arcNameSize = Raw.readShortLittleEndian(bArr, 4);
        this.userNameSize = Raw.readShortLittleEndian(bArr, 6);
    }

    public short getArcNameSize() {
        return this.arcNameSize;
    }

    public int getCreationTime() {
        return this.creationTime;
    }

    public short getUserNameSize() {
        return this.userNameSize;
    }
}
