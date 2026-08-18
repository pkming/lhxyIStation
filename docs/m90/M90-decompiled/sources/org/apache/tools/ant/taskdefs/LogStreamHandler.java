package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;

/* JADX INFO: loaded from: classes3.dex */
public class LogStreamHandler extends PumpStreamHandler {
    public LogStreamHandler(Task task, int i, int i2) {
        this((ProjectComponent) task, i, i2);
    }

    public LogStreamHandler(ProjectComponent projectComponent, int i, int i2) {
        super(new LogOutputStream(projectComponent, i), new LogOutputStream(projectComponent, i2));
    }

    @Override // org.apache.tools.ant.taskdefs.PumpStreamHandler, org.apache.tools.ant.taskdefs.ExecuteStreamHandler
    public void stop() {
        super.stop();
        try {
            getErr().close();
            getOut().close();
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }
}
