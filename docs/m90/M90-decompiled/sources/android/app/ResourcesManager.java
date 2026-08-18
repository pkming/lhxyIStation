package android.app;

import android.content.res.AssetManager;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.ResourcesKey;
import android.hardware.display.DisplayManagerGlobal;
import android.os.IBinder;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.DisplayAdjustments;
import java.lang.ref.WeakReference;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class ResourcesManager {
    static final boolean DEBUG_CACHE = false;
    static final boolean DEBUG_STATS = true;
    static final String TAG = "ResourcesManager";
    private static ResourcesManager sResourcesManager;
    CompatibilityInfo mResCompatibilityInfo;
    Configuration mResConfiguration;
    final ArrayMap<ResourcesKey, WeakReference<Resources>> mActiveResources = new ArrayMap<>();
    final ArrayMap<DisplayAdjustments, DisplayMetrics> mDefaultDisplayMetrics = new ArrayMap<>();
    final Configuration mTmpConfig = new Configuration();

    public static ResourcesManager getInstance() {
        ResourcesManager resourcesManager;
        synchronized (ResourcesManager.class) {
            if (sResourcesManager == null) {
                sResourcesManager = new ResourcesManager();
            }
            resourcesManager = sResourcesManager;
        }
        return resourcesManager;
    }

    public Configuration getConfiguration() {
        return this.mResConfiguration;
    }

    public void flushDisplayMetricsLocked() {
        this.mDefaultDisplayMetrics.clear();
    }

    public DisplayMetrics getDisplayMetricsLocked(int i) {
        return getDisplayMetricsLocked(i, DisplayAdjustments.DEFAULT_DISPLAY_ADJUSTMENTS);
    }

    public DisplayMetrics getDisplayMetricsLocked(int i, DisplayAdjustments displayAdjustments) {
        boolean z = i == 0;
        DisplayMetrics displayMetrics = z ? this.mDefaultDisplayMetrics.get(displayAdjustments) : null;
        if (displayMetrics != null) {
            return displayMetrics;
        }
        DisplayMetrics displayMetrics2 = new DisplayMetrics();
        DisplayManagerGlobal displayManagerGlobal = DisplayManagerGlobal.getInstance();
        if (displayManagerGlobal == null) {
            displayMetrics2.setToDefaults();
            return displayMetrics2;
        }
        if (z) {
            this.mDefaultDisplayMetrics.put(displayAdjustments, displayMetrics2);
        }
        Display compatibleDisplay = displayManagerGlobal.getCompatibleDisplay(i, displayAdjustments);
        if (compatibleDisplay != null) {
            compatibleDisplay.getMetrics(displayMetrics2);
        } else {
            displayMetrics2.setToDefaults();
        }
        return displayMetrics2;
    }

    final void applyNonDefaultDisplayMetricsToConfigurationLocked(DisplayMetrics displayMetrics, Configuration configuration) {
        configuration.touchscreen = 1;
        configuration.densityDpi = displayMetrics.densityDpi;
        configuration.screenWidthDp = (int) (displayMetrics.widthPixels / displayMetrics.density);
        configuration.screenHeightDp = (int) (displayMetrics.heightPixels / displayMetrics.density);
        int iResetScreenLayout = Configuration.resetScreenLayout(configuration.screenLayout);
        if (displayMetrics.widthPixels > displayMetrics.heightPixels) {
            configuration.orientation = 2;
            configuration.screenLayout = Configuration.reduceScreenLayout(iResetScreenLayout, configuration.screenWidthDp, configuration.screenHeightDp);
        } else {
            configuration.orientation = 1;
            configuration.screenLayout = Configuration.reduceScreenLayout(iResetScreenLayout, configuration.screenHeightDp, configuration.screenWidthDp);
        }
        configuration.smallestScreenWidthDp = configuration.screenWidthDp;
        configuration.compatScreenWidthDp = configuration.screenWidthDp;
        configuration.compatScreenHeightDp = configuration.screenHeightDp;
        configuration.compatSmallestScreenWidthDp = configuration.smallestScreenWidthDp;
    }

    public boolean applyCompatConfiguration(int i, Configuration configuration) {
        CompatibilityInfo compatibilityInfo = this.mResCompatibilityInfo;
        if (compatibilityInfo == null || compatibilityInfo.supportsScreen()) {
            return false;
        }
        this.mResCompatibilityInfo.applyToConfiguration(i, configuration);
        return true;
    }

    public Resources getTopLevelResources(String str, int i, Configuration configuration, CompatibilityInfo compatibilityInfo, IBinder iBinder) {
        Configuration configuration2;
        ResourcesKey resourcesKey = new ResourcesKey(str, i, configuration, compatibilityInfo.applicationScale, iBinder);
        synchronized (this) {
            WeakReference<Resources> weakReference = this.mActiveResources.get(resourcesKey);
            Resources resources = weakReference != null ? weakReference.get() : null;
            if (resources != null && resources.getAssets().isUpToDate()) {
                return resources;
            }
            AssetManager assetManager = new AssetManager();
            if (assetManager.addAssetPath(str) == 0) {
                return null;
            }
            DisplayMetrics displayMetricsLocked = getDisplayMetricsLocked(i);
            boolean z = i == 0;
            boolean zHasOverrideConfiguration = resourcesKey.hasOverrideConfiguration();
            if (!z || zHasOverrideConfiguration) {
                configuration2 = new Configuration(getConfiguration());
                if (!z) {
                    applyNonDefaultDisplayMetricsToConfigurationLocked(displayMetricsLocked, configuration2);
                }
                if (zHasOverrideConfiguration) {
                    configuration2.updateFrom(resourcesKey.mOverrideConfiguration);
                }
            } else {
                configuration2 = getConfiguration();
            }
            Resources resources2 = new Resources(assetManager, displayMetricsLocked, configuration2, compatibilityInfo, iBinder);
            synchronized (this) {
                WeakReference<Resources> weakReference2 = this.mActiveResources.get(resourcesKey);
                Resources resources3 = weakReference2 != null ? weakReference2.get() : null;
                if (resources3 != null && resources3.getAssets().isUpToDate()) {
                    resources2.getAssets().close();
                    return resources3;
                }
                this.mActiveResources.put(resourcesKey, new WeakReference<>(resources2));
                return resources2;
            }
        }
    }

    public final boolean applyConfigurationToResourcesLocked(Configuration configuration, CompatibilityInfo compatibilityInfo) {
        DisplayMetrics displayMetricsLocked;
        CompatibilityInfo compatibilityInfo2;
        if (this.mResConfiguration == null) {
            this.mResConfiguration = new Configuration();
        }
        if (!this.mResConfiguration.isOtherSeqNewer(configuration) && compatibilityInfo == null) {
            return false;
        }
        int iUpdateFrom = this.mResConfiguration.updateFrom(configuration);
        flushDisplayMetricsLocked();
        DisplayMetrics displayMetricsLocked2 = getDisplayMetricsLocked(0);
        if (compatibilityInfo != null && ((compatibilityInfo2 = this.mResCompatibilityInfo) == null || !compatibilityInfo2.equals(compatibilityInfo))) {
            this.mResCompatibilityInfo = compatibilityInfo;
            iUpdateFrom |= 3328;
        }
        if (configuration.locale != null) {
            Locale.setDefault(configuration.locale);
        }
        Resources.updateSystemConfiguration(configuration, displayMetricsLocked2, compatibilityInfo);
        ApplicationPackageManager.configurationChanged();
        Configuration configuration2 = null;
        for (int size = this.mActiveResources.size() - 1; size >= 0; size--) {
            ResourcesKey resourcesKeyKeyAt = this.mActiveResources.keyAt(size);
            Resources resources = this.mActiveResources.valueAt(size).get();
            if (resources != null) {
                int i = resourcesKeyKeyAt.mDisplayId;
                boolean z = i == 0;
                boolean zHasOverrideConfiguration = resourcesKeyKeyAt.hasOverrideConfiguration();
                if (!z || zHasOverrideConfiguration) {
                    if (configuration2 == null) {
                        configuration2 = new Configuration();
                    }
                    configuration2.setTo(configuration);
                    if (z) {
                        displayMetricsLocked = displayMetricsLocked2;
                    } else {
                        displayMetricsLocked = getDisplayMetricsLocked(i);
                        applyNonDefaultDisplayMetricsToConfigurationLocked(displayMetricsLocked, configuration2);
                    }
                    if (zHasOverrideConfiguration) {
                        configuration2.updateFrom(resourcesKeyKeyAt.mOverrideConfiguration);
                    }
                    resources.updateConfiguration(configuration2, displayMetricsLocked, compatibilityInfo);
                } else {
                    resources.updateConfiguration(configuration, displayMetricsLocked2, compatibilityInfo);
                }
            } else {
                this.mActiveResources.removeAt(size);
            }
        }
        return iUpdateFrom != 0;
    }
}
