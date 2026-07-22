package com.lianhexinye.serialprot;

import android.util.Log;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes2.dex */
public class SerialPort {
    private static final String TAG = "SerialPort";
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    private static native FileDescriptor open(String str, int i, int i2);

    public native void close();

    public SerialPort(File file, int i, int i2) throws IOException, SecurityException {
        if (!file.canRead() || !file.canWrite()) {
            try {
                Process processExec = Runtime.getRuntime().exec("/system/bin/su");
                processExec.getOutputStream().write(("chmod 777 " + file.getAbsolutePath() + "\nexit\n").getBytes());
                if (processExec.waitFor() != 0 || !file.canRead() || !file.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }
        LogUtils.d(TAG, "baudrate:" + i);
        FileDescriptor fileDescriptorOpen = open(file.getAbsolutePath(), i, i2);
        this.mFd = fileDescriptorOpen;
        if (fileDescriptorOpen == null) {
            Log.e(TAG, "native open returns null");
            throw new IOException();
        }
        this.mFileInputStream = new FileInputStream(this.mFd);
        this.mFileOutputStream = new FileOutputStream(this.mFd);
    }

    public InputStream getInputStream() {
        return this.mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return this.mFileOutputStream;
    }

    static {
        System.loadLibrary("serial_port");
    }
}
