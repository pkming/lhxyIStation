package org.apache.tools.zip;

import android.widget.ExpandableListView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.ZipException;

/* JADX INFO: loaded from: classes3.dex */
public class ZipOutputStream extends FilterOutputStream {
    private static final int BUFFER_SIZE = 512;
    public static final int DEFAULT_COMPRESSION = -1;
    static final String DEFAULT_ENCODING = null;
    public static final int DEFLATED = 8;
    private static final int DEFLATER_BLOCK_SIZE = 8192;

    @Deprecated
    public static final int EFS_FLAG = 2048;
    public static final int STORED = 0;
    protected byte[] buf;
    private long cdLength;
    private long cdOffset;
    private String comment;
    private final CRC32 crc;
    private UnicodeExtraFieldPolicy createUnicodeExtraFields;
    protected final Deflater def;
    private String encoding;
    private final List<ZipEntry> entries;
    private CurrentEntry entry;
    private boolean fallbackToUTF8;
    private boolean finished;
    private boolean hasCompressionLevelChanged;
    private boolean hasUsedZip64;
    private int level;
    private int method;
    private final Map<ZipEntry, Long> offsets;
    private final RandomAccessFile raf;
    private boolean useUTF8Flag;
    private long written;
    private Zip64Mode zip64Mode;
    private ZipEncoding zipEncoding;
    private static final byte[] EMPTY = new byte[0];
    private static final byte[] ZERO = {0, 0};
    private static final byte[] LZERO = {0, 0, 0, 0};
    protected static final byte[] LFH_SIG = ZipLong.LFH_SIG.getBytes();
    protected static final byte[] DD_SIG = ZipLong.DD_SIG.getBytes();
    protected static final byte[] CFH_SIG = ZipLong.CFH_SIG.getBytes();
    protected static final byte[] EOCD_SIG = ZipLong.getBytes(101010256);
    static final byte[] ZIP64_EOCD_SIG = ZipLong.getBytes(101075792);
    static final byte[] ZIP64_EOCD_LOC_SIG = ZipLong.getBytes(117853008);
    private static final byte[] ONE = ZipLong.getBytes(1);

    public ZipOutputStream(OutputStream outputStream) {
        super(outputStream);
        this.finished = false;
        this.comment = "";
        this.level = -1;
        this.hasCompressionLevelChanged = false;
        this.method = 8;
        this.entries = new LinkedList();
        this.crc = new CRC32();
        this.written = 0L;
        this.cdOffset = 0L;
        this.cdLength = 0L;
        this.offsets = new HashMap();
        this.encoding = null;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(DEFAULT_ENCODING);
        this.def = new Deflater(this.level, true);
        this.buf = new byte[512];
        this.useUTF8Flag = true;
        this.fallbackToUTF8 = false;
        this.createUnicodeExtraFields = UnicodeExtraFieldPolicy.NEVER;
        this.hasUsedZip64 = false;
        this.zip64Mode = Zip64Mode.AsNeeded;
        this.raf = null;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ZipOutputStream(File file) throws IOException {
        RandomAccessFile randomAccessFile;
        super(null);
        RandomAccessFile randomAccessFile2 = null;
        this.finished = false;
        this.comment = "";
        this.level = -1;
        this.hasCompressionLevelChanged = false;
        this.method = 8;
        this.entries = new LinkedList();
        this.crc = new CRC32();
        this.written = 0L;
        this.cdOffset = 0L;
        this.cdLength = 0L;
        this.offsets = new HashMap();
        this.encoding = null;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(DEFAULT_ENCODING);
        this.def = new Deflater(this.level, true);
        this.buf = new byte[512];
        this.useUTF8Flag = true;
        this.fallbackToUTF8 = false;
        this.createUnicodeExtraFields = UnicodeExtraFieldPolicy.NEVER;
        this.hasUsedZip64 = false;
        this.zip64Mode = Zip64Mode.AsNeeded;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            try {
                randomAccessFile.setLength(0L);
            } catch (IOException unused) {
                if (randomAccessFile != null) {
                    try {
                        randomAccessFile.close();
                    } catch (IOException unused2) {
                    }
                } else {
                    randomAccessFile2 = randomAccessFile;
                }
                this.out = new FileOutputStream(file);
                randomAccessFile = randomAccessFile2;
            }
        } catch (IOException unused3) {
            randomAccessFile = null;
        }
        this.raf = randomAccessFile;
    }

