package com.lianhexinye.m90.mvp;

import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.retrofit.Result;
import com.lianhexinye.m90.retrofit.exception.ExceptionEngine;
import com.lianhexinye.m90.retrofit.exception.ServerException;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import okhttp3.MediaType;

/* JADX INFO: loaded from: classes2.dex */
public class BasePresenter<V> {
    public static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    protected CompositeDisposable compositeDisposable = new CompositeDisposable();
    public Reference<V> mvpView;

    public void attachView(V v) {
        this.mvpView = new WeakReference(v);
    }

    protected V getView() {
        return this.mvpView.get();
    }

    public boolean isViewAttached() {
        Reference<V> reference = this.mvpView;
        return (reference == null || reference.get() == null) ? false : true;
    }

    public void detachView() {
        if (this.mvpView != null) {
            this.compositeDisposable.dispose();
            this.compositeDisposable.clear();
            this.mvpView.clear();
            this.mvpView = null;
        }
    }

    public void addSubscription(Observable<Result> observable, Observer observer) {
        observable.map(new ServerResultFunc()).onErrorResumeNext(new HttpResultFunc()).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    private class ServerResultFunc<T> implements Function<Result<T>, Result<T>> {
        private ServerResultFunc() {
        }

        @Override // io.reactivex.functions.Function
        public Result<T> apply(Result<T> result) throws Exception {
            LogUtils.d(this, "Result:" + result.getResult() + ",Message:" + result.getMessage());
            if (result.getResult() == null || result.getResult().trim().equals("0")) {
                throw new ServerException(result.getResult(), result.getMessage());
            }
            return result;
        }
    }

    private class HttpResultFunc<T> implements Function<Throwable, ObservableSource<T>> {
        private HttpResultFunc() {
        }

        @Override // io.reactivex.functions.Function
        public ObservableSource<T> apply(Throwable th) throws Exception {
            return Observable.error(ExceptionEngine.handleException(th));
        }
    }
}
