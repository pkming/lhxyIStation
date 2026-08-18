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
import com.lianhexinye.m90.ui.fragment.dispatch.LedPeripheralFragment;
import com.lianhexinye.m90.ui.fragment.dispatch.PaycardTestFragment;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class DispatchCenterActivity extends MvpActivity<MainView, MainPresenter> implements MainView {

    @BindView(R.id.flDispatchContext)
    FrameLayout flDispatchContext;
    private FragmentManager fragmentManager;

    @BindView(R.id.rb_radio_attendance)
    RadioButton rbRadioAttendance;

    @BindView(R.id.rb_radio_ledPeripheral)
    RadioButton rbRadioLedPeripheral;

    @BindView(R.id.rg_radio_dispatch)
    RadioGroup rgRadioDispatch;

    @BindView(R.id.rlToolbar)
    RelativeLayout rlToolbar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    private FragmentTransaction transaction;
    private final String TAG = "DispatchCenterActivity";
    BaseFragment baseFragment = null;
    private String tagShowFragment = "PaycardTestFragment";

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
        setContentView(R.layout.activity_dispatch_center);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        initToolBarBack(getResources().getString(R.string.dispatchcenter_title));
        this.fragmentManager = getSupportFragmentManager();
        this.rgRadioDispatch.check(R.id.rb_radio_attendance);
        this.rgRadioDispatch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: com.lianhexinye.m90.ui.activity.DispatchCenterActivity.1
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_radio_attendance) {
                    DispatchCenterActivity.this.tagShowFragment = "PaycardTestFragment";
                } else if (i == R.id.rb_radio_ledPeripheral) {
                    DispatchCenterActivity.this.tagShowFragment = "LedPeripheralFragment";
                }
                DispatchCenterActivity.this.loadFragment();
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
        if (!this.tagShowFragment.equals("PaycardTestFragment") && this.tagShowFragment.equals("LedPeripheralFragment")) {
            this.baseFragment = new LedPeripheralFragment();
        } else {
            this.baseFragment = new PaycardTestFragment();
        }
        this.transaction.add(R.id.flDispatchContext, this.baseFragment, this.tagShowFragment);
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
        LogUtils.d("DispatchCenterActivity", "onDestroy");
        this.transaction = null;
        this.fragmentManager = null;
    }
}
