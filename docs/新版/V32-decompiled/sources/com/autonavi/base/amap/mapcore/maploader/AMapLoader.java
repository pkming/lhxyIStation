package com.autonavi.base.amap.mapcore.maploader;

import android.content.Context;
import android.text.TextUtils;
import android.view.Window;
import com.amap.api.col.p0003sl.db;
import com.amap.api.col.p0003sl.du;
import com.amap.api.col.p0003sl.dx;
import com.amap.api.col.p0003sl.dy;
import com.amap.api.col.p0003sl.dz;
import com.amap.api.col.p0003sl.ig;
import com.amap.api.col.p0003sl.ij;
import com.amap.api.col.p0003sl.ip;
import com.amap.api.col.p0003sl.is;
import com.amap.api.col.p0003sl.jw;
import com.amap.api.col.p0003sl.kw;
import com.amap.api.maps.MapsInitializer;
import com.autonavi.base.ae.gmap.NetworkProxyManager;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/* JADX INFO: loaded from: classes2.dex */
public class AMapLoader implements kw.a {
    private static final int GET_METHOD = 0;
    private static final String NETWORK_RESPONSE_CODE_STRING = "网络异常状态码：";
    private Context context;
    private kw downloadManager;
    private ADataRequestParam mDataRequestParam;
    private boolean mRequestCancel;
    private volatile boolean isCanceled = false;
    private long requestMapDataTimestamp = 0;
    private long requestMapDataPackageSize = 0;

    public static class ADataRequestParam {
        public byte[] enCodeString;
        public long handler;
        public int nCompress;
        public int nRequestType;
        public String requestBaseUrl;
        public String requestUrl;
    }

    public static class AMapGridDownloadRequest extends db {
        private final Context mContext;
        private byte[] postEntityBytes;
        private String sUrl;
        private String userAgent;

        @Override // com.amap.api.col.p0003sl.db, com.amap.api.col.p0003sl.lb
        public Map<String, String> getParams() {
            return null;
        }

        @Override // com.amap.api.col.p0003sl.lb
        public boolean isSupportIPV6() {
            return true;
        }

        public AMapGridDownloadRequest(Context context, String str, String str2) {
            this.mContext = context;
            this.sUrl = str;
            this.userAgent = str2;
        }

        @Override // com.amap.api.col.p0003sl.lb
        public Map<String, String> getRequestHead() {
            is isVarA = dx.a();
            String strB = isVarA != null ? isVarA.b() : null;
            String strF = ig.f(this.mContext);
            try {
                strF = URLEncoder.encode(strF, "UTF-8");
            } catch (Throwable unused) {
            }
            Hashtable hashtable = new Hashtable(16);
            hashtable.put("User-Agent", this.userAgent);
            hashtable.put("platinfo", String.format(Locale.US, "platform=Android&sdkversion=%s&product=%s", strB, "3dmap"));
            hashtable.put("x-INFO", ij.a(this.mContext));
            hashtable.put("key", strF);
            hashtable.put("logversion", "2.1");
            return hashtable;
        }

        @Override // com.amap.api.col.p0003sl.lb
        public String getURL() {
            return this.sUrl;
        }

        @Override // com.amap.api.col.p0003sl.lb
        public String getIPV6URL() {
            return dx.a(getURL());
        }

        public void setPostEntityBytes(byte[] bArr) {
            this.postEntityBytes = bArr;
        }

        @Override // com.amap.api.col.p0003sl.lb
        public byte[] getEntityBytes() {
            return this.postEntityBytes;
        }
    }

    @Override // com.amap.api.col.3sl.kw.a
    public void onDownload(byte[] bArr, long j) {
        if (bArr == null || this.mDataRequestParam == null) {
            return;
        }
        NetworkProxyManager.getInstance().receiveNetData(this.mDataRequestParam.handler, bArr, bArr.length);
    }

    @Override // com.amap.api.col.3sl.kw.a
    public void onStop() {
        if (this.mDataRequestParam != null) {
            NetworkProxyManager.getInstance().netStop(this.mDataRequestParam.handler, -1);
        }
        staticNetworkPerformance();
    }

    @Override // com.amap.api.col.3sl.kw.a
    public void onFinish() {
        if (this.mDataRequestParam != null) {
            NetworkProxyManager.getInstance().finishDownLoad(this.mDataRequestParam.handler);
        }
        staticNetworkPerformance();
    }

    @Override // com.amap.api.col.3sl.kw.a
    public void onException(Throwable th) {
        int iIndexOf;
        try {
            String str = new String(th.getMessage().getBytes("UTF-8"), "UTF-8");
            int i = (TextUtils.isEmpty(str) || (iIndexOf = str.indexOf(NETWORK_RESPONSE_CODE_STRING)) == -1) ? -1 : Integer.parseInt(str.substring(iIndexOf + 8));
            if (this.mDataRequestParam != null) {
                NetworkProxyManager.getInstance().netError(this.mDataRequestParam.handler, -1, i);
            }
        } catch (Throwable unused) {
            if (this.mDataRequestParam != null) {
                NetworkProxyManager.getInstance().netError(this.mDataRequestParam.handler, -1, -1);
            }
        }
        du.a(this.context, hashCode(), !NetworkProxyManager.getInstance().isNetworkConnected() ? 1 : 0, getNetworkFailedReason(th.getMessage()));
        jw.c(th, "AMapLoader", "download onException");
        dz.b(dy.e, "map loader exception " + th.getMessage());
    }

