package android.preference;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class PreferenceManager {
    public static final String KEY_HAS_SET_DEFAULT_VALUES = "_has_set_default_values";
    public static final String METADATA_KEY_PREFERENCES = "android.preference";
    private static final String TAG = "PreferenceManager";
    private Activity mActivity;
    private List<OnActivityDestroyListener> mActivityDestroyListeners;
    private List<OnActivityResultListener> mActivityResultListeners;
    private List<OnActivityStopListener> mActivityStopListeners;
    private Context mContext;
    private SharedPreferences.Editor mEditor;
    private PreferenceFragment mFragment;
    private long mNextId = 0;
    private int mNextRequestCode;
    private boolean mNoCommit;
    private OnPreferenceTreeClickListener mOnPreferenceTreeClickListener;
    private PreferenceScreen mPreferenceScreen;
    private List<DialogInterface> mPreferencesScreens;
    private SharedPreferences mSharedPreferences;
    private int mSharedPreferencesMode;
    private String mSharedPreferencesName;

    public interface OnActivityDestroyListener {
        void onActivityDestroy();
    }

    public interface OnActivityResultListener {
        boolean onActivityResult(int i, int i2, Intent intent);
    }

    public interface OnActivityStopListener {
        void onActivityStop();
    }

    interface OnPreferenceTreeClickListener {
        boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference);
    }

    private static int getDefaultSharedPreferencesMode() {
        return 0;
    }

    public PreferenceManager(Activity activity, int i) {
        this.mActivity = activity;
        this.mNextRequestCode = i;
        init(activity);
    }

    private PreferenceManager(Context context) {
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setSharedPreferencesName(getDefaultSharedPreferencesName(context));
    }

    void setFragment(PreferenceFragment preferenceFragment) {
        this.mFragment = preferenceFragment;
    }

    PreferenceFragment getFragment() {
        return this.mFragment;
    }

    private List<ResolveInfo> queryIntentActivities(Intent intent) {
        return this.mContext.getPackageManager().queryIntentActivities(intent, 128);
    }

    PreferenceScreen inflateFromIntent(Intent intent, PreferenceScreen preferenceScreen) {
        List<ResolveInfo> listQueryIntentActivities = queryIntentActivities(intent);
        HashSet hashSet = new HashSet();
        for (int size = listQueryIntentActivities.size() - 1; size >= 0; size--) {
            ActivityInfo activityInfo = listQueryIntentActivities.get(size).activityInfo;
            Bundle bundle = activityInfo.metaData;
            if (bundle != null && bundle.containsKey(METADATA_KEY_PREFERENCES)) {
                String str = activityInfo.packageName + ":" + activityInfo.metaData.getInt(METADATA_KEY_PREFERENCES);
                if (!hashSet.contains(str)) {
                    hashSet.add(str);
                    try {
                        Context contextCreatePackageContext = this.mContext.createPackageContext(activityInfo.packageName, 0);
                        PreferenceInflater preferenceInflater = new PreferenceInflater(contextCreatePackageContext, this);
                        XmlResourceParser xmlResourceParserLoadXmlMetaData = activityInfo.loadXmlMetaData(contextCreatePackageContext.getPackageManager(), METADATA_KEY_PREFERENCES);
                        preferenceScreen = (PreferenceScreen) preferenceInflater.inflate((XmlPullParser) xmlResourceParserLoadXmlMetaData, preferenceScreen, true);
                        xmlResourceParserLoadXmlMetaData.close();
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.w(TAG, "Could not create context for " + activityInfo.packageName + ": " + Log.getStackTraceString(e));
                    }
                }
            }
        }
        preferenceScreen.onAttachedToHierarchy(this);
        return preferenceScreen;
    }

    public PreferenceScreen inflateFromResource(Context context, int i, PreferenceScreen preferenceScreen) {
        setNoCommit(true);
        PreferenceScreen preferenceScreen2 = (PreferenceScreen) new PreferenceInflater(context, this).inflate(i, preferenceScreen, true);
        preferenceScreen2.onAttachedToHierarchy(this);
        setNoCommit(false);
        return preferenceScreen2;
    }

    public PreferenceScreen createPreferenceScreen(Context context) {
        PreferenceScreen preferenceScreen = new PreferenceScreen(context, null);
        preferenceScreen.onAttachedToHierarchy(this);
        return preferenceScreen;
    }

    long getNextId() {
        long j;
        synchronized (this) {
            j = this.mNextId;
            this.mNextId = 1 + j;
        }
        return j;
    }

    public String getSharedPreferencesName() {
        return this.mSharedPreferencesName;
    }

    public void setSharedPreferencesName(String str) {
        this.mSharedPreferencesName = str;
        this.mSharedPreferences = null;
    }

    public int getSharedPreferencesMode() {
        return this.mSharedPreferencesMode;
    }

    public void setSharedPreferencesMode(int i) {
        this.mSharedPreferencesMode = i;
        this.mSharedPreferences = null;
    }

    public SharedPreferences getSharedPreferences() {
        if (this.mSharedPreferences == null) {
            this.mSharedPreferences = this.mContext.getSharedPreferences(this.mSharedPreferencesName, this.mSharedPreferencesMode);
        }
        return this.mSharedPreferences;
    }

    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        return context.getSharedPreferences(getDefaultSharedPreferencesName(context), getDefaultSharedPreferencesMode());
    }

    private static String getDefaultSharedPreferencesName(Context context) {
        return context.getPackageName() + "_preferences";
    }

    PreferenceScreen getPreferenceScreen() {
        return this.mPreferenceScreen;
    }

    boolean setPreferences(PreferenceScreen preferenceScreen) {
        if (preferenceScreen == this.mPreferenceScreen) {
            return false;
        }
        this.mPreferenceScreen = preferenceScreen;
        return true;
    }

    public Preference findPreference(CharSequence charSequence) {
        PreferenceScreen preferenceScreen = this.mPreferenceScreen;
        if (preferenceScreen == null) {
            return null;
        }
        return preferenceScreen.findPreference(charSequence);
    }

    public static void setDefaultValues(Context context, int i, boolean z) {
        setDefaultValues(context, getDefaultSharedPreferencesName(context), getDefaultSharedPreferencesMode(), i, z);
    }

    public static void setDefaultValues(Context context, String str, int i, int i2, boolean z) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_HAS_SET_DEFAULT_VALUES, 0);
        if (z || !sharedPreferences.getBoolean(KEY_HAS_SET_DEFAULT_VALUES, false)) {
            PreferenceManager preferenceManager = new PreferenceManager(context);
            preferenceManager.setSharedPreferencesName(str);
            preferenceManager.setSharedPreferencesMode(i);
            preferenceManager.inflateFromResource(context, i2, null);
            SharedPreferences.Editor editorPutBoolean = sharedPreferences.edit().putBoolean(KEY_HAS_SET_DEFAULT_VALUES, true);
            try {
                editorPutBoolean.apply();
            } catch (AbstractMethodError unused) {
                editorPutBoolean.commit();
            }
        }
    }

    SharedPreferences.Editor getEditor() {
        if (this.mNoCommit) {
            if (this.mEditor == null) {
                this.mEditor = getSharedPreferences().edit();
            }
            return this.mEditor;
        }
        return getSharedPreferences().edit();
    }

    boolean shouldCommit() {
        return !this.mNoCommit;
    }

    private void setNoCommit(boolean z) {
        SharedPreferences.Editor editor;
        if (!z && (editor = this.mEditor) != null) {
            try {
                editor.apply();
            } catch (AbstractMethodError unused) {
                this.mEditor.commit();
            }
        }
        this.mNoCommit = z;
    }

    Activity getActivity() {
        return this.mActivity;
    }

    Context getContext() {
        return this.mContext;
    }

    void registerOnActivityResultListener(OnActivityResultListener onActivityResultListener) {
        synchronized (this) {
            if (this.mActivityResultListeners == null) {
                this.mActivityResultListeners = new ArrayList();
            }
            if (!this.mActivityResultListeners.contains(onActivityResultListener)) {
                this.mActivityResultListeners.add(onActivityResultListener);
            }
        }
    }

    void unregisterOnActivityResultListener(OnActivityResultListener onActivityResultListener) {
        synchronized (this) {
            List<OnActivityResultListener> list = this.mActivityResultListeners;
            if (list != null) {
                list.remove(onActivityResultListener);
            }
        }
    }

    void dispatchActivityResult(int i, int i2, Intent intent) {
        synchronized (this) {
            if (this.mActivityResultListeners == null) {
                return;
            }
            ArrayList arrayList = new ArrayList(this.mActivityResultListeners);
            int size = arrayList.size();
            for (int i3 = 0; i3 < size && !((OnActivityResultListener) arrayList.get(i3)).onActivityResult(i, i2, intent); i3++) {
            }
        }
    }

    void registerOnActivityStopListener(OnActivityStopListener onActivityStopListener) {
        synchronized (this) {
            if (this.mActivityStopListeners == null) {
                this.mActivityStopListeners = new ArrayList();
            }
            if (!this.mActivityStopListeners.contains(onActivityStopListener)) {
                this.mActivityStopListeners.add(onActivityStopListener);
            }
        }
    }

    void unregisterOnActivityStopListener(OnActivityStopListener onActivityStopListener) {
        synchronized (this) {
            List<OnActivityStopListener> list = this.mActivityStopListeners;
            if (list != null) {
                list.remove(onActivityStopListener);
            }
        }
    }

    void dispatchActivityStop() {
        synchronized (this) {
            if (this.mActivityStopListeners == null) {
                return;
            }
            ArrayList arrayList = new ArrayList(this.mActivityStopListeners);
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                ((OnActivityStopListener) arrayList.get(i)).onActivityStop();
            }
        }
    }

    void registerOnActivityDestroyListener(OnActivityDestroyListener onActivityDestroyListener) {
        synchronized (this) {
            if (this.mActivityDestroyListeners == null) {
                this.mActivityDestroyListeners = new ArrayList();
            }
            if (!this.mActivityDestroyListeners.contains(onActivityDestroyListener)) {
                this.mActivityDestroyListeners.add(onActivityDestroyListener);
            }
        }
    }

    void unregisterOnActivityDestroyListener(OnActivityDestroyListener onActivityDestroyListener) {
        synchronized (this) {
            List<OnActivityDestroyListener> list = this.mActivityDestroyListeners;
            if (list != null) {
                list.remove(onActivityDestroyListener);
            }
        }
    }

    void dispatchActivityDestroy() {
        ArrayList arrayList;
        synchronized (this) {
            arrayList = this.mActivityDestroyListeners != null ? new ArrayList(this.mActivityDestroyListeners) : null;
        }
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                ((OnActivityDestroyListener) arrayList.get(i)).onActivityDestroy();
            }
        }
        dismissAllScreens();
    }

    int getNextRequestCode() {
        int i;
        synchronized (this) {
            i = this.mNextRequestCode;
            this.mNextRequestCode = i + 1;
        }
        return i;
    }

    void addPreferencesScreen(DialogInterface dialogInterface) {
        synchronized (this) {
            if (this.mPreferencesScreens == null) {
                this.mPreferencesScreens = new ArrayList();
            }
            this.mPreferencesScreens.add(dialogInterface);
        }
    }

    void removePreferencesScreen(DialogInterface dialogInterface) {
        synchronized (this) {
            List<DialogInterface> list = this.mPreferencesScreens;
            if (list == null) {
                return;
            }
            list.remove(dialogInterface);
        }
    }

    void dispatchNewIntent(Intent intent) {
        dismissAllScreens();
    }

    private void dismissAllScreens() {
        synchronized (this) {
            if (this.mPreferencesScreens == null) {
                return;
            }
            ArrayList arrayList = new ArrayList(this.mPreferencesScreens);
            this.mPreferencesScreens.clear();
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                ((DialogInterface) arrayList.get(size)).dismiss();
            }
        }
    }

    void setOnPreferenceTreeClickListener(OnPreferenceTreeClickListener onPreferenceTreeClickListener) {
        this.mOnPreferenceTreeClickListener = onPreferenceTreeClickListener;
    }

    OnPreferenceTreeClickListener getOnPreferenceTreeClickListener() {
        return this.mOnPreferenceTreeClickListener;
    }
}
