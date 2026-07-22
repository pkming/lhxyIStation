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
public class BusLineModelDao extends AbstractDao<BusLineModel, Long> {
    public static final String TABLENAME = "BUS_LINE_MODEL";

    public static class Properties {
        public static final Property _id = new Property(0, Long.class, "_id", true, "_id");
        public static final Property BusNo = new Property(1, Integer.TYPE, "busNo", false, "BUS_NO");
        public static final Property BusSound = new Property(2, String.class, "busSound", false, "BUS_SOUND");
        public static final Property BusFilePath = new Property(3, String.class, "busFilePath", false, "BUS_FILE_PATH");
        public static final Property BusName = new Property(4, String.class, "busName", false, "BUS_NAME");
        public static final Property BusLineName = new Property(5, String.class, SPUserInfoUtils.BUSLINENAME, false, "BUS_LINE_NAME");
        public static final Property Direction = new Property(6, Integer.TYPE, "direction", false, "DIRECTION");
        public static final Property Longitude = new Property(7, String.class, "longitude", false, "LONGITUDE");
        public static final Property Latitude = new Property(8, String.class, "latitude", false, "LATITUDE");
        public static final Property Angle = new Property(9, String.class, "angle", false, "ANGLE");
        public static final Property SiteCode = new Property(10, String.class, "siteCode", false, "SITE_CODE");
        public static final Property StationAdvert = new Property(11, String.class, "stationAdvert", false, "STATION_ADVERT");
        public static final Property DepartureAdvert = new Property(12, String.class, "departureAdvert", false, "DEPARTURE_ADVERT");
        public static final Property StationPrompt = new Property(13, String.class, "stationPrompt", false, "STATION_PROMPT");
        public static final Property DeparturePrompt = new Property(14, String.class, "departurePrompt", false, "DEPARTURE_PROMPT");
        public static final Property StationExpansion = new Property(15, String.class, "stationExpansion", false, "STATION_EXPANSION");
        public static final Property DepartureExpansion = new Property(16, String.class, "departureExpansion", false, "DEPARTURE_EXPANSION");
        public static final Property SpeedLimit = new Property(17, String.class, "speedLimit", false, "SPEED_LIMIT");
        public static final Property SpeedLimitInStation = new Property(18, String.class, "speedLimitInStation", false, "SPEED_LIMIT_IN_STATION");
        public static final Property Mileage = new Property(19, String.class, "mileage", false, "MILEAGE");
        public static final Property IMajorStation = new Property(20, String.class, "iMajorStation", false, "I_MAJOR_STATION");
        public static final Property VoiceNot = new Property(21, String.class, "voiceNot", false, "VOICE_NOT");
        public static final Property DirectionName = new Property(22, String.class, "directionName", false, "DIRECTION_NAME");
        public static final Property Speed = new Property(23, String.class, "speed", false, "SPEED");
        public static final Property BusNameE = new Property(24, String.class, "busNameE", false, "BUS_NAME_E");
        public static final Property BusPrice = new Property(25, String.class, "busPrice", false, "BUS_PRICE");
    }

    @Override // org.greenrobot.greendao.AbstractDao
    protected final boolean isEntityUpdateable() {
        return true;
    }

