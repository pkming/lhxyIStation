package com.lianhexinye.m90.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

/* JADX INFO: loaded from: classes2.dex */
public class PlayListModelDao extends AbstractDao<PlayListModel, Long> {
    public static final String TABLENAME = "PLAY_LIST_MODEL";

    public static class Properties {
        public static final Property _id = new Property(0, Long.class, "_id", true, "_id");
        public static final Property ChannelId = new Property(1, Integer.TYPE, "channelId", false, "CHANNEL_ID");
        public static final Property Name = new Property(2, String.class, "name", false, "NAME");
        public static final Property StartTime = new Property(3, String.class, "startTime", false, "START_TIME");
        public static final Property PlaylistId = new Property(4, Integer.TYPE, "playlistId", false, "PLAYLIST_ID");
    }

    @Override // org.greenrobot.greendao.AbstractDao
    protected final boolean isEntityUpdateable() {
        return true;
    }

    public PlayListModelDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public PlayListModelDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(Database database, boolean z) {
        database.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "\"PLAY_LIST_MODEL\" (\"_id\" INTEGER PRIMARY KEY ,\"CHANNEL_ID\" INTEGER NOT NULL ,\"NAME\" TEXT,\"START_TIME\" TEXT,\"PLAYLIST_ID\" INTEGER NOT NULL );");
    }

    public static void dropTable(Database database, boolean z) {
        database.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "\"PLAY_LIST_MODEL\"");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(DatabaseStatement databaseStatement, PlayListModel playListModel) {
        databaseStatement.clearBindings();
        Long l = playListModel.get_id();
        if (l != null) {
            databaseStatement.bindLong(1, l.longValue());
        }
        databaseStatement.bindLong(2, playListModel.getChannelId());
        String name = playListModel.getName();
        if (name != null) {
            databaseStatement.bindString(3, name);
        }
        String startTime = playListModel.getStartTime();
        if (startTime != null) {
            databaseStatement.bindString(4, startTime);
        }
        databaseStatement.bindLong(5, playListModel.getPlaylistId());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(SQLiteStatement sQLiteStatement, PlayListModel playListModel) {
        sQLiteStatement.clearBindings();
        Long l = playListModel.get_id();
        if (l != null) {
            sQLiteStatement.bindLong(1, l.longValue());
        }
        sQLiteStatement.bindLong(2, playListModel.getChannelId());
        String name = playListModel.getName();
        if (name != null) {
            sQLiteStatement.bindString(3, name);
        }
        String startTime = playListModel.getStartTime();
        if (startTime != null) {
            sQLiteStatement.bindString(4, startTime);
        }
        sQLiteStatement.bindLong(5, playListModel.getPlaylistId());
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
    public PlayListModel readEntity(Cursor cursor, int i) {
        int i2 = i + 0;
        Long lValueOf = cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2));
        int i3 = cursor.getInt(i + 1);
        int i4 = i + 2;
        String string = cursor.isNull(i4) ? null : cursor.getString(i4);
        int i5 = i + 3;
        return new PlayListModel(lValueOf, i3, string, cursor.isNull(i5) ? null : cursor.getString(i5), cursor.getInt(i + 4));
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public void readEntity(Cursor cursor, PlayListModel playListModel, int i) {
        int i2 = i + 0;
        playListModel.set_id(cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2)));
        playListModel.setChannelId(cursor.getInt(i + 1));
        int i3 = i + 2;
        playListModel.setName(cursor.isNull(i3) ? null : cursor.getString(i3));
        int i4 = i + 3;
        playListModel.setStartTime(cursor.isNull(i4) ? null : cursor.getString(i4));
        playListModel.setPlaylistId(cursor.getInt(i + 4));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final Long updateKeyAfterInsert(PlayListModel playListModel, long j) {
        playListModel.set_id(Long.valueOf(j));
        return Long.valueOf(j);
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public Long getKey(PlayListModel playListModel) {
        if (playListModel != null) {
            return playListModel.get_id();
        }
        return null;
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public boolean hasKey(PlayListModel playListModel) {
        return playListModel.get_id() != null;
    }
}
