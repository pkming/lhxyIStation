package org.apache.tools.ant;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Date;
import org.apache.tools.ant.util.DateUtils;
import org.apache.tools.ant.util.StringUtils;

/* JADX INFO: loaded from: classes3.dex */
public class DefaultLogger implements BuildLogger {
    public static final int LEFT_COLUMN_SIZE = 12;
    protected static final String lSep = StringUtils.LINE_SEP;
    protected PrintStream err;
    protected PrintStream out;
    protected int msgOutputLevel = 0;
    private long startTime = System.currentTimeMillis();
    protected boolean emacsMode = false;

    protected String getBuildFailedMessage() {
        return "BUILD FAILED";
    }

    protected String getBuildSuccessfulMessage() {
        return "BUILD SUCCESSFUL";
    }

    protected void log(String str) {
    }

    @Override // org.apache.tools.ant.BuildListener
    public void targetFinished(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.BuildListener
    public void taskFinished(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.BuildListener
    public void taskStarted(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.BuildLogger
    public void setMessageOutputLevel(int i) {
        this.msgOutputLevel = i;
    }

    @Override // org.apache.tools.ant.BuildLogger
    public void setOutputPrintStream(PrintStream printStream) {
        this.out = new PrintStream((OutputStream) printStream, true);
    }

    @Override // org.apache.tools.ant.BuildLogger
    public void setErrorPrintStream(PrintStream printStream) {
        this.err = new PrintStream((OutputStream) printStream, true);
    }

    @Override // org.apache.tools.ant.BuildLogger
    public void setEmacsMode(boolean z) {
        this.emacsMode = z;
    }

    @Override // org.apache.tools.ant.BuildListener
    public void buildStarted(BuildEvent buildEvent) {
        this.startTime = System.currentTimeMillis();
    }

    static void throwableMessage(StringBuffer stringBuffer, Throwable th, boolean z) {
        boolean z2;
        Throwable cause;
        while (true) {
            z2 = th instanceof BuildException;
            if (z2 && (cause = th.getCause()) != null) {
                String string = th.toString();
                String string2 = cause.toString();
                if (!string.endsWith(string2)) {
                    break;
                }
                stringBuffer.append(string.substring(0, string.length() - string2.length()));
                th = cause;
            } else {
                break;
            }
        }
        if (z || !z2) {
            stringBuffer.append(StringUtils.getStackTrace(th));
        } else {
            stringBuffer.append(th).append(lSep);
        }
    }

    @Override // org.apache.tools.ant.BuildListener
    public void buildFinished(BuildEvent buildEvent) {
        Throwable exception = buildEvent.getException();
        StringBuffer stringBuffer = new StringBuffer();
        if (exception == null) {
            stringBuffer.append(StringUtils.LINE_SEP);
            stringBuffer.append(getBuildSuccessfulMessage());
        } else {
            stringBuffer.append(StringUtils.LINE_SEP);
            stringBuffer.append(getBuildFailedMessage());
            stringBuffer.append(StringUtils.LINE_SEP);
            throwableMessage(stringBuffer, exception, 3 <= this.msgOutputLevel);
        }
        stringBuffer.append(StringUtils.LINE_SEP);
        stringBuffer.append("Total time: ");
        stringBuffer.append(formatTime(System.currentTimeMillis() - this.startTime));
        String string = stringBuffer.toString();
        if (exception == null) {
            printMessage(string, this.out, 3);
        } else {
            printMessage(string, this.err, 0);
        }
        log(string);
    }

    @Override // org.apache.tools.ant.BuildListener
    public void targetStarted(BuildEvent buildEvent) {
        if (2 > this.msgOutputLevel || buildEvent.getTarget().getName().equals("")) {
            return;
        }
        String str = StringUtils.LINE_SEP + buildEvent.getTarget().getName() + ":";
        printMessage(str, this.out, buildEvent.getPriority());
        log(str);
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x006e, code lost:
    
        r1.append(r2);
     */
    @Override // org.apache.tools.ant.BuildListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void messageLogged(org.apache.tools.ant.BuildEvent r9) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 214
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.DefaultLogger.messageLogged(org.apache.tools.ant.BuildEvent):void");
    }

    protected static String formatTime(long j) {
        return DateUtils.formatElapsedTime(j);
    }

    protected void printMessage(String str, PrintStream printStream, int i) {
        printStream.println(str);
    }

    protected String getTimestamp() {
        return DateFormat.getDateTimeInstance(3, 3).format(new Date(System.currentTimeMillis()));
    }

    protected String extractProjectName(BuildEvent buildEvent) {
        Project project = buildEvent.getProject();
        if (project != null) {
            return project.getName();
        }
        return null;
    }
}
