package android.accounts;

import android.R;
import android.app.Activity;
import android.app.ActivityManagerNative;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.collect.Sets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class ChooseTypeAndAccountActivity extends Activity implements AccountManagerCallback<Bundle> {
    public static final String EXTRA_ADD_ACCOUNT_AUTH_TOKEN_TYPE_STRING = "authTokenType";
    public static final String EXTRA_ADD_ACCOUNT_OPTIONS_BUNDLE = "addAccountOptions";
    public static final String EXTRA_ADD_ACCOUNT_REQUIRED_FEATURES_STRING_ARRAY = "addAccountRequiredFeatures";
    public static final String EXTRA_ALLOWABLE_ACCOUNTS_ARRAYLIST = "allowableAccounts";
    public static final String EXTRA_ALLOWABLE_ACCOUNT_TYPES_STRING_ARRAY = "allowableAccountTypes";
    public static final String EXTRA_ALWAYS_PROMPT_FOR_ACCOUNT = "alwaysPromptForAccount";
    public static final String EXTRA_DESCRIPTION_TEXT_OVERRIDE = "descriptionTextOverride";
    public static final String EXTRA_SELECTED_ACCOUNT = "selectedAccount";
    private static final String KEY_INSTANCE_STATE_ACCOUNT_LIST = "accountList";
    private static final String KEY_INSTANCE_STATE_EXISTING_ACCOUNTS = "existingAccounts";
    private static final String KEY_INSTANCE_STATE_PENDING_REQUEST = "pendingRequest";
    private static final String KEY_INSTANCE_STATE_SELECTED_ACCOUNT_NAME = "selectedAccountName";
    private static final String KEY_INSTANCE_STATE_SELECTED_ADD_ACCOUNT = "selectedAddAccount";
    public static final int REQUEST_ADD_ACCOUNT = 2;
    public static final int REQUEST_CHOOSE_TYPE = 1;
    public static final int REQUEST_NULL = 0;
    private static final int SELECTED_ITEM_NONE = -1;
    private static final String TAG = "AccountChooser";
    private ArrayList<Account> mAccounts;
    private String mCallingPackage;
    private int mCallingUid;
    private String mDescriptionOverride;
    private boolean mDisallowAddAccounts;
    private boolean mDontShowPicker;
    private Button mOkButton;
    private int mSelectedItemIndex;
    private Set<Account> mSetOfAllowableAccounts;
    private Set<String> mSetOfRelevantAccountTypes;
    private String mSelectedAccountName = null;
    private boolean mSelectedAddNewAccount = false;
    private boolean mAlwaysPromptForAccount = false;
    private int mPendingRequest = 0;
    private Parcelable[] mExistingAccounts = null;

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "ChooseTypeAndAccountActivity.onCreate(savedInstanceState=" + bundle + ")");
        }
        try {
            IBinder activityToken = getActivityToken();
            this.mCallingUid = ActivityManagerNative.getDefault().getLaunchedFromUid(activityToken);
            String launchedFromPackage = ActivityManagerNative.getDefault().getLaunchedFromPackage(activityToken);
            this.mCallingPackage = launchedFromPackage;
            if (this.mCallingUid != 0 && launchedFromPackage != null) {
                this.mDisallowAddAccounts = UserManager.get(this).getUserRestrictions(new UserHandle(UserHandle.getUserId(this.mCallingUid))).getBoolean(UserManager.DISALLOW_MODIFY_ACCOUNTS, false);
            }
        } catch (RemoteException e) {
            Log.w(getClass().getSimpleName(), "Unable to get caller identity \n" + e);
        }
        Intent intent = getIntent();
        if (bundle != null) {
            this.mPendingRequest = bundle.getInt(KEY_INSTANCE_STATE_PENDING_REQUEST);
            this.mExistingAccounts = bundle.getParcelableArray(KEY_INSTANCE_STATE_EXISTING_ACCOUNTS);
            this.mSelectedAccountName = bundle.getString(KEY_INSTANCE_STATE_SELECTED_ACCOUNT_NAME);
            this.mSelectedAddNewAccount = bundle.getBoolean(KEY_INSTANCE_STATE_SELECTED_ADD_ACCOUNT, false);
            this.mAccounts = bundle.getParcelableArrayList(KEY_INSTANCE_STATE_ACCOUNT_LIST);
        } else {
            this.mPendingRequest = 0;
            this.mExistingAccounts = null;
            Account account = (Account) intent.getParcelableExtra(EXTRA_SELECTED_ACCOUNT);
            if (account != null) {
                this.mSelectedAccountName = account.name;
            }
        }
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "selected account name is " + this.mSelectedAccountName);
        }
        this.mSetOfAllowableAccounts = getAllowableAccountSet(intent);
        this.mSetOfRelevantAccountTypes = getReleventAccountTypes(intent);
        this.mAlwaysPromptForAccount = intent.getBooleanExtra(EXTRA_ALWAYS_PROMPT_FOR_ACCOUNT, false);
        this.mDescriptionOverride = intent.getStringExtra(EXTRA_DESCRIPTION_TEXT_OVERRIDE);
        ArrayList<Account> acceptableAccountChoices = getAcceptableAccountChoices(AccountManager.get(this));
        this.mAccounts = acceptableAccountChoices;
        if (acceptableAccountChoices.isEmpty() && this.mDisallowAddAccounts) {
            requestWindowFeature(1);
            setContentView(17367082);
            this.mDontShowPicker = true;
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        if (this.mDontShowPicker) {
            return;
        }
        ArrayList<Account> acceptableAccountChoices = getAcceptableAccountChoices(AccountManager.get(this));
        this.mAccounts = acceptableAccountChoices;
        if (this.mPendingRequest == 0) {
            if (acceptableAccountChoices.isEmpty()) {
                if (this.mSetOfRelevantAccountTypes.size() == 1) {
                    runAddAccountForAuthenticator(this.mSetOfRelevantAccountTypes.iterator().next());
                    return;
                } else {
                    startChooseAccountTypeActivity();
                    return;
                }
            }
            if (!this.mAlwaysPromptForAccount && this.mAccounts.size() == 1) {
                Account account = this.mAccounts.get(0);
                setResultAndFinish(account.name, account.type);
                return;
            }
        }
        String[] listOfDisplayableOptions = getListOfDisplayableOptions(this.mAccounts);
        this.mSelectedItemIndex = getItemIndexToSelect(this.mAccounts, this.mSelectedAccountName, this.mSelectedAddNewAccount);
        setContentView(17367095);
        overrideDescriptionIfSupplied(this.mDescriptionOverride);
        populateUIAccountList(listOfDisplayableOptions);
        Button button = (Button) findViewById(R.id.button2);
        this.mOkButton = button;
        button.setEnabled(this.mSelectedItemIndex != -1);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "ChooseTypeAndAccountActivity.onDestroy()");
        }
        super.onDestroy();
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(KEY_INSTANCE_STATE_PENDING_REQUEST, this.mPendingRequest);
        if (this.mPendingRequest == 2) {
            bundle.putParcelableArray(KEY_INSTANCE_STATE_EXISTING_ACCOUNTS, this.mExistingAccounts);
        }
        int i = this.mSelectedItemIndex;
        if (i != -1) {
            if (i == this.mAccounts.size()) {
                bundle.putBoolean(KEY_INSTANCE_STATE_SELECTED_ADD_ACCOUNT, true);
            } else {
                bundle.putBoolean(KEY_INSTANCE_STATE_SELECTED_ADD_ACCOUNT, false);
                bundle.putString(KEY_INSTANCE_STATE_SELECTED_ACCOUNT_NAME, this.mAccounts.get(this.mSelectedItemIndex).name);
            }
        }
        bundle.putParcelableArrayList(KEY_INSTANCE_STATE_ACCOUNT_LIST, this.mAccounts);
    }

    public void onCancelButtonClicked(View view) {
        onBackPressed();
    }

    public void onOkButtonClicked(View view) {
        if (this.mSelectedItemIndex == this.mAccounts.size()) {
            startChooseAccountTypeActivity();
            return;
        }
        int i = this.mSelectedItemIndex;
        if (i != -1) {
            onAccountSelected(this.mAccounts.get(i));
        }
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        String stringExtra;
        String stringExtra2;
        String stringExtra3 = null;
        if (Log.isLoggable(TAG, 2)) {
            if (intent != null && intent.getExtras() != null) {
                intent.getExtras().keySet();
            }
            Log.v(TAG, "ChooseTypeAndAccountActivity.onActivityResult(reqCode=" + i + ", resCode=" + i2 + ", extras=" + (intent != null ? intent.getExtras() : null) + ")");
        }
        this.mPendingRequest = 0;
        if (i2 == 0) {
            if (this.mAccounts.isEmpty()) {
                setResult(0);
                finish();
                return;
            }
            return;
        }
        if (i2 == -1) {
            if (i == 1) {
                if (intent != null && (stringExtra2 = intent.getStringExtra("accountType")) != null) {
                    runAddAccountForAuthenticator(stringExtra2);
                    return;
                }
                Log.d(TAG, "ChooseTypeAndAccountActivity.onActivityResult: unable to find account type, pretending the request was canceled");
            } else if (i == 2) {
                if (intent != null) {
                    stringExtra3 = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    stringExtra = intent.getStringExtra("accountType");
                } else {
                    stringExtra = null;
                }
                if (stringExtra3 == null || stringExtra == null) {
                    Account[] accountsForPackage = AccountManager.get(this).getAccountsForPackage(this.mCallingPackage, this.mCallingUid);
                    HashSet hashSet = new HashSet();
                    for (Parcelable parcelable : this.mExistingAccounts) {
                        hashSet.add((Account) parcelable);
                    }
                    int length = accountsForPackage.length;
                    int i3 = 0;
                    while (true) {
                        if (i3 >= length) {
                            break;
                        }
                        Account account = accountsForPackage[i3];
                        if (!hashSet.contains(account)) {
                            stringExtra3 = account.name;
                            stringExtra = account.type;
                            break;
                        }
                        i3++;
                    }
                }
                if (stringExtra3 != null || stringExtra != null) {
                    setResultAndFinish(stringExtra3, stringExtra);
                    return;
                }
            }
            Log.d(TAG, "ChooseTypeAndAccountActivity.onActivityResult: unable to find added account, pretending the request was canceled");
        }
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "ChooseTypeAndAccountActivity.onActivityResult: canceled");
        }
        setResult(0);
        finish();
    }

    protected void runAddAccountForAuthenticator(String str) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "runAddAccountForAuthenticator: " + str);
        }
        Bundle bundleExtra = getIntent().getBundleExtra(EXTRA_ADD_ACCOUNT_OPTIONS_BUNDLE);
        AccountManager.get(this).addAccount(str, getIntent().getStringExtra("authTokenType"), getIntent().getStringArrayExtra(EXTRA_ADD_ACCOUNT_REQUIRED_FEATURES_STRING_ARRAY), bundleExtra, null, this, null);
    }

    @Override // android.accounts.AccountManagerCallback
    public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
        try {
            Intent intent = (Intent) accountManagerFuture.getResult().getParcelable("intent");
            if (intent != null) {
                this.mPendingRequest = 2;
                this.mExistingAccounts = AccountManager.get(this).getAccountsForPackage(this.mCallingPackage, this.mCallingUid);
                intent.setFlags(intent.getFlags() & (-268435457));
                startActivityForResult(intent, 2);
                return;
            }
        } catch (AuthenticatorException | IOException unused) {
        } catch (OperationCanceledException unused2) {
            setResult(0);
            finish();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(AccountManager.KEY_ERROR_MESSAGE, "error communicating with server");
        setResult(-1, new Intent().putExtras(bundle));
        finish();
    }

    private void onAccountSelected(Account account) {
        Log.d(TAG, "selected account " + account);
        setResultAndFinish(account.name, account.type);
    }

    private void setResultAndFinish(String str, String str2) {
        Bundle bundle = new Bundle();
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, str);
        bundle.putString("accountType", str2);
        setResult(-1, new Intent().putExtras(bundle));
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "ChooseTypeAndAccountActivity.setResultAndFinish: selected account " + str + ", " + str2);
        }
        finish();
    }

    private void startChooseAccountTypeActivity() {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "ChooseAccountTypeActivity.startChooseAccountTypeActivity()");
        }
        Intent intent = new Intent(this, (Class<?>) ChooseAccountTypeActivity.class);
        intent.setFlags(524288);
        intent.putExtra(EXTRA_ALLOWABLE_ACCOUNT_TYPES_STRING_ARRAY, getIntent().getStringArrayExtra(EXTRA_ALLOWABLE_ACCOUNT_TYPES_STRING_ARRAY));
        intent.putExtra(EXTRA_ADD_ACCOUNT_OPTIONS_BUNDLE, getIntent().getBundleExtra(EXTRA_ADD_ACCOUNT_OPTIONS_BUNDLE));
        intent.putExtra(EXTRA_ADD_ACCOUNT_REQUIRED_FEATURES_STRING_ARRAY, getIntent().getStringArrayExtra(EXTRA_ADD_ACCOUNT_REQUIRED_FEATURES_STRING_ARRAY));
        intent.putExtra("authTokenType", getIntent().getStringExtra("authTokenType"));
        startActivityForResult(intent, 1);
        this.mPendingRequest = 1;
    }

    private int getItemIndexToSelect(ArrayList<Account> arrayList, String str, boolean z) {
        if (z) {
            return arrayList.size();
        }
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).name.equals(str)) {
                return i;
            }
        }
        return -1;
    }

    private String[] getListOfDisplayableOptions(ArrayList<Account> arrayList) {
        String[] strArr = new String[arrayList.size() + (!this.mDisallowAddAccounts ? 1 : 0)];
        for (int i = 0; i < arrayList.size(); i++) {
            strArr[i] = arrayList.get(i).name;
        }
        if (!this.mDisallowAddAccounts) {
            strArr[arrayList.size()] = getResources().getString(17040618);
        }
        return strArr;
    }

    private ArrayList<Account> getAcceptableAccountChoices(AccountManager accountManager) {
        Set<String> set;
        Account[] accountsForPackage = accountManager.getAccountsForPackage(this.mCallingPackage, this.mCallingUid);
        ArrayList<Account> arrayList = new ArrayList<>(accountsForPackage.length);
        for (Account account : accountsForPackage) {
            Set<Account> set2 = this.mSetOfAllowableAccounts;
            if ((set2 == null || set2.contains(account)) && ((set = this.mSetOfRelevantAccountTypes) == null || set.contains(account.type))) {
                arrayList.add(account);
            }
        }
        return arrayList;
    }

    private Set<String> getReleventAccountTypes(Intent intent) {
        String[] stringArrayExtra = intent.getStringArrayExtra(EXTRA_ALLOWABLE_ACCOUNT_TYPES_STRING_ARRAY);
        if (stringArrayExtra == null) {
            return null;
        }
        HashSet hashSetNewHashSet = Sets.newHashSet(stringArrayExtra);
        AuthenticatorDescription[] authenticatorTypes = AccountManager.get(this).getAuthenticatorTypes();
        HashSet hashSet = new HashSet(authenticatorTypes.length);
        for (AuthenticatorDescription authenticatorDescription : authenticatorTypes) {
            hashSet.add(authenticatorDescription.type);
        }
        hashSetNewHashSet.retainAll(hashSet);
        return hashSetNewHashSet;
    }

    private Set<Account> getAllowableAccountSet(Intent intent) {
        ArrayList parcelableArrayListExtra = intent.getParcelableArrayListExtra(EXTRA_ALLOWABLE_ACCOUNTS_ARRAYLIST);
        if (parcelableArrayListExtra == null) {
            return null;
        }
        HashSet hashSet = new HashSet(parcelableArrayListExtra.size());
        Iterator it = parcelableArrayListExtra.iterator();
        while (it.hasNext()) {
            hashSet.add((Account) ((Parcelable) it.next()));
        }
        return hashSet;
    }

    private void overrideDescriptionIfSupplied(String str) {
        TextView textView = (TextView) findViewById(16908934);
        if (!TextUtils.isEmpty(str)) {
            textView.setText(str);
        } else {
            textView.setVisibility(8);
        }
    }

    private final void populateUIAccountList(String[] strArr) {
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter((ListAdapter) new ArrayAdapter(this, R.layout.simple_list_item_single_choice, strArr));
        listView.setChoiceMode(1);
        listView.setItemsCanFocus(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: android.accounts.ChooseTypeAndAccountActivity.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ChooseTypeAndAccountActivity.this.mSelectedItemIndex = i;
                ChooseTypeAndAccountActivity.this.mOkButton.setEnabled(true);
            }
        });
        int i = this.mSelectedItemIndex;
        if (i != -1) {
            listView.setItemChecked(i, true);
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "List item " + this.mSelectedItemIndex + " should be selected");
            }
        }
    }
}
