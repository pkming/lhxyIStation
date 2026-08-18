package org.apache.poi.hpsf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hpsf.wellknown.SectionIDMap;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class PropertySet {
    static final byte[] BYTE_ORDER_ASSERTION = {-2, -1};
    static final byte[] FORMAT_ASSERTION = {0, 0};
    public static final int OS_MACINTOSH = 1;
    public static final int OS_WIN16 = 0;
    public static final int OS_WIN32 = 2;
    protected int byteOrder;
    protected ClassID classID;
    protected int format;
    protected int osVersion;
    protected int sectionCount;
    protected List sections;

    public int getByteOrder() {
        return this.byteOrder;
    }

    public int getFormat() {
        return this.format;
    }

    public long getOSVersion() {
        return this.osVersion;
    }

    public ClassID getClassID() {
        return this.classID;
    }

    public long getSectionCount() {
        return this.sectionCount;
    }

    public List getSections() {
        return this.sections;
    }

    protected PropertySet() {
    }

    public PropertySet(InputStream inputStream) throws MarkUnsupportedException, NoPropertySetStreamException, IOException {
        if (isPropertySetStream(inputStream)) {
            int iAvailable = inputStream.available();
            byte[] bArr = new byte[iAvailable];
            inputStream.read(bArr, 0, iAvailable);
            init(bArr, 0, iAvailable);
            return;
        }
        throw new NoPropertySetStreamException();
    }

    public PropertySet(byte[] bArr, int i, int i2) throws NoPropertySetStreamException {
        if (isPropertySetStream(bArr, i, i2)) {
            init(bArr, i, i2);
            return;
        }
        throw new NoPropertySetStreamException();
    }

    public PropertySet(byte[] bArr) throws NoPropertySetStreamException {
        this(bArr, 0, bArr.length);
    }

    public static boolean isPropertySetStream(InputStream inputStream) throws MarkUnsupportedException, IOException {
        if (!inputStream.markSupported()) {
            throw new MarkUnsupportedException(inputStream.getClass().getName());
        }
        inputStream.mark(50);
        byte[] bArr = new byte[50];
        boolean zIsPropertySetStream = isPropertySetStream(bArr, 0, inputStream.read(bArr, 0, Math.min(50, inputStream.available())));
        inputStream.reset();
        return zIsPropertySetStream;
    }

    public static boolean isPropertySetStream(byte[] bArr, int i, int i2) {
        int uShort = LittleEndian.getUShort(bArr, i);
        int i3 = i + 2;
        byte[] bArr2 = new byte[2];
        LittleEndian.putShort(bArr2, (short) uShort);
        if (!Util.equal(bArr2, BYTE_ORDER_ASSERTION)) {
            return false;
        }
        int uShort2 = LittleEndian.getUShort(bArr, i3);
        int i4 = i3 + 2;
        byte[] bArr3 = new byte[2];
        LittleEndian.putShort(bArr3, (short) uShort2);
        if (!Util.equal(bArr3, FORMAT_ASSERTION)) {
            return false;
        }
        LittleEndian.getUInt(bArr, i4);
        int i5 = i4 + 4;
        new ClassID(bArr, i5);
        return LittleEndian.getUInt(bArr, i5 + 16) >= 1;
    }

    private void init(byte[] bArr, int i, int i2) {
        this.byteOrder = LittleEndian.getUShort(bArr, i);
        int i3 = i + 2;
        this.format = LittleEndian.getUShort(bArr, i3);
        int i4 = i3 + 2;
        this.osVersion = (int) LittleEndian.getUInt(bArr, i4);
        int i5 = i4 + 4;
        this.classID = new ClassID(bArr, i5);
        int i6 = i5 + 16;
        int i7 = LittleEndian.getInt(bArr, i6);
        this.sectionCount = i7;
        int i8 = i6 + 4;
        if (i7 <= 0) {
            throw new HPSFRuntimeException(new StringBuffer().append("Section count ").append(this.sectionCount).append(" must be greater than 0.").toString());
        }
        this.sections = new ArrayList(2);
        for (int i9 = 0; i9 < this.sectionCount; i9++) {
            Section section = new Section(bArr, i8);
            i8 += 20;
            this.sections.add(section);
        }
    }

    public boolean isSummaryInformation() {
        return Util.equal(((Section) this.sections.get(0)).getFormatID().getBytes(), SectionIDMap.SUMMARY_INFORMATION_ID);
    }

    public boolean isDocumentSummaryInformation() {
        return Util.equal(((Section) this.sections.get(0)).getFormatID().getBytes(), SectionIDMap.DOCUMENT_SUMMARY_INFORMATION_ID);
    }

    public Property[] getProperties() throws NoSingleSectionException {
        return getSingleSection().getProperties();
    }

    protected Object getProperty(int i) throws NoSingleSectionException {
        return getSingleSection().getProperty(i);
    }

    protected boolean getPropertyBooleanValue(int i) throws NoSingleSectionException {
        return getSingleSection().getPropertyBooleanValue(i);
    }

    protected int getPropertyIntValue(int i) throws NoSingleSectionException {
        return getSingleSection().getPropertyIntValue(i);
    }

    public boolean wasNull() throws NoSingleSectionException {
        return getSingleSection().wasNull();
    }

    public Section getSingleSection() {
        if (this.sectionCount != 1) {
            throw new NoSingleSectionException(new StringBuffer().append("Property set contains ").append(this.sectionCount).append(" sections.").toString());
        }
        return (Section) this.sections.get(0);
    }
}
