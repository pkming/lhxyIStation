package com.lianhexinye.m90.common.utils.greendao;

import android.database.sqlite.SQLiteDatabase;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.greendao.gen.DaoMaster;
import com.lianhexinye.m90.greendao.gen.DaoSession;

/* JADX INFO: loaded from: classes2.dex */
public class GreenDaoUtils {
    private static GreenDaoUtils greenDaoUtils;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private MyOpenHelper mHelper;

    private GreenDaoUtils() {
    }

    public static GreenDaoUtils getSingleTon() {
        if (greenDaoUtils == null) {
            greenDaoUtils = new GreenDaoUtils();
        }
        return greenDaoUtils;
    }

    private void initGreenDao() {
        MyOpenHelper myOpenHelper = new MyOpenHelper(AppApplication.getInstance(), "m90-db", null);
        this.mHelper = myOpenHelper;
        this.db = myOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(this.db);
        this.mDaoMaster = daoMaster;
        this.mDaoSession = daoMaster.newSession();
    }

    public DaoSession getmDaoSession() {
        if (this.mDaoMaster == null) {
            initGreenDao();
        }
        return this.mDaoSession;
    }

    public SQLiteDatabase getDb() {
        if (this.db == null) {
            initGreenDao();
        }
        return this.db;
    }
}
