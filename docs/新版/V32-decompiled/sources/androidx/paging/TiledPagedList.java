package androidx.paging;

import androidx.paging.PageResult;
import androidx.paging.PagedList;
import androidx.paging.PagedStorage;
import java.util.List;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes2.dex */
class TiledPagedList<T> extends PagedList<T> implements PagedStorage.Callback {
    final PositionalDataSource<T> mDataSource;
    PageResult.Receiver<T> mReceiver;

    @Override // androidx.paging.PagedList
    boolean isContiguous() {
        return false;
    }

    TiledPagedList(PositionalDataSource<T> positionalDataSource, Executor executor, Executor executor2, PagedList.BoundaryCallback<T> boundaryCallback, PagedList.Config config, int i) {
        super(new PagedStorage(), executor, executor2, boundaryCallback, config);
        this.mReceiver = new PageResult.Receiver<T>() { // from class: androidx.paging.TiledPagedList.1
            @Override // androidx.paging.PageResult.Receiver
            public void onPageResult(int i2, PageResult<T> pageResult) {
                if (pageResult.isInvalid()) {
                    TiledPagedList.this.detach();
                    return;
                }
                if (TiledPagedList.this.isDetached()) {
                    return;
                }
                if (i2 != 0 && i2 != 3) {
                    throw new IllegalArgumentException("unexpected resultType" + i2);
                }
                List<T> list = pageResult.page;
                if (TiledPagedList.this.mStorage.getPageCount() == 0) {
                    TiledPagedList.this.mStorage.initAndSplit(pageResult.leadingNulls, list, pageResult.trailingNulls, pageResult.positionOffset, TiledPagedList.this.mConfig.pageSize, TiledPagedList.this);
                } else {
                    TiledPagedList.this.mStorage.tryInsertPageAndTrim(pageResult.positionOffset, list, TiledPagedList.this.mLastLoad, TiledPagedList.this.mConfig.maxSize, TiledPagedList.this.mRequiredRemainder, TiledPagedList.this);
                }
                if (TiledPagedList.this.mBoundaryCallback != null) {
                    boolean z = true;
                    boolean z2 = TiledPagedList.this.mStorage.size() == 0;
                    boolean z3 = !z2 && pageResult.leadingNulls == 0 && pageResult.positionOffset == 0;
                    int size = TiledPagedList.this.size();
                    if (z2 || ((i2 != 0 || pageResult.trailingNulls != 0) && (i2 != 3 || pageResult.positionOffset + TiledPagedList.this.mConfig.pageSize < size))) {
                        z = false;
                    }
                    TiledPagedList.this.deferBoundaryCallbacks(z2, z3, z);
                }
            }
        };
        this.mDataSource = positionalDataSource;
        int i2 = this.mConfig.pageSize;
        this.mLastLoad = i;
        if (positionalDataSource.isInvalid()) {
            detach();
        } else {
            int iMax = Math.max(this.mConfig.initialLoadSizeHint / i2, 2) * i2;
            positionalDataSource.dispatchLoadInitial(true, Math.max(0, ((i - (iMax / 2)) / i2) * i2), iMax, i2, this.mMainThreadExecutor, this.mReceiver);
        }
    }

    @Override // androidx.paging.PagedList
    public DataSource<?, T> getDataSource() {
        return this.mDataSource;
    }

    @Override // androidx.paging.PagedList
    public Object getLastKey() {
        return Integer.valueOf(this.mLastLoad);
    }

    @Override // androidx.paging.PagedList
    protected void dispatchUpdatesSinceSnapshot(PagedList<T> pagedList, PagedList.Callback callback) {
        PagedStorage<T> pagedStorage = pagedList.mStorage;
        if (pagedStorage.isEmpty() || this.mStorage.size() != pagedStorage.size()) {
            throw new IllegalArgumentException("Invalid snapshot provided - doesn't appear to be a snapshot of this PagedList");
        }
        int i = this.mConfig.pageSize;
        int leadingNullCount = this.mStorage.getLeadingNullCount() / i;
        int pageCount = this.mStorage.getPageCount();
        int i2 = 0;
        while (i2 < pageCount) {
            int i3 = i2 + leadingNullCount;
            int i4 = 0;
            while (i4 < this.mStorage.getPageCount()) {
                int i5 = i3 + i4;
                if (!this.mStorage.hasPage(i, i5) || pagedStorage.hasPage(i, i5)) {
                    break;
                } else {
                    i4++;
                }
            }
            if (i4 > 0) {
                callback.onChanged(i3 * i, i * i4);
                i2 += i4 - 1;
            }
            i2++;
        }
    }

    @Override // androidx.paging.PagedList
    protected void loadAroundInternal(int i) {
        this.mStorage.allocatePlaceholders(i, this.mConfig.prefetchDistance, this.mConfig.pageSize, this);
    }

    @Override // androidx.paging.PagedStorage.Callback
    public void onInitialized(int i) {
        notifyInserted(0, i);
    }

    @Override // androidx.paging.PagedStorage.Callback
    public void onPagePrepended(int i, int i2, int i3) {
        throw new IllegalStateException("Contiguous callback on TiledPagedList");
    }

    @Override // androidx.paging.PagedStorage.Callback
    public void onPageAppended(int i, int i2, int i3) {
        throw new IllegalStateException("Contiguous callback on TiledPagedList");
    }

    @Override // androidx.paging.PagedStorage.Callback
    public void onEmptyPrepend() {
        throw new IllegalStateException("Contiguous callback on TiledPagedList");
    }

    @Override // androidx.paging.PagedStorage.Callback
    public void onEmptyAppend() {
        throw new IllegalStateException("Contiguous callback on TiledPagedList");
    }

    @Override // androidx.paging.PagedStorage.Callback
    public void onPagePlaceholderInserted(final int i) {
        this.mBackgroundThreadExecutor.execute(new Runnable() { // from class: androidx.paging.TiledPagedList.2
            @Override // java.lang.Runnable
            public void run() {
                if (TiledPagedList.this.isDetached()) {
                    return;
                }
                int i2 = TiledPagedList.this.mConfig.pageSize;
                if (TiledPagedList.this.mDataSource.isInvalid()) {
                    TiledPagedList.this.detach();
                    return;
                }
                int i3 = i * i2;
                TiledPagedList.this.mDataSource.dispatchLoadRange(3, i3, Math.min(i2, TiledPagedList.this.mStorage.size() - i3), TiledPagedList.this.mMainThreadExecutor, TiledPagedList.this.mReceiver);
            }
        });
    }

    @Override // androidx.paging.PagedStorage.Callback
    public void onPageInserted(int i, int i2) {
        notifyChanged(i, i2);
    }

    @Override // androidx.paging.PagedStorage.Callback
    public void onPagesRemoved(int i, int i2) {
        notifyRemoved(i, i2);
    }

    @Override // androidx.paging.PagedStorage.Callback
    public void onPagesSwappedToPlaceholder(int i, int i2) {
        notifyChanged(i, i2);
    }
}
