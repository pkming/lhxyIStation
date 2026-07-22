package com.lianhexinye.m90.common.utils.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.lianhexinye.m90.greendao.gen.BusLineFriendRemindModelDao;
import com.lianhexinye.m90.greendao.gen.BusLineInfoModelDao;
import com.lianhexinye.m90.greendao.gen.BusLineModelDao;
import com.lianhexinye.m90.greendao.gen.BusMediaModelDao;
import com.lianhexinye.m90.greendao.gen.ChannelModelDao;
import com.lianhexinye.m90.greendao.gen.ConfigInfoModelDao;
import com.lianhexinye.m90.greendao.gen.DaoMaster;
import com.lianhexinye.m90.greendao.gen.DownLoadInfoModelDao;
import com.lianhexinye.m90.greendao.gen.MessageModelDao;
import com.lianhexinye.m90.greendao.gen.PlayListModelDao;
import com.lianhexinye.m90.greendao.gen.ProgramModelDao;
import org.greenrobot.greendao.database.Database;

/* JADX INFO: loaded from: classes2.dex */
public class MyOpenHelper extends DaoMaster.OpenHelper {
    public MyOpenHelper(Context context, String str, SQLiteDatabase.CursorFactory cursorFactory) {
        super(context, str, cursorFactory);
    }

    @Override // org.greenrobot.greendao.database.DatabaseOpenHelper
    public void onUpgrade(Database database, int i, int i2) {
        super.onUpgrade(database, i, i2);
        if (i < i2) {
            MigrationHelper.getInstance().migrate(database, BusLineFriendRemindModelDao.class, BusLineInfoModelDao.class, BusLineModelDao.class, BusMediaModelDao.class, ChannelModelDao.class, ConfigInfoModelDao.class, DownLoadInfoModelDao.class, PlayListModelDao.class, ProgramModelDao.class, MessageModelDao.class);
        }
    }
}
