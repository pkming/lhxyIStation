package android.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.util.Slog;
import android.util.TimedRemoteCaller;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import com.android.internal.widget.IRemoteViewsAdapterConnection;
import com.android.internal.widget.IRemoteViewsFactory;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/* JADX INFO: loaded from: classes.dex */
public class RemoteViewsAdapter extends BaseAdapter implements Handler.Callback {
    private static final String MULTI_USER_PERM = "android.permission.INTERACT_ACROSS_USERS_FULL";
    private static final int REMOTE_VIEWS_CACHE_DURATION = 5000;
    private static final String TAG = "RemoteViewsAdapter";
    private static Handler sCacheRemovalQueue = null;
    private static HandlerThread sCacheRemovalThread = null;
    private static final int sDefaultCacheSize = 40;
    private static final int sDefaultLoadingViewHeight = 50;
    private static final int sDefaultMessageType = 0;
    private static final int sUnbindServiceDelay = 5000;
    private static final int sUnbindServiceMessageType = 1;
    private final int mAppWidgetId;
    private FixedSizeRemoteViewsCache mCache;
    private WeakReference<RemoteAdapterConnectionCallback> mCallback;
    private final Context mContext;
    private boolean mDataReady;
    private final Intent mIntent;
    private LayoutInflater mLayoutInflater;
    private Handler mMainQueue;
    private boolean mNotifyDataSetChangedAfterOnServiceConnected = false;
    private RemoteViews.OnClickHandler mRemoteViewsOnClickHandler;
    private RemoteViewsFrameLayoutRefSet mRequestedViews;
    private RemoteViewsAdapterServiceConnection mServiceConnection;
    int mUserId;
    private int mVisibleWindowLowerBound;
    private int mVisibleWindowUpperBound;
    private Handler mWorkerQueue;
    private HandlerThread mWorkerThread;
    private static final HashMap<RemoteViewsCacheKey, FixedSizeRemoteViewsCache> sCachedRemoteViewsCaches = new HashMap<>();
    private static final HashMap<RemoteViewsCacheKey, Runnable> sRemoteViewsCacheRemoveRunnables = new HashMap<>();

    public interface RemoteAdapterConnectionCallback {
        void deferNotifyDataSetChanged();

        boolean onRemoteAdapterConnected();

        void onRemoteAdapterDisconnected();
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return null;
    }

    private static class RemoteViewsAdapterServiceConnection extends IRemoteViewsAdapterConnection.Stub {
        private WeakReference<RemoteViewsAdapter> mAdapter;
        private boolean mIsConnected;
        private boolean mIsConnecting;
        private IRemoteViewsFactory mRemoteViewsFactory;

        public RemoteViewsAdapterServiceConnection(RemoteViewsAdapter remoteViewsAdapter) {
            this.mAdapter = new WeakReference<>(remoteViewsAdapter);
        }

        public synchronized void bind(Context context, int i, Intent intent) {
            if (!this.mIsConnecting) {
                try {
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    RemoteViewsAdapter remoteViewsAdapter = this.mAdapter.get();
                    if (remoteViewsAdapter != null) {
                        RemoteViewsAdapter.checkInteractAcrossUsersPermission(context, remoteViewsAdapter.mUserId);
                        appWidgetManager.bindRemoteViewsService(i, intent, asBinder(), new UserHandle(remoteViewsAdapter.mUserId));
                    } else {
                        Slog.w(RemoteViewsAdapter.TAG, "bind: adapter was null");
                    }
                    this.mIsConnecting = true;
                } catch (Exception e) {
                    Log.e("RemoteViewsAdapterServiceConnection", "bind(): " + e.getMessage());
                    this.mIsConnecting = false;
                    this.mIsConnected = false;
                }
            }
        }

        public synchronized void unbind(Context context, int i, Intent intent) {
            try {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                RemoteViewsAdapter remoteViewsAdapter = this.mAdapter.get();
                if (remoteViewsAdapter != null) {
                    RemoteViewsAdapter.checkInteractAcrossUsersPermission(context, remoteViewsAdapter.mUserId);
                    appWidgetManager.unbindRemoteViewsService(i, intent, new UserHandle(remoteViewsAdapter.mUserId));
                } else {
                    Slog.w(RemoteViewsAdapter.TAG, "unbind: adapter was null");
                }
                this.mIsConnecting = false;
            } catch (Exception e) {
                Log.e("RemoteViewsAdapterServiceConnection", "unbind(): " + e.getMessage());
                this.mIsConnecting = false;
                this.mIsConnected = false;
            }
        }

        public synchronized void onServiceConnected(IBinder iBinder) {
            this.mRemoteViewsFactory = IRemoteViewsFactory.Stub.asInterface(iBinder);
            final RemoteViewsAdapter remoteViewsAdapter = this.mAdapter.get();
            if (remoteViewsAdapter == null) {
                return;
            }
            remoteViewsAdapter.mWorkerQueue.post(new Runnable() { // from class: android.widget.RemoteViewsAdapter.RemoteViewsAdapterServiceConnection.1
                @Override // java.lang.Runnable
                public void run() {
                    if (remoteViewsAdapter.mNotifyDataSetChangedAfterOnServiceConnected) {
                        remoteViewsAdapter.onNotifyDataSetChanged();
                    } else {
                        IRemoteViewsFactory remoteViewsFactory = remoteViewsAdapter.mServiceConnection.getRemoteViewsFactory();
                        try {
                            if (!remoteViewsFactory.isCreated()) {
                                remoteViewsFactory.onDataSetChanged();
                            }
                        } catch (RemoteException e) {
                            Log.e(RemoteViewsAdapter.TAG, "Error notifying factory of data set changed in onServiceConnected(): " + e.getMessage());
                            return;
                        } catch (RuntimeException e2) {
                            Log.e(RemoteViewsAdapter.TAG, "Error notifying factory of data set changed in onServiceConnected(): " + e2.getMessage());
                        }
                        remoteViewsAdapter.updateTemporaryMetaData();
                        remoteViewsAdapter.mMainQueue.post(new Runnable() { // from class: android.widget.RemoteViewsAdapter.RemoteViewsAdapterServiceConnection.1.1
                            @Override // java.lang.Runnable
                            public void run() {
                                synchronized (remoteViewsAdapter.mCache) {
                                    remoteViewsAdapter.mCache.commitTemporaryMetaData();
                                }
                                RemoteAdapterConnectionCallback remoteAdapterConnectionCallback = (RemoteAdapterConnectionCallback) remoteViewsAdapter.mCallback.get();
                                if (remoteAdapterConnectionCallback != null) {
                                    remoteAdapterConnectionCallback.onRemoteAdapterConnected();
                                }
                            }
                        });
                    }
                    remoteViewsAdapter.enqueueDeferredUnbindServiceMessage();
                    RemoteViewsAdapterServiceConnection.this.mIsConnected = true;
                    RemoteViewsAdapterServiceConnection.this.mIsConnecting = false;
                }
            });
        }

