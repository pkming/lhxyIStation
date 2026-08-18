package com.lianhexinye.m90.mvp.busset;

import com.lianhexinye.m90.common.utils.greendao.GreenDaoUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.greendao.gen.BusLineInfoModel;
import com.lianhexinye.m90.greendao.gen.BusLineInfoModelDao;
import com.lianhexinye.m90.greendao.gen.BusLineModel;
import com.lianhexinye.m90.greendao.gen.BusLineModelDao;
import com.lianhexinye.m90.mvp.BasePresenter;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.greendao.query.WhereCondition;

/* JADX INFO: loaded from: classes2.dex */
public class BusSetPresenter extends BasePresenter<BusSetView> {
    public BusSetPresenter(BusSetView busSetView) {
        attachView(busSetView);
    }

    public void getLineList() {
        Observable.just(queryArrayLine()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<LineNameModel>>() { // from class: com.lianhexinye.m90.mvp.busset.BusSetPresenter.1
            @Override // io.reactivex.Observer
            public void onComplete() {
            }

            @Override // io.reactivex.Observer
            public void onError(Throwable th) {
            }

            @Override // io.reactivex.Observer
            public void onSubscribe(Disposable disposable) {
            }

            @Override // io.reactivex.Observer
            public void onNext(List<LineNameModel> list) {
                if (BusSetPresenter.this.isViewAttached()) {
                    ((BusSetView) BusSetPresenter.this.mvpView.get()).getLineNameDataSuccess(list);
                }
            }
        });
    }

    public void upBusLineInfo(BusLineInfoModel busLineInfoModel) {
        BusLineInfoModelDao busLineInfoModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineInfoModelDao();
        BusLineInfoModel busLineInfoModelUnique = busLineInfoModelDao.queryBuilder().where(BusLineInfoModelDao.Properties.LineName.eq(busLineInfoModel.getLineName()), new WhereCondition[0]).unique();
        busLineInfoModelUnique.setISelect(busLineInfoModel.getISelect());
        busLineInfoModelDao.update(busLineInfoModelUnique);
    }

    private List<LineNameModel> queryArrayLine() {
        BusLineInfoModelDao busLineInfoModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineInfoModelDao();
        BusLineModelDao busLineModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineModelDao();
        ArrayList arrayList = new ArrayList();
        List<BusLineInfoModel> listLoadAll = busLineInfoModelDao.loadAll();
        LogUtils.d("LineChoiceActivity", "queryArrayLine busLineInfoModels size" + listLoadAll.size());
        if (listLoadAll != null && listLoadAll.size() > 0) {
            for (BusLineInfoModel busLineInfoModel : listLoadAll) {
                LineNameModel lineNameModel = new LineNameModel();
                lineNameModel.setBusName(busLineInfoModel.getLineName());
                lineNameModel.setLineNumber(busLineInfoModel.getLineNumber());
                lineNameModel.setAttribute(busLineInfoModel.getAttribute());
                LogUtils.d("LineChoiceActivity", "setLineNumber" + busLineInfoModel.getLineNumber());
                List<BusLineModel> list = busLineModelDao.queryBuilder().where(BusLineModelDao.Properties.Direction.eq(1), BusLineModelDao.Properties.BusLineName.eq(lineNameModel.getBusName())).orderAsc(BusLineModelDao.Properties.BusNo).list();
                if (list != null && list.size() > 0) {
                    lineNameModel.setUpStartName(list.get(0).getBusName());
                    lineNameModel.setUpEndName(list.get(list.size() - 1).getBusName());
                    LogUtils.d("LineChoiceActivity", "setLineNumber" + busLineInfoModel.getLineNumber() + "setUpStartName" + list.get(0).getBusName() + "setUpEndName" + list.get(list.size() - 1).getBusName());
                }
                List<BusLineModel> list2 = busLineModelDao.queryBuilder().where(BusLineModelDao.Properties.Direction.eq(2), BusLineModelDao.Properties.BusLineName.eq(lineNameModel.getBusName())).orderAsc(BusLineModelDao.Properties.BusNo).list();
                if (list2 != null && list2.size() > 0) {
                    lineNameModel.setDownStartName(list2.get(0).getBusName());
                    lineNameModel.setDownEndName(list2.get(list2.size() - 1).getBusName());
                    LogUtils.d("LineChoiceActivity", "setLineNumber" + busLineInfoModel.getLineNumber() + "setDownStartName" + list2.get(0).getBusName() + "setDownEndName" + list2.get(list2.size() - 1).getBusName());
                }
                if (busLineInfoModel.getISelect()) {
                    lineNameModel.setChecked(true);
                } else {
                    lineNameModel.setChecked(false);
                }
                arrayList.add(lineNameModel);
                if (list2 != null) {
                    list2.clear();
                }
            }
        }
        return arrayList;
    }
}
