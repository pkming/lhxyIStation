package com.lianhexinye.m90.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

/* JADX INFO: loaded from: classes2.dex */
public class ConfigInfoModelDao extends AbstractDao<ConfigInfoModel, Long> {
    public static final String TABLENAME = "CONFIG_INFO_MODEL";

    public static class Properties {
        public static final Property _id = new Property(0, Long.class, "_id", true, "_id");
        public static final Property ConfigItem = new Property(1, String.class, "configItem", false, "CONFIG_ITEM");
        public static final Property ConfigValue = new Property(2, String.class, "configValue", false, "CONFIG_VALUE");
        public static final Property ConfigExplain = new Property(3, String.class, "configExplain", false, "CONFIG_EXPLAIN");
        public static final Property FilePath = new Property(4, String.class, "filePath", false, "FILE_PATH");
    }

    @Override // org.greenrobot.greendao.AbstractDao
    protected final boolean isEntityUpdateable() {
        return true;
    }

    public ConfigInfoModelDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public ConfigInfoModelDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(Database database, boolean z) {
        database.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "\"CONFIG_INFO_MODEL\" (\"_id\" INTEGER PRIMARY KEY ,\"CONFIG_ITEM\" TEXT,\"CONFIG_VALUE\" TEXT,\"CONFIG_EXPLAIN\" TEXT,\"FILE_PATH\" TEXT);");
    }

    public static void dropTable(Database database, boolean z) {
        database.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "\"CONFIG_INFO_MODEL\"");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(DatabaseStatement databaseStatement, ConfigInfoModel configInfoModel) {
        databaseStatement.clearBindings();
        Long l = configInfoModel.get_id();
        if (l != null) {
            databaseStatement.bindLong(1, l.longValue());
        }
        String configItem = configInfoModel.getConfigItem();
        if (configItem != null) {
            databaseStatement.bindString(2, configItem);
        }
        String configValue = configInfoModel.getConfigValue();
        if (configValue != null) {
            databaseStatement.bindString(3, configValue);
        }
        String configExplain = configInfoModel.getConfigExplain();
        if (configExplain != null) {
            databaseStatement.bindString(4, configExplain);
        }
        String filePath = configInfoModel.getFilePath();
        if (filePath != null) {
            databaseStatement.bindString(5, filePath);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(SQLiteStatement sQLiteStatement, ConfigInfoModel configInfoModel) {
        sQLiteStatement.clearBindings();
        Long l = configInfoModel.get_id();
        if (l != null) {
            sQLiteStatement.bindLong(1, l.longValue());
        }
        String configItem = configInfoModel.getConfigItem();
        if (configItem != null) {
            sQLiteStatement.bindString(2, configItem);
        }
        String configValue = configInfoModel.getConfigValue();
        if (configValue != null) {
            sQLiteStatement.bindString(3, configValue);
        }
        String configExplain = configInfoModel.getConfigExplain();
        if (configExplain != null) {
            sQLiteStatement.bindString(4, configExplain);
        }
        String filePath = configInfoModel.getFilePath();
        if (filePath != null) {
            sQLiteStatement.bindString(5, filePath);
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
    public ConfigInfoModel readEntity(Cursor cursor, int i) {
        int i2 = i + 0;
        Long lValueOf = cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2));
        int i3 = i + 1;
        String string = cursor.isNull(i3) ? null : cursor.getString(i3);
        int i4 = i + 2;
        String string2 = cursor.isNull(i4) ? null : cursor.getString(i4);
        int i5 = i + 3;
        int i6 = i + 4;
        return new ConfigInfoModel(lValueOf, string, string2, cursor.isNull(i5) ? null : cursor.getString(i5), cursor.isNull(i6) ? null : cursor.getString(i6));
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public void readEntity(Cursor cursor, ConfigInfoModel configInfoModel, int i) {
        int i2 = i + 0;
        configInfoModel.set_id(cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2)));
        int i3 = i + 1;
        configInfoModel.setConfigItem(cursor.isNull(i3) ? null : cursor.getString(i3));
        int i4 = i + 2;
        configInfoModel.setConfigValue(cursor.isNull(i4) ? null : cursor.getString(i4));
        int i5 = i + 3;
        configInfoModel.setConfigExplain(cursor.isNull(i5) ? null : cursor.getString(i5));
        int i6 = i + 4;
        configInfoModel.setFilePath(cursor.isNull(i6) ? null : cursor.getString(i6));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final Long updateKeyAfterInsert(ConfigInfoModel configInfoModel, long j) {
        configInfoModel.set_id(Long.valueOf(j));
        return Long.valueOf(j);
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public Long getKey(ConfigInfoModel configInfoModel) {
        if (configInfoModel != null) {
            return configInfoModel.get_id();
        }
        return null;
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public boolean hasKey(ConfigInfoModel configInfoModel) {
        return configInfoModel.get_id() != null;
    }
}