    public boolean isSeekable() {
        return this.raf != null;
    }

    public void setEncoding(String str) {
        this.encoding = str;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(str);
        if (!this.useUTF8Flag || ZipEncodingHelper.isUTF8(str)) {
            return;
        }
        this.useUTF8Flag = false;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setUseLanguageEncodingFlag(boolean z) {
        this.useUTF8Flag = z && ZipEncodingHelper.isUTF8(this.encoding);
    }

    public void setCreateUnicodeExtraFields(UnicodeExtraFieldPolicy unicodeExtraFieldPolicy) {
        this.createUnicodeExtraFields = unicodeExtraFieldPolicy;
    }

    public void setFallbackToUTF8(boolean z) {
        this.fallbackToUTF8 = z;
    }

    public void setUseZip64(Zip64Mode zip64Mode) {
        this.zip64Mode = zip64Mode;
    }

    public void finish() throws IOException {
        if (this.finished) {
            throw new IOException("This archive has already been finished");
        }
        if (this.entry != null) {
            closeEntry();
        }
        this.cdOffset = this.written;
        Iterator<ZipEntry> it = this.entries.iterator();
        while (it.hasNext()) {
            writeCentralFileHeader(it.next());
        }
        this.cdLength = this.written - this.cdOffset;
        writeZip64CentralDirectory();
        writeCentralDirectoryEnd();
        this.offsets.clear();
        this.entries.clear();
        this.def.end();
        this.finished = true;
    }

    public void closeEntry() throws IOException {
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        }
        CurrentEntry currentEntry = this.entry;
        if (currentEntry == null) {
            throw new IOException("No current entry to close");
        }
        if (!currentEntry.hasWritten) {
            write(EMPTY, 0, 0);
        }
        flushDeflater();
        Zip64Mode effectiveZip64Mode = getEffectiveZip64Mode(this.entry.entry);
        long j = this.written - this.entry.dataStart;
        long value = this.crc.getValue();
        this.crc.reset();
        boolean zHandleSizesAndCrc = handleSizesAndCrc(j, value, effectiveZip64Mode);
        if (this.raf != null) {
            rewriteSizesAndCrc(zHandleSizesAndCrc);
        }
        writeDataDescriptor(this.entry.entry);
        this.entry = null;
    }

    private void flushDeflater() throws IOException {
        if (this.entry.entry.getMethod() == 8) {
            this.def.finish();
            while (!this.def.finished()) {
                deflate();
            }
        }
    }

    private boolean handleSizesAndCrc(long j, long j2, Zip64Mode zip64Mode) throws ZipException {
        if (this.entry.entry.getMethod() != 8) {
            if (this.raf != null) {
                this.entry.entry.setSize(j);
                this.entry.entry.setCompressedSize(j);
                this.entry.entry.setCrc(j2);
            } else {
                if (this.entry.entry.getCrc() != j2) {
                    throw new ZipException("bad CRC checksum for entry " + this.entry.entry.getName() + ": " + Long.toHexString(this.entry.entry.getCrc()) + " instead of " + Long.toHexString(j2));
                }
                if (this.entry.entry.getSize() != j) {
                    throw new ZipException("bad size for entry " + this.entry.entry.getName() + ": " + this.entry.entry.getSize() + " instead of " + j);
                }
            }
        } else {
            this.entry.entry.setSize(this.entry.bytesRead);
            this.entry.entry.setCompressedSize(j);
            this.entry.entry.setCrc(j2);
            this.def.reset();
        }
        boolean z = zip64Mode == Zip64Mode.Always || this.entry.entry.getSize() >= ExpandableListView.PACKED_POSITION_VALUE_NULL || this.entry.entry.getCompressedSize() >= ExpandableListView.PACKED_POSITION_VALUE_NULL;
        if (z && zip64Mode == Zip64Mode.Never) {
            throw new Zip64RequiredException(Zip64RequiredException.getEntryTooBigMessage(this.entry.entry));
        }
        return z;
    }

