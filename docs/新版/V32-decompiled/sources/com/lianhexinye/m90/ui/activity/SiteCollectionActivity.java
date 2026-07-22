package com.lianhexinye.m90.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.mvp.BaseFragment;
import com.lianhexinye.m90.mvp.MvpActivity;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import com.lianhexinye.m90.mvp.main.MainPresenter;
import com.lianhexinye.m90.mvp.main.MainView;
import com.lianhexinye.m90.ui.fragment.sitelearn.OtherCollectionFragment;
import com.lianhexinye.m90.ui.fragment.sitelearn.SiteCollectionFragment;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class SiteCollectionActivity extends MvpActivity<MainView, MainPresenter> implements MainView {

    @BindView(R.id.flSiteContext)
    FrameLayout flSiteContext;
    private FragmentManager fragmentManager;

    @BindView(R.id.rb_radio_other)
    RadioButton rbRadioOther;

    @BindView(R.id.rb_radio_site_learn)
    RadioButton rbRadioSiteLearn;

    @BindView(R.id.rg_radio_learn_navigation)
    RadioGroup rgRadioLearnNavigation;

    @BindView(R.id.rlToolbar)
    RelativeLayout rlToolbar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    private FragmentTransaction transaction;
    private final String TAG = "SiteCollectionActivity";
    BaseFragment baseFragment = null;
    private String tagShowFragment = "SiteCollectionFragment";

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void authorizationResult(int i, String str) {
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
        setContentView(R.layout.activity_site_collection);
        ButterKnife.bind(this);
        initView();
        AppApplication.gotoSiteCollection = true;
    }

    private void initView() {
        initToolBarBack(getResources().getString(R.string.sitelearn_title));
        this.fragmentManager = getSupportFragmentManager();
        this.rgRadioLearnNavigation.check(R.id.rb_radio_site_learn);
        this.baseFragment = new SiteCollectionFragment();
        this.rgRadioLearnNavigation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: com.lianhexinye.m90.ui.activity.SiteCollectionActivity.1
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_radio_other) {
                    SiteCollectionActivity.this.tagShowFragment = "OtherCollectionFragment";
                } else if (i == R.id.rb_radio_site_learn) {
                    SiteCollectionActivity.this.tagShowFragment = "SiteCollectionFragment";
                }
                SiteCollectionActivity.this.loadFragment();
            }
        });
        loadFragment();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadFragment() {
        this.transaction = this.fragmentManager.beginTransaction();
        Fragment fragmentFindFragmentByTag = this.fragmentManager.findFragmentByTag(this.tagShowFragment);
        if (fragmentFindFragmentByTag != null) {
            for (Fragment fragment : this.fragmentManager.getFragments()) {
                if (fragment.getClass().getName().equals(fragmentFindFragmentByTag.getClass().getName())) {
                    this.transaction.show(fragment);
                } else {
                    this.transaction.hide(fragment);
                }
            }
            this.transaction.commit();
            return;
        }
        Iterator<Fragment> it = this.fragmentManager.getFragments().iterator();
        while (it.hasNext()) {
            this.transaction.hide(it.next());
        }
        if (this.tagShowFragment.equals("SiteCollectionFragment")) {
            this.baseFragment = new SiteCollectionFragment();
        } else {
            this.baseFragment = new OtherCollectionFragment();
        }
        this.transaction.add(R.id.flSiteContext, this.baseFragment, this.tagShowFragment);
        this.transaction.commit();
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
        setResult(-1);
        finish();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lianhexinye.m90.mvp.MvpActivity
    public MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override // com.lianhexinye.m90.mvp.main.MainView
    public void getDataFail(String str) {
        toastShow(str);
    }

    @Override // com.lianhexinye.m90.mvp.MvpActivity, com.lianhexinye.m90.mvp.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.transaction = null;
        this.fragmentManager = null;
    }
}
