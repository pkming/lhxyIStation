package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class BuildNumber extends Task {
    private static final String DEFAULT_FILENAME = "build.number";
    private static final String DEFAULT_PROPERTY_NAME = "build.number";
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private File myFile;

    public void setFile(File file) {
        this.myFile = file;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws Throwable {
        FileOutputStream fileOutputStream;
        File file = this.myFile;
        validate();
        Properties propertiesLoadProperties = loadProperties();
        int buildNumber = getBuildNumber(propertiesLoadProperties);
        propertiesLoadProperties.put("build.number", String.valueOf(buildNumber + 1));
        FileOutputStream fileOutputStream2 = null;
        try {
            try {
                fileOutputStream = new FileOutputStream(this.myFile);
            } catch (IOException e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            propertiesLoadProperties.store(fileOutputStream, "Build Number for ANT. Do not edit!");
            try {
                fileOutputStream.close();
            } catch (IOException e2) {
                log("error closing output stream " + e2, 0);
            }
            this.myFile = file;
            getProject().setNewProperty("build.number", String.valueOf(buildNumber));
        } catch (IOException e3) {
            e = e3;
            fileOutputStream2 = fileOutputStream;
            throw new BuildException("Error while writing " + this.myFile, e);
        } catch (Throwable th2) {
            th = th2;
            fileOutputStream2 = fileOutputStream;
            if (fileOutputStream2 != null) {
                try {
                    fileOutputStream2.close();
                } catch (IOException e4) {
                    log("error closing output stream " + e4, 0);
                }
            }
            this.myFile = file;
            throw th;
        }
    }

    private int getBuildNumber(Properties properties) throws BuildException {
        String strTrim = properties.getProperty("build.number", "0").trim();
        try {
            return Integer.parseInt(strTrim);
        } catch (NumberFormatException e) {
            throw new BuildException(this.myFile + " contains a non integer build number: " + strTrim, e);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x003f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.util.Properties loadProperties() throws java.lang.Throwable {
        /*
            r6 = this;
            java.lang.String r0 = "error closing input stream "
            r1 = 0
            r2 = 0
            java.util.Properties r3 = new java.util.Properties     // Catch: java.lang.Throwable -> L2f java.io.IOException -> L33
            r3.<init>()     // Catch: java.lang.Throwable -> L2f java.io.IOException -> L33
            java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L2f java.io.IOException -> L33
            java.io.File r5 = r6.myFile     // Catch: java.lang.Throwable -> L2f java.io.IOException -> L33
            r4.<init>(r5)     // Catch: java.lang.Throwable -> L2f java.io.IOException -> L33
            r3.load(r4)     // Catch: java.io.IOException -> L2d java.lang.Throwable -> L3c
            r4.close()     // Catch: java.io.IOException -> L17
            goto L2c
        L17:
            r2 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.StringBuilder r0 = r4.append(r0)
            java.lang.StringBuilder r0 = r0.append(r2)
            java.lang.String r0 = r0.toString()
            r6.log(r0, r1)
        L2c:
            return r3
        L2d:
            r2 = move-exception
            goto L36
        L2f:
            r3 = move-exception
            r4 = r2
            r2 = r3
            goto L3d
        L33:
            r3 = move-exception
            r4 = r2
            r2 = r3
        L36:
            org.apache.tools.ant.BuildException r3 = new org.apache.tools.ant.BuildException     // Catch: java.lang.Throwable -> L3c
            r3.<init>(r2)     // Catch: java.lang.Throwable -> L3c
            throw r3     // Catch: java.lang.Throwable -> L3c
        L3c:
            r2 = move-exception
        L3d:
            if (r4 == 0) goto L58
            r4.close()     // Catch: java.io.IOException -> L43
            goto L58
        L43:
            r3 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.StringBuilder r0 = r4.append(r0)
            java.lang.StringBuilder r0 = r0.append(r3)
            java.lang.String r0 = r0.toString()
            r6.log(r0, r1)
        L58:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.taskdefs.BuildNumber.loadProperties():java.util.Properties");
    }

    private void validate() throws BuildException {
        if (this.myFile == null) {
            this.myFile = FILE_UTILS.resolveFile(getProject().getBaseDir(), "build.number");
        }
        if (!this.myFile.exists()) {
            try {
                FILE_UTILS.createNewFile(this.myFile);
            } catch (IOException e) {
                throw new BuildException(this.myFile + " doesn't exist and new file can't be created.", e);
            }
        }
        if (!this.myFile.canRead()) {
            throw new BuildException("Unable to read from " + this.myFile + ".");
        }
        if (!this.myFile.canWrite()) {
            throw new BuildException("Unable to write to " + this.myFile + ".");
        }
    }
}
