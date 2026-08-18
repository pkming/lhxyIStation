package com.lianhexinye.m90.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

/* JADX INFO: loaded from: classes2.dex */
public class DownLoadInfoModelDao extends AbstractDao<DownLoadInfoModel, Long> {
    public static final String TABLENAME = "DOWN_LOAD_INFO_MODEL";

    public static class Properties {
        public static final Property _id = new Property(0, Long.class, "_id", true, "_id");
        public static final Property Thread_id = new Property(1, Integer.TYPE, "thread_id", false, "THREAD_ID");
        public static final Property Start_pos = new Property(2, Long.class, "start_pos", false, "START_POS");
        public static final Property End_pos = new Property(3, Long.class, "end_pos", false, "END_POS");
        public static final Property Compelete_size = new Property(4, Long.class, "compelete_size", false, "COMPELETE_SIZE");
        public static final Property Url = new Property(5, String.class, "url", false, "URL");
        public static final Property File_createtime = new Property(6, String.class, "file_createtime", false, "FILE_CREATETIME");
    }

    @Override // org.greenrobot.greendao.AbstractDao
    protected final boolean isEntityUpdateable() {
        return true;
    }

    public DownLoadInfoModelDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public DownLoadInfoModelDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(Database database, boolean z) {
        database.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "\"DOWN_LOAD_INFO_MODEL\" (\"_id\" INTEGER PRIMARY KEY ,\"THREAD_ID\" INTEGER NOT NULL ,\"START_POS\" INTEGER,\"END_POS\" INTEGER,\"COMPELETE_SIZE\" INTEGER,\"URL\" TEXT,\"FILE_CREATETIME\" TEXT);");
    }

    public static void dropTable(Database database, boolean z) {
        database.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "\"DOWN_LOAD_INFO_MODEL\"");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(DatabaseStatement databaseStatement, DownLoadInfoModel downLoadInfoModel) {
        databaseStatement.clearBindings();
        Long l = downLoadInfoModel.get_id();
        if (l != null) {
            databaseStatement.bindLong(1, l.longValue());
        }
        databaseStatement.bindLong(2, downLoadInfoModel.getThread_id());
        Long start_pos = downLoadInfoModel.getStart_pos();
        if (start_pos != null) {
            databaseStatement.bindLong(3, start_pos.longValue());
        }
        Long end_pos = downLoadInfoModel.getEnd_pos();
        if (end_pos != null) {
            databaseStatement.bindLong(4, end_pos.longValue());
        }
        Long compelete_size = downLoadInfoModel.getCompelete_size();
        if (compelete_size != null) {
            databaseStatement.bindLong(5, compelete_size.longValue());
        }
        String url = downLoadInfoModel.getUrl();
        if (url != null) {
            databaseStatement.bindString(6, url);
        }
        String file_createtime = downLoadInfoModel.getFile_createtime();
        if (file_createtime != null) {
            databaseStatement.bindString(7, file_createtime);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(SQLiteStatement sQLiteStatement, DownLoadInfoModel downLoadInfoModel) {
        sQLiteStatement.clearBindings();
        Long l = downLoadInfoModel.get_id();
        if (l != null) {
            sQLiteStatement.bindLong(1, l.longValue());
        }
        sQLiteStatement.bindLong(2, downLoadInfoModel.getThread_id());
        Long start_pos = downLoadInfoModel.getStart_pos();
        if (start_pos != null) {
            sQLiteStatement.bindLong(3, start_pos.longValue());
        }
        Long end_pos = downLoadInfoModel.getEnd_pos();
        if (end_pos != null) {
            sQLiteStatement.bindLong(4, end_pos.longValue());
        }
        Long compelete_size = downLoadInfoModel.getCompelete_size();
        if (compelete_size != null) {
            sQLiteStatement.bindLong(5, compelete_size.longValue());
        }
        String url = downLoadInfoModel.getUrl();
        if (url != null) {
            sQLiteStatement.bindString(6, url);
        }
        String file_createtime = downLoadInfoModel.getFile_createtime();
        if (file_createtime != null) {
            sQLiteStatement.bindString(7, file_createtime);
        }
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
    public DownLoadInfoModel readEntity(Cursor cursor, int i) {
        int i2 = i + 0;
        Long lValueOf = cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2));
        int i3 = cursor.getInt(i + 1);
        int i4 = i + 2;
        Long lValueOf2 = cursor.isNull(i4) ? null : Long.valueOf(cursor.getLong(i4));
        int i5 = i + 3;
        Long lValueOf3 = cursor.isNull(i5) ? null : Long.valueOf(cursor.getLong(i5));
        int i6 = i + 4;
        Long lValueOf4 = cursor.isNull(i6) ? null : Long.valueOf(cursor.getLong(i6));
        int i7 = i + 5;
        int i8 = i + 6;
        return new DownLoadInfoModel(lValueOf, i3, lValueOf2, lValueOf3, lValueOf4, cursor.isNull(i7) ? null : cursor.getString(i7), cursor.isNull(i8) ? null : cursor.getString(i8));
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public void readEntity(Cursor cursor, DownLoadInfoModel downLoadInfoModel, int i) {
        int i2 = i + 0;
        downLoadInfoModel.set_id(cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2)));
        downLoadInfoModel.setThread_id(cursor.getInt(i + 1));
        int i3 = i + 2;
        downLoadInfoModel.setStart_pos(cursor.isNull(i3) ? null : Long.valueOf(cursor.getLong(i3)));
        int i4 = i + 3;
        downLoadInfoModel.setEnd_pos(cursor.isNull(i4) ? null : Long.valueOf(cursor.getLong(i4)));
        int i5 = i + 4;
        downLoadInfoModel.setCompelete_size(cursor.isNull(i5) ? null : Long.valueOf(cursor.getLong(i5)));
        int i6 = i + 5;
        downLoadInfoModel.setUrl(cursor.isNull(i6) ? null : cursor.getString(i6));
        int i7 = i + 6;
        downLoadInfoModel.setFile_createtime(cursor.isNull(i7) ? null : cursor.getString(i7));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final Long updateKeyAfterInsert(DownLoadInfoModel downLoadInfoModel, long j) {
        downLoadInfoModel.set_id(Long.valueOf(j));
        return Long.valueOf(j);
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public Long getKey(DownLoadInfoModel downLoadInfoModel) {
        if (downLoadInfoModel != null) {
            return downLoadInfoModel.get_id();
        }
        return null;
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public boolean hasKey(DownLoadInfoModel downLoadInfoModel) {
        return downLoadInfoModel.get_id() != null;
    }
}
