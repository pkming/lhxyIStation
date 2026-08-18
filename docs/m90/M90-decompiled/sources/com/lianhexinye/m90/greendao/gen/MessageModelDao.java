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
public class MessageModelDao extends AbstractDao<MessageModel, Long> {
    public static final String TABLENAME = "MESSAGE_MODEL";

    public static class Properties {
        public static final Property _id = new Property(0, Long.class, "_id", true, "_id");
        public static final Property MessageNo = new Property(1, Integer.TYPE, SPUserInfoUtils.MESSAGENO, false, "MESSAGE_NO");
        public static final Property MessageTime = new Property(2, String.class, "messageTime", false, "MESSAGE_TIME");
        public static final Property MessageContent = new Property(3, String.class, "messageContent", false, "MESSAGE_CONTENT");
        public static final Property FilePath = new Property(4, String.class, "filePath", false, "FILE_PATH");
        public static final Property FileFormat = new Property(5, String.class, "fileFormat", false, "FILE_FORMAT");
    }

    @Override // org.greenrobot.greendao.AbstractDao
    protected final boolean isEntityUpdateable() {
        return true;
    }

    public MessageModelDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public MessageModelDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(Database database, boolean z) {
        database.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "\"MESSAGE_MODEL\" (\"_id\" INTEGER PRIMARY KEY ,\"MESSAGE_NO\" INTEGER NOT NULL ,\"MESSAGE_TIME\" TEXT,\"MESSAGE_CONTENT\" TEXT,\"FILE_PATH\" TEXT,\"FILE_FORMAT\" TEXT);");
    }

    public static void dropTable(Database database, boolean z) {
        database.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "\"MESSAGE_MODEL\"");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(DatabaseStatement databaseStatement, MessageModel messageModel) {
        databaseStatement.clearBindings();
        Long l = messageModel.get_id();
        if (l != null) {
            databaseStatement.bindLong(1, l.longValue());
        }
        databaseStatement.bindLong(2, messageModel.getMessageNo());
        String messageTime = messageModel.getMessageTime();
        if (messageTime != null) {
            databaseStatement.bindString(3, messageTime);
        }
        String messageContent = messageModel.getMessageContent();
        if (messageContent != null) {
            databaseStatement.bindString(4, messageContent);
        }
        String filePath = messageModel.getFilePath();
        if (filePath != null) {
            databaseStatement.bindString(5, filePath);
        }
        String fileFormat = messageModel.getFileFormat();
        if (fileFormat != null) {
            databaseStatement.bindString(6, fileFormat);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final void bindValues(SQLiteStatement sQLiteStatement, MessageModel messageModel) {
        sQLiteStatement.clearBindings();
        Long l = messageModel.get_id();
        if (l != null) {
            sQLiteStatement.bindLong(1, l.longValue());
        }
        sQLiteStatement.bindLong(2, messageModel.getMessageNo());
        String messageTime = messageModel.getMessageTime();
        if (messageTime != null) {
            sQLiteStatement.bindString(3, messageTime);
        }
        String messageContent = messageModel.getMessageContent();
        if (messageContent != null) {
            sQLiteStatement.bindString(4, messageContent);
        }
        String filePath = messageModel.getFilePath();
        if (filePath != null) {
            sQLiteStatement.bindString(5, filePath);
        }
        String fileFormat = messageModel.getFileFormat();
        if (fileFormat != null) {
            sQLiteStatement.bindString(6, fileFormat);
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
    public MessageModel readEntity(Cursor cursor, int i) {
        int i2 = i + 0;
        Long lValueOf = cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2));
        int i3 = cursor.getInt(i + 1);
        int i4 = i + 2;
        String string = cursor.isNull(i4) ? null : cursor.getString(i4);
        int i5 = i + 3;
        String string2 = cursor.isNull(i5) ? null : cursor.getString(i5);
        int i6 = i + 4;
        int i7 = i + 5;
        return new MessageModel(lValueOf, i3, string, string2, cursor.isNull(i6) ? null : cursor.getString(i6), cursor.isNull(i7) ? null : cursor.getString(i7));
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public void readEntity(Cursor cursor, MessageModel messageModel, int i) {
        int i2 = i + 0;
        messageModel.set_id(cursor.isNull(i2) ? null : Long.valueOf(cursor.getLong(i2)));
        messageModel.setMessageNo(cursor.getInt(i + 1));
        int i3 = i + 2;
        messageModel.setMessageTime(cursor.isNull(i3) ? null : cursor.getString(i3));
        int i4 = i + 3;
        messageModel.setMessageContent(cursor.isNull(i4) ? null : cursor.getString(i4));
        int i5 = i + 4;
        messageModel.setFilePath(cursor.isNull(i5) ? null : cursor.getString(i5));
        int i6 = i + 5;
        messageModel.setFileFormat(cursor.isNull(i6) ? null : cursor.getString(i6));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.greenrobot.greendao.AbstractDao
    public final Long updateKeyAfterInsert(MessageModel messageModel, long j) {
        messageModel.set_id(Long.valueOf(j));
        return Long.valueOf(j);
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public Long getKey(MessageModel messageModel) {
        if (messageModel != null) {
            return messageModel.get_id();
        }
        return null;
    }

    @Override // org.greenrobot.greendao.AbstractDao
    public boolean hasKey(MessageModel messageModel) {
        return messageModel.get_id() != null;
    }
}
