package org.apache.tools.tar;

import android.accounts.GrantCredentialsPermissionActivity;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.apache.tools.zip.ZipEncoding;
import org.apache.tools.zip.ZipEncodingHelper;

/* JADX INFO: loaded from: classes3.dex */
public class TarOutputStream extends FilterOutputStream {
    private static final ZipEncoding ASCII = ZipEncodingHelper.getZipEncoding("ASCII");
    public static final int BIGNUMBER_ERROR = 0;
    public static final int BIGNUMBER_POSIX = 2;
    public static final int BIGNUMBER_STAR = 1;
    public static final int LONGFILE_ERROR = 0;
    public static final int LONGFILE_GNU = 2;
    public static final int LONGFILE_POSIX = 3;
    public static final int LONGFILE_TRUNCATE = 1;
    private boolean addPaxHeadersForNonAsciiNames;
    protected byte[] assemBuf;
    protected int assemLen;
    private int bigNumberMode;
    protected TarBuffer buffer;
    private boolean closed;
    protected long currBytes;
    protected String currName;
    protected long currSize;
    protected boolean debug;
    private final ZipEncoding encoding;
    private boolean finished;
    private boolean haveUnclosedEntry;
    protected int longFileMode;
    protected byte[] oneBuf;
    protected byte[] recordBuf;

    public TarOutputStream(OutputStream outputStream) {
        this(outputStream, 10240, 512);
    }

    public TarOutputStream(OutputStream outputStream, String str) {
        this(outputStream, 10240, 512, str);
    }

    public TarOutputStream(OutputStream outputStream, int i) {
        this(outputStream, i, 512);
    }

    public TarOutputStream(OutputStream outputStream, int i, String str) {
        this(outputStream, i, 512, str);
    }

    public TarOutputStream(OutputStream outputStream, int i, int i2) {
        this(outputStream, i, i2, null);
    }

    public TarOutputStream(OutputStream outputStream, int i, int i2, String str) {
        super(outputStream);
        this.longFileMode = 0;
        this.bigNumberMode = 0;
        this.closed = false;
        this.haveUnclosedEntry = false;
        this.finished = false;
        this.addPaxHeadersForNonAsciiNames = false;
        this.encoding = ZipEncodingHelper.getZipEncoding(str);
        this.buffer = new TarBuffer(outputStream, i, i2);
        this.debug = false;
        this.assemLen = 0;
        this.assemBuf = new byte[i2];
        this.recordBuf = new byte[i2];
        this.oneBuf = new byte[1];
    }

    public void setLongFileMode(int i) {
        this.longFileMode = i;
    }

    public void setBigNumberMode(int i) {
        this.bigNumberMode = i;
    }

    public void setAddPaxHeadersForNonAsciiNames(boolean z) {
        this.addPaxHeadersForNonAsciiNames = z;
    }

    public void setDebug(boolean z) {
        this.debug = z;
    }

    public void setBufferDebug(boolean z) {
        this.buffer.setDebug(z);
    }

    public void finish() throws IOException {
        if (this.finished) {
            throw new IOException("This archive has already been finished");
        }
        if (this.haveUnclosedEntry) {
            throw new IOException("This archives contains unclosed entries.");
        }
        writeEOFRecord();
        writeEOFRecord();
        this.buffer.flushBlock();
        this.finished = true;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (!this.finished) {
            finish();
        }
        if (this.closed) {
            return;
        }
        this.buffer.close();
        this.out.close();
        this.closed = true;
    }

    public int getRecordSize() {
        return this.buffer.getRecordSize();
    }

