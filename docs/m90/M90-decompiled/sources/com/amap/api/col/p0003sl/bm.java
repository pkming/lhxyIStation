package com.amap.api.col.p0003sl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/* JADX INFO: compiled from: OfflineDBCreator.java */
/* JADX INFO: loaded from: classes2.dex */
public class bm implements ke {
    private static volatile bm a;

    @Override // com.amap.api.col.p0003sl.ke
    public final String b() {
        return "offlineDbV4.db";
    }

    @Override // com.amap.api.col.p0003sl.ke
    public final int c() {
        return 2;
    }

    public static bm a() {
        if (a == null) {
            synchronized (bm.class) {
                if (a == null) {
                    a = new bm();
                }
            }
        }
        return a;
    }

    private bm() {
    }

    @Override // com.amap.api.col.p0003sl.ke
    public final void a(SQLiteDatabase sQLiteDatabase) {
        if (sQLiteDatabase == null) {
            return;
        }
        try {
            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS update_item (_id integer primary key autoincrement, title  TEXT, url TEXT,mAdcode TEXT,fileName TEXT,version TEXT,lLocalLength INTEGER,lRemoteLength INTEGER,localPath TEXT,mIndex INTEGER,isProvince INTEGER NOT NULL,mCompleteCode INTEGER,mCityCode TEXT,mState INTEGER,mPinyin TEXT, UNIQUE(mAdcode));");
            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS update_item_file (_id integer primary key autoincrement,mAdcode TTEXT, file TEXT);");
            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS update_item_download_info (_id integer primary key autoincrement,mAdcode TEXT,fileLength integer,splitter integer,startPos integer,endPos integer, UNIQUE(mAdcode));");
        } catch (Throwable th) {
            jw.c(th, "DB", "onCreate");
            th.printStackTrace();
        }
    }

    @Override // com.amap.api.col.p0003sl.ke
    public final void a(SQLiteDatabase sQLiteDatabase, int i) {
        if (sQLiteDatabase != null && i == 1) {
            sQLiteDatabase.execSQL("ALTER TABLE update_item ADD COLUMN mPinyin TEXT;");
            Cursor cursorQuery = sQLiteDatabase.query("update_item", null, null, null, null, null, null);
            if (cursorQuery == null) {
                sQLiteDatabase.close();
                sQLiteDatabase = null;
            }
            if (cursorQuery != null) {
                while (cursorQuery.moveToNext()) {
                    String string = cursorQuery.getString(cursorQuery.getColumnIndex("url"));
                    String strSubstring = string.substring(string.lastIndexOf("/") + 1);
                    sQLiteDatabase.execSQL("update update_item set mPinyin=? where url =?", new String[]{strSubstring.substring(0, strSubstring.lastIndexOf(".")), string});
                }
                cursorQuery.close();
            }
        }
    }
}
