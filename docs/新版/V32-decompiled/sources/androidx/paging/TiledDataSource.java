package androidx.paging;

import androidx.paging.PositionalDataSource;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
@Deprecated
public abstract class TiledDataSource<T> extends PositionalDataSource<T> {
    public abstract int countItems();

    @Override // androidx.paging.PositionalDataSource, androidx.paging.DataSource
    boolean isContiguous() {
        return false;
    }

    public abstract List<T> loadRange(int i, int i2);

    @Override // androidx.paging.PositionalDataSource
    public void loadInitial(PositionalDataSource.LoadInitialParams loadInitialParams, PositionalDataSource.LoadInitialCallback<T> loadInitialCallback) {
        int iCountItems = countItems();
        if (iCountItems == 0) {
            loadInitialCallback.onResult(Collections.emptyList(), 0, 0);
            return;
        }
        int iComputeInitialLoadPosition = computeInitialLoadPosition(loadInitialParams, iCountItems);
        int iComputeInitialLoadSize = computeInitialLoadSize(loadInitialParams, iComputeInitialLoadPosition, iCountItems);
        List<T> listLoadRange = loadRange(iComputeInitialLoadPosition, iComputeInitialLoadSize);
        if (listLoadRange != null && listLoadRange.size() == iComputeInitialLoadSize) {
            loadInitialCallback.onResult(listLoadRange, iComputeInitialLoadPosition, iCountItems);
        } else {
            invalidate();
        }
    }

    @Override // androidx.paging.PositionalDataSource
    public void loadRange(PositionalDataSource.LoadRangeParams loadRangeParams, PositionalDataSource.LoadRangeCallback<T> loadRangeCallback) {
        List<T> listLoadRange = loadRange(loadRangeParams.startPosition, loadRangeParams.loadSize);
        if (listLoadRange != null) {
            loadRangeCallback.onResult(listLoadRange);
        } else {
            invalidate();
        }
    }
}