        public synchronized void onServiceDisconnected() {
            this.mIsConnected = false;
            this.mIsConnecting = false;
            this.mRemoteViewsFactory = null;
            final RemoteViewsAdapter remoteViewsAdapter = this.mAdapter.get();
            if (remoteViewsAdapter == null) {
                return;
            }
            remoteViewsAdapter.mMainQueue.post(new Runnable() { // from class: android.widget.RemoteViewsAdapter.RemoteViewsAdapterServiceConnection.2
                @Override // java.lang.Runnable
                public void run() {
                    remoteViewsAdapter.mMainQueue.removeMessages(1);
                    RemoteAdapterConnectionCallback remoteAdapterConnectionCallback = (RemoteAdapterConnectionCallback) remoteViewsAdapter.mCallback.get();
                    if (remoteAdapterConnectionCallback != null) {
                        remoteAdapterConnectionCallback.onRemoteAdapterDisconnected();
                    }
                }
            });
        }

        public synchronized IRemoteViewsFactory getRemoteViewsFactory() {
            return this.mRemoteViewsFactory;
        }

        public synchronized boolean isConnected() {
            return this.mIsConnected;
        }
    }

    private static class RemoteViewsFrameLayout extends FrameLayout {
        public RemoteViewsFrameLayout(Context context) {
            super(context);
        }

        public void onRemoteViewsLoaded(RemoteViews remoteViews, RemoteViews.OnClickHandler onClickHandler) {
            try {
                removeAllViews();
                addView(remoteViews.apply(getContext(), this, onClickHandler));
            } catch (Exception unused) {
                Log.e(RemoteViewsAdapter.TAG, "Failed to apply RemoteViews.");
            }
        }
    }

    private class RemoteViewsFrameLayoutRefSet {
        private HashMap<Integer, LinkedList<RemoteViewsFrameLayout>> mReferences = new HashMap<>();
        private HashMap<RemoteViewsFrameLayout, LinkedList<RemoteViewsFrameLayout>> mViewToLinkedList = new HashMap<>();

        public RemoteViewsFrameLayoutRefSet() {
        }

        public void add(int i, RemoteViewsFrameLayout remoteViewsFrameLayout) {
            LinkedList<RemoteViewsFrameLayout> linkedList;
            Integer numValueOf = Integer.valueOf(i);
            if (this.mReferences.containsKey(numValueOf)) {
                linkedList = this.mReferences.get(numValueOf);
            } else {
                LinkedList<RemoteViewsFrameLayout> linkedList2 = new LinkedList<>();
                this.mReferences.put(numValueOf, linkedList2);
                linkedList = linkedList2;
            }
            this.mViewToLinkedList.put(remoteViewsFrameLayout, linkedList);
            linkedList.add(remoteViewsFrameLayout);
        }

        public void notifyOnRemoteViewsLoaded(int i, RemoteViews remoteViews) {
            if (remoteViews == null) {
                return;
            }
            Integer numValueOf = Integer.valueOf(i);
            if (this.mReferences.containsKey(numValueOf)) {
                LinkedList<RemoteViewsFrameLayout> linkedList = this.mReferences.get(numValueOf);
                for (RemoteViewsFrameLayout remoteViewsFrameLayout : linkedList) {
                    remoteViewsFrameLayout.onRemoteViewsLoaded(remoteViews, RemoteViewsAdapter.this.mRemoteViewsOnClickHandler);
                    if (this.mViewToLinkedList.containsKey(remoteViewsFrameLayout)) {
                        this.mViewToLinkedList.remove(remoteViewsFrameLayout);
                    }
                }
                linkedList.clear();
                this.mReferences.remove(numValueOf);
            }
        }

        public void removeView(RemoteViewsFrameLayout remoteViewsFrameLayout) {
            if (this.mViewToLinkedList.containsKey(remoteViewsFrameLayout)) {
                this.mViewToLinkedList.get(remoteViewsFrameLayout).remove(remoteViewsFrameLayout);
                this.mViewToLinkedList.remove(remoteViewsFrameLayout);
            }
        }

        public void clear() {
            this.mReferences.clear();
            this.mViewToLinkedList.clear();
        }
    }

    private static class RemoteViewsMetaData {
        int count;
        boolean hasStableIds;
        RemoteViews mFirstView;
        int mFirstViewHeight;
        private final HashMap<Integer, Integer> mTypeIdIndexMap = new HashMap<>();
        RemoteViews mUserLoadingView;
        int viewTypeCount;

        public RemoteViewsMetaData() {
            reset();
        }

