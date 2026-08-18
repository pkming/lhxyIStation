package com.lianhexinye.m90.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;
import com.rengwuxian.materialedittext.MaterialEditText;

/* JADX INFO: loaded from: classes2.dex */
public class LoginActivity_ViewBinding implements Unbinder {
    private LoginActivity target;
    private View view7f09005b;

    public LoginActivity_ViewBinding(LoginActivity loginActivity) {
        this(loginActivity, loginActivity.getWindow().getDecorView());
    }

    public LoginActivity_ViewBinding(final LoginActivity loginActivity, View view) {
        this.target = loginActivity;
        loginActivity.toolbarTitle = (TextView) Utils.findRequiredViewAsType(view, R.id.toolbar_title, "field 'toolbarTitle'", TextView.class);
        loginActivity.toolbar = (Toolbar) Utils.findRequiredViewAsType(view, R.id.toolbar, "field 'toolbar'", Toolbar.class);
        loginActivity.etUserName = (MaterialEditText) Utils.findRequiredViewAsType(view, R.id.etUserName, "field 'etUserName'", MaterialEditText.class);
        loginActivity.lyUserName = (LinearLayout) Utils.findRequiredViewAsType(view, R.id.lyUserName, "field 'lyUserName'", LinearLayout.class);
        loginActivity.etUserPwd = (MaterialEditText) Utils.findRequiredViewAsType(view, R.id.etUserPwd, "field 'etUserPwd'", MaterialEditText.class);
        loginActivity.lyUserPwd = (LinearLayout) Utils.findRequiredViewAsType(view, R.id.lyUserPwd, "field 'lyUserPwd'", LinearLayout.class);
        View viewFindRequiredView = Utils.findRequiredView(view, R.id.butLogin, "field 'butLogin' and method 'onViewClicked'");
        loginActivity.butLogin = (Button) Utils.castView(viewFindRequiredView, R.id.butLogin, "field 'butLogin'", Button.class);
        this.view7f09005b = viewFindRequiredView;
        viewFindRequiredView.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.LoginActivity_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                loginActivity.onViewClicked();
            }
        });
        loginActivity.lyLoginRoot = (LinearLayout) Utils.findRequiredViewAsType(view, R.id.lyLoginRoot, "field 'lyLoginRoot'", LinearLayout.class);
        loginActivity.rlLoginRoot = (RelativeLayout) Utils.findRequiredViewAsType(view, R.id.rlLoginRoot, "field 'rlLoginRoot'", RelativeLayout.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        LoginActivity loginActivity = this.target;
        if (loginActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        loginActivity.toolbarTitle = null;
        loginActivity.toolbar = null;
        loginActivity.etUserName = null;
        loginActivity.lyUserName = null;
        loginActivity.etUserPwd = null;
        loginActivity.lyUserPwd = null;
        loginActivity.butLogin = null;
        loginActivity.lyLoginRoot = null;
        loginActivity.rlLoginRoot = null;
        this.view7f09005b.setOnClickListener(null);
        this.view7f09005b = null;
    }
}
