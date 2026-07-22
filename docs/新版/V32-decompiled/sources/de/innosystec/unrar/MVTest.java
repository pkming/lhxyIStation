package de.innosystec.unrar;

import de.innosystec.unrar.exception.RarException;
import de.innosystec.unrar.rarfile.FileHeader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes2.dex */
public class MVTest {
    public static void main(String[] strArr) {
        Archive archive;
        try {
            archive = new Archive(new File("/home/Avenger/testdata/test2.part01.rar"));
        } catch (RarException e) {
            e.printStackTrace();
            archive = null;
        } catch (IOException e2) {
            e2.printStackTrace();
            archive = null;
        }
        if (archive != null) {
            archive.getMainHeader().print();
            for (FileHeader fileHeaderNextFileHeader = archive.nextFileHeader(); fileHeaderNextFileHeader != null; fileHeaderNextFileHeader = archive.nextFileHeader()) {
                try {
                    File file = new File("/home/Avenger/testdata/" + fileHeaderNextFileHeader.getFileNameString().trim());
                    System.out.println(file.getAbsolutePath());
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    archive.extractFile(fileHeaderNextFileHeader, fileOutputStream);
                    fileOutputStream.close();
                } catch (RarException e3) {
                    e3.printStackTrace();
                } catch (FileNotFoundException e4) {
                    e4.printStackTrace();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
        }
    }
}
