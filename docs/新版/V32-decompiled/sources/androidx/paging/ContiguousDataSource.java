package androidx.paging;

import androidx.paging.PageResult;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes2.dex */
abstract class ContiguousDataSource<Key, Value> extends DataSource<Key, Value> {
    abstract void dispatchLoadAfter(int i, Value value, int i2, Executor executor, PageResult.Receiver<Value> receiver);

    abstract void dispatchLoadBefore(int i, Value value, int i2, Executor executor, PageResult.Receiver<Value> receiver);

    abstract void dispatchLoadInitial(Key key, int i, int i2, boolean z, Executor executor, PageResult.Receiver<Value> receiver);

    abstract Key getKey(int i, Value value);

    @Override // androidx.paging.DataSource
    boolean isContiguous() {
        return true;
    }

    boolean supportsPageDropping() {
        return true;
    }

    ContiguousDataSource() {
    }
}
