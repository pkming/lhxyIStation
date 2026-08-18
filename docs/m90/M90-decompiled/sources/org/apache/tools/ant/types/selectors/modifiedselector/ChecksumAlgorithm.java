package org.apache.tools.ant.types.selectors.modifiedselector;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;
import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public class ChecksumAlgorithm implements Algorithm {
    private String algorithm = "CRC";
    private Checksum checksum = null;

    public void setAlgorithm(String str) {
        this.algorithm = str != null ? str.toUpperCase(Locale.ENGLISH) : null;
    }

    public void initChecksum() {
        if (this.checksum != null) {
            return;
        }
        if ("CRC".equals(this.algorithm)) {
            this.checksum = new CRC32();
        } else {
            if ("ADLER".equals(this.algorithm)) {
                this.checksum = new Adler32();
                return;
            }
            throw new BuildException(new NoSuchAlgorithmException());
        }
    }

    @Override // org.apache.tools.ant.types.selectors.modifiedselector.Algorithm
    public boolean isValid() {
        return "CRC".equals(this.algorithm) || "ADLER".equals(this.algorithm);
    }

    @Override // org.apache.tools.ant.types.selectors.modifiedselector.Algorithm
    public String getValue(File file) {
        initChecksum();
        try {
            if (!file.canRead()) {
                return null;
            }
            this.checksum.reset();
            CheckedInputStream checkedInputStream = new CheckedInputStream(new FileInputStream(file), this.checksum);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(checkedInputStream);
            while (bufferedInputStream.read() != -1) {
            }
            String string = Long.toString(checkedInputStream.getChecksum().getValue());
            bufferedInputStream.close();
            return string;
        } catch (Exception unused) {
            return null;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<ChecksumAlgorithm:");
        sb.append("algorithm=").append(this.algorithm);
        sb.append(">");
        return sb.toString();
    }
}
