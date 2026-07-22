package com.lianhexinye.m90.mvp.basisset;

import com.lianhexinye.m90.mvp.BaseView;

/* JADX INFO: loaded from: classes2.dex */
public interface BasisSetView<T> extends BaseView {
    void authorizationResult(int i, String str);

    void getDataFail(String str);

    void getDataSuccess(T t);

    void hideLoading();

    void showLoading();

    void showLoading(String str);
}
