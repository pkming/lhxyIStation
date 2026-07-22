package io.reactivex.internal.disposables;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.plugins.RxJavaPlugins;

/* JADX INFO: loaded from: classes2.dex */
public final class ObserverFullArbiter<T> extends FullArbiterPad1 implements Disposable {
    final Observer<? super T> actual;
    volatile boolean cancelled;
    final SpscLinkedArrayQueue<Object> queue;
    Disposable resource;
    volatile Disposable s = EmptyDisposable.INSTANCE;

    public ObserverFullArbiter(Observer<? super T> observer, Disposable disposable, int i) {
        this.actual = observer;
        this.resource = disposable;
        this.queue = new SpscLinkedArrayQueue<>(i);
    }

    @Override // io.reactivex.disposables.Disposable
    public void dispose() {
        if (this.cancelled) {
            return;
        }
        this.cancelled = true;
        disposeResource();
    }

    @Override // io.reactivex.disposables.Disposable
    public boolean isDisposed() {
        Disposable disposable = this.resource;
        return disposable != null ? disposable.isDisposed() : this.cancelled;
    }

    void disposeResource() {
        Disposable disposable = this.resource;
        this.resource = null;
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public boolean setDisposable(Disposable disposable) {
        if (this.cancelled) {
            return false;
        }
        this.queue.offer(this.s, NotificationLite.disposable(disposable));
        drain();
        return true;
    }

    public boolean onNext(T t, Disposable disposable) {
        if (this.cancelled) {
            return false;
        }
        this.queue.offer(disposable, NotificationLite.next(t));
        drain();
        return true;
    }

    public void onError(Throwable th, Disposable disposable) {
        if (this.cancelled) {
            RxJavaPlugins.onError(th);
        } else {
            this.queue.offer(disposable, NotificationLite.error(th));
            drain();
        }
    }

    public void onComplete(Disposable disposable) {
        this.queue.offer(disposable, NotificationLite.complete());
        drain();
    }

    void drain() {
        if (this.wip.getAndIncrement() != 0) {
            return;
        }
        SpscLinkedArrayQueue<Object> spscLinkedArrayQueue = this.queue;
        Observer<? super T> observer = this.actual;
        int iAddAndGet = 1;
        while (true) {
            Object objPoll = spscLinkedArrayQueue.poll();
            if (objPoll != null) {
                Object objPoll2 = spscLinkedArrayQueue.poll();
                if (objPoll == this.s) {
                    if (NotificationLite.isDisposable(objPoll2)) {
                        Disposable disposable = NotificationLite.getDisposable(objPoll2);
                        this.s.dispose();
                        if (!this.cancelled) {
                            this.s = disposable;
                        } else {
                            disposable.dispose();
                        }
                    } else if (NotificationLite.isError(objPoll2)) {
                        spscLinkedArrayQueue.clear();
                        disposeResource();
                        Throwable error = NotificationLite.getError(objPoll2);
                        if (!this.cancelled) {
                            this.cancelled = true;
                            observer.onError(error);
                        } else {
                            RxJavaPlugins.onError(error);
                        }
                    } else if (NotificationLite.isComplete(objPoll2)) {
                        spscLinkedArrayQueue.clear();
                        disposeResource();
                        if (!this.cancelled) {
                            this.cancelled = true;
                            observer.onComplete();
                        }
                    } else {
                        observer.onNext((Object) NotificationLite.getValue(objPoll2));
                    }
                }
            } else {
                iAddAndGet = this.wip.addAndGet(-iAddAndGet);
                if (iAddAndGet == 0) {
                    return;
                }
            }
        }
    }
}
