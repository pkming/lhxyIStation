package com.amap.api.col.p0003sl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: compiled from: DBOperation.java */
/* JADX INFO: loaded from: classes2.dex */
public final class kf {
    private static Map<Class<? extends ke>, ke> d = new HashMap();
    private ki a;
    private SQLiteDatabase b;
    private ke c;

    public static synchronized ke a(Class<? extends ke> cls) throws IllegalAccessException, InstantiationException {
        if (d.get(cls) == null) {
            d.put(cls, cls.newInstance());
        }
        return d.get(cls);
    }

    public kf(Context context, ke keVar) {
        try {
            this.a = new ki(context.getApplicationContext(), keVar.b(), keVar.c(), keVar);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        this.c = keVar;
    }

    public final <T> void a(String str, Class<T> cls) {
        SQLiteDatabase sQLiteDatabase;
        synchronized (this.c) {
            String strA = a(b((Class) cls));
            if (TextUtils.isEmpty(strA)) {
                return;
            }
            SQLiteDatabase sQLiteDatabaseB = b();
            this.b = sQLiteDatabaseB;
            if (sQLiteDatabaseB == null) {
                return;
            }
            try {
                sQLiteDatabaseB.delete(strA, str, null);
                sQLiteDatabase = this.b;
            } catch (Throwable th) {
                try {
                    jt.a(th, "dbs", "dld");
                    SQLiteDatabase sQLiteDatabase2 = this.b;
                    if (sQLiteDatabase2 != null) {
                        sQLiteDatabase2.close();
                    }
                } catch (Throwable th2) {
                    SQLiteDatabase sQLiteDatabase3 = this.b;
                    if (sQLiteDatabase3 != null) {
                        sQLiteDatabase3.close();
                        this.b = null;
                    }
                    throw th2;
                }
            }
            if (sQLiteDatabase != null) {
                sQLiteDatabase.close();
                this.b = null;
            }
        }
    }

    private <T> void a(String str, Object obj) {
        SQLiteDatabase sQLiteDatabase;
        synchronized (this.c) {
            if (obj == null) {
                return;
            }
            kg kgVarB = b((Class) obj.getClass());
            String strA = a(kgVarB);
            if (TextUtils.isEmpty(strA)) {
                return;
            }
            ContentValues contentValuesA = a(obj, kgVarB);
            SQLiteDatabase sQLiteDatabaseB = b();
            this.b = sQLiteDatabaseB;
            if (sQLiteDatabaseB == null) {
                return;
            }
            try {
                sQLiteDatabaseB.update(strA, contentValuesA, str, null);
                sQLiteDatabase = this.b;
            } catch (Throwable th) {
                try {
                    jt.a(th, "dbs", "udd");
                    SQLiteDatabase sQLiteDatabase2 = this.b;
                    if (sQLiteDatabase2 != null) {
                        sQLiteDatabase2.close();
                    }
                } catch (Throwable th2) {
                    SQLiteDatabase sQLiteDatabase3 = this.b;
                    if (sQLiteDatabase3 != null) {
                        sQLiteDatabase3.close();
                        this.b = null;
                    }
                    throw th2;
                }
            }
            if (sQLiteDatabase != null) {
                sQLiteDatabase.close();
                this.b = null;
            }
        }
    }

    private <T> void b(String str, Object obj) {
        a(str, obj);
    }

    public final void a(Object obj, String str) {
        synchronized (this.c) {
            List listB = b(str, (Class) obj.getClass());
            if (listB == null || listB.size() == 0) {
                a(obj);
            } else {
                b(str, obj);
            }
        }
    }

    private <T> void a(T t) {
        b(t);
    }

    private <T> void b(T t) {
        SQLiteDatabase sQLiteDatabase;
        synchronized (this.c) {
            SQLiteDatabase sQLiteDatabaseB = b();
            this.b = sQLiteDatabaseB;
            if (sQLiteDatabaseB == null) {
                return;
            }
            try {
                a(sQLiteDatabaseB, t);
                sQLiteDatabase = this.b;
            } catch (Throwable th) {
                try {
                    jt.a(th, "dbs", "itd");
                    SQLiteDatabase sQLiteDatabase2 = this.b;
                    if (sQLiteDatabase2 != null) {
                        sQLiteDatabase2.close();
                    }
                } catch (Throwable th2) {
                    SQLiteDatabase sQLiteDatabase3 = this.b;
                    if (sQLiteDatabase3 != null) {
                        sQLiteDatabase3.close();
                        this.b = null;
                    }
                    throw th2;
                }
            }
            if (sQLiteDatabase != null) {
                sQLiteDatabase.close();
                this.b = null;
            }
        }
    }

    private static <T> void a(SQLiteDatabase sQLiteDatabase, T t) {
        kg kgVarB = b((Class) t.getClass());
        String strA = a(kgVarB);
        if (TextUtils.isEmpty(strA) || t == null || sQLiteDatabase == null) {
            return;
        }
        sQLiteDatabase.insert(strA, null, a(t, kgVarB));
    }

    public final <T> void a(List<T> list) {
        String str;
        String str2;
        synchronized (this.c) {
            if (list.size() == 0) {
                return;
            }
            SQLiteDatabase sQLiteDatabaseB = b();
            this.b = sQLiteDatabaseB;
            if (sQLiteDatabaseB == null) {
                return;
            }
            try {
                sQLiteDatabaseB.beginTransaction();
                Iterator<T> it = list.iterator();
                while (it.hasNext()) {
                    a(this.b, it.next());
                }
                this.b.setTransactionSuccessful();
                try {
                    this.b.close();
                    this.b = null;
                } catch (Throwable th) {
                    th = th;
                    str = "dbs";
                    str2 = "ild";
                    jt.a(th, str, str2);
                }
            } catch (Throwable th2) {
                try {
                    jt.a(th2, "dbs", "ild");
                    try {
                        if (this.b.inTransaction()) {
                            this.b.endTransaction();
                        }
                    } catch (Throwable th3) {
                        jt.a(th3, "dbs", "ild");
                    }
                    try {
                        this.b.close();
                        this.b = null;
                    } catch (Throwable th4) {
                        th = th4;
                        str = "dbs";
                        str2 = "ild";
                        jt.a(th, str, str2);
                    }
                } finally {
                    try {
                    } catch (Throwable th5) {
                        jt.a(th5, "dbs", "ild");
                    }
                    if (this.b.inTransaction()) {
                        this.b.endTransaction();
                        try {
                            this.b.close();
                            this.b = null;
                            throw th;
                        } catch (Throwable th6) {
                            jt.a(th6, "dbs", "ild");
                        }
                    }
                    this.b.close();
                    this.b = null;
                    throw th;
                }
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:106:? A[Catch: all -> 0x00de, SYNTHETIC, TryCatch #10 {, blocks: (B:4:0x0003, B:6:0x0014, B:7:0x001a, B:9:0x001e, B:28:0x005e, B:27:0x0057, B:21:0x0045, B:63:0x00b8, B:45:0x008b, B:38:0x0075, B:56:0x00a1, B:77:0x00db, B:76:0x00d4, B:70:0x00c2, B:78:0x00dc, B:53:0x009c, B:67:0x00bd, B:71:0x00c9, B:73:0x00cd, B:35:0x0070, B:18:0x0040, B:22:0x004c, B:24:0x0050, B:51:0x0093), top: B:103:0x0003, inners: #0, #1, #4, #5, #6, #8, #9 }] */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0050 A[Catch: all -> 0x0056, TRY_LEAVE, TryCatch #8 {all -> 0x0056, blocks: (B:22:0x004c, B:24:0x0050), top: B:99:0x004c, outer: #10 }] */
    /* JADX WARN: Removed duplicated region for block: B:73:0x00cd A[Catch: all -> 0x00d3, TRY_LEAVE, TryCatch #4 {all -> 0x00d3, blocks: (B:71:0x00c9, B:73:0x00cd), top: B:91:0x00c9, outer: #10 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private <T> java.util.List<T> c(java.lang.String r13, java.lang.Class<T> r14) {
        /*
            Method dump skipped, instruction units count: 225
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.kf.c(java.lang.String, java.lang.Class):java.util.List");
    }

    public final <T> List<T> b(String str, Class<T> cls) {
        return c(str, cls);
    }

    private static <T> T a(Cursor cursor, Class<T> cls, kg kgVar) throws IllegalAccessException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        Field[] fieldArrA = a((Class<?>) cls, kgVar.b());
        Constructor<T> declaredConstructor = cls.getDeclaredConstructor(new Class[0]);
        declaredConstructor.setAccessible(true);
        T tNewInstance = declaredConstructor.newInstance(new Object[0]);
        for (Field field : fieldArrA) {
            field.setAccessible(true);
            Annotation annotation = field.getAnnotation(kh.class);
            if (annotation != null) {
                kh khVar = (kh) annotation;
                int iB = khVar.b();
                int columnIndex = cursor.getColumnIndex(khVar.a());
                switch (iB) {
                    case 1:
                        field.set(tNewInstance, Short.valueOf(cursor.getShort(columnIndex)));
                        break;
                    case 2:
                        field.set(tNewInstance, Integer.valueOf(cursor.getInt(columnIndex)));
                        break;
                    case 3:
                        field.set(tNewInstance, Float.valueOf(cursor.getFloat(columnIndex)));
                        break;
                    case 4:
                        field.set(tNewInstance, Double.valueOf(cursor.getDouble(columnIndex)));
                        break;
                    case 5:
                        field.set(tNewInstance, Long.valueOf(cursor.getLong(columnIndex)));
                        break;
                    case 6:
                        field.set(tNewInstance, cursor.getString(columnIndex));
                        break;
                    case 7:
                        field.set(tNewInstance, cursor.getBlob(columnIndex));
                        break;
                }
            }
        }
        return tNewInstance;
    }

    private static void a(Object obj, Field field, ContentValues contentValues) {
        Annotation annotation = field.getAnnotation(kh.class);
        if (annotation == null) {
        }
        kh khVar = (kh) annotation;
        try {
            switch (khVar.b()) {
                case 1:
                    contentValues.put(khVar.a(), Short.valueOf(field.getShort(obj)));
                    break;
                case 2:
                    contentValues.put(khVar.a(), Integer.valueOf(field.getInt(obj)));
                    break;
                case 3:
                    contentValues.put(khVar.a(), Float.valueOf(field.getFloat(obj)));
                    break;
                case 4:
                    contentValues.put(khVar.a(), Double.valueOf(field.getDouble(obj)));
                    break;
                case 5:
                    contentValues.put(khVar.a(), Long.valueOf(field.getLong(obj)));
                    break;
                case 6:
                    contentValues.put(khVar.a(), (String) field.get(obj));
                    break;
                case 7:
                    contentValues.put(khVar.a(), (byte[]) field.get(obj));
                    break;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static ContentValues a(Object obj, kg kgVar) {
        ContentValues contentValues = new ContentValues();
        for (Field field : a(obj.getClass(), kgVar.b())) {
            field.setAccessible(true);
            a(obj, field, contentValues);
        }
        return contentValues;
    }

    private static Field[] a(Class<?> cls, boolean z) {
        if (cls == null) {
            return null;
        }
        if (z) {
            return cls.getSuperclass().getDeclaredFields();
        }
        return cls.getDeclaredFields();
    }

    private SQLiteDatabase a() {
        try {
            if (this.b == null) {
                this.b = this.a.getReadableDatabase();
            }
        } catch (Throwable th) {
            jt.a(th, "dbs", "grd");
        }
        return this.b;
    }

    private SQLiteDatabase b() {
        try {
            SQLiteDatabase sQLiteDatabase = this.b;
            if (sQLiteDatabase == null || sQLiteDatabase.isReadOnly()) {
                SQLiteDatabase sQLiteDatabase2 = this.b;
                if (sQLiteDatabase2 != null) {
                    sQLiteDatabase2.close();
                }
                this.b = this.a.getWritableDatabase();
            }
        } catch (Throwable th) {
            jt.a(th, "dbs", "gwd");
        }
        return this.b;
    }

    private static <T> String a(kg kgVar) {
        if (kgVar == null) {
            return null;
        }
        return kgVar.a();
    }

    private static <T> kg b(Class<T> cls) {
        Annotation annotation = cls.getAnnotation(kg.class);
        if (annotation != null) {
            return (kg) annotation;
        }
        return null;
    }
}
