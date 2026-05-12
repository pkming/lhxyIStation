package com.lhxy.istationdevice.android11.domain.upgrade;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 本地升级包扫描器。
 * <p>
 * 兼容旧项目常用的 TF 卡目录，也兼容 Android11 壳当前可写的 app 专属目录。
 * <p>
 * 查找关键字：本地 APK 扫描、升级包搜索目录、优先 APK 名。
 */
public final class LocalUpgradeApkFinder {
    private static final List<String> PREFERRED_FILE_NAMES = Arrays.asList("M90.apk", "K80.apk");

    private LocalUpgradeApkFinder() {
    }

    /**
     * 从预设目录里找出最合适的升级 APK。
     */
    public static File findBest(Context context) {
        if (context == null) {
            return null;
        }
        List<File> candidates = new ArrayList<>();
        for (File directory : resolveSearchDirectories(context)) {
            collectCandidates(directory, candidates);
        }
        if (candidates.isEmpty()) {
            return null;
        }
        candidates.sort(Comparator
                .comparingInt(LocalUpgradeApkFinder::preferredNameRank)
                .thenComparingLong(File::lastModified)
                .reversed());
        return candidates.get(0);
    }

    /**
     * 返回需要扫描的升级目录列表。
     */
    private static List<File> resolveSearchDirectories(Context context) {
        List<File> directories = new ArrayList<>();
        File externalRoot = Environment.getExternalStorageDirectory();
        if (externalRoot != null) {
            directories.add(new File(externalRoot, "BusRes/ApkRes"));
            directories.add(new File(externalRoot, "IStationDevice/ApkRes"));
        }
        File upgradeDir = context.getExternalFilesDir("upgrade");
        if (upgradeDir != null) {
            directories.add(upgradeDir);
        }
        File downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (downloadsDir != null) {
            directories.add(downloadsDir);
        }
        File exportDir = context.getExternalFilesDir("exports");
        if (exportDir != null) {
            directories.add(exportDir);
        }
        directories.add(new File(context.getFilesDir(), "upgrade"));
        return directories;
    }

    /**
     * 收集目录下所有 APK 候选文件。
     */
    private static void collectCandidates(File directory, List<File> out) {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return;
        }
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file != null && file.isFile() && file.getName().toLowerCase().endsWith(".apk")) {
                out.add(file);
            }
        }
    }

    private static int preferredNameRank(File file) {
        String name = file == null ? "" : file.getName();
        int index = PREFERRED_FILE_NAMES.indexOf(name);
        return index >= 0 ? 100 - index : 0;
    }
}