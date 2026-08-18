package com.lianhexinye.serialprot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes2.dex */
public abstract class SerialHelper {
    private byte[] _bLoopData;
    private boolean _isOpen;
    private int iBaudRate;
    private int iDelay;
    private InputStream mInputStream;
    protected OutputStream mOutputStream;
    private ReadThread mReadThread;
    protected SerialPort mSerialPort;
    private String sPort;

    public abstract void onDataReceived(ComBean comBean);

    public void sendHex(String str) {
    }

    public void setHexLoopData(String str) {
    }

    public SerialHelper() {
        this("/dev/ttyS3", 9600);
    }

    public SerialHelper(String str) {
        this(str, 9600);
    }

    public SerialHelper(String str, int i) {
        this._isOpen = false;
        this.iBaudRate = 9600;
        this.sPort = "/dev/ttyS0";
        this._bLoopData = new byte[]{TarConstants.LF_NORMAL};
        this.iDelay = 500;
        this.sPort = str;
        this.iBaudRate = i;
    }

    public SerialHelper(String str, String str2) {
        this(str, Integer.parseInt(str2));
    }

    public void close() {
        this._isOpen = false;
        ReadThread readThread = this.mReadThread;
        if (readThread != null) {
            readThread.interrupt();
        }
        if (this.mSerialPort != null) {
            try {
                InputStream inputStream = this.mInputStream;
                if (inputStream != null) {
                    inputStream.close();
                    this.mOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.mSerialPort.close();
            this.mSerialPort = null;
        }
    }

    public int getBaudRate() {
        return this.iBaudRate;
    }

    public String getPort() {
        return this.sPort;
    }

    public byte[] getbLoopData() {
        return this._bLoopData;
    }

    public int getiDelay() {
        return this.iDelay;
    }

    public boolean isOpen() {
        return this._isOpen;
    }

    public void open() throws SecurityException, IOException {
        SerialPort serialPort = new SerialPort(new File(this.sPort), this.iBaudRate, 0);
        this.mSerialPort = serialPort;
        this.mOutputStream = serialPort.getOutputStream();
        this.mInputStream = this.mSerialPort.getInputStream();
        ReadThread readThread = new ReadThread();
        this.mReadThread = readThread;
        readThread.start();
        this._isOpen = true;
    }

    public void send(byte[] bArr) {
        try {
            this.mOutputStream.write(bArr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] read() {
        InputStream inputStream = this.mInputStream;
        if (inputStream == null) {
            return null;
        }
        byte[] bArr = new byte[512];
        try {
            inputStream.read(bArr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bArr;
    }

    public void sendTxt(String str) {
        send(str.getBytes());
    }

    public boolean setBaudRate(int i) {
        this.iBaudRate = i;
        return true;
    }

    public boolean setBaudRate(String str) {
        return setBaudRate(Integer.parseInt(str));
    }

    public boolean setPort(String str) {
        this.sPort = str;
        return true;
    }

    public void setTxtLoopData(String str) {
        this._bLoopData = str.getBytes();
    }

    public void setbLoopData(byte[] bArr) {
        this._bLoopData = bArr;
    }

    public void setiDelay(int i) {
        this.iDelay = i;
    }

    private class ReadThread extends Thread {
        byte[] arrayOfByte;

        private ReadThread() {
            this.arrayOfByte = null;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            while (SerialHelper.this._isOpen) {
                if (SerialHelper.this.mInputStream != null) {
                    this.arrayOfByte = null;
                    this.arrayOfByte = new byte[512];
                    int i = 0;
                    try {
                        i = SerialHelper.this.mInputStream.read(this.arrayOfByte);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (i > 0) {
                        SerialHelper.this.onDataReceived(new ComBean(this.arrayOfByte, i));
                    }
                    try {
                        Thread.sleep(90L);
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }
    }
}
