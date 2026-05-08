package com.lhxy.istationdevice.android11.domain.gps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collections;

public class LegacyGpsRouteCatalogTest {
    private static final Charset GB18030 = Charset.forName("GB18030");

    @Test
    public void loadRoute_readsExtendedReminderColumnsWhenPresent() throws Exception {
        File busDir = Files.createTempDirectory("legacy-route-catalog-test").toFile();
        File lineDir = new File(busDir, "101");
        if (!lineDir.mkdirs() && !lineDir.isDirectory()) {
            throw new IllegalStateException("无法创建测试线路目录");
        }
        Files.write(
                new File(lineDir, "101S.csv").toPath(),
                Collections.singletonList("站序,语音,站名,经度,纬度,角度,海拔,进站广告,出站广告,进站提示,出站提示,进站扩展,出站扩展,限速,里程,大站,外音开否\n0,A站,A站,11400.0000,2200.0000,,,,,,,,,,0,,"),
                GB18030
        );
        Files.write(
                new File(lineDir, "101SRemind.csv").toPath(),
                Collections.singletonList("Stop No.,Cross,Longitude,Latitude,Cross point,UID,CrossInNotice,CrossOutNotice,CrossInEx,CrossOutEx,SpeedLimit,CrossType,Spkr/not\n12,转弯提醒,11400.1000,2200.1000,30,UID-12,进路口提示,出路口提示,进扩展,出扩展,20,1,Y"),
                GB18030
        );

        LegacyGpsRouteCatalog catalog = new LegacyGpsRouteCatalog();
        Method method = LegacyGpsRouteCatalog.class.getDeclaredMethod("loadRoute", File.class, String.class, int.class, String.class);
        method.setAccessible(true);
        LegacyGpsRouteResource route = (LegacyGpsRouteResource) method.invoke(
                catalog,
                busDir,
                "101",
                LegacyGpsRouteResource.ATTRIBUTE_UP_DOWN,
                "上行"
        );

        assertNotNull(route);
        assertEquals(1, route.getReminders().size());
        LegacyGpsRouteResource.ReminderPoint reminderPoint = route.getReminders().get(0);
        assertEquals("UID-12", reminderPoint.getCrossCode());
        assertEquals("进路口提示", reminderPoint.getCrossPrompt());
        assertEquals("出路口提示", reminderPoint.getCrossDeparturePrompt());
        assertEquals("进扩展", reminderPoint.getCrossExpansion());
        assertEquals("出扩展", reminderPoint.getCrossDepartureExpansion());
        assertEquals("20", reminderPoint.getCrossSpeedLimit());
        assertEquals("1", reminderPoint.getCrossType());
        assertEquals("Y", reminderPoint.getVoiceNot());
    }
}