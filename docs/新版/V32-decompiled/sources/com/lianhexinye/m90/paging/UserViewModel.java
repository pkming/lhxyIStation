package com.lianhexinye.m90.paging;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class UserViewModel extends ViewModel {
    private UserDataSourceFactory mDataSourceFactory;
    public LiveData<PagedList<LineNameModel>> userPagedList;

    public UserViewModel() {
        PagedList.Config configBuild = new PagedList.Config.Builder().setPageSize(10).setPrefetchDistance(5).setEnablePlaceholders(false).setInitialLoadSizeHint(10).build();
        this.mDataSourceFactory = new UserDataSourceFactory(AppApplication.lineNameModels2);
        this.userPagedList = new LivePagedListBuilder(this.mDataSourceFactory, configBuild).build();
    }

    public void updateSelectedUserState(String str, int i, int i2) {
        UserDataSourceFactory userDataSourceFactory = this.mDataSourceFactory;
        if (userDataSourceFactory != null && userDataSourceFactory.create() != null) {
            this.mDataSourceFactory.create().invalidate();
            return;
        }
        if (this.mDataSourceFactory == null) {
            LogUtils.d("UserViewModel", "mDataSourceFactory为空");
        }
        if (this.mDataSourceFactory.create() == null) {
            LogUtils.d("UserViewModel", "mDataSourceFactory.create()为空");
        }
    }

    public List<LineNameModel> getTotalUserList() {
        return AppApplication.lineNameModels2;
    }
}
