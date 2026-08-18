package android.os;

import android.util.Log;
import android.util.Slog;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import libcore.io.ErrnoException;
import libcore.io.Libcore;

/* JADX INFO: loaded from: classes.dex */
public class FileUtils {
    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("[\\w%+,./=_-]+");
    public static final int S_IRGRP = 32;
    public static final int S_IROTH = 4;
    public static final int S_IRUSR = 256;
    public static final int S_IRWXG = 56;
    public static final int S_IRWXO = 7;
    public static final int S_IRWXU = 448;
    public static final int S_IWGRP = 16;
    public static final int S_IWOTH = 2;
    public static final int S_IWUSR = 128;
    public static final int S_IXGRP = 8;
    public static final int S_IXOTH = 1;
    public static final int S_IXUSR = 64;
    private static final String TAG = "FileUtils";

    public static int getFatVolumeId(String str) {
        return -1;
    }

    public static int setPermissions(File file, int i, int i2, int i3) {
        return setPermissions(file.getAbsolutePath(), i, i2, i3);
    }

    public static int setPermissions(String str, int i, int i2, int i3) {
        try {
            Libcore.os.chmod(str, i);
            if (i2 < 0 && i3 < 0) {
                return 0;
            }
            try {
                Libcore.os.chown(str, i2, i3);
                return 0;
            } catch (ErrnoException e) {
                Slog.w(TAG, "Failed to chown(" + str + "): " + e);
                return e.errno;
            }
        } catch (ErrnoException e2) {
            Slog.w(TAG, "Failed to chmod(" + str + "): " + e2);
            return e2.errno;
        }
    }

    public static int setPermissions(FileDescriptor fileDescriptor, int i, int i2, int i3) {
        try {
            Libcore.os.fchmod(fileDescriptor, i);
            if (i2 < 0 && i3 < 0) {
                return 0;
            }
            try {
                Libcore.os.fchown(fileDescriptor, i2, i3);
                return 0;
            } catch (ErrnoException e) {
                Slog.w(TAG, "Failed to fchown(): " + e);
                return e.errno;
            }
        } catch (ErrnoException e2) {
            Slog.w(TAG, "Failed to fchmod(): " + e2);
            return e2.errno;
        }
    }

    public static int getUid(String str) {
        try {
            return Libcore.os.stat(str).st_uid;
        } catch (ErrnoException unused) {
            return -1;
        }
    }

    public static boolean sync(FileOutputStream fileOutputStream) {
        if (fileOutputStream == null) {
            return true;
        }
        try {
            fileOutputStream.getFD().sync();
            return true;
        } catch (IOException unused) {
            return false;
        }
    }

