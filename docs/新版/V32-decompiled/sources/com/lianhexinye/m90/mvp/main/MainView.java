package com.lianhexinye.m90.mvp.main;

import com.lianhexinye.m90.mvp.BaseView;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public interface MainView extends BaseView {
    void authorizationResult(int i, String str);

    void getDataFail(String str);

    void getDataSuccess();

    void getLineNameDataSuccess2(List<LineNameModel> list);

    void hideLoading();

    void showLoading();

    void showLoading(String str);
}
