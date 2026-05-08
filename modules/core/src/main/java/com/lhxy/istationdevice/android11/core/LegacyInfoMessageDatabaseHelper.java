package com.lhxy.istationdevice.android11.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

final class LegacyInfoMessageDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "legacy_info_messages.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_MESSAGES = "MESSAGE_MODEL";
    private static final String LEGACY_TABLE_MESSAGES = "info_messages";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_MESSAGE_NO = "MESSAGE_NO";
    private static final String COLUMN_MESSAGE_TIME = "MESSAGE_TIME";
    private static final String COLUMN_MESSAGE_CONTENT = "MESSAGE_CONTENT";
    private static final String COLUMN_FILE_PATH = "FILE_PATH";
    private static final String COLUMN_FILE_FORMAT = "FILE_FORMAT";

    LegacyInfoMessageDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(buildCreateTableSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            migrateFromLegacyTable(db);
            return;
        }
        db.execSQL("DROP TABLE IF EXISTS \"" + TABLE_MESSAGES + "\"");
        onCreate(db);
    }

    List<LegacyInfoMessageRepository.InfoMessage> queryAll() {
        List<LegacyInfoMessageRepository.InfoMessage> result = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_MESSAGES,
                new String[]{COLUMN_MESSAGE_NO, COLUMN_MESSAGE_TIME, COLUMN_MESSAGE_CONTENT, COLUMN_FILE_PATH, COLUMN_FILE_FORMAT},
                null,
                null,
                null,
                null,
                COLUMN_MESSAGE_NO + " ASC"
        )) {
            while (cursor.moveToNext()) {
                result.add(new LegacyInfoMessageRepository.InfoMessage(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                ));
            }
        }
        return result;
    }

    void replace(LegacyInfoMessageRepository.InfoMessage message) {
        ContentValues values = new ContentValues();
        Long existingId = queryMessageId(message.getNumber());
        if (existingId != null) {
            values.put(COLUMN_ID, existingId);
        }
        values.put(COLUMN_MESSAGE_NO, message.getNumber());
        values.put(COLUMN_MESSAGE_TIME, message.getMessageTime());
        values.put(COLUMN_MESSAGE_CONTENT, message.getContent());
        values.put(COLUMN_FILE_PATH, message.getFilePath());
        values.put(COLUMN_FILE_FORMAT, message.getFileFormat());
        getWritableDatabase().insertWithOnConflict(TABLE_MESSAGES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    void replaceAll(List<LegacyInfoMessageRepository.InfoMessage> messages) {
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            database.delete(TABLE_MESSAGES, null, null);
            for (LegacyInfoMessageRepository.InfoMessage message : messages) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_MESSAGE_NO, message.getNumber());
                values.put(COLUMN_MESSAGE_TIME, message.getMessageTime());
                values.put(COLUMN_MESSAGE_CONTENT, message.getContent());
                values.put(COLUMN_FILE_PATH, message.getFilePath());
                values.put(COLUMN_FILE_FORMAT, message.getFileFormat());
                database.insertWithOnConflict(TABLE_MESSAGES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    void clearAll() {
        getWritableDatabase().delete(TABLE_MESSAGES, null, null);
    }

    private static String buildCreateTableSql() {
        return "CREATE TABLE IF NOT EXISTS \"" + TABLE_MESSAGES + "\" ("
                + "\"" + COLUMN_ID + "\" INTEGER PRIMARY KEY,"
                + "\"" + COLUMN_MESSAGE_NO + "\" INTEGER NOT NULL,"
                + "\"" + COLUMN_MESSAGE_TIME + "\" TEXT,"
                + "\"" + COLUMN_MESSAGE_CONTENT + "\" TEXT,"
                + "\"" + COLUMN_FILE_PATH + "\" TEXT,"
                + "\"" + COLUMN_FILE_FORMAT + "\" TEXT);";
    }

    private void migrateFromLegacyTable(SQLiteDatabase database) {
        database.beginTransaction();
        try {
            database.execSQL(buildCreateTableSql());
            if (tableExists(database, LEGACY_TABLE_MESSAGES)) {
                database.execSQL(
                        "INSERT INTO \"" + TABLE_MESSAGES + "\" ("
                                + "\"" + COLUMN_MESSAGE_NO + "\"," 
                                + "\"" + COLUMN_MESSAGE_TIME + "\"," 
                                + "\"" + COLUMN_MESSAGE_CONTENT + "\"," 
                                + "\"" + COLUMN_FILE_PATH + "\"," 
                                + "\"" + COLUMN_FILE_FORMAT + "\") "
                                + "SELECT message_no, message_time, message_content, file_path, file_format FROM " + LEGACY_TABLE_MESSAGES
                );
                database.execSQL("DROP TABLE IF EXISTS " + LEGACY_TABLE_MESSAGES);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    private boolean tableExists(SQLiteDatabase database, String tableName) {
        try (Cursor cursor = database.rawQuery(
                "SELECT name FROM sqlite_master WHERE type = ? AND name = ?",
                new String[]{"table", tableName}
        )) {
            return cursor.moveToFirst();
        }
    }

    private Long queryMessageId(int messageNo) {
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_MESSAGES,
                new String[]{COLUMN_ID},
                COLUMN_MESSAGE_NO + " = ?",
                new String[]{String.valueOf(messageNo)},
                null,
                null,
                null,
                "1"
        )) {
            if (!cursor.moveToFirst()) {
                return null;
            }
            return cursor.getLong(0);
        }
    }
}