package android.accounts;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class ChooseAccountActivity extends Activity {
    private static final String TAG = "AccountManager";
    private Bundle mResult;
    private Parcelable[] mAccounts = null;
    private AccountManagerResponse mAccountManagerResponse = null;
    private HashMap<String, AuthenticatorDescription> mTypeToAuthDescription = new HashMap<>();

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mAccounts = getIntent().getParcelableArrayExtra(AccountManager.KEY_ACCOUNTS);
        this.mAccountManagerResponse = (AccountManagerResponse) getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_MANAGER_RESPONSE);
        if (this.mAccounts == null) {
            setResult(0);
            finish();
            return;
        }
        getAuthDescriptions();
        AccountInfo[] accountInfoArr = new AccountInfo[this.mAccounts.length];
        for (int i = 0; i < this.mAccounts.length; i++) {
            accountInfoArr[i] = new AccountInfo(((Account) this.mAccounts[i]).name, getDrawableForType(((Account) this.mAccounts[i]).type));
        }
        setContentView(17367092);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter((ListAdapter) new AccountArrayAdapter(this, R.layout.simple_list_item_1, accountInfoArr));
        listView.setChoiceMode(1);
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: android.accounts.ChooseAccountActivity.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i2, long j) {
                ChooseAccountActivity.this.onListItemClick((ListView) adapterView, view, i2, j);
            }
        });
    }

    private void getAuthDescriptions() {
        for (AuthenticatorDescription authenticatorDescription : AccountManager.get(this).getAuthenticatorTypes()) {
            this.mTypeToAuthDescription.put(authenticatorDescription.type, authenticatorDescription);
        }
    }

    private Drawable getDrawableForType(String str) {
        if (this.mTypeToAuthDescription.containsKey(str)) {
            try {
                AuthenticatorDescription authenticatorDescription = this.mTypeToAuthDescription.get(str);
                return createPackageContext(authenticatorDescription.packageName, 0).getResources().getDrawable(authenticatorDescription.iconId);
            } catch (PackageManager.NameNotFoundException unused) {
                if (Log.isLoggable(TAG, 5)) {
                    Log.w(TAG, "No icon name for account type " + str);
                }
            } catch (Resources.NotFoundException unused2) {
                if (Log.isLoggable(TAG, 5)) {
                    Log.w(TAG, "No icon resource for account type " + str);
                }
            }
        }
        return null;
    }

    protected void onListItemClick(ListView listView, View view, int i, long j) {
        Account account = (Account) this.mAccounts[i];
        Log.d(TAG, "selected account " + account);
        Bundle bundle = new Bundle();
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        bundle.putString("accountType", account.type);
        this.mResult = bundle;
        finish();
    }

    @Override // android.app.Activity
    public void finish() {
        AccountManagerResponse accountManagerResponse = this.mAccountManagerResponse;
        if (accountManagerResponse != null) {
            Bundle bundle = this.mResult;
            if (bundle != null) {
                accountManagerResponse.onResult(bundle);
            } else {
                accountManagerResponse.onError(4, "canceled");
            }
        }
        super.finish();
    }

    private static class AccountInfo {
        final Drawable drawable;
        final String name;

        AccountInfo(String str, Drawable drawable) {
            this.name = str;
            this.drawable = drawable;
        }
    }

    private static class ViewHolder {
        ImageView icon;
        TextView text;

        private ViewHolder() {
        }
    }

    private static class AccountArrayAdapter extends ArrayAdapter<AccountInfo> {
        private AccountInfo[] mInfos;
        private LayoutInflater mLayoutInflater;

        public AccountArrayAdapter(Context context, int i, AccountInfo[] accountInfoArr) {
            super(context, i, accountInfoArr);
            this.mInfos = accountInfoArr;
            this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = this.mLayoutInflater.inflate(17367093, (ViewGroup) null);
                viewHolder = new ViewHolder();
                viewHolder.text = (TextView) view.findViewById(16908950);
                viewHolder.icon = (ImageView) view.findViewById(16908949);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.text.setText(this.mInfos[i].name);
            viewHolder.icon.setImageDrawable(this.mInfos[i].drawable);
            return view;
        }
    }
}
