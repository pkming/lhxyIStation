package com.lianhexinye.m90.greendao.gen;

import java.util.Map;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

/* JADX INFO: loaded from: classes2.dex */
public class DaoSession extends AbstractDaoSession {
    private final BusLineFriendRemindModelDao busLineFriendRemindModelDao;
    private final DaoConfig busLineFriendRemindModelDaoConfig;
    private final BusLineInfoModelDao busLineInfoModelDao;
    private final DaoConfig busLineInfoModelDaoConfig;
    private final BusLineModelDao busLineModelDao;
    private final DaoConfig busLineModelDaoConfig;
    private final BusMediaModelDao busMediaModelDao;
    private final DaoConfig busMediaModelDaoConfig;
    private final ChannelModelDao channelModelDao;
    private final DaoConfig channelModelDaoConfig;
    private final ConfigInfoModelDao configInfoModelDao;
    private final DaoConfig configInfoModelDaoConfig;
    private final DownLoadFTPModelDao downLoadFTPModelDao;
    private final DaoConfig downLoadFTPModelDaoConfig;
    private final DownLoadInfoModelDao downLoadInfoModelDao;
    private final DaoConfig downLoadInfoModelDaoConfig;
    private final MaintenanceModelDao maintenanceModelDao;
    private final DaoConfig maintenanceModelDaoConfig;
    private final MessageModelDao messageModelDao;
    private final DaoConfig messageModelDaoConfig;
    private final PlayListModelDao playListModelDao;
    private final DaoConfig playListModelDaoConfig;
    private final ProgramModelDao programModelDao;
    private final DaoConfig programModelDaoConfig;

    public DaoSession(Database database, IdentityScopeType identityScopeType, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> map) {
        super(database);
        DaoConfig daoConfigClone = map.get(BusLineFriendRemindModelDao.class).clone();
        this.busLineFriendRemindModelDaoConfig = daoConfigClone;
        daoConfigClone.initIdentityScope(identityScopeType);
        DaoConfig daoConfigClone2 = map.get(BusLineInfoModelDao.class).clone();
        this.busLineInfoModelDaoConfig = daoConfigClone2;
        daoConfigClone2.initIdentityScope(identityScopeType);
        DaoConfig daoConfigClone3 = map.get(BusLineModelDao.class).clone();
        this.busLineModelDaoConfig = daoConfigClone3;
        daoConfigClone3.initIdentityScope(identityScopeType);
        DaoConfig daoConfigClone4 = map.get(BusMediaModelDao.class).clone();
        this.busMediaModelDaoConfig = daoConfigClone4;
        daoConfigClone4.initIdentityScope(identityScopeType);
        DaoConfig daoConfigClone5 = map.get(ChannelModelDao.class).clone();
        this.channelModelDaoConfig = daoConfigClone5;
        daoConfigClone5.initIdentityScope(identityScopeType);
        DaoConfig daoConfigClone6 = map.get(ConfigInfoModelDao.class).clone();
        this.configInfoModelDaoConfig = daoConfigClone6;
        daoConfigClone6.initIdentityScope(identityScopeType);
        DaoConfig daoConfigClone7 = map.get(DownLoadFTPModelDao.class).clone();
        this.downLoadFTPModelDaoConfig = daoConfigClone7;
        daoConfigClone7.initIdentityScope(identityScopeType);
        DaoConfig daoConfigClone8 = map.get(DownLoadInfoModelDao.class).clone();
        this.downLoadInfoModelDaoConfig = daoConfigClone8;
        daoConfigClone8.initIdentityScope(identityScopeType);
        DaoConfig daoConfigClone9 = map.get(MaintenanceModelDao.class).clone();
        this.maintenanceModelDaoConfig = daoConfigClone9;
        daoConfigClone9.initIdentityScope(identityScopeType);
        DaoConfig daoConfigClone10 = map.get(MessageModelDao.class).clone();
        this.messageModelDaoConfig = daoConfigClone10;
        daoConfigClone10.initIdentityScope(identityScopeType);
        DaoConfig daoConfigClone11 = map.get(PlayListModelDao.class).clone();
        this.playListModelDaoConfig = daoConfigClone11;
        daoConfigClone11.initIdentityScope(identityScopeType);
        DaoConfig daoConfigClone12 = map.get(ProgramModelDao.class).clone();
        this.programModelDaoConfig = daoConfigClone12;
        daoConfigClone12.initIdentityScope(identityScopeType);
        BusLineFriendRemindModelDao busLineFriendRemindModelDao = new BusLineFriendRemindModelDao(daoConfigClone, this);
        this.busLineFriendRemindModelDao = busLineFriendRemindModelDao;
        BusLineInfoModelDao busLineInfoModelDao = new BusLineInfoModelDao(daoConfigClone2, this);
        this.busLineInfoModelDao = busLineInfoModelDao;
        BusLineModelDao busLineModelDao = new BusLineModelDao(daoConfigClone3, this);
        this.busLineModelDao = busLineModelDao;
        BusMediaModelDao busMediaModelDao = new BusMediaModelDao(daoConfigClone4, this);
        this.busMediaModelDao = busMediaModelDao;
        ChannelModelDao channelModelDao = new ChannelModelDao(daoConfigClone5, this);
        this.channelModelDao = channelModelDao;
        ConfigInfoModelDao configInfoModelDao = new ConfigInfoModelDao(daoConfigClone6, this);
        this.configInfoModelDao = configInfoModelDao;
        DownLoadFTPModelDao downLoadFTPModelDao = new DownLoadFTPModelDao(daoConfigClone7, this);
        this.downLoadFTPModelDao = downLoadFTPModelDao;
        DownLoadInfoModelDao downLoadInfoModelDao = new DownLoadInfoModelDao(daoConfigClone8, this);
        this.downLoadInfoModelDao = downLoadInfoModelDao;
        MaintenanceModelDao maintenanceModelDao = new MaintenanceModelDao(daoConfigClone9, this);
        this.maintenanceModelDao = maintenanceModelDao;
        MessageModelDao messageModelDao = new MessageModelDao(daoConfigClone10, this);
        this.messageModelDao = messageModelDao;
        PlayListModelDao playListModelDao = new PlayListModelDao(daoConfigClone11, this);
        this.playListModelDao = playListModelDao;
        ProgramModelDao programModelDao = new ProgramModelDao(daoConfigClone12, this);
        this.programModelDao = programModelDao;
        registerDao(BusLineFriendRemindModel.class, busLineFriendRemindModelDao);
        registerDao(BusLineInfoModel.class, busLineInfoModelDao);
        registerDao(BusLineModel.class, busLineModelDao);
        registerDao(BusMediaModel.class, busMediaModelDao);
        registerDao(ChannelModel.class, channelModelDao);
        registerDao(ConfigInfoModel.class, configInfoModelDao);
        registerDao(DownLoadFTPModel.class, downLoadFTPModelDao);
        registerDao(DownLoadInfoModel.class, downLoadInfoModelDao);
        registerDao(MaintenanceModel.class, maintenanceModelDao);
        registerDao(MessageModel.class, messageModelDao);
        registerDao(PlayListModel.class, playListModelDao);
        registerDao(ProgramModel.class, programModelDao);
    }

