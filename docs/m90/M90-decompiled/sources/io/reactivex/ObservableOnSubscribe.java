package io.reactivex;

/* JADX INFO: loaded from: classes2.dex */
public interface ObservableOnSubscribe<T> {
    void subscribe(ObservableEmitter<T> observableEmitter) throws Exception;
}