        public void set(RemoteViewsMetaData remoteViewsMetaData) {
            synchronized (remoteViewsMetaData) {
                this.count = remoteViewsMetaData.count;
                this.viewTypeCount = remoteViewsMetaData.viewTypeCount;
                this.hasStableIds = remoteViewsMetaData.hasStableIds;
                setLoadingViewTemplates(remoteViewsMetaData.mUserLoadingView, remoteViewsMetaData.mFirstView);
            }
        }

        public void reset() {
            this.count = 0;
            this.viewTypeCount = 1;
            this.hasStableIds = true;
            this.mUserLoadingView = null;
            this.mFirstView = null;
            this.mFirstViewHeight = 0;
            this.mTypeIdIndexMap.clear();
        }

        public void setLoadingViewTemplates(RemoteViews remoteViews, RemoteViews remoteViews2) {
            this.mUserLoadingView = remoteViews;
            if (remoteViews2 != null) {
                this.mFirstView = remoteViews2;
                this.mFirstViewHeight = -1;
            }
        }

        public int getMappedViewType(int i) {
            if (this.mTypeIdIndexMap.containsKey(Integer.valueOf(i))) {
                return this.mTypeIdIndexMap.get(Integer.valueOf(i)).intValue();
            }
            int size = this.mTypeIdIndexMap.size() + 1;
            this.mTypeIdIndexMap.put(Integer.valueOf(i), Integer.valueOf(size));
            return size;
        }

