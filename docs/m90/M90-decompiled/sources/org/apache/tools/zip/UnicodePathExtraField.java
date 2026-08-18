package org.apache.tools.zip;

/* JADX INFO: loaded from: classes3.dex */
public class UnicodePathExtraField extends AbstractUnicodeExtraField {
    public static final ZipShort UPATH_ID = new ZipShort(28789);

    public UnicodePathExtraField() {
    }

    public UnicodePathExtraField(String str, byte[] bArr, int i, int i2) {
        super(str, bArr, i, i2);
    }

    public UnicodePathExtraField(String str, byte[] bArr) {
        super(str, bArr);
    }

    @Override // org.apache.tools.zip.ZipExtraField
    public ZipShort getHeaderId() {
        return UPATH_ID;
    }
}
