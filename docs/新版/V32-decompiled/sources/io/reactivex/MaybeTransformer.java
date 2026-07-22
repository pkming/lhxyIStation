package io.reactivex;

/* JADX INFO: loaded from: classes2.dex */
public interface MaybeTransformer<Upstream, Downstream> {
    MaybeSource<Downstream> apply(Maybe<Upstream> maybe);
}
