package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.amap.api.col.p0003sl.ip;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.interfaces.IRouteSearchV2;
import com.amap.api.services.route.BusRouteResultV2;
import com.amap.api.services.route.DriveRouteResultV2;
import com.amap.api.services.route.RideRouteResultV2;
import com.amap.api.services.route.RouteSearchV2;
import com.amap.api.services.route.WalkRouteResultV2;

/* JADX INFO: compiled from: RouteSearchCoreV2.java */
/* JADX INFO: loaded from: classes2.dex */
public final class hu implements IRouteSearchV2 {
    private RouteSearchV2.OnRouteSearchListener a;
    private Context b;
    private Handler c;

    public hu(Context context) throws AMapException {
        iq iqVarA = ip.a(context, fo.a(false));
        if (iqVarA.a != ip.c.SuccessCode) {
            throw new AMapException(iqVarA.b, 1, iqVarA.b, iqVarA.a.a());
        }
        this.b = context.getApplicationContext();
        this.c = ga.a();
    }

    private static boolean a(RouteSearchV2.FromAndTo fromAndTo) {
        return (fromAndTo == null || fromAndTo.getFrom() == null || fromAndTo.getTo() == null) ? false : true;
    }

    @Override // com.amap.api.services.interfaces.IRouteSearchV2
    public final void setRouteSearchListener(RouteSearchV2.OnRouteSearchListener onRouteSearchListener) {
        this.a = onRouteSearchListener;
    }

    @Override // com.amap.api.services.interfaces.IRouteSearchV2
    public final DriveRouteResultV2 calculateDriveRoute(RouteSearchV2.DriveRouteQuery driveRouteQuery) throws AMapException {
        try {
            fy.a(this.b);
            if (driveRouteQuery == null) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            if (!a(driveRouteQuery.getFromAndTo())) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            gr.a();
            gr.b(driveRouteQuery.getPassedByPoints());
            gr.a().c(driveRouteQuery.getAvoidpolygons());
            RouteSearchV2.DriveRouteQuery driveRouteQueryClone = driveRouteQuery.m65clone();
            DriveRouteResultV2 driveRouteResultV2D = new fu(this.b, driveRouteQueryClone).d();
            if (driveRouteResultV2D != null) {
                driveRouteResultV2D.setDriveQuery(driveRouteQueryClone);
            }
            return driveRouteResultV2D;
        } catch (AMapException e) {
            fp.a(e, "RouteSearch", "calculateDriveRoute");
            throw e;
        }
    }

