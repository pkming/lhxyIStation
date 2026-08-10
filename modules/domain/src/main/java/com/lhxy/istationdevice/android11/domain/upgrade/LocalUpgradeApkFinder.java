package com.lhxy.istationdevice.android11.domain.upgrade;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        return findBestFromDirectories(resolveSearchDirectories(context));
    }

    static File findBestFromDirectories(List<File> directories) {
        List<File> candidates = new ArrayList<>();
        if (directories != null) {
            for (File directory : directories) {
                collectCandidates(directory, candidates);
            }
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
        Map<String, File> directories = new LinkedHashMap<>();
        for (File directory : buildLegacyUpgradeDirectories(resolveStorageRoots(context))) {
            addUnique(directories, directory);
        }
        File upgradeDir = context.getExternalFilesDir("upgrade");
        if (upgradeDir != null) {
            addUnique(directories, upgradeDir);
        }
        File downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (downloadsDir != null) {
            addUnique(directories, downloadsDir);
        }
        File exportDir = context.getExternalFilesDir("exports");
        if (exportDir != null) {
            addUnique(directories, exportDir);
        }
        addUnique(directories, new File(context.getFilesDir(), "upgrade"));
        return new ArrayList<>(directories.values());
    }

    static List<File> buildLegacyUpgradeDirectories(List<File> storageRoots) {
        Map<String, File> directories = new LinkedHashMap<>();
        if (storageRoots != null) {
            for (File root : storageRoots) {
                if (root == null) {
                    continue;
                }
                addUnique(directories, new File(root, "BusRes/ApkRes"));
                addUnique(directories, new File(root, "IStationDevice/ApkRes"));
            }
        }
        return new ArrayList<>(directories.values());
    }

    private static List<File> resolveStorageRoots(Context context) {
        Map<String, File> roots = new LinkedHashMap<>();
        addUnique(roots, Environment.getExternalStorageDirectory());

        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        if (storageManager != null) {
            addPublicStorageVolumes(roots, storageManager);
            addVendorStorageVolumes(roots, storageManager);
        }

        // 旧 M90 车机使用的典型挂载点；保留显式兜底以兼容厂商 StorageManager 不公开 U 盘的情况。
        addUnique(roots, new File("/mnt/usb_storage"));
        addUnique(roots, new File("/mnt/usb_storage/udisk0"));
        addUnique(roots, new File("/storage/usb_storage"));
        addStorageChildren(roots, new File("/storage"));
        addStorageChildren(roots, new File("/mnt/usb_storage"));
        return new ArrayList<>(roots.values());
    }

    private static void addPublicStorageVolumes(Map<String, File> roots, StorageManager storageManager) {
        try {
            for (StorageVolume volume : storageManager.getStorageVolumes()) {
                File directory = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ? volume.getDirectory() : null;
                if (directory == null) {
                    directory = reflectFile(volume, "getPathFile");
                }
                if (directory == null) {
                    directory = reflectFile(volume, "getPath");
                }
                addUnique(roots, directory);
            }
        } catch (RuntimeException ignored) {
            // 厂商 ROM 可能限制卷查询，继续走 getVolumePaths 和固定挂载点。
        }
    }

    private static void addVendorStorageVolumes(Map<String, File> roots, StorageManager storageManager) {
        try {
            Method method = storageManager.getClass().getMethod("getVolumePaths");
            Object value = method.invoke(storageManager);
            if (value instanceof String[]) {
                for (String path : (String[]) value) {
                    if (path != null && !path.trim().isEmpty()) {
                        addUnique(roots, new File(path.trim()));
                    }
                }
            }
        } catch (Exception ignored) {
            // 标准 Android 11 通常没有公开该方法，StorageVolume 路径已经覆盖。
        }
    }

    private static File reflectFile(Object target, String methodName) {
        try {
            Method method = target.getClass().getMethod(methodName);
            Object value = method.invoke(target);
            if (value instanceof File) {
                return (File) value;
            }
            if (value instanceof String && !((String) value).trim().isEmpty()) {
                return new File(((String) value).trim());
            }
        } catch (Exception ignored) {
            // 尝试下一种卷路径来源。
        }
        return null;
    }

    private static void addStorageChildren(Map<String, File> roots, File parent) {
        try {
            File[] children = parent.listFiles(File::isDirectory);
            if (children == null) {
                return;
            }
            for (File child : children) {
                addUnique(roots, child);
            }
        } catch (SecurityException ignored) {
            // 没有目录枚举权限时由所有文件访问授权或 StorageVolume 路径兜底。
        }
    }

    private static void addUnique(Map<String, File> files, File file) {
        if (file == null) {
            return;
        }
        String path = file.getAbsolutePath();
        if (!path.trim().isEmpty()) {
            files.put(path, file);
        }
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
