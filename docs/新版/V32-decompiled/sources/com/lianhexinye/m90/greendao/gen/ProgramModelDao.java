package com.lianhexinye.m90.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.provider.CalendarContract;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

/* JADX INFO: loaded from: classes2.dex */
public class ProgramModelDao extends AbstractDao<ProgramModel, Long> {
    public static final String TABLENAME = "PROGRAM_MODEL";

    public static class Properties {
        public static final Property _id = new Property(0, Long.class, "_id", true, "_id");
        public static final Property PlaylistId = new Property(1, Integer.TYPE, "playlistId", false, "PLAYLIST_ID");
        public static final Property Programid = new Property(2, Integer.TYPE, "programid", false, "PROGRAMID");
        public static final Property Name = new Property(3, String.class, "name", false, "NAME");
        public static final Property PlayTime = new Property(4, String.class, "playTime", false, "PLAY_TIME");
        public static final Property EndTime = new Property(5, String.class, CalendarContract.EXTRA_EVENT_END_TIME, false, "END_TIME");
        public static final Property Duration = new Property(6, Integer.TYPE, "duration", false, "DURATION");
        public static final Property BackgroundType = new Property(7, Integer.TYPE, "backgroundType", false, "BACKGROUND_TYPE");
        public static final Property BackgroundContent = new Property(8, String.class, "backgroundContent", false, "BACKGROUND_CONTENT");
        public static final Property BackgroundAudio = new Property(9, String.class, "backgroundAudio", false, "BACKGROUND_AUDIO");
        public static final Property Mute = new Property(10, Boolean.TYPE, "mute", false, "MUTE");
        public static final Property IsTile = new Property(11, Boolean.TYPE, "isTile", false, "IS_TILE");
        public static final Property ContentTypes = new Property(12, String.class, "contentTypes", false, "CONTENT_TYPES");
    }

    @Override // org.greenrobot.greendao.AbstractDao
    protected final boolean isEntityUpdateable() {
        return true;
    }

