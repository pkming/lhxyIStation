package com.lianhexinye.m90.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

/* JADX INFO: loaded from: classes2.dex */
public class MaintenanceModelDao extends AbstractDao<MaintenanceModel, Long> {
    public static final String TABLENAME = "MAINTENANCE_MODEL";

    public static class Properties {
        public static final Property _id = new Property(0, Long.class, "_id", true, "_id");
        public static final Property MId = new Property(1, Integer.TYPE, "mId", false, "M_ID");
        public static final Property Content = new Property(2, String.class, "content", false, "CONTENT");
        public static final Property Remarks = new Property(3, String.class, "remarks", false, "REMARKS");
        public static final Property FilePath = new Property(4, String.class, "filePath", false, "FILE_PATH");
        public static final Property FileFormat = new Property(5, String.class, "fileFormat", false, "FILE_FORMAT");
    }

    @Override // org.greenrobot.greendao.AbstractDao
    protected final boolean isEntityUpdateable() {
        return true;
    }

    public MaintenanceModelDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public MaintenanceModelDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(Database database, boolean z) {
        database.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "\"MAINTENANCE_MODEL\" (\"_id\" INTEGER PRIMARY KEY ,\"M_ID\" INTEGER NOT NULL ,\"CONTENT\" TEXT,\"REMARKS\" TEXT,\"FILE_PATH\" TEXT,\"FILE_FORMAT\" TEXT);");
    }

    public static void dropTable(Database database, boolean z) {
        database.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "\"MAINTENANCE_MODEL\"");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(DatabaseStatement databaseStatement, MaintenanceModel maintenanceModel) {
        databaseStatement.clearBindings();
        Long l = maintenanceModel.get_id();
        if (l != null) {
            databaseStatement.bindLong(1, l.longValue());
        }
        databaseStatement.bindLong(2, maintenanceModel.getMId());
        String content = maintenanceModel.getContent();
        if (content != null) {
            databaseStatement.bindString(3, content);
        }
        String remarks = maintenanceModel.getRemarks();
        if (remarks != null) {
            databaseStatement.bindString(4, remarks);
        }
        String filePath = maintenanceModel.getFilePath();
        if (filePath != null) {
            databaseStatement.bindString(5, filePath);
        }
        String fileFormat = maintenanceModel.getFileFormat();
        if (fileFormat != null) {
            databaseStatement.bindString(6, fileFormat);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(SQLiteStatement sQLiteStatement, MaintenanceModel maintenanceModel) {
        sQLiteStatement.clearBindings();
        Long l = maintenanceModel.get_id();
        if (l != null) {
            sQLiteStatement.bindLong(1, l.longValue());
        }
        sQLiteStatement.bindLong(2, maintenanceModel.getMId());
        String content = maintenanceModel.getContent();
        if (content != null) {
            sQLiteStatement.bindString(3, content);
        }
        String remarks = maintenanceModel.getRemarks();
        if (remarks != null) {
            sQLiteStatement.bindString(4, remarks);
        }
        String filePath = maintenanceModel.getFilePath();
        if (filePath != null) {
            sQLiteStatement.bindString(5, filePath);
        }
        String fileFormat = maintenanceModel.getFileFormat();
        if (fileFormat != null) {
            sQLiteStatement.bindString(6, fileFormat);
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
    public MaintenanceModel readEntity(Cursor cursor, int i) {
        int i2 = i + 0;
        Long lValueOf = cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2));
        int i3 = cursor.getInt(i + 1);
        int i4 = i + 2;
        String string = cursor.isNull(i4) ? null : cursor.getString(i4);
        int i5 = i + 3;
        String string2 = cursor.isNull(i5) ? null : cursor.getString(i5);
        int i6 = i + 4;
        int i7 = i + 5;
        return new MaintenanceModel(lValueOf, i3, string, string2, cursor.isNull(i6) ? null : cursor.getString(i6), cursor.isNull(i7) ? null : cursor.getString(i7));
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public void readEntity(Cursor cursor, MaintenanceModel maintenanceModel, int i) {
        int i2 = i + 0;
        maintenanceModel.set_id(cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2)));
        maintenanceModel.setMId(cursor.getInt(i + 1));
        int i3 = i + 2;
        maintenanceModel.setContent(cursor.isNull(i3) ? null : cursor.getString(i3));
        int i4 = i + 3;
        maintenanceModel.setRemarks(cursor.isNull(i4) ? null : cursor.getString(i4));
        int i5 = i + 4;
        maintenanceModel.setFilePath(cursor.isNull(i5) ? null : cursor.getString(i5));
        int i6 = i + 5;
        maintenanceModel.setFileFormat(cursor.isNull(i6) ? null : cursor.getString(i6));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final Long updateKeyAfterInsert(MaintenanceModel maintenanceModel, long j) {
        maintenanceModel.set_id(Long.valueOf(j));
        return Long.valueOf(j);
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public Long getKey(MaintenanceModel maintenanceModel) {
        if (maintenanceModel != null) {
            return maintenanceModel.get_id();
        }
        return null;
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public boolean hasKey(MaintenanceModel maintenanceModel) {
        return maintenanceModel.get_id() != null;
    }
}
