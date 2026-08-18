package io.reactivex.internal.operators.flowable;

import android.net.LinkQualityInfo;
import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.SimplePlainQueue;
import io.reactivex.internal.queue.MpscLinkedQueue;
import io.reactivex.internal.subscribers.QueueDrainSubscriber;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.QueueDrainHelper;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subscribers.DisposableSubscriber;
import io.reactivex.subscribers.SerializedSubscriber;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/* JADX INFO: loaded from: classes2.dex */
public final class FlowableBufferBoundary<T, U extends Collection<? super T>, Open, Close> extends AbstractFlowableWithUpstream<T, U> {
    final Function<? super Open, ? extends Publisher<? extends Close>> bufferClose;
    final Publisher<? extends Open> bufferOpen;
    final Callable<U> bufferSupplier;

    public FlowableBufferBoundary(Flowable<T> flowable, Publisher<? extends Open> publisher, Function<? super Open, ? extends Publisher<? extends Close>> function, Callable<U> callable) {
        super(flowable);
        this.bufferOpen = publisher;
        this.bufferClose = function;
        this.bufferSupplier = callable;
    }

    @Override // io.reactivex.Flowable
    protected void subscribeActual(Subscriber<? super U> subscriber) {
        this.source.subscribe((FlowableSubscriber) new BufferBoundarySubscriber(new SerializedSubscriber(subscriber), this.bufferOpen, this.bufferClose, this.bufferSupplier));
    }

    static final class BufferBoundarySubscriber<T, U extends Collection<? super T>, Open, Close> extends QueueDrainSubscriber<T, U, U> implements Subscription, Disposable {
        final Function<? super Open, ? extends Publisher<? extends Close>> bufferClose;
        final Publisher<? extends Open> bufferOpen;
        final Callable<U> bufferSupplier;
        final List<U> buffers;
        final CompositeDisposable resources;
        Subscription s;
        final AtomicInteger windows;

        @Override // io.reactivex.internal.subscribers.QueueDrainSubscriber, io.reactivex.internal.util.QueueDrain
        public /* bridge */ /* synthetic */ boolean accept(Subscriber subscriber, Object obj) {
            return accept((Subscriber<? super Collection>) subscriber, (Collection) obj);
        }

        BufferBoundarySubscriber(Subscriber<? super U> subscriber, Publisher<? extends Open> publisher, Function<? super Open, ? extends Publisher<? extends Close>> function, Callable<U> callable) {
            super(subscriber, new MpscLinkedQueue());
            this.windows = new AtomicInteger();
            this.bufferOpen = publisher;
            this.bufferClose = function;
            this.bufferSupplier = callable;
            this.buffers = new LinkedList();
            this.resources = new CompositeDisposable();
        }

        @Override // io.reactivex.FlowableSubscriber, org.reactivestreams.Subscriber
        public void onSubscribe(Subscription subscription) {
            if (SubscriptionHelper.validate(this.s, subscription)) {
                this.s = subscription;
                BufferOpenSubscriber bufferOpenSubscriber = new BufferOpenSubscriber(this);
                this.resources.add(bufferOpenSubscriber);
                this.actual.onSubscribe(this);
                this.windows.lazySet(1);
                this.bufferOpen.subscribe(bufferOpenSubscriber);
                subscription.request(LinkQualityInfo.UNKNOWN_LONG);
            }
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(T t) {
            synchronized (this) {
                Iterator<U> it = this.buffers.iterator();
                while (it.hasNext()) {
                    it.next().add(t);
                }
            }
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable th) {
            cancel();
            this.cancelled = true;
            synchronized (this) {
                this.buffers.clear();
            }
            this.actual.onError(th);
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            if (this.windows.decrementAndGet() == 0) {
                complete();
            }
        }

        /* JADX WARN: Type inference incomplete: some casts might be missing */
        void complete() {
            ArrayList arrayList;
            synchronized (this) {
                arrayList = new ArrayList(this.buffers);
                this.buffers.clear();
            }
            SimplePlainQueue<U> simplePlainQueue = this.queue;
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                simplePlainQueue.offer((U) ((Collection) it.next()));
            }
            this.done = true;
            if (enter()) {
                QueueDrainHelper.drainMaxLoop(simplePlainQueue, this.actual, false, this, this);
            }
        }

