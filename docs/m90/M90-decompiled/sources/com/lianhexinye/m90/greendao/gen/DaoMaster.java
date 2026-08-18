package com.lianhexinye.m90.greendao.gen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.greenrobot.greendao.AbstractDaoMaster;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseOpenHelper;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.identityscope.IdentityScopeType;

/* JADX INFO: loaded from: classes2.dex */
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 2;

    public static void createAllTables(Database database, boolean z) {
        BusLineFriendRemindModelDao.createTable(database, z);
        BusLineInfoModelDao.createTable(database, z);
        BusLineModelDao.createTable(database, z);
        BusMediaModelDao.createTable(database, z);
        ChannelModelDao.createTable(database, z);
        ConfigInfoModelDao.createTable(database, z);
        DownLoadFTPModelDao.createTable(database, z);
        DownLoadInfoModelDao.createTable(database, z);
        MaintenanceModelDao.createTable(database, z);
        MessageModelDao.createTable(database, z);
        PlayListModelDao.createTable(database, z);
        ProgramModelDao.createTable(database, z);
    }

    public static void dropAllTables(Database database, boolean z) {
        BusLineFriendRemindModelDao.dropTable(database, z);
        BusLineInfoModelDao.dropTable(database, z);
        BusLineModelDao.dropTable(database, z);
        BusMediaModelDao.dropTable(database, z);
        ChannelModelDao.dropTable(database, z);
        ConfigInfoModelDao.dropTable(database, z);
        DownLoadFTPModelDao.dropTable(database, z);
        DownLoadInfoModelDao.dropTable(database, z);
        MaintenanceModelDao.dropTable(database, z);
        MessageModelDao.dropTable(database, z);
        PlayListModelDao.dropTable(database, z);
        ProgramModelDao.dropTable(database, z);
    }

    public static DaoSession newDevSession(Context context, String str) {
        return new DaoMaster(new DevOpenHelper(context, str).getWritableDb()).newSession();
    }

    public DaoMaster(SQLiteDatabase sQLiteDatabase) {
        this(new StandardDatabase(sQLiteDatabase));
    }

    public DaoMaster(Database database) {
        super(database, 2);
        registerDaoClass(BusLineFriendRemindModelDao.class);
        registerDaoClass(BusLineInfoModelDao.class);
        registerDaoClass(BusLineModelDao.class);
        registerDaoClass(BusMediaModelDao.class);
        registerDaoClass(ChannelModelDao.class);
        registerDaoClass(ConfigInfoModelDao.class);
        registerDaoClass(DownLoadFTPModelDao.class);
        registerDaoClass(DownLoadInfoModelDao.class);
        registerDaoClass(MaintenanceModelDao.class);
        registerDaoClass(MessageModelDao.class);
        registerDaoClass(PlayListModelDao.class);
        registerDaoClass(ProgramModelDao.class);
    }

    @Override // org.greenrobot.greendao.AbstractDaoMaster
    public DaoSession newSession() {
        return new DaoSession(this.db, IdentityScopeType.Session, this.daoConfigMap);
    }

    @Override // org.greenrobot.greendao.AbstractDaoMaster
    public DaoSession newSession(IdentityScopeType identityScopeType) {
        return new DaoSession(this.db, identityScopeType, this.daoConfigMap);
    }

    public static abstract class OpenHelper extends DatabaseOpenHelper {
        public OpenHelper(Context context, String str) {
            super(context, str, 2);
        }

        public OpenHelper(Context context, String str, SQLiteDatabase.CursorFactory cursorFactory) {
            super(context, str, cursorFactory, 2);
        }

        @Override // org.greenrobot.greendao.database.DatabaseOpenHelper
        public void onCreate(Database database) {
            Log.i("greenDAO", "Creating tables for schema version 2");
            DaoMaster.createAllTables(database, false);
        }
    }

    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String str) {
            super(context, str);
        }

        public DevOpenHelper(Context context, String str, SQLiteDatabase.CursorFactory cursorFactory) {
            super(context, str, cursorFactory);
        }

        @Override // org.greenrobot.greendao.database.DatabaseOpenHelper
        public void onUpgrade(Database database, int i, int i2) {
            Log.i("greenDAO", "Upgrading schema from version " + i + " to " + i2 + " by dropping all tables");
            DaoMaster.dropAllTables(database, true);
            onCreate(database);
        }
    }
}
