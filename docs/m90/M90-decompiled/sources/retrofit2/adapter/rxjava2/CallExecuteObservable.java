package retrofit2.adapter.rxjava2;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;
import retrofit2.Call;
import retrofit2.Response;

/* JADX INFO: loaded from: classes3.dex */
final class CallExecuteObservable<T> extends Observable<Response<T>> {
    private final Call<T> originalCall;

    CallExecuteObservable(Call<T> call) {
        this.originalCall = call;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super Response<T>> observer) {
        boolean z;
        Call<T> callClone = this.originalCall.clone();
        observer.onSubscribe(new CallDisposable(callClone));
        try {
            Response<T> responseExecute = callClone.execute();
            if (!callClone.isCanceled()) {
                observer.onNext(responseExecute);
            }
            if (callClone.isCanceled()) {
                return;
            }
            try {
                observer.onComplete();
            } catch (Throwable th) {
                th = th;
                z = true;
                Exceptions.throwIfFatal(th);
                if (z) {
                    RxJavaPlugins.onError(th);
                    return;
                }
                if (callClone.isCanceled()) {
                    return;
                }
                try {
                    observer.onError(th);
                } catch (Throwable th2) {
                    Exceptions.throwIfFatal(th2);
                    RxJavaPlugins.onError(new CompositeException(th, th2));
                }
            }
        } catch (Throwable th3) {
            th = th3;
            z = false;
        }
    }

    private static final class CallDisposable implements Disposable {
        private final Call<?> call;

        CallDisposable(Call<?> call) {
            this.call = call;
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            this.call.cancel();
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return this.call.isCanceled();
        }
    }
}
