package com.lianhexinye.m90.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

/* JADX INFO: loaded from: classes2.dex */
public class DownLoadFTPModelDao extends AbstractDao<DownLoadFTPModel, Long> {
    public static final String TABLENAME = "DOWN_LOAD_FTPMODEL";

    public static class Properties {
        public static final Property _id = new Property(0, Long.class, "_id", true, "_id");
        public static final Property Url = new Property(1, String.class, "url", false, "URL");
        public static final Property Type = new Property(2, Integer.TYPE, "type", false, "TYPE");
        public static final Property Status = new Property(3, Integer.TYPE, "status", false, "STATUS");
        public static final Property MsgSerialNumber = new Property(4, String.class, "msgSerialNumber", false, "MSG_SERIAL_NUMBER");
        public static final Property FileUpgradeTime = new Property(5, String.class, "fileUpgradeTime", false, "FILE_UPGRADE_TIME");
        public static final Property LocalPath = new Property(6, String.class, "localPath", false, "LOCAL_PATH");
        public static final Property ServerAddress = new Property(7, String.class, "serverAddress", false, "SERVER_ADDRESS");
        public static final Property ServerAddressPort = new Property(8, Integer.TYPE, "serverAddressPort", false, "SERVER_ADDRESS_PORT");
        public static final Property ProtocolType = new Property(9, Integer.TYPE, "protocolType", false, "PROTOCOL_TYPE");
        public static final Property LoginName = new Property(10, String.class, "loginName", false, "LOGIN_NAME");
        public static final Property LoginPwd = new Property(11, String.class, "loginPwd", false, "LOGIN_PWD");
        public static final Property UpgradeType = new Property(12, Integer.TYPE, "upgradeType", false, "UPGRADE_TYPE");
    }

    @Override // org.greenrobot.greendao.AbstractDao
    protected final boolean isEntityUpdateable() {
        return true;
    }