        @Override // org.reactivestreams.Subscription
        public void request(long j) {
            requested(j);
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            this.resources.dispose();
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return this.resources.isDisposed();
        }

        @Override // org.reactivestreams.Subscription
        public void cancel() {
            if (this.cancelled) {
                return;
            }
            this.cancelled = true;
            dispose();
        }

        /* JADX WARN: Multi-variable type inference failed */
        public boolean accept(Subscriber<? super U> subscriber, U u) {
            subscriber.onNext(u);
            return true;
        }

        void open(Open open) {
            if (this.cancelled) {
                return;
            }
            try {
                Collection collection = (Collection) ObjectHelper.requireNonNull(this.bufferSupplier.call(), "The buffer supplied is null");
                try {
                    Publisher publisher = (Publisher) ObjectHelper.requireNonNull(this.bufferClose.apply(open), "The buffer closing publisher is null");
                    if (this.cancelled) {
                        return;
                    }
                    synchronized (this) {
                        if (this.cancelled) {
                            return;
                        }
                        this.buffers.add((U) collection);
                        BufferCloseSubscriber bufferCloseSubscriber = new BufferCloseSubscriber(collection, this);
                        this.resources.add(bufferCloseSubscriber);
                        this.windows.getAndIncrement();
                        publisher.subscribe(bufferCloseSubscriber);
                    }
                } catch (Throwable th) {
                    Exceptions.throwIfFatal(th);
                    onError(th);
                }
            } catch (Throwable th2) {
                Exceptions.throwIfFatal(th2);
                onError(th2);
            }
        }

        void openFinished(Disposable disposable) {
            if (this.resources.remove(disposable) && this.windows.decrementAndGet() == 0) {
                complete();
            }
        }

        void close(U u, Disposable disposable) {
            boolean zRemove;
            synchronized (this) {
                zRemove = this.buffers.remove(u);
            }
            if (zRemove) {
                fastPathOrderedEmitMax(u, false, this);
            }
            if (this.resources.remove(disposable) && this.windows.decrementAndGet() == 0) {
                complete();
            }
        }
    }

    static final class BufferOpenSubscriber<T, U extends Collection<? super T>, Open, Close> extends DisposableSubscriber<Open> {
        boolean done;
        final BufferBoundarySubscriber<T, U, Open, Close> parent;

        BufferOpenSubscriber(BufferBoundarySubscriber<T, U, Open, Close> bufferBoundarySubscriber) {
            this.parent = bufferBoundarySubscriber;
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(Open open) {
            if (this.done) {
                return;
            }
            this.parent.open(open);
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable th) {
            if (this.done) {
                RxJavaPlugins.onError(th);
            } else {
                this.done = true;
                this.parent.onError(th);
            }
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            if (this.done) {
                return;
            }
            this.done = true;
            this.parent.openFinished(this);
        }
    }

    static final class BufferCloseSubscriber<T, U extends Collection<? super T>, Open, Close> extends DisposableSubscriber<Close> {
        boolean done;
        final BufferBoundarySubscriber<T, U, Open, Close> parent;
        final U value;

        BufferCloseSubscriber(U u, BufferBoundarySubscriber<T, U, Open, Close> bufferBoundarySubscriber) {
            this.parent = bufferBoundarySubscriber;
            this.value = u;
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(Close close) {
            onComplete();
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable th) {
            if (this.done) {
                RxJavaPlugins.onError(th);
            } else {
                this.parent.onError(th);
            }
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            if (this.done) {
                return;
            }
            this.done = true;
            this.parent.close(this.value, this);
        }
    }
}
