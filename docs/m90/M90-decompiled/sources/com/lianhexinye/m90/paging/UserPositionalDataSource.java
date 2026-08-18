package com.lianhexinye.m90.paging;

import androidx.paging.PositionalDataSource;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class UserPositionalDataSource extends PositionalDataSource<LineNameModel> {
    private final List<LineNameModel> mTotalUserList;

    public UserPositionalDataSource(List<LineNameModel> list) {
        this.mTotalUserList = list;
    }

    @Override // androidx.paging.PositionalDataSource
    public void loadInitial(PositionalDataSource.LoadInitialParams loadInitialParams, PositionalDataSource.LoadInitialCallback<LineNameModel> loadInitialCallback) {
        int iMin = Math.min(loadInitialParams.requestedStartPosition, this.mTotalUserList.size() - 1);
        loadInitialCallback.onResult(this.mTotalUserList.subList(iMin, Math.min(loadInitialParams.requestedLoadSize, this.mTotalUserList.size() - iMin) + iMin), iMin, this.mTotalUserList.size());
    }

    @Override // androidx.paging.PositionalDataSource
    public void loadRange(PositionalDataSource.LoadRangeParams loadRangeParams, PositionalDataSource.LoadRangeCallback<LineNameModel> loadRangeCallback) {
        int i = loadRangeParams.startPosition;
        loadRangeCallback.onResult(this.mTotalUserList.subList(i, Math.min(loadRangeParams.loadSize + i, this.mTotalUserList.size())));
    }
}
