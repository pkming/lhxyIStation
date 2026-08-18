package org.apache.tools.zip;

import java.util.zip.ZipException;

/* JADX INFO: loaded from: classes3.dex */
public interface ZipExtraField {
    byte[] getCentralDirectoryData();

    ZipShort getCentralDirectoryLength();

    ZipShort getHeaderId();

    byte[] getLocalFileDataData();

    ZipShort getLocalFileDataLength();

    void parseFromLocalFileData(byte[] bArr, int i, int i2) throws ZipException;
}