    public void putNextEntry(TarEntry tarEntry) throws IOException {
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        }
        HashMap map = new HashMap();
        String name = tarEntry.getName();
        boolean zHandleLongName = handleLongName(name, map, "path", TarConstants.LF_GNUTYPE_LONGNAME, "file name");
        String linkName = tarEntry.getLinkName();
        boolean z = linkName != null && handleLongName(linkName, map, "linkpath", TarConstants.LF_GNUTYPE_LONGLINK, "link name");
        int i = this.bigNumberMode;
        if (i == 2) {
            addPaxHeadersForBigNumbers(map, tarEntry);
        } else if (i != 1) {
            failForBigNumbers(tarEntry);
        }
        if (this.addPaxHeadersForNonAsciiNames && !zHandleLongName && !ASCII.canEncode(name)) {
            map.put("path", name);
        }
        if (this.addPaxHeadersForNonAsciiNames && !z && ((tarEntry.isLink() || tarEntry.isSymbolicLink()) && !ASCII.canEncode(linkName))) {
            map.put("linkpath", linkName);
        }
        if (map.size() > 0) {
            writePaxHeaders(name, map);
        }
        tarEntry.writeEntryHeader(this.recordBuf, this.encoding, this.bigNumberMode == 1);
        this.buffer.writeRecord(this.recordBuf);
        this.currBytes = 0L;
        if (tarEntry.isDirectory()) {
            this.currSize = 0L;
        } else {
            this.currSize = tarEntry.getSize();
        }
        this.currName = name;
        this.haveUnclosedEntry = true;
    }

    public void closeEntry() throws IOException {
        byte[] bArr;
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        }
        if (!this.haveUnclosedEntry) {
            throw new IOException("No current entry to close");
        }
        int i = this.assemLen;
        if (i > 0) {
            while (true) {
                bArr = this.assemBuf;
                if (i >= bArr.length) {
                    break;
                }
                bArr[i] = 0;
                i++;
            }
            this.buffer.writeRecord(bArr);
            this.currBytes += (long) this.assemLen;
            this.assemLen = 0;
        }
        if (this.currBytes < this.currSize) {
            throw new IOException("entry '" + this.currName + "' closed at '" + this.currBytes + "' before the '" + this.currSize + "' bytes specified in the header were written");
        }
        this.haveUnclosedEntry = false;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int i) throws IOException {
        byte[] bArr = this.oneBuf;
        bArr[0] = (byte) i;
        write(bArr, 0, 1);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] bArr) throws IOException {
        write(bArr, 0, bArr.length);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] bArr, int i, int i2) throws IOException {
        if (this.currBytes + ((long) i2) > this.currSize) {
            throw new IOException("request to write '" + i2 + "' bytes exceeds size in header of '" + this.currSize + "' bytes for entry '" + this.currName + "'");
        }
        int i3 = this.assemLen;
        if (i3 > 0) {
            int i4 = i3 + i2;
            byte[] bArr2 = this.recordBuf;
            if (i4 >= bArr2.length) {
                int length = bArr2.length - i3;
                System.arraycopy(this.assemBuf, 0, bArr2, 0, i3);
                System.arraycopy(bArr, i, this.recordBuf, this.assemLen, length);
                this.buffer.writeRecord(this.recordBuf);
                this.currBytes += (long) this.recordBuf.length;
                i += length;
                i2 -= length;
                this.assemLen = 0;
            } else {
                System.arraycopy(bArr, i, this.assemBuf, i3, i2);
                i += i2;
                this.assemLen += i2;
                i2 = 0;
            }
        }
        while (i2 > 0) {
            if (i2 < this.recordBuf.length) {
                System.arraycopy(bArr, i, this.assemBuf, this.assemLen, i2);
                this.assemLen += i2;
                return;
            } else {
                this.buffer.writeRecord(bArr, i);
                int length2 = this.recordBuf.length;
                this.currBytes += (long) length2;
                i2 -= length2;
                i += length2;
            }
        }
    }

    void writePaxHeaders(String str, Map<String, String> map) throws IOException {
        String strSubstring = "./PaxHeaders.X/" + stripTo7Bits(str);
        if (strSubstring.length() >= 100) {
            strSubstring = strSubstring.substring(0, 99);
        }
        while (strSubstring.endsWith("/")) {
            strSubstring = strSubstring.substring(0, strSubstring.length() - 1);
        }
        TarEntry tarEntry = new TarEntry(strSubstring, TarConstants.LF_PAX_EXTENDED_HEADER_LC);
        StringWriter stringWriter = new StringWriter();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            int length = key.length() + value.length() + 3 + 2;
            String str2 = length + " " + key + "=" + value + "\n";
            int length2 = str2.getBytes("UTF-8").length;
            while (length != length2) {
                str2 = length2 + " " + key + "=" + value + "\n";
                int i = length2;
                length2 = str2.getBytes("UTF-8").length;
                length = i;
            }
            stringWriter.write(str2);
        }
        byte[] bytes = stringWriter.toString().getBytes("UTF-8");
        tarEntry.setSize(bytes.length);
        putNextEntry(tarEntry);
        write(bytes);
        closeEntry();
    }

    private String stripTo7Bits(String str) {
        int length = str.length();
        StringBuffer stringBuffer = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            char cCharAt = (char) (str.charAt(i) & 127);
            if (cCharAt != 0) {
                stringBuffer.append(cCharAt);
            }
        }
        return stringBuffer.toString();
    }

    private void writeEOFRecord() throws IOException {
        int i = 0;
        while (true) {
            byte[] bArr = this.recordBuf;
            if (i < bArr.length) {
                bArr[i] = 0;
                i++;
            } else {
                this.buffer.writeRecord(bArr);
                return;
            }
        }
    }

    private void addPaxHeadersForBigNumbers(Map<String, String> map, TarEntry tarEntry) {
        addPaxHeaderForBigNumber(map, "size", tarEntry.getSize(), TarConstants.MAXSIZE);
        addPaxHeaderForBigNumber(map, "gid", tarEntry.getGroupId(), TarConstants.MAXID);
        addPaxHeaderForBigNumber(map, "mtime", tarEntry.getModTime().getTime() / 1000, TarConstants.MAXSIZE);
        addPaxHeaderForBigNumber(map, GrantCredentialsPermissionActivity.EXTRAS_REQUESTING_UID, tarEntry.getUserId(), TarConstants.MAXID);
        addPaxHeaderForBigNumber(map, "SCHILY.devmajor", tarEntry.getDevMajor(), TarConstants.MAXID);
        addPaxHeaderForBigNumber(map, "SCHILY.devminor", tarEntry.getDevMinor(), TarConstants.MAXID);
        failForBigNumber("mode", tarEntry.getMode(), TarConstants.MAXID);
    }

    private void addPaxHeaderForBigNumber(Map<String, String> map, String str, long j, long j2) {
        if (j < 0 || j > j2) {
            map.put(str, String.valueOf(j));
        }
    }

    private void failForBigNumbers(TarEntry tarEntry) {
        failForBigNumber("entry size", tarEntry.getSize(), TarConstants.MAXSIZE);
        failForBigNumber("group id", tarEntry.getGroupId(), TarConstants.MAXID);
        failForBigNumber("last modification time", tarEntry.getModTime().getTime() / 1000, TarConstants.MAXSIZE);
        failForBigNumber("user id", tarEntry.getUserId(), TarConstants.MAXID);
        failForBigNumber("mode", tarEntry.getMode(), TarConstants.MAXID);
        failForBigNumber("major device number", tarEntry.getDevMajor(), TarConstants.MAXID);
        failForBigNumber("minor device number", tarEntry.getDevMinor(), TarConstants.MAXID);
    }

    private void failForBigNumber(String str, long j, long j2) {
        if (j < 0 || j > j2) {
            throw new RuntimeException(str + " '" + j + "' is too big ( > " + j2 + " )");
        }
    }

    private boolean handleLongName(String str, Map<String, String> map, String str2, byte b, String str3) throws IOException {
        ByteBuffer byteBufferEncode = this.encoding.encode(str);
        int iLimit = byteBufferEncode.limit() - byteBufferEncode.position();
        if (iLimit >= 100) {
            int i = this.longFileMode;
            if (i == 3) {
                map.put(str2, str);
                return true;
            }
            if (i == 2) {
                TarEntry tarEntry = new TarEntry(TarConstants.GNU_LONGLINK, b);
                tarEntry.setSize(iLimit + 1);
                putNextEntry(tarEntry);
                write(byteBufferEncode.array(), byteBufferEncode.arrayOffset(), iLimit);
                write(0);
                closeEntry();
            } else if (i != 1) {
                throw new RuntimeException(str3 + " '" + str + "' is too long ( > 100 bytes)");
            }
        }
        return false;
    }
}
