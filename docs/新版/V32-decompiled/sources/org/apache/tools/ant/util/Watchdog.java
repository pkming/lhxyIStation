package org.apache.tools.ant.util;

import java.util.Enumeration;
import java.util.Vector;

/* JADX INFO: loaded from: classes3.dex */
public class Watchdog implements Runnable {
    public static final String ERROR_INVALID_TIMEOUT = "timeout less than 1.";
    private Vector observers = new Vector(1);
    private volatile boolean stopped = false;
    private long timeout;

    public Watchdog(long j) {
        this.timeout = -1L;
        if (j < 1) {
            throw new IllegalArgumentException(ERROR_INVALID_TIMEOUT);
        }
        this.timeout = j;
    }

    public void addTimeoutObserver(TimeoutObserver timeoutObserver) {
        this.observers.addElement(timeoutObserver);
    }

    public void removeTimeoutObserver(TimeoutObserver timeoutObserver) {
        this.observers.removeElement(timeoutObserver);
    }

    protected final void fireTimeoutOccured() {
        Enumeration enumerationElements = this.observers.elements();
        while (enumerationElements.hasMoreElements()) {
            ((TimeoutObserver) enumerationElements.nextElement()).timeoutOccured(this);
        }
    }

    public synchronized void start() {
        this.stopped = false;
        Thread thread = new Thread(this, "WATCHDOG");
        thread.setDaemon(true);
        thread.start();
    }

    public synchronized void stop() {
        this.stopped = true;
        notifyAll();
    }

    @Override // java.lang.Runnable
    public synchronized void run() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        long j = this.timeout + jCurrentTimeMillis;
        while (!this.stopped && j > jCurrentTimeMillis) {
            try {
                wait(j - jCurrentTimeMillis);
                jCurrentTimeMillis = System.currentTimeMillis();
            } catch (InterruptedException unused) {
            }
        }
        if (!this.stopped) {
            fireTimeoutOccured();
        }
    }
}
