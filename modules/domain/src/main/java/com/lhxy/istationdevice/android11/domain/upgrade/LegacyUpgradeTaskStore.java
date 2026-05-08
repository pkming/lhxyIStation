package com.lhxy.istationdevice.android11.domain.upgrade;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 持久化旧 M90 风格升级任务，支撑重启后的任务恢复与复用判断。
 */
final class LegacyUpgradeTaskStore {
    private static final String PREFS_NAME = "legacy_upgrade_tasks";
    private static final String KEY_TASKS = "tasks";

    List<StoredTask> load(Context context) {
        List<StoredTask> result = new ArrayList<>();
        if (context == null) {
            return result;
        }
        String rawJson = prefs(context).getString(KEY_TASKS, "[]");
        try {
            JSONArray array = new JSONArray(rawJson == null ? "[]" : rawJson);
            for (int index = 0; index < array.length(); index++) {
                JSONObject item = array.optJSONObject(index);
                if (item == null) {
                    continue;
                }
                StoredTask task = StoredTask.fromJson(item);
                if (task != null) {
                    result.add(task);
                }
            }
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
        return result;
    }

    void save(Context context, StoredTask task) {
        if (context == null || task == null) {
            return;
        }
        Map<Integer, StoredTask> taskMap = loadMap(context);
        taskMap.put(task.getDownloadType(), task);
        writeAll(context, taskMap.values());
    }

    void remove(Context context, int downloadType) {
        if (context == null) {
            return;
        }
        Map<Integer, StoredTask> taskMap = loadMap(context);
        if (taskMap.remove(downloadType) != null) {
            writeAll(context, taskMap.values());
        }
    }

    private Map<Integer, StoredTask> loadMap(Context context) {
        Map<Integer, StoredTask> result = new LinkedHashMap<>();
        for (StoredTask task : load(context)) {
            result.put(task.getDownloadType(), task);
        }
        return result;
    }

    private void writeAll(Context context, Iterable<StoredTask> tasks) {
        JSONArray array = new JSONArray();
        for (StoredTask task : tasks) {
            if (task != null) {
                array.put(task.toJson());
            }
        }
        prefs(context).edit().putString(KEY_TASKS, array.toString()).apply();
    }

    private SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    static final class StoredTask {
        private final String channelName;
        private final int downloadType;
        private final String stateName;
        private final long scheduledAtMillis;
        private final String localPath;
        private final String terminalId;
        private final int requestSerialNumber;
        private final String serverAddress;
        private final int serverAddressPort;
        private final int protocolType;
        private final String loginName;
        private final String loginPwd;
        private final String versionUrl;
        private final int upgradeType;
        private final String scheduleTimeBcd;
        private final String cancelSerialHex;
        private final int retryCount;

        StoredTask(
                String channelName,
                int downloadType,
                String stateName,
                long scheduledAtMillis,
                String localPath,
                String terminalId,
                int requestSerialNumber,
                String serverAddress,
                int serverAddressPort,
                int protocolType,
                String loginName,
                String loginPwd,
                String versionUrl,
                int upgradeType,
                String scheduleTimeBcd,
                String cancelSerialHex,
                int retryCount
        ) {
            this.channelName = channelName == null ? "" : channelName.trim();
            this.downloadType = downloadType;
            this.stateName = stateName == null ? "FAILED" : stateName.trim();
            this.scheduledAtMillis = scheduledAtMillis;
            this.localPath = localPath == null ? "" : localPath.trim();
            this.terminalId = terminalId == null ? "" : terminalId.trim();
            this.requestSerialNumber = requestSerialNumber;
            this.serverAddress = serverAddress == null ? "" : serverAddress.trim();
            this.serverAddressPort = serverAddressPort;
            this.protocolType = protocolType;
            this.loginName = loginName == null ? "" : loginName.trim();
            this.loginPwd = loginPwd == null ? "" : loginPwd.trim();
            this.versionUrl = versionUrl == null ? "" : versionUrl.trim();
            this.upgradeType = upgradeType;
            this.scheduleTimeBcd = scheduleTimeBcd == null ? "" : scheduleTimeBcd.trim();
            this.cancelSerialHex = cancelSerialHex == null ? "" : cancelSerialHex.trim();
            this.retryCount = retryCount;
        }

        int getDownloadType() {
            return downloadType;
        }

        String getChannelName() {
            return channelName;
        }

        String getStateName() {
            return stateName;
        }

        long getScheduledAtMillis() {
            return scheduledAtMillis;
        }

        String getLocalPath() {
            return localPath;
        }

        String getTerminalId() {
            return terminalId;
        }

        int getRequestSerialNumber() {
            return requestSerialNumber;
        }

        String getServerAddress() {
            return serverAddress;
        }

        int getServerAddressPort() {
            return serverAddressPort;
        }

        int getProtocolType() {
            return protocolType;
        }

        String getLoginName() {
            return loginName;
        }

        String getLoginPwd() {
            return loginPwd;
        }

        String getVersionUrl() {
            return versionUrl;
        }

        int getUpgradeType() {
            return upgradeType;
        }

        String getScheduleTimeBcd() {
            return scheduleTimeBcd;
        }

        String getCancelSerialHex() {
            return cancelSerialHex;
        }

        int getRetryCount() {
            return retryCount;
        }

        JSONObject toJson() {
            JSONObject object = new JSONObject();
            try {
                object.put("channelName", channelName);
                object.put("downloadType", downloadType);
                object.put("stateName", stateName);
                object.put("scheduledAtMillis", scheduledAtMillis);
                object.put("localPath", localPath);
                object.put("terminalId", terminalId);
                object.put("requestSerialNumber", requestSerialNumber);
                object.put("serverAddress", serverAddress);
                object.put("serverAddressPort", serverAddressPort);
                object.put("protocolType", protocolType);
                object.put("loginName", loginName);
                object.put("loginPwd", loginPwd);
                object.put("versionUrl", versionUrl);
                object.put("upgradeType", upgradeType);
                object.put("scheduleTimeBcd", scheduleTimeBcd);
                object.put("cancelSerialHex", cancelSerialHex);
                object.put("retryCount", retryCount);
            } catch (Exception ignored) {
            }
            return object;
        }

        static StoredTask fromJson(JSONObject object) {
            if (object == null) {
                return null;
            }
            return new StoredTask(
                    object.optString("channelName", ""),
                    object.optInt("downloadType", 0),
                    object.optString("stateName", "FAILED"),
                    object.optLong("scheduledAtMillis", 0L),
                    object.optString("localPath", ""),
                    object.optString("terminalId", ""),
                    object.optInt("requestSerialNumber", 0),
                    object.optString("serverAddress", ""),
                    object.optInt("serverAddressPort", 0),
                    object.optInt("protocolType", 0),
                    object.optString("loginName", ""),
                    object.optString("loginPwd", ""),
                    object.optString("versionUrl", ""),
                    object.optInt("upgradeType", 0),
                    object.optString("scheduleTimeBcd", ""),
                    object.optString("cancelSerialHex", ""),
                    object.optInt("retryCount", 0)
            );
        }
    }
}