package com.lianhexinye.m90.socket;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.TimedRemoteCaller;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.greendao.gen.ReportInfoModel;
import com.lianhexinye.m90.socket.request.Generate808ReqPackage;
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

/* JADX INFO: loaded from: classes2.dex */
public class SocketIntercomManage {
    private static Socket clientSocket = null;
    private static InputStream inputStream = null;
    private static OutputStream outputStream = null;
    private static final int sotimeout = 8000;
    private AcceptServerMsgThread acceptServerMsg;
    private int heartbeatCount;
    private int heartbeatIntervalTime;
    public boolean isActiveShutdown;
    private String mDispatchProtocol;
    private int mPort;
    private String mStrIP;
    private ScheduledExecutorService mThreadPool;
    private int overtimeCount;
    private int reConnectCount;
    private ReportInfoModel reportInfoModel;
    private SocketIntercomCallback socketIntercomCallback;

    public interface SocketIntercomCallback {
        void onSocketClose(String str);

        void onSocketConnectionFailed(String str);

        void onSocketConnectionSuccess();

        void onSocketDisconnection(String str);

        void onSocketRead808Response(int i, String str);

        void onSocketReadResponse(int i, String str);

        void onSocketTimeoutException(String str);
    }

    static /* synthetic */ int access$708(SocketIntercomManage socketIntercomManage) {
        int i = socketIntercomManage.reConnectCount;
        socketIntercomManage.reConnectCount = i + 1;
        return i;
    }

    private SocketIntercomManage() {
        this.overtimeCount = 3;
        this.reConnectCount = 0;
        this.mThreadPool = Executors.newSingleThreadScheduledExecutor();
        this.reportInfoModel = new ReportInfoModel();
        this.heartbeatIntervalTime = 15;
        this.mStrIP = "";
        this.mPort = 0;
        this.isActiveShutdown = false;
        this.heartbeatCount = 0;
    }

    private static class SingletonHolder {
        private static final SocketIntercomManage instance = new SocketIntercomManage();

        private SingletonHolder() {
        }
    }

    public static SocketIntercomManage getInstance() {
        return SingletonHolder.instance;
    }

