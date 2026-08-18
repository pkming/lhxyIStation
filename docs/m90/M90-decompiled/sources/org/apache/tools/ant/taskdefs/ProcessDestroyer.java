package org.apache.tools.ant.taskdefs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;

/* JADX INFO: loaded from: classes3.dex */
class ProcessDestroyer implements Runnable {
    private static final int THREAD_DIE_TIMEOUT = 20000;
    private Method addShutdownHookMethod;
    private Method removeShutdownHookMethod;
    private HashSet processes = new HashSet();
    private ProcessDestroyerImpl destroyProcessThread = null;
    private boolean added = false;
    private boolean running = false;

    private class ProcessDestroyerImpl extends Thread {
        private boolean shouldDestroy;

        public ProcessDestroyerImpl() {
            super("ProcessDestroyer Shutdown Hook");
            this.shouldDestroy = true;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            if (this.shouldDestroy) {
                ProcessDestroyer.this.run();
            }
        }

        public void setShouldDestroy(boolean z) {
            this.shouldDestroy = z;
        }
    }

    ProcessDestroyer() {
        try {
            Class[] clsArr = {Thread.class};
            this.addShutdownHookMethod = Runtime.class.getMethod("addShutdownHook", clsArr);
            this.removeShutdownHookMethod = Runtime.class.getMethod("removeShutdownHook", clsArr);
        } catch (NoSuchMethodException unused) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addShutdownHook() {
        if (this.addShutdownHookMethod == null || this.running) {
            return;
        }
        ProcessDestroyerImpl processDestroyerImpl = new ProcessDestroyerImpl();
        this.destroyProcessThread = processDestroyerImpl;
        try {
            this.addShutdownHookMethod.invoke(Runtime.getRuntime(), processDestroyerImpl);
            this.added = true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e2) {
            Throwable targetException = e2.getTargetException();
            if (targetException != null && targetException.getClass() == IllegalStateException.class) {
                this.running = true;
            } else {
                e2.printStackTrace();
            }
        }
    }

    private void removeShutdownHook() {
        Method method = this.removeShutdownHookMethod;
        if (method == null || !this.added || this.running) {
            return;
        }
        try {
            if (!((Boolean) method.invoke(Runtime.getRuntime(), this.destroyProcessThread)).booleanValue()) {
                System.err.println("Could not remove shutdown hook");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e2) {
            Throwable targetException = e2.getTargetException();
            if (targetException != null && targetException.getClass() == IllegalStateException.class) {
                this.running = true;
            } else {
                e2.printStackTrace();
            }
        }
        this.destroyProcessThread.setShouldDestroy(false);
        if (!this.destroyProcessThread.getThreadGroup().isDestroyed()) {
            this.destroyProcessThread.start();
        }
        try {
            this.destroyProcessThread.join(20000L);
        } catch (InterruptedException unused) {
        }
        this.destroyProcessThread = null;
        this.added = false;
    }

    public boolean isAddedAsShutdownHook() {
        return this.added;
    }

    public boolean add(Process process) {
        boolean zAdd;
        synchronized (this.processes) {
            if (this.processes.size() == 0) {
                addShutdownHook();
            }
            zAdd = this.processes.add(process);
        }
        return zAdd;
    }

    public boolean remove(Process process) {
        boolean zRemove;
        synchronized (this.processes) {
            zRemove = this.processes.remove(process);
            if (zRemove && this.processes.size() == 0) {
                removeShutdownHook();
            }
        }
        return zRemove;
    }

    @Override // java.lang.Runnable
    public void run() {
        synchronized (this.processes) {
            this.running = true;
            Iterator it = this.processes.iterator();
            while (it.hasNext()) {
                ((Process) it.next()).destroy();
            }
        }
    }
}
