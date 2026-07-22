package org.reactivestreams;

/* JADX INFO: loaded from: classes3.dex */
public interface Subscription {
    void cancel();

    void request(long j);
}
