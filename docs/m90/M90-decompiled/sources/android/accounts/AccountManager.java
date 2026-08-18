package android.accounts;

import android.accounts.IAccountManagerResponse;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.collect.Maps;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* JADX INFO: loaded from: classes.dex */
public class AccountManager {
    public static final String ACTION_AUTHENTICATOR_INTENT = "android.accounts.AccountAuthenticator";
    public static final String AUTHENTICATOR_ATTRIBUTES_NAME = "account-authenticator";
    public static final String AUTHENTICATOR_META_DATA_NAME = "android.accounts.AccountAuthenticator";
    public static final int ERROR_CODE_BAD_ARGUMENTS = 7;
    public static final int ERROR_CODE_BAD_AUTHENTICATION = 9;
    public static final int ERROR_CODE_BAD_REQUEST = 8;
    public static final int ERROR_CODE_CANCELED = 4;
    public static final int ERROR_CODE_INVALID_RESPONSE = 5;
    public static final int ERROR_CODE_NETWORK_ERROR = 3;
    public static final int ERROR_CODE_REMOTE_EXCEPTION = 1;
    public static final int ERROR_CODE_UNSUPPORTED_OPERATION = 6;
    public static final int ERROR_CODE_USER_RESTRICTED = 100;
    public static final String KEY_ACCOUNTS = "accounts";
    public static final String KEY_ACCOUNT_AUTHENTICATOR_RESPONSE = "accountAuthenticatorResponse";
    public static final String KEY_ACCOUNT_MANAGER_RESPONSE = "accountManagerResponse";
    public static final String KEY_ACCOUNT_NAME = "authAccount";
    public static final String KEY_ACCOUNT_TYPE = "accountType";
    public static final String KEY_ANDROID_PACKAGE_NAME = "androidPackageName";
    public static final String KEY_AUTHENTICATOR_TYPES = "authenticator_types";
    public static final String KEY_AUTHTOKEN = "authtoken";
    public static final String KEY_AUTH_FAILED_MESSAGE = "authFailedMessage";
    public static final String KEY_AUTH_TOKEN_LABEL = "authTokenLabelKey";
    public static final String KEY_BOOLEAN_RESULT = "booleanResult";
    public static final String KEY_CALLER_PID = "callerPid";
    public static final String KEY_CALLER_UID = "callerUid";
    public static final String KEY_ERROR_CODE = "errorCode";
    public static final String KEY_ERROR_MESSAGE = "errorMessage";
    public static final String KEY_INTENT = "intent";
    public static final String KEY_NOTIFY_ON_FAILURE = "notifyOnAuthFailure";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_USERDATA = "userdata";
    public static final String LOGIN_ACCOUNTS_CHANGED_ACTION = "android.accounts.LOGIN_ACCOUNTS_CHANGED";
    private static final String TAG = "AccountManager";
    private final Context mContext;
    private final Handler mMainHandler;
    private final IAccountManager mService;
    private final HashMap<OnAccountsUpdateListener, Handler> mAccountsUpdatedListeners = Maps.newHashMap();
    private final BroadcastReceiver mAccountsChangedBroadcastReceiver = new BroadcastReceiver() { // from class: android.accounts.AccountManager.13
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Account[] accounts = AccountManager.this.getAccounts();
            synchronized (AccountManager.this.mAccountsUpdatedListeners) {
                for (Map.Entry entry : AccountManager.this.mAccountsUpdatedListeners.entrySet()) {
                    AccountManager.this.postToHandler((Handler) entry.getValue(), (OnAccountsUpdateListener) entry.getKey(), accounts);
                }
            }
        }
    };

    public AccountManager(Context context, IAccountManager iAccountManager) {
        this.mContext = context;
        this.mService = iAccountManager;
        this.mMainHandler = new Handler(context.getMainLooper());
    }

    public AccountManager(Context context, IAccountManager iAccountManager, Handler handler) {
        this.mContext = context;
        this.mService = iAccountManager;
        this.mMainHandler = handler;
    }

    public static Bundle sanitizeResult(Bundle bundle) {
        if (bundle == null || !bundle.containsKey(KEY_AUTHTOKEN) || TextUtils.isEmpty(bundle.getString(KEY_AUTHTOKEN))) {
            return bundle;
        }
        Bundle bundle2 = new Bundle(bundle);
        bundle2.putString(KEY_AUTHTOKEN, "<omitted for logging purposes>");
        return bundle2;
    }

    public static AccountManager get(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        return (AccountManager) context.getSystemService("account");
    }

    public String getPassword(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        try {
            return this.mService.getPassword(account);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUserData(Account account, String str) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        if (str == null) {
            throw new IllegalArgumentException("key is null");
        }
        try {
            return this.mService.getUserData(account, str);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public AuthenticatorDescription[] getAuthenticatorTypes() {
        try {
            return this.mService.getAuthenticatorTypes();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Account[] getAccounts() {
        try {
            return this.mService.getAccounts(null);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Account[] getAccountsForPackage(String str, int i) {
        try {
            return this.mService.getAccountsForPackage(str, i);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Account[] getAccountsByTypeForPackage(String str, String str2) {
        try {
            return this.mService.getAccountsByTypeForPackage(str, str2);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Account[] getAccountsByType(String str) {
        return getAccountsByTypeAsUser(str, Process.myUserHandle());
    }

    public Account[] getAccountsByTypeAsUser(String str, UserHandle userHandle) {
        try {
            return this.mService.getAccountsAsUser(str, userHandle.getIdentifier());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateAppPermission(Account account, String str, int i, boolean z) {
        try {
            this.mService.updateAppPermission(account, str, i, z);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public AccountManagerFuture<String> getAuthTokenLabel(final String str, final String str2, AccountManagerCallback<String> accountManagerCallback, Handler handler) {
        if (str == null) {
            throw new IllegalArgumentException("accountType is null");
        }
        if (str2 == null) {
            throw new IllegalArgumentException("authTokenType is null");
        }
        return new Future2Task<String>(handler, accountManagerCallback) { // from class: android.accounts.AccountManager.1
            @Override // android.accounts.AccountManager.BaseFutureTask
            public void doWork() throws RemoteException {
                AccountManager.this.mService.getAuthTokenLabel(this.mResponse, str, str2);
            }

            @Override // android.accounts.AccountManager.BaseFutureTask
            public String bundleToResult(Bundle bundle) throws AuthenticatorException {
                if (!bundle.containsKey(AccountManager.KEY_AUTH_TOKEN_LABEL)) {
                    throw new AuthenticatorException("no result in response");
                }
                return bundle.getString(AccountManager.KEY_AUTH_TOKEN_LABEL);
            }
        }.start();
    }

    public AccountManagerFuture<Boolean> hasFeatures(final Account account, final String[] strArr, AccountManagerCallback<Boolean> accountManagerCallback, Handler handler) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        if (strArr == null) {
            throw new IllegalArgumentException("features is null");
        }
        return new Future2Task<Boolean>(handler, accountManagerCallback) { // from class: android.accounts.AccountManager.2
            @Override // android.accounts.AccountManager.BaseFutureTask
            public void doWork() throws RemoteException {
                AccountManager.this.mService.hasFeatures(this.mResponse, account, strArr);
            }

            @Override // android.accounts.AccountManager.BaseFutureTask
            public Boolean bundleToResult(Bundle bundle) throws AuthenticatorException {
                if (!bundle.containsKey(AccountManager.KEY_BOOLEAN_RESULT)) {
                    throw new AuthenticatorException("no result in response");
                }
                return Boolean.valueOf(bundle.getBoolean(AccountManager.KEY_BOOLEAN_RESULT));
            }
        }.start();
    }

    public AccountManagerFuture<Account[]> getAccountsByTypeAndFeatures(final String str, final String[] strArr, AccountManagerCallback<Account[]> accountManagerCallback, Handler handler) {
        if (str == null) {
            throw new IllegalArgumentException("type is null");
        }
        return new Future2Task<Account[]>(handler, accountManagerCallback) { // from class: android.accounts.AccountManager.3
            @Override // android.accounts.AccountManager.BaseFutureTask
            public void doWork() throws RemoteException {
                AccountManager.this.mService.getAccountsByFeatures(this.mResponse, str, strArr);
            }

            @Override // android.accounts.AccountManager.BaseFutureTask
            public Account[] bundleToResult(Bundle bundle) throws AuthenticatorException {
                if (!bundle.containsKey(AccountManager.KEY_ACCOUNTS)) {
                    throw new AuthenticatorException("no result in response");
                }
                Parcelable[] parcelableArray = bundle.getParcelableArray(AccountManager.KEY_ACCOUNTS);
                Account[] accountArr = new Account[parcelableArray.length];
                for (int i = 0; i < parcelableArray.length; i++) {
                    accountArr[i] = (Account) parcelableArray[i];
                }
                return accountArr;
            }
        }.start();
    }

    public boolean addAccountExplicitly(Account account, String str, Bundle bundle) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        try {
            return this.mService.addAccountExplicitly(account, str, bundle);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public AccountManagerFuture<Boolean> removeAccount(final Account account, AccountManagerCallback<Boolean> accountManagerCallback, Handler handler) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        return new Future2Task<Boolean>(handler, accountManagerCallback) { // from class: android.accounts.AccountManager.4
            @Override // android.accounts.AccountManager.BaseFutureTask
            public void doWork() throws RemoteException {
                AccountManager.this.mService.removeAccount(this.mResponse, account);
            }

            @Override // android.accounts.AccountManager.BaseFutureTask
            public Boolean bundleToResult(Bundle bundle) throws AuthenticatorException {
                if (!bundle.containsKey(AccountManager.KEY_BOOLEAN_RESULT)) {
                    throw new AuthenticatorException("no result in response");
                }
                return Boolean.valueOf(bundle.getBoolean(AccountManager.KEY_BOOLEAN_RESULT));
            }
        }.start();
    }

    public void invalidateAuthToken(String str, String str2) {
        if (str == null) {
            throw new IllegalArgumentException("accountType is null");
        }
        if (str2 != null) {
            try {
                this.mService.invalidateAuthToken(str, str2);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String peekAuthToken(Account account, String str) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        if (str == null) {
            throw new IllegalArgumentException("authTokenType is null");
        }
        try {
            return this.mService.peekAuthToken(account, str);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPassword(Account account, String str) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        try {
            this.mService.setPassword(account, str);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearPassword(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        try {
            this.mService.clearPassword(account);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void setUserData(Account account, String str, String str2) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        if (str == null) {
            throw new IllegalArgumentException("key is null");
        }
        try {
            this.mService.setUserData(account, str, str2);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void setAuthToken(Account account, String str, String str2) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        if (str == null) {
            throw new IllegalArgumentException("authTokenType is null");
        }
        try {
            this.mService.setAuthToken(account, str, str2);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public String blockingGetAuthToken(Account account, String str, boolean z) throws OperationCanceledException, IOException, AuthenticatorException {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        if (str == null) {
            throw new IllegalArgumentException("authTokenType is null");
        }
        Bundle result = getAuthToken(account, str, z, null, null).getResult();
        if (result == null) {
            Log.e(TAG, "blockingGetAuthToken: null was returned from getResult() for " + account + ", authTokenType " + str);
            return null;
        }
        return result.getString(KEY_AUTHTOKEN);
    }

    public AccountManagerFuture<Bundle> getAuthToken(final Account account, final String str, Bundle bundle, Activity activity, AccountManagerCallback<Bundle> accountManagerCallback, Handler handler) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        if (str == null) {
            throw new IllegalArgumentException("authTokenType is null");
        }
        final Bundle bundle2 = new Bundle();
        if (bundle != null) {
            bundle2.putAll(bundle);
        }
        bundle2.putString(KEY_ANDROID_PACKAGE_NAME, this.mContext.getPackageName());
        return new AmsTask(activity, handler, accountManagerCallback) { // from class: android.accounts.AccountManager.5
            @Override // android.accounts.AccountManager.AmsTask
            public void doWork() throws RemoteException {
                AccountManager.this.mService.getAuthToken(this.mResponse, account, str, false, true, bundle2);
            }
        }.start();
    }

    @Deprecated
    public AccountManagerFuture<Bundle> getAuthToken(Account account, String str, boolean z, AccountManagerCallback<Bundle> accountManagerCallback, Handler handler) {
        return getAuthToken(account, str, (Bundle) null, z, accountManagerCallback, handler);
    }

    public AccountManagerFuture<Bundle> getAuthToken(final Account account, final String str, Bundle bundle, final boolean z, AccountManagerCallback<Bundle> accountManagerCallback, Handler handler) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        if (str == null) {
            throw new IllegalArgumentException("authTokenType is null");
        }
        final Bundle bundle2 = new Bundle();
        if (bundle != null) {
            bundle2.putAll(bundle);
        }
        bundle2.putString(KEY_ANDROID_PACKAGE_NAME, this.mContext.getPackageName());
        return new AmsTask(null, handler, accountManagerCallback) { // from class: android.accounts.AccountManager.6
            @Override // android.accounts.AccountManager.AmsTask
            public void doWork() throws RemoteException {
                AccountManager.this.mService.getAuthToken(this.mResponse, account, str, z, false, bundle2);
            }
        }.start();
    }

    public AccountManagerFuture<Bundle> addAccount(final String str, final String str2, final String[] strArr, Bundle bundle, final Activity activity, AccountManagerCallback<Bundle> accountManagerCallback, Handler handler) {
        if (str == null) {
            throw new IllegalArgumentException("accountType is null");
        }
        final Bundle bundle2 = new Bundle();
        if (bundle != null) {
            bundle2.putAll(bundle);
        }
        bundle2.putString(KEY_ANDROID_PACKAGE_NAME, this.mContext.getPackageName());
        return new AmsTask(activity, handler, accountManagerCallback) { // from class: android.accounts.AccountManager.7
            @Override // android.accounts.AccountManager.AmsTask
            public void doWork() throws RemoteException {
                AccountManager.this.mService.addAccount(this.mResponse, str, str2, strArr, activity != null, bundle2);
            }
        }.start();
    }

    public boolean addSharedAccount(Account account, UserHandle userHandle) {
        try {
            return this.mService.addSharedAccountAsUser(account, userHandle.getIdentifier());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean removeSharedAccount(Account account, UserHandle userHandle) {
        try {
            return this.mService.removeSharedAccountAsUser(account, userHandle.getIdentifier());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Account[] getSharedAccounts(UserHandle userHandle) {
        try {
            return this.mService.getSharedAccountsAsUser(userHandle.getIdentifier());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public AccountManagerFuture<Bundle> confirmCredentials(Account account, Bundle bundle, Activity activity, AccountManagerCallback<Bundle> accountManagerCallback, Handler handler) {
        return confirmCredentialsAsUser(account, bundle, activity, accountManagerCallback, handler, Process.myUserHandle());
    }

    public AccountManagerFuture<Bundle> confirmCredentialsAsUser(final Account account, final Bundle bundle, final Activity activity, AccountManagerCallback<Bundle> accountManagerCallback, Handler handler, UserHandle userHandle) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        final int identifier = userHandle.getIdentifier();
        return new AmsTask(activity, handler, accountManagerCallback) { // from class: android.accounts.AccountManager.8
            @Override // android.accounts.AccountManager.AmsTask
            public void doWork() throws RemoteException {
                AccountManager.this.mService.confirmCredentialsAsUser(this.mResponse, account, bundle, activity != null, identifier);
            }
        }.start();
    }

    public AccountManagerFuture<Bundle> updateCredentials(final Account account, final String str, final Bundle bundle, final Activity activity, AccountManagerCallback<Bundle> accountManagerCallback, Handler handler) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        return new AmsTask(activity, handler, accountManagerCallback) { // from class: android.accounts.AccountManager.9
            @Override // android.accounts.AccountManager.AmsTask
            public void doWork() throws RemoteException {
                AccountManager.this.mService.updateCredentials(this.mResponse, account, str, activity != null, bundle);
            }
        }.start();
    }

    public AccountManagerFuture<Bundle> editProperties(final String str, final Activity activity, AccountManagerCallback<Bundle> accountManagerCallback, Handler handler) {
        if (str == null) {
            throw new IllegalArgumentException("accountType is null");
        }
        return new AmsTask(activity, handler, accountManagerCallback) { // from class: android.accounts.AccountManager.10
            @Override // android.accounts.AccountManager.AmsTask
            public void doWork() throws RemoteException {
                AccountManager.this.mService.editProperties(this.mResponse, str, activity != null);
            }
        }.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ensureNotOnMainThread() {
        Looper looperMyLooper = Looper.myLooper();
        if (looperMyLooper == null || looperMyLooper != this.mContext.getMainLooper()) {
            return;
        }
        IllegalStateException illegalStateException = new IllegalStateException("calling this from your main thread can lead to deadlock");
        Log.e(TAG, "calling this from your main thread can lead to deadlock and/or ANRs", illegalStateException);
        if (this.mContext.getApplicationInfo().targetSdkVersion >= 8) {
            throw illegalStateException;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postToHandler(Handler handler, final AccountManagerCallback<Bundle> accountManagerCallback, final AccountManagerFuture<Bundle> accountManagerFuture) {
        if (handler == null) {
            handler = this.mMainHandler;
        }
        handler.post(new Runnable() { // from class: android.accounts.AccountManager.11
            @Override // java.lang.Runnable
            public void run() {
                accountManagerCallback.run(accountManagerFuture);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postToHandler(Handler handler, final OnAccountsUpdateListener onAccountsUpdateListener, Account[] accountArr) {
        int length = accountArr.length;
        final Account[] accountArr2 = new Account[length];
        System.arraycopy(accountArr, 0, accountArr2, 0, length);
        if (handler == null) {
            handler = this.mMainHandler;
        }
        handler.post(new Runnable() { // from class: android.accounts.AccountManager.12
            @Override // java.lang.Runnable
            public void run() {
                try {
                    onAccountsUpdateListener.onAccountsUpdated(accountArr2);
                } catch (SQLException e) {
                    Log.e(AccountManager.TAG, "Can't update accounts", e);
                }
            }
        });
    }

    private abstract class AmsTask extends FutureTask<Bundle> implements AccountManagerFuture<Bundle> {
        final Activity mActivity;
        final AccountManagerCallback<Bundle> mCallback;
        final Handler mHandler;
        final IAccountManagerResponse mResponse;

        public abstract void doWork() throws RemoteException;

        public AmsTask(Activity activity, Handler handler, AccountManagerCallback<Bundle> accountManagerCallback) {
            super(new Callable<Bundle>() { // from class: android.accounts.AccountManager.AmsTask.1
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Bundle call() throws Exception {
                    throw new IllegalStateException("this should never be called");
                }
            });
            this.mHandler = handler;
            this.mCallback = accountManagerCallback;
            this.mActivity = activity;
            this.mResponse = new Response();
        }

        public final AccountManagerFuture<Bundle> start() {
            try {
                doWork();
            } catch (RemoteException e) {
                setException(e);
            }
            return this;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.util.concurrent.FutureTask
        public void set(Bundle bundle) {
            if (bundle == null) {
                Log.e(AccountManager.TAG, "the bundle must not be null", new Exception());
            }
            super.set(bundle);
        }

        private Bundle internalGetResult(Long l, TimeUnit timeUnit) throws OperationCanceledException, IOException, AuthenticatorException {
            if (!isDone()) {
                AccountManager.this.ensureNotOnMainThread();
            }
            try {
                try {
                    try {
                        return l == null ? get() : get(l.longValue(), timeUnit);
                    } finally {
                        cancel(true);
                    }
                } catch (InterruptedException | TimeoutException unused) {
                    cancel(true);
                    throw new OperationCanceledException();
                }
            } catch (CancellationException unused2) {
                throw new OperationCanceledException();
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof IOException) {
                    throw ((IOException) cause);
                }
                if (cause instanceof UnsupportedOperationException) {
                    throw new AuthenticatorException(cause);
                }
                if (cause instanceof AuthenticatorException) {
                    throw ((AuthenticatorException) cause);
                }
                if (cause instanceof RuntimeException) {
                    throw ((RuntimeException) cause);
                }
                if (cause instanceof Error) {
                    throw ((Error) cause);
                }
                throw new IllegalStateException(cause);
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.accounts.AccountManagerFuture
        public Bundle getResult() throws OperationCanceledException, IOException, AuthenticatorException {
            return internalGetResult(null, null);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.accounts.AccountManagerFuture
        public Bundle getResult(long j, TimeUnit timeUnit) throws OperationCanceledException, IOException, AuthenticatorException {
            return internalGetResult(Long.valueOf(j), timeUnit);
        }

        @Override // java.util.concurrent.FutureTask
        protected void done() {
            AccountManagerCallback<Bundle> accountManagerCallback = this.mCallback;
            if (accountManagerCallback != null) {
                AccountManager.this.postToHandler(this.mHandler, accountManagerCallback, this);
            }
        }

        private class Response extends IAccountManagerResponse.Stub {
            private Response() {
            }

            @Override // android.accounts.IAccountManagerResponse
            public void onResult(Bundle bundle) {
                Intent intent = (Intent) bundle.getParcelable("intent");
                if (intent != null && AmsTask.this.mActivity != null) {
                    AmsTask.this.mActivity.startActivity(intent);
                } else if (bundle.getBoolean("retry")) {
                    try {
                        AmsTask.this.doWork();
                    } catch (RemoteException unused) {
                    }
                } else {
                    AmsTask.this.set(bundle);
                }
            }

            @Override // android.accounts.IAccountManagerResponse
            public void onError(int i, String str) {
                if (i == 4 || i == 100) {
                    AmsTask.this.cancel(true);
                } else {
                    AmsTask amsTask = AmsTask.this;
                    amsTask.setException(AccountManager.this.convertErrorToException(i, str));
                }
            }
        }
    }

    private abstract class BaseFutureTask<T> extends FutureTask<T> {
        final Handler mHandler;
        public final IAccountManagerResponse mResponse;

        public abstract T bundleToResult(Bundle bundle) throws AuthenticatorException;

        public abstract void doWork() throws RemoteException;

        public BaseFutureTask(Handler handler) {
            super(new Callable<T>() { // from class: android.accounts.AccountManager.BaseFutureTask.1
                @Override // java.util.concurrent.Callable
                public T call() throws Exception {
                    throw new IllegalStateException("this should never be called");
                }
            });
            this.mHandler = handler;
            this.mResponse = new Response();
        }

        protected void postRunnableToHandler(Runnable runnable) {
            Handler handler = this.mHandler;
            if (handler == null) {
                handler = AccountManager.this.mMainHandler;
            }
            handler.post(runnable);
        }

        protected void startTask() {
            try {
                doWork();
            } catch (RemoteException e) {
                setException(e);
            }
        }

        protected class Response extends IAccountManagerResponse.Stub {
            protected Response() {
            }

            @Override // android.accounts.IAccountManagerResponse
            public void onResult(Bundle bundle) {
                try {
                    Object objBundleToResult = BaseFutureTask.this.bundleToResult(bundle);
                    if (objBundleToResult == null) {
                        return;
                    }
                    BaseFutureTask.this.set(objBundleToResult);
                } catch (AuthenticatorException | ClassCastException unused) {
                    onError(5, "no result in response");
                }
            }

            @Override // android.accounts.IAccountManagerResponse
            public void onError(int i, String str) {
                if (i == 4) {
                    BaseFutureTask.this.cancel(true);
                } else {
                    BaseFutureTask baseFutureTask = BaseFutureTask.this;
                    baseFutureTask.setException(AccountManager.this.convertErrorToException(i, str));
                }
            }
        }
    }

    private abstract class Future2Task<T> extends BaseFutureTask<T> implements AccountManagerFuture<T> {
        final AccountManagerCallback<T> mCallback;

        public Future2Task(Handler handler, AccountManagerCallback<T> accountManagerCallback) {
            super(handler);
            this.mCallback = accountManagerCallback;
        }

        @Override // java.util.concurrent.FutureTask
        protected void done() {
            if (this.mCallback != null) {
                postRunnableToHandler(new Runnable() { // from class: android.accounts.AccountManager.Future2Task.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Future2Task.this.mCallback.run(Future2Task.this);
                    }
                });
            }
        }

        public Future2Task<T> start() {
            startTask();
            return this;
        }

        private T internalGetResult(Long l, TimeUnit timeUnit) throws OperationCanceledException, IOException, AuthenticatorException {
            if (!isDone()) {
                AccountManager.this.ensureNotOnMainThread();
            }
            try {
                try {
                    try {
                        if (l == null) {
                            return (T) get();
                        }
                        return (T) get(l.longValue(), timeUnit);
                    } catch (InterruptedException | CancellationException | TimeoutException unused) {
                        cancel(true);
                        throw new OperationCanceledException();
                    }
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof IOException) {
                        throw ((IOException) cause);
                    }
                    if (cause instanceof UnsupportedOperationException) {
                        throw new AuthenticatorException(cause);
                    }
                    if (cause instanceof AuthenticatorException) {
                        throw ((AuthenticatorException) cause);
                    }
                    if (cause instanceof RuntimeException) {
                        throw ((RuntimeException) cause);
                    }
                    if (cause instanceof Error) {
                        throw ((Error) cause);
                    }
                    throw new IllegalStateException(cause);
                }
            } finally {
                cancel(true);
            }
        }

        @Override // android.accounts.AccountManagerFuture
        public T getResult() throws OperationCanceledException, IOException, AuthenticatorException {
            return internalGetResult(null, null);
        }

        @Override // android.accounts.AccountManagerFuture
        public T getResult(long j, TimeUnit timeUnit) throws OperationCanceledException, IOException, AuthenticatorException {
            return internalGetResult(Long.valueOf(j), timeUnit);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Exception convertErrorToException(int i, String str) {
        if (i == 3) {
            return new IOException(str);
        }
        if (i == 6) {
            return new UnsupportedOperationException(str);
        }
        if (i == 5) {
            return new AuthenticatorException(str);
        }
        if (i == 7) {
            return new IllegalArgumentException(str);
        }
        return new AuthenticatorException(str);
    }

    private class GetAuthTokenByTypeAndFeaturesTask extends AmsTask implements AccountManagerCallback<Bundle> {
        final String mAccountType;
        final Bundle mAddAccountOptions;
        final String mAuthTokenType;
        final String[] mFeatures;
        volatile AccountManagerFuture<Bundle> mFuture;
        final Bundle mLoginOptions;
        final AccountManagerCallback<Bundle> mMyCallback;
        private volatile int mNumAccounts;

        GetAuthTokenByTypeAndFeaturesTask(String str, String str2, String[] strArr, Activity activity, Bundle bundle, Bundle bundle2, AccountManagerCallback<Bundle> accountManagerCallback, Handler handler) {
            super(activity, handler, accountManagerCallback);
            this.mFuture = null;
            this.mNumAccounts = 0;
            if (str == null) {
                throw new IllegalArgumentException("account type is null");
            }
            this.mAccountType = str;
            this.mAuthTokenType = str2;
            this.mFeatures = strArr;
            this.mAddAccountOptions = bundle;
            this.mLoginOptions = bundle2;
            this.mMyCallback = this;
        }

        @Override // android.accounts.AccountManager.AmsTask
        public void doWork() throws RemoteException {
            AccountManager.this.getAccountsByTypeAndFeatures(this.mAccountType, this.mFeatures, new AccountManagerCallback<Account[]>() { // from class: android.accounts.AccountManager.GetAuthTokenByTypeAndFeaturesTask.1
                @Override // android.accounts.AccountManagerCallback
                public void run(AccountManagerFuture<Account[]> accountManagerFuture) {
                    try {
                        Account[] result = accountManagerFuture.getResult();
                        GetAuthTokenByTypeAndFeaturesTask.this.mNumAccounts = result.length;
                        try {
                            if (result.length == 0) {
                                if (GetAuthTokenByTypeAndFeaturesTask.this.mActivity != null) {
                                    GetAuthTokenByTypeAndFeaturesTask getAuthTokenByTypeAndFeaturesTask = GetAuthTokenByTypeAndFeaturesTask.this;
                                    getAuthTokenByTypeAndFeaturesTask.mFuture = AccountManager.this.addAccount(GetAuthTokenByTypeAndFeaturesTask.this.mAccountType, GetAuthTokenByTypeAndFeaturesTask.this.mAuthTokenType, GetAuthTokenByTypeAndFeaturesTask.this.mFeatures, GetAuthTokenByTypeAndFeaturesTask.this.mAddAccountOptions, GetAuthTokenByTypeAndFeaturesTask.this.mActivity, GetAuthTokenByTypeAndFeaturesTask.this.mMyCallback, GetAuthTokenByTypeAndFeaturesTask.this.mHandler);
                                } else {
                                    Bundle bundle = new Bundle();
                                    bundle.putString(AccountManager.KEY_ACCOUNT_NAME, null);
                                    bundle.putString("accountType", null);
                                    bundle.putString(AccountManager.KEY_AUTHTOKEN, null);
                                    GetAuthTokenByTypeAndFeaturesTask.this.mResponse.onResult(bundle);
                                }
                            } else {
                                if (result.length == 1) {
                                    if (GetAuthTokenByTypeAndFeaturesTask.this.mActivity == null) {
                                        GetAuthTokenByTypeAndFeaturesTask getAuthTokenByTypeAndFeaturesTask2 = GetAuthTokenByTypeAndFeaturesTask.this;
                                        getAuthTokenByTypeAndFeaturesTask2.mFuture = AccountManager.this.getAuthToken(result[0], GetAuthTokenByTypeAndFeaturesTask.this.mAuthTokenType, false, GetAuthTokenByTypeAndFeaturesTask.this.mMyCallback, GetAuthTokenByTypeAndFeaturesTask.this.mHandler);
                                        return;
                                    } else {
                                        GetAuthTokenByTypeAndFeaturesTask getAuthTokenByTypeAndFeaturesTask3 = GetAuthTokenByTypeAndFeaturesTask.this;
                                        getAuthTokenByTypeAndFeaturesTask3.mFuture = AccountManager.this.getAuthToken(result[0], GetAuthTokenByTypeAndFeaturesTask.this.mAuthTokenType, GetAuthTokenByTypeAndFeaturesTask.this.mLoginOptions, GetAuthTokenByTypeAndFeaturesTask.this.mActivity, GetAuthTokenByTypeAndFeaturesTask.this.mMyCallback, GetAuthTokenByTypeAndFeaturesTask.this.mHandler);
                                        return;
                                    }
                                }
                                if (GetAuthTokenByTypeAndFeaturesTask.this.mActivity != null) {
                                    IAccountManagerResponse.Stub stub = new IAccountManagerResponse.Stub() { // from class: android.accounts.AccountManager.GetAuthTokenByTypeAndFeaturesTask.1.1
                                        @Override // android.accounts.IAccountManagerResponse
                                        public void onResult(Bundle bundle2) throws RemoteException {
                                            Account account = new Account(bundle2.getString(AccountManager.KEY_ACCOUNT_NAME), bundle2.getString("accountType"));
                                            GetAuthTokenByTypeAndFeaturesTask.this.mFuture = AccountManager.this.getAuthToken(account, GetAuthTokenByTypeAndFeaturesTask.this.mAuthTokenType, GetAuthTokenByTypeAndFeaturesTask.this.mLoginOptions, GetAuthTokenByTypeAndFeaturesTask.this.mActivity, GetAuthTokenByTypeAndFeaturesTask.this.mMyCallback, GetAuthTokenByTypeAndFeaturesTask.this.mHandler);
                                        }

                                        @Override // android.accounts.IAccountManagerResponse
                                        public void onError(int i, String str) throws RemoteException {
                                            GetAuthTokenByTypeAndFeaturesTask.this.mResponse.onError(i, str);
                                        }
                                    };
                                    Intent intent = new Intent();
                                    ComponentName componentNameUnflattenFromString = ComponentName.unflattenFromString(Resources.getSystem().getString(17039415));
                                    intent.setClassName(componentNameUnflattenFromString.getPackageName(), componentNameUnflattenFromString.getClassName());
                                    intent.putExtra(AccountManager.KEY_ACCOUNTS, result);
                                    intent.putExtra(AccountManager.KEY_ACCOUNT_MANAGER_RESPONSE, new AccountManagerResponse(stub));
                                    GetAuthTokenByTypeAndFeaturesTask.this.mActivity.startActivity(intent);
                                    return;
                                }
                                Bundle bundle2 = new Bundle();
                                bundle2.putString(AccountManager.KEY_ACCOUNTS, null);
                                GetAuthTokenByTypeAndFeaturesTask.this.mResponse.onResult(bundle2);
                            }
                        } catch (RemoteException unused) {
                        }
                    } catch (AuthenticatorException e) {
                        GetAuthTokenByTypeAndFeaturesTask.this.setException(e);
                    } catch (OperationCanceledException e2) {
                        GetAuthTokenByTypeAndFeaturesTask.this.setException(e2);
                    } catch (IOException e3) {
                        GetAuthTokenByTypeAndFeaturesTask.this.setException(e3);
                    }
                }
            }, this.mHandler);
        }

        @Override // android.accounts.AccountManagerCallback
        public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
            try {
                Bundle result = accountManagerFuture.getResult();
                if (this.mNumAccounts == 0) {
                    String string = result.getString(AccountManager.KEY_ACCOUNT_NAME);
                    String string2 = result.getString("accountType");
                    if (!TextUtils.isEmpty(string) && !TextUtils.isEmpty(string2)) {
                        Account account = new Account(string, string2);
                        this.mNumAccounts = 1;
                        AccountManager.this.getAuthToken(account, this.mAuthTokenType, (Bundle) null, this.mActivity, this.mMyCallback, this.mHandler);
                        return;
                    }
                    setException(new AuthenticatorException("account not in result"));
                    return;
                }
                set(result);
            } catch (AuthenticatorException e) {
                setException(e);
            } catch (OperationCanceledException unused) {
                cancel(true);
            } catch (IOException e2) {
                setException(e2);
            }
        }
    }

    public AccountManagerFuture<Bundle> getAuthTokenByFeatures(String str, String str2, String[] strArr, Activity activity, Bundle bundle, Bundle bundle2, AccountManagerCallback<Bundle> accountManagerCallback, Handler handler) {
        if (str == null) {
            throw new IllegalArgumentException("account type is null");
        }
        if (str2 == null) {
            throw new IllegalArgumentException("authTokenType is null");
        }
        GetAuthTokenByTypeAndFeaturesTask getAuthTokenByTypeAndFeaturesTask = new GetAuthTokenByTypeAndFeaturesTask(str, str2, strArr, activity, bundle, bundle2, accountManagerCallback, handler);
        getAuthTokenByTypeAndFeaturesTask.start();
        return getAuthTokenByTypeAndFeaturesTask;
    }

    public static Intent newChooseAccountIntent(Account account, ArrayList<Account> arrayList, String[] strArr, boolean z, String str, String str2, String[] strArr2, Bundle bundle) {
        Intent intent = new Intent();
        ComponentName componentNameUnflattenFromString = ComponentName.unflattenFromString(Resources.getSystem().getString(17039416));
        intent.setClassName(componentNameUnflattenFromString.getPackageName(), componentNameUnflattenFromString.getClassName());
        intent.putExtra(ChooseTypeAndAccountActivity.EXTRA_ALLOWABLE_ACCOUNTS_ARRAYLIST, arrayList);
        intent.putExtra(ChooseTypeAndAccountActivity.EXTRA_ALLOWABLE_ACCOUNT_TYPES_STRING_ARRAY, strArr);
        intent.putExtra(ChooseTypeAndAccountActivity.EXTRA_ADD_ACCOUNT_OPTIONS_BUNDLE, bundle);
        intent.putExtra(ChooseTypeAndAccountActivity.EXTRA_SELECTED_ACCOUNT, account);
        intent.putExtra(ChooseTypeAndAccountActivity.EXTRA_ALWAYS_PROMPT_FOR_ACCOUNT, z);
        intent.putExtra(ChooseTypeAndAccountActivity.EXTRA_DESCRIPTION_TEXT_OVERRIDE, str);
        intent.putExtra("authTokenType", str2);
        intent.putExtra(ChooseTypeAndAccountActivity.EXTRA_ADD_ACCOUNT_REQUIRED_FEATURES_STRING_ARRAY, strArr2);
        return intent;
    }

    public void addOnAccountsUpdatedListener(OnAccountsUpdateListener onAccountsUpdateListener, Handler handler, boolean z) {
        if (onAccountsUpdateListener == null) {
            throw new IllegalArgumentException("the listener is null");
        }
        synchronized (this.mAccountsUpdatedListeners) {
            if (this.mAccountsUpdatedListeners.containsKey(onAccountsUpdateListener)) {
                throw new IllegalStateException("this listener is already added");
            }
            boolean zIsEmpty = this.mAccountsUpdatedListeners.isEmpty();
            this.mAccountsUpdatedListeners.put(onAccountsUpdateListener, handler);
            if (zIsEmpty) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(LOGIN_ACCOUNTS_CHANGED_ACTION);
                intentFilter.addAction(Intent.ACTION_DEVICE_STORAGE_OK);
                this.mContext.registerReceiver(this.mAccountsChangedBroadcastReceiver, intentFilter);
            }
        }
        if (z) {
            postToHandler(handler, onAccountsUpdateListener, getAccounts());
        }
    }

    public void removeOnAccountsUpdatedListener(OnAccountsUpdateListener onAccountsUpdateListener) {
        if (onAccountsUpdateListener == null) {
            throw new IllegalArgumentException("listener is null");
        }
        synchronized (this.mAccountsUpdatedListeners) {
            if (!this.mAccountsUpdatedListeners.containsKey(onAccountsUpdateListener)) {
                Log.e(TAG, "Listener was not previously added");
                return;
            }
            this.mAccountsUpdatedListeners.remove(onAccountsUpdateListener);
            if (this.mAccountsUpdatedListeners.isEmpty()) {
                this.mContext.unregisterReceiver(this.mAccountsChangedBroadcastReceiver);
            }
        }
    }
}
