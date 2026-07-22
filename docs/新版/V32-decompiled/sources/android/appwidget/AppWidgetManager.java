package android.appwidget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;
import com.android.internal.appwidget.IAppWidgetService;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.WeakHashMap;

/* JADX INFO: loaded from: classes.dex */
public class AppWidgetManager {
    public static final String ACTION_APPWIDGET_BIND = "android.appwidget.action.APPWIDGET_BIND";
    public static final String ACTION_APPWIDGET_CONFIGURE = "android.appwidget.action.APPWIDGET_CONFIGURE";
    public static final String ACTION_APPWIDGET_DELETED = "android.appwidget.action.APPWIDGET_DELETED";
    public static final String ACTION_APPWIDGET_DISABLED = "android.appwidget.action.APPWIDGET_DISABLED";
    public static final String ACTION_APPWIDGET_ENABLED = "android.appwidget.action.APPWIDGET_ENABLED";
    public static final String ACTION_APPWIDGET_OPTIONS_CHANGED = "android.appwidget.action.APPWIDGET_UPDATE_OPTIONS";
    public static final String ACTION_APPWIDGET_PICK = "android.appwidget.action.APPWIDGET_PICK";
    public static final String ACTION_APPWIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE";
    public static final String ACTION_KEYGUARD_APPWIDGET_PICK = "android.appwidget.action.KEYGUARD_APPWIDGET_PICK";
    public static final String EXTRA_APPWIDGET_ID = "appWidgetId";
    public static final String EXTRA_APPWIDGET_IDS = "appWidgetIds";
    public static final String EXTRA_APPWIDGET_OPTIONS = "appWidgetOptions";
    public static final String EXTRA_APPWIDGET_PROVIDER = "appWidgetProvider";
    public static final String EXTRA_CATEGORY_FILTER = "categoryFilter";
    public static final String EXTRA_CUSTOM_EXTRAS = "customExtras";
    public static final String EXTRA_CUSTOM_INFO = "customInfo";
    public static final String EXTRA_CUSTOM_SORT = "customSort";
    public static final int INVALID_APPWIDGET_ID = 0;
    public static final String META_DATA_APPWIDGET_PROVIDER = "android.appwidget.provider";
    public static final String OPTION_APPWIDGET_HOST_CATEGORY = "appWidgetCategory";
    public static final String OPTION_APPWIDGET_MAX_HEIGHT = "appWidgetMaxHeight";
    public static final String OPTION_APPWIDGET_MAX_WIDTH = "appWidgetMaxWidth";
    public static final String OPTION_APPWIDGET_MIN_HEIGHT = "appWidgetMinHeight";
    public static final String OPTION_APPWIDGET_MIN_WIDTH = "appWidgetMinWidth";
    static final String TAG = "AppWidgetManager";
    static WeakHashMap<Context, WeakReference<AppWidgetManager>> sManagerCache = new WeakHashMap<>();
    static IAppWidgetService sService;
    Context mContext;
    private DisplayMetrics mDisplayMetrics;

    public static AppWidgetManager getInstance(Context context) {
        AppWidgetManager appWidgetManager;
        synchronized (sManagerCache) {
            if (sService == null) {
                sService = IAppWidgetService.Stub.asInterface(ServiceManager.getService(Context.APPWIDGET_SERVICE));
            }
            WeakReference<AppWidgetManager> weakReference = sManagerCache.get(context);
            appWidgetManager = weakReference != null ? weakReference.get() : null;
            if (appWidgetManager == null) {
                appWidgetManager = new AppWidgetManager(context);
                sManagerCache.put(context, new WeakReference<>(appWidgetManager));
            }
        }
        return appWidgetManager;
    }

    private AppWidgetManager(Context context) {
        this.mContext = context;
        this.mDisplayMetrics = context.getResources().getDisplayMetrics();
    }

