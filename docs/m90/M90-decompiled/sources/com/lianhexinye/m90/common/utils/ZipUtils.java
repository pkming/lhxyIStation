package com.lianhexinye.m90.common.utils;

import android.media.MediaPlayer;
import android.util.Log;
import de.innosystec.unrar.Archive;
import de.innosystec.unrar.exception.RarException;
import de.innosystec.unrar.rarfile.FileHeader;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.tools.zip.ZipFile;

/* JADX INFO: loaded from: classes2.dex */
public class ZipUtils {
    private static final int BUFF_SIZE = 2048;
    private static final String TAG = "ZipUtils";

    public static boolean unManage(String str, String str2) throws Exception {
        if (str.toLowerCase().endsWith("zip")) {
            readByApacheZipFile(str, str2);
            return true;
        }
        if (!str.toLowerCase().endsWith("rar")) {
            return false;
        }
        unrar(new File(str), str2);
        return true;
    }

    private static void UnZipFolder(String str, String str2) throws Exception {
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(str));
        while (true) {
            ZipEntry nextEntry = zipInputStream.getNextEntry();
            if (nextEntry != null) {
                String name = nextEntry.getName();
                if (nextEntry.isDirectory()) {
                    new File(str2 + File.separator + name.substring(0, name.length() - 1)).mkdirs();
                } else {
                    Log.e(TAG, str2 + File.separator + name);
                    File file = new File(str2 + File.separator + name);
                    if (!file.exists()) {
                        Log.e(TAG, "Create the file:" + str2 + File.separator + name);
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    byte[] bArr = new byte[1024];
                    while (true) {
                        int i = zipInputStream.read(bArr);
                        if (i < 0) {
                            break;
                        }
                        fileOutputStream.write(bArr, 0, i);
                        fileOutputStream.flush();
                    }
                    fileOutputStream.close();
                }
            } else {
                zipInputStream.close();
                return;
            }
        }
    }

