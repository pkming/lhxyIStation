package com.lianhexinye.m90.ui.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.KeyboardUtils;
import com.lianhexinye.m90.common.utils.PasswordManager;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.mvp.BasePresenter;
import com.lianhexinye.m90.mvp.BaseView;
import com.lianhexinye.m90.mvp.MvpActivity;
import com.rengwuxian.materialedittext.MaterialEditText;

/* JADX INFO: loaded from: classes2.dex */
public class LoginActivity extends MvpActivity<BaseView, BasePresenter> implements BaseView {
    private final String TAG = "LoginActivity";

    @BindView(R.id.butLogin)
    Button butLogin;

    @BindView(R.id.etUserName)
    MaterialEditText etUserName;

    @BindView(R.id.etUserPwd)
    MaterialEditText etUserPwd;

    @BindView(R.id.lyLoginRoot)
    LinearLayout lyLoginRoot;

    @BindView(R.id.lyUserName)
    LinearLayout lyUserName;

    @BindView(R.id.lyUserPwd)
    LinearLayout lyUserPwd;
    private String pwdUser;
    private PasswordManager pwdUserManager;

    @BindView(R.id.rlLoginRoot)
    RelativeLayout rlLoginRoot;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @Override // com.lianhexinye.m90.mvp.BaseView
    public void setPresenter(Object obj) {
    }

    @Override // com.lianhexinye.m90.mvp.MvpActivity, com.lianhexinye.m90.mvp.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
        PasswordManager passwordManager = new PasswordManager(this);
        this.pwdUserManager = passwordManager;
        this.pwdUser = passwordManager.getUserPassword();
        LogUtils.d("pwdUser", "用户密码：" + this.pwdUser);
    }

    private void initView() {
        initToolBarBack(getResources().getString(R.string.login_title));
        keepLoginBtnNotOver(this.lyLoginRoot, this.rlLoginRoot);
    }

    @OnClick({R.id.butLogin})
    public void onViewClicked() {
        if (this.etUserName.getText().toString().trim().length() == 0 || this.etUserPwd.getText().toString().trim().length() == 0) {
            toastShow(R.string.login_notnull_hint1);
            return;
        }
        String strTrim = this.etUserPwd.getText().toString().trim();
        if (this.pwdUserManager.checkPassword(strTrim)) {
            if (this.pwdUserManager.isSuperPassword(strTrim)) {
                toastShow("Success: Super Password");
                AppApplication.isUserPassword = false;
            } else {
                toastShow("Success: User Password");
                AppApplication.isUserPassword = true;
            }
            showIntent(this, MenuActivity.class);
            finish();
            return;
        }
        toastShow(R.string.login_error_hint1);
    }

    private void keepLoginBtnNotOver(final View view, final View view2) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.lianhexinye.m90.ui.activity.LoginActivity.1
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                Rect rect = new Rect();
                view.getWindowVisibleDisplayFrame(rect);
                int height = view.getRootView().getHeight() - rect.bottom;
                if (height > 200) {
                    int height2 = (height - (view.getHeight() - view2.getHeight())) - KeyboardUtils.getNavigationBarHeight(view.getContext());
                    if (height2 > 0) {
                        view.scrollTo(0, height2);
                        return;
                    }
                    return;
                }
                view.scrollTo(0, 0);
            }
        });
    }

    @Override // com.lianhexinye.m90.mvp.MvpActivity
    protected BasePresenter createPresenter() {
        return new BasePresenter();
    }

    @Override // com.lianhexinye.m90.mvp.MvpActivity, com.lianhexinye.m90.mvp.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
    }
}
