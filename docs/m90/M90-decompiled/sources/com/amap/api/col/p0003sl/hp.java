package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.ga;
import com.amap.api.col.p0003sl.ip;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.interfaces.INearbySearch;
import com.amap.api.services.nearby.NearbySearch;
import com.amap.api.services.nearby.NearbySearchResult;
import com.amap.api.services.nearby.UploadInfo;
import com.amap.api.services.nearby.UploadInfoCallback;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/* JADX INFO: compiled from: NearbySearchCore.java */
/* JADX INFO: loaded from: classes2.dex */
public final class hp implements INearbySearch {
    private static long e;
    private String b;
    private Context c;
    private ga d;
    private ExecutorService f;
    private UploadInfoCallback k;
    private TimerTask l;
    private List<NearbySearch.NearbyListener> a = new ArrayList();
    private LatLonPoint g = null;
    private String h = null;
    private boolean i = false;
    private Timer j = new Timer();

    public hp(Context context) throws AMapException {
        iq iqVarA = ip.a(context, fo.a(false));
        if (iqVarA.a != ip.c.SuccessCode) {
            throw new AMapException(iqVarA.b, 1, iqVarA.b, iqVarA.a.a());
        }
        this.c = context.getApplicationContext();
        this.d = ga.a();
    }

    @Override // com.amap.api.services.interfaces.INearbySearch
    public final synchronized void addNearbyListener(NearbySearch.NearbyListener nearbyListener) {
        try {
            this.a.add(nearbyListener);
        } catch (Throwable th) {
            fp.a(th, "NearbySearch", "addNearbyListener");
        }
    }

    @Override // com.amap.api.services.interfaces.INearbySearch
    public final synchronized void removeNearbyListener(NearbySearch.NearbyListener nearbyListener) {
        if (nearbyListener == null) {
            return;
        }
        try {
            this.a.remove(nearbyListener);
        } catch (Throwable th) {
            fp.a(th, "NearbySearch", "removeNearbyListener");
        }
    }

