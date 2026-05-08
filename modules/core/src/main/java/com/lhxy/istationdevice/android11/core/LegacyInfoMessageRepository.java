package com.lhxy.istationdevice.android11.core;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 旧版信息浏览页消息仓。
 * <p>
 * 对齐 M90 的消息列表读取口径，当前承接运行时调度消息和资源导入的消息 CSV/XLS。
 */
public final class LegacyInfoMessageRepository {
    private static final Object LOCK = new Object();
    private static final String PREFS_NAME = "legacy_info_messages";
    private static final String KEY_MESSAGES = "messages";
    private static final List<InfoMessage> MESSAGES = new ArrayList<>();

    private LegacyInfoMessageRepository() {
    }

    public static void append(Context context, String content) {
        String safeContent = content == null ? "" : content.trim();
        if (safeContent.isEmpty()) {
            return;
        }
        synchronized (LOCK) {
            List<InfoMessage> messages = loadMessagesLocked(context);
            InfoMessage message = new InfoMessage(nextMessageNo(messages), nowTimeText(), safeContent, null, null);
            messages.add(message);
            sortByMessageNo(messages);
            replaceCacheLocked(messages);
            saveMessageLocked(context, message);
        }
    }

    public static void upsert(Context context, int number, String messageTime, String content, String filePath, String fileFormat) {
        String safeContent = content == null ? "" : content.trim();
        if (number <= 0 || safeContent.isEmpty()) {
            return;
        }
        synchronized (LOCK) {
            List<InfoMessage> messages = loadMessagesLocked(context);
            InfoMessage candidate = new InfoMessage(
                    number,
                    normalizeTime(messageTime),
                    safeContent,
                    emptyToNull(filePath),
                    emptyToNull(fileFormat)
            );
            boolean replaced = false;
            for (int index = 0; index < messages.size(); index++) {
                if (messages.get(index).getNumber() == number) {
                    messages.set(index, candidate);
                    replaced = true;
                    break;
                }
            }
            if (!replaced) {
                messages.add(candidate);
            }
            sortByMessageNo(messages);
            replaceCacheLocked(messages);
            saveMessageLocked(context, candidate);
        }
    }

    public static List<InfoMessage> snapshot(Context context) {
        synchronized (LOCK) {
            List<InfoMessage> messages = loadMessagesLocked(context);
            replaceCacheLocked(messages);
            return new ArrayList<>(MESSAGES);
        }
    }

    public static void clear(Context context) {
        synchronized (LOCK) {
            MESSAGES.clear();
            if (context != null) {
                LegacyInfoMessageDatabaseHelper helper = new LegacyInfoMessageDatabaseHelper(context.getApplicationContext());
                helper.clearAll();
                clearLegacyPreferences(context);
            }
        }
    }

    private static List<InfoMessage> loadMessagesLocked(Context context) {
        if (context == null) {
            return new ArrayList<>(MESSAGES);
        }
        LegacyInfoMessageDatabaseHelper helper = new LegacyInfoMessageDatabaseHelper(context.getApplicationContext());
        List<InfoMessage> result = helper.queryAll();
        if (!result.isEmpty()) {
            sortByMessageNo(result);
            return result;
        }
        result = loadLegacyPreferences(context);
        if (!result.isEmpty()) {
            helper.replaceAll(result);
            clearLegacyPreferences(context);
        }
        sortByMessageNo(result);
        return result;
    }

    private static List<InfoMessage> loadLegacyPreferences(Context context) {
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String raw = preferences.getString(KEY_MESSAGES, "[]");
        List<InfoMessage> result = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(raw);
            for (int index = 0; index < array.length(); index++) {
                JSONObject object = array.optJSONObject(index);
                if (object == null) {
                    continue;
                }
                int number = object.optInt("number", 0);
                String time = object.optString("messageTime", "-");
                String entryContent = object.optString("messageContent", "").trim();
                String filePath = object.optString("filePath", null);
                String fileFormat = object.optString("fileFormat", null);
                if (number <= 0 || entryContent.isEmpty()) {
                    continue;
                }
                result.add(new InfoMessage(number, time, entryContent, emptyToNull(filePath), emptyToNull(fileFormat)));
            }
        } catch (JSONException ignored) {
            result.clear();
        }
        sortByMessageNo(result);
        return result;
    }

    private static void saveMessageLocked(Context context, InfoMessage message) {
        if (context == null) {
            return;
        }
        new LegacyInfoMessageDatabaseHelper(context.getApplicationContext()).replace(message);
    }

    private static void clearLegacyPreferences(Context context) {
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit().remove(KEY_MESSAGES).apply();
    }

    private static void replaceCacheLocked(List<InfoMessage> messages) {
        MESSAGES.clear();
        MESSAGES.addAll(messages);
    }

    private static int nextMessageNo(List<InfoMessage> messages) {
        int max = 0;
        for (InfoMessage message : messages) {
            if (message.getNumber() > max) {
                max = message.getNumber();
            }
        }
        return max + 1;
    }

    private static String nowTimeText() {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    private static String normalizeTime(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    private static void sortByMessageNo(List<InfoMessage> messages) {
        messages.sort((left, right) -> Integer.compare(left.getNumber(), right.getNumber()));
    }

    private static String emptyToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() || "null".equalsIgnoreCase(trimmed) ? null : trimmed;
    }

    public static final class InfoMessage {
        private final int number;
        private final String messageTime;
        private final String content;
        private final String filePath;
        private final String fileFormat;

        InfoMessage(int number, String messageTime, String content, String filePath, String fileFormat) {
            this.number = number;
            this.messageTime = messageTime;
            this.content = content;
            this.filePath = filePath;
            this.fileFormat = fileFormat;
        }

        public int getNumber() {
            return number;
        }

        public String getMessageTime() {
            return messageTime;
        }

        public String getContent() {
            return content;
        }

        public String getFilePath() {
            return filePath;
        }

        public String getFileFormat() {
            return fileFormat;
        }
    }
}