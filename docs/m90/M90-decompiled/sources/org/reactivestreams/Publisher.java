package org.reactivestreams;

/* JADX INFO: loaded from: classes3.dex */
public interface Publisher<T> {
    void subscribe(Subscriber<? super T> subscriber);
}
