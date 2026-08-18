package org.apache.tools.ant.taskdefs.optional.jlink;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class jlink {
    private static final int BUFFER_SIZE = 8192;
    private static final int VECTOR_INIT_SIZE = 10;
    private String outfile = null;
    private Vector mergefiles = new Vector(10);
    private Vector addfiles = new Vector(10);
    private boolean compression = false;
    byte[] buffer = new byte[8192];

    public void setOutfile(String str) {
        if (str == null) {
            return;
        }
        this.outfile = str;
    }

    public void addMergeFile(String str) {
        if (str == null) {
            return;
        }
        this.mergefiles.addElement(str);
    }

    public void addAddFile(String str) {
        if (str == null) {
            return;
        }
        this.addfiles.addElement(str);
    }

    public void addMergeFiles(String[] strArr) {
        if (strArr == null) {
            return;
        }
        for (String str : strArr) {
            addMergeFile(str);
        }
    }

    public void addAddFiles(String[] strArr) {
        if (strArr == null) {
            return;
        }
        for (String str : strArr) {
            addAddFile(str);
        }
    }

    public void setCompression(boolean z) {
        this.compression = z;
    }

    public void link() throws Exception {
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(this.outfile));
        if (this.compression) {
            zipOutputStream.setMethod(8);
            zipOutputStream.setLevel(-1);
        } else {
            zipOutputStream.setMethod(0);
        }
        Enumeration enumerationElements = this.mergefiles.elements();
        while (enumerationElements.hasMoreElements()) {
            String str = (String) enumerationElements.nextElement();
            File file = new File(str);
            if (file.getName().endsWith(".jar") || file.getName().endsWith(".zip")) {
                mergeZipJarContents(zipOutputStream, file);
            } else {
                addAddFile(str);
            }
        }
        Enumeration enumerationElements2 = this.addfiles.elements();
        while (enumerationElements2.hasMoreElements()) {
            File file2 = new File((String) enumerationElements2.nextElement());
            if (file2.isDirectory()) {
                addDirContents(zipOutputStream, file2, file2.getName() + '/', this.compression);
            } else {
                addFile(zipOutputStream, file2, "", this.compression);
            }
        }
        FileUtils.close(zipOutputStream);
    }

    public static void main(String[] strArr) {
        if (strArr.length < 2) {
            System.out.println("usage: jlink output input1 ... inputN");
            System.exit(1);
        }
        jlink jlinkVar = new jlink();
        jlinkVar.setOutfile(strArr[0]);
        for (int i = 1; i < strArr.length; i++) {
            jlinkVar.addMergeFile(strArr[i]);
        }
        try {
            jlinkVar.link();
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }
    }

    private void mergeZipJarContents(ZipOutputStream zipOutputStream, File file) throws IOException {
        if (file.exists()) {
            ZipFile zipFile = new ZipFile(file);
            Enumeration<? extends ZipEntry> enumerationEntries = zipFile.entries();
            while (enumerationEntries.hasMoreElements()) {
                ZipEntry zipEntryNextElement = enumerationEntries.nextElement();
                if (zipEntryNextElement.getName().indexOf("META-INF") < 0) {
                    try {
                        zipOutputStream.putNextEntry(processEntry(zipFile, zipEntryNextElement));
                        InputStream inputStream = zipFile.getInputStream(zipEntryNextElement);
                        int length = this.buffer.length;
                        while (true) {
                            int i = inputStream.read(this.buffer, 0, length);
                            if (i <= 0) {
                                break;
                            } else {
                                zipOutputStream.write(this.buffer, 0, i);
                            }
                        }
                        inputStream.close();
                        zipOutputStream.closeEntry();
                    } catch (ZipException e) {
                        if (e.getMessage().indexOf("duplicate") < 0) {
                            throw e;
                        }
                    }
                }
            }
            zipFile.close();
        }
    }

    private void addDirContents(ZipOutputStream zipOutputStream, File file, String str, boolean z) throws IOException {
        for (String str2 : file.list()) {
            File file2 = new File(file, str2);
            if (file2.isDirectory()) {
                addDirContents(zipOutputStream, file2, str + str2 + '/', z);
            } else {
                addFile(zipOutputStream, file2, str, z);
            }
        }
    }

    private String getEntryName(File file, String str) throws Throwable {
        FileInputStream fileInputStream;
        String name = file.getName();
        if (!name.endsWith(".class")) {
            FileInputStream fileInputStream2 = null;
            try {
                try {
                    fileInputStream = new FileInputStream(file);
                } catch (IOException unused) {
                }
            } catch (IOException unused2) {
            } catch (Throwable th) {
                th = th;
            }
            try {
                String className = ClassNameReader.getClassName(fileInputStream);
                if (className != null) {
                    String str2 = className.replace('.', '/') + ".class";
                    try {
                        fileInputStream.close();
                    } catch (IOException unused3) {
                    }
                    return str2;
                }
                fileInputStream.close();
            } catch (IOException unused4) {
                fileInputStream2 = fileInputStream;
                if (fileInputStream2 != null) {
                    fileInputStream2.close();
                }
                System.out.println("From " + file.getPath() + " and prefix " + str + ", creating entry " + str + name);
                return str + name;
            } catch (Throwable th2) {
                th = th2;
                fileInputStream2 = fileInputStream;
                if (fileInputStream2 != null) {
                    try {
                        fileInputStream2.close();
                    } catch (IOException unused5) {
                    }
                }
                throw th;
            }
        }
        System.out.println("From " + file.getPath() + " and prefix " + str + ", creating entry " + str + name);
        return str + name;
    }

    private void addFile(ZipOutputStream zipOutputStream, File file, String str, boolean z) throws IOException {
        if (file.exists()) {
            ZipEntry zipEntry = new ZipEntry(getEntryName(file, str));
            zipEntry.setTime(file.lastModified());
            zipEntry.setSize(file.length());
            if (!z) {
                zipEntry.setCrc(calcChecksum(file));
            }
            addToOutputStream(zipOutputStream, new FileInputStream(file), zipEntry);
        }
    }

    private void addToOutputStream(ZipOutputStream zipOutputStream, InputStream inputStream, ZipEntry zipEntry) throws IOException {
        try {
            zipOutputStream.putNextEntry(zipEntry);
            while (true) {
                int i = inputStream.read(this.buffer);
                if (i > 0) {
                    zipOutputStream.write(this.buffer, 0, i);
                } else {
                    zipOutputStream.closeEntry();
                    inputStream.close();
                    return;
                }
            }
        } catch (ZipException unused) {
            inputStream.close();
        }
    }

    private ZipEntry processEntry(ZipFile zipFile, ZipEntry zipEntry) {
        String name = zipEntry.getName();
        if (!zipEntry.isDirectory() && !name.endsWith(".class")) {
            try {
                InputStream inputStream = zipFile.getInputStream(zipFile.getEntry(name));
                String className = ClassNameReader.getClassName(inputStream);
                inputStream.close();
                if (className != null) {
                    name = className.replace('.', '/') + ".class";
                }
            } catch (IOException unused) {
            }
        }
        ZipEntry zipEntry2 = new ZipEntry(name);
        zipEntry2.setTime(zipEntry.getTime());
        zipEntry2.setExtra(zipEntry.getExtra());
        zipEntry2.setComment(zipEntry.getComment());
        zipEntry2.setTime(zipEntry.getTime());
        if (this.compression) {
            zipEntry2.setMethod(8);
        } else {
            zipEntry2.setMethod(0);
            zipEntry2.setCrc(zipEntry.getCrc());
            zipEntry2.setSize(zipEntry.getSize());
        }
        return zipEntry2;
    }

    private long calcChecksum(File file) throws IOException {
        return calcChecksum(new BufferedInputStream(new FileInputStream(file)));
    }

    private long calcChecksum(InputStream inputStream) throws IOException {
        CRC32 crc32 = new CRC32();
        int length = this.buffer.length;
        while (true) {
            int i = inputStream.read(this.buffer, 0, length);
            if (i > 0) {
                crc32.update(this.buffer, 0, i);
            } else {
                inputStream.close();
                return crc32.getValue();
            }
        }
    }
}
