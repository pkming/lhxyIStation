package com.lianhexinye.m90.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.greendao.gen.MessageModel;
import com.lianhexinye.m90.mvp.MvpActivity;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import com.lianhexinye.m90.mvp.main.MainPresenter;
import com.lianhexinye.m90.mvp.main.MainView;
import com.lianhexinye.m90.ui.adapter.MessageInfoAdapter;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class InfoBrowsActivity extends MvpActivity<MainView, MainPresenter> implements MainView {
    private final String TAG = "InfoBrowsActivity";

    @BindView(R.id.lvInfoMessage)
    ListView lvInfoMessage;

    @BindView(R.id.lyNotInfoMessage)
    LinearLayout lyNotInfoMessage;
    private MessageInfoAdapter messageInfoAdapter;
    private List<MessageModel> messageModels;

    @BindView(R.id.rlToolbar)
    RelativeLayout rlToolbar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.tvInfoMessageTip)
    TextView tvInfoMessageTip;

    @BindView(R.id.vInfo)
    View vInfo;

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
        setContentView(R.layout.activity_info_brows);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        initToolBarBack(getResources().getString(R.string.menu_info_browsing));
        List<MessageModel> listQueryArrayMessage = ((MainPresenter) this.mvpPresenter).queryArrayMessage();
        if (listQueryArrayMessage != null && listQueryArrayMessage.size() > 0) {
            this.lvInfoMessage.setVisibility(0);
            this.lyNotInfoMessage.setVisibility(8);
            this.messageModels = listQueryArrayMessage;
            MessageInfoAdapter messageInfoAdapter = new MessageInfoAdapter(this, this.messageModels);
            this.messageInfoAdapter = messageInfoAdapter;
            this.lvInfoMessage.setAdapter((ListAdapter) messageInfoAdapter);
        } else {
            this.lvInfoMessage.setVisibility(8);
            this.lyNotInfoMessage.setVisibility(0);
        }
        this.lvInfoMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.lianhexinye.m90.ui.activity.InfoBrowsActivity.1
            int currentNum = -1;

            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                int i2 = this.currentNum;
                if (i2 == -1) {
                    this.currentNum = i;
                } else if (i2 == i) {
                    this.currentNum = -1;
                } else if (i2 != i) {
                    this.currentNum = i;
                }
                InfoBrowsActivity.this.messageInfoAdapter.notifyDataSetChanged();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lianhexinye.m90.mvp.MvpActivity
    public MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_del, menu);
        return true;
    }

    @Override // com.lianhexinye.m90.mvp.BaseActivity, android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.del || menuItem.getItemId() != R.id.index) {
            return true;
        }
        setResult(-1);
        finish();
        return true;
    }

    @Override // com.lianhexinye.m90.mvp.MvpActivity, com.lianhexinye.m90.mvp.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
    }
}