    /* JADX WARN: Type inference failed for: r0v4, types: [com.lianhexinye.m90.socket.SocketIntercomManage$1] */
    public void connect(final String str, final int i) {
        this.mStrIP = str;
        this.mPort = i;
        this.mDispatchProtocol = ((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchProtocol", "无")).trim();
        new Thread() { // from class: com.lianhexinye.m90.socket.SocketIntercomManage.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                super.run();
                if (SocketIntercomManage.clientSocket == null || !SocketIntercomManage.clientSocket.isConnected() || SocketIntercomManage.clientSocket.isClosed()) {
                    try {
                        LogUtils.d("SocketIntercomManage", "对讲开始连接");
                        SocketIntercomManage.this.mThreadPool = Executors.newSingleThreadScheduledExecutor();
                        LogUtils.d("SocketIntercomManage", "strIP:" + str + ",port:" + i);
                        InetSocketAddress inetSocketAddress = new InetSocketAddress(str, i);
                        Socket unused = SocketIntercomManage.clientSocket = new Socket();
                        SocketIntercomManage.clientSocket.connect(inetSocketAddress, 8000);
                        InputStream unused2 = SocketIntercomManage.inputStream = SocketIntercomManage.clientSocket.getInputStream();
                        OutputStream unused3 = SocketIntercomManage.outputStream = SocketIntercomManage.clientSocket.getOutputStream();
                        SocketIntercomManage.this.acceptServerMsg = SocketIntercomManage.this.new AcceptServerMsgThread();
                        SocketIntercomManage.this.acceptServerMsg.start();
                        SocketIntercomManage.this.reConnectCount = 0;
                        SocketIntercomManage.this.isActiveShutdown = false;
                    } catch (SocketException e) {
                        LogUtils.d("SocketIntercomManage", "SocketException:" + e.getMessage());
                        if (SocketIntercomManage.this.socketIntercomCallback != null) {
                            SocketIntercomManage.this.socketIntercomCallback.onSocketDisconnection("连接关闭");
                        }
                        SocketIntercomManage.this.closeAllSocket();
                        if (SocketIntercomManage.this.isActiveShutdown || SocketIntercomManage.this.reConnectCount >= 3) {
                            return;
                        }
                        SocketIntercomManage.access$708(SocketIntercomManage.this);
                        SocketIntercomManage socketIntercomManage = SocketIntercomManage.this;
                        socketIntercomManage.connect(socketIntercomManage.mStrIP, SocketIntercomManage.this.mPort);
                    } catch (SocketTimeoutException e2) {
                        LogUtils.d("SocketIntercomManage", "SocketTimeoutException:" + e2.getMessage());
                        if (SocketIntercomManage.this.socketIntercomCallback != null) {
                            SocketIntercomManage.this.socketIntercomCallback.onSocketTimeoutException("连接超时");
                        }
                        SocketIntercomManage.this.closeAllSocket();
                    } catch (UnknownHostException e3) {
                        LogUtils.d("SocketIntercomManage", "UnknownHostException:" + e3.getMessage());
                        if (SocketIntercomManage.this.socketIntercomCallback != null) {
                            SocketIntercomManage.this.socketIntercomCallback.onSocketConnectionFailed(e3.getMessage());
                        }
                        SocketIntercomManage.this.closeAllSocket();
                    } catch (IOException e4) {
                        LogUtils.d("SocketIntercomManage", "IOException:" + e4.getMessage());
                        if (SocketIntercomManage.this.socketIntercomCallback != null) {
                            SocketIntercomManage.this.socketIntercomCallback.onSocketConnectionFailed(e4.getMessage());
                        }
                        SocketIntercomManage.this.closeAllSocket();
                    } catch (Exception e5) {
                        LogUtils.d("SocketIntercomManage", "Exception:" + e5.getMessage());
                        if (SocketIntercomManage.this.socketIntercomCallback != null) {
                            SocketIntercomManage.this.socketIntercomCallback.onSocketConnectionFailed(e5.getMessage());
                        }
                        SocketIntercomManage.this.closeAllSocket();
                    }
                }
            }
        }.start();
    }

    public void reConnect() {
        this.mThreadPool.schedule(new Runnable() { // from class: com.lianhexinye.m90.socket.SocketIntercomManage.2
            @Override // java.lang.Runnable
            public void run() {
            }
        }, TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
    }

    public boolean isConnected() {
        Socket socket = clientSocket;
        if (socket != null) {
            return socket.isConnected();
        }
        return false;
    }

    public boolean isClosed() {
        Socket socket = clientSocket;
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
                byte[] bArr = new byte[1024];
                while (true) {
                    int i = SocketIntercomManage.inputStream.read(bArr);
                    if (i == -1 || this.suspend) {
                        return;
                    }
                    if ("ALINK".equals(SocketIntercomManage.this.mDispatchProtocol)) {
                        SocketIntercomManage.this.filterDataPacket(JavaUtils.bytesToHexString(bArr, i).toLowerCase());
                    } else if ("808".equals(SocketIntercomManage.this.mDispatchProtocol)) {
                        SocketIntercomManage.this.filter808DataPacket(JavaUtils.bytesToHexString(bArr, i).toLowerCase());
                    } else if ("CC808".equals(SocketIntercomManage.this.mDispatchProtocol)) {
                        SocketIntercomManage.this.filterJQ808DataPacket(JavaUtils.bytesToHexString(bArr, i).toLowerCase());
                    }
                }
            } catch (Exception e) {
                if (SocketIntercomManage.this.socketIntercomCallback != null) {
                    SocketIntercomManage.this.socketIntercomCallback.onSocketConnectionFailed(e.getMessage());
                }
                e.printStackTrace();
            }
        }
    }

    private void closeAcceptServer() {
        AcceptServerMsgThread acceptServerMsgThread = this.acceptServerMsg;
        if (acceptServerMsgThread != null) {
            acceptServerMsgThread.setSuspend(true);
        }
    }

