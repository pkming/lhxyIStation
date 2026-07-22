package com.lianhexinye.m90.mvp.main;

import android.database.Cursor;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.common.utils.AndroidUtils;
import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.utils.greendao.GreenDaoUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.greendao.gen.BusLineFriendRemindModel;
import com.lianhexinye.m90.greendao.gen.BusLineFriendRemindModelDao;
import com.lianhexinye.m90.greendao.gen.BusLineInfoModel;
import com.lianhexinye.m90.greendao.gen.BusLineInfoModelDao;
import com.lianhexinye.m90.greendao.gen.BusLineModel;
import com.lianhexinye.m90.greendao.gen.BusLineModelDao;
import com.lianhexinye.m90.greendao.gen.ConfigInfoModel;
import com.lianhexinye.m90.greendao.gen.MaintenanceModel;
import com.lianhexinye.m90.greendao.gen.MaintenanceModelDao;
import com.lianhexinye.m90.greendao.gen.MessageModel;
import com.lianhexinye.m90.greendao.gen.MessageModelDao;
import com.lianhexinye.m90.mvp.BasePresenter;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import com.lianhexinye.m90.retrofit.ApiManager;
import com.lianhexinye.m90.retrofit.MySubscriber;
import com.lianhexinye.m90.retrofit.exception.ApiException;
import com.lianhexinye.m90.retrofit.request.AuthorizReqBody;
import com.lianhexinye.m90.retrofit.request.RegisterReqBody;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.greenrobot.greendao.query.WhereCondition;

/* JADX INFO: loaded from: classes2.dex */
public class MainPresenter extends BasePresenter<MainView> {
    private List<String> busLineNoTmp = new ArrayList();

    public MainPresenter(MainView mainView) {
        attachView(mainView);
    }

