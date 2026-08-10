package com.lhxy.istationdevice.android11.domain.gps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class LegacyGpsAutoReportEngineTest {

    @Test
        public void evaluate_entersNextStationUsingM90Rules() {
        LegacyGpsAutoReportEngine engine = new LegacyGpsAutoReportEngine();
        LegacyGpsRouteResource route = new LegacyGpsRouteResource(
                "L1",
                LegacyGpsRouteResource.ATTRIBUTE_UP_DOWN,
                "上行",
                Arrays.asList(
                        new LegacyGpsRouteResource.StationPoint(
                                0,
                                "起点播报",
                                "A站",
                                "114.000000",
                                "22.000000",
                                114.000000d,
                                22.000000d,
                                "0",
                                "10",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "40",
                                30d,
                                "",
                                ""
                        ),
                        new LegacyGpsRouteResource.StationPoint(
                                1,
                                "下一站播报",
                                "B站",
                                "114.010000",
                                "22.010000",
                                114.010000d,
                                22.010000d,
                                "0",
                                "10",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "40",
                                30d,
                                "",
                                ""
                        ),
                        new LegacyGpsRouteResource.StationPoint(
                                2,
                                "终点播报",
                                "C站",
                                "114.020000",
                                "22.020000",
                                114.020000d,
                                22.020000d,
                                "0",
                                "10",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "40",
                                30d,
                                "",
                                ""
                        )
                ),
                Collections.emptyList()
        );
        GpsFixSnapshot warmupSnapshot = new GpsFixSnapshot(
                "$GNGGA,...",
                true,
                1,
                3,
                "115959",
                "060526",
                "2200.0000",
                "N",
                "22.000000",
                "11400.0000",
                "E",
                "114.000000",
                "10",
                "0",
                "12",
                6
        );
        GpsFixSnapshot leaveFirstStationSnapshot = new GpsFixSnapshot(
                "$GNGGA,...",
                true,
                1,
                3,
                "115959",
                "060526",
                "2200.0300",
                "N",
                "22.000500",
                "11400.0300",
                "E",
                "114.000500",
                "10",
                "0",
                "12",
                6
        );
        GpsFixSnapshot snapshot = new GpsFixSnapshot(
                "$GNGGA,...",
                true,
                1,
                3,
                "120000",
                "060526",
                "2200.6000",
                "N",
                "22.010000",
                "11400.6000",
                "E",
                "114.010000",
                "10",
                "0",
                "12",
                6
        );

        engine.evaluate(route, warmupSnapshot, false);
        engine.evaluate(route, leaveFirstStationSnapshot, false);
        LegacyGpsAutoReportEngine.AutoReportEvent event = engine.evaluate(route, snapshot, false);

        assertNotNull(event);
        assertFalse(event.isNone());
        assertEquals(LegacyGpsAutoReportEngine.OP_STATION, event.getOperationType());
        assertEquals(LegacyGpsAutoReportEngine.STATION_TYPE_ENTER, event.getStationType());
        assertNotNull(event.getStationPoint());
        assertEquals("B站", event.getStationPoint().getStationName());
    }

    @Test
    public void evaluate_switchesDirectionOnlyAfterFiveUniqueVotes() {
        LegacyGpsAutoReportEngine engine = new LegacyGpsAutoReportEngine();
        LegacyGpsRouteResource route = new LegacyGpsRouteResource(
                "L1",
                LegacyGpsRouteResource.ATTRIBUTE_UP_DOWN,
                "上行",
                Arrays.asList(
                        station(0, "A站", 114.000000d, 22.000000d),
                        station(1, "B站", 114.010000d, 22.010000d),
                        station(2, "C站", 114.020000d, 22.020000d),
                        station(3, "D站", 114.030000d, 22.030000d),
                        station(4, "E站", 114.040000d, 22.040000d),
                        station(5, "F站", 114.050000d, 22.050000d)
                ),
                Collections.emptyList()
        );

        LegacyGpsAutoReportEngine.AutoReportEvent event1 = engine.evaluate(route, offStationSnapshot(114.010000d, 22.010900d), false);
        LegacyGpsAutoReportEngine.AutoReportEvent event2 = engine.evaluate(route, offStationSnapshot(114.020000d, 22.020900d), false);
        LegacyGpsAutoReportEngine.AutoReportEvent event3 = engine.evaluate(route, offStationSnapshot(114.030000d, 22.030900d), false);
        LegacyGpsAutoReportEngine.AutoReportEvent event4 = engine.evaluate(route, offStationSnapshot(114.040000d, 22.040900d), false);
        LegacyGpsAutoReportEngine.AutoReportEvent event5 = engine.evaluate(route, offStationSnapshot(114.050000d, 22.050900d), false);

        assertNotNull(event1);
        assertTrue(event1.isNone());
        assertTrue(event2.isNone());
        assertTrue(event3.isNone());
        assertTrue(event4.isNone());
        assertEquals(LegacyGpsAutoReportEngine.OP_SWITCH_DIRECTION, event5.getOperationType());
        assertNotNull(event5.getStationPoint());
        assertEquals("F站", event5.getStationPoint().getStationName());
    }

    @Test
    public void evaluate_triggersReminderUsingMileageWithoutAngleGate() {
        LegacyGpsAutoReportEngine engine = new LegacyGpsAutoReportEngine();
        LegacyGpsRouteResource route = new LegacyGpsRouteResource(
                "L1",
                LegacyGpsRouteResource.ATTRIBUTE_UP_DOWN,
                "上行",
                Arrays.asList(
                        station(0, "A站", 114.010000d, 22.010000d),
                        station(1, "B站", 114.020000d, 22.020000d)
                ),
                Collections.singletonList(
                        new LegacyGpsRouteResource.ReminderPoint(
                                0,
                                "转弯提醒",
                                "114.000000",
                                "22.000000",
                                114.000000d,
                                22.000000d,
                                "30",
                                "",
                                30d,
                                "",
                                "",
                                "",
                                "",
                                "",
                                "20",
                                "",
                                ""
                        )
                )
        );

        LegacyGpsAutoReportEngine.AutoReportEvent event = engine.evaluate(
                route,
                new GpsFixSnapshot(
                        "$GNRMC,...",
                        true,
                        1,
                        3,
                        "120000",
                        "060526",
                        "2200.0000",
                        "N",
                        "22.000000",
                        "11400.0162",
                        "E",
                        "114.000270",
                        "10",
                        "200",
                        "12",
                        6
                ),
                true
        );

        assertNotNull(event);
        assertFalse(event.isNone());
        assertEquals(LegacyGpsAutoReportEngine.OP_REMINDER, event.getOperationType());
        assertEquals(LegacyGpsAutoReportEngine.REMINDER_TYPE_ENTER, event.getReminderType());
        assertNotNull(event.getReminderPoint());
        assertEquals("转弯提醒", event.getReminderPoint().getReminderName());
    }

    @Test
    public void evaluate_emitsReminderLeaveAfterLeavingCrossingRange() {
        LegacyGpsAutoReportEngine engine = new LegacyGpsAutoReportEngine();
        LegacyGpsRouteResource route = new LegacyGpsRouteResource(
                "L1",
                LegacyGpsRouteResource.ATTRIBUTE_UP_DOWN,
                "上行",
                Arrays.asList(
                        station(0, "A站", 114.010000d, 22.010000d),
                        station(1, "B站", 114.020000d, 22.020000d)
                ),
                Collections.singletonList(
                        new LegacyGpsRouteResource.ReminderPoint(
                                0,
                                "转弯提醒",
                                "114.000000",
                                "22.000000",
                                114.000000d,
                                22.000000d,
                                "",
                                "",
                                30d,
                                "",
                                "",
                                "",
                                "",
                                "",
                                "20",
                                "",
                                ""
                        )
                )
        );

        engine.evaluate(
                route,
                new GpsFixSnapshot(
                        "$GNRMC,...",
                        true,
                        1,
                        3,
                        "120000",
                        "060526",
                        "2200.0000",
                        "N",
                        "22.000000",
                        "11400.0162",
                        "E",
                        "114.000270",
                        "10",
                        "200",
                        "12",
                        6
                ),
                true
        );

        LegacyGpsAutoReportEngine.AutoReportEvent event = engine.evaluate(
                route,
                new GpsFixSnapshot(
                        "$GNRMC,...",
                        true,
                        1,
                        3,
                        "120010",
                        "060526",
                        "2200.0500",
                        "N",
                        "22.000833",
                        "11400.0500",
                        "E",
                        "114.000833",
                        "10",
                        "200",
                        "12",
                        6
                ),
                true
        );

        assertNotNull(event);
        assertFalse(event.isNone());
        assertEquals(LegacyGpsAutoReportEngine.OP_REMINDER, event.getOperationType());
        assertEquals(LegacyGpsAutoReportEngine.REMINDER_TYPE_LEAVE, event.getReminderType());
        assertNotNull(event.getReminderPoint());
        assertEquals("转弯提醒", event.getReminderPoint().getReminderName());
    }

    /**
     * 进站 → 离站 → GPS 抖回原坐标：不得重复播报同一站。
     * <p>
     * 正确流程：进站播报 → 离开该站坐标范围 → 到达下一站再播报。
     * 同一站只有在真正离开后且 GPS 锁定到新站时才能再次触发，
     * 在同一坐标范围内来回抖动不应重复播报。
     */
    @Test
    public void evaluate_suppressesDuplicateAnnounceAtSamePosition() {
        LegacyGpsAutoReportEngine engine = new LegacyGpsAutoReportEngine();
        LegacyGpsRouteResource route = new LegacyGpsRouteResource(
                "L1",
                LegacyGpsRouteResource.ATTRIBUTE_UP_DOWN,
                "上行",
                Arrays.asList(
                        station(0, "A站", 114.000000d, 22.000000d),
                        station(1, "B站", 114.010000d, 22.010000d),
                        station(2, "C站", 114.020000d, 22.020000d)
                ),
                Collections.emptyList()
        );

        // Tick 1: warmup 在 A 站坐标中心，ENTER 被初始 stationInner=true 抑制，
        //         但 initSite 被设为 false
        engine.evaluate(route, snapshotAt(114.000000d, 22.000000d), false);

        // Tick 2: 移到 A 站坐标范围外（约 50m）→ LEAVE 触发，lastLeftStationNo 记录 A(0)
        LegacyGpsAutoReportEngine.AutoReportEvent leaveEvent =
                engine.evaluate(route, snapshotAt(114.000000d, 22.000450d), false);
        assertFalse("离站事件应触发", leaveEvent.isNone());
        assertEquals(LegacyGpsAutoReportEngine.OP_STATION, leaveEvent.getOperationType());
        assertEquals(LegacyGpsAutoReportEngine.STATION_TYPE_LEAVE, leaveEvent.getStationType());

        // Tick 3: GPS 抖回 A 站坐标范围内 → 必须被抑制，不得重复播报 A
        LegacyGpsAutoReportEngine.AutoReportEvent spurious =
                engine.evaluate(route, snapshotAt(114.000000d, 22.000000d), false);
        assertTrue("离站后 GPS 抖回同一坐标，不得重复进站播报", spurious.isNone());

        // Tick 4: 再次验证，连续多个 tick 停在 A 坐标仍不应播报
        LegacyGpsAutoReportEngine.AutoReportEvent spurious2 =
                engine.evaluate(route, snapshotAt(114.000050d, 22.000050d), false);
        assertTrue("持续停在 A 坐标范围不应再次播报", spurious2.isNone());

        // Tick 5: 真正到达 B 站 → 应正常播报
        LegacyGpsAutoReportEngine.AutoReportEvent enterB =
                engine.evaluate(route, snapshotAt(114.010000d, 22.010000d), false);
        assertFalse("到达 B 站应触发进站播报", enterB.isNone());
        assertEquals(LegacyGpsAutoReportEngine.OP_STATION, enterB.getOperationType());
        assertEquals(LegacyGpsAutoReportEngine.STATION_TYPE_ENTER, enterB.getStationType());
        assertEquals("B站", enterB.getStationPoint().getStationName());
    }

    private GpsFixSnapshot snapshotAt(double longitude, double latitude) {
        return new GpsFixSnapshot(
                "$GNGGA,...",
                true,
                1,
                3,
                "120000",
                "060526",
                String.format("%.6f", latitude),
                "N",
                String.format("%.6f", latitude),
                String.format("%.6f", longitude),
                "E",
                String.format("%.6f", longitude),
                "10",
                "0",
                "12",
                6
        );
    }

    private LegacyGpsRouteResource.StationPoint station(int stationNo, String stationName, double longitude, double latitude) {
        return new LegacyGpsRouteResource.StationPoint(
                stationNo,
                stationName + "播报",
                stationName,
                String.format("%.6f", longitude),
                String.format("%.6f", latitude),
                longitude,
                latitude,
                "0",
                "10",
                "",
                "",
                "",
                "",
                "",
                "",
                "40",
                20d,
                "",
                ""
        );
    }

    private GpsFixSnapshot offStationSnapshot(double longitude, double latitude) {
        return new GpsFixSnapshot(
                "$GNGGA,...",
                true,
                1,
                3,
                "120000",
                "060526",
                String.format("%.4f", latitude),
                "N",
                String.format("%.6f", latitude),
                String.format("%.4f", longitude),
                "E",
                String.format("%.6f", longitude),
                "10",
                "0",
                "12",
                6
        );
    }
}
