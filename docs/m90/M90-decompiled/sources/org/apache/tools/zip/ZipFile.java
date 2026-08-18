package org.apache.tools.zip;

import android.app.backup.FullBackup;
import android.widget.ExpandableListView;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;

/* JADX INFO: loaded from: classes3.dex */
public class ZipFile {
    static final int BYTE_SHIFT = 8;
    private static final int CFD_LOCATOR_OFFSET = 16;
    private static final int CFH_LEN = 42;
    private static final long CFH_SIG = ZipLong.getValue(ZipOutputStream.CFH_SIG);
    private static final int HASH_SIZE = 509;
    private static final long LFH_OFFSET_FOR_FILENAME_LENGTH = 26;
    private static final int MAX_EOCD_SIZE = 65557;
    private static final int MIN_EOCD_SIZE = 22;
    static final int NIBLET_MASK = 15;
    private static final int POS_0 = 0;
    private static final int POS_1 = 1;
    private static final int POS_2 = 2;
    private static final int POS_3 = 3;
    private static final int ZIP64_EOCDL_LENGTH = 20;
    private static final int ZIP64_EOCDL_LOCATOR_OFFSET = 8;
    private static final int ZIP64_EOCD_CFD_LOCATOR_OFFSET = 48;
    private final byte[] CFH_BUF;
    private final byte[] DWORD_BUF;
    private final Comparator<ZipEntry> OFFSET_COMPARATOR;
    private final byte[] SHORT_BUF;
    private final byte[] WORD_BUF;
    private final RandomAccessFile archive;
    private final String archiveName;
    private boolean closed;
    private final String encoding;
    private final List<ZipEntry> entries;
    private final Map<String, LinkedList<ZipEntry>> nameMap;
    private final boolean useUnicodeExtraFields;
    private final ZipEncoding zipEncoding;

    private static final class OffsetEntry {
        private long dataOffset;
        private long headerOffset;

        private OffsetEntry() {
            this.headerOffset = -1L;
            this.dataOffset = -1L;
        }
    }

    public ZipFile(File file) throws IOException {
        this(file, (String) null);
    }

    public ZipFile(String str) throws IOException {
        this(new File(str), (String) null);
    }

    public ZipFile(String str, String str2) throws IOException {
        this(new File(str), str2, true);
    }

    public ZipFile(File file, String str) throws IOException {
        this(file, str, true);
    }

