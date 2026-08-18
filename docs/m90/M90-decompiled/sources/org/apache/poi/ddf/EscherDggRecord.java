package org.apache.poi.ddf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import org.apache.poi.hssf.record.RecordFormatException;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class EscherDggRecord extends EscherRecord {
    public static final String RECORD_DESCRIPTION = "MsofbtDgg";
    public static final short RECORD_ID = -4090;
    private int field_1_shapeIdMax;
    private int field_3_numShapesSaved;
    private int field_4_drawingsSaved;
    private FileIdCluster[] field_5_fileIdClusters;

    @Override // org.apache.poi.ddf.EscherRecord
    public short getRecordId() {
        return RECORD_ID;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public String getRecordName() {
        return "Dgg";
    }

    public static class FileIdCluster {
        private int field_1_drawingGroupId;
        private int field_2_numShapeIdsUsed;

        public FileIdCluster(int i, int i2) {
            this.field_1_drawingGroupId = i;
            this.field_2_numShapeIdsUsed = i2;
        }

        public int getDrawingGroupId() {
            return this.field_1_drawingGroupId;
        }

        public int getNumShapeIdsUsed() {
            return this.field_2_numShapeIdsUsed;
        }

        public void incrementShapeId() {
            this.field_2_numShapeIdsUsed++;
        }
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int fillFields(byte[] bArr, int i, EscherRecordFactory escherRecordFactory) {
        int header = readHeader(bArr, i);
        int i2 = i + 8;
        this.field_1_shapeIdMax = LittleEndian.getInt(bArr, i2 + 0);
        int i3 = LittleEndian.getInt(bArr, i2 + 4);
        this.field_3_numShapesSaved = LittleEndian.getInt(bArr, i2 + 8);
        this.field_4_drawingsSaved = LittleEndian.getInt(bArr, i2 + 12);
        int i4 = i3 - 1;
        this.field_5_fileIdClusters = new FileIdCluster[i4];
        int i5 = 16;
        for (int i6 = 0; i6 < i4; i6++) {
            int i7 = i2 + i5;
            this.field_5_fileIdClusters[i6] = new FileIdCluster(LittleEndian.getInt(bArr, i7), LittleEndian.getInt(bArr, i7 + 4));
            i5 += 8;
        }
        int i8 = header - i5;
        if (i8 == 0) {
            return i5 + 8 + i8;
        }
        throw new RecordFormatException(new StringBuffer().append("Expecting no remaining data but got ").append(i8).append(" byte(s).").toString());
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int serialize(int i, byte[] bArr, EscherSerializationListener escherSerializationListener) {
        escherSerializationListener.beforeRecordSerialize(i, getRecordId(), this);
        LittleEndian.putShort(bArr, i, getOptions());
        int i2 = i + 2;
        LittleEndian.putShort(bArr, i2, getRecordId());
        int i3 = i2 + 2;
        LittleEndian.putInt(bArr, i3, getRecordSize() - 8);
        int i4 = i3 + 4;
        LittleEndian.putInt(bArr, i4, this.field_1_shapeIdMax);
        int i5 = i4 + 4;
        LittleEndian.putInt(bArr, i5, getNumIdClusters());
        int i6 = i5 + 4;
        LittleEndian.putInt(bArr, i6, this.field_3_numShapesSaved);
        int i7 = i6 + 4;
        LittleEndian.putInt(bArr, i7, this.field_4_drawingsSaved);
        int i8 = i7 + 4;
        int i9 = 0;
        while (true) {
            FileIdCluster[] fileIdClusterArr = this.field_5_fileIdClusters;
            if (i9 < fileIdClusterArr.length) {
                LittleEndian.putInt(bArr, i8, fileIdClusterArr[i9].field_1_drawingGroupId);
                int i10 = i8 + 4;
                LittleEndian.putInt(bArr, i10, this.field_5_fileIdClusters[i9].field_2_numShapeIdsUsed);
                i8 = i10 + 4;
                i9++;
            } else {
                escherSerializationListener.afterRecordSerialize(i8, getRecordId(), getRecordSize(), this);
                return getRecordSize();
            }
        }
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int getRecordSize() {
        return (this.field_5_fileIdClusters.length * 8) + 24;
    }

    public String toString() {
        String property = System.getProperty("line.separator");
        StringBuffer stringBuffer = new StringBuffer();
        int i = 0;
        while (i < this.field_5_fileIdClusters.length) {
            int i2 = i + 1;
            stringBuffer.append("  DrawingGroupId").append(i2).append(": ");
            stringBuffer.append(this.field_5_fileIdClusters[i].field_1_drawingGroupId);
            stringBuffer.append(property);
            stringBuffer.append("  NumShapeIdsUsed").append(i2).append(": ");
            stringBuffer.append(this.field_5_fileIdClusters[i].field_2_numShapeIdsUsed);
            stringBuffer.append(property);
            i = i2;
        }
        return new StringBuffer().append(getClass().getName()).append(":").append(property).append("  RecordId: 0x").append(HexDump.toHex(RECORD_ID)).append(property).append("  Options: 0x").append(HexDump.toHex(getOptions())).append(property).append("  ShapeIdMax: ").append(this.field_1_shapeIdMax).append(property).append("  NumIdClusters: ").append(getNumIdClusters()).append(property).append("  NumShapesSaved: ").append(this.field_3_numShapesSaved).append(property).append("  DrawingsSaved: ").append(this.field_4_drawingsSaved).append(property).append("").append(stringBuffer.toString()).toString();
    }

    public int getShapeIdMax() {
        return this.field_1_shapeIdMax;
    }

    public void setShapeIdMax(int i) {
        this.field_1_shapeIdMax = i;
    }

    public int getNumIdClusters() {
        return this.field_5_fileIdClusters.length + 1;
    }

    public int getNumShapesSaved() {
        return this.field_3_numShapesSaved;
    }

    public void setNumShapesSaved(int i) {
        this.field_3_numShapesSaved = i;
    }

    public int getDrawingsSaved() {
        return this.field_4_drawingsSaved;
    }

    public void setDrawingsSaved(int i) {
        this.field_4_drawingsSaved = i;
    }

    public FileIdCluster[] getFileIdClusters() {
        return this.field_5_fileIdClusters;
    }

    public void setFileIdClusters(FileIdCluster[] fileIdClusterArr) {
        this.field_5_fileIdClusters = fileIdClusterArr;
    }

    public void addCluster(int i, int i2) {
        ArrayList arrayList = new ArrayList(Arrays.asList(this.field_5_fileIdClusters));
        arrayList.add(new FileIdCluster(i, i2));
        Collections.sort(arrayList, new Comparator() { // from class: org.apache.poi.ddf.EscherDggRecord.1
            @Override // java.util.Comparator
            public int compare(Object obj, Object obj2) {
                FileIdCluster fileIdCluster = (FileIdCluster) obj;
                FileIdCluster fileIdCluster2 = (FileIdCluster) obj2;
                if (fileIdCluster.getDrawingGroupId() == fileIdCluster2.getDrawingGroupId()) {
                    return 0;
                }
                return fileIdCluster.getDrawingGroupId() < fileIdCluster2.getDrawingGroupId() ? -1 : 1;
            }
        });
        this.field_5_fileIdClusters = (FileIdCluster[]) arrayList.toArray(new FileIdCluster[arrayList.size()]);
    }
}
