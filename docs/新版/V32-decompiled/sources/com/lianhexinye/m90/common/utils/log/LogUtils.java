package com.lianhexinye.m90.common.utils.log;

import android.content.Context;
import android.os.Process;
import com.lianhexinye.m90.common.Constants;
import com.lianhexinye.m90.common.utils.JavaUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;

/* JADX INFO: loaded from: classes2.dex */
public class LogUtils {
    private static final String LOG_FORMAT = "%s %s-%s/%s %s/%s ";
    private static Level currentLevel = null;
    private static boolean isWriter = false;
    private static File logFold = null;
    private static String pkgName = "";
    private static String logFilePath = Constants.SD_ROOT + Constants.LOG_RES_PATH;
    private static FileOutputStream fos = null;
    private static OutputStreamWriter osWriter = null;
    private static BufferedWriter writer = null;

    public static void initialize(Context context, boolean z, Level level) {
        currentLevel = level;
        if (level == Level.CLOSE) {
            isWriter = false;
            return;
        }
        isWriter = z;
        if (z) {
            pkgName = context.getPackageName();
            File file = new File(logFilePath);
            logFold = file;
            boolean zExists = file.exists();
            if (!zExists) {
                zExists = logFold.mkdirs();
            }
            if (!zExists) {
                isWriter = false;
                return;
            }
            logFilePath += "/info.log";
            try {
                File file2 = new File(logFilePath);
                boolean zExists2 = file2.exists();
                if (!zExists2) {
                    zExists2 = file2.createNewFile();
                }
                boolean z2 = z & zExists2;
                isWriter = z2;
                if (z2) {
                    fos = new FileOutputStream(file2, true);
                    osWriter = new OutputStreamWriter(fos);
                    writer = new BufferedWriter(osWriter);
                }
            } catch (IOException e) {
                e.printStackTrace();
                isWriter = false;
            }
        }
    }

    public static boolean isIsWriter() {
        return isWriter;
    }

    public static void setIsWriter(boolean z) {
        isWriter = z;
    }

    public static void i(String str, String str2) {
        if (currentLevel.value <= Level.INFO.value && isWriter) {
            write(str, str2, "I", null);
        }
    }

    public static void i(String str, String str2, Throwable th) {
        if (currentLevel.value <= Level.INFO.value && isWriter) {
            write(str, str2, "I", th);
        }
    }

    public static void v(String str, String str2) {
        if (currentLevel.value <= Level.VERBOSE.value && isWriter) {
            write(str, str2, "V", null);
        }
    }

    public static void v(String str, String str2, Throwable th) {
        if (currentLevel.value <= Level.VERBOSE.value && isWriter) {
            write(str, str2, "V", th);
        }
    }

    public static void d(String str, String str2) {
        if (currentLevel.value <= Level.DEBUG.value && isWriter) {
            write(str, str2, "D", null);
        }
    }

    public static void d(String str, String str2, Throwable th) {
        if (currentLevel.value <= Level.DEBUG.value && isWriter) {
            write(str, str2, "D", th);
        }
    }

    public static void e(String str, String str2) {
        if (currentLevel.value <= Level.ERROR.value && isWriter) {
            write(str, str2, "E", null);
        }
    }

    public static void e(String str, String str2, Throwable th) {
        if (currentLevel.value <= Level.ERROR.value && isWriter) {
            write(str, str2, "E", th);
        }
    }

    public static void w(String str, String str2) {
        if (currentLevel.value <= Level.WARN.value && isWriter) {
            write(str, str2, "W", null);
        }
    }

    public static void w(String str, String str2, Throwable th) {
        if (currentLevel.value <= Level.WARN.value && isWriter) {
            write(str, str2, "W", th);
        }
    }

    public static void i(Object obj, String str) {
        i(obj.getClass().getSimpleName(), str);
    }

    public static void i(Object obj, String str, Throwable th) {
        i(obj.getClass().getSimpleName(), str, th);
    }

    public static void v(Object obj, String str) {
        v(obj.getClass().getSimpleName(), str);
    }

    public static void v(Object obj, String str, Throwable th) {
        v(obj.getClass().getSimpleName(), str, th);
    }

    public static void d(Object obj, String str) {
        d(obj.getClass().getSimpleName(), str);
    }

    public static void d(Object obj, String str, Throwable th) {
        d(obj.getClass().getSimpleName(), str, th);
    }

    public static void e(Object obj, String str) {
        e(obj.getClass().getSimpleName(), str);
    }

    public static void e(Object obj, String str, Throwable th) {
        e(obj.getClass().getSimpleName(), str, th);
    }

    public static void w(Object obj, String str) {
        w(obj.getClass().getSimpleName(), str);
    }

    public static void w(Object obj, String str, Throwable th) {
        w(obj.getClass().getSimpleName(), str, th);
    }

    private static void write(String str, String str2, String str3, Throwable th) {
        String strDateToString = JavaUtils.dateToString(Calendar.getInstance().getTime(), new String[0]);
        try {
            File file = new File(logFilePath);
            if (!file.exists()) {
                file.createNewFile();
                if (isWriter) {
                    fos = new FileOutputStream(file, true);
                    osWriter = new OutputStreamWriter(fos);
                    writer = new BufferedWriter(osWriter);
                }
            }
            writer.write(String.format(LOG_FORMAT, strDateToString, "" + Process.myPid(), "" + Process.myPid(), pkgName, str3, str));
            writer.write(str2);
            writer.newLine();
            writer.flush();
            osWriter.flush();
            fos.flush();
            if (th != null) {
                saveCrash(th);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveCrash(Throwable th) throws IOException {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        th.printStackTrace(printWriter);
        for (Throwable cause = th.getCause(); cause != null; cause = cause.getCause()) {
            cause.printStackTrace(printWriter);
        }
        printWriter.flush();
        printWriter.close();
        stringWriter.flush();
        String string = writer.toString();
        stringWriter.close();
        writer.write(string);
        writer.newLine();
        writer.flush();
        osWriter.flush();
        fos.flush();
    }
}
