package com.lhxy.istationdevice.android11.domain.station;

import com.lhxy.istationdevice.android11.domain.gps.LegacyGpsRouteResource;
import com.lhxy.istationdevice.android11.domain.module.state.StationState;
import com.lhxy.istationdevice.android11.protocol.legacy.BusLineSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 把运行期报站状态转换成旧屏显协议需要的快照模型。
 */
public final class LegacyStationSnapshotFactory {
    public BusLineSnapshot createCurrentSnapshot(LegacyGpsRouteResource route, StationState stationState) {
        if (route == null || route.getStations().isEmpty()) {
            return fallbackSnapshot(stationState);
        }
        int stationIndex = stationState == null ? 0 : clampIndex(stationState.getCurrentStationNo(), route.getStations().size());
        LegacyGpsRouteResource.StationPoint stationPoint = route.getStations().get(stationIndex);
        return new BusLineSnapshot(
                stationIndex,
                stationState == null ? 0 : stationState.getCurrentStationType(),
                resolveDirection(route, stationState),
                route.getStations().size(),
                stationPoint.getStationName(),
                route.getLineName(),
                route.firstStation() == null ? "-" : route.firstStation().getStationName(),
                route.lastStation() == null ? "-" : route.lastStation().getStationName()
        );
    }

    public List<BusLineSnapshot> createRouteSnapshots(LegacyGpsRouteResource route, StationState stationState) {
        if (route == null || route.getStations().isEmpty()) {
            return Collections.emptyList();
        }
        List<BusLineSnapshot> snapshots = new ArrayList<>();
        int direction = resolveDirection(route, stationState);
        String start = route.firstStation() == null ? "-" : route.firstStation().getStationName();
        String end = route.lastStation() == null ? "-" : route.lastStation().getStationName();
        int count = route.getStations().size();
        for (LegacyGpsRouteResource.StationPoint stationPoint : route.getStations()) {
            snapshots.add(new BusLineSnapshot(
                    stationPoint.getStationNo(),
                    0,
                    direction,
                    count,
                    stationPoint.getStationName(),
                    route.getLineName(),
                    start,
                    end
            ));
        }
        return snapshots;
    }

    private BusLineSnapshot fallbackSnapshot(StationState stationState) {
        int stationNo = stationState == null || stationState.getCurrentStationNo() < 0 ? 0 : stationState.getCurrentStationNo();
        String currentStation = stationState == null ? "-" : stationState.getCurrentStation();
        String lineName = stationState == null ? "-" : stationState.getLineName();
        String terminalStation = stationState == null ? "-" : stationState.getTerminalStation();
        int stationCount = stationState == null ? 1 : Math.max(1, stationState.getStationCount());
        return new BusLineSnapshot(
                stationNo,
                stationState == null ? 0 : stationState.getCurrentStationType(),
                resolveDirection(null, stationState),
                stationCount,
                currentStation,
                lineName,
                currentStation,
                terminalStation
        );
    }

    private int resolveDirection(LegacyGpsRouteResource route, StationState stationState) {
        String directionText = route == null ? null : route.getDirectionText();
        if (directionText == null || directionText.trim().isEmpty()) {
            directionText = stationState == null ? "上行" : stationState.getDirectionText();
        }
        return directionText != null && directionText.contains("下") ? 2 : 1;
    }

    private int clampIndex(int stationIndex, int size) {
        if (size <= 0) {
            return 0;
        }
        if (stationIndex < 0) {
            return 0;
        }
        return Math.min(stationIndex, size - 1);
    }
}