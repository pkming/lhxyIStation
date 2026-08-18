package androidx.paging;

import java.lang.ref.WeakReference;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: loaded from: classes2.dex */
public abstract class PagedList<T> extends AbstractList<T> {
    final Executor mBackgroundThreadExecutor;
    final BoundaryCallback<T> mBoundaryCallback;
    final Config mConfig;
    final Executor mMainThreadExecutor;
    final int mRequiredRemainder;
    final PagedStorage<T> mStorage;
    int mLastLoad = 0;
    T mLastItem = null;
    boolean mBoundaryCallbackBeginDeferred = false;
    boolean mBoundaryCallbackEndDeferred = false;
    private int mLowestIndexAccessed = Integer.MAX_VALUE;
    private int mHighestIndexAccessed = Integer.MIN_VALUE;
    private final AtomicBoolean mDetached = new AtomicBoolean(false);
    private final ArrayList<WeakReference<Callback>> mCallbacks = new ArrayList<>();

    public static abstract class BoundaryCallback<T> {
        public void onItemAtEndLoaded(T t) {
        }

        public void onItemAtFrontLoaded(T t) {
        }

        public void onZeroItemsLoaded() {
        }
    }

    public static abstract class Callback {
        public abstract void onChanged(int i, int i2);

        public abstract void onInserted(int i, int i2);

        public abstract void onRemoved(int i, int i2);
    }

    abstract void dispatchUpdatesSinceSnapshot(PagedList<T> pagedList, Callback callback);

    public abstract DataSource<?, T> getDataSource();

    public abstract Object getLastKey();

    abstract boolean isContiguous();

    abstract void loadAroundInternal(int i);

    PagedList(PagedStorage<T> pagedStorage, Executor executor, Executor executor2, BoundaryCallback<T> boundaryCallback, Config config) {
        this.mStorage = pagedStorage;
        this.mMainThreadExecutor = executor;
        this.mBackgroundThreadExecutor = executor2;
        this.mBoundaryCallback = boundaryCallback;
        this.mConfig = config;
        this.mRequiredRemainder = (config.prefetchDistance * 2) + config.pageSize;
    }

    /* JADX WARN: Multi-variable type inference failed */
    static <K, T> PagedList<T> create(DataSource<K, T> dataSource, Executor executor, Executor executor2, BoundaryCallback<T> boundaryCallback, Config config, K k) {
        if (dataSource.isContiguous() || !config.enablePlaceholders) {
            int iIntValue = -1;
            if (!dataSource.isContiguous()) {
                dataSource = ((PositionalDataSource) dataSource).wrapAsContiguousWithoutPlaceholders();
                if (k != 0) {
                    iIntValue = ((Integer) k).intValue();
                }
            }
            return new ContiguousPagedList((ContiguousDataSource) dataSource, executor, executor2, boundaryCallback, config, k, iIntValue);
        }
        return new TiledPagedList((PositionalDataSource) dataSource, executor, executor2, boundaryCallback, config, k != 0 ? ((Integer) k).intValue() : 0);
    }

    public static final class Builder<Key, Value> {
        private BoundaryCallback mBoundaryCallback;
        private final Config mConfig;
        private final DataSource<Key, Value> mDataSource;
        private Executor mFetchExecutor;
        private Key mInitialKey;
        private Executor mNotifyExecutor;

        public Builder(DataSource<Key, Value> dataSource, Config config) {
            if (dataSource == null) {
                throw new IllegalArgumentException("DataSource may not be null");
            }
            if (config == null) {
                throw new IllegalArgumentException("Config may not be null");
            }
            this.mDataSource = dataSource;
            this.mConfig = config;
        }

        public Builder(DataSource<Key, Value> dataSource, int i) {
            this(dataSource, new Config.Builder().setPageSize(i).build());
        }

        public Builder<Key, Value> setNotifyExecutor(Executor executor) {
            this.mNotifyExecutor = executor;
            return this;
        }

        public Builder<Key, Value> setFetchExecutor(Executor executor) {
            this.mFetchExecutor = executor;
            return this;
        }

        public Builder<Key, Value> setBoundaryCallback(BoundaryCallback boundaryCallback) {
            this.mBoundaryCallback = boundaryCallback;
            return this;
        }

        public Builder<Key, Value> setInitialKey(Key key) {
            this.mInitialKey = key;
            return this;
        }

