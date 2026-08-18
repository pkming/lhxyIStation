package de.innosystec.unrar.unpack.ppm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.optional.SchemaValidate;

/* JADX INFO: loaded from: classes2.dex */
public class AnalyzeHeapDump {
    public static void main(String[] strArr) throws Throwable {
        BufferedInputStream bufferedInputStream;
        File file = new File("P:\\test\\heapdumpc");
        File file2 = new File("P:\\test\\heapdumpj");
        if (!file.exists()) {
            System.err.println(SchemaValidate.SchemaLocation.ERROR_NO_FILE + file.getAbsolutePath());
            return;
        }
        if (!file2.exists()) {
            System.err.println(SchemaValidate.SchemaLocation.ERROR_NO_FILE + file2.getAbsolutePath());
            return;
        }
        long length = file.length();
        long length2 = file2.length();
        if (length != length2) {
            System.out.println("File size mismatch");
            System.out.println("clen = " + length);
            System.out.println("jlen = " + length2);
        }
        long jMin = Math.min(length, length2);
        BufferedInputStream bufferedInputStream2 = null;
        try {
            try {
                BufferedInputStream bufferedInputStream3 = new BufferedInputStream(new FileInputStream(file), 262144);
                try {
                    bufferedInputStream = new BufferedInputStream(new FileInputStream(file2), 262144);
                    long j = 0;
                    boolean z = false;
                    long j2 = 0;
                    boolean z2 = true;
                    while (j < jMin) {
                        try {
                            if (bufferedInputStream3.read() != bufferedInputStream.read()) {
                                if (z2) {
                                    z2 = false;
                                    j2 = j;
                                    z = true;
                                }
                            } else if (!z2) {
                                printMismatch(j2, j);
                                z2 = true;
                            }
                            j++;
                        } catch (IOException e) {
                            e = e;
                            bufferedInputStream2 = bufferedInputStream3;
                            try {
                                e.printStackTrace();
                                bufferedInputStream2.close();
                                bufferedInputStream.close();
                            } catch (Throwable th) {
                                th = th;
                                try {
                                    bufferedInputStream2.close();
                                    bufferedInputStream.close();
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                }
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            bufferedInputStream2 = bufferedInputStream3;
                            bufferedInputStream2.close();
                            bufferedInputStream.close();
                            throw th;
                        }
                    }
                    if (!z2) {
                        printMismatch(j2, j);
                    }
                    if (!z) {
                        System.out.println("Files are identical");
                    }
                    System.out.println("Done");
                    bufferedInputStream3.close();
                    bufferedInputStream.close();
                } catch (IOException e3) {
                    e = e3;
                    bufferedInputStream = null;
                } catch (Throwable th3) {
                    th = th3;
                    bufferedInputStream = null;
                }
            } catch (IOException e4) {
                e4.printStackTrace();
            }
        } catch (IOException e5) {
            e = e5;
            bufferedInputStream = null;
        } catch (Throwable th4) {
            th = th4;
            bufferedInputStream = null;
        }
    }

    private static void printMismatch(long j, long j2) {
        System.out.println("Mismatch: off=" + j + "(0x" + Long.toHexString(j) + "), len=" + (j2 - j));
    }
}