    private void rewriteSizesAndCrc(boolean z) throws IOException {
        long filePointer = this.raf.getFilePointer();
        this.raf.seek(this.entry.localDataStart);
        writeOut(ZipLong.getBytes(this.entry.entry.getCrc()));
        if (hasZip64Extra(this.entry.entry) && z) {
            writeOut(ZipLong.ZIP64_MAGIC.getBytes());
            writeOut(ZipLong.ZIP64_MAGIC.getBytes());
        } else {
            writeOut(ZipLong.getBytes(this.entry.entry.getCompressedSize()));
            writeOut(ZipLong.getBytes(this.entry.entry.getSize()));
        }
        if (hasZip64Extra(this.entry.entry)) {
            this.raf.seek(this.entry.localDataStart + 12 + 4 + ((long) getName(this.entry.entry).limit()) + 4);
            writeOut(ZipEightByteInteger.getBytes(this.entry.entry.getSize()));
            writeOut(ZipEightByteInteger.getBytes(this.entry.entry.getCompressedSize()));
            if (!z) {
                this.raf.seek(this.entry.localDataStart - 10);
                writeOut(ZipShort.getBytes(10));
                this.entry.entry.removeExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
                this.entry.entry.setExtra();
                if (this.entry.causedUseOfZip64) {
                    this.hasUsedZip64 = false;
                }
            }
        }
        this.raf.seek(filePointer);
    }

