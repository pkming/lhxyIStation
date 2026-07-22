package com.lianhexinye.m90.mvp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.language.LocalManageUtil;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;

/* JADX INFO: loaded from: classes2.dex */
public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    private List<Call> calls;
    public Activity mActivity;
    public ProgressDialog progressDialog;

    @Override // androidx.appcompat.app.AppCompatActivity, android.view.ContextThemeWrapper, android.content.ContextWrapper
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(LocalManageUtil.setLocal(context));
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mActivity = this;
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity
    public void setContentView(int i) {
        super.setContentView(i);
        this.mActivity = this;
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity
    public void setContentView(View view) {
        super.setContentView(view);
        this.mActivity = this;
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity
    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        super.setContentView(view, layoutParams);
        this.mActivity = this;
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        callCancel();
        super.onDestroy();
    }

    public void addCalls(Call call) {
        if (this.calls == null) {
            this.calls = new ArrayList();
        }
        this.calls.add(call);
    }

    private void callCancel() {
        List<Call> list = this.calls;
        if (list == null || list.size() <= 0) {
            return;
        }
        for (Call call : this.calls) {
            if (!call.isCanceled()) {
                call.cancel();
            }
        }
        this.calls.clear();
    }

    public Toolbar initToolBar(String str) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(str);
        }
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(false);
            supportActionBar.setDisplayShowTitleEnabled(false);
        }
        return toolbar;
    }

    public Toolbar initToolBarBack(String str) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(str);
            toolbar.setNavigationIcon(R.mipmap.icon_back_normal);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: com.lianhexinye.m90.mvp.BaseActivity.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    BaseActivity.this.mActivity.finish();
                }
            });
        }
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(false);
            supportActionBar.setDisplayShowTitleEnabled(false);
        }
        return toolbar;
    }

    public void toastShow(int i) {
        Toast.makeText(this.mActivity, i, 0).show();
    }

    public void toastShow(String str) {
        Toast.makeText(this.mActivity, str, 0).show();
    }

    public void showIntent(Activity activity, Class<?> cls) {
        showIntent(activity, cls, null, null);
    }

    public void showIntent(Activity activity, Class<?> cls, String[] strArr, String[] strArr2) {
        Intent intent = new Intent(activity, cls);
        if (strArr2 != null && strArr2.length > 0) {
            for (int i = 0; i < strArr2.length; i++) {
                if (!TextUtils.isEmpty(strArr[i]) && !TextUtils.isEmpty(strArr2[i])) {
                    intent.putExtra(strArr[i], strArr2[i]);
                }
            }
        }
        activity.startActivity(intent);
    }

    public void showIntentForResult(Activity activity, Class<?> cls, int i, String[] strArr, String[] strArr2) {
        Intent intent = new Intent(activity, cls);
        if (strArr2 != null && strArr2.length > 0) {
            for (int i2 = 0; i2 < strArr2.length; i2++) {
                if (!TextUtils.isEmpty(strArr[i2]) && !TextUtils.isEmpty(strArr2[i2])) {
                    intent.putExtra(strArr[i2], strArr2[i2]);
                }
            }
        }
        activity.startActivityForResult(intent, i);
    }

    public void showIntentForResult(Activity activity, Class<?> cls, int i) {
        showIntentForResult(activity, cls, i, null, null);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public ProgressDialog showProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this.mActivity);
        this.progressDialog = progressDialog;
        progressDialog.setMessage("加载中");
        this.progressDialog.show();
        return this.progressDialog;
    }

    public ProgressDialog showProgressDialog(CharSequence charSequence) {
        ProgressDialog progressDialog = new ProgressDialog(this.mActivity);
        this.progressDialog = progressDialog;
        progressDialog.setMessage(charSequence);
        this.progressDialog.show();
        return this.progressDialog;
    }

    public void dismissProgressDialog() {
        ProgressDialog progressDialog = this.progressDialog;
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        this.progressDialog.dismiss();
    }

    public boolean isShouldHideInput(View view, MotionEvent motionEvent) {
        if (view == null || !(view instanceof EditText)) {
            return false;
        }
        int[] iArr = {0, 0};
        view.getLocationInWindow(iArr);
        int i = iArr[0];
        int i2 = iArr[1];
        return motionEvent.getX() <= ((float) i) || motionEvent.getX() >= ((float) (view.getWidth() + i)) || motionEvent.getY() <= ((float) i2) || motionEvent.getY() >= ((float) (view.getHeight() + i2));
    }
}
