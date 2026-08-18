package de.innosystec.unrar.rarfile;

import de.innosystec.unrar.io.Raw;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: classes2.dex */
public class UnixOwnersHeader extends SubBlockHeader {
    private String group;
    private int groupNameSize;
    private Log logger;
    private String owner;
    private int ownerNameSize;

    public UnixOwnersHeader(SubBlockHeader subBlockHeader, byte[] bArr) {
        super(subBlockHeader);
        this.logger = LogFactory.getLog(UnixOwnersHeader.class);
        this.ownerNameSize = Raw.readShortLittleEndian(bArr, 0) & 65535;
        this.groupNameSize = Raw.readShortLittleEndian(bArr, 2) & 65535;
        int i = this.ownerNameSize;
        if (4 + i < bArr.length) {
            byte[] bArr2 = new byte[i];
            System.arraycopy(bArr, 4, bArr2, 0, i);
            this.owner = new String(bArr2);
        }
        int i2 = 4 + this.ownerNameSize;
        int i3 = this.groupNameSize;
        if (i2 + i3 < bArr.length) {
            byte[] bArr3 = new byte[i3];
            System.arraycopy(bArr, i2, bArr3, 0, i3);
            this.group = new String(bArr3);
        }
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String str) {
        this.group = str;
    }

    public int getGroupNameSize() {
        return this.groupNameSize;
    }

    public void setGroupNameSize(int i) {
        this.groupNameSize = i;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String str) {
        this.owner = str;
    }

    public int getOwnerNameSize() {
        return this.ownerNameSize;
    }

    public void setOwnerNameSize(int i) {
        this.ownerNameSize = i;
    }

    @Override // de.innosystec.unrar.rarfile.SubBlockHeader, de.innosystec.unrar.rarfile.BlockHeader, de.innosystec.unrar.rarfile.BaseBlock
    public void print() {
        super.print();
        this.logger.info("ownerNameSize: " + this.ownerNameSize);
        this.logger.info("owner: " + this.owner);
        this.logger.info("groupNameSize: " + this.groupNameSize);
        this.logger.info("group: " + this.group);
    }
}
