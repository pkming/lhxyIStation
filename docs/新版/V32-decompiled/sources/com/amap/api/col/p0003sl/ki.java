package com.amap.api.col.p0003sl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* JADX INFO: compiled from: DB.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ki extends SQLiteOpenHelper {
    private static boolean b = true;
    private static boolean c = false;
    private ke a;

    public ki(Context context, String str, int i, ke keVar) {
        super(context, str, null, i);
        this.a = keVar;
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public final void onCreate(SQLiteDatabase sQLiteDatabase) {
        this.a.a(sQLiteDatabase);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public final void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        this.a.a(sQLiteDatabase, i);
    }
}
