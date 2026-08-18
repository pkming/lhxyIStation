package com.lianhexinye.m90.mvp.file;

import com.lianhexinye.m90.mvp.BaseView;

/* JADX INFO: loaded from: classes2.dex */
public interface FileView extends BaseView {
    void getDataFail(String str);

    void getDataSuccess(int i, int i2, String str);

    void hideLoading();

    void showLoading();

    void showLoading(String str);
}
