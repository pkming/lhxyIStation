package com.lianhexinye.m90.mvp.sitelearn;

import android.database.Cursor;
import com.lianhexinye.m90.common.utils.greendao.GreenDaoUtils;
import com.lianhexinye.m90.greendao.gen.BusLineFriendRemindModel;
import com.lianhexinye.m90.greendao.gen.BusLineFriendRemindModelDao;
import com.lianhexinye.m90.greendao.gen.BusLineInfoModel;
import com.lianhexinye.m90.greendao.gen.BusLineInfoModelDao;
import com.lianhexinye.m90.greendao.gen.BusLineModel;
import com.lianhexinye.m90.greendao.gen.BusLineModelDao;
import com.lianhexinye.m90.mvp.BasePresenter;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.greendao.query.WhereCondition;

/* JADX INFO: loaded from: classes2.dex */
public class SiteLearnPresenter extends BasePresenter<SiteLearnView> {
    public SiteLearnPresenter(SiteLearnView siteLearnView) {
        attachView(siteLearnView);
    }

    public void getLineList() {
        Observable.create(new ObservableOnSubscribe<List<LineNameModel>>() { // from class: com.lianhexinye.m90.mvp.sitelearn.SiteLearnPresenter.2
            @Override // io.reactivex.ObservableOnSubscribe
            public void subscribe(ObservableEmitter<List<LineNameModel>> observableEmitter) throws Exception {
                observableEmitter.onNext(SiteLearnPresenter.this.queryArrayLine());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<LineNameModel>>() { // from class: com.lianhexinye.m90.mvp.sitelearn.SiteLearnPresenter.1
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
                if (SiteLearnPresenter.this.isViewAttached()) {
                    ((SiteLearnView) SiteLearnPresenter.this.mvpView.get()).getLineNameDataSuccess(list);
                }
            }
        });
    }

    public void getSiteList(final String str) {
        Observable.create(new ObservableOnSubscribe<List<BusLineModel>>() { // from class: com.lianhexinye.m90.mvp.sitelearn.SiteLearnPresenter.4
            @Override // io.reactivex.ObservableOnSubscribe
            public void subscribe(ObservableEmitter<List<BusLineModel>> observableEmitter) throws Exception {
                observableEmitter.onNext(SiteLearnPresenter.this.queryArraySiteName(str));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<BusLineModel>>() { // from class: com.lianhexinye.m90.mvp.sitelearn.SiteLearnPresenter.3
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
            public void onNext(List<BusLineModel> list) {
                if (SiteLearnPresenter.this.isViewAttached()) {
                    ((SiteLearnView) SiteLearnPresenter.this.mvpView.get()).getSiteNameDataSuccess(list);
                }
            }
        });
    }

    public void upSite(final BusLineModel busLineModel) {
        Observable.create(new ObservableOnSubscribe<Boolean>() { // from class: com.lianhexinye.m90.mvp.sitelearn.SiteLearnPresenter.6
            @Override // io.reactivex.ObservableOnSubscribe
            public void subscribe(ObservableEmitter<Boolean> observableEmitter) throws Exception {
                observableEmitter.onNext(Boolean.valueOf(SiteLearnPresenter.this.upSiteData(busLineModel)));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Boolean>() { // from class: com.lianhexinye.m90.mvp.sitelearn.SiteLearnPresenter.5
            @Override // io.reactivex.Observer
            public void onComplete() {
            }

            @Override // io.reactivex.Observer
            public void onSubscribe(Disposable disposable) {
            }

            @Override // io.reactivex.Observer
            public void onNext(Boolean bool) {
                if (SiteLearnPresenter.this.isViewAttached()) {
                    ((SiteLearnView) SiteLearnPresenter.this.mvpView.get()).operationDataSuccess();
                }
            }

            @Override // io.reactivex.Observer
            public void onError(Throwable th) {
                if (SiteLearnPresenter.this.isViewAttached()) {
                    ((SiteLearnView) SiteLearnPresenter.this.mvpView.get()).getDataFail("站点学习失败.");
                }
            }
        });
    }

    public void upLineFR(final BusLineFriendRemindModel busLineFriendRemindModel) {
        Observable.create(new ObservableOnSubscribe<Boolean>() { // from class: com.lianhexinye.m90.mvp.sitelearn.SiteLearnPresenter.8
            @Override // io.reactivex.ObservableOnSubscribe
            public void subscribe(ObservableEmitter<Boolean> observableEmitter) throws Exception {
                observableEmitter.onNext(Boolean.valueOf(SiteLearnPresenter.this.upLineFRData(busLineFriendRemindModel)));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Boolean>() { // from class: com.lianhexinye.m90.mvp.sitelearn.SiteLearnPresenter.7
            @Override // io.reactivex.Observer
            public void onComplete() {
            }

            @Override // io.reactivex.Observer
            public void onSubscribe(Disposable disposable) {
            }

            @Override // io.reactivex.Observer
            public void onNext(Boolean bool) {
                if (SiteLearnPresenter.this.isViewAttached()) {
                    ((SiteLearnView) SiteLearnPresenter.this.mvpView.get()).operationDataSuccess();
                }
            }

            @Override // io.reactivex.Observer
            public void onError(Throwable th) {
                if (SiteLearnPresenter.this.isViewAttached()) {
                    ((SiteLearnView) SiteLearnPresenter.this.mvpView.get()).getDataFail("站点学习失败.");
                }
            }
        });
    }

    public void getLineFRList(final String str) {
        Observable.create(new ObservableOnSubscribe<List<BusLineFriendRemindModel>>() { // from class: com.lianhexinye.m90.mvp.sitelearn.SiteLearnPresenter.10
            @Override // io.reactivex.ObservableOnSubscribe
            public void subscribe(ObservableEmitter<List<BusLineFriendRemindModel>> observableEmitter) throws Exception {
                observableEmitter.onNext(SiteLearnPresenter.this.queryArrayLineFRName(str));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<BusLineFriendRemindModel>>() { // from class: com.lianhexinye.m90.mvp.sitelearn.SiteLearnPresenter.9
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
            public void onNext(List<BusLineFriendRemindModel> list) {
                if (SiteLearnPresenter.this.isViewAttached()) {
                    ((SiteLearnView) SiteLearnPresenter.this.mvpView.get()).getLineFRDataSuccess(list);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<LineNameModel> queryArrayLine() {
        ArrayList arrayList = new ArrayList();
        GreenDaoUtils.getSingleTon().getmDaoSession().clear();
        Cursor cursorRawQuery = GreenDaoUtils.getSingleTon().getmDaoSession().getDatabase().rawQuery("SELECT " + BusLineModelDao.Properties.BusLineName.columnName + " FROM " + BusLineModelDao.TABLENAME + " GROUP BY " + BusLineModelDao.Properties.BusLineName.columnName, null);
        try {
            if (cursorRawQuery.moveToFirst()) {
                do {
                    LineNameModel lineNameModel = new LineNameModel();
                    lineNameModel.setBusName(cursorRawQuery.getString(0));
                    arrayList.add(lineNameModel);
                } while (cursorRawQuery.moveToNext());
            }
            return arrayList;
        } finally {
            cursorRawQuery.close();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<BusLineModel> queryArraySiteName(String str) {
        GreenDaoUtils.getSingleTon().getmDaoSession().clear();
        return GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineModelDao().queryBuilder().where(BusLineModelDao.Properties.DirectionName.eq(str), new WhereCondition[0]).orderAsc(BusLineModelDao.Properties.BusNo).list();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<BusLineFriendRemindModel> queryArrayLineFRName(String str) {
        GreenDaoUtils.getSingleTon().getmDaoSession().clear();
        return GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineFriendRemindModelDao().queryBuilder().where(BusLineFriendRemindModelDao.Properties.DirectionName.eq(str), new WhereCondition[0]).orderAsc(BusLineFriendRemindModelDao.Properties.FrNo).list();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean upSiteData(BusLineModel busLineModel) {
        GreenDaoUtils.getSingleTon().getmDaoSession().clear();
        BusLineInfoModel busLineInfoModelUnique = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineInfoModelDao().queryBuilder().where(BusLineInfoModelDao.Properties.LineName.eq(busLineModel.getBusLineName()), new WhereCondition[0]).unique();
        if (busLineInfoModelUnique != null) {
            BusLineModelDao busLineModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineModelDao();
            if (busLineInfoModelUnique.getAttribute() != 2) {
                BusLineModel busLineModelUnique = busLineModelDao.queryBuilder().where(BusLineModelDao.Properties.BusNo.eq(Integer.valueOf(busLineModel.getBusNo())), BusLineModelDao.Properties.BusName.eq(busLineModel.getBusName()), BusLineModelDao.Properties.DirectionName.eq(busLineModel.getDirectionName())).unique();
                if (busLineModelUnique != null) {
                    busLineModelUnique.setLatitude(busLineModel.getLatitude());
                    busLineModelUnique.setLongitude(busLineModel.getLongitude());
                    busLineModelUnique.setAngle(busLineModel.getAngle());
                    busLineModelUnique.setSpeed(busLineModel.getSpeed());
                    busLineModelDao.update(busLineModelUnique);
                }
            } else {
                BusLineModel busLineModelUnique2 = busLineModelDao.queryBuilder().where(BusLineModelDao.Properties.BusNo.eq(Integer.valueOf(busLineModel.getBusNo())), BusLineModelDao.Properties.BusName.eq(busLineModel.getBusName()), BusLineModelDao.Properties.Direction.eq(1), BusLineModelDao.Properties.BusLineName.eq(busLineModel.getBusLineName())).unique();
                if (busLineModelUnique2 != null) {
                    busLineModelUnique2.setLatitude(busLineModel.getLatitude());
                    busLineModelUnique2.setLongitude(busLineModel.getLongitude());
                    busLineModelUnique2.setAngle(busLineModel.getAngle());
                    busLineModelUnique2.setSpeed(busLineModel.getSpeed());
                    busLineModelDao.update(busLineModelUnique2);
                }
                BusLineModel busLineModelUnique3 = busLineModelDao.queryBuilder().where(BusLineModelDao.Properties.BusNo.eq(Integer.valueOf(busLineModel.getBusNo())), BusLineModelDao.Properties.BusName.eq(busLineModel.getBusName()), BusLineModelDao.Properties.Direction.eq(2), BusLineModelDao.Properties.BusLineName.eq(busLineModel.getBusLineName())).unique();
                if (busLineModelUnique3 != null) {
                    busLineModelUnique3.setLatitude(busLineModel.getLatitude());
                    busLineModelUnique3.setLongitude(busLineModel.getLongitude());
                    busLineModelUnique3.setAngle(busLineModel.getAngle());
                    busLineModelUnique3.setSpeed(busLineModel.getSpeed());
                    busLineModelDao.update(busLineModelUnique3);
                }
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean upLineFRData(BusLineFriendRemindModel busLineFriendRemindModel) {
        GreenDaoUtils.getSingleTon().getmDaoSession().clear();
        BusLineInfoModel busLineInfoModelUnique = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineInfoModelDao().queryBuilder().where(BusLineInfoModelDao.Properties.LineName.eq(busLineFriendRemindModel.getBusLineName()), new WhereCondition[0]).unique();
        if (busLineInfoModelUnique != null) {
            BusLineFriendRemindModelDao busLineFriendRemindModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineFriendRemindModelDao();
            if (busLineInfoModelUnique.getAttribute() != 2) {
                BusLineFriendRemindModel busLineFriendRemindModelUnique = busLineFriendRemindModelDao.queryBuilder().where(BusLineFriendRemindModelDao.Properties.DirectionName.eq(busLineFriendRemindModel.getDirectionName()), BusLineFriendRemindModelDao.Properties.FrNo.eq(Integer.valueOf(busLineFriendRemindModel.getFrNo())), BusLineFriendRemindModelDao.Properties.FrVoice.eq(busLineFriendRemindModel.getFrVoice())).unique();
                if (busLineFriendRemindModelUnique != null) {
                    busLineFriendRemindModelUnique.setLatitude(busLineFriendRemindModel.getLatitude());
                    busLineFriendRemindModelUnique.setLongitude(busLineFriendRemindModel.getLongitude());
                    busLineFriendRemindModelDao.update(busLineFriendRemindModelUnique);
                }
            } else {
                BusLineFriendRemindModel busLineFriendRemindModelUnique2 = busLineFriendRemindModelDao.queryBuilder().where(BusLineFriendRemindModelDao.Properties.Direction.eq(1), BusLineFriendRemindModelDao.Properties.BusLineName.eq(busLineFriendRemindModel.getBusLineName()), BusLineFriendRemindModelDao.Properties.FrNo.eq(Integer.valueOf(busLineFriendRemindModel.getFrNo())), BusLineFriendRemindModelDao.Properties.FrVoice.eq(busLineFriendRemindModel.getFrVoice())).unique();
                if (busLineFriendRemindModelUnique2 != null) {
                    busLineFriendRemindModelUnique2.setLatitude(busLineFriendRemindModel.getLatitude());
                    busLineFriendRemindModelUnique2.setLongitude(busLineFriendRemindModel.getLongitude());
                    busLineFriendRemindModelDao.update(busLineFriendRemindModelUnique2);
                }
                BusLineFriendRemindModel busLineFriendRemindModelUnique3 = busLineFriendRemindModelDao.queryBuilder().where(BusLineFriendRemindModelDao.Properties.Direction.eq(2), BusLineFriendRemindModelDao.Properties.BusLineName.eq(busLineFriendRemindModel.getBusLineName()), BusLineFriendRemindModelDao.Properties.FrNo.eq(Integer.valueOf(busLineFriendRemindModel.getFrNo())), BusLineFriendRemindModelDao.Properties.FrVoice.eq(busLineFriendRemindModel.getFrVoice())).unique();
                if (busLineFriendRemindModelUnique3 != null) {
                    busLineFriendRemindModelUnique3.setLatitude(busLineFriendRemindModel.getLatitude());
                    busLineFriendRemindModelUnique3.setLongitude(busLineFriendRemindModel.getLongitude());
                    busLineFriendRemindModelDao.update(busLineFriendRemindModelUnique3);
                }
            }
        }
        return true;
    }
}
