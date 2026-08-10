package com.lhxy.istationdevice.android11.domain.upgrade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class LocalUpgradeApkFinderTest {

    @Test
    public void buildLegacyUpgradeDirectories_includesOnsiteUsbLayout() {
        List<File> directories = LocalUpgradeApkFinder.buildLegacyUpgradeDirectories(Arrays.asList(
                new File("/storage/1234-5678"),
                new File("/mnt/usb_storage/udisk0")
        ));

        assertTrue(directories.contains(new File("/storage/1234-5678/BusRes/ApkRes")));
        assertTrue(directories.contains(new File("/mnt/usb_storage/udisk0/BusRes/ApkRes")));
    }

    @Test
    public void findBestFromDirectories_prefersM90Package() throws Exception {
        File workspace = Files.createTempDirectory("local-upgrade-apk").toFile();
        File usbApkDir = new File(workspace, "BusRes/ApkRes");
        if (!usbApkDir.mkdirs() && !usbApkDir.isDirectory()) {
            throw new IllegalStateException("无法创建测试升级目录");
        }
        File m90Apk = new File(usbApkDir, "M90.apk");
        File arbitraryApk = new File(usbApkDir, "other-newer.apk");
        Files.write(m90Apk.toPath(), "m90".getBytes(StandardCharsets.UTF_8));
        Files.write(arbitraryApk.toPath(), "other".getBytes(StandardCharsets.UTF_8));
        arbitraryApk.setLastModified(m90Apk.lastModified() + 10_000L);

        File selected = LocalUpgradeApkFinder.findBestFromDirectories(Arrays.asList(usbApkDir));

        assertEquals(m90Apk.getAbsolutePath(), selected.getAbsolutePath());
    }
}
