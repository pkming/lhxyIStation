package com.lianhexinye.m90.mvp.basisset;

import com.lianhexinye.m90.common.utils.greendao.GreenDaoUtils;
import com.lianhexinye.m90.greendao.gen.BusLineInfoModel;
import com.lianhexinye.m90.greendao.gen.BusLineInfoModelDao;
import com.lianhexinye.m90.greendao.gen.ConfigInfoModel;
import com.lianhexinye.m90.greendao.gen.ConfigInfoModelDao;
import com.lianhexinye.m90.mvp.BasePresenter;
import com.lianhexinye.m90.retrofit.ApiManager;
import com.lianhexinye.m90.retrofit.MySubscriber;
import com.lianhexinye.m90.retrofit.exception.ApiException;
import com.lianhexinye.m90.retrofit.request.AuthorizReqBody;
import com.lianhexinye.m90.retrofit.request.RegisterReqBody;
import io.reactivex.disposables.Disposable;
import org.greenrobot.greendao.query.WhereCondition;

/* JADX INFO: loaded from: classes2.dex */
public class BasisSetPresenter extends BasePresenter<BasisSetView> {
    public BasisSetPresenter(BasisSetView basisSetView) {
        attachView(basisSetView);
    }

    public void upConfigData(ConfigInfoModel configInfoModel) {
        ConfigInfoModelDao configInfoModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getConfigInfoModelDao();
        ConfigInfoModel configInfoModelUnique = configInfoModelDao.queryBuilder().where(ConfigInfoModelDao.Properties.ConfigItem.eq(configInfoModel.getConfigItem()), new WhereCondition[0]).unique();
        if (configInfoModelUnique != null) {
            configInfoModelUnique.setConfigValue(configInfoModel.getConfigValue());
            configInfoModelDao.update(configInfoModelUnique);
        } else {
            configInfoModelDao.insert(configInfoModel);
        }
    }

    public void upBusLineInfoModel(BusLineInfoModel busLineInfoModel) {
        BusLineInfoModelDao busLineInfoModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineInfoModelDao();
        BusLineInfoModel busLineInfoModelUnique = busLineInfoModelDao.queryBuilder().where(BusLineInfoModelDao.Properties.LineName.eq(busLineInfoModel.getLineName()), new WhereCondition[0]).unique();
        busLineInfoModelUnique.setAttribute(busLineInfoModel.getAttribute());
        busLineInfoModelDao.update(busLineInfoModelUnique);
    }

    public void register(final RegisterReqBody registerReqBody) {
        ((BasisSetView) this.mvpView.get()).showLoading("Loading...");
        addSubscription(ApiManager.apiManager.register(registerReqBody), new MySubscriber() { // from class: com.lianhexinye.m90.mvp.basisset.BasisSetPresenter.1
            @Override // io.reactivex.Observer
            public void onComplete() {
            }

            @Override // com.lianhexinye.m90.retrofit.MySubscriber
            public void onError(ApiException apiException) {
                if (BasisSetPresenter.this.isViewAttached()) {
                    ((BasisSetView) BasisSetPresenter.this.mvpView.get()).hideLoading();
                    ((BasisSetView) BasisSetPresenter.this.mvpView.get()).authorizationResult(apiException.getCode(), apiException.getDisplayMessage());
                }
            }

            @Override // io.reactivex.Observer
            public void onSubscribe(Disposable disposable) {
                BasisSetPresenter.this.compositeDisposable.add(disposable);
            }

            @Override // io.reactivex.Observer
            public void onNext(Object obj) {
                BasisSetPresenter.this.authorization(registerReqBody.getMac());
            }
        });
    }

    public void authorization(String str) {
        AuthorizReqBody authorizReqBody = new AuthorizReqBody();
        authorizReqBody.setAcitveStatus("2");
        addSubscription(ApiManager.apiManager.myInformation(str, authorizReqBody), new MySubscriber() { // from class: com.lianhexinye.m90.mvp.basisset.BasisSetPresenter.2
            @Override // io.reactivex.Observer
            public void onComplete() {
            }

            @Override // com.lianhexinye.m90.retrofit.MySubscriber
            public void onError(ApiException apiException) {
                if (BasisSetPresenter.this.isViewAttached()) {
                    ((BasisSetView) BasisSetPresenter.this.mvpView.get()).hideLoading();
                    ((BasisSetView) BasisSetPresenter.this.mvpView.get()).authorizationResult(apiException.getCode(), apiException.getDisplayMessage());
                }
            }

            @Override // io.reactivex.Observer
            public void onSubscribe(Disposable disposable) {
                BasisSetPresenter.this.compositeDisposable.add(disposable);
            }

            @Override // io.reactivex.Observer
            public void onNext(Object obj) {
                if (BasisSetPresenter.this.isViewAttached()) {
                    ((BasisSetView) BasisSetPresenter.this.mvpView.get()).hideLoading();
                    ((BasisSetView) BasisSetPresenter.this.mvpView.get()).authorizationResult(1, "成功");
                }
            }
        });
    }
}
