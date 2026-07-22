package com.lianhexinye.m90.socket;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.util.Log;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.socket.request.Action;
import com.lianhexinye.m90.socket.request.GenerateReqPackage;
import com.lianhexinye.m90.socket.request.Request;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/* JADX INFO: loaded from: classes2.dex */
public class SocketManageTmp {
    private static final int REQUEST_TIMEOUT = 10000;
    private static final String serverIp = "211.154.159.34";
    private static final int serverPort = 7000;
    private static final int sotimeout = 8000;
    private Socket clientSocket;
    private int count;
    private InputStream inputStream;
    private ExecutorService mThreadPool;
    private OutputStream outputStream;
    private int overtimeCount;
    private AtomicLong seqId;
    private SocketActionCallback socketActionCallback;

    public interface SocketActionCallback {
        void onSocketConnectionFailed(String str);

        void onSocketConnectionSuccess();

        void onSocketDisconnection(String str);

        void onSocketReadResponse(String str);

        void onSocketTimeoutException(String str);
    }

    private SocketManageTmp() {
        this.overtimeCount = 3;
        this.count = 0;
        this.seqId = new AtomicLong(SystemClock.uptimeMillis());
        this.clientSocket = new Socket();
    }

    private static class SingletonHolder {
        private static final SocketManageTmp instance = new SocketManageTmp();

        private SingletonHolder() {
        }
    }

    public static SocketManageTmp getInstance() {
        return SingletonHolder.instance;
    }

    public void connect() {
        new OpenSocketConnect().start();
    }

