package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.amap.api.col.p0003sl.ip;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.interfaces.IRouteSearch;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRoutePlanResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.TruckRouteRestult;
import com.amap.api.services.route.WalkRouteResult;

/* JADX INFO: compiled from: RouteSearchCore.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ht implements IRouteSearch {
    private RouteSearch.OnRouteSearchListener a;
    private RouteSearch.OnTruckRouteSearchListener b;
    private RouteSearch.OnRoutePlanSearchListener c;
    private Context d;
    private Handler e;

    public ht(Context context) throws AMapException {
        iq iqVarA = ip.a(context, fo.a(false));
        if (iqVarA.a != ip.c.SuccessCode) {
            throw new AMapException(iqVarA.b, 1, iqVarA.b, iqVarA.a.a());
        }
        this.d = context.getApplicationContext();
        this.e = ga.a();
    }

    @Override // com.amap.api.services.interfaces.IRouteSearch
    public final void setRouteSearchListener(RouteSearch.OnRouteSearchListener onRouteSearchListener) {
        this.a = onRouteSearchListener;
    }

    @Override // com.amap.api.services.interfaces.IRouteSearch
    public final void setOnTruckRouteSearchListener(RouteSearch.OnTruckRouteSearchListener onTruckRouteSearchListener) {
        this.b = onTruckRouteSearchListener;
    }

    @Override // com.amap.api.services.interfaces.IRouteSearch
    public final void setOnRoutePlanSearchListener(RouteSearch.OnRoutePlanSearchListener onRoutePlanSearchListener) {
        this.c = onRoutePlanSearchListener;
    }

    @Override // com.amap.api.services.interfaces.IRouteSearch
    public final WalkRouteResult calculateWalkRoute(RouteSearch.WalkRouteQuery walkRouteQuery) throws AMapException {
        try {
            fy.a(this.d);
            if (walkRouteQuery == null) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            if (!a(walkRouteQuery.getFromAndTo())) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            gr.a().b(walkRouteQuery.getFromAndTo());
            RouteSearch.WalkRouteQuery walkRouteQueryClone = walkRouteQuery.m63clone();
            WalkRouteResult walkRouteResultD = new hb(this.d, walkRouteQueryClone).d();
            if (walkRouteResultD != null) {
                walkRouteResultD.setWalkQuery(walkRouteQueryClone);
            }
            return walkRouteResultD;
        } catch (AMapException e) {
            fp.a(e, "RouteSearch", "calculateWalkRoute");
            throw e;
        }
    }

    @Override // com.amap.api.services.interfaces.IRouteSearch
    public final void calculateWalkRouteAsyn(final RouteSearch.WalkRouteQuery walkRouteQuery) {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.ht.1
                @Override // java.lang.Runnable
                public final void run() {
                    Message messageObtainMessage = ga.a().obtainMessage();
                    messageObtainMessage.what = 102;
                    messageObtainMessage.arg1 = 1;
                    Bundle bundle = new Bundle();
                    WalkRouteResult walkRouteResultCalculateWalkRoute = null;
                    try {
                        try {
                            walkRouteResultCalculateWalkRoute = ht.this.calculateWalkRoute(walkRouteQuery);
                            bundle.putInt("errorCode", 1000);
                        } catch (AMapException e) {
                            bundle.putInt("errorCode", e.getErrorCode());
                        }
                    } finally {
                        messageObtainMessage.obj = ht.this.a;
                        bundle.putParcelable("result", walkRouteResultCalculateWalkRoute);
                        messageObtainMessage.setData(bundle);
                        ht.this.e.sendMessage(messageObtainMessage);
                    }
                }
            });
        } catch (Throwable th) {
            fp.a(th, "RouteSearch", "calculateWalkRouteAsyn");
        }
    }

    @Override // com.amap.api.services.interfaces.IRouteSearch
    public final BusRouteResult calculateBusRoute(RouteSearch.BusRouteQuery busRouteQuery) throws AMapException {
        try {
            fy.a(this.d);
            if (busRouteQuery == null) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            if (!a(busRouteQuery.getFromAndTo())) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            RouteSearch.BusRouteQuery busRouteQueryClone = busRouteQuery.m57clone();
            BusRouteResult busRouteResultD = new fi(this.d, busRouteQueryClone).d();
            if (busRouteResultD != null) {
                busRouteResultD.setBusQuery(busRouteQueryClone);
            }
            return busRouteResultD;
        } catch (AMapException e) {
            fp.a(e, "RouteSearch", "calculateBusRoute");
            throw e;
        }
    }

    @Override // com.amap.api.services.interfaces.IRouteSearch
    public final void calculateBusRouteAsyn(final RouteSearch.BusRouteQuery busRouteQuery) {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.ht.2
                @Override // java.lang.Runnable
                public final void run() {
                    Message messageObtainMessage = ga.a().obtainMessage();
                    messageObtainMessage.what = 100;
                    messageObtainMessage.arg1 = 1;
                    Bundle bundle = new Bundle();
                    BusRouteResult busRouteResultCalculateBusRoute = null;
                    try {
                        try {
                            busRouteResultCalculateBusRoute = ht.this.calculateBusRoute(busRouteQuery);
                            bundle.putInt("errorCode", 1000);
                        } catch (AMapException e) {
                            bundle.putInt("errorCode", e.getErrorCode());
                        }
                    } finally {
                        messageObtainMessage.obj = ht.this.a;
                        bundle.putParcelable("result", busRouteResultCalculateBusRoute);
                        messageObtainMessage.setData(bundle);
                        ht.this.e.sendMessage(messageObtainMessage);
                    }
                }
            });
        } catch (Throwable th) {
            fp.a(th, "RouteSearch", "calculateBusRouteAsyn");
        }
    }

    @Override // com.amap.api.services.interfaces.IRouteSearch
    public final DriveRouteResult calculateDriveRoute(RouteSearch.DriveRouteQuery driveRouteQuery) throws AMapException {
        try {
            fy.a(this.d);
            if (driveRouteQuery == null) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            if (!a(driveRouteQuery.getFromAndTo())) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            gr.a().a(driveRouteQuery.getPassedByPoints());
            gr.a().c(driveRouteQuery.getAvoidpolygons());
            RouteSearch.DriveRouteQuery driveRouteQueryClone = driveRouteQuery.m59clone();
            DriveRouteResult driveRouteResultD = new ft(this.d, driveRouteQueryClone).d();
            if (driveRouteResultD != null) {
                driveRouteResultD.setDriveQuery(driveRouteQueryClone);
            }
            return driveRouteResultD;
        } catch (AMapException e) {
            fp.a(e, "RouteSearch", "calculateDriveRoute");
            throw e;
        }
    }

    @Override // com.amap.api.services.interfaces.IRouteSearch
    public final void calculateDriveRouteAsyn(final RouteSearch.DriveRouteQuery driveRouteQuery) {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.ht.3
                @Override // java.lang.Runnable
                public final void run() {
                    Message messageObtainMessage = ga.a().obtainMessage();
                    messageObtainMessage.what = 101;
                    messageObtainMessage.arg1 = 1;
                    Bundle bundle = new Bundle();
                    DriveRouteResult driveRouteResultCalculateDriveRoute = null;
                    try {
                        try {
                            driveRouteResultCalculateDriveRoute = ht.this.calculateDriveRoute(driveRouteQuery);
                            bundle.putInt("errorCode", 1000);
                        } catch (AMapException e) {
                            bundle.putInt("errorCode", e.getErrorCode());
                        }
                    } finally {
                        messageObtainMessage.obj = ht.this.a;
                        bundle.putParcelable("result", driveRouteResultCalculateDriveRoute);
                        messageObtainMessage.setData(bundle);
                        ht.this.e.sendMessage(messageObtainMessage);
                    }
                }
            });
        } catch (Throwable th) {
            fp.a(th, "RouteSearch", "calculateDriveRouteAsyn");
        }
    }

    private static boolean a(RouteSearch.FromAndTo fromAndTo) {
        return (fromAndTo == null || fromAndTo.getFrom() == null || fromAndTo.getTo() == null) ? false : true;
    }

    @Override // com.amap.api.services.interfaces.IRouteSearch
    public final void calculateRideRouteAsyn(final RouteSearch.RideRouteQuery rideRouteQuery) {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.ht.4
                @Override // java.lang.Runnable
                public final void run() {
                    Message messageObtainMessage = ga.a().obtainMessage();
                    messageObtainMessage.what = 103;
                    messageObtainMessage.arg1 = 1;
                    Bundle bundle = new Bundle();
                    RideRouteResult rideRouteResultCalculateRideRoute = null;
                    try {
                        try {
                            rideRouteResultCalculateRideRoute = ht.this.calculateRideRoute(rideRouteQuery);
                            bundle.putInt("errorCode", 1000);
                        } catch (AMapException e) {
                            bundle.putInt("errorCode", e.getErrorCode());
                        }
                    } finally {
                        messageObtainMessage.obj = ht.this.a;
                        bundle.putParcelable("result", rideRouteResultCalculateRideRoute);
                        messageObtainMessage.setData(bundle);
                        ht.this.e.sendMessage(messageObtainMessage);
                    }
                }
            });
        } catch (Throwable th) {
            fp.a(th, "RouteSearch", "calculateRideRouteAsyn");
        }
    }

    @Override // com.amap.api.services.interfaces.IRouteSearch
    public final RideRouteResult calculateRideRoute(RouteSearch.RideRouteQuery rideRouteQuery) throws AMapException {
        try {
            fy.a(this.d);
            if (rideRouteQuery == null) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            if (!a(rideRouteQuery.getFromAndTo())) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            gr.a().a(rideRouteQuery.getFromAndTo());
            RouteSearch.RideRouteQuery rideRouteQueryClone = rideRouteQuery.m61clone();
            RideRouteResult rideRouteResultD = new gt(this.d, rideRouteQueryClone).d();
            if (rideRouteResultD != null) {
                rideRouteResultD.setRideQuery(rideRouteQueryClone);
            }
            return rideRouteResultD;
        } catch (AMapException e) {
            fp.a(e, "RouteSearch", "calculaterideRoute");
            throw e;
        }
    }

    @Override // com.amap.api.services.interfaces.IRouteSearch
    public final TruckRouteRestult calculateTruckRoute(RouteSearch.TruckRouteQuery truckRouteQuery) throws AMapException {
        try {
            fy.a(this.d);
            if (truckRouteQuery == null) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            if (!a(truckRouteQuery.getFromAndTo())) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            gr.a().a(truckRouteQuery.getFromAndTo(), truckRouteQuery.getPassedByPoints());
            gr.a();
            gr.b(truckRouteQuery.getPassedByPoints());
            RouteSearch.TruckRouteQuery truckRouteQueryClone = truckRouteQuery.m62clone();
            TruckRouteRestult truckRouteRestultD = new ha(this.d, truckRouteQueryClone).d();
            if (truckRouteRestultD != null) {
                truckRouteRestultD.setTruckQuery(truckRouteQueryClone);
            }
            return truckRouteRestultD;
        } catch (AMapException e) {
            fp.a(e, "RouteSearch", "calculateDriveRoute");
            throw e;
        }
    }

    @Override // com.amap.api.services.interfaces.IRouteSearch
    public final void calculateTruckRouteAsyn(final RouteSearch.TruckRouteQuery truckRouteQuery) {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.ht.5
                @Override // java.lang.Runnable
                public final void run() {
                    Message messageObtainMessage = ga.a().obtainMessage();
                    messageObtainMessage.what = 104;
                    messageObtainMessage.arg1 = 17;
                    Bundle bundle = new Bundle();
                    TruckRouteRestult truckRouteRestultCalculateTruckRoute = null;
                    try {
                        try {
                            truckRouteRestultCalculateTruckRoute = ht.this.calculateTruckRoute(truckRouteQuery);
                            bundle.putInt("errorCode", 1000);
                        } catch (AMapException e) {
                            bundle.putInt("errorCode", e.getErrorCode());
                        }
                    } finally {
                        messageObtainMessage.obj = ht.this.b;
                        bundle.putParcelable("result", truckRouteRestultCalculateTruckRoute);
                        messageObtainMessage.setData(bundle);
                        ht.this.e.sendMessage(messageObtainMessage);
                    }
                }
            });
        } catch (Throwable th) {
            fp.a(th, "RouteSearch", "calculateTruckRouteAsyn");
        }
    }

    @Override // com.amap.api.services.interfaces.IRouteSearch
    public final DriveRoutePlanResult calculateDrivePlan(RouteSearch.DrivePlanQuery drivePlanQuery) throws AMapException {
        try {
            fy.a(this.d);
            if (drivePlanQuery == null) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            if (!a(drivePlanQuery.getFromAndTo())) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            DriveRoutePlanResult driveRoutePlanResultD = new fs(this.d, drivePlanQuery.m58clone()).d();
            if (driveRoutePlanResultD != null) {
                driveRoutePlanResultD.setDrivePlanQuery(drivePlanQuery);
            }
            return driveRoutePlanResultD;
        } catch (AMapException e) {
            fp.a(e, "RouteSearch", "calculateDrivePlan");
            throw e;
        }
    }

    @Override // com.amap.api.services.interfaces.IRouteSearch
    public final void calculateDrivePlanAsyn(final RouteSearch.DrivePlanQuery drivePlanQuery) {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.ht.6
                @Override // java.lang.Runnable
                public final void run() {
                    Message messageObtainMessage = ga.a().obtainMessage();
                    messageObtainMessage.what = 105;
                    messageObtainMessage.arg1 = 18;
                    Bundle bundle = new Bundle();
                    DriveRoutePlanResult driveRoutePlanResultCalculateDrivePlan = null;
                    try {
                        try {
                            driveRoutePlanResultCalculateDrivePlan = ht.this.calculateDrivePlan(drivePlanQuery);
                            bundle.putInt("errorCode", 1000);
                        } catch (AMapException e) {
                            bundle.putInt("errorCode", e.getErrorCode());
                        }
                    } finally {
                        messageObtainMessage.obj = ht.this.c;
                        bundle.putParcelable("result", driveRoutePlanResultCalculateDrivePlan);
                        messageObtainMessage.setData(bundle);
                        ht.this.e.sendMessage(messageObtainMessage);
                    }
                }
            });
        } catch (Throwable th) {
            fp.a(th, "RouteSearch", "calculateTruckRouteAsyn");
        }
    }
}
