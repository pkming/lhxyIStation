package com.lianhexinye.m90.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

/* JADX INFO: loaded from: classes2.dex */
public class ChannelModelDao extends AbstractDao<ChannelModel, Long> {
    public static final String TABLENAME = "CHANNEL_MODEL";

    public static class Properties {
        public static final Property _id = new Property(0, Long.class, "_id", true, "_id");
        public static final Property ChannelId = new Property(1, Integer.TYPE, "channelId", false, "CHANNEL_ID");
        public static final Property Server = new Property(2, String.class, "server", false, "SERVER");
        public static final Property Name = new Property(3, String.class, "name", false, "NAME");
        public static final Property Version = new Property(4, String.class, "version", false, "VERSION");
        public static final Property PublishDate = new Property(5, String.class, "publishDate", false, "PUBLISH_DATE");
        public static final Property DownloadDate = new Property(6, String.class, "downloadDate", false, "DOWNLOAD_DATE");
        public static final Property DownloadDays = new Property(7, String.class, "downloadDays", false, "DOWNLOAD_DAYS");
    }

    @Override // org.greenrobot.greendao.AbstractDao
    protected final boolean isEntityUpdateable() {
        return true;
    }

    public ChannelModelDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public ChannelModelDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(Database database, boolean z) {
        database.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "\"CHANNEL_MODEL\" (\"_id\" INTEGER PRIMARY KEY ,\"CHANNEL_ID\" INTEGER NOT NULL ,\"SERVER\" TEXT,\"NAME\" TEXT,\"VERSION\" TEXT,\"PUBLISH_DATE\" TEXT,\"DOWNLOAD_DATE\" TEXT,\"DOWNLOAD_DAYS\" TEXT);");
    }

    public static void dropTable(Database database, boolean z) {
        database.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "\"CHANNEL_MODEL\"");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(DatabaseStatement databaseStatement, ChannelModel channelModel) {
        databaseStatement.clearBindings();
        Long l = channelModel.get_id();
        if (l != null) {
            databaseStatement.bindLong(1, l.longValue());
        }
        databaseStatement.bindLong(2, channelModel.getChannelId());
        String server = channelModel.getServer();
        if (server != null) {
            databaseStatement.bindString(3, server);
        }
        String name = channelModel.getName();
        if (name != null) {
            databaseStatement.bindString(4, name);
        }
        String version = channelModel.getVersion();
        if (version != null) {
            databaseStatement.bindString(5, version);
        }
        String publishDate = channelModel.getPublishDate();
        if (publishDate != null) {
            databaseStatement.bindString(6, publishDate);
        }
        String downloadDate = channelModel.getDownloadDate();
        if (downloadDate != null) {
            databaseStatement.bindString(7, downloadDate);
        }
        String downloadDays = channelModel.getDownloadDays();
        if (downloadDays != null) {
            databaseStatement.bindString(8, downloadDays);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(SQLiteStatement sQLiteStatement, ChannelModel channelModel) {
        sQLiteStatement.clearBindings();
        Long l = channelModel.get_id();
        if (l != null) {
            sQLiteStatement.bindLong(1, l.longValue());
        }
        sQLiteStatement.bindLong(2, channelModel.getChannelId());
        String server = channelModel.getServer();
        if (server != null) {
            sQLiteStatement.bindString(3, server);
        }
        String name = channelModel.getName();
        if (name != null) {
            sQLiteStatement.bindString(4, name);
        }
        String version = channelModel.getVersion();
        if (version != null) {
            sQLiteStatement.bindString(5, version);
        }
        String publishDate = channelModel.getPublishDate();
        if (publishDate != null) {
            sQLiteStatement.bindString(6, publishDate);
        }
        String downloadDate = channelModel.getDownloadDate();
        if (downloadDate != null) {
            sQLiteStatement.bindString(7, downloadDate);
        }
        String downloadDays = channelModel.getDownloadDays();
        if (downloadDays != null) {
            sQLiteStatement.bindString(8, downloadDays);
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
    public ChannelModel readEntity(Cursor cursor, int i) {
        int i2 = i + 0;
        Long lValueOf = cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2));
        int i3 = cursor.getInt(i + 1);
        int i4 = i + 2;
        String string = cursor.isNull(i4) ? null : cursor.getString(i4);
        int i5 = i + 3;
        String string2 = cursor.isNull(i5) ? null : cursor.getString(i5);
        int i6 = i + 4;
        String string3 = cursor.isNull(i6) ? null : cursor.getString(i6);
        int i7 = i + 5;
        String string4 = cursor.isNull(i7) ? null : cursor.getString(i7);
        int i8 = i + 6;
        int i9 = i + 7;
        return new ChannelModel(lValueOf, i3, string, string2, string3, string4, cursor.isNull(i8) ? null : cursor.getString(i8), cursor.isNull(i9) ? null : cursor.getString(i9));
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public void readEntity(Cursor cursor, ChannelModel channelModel, int i) {
        int i2 = i + 0;
        channelModel.set_id(cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2)));
        channelModel.setChannelId(cursor.getInt(i + 1));
        int i3 = i + 2;
        channelModel.setServer(cursor.isNull(i3) ? null : cursor.getString(i3));
        int i4 = i + 3;
        channelModel.setName(cursor.isNull(i4) ? null : cursor.getString(i4));
        int i5 = i + 4;
        channelModel.setVersion(cursor.isNull(i5) ? null : cursor.getString(i5));
        int i6 = i + 5;
        channelModel.setPublishDate(cursor.isNull(i6) ? null : cursor.getString(i6));
        int i7 = i + 6;
        channelModel.setDownloadDate(cursor.isNull(i7) ? null : cursor.getString(i7));
        int i8 = i + 7;
        channelModel.setDownloadDays(cursor.isNull(i8) ? null : cursor.getString(i8));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final Long updateKeyAfterInsert(ChannelModel channelModel, long j) {
        channelModel.set_id(Long.valueOf(j));
        return Long.valueOf(j);
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public Long getKey(ChannelModel channelModel) {
        if (channelModel != null) {
            return channelModel.get_id();
        }
        return null;
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public boolean hasKey(ChannelModel channelModel) {
        return channelModel.get_id() != null;
    }
}
