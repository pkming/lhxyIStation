package com.lianhexinye.m90.gpio;

import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.common.utils.language.SPUtil;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/* JADX INFO: loaded from: classes2.dex */
public class GpioOperation {
    private static String GPIO_PATH = "/proc/rp_gpio/";
    private static FileWriter fileIn;
    private static FileReader fileOut;

    public static void gpioWrite(String str, String str2) {
        synchronized (SPUtil.getInstance(AppApplication.getContext())) {
            String str3 = GPIO_PATH + str;
            File file = new File(str3);
            if (file.isFile()) {
                try {
                    if (file.canWrite()) {
                        try {
                            FileWriter fileWriter = new FileWriter(str3);
                            fileIn = fileWriter;
                            fileWriter.write(str2);
                            fileIn.flush();
                            fileIn.close();
                            try {
                                fileIn.close();
                            } catch (IOException e) {
                                e = e;
                                e.printStackTrace();
                            }
                        } catch (IOException e2) {
                            e2.printStackTrace();
                            try {
                                fileIn.close();
                            } catch (IOException e3) {
                                e = e3;
                                e.printStackTrace();
                            }
                        }
                    }
                } finally {
                }
            }
        }
    }

    public static int gpioRead(String str) {
        synchronized (SPUtil.getInstance(AppApplication.getContext())) {
            String str2 = GPIO_PATH + str;
            char[] cArr = {255};
            File file = new File(str2);
            if (file.isFile() && file.canRead()) {
                try {
                    try {
                        FileReader fileReader = new FileReader(str2);
                        fileOut = fileReader;
                        fileReader.read(cArr);
                        fileOut.close();
                        try {
                            fileOut.close();
                        } catch (IOException e) {
                            e = e;
                            e.printStackTrace();
                        }
                    } catch (IOException e2) {
                        e2.printStackTrace();
                        try {
                            fileOut.close();
                        } catch (IOException e3) {
                            e = e3;
                            e.printStackTrace();
                        }
                    }
                } finally {
                }
            }
            if (cArr[0] == '0') {
                return 0;
            }
            if (cArr[0] == '1') {
                return 1;
            }
            return Integer.valueOf(cArr[0]).intValue();
        }
    }
}
