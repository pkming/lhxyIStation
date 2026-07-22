package android.os;

import android.os.storage.IMountService;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class Environment {
    private static final File DATA_DIRECTORY;
    public static String DIRECTORY_ALARMS = null;

    @Deprecated
    public static final String DIRECTORY_ANDROID = "Android";
    public static String DIRECTORY_DCIM = null;
    public static String DIRECTORY_DOCUMENTS = null;
    public static String DIRECTORY_DOWNLOADS = null;
    public static String DIRECTORY_MOVIES = null;
    public static String DIRECTORY_MUSIC = null;
    public static String DIRECTORY_NOTIFICATIONS = null;
    public static String DIRECTORY_PICTURES = null;
    public static String DIRECTORY_PODCASTS = null;
    public static String DIRECTORY_RINGTONES = null;
    public static final String DIR_ANDROID = "Android";
    private static final String DIR_CACHE = "cache";
    private static final String DIR_DATA = "data";
    private static final String DIR_FILES = "files";
    private static final String DIR_MEDIA = "media";
    private static final String DIR_OBB = "obb";
    private static final File DOWNLOAD_CACHE_DIRECTORY;
    private static final String ENV_EMULATED_STORAGE_SOURCE = "EMULATED_STORAGE_SOURCE";
    private static final String ENV_EXTERNAL_STORAGE = "EXTERNAL_STORAGE";
    private static final String ENV_SECONDARY_STORAGE = "SECONDARY_STORAGE";
    public static final String MEDIA_BAD_REMOVAL = "bad_removal";
    public static final String MEDIA_CHECKING = "checking";
    public static final String MEDIA_MOUNTED = "mounted";
    public static final String MEDIA_MOUNTED_READ_ONLY = "mounted_ro";
    public static final String MEDIA_NOFS = "nofs";
    public static final String MEDIA_REMOVED = "removed";
    public static final String MEDIA_SHARED = "shared";
    public static final String MEDIA_UNKNOWN = "unknown";
    public static final String MEDIA_UNMOUNTABLE = "unmountable";
    public static final String MEDIA_UNMOUNTED = "unmounted";
    private static final File SECURE_DATA_DIRECTORY;
    private static final String SYSTEM_PROPERTY_EFS_ENABLED = "persist.security.efs.enabled";
    private static final String TAG = "Environment";
    private static UserEnvironment sCurrentUser;
    private static volatile StorageVolume sPrimaryVolume;
    private static boolean sUserRequired;
    private static final String ENV_ANDROID_ROOT = "ANDROID_ROOT";
    private static final File DIR_ANDROID_ROOT = getDirectory(ENV_ANDROID_ROOT, "/system");
    private static final String ENV_MEDIA_STORAGE = "MEDIA_STORAGE";
    private static final File DIR_MEDIA_STORAGE = getDirectory(ENV_MEDIA_STORAGE, "/data/media");
    private static final String ENV_EMULATED_STORAGE_TARGET = "EMULATED_STORAGE_TARGET";
    private static final String CANONCIAL_EMULATED_STORAGE_TARGET = getCanonicalPathOrNull(ENV_EMULATED_STORAGE_TARGET);
    private static final Object sLock = new Object();

    static {
        initForCurrentUser();
        DATA_DIRECTORY = getDirectory("ANDROID_DATA", "/data");
        SECURE_DATA_DIRECTORY = getDirectory("ANDROID_SECURE_DATA", "/data/secure");
        DOWNLOAD_CACHE_DIRECTORY = getDirectory("DOWNLOAD_CACHE", "/cache");
        DIRECTORY_MUSIC = "Music";
        DIRECTORY_PODCASTS = "Podcasts";
        DIRECTORY_RINGTONES = "Ringtones";
        DIRECTORY_ALARMS = "Alarms";
        DIRECTORY_NOTIFICATIONS = "Notifications";
        DIRECTORY_PICTURES = "Pictures";
        DIRECTORY_MOVIES = "Movies";
        DIRECTORY_DOWNLOADS = "Download";
        DIRECTORY_DCIM = "DCIM";
        DIRECTORY_DOCUMENTS = "Documents";
    }

    private static StorageVolume getPrimaryVolume() {
        if (SystemProperties.getBoolean("config.disable_storage", false)) {
            return null;
        }
        if (sPrimaryVolume == null) {
            synchronized (sLock) {
                if (sPrimaryVolume == null) {
                    try {
                        sPrimaryVolume = StorageManager.getPrimaryVolume(IMountService.Stub.asInterface(ServiceManager.getService("mount")).getVolumeList());
                    } catch (Exception e) {
                        Log.e(TAG, "couldn't talk to MountService", e);
                    }
                }
            }
        }
        return sPrimaryVolume;
    }

    public static void initForCurrentUser() {
        sCurrentUser = new UserEnvironment(UserHandle.myUserId());
        synchronized (sLock) {
            sPrimaryVolume = null;
        }
    }

    public static class UserEnvironment {
        private final File mEmulatedDirForDirect;
        private final File[] mExternalDirsForApp;
        private final File[] mExternalDirsForVold;

        public UserEnvironment(int i) {
            String str = System.getenv(Environment.ENV_EXTERNAL_STORAGE);
            String str2 = System.getenv(Environment.ENV_EMULATED_STORAGE_SOURCE);
            String str3 = System.getenv(Environment.ENV_EMULATED_STORAGE_TARGET);
            String str4 = System.getenv(Environment.ENV_MEDIA_STORAGE);
            str4 = TextUtils.isEmpty(str4) ? "/data/media" : str4;
            ArrayList arrayListNewArrayList = Lists.newArrayList();
            ArrayList arrayListNewArrayList2 = Lists.newArrayList();
            if (!TextUtils.isEmpty(str3)) {
                String string = Integer.toString(i);
                File file = new File(str2);
                File file2 = new File(str3);
                File file3 = new File(str4);
                arrayListNewArrayList.add(Environment.buildPath(file, string));
                arrayListNewArrayList2.add(Environment.buildPath(file2, string));
                this.mEmulatedDirForDirect = Environment.buildPath(file3, string);
            } else {
                if (TextUtils.isEmpty(str)) {
                    Log.w(Environment.TAG, "EXTERNAL_STORAGE undefined; falling back to default");
                    str = "/storage/sdcard0";
                }
                arrayListNewArrayList.add(new File(str));
                arrayListNewArrayList2.add(new File(str));
                this.mEmulatedDirForDirect = new File(str4);
            }
            String str5 = System.getenv(Environment.ENV_SECONDARY_STORAGE);
            if (!TextUtils.isEmpty(str5) && i == 0) {
                for (String str6 : str5.split(":")) {
                    arrayListNewArrayList.add(new File(str6));
                    arrayListNewArrayList2.add(new File(str6));
                }
            }
            this.mExternalDirsForVold = (File[]) arrayListNewArrayList.toArray(new File[arrayListNewArrayList.size()]);
            this.mExternalDirsForApp = (File[]) arrayListNewArrayList2.toArray(new File[arrayListNewArrayList2.size()]);
        }

        @Deprecated
        public File getExternalStorageDirectory() {
            return this.mExternalDirsForApp[0];
        }

        @Deprecated
        public File getExternalStoragePublicDirectory(String str) {
            return buildExternalStoragePublicDirs(str)[0];
        }

        public File[] getExternalDirsForVold() {
            return this.mExternalDirsForVold;
        }

        public File[] getExternalDirsForApp() {
            return this.mExternalDirsForApp;
        }

        public File getMediaDir() {
            return this.mEmulatedDirForDirect;
        }

        public File[] buildExternalStoragePublicDirs(String str) {
            return Environment.buildPaths(this.mExternalDirsForApp, str);
        }

        public File[] buildExternalStorageAndroidDataDirs() {
            return Environment.buildPaths(this.mExternalDirsForApp, "Android", "data");
        }

        public File[] buildExternalStorageAndroidObbDirs() {
            return Environment.buildPaths(this.mExternalDirsForApp, "Android", "obb");
        }

        public File[] buildExternalStorageAppDataDirs(String str) {
            return Environment.buildPaths(this.mExternalDirsForApp, "Android", "data", str);
        }

        public File[] buildExternalStorageAppDataDirsForVold(String str) {
            return Environment.buildPaths(this.mExternalDirsForVold, "Android", "data", str);
        }

        public File[] buildExternalStorageAppMediaDirs(String str) {
            return Environment.buildPaths(this.mExternalDirsForApp, "Android", "media", str);
        }

        public File[] buildExternalStorageAppObbDirs(String str) {
            return Environment.buildPaths(this.mExternalDirsForApp, "Android", "obb", str);
        }

        public File[] buildExternalStorageAppObbDirsForVold(String str) {
            return Environment.buildPaths(this.mExternalDirsForVold, "Android", "obb", str);
        }

        public File[] buildExternalStorageAppFilesDirs(String str) {
            return Environment.buildPaths(this.mExternalDirsForApp, "Android", "data", str, Environment.DIR_FILES);
        }

        public File[] buildExternalStorageAppCacheDirs(String str) {
            return Environment.buildPaths(this.mExternalDirsForApp, "Android", "data", str, Environment.DIR_CACHE);
        }
    }

    public static File getRootDirectory() {
        return DIR_ANDROID_ROOT;
    }

    public static File getSystemSecureDirectory() {
        if (isEncryptedFilesystemEnabled()) {
            return new File(SECURE_DATA_DIRECTORY, "system");
        }
        return new File(DATA_DIRECTORY, "system");
    }

    public static File getSecureDataDirectory() {
        if (isEncryptedFilesystemEnabled()) {
            return SECURE_DATA_DIRECTORY;
        }
        return DATA_DIRECTORY;
    }

    public static File getMediaStorageDirectory() {
        throwIfUserRequired();
        return sCurrentUser.getMediaDir();
    }

    public static File getUserSystemDirectory(int i) {
        return new File(new File(getSystemSecureDirectory(), "users"), Integer.toString(i));
    }

    public static boolean isEncryptedFilesystemEnabled() {
        return SystemProperties.getBoolean(SYSTEM_PROPERTY_EFS_ENABLED, false);
    }

    public static File getDataDirectory() {
        return DATA_DIRECTORY;
    }

    public static File getExternalStorageDirectory() {
        throwIfUserRequired();
        return sCurrentUser.getExternalDirsForApp()[0];
    }

    public static File getLegacyExternalStorageDirectory() {
        return new File(System.getenv(ENV_EXTERNAL_STORAGE));
    }

    public static File getLegacyExternalStorageObbDirectory() {
        return buildPath(getLegacyExternalStorageDirectory(), "Android", "obb");
    }

    public static File getEmulatedStorageSource(int i) {
        return new File(System.getenv(ENV_EMULATED_STORAGE_SOURCE), String.valueOf(i));
    }

    public static File getEmulatedStorageObbSource() {
        return new File(System.getenv(ENV_EMULATED_STORAGE_SOURCE), "obb");
    }

    public static File getExternalStoragePublicDirectory(String str) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStoragePublicDirs(str)[0];
    }

    public static File[] buildExternalStorageAndroidDataDirs() {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAndroidDataDirs();
    }

    public static File[] buildExternalStorageAppDataDirs(String str) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAppDataDirs(str);
    }

    public static File[] buildExternalStorageAppMediaDirs(String str) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAppMediaDirs(str);
    }

    public static File[] buildExternalStorageAppObbDirs(String str) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAppObbDirs(str);
    }

    public static File[] buildExternalStorageAppFilesDirs(String str) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAppFilesDirs(str);
    }

    public static File[] buildExternalStorageAppCacheDirs(String str) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAppCacheDirs(str);
    }

    public static File getDownloadCacheDirectory() {
        return DOWNLOAD_CACHE_DIRECTORY;
    }

    public static String getExternalStorageState() {
        return getStorageState(sCurrentUser.getExternalDirsForApp()[0]);
    }

    public static String getStorageState(File file) {
        IMountService iMountServiceAsInterface;
        int i;
        try {
            String canonicalPath = file.getCanonicalPath();
            try {
                iMountServiceAsInterface = IMountService.Stub.asInterface(ServiceManager.getService("mount"));
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to find external storage state: " + e);
            }
            for (StorageVolume storageVolume : iMountServiceAsInterface.getVolumeList()) {
                if (canonicalPath.startsWith(storageVolume.getPath())) {
                    return iMountServiceAsInterface.getVolumeState(storageVolume.getPath());
                }
                return "unknown";
            }
            return "unknown";
        } catch (IOException e2) {
            Log.w(TAG, "Failed to resolve target path: " + e2);
            return "unknown";
        }
    }

    public static boolean isExternalStorageRemovable() {
        StorageVolume primaryVolume = getPrimaryVolume();
        return primaryVolume != null && primaryVolume.isRemovable();
    }

    public static boolean isExternalStorageEmulated() {
        StorageVolume primaryVolume = getPrimaryVolume();
        return primaryVolume != null && primaryVolume.isEmulated();
    }

    static File getDirectory(String str, String str2) {
        String str3 = System.getenv(str);
        return str3 == null ? new File(str2) : new File(str3);
    }

    private static String getCanonicalPathOrNull(String str) {
        String str2 = System.getenv(str);
        if (str2 == null) {
            return null;
        }
        try {
            return new File(str2).getCanonicalPath();
        } catch (IOException unused) {
            Log.w(TAG, "Unable to resolve canonical path for " + str2);
            return null;
        }
    }

    public static void setUserRequired(boolean z) {
        sUserRequired = z;
    }

    private static void throwIfUserRequired() {
        if (sUserRequired) {
            Log.wtf(TAG, "Path requests must specify a user by using UserEnvironment", new Throwable());
        }
    }

    public static File[] buildPaths(File[] fileArr, String... strArr) {
        File[] fileArr2 = new File[fileArr.length];
        for (int i = 0; i < fileArr.length; i++) {
            fileArr2[i] = buildPath(fileArr[i], strArr);
        }
        return fileArr2;
    }

    public static File buildPath(File file, String... strArr) {
        for (String str : strArr) {
            if (file == null) {
                file = new File(str);
            } else {
                file = new File(file, str);
            }
        }
        return file;
    }

    public static File maybeTranslateEmulatedPathToInternal(File file) {
        String str;
        if (isExternalStorageEmulated() && (str = CANONCIAL_EMULATED_STORAGE_TARGET) != null) {
            try {
                String canonicalPath = file.getCanonicalPath();
                if (canonicalPath.startsWith(str)) {
                    File file2 = new File(DIR_MEDIA_STORAGE, canonicalPath.substring(str.length()));
                    if (file2.exists()) {
                        return file2;
                    }
                }
            } catch (IOException unused) {
                Log.w(TAG, "Failed to resolve canonical path for " + file);
            }
        }
        return file;
    }
}
