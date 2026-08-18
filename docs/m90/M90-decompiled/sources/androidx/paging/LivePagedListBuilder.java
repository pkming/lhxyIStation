package androidx.paging;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.ComputableLiveData;
import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.PagedList;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes2.dex */
public final class LivePagedListBuilder<Key, Value> {
    private PagedList.BoundaryCallback mBoundaryCallback;
    private PagedList.Config mConfig;
    private DataSource.Factory<Key, Value> mDataSourceFactory;
    private Executor mFetchExecutor;
    private Key mInitialLoadKey;

    public LivePagedListBuilder(DataSource.Factory<Key, Value> factory, PagedList.Config config) {
        this.mFetchExecutor = ArchTaskExecutor.getIOThreadExecutor();
        if (config == null) {
            throw new IllegalArgumentException("PagedList.Config must be provided");
        }
        if (factory == null) {
            throw new IllegalArgumentException("DataSource.Factory must be provided");
        }
        this.mDataSourceFactory = factory;
        this.mConfig = config;
    }

    public LivePagedListBuilder(DataSource.Factory<Key, Value> factory, int i) {
        this(factory, new PagedList.Config.Builder().setPageSize(i).build());
    }

    public LivePagedListBuilder<Key, Value> setInitialLoadKey(Key key) {
        this.mInitialLoadKey = key;
        return this;
    }

    public LivePagedListBuilder<Key, Value> setBoundaryCallback(PagedList.BoundaryCallback<Value> boundaryCallback) {
        this.mBoundaryCallback = boundaryCallback;
        return this;
    }

    public LivePagedListBuilder<Key, Value> setFetchExecutor(Executor executor) {
        this.mFetchExecutor = executor;
        return this;
    }

    public LiveData<PagedList<Value>> build() {
        return create(this.mInitialLoadKey, this.mConfig, this.mBoundaryCallback, this.mDataSourceFactory, ArchTaskExecutor.getMainThreadExecutor(), this.mFetchExecutor);
    }

    private static <Key, Value> LiveData<PagedList<Value>> create(final Key key, final PagedList.Config config, final PagedList.BoundaryCallback boundaryCallback, final DataSource.Factory<Key, Value> factory, final Executor executor, final Executor executor2) {
        return new ComputableLiveData<PagedList<Value>>(executor2) { // from class: androidx.paging.LivePagedListBuilder.1
            private final DataSource.InvalidatedCallback mCallback = new DataSource.InvalidatedCallback() { // from class: androidx.paging.LivePagedListBuilder.1.1
                @Override // androidx.paging.DataSource.InvalidatedCallback
                public void onInvalidated() {
                    invalidate();
                }
            };
            private DataSource<Key, Value> mDataSource;
            private PagedList<Value> mList;

            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Multi-variable type inference failed */
            @Override // androidx.lifecycle.ComputableLiveData
            public PagedList<Value> compute() {
                PagedList<Value> pagedListBuild;
                Object lastKey = key;
                PagedList<Value> pagedList = this.mList;
                if (pagedList != null) {
                    lastKey = pagedList.getLastKey();
                }
                do {
                    DataSource<Key, Value> dataSource = this.mDataSource;
                    if (dataSource != null) {
                        dataSource.removeInvalidatedCallback(this.mCallback);
                    }
                    DataSource<Key, Value> dataSourceCreate = factory.create();
                    this.mDataSource = dataSourceCreate;
                    dataSourceCreate.addInvalidatedCallback(this.mCallback);
                    pagedListBuild = new PagedList.Builder(this.mDataSource, config).setNotifyExecutor(executor).setFetchExecutor(executor2).setBoundaryCallback(boundaryCallback).setInitialKey(lastKey).build();
                    this.mList = pagedListBuild;
                } while (pagedListBuild.isDetached());
                return this.mList;
            }
        }.getLiveData();
    }
}
