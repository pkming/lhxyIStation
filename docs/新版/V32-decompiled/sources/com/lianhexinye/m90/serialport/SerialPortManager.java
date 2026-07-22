package com.lianhexinye.m90.serialport;

/* JADX INFO: loaded from: classes2.dex */
public class SerialPortManager {
    public ProtocolGenerate crateTongdaProtocol() {
        return new TongDaProtocol();
    }

    public ProtocolGenerate crateHengWuProtocol() {
        return new HengWuProtocol();
    }

    public ProtocolGenerate crateLEDProtocol() {
        return new LEDProtocol();
    }

    public ProtocolGenerate crateLHXYProtocol() {
        return new LHXYProtocol();
    }
}
