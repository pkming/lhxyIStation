package com.lianhexinye.m90.mvp.file;

import android.database.Cursor;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.Constants;
import com.lianhexinye.m90.common.utils.AndroidUtils;
import com.lianhexinye.m90.common.utils.FileUtils;
import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.utils.SilentInstall;
import com.lianhexinye.m90.common.utils.ZipUtils;
import com.lianhexinye.m90.common.utils.greendao.GreenDaoUtils;
import com.lianhexinye.m90.common.utils.language.SPUtil;
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
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.greenrobot.greendao.query.WhereCondition;

/* JADX INFO: loaded from: classes2.dex */
public class FilePresenter extends BasePresenter<FileView> {
    public FilePresenter(FileView fileView) {
        attachView(fileView);
    }

    public void fileImport() {
        AppApplication.iLocalUpdate = true;
        ((FileView) this.mvpView.get()).showLoading(AppApplication.getContext().getResources().getString(R.string.file_import_load_tip));
        Observable.create(new ObservableOnSubscribe<String>() { // from class: com.lianhexinye.m90.mvp.file.FilePresenter.2
            @Override // io.reactivex.ObservableOnSubscribe
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                observableEmitter.onNext(FilePresenter.this.fileImportExport(1));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() { // from class: com.lianhexinye.m90.mvp.file.FilePresenter.1
            @Override // io.reactivex.Observer
            public void onComplete() {
            }

            @Override // io.reactivex.Observer
            public void onSubscribe(Disposable disposable) {
            }

            @Override // io.reactivex.Observer
            public void onNext(String str) {
                AppApplication.iLocalUpdate = false;
                if (FilePresenter.this.isViewAttached()) {
                    ((FileView) FilePresenter.this.mvpView.get()).hideLoading();
                    if (str.equals("0")) {
                        ((FileView) FilePresenter.this.mvpView.get()).getDataSuccess(1, 0, AppApplication.getContext().getResources().getString(R.string.file_import_complete_suss));
                    } else if (str.equals("1")) {
                        ((FileView) FilePresenter.this.mvpView.get()).getDataSuccess(1, 1, AppApplication.getContext().getResources().getString(R.string.file_import_complete_fail));
                    } else {
                        ((FileView) FilePresenter.this.mvpView.get()).getDataSuccess(1, 1, AppApplication.getContext().getResources().getString(R.string.file_import_complete_fail1));
                    }
                }
            }

            @Override // io.reactivex.Observer
            public void onError(Throwable th) {
                AppApplication.iLocalUpdate = false;
                if (FilePresenter.this.isViewAttached()) {
                    ((FileView) FilePresenter.this.mvpView.get()).hideLoading();
                    ((FileView) FilePresenter.this.mvpView.get()).getDataSuccess(1, 1, th.getMessage());
                }
            }
        });
    }

    public void fileExport() {
        AppApplication.iLocalUpdate = true;
        ((FileView) this.mvpView.get()).showLoading(AppApplication.getContext().getResources().getString(R.string.file_export_load_tip));
        Observable.create(new ObservableOnSubscribe<String>() { // from class: com.lianhexinye.m90.mvp.file.FilePresenter.4
            @Override // io.reactivex.ObservableOnSubscribe
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                observableEmitter.onNext(FilePresenter.this.fileImportExport(2));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() { // from class: com.lianhexinye.m90.mvp.file.FilePresenter.3
            @Override // io.reactivex.Observer
            public void onComplete() {
            }

            @Override // io.reactivex.Observer
            public void onSubscribe(Disposable disposable) {
            }

            @Override // io.reactivex.Observer
            public void onNext(String str) {
                AppApplication.iLocalUpdate = false;
                if (FilePresenter.this.isViewAttached()) {
                    ((FileView) FilePresenter.this.mvpView.get()).hideLoading();
                    if (str.equals("0")) {
                        ((FileView) FilePresenter.this.mvpView.get()).getDataSuccess(2, 0, AppApplication.getContext().getResources().getString(R.string.file_export_complete_suss));
                    } else if (str.equals("1")) {
                        ((FileView) FilePresenter.this.mvpView.get()).getDataSuccess(2, 1, AppApplication.getContext().getResources().getString(R.string.file_export_complete_fail));
                    } else {
                        ((FileView) FilePresenter.this.mvpView.get()).getDataSuccess(2, 1, AppApplication.getContext().getResources().getString(R.string.file_export_complete_fail1));
                    }
                }
            }

            @Override // io.reactivex.Observer
            public void onError(Throwable th) {
                AppApplication.iLocalUpdate = false;
                if (FilePresenter.this.isViewAttached()) {
                    ((FileView) FilePresenter.this.mvpView.get()).hideLoading();
                    ((FileView) FilePresenter.this.mvpView.get()).getDataSuccess(2, 1, th.getMessage());
                }
            }
        });
    }

    public void fileExportLog() {
        ((FileView) this.mvpView.get()).showLoading(AppApplication.getContext().getResources().getString(R.string.file_export_log_load_tip));
        Observable.create(new ObservableOnSubscribe<String>() { // from class: com.lianhexinye.m90.mvp.file.FilePresenter.6
            @Override // io.reactivex.ObservableOnSubscribe
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                observableEmitter.onNext(FilePresenter.this.fileImportExport(3));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() { // from class: com.lianhexinye.m90.mvp.file.FilePresenter.5
            @Override // io.reactivex.Observer
            public void onComplete() {
            }

            @Override // io.reactivex.Observer
            public void onSubscribe(Disposable disposable) {
            }

            @Override // io.reactivex.Observer
            public void onNext(String str) {
                if (FilePresenter.this.isViewAttached()) {
                    ((FileView) FilePresenter.this.mvpView.get()).hideLoading();
                    if (str.equals("0")) {
                        ((FileView) FilePresenter.this.mvpView.get()).getDataSuccess(2, 0, AppApplication.getContext().getResources().getString(R.string.file_export_complete_log_suss));
                        return;
                    }
                    if (str.equals("1")) {
                        ((FileView) FilePresenter.this.mvpView.get()).getDataSuccess(2, 1, AppApplication.getContext().getResources().getString(R.string.file_export_log_complete_fail));
                    } else if (str.equals("2")) {
                        ((FileView) FilePresenter.this.mvpView.get()).getDataSuccess(2, 1, AppApplication.getContext().getResources().getString(R.string.file_export_log_complete_fail1));
                    } else {
                        ((FileView) FilePresenter.this.mvpView.get()).getDataSuccess(2, 1, AppApplication.getContext().getResources().getString(R.string.file_export_complete_fail1));
                    }
                }
            }

            @Override // io.reactivex.Observer
            public void onError(Throwable th) {
                if (FilePresenter.this.isViewAttached()) {
                    ((FileView) FilePresenter.this.mvpView.get()).hideLoading();
                    ((FileView) FilePresenter.this.mvpView.get()).getDataSuccess(2, 1, th.getMessage());
                }
            }
        });
    }

    public String readApk() {
        String strCheckSD = AndroidUtils.checkSD(AndroidUtils.getPrimaryStoragePath(), "/BusRes/ApkRes", Constants.APK_RES_NAME, true);
        return (strCheckSD == null || strCheckSD.trim().equals("")) ? "" : strCheckSD.substring(strCheckSD.lastIndexOf("/") + 1);
    }

    public boolean checkTFLogCatalogue() {
        String strCheckSD = AndroidUtils.checkSD(AndroidUtils.getPrimaryStoragePath(), "/BusRes", "Log", false);
        return (strCheckSD == null || strCheckSD.trim().equals("")) ? false : true;
    }

    public void apkUpgrade() {
        AppApplication.iLocalUpdateApp = true;
        ((FileView) this.mvpView.get()).showLoading(AppApplication.getContext().getResources().getString(R.string.file_upgrade_load_tip));
        Observable.create(new ObservableOnSubscribe<Integer>() { // from class: com.lianhexinye.m90.mvp.file.FilePresenter.8
            @Override // io.reactivex.ObservableOnSubscribe
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                observableEmitter.onNext(Integer.valueOf(FilePresenter.this.handleApkUpgrade()));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() { // from class: com.lianhexinye.m90.mvp.file.FilePresenter.7
            @Override // io.reactivex.Observer
            public void onComplete() {
            }

            @Override // io.reactivex.Observer
            public void onSubscribe(Disposable disposable) {
            }

            @Override // io.reactivex.Observer
            public void onNext(Integer num) {
                AppApplication.iLocalUpdateApp = false;
                if (FilePresenter.this.isViewAttached()) {
                    ((FileView) FilePresenter.this.mvpView.get()).hideLoading();
                }
            }

            @Override // io.reactivex.Observer
            public void onError(Throwable th) {
                AppApplication.iLocalUpdateApp = false;
                if (FilePresenter.this.isViewAttached()) {
                    ((FileView) FilePresenter.this.mvpView.get()).hideLoading();
                    ((FileView) FilePresenter.this.mvpView.get()).getDataSuccess(3, 1, th.getMessage());
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int handleApkUpgrade() {
        String strCheckSD = AndroidUtils.checkSD(AndroidUtils.getPrimaryStoragePath(), "/BusRes/ApkRes", Constants.APK_RES_NAME, true);
        if (strCheckSD == null || strCheckSD.trim().equals("")) {
            return 2;
        }
        new SilentInstall();
        if (new File(strCheckSD).exists()) {
            return SilentInstall.installSilent(strCheckSD);
        }
        return 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String fileImportExport(int i) throws Throwable {
        String fileFormat;
        String[] primaryStoragePath = AndroidUtils.getPrimaryStoragePath();
        if (i == 1) {
            LogUtils.d("导入资源", "导入开始......");
            String strCheckSD = AndroidUtils.checkSD(primaryStoragePath, "/BusRes/BusImport", "SourceFile.zip", false);
            if (strCheckSD == null || strCheckSD.trim().equals("")) {
                strCheckSD = AndroidUtils.checkSD(primaryStoragePath, "/BusRes/BusImport", "SourceFile.rar", false);
            }
            LogUtils.d("导入资源", "资源路径：" + strCheckSD);
            if (strCheckSD != null && !strCheckSD.trim().equals("")) {
                if (ImportAnalysisSDDate(strCheckSD)) {
                    queryArraySelectLineInfo();
                    LogUtils.d("导入资源", "导入结束......");
                    return "0";
                }
                LogUtils.d("导入资源", "导入结束......");
                return "1";
            }
            LogUtils.d("导入资源", "导入结束......");
            return "2";
        }
        if (i == 3) {
            LogUtils.setIsWriter(false);
            String strCheckSD2 = AndroidUtils.checkSD(primaryStoragePath, "/BusRes", "Log", false);
            if (strCheckSD2 == null || strCheckSD2.trim().equals("")) {
                return "3";
            }
            FileUtils.delAllFile(strCheckSD2);
            String str = Constants.SD_ROOT + Constants.LOG_RES_PATH + "/info.log";
            if (!new File(str).exists()) {
                return "2";
            }
            boolean zCopySDToSD = FileUtils.copySDToSD(str, strCheckSD2 + "/info.log");
            try {
                Thread.sleep(8000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!zCopySDToSD) {
                return "1";
            }
            LogUtils.setIsWriter(true);
            return "0";
        }
        String strCheckSD3 = AndroidUtils.checkSD(primaryStoragePath, "", "BusRes", false);
        if (strCheckSD3 == null || strCheckSD3.trim().equals("")) {
            return "2";
        }
        LogUtils.d(this, "fromFile2:" + strCheckSD3);
        List<LineNameModel> listQueryArrayLineName = queryArrayLineName();
        LogUtils.d(this, "lineNameModels:" + listQueryArrayLineName.size());
        List<BusLineInfoModel> listQueryArrayLineInfo = queryArrayLineInfo();
        if (listQueryArrayLineInfo == null || listQueryArrayLineInfo.size() <= 0) {
            fileFormat = "csv";
        } else {
            fileFormat = listQueryArrayLineInfo.get(0).getFileFormat();
            if (fileFormat != null && !fileFormat.trim().equals("") && fileFormat.trim().equals("xls")) {
                File file = new File(listQueryArrayLineInfo.get(0).getFilePath() + "/lineInfo.xls");
                if (file.exists()) {
                    FileUtils.deleteByPath(file.getPath());
                }
                FileUtils.writeLineInfoXls(listQueryArrayLineInfo, file);
            } else {
                File file2 = new File(listQueryArrayLineInfo.get(0).getFilePath() + "/lineInfo.csv");
                if (file2.exists()) {
                    FileUtils.deleteByPath(file2.getPath());
                }
                FileUtils.writeLineInfoCsv(listQueryArrayLineInfo, file2);
            }
            listQueryArrayLineInfo.clear();
        }
        LogUtils.d("FilePresenter", "queryArrayMaintenanceInfo:");
        List<MaintenanceModel> listQueryArrayMaintenanceInfo = queryArrayMaintenanceInfo();
        if (listQueryArrayMaintenanceInfo != null && listQueryArrayMaintenanceInfo.size() > 0) {
            LogUtils.d("FilePresenter", "fileFormat:" + fileFormat);
            if (fileFormat != null && !fileFormat.trim().equals("") && fileFormat.trim().equals("xls")) {
                File file3 = new File(listQueryArrayMaintenanceInfo.get(0).getFilePath() + "/Vchinfo.xls");
                LogUtils.d("FilePresenter", "getPath:" + file3.getPath());
                if (file3.exists()) {
                    FileUtils.deleteByPath(file3.getPath());
                }
                FileUtils.writeMaintenanceInfoXls(listQueryArrayMaintenanceInfo, file3);
            } else {
                File file4 = new File(listQueryArrayMaintenanceInfo.get(0).getFilePath() + "/Vchinfo.csv");
                if (file4.exists()) {
                    FileUtils.deleteByPath(file4.getPath());
                }
                FileUtils.writeMaintenanceInfoCsv(listQueryArrayMaintenanceInfo, file4);
            }
            listQueryArrayMaintenanceInfo.clear();
        }
        LogUtils.d("FilePresenter", "queryArrayMessageInfo:");
        List<MessageModel> listQueryArrayMessageInfo = queryArrayMessageInfo();
        if (listQueryArrayMessageInfo != null && listQueryArrayMessageInfo.size() > 0) {
            LogUtils.d("FilePresenter", "fileFormat:" + fileFormat);
            if (fileFormat != null && !fileFormat.trim().equals("") && fileFormat.trim().equals("xls")) {
                File file5 = new File(listQueryArrayMessageInfo.get(0).getFilePath() + "/Message.xls");
                LogUtils.d("FilePresenter", "getPath:" + file5.getPath());
                if (file5.exists()) {
                    FileUtils.deleteByPath(file5.getPath());
                }
                FileUtils.writeMessageModelInfoXls(listQueryArrayMessageInfo, file5);
            } else {
                File file6 = new File(listQueryArrayMessageInfo.get(0).getFilePath() + "/Message.csv");
                if (file6.exists()) {
                    FileUtils.deleteByPath(file6.getPath());
                }
                FileUtils.writeMessageModelInfoCsv(listQueryArrayMessageInfo, file6);
            }
            listQueryArrayMessageInfo.clear();
        }
        List<ConfigInfoModel> listQueryArrayConfigInfo = queryArrayConfigInfo();
        if (listQueryArrayConfigInfo != null && listQueryArrayConfigInfo.size() > 0) {
            if (fileFormat != null && !fileFormat.trim().equals("") && fileFormat.trim().equals("xls")) {
                File file7 = new File(listQueryArrayConfigInfo.get(0).getFilePath() + "/config.xls");
                if (file7.exists()) {
                    FileUtils.deleteByPath(file7.getPath());
                }
                FileUtils.writeConfigInfoXls(listQueryArrayConfigInfo, file7);
            } else {
                File file8 = new File(listQueryArrayConfigInfo.get(0).getFilePath() + "/config.csv");
                if (file8.exists()) {
                    FileUtils.deleteByPath(file8.getPath());
                }
                FileUtils.writeConfigInfoCsv(listQueryArrayConfigInfo, file8);
            }
            listQueryArrayConfigInfo.clear();
        }
        if (listQueryArrayLineName != null && listQueryArrayLineName.size() > 0) {
            for (LineNameModel lineNameModel : listQueryArrayLineName) {
                LogUtils.d(this, "getBusName:" + lineNameModel.getBusName());
                List<BusLineModel> listQueryArrayBusLineName = queryArrayBusLineName(lineNameModel.getBusName());
                LogUtils.d(this, "lineModels:" + listQueryArrayBusLineName.size());
                if (listQueryArrayBusLineName != null && listQueryArrayBusLineName.size() > 0) {
                    if (fileFormat != null && !fileFormat.trim().equals("") && fileFormat.trim().equals("xls")) {
                        File file9 = new File(listQueryArrayBusLineName.get(0).getBusFilePath() + "/" + listQueryArrayBusLineName.get(0).getDirectionName() + ".xls");
                        if (file9.exists()) {
                            FileUtils.deleteByPath(file9.getPath());
                        }
                        FileUtils.writeBusLineXls(listQueryArrayBusLineName, file9);
                    } else {
                        File file10 = new File(listQueryArrayBusLineName.get(0).getBusFilePath() + "/" + listQueryArrayBusLineName.get(0).getDirectionName() + ".csv");
                        if (file10.exists()) {
                            FileUtils.deleteByPath(file10.getPath());
                        }
                        FileUtils.writeBusLineCsv(listQueryArrayBusLineName, file10);
                    }
                    listQueryArrayBusLineName.clear();
                }
                List<BusLineFriendRemindModel> listQueryArrayBusLineFR = queryArrayBusLineFR(lineNameModel.getBusName());
                LogUtils.d(this, "lineFriendRemindModels:" + listQueryArrayBusLineFR.size());
                if (listQueryArrayBusLineFR != null && listQueryArrayBusLineFR.size() > 0) {
                    if (fileFormat != null && !fileFormat.trim().equals("") && fileFormat.trim().equals("xls")) {
                        File file11 = new File(listQueryArrayBusLineFR.get(0).getFilePath() + "/" + listQueryArrayBusLineFR.get(0).getDirectionName() + "Remind.xls");
                        if (file11.exists()) {
                            FileUtils.deleteByPath(file11.getPath());
                        }
                        FileUtils.writeBusLineFRXls(listQueryArrayBusLineFR, file11);
                    } else {
                        File file12 = new File(listQueryArrayBusLineFR.get(0).getFilePath() + "/" + listQueryArrayBusLineFR.get(0).getDirectionName() + "Remind.csv");
                        if (file12.exists()) {
                            FileUtils.deleteByPath(file12.getPath());
                        }
                        FileUtils.writeBusLineFRCsv(listQueryArrayBusLineFR, file12);
                    }
                    listQueryArrayBusLineFR.clear();
                }
            }
            listQueryArrayLineName.clear();
        }
        String str2 = strCheckSD3 + "/BusExport/";
        File file13 = new File(str2);
        if (!file13.exists()) {
            file13.mkdirs();
        }
        String str3 = str2 + "SourceFile.zip";
        if (new File(str3).exists()) {
            FileUtils.deleteByPath(str3);
        }
        try {
            ZipUtils.ZipFolder(Constants.SD_ROOT + Constants.BUS_RES_SFile_PATH, str3);
            try {
                Thread.sleep(8000L);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
            return "0";
        } catch (Exception e3) {
            e3.printStackTrace();
            return "1";
        }
    }

    private List<LineNameModel> queryArrayLineName() {
        ArrayList arrayList = new ArrayList();
        Cursor cursorRawQuery = GreenDaoUtils.getSingleTon().getmDaoSession().getDatabase().rawQuery("SELECT " + BusLineModelDao.Properties.DirectionName.columnName + " FROM " + BusLineModelDao.TABLENAME + " GROUP BY " + BusLineModelDao.Properties.DirectionName.columnName, null);
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

    private List<BusLineInfoModel> queryArrayLineInfo() {
        return GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineInfoModelDao().loadAll();
    }

    private List<BusLineModel> queryArrayBusLineName(String str) {
        return GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineModelDao().queryBuilder().where(BusLineModelDao.Properties.DirectionName.eq(str), new WhereCondition[0]).orderAsc(BusLineModelDao.Properties.BusNo).list();
    }

    private List<BusLineFriendRemindModel> queryArrayBusLineFR(String str) {
        GreenDaoUtils.getSingleTon().getmDaoSession().clear();
        return GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineFriendRemindModelDao().queryBuilder().where(BusLineFriendRemindModelDao.Properties.DirectionName.eq(str), new WhereCondition[0]).orderAsc(BusLineFriendRemindModelDao.Properties.FrNo).list();
    }

    private boolean ImportAnalysisSDDate(String str) {
        LogUtils.d(this, "analysisSDDate Start fromFile:" + str);
        if (str != null) {
            File file = new File(str);
            if (file.exists()) {
                if (FileUtils.analysisBusFile(str)) {
                    List<ConfigInfoModel> listQueryArrayConfigInfo = queryArrayConfigInfo();
                    if (listQueryArrayConfigInfo != null && listQueryArrayConfigInfo.size() > 0) {
                        for (ConfigInfoModel configInfoModel : listQueryArrayConfigInfo) {
                            if (!configInfoModel.getConfigItem().trim().equals("LanguageSettings")) {
                                SPUserInfoUtils.put(AppApplication.getContext(), configInfoModel.getConfigItem(), configInfoModel.getConfigValue());
                            } else if (!JavaUtils.isEmpty(configInfoModel.getConfigValue())) {
                                SPUtil.getInstance(AppApplication.getContext()).saveLanguage(configInfoModel.getConfigValue());
                            } else {
                                SPUtil.getInstance(AppApplication.getContext()).saveLanguage("0");
                            }
                        }
                    }
                    String str2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(file.lastModified()));
                    LogUtils.d("PublicFileTime", "公共文件修改时间：" + str2);
                    SPUserInfoUtils.put(AppApplication.getContext(), SPUserInfoUtils.SOURCEFILELASTMODIFYTIME, str2);
                    LogUtils.d("导入资源", "资源导入成功");
                    return true;
                }
                LogUtils.d("导入资源", "资源导入失败");
            }
        }
        return false;
    }

    private void queryLineName() {
        String string;
        new ArrayList();
        Cursor cursorRawQuery = GreenDaoUtils.getSingleTon().getmDaoSession().getDatabase().rawQuery("SELECT " + BusLineModelDao.Properties.BusLineName.columnName + " FROM " + BusLineModelDao.TABLENAME + " GROUP BY " + BusLineModelDao.Properties.BusLineName.columnName, null);
        try {
            if (cursorRawQuery.moveToFirst() && (string = cursorRawQuery.getString(0)) != null && !string.equals("")) {
                SPUserInfoUtils.put(AppApplication.getInstance(), SPUserInfoUtils.BUSLINENAME, string);
                SPUserInfoUtils.put(AppApplication.getInstance(), SPUserInfoUtils.BUSDIRECTIONNAME, string + "S");
                SPUserInfoUtils.put(AppApplication.getInstance(), SPUserInfoUtils.ISSWITCHBUSLINE, true);
            }
        } finally {
            cursorRawQuery.close();
        }
    }

    private void queryArraySelectLineInfo() {
        BusLineInfoModel busLineInfoModelUnique = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineInfoModelDao().queryBuilder().where(BusLineInfoModelDao.Properties.ISelect.in(1), new WhereCondition[0]).unique();
        if (busLineInfoModelUnique != null) {
            SPUserInfoUtils.put(AppApplication.getInstance(), SPUserInfoUtils.BUSLINENAME, busLineInfoModelUnique.getLineName());
            SPUserInfoUtils.put(AppApplication.getInstance(), SPUserInfoUtils.BUSDIRECTIONNAME, busLineInfoModelUnique.getLineName() + "S");
            SPUserInfoUtils.put(AppApplication.getInstance(), SPUserInfoUtils.LINEATTRIBUTE, Integer.valueOf(busLineInfoModelUnique.getAttribute()));
            SPUserInfoUtils.put(AppApplication.getInstance(), SPUserInfoUtils.LINENUMBER, busLineInfoModelUnique.getLineNumber());
        }
    }

    private List<ConfigInfoModel> queryArrayConfigInfo() {
        return GreenDaoUtils.getSingleTon().getmDaoSession().getConfigInfoModelDao().loadAll();
    }

    private List<MaintenanceModel> queryArrayMaintenanceInfo() {
        return GreenDaoUtils.getSingleTon().getmDaoSession().getMaintenanceModelDao().queryBuilder().orderAsc(MaintenanceModelDao.Properties.MId).list();
    }

    private List<MessageModel> queryArrayMessageInfo() {
        return GreenDaoUtils.getSingleTon().getmDaoSession().getMessageModelDao().queryBuilder().orderAsc(MessageModelDao.Properties.MessageNo).list();
    }
}