    public BusLineModelDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public BusLineModelDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(Database database, boolean z) {
        database.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "\"BUS_LINE_MODEL\" (\"_id\" INTEGER PRIMARY KEY ,\"BUS_NO\" INTEGER NOT NULL ,\"BUS_SOUND\" TEXT,\"BUS_FILE_PATH\" TEXT,\"BUS_NAME\" TEXT,\"BUS_LINE_NAME\" TEXT,\"DIRECTION\" INTEGER NOT NULL ,\"LONGITUDE\" TEXT,\"LATITUDE\" TEXT,\"ANGLE\" TEXT,\"SITE_CODE\" TEXT,\"STATION_ADVERT\" TEXT,\"DEPARTURE_ADVERT\" TEXT,\"STATION_PROMPT\" TEXT,\"DEPARTURE_PROMPT\" TEXT,\"STATION_EXPANSION\" TEXT,\"DEPARTURE_EXPANSION\" TEXT,\"SPEED_LIMIT\" TEXT,\"SPEED_LIMIT_IN_STATION\" TEXT,\"MILEAGE\" TEXT,\"I_MAJOR_STATION\" TEXT,\"VOICE_NOT\" TEXT,\"DIRECTION_NAME\" TEXT,\"SPEED\" TEXT,\"BUS_NAME_E\" TEXT,\"BUS_PRICE\" TEXT);");
    }

    public static void dropTable(Database database, boolean z) {
        database.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "\"BUS_LINE_MODEL\"");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(DatabaseStatement databaseStatement, BusLineModel busLineModel) {
        databaseStatement.clearBindings();
        Long l = busLineModel.get_id();
        if (l != null) {
            databaseStatement.bindLong(1, l.longValue());
        }
        databaseStatement.bindLong(2, busLineModel.getBusNo());
        String busSound = busLineModel.getBusSound();
        if (busSound != null) {
            databaseStatement.bindString(3, busSound);
        }
        String busFilePath = busLineModel.getBusFilePath();
        if (busFilePath != null) {
            databaseStatement.bindString(4, busFilePath);
        }
        String busName = busLineModel.getBusName();
        if (busName != null) {
            databaseStatement.bindString(5, busName);
        }
        String busLineName = busLineModel.getBusLineName();
        if (busLineName != null) {
            databaseStatement.bindString(6, busLineName);
        }
        databaseStatement.bindLong(7, busLineModel.getDirection());
        String longitude = busLineModel.getLongitude();
        if (longitude != null) {
            databaseStatement.bindString(8, longitude);
        }
        String latitude = busLineModel.getLatitude();
        if (latitude != null) {
            databaseStatement.bindString(9, latitude);
        }
        String angle = busLineModel.getAngle();
        if (angle != null) {
            databaseStatement.bindString(10, angle);
        }
        String siteCode = busLineModel.getSiteCode();
        if (siteCode != null) {
            databaseStatement.bindString(11, siteCode);
        }
        String stationAdvert = busLineModel.getStationAdvert();
        if (stationAdvert != null) {
            databaseStatement.bindString(12, stationAdvert);
        }
        String departureAdvert = busLineModel.getDepartureAdvert();
        if (departureAdvert != null) {
            databaseStatement.bindString(13, departureAdvert);
        }
        String stationPrompt = busLineModel.getStationPrompt();
        if (stationPrompt != null) {
            databaseStatement.bindString(14, stationPrompt);
        }
        String departurePrompt = busLineModel.getDeparturePrompt();
        if (departurePrompt != null) {
            databaseStatement.bindString(15, departurePrompt);
        }
        String stationExpansion = busLineModel.getStationExpansion();
        if (stationExpansion != null) {
            databaseStatement.bindString(16, stationExpansion);
        }
        String departureExpansion = busLineModel.getDepartureExpansion();
        if (departureExpansion != null) {
            databaseStatement.bindString(17, departureExpansion);
        }
        String speedLimit = busLineModel.getSpeedLimit();
        if (speedLimit != null) {
            databaseStatement.bindString(18, speedLimit);
        }
        String speedLimitInStation = busLineModel.getSpeedLimitInStation();
        if (speedLimitInStation != null) {
            databaseStatement.bindString(19, speedLimitInStation);
        }
        String mileage = busLineModel.getMileage();
        if (mileage != null) {
            databaseStatement.bindString(20, mileage);
        }
        String iMajorStation = busLineModel.getIMajorStation();
        if (iMajorStation != null) {
            databaseStatement.bindString(21, iMajorStation);
        }
        String voiceNot = busLineModel.getVoiceNot();
        if (voiceNot != null) {
            databaseStatement.bindString(22, voiceNot);
        }
        String directionName = busLineModel.getDirectionName();
        if (directionName != null) {
            databaseStatement.bindString(23, directionName);
        }
        String speed = busLineModel.getSpeed();
        if (speed != null) {
            databaseStatement.bindString(24, speed);
        }
        String busNameE = busLineModel.getBusNameE();
        if (busNameE != null) {
            databaseStatement.bindString(25, busNameE);
        }
        String busPrice = busLineModel.getBusPrice();
        if (busPrice != null) {
            databaseStatement.bindString(26, busPrice);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(SQLiteStatement sQLiteStatement, BusLineModel busLineModel) {
        sQLiteStatement.clearBindings();
        Long l = busLineModel.get_id();
        if (l != null) {
            sQLiteStatement.bindLong(1, l.longValue());
        }
        sQLiteStatement.bindLong(2, busLineModel.getBusNo());
        String busSound = busLineModel.getBusSound();
        if (busSound != null) {
            sQLiteStatement.bindString(3, busSound);
        }
        String busFilePath = busLineModel.getBusFilePath();
        if (busFilePath != null) {
            sQLiteStatement.bindString(4, busFilePath);
        }
        String busName = busLineModel.getBusName();
        if (busName != null) {
            sQLiteStatement.bindString(5, busName);
        }
        String busLineName = busLineModel.getBusLineName();
        if (busLineName != null) {
            sQLiteStatement.bindString(6, busLineName);
        }
        sQLiteStatement.bindLong(7, busLineModel.getDirection());
        String longitude = busLineModel.getLongitude();
        if (longitude != null) {
            sQLiteStatement.bindString(8, longitude);
        }
        String latitude = busLineModel.getLatitude();
        if (latitude != null) {
            sQLiteStatement.bindString(9, latitude);
        }
        String angle = busLineModel.getAngle();
        if (angle != null) {
            sQLiteStatement.bindString(10, angle);
        }
        String siteCode = busLineModel.getSiteCode();
        if (siteCode != null) {
            sQLiteStatement.bindString(11, siteCode);
        }
        String stationAdvert = busLineModel.getStationAdvert();
        if (stationAdvert != null) {
            sQLiteStatement.bindString(12, stationAdvert);
        }
        String departureAdvert = busLineModel.getDepartureAdvert();
        if (departureAdvert != null) {
            sQLiteStatement.bindString(13, departureAdvert);
        }
        String stationPrompt = busLineModel.getStationPrompt();
        if (stationPrompt != null) {
            sQLiteStatement.bindString(14, stationPrompt);
        }
        String departurePrompt = busLineModel.getDeparturePrompt();
        if (departurePrompt != null) {
            sQLiteStatement.bindString(15, departurePrompt);
        }
        String stationExpansion = busLineModel.getStationExpansion();
        if (stationExpansion != null) {
            sQLiteStatement.bindString(16, stationExpansion);
        }
        String departureExpansion = busLineModel.getDepartureExpansion();
        if (departureExpansion != null) {
            sQLiteStatement.bindString(17, departureExpansion);
        }
        String speedLimit = busLineModel.getSpeedLimit();
        if (speedLimit != null) {
            sQLiteStatement.bindString(18, speedLimit);
        }
        String speedLimitInStation = busLineModel.getSpeedLimitInStation();
        if (speedLimitInStation != null) {
            sQLiteStatement.bindString(19, speedLimitInStation);
        }
        String mileage = busLineModel.getMileage();
        if (mileage != null) {
            sQLiteStatement.bindString(20, mileage);
        }
        String iMajorStation = busLineModel.getIMajorStation();
        if (iMajorStation != null) {
            sQLiteStatement.bindString(21, iMajorStation);
        }
        String voiceNot = busLineModel.getVoiceNot();
        if (voiceNot != null) {
            sQLiteStatement.bindString(22, voiceNot);
        }
        String directionName = busLineModel.getDirectionName();
        if (directionName != null) {
            sQLiteStatement.bindString(23, directionName);
        }
        String speed = busLineModel.getSpeed();
        if (speed != null) {
            sQLiteStatement.bindString(24, speed);
        }
        String busNameE = busLineModel.getBusNameE();
        if (busNameE != null) {
            sQLiteStatement.bindString(25, busNameE);
        }
        String busPrice = busLineModel.getBusPrice();
        if (busPrice != null) {
            sQLiteStatement.bindString(26, busPrice);
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
    public BusLineModel readEntity(Cursor cursor, int i) {
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
        int i8 = cursor.getInt(i + 6);
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
        String string15 = cursor.isNull(i19) ? null : cursor.getString(i19);
        int i20 = i + 18;
        String string16 = cursor.isNull(i20) ? null : cursor.getString(i20);
        int i21 = i + 19;
        String string17 = cursor.isNull(i21) ? null : cursor.getString(i21);
        int i22 = i + 20;
        String string18 = cursor.isNull(i22) ? null : cursor.getString(i22);
        int i23 = i + 21;
        String string19 = cursor.isNull(i23) ? null : cursor.getString(i23);
        int i24 = i + 22;
        String string20 = cursor.isNull(i24) ? null : cursor.getString(i24);
        int i25 = i + 23;
        String string21 = cursor.isNull(i25) ? null : cursor.getString(i25);
        int i26 = i + 24;
        String string22 = cursor.isNull(i26) ? null : cursor.getString(i26);
        int i27 = i + 25;
        return new BusLineModel(lValueOf, i3, string, string2, string3, string4, i8, string5, string6, string7, string8, string9, string10, string11, string12, string13, string14, string15, string16, string17, string18, string19, string20, string21, string22, cursor.isNull(i27) ? null : cursor.getString(i27));
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public void readEntity(Cursor cursor, BusLineModel busLineModel, int i) {
        int i2 = i + 0;
        busLineModel.set_id(cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2)));
        busLineModel.setBusNo(cursor.getInt(i + 1));
        int i3 = i + 2;
        busLineModel.setBusSound(cursor.isNull(i3) ? null : cursor.getString(i3));
        int i4 = i + 3;
        busLineModel.setBusFilePath(cursor.isNull(i4) ? null : cursor.getString(i4));
        int i5 = i + 4;
        busLineModel.setBusName(cursor.isNull(i5) ? null : cursor.getString(i5));
        int i6 = i + 5;
        busLineModel.setBusLineName(cursor.isNull(i6) ? null : cursor.getString(i6));
        busLineModel.setDirection(cursor.getInt(i + 6));
        int i7 = i + 7;
        busLineModel.setLongitude(cursor.isNull(i7) ? null : cursor.getString(i7));
        int i8 = i + 8;
        busLineModel.setLatitude(cursor.isNull(i8) ? null : cursor.getString(i8));
        int i9 = i + 9;
        busLineModel.setAngle(cursor.isNull(i9) ? null : cursor.getString(i9));
        int i10 = i + 10;
        busLineModel.setSiteCode(cursor.isNull(i10) ? null : cursor.getString(i10));
        int i11 = i + 11;
        busLineModel.setStationAdvert(cursor.isNull(i11) ? null : cursor.getString(i11));
        int i12 = i + 12;
        busLineModel.setDepartureAdvert(cursor.isNull(i12) ? null : cursor.getString(i12));
        int i13 = i + 13;
        busLineModel.setStationPrompt(cursor.isNull(i13) ? null : cursor.getString(i13));
        int i14 = i + 14;
        busLineModel.setDeparturePrompt(cursor.isNull(i14) ? null : cursor.getString(i14));
        int i15 = i + 15;
        busLineModel.setStationExpansion(cursor.isNull(i15) ? null : cursor.getString(i15));
        int i16 = i + 16;
        busLineModel.setDepartureExpansion(cursor.isNull(i16) ? null : cursor.getString(i16));
        int i17 = i + 17;
        busLineModel.setSpeedLimit(cursor.isNull(i17) ? null : cursor.getString(i17));
        int i18 = i + 18;
        busLineModel.setSpeedLimitInStation(cursor.isNull(i18) ? null : cursor.getString(i18));
        int i19 = i + 19;
        busLineModel.setMileage(cursor.isNull(i19) ? null : cursor.getString(i19));
        int i20 = i + 20;
        busLineModel.setIMajorStation(cursor.isNull(i20) ? null : cursor.getString(i20));
        int i21 = i + 21;
        busLineModel.setVoiceNot(cursor.isNull(i21) ? null : cursor.getString(i21));
        int i22 = i + 22;
        busLineModel.setDirectionName(cursor.isNull(i22) ? null : cursor.getString(i22));
        int i23 = i + 23;
        busLineModel.setSpeed(cursor.isNull(i23) ? null : cursor.getString(i23));
        int i24 = i + 24;
        busLineModel.setBusNameE(cursor.isNull(i24) ? null : cursor.getString(i24));
        int i25 = i + 25;
        busLineModel.setBusPrice(cursor.isNull(i25) ? null : cursor.getString(i25));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final Long updateKeyAfterInsert(BusLineModel busLineModel, long j) {
        busLineModel.set_id(Long.valueOf(j));
        return Long.valueOf(j);
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public Long getKey(BusLineModel busLineModel) {
        if (busLineModel != null) {
            return busLineModel.get_id();
        }
        return null;
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public boolean hasKey(BusLineModel busLineModel) {
        return busLineModel.get_id() != null;
    }
}