    public void clear() {
        this.busLineFriendRemindModelDaoConfig.clearIdentityScope();
        this.busLineInfoModelDaoConfig.clearIdentityScope();
        this.busLineModelDaoConfig.clearIdentityScope();
        this.busMediaModelDaoConfig.clearIdentityScope();
        this.channelModelDaoConfig.clearIdentityScope();
        this.configInfoModelDaoConfig.clearIdentityScope();
        this.downLoadFTPModelDaoConfig.clearIdentityScope();
        this.downLoadInfoModelDaoConfig.clearIdentityScope();
        this.maintenanceModelDaoConfig.clearIdentityScope();
        this.messageModelDaoConfig.clearIdentityScope();
        this.playListModelDaoConfig.clearIdentityScope();
        this.programModelDaoConfig.clearIdentityScope();
    }

    public BusLineFriendRemindModelDao getBusLineFriendRemindModelDao() {
        return this.busLineFriendRemindModelDao;
    }

    public BusLineInfoModelDao getBusLineInfoModelDao() {
        return this.busLineInfoModelDao;
    }

    public BusLineModelDao getBusLineModelDao() {
        return this.busLineModelDao;
    }

    public BusMediaModelDao getBusMediaModelDao() {
        return this.busMediaModelDao;
    }

    public ChannelModelDao getChannelModelDao() {
        return this.channelModelDao;
    }

    public ConfigInfoModelDao getConfigInfoModelDao() {
        return this.configInfoModelDao;
    }

    public DownLoadFTPModelDao getDownLoadFTPModelDao() {
        return this.downLoadFTPModelDao;
    }

    public DownLoadInfoModelDao getDownLoadInfoModelDao() {
        return this.downLoadInfoModelDao;
    }

    public MaintenanceModelDao getMaintenanceModelDao() {
        return this.maintenanceModelDao;
    }

    public MessageModelDao getMessageModelDao() {
        return this.messageModelDao;
    }

    public PlayListModelDao getPlayListModelDao() {
        return this.playListModelDao;
    }

    public ProgramModelDao getProgramModelDao() {
        return this.programModelDao;
    }
}