    public ProgramModelDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public ProgramModelDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(Database database, boolean z) {
        database.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "\"PROGRAM_MODEL\" (\"_id\" INTEGER PRIMARY KEY ,\"PLAYLIST_ID\" INTEGER NOT NULL ,\"PROGRAMID\" INTEGER NOT NULL ,\"NAME\" TEXT,\"PLAY_TIME\" TEXT,\"END_TIME\" TEXT,\"DURATION\" INTEGER NOT NULL ,\"BACKGROUND_TYPE\" INTEGER NOT NULL ,\"BACKGROUND_CONTENT\" TEXT,\"BACKGROUND_AUDIO\" TEXT,\"MUTE\" INTEGER NOT NULL ,\"IS_TILE\" INTEGER NOT NULL ,\"CONTENT_TYPES\" TEXT);");
    }

    public static void dropTable(Database database, boolean z) {
        database.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "\"PROGRAM_MODEL\"");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(DatabaseStatement databaseStatement, ProgramModel programModel) {
        databaseStatement.clearBindings();
        Long l = programModel.get_id();
        if (l != null) {
            databaseStatement.bindLong(1, l.longValue());
        }
        databaseStatement.bindLong(2, programModel.getPlaylistId());
        databaseStatement.bindLong(3, programModel.getProgramid());
        String name = programModel.getName();
        if (name != null) {
            databaseStatement.bindString(4, name);
        }
        String playTime = programModel.getPlayTime();
        if (playTime != null) {
            databaseStatement.bindString(5, playTime);
        }
        String endTime = programModel.getEndTime();
        if (endTime != null) {
            databaseStatement.bindString(6, endTime);
        }
        databaseStatement.bindLong(7, programModel.getDuration());
        databaseStatement.bindLong(8, programModel.getBackgroundType());
        String backgroundContent = programModel.getBackgroundContent();
        if (backgroundContent != null) {
            databaseStatement.bindString(9, backgroundContent);
        }
        String backgroundAudio = programModel.getBackgroundAudio();
        if (backgroundAudio != null) {
            databaseStatement.bindString(10, backgroundAudio);
        }
        databaseStatement.bindLong(11, programModel.getMute() ? 1L : 0L);
        databaseStatement.bindLong(12, programModel.getIsTile() ? 1L : 0L);
        String contentTypes = programModel.getContentTypes();
        if (contentTypes != null) {
            databaseStatement.bindString(13, contentTypes);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(SQLiteStatement sQLiteStatement, ProgramModel programModel) {
        sQLiteStatement.clearBindings();
        Long l = programModel.get_id();
        if (l != null) {
            sQLiteStatement.bindLong(1, l.longValue());
        }
        sQLiteStatement.bindLong(2, programModel.getPlaylistId());
        sQLiteStatement.bindLong(3, programModel.getProgramid());
        String name = programModel.getName();
        if (name != null) {
            sQLiteStatement.bindString(4, name);
        }
        String playTime = programModel.getPlayTime();
        if (playTime != null) {
            sQLiteStatement.bindString(5, playTime);
        }
        String endTime = programModel.getEndTime();
        if (endTime != null) {
            sQLiteStatement.bindString(6, endTime);
        }
        sQLiteStatement.bindLong(7, programModel.getDuration());
        sQLiteStatement.bindLong(8, programModel.getBackgroundType());
        String backgroundContent = programModel.getBackgroundContent();
        if (backgroundContent != null) {
            sQLiteStatement.bindString(9, backgroundContent);
        }
        String backgroundAudio = programModel.getBackgroundAudio();
        if (backgroundAudio != null) {
            sQLiteStatement.bindString(10, backgroundAudio);
        }
        sQLiteStatement.bindLong(11, programModel.getMute() ? 1L : 0L);
        sQLiteStatement.bindLong(12, programModel.getIsTile() ? 1L : 0L);
        String contentTypes = programModel.getContentTypes();
        if (contentTypes != null) {
            sQLiteStatement.bindString(13, contentTypes);
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
    public ProgramModel readEntity(Cursor cursor, int i) {
        int i2 = i + 0;
        int i3 = i + 3;
        int i4 = i + 4;
        int i5 = i + 5;
        int i6 = i + 8;
        int i7 = i + 9;
        int i8 = i + 12;
        return new ProgramModel(cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2)), cursor.getInt(i + 1), cursor.getInt(i + 2), cursor.isNull(i3) ? null : cursor.getString(i3), cursor.isNull(i4) ? null : cursor.getString(i4), cursor.isNull(i5) ? null : cursor.getString(i5), cursor.getInt(i + 6), cursor.getInt(i + 7), cursor.isNull(i6) ? null : cursor.getString(i6), cursor.isNull(i7) ? null : cursor.getString(i7), cursor.getShort(i + 10) != 0, cursor.getShort(i + 11) != 0, cursor.isNull(i8) ? null : cursor.getString(i8));
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public void readEntity(Cursor cursor, ProgramModel programModel, int i) {
        int i2 = i + 0;
        programModel.set_id(cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2)));
        programModel.setPlaylistId(cursor.getInt(i + 1));
        programModel.setProgramid(cursor.getInt(i + 2));
        int i3 = i + 3;
        programModel.setName(cursor.isNull(i3) ? null : cursor.getString(i3));
        int i4 = i + 4;
        programModel.setPlayTime(cursor.isNull(i4) ? null : cursor.getString(i4));
        int i5 = i + 5;
        programModel.setEndTime(cursor.isNull(i5) ? null : cursor.getString(i5));
        programModel.setDuration(cursor.getInt(i + 6));
        programModel.setBackgroundType(cursor.getInt(i + 7));
        int i6 = i + 8;
        programModel.setBackgroundContent(cursor.isNull(i6) ? null : cursor.getString(i6));
        int i7 = i + 9;
        programModel.setBackgroundAudio(cursor.isNull(i7) ? null : cursor.getString(i7));
        programModel.setMute(cursor.getShort(i + 10) != 0);
        programModel.setIsTile(cursor.getShort(i + 11) != 0);
        int i8 = i + 12;
        programModel.setContentTypes(cursor.isNull(i8) ? null : cursor.getString(i8));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final Long updateKeyAfterInsert(ProgramModel programModel, long j) {
        programModel.set_id(Long.valueOf(j));
        return Long.valueOf(j);
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public Long getKey(ProgramModel programModel) {
        if (programModel != null) {
            return programModel.get_id();
        }
        return null;
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public boolean hasKey(ProgramModel programModel) {
        return programModel.get_id() != null;
    }
}
