package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.util.StringUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Retry extends Task implements TaskContainer {
    private Task nestedTask;
    private int retryCount = 1;
    private int retryDelay = 0;

    @Override // org.apache.tools.ant.TaskContainer
    public synchronized void addTask(Task task) {
        if (this.nestedTask != null) {
            throw new BuildException("The retry task container accepts a single nested task (which may be a sequential task container)");
        }
        this.nestedTask = task;
    }

    public void setRetryCount(int i) {
        this.retryCount = i;
    }

    public void setRetryDelay(int i) {
        if (i < 0) {
            throw new BuildException("retryDelay must be a non-negative number");
        }
        this.retryDelay = i;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws Throwable {
        String str;
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i <= this.retryCount; i++) {
            try {
                this.nestedTask.perform();
                return;
            } catch (Exception e) {
                stringBuffer.append(e.getMessage());
                if (i >= this.retryCount) {
                    StringBuffer stringBuffer2 = new StringBuffer();
                    stringBuffer2.append("Task [").append(this.nestedTask.getTaskName());
                    stringBuffer2.append("] failed after [").append(this.retryCount);
                    stringBuffer2.append("] attempts; giving up.").append(StringUtils.LINE_SEP);
                    stringBuffer2.append("Error messages:").append(StringUtils.LINE_SEP);
                    stringBuffer2.append(stringBuffer);
                    throw new BuildException(stringBuffer2.toString(), getLocation());
                }
                if (this.retryDelay > 0) {
                    str = "Attempt [" + i + "]: error occurred; retrying after " + this.retryDelay + " ms...";
                } else {
                    str = "Attempt [" + i + "]: error occurred; retrying...";
                }
                log(str, e, 2);
                stringBuffer.append(StringUtils.LINE_SEP);
                int i2 = this.retryDelay;
                if (i2 > 0) {
                    try {
                        Thread.sleep(i2);
                    } catch (InterruptedException unused) {
                    }
                }
            }
        }
    }
}
