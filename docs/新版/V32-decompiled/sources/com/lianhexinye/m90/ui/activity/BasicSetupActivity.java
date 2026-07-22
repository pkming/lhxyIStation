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
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.mvp.BaseFragment;
import com.lianhexinye.m90.mvp.MvpActivity;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import com.lianhexinye.m90.mvp.main.MainPresenter;
import com.lianhexinye.m90.mvp.main.MainView;
import com.lianhexinye.m90.ui.fragment.systemsetup.LanguageSetupFragment;
import com.lianhexinye.m90.ui.fragment.systemsetup.NetworkSetupFragment;
import com.lianhexinye.m90.ui.fragment.systemsetup.NewspaperSetupFragment;
import com.lianhexinye.m90.ui.fragment.systemsetup.OtherSetupFragment;
import com.lianhexinye.m90.ui.fragment.systemsetup.SerialPortSetupFragment;
import com.lianhexinye.m90.ui.fragment.systemsetup.TTSSetupFragment;
import com.lianhexinye.m90.ui.fragment.systemsetup.WirelessSetupFragment;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class BasicSetupActivity extends MvpActivity<MainView, MainPresenter> implements MainView {

    @BindView(R.id.flSetupContext)
    FrameLayout flSetupContext;
    private FragmentManager fragmentManager;

    @BindView(R.id.rb_radio_language)
    RadioButton rbRadioLanguage;

    @BindView(R.id.rb_radio_network)
    RadioButton rbRadioNetwork;

    @BindView(R.id.rb_radio_newspaper)
    RadioButton rbRadioNewspaper;

    @BindView(R.id.rb_radio_serial_port)
    RadioButton rbRadioSerialPort;

    @BindView(R.id.rg_radio_setup_navigation)
    RadioGroup rgRadioSetupNavigation;

    @BindView(R.id.rlToolbar)
    RelativeLayout rlToolbar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    private FragmentTransaction transaction;
    private final String TAG = "BasicSetupActivity";
    BaseFragment baseFragment = null;
    private String tagShowFragment = "NewspaperSetupFragment";

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
        setContentView(R.layout.activity_basic_setup);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        initToolBarBack(getResources().getString(R.string.systemsetup_title));
        this.fragmentManager = getSupportFragmentManager();
        this.rgRadioSetupNavigation.check(R.id.rb_radio_newspaper);
        this.baseFragment = new NewspaperSetupFragment();
        this.rgRadioSetupNavigation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: com.lianhexinye.m90.ui.activity.BasicSetupActivity.1
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i != R.id.rb_radio_language) {
                    switch (i) {
                        case R.id.rb_radio_network /* 2131296618 */:
                            BasicSetupActivity.this.tagShowFragment = "NetworkSetupFragment";
                            break;
                        case R.id.rb_radio_newspaper /* 2131296619 */:
                            BasicSetupActivity.this.tagShowFragment = "NewspaperSetupFragment";
                            break;
                        case R.id.rb_radio_other /* 2131296620 */:
                            BasicSetupActivity.this.tagShowFragment = "OtherSetupFragment";
                            break;
                        case R.id.rb_radio_serial_port /* 2131296621 */:
                            BasicSetupActivity.this.tagShowFragment = "SerialPortSetupFragment";
                            break;
                        default:
                            switch (i) {
                                case R.id.rb_radio_tts /* 2131296625 */:
                                    BasicSetupActivity.this.tagShowFragment = "TTSSetupFragment";
                                    break;
                                case R.id.rb_radio_wireless /* 2131296626 */:
                                    BasicSetupActivity.this.tagShowFragment = "WirelessSetupFragment";
                                    break;
                            }
                            break;
                    }
                } else {
                    BasicSetupActivity.this.tagShowFragment = "LanguageSetupFragment";
                }
                BasicSetupActivity.this.loadFragment();
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
        if (this.tagShowFragment.equals("NewspaperSetupFragment")) {
            this.baseFragment = new NewspaperSetupFragment();
        } else if (this.tagShowFragment.equals("NetworkSetupFragment")) {
            this.baseFragment = new NetworkSetupFragment();
        } else if (this.tagShowFragment.equals("SerialPortSetupFragment")) {
            this.baseFragment = new SerialPortSetupFragment();
        } else if (this.tagShowFragment.equals("TTSSetupFragment")) {
            this.baseFragment = new TTSSetupFragment();
        } else if (this.tagShowFragment.equals("LanguageSetupFragment")) {
            this.baseFragment = new LanguageSetupFragment();
        } else if (this.tagShowFragment.equals("OtherSetupFragment")) {
            this.baseFragment = new OtherSetupFragment();
        } else if (this.tagShowFragment.equals("WirelessSetupFragment")) {
            this.baseFragment = new WirelessSetupFragment();
        }
        this.transaction.add(R.id.flSetupContext, this.baseFragment, this.tagShowFragment);
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

    @Override // com.lianhexinye.m90.mvp.MvpActivity, com.lianhexinye.m90.mvp.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d("BasicSetupActivity", "onDestroy");
        this.transaction = null;
        this.fragmentManager = null;
    }
}
