package com.lhxy.istationdevice.android11.devicem90;

import com.lhxy.istationdevice.android11.deviceapi.SocketEndpointConfig;
import com.lhxy.istationdevice.android11.deviceapi.SocketMode;

public final class M90SocketMap {
    public static final SocketEndpointConfig JT808_MAIN = new SocketEndpointConfig("JT808_SOCKET", "pending-jt808-host", 0, SocketMode.STUB);
    public static final SocketEndpointConfig AL808_MAIN = new SocketEndpointConfig("AL808_SOCKET", "pending-al808-host", 0, SocketMode.STUB);

    private M90SocketMap() {
    }

    public static String describe() {
        return "Socket 占位:\n"
                + "- JT808_SOCKET -> 待配置 host:port / mode\n"
                + "- AL808_SOCKET -> 待配置 host:port / mode";
    }
}
