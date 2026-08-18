package org.apache.tools.ant;

import java.io.PrintStream;

/* JADX INFO: loaded from: classes3.dex */
public interface BuildLogger extends BuildListener {
    void setEmacsMode(boolean z);

    void setErrorPrintStream(PrintStream printStream);

    void setMessageOutputLevel(int i);

    void setOutputPrintStream(PrintStream printStream);
}