    public void putNextEntry(ZipEntry zipEntry) throws IOException {
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        }
        if (this.entry != null) {
            closeEntry();
        }
        CurrentEntry currentEntry = new CurrentEntry(zipEntry);
        this.entry = currentEntry;
        this.entries.add(currentEntry.entry);
        setDefaults(this.entry.entry);
        Zip64Mode effectiveZip64Mode = getEffectiveZip64Mode(this.entry.entry);
        validateSizeInformation(effectiveZip64Mode);
        if (shouldAddZip64Extra(this.entry.entry, effectiveZip64Mode)) {
            Zip64ExtendedInformationExtraField zip64Extra = getZip64Extra(this.entry.entry);
            ZipEightByteInteger zipEightByteInteger = ZipEightByteInteger.ZERO;
            if (this.entry.entry.getMethod() == 0 && this.entry.entry.getSize() != -1) {
                zipEightByteInteger = new ZipEightByteInteger(this.entry.entry.getSize());
            }
            zip64Extra.setSize(zipEightByteInteger);
            zip64Extra.setCompressedSize(zipEightByteInteger);
            this.entry.entry.setExtra();
        }
        if (this.entry.entry.getMethod() == 8 && this.hasCompressionLevelChanged) {
            this.def.setLevel(this.level);
            this.hasCompressionLevelChanged = false;
        }
        writeLocalFileHeader(this.entry.entry);
    }

    private void setDefaults(ZipEntry zipEntry) {
        if (zipEntry.getMethod() == -1) {
            zipEntry.setMethod(this.method);
        }
        if (zipEntry.getTime() == -1) {
            zipEntry.setTime(System.currentTimeMillis());
        }
    }

    private void validateSizeInformation(Zip64Mode zip64Mode) throws ZipException {
        if (this.entry.entry.getMethod() == 0 && this.raf == null) {
            if (this.entry.entry.getSize() == -1) {
                throw new ZipException("uncompressed size is required for STORED method when not writing to a file");
            }
            if (this.entry.entry.getCrc() == -1) {
                throw new ZipException("crc checksum is required for STORED method when not writing to a file");
            }
            this.entry.entry.setCompressedSize(this.entry.entry.getSize());
        }
        if ((this.entry.entry.getSize() >= ExpandableListView.PACKED_POSITION_VALUE_NULL || this.entry.entry.getCompressedSize() >= ExpandableListView.PACKED_POSITION_VALUE_NULL) && zip64Mode == Zip64Mode.Never) {
            throw new Zip64RequiredException(Zip64RequiredException.getEntryTooBigMessage(this.entry.entry));
        }
    }

    private boolean shouldAddZip64Extra(ZipEntry zipEntry, Zip64Mode zip64Mode) {
        return zip64Mode == Zip64Mode.Always || zipEntry.getSize() >= ExpandableListView.PACKED_POSITION_VALUE_NULL || zipEntry.getCompressedSize() >= ExpandableListView.PACKED_POSITION_VALUE_NULL || !(zipEntry.getSize() != -1 || this.raf == null || zip64Mode == Zip64Mode.Never);
    }

    public void setComment(String str) {
        this.comment = str;
    }

    public void setLevel(int i) {
        if (i < -1 || i > 9) {
            throw new IllegalArgumentException("Invalid compression level: " + i);
        }
        this.hasCompressionLevelChanged = this.level != i;
        this.level = i;
    }

    public void setMethod(int i) {
        this.method = i;
    }

    public boolean canWriteEntryData(ZipEntry zipEntry) {
        return ZipUtil.canHandleEntryData(zipEntry);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] bArr, int i, int i2) throws IOException {
        ZipUtil.checkRequestedFeatures(this.entry.entry);
        this.entry.hasWritten = true;
        if (this.entry.entry.getMethod() == 8) {
            writeDeflated(bArr, i, i2);
        } else {
            writeOut(bArr, i, i2);
            this.written += (long) i2;
        }
        this.crc.update(bArr, i, i2);
    }

    private void writeDeflated(byte[] bArr, int i, int i2) throws IOException {
        if (i2 <= 0 || this.def.finished()) {
            return;
        }
        CurrentEntry.access$314(this.entry, i2);
        if (i2 <= 8192) {
            this.def.setInput(bArr, i, i2);
            deflateUntilInputIsNeeded();
            return;
        }
        int i3 = i2 / 8192;
        for (int i4 = 0; i4 < i3; i4++) {
            this.def.setInput(bArr, (i4 * 8192) + i, 8192);
            deflateUntilInputIsNeeded();
        }
        int i5 = i3 * 8192;
        if (i5 < i2) {
            this.def.setInput(bArr, i + i5, i2 - i5);
            deflateUntilInputIsNeeded();
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (!this.finished) {
            finish();
        }
        destroy();
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        if (this.out != null) {
            this.out.flush();
        }
    }

    protected final void deflate() throws IOException {
        Deflater deflater = this.def;
        byte[] bArr = this.buf;
        int iDeflate = deflater.deflate(bArr, 0, bArr.length);
        if (iDeflate > 0) {
            writeOut(this.buf, 0, iDeflate);
            this.written += (long) iDeflate;
        }
    }

    protected void writeLocalFileHeader(ZipEntry zipEntry) throws IOException {
        boolean zCanEncode = this.zipEncoding.canEncode(zipEntry.getName());
        ByteBuffer name = getName(zipEntry);
        if (this.createUnicodeExtraFields != UnicodeExtraFieldPolicy.NEVER) {
            addUnicodeExtraFields(zipEntry, zCanEncode, name);
        }
        this.offsets.put(zipEntry, Long.valueOf(this.written));
        writeOut(LFH_SIG);
        this.written += 4;
        int method = zipEntry.getMethod();
        writeVersionNeededToExtractAndGeneralPurposeBits(method, !zCanEncode && this.fallbackToUTF8, hasZip64Extra(zipEntry));
        this.written += 4;
        writeOut(ZipShort.getBytes(method));
        this.written += 2;
        writeOut(ZipUtil.toDosTime(zipEntry.getTime()));
        long j = this.written + 4;
        this.written = j;
        this.entry.localDataStart = j;
        if (method == 8 || this.raf != null) {
            byte[] bArr = LZERO;
            writeOut(bArr);
            if (hasZip64Extra(this.entry.entry)) {
                writeOut(ZipLong.ZIP64_MAGIC.getBytes());
                writeOut(ZipLong.ZIP64_MAGIC.getBytes());
            } else {
                writeOut(bArr);
                writeOut(bArr);
            }
        } else {
            writeOut(ZipLong.getBytes(zipEntry.getCrc()));
            byte[] bytes = ZipLong.ZIP64_MAGIC.getBytes();
            if (!hasZip64Extra(zipEntry)) {
                bytes = ZipLong.getBytes(zipEntry.getSize());
            }
            writeOut(bytes);
            writeOut(bytes);
        }
        this.written += 12;
        writeOut(ZipShort.getBytes(name.limit()));
        this.written += 2;
        byte[] localFileDataExtra = zipEntry.getLocalFileDataExtra();
        writeOut(ZipShort.getBytes(localFileDataExtra.length));
        this.written += 2;
        writeOut(name.array(), name.arrayOffset(), name.limit() - name.position());
        this.written += (long) name.limit();
        writeOut(localFileDataExtra);
        long length = this.written + ((long) localFileDataExtra.length);
        this.written = length;
        this.entry.dataStart = length;
    }

    private void addUnicodeExtraFields(ZipEntry zipEntry, boolean z, ByteBuffer byteBuffer) throws IOException {
        if (this.createUnicodeExtraFields == UnicodeExtraFieldPolicy.ALWAYS || !z) {
            zipEntry.addExtraField(new UnicodePathExtraField(zipEntry.getName(), byteBuffer.array(), byteBuffer.arrayOffset(), byteBuffer.limit() - byteBuffer.position()));
        }
        String comment = zipEntry.getComment();
        if (comment == null || "".equals(comment)) {
            return;
        }
        boolean zCanEncode = this.zipEncoding.canEncode(comment);
        if (this.createUnicodeExtraFields == UnicodeExtraFieldPolicy.ALWAYS || !zCanEncode) {
            ByteBuffer byteBufferEncode = getEntryEncoding(zipEntry).encode(comment);
            zipEntry.addExtraField(new UnicodeCommentExtraField(comment, byteBufferEncode.array(), byteBufferEncode.arrayOffset(), byteBufferEncode.limit() - byteBufferEncode.position()));
        }
    }

    protected void writeDataDescriptor(ZipEntry zipEntry) throws IOException {
        if (zipEntry.getMethod() == 8 && this.raf == null) {
            writeOut(DD_SIG);
            writeOut(ZipLong.getBytes(zipEntry.getCrc()));
            int i = 4;
            if (!hasZip64Extra(zipEntry)) {
                writeOut(ZipLong.getBytes(zipEntry.getCompressedSize()));
                writeOut(ZipLong.getBytes(zipEntry.getSize()));
            } else {
                writeOut(ZipEightByteInteger.getBytes(zipEntry.getCompressedSize()));
                writeOut(ZipEightByteInteger.getBytes(zipEntry.getSize()));
                i = 8;
            }
            this.written += (long) ((i * 2) + 8);
        }
    }

    protected void writeCentralFileHeader(ZipEntry zipEntry) throws IOException {
        writeOut(CFH_SIG);
        this.written += 4;
        long jLongValue = this.offsets.get(zipEntry).longValue();
        boolean z = false;
        boolean z2 = hasZip64Extra(zipEntry) || zipEntry.getCompressedSize() >= ExpandableListView.PACKED_POSITION_VALUE_NULL || zipEntry.getSize() >= ExpandableListView.PACKED_POSITION_VALUE_NULL || jLongValue >= ExpandableListView.PACKED_POSITION_VALUE_NULL;
        if (z2 && this.zip64Mode == Zip64Mode.Never) {
            throw new Zip64RequiredException("archive's size exceeds the limit of 4GByte.");
        }
        handleZip64Extra(zipEntry, jLongValue, z2);
        writeOut(ZipShort.getBytes((zipEntry.getPlatform() << 8) | (!this.hasUsedZip64 ? 20 : 45)));
        this.written += 2;
        int method = zipEntry.getMethod();
        if (!this.zipEncoding.canEncode(zipEntry.getName()) && this.fallbackToUTF8) {
            z = true;
        }
        writeVersionNeededToExtractAndGeneralPurposeBits(method, z, z2);
        this.written += 4;
        writeOut(ZipShort.getBytes(method));
        this.written += 2;
        writeOut(ZipUtil.toDosTime(zipEntry.getTime()));
        this.written += 4;
        writeOut(ZipLong.getBytes(zipEntry.getCrc()));
        if (zipEntry.getCompressedSize() >= ExpandableListView.PACKED_POSITION_VALUE_NULL || zipEntry.getSize() >= ExpandableListView.PACKED_POSITION_VALUE_NULL) {
            writeOut(ZipLong.ZIP64_MAGIC.getBytes());
            writeOut(ZipLong.ZIP64_MAGIC.getBytes());
        } else {
            writeOut(ZipLong.getBytes(zipEntry.getCompressedSize()));
            writeOut(ZipLong.getBytes(zipEntry.getSize()));
        }
        this.written += 12;
        ByteBuffer name = getName(zipEntry);
        writeOut(ZipShort.getBytes(name.limit()));
        this.written += 2;
        byte[] centralDirectoryExtra = zipEntry.getCentralDirectoryExtra();
        writeOut(ZipShort.getBytes(centralDirectoryExtra.length));
        this.written += 2;
        String comment = zipEntry.getComment();
        if (comment == null) {
            comment = "";
        }
        ByteBuffer byteBufferEncode = getEntryEncoding(zipEntry).encode(comment);
        writeOut(ZipShort.getBytes(byteBufferEncode.limit()));
        this.written += 2;
        writeOut(ZERO);
        this.written += 2;
        writeOut(ZipShort.getBytes(zipEntry.getInternalAttributes()));
        this.written += 2;
        writeOut(ZipLong.getBytes(zipEntry.getExternalAttributes()));
        this.written += 4;
        writeOut(ZipLong.getBytes(Math.min(jLongValue, ExpandableListView.PACKED_POSITION_VALUE_NULL)));
        this.written += 4;
        writeOut(name.array(), name.arrayOffset(), name.limit() - name.position());
        this.written += (long) name.limit();
        writeOut(centralDirectoryExtra);
        this.written += (long) centralDirectoryExtra.length;
        writeOut(byteBufferEncode.array(), byteBufferEncode.arrayOffset(), byteBufferEncode.limit() - byteBufferEncode.position());
        this.written += (long) byteBufferEncode.limit();
    }

    private void handleZip64Extra(ZipEntry zipEntry, long j, boolean z) {
        if (z) {
            Zip64ExtendedInformationExtraField zip64Extra = getZip64Extra(zipEntry);
            if (zipEntry.getCompressedSize() >= ExpandableListView.PACKED_POSITION_VALUE_NULL || zipEntry.getSize() >= ExpandableListView.PACKED_POSITION_VALUE_NULL) {
                zip64Extra.setCompressedSize(new ZipEightByteInteger(zipEntry.getCompressedSize()));
                zip64Extra.setSize(new ZipEightByteInteger(zipEntry.getSize()));
            } else {
                zip64Extra.setCompressedSize(null);
                zip64Extra.setSize(null);
            }
            if (j >= ExpandableListView.PACKED_POSITION_VALUE_NULL) {
                zip64Extra.setRelativeHeaderOffset(new ZipEightByteInteger(j));
            }
            zipEntry.setExtra();
        }
    }

    protected void writeCentralDirectoryEnd() throws IOException {
        writeOut(EOCD_SIG);
        byte[] bArr = ZERO;
        writeOut(bArr);
        writeOut(bArr);
        int size = this.entries.size();
        if (size > 65535 && this.zip64Mode == Zip64Mode.Never) {
            throw new Zip64RequiredException("archive contains more than 65535 entries.");
        }
        if (this.cdOffset > ExpandableListView.PACKED_POSITION_VALUE_NULL && this.zip64Mode == Zip64Mode.Never) {
            throw new Zip64RequiredException("archive's size exceeds the limit of 4GByte.");
        }
        byte[] bytes = ZipShort.getBytes(Math.min(size, 65535));
        writeOut(bytes);
        writeOut(bytes);
        writeOut(ZipLong.getBytes(Math.min(this.cdLength, ExpandableListView.PACKED_POSITION_VALUE_NULL)));
        writeOut(ZipLong.getBytes(Math.min(this.cdOffset, ExpandableListView.PACKED_POSITION_VALUE_NULL)));
        ByteBuffer byteBufferEncode = this.zipEncoding.encode(this.comment);
        writeOut(ZipShort.getBytes(byteBufferEncode.limit()));
        writeOut(byteBufferEncode.array(), byteBufferEncode.arrayOffset(), byteBufferEncode.limit() - byteBufferEncode.position());
    }

    protected static ZipLong toDosTime(Date date) {
        return ZipUtil.toDosTime(date);
    }

    protected static byte[] toDosTime(long j) {
        return ZipUtil.toDosTime(j);
    }

    protected byte[] getBytes(String str) throws ZipException {
        try {
            ByteBuffer byteBufferEncode = ZipEncodingHelper.getZipEncoding(this.encoding).encode(str);
            int iLimit = byteBufferEncode.limit();
            byte[] bArr = new byte[iLimit];
            System.arraycopy(byteBufferEncode.array(), byteBufferEncode.arrayOffset(), bArr, 0, iLimit);
            return bArr;
        } catch (IOException e) {
            throw new ZipException("Failed to encode name: " + e.getMessage());
        }
    }

    protected void writeZip64CentralDirectory() throws IOException {
        if (this.zip64Mode == Zip64Mode.Never) {
            return;
        }
        if (!this.hasUsedZip64 && (this.cdOffset >= ExpandableListView.PACKED_POSITION_VALUE_NULL || this.cdLength >= ExpandableListView.PACKED_POSITION_VALUE_NULL || this.entries.size() >= 65535)) {
            this.hasUsedZip64 = true;
        }
        if (this.hasUsedZip64) {
            long j = this.written;
            writeOut(ZIP64_EOCD_SIG);
            writeOut(ZipEightByteInteger.getBytes(44L));
            writeOut(ZipShort.getBytes(45));
            writeOut(ZipShort.getBytes(45));
            byte[] bArr = LZERO;
            writeOut(bArr);
            writeOut(bArr);
            byte[] bytes = ZipEightByteInteger.getBytes(this.entries.size());
            writeOut(bytes);
            writeOut(bytes);
            writeOut(ZipEightByteInteger.getBytes(this.cdLength));
            writeOut(ZipEightByteInteger.getBytes(this.cdOffset));
            writeOut(ZIP64_EOCD_LOC_SIG);
            writeOut(bArr);
            writeOut(ZipEightByteInteger.getBytes(j));
            writeOut(ONE);
        }
    }

    protected final void writeOut(byte[] bArr) throws IOException {
        writeOut(bArr, 0, bArr.length);
    }

    protected final void writeOut(byte[] bArr, int i, int i2) throws IOException {
        RandomAccessFile randomAccessFile = this.raf;
        if (randomAccessFile != null) {
            randomAccessFile.write(bArr, i, i2);
        } else {
            this.out.write(bArr, i, i2);
        }
    }

    protected static long adjustToLong(int i) {
        return ZipUtil.adjustToLong(i);
    }

    private void deflateUntilInputIsNeeded() throws IOException {
        while (!this.def.needsInput()) {
            deflate();
        }
    }

    private void writeVersionNeededToExtractAndGeneralPurposeBits(int i, boolean z, boolean z2) throws IOException {
        int i2;
        GeneralPurposeBit generalPurposeBit = new GeneralPurposeBit();
        generalPurposeBit.useUTF8ForNames(this.useUTF8Flag || z);
        if (i == 8 && this.raf == null) {
            i2 = 20;
            generalPurposeBit.useDataDescriptor(true);
        } else {
            i2 = 10;
        }
        if (z2) {
            i2 = 45;
        }
        writeOut(ZipShort.getBytes(i2));
        writeOut(generalPurposeBit.encode());
    }

    private Zip64ExtendedInformationExtraField getZip64Extra(ZipEntry zipEntry) {
        CurrentEntry currentEntry = this.entry;
        if (currentEntry != null) {
            currentEntry.causedUseOfZip64 = !this.hasUsedZip64;
        }
        this.hasUsedZip64 = true;
        Zip64ExtendedInformationExtraField zip64ExtendedInformationExtraField = (Zip64ExtendedInformationExtraField) zipEntry.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
        if (zip64ExtendedInformationExtraField == null) {
            zip64ExtendedInformationExtraField = new Zip64ExtendedInformationExtraField();
        }
        zipEntry.addAsFirstExtraField(zip64ExtendedInformationExtraField);
        return zip64ExtendedInformationExtraField;
    }

    private boolean hasZip64Extra(ZipEntry zipEntry) {
        return zipEntry.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID) != null;
    }

    private Zip64Mode getEffectiveZip64Mode(ZipEntry zipEntry) {
        if (this.zip64Mode != Zip64Mode.AsNeeded || this.raf != null || zipEntry.getMethod() != 8 || zipEntry.getSize() != -1) {
            return this.zip64Mode;
        }
        return Zip64Mode.Never;
    }

    private ZipEncoding getEntryEncoding(ZipEntry zipEntry) {
        return (this.zipEncoding.canEncode(zipEntry.getName()) || !this.fallbackToUTF8) ? this.zipEncoding : ZipEncodingHelper.UTF8_ZIP_ENCODING;
    }

    private ByteBuffer getName(ZipEntry zipEntry) throws IOException {
        return getEntryEncoding(zipEntry).encode(zipEntry.getName());
    }

    void destroy() throws IOException {
        RandomAccessFile randomAccessFile = this.raf;
        if (randomAccessFile != null) {
            randomAccessFile.close();
        }
        if (this.out != null) {
            this.out.close();
        }
    }

    public static final class UnicodeExtraFieldPolicy {
        public static final UnicodeExtraFieldPolicy ALWAYS = new UnicodeExtraFieldPolicy("always");
        public static final UnicodeExtraFieldPolicy NEVER = new UnicodeExtraFieldPolicy("never");
        public static final UnicodeExtraFieldPolicy NOT_ENCODEABLE = new UnicodeExtraFieldPolicy("not encodeable");
        private final String name;

        private UnicodeExtraFieldPolicy(String str) {
            this.name = str;
        }

        public String toString() {
            return this.name;
        }
    }

    private static final class CurrentEntry {
        private long bytesRead;
        private boolean causedUseOfZip64;
        private long dataStart;
        private final ZipEntry entry;
        private boolean hasWritten;
        private long localDataStart;

        static /* synthetic */ long access$314(CurrentEntry currentEntry, long j) {
            long j2 = currentEntry.bytesRead + j;
            currentEntry.bytesRead = j2;
            return j2;
        }

        private CurrentEntry(ZipEntry zipEntry) {
            this.localDataStart = 0L;
            this.dataStart = 0L;
            this.bytesRead = 0L;
            this.causedUseOfZip64 = false;
            this.entry = zipEntry;
        }
    }
}
