package com.lhxy.istationdevice.android11.domain.module;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;

import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsAutoReportEngine;
import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsRouteResource;
import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;

public class StationBusinessModuleTest {
    @Test
    public void resolveSpeedLimit_prefersActiveCrossingLimit() throws Exception {
        StationBusinessModule module = new StationBusinessModule(null, null, null, null, null, null, null);
        module.getStationState().applyLineProfile("101", "上行", Arrays.asList("A站", "B站"));
        module.getStationState().recordAutoStation(1, "B站", LegacyGpsAutoReportEngine.STATION_TYPE_ENTER);
        module.getStationState().recordReminder("转弯提醒", "UID-12", "1", 12, "20", LegacyGpsAutoReportEngine.REMINDER_TYPE_ENTER);

        LegacyGpsRouteResource route = new LegacyGpsRouteResource(
                "101",
                LegacyGpsRouteResource.ATTRIBUTE_UP_DOWN,
                "上行",
                Arrays.asList(
                        station(0, "A站", "40"),
                        station(1, "B站", "50")
                ),
                Collections.emptyList()
        );

        Method method = StationBusinessModule.class.getDeclaredMethod("resolveSpeedLimit", LegacyGpsRouteResource.class);
        method.setAccessible(true);
        int speedLimit = (Integer) method.invoke(module, route);

        assertEquals(20, speedLimit);
    }

    @Test
    public void resolveSpeedLimit_fallsBackToStationLimitWhenNoCrossing() throws Exception {
        StationBusinessModule module = new StationBusinessModule(null, null, null, null, null, null, null);
        module.getStationState().applyLineProfile("101", "上行", Arrays.asList("A站", "B站"));
        module.getStationState().recordAutoStation(1, "B站", LegacyGpsAutoReportEngine.STATION_TYPE_ENTER);

        LegacyGpsRouteResource route = new LegacyGpsRouteResource(
                "101",
                LegacyGpsRouteResource.ATTRIBUTE_UP_DOWN,
                "上行",
                Arrays.asList(
                        station(0, "A站", "40"),
                        station(1, "B站", "50")
                ),
                Collections.emptyList()
        );

        Method method = StationBusinessModule.class.getDeclaredMethod("resolveSpeedLimit", LegacyGpsRouteResource.class);
        method.setAccessible(true);
        int speedLimit = (Integer) method.invoke(module, route);

        assertEquals(50, speedLimit);
    }

        @Test
        public void resolveSpeedWarningRepeatInterval_matchesM90Throttle() throws Exception {
                StationBusinessModule module = new StationBusinessModule(null, null, null, null, null, null, null);

                Method method = StationBusinessModule.class.getDeclaredMethod("resolveSpeedWarningRepeatIntervalMs");
                method.setAccessible(true);
                long repeatIntervalMs = (Long) method.invoke(module);

                assertEquals(2500L, repeatIntervalMs);
        }

        @Test
        public void isSpeedWarningActive_requiresStrictlyExceedingSpeedLimit() throws Exception {
                StationBusinessModule module = new StationBusinessModule(null, null, null, null, null, null, null);

                Method method = StationBusinessModule.class.getDeclaredMethod("isSpeedWarningActive", int.class, int.class);
                method.setAccessible(true);

                assertEquals(false, (Boolean) method.invoke(module, 20, 20));
                assertEquals(false, (Boolean) method.invoke(module, 19, 20));
                assertEquals(true, (Boolean) method.invoke(module, 21, 20));
        }

        @Test
        public void isGpsReadyForSpeedWarning_requiresThreeSecondWarmupAfterInvalidFix() throws Exception {
                StationBusinessModule module = new StationBusinessModule(null, null, null, null, null, null, null);

                Method method = StationBusinessModule.class.getDeclaredMethod("isGpsReadyForSpeedWarning", GpsFixSnapshot.class, long.class);
                method.setAccessible(true);

                assertEquals(false, (Boolean) method.invoke(module, gpsSnapshot(false, "10.0"), 1_000L));
                assertEquals(false, (Boolean) method.invoke(module, gpsSnapshot(true, "10.0"), 2_000L));
                assertEquals(false, (Boolean) method.invoke(module, gpsSnapshot(true, "10.0"), 4_000L));
                assertEquals(true, (Boolean) method.invoke(module, gpsSnapshot(true, "10.0"), 4_001L));
        }

        @Test
        public void hasSpeedWarningInput_requiresCoordinatesAndLegacySpeedCap() throws Exception {
                StationBusinessModule module = new StationBusinessModule(null, null, null, null, null, null, null);

                Method method = StationBusinessModule.class.getDeclaredMethod("hasSpeedWarningInput", GpsFixSnapshot.class, int.class);
                method.setAccessible(true);

                assertEquals(true, (Boolean) method.invoke(module, gpsSnapshot(true, "10.0"), 19));
                assertEquals(false, (Boolean) method.invoke(module, gpsSnapshot(true, "50.0"), 93));
                assertEquals(false, (Boolean) method.invoke(module, gpsSnapshot(true, "10.0", "", "114.123456"), 19));
                assertEquals(false, (Boolean) method.invoke(module, gpsSnapshot(true, "10.0", "22.654321", ""), 19));
        }

    private LegacyGpsRouteResource.StationPoint station(int stationNo, String stationName, String speedLimit) {
        return new LegacyGpsRouteResource.StationPoint(
                stationNo,
                stationName,
                stationName,
                "114.000000",
                "22.000000",
                114.0d,
                22.0d,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                speedLimit,
                0d,
                "",
                ""
        );
    }

        private GpsFixSnapshot gpsSnapshot(boolean valid, String speedKnots) {
                return gpsSnapshot(valid, speedKnots, "22.654321", "114.123456");
        }

        private GpsFixSnapshot gpsSnapshot(boolean valid, String speedKnots, String latitudeDecimal, String longitudeDecimal) {
                return new GpsFixSnapshot(
                                "$GPRMC",
                                valid,
                                valid ? 1 : 0,
                                valid ? 3 : 1,
                                "120000",
                                "260508",
                                "2239.25926",
                                "N",
                                latitudeDecimal,
                                "11407.40736",
                                "E",
                                longitudeDecimal,
                                speedKnots,
                                "0.0",
                                "0.0",
                                valid ? 8 : 0
                );
        }
}