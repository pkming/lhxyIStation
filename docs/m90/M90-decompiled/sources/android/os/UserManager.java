package android.os;

import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class UserManager {
    public static final String DISALLOW_CONFIG_BLUETOOTH = "no_config_bluetooth";
    public static final String DISALLOW_CONFIG_CREDENTIALS = "no_config_credentials";
    public static final String DISALLOW_CONFIG_WIFI = "no_config_wifi";
    public static final String DISALLOW_INSTALL_APPS = "no_install_apps";
    public static final String DISALLOW_INSTALL_UNKNOWN_SOURCES = "no_install_unknown_sources";
    public static final String DISALLOW_MODIFY_ACCOUNTS = "no_modify_accounts";
    public static final String DISALLOW_REMOVE_USER = "no_remove_user";
    public static final String DISALLOW_SHARE_LOCATION = "no_share_location";
    public static final String DISALLOW_UNINSTALL_APPS = "no_uninstall_apps";
    public static final String DISALLOW_USB_FILE_TRANSFER = "no_usb_file_transfer";
    public static final int PIN_VERIFICATION_FAILED_INCORRECT = -3;
    public static final int PIN_VERIFICATION_FAILED_NOT_SET = -2;
    public static final int PIN_VERIFICATION_SUCCESS = -1;
    private static String TAG = "UserManager";
    private static UserManager sInstance;
    private final Context mContext;
    private final IUserManager mService;

    public boolean isUserAGoat() {
        return false;
    }

    public static synchronized UserManager get(Context context) {
        if (sInstance == null) {
            sInstance = (UserManager) context.getSystemService(Context.USER_SERVICE);
        }
        return sInstance;
    }

    public UserManager(Context context, IUserManager iUserManager) {
        this.mService = iUserManager;
        this.mContext = context;
    }

    public static boolean supportsMultipleUsers() {
        return getMaxSupportedUsers() > 1;
    }

    public int getUserHandle() {
        return UserHandle.myUserId();
    }

    public String getUserName() {
        try {
            return this.mService.getUserInfo(getUserHandle()).name;
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get user name", e);
            return "";
        }
    }

    public boolean isLinkedUser() {
        try {
            return this.mService.isRestricted();
        } catch (RemoteException e) {
            Log.w(TAG, "Could not check if user is limited ", e);
            return false;
        }
    }

    public boolean isUserRunning(UserHandle userHandle) {
        try {
            return ActivityManagerNative.getDefault().isUserRunning(userHandle.getIdentifier(), false);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean isUserRunningOrStopping(UserHandle userHandle) {
        try {
            return ActivityManagerNative.getDefault().isUserRunning(userHandle.getIdentifier(), true);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public UserInfo getUserInfo(int i) {
        try {
            return this.mService.getUserInfo(i);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get user info", e);
            return null;
        }
    }

    public Bundle getUserRestrictions() {
        return getUserRestrictions(Process.myUserHandle());
    }

    public Bundle getUserRestrictions(UserHandle userHandle) {
        try {
            return this.mService.getUserRestrictions(userHandle.getIdentifier());
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get user restrictions", e);
            return Bundle.EMPTY;
        }
    }

    public void setUserRestrictions(Bundle bundle) {
        setUserRestrictions(bundle, Process.myUserHandle());
    }

    public void setUserRestrictions(Bundle bundle, UserHandle userHandle) {
        try {
            this.mService.setUserRestrictions(bundle, userHandle.getIdentifier());
        } catch (RemoteException e) {
            Log.w(TAG, "Could not set user restrictions", e);
        }
    }

    public void setUserRestriction(String str, boolean z) {
        Bundle userRestrictions = getUserRestrictions();
        userRestrictions.putBoolean(str, z);
        setUserRestrictions(userRestrictions);
    }

    public void setUserRestriction(String str, boolean z, UserHandle userHandle) {
        Bundle userRestrictions = getUserRestrictions(userHandle);
        userRestrictions.putBoolean(str, z);
        setUserRestrictions(userRestrictions, userHandle);
    }

    public boolean hasUserRestriction(String str) {
        return hasUserRestriction(str, Process.myUserHandle());
    }

    public boolean hasUserRestriction(String str, UserHandle userHandle) {
        return getUserRestrictions(userHandle).getBoolean(str, false);
    }

    public long getSerialNumberForUser(UserHandle userHandle) {
        return getUserSerialNumber(userHandle.getIdentifier());
    }

    public UserHandle getUserForSerialNumber(long j) {
        int userHandle = getUserHandle((int) j);
        if (userHandle >= 0) {
            return new UserHandle(userHandle);
        }
        return null;
    }

    public UserInfo createUser(String str, int i) {
        try {
            return this.mService.createUser(str, i);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not create a user", e);
            return null;
        }
    }

    public int getUserCount() {
        List<UserInfo> users = getUsers();
        if (users != null) {
            return users.size();
        }
        return 1;
    }

    public List<UserInfo> getUsers() {
        try {
            return this.mService.getUsers(false);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get user list", e);
            return null;
        }
    }

    public List<UserInfo> getUsers(boolean z) {
        try {
            return this.mService.getUsers(z);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get user list", e);
            return null;
        }
    }

    public boolean removeUser(int i) {
        try {
            return this.mService.removeUser(i);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not remove user ", e);
            return false;
        }
    }

    public void setUserName(int i, String str) {
        try {
            this.mService.setUserName(i, str);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not set the user name ", e);
        }
    }

    public void setUserIcon(int i, Bitmap bitmap) {
        try {
            this.mService.setUserIcon(i, bitmap);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not set the user icon ", e);
        }
    }

    public Bitmap getUserIcon(int i) {
        try {
            return this.mService.getUserIcon(i);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get the user icon ", e);
            return null;
        }
    }

    public void setGuestEnabled(boolean z) {
        try {
            this.mService.setGuestEnabled(z);
        } catch (RemoteException unused) {
            Log.w(TAG, "Could not change guest account availability to " + z);
        }
    }

    public boolean isGuestEnabled() {
        try {
            return this.mService.isGuestEnabled();
        } catch (RemoteException unused) {
            Log.w(TAG, "Could not retrieve guest enabled state");
            return false;
        }
    }

    public void wipeUser(int i) {
        try {
            this.mService.wipeUser(i);
        } catch (RemoteException unused) {
            Log.w(TAG, "Could not wipe user " + i);
        }
    }

    public static int getMaxSupportedUsers() {
        if (Build.ID.startsWith("JVP")) {
            return 1;
        }
        return SystemProperties.getInt("fw.max_users", Resources.getSystem().getInteger(17694787));
    }

    public int getUserSerialNumber(int i) {
        try {
            return this.mService.getUserSerialNumber(i);
        } catch (RemoteException unused) {
            Log.w(TAG, "Could not get serial number for user " + i);
            return -1;
        }
    }

    public int getUserHandle(int i) {
        try {
            return this.mService.getUserHandle(i);
        } catch (RemoteException unused) {
            Log.w(TAG, "Could not get userHandle for user " + i);
            return -1;
        }
    }

    public Bundle getApplicationRestrictions(String str) {
        try {
            return this.mService.getApplicationRestrictions(str);
        } catch (RemoteException unused) {
            Log.w(TAG, "Could not get application restrictions for package " + str);
            return null;
        }
    }

    public Bundle getApplicationRestrictions(String str, UserHandle userHandle) {
        try {
            return this.mService.getApplicationRestrictionsForUser(str, userHandle.getIdentifier());
        } catch (RemoteException unused) {
            Log.w(TAG, "Could not get application restrictions for user " + userHandle.getIdentifier());
            return null;
        }
    }

    public void setApplicationRestrictions(String str, Bundle bundle, UserHandle userHandle) {
        try {
            this.mService.setApplicationRestrictions(str, bundle, userHandle.getIdentifier());
        } catch (RemoteException unused) {
            Log.w(TAG, "Could not set application restrictions for user " + userHandle.getIdentifier());
        }
    }

    public boolean setRestrictionsChallenge(String str) {
        try {
            return this.mService.setRestrictionsChallenge(str);
        } catch (RemoteException unused) {
            Log.w(TAG, "Could not change restrictions pin");
            return false;
        }
    }

    public int checkRestrictionsChallenge(String str) {
        try {
            return this.mService.checkRestrictionsChallenge(str);
        } catch (RemoteException unused) {
            Log.w(TAG, "Could not check restrictions pin");
            return -3;
        }
    }

    public boolean hasRestrictionsChallenge() {
        try {
            return this.mService.hasRestrictionsChallenge();
        } catch (RemoteException unused) {
            Log.w(TAG, "Could not change restrictions pin");
            return false;
        }
    }

    public void removeRestrictions() {
        try {
            this.mService.removeRestrictions();
        } catch (RemoteException unused) {
            Log.w(TAG, "Could not change restrictions pin");
        }
    }
}