    public void updateAppWidget(int[] iArr, RemoteViews remoteViews) {
        try {
            sService.updateAppWidgetIds(iArr, remoteViews, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void updateAppWidgetOptions(int i, Bundle bundle) {
        try {
            sService.updateAppWidgetOptions(i, bundle, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public Bundle getAppWidgetOptions(int i) {
        try {
            return sService.getAppWidgetOptions(i, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void updateAppWidget(int i, RemoteViews remoteViews) {
        updateAppWidget(new int[]{i}, remoteViews);
    }

    public void partiallyUpdateAppWidget(int[] iArr, RemoteViews remoteViews) {
        try {
            sService.partiallyUpdateAppWidgetIds(iArr, remoteViews, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void partiallyUpdateAppWidget(int i, RemoteViews remoteViews) {
        partiallyUpdateAppWidget(new int[]{i}, remoteViews);
    }

    public void updateAppWidget(ComponentName componentName, RemoteViews remoteViews) {
        try {
            sService.updateAppWidgetProvider(componentName, remoteViews, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void notifyAppWidgetViewDataChanged(int[] iArr, int i) {
        try {
            sService.notifyAppWidgetViewDataChanged(iArr, i, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void notifyAppWidgetViewDataChanged(int i, int i2) {
        notifyAppWidgetViewDataChanged(new int[]{i}, i2);
    }

    public List<AppWidgetProviderInfo> getInstalledProviders() {
        return getInstalledProviders(1);
    }

    public List<AppWidgetProviderInfo> getInstalledProviders(int i) {
        try {
            List<AppWidgetProviderInfo> installedProviders = sService.getInstalledProviders(i, this.mContext.getUserId());
            for (AppWidgetProviderInfo appWidgetProviderInfo : installedProviders) {
                appWidgetProviderInfo.minWidth = TypedValue.complexToDimensionPixelSize(appWidgetProviderInfo.minWidth, this.mDisplayMetrics);
                appWidgetProviderInfo.minHeight = TypedValue.complexToDimensionPixelSize(appWidgetProviderInfo.minHeight, this.mDisplayMetrics);
                appWidgetProviderInfo.minResizeWidth = TypedValue.complexToDimensionPixelSize(appWidgetProviderInfo.minResizeWidth, this.mDisplayMetrics);
                appWidgetProviderInfo.minResizeHeight = TypedValue.complexToDimensionPixelSize(appWidgetProviderInfo.minResizeHeight, this.mDisplayMetrics);
            }
            return installedProviders;
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public AppWidgetProviderInfo getAppWidgetInfo(int i) {
        try {
            AppWidgetProviderInfo appWidgetInfo = sService.getAppWidgetInfo(i, this.mContext.getUserId());
            if (appWidgetInfo != null) {
                appWidgetInfo.minWidth = TypedValue.complexToDimensionPixelSize(appWidgetInfo.minWidth, this.mDisplayMetrics);
                appWidgetInfo.minHeight = TypedValue.complexToDimensionPixelSize(appWidgetInfo.minHeight, this.mDisplayMetrics);
                appWidgetInfo.minResizeWidth = TypedValue.complexToDimensionPixelSize(appWidgetInfo.minResizeWidth, this.mDisplayMetrics);
                appWidgetInfo.minResizeHeight = TypedValue.complexToDimensionPixelSize(appWidgetInfo.minResizeHeight, this.mDisplayMetrics);
            }
            return appWidgetInfo;
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void bindAppWidgetId(int i, ComponentName componentName) {
        try {
            sService.bindAppWidgetId(i, componentName, (Bundle) null, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void bindAppWidgetId(int i, ComponentName componentName, Bundle bundle) {
        try {
            sService.bindAppWidgetId(i, componentName, bundle, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public boolean bindAppWidgetIdIfAllowed(int i, ComponentName componentName) {
        Context context = this.mContext;
        if (context == null) {
            return false;
        }
        try {
            return sService.bindAppWidgetIdIfAllowed(context.getPackageName(), i, componentName, (Bundle) null, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public boolean bindAppWidgetIdIfAllowed(int i, ComponentName componentName, Bundle bundle) {
        Context context = this.mContext;
        if (context == null) {
            return false;
        }
        try {
            return sService.bindAppWidgetIdIfAllowed(context.getPackageName(), i, componentName, bundle, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public boolean hasBindAppWidgetPermission(String str) {
        try {
            return sService.hasBindAppWidgetPermission(str, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void setBindAppWidgetPermission(String str, boolean z) {
        try {
            sService.setBindAppWidgetPermission(str, z, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void bindRemoteViewsService(int i, Intent intent, IBinder iBinder, UserHandle userHandle) {
        try {
            sService.bindRemoteViewsService(i, intent, iBinder, userHandle.getIdentifier());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void unbindRemoteViewsService(int i, Intent intent, UserHandle userHandle) {
        try {
            sService.unbindRemoteViewsService(i, intent, userHandle.getIdentifier());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public int[] getAppWidgetIds(ComponentName componentName) {
        try {
            return sService.getAppWidgetIds(componentName, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }
}
