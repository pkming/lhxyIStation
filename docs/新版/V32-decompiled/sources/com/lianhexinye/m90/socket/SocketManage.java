package com.lianhexinye.m90.socket;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.util.TimedRemoteCaller;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.greendao.gen.ReportInfoModel;
import com.lianhexinye.m90.socket.request.Generate808ReqPackage;
import com.lianhexinye.m90.socket.request.GenerateReqPackage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/* JADX INFO: loaded from: classes2.dex */
public class SocketManage {
    private static final int REQUEST_TIMEOUT = 10000;
    private static final int sotimeout = 8000;
    private AcceptServerMsgThread acceptServerMsg;
    private Socket clientSocket;
    private int count;
    private int heartbeatCount;
    private int heartbeatIntervalTime;
    public long heartbeatTime;
    public boolean iHeartbeat;
    private InputStream inputStream;
    private String mDispatchProtocol;
    private ScheduledExecutorService mThreadPool;
    private OutputStream outputStream;
    private int overtimeCount;
    private int registerDeviceCount;
    private ReportInfoModel reportInfoModel;
    private AtomicLong seqId;
    private SocketActionCallback socketActionCallback;

    public interface SocketActionCallback {
        void onSocketClose(String str);

        void onSocketConnectionFailed(String str);

        void onSocketConnectionSuccess();

        void onSocketDisconnection(String str);

        void onSocketRead808Response(int i, String str);

        void onSocketReadResponse(int i, String str);

        void onSocketTimeoutException(String str);
    }

    static /* synthetic */ int access$308(SocketManage socketManage) {
        int i = socketManage.registerDeviceCount;
        socketManage.registerDeviceCount = i + 1;
        return i;
    }

    private SocketManage() {
        this.overtimeCount = 3;
        this.count = 0;
        this.mThreadPool = Executors.newSingleThreadScheduledExecutor();
        this.seqId = new AtomicLong(SystemClock.uptimeMillis());
        this.iHeartbeat = false;
        this.reportInfoModel = new ReportInfoModel();
        this.heartbeatIntervalTime = 15;
        this.registerDeviceCount = 0;
        this.heartbeatCount = 0;
    }

    private static class SingletonHolder {
        private static final SocketManage instance = new SocketManage();

        private SingletonHolder() {
        }
    }

    public static SocketManage getInstance() {
        return SingletonHolder.instance;
    }

