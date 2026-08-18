package de.innosystec.unrar.rarfile;

import java.util.Arrays;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes2.dex */
public class NewSubHeaderType {
    private byte[] headerTypes;
    public static final NewSubHeaderType SUBHEAD_TYPE_CMT = new NewSubHeaderType(new byte[]{67, 77, 84});
    public static final NewSubHeaderType SUBHEAD_TYPE_ACL = new NewSubHeaderType(new byte[]{65, 67, TarConstants.LF_GNUTYPE_LONGNAME});
    public static final NewSubHeaderType SUBHEAD_TYPE_STREAM = new NewSubHeaderType(new byte[]{TarConstants.LF_GNUTYPE_SPARSE, 84, 77});
    public static final NewSubHeaderType SUBHEAD_TYPE_UOWNER = new NewSubHeaderType(new byte[]{85, 79, 87});
    public static final NewSubHeaderType SUBHEAD_TYPE_AV = new NewSubHeaderType(new byte[]{65, 86});
    public static final NewSubHeaderType SUBHEAD_TYPE_RR = new NewSubHeaderType(new byte[]{82, 82});
    public static final NewSubHeaderType SUBHEAD_TYPE_OS2EA = new NewSubHeaderType(new byte[]{69, 65, TarConstants.LF_SYMLINK});
    public static final NewSubHeaderType SUBHEAD_TYPE_BEOSEA = new NewSubHeaderType(new byte[]{69, 65, 66, 69});

    private NewSubHeaderType(byte[] bArr) {
        this.headerTypes = bArr;
    }

    public boolean byteEquals(byte[] bArr) {
        return Arrays.equals(this.headerTypes, bArr);
    }

    public String toString() {
        return new String(this.headerTypes);
    }
}
