package io.reactivex.internal.operators.flowable;

import android.net.LinkQualityInfo;
import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.queue.MpscLinkedQueue;
import io.reactivex.internal.subscribers.QueueDrainSubscriber;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.UnicastProcessor;
import io.reactivex.subscribers.DisposableSubscriber;
import io.reactivex.subscribers.SerializedSubscriber;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/* JADX INFO: loaded from: classes2.dex */
public final class FlowableWindowBoundarySupplier<T, B> extends AbstractFlowableWithUpstream<T, Flowable<T>> {
    final int bufferSize;
    final Callable<? extends Publisher<B>> other;

    public FlowableWindowBoundarySupplier(Flowable<T> flowable, Callable<? extends Publisher<B>> callable, int i) {
        super(flowable);
        this.other = callable;
        this.bufferSize = i;
    }

    @Override // io.reactivex.Flowable
    protected void subscribeActual(Subscriber<? super Flowable<T>> subscriber) {
        this.source.subscribe((FlowableSubscriber) new WindowBoundaryMainSubscriber(new SerializedSubscriber(subscriber), this.other, this.bufferSize));
    }

    static final class WindowBoundaryMainSubscriber<T, B> extends QueueDrainSubscriber<T, Object, Flowable<T>> implements Subscription {
        static final Object NEXT = new Object();
        final AtomicReference<Disposable> boundary;
        final int bufferSize;
        final Callable<? extends Publisher<B>> other;
        Subscription s;
        UnicastProcessor<T> window;
        final AtomicLong windows;

        WindowBoundaryMainSubscriber(Subscriber<? super Flowable<T>> subscriber, Callable<? extends Publisher<B>> callable, int i) {
            super(subscriber, new MpscLinkedQueue());
            this.boundary = new AtomicReference<>();
            AtomicLong atomicLong = new AtomicLong();
            this.windows = atomicLong;
            this.other = callable;
            this.bufferSize = i;
            atomicLong.lazySet(1L);
        }

        /* JADX WARN: Type inference incomplete: some casts might be missing */
        @Override // io.reactivex.FlowableSubscriber, org.reactivestreams.Subscriber
        public void onSubscribe(Subscription subscription) {
            if (SubscriptionHelper.validate(this.s, subscription)) {
                this.s = subscription;
                Subscriber<? super V> subscriber = this.actual;
                subscriber.onSubscribe(this);
                if (this.cancelled) {
                    return;
                }
                try {
                    Publisher publisher = (Publisher) ObjectHelper.requireNonNull(this.other.call(), "The first window publisher supplied is null");
                    UnicastProcessor<T> unicastProcessorCreate = UnicastProcessor.create(this.bufferSize);
                    long jRequested = requested();
                    if (jRequested != 0) {
                        subscriber.onNext(unicastProcessorCreate);
                        if (jRequested != LinkQualityInfo.UNKNOWN_LONG) {
                            produced(1L);
                        }
                        this.window = unicastProcessorCreate;
                        WindowBoundaryInnerSubscriber windowBoundaryInnerSubscriber = new WindowBoundaryInnerSubscriber(this);
                        if (this.boundary.compareAndSet(null, windowBoundaryInnerSubscriber)) {
                            this.windows.getAndIncrement();
                            subscription.request(LinkQualityInfo.UNKNOWN_LONG);
                            publisher.subscribe(windowBoundaryInnerSubscriber);
                            return;
                        }
                        return;
                    }
                    subscription.cancel();
                    subscriber.onError(new MissingBackpressureException("Could not deliver first window due to lack of requests"));
                } catch (Throwable th) {
                    Exceptions.throwIfFatal(th);
                    subscription.cancel();
                    subscriber.onError(th);
                }
            }
        }

