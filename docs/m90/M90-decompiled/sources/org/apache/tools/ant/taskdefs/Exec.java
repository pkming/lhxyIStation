package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Locale;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Os;

/* JADX INFO: loaded from: classes3.dex */
public class Exec extends Task {
    private String command;
    private File dir;
    private String os;
    private String out;
    protected PrintWriter fos = null;
    private boolean failOnError = false;

    public Exec() {
        System.err.println("As of Ant 1.2 released in October 2000, the Exec class");
        System.err.println("is considered to be dead code by the Ant developers and is unmaintained.");
        System.err.println("Don't use it!");
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        run(this.command);
    }

    protected int run(String str) throws BuildException {
        String property = System.getProperty("os.name");
        log("Myos = " + property, 3);
        String str2 = this.os;
        if (str2 != null && str2.indexOf(property) < 0) {
            log("Not found in " + this.os, 3);
            return 0;
        }
        if (this.dir == null) {
            this.dir = getProject().getBaseDir();
        }
        if (property.toLowerCase(Locale.ENGLISH).indexOf(Os.FAMILY_WINDOWS) >= 0) {
            if (!this.dir.equals(getProject().resolveFile("."))) {
                if (property.toLowerCase(Locale.ENGLISH).indexOf("nt") >= 0) {
                    str = "cmd /c cd " + this.dir + " && " + str;
                } else {
                    String property2 = getProject().getProperty(MagicNames.ANT_HOME);
                    if (property2 == null) {
                        throw new BuildException("Property 'ant.home' not found", getLocation());
                    }
                    str = getProject().resolveFile(property2 + "/bin/antRun.bat").toString() + " " + this.dir + " " + str;
                }
            }
        } else {
            String property3 = getProject().getProperty(MagicNames.ANT_HOME);
            if (property3 == null) {
                throw new BuildException("Property 'ant.home' not found", getLocation());
            }
            str = getProject().resolveFile(property3 + "/bin/antRun").toString() + " " + this.dir + " " + str;
        }
        int iExitValue = -1;
        try {
            log(str, 3);
            Process processExec = Runtime.getRuntime().exec(str);
            if (this.out != null) {
                this.fos = new PrintWriter(new FileWriter(this.out));
                log("Output redirected to " + this.out, 3);
            }
            StreamPumper streamPumper = new StreamPumper(processExec.getInputStream(), 2);
            StreamPumper streamPumper2 = new StreamPumper(processExec.getErrorStream(), 1);
            streamPumper.start();
            streamPumper2.start();
            processExec.waitFor();
            streamPumper.join();
            streamPumper2.join();
            processExec.destroy();
            logFlush();
            iExitValue = processExec.exitValue();
            if (iExitValue != 0) {
                if (this.failOnError) {
                    throw new BuildException("Exec returned: " + iExitValue, getLocation());
                }
                log("Result: " + iExitValue, 0);
            }
        } catch (IOException e) {
            throw new BuildException("Error exec: " + str, e, getLocation());
        } catch (InterruptedException unused) {
        }
        return iExitValue;
    }

    public void setDir(String str) {
        this.dir = getProject().resolveFile(str);
    }

    public void setOs(String str) {
        this.os = str;
    }

    public void setCommand(String str) {
        this.command = str;
    }

    public void setOutput(String str) {
        this.out = str;
    }

    public void setFailonerror(boolean z) {
        this.failOnError = z;
    }

    protected void outputLog(String str, int i) {
        PrintWriter printWriter = this.fos;
        if (printWriter == null) {
            log(str, i);
        } else {
            printWriter.println(str);
        }
    }

    protected void logFlush() {
        PrintWriter printWriter = this.fos;
        if (printWriter != null) {
            printWriter.close();
        }
    }

    class StreamPumper extends Thread {
        private static final int SLEEP_TIME = 5;
        private BufferedReader din;
        private boolean endOfStream = false;
        private int messageLevel;

        public StreamPumper(InputStream inputStream, int i) {
            this.din = new BufferedReader(new InputStreamReader(inputStream));
            this.messageLevel = i;
        }

        public void pumpStream() throws IOException {
            if (this.endOfStream) {
                return;
            }
            String line = this.din.readLine();
            if (line != null) {
                Exec.this.outputLog(line, this.messageLevel);
            } else {
                this.endOfStream = true;
            }
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (!this.endOfStream) {
                try {
                    try {
                        pumpStream();
                        sleep(5L);
                    } catch (IOException unused) {
                        return;
                    }
                } catch (InterruptedException unused2) {
                }
            }
            this.din.close();
        }
    }
}