    public ZipFile(File file, String str, boolean z) throws IOException {
        this.entries = new LinkedList();
        this.nameMap = new HashMap(HASH_SIZE);
        this.DWORD_BUF = new byte[8];
        this.WORD_BUF = new byte[4];
        this.CFH_BUF = new byte[42];
        this.SHORT_BUF = new byte[2];
        this.OFFSET_COMPARATOR = new Comparator<ZipEntry>() { // from class: org.apache.tools.zip.ZipFile.2
            @Override // java.util.Comparator
            public int compare(ZipEntry zipEntry, ZipEntry zipEntry2) {
                if (zipEntry == zipEntry2) {
                    return 0;
                }
                Entry entry = zipEntry instanceof Entry ? (Entry) zipEntry : null;
                Entry entry2 = zipEntry2 instanceof Entry ? (Entry) zipEntry2 : null;
                if (entry == null) {
                    return 1;
                }
                if (entry2 == null) {
                    return -1;
                }
                long j = entry.getOffsetEntry().headerOffset - entry2.getOffsetEntry().headerOffset;
                if (j == 0) {
                    return 0;
                }
                return j < 0 ? -1 : 1;
            }
        };
        this.archiveName = file.getAbsolutePath();
        this.encoding = str;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(str);
        this.useUnicodeExtraFields = z;
        this.archive = new RandomAccessFile(file, FullBackup.ROOT_TREE_TOKEN);
        try {
            resolveLocalFileHeaderData(populateFromCentralDirectory());
        } catch (Throwable th) {
            try {
                this.closed = true;
                this.archive.close();
            } catch (IOException unused) {
            }
            throw th;
        }
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void close() throws IOException {
        this.closed = true;
        this.archive.close();
    }

    public static void closeQuietly(ZipFile zipFile) {
        if (zipFile != null) {
            try {
                zipFile.close();
            } catch (IOException unused) {
            }
        }
    }

    public Enumeration<ZipEntry> getEntries() {
        return Collections.enumeration(this.entries);
    }

    public Enumeration<ZipEntry> getEntriesInPhysicalOrder() {
        ZipEntry[] zipEntryArr = (ZipEntry[]) this.entries.toArray(new ZipEntry[0]);
        Arrays.sort(zipEntryArr, this.OFFSET_COMPARATOR);
        return Collections.enumeration(Arrays.asList(zipEntryArr));
    }

    public ZipEntry getEntry(String str) {
        LinkedList<ZipEntry> linkedList = this.nameMap.get(str);
        if (linkedList != null) {
            return linkedList.getFirst();
        }
        return null;
    }

    public Iterable<ZipEntry> getEntries(String str) {
        LinkedList<ZipEntry> linkedList = this.nameMap.get(str);
        return linkedList != null ? linkedList : Collections.emptyList();
    }

    public Iterable<ZipEntry> getEntriesInPhysicalOrder(String str) {
        ZipEntry[] zipEntryArr = new ZipEntry[0];
        if (this.nameMap.containsKey(str)) {
            zipEntryArr = (ZipEntry[]) this.nameMap.get(str).toArray(zipEntryArr);
            Arrays.sort(zipEntryArr, this.OFFSET_COMPARATOR);
        }
        return Arrays.asList(zipEntryArr);
    }

    public boolean canReadEntryData(ZipEntry zipEntry) {
        return ZipUtil.canHandleEntryData(zipEntry);
    }

    public InputStream getInputStream(ZipEntry zipEntry) throws IOException {
        if (!(zipEntry instanceof Entry)) {
            return null;
        }
        OffsetEntry offsetEntry = ((Entry) zipEntry).getOffsetEntry();
        ZipUtil.checkRequestedFeatures(zipEntry);
        BoundedInputStream boundedInputStream = new BoundedInputStream(offsetEntry.dataOffset, zipEntry.getCompressedSize());
        int method = zipEntry.getMethod();
        if (method == 0) {
            return boundedInputStream;
        }
        if (method == 8) {
            boundedInputStream.addDummy();
            final Inflater inflater = new Inflater(true);
            return new InflaterInputStream(boundedInputStream, inflater) { // from class: org.apache.tools.zip.ZipFile.1
                @Override // java.util.zip.InflaterInputStream, java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
                public void close() throws IOException {
                    super.close();
                    inflater.end();
                }
            };
        }
        throw new ZipException("Found unsupported compression method " + zipEntry.getMethod());
    }

    protected void finalize() throws Throwable {
        try {
            if (!this.closed) {
                System.err.println("Cleaning up unclosed ZipFile for archive " + this.archiveName);
                close();
            }
        } finally {
            super.finalize();
        }
    }

    private Map<ZipEntry, NameAndComment> populateFromCentralDirectory() throws IOException {
        HashMap map = new HashMap();
        positionAtCentralDirectory();
        this.archive.readFully(this.WORD_BUF);
        long value = ZipLong.getValue(this.WORD_BUF);
        if (value != CFH_SIG && startsWithLocalFileHeader()) {
            throw new IOException("central directory is empty, can't expand corrupt archive.");
        }
        while (value == CFH_SIG) {
            readCentralDirectoryEntry(map);
            this.archive.readFully(this.WORD_BUF);
            value = ZipLong.getValue(this.WORD_BUF);
        }
        return map;
    }

    private void readCentralDirectoryEntry(Map<ZipEntry, NameAndComment> map) throws IOException {
        this.archive.readFully(this.CFH_BUF);
        OffsetEntry offsetEntry = new OffsetEntry();
        Entry entry = new Entry(offsetEntry);
        entry.setPlatform((ZipShort.getValue(this.CFH_BUF, 0) >> 8) & 15);
        GeneralPurposeBit generalPurposeBit = GeneralPurposeBit.parse(this.CFH_BUF, 4);
        boolean zUsesUTF8ForNames = generalPurposeBit.usesUTF8ForNames();
        ZipEncoding zipEncoding = zUsesUTF8ForNames ? ZipEncodingHelper.UTF8_ZIP_ENCODING : this.zipEncoding;
        entry.setGeneralPurposeBit(generalPurposeBit);
        entry.setMethod(ZipShort.getValue(this.CFH_BUF, 6));
        entry.setTime(ZipUtil.dosToJavaTime(ZipLong.getValue(this.CFH_BUF, 8)));
        entry.setCrc(ZipLong.getValue(this.CFH_BUF, 12));
        entry.setCompressedSize(ZipLong.getValue(this.CFH_BUF, 16));
        entry.setSize(ZipLong.getValue(this.CFH_BUF, 20));
        int value = ZipShort.getValue(this.CFH_BUF, 24);
        int value2 = ZipShort.getValue(this.CFH_BUF, 26);
        int value3 = ZipShort.getValue(this.CFH_BUF, 28);
        int value4 = ZipShort.getValue(this.CFH_BUF, 30);
        entry.setInternalAttributes(ZipShort.getValue(this.CFH_BUF, 32));
        entry.setExternalAttributes(ZipLong.getValue(this.CFH_BUF, 34));
        byte[] bArr = new byte[value];
        this.archive.readFully(bArr);
        entry.setName(zipEncoding.decode(bArr), bArr);
        offsetEntry.headerOffset = ZipLong.getValue(this.CFH_BUF, 38);
        this.entries.add(entry);
        byte[] bArr2 = new byte[value2];
        this.archive.readFully(bArr2);
        entry.setCentralDirectoryExtra(bArr2);
        setSizesAndOffsetFromZip64Extra(entry, offsetEntry, value4);
        byte[] bArr3 = new byte[value3];
        this.archive.readFully(bArr3);
        entry.setComment(zipEncoding.decode(bArr3));
        if (zUsesUTF8ForNames || !this.useUnicodeExtraFields) {
            return;
        }
        map.put(entry, new NameAndComment(bArr, bArr3));
    }

    private void setSizesAndOffsetFromZip64Extra(ZipEntry zipEntry, OffsetEntry offsetEntry, int i) throws IOException {
        Zip64ExtendedInformationExtraField zip64ExtendedInformationExtraField = (Zip64ExtendedInformationExtraField) zipEntry.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
        if (zip64ExtendedInformationExtraField != null) {
            boolean z = zipEntry.getSize() == ExpandableListView.PACKED_POSITION_VALUE_NULL;
            boolean z2 = zipEntry.getCompressedSize() == ExpandableListView.PACKED_POSITION_VALUE_NULL;
            boolean z3 = offsetEntry.headerOffset == ExpandableListView.PACKED_POSITION_VALUE_NULL;
            zip64ExtendedInformationExtraField.reparseCentralDirectoryData(z, z2, z3, i == 65535);
            if (z) {
                zipEntry.setSize(zip64ExtendedInformationExtraField.getSize().getLongValue());
            } else if (z2) {
                zip64ExtendedInformationExtraField.setSize(new ZipEightByteInteger(zipEntry.getSize()));
            }
            if (z2) {
                zipEntry.setCompressedSize(zip64ExtendedInformationExtraField.getCompressedSize().getLongValue());
            } else if (z) {
                zip64ExtendedInformationExtraField.setCompressedSize(new ZipEightByteInteger(zipEntry.getCompressedSize()));
            }
            if (z3) {
                offsetEntry.headerOffset = zip64ExtendedInformationExtraField.getRelativeHeaderOffset().getLongValue();
            }
        }
    }

    private void positionAtCentralDirectory() throws IOException {
        positionAtEndOfCentralDirectoryRecord();
        boolean zEquals = false;
        boolean z = this.archive.getFilePointer() > 20;
        if (z) {
            RandomAccessFile randomAccessFile = this.archive;
            randomAccessFile.seek(randomAccessFile.getFilePointer() - 20);
            this.archive.readFully(this.WORD_BUF);
            zEquals = Arrays.equals(ZipOutputStream.ZIP64_EOCD_LOC_SIG, this.WORD_BUF);
        }
        if (!zEquals) {
            if (z) {
                skipBytes(16);
            }
            positionAtCentralDirectory32();
            return;
        }
        positionAtCentralDirectory64();
    }

    private void positionAtCentralDirectory64() throws IOException {
        skipBytes(4);
        this.archive.readFully(this.DWORD_BUF);
        this.archive.seek(ZipEightByteInteger.getLongValue(this.DWORD_BUF));
        this.archive.readFully(this.WORD_BUF);
        if (!Arrays.equals(this.WORD_BUF, ZipOutputStream.ZIP64_EOCD_SIG)) {
            throw new ZipException("archive's ZIP64 end of central directory locator is corrupt.");
        }
        skipBytes(44);
        this.archive.readFully(this.DWORD_BUF);
        this.archive.seek(ZipEightByteInteger.getLongValue(this.DWORD_BUF));
    }

    private void positionAtCentralDirectory32() throws IOException {
        skipBytes(16);
        this.archive.readFully(this.WORD_BUF);
        this.archive.seek(ZipLong.getValue(this.WORD_BUF));
    }

    private void positionAtEndOfCentralDirectoryRecord() throws IOException {
        if (!tryToLocateSignature(22L, 65557L, ZipOutputStream.EOCD_SIG)) {
            throw new ZipException("archive is not a ZIP archive");
        }
    }

    private boolean tryToLocateSignature(long j, long j2, byte[] bArr) throws IOException {
        long length = this.archive.length() - j;
        long jMax = Math.max(0L, this.archive.length() - j2);
        boolean z = true;
        if (length >= 0) {
            while (length >= jMax) {
                this.archive.seek(length);
                int i = this.archive.read();
                if (i != -1) {
                    if (i == bArr[0] && this.archive.read() == bArr[1] && this.archive.read() == bArr[2] && this.archive.read() == bArr[3]) {
                        break;
                    }
                    length--;
                } else {
                    break;
                }
            }
            z = false;
        } else {
            z = false;
        }
        if (z) {
            this.archive.seek(length);
        }
        return z;
    }

    private void skipBytes(int i) throws IOException {
        int i2 = 0;
        while (i2 < i) {
            int iSkipBytes = this.archive.skipBytes(i - i2);
            if (iSkipBytes <= 0) {
                throw new EOFException();
            }
            i2 += iSkipBytes;
        }
    }

    private void resolveLocalFileHeaderData(Map<ZipEntry, NameAndComment> map) throws IOException {
        Iterator<ZipEntry> it = this.entries.iterator();
        while (it.hasNext()) {
            Entry entry = (Entry) it.next();
            OffsetEntry offsetEntry = entry.getOffsetEntry();
            long j = offsetEntry.headerOffset;
            RandomAccessFile randomAccessFile = this.archive;
            long j2 = j + LFH_OFFSET_FOR_FILENAME_LENGTH;
            randomAccessFile.seek(j2);
            this.archive.readFully(this.SHORT_BUF);
            int value = ZipShort.getValue(this.SHORT_BUF);
            this.archive.readFully(this.SHORT_BUF);
            int value2 = ZipShort.getValue(this.SHORT_BUF);
            int i = value;
            while (i > 0) {
                int iSkipBytes = this.archive.skipBytes(i);
                if (iSkipBytes <= 0) {
                    throw new IOException("failed to skip file name in local file header");
                }
                i -= iSkipBytes;
            }
            byte[] bArr = new byte[value2];
            this.archive.readFully(bArr);
            entry.setExtra(bArr);
            offsetEntry.dataOffset = j2 + 2 + 2 + ((long) value) + ((long) value2);
            if (map.containsKey(entry)) {
                NameAndComment nameAndComment = map.get(entry);
                ZipUtil.setNameAndCommentFromExtraFields(entry, nameAndComment.name, nameAndComment.comment);
            }
            String name = entry.getName();
            LinkedList<ZipEntry> linkedList = this.nameMap.get(name);
            if (linkedList == null) {
                linkedList = new LinkedList<>();
                this.nameMap.put(name, linkedList);
            }
            linkedList.addLast(entry);
        }
    }

    private boolean startsWithLocalFileHeader() throws IOException {
        this.archive.seek(0L);
        this.archive.readFully(this.WORD_BUF);
        return Arrays.equals(this.WORD_BUF, ZipOutputStream.LFH_SIG);
    }

    private class BoundedInputStream extends InputStream {
        private boolean addDummyByte = false;
        private long loc;
        private long remaining;

        BoundedInputStream(long j, long j2) {
            this.remaining = j2;
            this.loc = j;
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            int i;
            long j = this.remaining;
            this.remaining = j - 1;
            if (j > 0) {
                synchronized (ZipFile.this.archive) {
                    RandomAccessFile randomAccessFile = ZipFile.this.archive;
                    long j2 = this.loc;
                    this.loc = 1 + j2;
                    randomAccessFile.seek(j2);
                    i = ZipFile.this.archive.read();
                }
                return i;
            }
            if (!this.addDummyByte) {
                return -1;
            }
            this.addDummyByte = false;
            return 0;
        }

        @Override // java.io.InputStream
        public int read(byte[] bArr, int i, int i2) throws IOException {
            int i3;
            long j = this.remaining;
            if (j <= 0) {
                if (!this.addDummyByte) {
                    return -1;
                }
                this.addDummyByte = false;
                bArr[i] = 0;
                return 1;
            }
            if (i2 <= 0) {
                return 0;
            }
            if (i2 > j) {
                i2 = (int) j;
            }
            synchronized (ZipFile.this.archive) {
                ZipFile.this.archive.seek(this.loc);
                i3 = ZipFile.this.archive.read(bArr, i, i2);
            }
            if (i3 > 0) {
                long j2 = i3;
                this.loc += j2;
                this.remaining -= j2;
            }
            return i3;
        }

        void addDummy() {
            this.addDummyByte = true;
        }
    }

    private static final class NameAndComment {
        private final byte[] comment;
        private final byte[] name;

        private NameAndComment(byte[] bArr, byte[] bArr2) {
            this.name = bArr;
            this.comment = bArr2;
        }
    }

    private static class Entry extends ZipEntry {
        private final OffsetEntry offsetEntry;

        Entry(OffsetEntry offsetEntry) {
            this.offsetEntry = offsetEntry;
        }

        OffsetEntry getOffsetEntry() {
            return this.offsetEntry;
        }

        @Override // org.apache.tools.zip.ZipEntry, java.util.zip.ZipEntry
        public int hashCode() {
            return (super.hashCode() * 3) + ((int) (this.offsetEntry.headerOffset % 2147483647L));
        }

        @Override // org.apache.tools.zip.ZipEntry
        public boolean equals(Object obj) {
            if (!super.equals(obj)) {
                return false;
            }
            Entry entry = (Entry) obj;
            return this.offsetEntry.headerOffset == entry.offsetEntry.headerOffset && this.offsetEntry.dataOffset == entry.offsetEntry.dataOffset;
        }
    }
}