    @Override // com.amap.api.services.interfaces.INearbySearch
    public final void clearUserInfoAsyn() {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.hp.1
                @Override // java.lang.Runnable
                public final void run() {
                    Message messageObtainMessage = hp.this.d.obtainMessage();
                    messageObtainMessage.arg1 = 8;
                    messageObtainMessage.obj = hp.this.a;
                    try {
                        try {
                            hp.this.a();
                            messageObtainMessage.what = 1000;
                            if (hp.this.d == null) {
                                return;
                            }
                        } catch (AMapException e2) {
                            messageObtainMessage.what = e2.getErrorCode();
                            fp.a(e2, "NearbySearch", "clearUserInfoAsyn");
                            if (hp.this.d == null) {
                                return;
                            }
                        }
                        hp.this.d.sendMessage(messageObtainMessage);
                    } catch (Throwable th) {
                        if (hp.this.d != null) {
                            hp.this.d.sendMessage(messageObtainMessage);
                        }
                        throw th;
                    }
                }
            });
        } catch (Throwable th) {
            fp.a(th, "NearbySearch", "clearUserInfoAsynThrowable");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int a() throws AMapException {
        try {
            if (this.i) {
                throw new AMapException(AMapException.AMAP_CLIENT_UPLOADAUTO_STARTED_ERROR);
            }
            if (!a(this.b)) {
                throw new AMapException(AMapException.AMAP_CLIENT_USERID_ILLEGAL);
            }
            fy.a(this.c);
            return new gb(this.c, this.b).d().intValue();
        } catch (AMapException e2) {
            throw e2;
        }
    }

    @Override // com.amap.api.services.interfaces.INearbySearch
    public final void setUserID(String str) {
        this.b = str;
    }

    @Override // com.amap.api.services.interfaces.INearbySearch
    public final synchronized void startUploadNearbyInfoAuto(UploadInfoCallback uploadInfoCallback, int i) {
        TimerTask timerTask;
        if (i < 7000) {
            i = 7000;
        }
        try {
            this.k = uploadInfoCallback;
            if (this.i && (timerTask = this.l) != null) {
                timerTask.cancel();
            }
            this.i = true;
            a aVar = new a(this, (byte) 0);
            this.l = aVar;
            this.j.schedule(aVar, 0L, i);
        } catch (Throwable th) {
            fp.a(th, "NearbySearch", "startUploadNearbyInfoAuto");
        }
    }

    @Override // com.amap.api.services.interfaces.INearbySearch
    public final synchronized void stopUploadNearbyInfoAuto() {
        TimerTask timerTask;
        try {
            timerTask = this.l;
        } finally {
        }
        if (timerTask != null) {
            timerTask.cancel();
            this.i = false;
            this.l = null;
        } else {
            this.i = false;
            this.l = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int a(UploadInfo uploadInfo) {
        return this.i ? AMapException.CODE_AMAP_CLIENT_UPLOADAUTO_STARTED_ERROR : b(uploadInfo);
    }

    private static boolean a(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return Pattern.compile("^[a-z0-9A-Z_-]{1,32}$").matcher(str).find();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int b(UploadInfo uploadInfo) {
        try {
            fy.a(this.c);
            if (uploadInfo == null) {
                return AMapException.CODE_AMAP_CLIENT_NEARBY_NULL_RESULT;
            }
            long time = new Date().getTime();
            if (time - e < 6500) {
                return AMapException.CODE_AMAP_CLIENT_UPLOAD_TOO_FREQUENT;
            }
            e = time;
            String userID = uploadInfo.getUserID();
            if (!a(userID)) {
                return AMapException.CODE_AMAP_CLIENT_USERID_ILLEGAL;
            }
            if (TextUtils.isEmpty(this.h)) {
                this.h = userID;
            }
            if (!userID.equals(this.h)) {
                return AMapException.CODE_AMAP_CLIENT_USERID_ILLEGAL;
            }
            LatLonPoint point = uploadInfo.getPoint();
            if (point != null && !point.equals(this.g)) {
                new gd(this.c, uploadInfo).d();
                this.g = point.copy();
                return 1000;
            }
            return AMapException.CODE_AMAP_CLIENT_UPLOAD_LOCATION_ERROR;
        } catch (AMapException e2) {
            return e2.getErrorCode();
        } catch (Throwable unused) {
            return 1900;
        }
    }

    @Override // com.amap.api.services.interfaces.INearbySearch
    public final void uploadNearbyInfoAsyn(final UploadInfo uploadInfo) {
        if (this.f == null) {
            this.f = Executors.newSingleThreadExecutor();
        }
        this.f.submit(new Runnable() { // from class: com.amap.api.col.3sl.hp.2
            @Override // java.lang.Runnable
            public final void run() {
                try {
                    Message messageObtainMessage = hp.this.d.obtainMessage();
                    messageObtainMessage.arg1 = 10;
                    messageObtainMessage.obj = hp.this.a;
                    messageObtainMessage.what = hp.this.a(uploadInfo);
                    hp.this.d.sendMessage(messageObtainMessage);
                } catch (Throwable th) {
                    fp.a(th, "NearbySearch", "uploadNearbyInfoAsyn");
                }
            }
        });
    }

    @Override // com.amap.api.services.interfaces.INearbySearch
    public final void searchNearbyInfoAsyn(final NearbySearch.NearbyQuery nearbyQuery) {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.hp.3
                @Override // java.lang.Runnable
                public final void run() {
                    Message messageObtainMessage = hp.this.d.obtainMessage();
                    messageObtainMessage.arg1 = 9;
                    ga.g gVar = new ga.g();
                    gVar.a = hp.this.a;
                    messageObtainMessage.obj = gVar;
                    try {
                        try {
                            gVar.b = hp.this.searchNearbyInfo(nearbyQuery);
                            messageObtainMessage.what = 1000;
                            if (hp.this.d == null) {
                                return;
                            }
                        } catch (AMapException e2) {
                            messageObtainMessage.what = e2.getErrorCode();
                            fp.a(e2, "NearbySearch", "searchNearbyInfoAsyn");
                            if (hp.this.d == null) {
                                return;
                            }
                        }
                        hp.this.d.sendMessage(messageObtainMessage);
                    } catch (Throwable th) {
                        if (hp.this.d != null) {
                            hp.this.d.sendMessage(messageObtainMessage);
                        }
                        throw th;
                    }
                }
            });
        } catch (Throwable th) {
            fp.a(th, "NearbySearch", "searchNearbyInfoAsynThrowable");
        }
    }

    @Override // com.amap.api.services.interfaces.INearbySearch
    public final NearbySearchResult searchNearbyInfo(NearbySearch.NearbyQuery nearbyQuery) throws AMapException {
        try {
            fy.a(this.c);
            if (!a(nearbyQuery)) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            return new gc(this.c, nearbyQuery).d();
        } catch (AMapException e2) {
            throw e2;
        } catch (Throwable th) {
            fp.a(th, "NearbySearch", "searchNearbyInfo");
            throw new AMapException(AMapException.AMAP_CLIENT_UNKNOWN_ERROR);
        }
    }

    private static boolean a(NearbySearch.NearbyQuery nearbyQuery) {
        return (nearbyQuery == null || nearbyQuery.getCenterPoint() == null) ? false : true;
    }

    @Override // com.amap.api.services.interfaces.INearbySearch
    public final synchronized void destroy() {
        try {
            this.j.cancel();
        } catch (Throwable th) {
            fp.a(th, "NearbySearch", "destryoy");
        }
    }

    /* JADX INFO: compiled from: NearbySearchCore.java */
    private class a extends TimerTask {
        private a() {
        }

        /* synthetic */ a(hp hpVar, byte b) {
            this();
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public final void run() {
            try {
                if (hp.this.k != null) {
                    int iB = hp.this.b(hp.this.k.OnUploadInfoCallback());
                    Message messageObtainMessage = hp.this.d.obtainMessage();
                    messageObtainMessage.arg1 = 10;
                    messageObtainMessage.obj = hp.this.a;
                    messageObtainMessage.what = iB;
                    hp.this.d.sendMessage(messageObtainMessage);
                }
            } catch (Throwable th) {
                fp.a(th, "NearbySearch", "UpdateDataTask");
            }
        }
    }
}
