package io.reactivex;

/* JADX INFO: loaded from: classes2.dex */
public interface MaybeSource<T> {
    void subscribe(MaybeObserver<? super T> maybeObserver);
}
