package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.LineOrientedOutputStream;

/* JADX INFO: loaded from: classes3.dex */
public class LogOutputStream extends LineOrientedOutputStream {
    private int level;
    private ProjectComponent pc;

    public LogOutputStream(ProjectComponent projectComponent) {
        this.level = 2;
        this.pc = projectComponent;
    }

    public LogOutputStream(Task task, int i) {
        this((ProjectComponent) task, i);
    }

    public LogOutputStream(ProjectComponent projectComponent, int i) {
        this(projectComponent);
        this.level = i;
    }

    @Override // org.apache.tools.ant.util.LineOrientedOutputStream
    protected void processBuffer() {
        try {
            super.processBuffer();
        } catch (IOException e) {
            throw new RuntimeException("Impossible IOException caught: " + e);
        }
    }

    @Override // org.apache.tools.ant.util.LineOrientedOutputStream
    protected void processLine(String str) {
        processLine(str, this.level);
    }

    protected void processLine(String str, int i) {
        this.pc.log(str, i);
    }

    public int getMessageLevel() {
        return this.level;
    }
}
