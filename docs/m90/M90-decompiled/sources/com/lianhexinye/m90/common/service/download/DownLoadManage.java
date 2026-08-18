package com.lianhexinye.m90.common.service.download;

import com.lianhexinye.m90.common.utils.greendao.GreenDaoUtils;
import com.lianhexinye.m90.greendao.gen.DownLoadInfoModel;
import com.lianhexinye.m90.greendao.gen.DownLoadInfoModelDao;
import java.util.Iterator;
import java.util.List;
import org.greenrobot.greendao.query.WhereCondition;

/* JADX INFO: loaded from: classes2.dex */
public class DownLoadManage {
    private static DownLoadManage downLoadManage;
    private DownLoadInfoModelDao downLoadInfoModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getDownLoadInfoModelDao();

    private DownLoadManage() {
    }

    public static DownLoadManage getInstance() {
        if (downLoadManage == null) {
            downLoadManage = new DownLoadManage();
        }
        return downLoadManage;
    }

    public List<DownLoadInfoModel> getInfos(String str) {
        return this.downLoadInfoModelDao.queryBuilder().where(DownLoadInfoModelDao.Properties.Url.eq(str), new WhereCondition[0]).orderAsc(DownLoadInfoModelDao.Properties.Thread_id).list();
    }

    public void saveInfos(List<DownLoadInfoModel> list) {
        Iterator<DownLoadInfoModel> it = list.iterator();
        while (it.hasNext()) {
            this.downLoadInfoModelDao.insert(it.next());
        }
    }

    public void saveInfo(DownLoadInfoModel downLoadInfoModel) {
        this.downLoadInfoModelDao.insert(downLoadInfoModel);
    }

    public void updataInfos(int i, String str, Long l) {
        DownLoadInfoModel downLoadInfoModelUnique = this.downLoadInfoModelDao.queryBuilder().where(DownLoadInfoModelDao.Properties.Thread_id.eq(Integer.valueOf(i)), DownLoadInfoModelDao.Properties.Url.eq(str)).orderAsc(DownLoadInfoModelDao.Properties.Thread_id).limit(1).unique();
        downLoadInfoModelUnique.setCompelete_size(l);
        this.downLoadInfoModelDao.update(downLoadInfoModelUnique);
    }

    public void updataInfos(List<DownLoadInfoModel> list) {
        Iterator<DownLoadInfoModel> it = list.iterator();
        while (it.hasNext()) {
            this.downLoadInfoModelDao.update(it.next());
        }
    }

    public void updataInfos(DownLoadInfoModel downLoadInfoModel) {
        this.downLoadInfoModelDao.update(downLoadInfoModel);
    }

    public void deleInfos(DownLoadInfoModel downLoadInfoModel) {
        this.downLoadInfoModelDao.delete(downLoadInfoModel);
    }
}