    public AMapLoader(Context context, ADataRequestParam aDataRequestParam) {
        this.mRequestCancel = false;
        this.context = context.getApplicationContext();
        this.mDataRequestParam = aDataRequestParam;
        this.mRequestCancel = false;
    }

    public void doRequest() {
        if (ip.a(this.context, dx.a()).a != ip.c.SuccessCode) {
            if (this.mDataRequestParam != null) {
                NetworkProxyManager.getInstance().netError(this.mDataRequestParam.handler, -1, -1);
                return;
            }
            return;
        }
        if (this.mRequestCancel) {
            if (this.mDataRequestParam != null) {
                NetworkProxyManager.getInstance().netError(this.mDataRequestParam.handler, -1, -1);
                return;
            }
            return;
        }
        String str = this.mDataRequestParam.requestBaseUrl;
        String str2 = this.mDataRequestParam.requestUrl;
        if (!str.endsWith("?")) {
            str = str + "?";
        }
        String requestParams = getRequestParams(str2.replaceAll(";", getEncodeRequestParams(";").toString()), str != null && str.contains("http://m5.amap.com/"), this.mDataRequestParam.nRequestType);
        StringBuffer stringBuffer = new StringBuffer();
        if (this.mDataRequestParam.nRequestType == 0) {
            stringBuffer.append(requestParams);
            stringBuffer.append("&csid=" + UUID.randomUUID().toString());
        } else {
            stringBuffer.append("csid=" + UUID.randomUUID().toString());
        }
        try {
            AMapGridDownloadRequest aMapGridDownloadRequest = new AMapGridDownloadRequest(this.context, str + generateQueryString(this.context, stringBuffer.toString()), NetworkProxyManager.getInstance().getUserAgent());
            aMapGridDownloadRequest.setConnectionTimeout(Window.PROGRESS_SECONDARY_END);
            aMapGridDownloadRequest.setSoTimeout(Window.PROGRESS_SECONDARY_END);
            if (this.mDataRequestParam.nRequestType != 0) {
                aMapGridDownloadRequest.setPostEntityBytes(requestParams.getBytes("UTF-8"));
            }
            this.requestMapDataTimestamp = System.currentTimeMillis();
            this.requestMapDataPackageSize = aMapGridDownloadRequest.getEntityBytes() == null ? 0L : aMapGridDownloadRequest.getEntityBytes().length;
            kw kwVar = new kw(aMapGridDownloadRequest, 0L, -1L, MapsInitializer.getProtocol() == 2);
            this.downloadManager = kwVar;
            kwVar.a(this);
        } catch (Throwable th) {
            try {
                onException(th);
            } finally {
                doCancel();
            }
        }
    }

    public void doCancel() {
        this.mRequestCancel = true;
        if (this.downloadManager == null || this.isCanceled) {
            return;
        }
        synchronized (this.downloadManager) {
            try {
                this.isCanceled = true;
                this.downloadManager.a();
            } finally {
            }
        }
    }

    public void doCancelAndNotify() {
        onCancel();
        doCancel();
    }

    private void onCancel() {
        if (this.mDataRequestParam != null) {
            NetworkProxyManager.getInstance().netCancel(this.mDataRequestParam.handler, -1);
        }
    }

    private String getEncodeRequestParams(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected String getRequestParams(String str, boolean z, int i) {
        StringBuffer stringBuffer = new StringBuffer(str);
        if (z) {
            stringBuffer.append("&channel=amap7&div=GNaviMap");
        } else {
            stringBuffer.append("&channel=amapapi");
            stringBuffer.append("&div=GNaviMap");
        }
        return stringBuffer.toString();
    }

    private String generateQueryString(Context context, String str) {
        StringBuffer stringBuffer = new StringBuffer(str);
        String strF = ig.f(this.context);
        try {
            strF = URLEncoder.encode(strF, "UTF-8");
        } catch (Throwable unused) {
        }
        stringBuffer.append("&key=").append(strF);
        String strSortReEncoderParams = sortReEncoderParams(stringBuffer.toString());
        String strA = ij.a();
        stringBuffer.append("&ts=".concat(String.valueOf(strA)));
        stringBuffer.append("&scode=" + ij.a(context, strA, strSortReEncoderParams));
        stringBuffer.append("&dip=16300");
        return stringBuffer.toString();
    }

    private String sortReEncoderParams(String str) {
        String[] strArrSplit = str.split("&");
        Arrays.sort(strArrSplit);
        StringBuffer stringBuffer = new StringBuffer();
        for (String str2 : strArrSplit) {
            stringBuffer.append(strReEncoder(str2));
            stringBuffer.append("&");
        }
        String string = stringBuffer.toString();
        return string.length() > 1 ? (String) string.subSequence(0, string.length() - 1) : str;
    }

    private String strReEncoder(String str) {
        if (str == null) {
            return str;
        }
        try {
            return URLDecoder.decode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            jw.c(e, "AbstractProtocalHandler", "strReEncoder");
            return "";
        } catch (Exception e2) {
            jw.c(e2, "AbstractProtocalHandler", "strReEncoderException");
            return "";
        }
    }

    private String getNetworkFailedReason(String str) {
        return !NetworkProxyManager.getInstance().isNetworkConnected() ? "无网络" : str;
    }

    private void staticNetworkPerformance() {
        du.a(this.context, hashCode(), System.currentTimeMillis() - this.requestMapDataTimestamp, this.requestMapDataPackageSize);
    }
}