    private static void unrar(File file, String str) throws RarException, IOException {
        String strTrim;
        if (str == null || "".equals(str)) {
            str = file.getParentFile().getPath();
        }
        Archive archive = new Archive(file);
        for (FileHeader fileHeaderNextFileHeader = archive.nextFileHeader(); fileHeaderNextFileHeader != null; fileHeaderNextFileHeader = archive.nextFileHeader()) {
            try {
                if (fileHeaderNextFileHeader.isUnicode()) {
                    strTrim = fileHeaderNextFileHeader.getFileNameW().trim();
                } else {
                    strTrim = fileHeaderNextFileHeader.getFileNameString().trim();
                }
                File file2 = new File(str + "/" + strTrim.replaceAll("\\\\", "/"));
                System.out.println("unrar entry file :" + file2.getPath());
                if (fileHeaderNextFileHeader.isDirectory()) {
                    file2.mkdirs();
                } else {
                    File parentFile = file2.getParentFile();
                    if (parentFile != null && !parentFile.exists()) {
                        parentFile.mkdirs();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(file2);
                    archive.extractFile(fileHeaderNextFileHeader, fileOutputStream);
                    fileOutputStream.close();
                }
            } finally {
                archive.close();
            }
        }
    }

    public static boolean zipFiles(File[] fileArr, String str) throws Throwable {
        Objects.requireNonNull(fileArr, "fs == null");
        ZipOutputStream zipOutputStream = null;
        boolean z = true;
        try {
            try {
                try {
                    ZipOutputStream zipOutputStream2 = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(str)));
                    try {
                        try {
                            for (File file : fileArr) {
                                if (file != null && file.exists()) {
                                    if (file.isDirectory()) {
                                        recursionZip(zipOutputStream2, file, file.getName() + File.separator);
                                    } else {
                                        recursionZip(zipOutputStream2, file, "");
                                    }
                                }
                            }
                            try {
                                zipOutputStream2.flush();
                                zipOutputStream2.closeEntry();
                                zipOutputStream2.close();
                            } catch (Exception e) {
                                e = e;
                                zipOutputStream = zipOutputStream2;
                                e.printStackTrace();
                                Log.e(TAG, "zip file failed err: " + e.getMessage());
                                if (zipOutputStream != null) {
                                    zipOutputStream.closeEntry();
                                    zipOutputStream.close();
                                }
                                return z;
                            }
                        } catch (Throwable th) {
                            th = th;
                            zipOutputStream = zipOutputStream2;
                            if (zipOutputStream != null) {
                                try {
                                    zipOutputStream.closeEntry();
                                    zipOutputStream.close();
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                }
                            }
                            throw th;
                        }
                    } catch (Exception e3) {
                        e = e3;
                        z = false;
                    }
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            } catch (Exception e5) {
                e = e5;
                z = false;
            }
            return z;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private static void recursionZip(ZipOutputStream zipOutputStream, File file, String str) throws Exception {
        if (file.isDirectory()) {
            Log.i(TAG, "the file is dir name -->>" + file.getName() + " the baseDir-->>>" + str);
            for (File file2 : file.listFiles()) {
                if (file2 != null) {
                    if (file2.isDirectory()) {
                        str = file.getName() + File.separator + file2.getName() + File.separator;
                        Log.i(TAG, "basDir111-->>" + str);
                        recursionZip(zipOutputStream, file2, str);
                    } else {
                        Log.i(TAG, "basDir222-->>" + str);
                        recursionZip(zipOutputStream, file2, str);
                    }
                }
            }
            return;
        }
        Log.i(TAG, "the file name is -->>" + file.getName() + " the base dir -->>" + str);
        byte[] bArr = new byte[2048];
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        zipOutputStream.putNextEntry(new ZipEntry(str + file.getName()));
        while (true) {
            int i = bufferedInputStream.read(bArr);
            if (i != -1) {
                zipOutputStream.write(bArr, 0, i);
            } else {
                bufferedInputStream.close();
                return;
            }
        }
    }

    public static void ZipFolder(String str, String str2) throws Exception {
        Log.v("XZip", "ZipFolder(String, String)");
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(str2));
        File file = new File(str);
        ZipFiles(file.getParent() + File.separator, file.getName(), zipOutputStream);
        zipOutputStream.finish();
        zipOutputStream.close();
    }

    private static void ZipFiles(String str, String str2, ZipOutputStream zipOutputStream) throws Exception {
        Log.v("XZip", "ZipFiles(String, String, ZipOutputStream)");
        if (zipOutputStream == null) {
            return;
        }
        File file = new File(str + str2);
        if (file.isFile()) {
            ZipEntry zipEntry = new ZipEntry(str2);
            FileInputStream fileInputStream = new FileInputStream(file);
            zipOutputStream.putNextEntry(zipEntry);
            byte[] bArr = new byte[4096];
            while (true) {
                int i = fileInputStream.read(bArr);
                if (i != -1) {
                    zipOutputStream.write(bArr, 0, i);
                } else {
                    fileInputStream.close();
                    zipOutputStream.closeEntry();
                    return;
                }
            }
        } else {
            String[] list = file.list();
            if (list.length <= 0) {
                zipOutputStream.putNextEntry(new ZipEntry(str2 + File.separator));
                zipOutputStream.closeEntry();
            }
            for (String str3 : list) {
                ZipFiles(str, str2 + File.separator + str3, zipOutputStream);
            }
        }
    }

    public static void readByApacheZipFile(String str, String str2) throws IOException {
        ZipFile zipFile = new ZipFile(str, MediaPlayer.CHARSET_GBK);
        Enumeration<org.apache.tools.zip.ZipEntry> entries = zipFile.getEntries();
        while (entries.hasMoreElements()) {
            try {
                org.apache.tools.zip.ZipEntry zipEntryNextElement = entries.nextElement();
                String name = zipEntryNextElement.getName();
                String str3 = str2 + "/" + name;
                if (zipEntryNextElement.isDirectory()) {
                    System.out.println("正在创建解压目录 - " + name);
                    File file = new File(str3);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                } else {
                    System.out.println("正在创建解压文件 - " + name);
                    File file2 = new File(str3.substring(0, str3.lastIndexOf("/")));
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(str2 + "/" + name));
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(zipFile.getInputStream(zipEntryNextElement));
                    byte[] bArr = new byte[1024];
                    for (int i = bufferedInputStream.read(bArr); i != -1; i = bufferedInputStream.read(bArr)) {
                        bufferedOutputStream.write(bArr, 0, i);
                    }
                    bufferedInputStream.close();
                    bufferedOutputStream.close();
                }
            } finally {
                zipFile.close();
            }
        }
    }
}
