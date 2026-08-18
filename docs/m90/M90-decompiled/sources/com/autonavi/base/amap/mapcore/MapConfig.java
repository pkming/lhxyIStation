package com.autonavi.base.amap.mapcore;

import android.opengl.Matrix;
import com.amap.api.maps.model.LatLngBounds;
import com.autonavi.amap.mapcore.DPoint;
import com.autonavi.amap.mapcore.IPoint;
import com.autonavi.amap.mapcore.interfaces.IMapConfig;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes2.dex */
public class MapConfig implements IMapConfig, Cloneable {
    public static final int DEFAULT_RATIO = 1;
    private static final int GEO_POINT_ZOOM = 20;
    public static final float MAX_ZOOM = 20.0f;
    public static final float MAX_ZOOM_INDOOR = 20.0f;
    public static final float MIN_ZOOM = 3.0f;
    public static final int MSG_ACTION_MAINTHREAD_TRIGGER = 30;
    public static final int MSG_ACTION_ONBASEPOICLICK = 20;
    public static final int MSG_ACTION_ONMAPCLICK = 19;
    public static final int MSG_AUTH_FAILURE = 2;
    public static final int MSG_CALLBACK_MAPLOADED = 16;
    public static final int MSG_CALLBACK_ONTOUCHEVENT = 14;
    public static final int MSG_CALLBACK_SCREENSHOT = 15;
    public static final int MSG_CAMERAUPDATE_CHANGE = 10;
    public static final int MSG_CAMERAUPDATE_FINISH = 11;
    public static final int MSG_COMPASSVIEW_CHANGESTATE = 13;
    public static final int MSG_INFOWINDOW_UPDATE = 18;
    public static final int MSG_TILEOVERLAY_REFRESH = 17;
    public static final int MSG_ZOOMVIEW_CHANGESTATE = 12;
    private static final int TILE_SIZE_POW = 8;
    private String customTextureResourcePath;
    private boolean isSetLimitZoomLevel;
    MapConfig lastMapconfig;
    private IPoint[] limitIPoints;
    private LatLngBounds limitLatLngBounds;
    private String mCustomStyleID;
    private String mCustomStylePath;
    private int mapHeight;
    private float mapPerPixelUnitLength;
    private int mapWidth;
    private float skyHeight;
    public float maxZoomLevel = 20.0f;
    public float minZoomLevel = 3.0f;
    private FPoint[] mapRect = null;
    private Rectangle geoRectangle = new Rectangle();
    private boolean isIndoorEnable = false;
    private boolean isBuildingEnable = true;
    private boolean isMapTextEnable = true;
    private boolean isTrafficEnabled = false;
    private boolean isCustomStyleEnabled = false;
    private double sX = 2.21010267E8d;
    private double sY = 1.01697799E8d;
    private DPoint mapGeoCenter = new DPoint(this.sX, this.sY);
    private float sZ = 10.0f;
    private float sC = 0.0f;
    private float sR = 0.0f;
    private boolean isCenterChanged = false;
    private boolean isZoomChanged = false;
    private boolean isTiltChanged = false;
    private boolean isBearingChanged = false;
    private boolean isNeedUpdateZoomControllerState = false;
    private boolean isNeedUpdateMapRectNextFrame = false;
    private int mMapStyleMode = 0;
    private int mMapStyleTime = 0;
    private int anchorX = 0;
    private String mMapLanguage = "zh_cn";
    private boolean isHideLogoEnable = false;
    private boolean isWorldMapEnable = false;
    private boolean isTouchPoiEnable = true;
    private int abroadState = 1;
    private boolean isAbroadEnable = false;
    private boolean isTerrainEnable = false;
    float[] viewMatrix = new float[16];
    float[] projectionMatrix = new float[16];
    float[] mvpMatrix = new float[16];
    private int[] tilsIDs = new int[100];
    private boolean mapEnable = true;
    private int anchorY = 0;
    private boolean isProFunctionAuthEnable = true;
    private boolean isUseProFunction = false;
    private int customBackgroundColor = -1;
    private float mapZoomScale = 1.0f;
    private AtomicInteger changedCounter = new AtomicInteger(0);
    private volatile double changeRatio = 1.0d;
    private volatile double changeGridRatio = 1.0d;
    private int gridX = 0;
    private int gridY = 0;

    @Override // com.autonavi.amap.mapcore.interfaces.IMapConfig
    public int getAnchorY() {
        return this.anchorY;
    }

