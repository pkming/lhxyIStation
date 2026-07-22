package com.lianhexinye.m90.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

/* JADX INFO: loaded from: classes2.dex */
public class BusMediaModelDao extends AbstractDao<BusMediaModel, Long> {
    public static final String TABLENAME = "BUS_MEDIA_MODEL";

    public static class Properties {
        public static final Property _id = new Property(0, Long.class, "_id", true, "_id");
        public static final Property DownloadUrls = new Property(1, String.class, "downloadUrls", false, "DOWNLOAD_URLS");
        public static final Property DownloadState = new Property(2, Integer.TYPE, "downloadState", false, "DOWNLOAD_STATE");
        public static final Property FileResult = new Property(3, Integer.TYPE, "fileResult", false, "FILE_RESULT");
        public static final Property DataState = new Property(4, Integer.TYPE, "dataState", false, "DATA_STATE");
        public static final Property ProgramId = new Property(5, Integer.TYPE, "programId", false, "PROGRAM_ID");
        public static final Property ContentType = new Property(6, Integer.TYPE, "contentType", false, "CONTENT_TYPE");
    }

    @Override // org.greenrobot.greendao.AbstractDao
    protected final boolean isEntityUpdateable() {
        return true;
    }

    public BusMediaModelDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public BusMediaModelDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(Database database, boolean z) {
        database.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "\"BUS_MEDIA_MODEL\" (\"_id\" INTEGER PRIMARY KEY ,\"DOWNLOAD_URLS\" TEXT,\"DOWNLOAD_STATE\" INTEGER NOT NULL ,\"FILE_RESULT\" INTEGER NOT NULL ,\"DATA_STATE\" INTEGER NOT NULL ,\"PROGRAM_ID\" INTEGER NOT NULL ,\"CONTENT_TYPE\" INTEGER NOT NULL );");
    }

    public static void dropTable(Database database, boolean z) {
        database.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "\"BUS_MEDIA_MODEL\"");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(DatabaseStatement databaseStatement, BusMediaModel busMediaModel) {
        databaseStatement.clearBindings();
        Long l = busMediaModel.get_id();
        if (l != null) {
            databaseStatement.bindLong(1, l.longValue());
        }
        String downloadUrls = busMediaModel.getDownloadUrls();
        if (downloadUrls != null) {
            databaseStatement.bindString(2, downloadUrls);
        }
        databaseStatement.bindLong(3, busMediaModel.getDownloadState());
        databaseStatement.bindLong(4, busMediaModel.getFileResult());
        databaseStatement.bindLong(5, busMediaModel.getDataState());
        databaseStatement.bindLong(6, busMediaModel.getProgramId());
        databaseStatement.bindLong(7, busMediaModel.getContentType());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(SQLiteStatement sQLiteStatement, BusMediaModel busMediaModel) {
        sQLiteStatement.clearBindings();
        Long l = busMediaModel.get_id();
        if (l != null) {
            sQLiteStatement.bindLong(1, l.longValue());
        }
        String downloadUrls = busMediaModel.getDownloadUrls();
        if (downloadUrls != null) {
            sQLiteStatement.bindString(2, downloadUrls);
        }
        sQLiteStatement.bindLong(3, busMediaModel.getDownloadState());
        sQLiteStatement.bindLong(4, busMediaModel.getFileResult());
        sQLiteStatement.bindLong(5, busMediaModel.getDataState());
        sQLiteStatement.bindLong(6, busMediaModel.getProgramId());
        sQLiteStatement.bindLong(7, busMediaModel.getContentType());
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
    public BusMediaModel readEntity(Cursor cursor, int i) {
        int i2 = i + 0;
        Long lValueOf = cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2));
        int i3 = i + 1;
        return new BusMediaModel(lValueOf, cursor.isNull(i3) ? null : cursor.getString(i3), cursor.getInt(i + 2), cursor.getInt(i + 3), cursor.getInt(i + 4), cursor.getInt(i + 5), cursor.getInt(i + 6));
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public void readEntity(Cursor cursor, BusMediaModel busMediaModel, int i) {
        int i2 = i + 0;
        busMediaModel.set_id(cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2)));
        int i3 = i + 1;
        busMediaModel.setDownloadUrls(cursor.isNull(i3) ? null : cursor.getString(i3));
        busMediaModel.setDownloadState(cursor.getInt(i + 2));
        busMediaModel.setFileResult(cursor.getInt(i + 3));
        busMediaModel.setDataState(cursor.getInt(i + 4));
        busMediaModel.setProgramId(cursor.getInt(i + 5));
        busMediaModel.setContentType(cursor.getInt(i + 6));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final Long updateKeyAfterInsert(BusMediaModel busMediaModel, long j) {
        busMediaModel.set_id(Long.valueOf(j));
        return Long.valueOf(j);
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public Long getKey(BusMediaModel busMediaModel) {
        if (busMediaModel != null) {
            return busMediaModel.get_id();
        }
        return null;
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public boolean hasKey(BusMediaModel busMediaModel) {
        return busMediaModel.get_id() != null;
    }
}
