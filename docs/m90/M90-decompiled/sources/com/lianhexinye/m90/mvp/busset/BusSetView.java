package com.lianhexinye.m90.mvp.busset;

import com.lianhexinye.m90.mvp.BaseView;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public interface BusSetView extends BaseView {
    void getDataFail(String str);

    void getLineNameDataSuccess(List<LineNameModel> list);

    void hideLoading();

    void showLoading();

    void showLoading(String str);
}
