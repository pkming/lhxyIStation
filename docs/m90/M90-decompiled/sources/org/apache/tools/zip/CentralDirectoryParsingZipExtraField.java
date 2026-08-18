package org.apache.tools.zip;

import java.util.zip.ZipException;

/* JADX INFO: loaded from: classes3.dex */
public interface CentralDirectoryParsingZipExtraField extends ZipExtraField {
    void parseFromCentralDirectoryData(byte[] bArr, int i, int i2) throws ZipException;
}
