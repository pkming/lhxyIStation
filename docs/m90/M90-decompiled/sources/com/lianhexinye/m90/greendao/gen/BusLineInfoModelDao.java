package com.lianhexinye.m90.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

/* JADX INFO: loaded from: classes2.dex */
public class BusLineInfoModelDao extends AbstractDao<BusLineInfoModel, Long> {
    public static final String TABLENAME = "BUS_LINE_INFO_MODEL";

    public static class Properties {
        public static final Property _id = new Property(0, Long.class, "_id", true, "_id");
        public static final Property LineNo = new Property(1, Integer.TYPE, "lineNo", false, "LINE_NO");
        public static final Property LineName = new Property(2, String.class, "lineName", false, "LINE_NAME");
        public static final Property ISelect = new Property(3, Boolean.TYPE, "iSelect", false, "I_SELECT");
        public static final Property Attribute = new Property(4, Integer.TYPE, "attribute", false, "ATTRIBUTE");
        public static final Property LineNumber = new Property(5, String.class, SPUserInfoUtils.LINENUMBER, false, "LINE_NUMBER");
        public static final Property FilePath = new Property(6, String.class, "filePath", false, "FILE_PATH");
        public static final Property FileFormat = new Property(7, String.class, "fileFormat", false, "FILE_FORMAT");
    }

    @Override // org.greenrobot.greendao.AbstractDao
    protected final boolean isEntityUpdateable() {
        return true;
    }

    public BusLineInfoModelDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public BusLineInfoModelDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(Database database, boolean z) {
        database.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "\"BUS_LINE_INFO_MODEL\" (\"_id\" INTEGER PRIMARY KEY ,\"LINE_NO\" INTEGER NOT NULL ,\"LINE_NAME\" TEXT,\"I_SELECT\" INTEGER NOT NULL ,\"ATTRIBUTE\" INTEGER NOT NULL ,\"LINE_NUMBER\" TEXT,\"FILE_PATH\" TEXT,\"FILE_FORMAT\" TEXT);");
    }

    public static void dropTable(Database database, boolean z) {
        database.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "\"BUS_LINE_INFO_MODEL\"");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(DatabaseStatement databaseStatement, BusLineInfoModel busLineInfoModel) {
        databaseStatement.clearBindings();
        Long l = busLineInfoModel.get_id();
        if (l != null) {
            databaseStatement.bindLong(1, l.longValue());
        }
        databaseStatement.bindLong(2, busLineInfoModel.getLineNo());
        String lineName = busLineInfoModel.getLineName();
        if (lineName != null) {
            databaseStatement.bindString(3, lineName);
        }
        databaseStatement.bindLong(4, busLineInfoModel.getISelect() ? 1L : 0L);
        databaseStatement.bindLong(5, busLineInfoModel.getAttribute());
        String lineNumber = busLineInfoModel.getLineNumber();
        if (lineNumber != null) {
            databaseStatement.bindString(6, lineNumber);
        }
        String filePath = busLineInfoModel.getFilePath();
        if (filePath != null) {
            databaseStatement.bindString(7, filePath);
        }
        String fileFormat = busLineInfoModel.getFileFormat();
        if (fileFormat != null) {
            databaseStatement.bindString(8, fileFormat);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(SQLiteStatement sQLiteStatement, BusLineInfoModel busLineInfoModel) {
        sQLiteStatement.clearBindings();
        Long l = busLineInfoModel.get_id();
        if (l != null) {
            sQLiteStatement.bindLong(1, l.longValue());
        }
        sQLiteStatement.bindLong(2, busLineInfoModel.getLineNo());
        String lineName = busLineInfoModel.getLineName();
        if (lineName != null) {
            sQLiteStatement.bindString(3, lineName);
        }
        sQLiteStatement.bindLong(4, busLineInfoModel.getISelect() ? 1L : 0L);
        sQLiteStatement.bindLong(5, busLineInfoModel.getAttribute());
        String lineNumber = busLineInfoModel.getLineNumber();
        if (lineNumber != null) {
            sQLiteStatement.bindString(6, lineNumber);
        }
        String filePath = busLineInfoModel.getFilePath();
        if (filePath != null) {
            sQLiteStatement.bindString(7, filePath);
        }
        String fileFormat = busLineInfoModel.getFileFormat();
        if (fileFormat != null) {
            sQLiteStatement.bindString(8, fileFormat);
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
    public BusLineInfoModel readEntity(Cursor cursor, int i) {
        int i2 = i + 0;
        Long lValueOf = cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2));
        int i3 = cursor.getInt(i + 1);
        int i4 = i + 2;
        String string = cursor.isNull(i4) ? null : cursor.getString(i4);
        boolean z = cursor.getShort(i + 3) != 0;
        int i5 = cursor.getInt(i + 4);
        int i6 = i + 5;
        String string2 = cursor.isNull(i6) ? null : cursor.getString(i6);
        int i7 = i + 6;
        int i8 = i + 7;
        return new BusLineInfoModel(lValueOf, i3, string, z, i5, string2, cursor.isNull(i7) ? null : cursor.getString(i7), cursor.isNull(i8) ? null : cursor.getString(i8));
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public void readEntity(Cursor cursor, BusLineInfoModel busLineInfoModel, int i) {
        int i2 = i + 0;
        busLineInfoModel.set_id(cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2)));
        busLineInfoModel.setLineNo(cursor.getInt(i + 1));
        int i3 = i + 2;
        busLineInfoModel.setLineName(cursor.isNull(i3) ? null : cursor.getString(i3));
        busLineInfoModel.setISelect(cursor.getShort(i + 3) != 0);
        busLineInfoModel.setAttribute(cursor.getInt(i + 4));
        int i4 = i + 5;
        busLineInfoModel.setLineNumber(cursor.isNull(i4) ? null : cursor.getString(i4));
        int i5 = i + 6;
        busLineInfoModel.setFilePath(cursor.isNull(i5) ? null : cursor.getString(i5));
        int i6 = i + 7;
        busLineInfoModel.setFileFormat(cursor.isNull(i6) ? null : cursor.getString(i6));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final Long updateKeyAfterInsert(BusLineInfoModel busLineInfoModel, long j) {
        busLineInfoModel.set_id(Long.valueOf(j));
        return Long.valueOf(j);
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public Long getKey(BusLineInfoModel busLineInfoModel) {
        if (busLineInfoModel != null) {
            return busLineInfoModel.get_id();
        }
        return null;
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public boolean hasKey(BusLineInfoModel busLineInfoModel) {
        return busLineInfoModel.get_id() != null;
    }
}
