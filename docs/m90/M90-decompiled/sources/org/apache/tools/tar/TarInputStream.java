package org.apache.tools.tar;

import android.accounts.GrantCredentialsPermissionActivity;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import org.apache.tools.zip.ZipEncoding;
import org.apache.tools.zip.ZipEncodingHelper;

/* JADX INFO: loaded from: classes3.dex */
public class TarInputStream extends FilterInputStream {
    private static final int BUFFER_SIZE = 8192;
    private static final int BYTE_MASK = 255;
    private static final int LARGE_BUFFER_SIZE = 32768;
    private static final int SMALL_BUFFER_SIZE = 256;
    private final byte[] SKIP_BUF;
    private final byte[] SMALL_BUF;
    protected TarBuffer buffer;
    protected TarEntry currEntry;
    protected boolean debug;
    private final ZipEncoding encoding;
    protected long entryOffset;
    protected long entrySize;
    protected boolean hasHitEOF;
    protected byte[] oneBuf;
    protected byte[] readBuf;

    @Override // java.io.FilterInputStream, java.io.InputStream
    public void mark(int i) {
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public boolean markSupported() {
        return false;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public void reset() {
    }

    public TarInputStream(InputStream inputStream) {
        this(inputStream, 10240, 512);
    }

    public TarInputStream(InputStream inputStream, String str) {
        this(inputStream, 10240, 512, str);
    }

    public TarInputStream(InputStream inputStream, int i) {
        this(inputStream, i, 512);
    }

    public TarInputStream(InputStream inputStream, int i, String str) {
        this(inputStream, i, 512, str);
    }

    public TarInputStream(InputStream inputStream, int i, int i2) {
        this(inputStream, i, i2, null);
    }

    public TarInputStream(InputStream inputStream, int i, int i2, String str) {
        super(inputStream);
        this.SKIP_BUF = new byte[8192];
        this.SMALL_BUF = new byte[256];
        this.buffer = new TarBuffer(inputStream, i, i2);
        this.readBuf = null;
        this.oneBuf = new byte[1];
        this.debug = false;
        this.hasHitEOF = false;
        this.encoding = ZipEncodingHelper.getZipEncoding(str);
    }

    public void setDebug(boolean z) {
        this.debug = z;
        this.buffer.setDebug(z);
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.buffer.close();
    }

    public int getRecordSize() {
        return this.buffer.getRecordSize();
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int available() throws IOException {
        long j = this.entrySize;
        long j2 = this.entryOffset;
        if (j - j2 > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int) (j - j2);
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public long skip(long j) throws IOException {
        long j2 = j;
        while (j2 > 0) {
            byte[] bArr = this.SKIP_BUF;
            int i = read(bArr, 0, (int) (j2 > ((long) bArr.length) ? bArr.length : j2));
            if (i == -1) {
                break;
            }
            j2 -= (long) i;
        }
        return j - j2;
    }

    public TarEntry getNextEntry() throws IOException {
        if (this.hasHitEOF) {
            return null;
        }
        if (this.currEntry != null) {
            long j = this.entrySize - this.entryOffset;
            if (this.debug) {
                System.err.println("TarInputStream: SKIP currENTRY '" + this.currEntry.getName() + "' SZ " + this.entrySize + " OFF " + this.entryOffset + "  skipping " + j + " bytes");
            }
            while (j > 0) {
                long jSkip = skip(j);
                if (jSkip <= 0) {
                    throw new RuntimeException("failed to skip current tar entry");
                }
                j -= jSkip;
            }
            this.readBuf = null;
        }
        byte[] record = getRecord();
        if (this.hasHitEOF) {
            this.currEntry = null;
            return null;
        }
        try {
            this.currEntry = new TarEntry(record, this.encoding);
            if (this.debug) {
                System.err.println("TarInputStream: SET CURRENTRY '" + this.currEntry.getName() + "' size = " + this.currEntry.getSize());
            }
            this.entryOffset = 0L;
            this.entrySize = this.currEntry.getSize();
            if (this.currEntry.isGNULongLinkEntry()) {
                byte[] longNameData = getLongNameData();
                if (longNameData == null) {
                    return null;
                }
                this.currEntry.setLinkName(this.encoding.decode(longNameData));
            }
            if (this.currEntry.isGNULongNameEntry()) {
                byte[] longNameData2 = getLongNameData();
                if (longNameData2 == null) {
                    return null;
                }
                this.currEntry.setName(this.encoding.decode(longNameData2));
            }
            if (this.currEntry.isPaxHeader()) {
                paxHeaders();
            }
            if (this.currEntry.isGNUSparse()) {
                readGNUSparse();
            }
            this.entrySize = this.currEntry.getSize();
            return this.currEntry;
        } catch (IllegalArgumentException e) {
            IOException iOException = new IOException("Error detected parsing the header");
            iOException.initCause(e);
            throw iOException;
        }
    }

    protected byte[] getLongNameData() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (true) {
            int i = read(this.SMALL_BUF);
            if (i < 0) {
                break;
            }
            byteArrayOutputStream.write(this.SMALL_BUF, 0, i);
        }
        getNextEntry();
        if (this.currEntry == null) {
            return null;
        }
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        int length = byteArray.length;
        while (length > 0 && byteArray[length - 1] == 0) {
            length--;
        }
        if (length == byteArray.length) {
            return byteArray;
        }
        byte[] bArr = new byte[length];
        System.arraycopy(byteArray, 0, bArr, 0, length);
        return bArr;
    }

    private byte[] getRecord() throws IOException {
        if (this.hasHitEOF) {
            return null;
        }
        byte[] record = this.buffer.readRecord();
        if (record == null) {
            if (this.debug) {
                System.err.println("READ NULL RECORD");
            }
            this.hasHitEOF = true;
        } else if (this.buffer.isEOFRecord(record)) {
            if (this.debug) {
                System.err.println("READ EOF RECORD");
            }
            this.hasHitEOF = true;
        }
        if (this.hasHitEOF) {
            return null;
        }
        return record;
    }

    private void paxHeaders() throws IOException {
        Map<String, String> paxHeaders = parsePaxHeaders(this);
        getNextEntry();
        applyPaxHeadersToCurrentEntry(paxHeaders);
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x0068, code lost:
    
        r4 = r6;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    java.util.Map<java.lang.String, java.lang.String> parsePaxHeaders(java.io.InputStream r10) throws java.io.IOException {
        /*
            r9 = this;
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
        L5:
            r1 = 0
            r2 = r1
            r3 = r2
        L8:
            int r4 = r10.read()
            r5 = -1
            if (r4 == r5) goto L70
            int r2 = r2 + 1
            r6 = 32
            if (r4 != r6) goto L6a
            java.io.ByteArrayOutputStream r4 = new java.io.ByteArrayOutputStream
            r4.<init>()
        L1a:
            int r6 = r10.read()
            if (r6 == r5) goto L68
            int r2 = r2 + 1
            r7 = 61
            if (r6 != r7) goto L63
            java.lang.String r7 = "UTF-8"
            java.lang.String r4 = r4.toString(r7)
            int r3 = r3 - r2
            byte[] r2 = new byte[r3]
            int r8 = r10.read(r2)
            if (r8 != r3) goto L40
            java.lang.String r8 = new java.lang.String
            int r3 = r3 + (-1)
            r8.<init>(r2, r1, r3, r7)
            r0.put(r4, r8)
            goto L68
        L40:
            java.io.IOException r10 = new java.io.IOException
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Failed to read Paxheader. Expected "
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.StringBuilder r0 = r0.append(r3)
            java.lang.String r1 = " bytes, read "
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.StringBuilder r0 = r0.append(r8)
            java.lang.String r0 = r0.toString()
            r10.<init>(r0)
            throw r10
        L63:
            byte r6 = (byte) r6
            r4.write(r6)
            goto L1a
        L68:
            r4 = r6
            goto L70
        L6a:
            int r3 = r3 * 10
            int r4 = r4 + (-48)
            int r3 = r3 + r4
            goto L8
        L70:
            if (r4 != r5) goto L5
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.tar.TarInputStream.parsePaxHeaders(java.io.InputStream):java.util.Map");
    }

    private void applyPaxHeadersToCurrentEntry(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if ("path".equals(key)) {
                this.currEntry.setName(value);
            } else if ("linkpath".equals(key)) {
                this.currEntry.setLinkName(value);
            } else if ("gid".equals(key)) {
                this.currEntry.setGroupId(Integer.parseInt(value));
            } else if ("gname".equals(key)) {
                this.currEntry.setGroupName(value);
            } else if (GrantCredentialsPermissionActivity.EXTRAS_REQUESTING_UID.equals(key)) {
                this.currEntry.setUserId(Integer.parseInt(value));
            } else if ("uname".equals(key)) {
                this.currEntry.setUserName(value);
            } else if ("size".equals(key)) {
                this.currEntry.setSize(Long.parseLong(value));
            } else if ("mtime".equals(key)) {
                this.currEntry.setModTime((long) (Double.parseDouble(value) * 1000.0d));
            } else if ("SCHILY.devminor".equals(key)) {
                this.currEntry.setDevMinor(Integer.parseInt(value));
            } else if ("SCHILY.devmajor".equals(key)) {
                this.currEntry.setDevMajor(Integer.parseInt(value));
            }
        }
    }

    private void readGNUSparse() throws IOException {
        byte[] record;
        if (this.currEntry.isExtended()) {
            do {
                record = getRecord();
                if (this.hasHitEOF) {
                    this.currEntry = null;
                    return;
                }
            } while (new TarArchiveSparseEntry(record).isExtended());
        }
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        if (read(this.oneBuf, 0, 1) == -1) {
            return -1;
        }
        return this.oneBuf[0] & 255;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        int i3;
        long j = this.entryOffset;
        long j2 = this.entrySize;
        if (j >= j2) {
            return -1;
        }
        if (((long) i2) + j > j2) {
            i2 = (int) (j2 - j);
        }
        byte[] bArr2 = this.readBuf;
        if (bArr2 != null) {
            int length = i2 > bArr2.length ? bArr2.length : i2;
            System.arraycopy(bArr2, 0, bArr, i, length);
            byte[] bArr3 = this.readBuf;
            if (length >= bArr3.length) {
                this.readBuf = null;
            } else {
                int length2 = bArr3.length - length;
                byte[] bArr4 = new byte[length2];
                System.arraycopy(bArr3, length, bArr4, 0, length2);
                this.readBuf = bArr4;
            }
            i3 = length + 0;
            i2 -= length;
            i += length;
        } else {
            i3 = 0;
        }
        while (i2 > 0) {
            byte[] record = this.buffer.readRecord();
            if (record == null) {
                throw new IOException("unexpected EOF with " + i2 + " bytes unread");
            }
            int length3 = record.length;
            if (length3 > i2) {
                System.arraycopy(record, 0, bArr, i, i2);
                int i4 = length3 - i2;
                byte[] bArr5 = new byte[i4];
                this.readBuf = bArr5;
                System.arraycopy(record, i2, bArr5, 0, i4);
                length3 = i2;
            } else {
                System.arraycopy(record, 0, bArr, i, length3);
            }
            i3 += length3;
            i2 -= length3;
            i += length3;
        }
        this.entryOffset += (long) i3;
        return i3;
    }

    public void copyEntryContents(OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[32768];
        while (true) {
            int i = read(bArr, 0, 32768);
            if (i == -1) {
                return;
            } else {
                outputStream.write(bArr, 0, i);
            }
        }
    }

    public boolean canReadEntryData(TarEntry tarEntry) {
        return !tarEntry.isGNUSparse();
    }
}
