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
    public void lineInfoUsesPlatformLineSerialInsteadOfDirectoryName() throws Exception {
        File busDir = Files.createTempDirectory("legacy-line-info-number").toFile();
        Files.write(
                new File(busDir, "lineInfo.csv").toPath(),
                Collections.singletonList(
                        "NO.,Line name,Current line,Attribute(1:Normal 2:Circle 3:Back),Line serial\n"
                                + "2,L1,Y,1,281"
                ),
                GB18030
        );

        LegacyGpsRouteCatalog catalog = new LegacyGpsRouteCatalog();
        Method method = LegacyGpsRouteCatalog.class.getDeclaredMethod(
                "resolveLineInfo", File.class, String.class);
        method.setAccessible(true);
        Object lineInfo = method.invoke(catalog, busDir, "L1");
        java.lang.reflect.Field lineNumber = lineInfo.getClass().getDeclaredField("lineNumber");
        lineNumber.setAccessible(true);

        assertEquals(281, lineNumber.getInt(lineInfo));
    }

    @Test
    public void loadRoute_mapsLegacyStationUidAndMileageColumns() throws Exception {
        File busDir = Files.createTempDirectory("legacy-route-station-columns").toFile();
        File lineDir = new File(busDir, "101");
        if (!lineDir.mkdirs() && !lineDir.isDirectory()) {
            throw new IllegalStateException("Unable to create test route directory");
        }
        Files.write(
                new File(lineDir, "101S.csv").toPath(),
                Collections.singletonList(
                        "Stop No.,Play,Stop name,Longitude,Latitude,Angular,Altitude,UID,Inbound Ad.,Outbound Ad.,Inbound hint,Outbound hint,Inbound Ex,Outbound Ex,InSpeed limit,Speed limit,Play point,L/M stop,Spkr/not\n"
                                + "0,A voice,A station,11400.0000,2200.0000,0,10,UID-A,arrival ad,departure ad,arrival hint,departure hint,arrival ex,departure ex,40,30,12,major,Y"
                ),
                GB18030
        );
        Files.write(
                new File(lineDir, "101SRemind.csv").toPath(),
                Collections.singletonList("Stop No.,Cross,Longitude,Latitude,Cross point,UID\n"),
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
                "up"
        );

        LegacyGpsRouteResource.StationPoint station = route.getStations().get(0);
        assertEquals("UID-A", station.getSiteCode());
        assertEquals("arrival ad", station.getStationAdvert());
        assertEquals("departure ad", station.getDepartureAdvert());
        assertEquals("40", station.getSpeedLimit());
        assertEquals(12d, station.getMileage(), 0d);
        assertEquals("major", station.getMajorStation());
        assertEquals("Y", station.getVoiceNot());
    }

    @Test
    public void loadRoute_mapsTheActualEighteenColumnLegacyUidLayout() throws Exception {
        File busDir = Files.createTempDirectory("legacy-route-eighteen-columns").toFile();
        File lineDir = new File(busDir, "101");
        if (!lineDir.mkdirs() && !lineDir.isDirectory()) {
            throw new IllegalStateException("Unable to create test route directory");
        }
        Files.write(
                new File(lineDir, "101S.csv").toPath(),
                Collections.singletonList(
                        "Stop No.,Play,Stop name,Longitude,Latitude,Angular,UID,Inbound Ad.,Outbound Ad.,Inbound hint,Outbound hint,Inbound Ex,Outbound Ex,InSpeed limit,Speed limit,Play point,L/M stop,Spkr/not\n"
                                + "0,A voice,A station,11400.0000,2200.0000,0,UID-18,arrival ad,departure ad,arrival hint,departure hint,arrival ex,departure ex,40,30,12,major,Y"
                ),
                GB18030
        );
        Files.write(
                new File(lineDir, "101SRemind.csv").toPath(),
                Collections.singletonList("Stop No.,Cross,Longitude,Latitude,Cross point,UID\n"),
                GB18030
        );

        LegacyGpsRouteCatalog catalog = new LegacyGpsRouteCatalog();
        Method method = LegacyGpsRouteCatalog.class.getDeclaredMethod(
                "loadRoute", File.class, String.class, int.class, String.class);
        method.setAccessible(true);
        LegacyGpsRouteResource route = (LegacyGpsRouteResource) method.invoke(
                catalog,
                busDir,
                "101",
                LegacyGpsRouteResource.ATTRIBUTE_UP_DOWN,
                "up"
        );

        LegacyGpsRouteResource.StationPoint station = route.getStations().get(0);
        assertEquals("UID-18", station.getSiteCode());
        assertEquals("arrival ad", station.getStationAdvert());
        assertEquals("departure ad", station.getDepartureAdvert());
        assertEquals("40", station.getSpeedLimit());
        assertEquals(12d, station.getMileage(), 0d);
        assertEquals("major", station.getMajorStation());
        assertEquals("Y", station.getVoiceNot());
    }

    @Test
    public void loadRoute_mapsChineseSixteenColumnLayoutWithoutInventingUid() throws Exception {
        File busDir = Files.createTempDirectory("legacy-route-chinese-columns").toFile();
        File lineDir = new File(busDir, "L1");
        if (!lineDir.mkdirs() && !lineDir.isDirectory()) {
            throw new IllegalStateException("Unable to create test route directory");
        }
        Files.write(
                new File(lineDir, "L1S.csv").toPath(),
                Collections.singletonList(
                        "站台编号,报站语音,站名,经度,纬度,角度,进站广告,出站广告,进站提示,出站提示,进站扩展,出站扩展,限速,站前里程,是否大站,外音开否\n"
                                + "1,岗新小学,岗新小学,11405.8666,2242.5399,176,,,,,,,25,20,小站,是"
                ),
                GB18030
        );
        Files.write(
                new File(lineDir, "L1SRemind.csv").toPath(),
                Collections.singletonList("Stop No.,Cross,Longitude,Latitude,Cross point,UID\n"),
                GB18030
        );

        LegacyGpsRouteCatalog catalog = new LegacyGpsRouteCatalog();
        Method method = LegacyGpsRouteCatalog.class.getDeclaredMethod(
                "loadRoute", File.class, String.class, int.class, String.class);
        method.setAccessible(true);
        LegacyGpsRouteResource route = (LegacyGpsRouteResource) method.invoke(
                catalog,
                busDir,
                "L1",
                LegacyGpsRouteResource.ATTRIBUTE_UP_DOWN,
                "上行"
        );

        LegacyGpsRouteResource.StationPoint station = route.getStations().get(0);
        assertEquals("岗新小学", station.getStationSound());
        assertEquals("岗新小学", station.getStationName());
        assertEquals("", station.getSiteCode());
        assertEquals("1", station.getCompatibleSiteCode());
        assertEquals("25", station.getSpeedLimit());
        assertEquals(20d, station.getMileage(), 0d);
        assertEquals("小站", station.getMajorStation());
        assertEquals("是", station.getVoiceNot());
    }

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
