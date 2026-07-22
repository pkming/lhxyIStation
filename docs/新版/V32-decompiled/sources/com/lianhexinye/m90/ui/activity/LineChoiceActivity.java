package com.lianhexinye.m90.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.common.widget.DialogBusDirectionSeleView;
import com.lianhexinye.m90.greendao.gen.BusLineInfoModel;
import com.lianhexinye.m90.mvp.MvpActivity;
import com.lianhexinye.m90.mvp.busset.BusSetPresenter;
import com.lianhexinye.m90.mvp.busset.BusSetView;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import com.lianhexinye.m90.paging.UserPagedListAdapter;
import com.lianhexinye.m90.paging.UserViewModel;
import com.lianhexinye.m90.ui.adapter.LineChoiceAdapter;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class LineChoiceActivity extends MvpActivity<BusSetView, BusSetPresenter> implements BusSetView {
    LiveData<PagedList<LineNameModel>> allLineLivePaged;

    @BindView(R.id.butAffirm)
    Button butAffirm;
    private LineChoiceAdapter lineChoiceAdapter;
    private List<LineNameModel> lineNameModels;

    @BindView(R.id.lyLineInfoTitle)
    LinearLayout lyLineInfoTitle;

    @BindView(R.id.lyNotLine)
    LinearLayout lyNotLine;
    private RecyclerView recyclerView;

    @BindView(R.id.rlAffirm)
    RelativeLayout rlAffirm;

    @BindView(R.id.rlToolbar)
    RelativeLayout rlToolbar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.tvLineTip)
    TextView tvLineTip;

    @BindView(R.id.vLineList)
    View vLineList;
    private UserViewModel viewModel;
    private final String TAG = "LineChoiceActivity";
    private String lineDirectionName = "S";
    private String lineName = "";
    private String lineNumber = "";
    private int lineAttribute = 0;
    int currentNum = -1;

    @Override // com.lianhexinye.m90.mvp.busset.BusSetView
    public void getDataFail(String str) {
    }

    @Override // com.lianhexinye.m90.mvp.BaseView
    public void setPresenter(Object obj) {
    }

    @Override // com.lianhexinye.m90.mvp.busset.BusSetView
    public void showLoading(String str) {
    }

    @Override // com.lianhexinye.m90.mvp.MvpActivity, com.lianhexinye.m90.mvp.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_line_choice);
        ButterKnife.bind(this);
        initView();
        if (AppApplication.lineNameModels2 != null && AppApplication.lineNameModels2.size() > 0) {
            this.viewModel = (UserViewModel) new ViewModelProvider(this).get(UserViewModel.class);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewLine);
            recyclerView.setLayoutManager(new LinearLayoutManager(AppApplication.getContext()));
            final UserPagedListAdapter userPagedListAdapter = new UserPagedListAdapter();
            recyclerView.setAdapter(userPagedListAdapter);
            recyclerView.setVisibility(0);
            this.lyNotLine.setVisibility(8);
            this.rlAffirm.setVisibility(0);
            userPagedListAdapter.setOnItemSelectedListener(new UserPagedListAdapter.OnItemSelectedListener() { // from class: com.lianhexinye.m90.ui.activity.-$$Lambda$LineChoiceActivity$G-RbN1Fr4ZYEZAeWjuzj67edA9o
                @Override // com.lianhexinye.m90.paging.UserPagedListAdapter.OnItemSelectedListener
                public final void onItemSelected(LineNameModel lineNameModel) {
                    this.f$0.lambda$onCreate$0$LineChoiceActivity(userPagedListAdapter, lineNameModel);
                }
            });
            if (AppApplication.currentLineId != null) {
                userPagedListAdapter.setSelectedUserId(AppApplication.currentLineId);
            }
            ((UserViewModel) new ViewModelProvider(this).get(UserViewModel.class)).userPagedList.observe(this, new Observer<PagedList<LineNameModel>>() { // from class: com.lianhexinye.m90.ui.activity.LineChoiceActivity.1
                @Override // androidx.lifecycle.Observer
                public void onChanged(PagedList<LineNameModel> pagedList) {
                    LogUtils.d("LineChoiceActivity", "加载数据");
                    userPagedListAdapter.submitList(pagedList);
                }
            });
            return;
        }
        RecyclerView recyclerView2 = (RecyclerView) findViewById(R.id.recyclerViewLine);
        recyclerView2.setLayoutManager(new LinearLayoutManager(AppApplication.getContext()));
        recyclerView2.setVisibility(8);
        this.lyNotLine.setVisibility(0);
        this.rlAffirm.setVisibility(8);
    }

    public /* synthetic */ void lambda$onCreate$0$LineChoiceActivity(UserPagedListAdapter userPagedListAdapter, LineNameModel lineNameModel) {
        LineNameModel selectedUser = userPagedListAdapter.getSelectedUser();
        if (selectedUser != null) {
            int i = Integer.parseInt(selectedUser.getId());
            AppApplication.currentLineId = String.valueOf(i);
            int i2 = i - 1;
            LogUtils.d("setOnItemSelectedListener", "lineNameModel.getId():" + lineNameModel.getId() + ";selectedUser.getId()-position:" + selectedUser.getId() + ";currentNum:" + this.currentNum);
            Iterator<LineNameModel> it = AppApplication.lineNameModels2.iterator();
            while (it.hasNext()) {
                it.next().setChecked(false);
            }
            int i3 = this.currentNum;
            if (i3 == -1) {
                AppApplication.lineNameModels2.get(i2).setChecked(true);
                this.lineName = AppApplication.lineNameModels2.get(i2).getBusName();
                this.lineAttribute = AppApplication.lineNameModels2.get(i2).getAttribute();
                this.lineNumber = AppApplication.lineNameModels2.get(i2).getLineNumber();
                this.currentNum = i2;
            } else if (i3 == i2) {
                Iterator<LineNameModel> it2 = AppApplication.lineNameModels2.iterator();
                while (it2.hasNext()) {
                    it2.next().setChecked(false);
                }
                this.currentNum = -1;
            } else if (i3 != i2) {
                Iterator<LineNameModel> it3 = AppApplication.lineNameModels2.iterator();
                while (it3.hasNext()) {
                    it3.next().setChecked(false);
                }
                AppApplication.lineNameModels2.get(i2).setChecked(true);
                this.currentNum = i2;
                this.lineName = AppApplication.lineNameModels2.get(i2).getBusName();
                this.lineAttribute = AppApplication.lineNameModels2.get(i2).getAttribute();
                this.lineNumber = AppApplication.lineNameModels2.get(i2).getLineNumber();
            }
            this.viewModel.updateSelectedUserState(selectedUser.getId(), i2, this.currentNum);
            return;
        }
        Toast.makeText(this, "请先选中一个用户", 0).show();
    }

    private void initView() {
        initToolBarBack(getResources().getString(R.string.line_choice_title));
        LogUtils.d("LineChoiceActivity", "线路选择");
    }

    @Override // com.lianhexinye.m90.mvp.busset.BusSetView
    public void getLineNameDataSuccess(List<LineNameModel> list) {
        LogUtils.d("LineChoiceActivity", "getLineNameDataSuccess");
        this.lineName = String.valueOf(SPUserInfoUtils.get(this, SPUserInfoUtils.BUSLINENAME, ""));
        this.lineAttribute = ((Integer) SPUserInfoUtils.get(this, SPUserInfoUtils.LINEATTRIBUTE, 1)).intValue();
        this.lineDirectionName = String.valueOf(SPUserInfoUtils.get(this, SPUserInfoUtils.BUSDIRECTIONNAME, ""));
        this.lineNumber = String.valueOf(SPUserInfoUtils.get(this, SPUserInfoUtils.LINENUMBER, ""));
        if (list != null && list.size() > 0) {
            LogUtils.d("LineChoiceActivity", "list.size()" + list.size());
            if (!"".equals(this.lineName.trim())) {
                for (LineNameModel lineNameModel : list) {
                    if (this.lineName.trim().equals(lineNameModel.getBusName().trim())) {
                        lineNameModel.setChecked(true);
                    } else {
                        lineNameModel.setChecked(false);
                    }
                }
            }
            this.lyNotLine.setVisibility(8);
            this.rlAffirm.setVisibility(0);
            this.lineNameModels = list;
            this.lineChoiceAdapter = new LineChoiceAdapter();
            this.recyclerView.setLayoutManager(new LinearLayoutManager(this, 1, false));
            this.recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
            this.recyclerView.setAdapter(this.lineChoiceAdapter);
            LiveData<PagedList<LineNameModel>> liveDataBuild = new LivePagedListBuilder((DataSource.Factory) this.lineNameModels, 5).build();
            this.allLineLivePaged = liveDataBuild;
            liveDataBuild.observe(this, new Observer<PagedList<LineNameModel>>() { // from class: com.lianhexinye.m90.ui.activity.LineChoiceActivity.2
                @Override // androidx.lifecycle.Observer
                public void onChanged(PagedList<LineNameModel> pagedList) {
                    LineChoiceActivity.this.lineChoiceAdapter.submitList(pagedList);
                    pagedList.addWeakCallback(null, new PagedList.Callback() { // from class: com.lianhexinye.m90.ui.activity.LineChoiceActivity.2.1
                        @Override // androidx.paging.PagedList.Callback
                        public void onInserted(int i, int i2) {
                        }

                        @Override // androidx.paging.PagedList.Callback
                        public void onRemoved(int i, int i2) {
                        }

                        @Override // androidx.paging.PagedList.Callback
                        public void onChanged(int i, int i2) {
                            LogUtils.d("TAG", "----观察--i=" + i + "---i1=" + i2 + "----");
                        }
                    });
                }
            });
        } else {
            this.lyNotLine.setVisibility(0);
            this.rlAffirm.setVisibility(8);
        }
        LogUtils.d("LineChoiceActivity", "线路切换完成");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lianhexinye.m90.mvp.MvpActivity
    public BusSetPresenter createPresenter() {
        return new BusSetPresenter(this);
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

    @Override // com.lianhexinye.m90.mvp.MvpActivity, com.lianhexinye.m90.mvp.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.lineChoiceAdapter = null;
        this.lineNameModels = null;
    }

    @OnClick({R.id.butAffirm})
    public void onViewClicked() {
        final DialogBusDirectionSeleView dialogBusDirectionSeleView = new DialogBusDirectionSeleView(this, this.lineDirectionName, this.lineName);
        dialogBusDirectionSeleView.setOnOKClickListener(new DialogBusDirectionSeleView.OnOKClickListener() { // from class: com.lianhexinye.m90.ui.activity.LineChoiceActivity.3
            @Override // com.lianhexinye.m90.common.widget.DialogBusDirectionSeleView.OnOKClickListener
            public void onOKClick(String str) {
                dialogBusDirectionSeleView.dismiss();
                BusLineInfoModel busLineInfoModel = new BusLineInfoModel();
                busLineInfoModel.setLineName((String) SPUserInfoUtils.get(LineChoiceActivity.this, SPUserInfoUtils.BUSLINENAME, ""));
                busLineInfoModel.setISelect(false);
                ((BusSetPresenter) LineChoiceActivity.this.mvpPresenter).upBusLineInfo(busLineInfoModel);
                LineChoiceActivity lineChoiceActivity = LineChoiceActivity.this;
                SPUserInfoUtils.put(lineChoiceActivity, SPUserInfoUtils.BUSLINENAME, lineChoiceActivity.lineName.trim());
                LineChoiceActivity lineChoiceActivity2 = LineChoiceActivity.this;
                SPUserInfoUtils.put(lineChoiceActivity2, SPUserInfoUtils.LINENUMBER, lineChoiceActivity2.lineNumber);
                SPUserInfoUtils.put(LineChoiceActivity.this, SPUserInfoUtils.BUSDIRECTIONNAME, LineChoiceActivity.this.lineName.trim() + str.substring(str.length() - 1));
                SPUserInfoUtils.put(LineChoiceActivity.this, SPUserInfoUtils.ISSWITCHBUSLINE, true);
                LineChoiceActivity lineChoiceActivity3 = LineChoiceActivity.this;
                SPUserInfoUtils.put(lineChoiceActivity3, SPUserInfoUtils.LINEATTRIBUTE, Integer.valueOf(lineChoiceActivity3.lineAttribute));
                BusLineInfoModel busLineInfoModel2 = new BusLineInfoModel();
                LogUtils.d("onOKClick", "lineName:" + LineChoiceActivity.this.lineName + ";");
                busLineInfoModel2.setLineName(LineChoiceActivity.this.lineName.trim());
                busLineInfoModel2.setISelect(true);
                ((BusSetPresenter) LineChoiceActivity.this.mvpPresenter).upBusLineInfo(busLineInfoModel2);
                LineChoiceActivity.this.toastShow(R.string.line_line_switch_suc);
                LineChoiceActivity.this.finish();
            }

            @Override // com.lianhexinye.m90.common.widget.DialogBusDirectionSeleView.OnOKClickListener
            public void onCancelClick() {
                dialogBusDirectionSeleView.dismiss();
            }
        });
        dialogBusDirectionSeleView.show();
    }
}
