package org.greenrobot.greendao.database;

import android.content.Context;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
import org.greenrobot.greendao.database.DatabaseOpenHelper;

/* JADX INFO: loaded from: classes3.dex */
class SqlCipherEncryptedHelper extends SQLiteOpenHelper implements DatabaseOpenHelper.EncryptedHelper {
    private final DatabaseOpenHelper delegate;

    public SqlCipherEncryptedHelper(DatabaseOpenHelper databaseOpenHelper, Context context, String str, int i, boolean z) {
        super(context, str, (SQLiteDatabase.CursorFactory) null, i);
        this.delegate = databaseOpenHelper;
        if (z) {
            SQLiteDatabase.loadLibs(context);
        }
    }

    private Database wrap(SQLiteDatabase sQLiteDatabase) {
        return new EncryptedDatabase(sQLiteDatabase);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        this.delegate.onCreate(wrap(sQLiteDatabase));
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        this.delegate.onUpgrade(wrap(sQLiteDatabase), i, i2);
    }

    public void onOpen(SQLiteDatabase sQLiteDatabase) {
        this.delegate.onOpen(wrap(sQLiteDatabase));
    }

    @Override // org.greenrobot.greendao.database.DatabaseOpenHelper.EncryptedHelper
    public Database getEncryptedReadableDb(String str) {
        return wrap(getReadableDatabase(str));
    }

    @Override // org.greenrobot.greendao.database.DatabaseOpenHelper.EncryptedHelper
    public Database getEncryptedReadableDb(char[] cArr) {
        return wrap(getReadableDatabase(cArr));
    }

    @Override // org.greenrobot.greendao.database.DatabaseOpenHelper.EncryptedHelper
    public Database getEncryptedWritableDb(String str) {
        return wrap(getWritableDatabase(str));
    }

    @Override // org.greenrobot.greendao.database.DatabaseOpenHelper.EncryptedHelper
    public Database getEncryptedWritableDb(char[] cArr) {
        return wrap(getWritableDatabase(cArr));
    }
}
