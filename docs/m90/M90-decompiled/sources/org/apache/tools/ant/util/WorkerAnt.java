package org.apache.tools.ant.util;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/* JADX INFO: loaded from: classes3.dex */
public class WorkerAnt extends Thread {
    public static final String ERROR_NO_TASK = "No task defined";
    private volatile BuildException buildException;
    private volatile Throwable exception;
    private volatile boolean finished;
    private Object notify;
    private Task task;

    public WorkerAnt(Task task, Object obj) {
        this.finished = false;
        this.task = task;
        this.notify = obj == null ? this : obj;
    }

    public WorkerAnt(Task task) {
        this(task, null);
    }

    public synchronized BuildException getBuildException() {
        return this.buildException;
    }

    public synchronized Throwable getException() {
        return this.exception;
    }

    public Task getTask() {
        return this.task;
    }

    public synchronized boolean isFinished() {
        return this.finished;
    }

    public void waitUntilFinished(long j) throws InterruptedException {
        synchronized (this.notify) {
            if (!this.finished) {
                this.notify.wait(j);
            }
        }
    }

    public void rethrowAnyBuildException() {
        BuildException buildException = getBuildException();
        if (buildException != null) {
            throw buildException;
        }
    }

    private synchronized void caught(Throwable th) {
        this.exception = th;
        this.buildException = th instanceof BuildException ? (BuildException) th : new BuildException(th);
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        try {
            Task task = this.task;
            if (task != null) {
                task.execute();
            }
            synchronized (this.notify) {
                this.finished = true;
                this.notify.notifyAll();
            }
        } catch (Throwable th) {
            try {
                caught(th);
                synchronized (this.notify) {
                    this.finished = true;
                    this.notify.notifyAll();
                }
            } catch (Throwable th2) {
                synchronized (this.notify) {
                    this.finished = true;
                    this.notify.notifyAll();
                    throw th2;
                }
            }
        }
    }
}