        public boolean isViewTypeInRange(int i) {
            return getMappedViewType(i) < this.viewTypeCount;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Removed duplicated region for block: B:13:0x0032 A[Catch: all -> 0x009e, TRY_LEAVE, TryCatch #2 {, blocks: (B:4:0x000a, B:7:0x000f, B:13:0x0032, B:16:0x0037, B:19:0x0056, B:20:0x0083, B:21:0x009c, B:10:0x0028), top: B:30:0x000a, inners: #0, #1 }] */
        /* JADX WARN: Removed duplicated region for block: B:21:0x009c A[Catch: all -> 0x009e, DONT_GENERATE, TryCatch #2 {, blocks: (B:4:0x000a, B:7:0x000f, B:13:0x0032, B:16:0x0037, B:19:0x0056, B:20:0x0083, B:21:0x009c, B:10:0x0028), top: B:30:0x000a, inners: #0, #1 }] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public android.widget.RemoteViewsAdapter.RemoteViewsFrameLayout createLoadingView(int r5, android.view.View r6, android.view.ViewGroup r7, java.lang.Object r8, android.view.LayoutInflater r9, android.widget.RemoteViews.OnClickHandler r10) {
            /*
                r4 = this;
                android.content.Context r5 = r7.getContext()
                android.widget.RemoteViewsAdapter$RemoteViewsFrameLayout r6 = new android.widget.RemoteViewsAdapter$RemoteViewsFrameLayout
                r6.<init>(r5)
                monitor-enter(r8)
                android.widget.RemoteViews r0 = r4.mUserLoadingView     // Catch: java.lang.Throwable -> L9e
                r1 = 0
                if (r0 == 0) goto L2f
                android.content.Context r2 = r7.getContext()     // Catch: java.lang.Exception -> L27 java.lang.Throwable -> L9e
                android.view.View r0 = r0.apply(r2, r7, r10)     // Catch: java.lang.Exception -> L27 java.lang.Throwable -> L9e
                r2 = 16908895(0x102025f, float:2.387893E-38)
                java.lang.Integer r3 = new java.lang.Integer     // Catch: java.lang.Exception -> L27 java.lang.Throwable -> L9e
                r3.<init>(r1)     // Catch: java.lang.Exception -> L27 java.lang.Throwable -> L9e
                r0.setTagInternal(r2, r3)     // Catch: java.lang.Exception -> L27 java.lang.Throwable -> L9e
                r6.addView(r0)     // Catch: java.lang.Exception -> L27 java.lang.Throwable -> L9e
                r0 = 1
                goto L30
            L27:
                r0 = move-exception
                java.lang.String r2 = "RemoteViewsAdapter"
                java.lang.String r3 = "Error inflating custom loading view, using default loadingview instead"
                android.util.Log.w(r2, r3, r0)     // Catch: java.lang.Throwable -> L9e
            L2f:
                r0 = r1
            L30:
                if (r0 != 0) goto L9c
                int r0 = r4.mFirstViewHeight     // Catch: java.lang.Throwable -> L9e
                if (r0 >= 0) goto L83
                r0 = 0
                android.widget.RemoteViews r2 = r4.mFirstView     // Catch: java.lang.Exception -> L55 java.lang.Throwable -> L9e
                android.content.Context r3 = r7.getContext()     // Catch: java.lang.Exception -> L55 java.lang.Throwable -> L9e
                android.view.View r7 = r2.apply(r3, r7, r10)     // Catch: java.lang.Exception -> L55 java.lang.Throwable -> L9e
                int r10 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r1)     // Catch: java.lang.Exception -> L55 java.lang.Throwable -> L9e
                int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r1)     // Catch: java.lang.Exception -> L55 java.lang.Throwable -> L9e
                r7.measure(r10, r2)     // Catch: java.lang.Exception -> L55 java.lang.Throwable -> L9e
                int r7 = r7.getMeasuredHeight()     // Catch: java.lang.Exception -> L55 java.lang.Throwable -> L9e
                r4.mFirstViewHeight = r7     // Catch: java.lang.Exception -> L55 java.lang.Throwable -> L9e
                r4.mFirstView = r0     // Catch: java.lang.Exception -> L55 java.lang.Throwable -> L9e
                goto L83
            L55:
                r7 = move-exception
                android.content.res.Resources r5 = r5.getResources()     // Catch: java.lang.Throwable -> L9e
                android.util.DisplayMetrics r5 = r5.getDisplayMetrics()     // Catch: java.lang.Throwable -> L9e
                float r5 = r5.density     // Catch: java.lang.Throwable -> L9e
                r10 = 1112014848(0x42480000, float:50.0)
                float r5 = r5 * r10
                int r5 = java.lang.Math.round(r5)     // Catch: java.lang.Throwable -> L9e
                r4.mFirstViewHeight = r5     // Catch: java.lang.Throwable -> L9e
                r4.mFirstView = r0     // Catch: java.lang.Throwable -> L9e
                java.lang.String r5 = "RemoteViewsAdapter"
                java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L9e
                r10.<init>()     // Catch: java.lang.Throwable -> L9e
                java.lang.String r0 = "Error inflating first RemoteViews"
                java.lang.StringBuilder r10 = r10.append(r0)     // Catch: java.lang.Throwable -> L9e
                java.lang.StringBuilder r7 = r10.append(r7)     // Catch: java.lang.Throwable -> L9e
                java.lang.String r7 = r7.toString()     // Catch: java.lang.Throwable -> L9e
                android.util.Log.w(r5, r7)     // Catch: java.lang.Throwable -> L9e
            L83:
                r5 = 17367174(0x1090086, float:2.5163301E-38)
                android.view.View r5 = r9.inflate(r5, r6, r1)     // Catch: java.lang.Throwable -> L9e
                android.widget.TextView r5 = (android.widget.TextView) r5     // Catch: java.lang.Throwable -> L9e
                int r7 = r4.mFirstViewHeight     // Catch: java.lang.Throwable -> L9e
                r5.setHeight(r7)     // Catch: java.lang.Throwable -> L9e
                java.lang.Integer r7 = new java.lang.Integer     // Catch: java.lang.Throwable -> L9e
                r7.<init>(r1)     // Catch: java.lang.Throwable -> L9e
                r5.setTag(r7)     // Catch: java.lang.Throwable -> L9e
                r6.addView(r5)     // Catch: java.lang.Throwable -> L9e
            L9c:
                monitor-exit(r8)     // Catch: java.lang.Throwable -> L9e
                return r6
            L9e:
                r5 = move-exception
                monitor-exit(r8)     // Catch: java.lang.Throwable -> L9e
                throw r5
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.RemoteViewsAdapter.RemoteViewsMetaData.createLoadingView(int, android.view.View, android.view.ViewGroup, java.lang.Object, android.view.LayoutInflater, android.widget.RemoteViews$OnClickHandler):android.widget.RemoteViewsAdapter$RemoteViewsFrameLayout");
        }
    }

    private static class RemoteViewsIndexMetaData {
        long itemId;
        int typeId;

        public RemoteViewsIndexMetaData(RemoteViews remoteViews, long j) {
            set(remoteViews, j);
        }

        public void set(RemoteViews remoteViews, long j) {
            this.itemId = j;
            if (remoteViews != null) {
                this.typeId = remoteViews.getLayoutId();
            } else {
                this.typeId = 0;
            }
        }
    }

    private static class FixedSizeRemoteViewsCache {
        private static final String TAG = "FixedSizeRemoteViewsCache";
        private static final float sMaxCountSlackPercent = 0.75f;
        private static final int sMaxMemoryLimitInBytes = 2097152;
        private int mMaxCount;
        private int mMaxCountSlack;
        private int mPreloadLowerBound = 0;
        private int mPreloadUpperBound = -1;
        private final RemoteViewsMetaData mMetaData = new RemoteViewsMetaData();
        private final RemoteViewsMetaData mTemporaryMetaData = new RemoteViewsMetaData();
        private HashMap<Integer, RemoteViewsIndexMetaData> mIndexMetaData = new HashMap<>();
        private HashMap<Integer, RemoteViews> mIndexRemoteViews = new HashMap<>();
        private HashSet<Integer> mRequestedIndices = new HashSet<>();
        private int mLastRequestedIndex = -1;
        private HashSet<Integer> mLoadIndices = new HashSet<>();

        public FixedSizeRemoteViewsCache(int i) {
            this.mMaxCount = i;
            this.mMaxCountSlack = Math.round((i / 2) * 0.75f);
        }

        public void insert(int i, RemoteViews remoteViews, long j, ArrayList<Integer> arrayList) {
            if (this.mIndexRemoteViews.size() >= this.mMaxCount) {
                this.mIndexRemoteViews.remove(Integer.valueOf(getFarthestPositionFrom(i, arrayList)));
            }
            int i2 = this.mLastRequestedIndex;
            if (i2 <= -1) {
                i2 = i;
            }
            while (getRemoteViewsBitmapMemoryUsage() >= 2097152) {
                this.mIndexRemoteViews.remove(Integer.valueOf(getFarthestPositionFrom(i2, arrayList)));
            }
            if (this.mIndexMetaData.containsKey(Integer.valueOf(i))) {
                this.mIndexMetaData.get(Integer.valueOf(i)).set(remoteViews, j);
            } else {
                this.mIndexMetaData.put(Integer.valueOf(i), new RemoteViewsIndexMetaData(remoteViews, j));
            }
            this.mIndexRemoteViews.put(Integer.valueOf(i), remoteViews);
        }

        public RemoteViewsMetaData getMetaData() {
            return this.mMetaData;
        }

        public RemoteViewsMetaData getTemporaryMetaData() {
            return this.mTemporaryMetaData;
        }

        public RemoteViews getRemoteViewsAt(int i) {
            if (this.mIndexRemoteViews.containsKey(Integer.valueOf(i))) {
                return this.mIndexRemoteViews.get(Integer.valueOf(i));
            }
            return null;
        }

        public RemoteViewsIndexMetaData getMetaDataAt(int i) {
            if (this.mIndexMetaData.containsKey(Integer.valueOf(i))) {
                return this.mIndexMetaData.get(Integer.valueOf(i));
            }
            return null;
        }

        public void commitTemporaryMetaData() {
            synchronized (this.mTemporaryMetaData) {
                synchronized (this.mMetaData) {
                    this.mMetaData.set(this.mTemporaryMetaData);
                }
            }
        }

        private int getRemoteViewsBitmapMemoryUsage() {
            Iterator<Integer> it = this.mIndexRemoteViews.keySet().iterator();
            int iEstimateMemoryUsage = 0;
            while (it.hasNext()) {
                RemoteViews remoteViews = this.mIndexRemoteViews.get(it.next());
                if (remoteViews != null) {
                    iEstimateMemoryUsage += remoteViews.estimateMemoryUsage();
                }
            }
            return iEstimateMemoryUsage;
        }

        private int getFarthestPositionFrom(int i, ArrayList<Integer> arrayList) {
            Iterator<Integer> it = this.mIndexRemoteViews.keySet().iterator();
            int i2 = 0;
            int i3 = 0;
            int i4 = -1;
            int i5 = -1;
            while (it.hasNext()) {
                int iIntValue = it.next().intValue();
                int iAbs = Math.abs(iIntValue - i);
                if (iAbs > i2 && !arrayList.contains(Integer.valueOf(iIntValue))) {
                    i4 = iIntValue;
                    i2 = iAbs;
                }
                if (iAbs >= i3) {
                    i5 = iIntValue;
                    i3 = iAbs;
                }
            }
            return i4 > -1 ? i4 : i5;
        }

        public void queueRequestedPositionToLoad(int i) {
            this.mLastRequestedIndex = i;
            synchronized (this.mLoadIndices) {
                this.mRequestedIndices.add(Integer.valueOf(i));
                this.mLoadIndices.add(Integer.valueOf(i));
            }
        }

        public boolean queuePositionsToBePreloadedFromRequestedPosition(int i) {
            int i2;
            int i3;
            int i4 = this.mPreloadLowerBound;
            if (i4 <= i && i <= (i3 = this.mPreloadUpperBound) && Math.abs(i - ((i3 + i4) / 2)) < this.mMaxCountSlack) {
                return false;
            }
            synchronized (this.mMetaData) {
                i2 = this.mMetaData.count;
            }
            synchronized (this.mLoadIndices) {
                this.mLoadIndices.clear();
                this.mLoadIndices.addAll(this.mRequestedIndices);
                int i5 = this.mMaxCount / 2;
                int i6 = i - i5;
                this.mPreloadLowerBound = i6;
                this.mPreloadUpperBound = i + i5;
                int iMin = Math.min(this.mPreloadUpperBound, i2 - 1);
                for (int iMax = Math.max(0, i6); iMax <= iMin; iMax++) {
                    this.mLoadIndices.add(Integer.valueOf(iMax));
                }
                this.mLoadIndices.removeAll(this.mIndexRemoteViews.keySet());
            }
            return true;
        }

        public int[] getNextIndexToLoad() {
            synchronized (this.mLoadIndices) {
                if (!this.mRequestedIndices.isEmpty()) {
                    Integer next = this.mRequestedIndices.iterator().next();
                    this.mRequestedIndices.remove(next);
                    this.mLoadIndices.remove(next);
                    return new int[]{next.intValue(), 1};
                }
                if (this.mLoadIndices.isEmpty()) {
                    return new int[]{-1, 0};
                }
                Integer next2 = this.mLoadIndices.iterator().next();
                this.mLoadIndices.remove(next2);
                return new int[]{next2.intValue(), 0};
            }
        }

        public boolean containsRemoteViewAt(int i) {
            return this.mIndexRemoteViews.containsKey(Integer.valueOf(i));
        }

        public boolean containsMetaDataAt(int i) {
            return this.mIndexMetaData.containsKey(Integer.valueOf(i));
        }

        public void reset() {
            this.mPreloadLowerBound = 0;
            this.mPreloadUpperBound = -1;
            this.mLastRequestedIndex = -1;
            this.mIndexRemoteViews.clear();
            this.mIndexMetaData.clear();
            synchronized (this.mLoadIndices) {
                this.mRequestedIndices.clear();
                this.mLoadIndices.clear();
            }
        }
    }

    static class RemoteViewsCacheKey {
        final Intent.FilterComparison filter;
        final int userId;
        final int widgetId;

        RemoteViewsCacheKey(Intent.FilterComparison filterComparison, int i, int i2) {
            this.filter = filterComparison;
            this.widgetId = i;
            this.userId = i2;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof RemoteViewsCacheKey)) {
                return false;
            }
            RemoteViewsCacheKey remoteViewsCacheKey = (RemoteViewsCacheKey) obj;
            return remoteViewsCacheKey.filter.equals(this.filter) && remoteViewsCacheKey.widgetId == this.widgetId && remoteViewsCacheKey.userId == this.userId;
        }

        public int hashCode() {
            Intent.FilterComparison filterComparison = this.filter;
            return ((filterComparison == null ? 0 : filterComparison.hashCode()) ^ (this.widgetId << 2)) ^ (this.userId << 10);
        }
    }

    public RemoteViewsAdapter(Context context, Intent intent, RemoteAdapterConnectionCallback remoteAdapterConnectionCallback) {
        this.mDataReady = false;
        this.mContext = context;
        this.mIntent = intent;
        int intExtra = intent.getIntExtra("remoteAdapterAppWidgetId", -1);
        this.mAppWidgetId = intExtra;
        this.mLayoutInflater = LayoutInflater.from(context);
        if (intent == null) {
            throw new IllegalArgumentException("Non-null Intent must be specified.");
        }
        this.mRequestedViews = new RemoteViewsFrameLayoutRefSet();
        checkInteractAcrossUsersPermission(context, UserHandle.myUserId());
        this.mUserId = context.getUserId();
        if (intent.hasExtra("remoteAdapterAppWidgetId")) {
            intent.removeExtra("remoteAdapterAppWidgetId");
        }
        HandlerThread handlerThread = new HandlerThread("RemoteViewsCache-loader");
        this.mWorkerThread = handlerThread;
        handlerThread.start();
        this.mWorkerQueue = new Handler(this.mWorkerThread.getLooper());
        this.mMainQueue = new Handler(Looper.myLooper(), this);
        if (sCacheRemovalThread == null) {
            HandlerThread handlerThread2 = new HandlerThread("RemoteViewsAdapter-cachePruner");
            sCacheRemovalThread = handlerThread2;
            handlerThread2.start();
            sCacheRemovalQueue = new Handler(sCacheRemovalThread.getLooper());
        }
        this.mCallback = new WeakReference<>(remoteAdapterConnectionCallback);
        this.mServiceConnection = new RemoteViewsAdapterServiceConnection(this);
        RemoteViewsCacheKey remoteViewsCacheKey = new RemoteViewsCacheKey(new Intent.FilterComparison(intent), intExtra, this.mUserId);
        HashMap<RemoteViewsCacheKey, FixedSizeRemoteViewsCache> map = sCachedRemoteViewsCaches;
        synchronized (map) {
            if (map.containsKey(remoteViewsCacheKey)) {
                FixedSizeRemoteViewsCache fixedSizeRemoteViewsCache = map.get(remoteViewsCacheKey);
                this.mCache = fixedSizeRemoteViewsCache;
                synchronized (fixedSizeRemoteViewsCache.mMetaData) {
                    if (this.mCache.mMetaData.count > 0) {
                        this.mDataReady = true;
                    }
                }
            } else {
                this.mCache = new FixedSizeRemoteViewsCache(40);
            }
            if (!this.mDataReady) {
                requestBindService();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkInteractAcrossUsersPermission(Context context, int i) {
        if (context.getUserId() != i && context.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL") != 0) {
            throw new SecurityException("Must have permission android.permission.INTERACT_ACROSS_USERS_FULL to inflate another user's widget");
        }
    }

    protected void finalize() throws Throwable {
        try {
            HandlerThread handlerThread = this.mWorkerThread;
            if (handlerThread != null) {
                handlerThread.quit();
            }
        } finally {
            super.finalize();
        }
    }

    public boolean isDataReady() {
        return this.mDataReady;
    }

    public void setRemoteViewsOnClickHandler(RemoteViews.OnClickHandler onClickHandler) {
        this.mRemoteViewsOnClickHandler = onClickHandler;
    }

    public void saveRemoteViewsCache() {
        int i;
        int size;
        final RemoteViewsCacheKey remoteViewsCacheKey = new RemoteViewsCacheKey(new Intent.FilterComparison(this.mIntent), this.mAppWidgetId, this.mUserId);
        HashMap<RemoteViewsCacheKey, FixedSizeRemoteViewsCache> map = sCachedRemoteViewsCaches;
        synchronized (map) {
            HashMap<RemoteViewsCacheKey, Runnable> map2 = sRemoteViewsCacheRemoveRunnables;
            if (map2.containsKey(remoteViewsCacheKey)) {
                sCacheRemovalQueue.removeCallbacks(map2.get(remoteViewsCacheKey));
                map2.remove(remoteViewsCacheKey);
            }
            synchronized (this.mCache.mMetaData) {
                i = this.mCache.mMetaData.count;
            }
            synchronized (this.mCache) {
                size = this.mCache.mIndexRemoteViews.size();
            }
            if (i > 0 && size > 0) {
                map.put(remoteViewsCacheKey, this.mCache);
            }
            Runnable runnable = new Runnable() { // from class: android.widget.RemoteViewsAdapter.1
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (RemoteViewsAdapter.sCachedRemoteViewsCaches) {
                        if (RemoteViewsAdapter.sCachedRemoteViewsCaches.containsKey(remoteViewsCacheKey)) {
                            RemoteViewsAdapter.sCachedRemoteViewsCaches.remove(remoteViewsCacheKey);
                        }
                        if (RemoteViewsAdapter.sRemoteViewsCacheRemoveRunnables.containsKey(remoteViewsCacheKey)) {
                            RemoteViewsAdapter.sRemoteViewsCacheRemoveRunnables.remove(remoteViewsCacheKey);
                        }
                    }
                }
            };
            map2.put(remoteViewsCacheKey, runnable);
            sCacheRemovalQueue.postDelayed(runnable, TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadNextIndexInBackground() {
        this.mWorkerQueue.post(new Runnable() { // from class: android.widget.RemoteViewsAdapter.2
            @Override // java.lang.Runnable
            public void run() {
                int i;
                if (RemoteViewsAdapter.this.mServiceConnection.isConnected()) {
                    synchronized (RemoteViewsAdapter.this.mCache) {
                        i = RemoteViewsAdapter.this.mCache.getNextIndexToLoad()[0];
                    }
                    if (i > -1) {
                        RemoteViewsAdapter.this.updateRemoteViews(i, true);
                        RemoteViewsAdapter.this.loadNextIndexInBackground();
                    } else {
                        RemoteViewsAdapter.this.enqueueDeferredUnbindServiceMessage();
                    }
                }
            }
        });
    }

    private void processException(String str, Exception exc) {
        Log.e(TAG, "Error in " + str + ": " + exc.getMessage());
        RemoteViewsMetaData metaData = this.mCache.getMetaData();
        synchronized (metaData) {
            metaData.reset();
        }
        synchronized (this.mCache) {
            this.mCache.reset();
        }
        this.mMainQueue.post(new Runnable() { // from class: android.widget.RemoteViewsAdapter.3
            @Override // java.lang.Runnable
            public void run() {
                RemoteViewsAdapter.this.superNotifyDataSetChanged();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTemporaryMetaData() {
        IRemoteViewsFactory remoteViewsFactory = this.mServiceConnection.getRemoteViewsFactory();
        try {
            boolean zHasStableIds = remoteViewsFactory.hasStableIds();
            int viewTypeCount = remoteViewsFactory.getViewTypeCount();
            int count = remoteViewsFactory.getCount();
            RemoteViews loadingView = remoteViewsFactory.getLoadingView();
            RemoteViews viewAt = null;
            if (count > 0 && loadingView == null) {
                viewAt = remoteViewsFactory.getViewAt(0);
            }
            RemoteViewsMetaData temporaryMetaData = this.mCache.getTemporaryMetaData();
            synchronized (temporaryMetaData) {
                temporaryMetaData.hasStableIds = zHasStableIds;
                temporaryMetaData.viewTypeCount = viewTypeCount + 1;
                temporaryMetaData.count = count;
                temporaryMetaData.setLoadingViewTemplates(loadingView, viewAt);
            }
        } catch (RemoteException e) {
            processException("updateMetaData", e);
        } catch (RuntimeException e2) {
            processException("updateMetaData", e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRemoteViews(final int i, boolean z) {
        boolean zIsViewTypeInRange;
        int i2;
        IRemoteViewsFactory remoteViewsFactory = this.mServiceConnection.getRemoteViewsFactory();
        try {
            final RemoteViews viewAt = remoteViewsFactory.getViewAt(i);
            viewAt.setUser(new UserHandle(this.mUserId));
            long itemId = remoteViewsFactory.getItemId(i);
            if (viewAt == null) {
                Log.e(TAG, "Error in updateRemoteViews(" + i + "):  null RemoteViews returned from RemoteViewsFactory.");
                return;
            }
            int layoutId = viewAt.getLayoutId();
            RemoteViewsMetaData metaData = this.mCache.getMetaData();
            synchronized (metaData) {
                zIsViewTypeInRange = metaData.isViewTypeInRange(layoutId);
                i2 = this.mCache.mMetaData.count;
            }
            synchronized (this.mCache) {
                if (zIsViewTypeInRange) {
                    this.mCache.insert(i, viewAt, itemId, getVisibleWindow(this.mVisibleWindowLowerBound, this.mVisibleWindowUpperBound, i2));
                    if (z) {
                        this.mMainQueue.post(new Runnable() { // from class: android.widget.RemoteViewsAdapter.4
                            @Override // java.lang.Runnable
                            public void run() {
                                RemoteViewsAdapter.this.mRequestedViews.notifyOnRemoteViewsLoaded(i, viewAt);
                            }
                        });
                    }
                } else {
                    Log.e(TAG, "Error: widget's RemoteViewsFactory returns more view types than  indicated by getViewTypeCount() ");
                }
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error in updateRemoteViews(" + i + "): " + e.getMessage());
        } catch (RuntimeException e2) {
            Log.e(TAG, "Error in updateRemoteViews(" + i + "): " + e2.getMessage());
        }
    }

    public Intent getRemoteViewsServiceIntent() {
        return this.mIntent;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        int i;
        RemoteViewsMetaData metaData = this.mCache.getMetaData();
        synchronized (metaData) {
            i = metaData.count;
        }
        return i;
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        synchronized (this.mCache) {
            if (!this.mCache.containsMetaDataAt(i)) {
                return 0L;
            }
            return this.mCache.getMetaDataAt(i).itemId;
        }
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getItemViewType(int i) {
        int mappedViewType;
        synchronized (this.mCache) {
            if (!this.mCache.containsMetaDataAt(i)) {
                return 0;
            }
            int i2 = this.mCache.getMetaDataAt(i).typeId;
            RemoteViewsMetaData metaData = this.mCache.getMetaData();
            synchronized (metaData) {
                mappedViewType = metaData.getMappedViewType(i2);
            }
            return mappedViewType;
        }
    }

    private int getConvertViewTypeId(View view) {
        Object tag;
        if (view == null || (tag = view.getTag(16908895)) == null) {
            return -1;
        }
        return ((Integer) tag).intValue();
    }

    public void setVisibleRangeHint(int i, int i2) {
        this.mVisibleWindowLowerBound = i;
        this.mVisibleWindowUpperBound = i2;
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        boolean zQueuePositionsToBePreloadedFromRequestedPosition;
        RemoteViewsFrameLayout remoteViewsFrameLayoutCreateLoadingView;
        View childAt;
        synchronized (this.mCache) {
            boolean zContainsRemoteViewAt = this.mCache.containsRemoteViewAt(i);
            boolean zIsConnected = this.mServiceConnection.isConnected();
            if (view != null && (view instanceof RemoteViewsFrameLayout)) {
                this.mRequestedViews.removeView((RemoteViewsFrameLayout) view);
            }
            int convertViewTypeId = 0;
            if (zContainsRemoteViewAt || zIsConnected) {
                zQueuePositionsToBePreloadedFromRequestedPosition = this.mCache.queuePositionsToBePreloadedFromRequestedPosition(i);
            } else {
                requestBindService();
                zQueuePositionsToBePreloadedFromRequestedPosition = false;
            }
            if (!zContainsRemoteViewAt) {
                RemoteViewsMetaData metaData = this.mCache.getMetaData();
                synchronized (metaData) {
                    remoteViewsFrameLayoutCreateLoadingView = metaData.createLoadingView(i, view, viewGroup, this.mCache, this.mLayoutInflater, this.mRemoteViewsOnClickHandler);
                }
                this.mRequestedViews.add(i, remoteViewsFrameLayoutCreateLoadingView);
                this.mCache.queueRequestedPositionToLoad(i);
                loadNextIndexInBackground();
                return remoteViewsFrameLayoutCreateLoadingView;
            }
            RemoteViewsFrameLayout remoteViewsFrameLayout = null;
            if (view instanceof RemoteViewsFrameLayout) {
                remoteViewsFrameLayout = (RemoteViewsFrameLayout) view;
                childAt = remoteViewsFrameLayout.getChildAt(0);
                convertViewTypeId = getConvertViewTypeId(childAt);
            } else {
                childAt = null;
            }
            Context context = viewGroup.getContext();
            RemoteViews remoteViewsAt = this.mCache.getRemoteViewsAt(i);
            int i2 = this.mCache.getMetaDataAt(i).typeId;
            try {
                try {
                    if (remoteViewsFrameLayout == null) {
                        remoteViewsFrameLayout = new RemoteViewsFrameLayout(context);
                    } else {
                        if (convertViewTypeId == i2) {
                            remoteViewsAt.reapply(context, childAt, this.mRemoteViewsOnClickHandler);
                            return remoteViewsFrameLayout;
                        }
                        remoteViewsFrameLayout.removeAllViews();
                    }
                    View viewApply = remoteViewsAt.apply(context, viewGroup, this.mRemoteViewsOnClickHandler);
                    viewApply.setTagInternal(16908895, new Integer(i2));
                    remoteViewsFrameLayout.addView(viewApply);
                    if (zQueuePositionsToBePreloadedFromRequestedPosition) {
                        loadNextIndexInBackground();
                    }
                    return remoteViewsFrameLayout;
                } catch (Exception e) {
                    Log.w(TAG, "Error inflating RemoteViews at position: " + i + ", usingloading view instead" + e);
                    RemoteViewsMetaData metaData2 = this.mCache.getMetaData();
                    synchronized (metaData2) {
                        RemoteViewsFrameLayout remoteViewsFrameLayoutCreateLoadingView2 = metaData2.createLoadingView(i, view, viewGroup, this.mCache, this.mLayoutInflater, this.mRemoteViewsOnClickHandler);
                        if (zQueuePositionsToBePreloadedFromRequestedPosition) {
                            loadNextIndexInBackground();
                        }
                        return remoteViewsFrameLayoutCreateLoadingView2;
                    }
                }
            } finally {
                if (zQueuePositionsToBePreloadedFromRequestedPosition) {
                    loadNextIndexInBackground();
                }
            }
        }
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getViewTypeCount() {
        int i;
        RemoteViewsMetaData metaData = this.mCache.getMetaData();
        synchronized (metaData) {
            i = metaData.viewTypeCount;
        }
        return i;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean hasStableIds() {
        boolean z;
        RemoteViewsMetaData metaData = this.mCache.getMetaData();
        synchronized (metaData) {
            z = metaData.hasStableIds;
        }
        return z;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean isEmpty() {
        return getCount() <= 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNotifyDataSetChanged() {
        int i;
        ArrayList<Integer> visibleWindow;
        try {
            this.mServiceConnection.getRemoteViewsFactory().onDataSetChanged();
            synchronized (this.mCache) {
                this.mCache.reset();
            }
            updateTemporaryMetaData();
            synchronized (this.mCache.getTemporaryMetaData()) {
                i = this.mCache.getTemporaryMetaData().count;
                visibleWindow = getVisibleWindow(this.mVisibleWindowLowerBound, this.mVisibleWindowUpperBound, i);
            }
            Iterator<Integer> it = visibleWindow.iterator();
            while (it.hasNext()) {
                int iIntValue = it.next().intValue();
                if (iIntValue < i) {
                    updateRemoteViews(iIntValue, false);
                }
            }
            this.mMainQueue.post(new Runnable() { // from class: android.widget.RemoteViewsAdapter.5
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (RemoteViewsAdapter.this.mCache) {
                        RemoteViewsAdapter.this.mCache.commitTemporaryMetaData();
                    }
                    RemoteViewsAdapter.this.superNotifyDataSetChanged();
                    RemoteViewsAdapter.this.enqueueDeferredUnbindServiceMessage();
                }
            });
            this.mNotifyDataSetChangedAfterOnServiceConnected = false;
        } catch (RemoteException e) {
            Log.e(TAG, "Error in updateNotifyDataSetChanged(): " + e.getMessage());
        } catch (RuntimeException e2) {
            Log.e(TAG, "Error in updateNotifyDataSetChanged(): " + e2.getMessage());
        }
    }

    private ArrayList<Integer> getVisibleWindow(int i, int i2, int i3) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        if ((i != 0 || i2 != 0) && i >= 0 && i2 >= 0) {
            if (i <= i2) {
                while (i <= i2) {
                    arrayList.add(Integer.valueOf(i));
                    i++;
                }
            } else {
                while (i < i3) {
                    arrayList.add(Integer.valueOf(i));
                    i++;
                }
                for (int i4 = 0; i4 <= i2; i4++) {
                    arrayList.add(Integer.valueOf(i4));
                }
            }
        }
        return arrayList;
    }

    @Override // android.widget.BaseAdapter
    public void notifyDataSetChanged() {
        this.mMainQueue.removeMessages(1);
        if (!this.mServiceConnection.isConnected()) {
            if (this.mNotifyDataSetChangedAfterOnServiceConnected) {
                return;
            }
            this.mNotifyDataSetChangedAfterOnServiceConnected = true;
            requestBindService();
            return;
        }
        this.mWorkerQueue.post(new Runnable() { // from class: android.widget.RemoteViewsAdapter.6
            @Override // java.lang.Runnable
            public void run() {
                RemoteViewsAdapter.this.onNotifyDataSetChanged();
            }
        });
    }

    void superNotifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        if (message.what != 1) {
            return false;
        }
        if (!this.mServiceConnection.isConnected()) {
            return true;
        }
        this.mServiceConnection.unbind(this.mContext, this.mAppWidgetId, this.mIntent);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enqueueDeferredUnbindServiceMessage() {
        this.mMainQueue.removeMessages(1);
        this.mMainQueue.sendEmptyMessageDelayed(1, TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
    }

    private boolean requestBindService() {
        if (!this.mServiceConnection.isConnected()) {
            this.mServiceConnection.bind(this.mContext, this.mAppWidgetId, this.mIntent);
        }
        this.mMainQueue.removeMessages(1);
        return this.mServiceConnection.isConnected();
    }
}
