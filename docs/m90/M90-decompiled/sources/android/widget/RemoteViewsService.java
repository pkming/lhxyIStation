package android.widget;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.android.internal.widget.IRemoteViewsFactory;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public abstract class RemoteViewsService extends Service {
    private static final String LOG_TAG = "RemoteViewsService";
    private static final HashMap<Intent.FilterComparison, RemoteViewsFactory> sRemoteViewFactories = new HashMap<>();
    private static final Object sLock = new Object();

    public interface RemoteViewsFactory {
        int getCount();

        long getItemId(int i);

        RemoteViews getLoadingView();

        RemoteViews getViewAt(int i);

        int getViewTypeCount();

        boolean hasStableIds();

        void onCreate();

        void onDataSetChanged();

        void onDestroy();
    }

    public abstract RemoteViewsFactory onGetViewFactory(Intent intent);

    private static class RemoteViewsFactoryAdapter extends IRemoteViewsFactory.Stub {
        private RemoteViewsFactory mFactory;
        private boolean mIsCreated;

        public RemoteViewsFactoryAdapter(RemoteViewsFactory remoteViewsFactory, boolean z) {
            this.mFactory = remoteViewsFactory;
            this.mIsCreated = z;
        }

        public synchronized boolean isCreated() {
            return this.mIsCreated;
        }

        public synchronized void onDataSetChanged() {
            try {
                this.mFactory.onDataSetChanged();
            } catch (Exception e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }

        public synchronized void onDataSetChangedAsync() {
            onDataSetChanged();
        }

        public synchronized int getCount() {
            int count;
            try {
                count = this.mFactory.getCount();
            } catch (Exception e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                count = 0;
            }
            return count;
        }

        public synchronized RemoteViews getViewAt(int i) {
            RemoteViews viewAt;
            viewAt = null;
            try {
                viewAt = this.mFactory.getViewAt(i);
                if (viewAt != null) {
                    viewAt.setIsWidgetCollectionChild(true);
                }
            } catch (Exception e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
            return viewAt;
        }

        public synchronized RemoteViews getLoadingView() {
            RemoteViews loadingView;
            try {
                loadingView = this.mFactory.getLoadingView();
            } catch (Exception e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                loadingView = null;
            }
            return loadingView;
        }

        public synchronized int getViewTypeCount() {
            int viewTypeCount;
            try {
                viewTypeCount = this.mFactory.getViewTypeCount();
            } catch (Exception e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                viewTypeCount = 0;
            }
            return viewTypeCount;
        }

        public synchronized long getItemId(int i) {
            long itemId;
            try {
                itemId = this.mFactory.getItemId(i);
            } catch (Exception e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                itemId = 0;
            }
            return itemId;
        }

        public synchronized boolean hasStableIds() {
            boolean zHasStableIds;
            try {
                zHasStableIds = this.mFactory.hasStableIds();
            } catch (Exception e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                zHasStableIds = false;
            }
            return zHasStableIds;
        }

        public void onDestroy(Intent intent) {
            synchronized (RemoteViewsService.sLock) {
                Intent.FilterComparison filterComparison = new Intent.FilterComparison(intent);
                if (RemoteViewsService.sRemoteViewFactories.containsKey(filterComparison)) {
                    try {
                        ((RemoteViewsFactory) RemoteViewsService.sRemoteViewFactories.get(filterComparison)).onDestroy();
                    } catch (Exception e) {
                        Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                    }
                    RemoteViewsService.sRemoteViewFactories.remove(filterComparison);
                }
            }
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        RemoteViewsFactory remoteViewsFactoryOnGetViewFactory;
        boolean z;
        IRemoteViewsFactory.Stub remoteViewsFactoryAdapter;
        synchronized (sLock) {
            Intent.FilterComparison filterComparison = new Intent.FilterComparison(intent);
            HashMap<Intent.FilterComparison, RemoteViewsFactory> map = sRemoteViewFactories;
            if (!map.containsKey(filterComparison)) {
                remoteViewsFactoryOnGetViewFactory = onGetViewFactory(intent);
                map.put(filterComparison, remoteViewsFactoryOnGetViewFactory);
                remoteViewsFactoryOnGetViewFactory.onCreate();
                z = false;
            } else {
                remoteViewsFactoryOnGetViewFactory = map.get(filterComparison);
                z = true;
            }
            remoteViewsFactoryAdapter = new RemoteViewsFactoryAdapter(remoteViewsFactoryOnGetViewFactory, z);
        }
        return remoteViewsFactoryAdapter;
    }
}
