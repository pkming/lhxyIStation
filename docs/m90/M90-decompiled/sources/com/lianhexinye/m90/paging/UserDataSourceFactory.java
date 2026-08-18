package com.lianhexinye.m90.paging;

import androidx.paging.DataSource;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class UserDataSourceFactory extends DataSource.Factory<Integer, LineNameModel> {
    private final List<LineNameModel> mTotalUserList;

    public UserDataSourceFactory(List<LineNameModel> list) {
        this.mTotalUserList = list;
    }

    @Override // androidx.paging.DataSource.Factory
    public DataSource<Integer, LineNameModel> create() {
        return new UserPositionalDataSource(this.mTotalUserList);
    }
}