    /* JADX WARN: Type inference failed for: r0v9, types: [com.lianhexinye.m90.socket.SocketManage$1] */
    public void connect() {
        this.mDispatchProtocol = ((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchProtocol", "无")).trim();
        this.reportInfoModel.setTerminalID(((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", "")).trim());
        this.heartbeatIntervalTime = Integer.parseInt(SPUserInfoUtils.get(AppApplication.getContext(), "NetDdispatchHeartbeatInterval", "15").toString());
        new Thread() { // from class: com.lianhexinye.m90.socket.SocketManage.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                super.run();
                if (SocketManage.this.clientSocket == null || !SocketManage.this.clientSocket.isConnected() || SocketManage.this.clientSocket.isClosed()) {
                    try {
                        LogUtils.d("SocketManage", "Socket开始连接");
                        SocketManage.this.heartbeatTime = System.currentTimeMillis() / 1000;
                        SocketManage.this.iHeartbeat = false;
                        SocketManage.this.registerDeviceCount = 0;
                        SocketManage.this.mThreadPool = Executors.newSingleThreadScheduledExecutor();
                        String strValueOf = String.valueOf(SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchIP", "211.154.159.34"));
                        String strValueOf2 = String.valueOf(SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchPort", "7000"));
                        LogUtils.d("SocketManage", "dispatchIP:" + strValueOf + ",dispatchPort:" + strValueOf2);
                        InetSocketAddress inetSocketAddress = new InetSocketAddress(strValueOf, Integer.valueOf(strValueOf2).intValue());
                        SocketManage.this.clientSocket = new Socket();
                        SocketManage.this.clientSocket.connect(inetSocketAddress, 8000);
                        SocketManage socketManage = SocketManage.this;
                        socketManage.inputStream = socketManage.clientSocket.getInputStream();
                        SocketManage socketManage2 = SocketManage.this;
                        socketManage2.outputStream = socketManage2.clientSocket.getOutputStream();
                        SocketManage.this.acceptServerMsg = SocketManage.this.new AcceptServerMsgThread();
                        SocketManage.this.acceptServerMsg.start();
                        SocketManage.this.registerDevice();
                    } catch (SocketException e) {
                        LogUtils.d("SocketManage", "SocketException:" + e.getMessage());
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketDisconnection("连接关闭");
                        }
                        SocketManage.this.closeAllSocket();
                    } catch (SocketTimeoutException e2) {
                        LogUtils.d("SocketManage", "SocketTimeoutException:" + e2.getMessage());
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketTimeoutException("连接超时");
                        }
                        SocketManage.this.closeAllSocket();
                    } catch (UnknownHostException e3) {
                        LogUtils.d("SocketManage", "UnknownHostException:" + e3.getMessage());
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketConnectionFailed(e3.getMessage());
                        }
                        SocketManage.this.closeAllSocket();
                    } catch (IOException e4) {
                        LogUtils.d("SocketManage", "IOException:" + e4.getMessage());
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketConnectionFailed(e4.getMessage());
                        }
                        SocketManage.this.closeAllSocket();
                    } catch (Exception e5) {
                        LogUtils.d("SocketManage", "Exception:" + e5.getMessage());
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketConnectionFailed(e5.getMessage());
                        }
                        SocketManage.this.closeAllSocket();
                    }
                }
            }
        }.start();
    }

    public void reConnect() {
        this.mThreadPool.schedule(new Runnable() { // from class: com.lianhexinye.m90.socket.SocketManage.2
            @Override // java.lang.Runnable
            public void run() {
                SocketManage.this.connect();
            }
        }, TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
    }

    public boolean isConnected() {
        Socket socket = this.clientSocket;
        if (socket != null) {
            return socket.isConnected();
        }
        return false;
    }

    public boolean isClosed() {
        Socket socket = this.clientSocket;
        if (socket != null) {
            return socket.isClosed();
        }
        return true;
    }

    class AcceptServerMsgThread extends Thread {
        private boolean suspend = false;

        AcceptServerMsgThread() {
        }

        public void setSuspend(boolean z) {
            this.suspend = z;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                try {
                    byte[] bArr = new byte[1024];
                    while (true) {
                        int i = SocketManage.this.inputStream.read(bArr);
                        if (i == -1) {
                            break;
                        }
                        if (this.suspend) {
                            return;
                        }
                        if ("ALINK".equals(SocketManage.this.mDispatchProtocol)) {
                            SocketManage.this.filterDataPacket(JavaUtils.bytesToHexString(bArr, i).toLowerCase());
                        } else {
                            int i2 = 0;
                            if (!"808".equals(SocketManage.this.mDispatchProtocol)) {
                                if ("CC808".equals(SocketManage.this.mDispatchProtocol)) {
                                    LogUtils.d("SocketManage", "CC808原数据:" + JavaUtils.bytesToHexString(bArr, i));
                                    int i3 = i - 1;
                                    byte[] bArr2 = new byte[1024];
                                    int i4 = 0;
                                    while (i2 < i) {
                                        if (i2 < i3 && i2 != 0 && bArr[i2] == 125) {
                                            i2++;
                                            if (bArr[i2] == 1) {
                                                bArr2[i4] = 125;
                                            } else {
                                                bArr2[i4] = 126;
                                            }
                                        } else {
                                            bArr2[i4] = bArr[i2];
                                        }
                                        i4++;
                                        i2++;
                                    }
                                    SocketManage.this.filterJQ808DataPacket(JavaUtils.bytesToHexString(bArr2, i4).toLowerCase());
                                }
                            } else {
                                LogUtils.d("SocketManage", "808原数据:" + JavaUtils.bytesToHexString(bArr, i));
                                int i5 = i - 1;
                                byte[] bArr3 = new byte[1024];
                                int i6 = 0;
                                while (i2 < i) {
                                    if (i2 < i5 && i2 != 0 && bArr[i2] == 125) {
                                        i2++;
                                        if (bArr[i2] == 1) {
                                            bArr3[i6] = 125;
                                        } else {
                                            bArr3[i6] = 126;
                                        }
                                    } else {
                                        bArr3[i6] = bArr[i2];
                                    }
                                    i6++;
                                    i2++;
                                }
                                SocketManage.this.filter808DataPacket(JavaUtils.bytesToHexString(bArr3, i6).toLowerCase());
                            }
                        }
                    }
                } catch (Exception e) {
                    if (SocketManage.this.socketActionCallback != null) {
                        SocketManage.this.socketActionCallback.onSocketConnectionFailed(e.getMessage());
                    }
                    e.printStackTrace();
                }
            } finally {
                SocketManage.this.closeAllSocket();
            }
        }
    }

    private void closeAcceptServer() {
        AcceptServerMsgThread acceptServerMsgThread = this.acceptServerMsg;
        if (acceptServerMsgThread != null) {
            acceptServerMsgThread.setSuspend(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerDevice() {
        this.mThreadPool.schedule(new Runnable() { // from class: com.lianhexinye.m90.socket.SocketManage.3
            @Override // java.lang.Runnable
            public void run() {
                if (SocketManage.this.registerDeviceCount < 2) {
                    if (!SocketManage.this.clientSocket.isClosed() && SocketManage.this.clientSocket.isConnected()) {
                        SocketManage.access$308(SocketManage.this);
                        if (!"ALINK".equals(SocketManage.this.mDispatchProtocol)) {
                            byte[] bArrRegisterDevice = Generate808ReqPackage.registerDevice(SocketManage.this.reportInfoModel);
                            LogUtils.d("SocketManage", "registerDevice pLog:" + JavaUtils.bytesToHexString(bArrRegisterDevice, bArrRegisterDevice.length));
                            SocketManage.this.sendReq(bArrRegisterDevice);
                            return;
                        } else {
                            byte[] bArrRegisterDevice2 = GenerateReqPackage.registerDevice();
                            LogUtils.d("SocketManage", "registerDevice pLog:" + JavaUtils.bytesToHexString(bArrRegisterDevice2, bArrRegisterDevice2.length));
                            SocketManage.this.sendReq(bArrRegisterDevice2);
                            return;
                        }
                    }
                    SocketManage.access$308(SocketManage.this);
                    SocketManage.this.registerDevice();
                }
            }
        }, 4000L, TimeUnit.MILLISECONDS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestHeartbeat() {
        LogUtils.d("SocketManage", "heartbeatIntervalTime：" + this.heartbeatIntervalTime);
        this.mThreadPool.scheduleAtFixedRate(new Runnable() { // from class: com.lianhexinye.m90.socket.SocketManage.4
            @Override // java.lang.Runnable
            public void run() {
                SocketManage.this.sendReq(Generate808ReqPackage.generateHeartbeat(SocketManage.this.reportInfoModel));
            }
        }, 3L, this.heartbeatIntervalTime, TimeUnit.SECONDS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void filterDataPacket(final String str) {
        this.mThreadPool.execute(new Runnable() { // from class: com.lianhexinye.m90.socket.SocketManage.5
            @Override // java.lang.Runnable
            public void run() {
                String strSubstring = str;
                while (strSubstring.length() > 16) {
                    LogUtils.d("SocketManage", "filterDataPacket:" + strSubstring);
                    int iByteToShort_HL = (JavaUtils.byteToShort_HL(JavaUtils.hexToBytes(strSubstring.substring(4, 8)), 0) + 16) * 2;
                    if (strSubstring.length() < iByteToShort_HL) {
                        return;
                    }
                    if (strSubstring.substring(4, 16).equals("100001100100")) {
                        SocketManage.this.iHeartbeat = true;
                        SocketManage.getInstance().heartbeatTime = System.currentTimeMillis() / 1000;
                        SocketManage.this.sendReq(GenerateReqPackage.generateHeartbeat(JavaUtils.hexToBytes(strSubstring.substring(16, 24))));
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketConnectionSuccess();
                        }
                    } else if (strSubstring.substring(4, 16).equals("100004100400")) {
                        if (JavaUtils.byteToInt_HL(JavaUtils.hexToBytes(strSubstring.substring(32, 40)), 0) != 0) {
                            SocketManage.this.iHeartbeat = false;
                            SocketManage.this.registerDevice();
                        } else {
                            SocketManage.this.iHeartbeat = true;
                            if (SocketManage.this.socketActionCallback != null) {
                                LogUtils.d("SocketManage", "注册成功.");
                                SocketManage.this.socketActionCallback.onSocketConnectionSuccess();
                            }
                        }
                    } else if (strSubstring.substring(4, 16).equals("400001200100")) {
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketReadResponse(1, strSubstring.substring(0, iByteToShort_HL));
                        }
                    } else if (strSubstring.substring(4, 16).equals("800003200100")) {
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketReadResponse(2, strSubstring.substring(0, iByteToShort_HL));
                        }
                    } else if (strSubstring.substring(4, 16).equals("0800aa100400")) {
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketReadResponse(3, strSubstring.substring(0, iByteToShort_HL));
                        }
                    } else if (strSubstring.substring(4, 16).equals("0800ac100400")) {
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketReadResponse(4, strSubstring.substring(0, iByteToShort_HL));
                        }
                    } else if (strSubstring.substring(4, 16).equals("4800b5100400") && SocketManage.this.socketActionCallback != null) {
                        SocketManage.this.socketActionCallback.onSocketReadResponse(5, strSubstring.substring(0, iByteToShort_HL));
                    }
                    strSubstring = strSubstring.substring(iByteToShort_HL);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void filter808DataPacket(final String str) {
        this.mThreadPool.execute(new Runnable() { // from class: com.lianhexinye.m90.socket.SocketManage.6
            @Override // java.lang.Runnable
            public void run() {
                String strSubstring = str;
                while (strSubstring.length() > 10) {
                    LogUtils.d("SocketManage", "filter808DataPacket:" + strSubstring);
                    int iHexToInt = (JavaUtils.HexToInt(strSubstring.substring(6, 10)) + 15) * 2;
                    if (strSubstring.length() < iHexToInt) {
                        return;
                    }
                    if (strSubstring.substring(2, 6).equals("8001")) {
                        if (strSubstring.substring(30, 34).equals("0002")) {
                            SocketManage.this.iHeartbeat = true;
                            SocketManage.getInstance().heartbeatTime = System.currentTimeMillis() / 1000;
                        }
                    } else if (strSubstring.substring(2, 6).equals("8100")) {
                        int iHexToInt2 = JavaUtils.HexToInt(strSubstring.substring(30, 32));
                        if (iHexToInt2 != 0) {
                            SocketManage.this.iHeartbeat = false;
                            String str2 = iHexToInt2 != 1 ? iHexToInt2 != 2 ? iHexToInt2 != 3 ? "数据库中无该终端" : "终端已被注册" : "数据库中无该车辆" : "车辆已被注册";
                            if (SocketManage.this.socketActionCallback != null) {
                                SocketManage.this.socketActionCallback.onSocketRead808Response(1, str2);
                            }
                            SocketManage.this.registerDevice();
                        } else {
                            SocketManage.this.iHeartbeat = true;
                            SocketManage.this.requestHeartbeat();
                            if (SocketManage.this.socketActionCallback != null) {
                                LogUtils.d("SocketManage", "注册成功.");
                                SocketManage.this.socketActionCallback.onSocketConnectionSuccess();
                            }
                        }
                    } else if (strSubstring.substring(2, 6).equals("8b05")) {
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(3, strSubstring.substring(0, iHexToInt));
                        }
                    } else if (strSubstring.substring(2, 6).equals("8300")) {
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(4, strSubstring.substring(0, iHexToInt));
                        }
                    } else if (strSubstring.substring(2, 6).equals("8b02")) {
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(5, strSubstring.substring(0, iHexToInt));
                        }
                    } else if (strSubstring.substring(2, 6).equals("8b09")) {
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(6, strSubstring.substring(0, iHexToInt));
                        }
                    } else if (strSubstring.substring(2, 6).equals("8b01")) {
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(7, strSubstring.substring(0, iHexToInt));
                        }
                    } else if (strSubstring.substring(2, 6).equals("9101")) {
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(8, strSubstring.substring(0, iHexToInt));
                        }
                    } else if (strSubstring.substring(2, 6).equals("9102")) {
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(9, strSubstring.substring(0, iHexToInt));
                        }
                    } else if (strSubstring.substring(2, 6).equals("8b0a") && SocketManage.this.socketActionCallback != null) {
                        SocketManage.this.socketActionCallback.onSocketRead808Response(10, strSubstring.substring(0, iHexToInt));
                    }
                    strSubstring = strSubstring.substring(iHexToInt);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void filterJQ808DataPacket(final String str) {
        this.mThreadPool.execute(new Runnable() { // from class: com.lianhexinye.m90.socket.SocketManage.7
            @Override // java.lang.Runnable
            public void run() {
                String strSubstring = str;
                while (strSubstring.length() > 10) {
                    LogUtils.d("SocketManage", "filterCC808DataPacket:" + strSubstring);
                    int iHexToInt = (JavaUtils.HexToInt(strSubstring.substring(6, 10)) + 15) * 2;
                    if (strSubstring.length() < iHexToInt) {
                        return;
                    }
                    if (strSubstring.substring(2, 6).equals("8001")) {
                        if (strSubstring.substring(30, 34).equals("0002")) {
                            LogUtils.d("SocketManage", "心跳应答.");
                            SocketManage.this.iHeartbeat = true;
                            SocketManage.getInstance().heartbeatTime = System.currentTimeMillis() / 1000;
                        } else if (strSubstring.substring(30, 34).equals("0102")) {
                            int iHexToInt2 = JavaUtils.HexToInt(strSubstring.substring(34, 36));
                            if (iHexToInt2 != 0) {
                                SocketManage.this.iHeartbeat = false;
                                String str2 = iHexToInt2 != 1 ? iHexToInt2 != 2 ? iHexToInt2 != 3 ? "报警处理确认" : "不支持" : "消息有误" : "失败";
                                if (SocketManage.this.socketActionCallback != null) {
                                    SocketManage.this.socketActionCallback.onSocketRead808Response(2, str2);
                                }
                            } else {
                                SocketManage.this.iHeartbeat = true;
                                SocketManage.this.requestHeartbeat();
                                if (SocketManage.this.socketActionCallback != null) {
                                    LogUtils.d("SocketManage", "注册成功.");
                                    SocketManage.this.socketActionCallback.onSocketConnectionSuccess();
                                }
                            }
                        }
                    } else if (strSubstring.substring(2, 6).equals("8100")) {
                        int iHexToInt3 = JavaUtils.HexToInt(strSubstring.substring(30, 32));
                        if (iHexToInt3 != 0) {
                            SocketManage.this.iHeartbeat = false;
                            String str3 = iHexToInt3 != 1 ? iHexToInt3 != 2 ? iHexToInt3 != 3 ? "数据库中无该终端" : "终端已被注册" : "数据库中无该车辆" : "车辆已被注册";
                            if (SocketManage.this.socketActionCallback != null) {
                                SocketManage.this.socketActionCallback.onSocketRead808Response(1, str3);
                            }
                            SocketManage.this.registerDevice();
                        } else {
                            String strSubstring2 = strSubstring.substring(32, iHexToInt - 4);
                            LogUtils.d("SocketManage", "filterJQ808DataPacket autTmp:" + strSubstring2);
                            SocketManage.this.reportInfoModel.setAuthorityData(strSubstring2);
                            SocketManage.this.sendReq(Generate808ReqPackage.generateAuthority(SocketManage.this.reportInfoModel));
                        }
                    } else if (strSubstring.substring(2, 6).equals("8b05")) {
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(3, strSubstring.substring(0, iHexToInt));
                        }
                    } else if (strSubstring.substring(2, 6).equals("8300")) {
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(4, strSubstring.substring(0, iHexToInt));
                        }
                    } else if (strSubstring.substring(2, 6).equals("8b02")) {
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(5, strSubstring.substring(0, iHexToInt));
                        }
                    } else if (strSubstring.substring(2, 6).equals("8b09")) {
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(6, strSubstring.substring(0, iHexToInt));
                        }
                    } else if (strSubstring.substring(2, 6).equals("8b01")) {
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(7, strSubstring.substring(0, iHexToInt));
                        }
                    } else if (strSubstring.substring(2, 6).equals("9101")) {
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(8, strSubstring.substring(0, iHexToInt));
                        }
                    } else if (strSubstring.substring(2, 6).equals("9102")) {
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(9, strSubstring.substring(0, iHexToInt));
                        }
                    } else if (strSubstring.substring(2, 6).equals("8b0a")) {
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(10, strSubstring.substring(0, iHexToInt));
                        }
                    } else if (strSubstring.substring(2, 6).equals("8310")) {
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(11, strSubstring.substring(0, iHexToInt));
                        }
                    } else if (strSubstring.substring(2, 6).equals("8900")) {
                        if (strSubstring.substring(26, 28).equals("80") && SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(12, strSubstring.substring(0, iHexToInt));
                        }
                        if (strSubstring.substring(26, 28).equals("21") && SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(15, strSubstring.substring(0, iHexToInt));
                        }
                        if (strSubstring.substring(26, 28).equals("25") && SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(16, strSubstring.substring(0, iHexToInt));
                        }
                        if (strSubstring.substring(26, 28).equals("28") && SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(17, strSubstring.substring(0, iHexToInt));
                        }
                        if (strSubstring.substring(26, 28).equals("C8") && SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(18, strSubstring.substring(0, iHexToInt));
                        }
                        if (strSubstring.substring(26, 28).equals("17") && SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(19, strSubstring.substring(0, iHexToInt));
                        }
                        if (strSubstring.substring(26, 28).equals("19") && SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(20, strSubstring.substring(0, iHexToInt));
                        }
                    } else if (strSubstring.substring(2, 6).equals("8309")) {
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(13, strSubstring.substring(0, iHexToInt));
                        }
                    } else if (strSubstring.substring(2, 6).equals("8308")) {
                        if (SocketManage.this.socketActionCallback != null) {
                            SocketManage.this.socketActionCallback.onSocketRead808Response(14, strSubstring.substring(0, iHexToInt));
                        }
                    } else if (strSubstring.substring(2, 6).equals("fa00") && SocketManage.this.socketActionCallback != null) {
                        SocketManage.this.socketActionCallback.onSocketRead808Response(21, strSubstring.substring(0, iHexToInt));
                    }
                    strSubstring = strSubstring.substring(iHexToInt);
                }
            }
        });
    }

    public boolean sendReq(final Object obj) {
        if (!isNetConnect()) {
            return false;
        }
        this.mThreadPool.execute(new Runnable() { // from class: com.lianhexinye.m90.socket.SocketManage.8
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (SocketManage.this.outputStream != null) {
                        StringBuilder sbAppend = new StringBuilder().append("sendReq：");
                        Object obj2 = obj;
                        LogUtils.d("SocketManage", sbAppend.append(JavaUtils.bytesToHexString((byte[]) obj2, ((byte[]) obj2).length)).toString());
                        SocketManage.this.outputStream.write((byte[]) obj);
                        SocketManage.this.outputStream.flush();
                    }
                } catch (IOException e) {
                    LogUtils.d("SocketManage", "发送异常：" + e.getMessage());
                    try {
                        if (SocketManage.this.outputStream != null) {
                            SocketManage.this.outputStream.close();
                        }
                        if (SocketManage.this.inputStream != null) {
                            SocketManage.this.inputStream.close();
                        }
                        if (SocketManage.this.clientSocket != null) {
                            SocketManage.this.clientSocket.close();
                            SocketManage.this.clientSocket = null;
                        }
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        });
        return true;
    }

    public void closeSocket() {
        closeAcceptServer();
        try {
            OutputStream outputStream = this.outputStream;
            if (outputStream != null) {
                outputStream.close();
            }
            InputStream inputStream = this.inputStream;
            if (inputStream != null) {
                inputStream.close();
            }
            Socket socket = this.clientSocket;
            if (socket != null) {
                socket.close();
                this.clientSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeAllSocket() {
        this.iHeartbeat = false;
        closeAcceptServer();
        ScheduledExecutorService scheduledExecutorService = this.mThreadPool;
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
            this.mThreadPool = null;
        }
        try {
            OutputStream outputStream = this.outputStream;
            if (outputStream != null) {
                outputStream.close();
            }
            InputStream inputStream = this.inputStream;
            if (inputStream != null) {
                inputStream.close();
            }
            Socket socket = this.clientSocket;
            if (socket != null) {
                socket.close();
                this.clientSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readStream(InputStream inputStream) throws Exception {
        byte[] bArr = new byte[1024];
        int i = inputStream.read(bArr);
        if (i != -1) {
            return JavaUtils.bytesToHexString(bArr, i);
        }
        return null;
    }

    public void registerCallback(SocketActionCallback socketActionCallback) {
        this.socketActionCallback = socketActionCallback;
    }

    private boolean isNetConnect() {
        NetworkInfo activeNetworkInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager) AppApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager != null && (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) != null && activeNetworkInfo.isConnected() && activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED;
    }
}
