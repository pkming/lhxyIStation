package org.apache.tools.ant.taskdefs.condition;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.ManifestTask;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

/* JADX INFO: loaded from: classes3.dex */
public class IsSigned extends DataType implements Condition {
    private static final int SHORT_SIG_LIMIT = 8;
    private static final String SIG_END = ".SF";
    private static final String SIG_START = "META-INF/";
    private File file;
    private String name;

    public void setFile(File file) {
        this.file = file;
    }

    public void setName(String str) {
        this.name = str;
    }

    public static boolean isSigned(File file, String str) throws Throwable {
        ZipFile zipFile = null;
        try {
            ZipFile zipFile2 = new ZipFile(file);
            boolean z = true;
            try {
                if (str == null) {
                    Enumeration<ZipEntry> entries = zipFile2.getEntries();
                    while (entries.hasMoreElements()) {
                        String name = entries.nextElement().getName();
                        if (name.startsWith(SIG_START) && name.endsWith(SIG_END)) {
                            ZipFile.closeQuietly(zipFile2);
                            return true;
                        }
                    }
                    ZipFile.closeQuietly(zipFile2);
                    return false;
                }
                String strReplaceInvalidChars = replaceInvalidChars(str);
                boolean z2 = zipFile2.getEntry(new StringBuilder().append(SIG_START).append(strReplaceInvalidChars.toUpperCase()).append(SIG_END).toString()) != null;
                boolean z3 = strReplaceInvalidChars.length() > 8 && zipFile2.getEntry(new StringBuilder().append(SIG_START).append(strReplaceInvalidChars.substring(0, 8).toUpperCase()).append(SIG_END).toString()) != null;
                if (!z2 && !z3) {
                    z = false;
                }
                ZipFile.closeQuietly(zipFile2);
                return z;
            } catch (Throwable th) {
                th = th;
                zipFile = zipFile2;
                ZipFile.closeQuietly(zipFile);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() throws Throwable {
        File file = this.file;
        if (file == null) {
            throw new BuildException("The file attribute must be set.");
        }
        boolean zIsSigned = false;
        if (!file.exists()) {
            log("The file \"" + this.file.getAbsolutePath() + "\" does not exist.", 3);
            return false;
        }
        try {
            zIsSigned = isSigned(this.file, this.name);
        } catch (IOException e) {
            log("Got IOException reading file \"" + this.file.getAbsolutePath() + "\"" + e, 1);
        }
        if (zIsSigned) {
            log("File \"" + this.file.getAbsolutePath() + "\" is signed.", 3);
        }
        return zIsSigned;
    }

    private static String replaceInvalidChars(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        int length = str.length();
        boolean z = false;
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            if (ManifestTask.VALID_ATTRIBUTE_CHARS.indexOf(cCharAt) < 0) {
                stringBuffer.append("_");
                z = true;
            } else {
                stringBuffer.append(cCharAt);
            }
        }
        return z ? stringBuffer.toString() : str;
    }
}