    public void test(Map<String, Object> map) {
        final CompositeDisposable compositeDisposable = new CompositeDisposable();
        addSubscription(null, new MySubscriber() { // from class: com.lianhexinye.m90.mvp.main.MainPresenter.1
            @Override // io.reactivex.Observer
            public void onComplete() {
            }

            @Override // io.reactivex.Observer
            public void onNext(Object obj) {
            }

            @Override // com.lianhexinye.m90.retrofit.MySubscriber
            public void onError(ApiException apiException) {
                ((MainView) MainPresenter.this.mvpView.get()).getDataFail(apiException.getDisplayMessage());
            }

            @Override // io.reactivex.Observer
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }
        });
        compositeDisposable.dispose();
    }

    public void upMessageData(MessageModel messageModel) {
        MessageModelDao messageModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getMessageModelDao();
        MessageModel messageModelUnique = messageModelDao.queryBuilder().where(MessageModelDao.Properties.MessageNo.eq(Integer.valueOf(messageModel.getMessageNo())), new WhereCondition[0]).unique();
        if (messageModelUnique != null) {
            messageModelUnique.setMessageTime(messageModel.getMessageTime());
            messageModelUnique.setMessageContent(messageModel.getMessageContent());
            messageModelDao.update(messageModelUnique);
            return;
        }
        messageModelDao.insert(messageModel);
    }

    public void register(final RegisterReqBody registerReqBody) {
        ((MainView) this.mvpView.get()).showLoading("Loading...");
        addSubscription(ApiManager.apiManager.register(registerReqBody), new MySubscriber() { // from class: com.lianhexinye.m90.mvp.main.MainPresenter.2
            @Override // io.reactivex.Observer
            public void onComplete() {
            }

            @Override // com.lianhexinye.m90.retrofit.MySubscriber
            public void onError(ApiException apiException) {
                if (MainPresenter.this.isViewAttached()) {
                    ((MainView) MainPresenter.this.mvpView.get()).hideLoading();
                    ((MainView) MainPresenter.this.mvpView.get()).authorizationResult(apiException.getCode(), apiException.getDisplayMessage());
                }
            }

            @Override // io.reactivex.Observer
            public void onSubscribe(Disposable disposable) {
                MainPresenter.this.compositeDisposable.add(disposable);
            }

            @Override // io.reactivex.Observer
            public void onNext(Object obj) {
                MainPresenter.this.authorization(registerReqBody.getMac());
            }
        });
    }

    public void authorization(String str) {
        AuthorizReqBody authorizReqBody = new AuthorizReqBody();
        authorizReqBody.setAcitveStatus("2");
        addSubscription(ApiManager.apiManager.myInformation(str, authorizReqBody), new MySubscriber() { // from class: com.lianhexinye.m90.mvp.main.MainPresenter.3
            @Override // io.reactivex.Observer
            public void onComplete() {
            }

            @Override // com.lianhexinye.m90.retrofit.MySubscriber
            public void onError(ApiException apiException) {
                if (MainPresenter.this.isViewAttached()) {
                    ((MainView) MainPresenter.this.mvpView.get()).hideLoading();
                    ((MainView) MainPresenter.this.mvpView.get()).authorizationResult(apiException.getCode(), apiException.getDisplayMessage());
                }
            }

            @Override // io.reactivex.Observer
            public void onSubscribe(Disposable disposable) {
                MainPresenter.this.compositeDisposable.add(disposable);
            }

            @Override // io.reactivex.Observer
            public void onNext(Object obj) {
                if (MainPresenter.this.isViewAttached()) {
                    ((MainView) MainPresenter.this.mvpView.get()).hideLoading();
                    ((MainView) MainPresenter.this.mvpView.get()).authorizationResult(1, "成功");
                }
            }
        });
    }

    public void getBusLineList(String str) {
        Observable.just(Boolean.valueOf(queryArrayBusLineName(str))).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Boolean>() { // from class: com.lianhexinye.m90.mvp.main.MainPresenter.4
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
            public void onNext(Boolean bool) {
                if (MainPresenter.this.isViewAttached()) {
                    ((MainView) MainPresenter.this.mvpView.get()).getDataSuccess();
                }
            }
        });
    }

    private boolean queryArrayBusLineName(String str) {
        GreenDaoUtils.getSingleTon().getmDaoSession().clear();
        List<BusLineModel> list = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineModelDao().queryBuilder().where(BusLineModelDao.Properties.DirectionName.eq(str), new WhereCondition[0]).orderAsc(BusLineModelDao.Properties.BusNo).list();
        if (list != null && list.size() > 0) {
            AppApplication.mapBusLine.clear();
            AppApplication.listBusLine.clear();
            for (BusLineModel busLineModel : list) {
                if (busLineModel.getLatitude() != null && !"".equals(busLineModel.getLatitude().trim()) && busLineModel.getLongitude() != null && !"".equals(busLineModel.getLongitude().trim())) {
                    String[] strArrSplit = String.valueOf(Double.valueOf(busLineModel.getLatitude().trim()).doubleValue() / 100.0d).split("\\.");
                    if (strArrSplit.length > 1 && strArrSplit[1].length() > 2) {
                        double dDoubleValue = (Double.valueOf(strArrSplit[1].substring(0, 2) + "." + strArrSplit[1].substring(2)).doubleValue() / 60.0d) + Double.valueOf(strArrSplit[0]).doubleValue();
                        String[] strArrSplit2 = String.valueOf(Double.valueOf(busLineModel.getLongitude().trim()).doubleValue() / 100.0d).split("\\.");
                        if (strArrSplit2.length > 1 && strArrSplit2[1].length() > 2) {
                            busLineModel.setLongitude(String.format(Locale.CHINA, "%.6f", Double.valueOf((Double.valueOf(strArrSplit2[1].substring(0, 2) + "." + strArrSplit2[1].substring(2)).doubleValue() / 60.0d) + Double.valueOf(strArrSplit2[0]).doubleValue())));
                            busLineModel.setLatitude(String.format(Locale.CHINA, "%.6f", Double.valueOf(dDoubleValue)));
                        } else {
                            busLineModel.setLongitude("0");
                            busLineModel.setLatitude("0");
                        }
                    } else {
                        busLineModel.setLongitude("0");
                        busLineModel.setLatitude("0");
                    }
                }
                AppApplication.mapBusLine.put("" + busLineModel.getBusNo(), busLineModel);
            }
            AppApplication.currentBusLineModel = list.get(0);
            AppApplication.listBusLine.addAll(list);
        } else {
            AppApplication.listBusLine.clear();
            AppApplication.currentBusLineModel = null;
            AppApplication.mapBusLine.clear();
        }
        queryArrayBusLineFR(str);
        return true;
    }

    public List<BusLineModel> queryArrayBusLine(String str) {
        GreenDaoUtils.getSingleTon().getmDaoSession().clear();
        return GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineModelDao().queryBuilder().where(BusLineModelDao.Properties.DirectionName.eq(str), new WhereCondition[0]).orderAsc(BusLineModelDao.Properties.BusNo).list();
    }

    private void queryArrayBusLineFR(String str) {
        GreenDaoUtils.getSingleTon().getmDaoSession().clear();
        List<BusLineFriendRemindModel> list = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineFriendRemindModelDao().queryBuilder().where(BusLineFriendRemindModelDao.Properties.DirectionName.eq(str), new WhereCondition[0]).orderAsc(BusLineFriendRemindModelDao.Properties.FrNo).list();
        AppApplication.listBusLineFriendRemindModel.clear();
        if (list == null || list.size() <= 0) {
            return;
        }
        for (BusLineFriendRemindModel busLineFriendRemindModel : list) {
            if (busLineFriendRemindModel.getLatitude() != null && !"".equals(busLineFriendRemindModel.getLatitude().trim()) && busLineFriendRemindModel.getLongitude() != null && !"".equals(busLineFriendRemindModel.getLongitude().trim())) {
                String[] strArrSplit = String.valueOf(Double.valueOf(busLineFriendRemindModel.getLatitude().trim()).doubleValue() / 100.0d).split("\\.");
                if (strArrSplit.length > 1 && strArrSplit[1].length() > 2) {
                    double dDoubleValue = (Double.valueOf(strArrSplit[1].substring(0, 2) + "." + strArrSplit[1].substring(2)).doubleValue() / 60.0d) + Double.valueOf(strArrSplit[0]).doubleValue();
                    String[] strArrSplit2 = String.valueOf(Double.valueOf(busLineFriendRemindModel.getLongitude().trim()).doubleValue() / 100.0d).split("\\.");
                    if (strArrSplit2.length <= 1 || strArrSplit2[1].length() <= 2) {
                        AppApplication.listBusLineFriendRemindModel.clear();
                        return;
                    } else {
                        busLineFriendRemindModel.setLongitude(String.format(Locale.CHINA, "%.6f", Double.valueOf((Double.valueOf(strArrSplit2[1].substring(0, 2) + "." + strArrSplit2[1].substring(2)).doubleValue() / 60.0d) + Double.valueOf(strArrSplit2[0]).doubleValue())));
                        busLineFriendRemindModel.setLatitude(String.format(Locale.CHINA, "%.6f", Double.valueOf(dDoubleValue)));
                    }
                } else {
                    AppApplication.listBusLineFriendRemindModel.clear();
                    return;
                }
            }
        }
        AppApplication.listBusLineFriendRemindModel.addAll(list);
    }

    public String queryLineName() {
        new ArrayList();
        Cursor cursorRawQuery = GreenDaoUtils.getSingleTon().getmDaoSession().getDatabase().rawQuery("SELECT " + BusLineModelDao.Properties.BusLineName.columnName + " FROM " + BusLineModelDao.TABLENAME + " GROUP BY " + BusLineModelDao.Properties.BusLineName.columnName, null);
        try {
            return cursorRawQuery.moveToFirst() ? cursorRawQuery.getString(0) + "S" : "";
        } finally {
            cursorRawQuery.close();
        }
    }

    public void removeDuplicateWithOrder(List list) {
        HashSet hashSet = new HashSet();
        ArrayList arrayList = new ArrayList();
        for (Object obj : list) {
            if (hashSet.add(obj)) {
                arrayList.add(obj);
            }
        }
        list.clear();
        list.addAll(arrayList);
    }

    public GPSReportStationResult getGpsRStationInfo(GPSOperationParam gPSOperationParam) {
        GPSReportStationResult gPSReportStationResult = new GPSReportStationResult();
        int iIntValue = ((Integer) SPUserInfoUtils.get(AppApplication.getContext(), SPUserInfoUtils.LINEATTRIBUTE, 1)).intValue();
        if (iIntValue == 1) {
            duikaiRStationHandle(gPSOperationParam, gPSReportStationResult);
        } else if (iIntValue == 2) {
            annularRStationHandle(gPSOperationParam, gPSReportStationResult);
        } else if (iIntValue == 3) {
            antiReverseRStationHandle(gPSOperationParam, gPSReportStationResult);
        }
        return gPSReportStationResult;
    }

    private void duikaiRStationHandle(GPSOperationParam gPSOperationParam, GPSReportStationResult gPSReportStationResult) {
        int iAbs;
        checkPosition(gPSOperationParam, gPSReportStationResult);
        if (gPSReportStationResult.getOperationType() == 0 && gPSReportStationResult.getBusLineModel() != null) {
            double distance = AndroidUtils.getDistance(gPSOperationParam.getcLong(), gPSOperationParam.getcLat(), Double.valueOf(gPSReportStationResult.getBusLineModel().getLongitude().trim()).doubleValue(), Double.valueOf(gPSReportStationResult.getBusLineModel().getLatitude().trim()).doubleValue());
            if (distance >= Double.valueOf(gPSReportStationResult.getBusLineModel().getMileage().trim()).doubleValue() + 20.0d) {
                if (gPSOperationParam.getBusLineModel().getBusNo() == gPSReportStationResult.getBusLineModel().getBusNo()) {
                    if ((gPSReportStationResult.getBusLineModel().getBusNo() != 0 || !gPSOperationParam.isiInitSite()) && gPSOperationParam.isiStationInner() && gPSReportStationResult.getBusLineModel().getBusNo() + 1 < AppApplication.listBusLine.size()) {
                        gPSReportStationResult.setBusLineModel(AppApplication.listBusLine.get(gPSReportStationResult.getBusLineModel().getBusNo() + 1));
                        this.busLineNoTmp.clear();
                        LogUtils.d("MainPresenter", "出站 resultLongitude:" + gPSReportStationResult.getBusLineModel().getLongitude().trim() + ",resultLatitude:" + gPSReportStationResult.getBusLineModel().getLatitude() + ",distance:" + distance + ",busName:" + gPSReportStationResult.getBusLineModel().getBusName() + ",BusNo:" + gPSReportStationResult.getBusLineModel().getBusNo());
                    } else {
                        gPSReportStationResult.setOperationType(2);
                    }
                } else {
                    this.busLineNoTmp.add("" + gPSReportStationResult.getBusLineModel().getBusNo());
                    removeDuplicateWithOrder(this.busLineNoTmp);
                    LogUtils.d("MainPresenter", "busLineNoTmp size:" + this.busLineNoTmp.size());
                    if (this.busLineNoTmp.size() < 5) {
                        gPSReportStationResult.setOperationType(2);
                    } else {
                        this.busLineNoTmp.clear();
                        gPSReportStationResult.setOperationType(3);
                        LogUtils.d("MainPresenter", "自动切换方向 resultLongitude:" + gPSReportStationResult.getBusLineModel().getLongitude().trim() + ",resultLatitude:" + gPSReportStationResult.getBusLineModel().getLatitude() + ",distance:" + distance + ",busName:" + gPSReportStationResult.getBusLineModel().getBusName() + ",BusNo:" + gPSReportStationResult.getBusLineModel().getBusNo());
                    }
                }
                gPSReportStationResult.setStationType(1);
                return;
            }
            if (gPSReportStationResult.getBusLineModel().getBusNo() == 0) {
                gPSOperationParam.setiInitSite(false);
            }
            if (gPSOperationParam.isiStationInner() && (gPSOperationParam.getBusLineModel().getBusNo() == gPSReportStationResult.getBusLineModel().getBusNo() || gPSOperationParam.getBusLineModel().getBusNo() + 1 == gPSReportStationResult.getBusLineModel().getBusNo())) {
                gPSReportStationResult.setOperationType(2);
            }
            gPSReportStationResult.setStationType(0);
            if (gPSReportStationResult.getBusLineModel().getBusNo() < AppApplication.listBusLine.size() - 1 && ((String) SPUserInfoUtils.get(AppApplication.getContext(), "IfAngle", "否")).equals("是") && !JavaUtils.isEmpty(gPSReportStationResult.getBusLineModel().getAngle()) && !gPSReportStationResult.getBusLineModel().getAngle().toLowerCase().equals("n") && (iAbs = (int) Math.abs(Double.valueOf(gPSReportStationResult.getBusLineModel().getAngle()).doubleValue() - gPSOperationParam.getAngle())) > 60 && iAbs < 300) {
                LogUtils.d("MainPresenter", "角度不对 进站 resultLongitude:" + gPSReportStationResult.getBusLineModel().getLongitude().trim() + ",resultLatitude:" + gPSReportStationResult.getBusLineModel().getLatitude() + ",distance:" + distance + ",busName:" + gPSReportStationResult.getBusLineModel().getBusName() + ",BusNo:" + gPSReportStationResult.getBusLineModel().getBusNo() + ",采集角度:" + gPSReportStationResult.getBusLineModel().getAngle() + ",实时角度:" + gPSOperationParam.getAngle());
                gPSReportStationResult.setOperationType(2);
            }
            if (gPSReportStationResult.getOperationType() == 0) {
                this.busLineNoTmp.clear();
                LogUtils.d("MainPresenter", "进站 resultLongitude:" + gPSReportStationResult.getBusLineModel().getLongitude().trim() + ",resultLatitude:" + gPSReportStationResult.getBusLineModel().getLatitude() + ",distance:" + distance + ",busName:" + gPSReportStationResult.getBusLineModel().getBusName() + ",BusNo:" + gPSReportStationResult.getBusLineModel().getBusNo());
            }
        }
    }

    private void annularRStationHandle(GPSOperationParam gPSOperationParam, GPSReportStationResult gPSReportStationResult) {
        int iAbs;
        checkPosition(gPSOperationParam, gPSReportStationResult);
        if (gPSReportStationResult.getOperationType() == 0 && gPSReportStationResult.getBusLineModel() != null) {
            double distance = AndroidUtils.getDistance(gPSOperationParam.getcLong(), gPSOperationParam.getcLat(), Double.valueOf(gPSReportStationResult.getBusLineModel().getLongitude().trim()).doubleValue(), Double.valueOf(gPSReportStationResult.getBusLineModel().getLatitude().trim()).doubleValue());
            if (distance >= Double.valueOf(gPSReportStationResult.getBusLineModel().getMileage().trim()).doubleValue() + 20.0d) {
                if (gPSOperationParam.getBusLineModel().getBusNo() == gPSReportStationResult.getBusLineModel().getBusNo()) {
                    if ((gPSReportStationResult.getBusLineModel().getBusNo() != 0 || !gPSOperationParam.isiInitSite()) && gPSOperationParam.isiStationInner() && gPSReportStationResult.getBusLineModel().getBusNo() + 1 < AppApplication.listBusLine.size()) {
                        gPSReportStationResult.setBusLineModel(AppApplication.listBusLine.get(gPSReportStationResult.getBusLineModel().getBusNo() + 1));
                        LogUtils.d("MainPresenter", "出站 resultLongitude:" + gPSReportStationResult.getBusLineModel().getLongitude().trim() + ",resultLatitude:" + gPSReportStationResult.getBusLineModel().getLatitude() + ",distance:" + distance + ",busName:" + gPSReportStationResult.getBusLineModel().getBusName() + ",BusNo:" + gPSReportStationResult.getBusLineModel().getBusNo());
                    } else {
                        gPSReportStationResult.setOperationType(2);
                    }
                } else {
                    gPSReportStationResult.setOperationType(2);
                }
                gPSReportStationResult.setStationType(1);
                return;
            }
            if (gPSReportStationResult.getBusLineModel().getBusNo() == 0) {
                gPSOperationParam.setiInitSite(false);
            }
            if (gPSOperationParam.isiStationInner() && gPSOperationParam.getBusLineModel().getBusNo() == gPSReportStationResult.getBusLineModel().getBusNo()) {
                gPSReportStationResult.setOperationType(2);
            }
            gPSReportStationResult.setStationType(0);
            if (((String) SPUserInfoUtils.get(AppApplication.getContext(), "IfAngle", "否")).equals("是") && !JavaUtils.isEmpty(gPSReportStationResult.getBusLineModel().getAngle()) && !gPSReportStationResult.getBusLineModel().getAngle().toLowerCase().equals("n") && (iAbs = (int) Math.abs(Double.valueOf(gPSReportStationResult.getBusLineModel().getAngle()).doubleValue() - gPSOperationParam.getAngle())) > 60 && iAbs < 300) {
                gPSReportStationResult.setOperationType(2);
            }
            if (gPSReportStationResult.getOperationType() == 0) {
                LogUtils.d("MainPresenter", "进站 resultLongitude:" + gPSReportStationResult.getBusLineModel().getLongitude().trim() + ",resultLatitude:" + gPSReportStationResult.getBusLineModel().getLatitude() + ",distance:" + distance + ",busName:" + gPSReportStationResult.getBusLineModel().getBusName() + ",BusNo:" + gPSReportStationResult.getBusLineModel().getBusNo());
            }
        }
    }

    private void antiReverseRStationHandle(GPSOperationParam gPSOperationParam, GPSReportStationResult gPSReportStationResult) {
        checkPosition(gPSOperationParam, gPSReportStationResult);
        if (gPSReportStationResult.getOperationType() == 0 && gPSReportStationResult.getBusLineModel() != null) {
            double distance = AndroidUtils.getDistance(gPSOperationParam.getcLong(), gPSOperationParam.getcLat(), Double.valueOf(gPSReportStationResult.getBusLineModel().getLongitude().trim()).doubleValue(), Double.valueOf(gPSReportStationResult.getBusLineModel().getLatitude().trim()).doubleValue());
            if (distance >= Double.valueOf(gPSReportStationResult.getBusLineModel().getMileage().trim()).doubleValue() + 20.0d) {
                if (gPSOperationParam.getBusLineModel().getBusNo() == gPSReportStationResult.getBusLineModel().getBusNo()) {
                    if ((gPSReportStationResult.getBusLineModel().getBusNo() != 0 || !gPSOperationParam.isiInitSite()) && gPSOperationParam.isiStationInner() && gPSReportStationResult.getBusLineModel().getBusNo() + 1 < AppApplication.listBusLine.size()) {
                        gPSReportStationResult.setBusLineModel(AppApplication.listBusLine.get(gPSReportStationResult.getBusLineModel().getBusNo() + 1));
                        LogUtils.d("MainPresenter", "出站 resultLongitude:" + gPSReportStationResult.getBusLineModel().getLongitude().trim() + ",resultLatitude:" + gPSReportStationResult.getBusLineModel().getLatitude() + ",paramcLong:" + gPSOperationParam.getcLong() + ",paramcLat:" + gPSOperationParam.getcLat() + ",distance:" + distance + ",busName:" + gPSReportStationResult.getBusLineModel().getBusName());
                    } else {
                        gPSReportStationResult.setOperationType(2);
                    }
                } else {
                    gPSReportStationResult.setOperationType(2);
                }
                gPSReportStationResult.setStationType(1);
                return;
            }
            if (gPSReportStationResult.getBusLineModel().getBusNo() == 0) {
                gPSOperationParam.setiInitSite(false);
            }
            if (gPSOperationParam.isiStationInner()) {
                if (gPSOperationParam.getBusLineModel().getBusNo() == gPSReportStationResult.getBusLineModel().getBusNo() || gPSOperationParam.getBusLineModel().getBusNo() + 1 == gPSReportStationResult.getBusLineModel().getBusNo()) {
                    gPSReportStationResult.setOperationType(2);
                }
            } else if (gPSOperationParam.getBusLineModel().getBusNo() != gPSReportStationResult.getBusLineModel().getBusNo()) {
                gPSReportStationResult.setOperationType(2);
            }
            gPSReportStationResult.setStationType(0);
            if (gPSReportStationResult.getBusLineModel().getBusNo() >= AppApplication.listBusLine.size() - 1 || !((String) SPUserInfoUtils.get(AppApplication.getContext(), "IfAngle", "否")).equals("是") || JavaUtils.isEmpty(gPSReportStationResult.getBusLineModel().getAngle()) || gPSReportStationResult.getBusLineModel().getAngle().toLowerCase().equals("n")) {
                return;
            }
            int iAbs = (int) Math.abs(Double.valueOf(gPSReportStationResult.getBusLineModel().getAngle()).doubleValue() - gPSOperationParam.getAngle());
            if (iAbs > 60 && iAbs < 300) {
                gPSReportStationResult.setOperationType(2);
            }
            if (gPSReportStationResult.getOperationType() == 0) {
                LogUtils.d("MainPresenter", "打开角度判断 进站 resultLongitude:" + gPSReportStationResult.getBusLineModel().getLongitude().trim() + ",resultLatitude:" + gPSReportStationResult.getBusLineModel().getLatitude() + ",paramcLong:" + gPSOperationParam.getcLong() + ",paramcLat:" + gPSOperationParam.getcLat() + ",distance:" + distance + ",busName:" + gPSReportStationResult.getBusLineModel().getBusName());
            }
        }
    }

    private void checkPosition(GPSOperationParam gPSOperationParam, GPSReportStationResult gPSReportStationResult) {
        gPSReportStationResult.setOperationType(2);
        BusLineFriendRemindModel busLineFriendRemindModel = null;
        if (AppApplication.listBusLine != null && AppApplication.listBusLine.size() > 0) {
            gPSReportStationResult.setBusLineModel(AppApplication.listBusLine.get(0));
            if (gPSReportStationResult.getBusLineModel().getLatitude() != null && !"".equals(gPSReportStationResult.getBusLineModel().getLatitude().trim()) && gPSReportStationResult.getBusLineModel().getLongitude() != null && !"".equals(gPSReportStationResult.getBusLineModel().getLongitude().trim())) {
                gPSReportStationResult.setOperationType(0);
                double distance = AndroidUtils.getDistance(gPSOperationParam.getcLong(), gPSOperationParam.getcLat(), Double.valueOf(gPSReportStationResult.getBusLineModel().getLongitude().trim()).doubleValue(), Double.valueOf(gPSReportStationResult.getBusLineModel().getLatitude()).doubleValue());
                for (int i = 1; i < AppApplication.listBusLine.size(); i++) {
                    BusLineModel busLineModel = AppApplication.listBusLine.get(i);
                    if (busLineModel.getLatitude() != null && !"".equals(busLineModel.getLatitude().trim()) && busLineModel.getLongitude() != null && !"".equals(busLineModel.getLongitude().trim())) {
                        double distance2 = AndroidUtils.getDistance(gPSOperationParam.getcLong(), gPSOperationParam.getcLat(), Double.valueOf(busLineModel.getLongitude().trim()).doubleValue(), Double.valueOf(busLineModel.getLatitude().trim()).doubleValue());
                        if (distance > distance2) {
                            gPSReportStationResult.setBusLineModel(busLineModel);
                            distance = distance2;
                        }
                    }
                }
                for (int i2 = 0; i2 < AppApplication.listBusLineFriendRemindModel.size(); i2++) {
                    BusLineFriendRemindModel busLineFriendRemindModel2 = AppApplication.listBusLineFriendRemindModel.get(i2);
                    if (busLineFriendRemindModel2.getLatitude() != null && !"".equals(busLineFriendRemindModel2.getLatitude().trim()) && busLineFriendRemindModel2.getLongitude() != null && !"".equals(busLineFriendRemindModel2.getLongitude().trim())) {
                        double distance3 = AndroidUtils.getDistance(Double.valueOf(busLineFriendRemindModel2.getLongitude().trim()).doubleValue(), Double.valueOf(busLineFriendRemindModel2.getLatitude()).doubleValue(), gPSOperationParam.getcLong(), gPSOperationParam.getcLat());
                        if (distance > distance3) {
                            gPSReportStationResult.setOperationType(1);
                            busLineFriendRemindModel = busLineFriendRemindModel2;
                            distance = distance3;
                        }
                    }
                }
            }
        }
        if (gPSReportStationResult.getOperationType() == 1) {
            if (busLineFriendRemindModel != null) {
                if (busLineFriendRemindModel.getMileage() != null && !busLineFriendRemindModel.getMileage().trim().equals("")) {
                    if (AndroidUtils.getDistance(Double.valueOf(busLineFriendRemindModel.getLongitude().trim()).doubleValue(), Double.valueOf(busLineFriendRemindModel.getLatitude()).doubleValue(), gPSOperationParam.getcLong(), gPSOperationParam.getcLat()) > Double.valueOf(busLineFriendRemindModel.getMileage().trim()).doubleValue() + 20.0d) {
                        if (gPSOperationParam.getBusLineFriendRemindModel().getFrNo() != busLineFriendRemindModel.getFrNo() && gPSOperationParam.isiCrossInner()) {
                            gPSReportStationResult.setOperationType(1);
                        } else {
                            gPSReportStationResult.setOperationType(2);
                        }
                        gPSReportStationResult.setCrossType(1);
                    } else {
                        if (gPSOperationParam.isiCrossInner() && gPSOperationParam.getBusLineFriendRemindModel().getFrNo() == busLineFriendRemindModel.getFrNo()) {
                            gPSReportStationResult.setOperationType(2);
                        }
                        gPSReportStationResult.setCrossType(0);
                    }
                }
            } else {
                gPSReportStationResult.setOperationType(2);
            }
        }
        gPSReportStationResult.setBusLineFriendRemindModel(busLineFriendRemindModel);
    }

    public List<BusLineModel> queryArrayBusLineName(String str, int i) {
        return GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineModelDao().queryBuilder().where(BusLineModelDao.Properties.DirectionName.eq(str), BusLineModelDao.Properties.BusNo.gt(Integer.valueOf(i))).orderAsc(BusLineModelDao.Properties.BusNo).list();
    }

    public List<MaintenanceModel> queryArrayMaintenance() {
        return GreenDaoUtils.getSingleTon().getmDaoSession().getMaintenanceModelDao().queryBuilder().orderAsc(MaintenanceModelDao.Properties.MId).list();
    }

    public List<MessageModel> queryArrayMessage() {
        return GreenDaoUtils.getSingleTon().getmDaoSession().getMessageModelDao().queryBuilder().orderAsc(MessageModelDao.Properties.MessageNo).list();
    }

    public BusLineInfoModel queryLineInfo(String str) {
        List<BusLineInfoModel> list = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineInfoModelDao().queryBuilder().where(BusLineInfoModelDao.Properties.LineNumber.eq(str), new WhereCondition[0]).list();
        if (list == null || list.size() <= 0) {
            return null;
        }
        return list.get(0);
    }

    public void queryArraySelectLineInfo() {
        BusLineInfoModel busLineInfoModelUnique = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineInfoModelDao().queryBuilder().where(BusLineInfoModelDao.Properties.ISelect.in(1), new WhereCondition[0]).unique();
        if (busLineInfoModelUnique != null) {
            SPUserInfoUtils.put(AppApplication.getInstance(), SPUserInfoUtils.BUSLINENAME, busLineInfoModelUnique.getLineName());
            SPUserInfoUtils.put(AppApplication.getInstance(), SPUserInfoUtils.BUSDIRECTIONNAME, busLineInfoModelUnique.getLineName() + "S");
            SPUserInfoUtils.put(AppApplication.getInstance(), SPUserInfoUtils.LINEATTRIBUTE, Integer.valueOf(busLineInfoModelUnique.getAttribute()));
        }
    }

    public List<ConfigInfoModel> queryArrayConfigInfo() {
        return GreenDaoUtils.getSingleTon().getmDaoSession().getConfigInfoModelDao().loadAll();
    }

    public void getLineList2() {
        Observable.just(queryArrayLine2()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<LineNameModel>>() { // from class: com.lianhexinye.m90.mvp.main.MainPresenter.5
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
                if (MainPresenter.this.isViewAttached()) {
                    ((MainView) MainPresenter.this.mvpView.get()).getLineNameDataSuccess2(list);
                }
            }
        });
    }

    private List<LineNameModel> queryArrayLine2() {
        BusLineInfoModelDao busLineInfoModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineInfoModelDao();
        BusLineModelDao busLineModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineModelDao();
        ArrayList arrayList = new ArrayList();
        List<BusLineInfoModel> listLoadAll = busLineInfoModelDao.loadAll();
        if (listLoadAll != null && listLoadAll.size() > 0) {
            for (BusLineInfoModel busLineInfoModel : listLoadAll) {
                LineNameModel lineNameModel = new LineNameModel();
                lineNameModel.setBusName(busLineInfoModel.getLineName());
                lineNameModel.setLineNumber(busLineInfoModel.getLineNumber());
                lineNameModel.setAttribute(busLineInfoModel.getAttribute());
                lineNameModel.setId("" + busLineInfoModel.get_id());
                List<BusLineModel> list = busLineModelDao.queryBuilder().where(BusLineModelDao.Properties.Direction.eq(1), BusLineModelDao.Properties.BusLineName.eq(lineNameModel.getBusName())).orderAsc(BusLineModelDao.Properties.BusNo).list();
                if (list != null && list.size() > 0) {
                    lineNameModel.setUpStartName(list.get(0).getBusName());
                    lineNameModel.setUpEndName(list.get(list.size() - 1).getBusName());
                }
                List<BusLineModel> list2 = busLineModelDao.queryBuilder().where(BusLineModelDao.Properties.Direction.eq(2), BusLineModelDao.Properties.BusLineName.eq(lineNameModel.getBusName())).orderAsc(BusLineModelDao.Properties.BusNo).list();
                if (list2 != null && list2.size() > 0) {
                    lineNameModel.setDownStartName(list2.get(0).getBusName());
                    lineNameModel.setDownEndName(list2.get(list2.size() - 1).getBusName());
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