        /* JADX WARN: Type inference incomplete: some casts might be missing */
        @Override // org.reactivestreams.Subscriber
        public void onNext(T t) {
            if (this.done) {
                return;
            }
            if (fastEnter()) {
                this.window.onNext(t);
                if (leave(-1) == 0) {
                    return;
                }
            } else {
                this.queue.offer((U) NotificationLite.next(t));
                if (!enter()) {
                    return;
                }
            }
            drainLoop();
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable th) {
            if (this.done) {
                RxJavaPlugins.onError(th);
                return;
            }
            this.error = th;
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            if (this.windows.decrementAndGet() == 0) {
                DisposableHelper.dispose(this.boundary);
            }
            this.actual.onError(th);
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            if (this.done) {
                return;
            }
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            if (this.windows.decrementAndGet() == 0) {
                DisposableHelper.dispose(this.boundary);
            }
            this.actual.onComplete();
        }

        @Override // org.reactivestreams.Subscription
        public void request(long j) {
            requested(j);
        }

        @Override // org.reactivestreams.Subscription
        public void cancel() {
            this.cancelled = true;
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference incomplete: some casts might be missing */
        void drainLoop() {
            SimpleQueue simpleQueue = this.queue;
            Subscriber<? super V> subscriber = this.actual;
            UnicastProcessor<T> unicastProcessor = this.window;
            int iLeave = 1;
            while (true) {
                boolean z = this.done;
                Object objPoll = simpleQueue.poll();
                boolean z2 = objPoll == null;
                if (z && z2) {
                    DisposableHelper.dispose(this.boundary);
                    Throwable th = this.error;
                    if (th != null) {
                        unicastProcessor.onError(th);
                        return;
                    } else {
                        unicastProcessor.onComplete();
                        return;
                    }
                }
                if (!z2) {
                    if (objPoll == NEXT) {
                        unicastProcessor.onComplete();
                        if (this.windows.decrementAndGet() == 0) {
                            DisposableHelper.dispose(this.boundary);
                            return;
                        }
                        if (this.cancelled) {
                            continue;
                        } else {
                            try {
                                Publisher publisher = (Publisher) ObjectHelper.requireNonNull(this.other.call(), "The publisher supplied is null");
                                UnicastProcessor<T> unicastProcessorCreate = UnicastProcessor.create(this.bufferSize);
                                long jRequested = requested();
                                if (jRequested != 0) {
                                    this.windows.getAndIncrement();
                                    subscriber.onNext(unicastProcessorCreate);
                                    if (jRequested != LinkQualityInfo.UNKNOWN_LONG) {
                                        produced(1L);
                                    }
                                    this.window = unicastProcessorCreate;
                                    WindowBoundaryInnerSubscriber windowBoundaryInnerSubscriber = new WindowBoundaryInnerSubscriber(this);
                                    AtomicReference<Disposable> atomicReference = this.boundary;
                                    if (atomicReference.compareAndSet(atomicReference.get(), windowBoundaryInnerSubscriber)) {
                                        publisher.subscribe(windowBoundaryInnerSubscriber);
                                    }
                                } else {
                                    this.cancelled = true;
                                    subscriber.onError(new MissingBackpressureException("Could not deliver new window due to lack of requests"));
                                }
                                unicastProcessor = unicastProcessorCreate;
                            } catch (Throwable th2) {
                                Exceptions.throwIfFatal(th2);
                                DisposableHelper.dispose(this.boundary);
                                subscriber.onError(th2);
                                return;
                            }
                        }
                    } else {
                        unicastProcessor.onNext(NotificationLite.getValue(objPoll));
                    }
                } else {
                    iLeave = leave(-iLeave);
                    if (iLeave == 0) {
                        return;
                    }
                }
            }
        }

        /* JADX WARN: Type inference incomplete: some casts might be missing */
        void next() {
            this.queue.offer((U) NEXT);
            if (enter()) {
                drainLoop();
            }
        }
    }

    static final class WindowBoundaryInnerSubscriber<T, B> extends DisposableSubscriber<B> {
        boolean done;
        final WindowBoundaryMainSubscriber<T, B> parent;

        WindowBoundaryInnerSubscriber(WindowBoundaryMainSubscriber<T, B> windowBoundaryMainSubscriber) {
            this.parent = windowBoundaryMainSubscriber;
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(B b) {
            if (this.done) {
                return;
            }
            this.done = true;
            cancel();
            this.parent.next();
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
            this.parent.onComplete();
        }
    }
}
