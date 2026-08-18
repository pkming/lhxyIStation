package org.apache.tools.ant.taskdefs.email;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.UUEncoder;

/* JADX INFO: loaded from: classes3.dex */
class UUMailer extends PlainMailer {
    UUMailer() {
    }

    @Override // org.apache.tools.ant.taskdefs.email.PlainMailer
    protected void attach(File file, PrintStream printStream) throws IOException {
        if (!file.exists() || !file.canRead()) {
            throw new BuildException("File \"" + file.getName() + "\" does not exist or is not readable.");
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            new UUEncoder(file.getName()).encode(new BufferedInputStream(fileInputStream), printStream);
        } finally {
            fileInputStream.close();
        }
    }
}