    private void requestHeartbeat() {
        this.mThreadPool.scheduleAtFixedRate(new Runnable() { // from class: com.lianhexinye.m90.socket.SocketIntercomManage.3
            @Override // java.lang.Runnable
            public void run() {
                SocketIntercomManage.this.sendReq(Generate808ReqPackage.generateHeartbeat(SocketIntercomManage.this.reportInfoModel));
            }
        }, 3L, this.heartbeatIntervalTime, TimeUnit.SECONDS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void filterDataPacket(final String str) {
        this.mThreadPool.execute(new Runnable() { // from class: com.lianhexinye.m90.socket.SocketIntercomManage.4
            @Override // java.lang.Runnable
            public void run() {
                LogUtils.d("SocketIntercomManage", "filterDataPacket:" + str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void filter808DataPacket(final String str) {
        this.mThreadPool.execute(new Runnable() { // from class: com.lianhexinye.m90.socket.SocketIntercomManage.5
            @Override // java.lang.Runnable
            public void run() {
                String strSubstring = str;
                while (strSubstring.length() > 8 && strSubstring.startsWith("30316364")) {
                    int iHexToInt = (JavaUtils.HexToInt(strSubstring.substring(48, 52)) * 2) + 52;
                    if (strSubstring.length() < iHexToInt) {
                        return;
                    }
                    if (SocketIntercomManage.this.socketIntercomCallback != null) {
                        SocketIntercomManage.this.socketIntercomCallback.onSocketRead808Response(1, strSubstring.substring(52, iHexToInt));
                    }
                    strSubstring = strSubstring.substring(iHexToInt);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void filterJQ808DataPacket(final String str) {
        this.mThreadPool.execute(new Runnable() { // from class: com.lianhexinye.m90.socket.SocketIntercomManage.6
            @Override // java.lang.Runnable
            public void run() {
                String strSubstring = str;
                while (strSubstring.length() > 8 && strSubstring.startsWith("30316364")) {
                    int iHexToInt = (JavaUtils.HexToInt(strSubstring.substring(48, 52)) * 2) + 52;
                    if (strSubstring.length() < iHexToInt) {
                        return;
                    }
                    if (SocketIntercomManage.this.socketIntercomCallback != null) {
                        SocketIntercomManage.this.socketIntercomCallback.onSocketRead808Response(1, strSubstring.substring(52, iHexToInt));
                    }
                    strSubstring = strSubstring.substring(iHexToInt);
                }
            }
        });
    }

    public void sendReq(Object obj) {
        if (isNetConnect()) {
            try {
                OutputStream outputStream2 = outputStream;
                if (outputStream2 != null) {
                    outputStream2.write((byte[]) obj);
                    outputStream.flush();
                }
            } catch (IOException e) {
                LogUtils.d("SocketIntercomManage", "发送异常：" + e.getMessage());
            }
        }
    }

    public void closeSocket() {
        closeAcceptServer();
        try {
            OutputStream outputStream2 = outputStream;
            if (outputStream2 != null) {
                outputStream2.close();
            }
            InputStream inputStream2 = inputStream;
            if (inputStream2 != null) {
                inputStream2.close();
            }
            Socket socket = clientSocket;
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeAllSocket() {
        closeAcceptServer();
        ScheduledExecutorService scheduledExecutorService = this.mThreadPool;
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
            this.mThreadPool = null;
        }
        try {
            OutputStream outputStream2 = outputStream;
            if (outputStream2 != null) {
                outputStream2.close();
            }
            InputStream inputStream2 = inputStream;
            if (inputStream2 != null) {
                inputStream2.close();
            }
            Socket socket = clientSocket;
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readStream(InputStream inputStream2) throws Exception {
        byte[] bArr = new byte[1024];
        int i = inputStream2.read(bArr);
        if (i != -1) {
            return JavaUtils.bytesToHexString(bArr, i);
        }
        return null;
    }

    public void registerCallback(SocketIntercomCallback socketIntercomCallback) {
        this.socketIntercomCallback = socketIntercomCallback;
    }

    private boolean isNetConnect() {
        NetworkInfo activeNetworkInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager) AppApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager != null && (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) != null && activeNetworkInfo.isConnected() && activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED;
    }
}
