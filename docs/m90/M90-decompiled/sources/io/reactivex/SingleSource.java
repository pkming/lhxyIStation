package io.reactivex;

/* JADX INFO: loaded from: classes2.dex */
public interface SingleSource<T> {
    void subscribe(SingleObserver<? super T> singleObserver);
}