    class OpenSocketConnect extends Thread {
        OpenSocketConnect() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                if (SocketManageTmp.this.mThreadPool != null) {
                    SocketManageTmp.this.mThreadPool.shutdown();
                    SocketManageTmp.this.mThreadPool = null;
                    SocketManageTmp.this.mThreadPool = Executors.newCachedThreadPool();
                } else {
                    SocketManageTmp.this.mThreadPool = Executors.newCachedThreadPool();
                }
                SocketManageTmp.this.clientSocket.connect(new InetSocketAddress(SocketManageTmp.serverIp, SocketManageTmp.serverPort), 8000);
                SocketManageTmp.this.clientSocket.setKeepAlive(true);
                SocketManageTmp socketManageTmp = SocketManageTmp.this;
                socketManageTmp.inputStream = socketManageTmp.clientSocket.getInputStream();
                SocketManageTmp socketManageTmp2 = SocketManageTmp.this;
                socketManageTmp2.outputStream = socketManageTmp2.clientSocket.getOutputStream();
                SocketManageTmp.this.acceptServerMsg();
                SocketManageTmp.this.registerDevice();
            } catch (SocketException unused) {
                SocketManageTmp.this.socketActionCallback.onSocketDisconnection("连接关闭");
                SocketManageTmp.this.closeSocket();
            } catch (SocketTimeoutException unused2) {
                SocketManageTmp.this.socketActionCallback.onSocketTimeoutException("连接超时");
            } catch (UnknownHostException e) {
                SocketManageTmp.this.socketActionCallback.onSocketConnectionFailed(e.getMessage());
                SocketManageTmp.this.closeSocket();
            } catch (IOException e2) {
                SocketManageTmp.this.socketActionCallback.onSocketConnectionFailed(e2.getMessage());
                SocketManageTmp.this.closeSocket();
            } catch (Exception e3) {
                SocketManageTmp.this.socketActionCallback.onSocketConnectionFailed(e3.getMessage());
                SocketManageTmp.this.closeSocket();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void acceptServerMsg() {
        this.mThreadPool.execute(new Runnable() { // from class: com.lianhexinye.m90.socket.SocketManageTmp.1
            @Override // java.lang.Runnable
            public void run() {
                while (SocketManageTmp.this.clientSocket.getKeepAlive()) {
                    try {
                        SocketManageTmp socketManageTmp = SocketManageTmp.this;
                        String stream = socketManageTmp.readStream(socketManageTmp.inputStream);
                        if (stream != null && stream.length() > 0) {
                            Log.d("SocketManageTmp", "acceptServerMsg:" + stream);
                            if (!SocketManageTmp.this.filterDataPacket(stream)) {
                                SocketManageTmp.this.socketActionCallback.onSocketReadResponse(stream);
                            }
                        }
                    } catch (Exception e) {
                        Log.d("SocketManageTmp", "acceptServerMsg Exception:" + e.getMessage());
                        e.printStackTrace();
                        return;
                    }
                }
                throw new Exception("服务端不在线！");
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerDevice() {
        this.mThreadPool.execute(new Runnable() { // from class: com.lianhexinye.m90.socket.SocketManageTmp.2
            @Override // java.lang.Runnable
            public void run() {
                boolean z = true;
                while (z) {
                    if (!SocketManageTmp.this.clientSocket.isClosed() && SocketManageTmp.this.clientSocket.isConnected()) {
                        byte[] bArrRegisterDevice = GenerateReqPackage.registerDevice();
                        Log.d("SocketManageTmp", "registerDevice pLog:" + JavaUtils.bytesToHexString(bArrRegisterDevice, bArrRegisterDevice.length));
                        SocketManageTmp.this.sendReq(Action.HEARTBEAT, bArrRegisterDevice);
                        z = false;
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean filterDataPacket(String str) {
        if (str.length() < 64 || !str.substring(8, 12).equals("0110")) {
            return false;
        }
        Log.d("SocketManageTmp", "msg:" + str);
        byte[] bArrHexStringToByteArray = JavaUtils.hexStringToByteArray(str.substring(16, 24));
        Log.d("SocketManageTmp", "cNumber:" + JavaUtils.bytesToHexString(bArrHexStringToByteArray, bArrHexStringToByteArray.length));
        return true;
    }

    public void sendReq(Action action, Object obj) {
        sendReq(action, obj, 10000L);
    }

    public void sendReq(Action action, Object obj, long j) {
        sendReq(action, obj, j, 1);
    }

    private <T> void sendReq(Action action, final T t, long j, int i) {
        if (isNetConnect()) {
            new Request.Builder().action(action.getAction()).seqId(this.seqId.getAndIncrement()).reqCount(i).req(t).build();
            this.mThreadPool.execute(new Runnable() { // from class: com.lianhexinye.m90.socket.SocketManageTmp.3
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        StringBuilder sbAppend = new StringBuilder().append("sendReq：");
                        Object obj = t;
                        Log.d("SocketManageTmp", sbAppend.append(JavaUtils.bytesToHexString((byte[]) obj, ((byte[]) obj).length)).toString());
                        SocketManageTmp.this.outputStream.write((byte[]) t);
                        SocketManageTmp.this.outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void closeAllServer() {
        ExecutorService executorService = this.mThreadPool;
        if (executorService != null) {
            executorService.shutdown();
            this.mThreadPool = null;
        }
        if (this.clientSocket != null) {
            try {
                this.inputStream.close();
                this.outputStream.close();
                this.clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeSocket() {
        if (this.clientSocket != null) {
            try {
                this.inputStream.close();
                this.outputStream.close();
                this.clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String readStream(InputStream inputStream) throws Exception {
        StringBuffer stringBuffer = new StringBuffer();
        byte[] bArr = new byte[32];
        while (true) {
            int i = inputStream.read(bArr);
            if (i != -1) {
                stringBuffer.append(JavaUtils.bytesToHexString(bArr, i));
            } else {
                return stringBuffer.toString();
            }
        }
    }

    public void registerReceiver(SocketActionCallback socketActionCallback) {
        this.socketActionCallback = socketActionCallback;
    }

    private boolean isNetConnect() {
        NetworkInfo activeNetworkInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager) AppApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager != null && (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) != null && activeNetworkInfo.isConnected() && activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED;
    }
}