    public DownLoadFTPModelDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public DownLoadFTPModelDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(Database database, boolean z) {
        database.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "\"DOWN_LOAD_FTPMODEL\" (\"_id\" INTEGER PRIMARY KEY ,\"URL\" TEXT,\"TYPE\" INTEGER NOT NULL ,\"STATUS\" INTEGER NOT NULL ,\"MSG_SERIAL_NUMBER\" TEXT,\"FILE_UPGRADE_TIME\" TEXT,\"LOCAL_PATH\" TEXT,\"SERVER_ADDRESS\" TEXT,\"SERVER_ADDRESS_PORT\" INTEGER NOT NULL ,\"PROTOCOL_TYPE\" INTEGER NOT NULL ,\"LOGIN_NAME\" TEXT,\"LOGIN_PWD\" TEXT,\"UPGRADE_TYPE\" INTEGER NOT NULL );");
    }

    public static void dropTable(Database database, boolean z) {
        database.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "\"DOWN_LOAD_FTPMODEL\"");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(DatabaseStatement databaseStatement, DownLoadFTPModel downLoadFTPModel) {
        databaseStatement.clearBindings();
        Long l = downLoadFTPModel.get_id();
        if (l != null) {
            databaseStatement.bindLong(1, l.longValue());
        }
        String url = downLoadFTPModel.getUrl();
        if (url != null) {
            databaseStatement.bindString(2, url);
        }
        databaseStatement.bindLong(3, downLoadFTPModel.getType());
        databaseStatement.bindLong(4, downLoadFTPModel.getStatus());
        String msgSerialNumber = downLoadFTPModel.getMsgSerialNumber();
        if (msgSerialNumber != null) {
            databaseStatement.bindString(5, msgSerialNumber);
        }
        String fileUpgradeTime = downLoadFTPModel.getFileUpgradeTime();
        if (fileUpgradeTime != null) {
            databaseStatement.bindString(6, fileUpgradeTime);
        }
        String localPath = downLoadFTPModel.getLocalPath();
        if (localPath != null) {
            databaseStatement.bindString(7, localPath);
        }
        String serverAddress = downLoadFTPModel.getServerAddress();
        if (serverAddress != null) {
            databaseStatement.bindString(8, serverAddress);
        }
        databaseStatement.bindLong(9, downLoadFTPModel.getServerAddressPort());
        databaseStatement.bindLong(10, downLoadFTPModel.getProtocolType());
        String loginName = downLoadFTPModel.getLoginName();
        if (loginName != null) {
            databaseStatement.bindString(11, loginName);
        }
        String loginPwd = downLoadFTPModel.getLoginPwd();
        if (loginPwd != null) {
            databaseStatement.bindString(12, loginPwd);
        }
        databaseStatement.bindLong(13, downLoadFTPModel.getUpgradeType());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(SQLiteStatement sQLiteStatement, DownLoadFTPModel downLoadFTPModel) {
        sQLiteStatement.clearBindings();
        Long l = downLoadFTPModel.get_id();
        if (l != null) {
            sQLiteStatement.bindLong(1, l.longValue());
        }
        String url = downLoadFTPModel.getUrl();
        if (url != null) {
            sQLiteStatement.bindString(2, url);
        }
        sQLiteStatement.bindLong(3, downLoadFTPModel.getType());
        sQLiteStatement.bindLong(4, downLoadFTPModel.getStatus());
        String msgSerialNumber = downLoadFTPModel.getMsgSerialNumber();
        if (msgSerialNumber != null) {
            sQLiteStatement.bindString(5, msgSerialNumber);
        }
        String fileUpgradeTime = downLoadFTPModel.getFileUpgradeTime();
        if (fileUpgradeTime != null) {
            sQLiteStatement.bindString(6, fileUpgradeTime);
        }
        String localPath = downLoadFTPModel.getLocalPath();
        if (localPath != null) {
            sQLiteStatement.bindString(7, localPath);
        }
        String serverAddress = downLoadFTPModel.getServerAddress();
        if (serverAddress != null) {
            sQLiteStatement.bindString(8, serverAddress);
        }
        sQLiteStatement.bindLong(9, downLoadFTPModel.getServerAddressPort());
        sQLiteStatement.bindLong(10, downLoadFTPModel.getProtocolType());
        String loginName = downLoadFTPModel.getLoginName();
        if (loginName != null) {
            sQLiteStatement.bindString(11, loginName);
        }
        String loginPwd = downLoadFTPModel.getLoginPwd();
        if (loginPwd != null) {
            sQLiteStatement.bindString(12, loginPwd);
        }
        sQLiteStatement.bindLong(13, downLoadFTPModel.getUpgradeType());
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.greenrobot.greendao.AbstractDao
    public Long readKey(Cursor cursor, int i) {
        int i2 = i + 0;
        if (cursor.isNull(i2)) {
            return null;
        }
        return Long.valueOf(cursor.getLong(i2));
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.greenrobot.greendao.AbstractDao
    public DownLoadFTPModel readEntity(Cursor cursor, int i) {
        int i2 = i + 0;
        int i3 = i + 1;
        int i4 = i + 4;
        int i5 = i + 5;
        int i6 = i + 6;
        int i7 = i + 7;
        int i8 = i + 10;
        int i9 = i + 11;
        return new DownLoadFTPModel(cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2)), cursor.isNull(i3) ? null : cursor.getString(i3), cursor.getInt(i + 2), cursor.getInt(i + 3), cursor.isNull(i4) ? null : cursor.getString(i4), cursor.isNull(i5) ? null : cursor.getString(i5), cursor.isNull(i6) ? null : cursor.getString(i6), cursor.isNull(i7) ? null : cursor.getString(i7), cursor.getInt(i + 8), cursor.getInt(i + 9), cursor.isNull(i8) ? null : cursor.getString(i8), cursor.isNull(i9) ? null : cursor.getString(i9), cursor.getInt(i + 12));
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public void readEntity(Cursor cursor, DownLoadFTPModel downLoadFTPModel, int i) {
        int i2 = i + 0;
        downLoadFTPModel.set_id(cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2)));
        int i3 = i + 1;
        downLoadFTPModel.setUrl(cursor.isNull(i3) ? null : cursor.getString(i3));
        downLoadFTPModel.setType(cursor.getInt(i + 2));
        downLoadFTPModel.setStatus(cursor.getInt(i + 3));
        int i4 = i + 4;
        downLoadFTPModel.setMsgSerialNumber(cursor.isNull(i4) ? null : cursor.getString(i4));
        int i5 = i + 5;
        downLoadFTPModel.setFileUpgradeTime(cursor.isNull(i5) ? null : cursor.getString(i5));
        int i6 = i + 6;
        downLoadFTPModel.setLocalPath(cursor.isNull(i6) ? null : cursor.getString(i6));
        int i7 = i + 7;
        downLoadFTPModel.setServerAddress(cursor.isNull(i7) ? null : cursor.getString(i7));
        downLoadFTPModel.setServerAddressPort(cursor.getInt(i + 8));
        downLoadFTPModel.setProtocolType(cursor.getInt(i + 9));
        int i8 = i + 10;
        downLoadFTPModel.setLoginName(cursor.isNull(i8) ? null : cursor.getString(i8));
        int i9 = i + 11;
        downLoadFTPModel.setLoginPwd(cursor.isNull(i9) ? null : cursor.getString(i9));
        downLoadFTPModel.setUpgradeType(cursor.getInt(i + 12));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final Long updateKeyAfterInsert(DownLoadFTPModel downLoadFTPModel, long j) {
        downLoadFTPModel.set_id(Long.valueOf(j));
        return Long.valueOf(j);
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public Long getKey(DownLoadFTPModel downLoadFTPModel) {
        if (downLoadFTPModel != null) {
            return downLoadFTPModel.get_id();
        }
        return null;
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public boolean hasKey(DownLoadFTPModel downLoadFTPModel) {
        return downLoadFTPModel.get_id() != null;
    }
}