    public static boolean copyFile(File file, File file2) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                boolean zCopyToFile = copyToFile(fileInputStream, file2);
                fileInputStream.close();
                return zCopyToFile;
            } catch (Throwable th) {
                fileInputStream.close();
                throw th;
            }
        } catch (IOException unused) {
            return false;
        }
    }

    /* JADX WARN: Finally extract failed */
    public static boolean copyToFile(InputStream inputStream, File file) {
        try {
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            try {
                byte[] bArr = new byte[4096];
                while (true) {
                    int i = inputStream.read(bArr);
                    if (i < 0) {
                        break;
                    }
                    fileOutputStream.write(bArr, 0, i);
                }
                fileOutputStream.flush();
                try {
                    fileOutputStream.getFD().sync();
                } catch (IOException unused) {
                }
                fileOutputStream.close();
                return true;
            } catch (Throwable th) {
                fileOutputStream.flush();
                try {
                    fileOutputStream.getFD().sync();
                } catch (IOException unused2) {
                }
                fileOutputStream.close();
                throw th;
            }
        } catch (IOException unused3) {
            return false;
        }
    }

    public static boolean isFilenameSafe(File file) {
        return SAFE_FILENAME_PATTERN.matcher(file.getPath()).matches();
    }

    public static String readTextFile(File file, int i, String str) throws IOException {
        int i2;
        boolean z;
        int i3;
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        try {
            long length = file.length();
            String str2 = "";
            if (i > 0 || (length > 0 && i == 0)) {
                if (length > 0 && (i == 0 || length < i)) {
                    i = (int) length;
                }
                byte[] bArr = new byte[i + 1];
                int i4 = bufferedInputStream.read(bArr);
                if (i4 > 0) {
                    if (i4 <= i) {
                        str2 = new String(bArr, 0, i4);
                    } else if (str == null) {
                        str2 = new String(bArr, 0, i);
                    } else {
                        str2 = new String(bArr, 0, i) + str;
                    }
                }
            } else if (i < 0) {
                byte[] bArr2 = null;
                byte[] bArr3 = null;
                boolean z2 = false;
                while (true) {
                    z = true;
                    if (bArr2 != null) {
                        z2 = true;
                    }
                    if (bArr2 == null) {
                        bArr2 = new byte[-i];
                    }
                    i3 = bufferedInputStream.read(bArr2);
                    if (i3 != bArr2.length) {
                        break;
                    }
                    byte[] bArr4 = bArr3;
                    bArr3 = bArr2;
                    bArr2 = bArr4;
                }
                if (bArr3 != null || i3 > 0) {
                    if (bArr3 == null) {
                        str2 = new String(bArr2, 0, i3);
                    } else {
                        if (i3 > 0) {
                            System.arraycopy(bArr3, i3, bArr3, 0, bArr3.length - i3);
                            System.arraycopy(bArr2, 0, bArr3, bArr3.length - i3, i3);
                        } else {
                            z = z2;
                        }
                        if (str == null || !z) {
                            str2 = new String(bArr3);
                        } else {
                            str2 = str + new String(bArr3);
                        }
                    }
                }
            } else {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] bArr5 = new byte[1024];
                do {
                    i2 = bufferedInputStream.read(bArr5);
                    if (i2 > 0) {
                        byteArrayOutputStream.write(bArr5, 0, i2);
                    }
                } while (i2 == 1024);
                str2 = byteArrayOutputStream.toString();
            }
            return str2;
        } finally {
            bufferedInputStream.close();
            fileInputStream.close();
        }
    }

    public static void stringToFile(String str, String str2) throws IOException {
        FileWriter fileWriter = new FileWriter(str);
        try {
            fileWriter.write(str2);
        } finally {
            fileWriter.close();
        }
    }

    public static long checksumCrc32(File file) throws Throwable {
        CRC32 crc32 = new CRC32();
        CheckedInputStream checkedInputStream = null;
        try {
            CheckedInputStream checkedInputStream2 = new CheckedInputStream(new FileInputStream(file), crc32);
            try {
                while (checkedInputStream2.read(new byte[128]) >= 0) {
                }
                long value = crc32.getValue();
                try {
                    checkedInputStream2.close();
                } catch (IOException unused) {
                }
                return value;
            } catch (Throwable th) {
                th = th;
                checkedInputStream = checkedInputStream2;
                if (checkedInputStream != null) {
                    try {
                        checkedInputStream.close();
                    } catch (IOException unused2) {
                    }
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static void deleteOlderFiles(File file, int i, long j) {
        if (i < 0 || j < 0) {
            throw new IllegalArgumentException("Constraints must be positive or 0");
        }
        File[] fileArrListFiles = file.listFiles();
        if (fileArrListFiles == null) {
            return;
        }
        Arrays.sort(fileArrListFiles, new Comparator<File>() { // from class: android.os.FileUtils.1
            @Override // java.util.Comparator
            public int compare(File file2, File file3) {
                return (int) (file3.lastModified() - file2.lastModified());
            }
        });
        while (i < fileArrListFiles.length) {
            File file2 = fileArrListFiles[i];
            if (System.currentTimeMillis() - file2.lastModified() > j) {
                Log.d(TAG, "Deleting old file " + file2);
                file2.delete();
            }
            i++;
        }
    }
}
