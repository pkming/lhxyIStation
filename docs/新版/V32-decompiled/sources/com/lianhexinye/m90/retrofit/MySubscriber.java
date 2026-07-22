package com.lianhexinye.m90.retrofit;

import com.lianhexinye.m90.retrofit.exception.ApiException;
import io.reactivex.Observer;

/* JADX INFO: loaded from: classes2.dex */
public abstract class MySubscriber<T> implements Observer<T> {
    String TAG = MySubscriber.class.getName();

    public abstract void onError(ApiException apiException);

    @Override // io.reactivex.Observer
    public void onError(Throwable th) {
        if (th instanceof ApiException) {
            onError((ApiException) th);
        } else {
            onError(new ApiException(th, 123));
        }
    }
}
