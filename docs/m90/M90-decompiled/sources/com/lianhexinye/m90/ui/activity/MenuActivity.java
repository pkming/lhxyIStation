package com.lianhexinye.m90.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.mvp.MvpActivity;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import com.lianhexinye.m90.mvp.main.MainPresenter;
import com.lianhexinye.m90.mvp.main.MainView;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class MenuActivity extends MvpActivity<MainView, MainPresenter> implements MainView {

    @BindView(R.id.lyDispatchingCenter)
    RelativeLayout lyDispatchingCenter;

    @BindView(R.id.lyFileManage)
    RelativeLayout lyFileManage;

    @BindView(R.id.lyFinance1)
    LinearLayout lyFinance1;

    @BindView(R.id.lyInfoBrowsing)
    RelativeLayout lyInfoBrowsing;

    @BindView(R.id.lyLineSele)
    RelativeLayout lyLineSele;

    @BindView(R.id.lySiteLearn)
    RelativeLayout lySiteLearn;

    @BindView(R.id.lySysInfo)
    RelativeLayout lySysInfo;

    @BindView(R.id.lySystemSet)
    RelativeLayout lySystemSet;

    @BindView(R.id.lyVoiceCall)
    RelativeLayout lyVoiceCall;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.tvDispatchingCenter)
    TextView tvDispatchingCenter;

    @BindView(R.id.tvFileManage)
    TextView tvFileManage;

    @BindView(R.id.tvInfoBrowsing)
    TextView tvInfoBrowsing;

    @BindView(R.id.tvLineSele)
    TextView tvLineSele;

    @BindView(R.id.tvSiteLearn)
    TextView tvSiteLearn;

    @BindView(R.id.tvSysInfo)
    TextView tvSysInfo;

    @BindView(R.id.tvSystemSet)
    TextView tvSystemSet;

    @BindView(R.id.tvVoiceCall)
    TextView tvVoiceCall;
    private final String TAG = "MenuActivity";
    private final int REQUEST_SET_CODE = 1000;

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void authorizationResult(int i, String str) {
    }

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void getDataFail(String str) {
    }

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void getDataSuccess() {
    }

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void getLineNameDataSuccess2(List<LineNameModel> list) {
    }

    @Override // com.lianhexinye.m90.mvp.BaseView
    public void setPresenter(Object obj) {
    }

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void showLoading(String str) {
    }

    @Override // com.lianhexinye.m90.mvp.MvpActivity, com.lianhexinye.m90.mvp.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        initToolBarBack(getResources().getString(R.string.menu_title));
        new ImageView(this).setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
    }

    @OnClick({R.id.lyLineSele, R.id.lySiteLearn, R.id.lyFileManage, R.id.lySystemSet, R.id.lyVoiceCall, R.id.lyDispatchingCenter, R.id.lyInfoBrowsing, R.id.lySysInfo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lyDispatchingCenter /* 2131296515 */:
                showIntentForResult(this, DispatchCenterActivity.class, 1000);
                break;
            case R.id.lyFileManage /* 2131296518 */:
                if (!AppApplication.isUserPassword) {
                    showIntentForResult(this, FileManageActivity.class, 1000);
                } else {
                    toastShow("Please use the super password!");
                }
                break;
            case R.id.lyInfoBrowsing /* 2131296520 */:
                showIntentForResult(this, InfoBrowsActivity.class, 1000);
                break;
            case R.id.lyLineSele /* 2131296523 */:
                showIntentForResult(this, VideoMonitorActivity.class, 1000);
                break;
            case R.id.lySiteLearn /* 2131296531 */:
                showIntentForResult(this, SiteCollectionActivity.class, 1000);
                break;
            case R.id.lySysInfo /* 2131296533 */:
                showIntentForResult(this, SystemInfoActivity.class, 1000);
                break;
            case R.id.lySystemSet /* 2131296534 */:
                showIntentForResult(this, BasicSetupActivity.class, 1000);
                break;
            case R.id.lyVoiceCall /* 2131296541 */:
                showIntentForResult(this, PasswordActivity.class, 1000);
                break;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lianhexinye.m90.mvp.MvpActivity
    public MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1000 && i2 == -1) {
            finish();
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_index, menu);
        return true;
    }

    @Override // com.lianhexinye.m90.mvp.BaseActivity, android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != R.id.index) {
            return true;
        }
        finish();
        return true;
    }

    @Override // com.lianhexinye.m90.mvp.MvpActivity, com.lianhexinye.m90.mvp.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
    }
}
