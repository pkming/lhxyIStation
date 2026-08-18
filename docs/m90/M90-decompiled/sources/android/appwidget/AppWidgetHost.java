package android.appwidget;

import android.content.Context;
import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;
import com.android.internal.appwidget.IAppWidgetHost;
import com.android.internal.appwidget.IAppWidgetService;
import java.util.ArrayList;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class AppWidgetHost {
    static final int HANDLE_PROVIDERS_CHANGED = 3;
    static final int HANDLE_PROVIDER_CHANGED = 2;
    static final int HANDLE_UPDATE = 1;
    static final int HANDLE_VIEW_DATA_CHANGED = 4;
    static IAppWidgetService sService;
    static final Object sServiceLock = new Object();
    Callbacks mCallbacks;
    Context mContext;
    private DisplayMetrics mDisplayMetrics;
    Handler mHandler;
    int mHostId;
    private RemoteViews.OnClickHandler mOnClickHandler;
    String mPackageName;
    final HashMap<Integer, AppWidgetHostView> mViews;

    protected void onProvidersChanged() {
    }

    class Callbacks extends IAppWidgetHost.Stub {
        Callbacks() {
        }

        public void updateAppWidget(int i, RemoteViews remoteViews, int i2) {
            if (AppWidgetHost.this.isLocalBinder() && remoteViews != null) {
                remoteViews = remoteViews.m22clone();
                remoteViews.setUser(new UserHandle(i2));
            }
            AppWidgetHost.this.mHandler.obtainMessage(1, i, i2, remoteViews).sendToTarget();
        }

        public void providerChanged(int i, AppWidgetProviderInfo appWidgetProviderInfo, int i2) {
            if (AppWidgetHost.this.isLocalBinder() && appWidgetProviderInfo != null) {
                appWidgetProviderInfo = appWidgetProviderInfo.m7clone();
            }
            AppWidgetHost.this.mHandler.obtainMessage(2, i, i2, appWidgetProviderInfo).sendToTarget();
        }

        public void providersChanged(int i) {
            AppWidgetHost.this.mHandler.obtainMessage(3, i, 0).sendToTarget();
        }

        public void viewDataChanged(int i, int i2, int i3) {
            AppWidgetHost.this.mHandler.obtainMessage(4, i, i2, Integer.valueOf(i3)).sendToTarget();
        }
    }

    class UpdateHandler extends Handler {
        public UpdateHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                AppWidgetHost.this.updateAppWidgetView(message.arg1, (RemoteViews) message.obj, message.arg2);
                return;
            }
            if (i == 2) {
                AppWidgetHost.this.onProviderChanged(message.arg1, (AppWidgetProviderInfo) message.obj);
            } else if (i == 3) {
                AppWidgetHost.this.onProvidersChanged();
            } else {
                if (i != 4) {
                    return;
                }
                AppWidgetHost.this.viewDataChanged(message.arg1, message.arg2, ((Integer) message.obj).intValue());
            }
        }
    }

    public AppWidgetHost(Context context, int i) {
        this(context, i, null, context.getMainLooper());
    }

    public AppWidgetHost(Context context, int i, RemoteViews.OnClickHandler onClickHandler, Looper looper) {
        this.mCallbacks = new Callbacks();
        this.mViews = new HashMap<>();
        this.mContext = context;
        this.mHostId = i;
        this.mOnClickHandler = onClickHandler;
        this.mHandler = new UpdateHandler(looper);
        this.mDisplayMetrics = context.getResources().getDisplayMetrics();
        bindService();
    }

    private static void bindService() {
        synchronized (sServiceLock) {
            if (sService == null) {
                sService = IAppWidgetService.Stub.asInterface(ServiceManager.getService(Context.APPWIDGET_SERVICE));
            }
        }
    }

    public void startListening() {
        ArrayList arrayList = new ArrayList();
        int userId = this.mContext.getUserId();
        try {
            if (this.mPackageName == null) {
                this.mPackageName = this.mContext.getPackageName();
            }
            int[] iArrStartListening = sService.startListening(this.mCallbacks, this.mPackageName, this.mHostId, arrayList, userId);
            int length = iArrStartListening.length;
            for (int i = 0; i < length; i++) {
                if (arrayList.get(i) != null) {
                    ((RemoteViews) arrayList.get(i)).setUser(new UserHandle(userId));
                }
                updateAppWidgetView(iArrStartListening[i], (RemoteViews) arrayList.get(i), userId);
            }
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void stopListening() {
        try {
            sService.stopListening(this.mHostId, this.mContext.getUserId());
            clearViews();
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public int allocateAppWidgetId() {
        try {
            if (this.mPackageName == null) {
                this.mPackageName = this.mContext.getPackageName();
            }
            return sService.allocateAppWidgetId(this.mPackageName, this.mHostId, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public static int allocateAppWidgetIdForPackage(int i, int i2, String str) {
        checkCallerIsSystem();
        try {
            if (sService == null) {
                bindService();
            }
            return sService.allocateAppWidgetId(str, i, i2);
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public int[] getAppWidgetIds() {
        try {
            if (sService == null) {
                bindService();
            }
            return sService.getAppWidgetIdsForHost(this.mHostId, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    private static void checkCallerIsSystem() {
        int iMyUid = Process.myUid();
        if (UserHandle.getAppId(iMyUid) != 1000 && iMyUid != 0) {
            throw new SecurityException("Disallowed call for uid " + iMyUid);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isLocalBinder() {
        return Process.myPid() == Binder.getCallingPid();
    }

    public void deleteAppWidgetId(int i) {
        synchronized (this.mViews) {
            this.mViews.remove(Integer.valueOf(i));
            try {
                sService.deleteAppWidgetId(i, this.mContext.getUserId());
            } catch (RemoteException e) {
                throw new RuntimeException("system server dead?", e);
            }
        }
    }

    public static void deleteAppWidgetIdForSystem(int i, int i2) {
        checkCallerIsSystem();
        try {
            if (sService == null) {
                bindService();
            }
            sService.deleteAppWidgetId(i, i2);
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void deleteHost() {
        try {
            sService.deleteHost(this.mHostId, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public static void deleteAllHosts() {
        deleteAllHosts(UserHandle.myUserId());
    }

    public static void deleteAllHosts(int i) {
        try {
            sService.deleteAllHosts(i);
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public final AppWidgetHostView createView(Context context, int i, AppWidgetProviderInfo appWidgetProviderInfo) {
        int userId = this.mContext.getUserId();
        AppWidgetHostView appWidgetHostViewOnCreateView = onCreateView(this.mContext, i, appWidgetProviderInfo);
        appWidgetHostViewOnCreateView.setUserId(userId);
        appWidgetHostViewOnCreateView.setOnClickHandler(this.mOnClickHandler);
        appWidgetHostViewOnCreateView.setAppWidget(i, appWidgetProviderInfo);
        synchronized (this.mViews) {
            this.mViews.put(Integer.valueOf(i), appWidgetHostViewOnCreateView);
        }
        try {
            RemoteViews appWidgetViews = sService.getAppWidgetViews(i, userId);
            if (appWidgetViews != null) {
                appWidgetViews.setUser(new UserHandle(this.mContext.getUserId()));
            }
            appWidgetHostViewOnCreateView.updateAppWidget(appWidgetViews);
            return appWidgetHostViewOnCreateView;
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    protected AppWidgetHostView onCreateView(Context context, int i, AppWidgetProviderInfo appWidgetProviderInfo) {
        return new AppWidgetHostView(context, this.mOnClickHandler);
    }

    protected void onProviderChanged(int i, AppWidgetProviderInfo appWidgetProviderInfo) {
        AppWidgetHostView appWidgetHostView;
        appWidgetProviderInfo.minWidth = TypedValue.complexToDimensionPixelSize(appWidgetProviderInfo.minWidth, this.mDisplayMetrics);
        appWidgetProviderInfo.minHeight = TypedValue.complexToDimensionPixelSize(appWidgetProviderInfo.minHeight, this.mDisplayMetrics);
        appWidgetProviderInfo.minResizeWidth = TypedValue.complexToDimensionPixelSize(appWidgetProviderInfo.minResizeWidth, this.mDisplayMetrics);
        appWidgetProviderInfo.minResizeHeight = TypedValue.complexToDimensionPixelSize(appWidgetProviderInfo.minResizeHeight, this.mDisplayMetrics);
        synchronized (this.mViews) {
            appWidgetHostView = this.mViews.get(Integer.valueOf(i));
        }
        if (appWidgetHostView != null) {
            appWidgetHostView.resetAppWidget(appWidgetProviderInfo);
        }
    }

    void updateAppWidgetView(int i, RemoteViews remoteViews, int i2) {
        AppWidgetHostView appWidgetHostView;
        synchronized (this.mViews) {
            appWidgetHostView = this.mViews.get(Integer.valueOf(i));
        }
        if (appWidgetHostView != null) {
            appWidgetHostView.updateAppWidget(remoteViews);
        }
    }

    void viewDataChanged(int i, int i2, int i3) {
        AppWidgetHostView appWidgetHostView;
        synchronized (this.mViews) {
            appWidgetHostView = this.mViews.get(Integer.valueOf(i));
        }
        if (appWidgetHostView != null) {
            appWidgetHostView.viewDataChanged(i2);
        }
    }

    protected void clearViews() {
        this.mViews.clear();
    }
}
