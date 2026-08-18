package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.apache.tools.ant.Task;

/* JADX INFO: loaded from: classes3.dex */
public class JikesOutputParser implements ExecuteStreamHandler {
    protected BufferedReader br;
    protected boolean emacsMode;
    protected int errors;
    protected Task task;
    protected int warnings;
    protected boolean errorFlag = false;
    protected boolean error = false;

    @Override // org.apache.tools.ant.taskdefs.ExecuteStreamHandler
    public void setProcessErrorStream(InputStream inputStream) {
    }

    @Override // org.apache.tools.ant.taskdefs.ExecuteStreamHandler
    public void setProcessInputStream(OutputStream outputStream) {
    }

    @Override // org.apache.tools.ant.taskdefs.ExecuteStreamHandler
    public void stop() {
    }

    @Override // org.apache.tools.ant.taskdefs.ExecuteStreamHandler
    public void setProcessOutputStream(InputStream inputStream) throws IOException {
        this.br = new BufferedReader(new InputStreamReader(inputStream));
    }

    @Override // org.apache.tools.ant.taskdefs.ExecuteStreamHandler
    public void start() throws IOException {
        parseOutput(this.br);
    }

    protected JikesOutputParser(Task task, boolean z) {
        System.err.println("As of Ant 1.2 released in October 2000, the JikesOutputParser class");
        System.err.println("is considered to be dead code by the Ant developers and is unmaintained.");
        System.err.println("Don't use it!");
        this.task = task;
        this.emacsMode = z;
    }

    protected void parseOutput(BufferedReader bufferedReader) throws IOException {
        if (this.emacsMode) {
            parseEmacsOutput(bufferedReader);
        } else {
            parseStandardOutput(bufferedReader);
        }
    }

    private void parseStandardOutput(BufferedReader bufferedReader) throws IOException {
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null) {
                return;
            }
            String lowerCase = line.toLowerCase();
            if (!line.trim().equals("")) {
                if (lowerCase.indexOf("error") != -1) {
                    setError(true);
                } else if (lowerCase.indexOf("warning") != -1) {
                    setError(false);
                } else if (this.emacsMode) {
                    setError(true);
                }
                log(line);
            }
        }
    }

    private void parseEmacsOutput(BufferedReader bufferedReader) throws IOException {
        parseStandardOutput(bufferedReader);
    }

    private void setError(boolean z) {
        this.error = z;
        if (z) {
            this.errorFlag = true;
        }
    }

    private void log(String str) {
        if (!this.emacsMode) {
            this.task.log("", !this.error ? 1 : 0);
        }
        this.task.log(str, !this.error ? 1 : 0);
    }

    protected boolean getErrorFlag() {
        return this.errorFlag;
    }
}
