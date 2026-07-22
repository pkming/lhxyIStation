package org.apache.tools.ant.taskdefs;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ExitStatusException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.property.LocalProperties;
import org.apache.tools.ant.util.StringUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Parallel extends Task implements TaskContainer {
    private static final int NUMBER_TRIES = 100;
    private TaskList daemonTasks;
    private StringBuffer exceptionMessage;
    private boolean failOnAny;
    private Throwable firstException;
    private Integer firstExitStatus;
    private Location firstLocation;
    private volatile boolean stillRunning;
    private boolean timedOut;
    private long timeout;
    private Vector nestedTasks = new Vector();
    private final Object semaphore = new Object();
    private int numThreads = 0;
    private int numThreadsPerProcessor = 0;
    private int numExceptions = 0;

    public void setPollInterval(int i) {
    }

    public static class TaskList implements TaskContainer {
        private List tasks = new ArrayList();

        @Override // org.apache.tools.ant.TaskContainer
        public void addTask(Task task) {
            this.tasks.add(task);
        }
    }

    public void addDaemons(TaskList taskList) {
        if (this.daemonTasks != null) {
            throw new BuildException("Only one daemon group is supported");
        }
        this.daemonTasks = taskList;
    }

    public void setFailOnAny(boolean z) {
        this.failOnAny = z;
    }

    @Override // org.apache.tools.ant.TaskContainer
    public void addTask(Task task) {
        this.nestedTasks.addElement(task);
    }

    public void setThreadsPerProcessor(int i) {
        this.numThreadsPerProcessor = i;
    }

    public void setThreadCount(int i) {
        this.numThreads = i;
    }

    public void setTimeout(long j) {
        this.timeout = j;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        updateThreadCounts();
        if (this.numThreads == 0) {
            this.numThreads = this.nestedTasks.size();
        }
        spinThreads();
    }

    private void updateThreadCounts() {
        if (this.numThreadsPerProcessor != 0) {
            this.numThreads = Runtime.getRuntime().availableProcessors() * this.numThreadsPerProcessor;
        }
    }

    private void processExceptions(TaskRunnable[] taskRunnableArr) {
        if (taskRunnableArr == null) {
            return;
        }
        for (TaskRunnable taskRunnable : taskRunnableArr) {
            Throwable exception = taskRunnable.getException();
            if (exception != null) {
                this.numExceptions++;
                if (this.firstException == null) {
                    this.firstException = exception;
                }
                if ((exception instanceof BuildException) && this.firstLocation == Location.UNKNOWN_LOCATION) {
                    this.firstLocation = ((BuildException) exception).getLocation();
                }
                if ((exception instanceof ExitStatusException) && this.firstExitStatus == null) {
                    ExitStatusException exitStatusException = (ExitStatusException) exception;
                    this.firstExitStatus = Integer.valueOf(exitStatusException.getStatus());
                    this.firstLocation = exitStatusException.getLocation();
                }
                this.exceptionMessage.append(StringUtils.LINE_SEP);
                this.exceptionMessage.append(exception.getMessage());
            }
        }
    }

    private void spinThreads() throws BuildException {
        boolean z;
        int i;
        int size = this.nestedTasks.size();
        TaskRunnable[] taskRunnableArr = new TaskRunnable[size];
        this.stillRunning = true;
        this.timedOut = false;
        Enumeration enumerationElements = this.nestedTasks.elements();
        int i2 = 0;
        while (enumerationElements.hasMoreElements()) {
            taskRunnableArr[i2] = new TaskRunnable((Task) enumerationElements.nextElement());
            i2++;
        }
        int i3 = this.numThreads;
        if (size < i3) {
            i3 = size;
        }
        TaskRunnable[] taskRunnableArr2 = new TaskRunnable[i3];
        ThreadGroup threadGroup = new ThreadGroup("parallel");
        TaskList taskList = this.daemonTasks;
        TaskRunnable[] taskRunnableArr3 = (taskList == null || taskList.tasks.size() == 0) ? null : new TaskRunnable[this.daemonTasks.tasks.size()];
        synchronized (this.semaphore) {
        }
        synchronized (this.semaphore) {
            if (taskRunnableArr3 != null) {
                for (int i4 = 0; i4 < taskRunnableArr3.length; i4++) {
                    try {
                        taskRunnableArr3[i4] = new TaskRunnable((Task) this.daemonTasks.tasks.get(i4));
                        Thread thread = new Thread(threadGroup, taskRunnableArr3[i4]);
                        thread.setDaemon(true);
                        thread.start();
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
            int i5 = 0;
            int i6 = 0;
            while (i5 < i3) {
                taskRunnableArr2[i5] = taskRunnableArr[i6];
                new Thread(threadGroup, taskRunnableArr2[i5]).start();
                i5++;
                i6++;
            }
            if (this.timeout != 0) {
                new Thread() { // from class: org.apache.tools.ant.taskdefs.Parallel.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public synchronized void run() {
                        try {
                            wait(Parallel.this.timeout);
                            synchronized (Parallel.this.semaphore) {
                                Parallel.this.stillRunning = false;
                                Parallel.this.timedOut = true;
                                Parallel.this.semaphore.notifyAll();
                            }
                        } catch (InterruptedException unused) {
                        }
                    }
                }.start();
            }
            while (i6 < size) {
                try {
                    if (!this.stillRunning) {
                        break;
                    }
                    for (0; i < i3; i + 1) {
                        i = (taskRunnableArr2[i] == null || taskRunnableArr2[i].isFinished()) ? 0 : i + 1;
                        taskRunnableArr2[i] = taskRunnableArr[i6];
                        new Thread(threadGroup, taskRunnableArr2[i]).start();
                        i6++;
                        break;
                    }
                    this.semaphore.wait();
                } catch (InterruptedException unused) {
                    z = true;
                }
            }
            while (this.stillRunning) {
                int i7 = 0;
                while (true) {
                    if (i7 < i3) {
                        if (taskRunnableArr2[i7] != null && !taskRunnableArr2[i7].isFinished()) {
                            this.semaphore.wait();
                            break;
                        }
                        i7++;
                    } else {
                        this.stillRunning = false;
                        break;
                    }
                }
            }
            z = false;
            if (!this.timedOut && !this.failOnAny) {
                killAll(taskRunnableArr2);
            }
        }
        if (z) {
            throw new BuildException("Parallel execution interrupted.");
        }
        if (this.timedOut) {
            throw new BuildException("Parallel execution timed out");
        }
        this.exceptionMessage = new StringBuffer();
        this.numExceptions = 0;
        this.firstException = null;
        this.firstExitStatus = null;
        this.firstLocation = Location.UNKNOWN_LOCATION;
        processExceptions(taskRunnableArr3);
        processExceptions(taskRunnableArr);
        int i8 = this.numExceptions;
        if (i8 == 1) {
            Throwable th2 = this.firstException;
            if (th2 instanceof BuildException) {
                throw ((BuildException) th2);
            }
            throw new BuildException(this.firstException);
        }
        if (i8 > 1) {
            if (this.firstExitStatus == null) {
                throw new BuildException(this.exceptionMessage.toString(), this.firstLocation);
            }
            throw new ExitStatusException(this.exceptionMessage.toString(), this.firstExitStatus.intValue(), this.firstLocation);
        }
    }

    private void killAll(TaskRunnable[] taskRunnableArr) {
        int i = 0;
        do {
            boolean z = false;
            for (int i2 = 0; i2 < taskRunnableArr.length; i2++) {
                if (taskRunnableArr[i2] != null && !taskRunnableArr[i2].isFinished()) {
                    taskRunnableArr[i2].interrupt();
                    Thread.yield();
                    z = true;
                }
            }
            if (z) {
                i++;
                Thread.yield();
            }
            if (!z) {
                return;
            }
        } while (i < 100);
    }

    private class TaskRunnable implements Runnable {
        private Throwable exception;
        private boolean finished;
        private Task task;
        private volatile Thread thread;

        TaskRunnable(Task task) {
            this.task = task;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                LocalProperties.get(Parallel.this.getProject()).copy();
                this.thread = Thread.currentThread();
                this.task.perform();
                synchronized (Parallel.this.semaphore) {
                    this.finished = true;
                    Parallel.this.semaphore.notifyAll();
                }
            } catch (Throwable th) {
                try {
                    this.exception = th;
                    if (Parallel.this.failOnAny) {
                        Parallel.this.stillRunning = false;
                    }
                    synchronized (Parallel.this.semaphore) {
                        this.finished = true;
                        Parallel.this.semaphore.notifyAll();
                    }
                } catch (Throwable th2) {
                    synchronized (Parallel.this.semaphore) {
                        this.finished = true;
                        Parallel.this.semaphore.notifyAll();
                        throw th2;
                    }
                }
            }
        }

        public Throwable getException() {
            return this.exception;
        }

        boolean isFinished() {
            return this.finished;
        }

        void interrupt() {
            this.thread.interrupt();
        }
    }
}
