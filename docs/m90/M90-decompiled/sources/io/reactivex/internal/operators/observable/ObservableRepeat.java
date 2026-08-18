package io.reactivex.internal.operators.observable;

import android.net.LinkQualityInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.SequentialDisposable;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes2.dex */
public final class ObservableRepeat<T> extends AbstractObservableWithUpstream<T, T> {
    final long count;

    public ObservableRepeat(Observable<T> observable, long j) {
        super(observable);
        this.count = j;
    }

    @Override // io.reactivex.Observable
    public void subscribeActual(Observer<? super T> observer) {
        SequentialDisposable sequentialDisposable = new SequentialDisposable();
        observer.onSubscribe(sequentialDisposable);
        long j = this.count;
        long j2 = LinkQualityInfo.UNKNOWN_LONG;
        if (j != LinkQualityInfo.UNKNOWN_LONG) {
            j2 = j - 1;
        }
        new RepeatObserver(observer, j2, sequentialDisposable, this.source).subscribeNext();
    }

    static final class RepeatObserver<T> extends AtomicInteger implements Observer<T> {
        private static final long serialVersionUID = -7098360935104053232L;
        final Observer<? super T> actual;
        long remaining;
        final SequentialDisposable sd;
        final ObservableSource<? extends T> source;

        RepeatObserver(Observer<? super T> observer, long j, SequentialDisposable sequentialDisposable, ObservableSource<? extends T> observableSource) {
            this.actual = observer;
            this.sd = sequentialDisposable;
            this.source = observableSource;
            this.remaining = j;
        }

        @Override // io.reactivex.Observer
        public void onSubscribe(Disposable disposable) {
            this.sd.replace(disposable);
        }

        @Override // io.reactivex.Observer
        public void onNext(T t) {
            this.actual.onNext(t);
        }

        @Override // io.reactivex.Observer
        public void onError(Throwable th) {
            this.actual.onError(th);
        }

        @Override // io.reactivex.Observer
        public void onComplete() {
            long j = this.remaining;
            if (j != LinkQualityInfo.UNKNOWN_LONG) {
                this.remaining = j - 1;
            }
            if (j != 0) {
                subscribeNext();
            } else {
                this.actual.onComplete();
            }
        }

        void subscribeNext() {
            if (getAndIncrement() == 0) {
                int iAddAndGet = 1;
                while (!this.sd.isDisposed()) {
                    this.source.subscribe(this);
                    iAddAndGet = addAndGet(-iAddAndGet);
                    if (iAddAndGet == 0) {
                        return;
                    }
                }
            }
        }
    }
}
