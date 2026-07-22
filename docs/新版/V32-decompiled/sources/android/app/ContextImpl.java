package android.app;

import android.R;
import android.accounts.AccountManager;
import android.accounts.IAccountManager;
import android.app.IAlarmManager;
import android.app.LoadedApk;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.IContentProvider;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.ConsumerIrManager;
import android.hardware.ISerialManager;
import android.hardware.SerialManager;
import android.hardware.SystemSensorManager;
import android.hardware.camera2.CameraManager;
import android.hardware.display.DisplayManager;
import android.hardware.input.InputManager;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.UsbManager;
import android.location.CountryDetector;
import android.location.ICountryDetector;
import android.location.ILocationManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaRouter;
import android.net.ConnectivityManager;
import android.net.IConnectivityManager;
import android.net.INetworkPolicyManager;
import android.net.NetworkPolicyManager;
import android.net.Uri;
import android.net.ethernet.EthernetManager;
import android.net.ethernet.IEthernetManager;
import android.net.nsd.INsdManager;
import android.net.nsd.NsdManager;
import android.net.wifi.IWifiManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.IWifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.nfc.NfcManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.DropBoxManager;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBinder;
import android.os.IPowerManager;
import android.os.IUserManager;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemVibrator;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.IMountService;
import android.os.storage.StorageManager;
import android.print.IPrintManager;
import android.print.PrintManager;
import android.telephony.TelephonyManager;
import android.util.AndroidRuntimeException;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.DisplayAdjustments;
import android.view.WindowManagerImpl;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.CaptioningManager;
import android.view.inputmethod.InputMethodManager;
import android.view.textservice.TextServicesManager;
import com.android.internal.app.IAppOpsService;
import com.android.internal.os.IDropBoxManagerService;
import com.android.internal.policy.PolicyManager;
import com.android.internal.util.Preconditions;
import com.karaokeimpl.kMicphone;
import com.karaokeimpl.kRTSoundEffects;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
class ContextImpl extends Context {
    private static final boolean DEBUG = false;
    private static final String TAG = "ContextImpl";
    private static ArrayMap<String, ArrayMap<String, SharedPreferencesImpl>> sSharedPrefs;
    private IBinder mActivityToken;
    private String mBasePackageName;
    private File mCacheDir;
    private ApplicationContentResolver mContentResolver;
    private File mDatabasesDir;
    private Display mDisplay;
    private final DisplayAdjustments mDisplayAdjustments;
    private File[] mExternalCacheDirs;
    private File[] mExternalFilesDirs;
    private File[] mExternalObbDirs;
    private File mFilesDir;
    ActivityThread mMainThread;
    private String mOpPackageName;
    private Context mOuterContext;
    LoadedApk mPackageInfo;
    private PackageManager mPackageManager;
    private File mPreferencesDir;
    private Context mReceiverRestrictedContext;
    private Resources mResources;
    private ResourcesManager mResourcesManager;
    private boolean mRestricted;
    final ArrayList<Object> mServiceCache;
    private final Object mSync;
    private Resources.Theme mTheme;
    private int mThemeResource;
    private UserHandle mUser;
    private static final String[] EMPTY_FILE_LIST = new String[0];
    private static final HashMap<String, ServiceFetcher> SYSTEM_SERVICE_MAP = new HashMap<>();
    private static int sNextPerContextServiceCacheIndex = 0;
    private static ServiceFetcher WALLPAPER_FETCHER = new ServiceFetcher() { // from class: android.app.ContextImpl.1
        @Override // android.app.ContextImpl.ServiceFetcher
        public Object createService(ContextImpl contextImpl) {
            return new WallpaperManager(contextImpl.getOuterContext(), contextImpl.mMainThread.getHandler());
        }
    };

