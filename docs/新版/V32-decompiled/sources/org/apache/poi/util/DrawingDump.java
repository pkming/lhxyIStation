package org.apache.poi.util;

import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/* JADX INFO: loaded from: classes3.dex */
public class DrawingDump {
    public static void main(String[] strArr) throws IOException {
        new HSSFWorkbook(new POIFSFileSystem(new FileInputStream(strArr[0]))).getSheetAt(0).dumpDrawingRecords();
    }
}
