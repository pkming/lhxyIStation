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
public class BusLineFriendRemindModelDao extends AbstractDao<BusLineFriendRemindModel, Long> {
    public static final String TABLENAME = "BUS_LINE_FRIEND_REMIND_MODEL";

    public static class Properties {
        public static final Property _id = new Property(0, Long.class, "_id", true, "_id");
        public static final Property FrNo = new Property(1, Integer.TYPE, "frNo", false, "FR_NO");
        public static final Property BusLineName = new Property(2, String.class, SPUserInfoUtils.BUSLINENAME, false, "BUS_LINE_NAME");
        public static final Property Direction = new Property(3, Integer.TYPE, "direction", false, "DIRECTION");
        public static final Property FrVoice = new Property(4, String.class, "frVoice", false, "FR_VOICE");
        public static final Property Longitude = new Property(5, String.class, "longitude", false, "LONGITUDE");
        public static final Property Latitude = new Property(6, String.class, "latitude", false, "LATITUDE");
        public static final Property FilePath = new Property(7, String.class, "filePath", false, "FILE_PATH");
        public static final Property DirectionName = new Property(8, String.class, "directionName", false, "DIRECTION_NAME");
        public static final Property Mileage = new Property(9, String.class, "mileage", false, "MILEAGE");
        public static final Property CrossCode = new Property(10, String.class, "crossCode", false, "CROSS_CODE");
        public static final Property CrossPrompt = new Property(11, String.class, "crossPrompt", false, "CROSS_PROMPT");
        public static final Property CrossDeparturePrompt = new Property(12, String.class, "crossDeparturePrompt", false, "CROSS_DEPARTURE_PROMPT");
        public static final Property CrossExpansion = new Property(13, String.class, "crossExpansion", false, "CROSS_EXPANSION");
        public static final Property CrossDepartureExpansion = new Property(14, String.class, "crossDepartureExpansion", false, "CROSS_DEPARTURE_EXPANSION");
        public static final Property CrossSpeedLimit = new Property(15, String.class, "crossSpeedLimit", false, "CROSS_SPEED_LIMIT");
        public static final Property CrossType = new Property(16, String.class, "crossType", false, "CROSS_TYPE");
        public static final Property VoiceNot = new Property(17, String.class, "voiceNot", false, "VOICE_NOT");
    }

    @Override // org.greenrobot.greendao.AbstractDao
    protected final boolean isEntityUpdateable() {
        return true;
    }

    public BusLineFriendRemindModelDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public BusLineFriendRemindModelDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(Database database, boolean z) {
        database.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "\"BUS_LINE_FRIEND_REMIND_MODEL\" (\"_id\" INTEGER PRIMARY KEY ,\"FR_NO\" INTEGER NOT NULL ,\"BUS_LINE_NAME\" TEXT,\"DIRECTION\" INTEGER NOT NULL ,\"FR_VOICE\" TEXT,\"LONGITUDE\" TEXT,\"LATITUDE\" TEXT,\"FILE_PATH\" TEXT,\"DIRECTION_NAME\" TEXT,\"MILEAGE\" TEXT,\"CROSS_CODE\" TEXT,\"CROSS_PROMPT\" TEXT,\"CROSS_DEPARTURE_PROMPT\" TEXT,\"CROSS_EXPANSION\" TEXT,\"CROSS_DEPARTURE_EXPANSION\" TEXT,\"CROSS_SPEED_LIMIT\" TEXT,\"CROSS_TYPE\" TEXT,\"VOICE_NOT\" TEXT);");
    }

    public static void dropTable(Database database, boolean z) {
        database.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "\"BUS_LINE_FRIEND_REMIND_MODEL\"");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(DatabaseStatement databaseStatement, BusLineFriendRemindModel busLineFriendRemindModel) {
        databaseStatement.clearBindings();
        Long l = busLineFriendRemindModel.get_id();
        if (l != null) {
            databaseStatement.bindLong(1, l.longValue());
        }
        databaseStatement.bindLong(2, busLineFriendRemindModel.getFrNo());
        String busLineName = busLineFriendRemindModel.getBusLineName();
        if (busLineName != null) {
            databaseStatement.bindString(3, busLineName);
        }
        databaseStatement.bindLong(4, busLineFriendRemindModel.getDirection());
        String frVoice = busLineFriendRemindModel.getFrVoice();
        if (frVoice != null) {
            databaseStatement.bindString(5, frVoice);
        }
        String longitude = busLineFriendRemindModel.getLongitude();
        if (longitude != null) {
            databaseStatement.bindString(6, longitude);
        }
        String latitude = busLineFriendRemindModel.getLatitude();
        if (latitude != null) {
            databaseStatement.bindString(7, latitude);
        }
        String filePath = busLineFriendRemindModel.getFilePath();
        if (filePath != null) {
            databaseStatement.bindString(8, filePath);
        }
        String directionName = busLineFriendRemindModel.getDirectionName();
        if (directionName != null) {
            databaseStatement.bindString(9, directionName);
        }
        String mileage = busLineFriendRemindModel.getMileage();
        if (mileage != null) {
            databaseStatement.bindString(10, mileage);
        }
        String crossCode = busLineFriendRemindModel.getCrossCode();
        if (crossCode != null) {
            databaseStatement.bindString(11, crossCode);
        }
        String crossPrompt = busLineFriendRemindModel.getCrossPrompt();
        if (crossPrompt != null) {
            databaseStatement.bindString(12, crossPrompt);
        }
        String crossDeparturePrompt = busLineFriendRemindModel.getCrossDeparturePrompt();
        if (crossDeparturePrompt != null) {
            databaseStatement.bindString(13, crossDeparturePrompt);
        }
        String crossExpansion = busLineFriendRemindModel.getCrossExpansion();
        if (crossExpansion != null) {
            databaseStatement.bindString(14, crossExpansion);
        }
        String crossDepartureExpansion = busLineFriendRemindModel.getCrossDepartureExpansion();
        if (crossDepartureExpansion != null) {
            databaseStatement.bindString(15, crossDepartureExpansion);
        }
        String crossSpeedLimit = busLineFriendRemindModel.getCrossSpeedLimit();
        if (crossSpeedLimit != null) {
            databaseStatement.bindString(16, crossSpeedLimit);
        }
        String crossType = busLineFriendRemindModel.getCrossType();
        if (crossType != null) {
            databaseStatement.bindString(17, crossType);
        }
        String voiceNot = busLineFriendRemindModel.getVoiceNot();
        if (voiceNot != null) {
            databaseStatement.bindString(18, voiceNot);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(SQLiteStatement sQLiteStatement, BusLineFriendRemindModel busLineFriendRemindModel) {
        sQLiteStatement.clearBindings();
        Long l = busLineFriendRemindModel.get_id();
        if (l != null) {
            sQLiteStatement.bindLong(1, l.longValue());
        }
        sQLiteStatement.bindLong(2, busLineFriendRemindModel.getFrNo());
        String busLineName = busLineFriendRemindModel.getBusLineName();
        if (busLineName != null) {
            sQLiteStatement.bindString(3, busLineName);
        }
        sQLiteStatement.bindLong(4, busLineFriendRemindModel.getDirection());
        String frVoice = busLineFriendRemindModel.getFrVoice();
        if (frVoice != null) {
            sQLiteStatement.bindString(5, frVoice);
        }
        String longitude = busLineFriendRemindModel.getLongitude();
        if (longitude != null) {
            sQLiteStatement.bindString(6, longitude);
        }
        String latitude = busLineFriendRemindModel.getLatitude();
        if (latitude != null) {
            sQLiteStatement.bindString(7, latitude);
        }
        String filePath = busLineFriendRemindModel.getFilePath();
        if (filePath != null) {
            sQLiteStatement.bindString(8, filePath);
        }
        String directionName = busLineFriendRemindModel.getDirectionName();
        if (directionName != null) {
            sQLiteStatement.bindString(9, directionName);
        }
        String mileage = busLineFriendRemindModel.getMileage();
        if (mileage != null) {
            sQLiteStatement.bindString(10, mileage);
        }
        String crossCode = busLineFriendRemindModel.getCrossCode();
        if (crossCode != null) {
            sQLiteStatement.bindString(11, crossCode);
        }
        String crossPrompt = busLineFriendRemindModel.getCrossPrompt();
        if (crossPrompt != null) {
            sQLiteStatement.bindString(12, crossPrompt);
        }
        String crossDeparturePrompt = busLineFriendRemindModel.getCrossDeparturePrompt();
        if (crossDeparturePrompt != null) {
            sQLiteStatement.bindString(13, crossDeparturePrompt);
        }
        String crossExpansion = busLineFriendRemindModel.getCrossExpansion();
        if (crossExpansion != null) {
            sQLiteStatement.bindString(14, crossExpansion);
        }
        String crossDepartureExpansion = busLineFriendRemindModel.getCrossDepartureExpansion();
        if (crossDepartureExpansion != null) {
            sQLiteStatement.bindString(15, crossDepartureExpansion);
        }
        String crossSpeedLimit = busLineFriendRemindModel.getCrossSpeedLimit();
        if (crossSpeedLimit != null) {
            sQLiteStatement.bindString(16, crossSpeedLimit);
        }
        String crossType = busLineFriendRemindModel.getCrossType();
        if (crossType != null) {
            sQLiteStatement.bindString(17, crossType);
        }
        String voiceNot = busLineFriendRemindModel.getVoiceNot();
        if (voiceNot != null) {
            sQLiteStatement.bindString(18, voiceNot);
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
    public BusLineFriendRemindModel readEntity(Cursor cursor, int i) {
        int i2 = i + 0;
        Long lValueOf = cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2));
        int i3 = cursor.getInt(i + 1);
        int i4 = i + 2;
        String string = cursor.isNull(i4) ? null : cursor.getString(i4);
        int i5 = cursor.getInt(i + 3);
        int i6 = i + 4;
        String string2 = cursor.isNull(i6) ? null : cursor.getString(i6);
        int i7 = i + 5;
        String string3 = cursor.isNull(i7) ? null : cursor.getString(i7);
        int i8 = i + 6;
        String string4 = cursor.isNull(i8) ? null : cursor.getString(i8);
        int i9 = i + 7;
        String string5 = cursor.isNull(i9) ? null : cursor.getString(i9);
        int i10 = i + 8;
        String string6 = cursor.isNull(i10) ? null : cursor.getString(i10);
        int i11 = i + 9;
        String string7 = cursor.isNull(i11) ? null : cursor.getString(i11);
        int i12 = i + 10;
        String string8 = cursor.isNull(i12) ? null : cursor.getString(i12);
        int i13 = i + 11;
        String string9 = cursor.isNull(i13) ? null : cursor.getString(i13);
        int i14 = i + 12;
        String string10 = cursor.isNull(i14) ? null : cursor.getString(i14);
        int i15 = i + 13;
        String string11 = cursor.isNull(i15) ? null : cursor.getString(i15);
        int i16 = i + 14;
        String string12 = cursor.isNull(i16) ? null : cursor.getString(i16);
        int i17 = i + 15;
        String string13 = cursor.isNull(i17) ? null : cursor.getString(i17);
        int i18 = i + 16;
        String string14 = cursor.isNull(i18) ? null : cursor.getString(i18);
        int i19 = i + 17;
        return new BusLineFriendRemindModel(lValueOf, i3, string, i5, string2, string3, string4, string5, string6, string7, string8, string9, string10, string11, string12, string13, string14, cursor.isNull(i19) ? null : cursor.getString(i19));
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public void readEntity(Cursor cursor, BusLineFriendRemindModel busLineFriendRemindModel, int i) {
        int i2 = i + 0;
        busLineFriendRemindModel.set_id(cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2)));
        busLineFriendRemindModel.setFrNo(cursor.getInt(i + 1));
        int i3 = i + 2;
        busLineFriendRemindModel.setBusLineName(cursor.isNull(i3) ? null : cursor.getString(i3));
        busLineFriendRemindModel.setDirection(cursor.getInt(i + 3));
        int i4 = i + 4;
        busLineFriendRemindModel.setFrVoice(cursor.isNull(i4) ? null : cursor.getString(i4));
        int i5 = i + 5;
        busLineFriendRemindModel.setLongitude(cursor.isNull(i5) ? null : cursor.getString(i5));
        int i6 = i + 6;
        busLineFriendRemindModel.setLatitude(cursor.isNull(i6) ? null : cursor.getString(i6));
        int i7 = i + 7;
        busLineFriendRemindModel.setFilePath(cursor.isNull(i7) ? null : cursor.getString(i7));
        int i8 = i + 8;
        busLineFriendRemindModel.setDirectionName(cursor.isNull(i8) ? null : cursor.getString(i8));
        int i9 = i + 9;
        busLineFriendRemindModel.setMileage(cursor.isNull(i9) ? null : cursor.getString(i9));
        int i10 = i + 10;
        busLineFriendRemindModel.setCrossCode(cursor.isNull(i10) ? null : cursor.getString(i10));
        int i11 = i + 11;
        busLineFriendRemindModel.setCrossPrompt(cursor.isNull(i11) ? null : cursor.getString(i11));
        int i12 = i + 12;
        busLineFriendRemindModel.setCrossDeparturePrompt(cursor.isNull(i12) ? null : cursor.getString(i12));
        int i13 = i + 13;
        busLineFriendRemindModel.setCrossExpansion(cursor.isNull(i13) ? null : cursor.getString(i13));
        int i14 = i + 14;
        busLineFriendRemindModel.setCrossDepartureExpansion(cursor.isNull(i14) ? null : cursor.getString(i14));
        int i15 = i + 15;
        busLineFriendRemindModel.setCrossSpeedLimit(cursor.isNull(i15) ? null : cursor.getString(i15));
        int i16 = i + 16;
        busLineFriendRemindModel.setCrossType(cursor.isNull(i16) ? null : cursor.getString(i16));
        int i17 = i + 17;
        busLineFriendRemindModel.setVoiceNot(cursor.isNull(i17) ? null : cursor.getString(i17));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final Long updateKeyAfterInsert(BusLineFriendRemindModel busLineFriendRemindModel, long j) {
        busLineFriendRemindModel.set_id(Long.valueOf(j));
        return Long.valueOf(j);
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public Long getKey(BusLineFriendRemindModel busLineFriendRemindModel) {
        if (busLineFriendRemindModel != null) {
            return busLineFriendRemindModel.get_id();
        }
        return null;
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public boolean hasKey(BusLineFriendRemindModel busLineFriendRemindModel) {
        return busLineFriendRemindModel.get_id() != null;
    }
}