    static {
        registerService(Context.ACCESSIBILITY_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.2
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object getService(ContextImpl contextImpl) {
                return AccessibilityManager.getInstance(contextImpl);
            }
        });
        registerService(Context.CAPTIONING_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.3
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object getService(ContextImpl contextImpl) {
                return new CaptioningManager(contextImpl);
            }
        });
        registerService("account", new ServiceFetcher() { // from class: android.app.ContextImpl.4
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new AccountManager(contextImpl, IAccountManager.Stub.asInterface(ServiceManager.getService("account")));
            }
        });
        registerService(Context.ACTIVITY_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.5
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new ActivityManager(contextImpl.getOuterContext(), contextImpl.mMainThread.getHandler());
            }
        });
        registerService("alarm", new ServiceFetcher() { // from class: android.app.ContextImpl.6
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new AlarmManager(IAlarmManager.Stub.asInterface(ServiceManager.getService("alarm")), contextImpl);
            }
        });
        registerService(Context.AUDIO_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.7
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new AudioManager(contextImpl);
            }
        });
        registerService(Context.MEDIA_ROUTER_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.8
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new MediaRouter(contextImpl);
            }
        });
        registerService("bluetooth", new ServiceFetcher() { // from class: android.app.ContextImpl.9
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new BluetoothManager(contextImpl);
            }
        });
        registerService(Context.CLIPBOARD_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.10
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new ClipboardManager(contextImpl.getOuterContext(), contextImpl.mMainThread.getHandler());
            }
        });
        registerService(Context.CONNECTIVITY_SERVICE, new StaticServiceFetcher() { // from class: android.app.ContextImpl.11
            @Override // android.app.ContextImpl.StaticServiceFetcher
            public Object createStaticService() {
                return new ConnectivityManager(IConnectivityManager.Stub.asInterface(ServiceManager.getService(Context.CONNECTIVITY_SERVICE)));
            }
        });
        registerService(Context.COUNTRY_DETECTOR, new StaticServiceFetcher() { // from class: android.app.ContextImpl.12
            @Override // android.app.ContextImpl.StaticServiceFetcher
            public Object createStaticService() {
                return new CountryDetector(ICountryDetector.Stub.asInterface(ServiceManager.getService(Context.COUNTRY_DETECTOR)));
            }
        });
        registerService(Context.DEVICE_POLICY_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.13
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return DevicePolicyManager.create(contextImpl, contextImpl.mMainThread.getHandler());
            }
        });
        registerService(Context.DOWNLOAD_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.14
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new DownloadManager(contextImpl.getContentResolver(), contextImpl.getPackageName());
            }
        });
        registerService("nfc", new ServiceFetcher() { // from class: android.app.ContextImpl.15
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new NfcManager(contextImpl);
            }
        });
        registerService(Context.DROPBOX_SERVICE, new StaticServiceFetcher() { // from class: android.app.ContextImpl.16
            @Override // android.app.ContextImpl.StaticServiceFetcher
            public Object createStaticService() {
                return ContextImpl.createDropBoxManager();
            }
        });
        registerService(Context.INPUT_SERVICE, new StaticServiceFetcher() { // from class: android.app.ContextImpl.17
            @Override // android.app.ContextImpl.StaticServiceFetcher
            public Object createStaticService() {
                return InputManager.getInstance();
            }
        });
        registerService(Context.DISPLAY_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.18
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new DisplayManager(contextImpl.getOuterContext());
            }
        });
        registerService(Context.INPUT_METHOD_SERVICE, new StaticServiceFetcher() { // from class: android.app.ContextImpl.19
            @Override // android.app.ContextImpl.StaticServiceFetcher
            public Object createStaticService() {
                return InputMethodManager.getInstance();
            }
        });
        registerService(Context.TEXT_SERVICES_MANAGER_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.20
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return TextServicesManager.getInstance();
            }
        });
        registerService(Context.KEYGUARD_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.21
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object getService(ContextImpl contextImpl) {
                return new KeyguardManager();
            }
        });
        registerService(Context.LAYOUT_INFLATER_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.22
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return PolicyManager.makeNewLayoutInflater(contextImpl.getOuterContext());
            }
        });
        registerService("location", new ServiceFetcher() { // from class: android.app.ContextImpl.23
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new LocationManager(contextImpl, ILocationManager.Stub.asInterface(ServiceManager.getService("location")));
            }
        });
        registerService(Context.NETWORK_POLICY_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.24
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new NetworkPolicyManager(INetworkPolicyManager.Stub.asInterface(ServiceManager.getService(Context.NETWORK_POLICY_SERVICE)));
            }
        });
        registerService(Context.NOTIFICATION_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.25
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                Context outerContext = contextImpl.getOuterContext();
                return new NotificationManager(new ContextThemeWrapper(outerContext, Resources.selectSystemTheme(0, outerContext.getApplicationInfo().targetSdkVersion, R.style.Theme_Dialog, R.style.Theme_Holo_Dialog, R.style.Theme_DeviceDefault_Dialog)), contextImpl.mMainThread.getHandler());
            }
        });
        registerService(Context.NSD_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.26
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new NsdManager(contextImpl.getOuterContext(), INsdManager.Stub.asInterface(ServiceManager.getService(Context.NSD_SERVICE)));
            }
        });
        registerService(Context.POWER_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.27
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new PowerManager(contextImpl.getOuterContext(), IPowerManager.Stub.asInterface(ServiceManager.getService(Context.POWER_SERVICE)), contextImpl.mMainThread.getHandler());
            }
        });
        registerService("search", new ServiceFetcher() { // from class: android.app.ContextImpl.28
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new SearchManager(contextImpl.getOuterContext(), contextImpl.mMainThread.getHandler());
            }
        });
        registerService(Context.SENSOR_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.29
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new SystemSensorManager(contextImpl.getOuterContext(), contextImpl.mMainThread.getHandler().getLooper());
            }
        });
        registerService(Context.STATUS_BAR_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.30
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new StatusBarManager(contextImpl.getOuterContext());
            }
        });
        registerService(Context.STORAGE_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.31
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                try {
                    return new StorageManager(contextImpl.getContentResolver(), contextImpl.mMainThread.getHandler().getLooper());
                } catch (RemoteException e) {
                    Log.e(ContextImpl.TAG, "Failed to create StorageManager", e);
                    return null;
                }
            }
        });
        registerService("phone", new ServiceFetcher() { // from class: android.app.ContextImpl.32
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new TelephonyManager(contextImpl.getOuterContext());
            }
        });
        registerService(Context.UI_MODE_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.33
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new UiModeManager();
            }
        });
        registerService(Context.USB_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.34
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new UsbManager(contextImpl, IUsbManager.Stub.asInterface(ServiceManager.getService(Context.USB_SERVICE)));
            }
        });
        registerService(Context.SERIAL_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.35
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new SerialManager(contextImpl, ISerialManager.Stub.asInterface(ServiceManager.getService(Context.SERIAL_SERVICE)));
            }
        });
        registerService(Context.VIBRATOR_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.36
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new SystemVibrator(contextImpl);
            }
        });
        registerService(Context.WALLPAPER_SERVICE, WALLPAPER_FETCHER);
        registerService("wifi", new ServiceFetcher() { // from class: android.app.ContextImpl.37
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new WifiManager(contextImpl.getOuterContext(), IWifiManager.Stub.asInterface(ServiceManager.getService("wifi")));
            }
        });
        registerService(Context.WIFI_P2P_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.38
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new WifiP2pManager(IWifiP2pManager.Stub.asInterface(ServiceManager.getService(Context.WIFI_P2P_SERVICE)));
            }
        });
        registerService(Context.ETHERNET_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.39
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new EthernetManager(contextImpl.getOuterContext(), IEthernetManager.Stub.asInterface(ServiceManager.getService(Context.ETHERNET_SERVICE)));
            }
        });
        registerService("pppoe", new ServiceFetcher() { // from class: android.app.ContextImpl.40
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                try {
                    return Class.forName("android.net.pppoe.PppoeManager").getConstructor(Context.class).newInstance(contextImpl);
                } catch (Exception e) {
                    Log.d(ContextImpl.TAG, "register android.net.pppoe.PppoeManager fail.  ms(" + e.getMessage() + ")");
                    return null;
                }
            }
        });
        registerService(Context.WINDOW_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.41
            Display mDefaultDisplay;

            @Override // android.app.ContextImpl.ServiceFetcher
            public Object getService(ContextImpl contextImpl) {
                Display display = contextImpl.mDisplay;
                if (display == null) {
                    if (this.mDefaultDisplay == null) {
                        this.mDefaultDisplay = ((DisplayManager) contextImpl.getOuterContext().getSystemService(Context.DISPLAY_SERVICE)).getDisplay(0);
                    }
                    display = this.mDefaultDisplay;
                }
                return new WindowManagerImpl(display);
            }
        });
        registerService(Context.USER_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.42
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new UserManager(contextImpl, IUserManager.Stub.asInterface(ServiceManager.getService(Context.USER_SERVICE)));
            }
        });
        registerService(Context.APP_OPS_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.43
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new AppOpsManager(contextImpl, IAppOpsService.Stub.asInterface(ServiceManager.getService(Context.APP_OPS_SERVICE)));
            }
        });
        registerService(Context.CAMERA_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.44
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new CameraManager(contextImpl);
            }
        });
        registerService(Context.PRINT_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.45
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new PrintManager(contextImpl.getOuterContext(), IPrintManager.Stub.asInterface(ServiceManager.getService(Context.PRINT_SERVICE)), UserHandle.myUserId(), UserHandle.getAppId(Process.myUid()));
            }
        });
        registerService(Context.CONSUMER_IR_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.46
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new ConsumerIrManager(contextImpl);
            }
        });
        registerService(Context.DISPLAY_MANAGER_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.47
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                try {
                    return Class.forName("android.os.display.DisplayManager").getConstructor(Context.class).newInstance(contextImpl);
                } catch (Exception e) {
                    Log.d(ContextImpl.TAG, "register android.os.display.DisplayManager fail.  ms(" + e.getMessage() + ")");
                    return null;
                }
            }
        });
        registerService(Context.KARAOKE_MICPHONE_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.48
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new kMicphone(contextImpl);
            }
        });
        registerService(Context.KARAOKE_RTSOUNDEFFECTS_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.49
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl contextImpl) {
                return new kRTSoundEffects(contextImpl);
            }
        });
    }

    static class ServiceFetcher {
        int mContextCacheIndex = -1;

        ServiceFetcher() {
        }

        public Object getService(ContextImpl contextImpl) {
            ArrayList<Object> arrayList = contextImpl.mServiceCache;
            synchronized (arrayList) {
                if (arrayList.size() == 0) {
                    for (int i = 0; i < ContextImpl.sNextPerContextServiceCacheIndex; i++) {
                        arrayList.add(null);
                    }
                } else {
                    Object obj = arrayList.get(this.mContextCacheIndex);
                    if (obj != null) {
                        return obj;
                    }
                }
                Object objCreateService = createService(contextImpl);
                arrayList.set(this.mContextCacheIndex, objCreateService);
                return objCreateService;
            }
        }

        public Object createService(ContextImpl contextImpl) {
            throw new RuntimeException("Not implemented");
        }
    }

    static abstract class StaticServiceFetcher extends ServiceFetcher {
        private Object mCachedInstance;

        public abstract Object createStaticService();

        StaticServiceFetcher() {
        }

        @Override // android.app.ContextImpl.ServiceFetcher
        public final Object getService(ContextImpl contextImpl) {
            synchronized (this) {
                Object obj = this.mCachedInstance;
                if (obj != null) {
                    return obj;
                }
                Object objCreateStaticService = createStaticService();
                this.mCachedInstance = objCreateStaticService;
                return objCreateStaticService;
            }
        }
    }

    private static void registerService(String str, ServiceFetcher serviceFetcher) {
        if (!(serviceFetcher instanceof StaticServiceFetcher)) {
            int i = sNextPerContextServiceCacheIndex;
            sNextPerContextServiceCacheIndex = i + 1;
            serviceFetcher.mContextCacheIndex = i;
        }
        SYSTEM_SERVICE_MAP.put(str, serviceFetcher);
    }

    static ContextImpl getImpl(Context context) {
        Context baseContext;
        while ((context instanceof ContextWrapper) && (baseContext = ((ContextWrapper) context).getBaseContext()) != null) {
            context = baseContext;
        }
        return (ContextImpl) context;
    }

    @Override // android.content.Context
    public AssetManager getAssets() {
        return getResources().getAssets();
    }

    @Override // android.content.Context
    public Resources getResources() {
        return this.mResources;
    }

    @Override // android.content.Context
    public PackageManager getPackageManager() {
        PackageManager packageManager = this.mPackageManager;
        if (packageManager != null) {
            return packageManager;
        }
        IPackageManager packageManager2 = ActivityThread.getPackageManager();
        if (packageManager2 == null) {
            return null;
        }
        ApplicationPackageManager applicationPackageManager = new ApplicationPackageManager(this, packageManager2);
        this.mPackageManager = applicationPackageManager;
        return applicationPackageManager;
    }

    @Override // android.content.Context
    public ContentResolver getContentResolver() {
        return this.mContentResolver;
    }

    @Override // android.content.Context
    public Looper getMainLooper() {
        return this.mMainThread.getLooper();
    }

    @Override // android.content.Context
    public Context getApplicationContext() {
        LoadedApk loadedApk = this.mPackageInfo;
        return loadedApk != null ? loadedApk.getApplication() : this.mMainThread.getApplication();
    }

    @Override // android.content.Context
    public void setTheme(int i) {
        this.mThemeResource = i;
    }

    @Override // android.content.Context
    public int getThemeResId() {
        return this.mThemeResource;
    }

    @Override // android.content.Context
    public Resources.Theme getTheme() {
        if (this.mTheme == null) {
            this.mThemeResource = Resources.selectDefaultTheme(this.mThemeResource, getOuterContext().getApplicationInfo().targetSdkVersion);
            Resources.Theme themeNewTheme = this.mResources.newTheme();
            this.mTheme = themeNewTheme;
            themeNewTheme.applyStyle(this.mThemeResource, true);
        }
        return this.mTheme;
    }

    @Override // android.content.Context
    public ClassLoader getClassLoader() {
        LoadedApk loadedApk = this.mPackageInfo;
        return loadedApk != null ? loadedApk.getClassLoader() : ClassLoader.getSystemClassLoader();
    }

    @Override // android.content.Context
    public String getPackageName() {
        LoadedApk loadedApk = this.mPackageInfo;
        return loadedApk != null ? loadedApk.getPackageName() : "android";
    }

    @Override // android.content.Context
    public String getBasePackageName() {
        String str = this.mBasePackageName;
        return str != null ? str : getPackageName();
    }

    @Override // android.content.Context
    public String getOpPackageName() {
        String str = this.mOpPackageName;
        return str != null ? str : getBasePackageName();
    }

    @Override // android.content.Context
    public ApplicationInfo getApplicationInfo() {
        LoadedApk loadedApk = this.mPackageInfo;
        if (loadedApk != null) {
            return loadedApk.getApplicationInfo();
        }
        throw new RuntimeException("Not supported in system context");
    }

    @Override // android.content.Context
    public String getPackageResourcePath() {
        LoadedApk loadedApk = this.mPackageInfo;
        if (loadedApk != null) {
            return loadedApk.getResDir();
        }
        throw new RuntimeException("Not supported in system context");
    }

    @Override // android.content.Context
    public String getPackageCodePath() {
        LoadedApk loadedApk = this.mPackageInfo;
        if (loadedApk != null) {
            return loadedApk.getAppDir();
        }
        throw new RuntimeException("Not supported in system context");
    }

    @Override // android.content.Context
    public File getSharedPrefsFile(String str) {
        return makeFilename(getPreferencesDir(), str + ".xml");
    }

    @Override // android.content.Context
    public SharedPreferences getSharedPreferences(String str, int i) {
        synchronized (ContextImpl.class) {
            if (sSharedPrefs == null) {
                sSharedPrefs = new ArrayMap<>();
            }
            String packageName = getPackageName();
            ArrayMap<String, SharedPreferencesImpl> arrayMap = sSharedPrefs.get(packageName);
            if (arrayMap == null) {
                arrayMap = new ArrayMap<>();
                sSharedPrefs.put(packageName, arrayMap);
            }
            if (this.mPackageInfo.getApplicationInfo().targetSdkVersion < 19 && str == null) {
                str = "null";
            }
            SharedPreferencesImpl sharedPreferencesImpl = arrayMap.get(str);
            if (sharedPreferencesImpl == null) {
                SharedPreferencesImpl sharedPreferencesImpl2 = new SharedPreferencesImpl(getSharedPrefsFile(str), i);
                arrayMap.put(str, sharedPreferencesImpl2);
                return sharedPreferencesImpl2;
            }
            if ((i & 4) != 0 || getApplicationInfo().targetSdkVersion < 11) {
                sharedPreferencesImpl.startReloadIfChangedUnexpectedly();
            }
            return sharedPreferencesImpl;
        }
    }

    private File getPreferencesDir() {
        File file;
        synchronized (this.mSync) {
            if (this.mPreferencesDir == null) {
                this.mPreferencesDir = new File(getDataDirFile(), "shared_prefs");
            }
            file = this.mPreferencesDir;
        }
        return file;
    }

    @Override // android.content.Context
    public FileInputStream openFileInput(String str) throws FileNotFoundException {
        return new FileInputStream(makeFilename(getFilesDir(), str));
    }

    @Override // android.content.Context
    public FileOutputStream openFileOutput(String str, int i) throws FileNotFoundException {
        boolean z = (32768 & i) != 0;
        File fileMakeFilename = makeFilename(getFilesDir(), str);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileMakeFilename, z);
            setFilePermissionsFromMode(fileMakeFilename.getPath(), i, 0);
            return fileOutputStream;
        } catch (FileNotFoundException unused) {
            File parentFile = fileMakeFilename.getParentFile();
            parentFile.mkdir();
            FileUtils.setPermissions(parentFile.getPath(), 505, -1, -1);
            FileOutputStream fileOutputStream2 = new FileOutputStream(fileMakeFilename, z);
            setFilePermissionsFromMode(fileMakeFilename.getPath(), i, 0);
            return fileOutputStream2;
        }
    }

    @Override // android.content.Context
    public boolean deleteFile(String str) {
        return makeFilename(getFilesDir(), str).delete();
    }

    @Override // android.content.Context
    public File getFilesDir() {
        synchronized (this.mSync) {
            if (this.mFilesDir == null) {
                this.mFilesDir = new File(getDataDirFile(), "files");
            }
            if (!this.mFilesDir.exists()) {
                if (!this.mFilesDir.mkdirs()) {
                    if (this.mFilesDir.exists()) {
                        return this.mFilesDir;
                    }
                    Log.w(TAG, "Unable to create files directory " + this.mFilesDir.getPath());
                    return null;
                }
                FileUtils.setPermissions(this.mFilesDir.getPath(), 505, -1, -1);
            }
            return this.mFilesDir;
        }
    }

    @Override // android.content.Context
    public File getExternalFilesDir(String str) {
        return getExternalFilesDirs(str)[0];
    }

    @Override // android.content.Context
    public File[] getExternalFilesDirs(String str) {
        File[] fileArrEnsureDirsExistOrFilter;
        synchronized (this.mSync) {
            if (this.mExternalFilesDirs == null) {
                this.mExternalFilesDirs = Environment.buildExternalStorageAppFilesDirs(getPackageName());
            }
            File[] fileArrBuildPaths = this.mExternalFilesDirs;
            if (str != null) {
                fileArrBuildPaths = Environment.buildPaths(fileArrBuildPaths, str);
            }
            fileArrEnsureDirsExistOrFilter = ensureDirsExistOrFilter(fileArrBuildPaths);
        }
        return fileArrEnsureDirsExistOrFilter;
    }

    @Override // android.content.Context
    public File getObbDir() {
        return getObbDirs()[0];
    }

    @Override // android.content.Context
    public File[] getObbDirs() {
        File[] fileArrEnsureDirsExistOrFilter;
        synchronized (this.mSync) {
            if (this.mExternalObbDirs == null) {
                this.mExternalObbDirs = Environment.buildExternalStorageAppObbDirs(getPackageName());
            }
            fileArrEnsureDirsExistOrFilter = ensureDirsExistOrFilter(this.mExternalObbDirs);
        }
        return fileArrEnsureDirsExistOrFilter;
    }

    @Override // android.content.Context
    public File getCacheDir() {
        synchronized (this.mSync) {
            if (this.mCacheDir == null) {
                this.mCacheDir = new File(getDataDirFile(), "cache");
            }
            if (!this.mCacheDir.exists()) {
                if (!this.mCacheDir.mkdirs()) {
                    if (this.mCacheDir.exists()) {
                        return this.mCacheDir;
                    }
                    Log.w(TAG, "Unable to create cache directory " + this.mCacheDir.getAbsolutePath());
                    return null;
                }
                FileUtils.setPermissions(this.mCacheDir.getPath(), 505, -1, -1);
            }
            return this.mCacheDir;
        }
    }

    @Override // android.content.Context
    public File getExternalCacheDir() {
        return getExternalCacheDirs()[0];
    }

    @Override // android.content.Context
    public File[] getExternalCacheDirs() {
        File[] fileArrEnsureDirsExistOrFilter;
        synchronized (this.mSync) {
            if (this.mExternalCacheDirs == null) {
                this.mExternalCacheDirs = Environment.buildExternalStorageAppCacheDirs(getPackageName());
            }
            fileArrEnsureDirsExistOrFilter = ensureDirsExistOrFilter(this.mExternalCacheDirs);
        }
        return fileArrEnsureDirsExistOrFilter;
    }

    @Override // android.content.Context
    public File getFileStreamPath(String str) {
        return makeFilename(getFilesDir(), str);
    }

    @Override // android.content.Context
    public String[] fileList() {
        String[] list = getFilesDir().list();
        return list != null ? list : EMPTY_FILE_LIST;
    }

    @Override // android.content.Context
    public SQLiteDatabase openOrCreateDatabase(String str, int i, SQLiteDatabase.CursorFactory cursorFactory) {
        return openOrCreateDatabase(str, i, cursorFactory, null);
    }

    @Override // android.content.Context
    public SQLiteDatabase openOrCreateDatabase(String str, int i, SQLiteDatabase.CursorFactory cursorFactory, DatabaseErrorHandler databaseErrorHandler) {
        File fileValidateFilePath = validateFilePath(str, true);
        SQLiteDatabase sQLiteDatabaseOpenDatabase = SQLiteDatabase.openDatabase(fileValidateFilePath.getPath(), cursorFactory, (i & 8) != 0 ? ParcelFileDescriptor.MODE_READ_WRITE : 268435456, databaseErrorHandler);
        setFilePermissionsFromMode(fileValidateFilePath.getPath(), i, 0);
        return sQLiteDatabaseOpenDatabase;
    }

    @Override // android.content.Context
    public boolean deleteDatabase(String str) {
        try {
            return SQLiteDatabase.deleteDatabase(validateFilePath(str, false));
        } catch (Exception unused) {
            return false;
        }
    }

    @Override // android.content.Context
    public File getDatabasePath(String str) {
        return validateFilePath(str, false);
    }

    @Override // android.content.Context
    public String[] databaseList() {
        String[] list = getDatabasesDir().list();
        return list != null ? list : EMPTY_FILE_LIST;
    }

    private File getDatabasesDir() {
        File file;
        synchronized (this.mSync) {
            if (this.mDatabasesDir == null) {
                this.mDatabasesDir = new File(getDataDirFile(), "databases");
            }
            if (this.mDatabasesDir.getPath().equals("databases")) {
                this.mDatabasesDir = new File("/data/system");
            }
            file = this.mDatabasesDir;
        }
        return file;
    }

    @Override // android.content.Context
    public Drawable getWallpaper() {
        return getWallpaperManager().getDrawable();
    }

    @Override // android.content.Context
    public Drawable peekWallpaper() {
        return getWallpaperManager().peekDrawable();
    }

    @Override // android.content.Context
    public int getWallpaperDesiredMinimumWidth() {
        return getWallpaperManager().getDesiredMinimumWidth();
    }

    @Override // android.content.Context
    public int getWallpaperDesiredMinimumHeight() {
        return getWallpaperManager().getDesiredMinimumHeight();
    }

    @Override // android.content.Context
    public void setWallpaper(Bitmap bitmap) throws IOException {
        getWallpaperManager().setBitmap(bitmap);
    }

    @Override // android.content.Context
    public void setWallpaper(InputStream inputStream) throws IOException {
        getWallpaperManager().setStream(inputStream);
    }

    @Override // android.content.Context
    public void clearWallpaper() throws IOException {
        getWallpaperManager().clear();
    }

    @Override // android.content.Context
    public void startActivity(Intent intent) {
        warnIfCallingFromSystemProcess();
        startActivity(intent, null);
    }

    @Override // android.content.Context
    public void startActivityAsUser(Intent intent, UserHandle userHandle) {
        startActivityAsUser(intent, null, userHandle);
    }

    @Override // android.content.Context
    public void startActivity(Intent intent, Bundle bundle) {
        warnIfCallingFromSystemProcess();
        if ((intent.getFlags() & 268435456) == 0) {
            throw new AndroidRuntimeException("Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?");
        }
        this.mMainThread.getInstrumentation().execStartActivity(getOuterContext(), this.mMainThread.getApplicationThread(), (IBinder) null, (Activity) null, intent, -1, bundle);
    }

    @Override // android.content.Context
    public void startActivityAsUser(Intent intent, Bundle bundle, UserHandle userHandle) {
        try {
            try {
                ActivityManagerNative.getDefault().startActivityAsUser(this.mMainThread.getApplicationThread(), getBasePackageName(), intent, intent.resolveTypeIfNeeded(getContentResolver()), null, null, 0, 268435456, null, null, bundle, userHandle.getIdentifier());
            } catch (RemoteException unused) {
            }
        } catch (RemoteException unused2) {
        }
    }

    @Override // android.content.Context
    public void startActivities(Intent[] intentArr) {
        warnIfCallingFromSystemProcess();
        startActivities(intentArr, null);
    }

    @Override // android.content.Context
    public void startActivitiesAsUser(Intent[] intentArr, Bundle bundle, UserHandle userHandle) {
        if ((intentArr[0].getFlags() & 268435456) == 0) {
            throw new AndroidRuntimeException("Calling startActivities() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag on first Intent. Is this really what you want?");
        }
        this.mMainThread.getInstrumentation().execStartActivitiesAsUser(getOuterContext(), this.mMainThread.getApplicationThread(), null, (Activity) null, intentArr, bundle, userHandle.getIdentifier());
    }

    @Override // android.content.Context
    public void startActivities(Intent[] intentArr, Bundle bundle) {
        warnIfCallingFromSystemProcess();
        if ((intentArr[0].getFlags() & 268435456) == 0) {
            throw new AndroidRuntimeException("Calling startActivities() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag on first Intent. Is this really what you want?");
        }
        this.mMainThread.getInstrumentation().execStartActivities(getOuterContext(), this.mMainThread.getApplicationThread(), null, (Activity) null, intentArr, bundle);
    }

    @Override // android.content.Context
    public void startIntentSender(IntentSender intentSender, Intent intent, int i, int i2, int i3) throws IntentSender.SendIntentException {
        startIntentSender(intentSender, intent, i, i2, i3, null);
    }

    @Override // android.content.Context
    public void startIntentSender(IntentSender intentSender, Intent intent, int i, int i2, int i3, Bundle bundle) throws IntentSender.SendIntentException {
        String strResolveTypeIfNeeded;
        if (intent != null) {
            try {
                intent.migrateExtraStreamToClipData();
                intent.prepareToLeaveProcess();
                strResolveTypeIfNeeded = intent.resolveTypeIfNeeded(getContentResolver());
            } catch (RemoteException unused) {
                return;
            }
        } else {
            strResolveTypeIfNeeded = null;
        }
        try {
            int iStartActivityIntentSender = ActivityManagerNative.getDefault().startActivityIntentSender(this.mMainThread.getApplicationThread(), intentSender, intent, strResolveTypeIfNeeded, null, null, 0, i, i2, bundle);
            if (iStartActivityIntentSender == -6) {
                throw new IntentSender.SendIntentException();
            }
            Instrumentation.checkStartActivityResult(iStartActivityIntentSender, null);
        } catch (RemoteException unused2) {
        }
    }

    @Override // android.content.Context
    public void sendBroadcast(Intent intent) {
        warnIfCallingFromSystemProcess();
        String strResolveTypeIfNeeded = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, strResolveTypeIfNeeded, null, -1, null, null, null, -1, false, false, getUserId());
        } catch (RemoteException unused) {
        }
    }

    @Override // android.content.Context
    public void sendBroadcast(Intent intent, String str) {
        warnIfCallingFromSystemProcess();
        String strResolveTypeIfNeeded = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            try {
                ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, strResolveTypeIfNeeded, null, -1, null, null, str, -1, false, false, getUserId());
            } catch (RemoteException unused) {
            }
        } catch (RemoteException unused2) {
        }
    }

    @Override // android.content.Context
    public void sendBroadcast(Intent intent, String str, int i) {
        warnIfCallingFromSystemProcess();
        String strResolveTypeIfNeeded = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            try {
                ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, strResolveTypeIfNeeded, null, -1, null, null, str, i, false, false, getUserId());
            } catch (RemoteException unused) {
            }
        } catch (RemoteException unused2) {
        }
    }

    @Override // android.content.Context
    public void sendOrderedBroadcast(Intent intent, String str) {
        warnIfCallingFromSystemProcess();
        String strResolveTypeIfNeeded = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            try {
                ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, strResolveTypeIfNeeded, null, -1, null, null, str, -1, true, false, getUserId());
            } catch (RemoteException unused) {
            }
        } catch (RemoteException unused2) {
        }
    }

    @Override // android.content.Context
    public void sendOrderedBroadcast(Intent intent, String str, BroadcastReceiver broadcastReceiver, Handler handler, int i, String str2, Bundle bundle) {
        sendOrderedBroadcast(intent, str, -1, broadcastReceiver, handler, i, str2, bundle);
    }

    @Override // android.content.Context
    public void sendOrderedBroadcast(Intent intent, String str, int i, BroadcastReceiver broadcastReceiver, Handler handler, int i2, String str2, Bundle bundle) {
        IIntentReceiver iIntentReceiver;
        warnIfCallingFromSystemProcess();
        if (broadcastReceiver == null) {
            iIntentReceiver = null;
        } else if (this.mPackageInfo != null) {
            iIntentReceiver = this.mPackageInfo.getReceiverDispatcher(broadcastReceiver, getOuterContext(), handler == null ? this.mMainThread.getHandler() : handler, this.mMainThread.getInstrumentation(), false);
        } else {
            iIntentReceiver = new LoadedApk.ReceiverDispatcher(broadcastReceiver, getOuterContext(), handler == null ? this.mMainThread.getHandler() : handler, null, false).getIIntentReceiver();
        }
        IIntentReceiver iIntentReceiver2 = iIntentReceiver;
        String strResolveTypeIfNeeded = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, strResolveTypeIfNeeded, iIntentReceiver2, i2, str2, bundle, str, i, true, false, getUserId());
        } catch (RemoteException unused) {
        }
    }

    @Override // android.content.Context
    public void sendBroadcastAsUser(Intent intent, UserHandle userHandle) {
        String strResolveTypeIfNeeded = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            try {
                ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, strResolveTypeIfNeeded, null, -1, null, null, null, -1, false, false, userHandle.getIdentifier());
            } catch (RemoteException unused) {
            }
        } catch (RemoteException unused2) {
        }
    }

    @Override // android.content.Context
    public void sendBroadcastAsUser(Intent intent, UserHandle userHandle, String str) {
        String strResolveTypeIfNeeded = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            try {
                ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, strResolveTypeIfNeeded, null, -1, null, null, str, -1, false, false, userHandle.getIdentifier());
            } catch (RemoteException unused) {
            }
        } catch (RemoteException unused2) {
        }
    }

    @Override // android.content.Context
    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle userHandle, String str, BroadcastReceiver broadcastReceiver, Handler handler, int i, String str2, Bundle bundle) {
        IIntentReceiver iIntentReceiver;
        if (broadcastReceiver == null) {
            iIntentReceiver = null;
        } else if (this.mPackageInfo != null) {
            iIntentReceiver = this.mPackageInfo.getReceiverDispatcher(broadcastReceiver, getOuterContext(), handler == null ? this.mMainThread.getHandler() : handler, this.mMainThread.getInstrumentation(), false);
        } else {
            iIntentReceiver = new LoadedApk.ReceiverDispatcher(broadcastReceiver, getOuterContext(), handler == null ? this.mMainThread.getHandler() : handler, null, false).getIIntentReceiver();
        }
        IIntentReceiver iIntentReceiver2 = iIntentReceiver;
        String strResolveTypeIfNeeded = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, strResolveTypeIfNeeded, iIntentReceiver2, i, str2, bundle, str, -1, true, false, userHandle.getIdentifier());
        } catch (RemoteException unused) {
        }
    }

    @Override // android.content.Context
    public void sendStickyBroadcast(Intent intent) {
        warnIfCallingFromSystemProcess();
        String strResolveTypeIfNeeded = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, strResolveTypeIfNeeded, null, -1, null, null, null, -1, false, true, getUserId());
        } catch (RemoteException unused) {
        }
    }

    @Override // android.content.Context
    public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver broadcastReceiver, Handler handler, int i, String str, Bundle bundle) {
        IIntentReceiver iIntentReceiver;
        warnIfCallingFromSystemProcess();
        if (broadcastReceiver == null) {
            iIntentReceiver = null;
        } else if (this.mPackageInfo != null) {
            iIntentReceiver = this.mPackageInfo.getReceiverDispatcher(broadcastReceiver, getOuterContext(), handler == null ? this.mMainThread.getHandler() : handler, this.mMainThread.getInstrumentation(), false);
        } else {
            iIntentReceiver = new LoadedApk.ReceiverDispatcher(broadcastReceiver, getOuterContext(), handler == null ? this.mMainThread.getHandler() : handler, null, false).getIIntentReceiver();
        }
        IIntentReceiver iIntentReceiver2 = iIntentReceiver;
        String strResolveTypeIfNeeded = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, strResolveTypeIfNeeded, iIntentReceiver2, i, str, bundle, null, -1, true, true, getUserId());
        } catch (RemoteException unused) {
        }
    }

    @Override // android.content.Context
    public void removeStickyBroadcast(Intent intent) {
        String strResolveTypeIfNeeded = intent.resolveTypeIfNeeded(getContentResolver());
        if (strResolveTypeIfNeeded != null) {
            Intent intent2 = new Intent(intent);
            intent2.setDataAndType(intent2.getData(), strResolveTypeIfNeeded);
            intent = intent2;
        }
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().unbroadcastIntent(this.mMainThread.getApplicationThread(), intent, getUserId());
        } catch (RemoteException unused) {
        }
    }

    @Override // android.content.Context
    public void sendStickyBroadcastAsUser(Intent intent, UserHandle userHandle) {
        String strResolveTypeIfNeeded = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            try {
                ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, strResolveTypeIfNeeded, null, -1, null, null, null, -1, false, true, userHandle.getIdentifier());
            } catch (RemoteException unused) {
            }
        } catch (RemoteException unused2) {
        }
    }

    @Override // android.content.Context
    public void sendStickyOrderedBroadcastAsUser(Intent intent, UserHandle userHandle, BroadcastReceiver broadcastReceiver, Handler handler, int i, String str, Bundle bundle) {
        IIntentReceiver iIntentReceiver;
        if (broadcastReceiver == null) {
            iIntentReceiver = null;
        } else if (this.mPackageInfo != null) {
            iIntentReceiver = this.mPackageInfo.getReceiverDispatcher(broadcastReceiver, getOuterContext(), handler == null ? this.mMainThread.getHandler() : handler, this.mMainThread.getInstrumentation(), false);
        } else {
            iIntentReceiver = new LoadedApk.ReceiverDispatcher(broadcastReceiver, getOuterContext(), handler == null ? this.mMainThread.getHandler() : handler, null, false).getIIntentReceiver();
        }
        IIntentReceiver iIntentReceiver2 = iIntentReceiver;
        String strResolveTypeIfNeeded = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, strResolveTypeIfNeeded, iIntentReceiver2, i, str, bundle, null, -1, true, true, userHandle.getIdentifier());
        } catch (RemoteException unused) {
        }
    }

    @Override // android.content.Context
    public void removeStickyBroadcastAsUser(Intent intent, UserHandle userHandle) {
        String strResolveTypeIfNeeded = intent.resolveTypeIfNeeded(getContentResolver());
        if (strResolveTypeIfNeeded != null) {
            Intent intent2 = new Intent(intent);
            intent2.setDataAndType(intent2.getData(), strResolveTypeIfNeeded);
            intent = intent2;
        }
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().unbroadcastIntent(this.mMainThread.getApplicationThread(), intent, userHandle.getIdentifier());
        } catch (RemoteException unused) {
        }
    }

    @Override // android.content.Context
    public Intent registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        return registerReceiver(broadcastReceiver, intentFilter, null, null);
    }

    @Override // android.content.Context
    public Intent registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter, String str, Handler handler) {
        return registerReceiverInternal(broadcastReceiver, getUserId(), intentFilter, str, handler, getOuterContext());
    }

    @Override // android.content.Context
    public Intent registerReceiverAsUser(BroadcastReceiver broadcastReceiver, UserHandle userHandle, IntentFilter intentFilter, String str, Handler handler) {
        return registerReceiverInternal(broadcastReceiver, userHandle.getIdentifier(), intentFilter, str, handler, getOuterContext());
    }

    private Intent registerReceiverInternal(BroadcastReceiver broadcastReceiver, int i, IntentFilter intentFilter, String str, Handler handler, Context context) {
        IIntentReceiver iIntentReceiver;
        IIntentReceiver iIntentReceiver2;
        if (broadcastReceiver != null) {
            if (this.mPackageInfo != null && context != null) {
                if (handler == null) {
                    handler = this.mMainThread.getHandler();
                }
                iIntentReceiver2 = this.mPackageInfo.getReceiverDispatcher(broadcastReceiver, context, handler, this.mMainThread.getInstrumentation(), true);
            } else {
                if (handler == null) {
                    handler = this.mMainThread.getHandler();
                }
                iIntentReceiver2 = new LoadedApk.ReceiverDispatcher(broadcastReceiver, context, handler, null, true).getIIntentReceiver();
            }
            iIntentReceiver = iIntentReceiver2;
        } else {
            iIntentReceiver = null;
        }
        try {
            return ActivityManagerNative.getDefault().registerReceiver(this.mMainThread.getApplicationThread(), this.mBasePackageName, iIntentReceiver, intentFilter, str, i);
        } catch (RemoteException unused) {
            return null;
        }
    }

    @Override // android.content.Context
    public void unregisterReceiver(BroadcastReceiver broadcastReceiver) {
        LoadedApk loadedApk = this.mPackageInfo;
        if (loadedApk != null) {
            try {
                ActivityManagerNative.getDefault().unregisterReceiver(loadedApk.forgetReceiverDispatcher(getOuterContext(), broadcastReceiver));
                return;
            } catch (RemoteException unused) {
                return;
            }
        }
        throw new RuntimeException("Not supported in system context");
    }

    private void validateServiceIntent(Intent intent) {
        if (intent.getComponent() == null && intent.getPackage() == null) {
            Log.w(TAG, "Implicit intents with startService are not safe: " + intent + " " + Debug.getCallers(2, 3));
        }
    }

    @Override // android.content.Context
    public ComponentName startService(Intent intent) {
        warnIfCallingFromSystemProcess();
        return startServiceCommon(intent, this.mUser);
    }

    @Override // android.content.Context
    public boolean stopService(Intent intent) {
        warnIfCallingFromSystemProcess();
        return stopServiceCommon(intent, this.mUser);
    }

    @Override // android.content.Context
    public ComponentName startServiceAsUser(Intent intent, UserHandle userHandle) {
        return startServiceCommon(intent, userHandle);
    }

    private ComponentName startServiceCommon(Intent intent, UserHandle userHandle) {
        try {
            validateServiceIntent(intent);
            intent.prepareToLeaveProcess();
            ComponentName componentNameStartService = ActivityManagerNative.getDefault().startService(this.mMainThread.getApplicationThread(), intent, intent.resolveTypeIfNeeded(getContentResolver()), userHandle.getIdentifier());
            if (componentNameStartService != null) {
                if (componentNameStartService.getPackageName().equals("!")) {
                    throw new SecurityException("Not allowed to start service " + intent + " without permission " + componentNameStartService.getClassName());
                }
                if (componentNameStartService.getPackageName().equals("!!")) {
                    throw new SecurityException("Unable to start service " + intent + ": " + componentNameStartService.getClassName());
                }
            }
            return componentNameStartService;
        } catch (RemoteException unused) {
            return null;
        }
    }

    @Override // android.content.Context
    public boolean stopServiceAsUser(Intent intent, UserHandle userHandle) {
        return stopServiceCommon(intent, userHandle);
    }

    private boolean stopServiceCommon(Intent intent, UserHandle userHandle) {
        try {
            validateServiceIntent(intent);
            intent.prepareToLeaveProcess();
            int iStopService = ActivityManagerNative.getDefault().stopService(this.mMainThread.getApplicationThread(), intent, intent.resolveTypeIfNeeded(getContentResolver()), userHandle.getIdentifier());
            if (iStopService >= 0) {
                return iStopService != 0;
            }
            throw new SecurityException("Not allowed to stop service " + intent);
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.content.Context
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        warnIfCallingFromSystemProcess();
        return bindServiceCommon(intent, serviceConnection, i, Process.myUserHandle());
    }

    @Override // android.content.Context
    public boolean bindServiceAsUser(Intent intent, ServiceConnection serviceConnection, int i, UserHandle userHandle) {
        return bindServiceCommon(intent, serviceConnection, i, userHandle);
    }

    private boolean bindServiceCommon(Intent intent, ServiceConnection serviceConnection, int i, UserHandle userHandle) {
        LoadedApk loadedApk;
        if (serviceConnection == null) {
            throw new IllegalArgumentException("connection is null");
        }
        LoadedApk loadedApk2 = this.mPackageInfo;
        if (loadedApk2 != null) {
            IServiceConnection serviceDispatcher = loadedApk2.getServiceDispatcher(serviceConnection, getOuterContext(), this.mMainThread.getHandler(), i);
            validateServiceIntent(intent);
            try {
                if (getActivityToken() == null && (i & 1) == 0 && (loadedApk = this.mPackageInfo) != null && loadedApk.getApplicationInfo().targetSdkVersion < 14) {
                    i |= 32;
                }
                intent.prepareToLeaveProcess();
                int iBindService = ActivityManagerNative.getDefault().bindService(this.mMainThread.getApplicationThread(), getActivityToken(), intent, intent.resolveTypeIfNeeded(getContentResolver()), serviceDispatcher, i, userHandle.getIdentifier());
                if (iBindService >= 0) {
                    return iBindService != 0;
                }
                throw new SecurityException("Not allowed to bind to service " + intent);
            } catch (RemoteException unused) {
                return false;
            }
        }
        throw new RuntimeException("Not supported in system context");
    }

    @Override // android.content.Context
    public void unbindService(ServiceConnection serviceConnection) {
        if (serviceConnection == null) {
            throw new IllegalArgumentException("connection is null");
        }
        LoadedApk loadedApk = this.mPackageInfo;
        if (loadedApk != null) {
            try {
                ActivityManagerNative.getDefault().unbindService(loadedApk.forgetServiceDispatcher(getOuterContext(), serviceConnection));
                return;
            } catch (RemoteException unused) {
                return;
            }
        }
        throw new RuntimeException("Not supported in system context");
    }

    @Override // android.content.Context
    public boolean startInstrumentation(ComponentName componentName, String str, Bundle bundle) {
        if (bundle != null) {
            try {
                bundle.setAllowFds(false);
            } catch (RemoteException unused) {
                return false;
            }
        }
        return ActivityManagerNative.getDefault().startInstrumentation(componentName, str, 0, bundle, null, null, getUserId());
    }

    @Override // android.content.Context
    public Object getSystemService(String str) {
        ServiceFetcher serviceFetcher = SYSTEM_SERVICE_MAP.get(str);
        if (serviceFetcher == null) {
            return null;
        }
        return serviceFetcher.getService(this);
    }

    private WallpaperManager getWallpaperManager() {
        return (WallpaperManager) WALLPAPER_FETCHER.getService(this);
    }

    static DropBoxManager createDropBoxManager() {
        IDropBoxManagerService iDropBoxManagerServiceAsInterface = IDropBoxManagerService.Stub.asInterface(ServiceManager.getService(Context.DROPBOX_SERVICE));
        if (iDropBoxManagerServiceAsInterface == null) {
            return null;
        }
        return new DropBoxManager(iDropBoxManagerServiceAsInterface);
    }

    @Override // android.content.Context
    public int checkPermission(String str, int i, int i2) {
        if (str == null) {
            throw new IllegalArgumentException("permission is null");
        }
        try {
            return ActivityManagerNative.getDefault().checkPermission(str, i, i2);
        } catch (RemoteException unused) {
            return -1;
        }
    }

    @Override // android.content.Context
    public int checkCallingPermission(String str) {
        if (str == null) {
            throw new IllegalArgumentException("permission is null");
        }
        int callingPid = Binder.getCallingPid();
        if (callingPid != Process.myPid()) {
            return checkPermission(str, callingPid, Binder.getCallingUid());
        }
        return -1;
    }

    @Override // android.content.Context
    public int checkCallingOrSelfPermission(String str) {
        if (str == null) {
            throw new IllegalArgumentException("permission is null");
        }
        return checkPermission(str, Binder.getCallingPid(), Binder.getCallingUid());
    }

    private void enforce(String str, int i, boolean z, int i2, String str2) {
        StringBuilder sbAppend;
        String str3;
        if (i != 0) {
            StringBuilder sbAppend2 = new StringBuilder().append(str2 != null ? str2 + ": " : "");
            if (z) {
                sbAppend = new StringBuilder().append("Neither user ").append(i2);
                str3 = " nor current process has ";
            } else {
                sbAppend = new StringBuilder().append("uid ").append(i2);
                str3 = " does not have ";
            }
            throw new SecurityException(sbAppend2.append(sbAppend.append(str3).toString()).append(str).append(".").toString());
        }
    }

    @Override // android.content.Context
    public void enforcePermission(String str, int i, int i2, String str2) {
        enforce(str, checkPermission(str, i, i2), false, i2, str2);
    }

    @Override // android.content.Context
    public void enforceCallingPermission(String str, String str2) {
        enforce(str, checkCallingPermission(str), false, Binder.getCallingUid(), str2);
    }

    @Override // android.content.Context
    public void enforceCallingOrSelfPermission(String str, String str2) {
        enforce(str, checkCallingOrSelfPermission(str), true, Binder.getCallingUid(), str2);
    }

    @Override // android.content.Context
    public void grantUriPermission(String str, Uri uri, int i) {
        try {
            ActivityManagerNative.getDefault().grantUriPermission(this.mMainThread.getApplicationThread(), str, uri, i);
        } catch (RemoteException unused) {
        }
    }

    @Override // android.content.Context
    public void revokeUriPermission(Uri uri, int i) {
        try {
            ActivityManagerNative.getDefault().revokeUriPermission(this.mMainThread.getApplicationThread(), uri, i);
        } catch (RemoteException unused) {
        }
    }

    @Override // android.content.Context
    public int checkUriPermission(Uri uri, int i, int i2, int i3) {
        try {
            return ActivityManagerNative.getDefault().checkUriPermission(uri, i, i2, i3);
        } catch (RemoteException unused) {
            return -1;
        }
    }

    @Override // android.content.Context
    public int checkCallingUriPermission(Uri uri, int i) {
        int callingPid = Binder.getCallingPid();
        if (callingPid != Process.myPid()) {
            return checkUriPermission(uri, callingPid, Binder.getCallingUid(), i);
        }
        return -1;
    }

    @Override // android.content.Context
    public int checkCallingOrSelfUriPermission(Uri uri, int i) {
        return checkUriPermission(uri, Binder.getCallingPid(), Binder.getCallingUid(), i);
    }

    @Override // android.content.Context
    public int checkUriPermission(Uri uri, String str, String str2, int i, int i2, int i3) {
        if ((i3 & 1) != 0 && (str == null || checkPermission(str, i, i2) == 0)) {
            return 0;
        }
        if ((i3 & 2) != 0 && (str2 == null || checkPermission(str2, i, i2) == 0)) {
            return 0;
        }
        if (uri != null) {
            return checkUriPermission(uri, i, i2, i3);
        }
        return -1;
    }

    private String uriModeFlagToString(int i) {
        if (i == 1) {
            return "read";
        }
        if (i == 2) {
            return "write";
        }
        if (i == 3) {
            return "read and write";
        }
        throw new IllegalArgumentException("Unknown permission mode flags: " + i);
    }

    private void enforceForUri(int i, int i2, boolean z, int i3, Uri uri, String str) {
        StringBuilder sbAppend;
        String str2;
        if (i2 != 0) {
            StringBuilder sbAppend2 = new StringBuilder().append(str != null ? str + ": " : "");
            if (z) {
                sbAppend = new StringBuilder().append("Neither user ").append(i3);
                str2 = " nor current process has ";
            } else {
                sbAppend = new StringBuilder().append("User ").append(i3);
                str2 = " does not have ";
            }
            throw new SecurityException(sbAppend2.append(sbAppend.append(str2).toString()).append(uriModeFlagToString(i)).append(" permission on ").append(uri).append(".").toString());
        }
    }

    @Override // android.content.Context
    public void enforceUriPermission(Uri uri, int i, int i2, int i3, String str) {
        enforceForUri(i3, checkUriPermission(uri, i, i2, i3), false, i2, uri, str);
    }

    @Override // android.content.Context
    public void enforceCallingUriPermission(Uri uri, int i, String str) {
        enforceForUri(i, checkCallingUriPermission(uri, i), false, Binder.getCallingUid(), uri, str);
    }

    @Override // android.content.Context
    public void enforceCallingOrSelfUriPermission(Uri uri, int i, String str) {
        enforceForUri(i, checkCallingOrSelfUriPermission(uri, i), true, Binder.getCallingUid(), uri, str);
    }

    @Override // android.content.Context
    public void enforceUriPermission(Uri uri, String str, String str2, int i, int i2, int i3, String str3) {
        enforceForUri(i3, checkUriPermission(uri, str, str2, i, i2, i3), false, i2, uri, str3);
    }

    private void warnIfCallingFromSystemProcess() {
        if (Process.myUid() == 1000) {
            Slog.w(TAG, "Calling a method in the system process without a qualified user: " + Debug.getCallers(5));
        }
    }

    @Override // android.content.Context
    public Context createPackageContext(String str, int i) throws PackageManager.NameNotFoundException {
        UserHandle userHandleMyUserHandle = this.mUser;
        if (userHandleMyUserHandle == null) {
            userHandleMyUserHandle = Process.myUserHandle();
        }
        return createPackageContextAsUser(str, i, userHandleMyUserHandle);
    }

    @Override // android.content.Context
    public Context createPackageContextAsUser(String str, int i, UserHandle userHandle) throws PackageManager.NameNotFoundException {
        if (str.equals("system") || str.equals("android")) {
            ContextImpl contextImpl = new ContextImpl(this.mMainThread.getSystemContext());
            contextImpl.mRestricted = (i & 4) == 4;
            contextImpl.init(this.mPackageInfo, null, this.mMainThread, this.mResources, this.mBasePackageName, userHandle);
            return contextImpl;
        }
        LoadedApk packageInfo = this.mMainThread.getPackageInfo(str, this.mResources.getCompatibilityInfo(), i, userHandle.getIdentifier());
        if (packageInfo != null) {
            ContextImpl contextImpl2 = new ContextImpl();
            contextImpl2.mRestricted = (i & 4) == 4;
            contextImpl2.init(packageInfo, null, this.mMainThread, this.mResources, this.mBasePackageName, userHandle);
            if (contextImpl2.mResources != null) {
                return contextImpl2;
            }
        }
        throw new PackageManager.NameNotFoundException("Application package " + str + " not found");
    }

    @Override // android.content.Context
    public Context createConfigurationContext(Configuration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("overrideConfiguration must not be null");
        }
        ContextImpl contextImpl = new ContextImpl();
        contextImpl.init(this.mPackageInfo, (IBinder) null, this.mMainThread);
        contextImpl.mResources = this.mResourcesManager.getTopLevelResources(this.mPackageInfo.getResDir(), getDisplayId(), configuration, this.mResources.getCompatibilityInfo(), this.mActivityToken);
        return contextImpl;
    }

    @Override // android.content.Context
    public Context createDisplayContext(Display display) {
        if (display == null) {
            throw new IllegalArgumentException("display must not be null");
        }
        int displayId = display.getDisplayId();
        ContextImpl contextImpl = new ContextImpl();
        contextImpl.init(this.mPackageInfo, (IBinder) null, this.mMainThread);
        contextImpl.mDisplay = display;
        contextImpl.mResources = this.mResourcesManager.getTopLevelResources(this.mPackageInfo.getResDir(), displayId, null, getDisplayAdjustments(displayId).getCompatibilityInfo(), null);
        return contextImpl;
    }

    private int getDisplayId() {
        Display display = this.mDisplay;
        if (display != null) {
            return display.getDisplayId();
        }
        return 0;
    }

    @Override // android.content.Context
    public boolean isRestricted() {
        return this.mRestricted;
    }

    @Override // android.content.Context
    public DisplayAdjustments getDisplayAdjustments(int i) {
        return this.mDisplayAdjustments;
    }

    private File getDataDirFile() {
        LoadedApk loadedApk = this.mPackageInfo;
        if (loadedApk != null) {
            return loadedApk.getDataDirFile();
        }
        throw new RuntimeException("Not supported in system context");
    }

    @Override // android.content.Context
    public File getDir(String str, int i) {
        File fileMakeFilename = makeFilename(getDataDirFile(), "app_" + str);
        if (!fileMakeFilename.exists()) {
            fileMakeFilename.mkdir();
            setFilePermissionsFromMode(fileMakeFilename.getPath(), i, 505);
        }
        return fileMakeFilename;
    }

    @Override // android.content.Context
    public int getUserId() {
        return this.mUser.getIdentifier();
    }

    static ContextImpl createSystemContext(ActivityThread activityThread) {
        ContextImpl contextImpl = new ContextImpl();
        contextImpl.init(Resources.getSystem(), activityThread, Process.myUserHandle());
        return contextImpl;
    }

    ContextImpl() {
        this.mActivityToken = null;
        this.mThemeResource = 0;
        this.mTheme = null;
        this.mReceiverRestrictedContext = null;
        this.mSync = new Object();
        this.mDisplayAdjustments = new DisplayAdjustments();
        this.mServiceCache = new ArrayList<>();
        this.mOuterContext = this;
    }

    public ContextImpl(ContextImpl contextImpl) {
        this.mActivityToken = null;
        this.mThemeResource = 0;
        this.mTheme = null;
        this.mReceiverRestrictedContext = null;
        this.mSync = new Object();
        DisplayAdjustments displayAdjustments = new DisplayAdjustments();
        this.mDisplayAdjustments = displayAdjustments;
        this.mServiceCache = new ArrayList<>();
        LoadedApk loadedApk = contextImpl.mPackageInfo;
        this.mPackageInfo = loadedApk;
        this.mBasePackageName = contextImpl.mBasePackageName;
        this.mOpPackageName = contextImpl.mOpPackageName;
        this.mResources = contextImpl.mResources;
        this.mMainThread = contextImpl.mMainThread;
        this.mContentResolver = contextImpl.mContentResolver;
        this.mUser = contextImpl.mUser;
        this.mDisplay = contextImpl.mDisplay;
        this.mOuterContext = this;
        displayAdjustments.setCompatibilityInfo(loadedApk.getCompatibilityInfo());
    }

    final void init(LoadedApk loadedApk, IBinder iBinder, ActivityThread activityThread) {
        init(loadedApk, iBinder, activityThread, null, null, Process.myUserHandle());
    }

    final void init(LoadedApk loadedApk, IBinder iBinder, ActivityThread activityThread, Resources resources, String str, UserHandle userHandle) {
        this.mPackageInfo = loadedApk;
        if (str != null) {
            this.mOpPackageName = str;
            this.mBasePackageName = str;
        } else {
            this.mBasePackageName = loadedApk.mPackageName;
            ApplicationInfo applicationInfo = loadedApk.getApplicationInfo();
            if (applicationInfo.uid == 1000 && applicationInfo.uid != Process.myUid()) {
                this.mOpPackageName = ActivityThread.currentPackageName();
            } else {
                this.mOpPackageName = this.mBasePackageName;
            }
        }
        this.mResources = this.mPackageInfo.getResources(activityThread);
        this.mResourcesManager = ResourcesManager.getInstance();
        CompatibilityInfo compatibilityInfo = resources == null ? null : resources.getCompatibilityInfo();
        if (this.mResources != null && ((compatibilityInfo != null && compatibilityInfo.applicationScale != this.mResources.getCompatibilityInfo().applicationScale) || iBinder != null)) {
            if (compatibilityInfo == null) {
                compatibilityInfo = loadedApk.getCompatibilityInfo();
            }
            CompatibilityInfo compatibilityInfo2 = compatibilityInfo;
            this.mDisplayAdjustments.setCompatibilityInfo(compatibilityInfo2);
            this.mDisplayAdjustments.setActivityToken(iBinder);
            this.mResources = this.mResourcesManager.getTopLevelResources(this.mPackageInfo.getResDir(), 0, null, compatibilityInfo2, iBinder);
        } else {
            this.mDisplayAdjustments.setCompatibilityInfo(loadedApk.getCompatibilityInfo());
            this.mDisplayAdjustments.setActivityToken(iBinder);
        }
        this.mMainThread = activityThread;
        this.mActivityToken = iBinder;
        this.mContentResolver = new ApplicationContentResolver(this, activityThread, userHandle);
        this.mUser = userHandle;
    }

    final void init(Resources resources, ActivityThread activityThread, UserHandle userHandle) {
        this.mPackageInfo = null;
        this.mBasePackageName = null;
        this.mOpPackageName = null;
        this.mResources = resources;
        this.mMainThread = activityThread;
        this.mContentResolver = new ApplicationContentResolver(this, activityThread, userHandle);
        this.mUser = userHandle;
    }

    final void scheduleFinalCleanup(String str, String str2) {
        this.mMainThread.scheduleContextCleanup(this, str, str2);
    }

    final void performFinalCleanup(String str, String str2) {
        this.mPackageInfo.removeContextRegistrations(getOuterContext(), str, str2);
    }

    final Context getReceiverRestrictedContext() {
        Context context = this.mReceiverRestrictedContext;
        if (context != null) {
            return context;
        }
        ReceiverRestrictedContext receiverRestrictedContext = new ReceiverRestrictedContext(getOuterContext());
        this.mReceiverRestrictedContext = receiverRestrictedContext;
        return receiverRestrictedContext;
    }

    final void setOuterContext(Context context) {
        this.mOuterContext = context;
    }

    final Context getOuterContext() {
        return this.mOuterContext;
    }

    final IBinder getActivityToken() {
        return this.mActivityToken;
    }

    static void setFilePermissionsFromMode(String str, int i, int i2) {
        int i3 = i2 | 432;
        if ((i & 1) != 0) {
            i3 |= 4;
        }
        if ((i & 2) != 0) {
            i3 |= 2;
        }
        FileUtils.setPermissions(str, i3, -1, -1);
    }

    private File validateFilePath(String str, boolean z) {
        File databasesDir;
        File fileMakeFilename;
        if (str.charAt(0) == File.separatorChar) {
            databasesDir = new File(str.substring(0, str.lastIndexOf(File.separatorChar)));
            fileMakeFilename = new File(databasesDir, str.substring(str.lastIndexOf(File.separatorChar)));
        } else {
            databasesDir = getDatabasesDir();
            fileMakeFilename = makeFilename(databasesDir, str);
        }
        if (z && !databasesDir.isDirectory() && databasesDir.mkdir()) {
            FileUtils.setPermissions(databasesDir.getPath(), 505, -1, -1);
        }
        return fileMakeFilename;
    }

    private File makeFilename(File file, String str) {
        if (str.indexOf(File.separatorChar) < 0) {
            return new File(file, str);
        }
        throw new IllegalArgumentException("File " + str + " contains a path separator");
    }

    private File[] ensureDirsExistOrFilter(File[] fileArr) {
        File[] fileArr2 = new File[fileArr.length];
        for (int i = 0; i < fileArr.length; i++) {
            File file = fileArr[i];
            if (!file.exists() && !file.mkdirs() && !file.exists()) {
                int iMkdirs = -1;
                try {
                    iMkdirs = IMountService.Stub.asInterface(ServiceManager.getService("mount")).mkdirs(getPackageName(), file.getAbsolutePath());
                } catch (RemoteException unused) {
                }
                if (iMkdirs != 0) {
                    Log.w(TAG, "Failed to ensure directory: " + file);
                    file = null;
                }
            }
            fileArr2[i] = file;
        }
        return fileArr2;
    }

    private static final class ApplicationContentResolver extends ContentResolver {
        private final ActivityThread mMainThread;
        private final UserHandle mUser;

        public ApplicationContentResolver(Context context, ActivityThread activityThread, UserHandle userHandle) {
            super(context);
            this.mMainThread = (ActivityThread) Preconditions.checkNotNull(activityThread);
            this.mUser = (UserHandle) Preconditions.checkNotNull(userHandle);
        }

        @Override // android.content.ContentResolver
        protected IContentProvider acquireProvider(Context context, String str) {
            return this.mMainThread.acquireProvider(context, str, this.mUser.getIdentifier(), true);
        }

        @Override // android.content.ContentResolver
        protected IContentProvider acquireExistingProvider(Context context, String str) {
            return this.mMainThread.acquireExistingProvider(context, str, this.mUser.getIdentifier(), true);
        }

        @Override // android.content.ContentResolver
        public boolean releaseProvider(IContentProvider iContentProvider) {
            return this.mMainThread.releaseProvider(iContentProvider, true);
        }

        @Override // android.content.ContentResolver
        protected IContentProvider acquireUnstableProvider(Context context, String str) {
            return this.mMainThread.acquireProvider(context, str, this.mUser.getIdentifier(), false);
        }

        @Override // android.content.ContentResolver
        public boolean releaseUnstableProvider(IContentProvider iContentProvider) {
            return this.mMainThread.releaseProvider(iContentProvider, false);
        }

        @Override // android.content.ContentResolver
        public void unstableProviderDied(IContentProvider iContentProvider) {
            this.mMainThread.handleUnstableProviderDied(iContentProvider.asBinder(), true);
        }

        @Override // android.content.ContentResolver
        public void appNotRespondingViaProvider(IContentProvider iContentProvider) {
            this.mMainThread.appNotRespondingViaProvider(iContentProvider.asBinder());
        }
    }
}