    public void setAnchorY(int i) {
        this.anchorY = i;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapConfig
    public int getAnchorX() {
        return this.anchorX;
    }

    public void setAnchorX(int i) {
        this.anchorX = i;
    }

    public MapConfig(boolean z) {
        this.lastMapconfig = null;
        if (z) {
            MapConfig mapConfig = new MapConfig(false);
            this.lastMapconfig = mapConfig;
            mapConfig.setGridXY(0, 0);
            this.lastMapconfig.setSX(0.0d);
            this.lastMapconfig.setSY(0.0d);
            this.lastMapconfig.setSZ(0.0f);
            this.lastMapconfig.setSC(0.0f);
            this.lastMapconfig.setSR(0.0f);
        }
    }

    public int getChangedCounter() {
        return this.changedCounter.get();
    }

    public void resetChangedCounter() {
        this.changedCounter.set(0);
    }

    public void addChangedCounter() {
        this.changedCounter.incrementAndGet();
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x005b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean isMapStateChange() {
        /*
            r14 = this;
            com.autonavi.base.amap.mapcore.MapConfig r0 = r14.lastMapconfig
            r1 = 0
            if (r0 == 0) goto L96
            double r2 = r0.getSX()
            com.autonavi.base.amap.mapcore.MapConfig r0 = r14.lastMapconfig
            double r4 = r0.getSY()
            com.autonavi.base.amap.mapcore.MapConfig r0 = r14.lastMapconfig
            float r0 = r0.getSZ()
            com.autonavi.base.amap.mapcore.MapConfig r6 = r14.lastMapconfig
            float r6 = r6.getSC()
            com.autonavi.base.amap.mapcore.MapConfig r7 = r14.lastMapconfig
            float r7 = r7.getSR()
            double r8 = r14.sX
            int r2 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            r3 = 1
            if (r2 == 0) goto L2a
            r2 = r3
            goto L2b
        L2a:
            r2 = r1
        L2b:
            r14.isCenterChanged = r2
            double r10 = r14.sY
            int r4 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r4 == 0) goto L34
            r2 = r3
        L34:
            r14.isCenterChanged = r2
            float r4 = r14.sZ
            int r5 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r5 == 0) goto L3e
            r5 = r3
            goto L3f
        L3e:
            r5 = r1
        L3f:
            r14.isZoomChanged = r5
            if (r5 == 0) goto L5d
            float r12 = r14.minZoomLevel
            int r13 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r13 <= 0) goto L5b
            int r12 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
            if (r12 <= 0) goto L5b
            float r12 = r14.maxZoomLevel
            int r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r0 >= 0) goto L5b
            int r0 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
            if (r0 < 0) goto L58
            goto L5b
        L58:
            r14.isNeedUpdateZoomControllerState = r1
            goto L5d
        L5b:
            r14.isNeedUpdateZoomControllerState = r3
        L5d:
            float r0 = r14.sC
            int r0 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r0 == 0) goto L65
            r0 = r3
            goto L66
        L65:
            r0 = r1
        L66:
            r14.isTiltChanged = r0
            float r6 = r14.sR
            int r6 = (r7 > r6 ? 1 : (r7 == r6 ? 0 : -1))
            if (r6 == 0) goto L70
            r6 = r3
            goto L71
        L70:
            r6 = r1
        L71:
            r14.isBearingChanged = r6
            if (r2 != 0) goto L81
            if (r5 != 0) goto L81
            if (r0 != 0) goto L81
            if (r6 != 0) goto L81
            boolean r0 = r14.isNeedUpdateMapRectNextFrame
            if (r0 == 0) goto L80
            goto L81
        L80:
            r3 = r1
        L81:
            if (r3 == 0) goto L95
            r14.isNeedUpdateMapRectNextFrame = r1
            int r0 = (int) r4
            int r1 = (int) r8
            int r0 = 20 - r0
            int r0 = r0 + 8
            int r1 = r1 >> r0
            int r2 = (int) r10
            int r0 = r2 >> r0
            r14.setGridXY(r1, r0)
            r14.changeRatio()
        L95:
            r1 = r3
        L96:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.autonavi.base.amap.mapcore.MapConfig.isMapStateChange():boolean");
    }

    protected void setGridXY(int i, int i2) {
        MapConfig mapConfig = this.lastMapconfig;
        if (mapConfig != null) {
            mapConfig.setGridXY(this.gridX, this.gridY);
        }
        this.gridX = i;
        this.gridY = i2;
    }

    protected int getGridX() {
        return this.gridX;
    }

    protected int getGridY() {
        return this.gridY;
    }

    public double getChangeRatio() {
        return this.changeRatio;
    }

    public double getChangeGridRatio() {
        return this.changeGridRatio;
    }

    private void changeRatio() {
        double sx = this.lastMapconfig.getSX();
        double sy = this.lastMapconfig.getSY();
        float sz = this.lastMapconfig.getSZ();
        float sc = this.lastMapconfig.getSC();
        float sr = this.lastMapconfig.getSR();
        this.changeRatio = Math.abs(this.sX - sx) + Math.abs(this.sY - sy);
        this.changeRatio = this.changeRatio == 0.0d ? 1.0d : this.changeRatio * 2.0d;
        this.changeRatio = this.changeRatio * (sz == this.sZ ? 1.0d : Math.abs(sz - r11));
        float f = this.sC;
        float fAbs = sc == f ? 1.0f : Math.abs(sc - f);
        float f2 = this.sR;
        float fAbs2 = sr != f2 ? Math.abs(sr - f2) : 1.0f;
        double d = fAbs;
        this.changeRatio *= d;
        double d2 = fAbs2;
        this.changeRatio *= d2;
        this.changeGridRatio = Math.abs(this.lastMapconfig.getGridX() - this.gridX) + (this.lastMapconfig.getGridY() - this.gridY);
        this.changeGridRatio = this.changeGridRatio != 0.0d ? this.changeGridRatio * 2.0d : 1.0d;
        this.changeGridRatio *= d;
        this.changeGridRatio *= d2;
    }

    public String toString() {
        return " sX: " + this.sX + " sY: " + this.sY + " sZ: " + this.sZ + " sC: " + this.sC + " sR: " + this.sR + " skyHeight: " + this.skyHeight;
    }

    public boolean isZoomChanged() {
        return this.isZoomChanged;
    }

    public boolean isTiltChanged() {
        return this.isTiltChanged;
    }

    public boolean isBearingChanged() {
        return this.isBearingChanged;
    }

    public boolean isIndoorEnable() {
        return this.isIndoorEnable;
    }

    public void setIndoorEnable(boolean z) {
        this.isIndoorEnable = z;
    }

    public boolean isBuildingEnable() {
        return this.isBuildingEnable;
    }

    public void setBuildingEnable(boolean z) {
        this.isBuildingEnable = z;
    }

    public boolean isMapTextEnable() {
        return this.isMapTextEnable;
    }

    public void setMapTextEnable(boolean z) {
        this.isMapTextEnable = z;
    }

    public boolean isTrafficEnabled() {
        return this.isTrafficEnabled;
    }

    public void setTrafficEnabled(boolean z) {
        this.isTrafficEnabled = z;
    }

    public boolean isNeedUpdateZoomControllerState() {
        return this.isNeedUpdateZoomControllerState;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapConfig
    public double getSX() {
        return this.sX;
    }

    public void setSX(double d) {
        MapConfig mapConfig = this.lastMapconfig;
        if (mapConfig != null) {
            mapConfig.setSX(this.sX);
        }
        this.sX = d;
        this.mapGeoCenter.x = d;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapConfig
    public double getSY() {
        return this.sY;
    }

    public void setSY(double d) {
        MapConfig mapConfig = this.lastMapconfig;
        if (mapConfig != null) {
            mapConfig.setSY(this.sY);
        }
        this.sY = d;
        this.mapGeoCenter.x = d;
    }

    public DPoint getMapGeoCenter() {
        return this.mapGeoCenter;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapConfig
    public float getSZ() {
        return this.sZ;
    }

    public void setSZ(float f) {
        MapConfig mapConfig = this.lastMapconfig;
        if (mapConfig != null) {
            mapConfig.setSZ(this.sZ);
        }
        this.sZ = f;
    }

    public float getSC() {
        return this.sC;
    }

    public void setSC(float f) {
        MapConfig mapConfig = this.lastMapconfig;
        if (mapConfig != null) {
            mapConfig.setSC(this.sC);
        }
        this.sC = f;
    }

    public float getSR() {
        return this.sR;
    }

    public void setSR(float f) {
        MapConfig mapConfig = this.lastMapconfig;
        if (mapConfig != null) {
            mapConfig.setSR(this.sR);
        }
        this.sR = f;
    }

    public Rectangle getGeoRectangle() {
        return this.geoRectangle;
    }

    public void setMaxZoomLevel(float f) {
        if (f > 20.0f) {
            f = 20.0f;
        }
        if (f < 3.0f) {
            f = 3.0f;
        }
        if (f < getMinZoomLevel()) {
            f = getMinZoomLevel();
        }
        this.isSetLimitZoomLevel = true;
        this.maxZoomLevel = f;
    }

    public void setMinZoomLevel(float f) {
        if (f < 3.0f) {
            f = 3.0f;
        }
        if (f > 20.0f) {
            f = 20.0f;
        }
        if (f > getMaxZoomLevel()) {
            f = getMaxZoomLevel();
        }
        this.isSetLimitZoomLevel = true;
        this.minZoomLevel = f;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapConfig
    public float getMaxZoomLevel() {
        return this.maxZoomLevel;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapConfig
    public float getMinZoomLevel() {
        return this.minZoomLevel;
    }

    public IPoint[] getLimitIPoints() {
        return this.limitIPoints;
    }

    public void setLimitIPoints(IPoint[] iPointArr) {
        this.limitIPoints = iPointArr;
    }

    public boolean isSetLimitZoomLevel() {
        return this.isSetLimitZoomLevel;
    }

    public LatLngBounds getLimitLatLngBounds() {
        return this.limitLatLngBounds;
    }

    public void setLimitLatLngBounds(LatLngBounds latLngBounds) {
        this.limitLatLngBounds = latLngBounds;
        if (latLngBounds == null) {
            resetMinMaxZoomPreference();
        }
    }

    public void resetMinMaxZoomPreference() {
        this.minZoomLevel = 3.0f;
        this.maxZoomLevel = 20.0f;
        this.isSetLimitZoomLevel = false;
    }

    public void updateMapRectNextFrame(boolean z) {
        this.isNeedUpdateMapRectNextFrame = z;
    }

    public void setCustomStylePath(String str) {
        this.mCustomStylePath = str;
    }

    public String getCustomStylePath() {
        return this.mCustomStylePath;
    }

    public String getCustomStyleID() {
        return this.mCustomStyleID;
    }

    public void setCustomStyleID(String str) {
        this.mCustomStyleID = str;
    }

    public void setCustomStyleEnable(boolean z) {
        this.isCustomStyleEnabled = z;
    }

    public boolean isCustomStyleEnable() {
        return this.isCustomStyleEnabled;
    }

    public int getMapStyleTime() {
        return this.mMapStyleTime;
    }

    public void setMapStyleTime(int i) {
        this.mMapStyleTime = i;
    }

    public int getMapStyleMode() {
        return this.mMapStyleMode;
    }

    public void setMapStyleMode(int i) {
        this.mMapStyleMode = i;
    }

    public void setCustomTextureResourcePath(String str) {
        this.customTextureResourcePath = str;
    }

    public String getCustomTextureResourcePath() {
        return this.customTextureResourcePath;
    }

    public boolean isProFunctionAuthEnable() {
        return this.isProFunctionAuthEnable;
    }

    public void setProFunctionAuthEnable(boolean z) {
        this.isProFunctionAuthEnable = z;
    }

    public boolean isUseProFunction() {
        return this.isUseProFunction;
    }

    public void setUseProFunction(boolean z) {
        this.isUseProFunction = z;
    }

    public void setCustomBackgroundColor(int i) {
        this.customBackgroundColor = i;
    }

    public int getCustomBackgroundColor() {
        return this.customBackgroundColor;
    }

    public void setMapZoomScale(float f) {
        this.mapZoomScale = f;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapConfig
    public float getMapZoomScale() {
        return this.mapZoomScale;
    }

    public void setMapWidth(int i) {
        this.mapWidth = i;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapConfig
    public int getMapWidth() {
        return this.mapWidth;
    }

    public void setMapHeight(int i) {
        this.mapHeight = i;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapConfig
    public int getMapHeight() {
        return this.mapHeight;
    }

    public void setMapLanguage(String str) {
        this.mMapLanguage = str;
    }

    public String getMapLanguage() {
        return this.mMapLanguage;
    }

    public void setHideLogoEnble(boolean z) {
        this.isHideLogoEnable = z;
    }

    public boolean isHideLogoEnable() {
        return this.isHideLogoEnable;
    }

    public void setWorldMapEnable(boolean z) {
        this.isWorldMapEnable = z;
    }

    public boolean isWorldMapEnable() {
        return this.isWorldMapEnable;
    }

    public float getSkyHeight() {
        return this.skyHeight;
    }

    public void setSkyHeight(float f) {
        this.skyHeight = f;
    }

    public float[] getViewMatrix() {
        return this.viewMatrix;
    }

    public float[] getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public float[] getMvpMatrix() {
        return this.mvpMatrix;
    }

    public void updateFinalMatrix() {
        Matrix.multiplyMM(this.mvpMatrix, 0, this.projectionMatrix, 0, this.viewMatrix, 0);
    }

    public boolean isTouchPoiEnable() {
        return this.isTouchPoiEnable;
    }

    public void setTouchPoiEnable(boolean z) {
        this.isTouchPoiEnable = z;
    }

    public boolean isMapEnable() {
        return this.mapEnable;
    }

    public void setMapEnable(boolean z) {
        this.mapEnable = z;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapConfig
    public int getAbroadState() {
        return this.abroadState;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapConfig
    public void setAbroadState(int i) {
        this.abroadState = i;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapConfig
    public boolean isAbroadEnable() {
        return this.isAbroadEnable;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapConfig
    public void setAbroadEnable(boolean z) {
        this.isAbroadEnable = z;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapConfig
    public boolean isTerrainEnable() {
        return this.isTerrainEnable;
    }

    @Override // com.autonavi.amap.mapcore.interfaces.IMapConfig
    public void setTerrainEnable(boolean z) {
        this.isTerrainEnable = z;
    }
}