        public PagedList<Value> build() {
            Executor executor = this.mNotifyExecutor;
            if (executor == null) {
                throw new IllegalArgumentException("MainThreadExecutor required");
            }
            Executor executor2 = this.mFetchExecutor;
            if (executor2 == null) {
                throw new IllegalArgumentException("BackgroundThreadExecutor required");
            }
            return PagedList.create(this.mDataSource, executor, executor2, this.mBoundaryCallback, this.mConfig, this.mInitialKey);
        }
    }

    @Override // java.util.AbstractList, java.util.List
    public T get(int i) {
        T t = this.mStorage.get(i);
        if (t != null) {
            this.mLastItem = t;
        }
        return t;
    }

    public void loadAround(int i) {
        if (i < 0 || i >= size()) {
            throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + size());
        }
        this.mLastLoad = getPositionOffset() + i;
        loadAroundInternal(i);
        this.mLowestIndexAccessed = Math.min(this.mLowestIndexAccessed, i);
        this.mHighestIndexAccessed = Math.max(this.mHighestIndexAccessed, i);
        tryDispatchBoundaryCallbacks(true);
    }

    void deferBoundaryCallbacks(final boolean z, final boolean z2, final boolean z3) {
        if (this.mBoundaryCallback == null) {
            throw new IllegalStateException("Can't defer BoundaryCallback, no instance");
        }
        if (this.mLowestIndexAccessed == Integer.MAX_VALUE) {
            this.mLowestIndexAccessed = this.mStorage.size();
        }
        if (this.mHighestIndexAccessed == Integer.MIN_VALUE) {
            this.mHighestIndexAccessed = 0;
        }
        if (z || z2 || z3) {
            this.mMainThreadExecutor.execute(new Runnable() { // from class: androidx.paging.PagedList.1
                @Override // java.lang.Runnable
                public void run() {
                    if (z) {
                        PagedList.this.mBoundaryCallback.onZeroItemsLoaded();
                    }
                    if (z2) {
                        PagedList.this.mBoundaryCallbackBeginDeferred = true;
                    }
                    if (z3) {
                        PagedList.this.mBoundaryCallbackEndDeferred = true;
                    }
                    PagedList.this.tryDispatchBoundaryCallbacks(false);
                }
            });
        }
    }

    void tryDispatchBoundaryCallbacks(boolean z) {
        final boolean z2 = this.mBoundaryCallbackBeginDeferred && this.mLowestIndexAccessed <= this.mConfig.prefetchDistance;
        final boolean z3 = this.mBoundaryCallbackEndDeferred && this.mHighestIndexAccessed >= (size() - 1) - this.mConfig.prefetchDistance;
        if (z2 || z3) {
            if (z2) {
                this.mBoundaryCallbackBeginDeferred = false;
            }
            if (z3) {
                this.mBoundaryCallbackEndDeferred = false;
            }
            if (z) {
                this.mMainThreadExecutor.execute(new Runnable() { // from class: androidx.paging.PagedList.2
                    @Override // java.lang.Runnable
                    public void run() {
                        PagedList.this.dispatchBoundaryCallbacks(z2, z3);
                    }
                });
            } else {
                dispatchBoundaryCallbacks(z2, z3);
            }
        }
    }

    void dispatchBoundaryCallbacks(boolean z, boolean z2) {
        if (z) {
            this.mBoundaryCallback.onItemAtFrontLoaded(this.mStorage.getFirstLoadedItem());
        }
        if (z2) {
            this.mBoundaryCallback.onItemAtEndLoaded(this.mStorage.getLastLoadedItem());
        }
    }

    void offsetAccessIndices(int i) {
        this.mLastLoad += i;
        this.mLowestIndexAccessed += i;
        this.mHighestIndexAccessed += i;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.mStorage.size();
    }

    public int getLoadedCount() {
        return this.mStorage.getLoadedCount();
    }

    public boolean isImmutable() {
        return isDetached();
    }

    public List<T> snapshot() {
        return isImmutable() ? this : new SnapshotPagedList(this);
    }

    public Config getConfig() {
        return this.mConfig;
    }

    public boolean isDetached() {
        return this.mDetached.get();
    }

    public void detach() {
        this.mDetached.set(true);
    }

    public int getPositionOffset() {
        return this.mStorage.getPositionOffset();
    }

    public void addWeakCallback(List<T> list, Callback callback) {
        if (list != null && list != this) {
            if (list.isEmpty()) {
                if (!this.mStorage.isEmpty()) {
                    callback.onInserted(0, this.mStorage.size());
                }
            } else {
                dispatchUpdatesSinceSnapshot((PagedList) list, callback);
            }
        }
        for (int size = this.mCallbacks.size() - 1; size >= 0; size--) {
            if (this.mCallbacks.get(size).get() == null) {
                this.mCallbacks.remove(size);
            }
        }
        this.mCallbacks.add(new WeakReference<>(callback));
    }

    public void removeWeakCallback(Callback callback) {
        for (int size = this.mCallbacks.size() - 1; size >= 0; size--) {
            Callback callback2 = this.mCallbacks.get(size).get();
            if (callback2 == null || callback2 == callback) {
                this.mCallbacks.remove(size);
            }
        }
    }

    void notifyInserted(int i, int i2) {
        if (i2 != 0) {
            for (int size = this.mCallbacks.size() - 1; size >= 0; size--) {
                Callback callback = this.mCallbacks.get(size).get();
                if (callback != null) {
                    callback.onInserted(i, i2);
                }
            }
        }
    }

    void notifyChanged(int i, int i2) {
        if (i2 != 0) {
            for (int size = this.mCallbacks.size() - 1; size >= 0; size--) {
                Callback callback = this.mCallbacks.get(size).get();
                if (callback != null) {
                    callback.onChanged(i, i2);
                }
            }
        }
    }

    void notifyRemoved(int i, int i2) {
        if (i2 != 0) {
            for (int size = this.mCallbacks.size() - 1; size >= 0; size--) {
                Callback callback = this.mCallbacks.get(size).get();
                if (callback != null) {
                    callback.onRemoved(i, i2);
                }
            }
        }
    }

    public static class Config {
        public static final int MAX_SIZE_UNBOUNDED = Integer.MAX_VALUE;
        public final boolean enablePlaceholders;
        public final int initialLoadSizeHint;
        public final int maxSize;
        public final int pageSize;
        public final int prefetchDistance;

        Config(int i, int i2, boolean z, int i3, int i4) {
            this.pageSize = i;
            this.prefetchDistance = i2;
            this.enablePlaceholders = z;
            this.initialLoadSizeHint = i3;
            this.maxSize = i4;
        }

        public static final class Builder {
            static final int DEFAULT_INITIAL_PAGE_MULTIPLIER = 3;
            private int mPageSize = -1;
            private int mPrefetchDistance = -1;
            private int mInitialLoadSizeHint = -1;
            private boolean mEnablePlaceholders = true;
            private int mMaxSize = Integer.MAX_VALUE;

            public Builder setPageSize(int i) {
                if (i < 1) {
                    throw new IllegalArgumentException("Page size must be a positive number");
                }
                this.mPageSize = i;
                return this;
            }

            public Builder setPrefetchDistance(int i) {
                this.mPrefetchDistance = i;
                return this;
            }

            public Builder setEnablePlaceholders(boolean z) {
                this.mEnablePlaceholders = z;
                return this;
            }

            public Builder setInitialLoadSizeHint(int i) {
                this.mInitialLoadSizeHint = i;
                return this;
            }

            public Builder setMaxSize(int i) {
                this.mMaxSize = i;
                return this;
            }

            public Config build() {
                if (this.mPrefetchDistance < 0) {
                    this.mPrefetchDistance = this.mPageSize;
                }
                if (this.mInitialLoadSizeHint < 0) {
                    this.mInitialLoadSizeHint = this.mPageSize * 3;
                }
                if (!this.mEnablePlaceholders && this.mPrefetchDistance == 0) {
                    throw new IllegalArgumentException("Placeholders and prefetch are the only ways to trigger loading of more data in the PagedList, so either placeholders must be enabled, or prefetch distance must be > 0.");
                }
                int i = this.mMaxSize;
                if (i != Integer.MAX_VALUE && i < this.mPageSize + (this.mPrefetchDistance * 2)) {
                    throw new IllegalArgumentException("Maximum size must be at least pageSize + 2*prefetchDist, pageSize=" + this.mPageSize + ", prefetchDist=" + this.mPrefetchDistance + ", maxSize=" + this.mMaxSize);
                }
                return new Config(this.mPageSize, this.mPrefetchDistance, this.mEnablePlaceholders, this.mInitialLoadSizeHint, this.mMaxSize);
            }
        }
    }
}
