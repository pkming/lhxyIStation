package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/* JADX INFO: loaded from: classes3.dex */
public class Sleep extends Task {
    private boolean failOnError = true;
    private int seconds = 0;
    private int hours = 0;
    private int minutes = 0;
    private int milliseconds = 0;

    public void setSeconds(int i) {
        this.seconds = i;
    }

    public void setHours(int i) {
        this.hours = i;
    }

    public void setMinutes(int i) {
        this.minutes = i;
    }

    public void setMilliseconds(int i) {
        this.milliseconds = i;
    }

    public void doSleep(long j) {
        try {
            Thread.sleep(j);
        } catch (InterruptedException unused) {
        }
    }

    public void setFailOnError(boolean z) {
        this.failOnError = z;
    }

    private long getSleepTime() {
        return (((((((long) this.hours) * 60) + ((long) this.minutes)) * 60) + ((long) this.seconds)) * 1000) + ((long) this.milliseconds);
    }

    public void validate() throws BuildException {
        if (getSleepTime() < 0) {
            throw new BuildException("Negative sleep periods are not supported");
        }
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        try {
            validate();
            long sleepTime = getSleepTime();
            log("sleeping for " + sleepTime + " milliseconds", 3);
            doSleep(sleepTime);
        } catch (Exception e) {
            if (this.failOnError) {
                throw new BuildException(e);
            }
            log(e.toString(), 0);
        }
    }
}
