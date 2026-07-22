package com.lianhexinye.m90.mvp.sitelearn;

import com.lianhexinye.m90.greendao.gen.BusLineFriendRemindModel;
import com.lianhexinye.m90.greendao.gen.BusLineModel;
import com.lianhexinye.m90.mvp.BaseView;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public interface SiteLearnView extends BaseView {
    void getDataFail(String str);

    void getLineFRDataSuccess(List<BusLineFriendRemindModel> list);

    void getLineNameDataSuccess(List<LineNameModel> list);

    void getSiteNameDataSuccess(List<BusLineModel> list);

    void hideLoading();

    void operationDataSuccess();

    void showLoading();

    void showLoading(String str);
}