    @Override // com.amap.api.services.interfaces.IRouteSearchV2
    public final void calculateDriveRouteAsyn(final RouteSearchV2.DriveRouteQuery driveRouteQuery) {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.hu.1
                @Override // java.lang.Runnable
                public final void run() {
                    Message messageObtainMessage = ga.a().obtainMessage();
                    messageObtainMessage.what = 101;
                    messageObtainMessage.arg1 = 101;
                    Bundle bundle = new Bundle();
                    DriveRouteResultV2 driveRouteResultV2CalculateDriveRoute = null;
                    try {
                        try {
                            driveRouteResultV2CalculateDriveRoute = hu.this.calculateDriveRoute(driveRouteQuery);
                            bundle.putInt("errorCode", 1000);
                        } catch (AMapException e) {
                            bundle.putInt("errorCode", e.getErrorCode());
                        }
                    } finally {
                        messageObtainMessage.obj = hu.this.a;
                        bundle.putParcelable("result", driveRouteResultV2CalculateDriveRoute);
                        messageObtainMessage.setData(bundle);
                        hu.this.c.sendMessage(messageObtainMessage);
                    }
                }
            });
        } catch (Throwable th) {
            fp.a(th, "RouteSearch", "calculateDriveRouteAsyn");
        }
    }

    @Override // com.amap.api.services.interfaces.IRouteSearchV2
    public final WalkRouteResultV2 calculateWalkRoute(RouteSearchV2.WalkRouteQuery walkRouteQuery) throws AMapException {
        try {
            fy.a(this.b);
            if (walkRouteQuery == null) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            if (!a(walkRouteQuery.getFromAndTo())) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            gr.a().b(walkRouteQuery.getFromAndTo());
            RouteSearchV2.WalkRouteQuery walkRouteQueryClone = walkRouteQuery.m68clone();
            WalkRouteResultV2 walkRouteResultV2D = new hc(this.b, walkRouteQueryClone).d();
            if (walkRouteResultV2D != null) {
                walkRouteResultV2D.setWalkQuery(walkRouteQueryClone);
            }
            return walkRouteResultV2D;
        } catch (AMapException e) {
            fp.a(e, "RouteSearch", "calculateWalkRoute");
            throw e;
        }
    }

    @Override // com.amap.api.services.interfaces.IRouteSearchV2
    public final void calculateWalkRouteAsyn(final RouteSearchV2.WalkRouteQuery walkRouteQuery) {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.hu.2
                @Override // java.lang.Runnable
                public final void run() {
                    Message messageObtainMessage = ga.a().obtainMessage();
                    messageObtainMessage.what = 102;
                    messageObtainMessage.arg1 = 101;
                    Bundle bundle = new Bundle();
                    WalkRouteResultV2 walkRouteResultV2CalculateWalkRoute = null;
                    try {
                        try {
                            walkRouteResultV2CalculateWalkRoute = hu.this.calculateWalkRoute(walkRouteQuery);
                            bundle.putInt("errorCode", 1000);
                        } catch (AMapException e) {
                            bundle.putInt("errorCode", e.getErrorCode());
                        }
                    } finally {
                        messageObtainMessage.obj = hu.this.a;
                        bundle.putParcelable("result", walkRouteResultV2CalculateWalkRoute);
                        messageObtainMessage.setData(bundle);
                        hu.this.c.sendMessage(messageObtainMessage);
                    }
                }
            });
        } catch (Throwable th) {
            fp.a(th, "RouteSearch", "calculateWalkRouteAsyn");
        }
    }

    @Override // com.amap.api.services.interfaces.IRouteSearchV2
    public final RideRouteResultV2 calculateRideRoute(RouteSearchV2.RideRouteQuery rideRouteQuery) throws AMapException {
        try {
            fy.a(this.b);
            if (rideRouteQuery == null) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            if (!a(rideRouteQuery.getFromAndTo())) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            gr.a().a(rideRouteQuery.getFromAndTo());
            RouteSearchV2.RideRouteQuery rideRouteQueryClone = rideRouteQuery.m67clone();
            RideRouteResultV2 rideRouteResultV2D = new gu(this.b, rideRouteQueryClone).d();
            if (rideRouteResultV2D != null) {
                rideRouteResultV2D.setRideQuery(rideRouteQueryClone);
            }
            return rideRouteResultV2D;
        } catch (AMapException e) {
            fp.a(e, "RouteSearch", "calculaterideRoute");
            throw e;
        }
    }

    @Override // com.amap.api.services.interfaces.IRouteSearchV2
    public final void calculateRideRouteAsyn(final RouteSearchV2.RideRouteQuery rideRouteQuery) {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.hu.3
                @Override // java.lang.Runnable
                public final void run() {
                    Message messageObtainMessage = ga.a().obtainMessage();
                    messageObtainMessage.what = 103;
                    messageObtainMessage.arg1 = 101;
                    Bundle bundle = new Bundle();
                    RideRouteResultV2 rideRouteResultV2CalculateRideRoute = null;
                    try {
                        try {
                            rideRouteResultV2CalculateRideRoute = hu.this.calculateRideRoute(rideRouteQuery);
                            bundle.putInt("errorCode", 1000);
                        } catch (AMapException e) {
                            bundle.putInt("errorCode", e.getErrorCode());
                        }
                    } finally {
                        messageObtainMessage.obj = hu.this.a;
                        bundle.putParcelable("result", rideRouteResultV2CalculateRideRoute);
                        messageObtainMessage.setData(bundle);
                        hu.this.c.sendMessage(messageObtainMessage);
                    }
                }
            });
        } catch (Throwable th) {
            fp.a(th, "RouteSearch", "calculateRideRouteAsyn");
        }
    }

    @Override // com.amap.api.services.interfaces.IRouteSearchV2
    public final BusRouteResultV2 calculateBusRoute(RouteSearchV2.BusRouteQuery busRouteQuery) throws AMapException {
        try {
            fy.a(this.b);
            if (busRouteQuery == null) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            if (!a(busRouteQuery.getFromAndTo())) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            RouteSearchV2.BusRouteQuery busRouteQueryClone = busRouteQuery.m64clone();
            BusRouteResultV2 busRouteResultV2D = new fj(this.b, busRouteQueryClone).d();
            if (busRouteResultV2D != null) {
                busRouteResultV2D.setBusQuery(busRouteQueryClone);
            }
            return busRouteResultV2D;
        } catch (AMapException e) {
            fp.a(e, "RouteSearch", "calculateBusRoute");
            throw e;
        }
    }

    @Override // com.amap.api.services.interfaces.IRouteSearchV2
    public final void calculateBusRouteAsyn(final RouteSearchV2.BusRouteQuery busRouteQuery) {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.hu.4
                @Override // java.lang.Runnable
                public final void run() {
                    Message messageObtainMessage = ga.a().obtainMessage();
                    messageObtainMessage.what = 100;
                    messageObtainMessage.arg1 = 101;
                    Bundle bundle = new Bundle();
                    BusRouteResultV2 busRouteResultV2CalculateBusRoute = null;
                    try {
                        try {
                            busRouteResultV2CalculateBusRoute = hu.this.calculateBusRoute(busRouteQuery);
                            bundle.putInt("errorCode", 1000);
                        } catch (AMapException e) {
                            bundle.putInt("errorCode", e.getErrorCode());
                        }
                    } finally {
                        messageObtainMessage.obj = hu.this.a;
                        bundle.putParcelable("result", busRouteResultV2CalculateBusRoute);
                        messageObtainMessage.setData(bundle);
                        hu.this.c.sendMessage(messageObtainMessage);
                    }
                }
            });
        } catch (Throwable th) {
            fp.a(th, "RouteSearch", "calculateBusRouteAsyn");
        }
    }
}
