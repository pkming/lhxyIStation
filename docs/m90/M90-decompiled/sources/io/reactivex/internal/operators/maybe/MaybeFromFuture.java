package io.reactivex.internal.operators.maybe;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* JADX INFO: loaded from: classes2.dex */
public final class MaybeFromFuture<T> extends Maybe<T> {
    final Future<? extends T> future;
    final long timeout;
    final TimeUnit unit;

    public MaybeFromFuture(Future<? extends T> future, long j, TimeUnit timeUnit) {
        this.future = future;
        this.timeout = j;
        this.unit = timeUnit;
    }

    @Override // io.reactivex.Maybe
    protected void subscribeActual(MaybeObserver<? super T> maybeObserver) {
        T t;
        Disposable disposableEmpty = Disposables.empty();
        maybeObserver.onSubscribe(disposableEmpty);
        if (disposableEmpty.isDisposed()) {
            return;
        }
        try {
            long j = this.timeout;
            if (j <= 0) {
                t = this.future.get();
            } else {
                t = this.future.get(j, this.unit);
            }
            if (disposableEmpty.isDisposed()) {
                return;
            }
            if (t == null) {
                maybeObserver.onComplete();
            } else {
                maybeObserver.onSuccess(t);
            }
        } catch (InterruptedException e) {
            if (disposableEmpty.isDisposed()) {
                return;
            }
            maybeObserver.onError(e);
        } catch (ExecutionException e2) {
            if (disposableEmpty.isDisposed()) {
                return;
            }
            maybeObserver.onError(e2.getCause());
        } catch (TimeoutException e3) {
            if (disposableEmpty.isDisposed()) {
                return;
            }
            maybeObserver.onError(e3);
        }
    }
}
