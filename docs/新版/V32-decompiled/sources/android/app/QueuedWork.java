package android.app;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* JADX INFO: loaded from: classes.dex */
public class QueuedWork {
    private static final ConcurrentLinkedQueue<Runnable> sPendingWorkFinishers = new ConcurrentLinkedQueue<>();
    private static ExecutorService sSingleThreadExecutor = null;

    public static ExecutorService singleThreadExecutor() {
        ExecutorService executorService;
        synchronized (QueuedWork.class) {
            if (sSingleThreadExecutor == null) {
                sSingleThreadExecutor = Executors.newSingleThreadExecutor();
            }
            executorService = sSingleThreadExecutor;
        }
        return executorService;
    }

    public static void add(Runnable runnable) {
        sPendingWorkFinishers.add(runnable);
    }

    public static void remove(Runnable runnable) {
        sPendingWorkFinishers.remove(runnable);
    }

    public static void waitToFinish() {
        while (true) {
            Runnable runnablePoll = sPendingWorkFinishers.poll();
            if (runnablePoll == null) {
                return;
            } else {
                runnablePoll.run();
            }
        }
    }

    public static boolean hasPendingWork() {
        return !sPendingWorkFinishers.isEmpty();
    }
}
