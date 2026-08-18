package com.lianhexinye.m90.common.utils.greendao;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import com.lianhexinye.m90.greendao.gen.DaoMaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.internal.DaoConfig;

/* JADX INFO: loaded from: classes2.dex */
public class MigrationHelper {
    private static final String CONVERSION_CLASS_NOT_FOUND_EXCEPTION = "MIGRATION HELPER - CLASS DOESN'T MATCH WITH THE CURRENT PARAMETERS";
    private static MigrationHelper instance;

    public static MigrationHelper getInstance() {
        if (instance == null) {
            instance = new MigrationHelper();
        }
        return instance;
    }

    public void migrate(Database database, Class<? extends AbstractDao<?, ?>>... clsArr) {
        generateTempTables(database, clsArr);
        DaoMaster.dropAllTables(database, true);
        DaoMaster.createAllTables(database, false);
        restoreData(database, clsArr);
        Log.i("greenDAO", "MigrationHelper---migrate");
    }

    private void generateTempTables(Database database, Class<? extends AbstractDao<?, ?>>... clsArr) {
        for (Class<? extends AbstractDao<?, ?>> cls : clsArr) {
            DaoConfig daoConfig = new DaoConfig(database, cls);
            String str = daoConfig.tablename;
            String strConcat = daoConfig.tablename.concat("_TEMP");
            ArrayList arrayList = new ArrayList();
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE ").append(strConcat).append(" (");
            String str2 = "";
            for (int i = 0; i < daoConfig.properties.length; i++) {
                String str3 = daoConfig.properties[i].columnName;
                if (getColumns(database, str).contains(str3)) {
                    arrayList.add(str3);
                    String typeByClass = null;
                    try {
                        try {
                            typeByClass = getTypeByClass(daoConfig.properties[i].type);
                        } catch (Exception unused) {
                        }
                    } catch (Exception unused2) {
                    }
                    sb.append(str2).append(str3).append(" ").append(typeByClass);
                    if (daoConfig.properties[i].primaryKey) {
                        sb.append(" PRIMARY KEY");
                    }
                    str2 = ",";
                }
            }
            sb.append(");");
            database.execSQL(sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append("INSERT INTO ").append(strConcat).append(" (");
            sb2.append(TextUtils.join(",", arrayList));
            sb2.append(") SELECT ");
            sb2.append(TextUtils.join(",", arrayList));
            sb2.append(" FROM ").append(str).append(";");
            database.execSQL(sb2.toString());
        }
    }

    private void restoreData(Database database, Class<? extends AbstractDao<?, ?>>... clsArr) {
        for (Class<? extends AbstractDao<?, ?>> cls : clsArr) {
            DaoConfig daoConfig = new DaoConfig(database, cls);
            String str = daoConfig.tablename;
            String strConcat = daoConfig.tablename.concat("_TEMP");
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < daoConfig.properties.length; i++) {
                String str2 = daoConfig.properties[i].columnName;
                if (getColumns(database, strConcat).contains(str2)) {
                    arrayList.add(str2);
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO ").append(str).append(" (");
            sb.append(TextUtils.join(",", arrayList));
            sb.append(") SELECT ");
            sb.append(TextUtils.join(",", arrayList));
            sb.append(" FROM ").append(strConcat).append(";");
            StringBuilder sb2 = new StringBuilder();
            sb2.append("DROP TABLE ").append(strConcat);
            database.execSQL(sb.toString());
            database.execSQL(sb2.toString());
        }
    }

    private String getTypeByClass(Class<?> cls) throws Exception {
        if (cls.equals(String.class)) {
            return "TEXT";
        }
        if (cls.equals(Long.class) || cls.equals(Integer.class) || cls.equals(Long.TYPE)) {
            return "INTEGER";
        }
        if (cls.equals(Boolean.class)) {
            return "BOOLEAN";
        }
        throw new Exception(CONVERSION_CLASS_NOT_FOUND_EXCEPTION.concat(" - Class: ").concat(cls.toString()));
    }

    private static List<String> getColumns(Database database, String str) {
        ArrayList arrayList = new ArrayList();
        Cursor cursorRawQuery = null;
        try {
            try {
                cursorRawQuery = database.rawQuery("SELECT * FROM " + str + " limit 1", null);
                if (cursorRawQuery != null) {
                    arrayList = new ArrayList(Arrays.asList(cursorRawQuery.getColumnNames()));
                }
            } catch (Exception e) {
                Log.v(str, e.getMessage(), e);
                e.printStackTrace();
                if (cursorRawQuery != null) {
                }
            }
            return arrayList;
        } finally {
            if (cursorRawQuery != null) {
                cursorRawQuery.close();
            }
        }
    }
}
