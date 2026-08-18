package android.hardware.display;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.display.outputstate.DispOutputState;
import android.hardware.display.outputstate.DualDisplayOutput;
import android.hardware.display.outputstate.MainDispToDev0PlugIn;
import android.hardware.display.outputstate.MainDispToDev0PlugInExt;
import android.hardware.display.outputstate.MainDispToDev0PlugOut;
import android.hardware.display.outputstate.MainDispToDev1PlugIn;
import android.hardware.display.outputstate.MainDispToDev1PlugOut;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemProperties;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class DisplayManagerPolicy2 {
    private static final String TAG = "DisplayManagerPolicy2";
    private boolean mCanSetDisp2Enhance;
    private Context mContext;
    private File mDisp2EnhanceIdFile;
    private File mDisp2EnhanceModeFile;
    private DispOutputState mDispOutputState;
    private DisplayManager mDm;
    private DispOutputState mDualDisplayOutput;
    private DispOutputState mMainDispToDev0PlugIn;
    private DispOutputState mMainDispToDev0PlugInExt;
    private DispOutputState mMainDispToDev0PlugOut;
    private DispOutputState mMainDispToDev1PlugIn;
    private DispOutputState mMainDispToDev1PlugOut;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mBootCompleted = false;
    private BroadcastReceiver mBootCompletedReceiver = new BroadcastReceiver() { // from class: android.hardware.display.DisplayManagerPolicy2.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            DisplayManagerPolicy2.this.mBootCompleted = true;
        }
    };
    private ArrayList<DispDevice> mDispDevices = new ArrayList<>();

    class DataBaseObserver extends ContentObserver {
        public void update() {
        }

        DataBaseObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            DisplayManagerPolicy2.this.mContext.getContentResolver();
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            update();
        }
    }

    private class DispDevice {
        boolean mHotplugSupport;
        int mRevertPlugStateType;
        int mType;

        private DispDevice() {
        }
    }

    public DisplayManagerPolicy2(Context context) {
        this.mContext = context;
        this.mDm = new DisplayManager(context);
        new DataBaseObserver(new Handler()).observe();
        this.mContext.registerReceiver(this.mBootCompletedReceiver, new IntentFilter(Intent.ACTION_BOOT_COMPLETED));
        this.mDisp2EnhanceIdFile = new File("/sys/class/disp/disp/attr/disp");
        this.mDisp2EnhanceModeFile = new File("/sys/class/disp/disp/attr/enhance_mode");
        if (this.mDisp2EnhanceIdFile.exists() && this.mDisp2EnhanceModeFile.exists()) {
            this.mCanSetDisp2Enhance = true;
            setDisplay2EnhanceMode(0, SystemProperties.getInt("persist.sys.disp_enhance_mode", 0));
        } else {
            this.mCanSetDisp2Enhance = false;
        }
        initOutputState();
    }

    private void initOutputState() {
        int i;
        this.mMainDispToDev0PlugIn = new MainDispToDev0PlugIn(this);
        this.mMainDispToDev0PlugInExt = new MainDispToDev0PlugInExt(this);
        this.mMainDispToDev0PlugOut = new MainDispToDev0PlugOut(this);
        this.mMainDispToDev1PlugIn = new MainDispToDev1PlugIn(this);
        this.mMainDispToDev1PlugOut = new MainDispToDev1PlugOut(this);
        this.mDualDisplayOutput = new DualDisplayOutput(this);
        String[] devicesDefFormat = getDevicesDefFormat();
        int i2 = 0;
        int i3 = 0;
        while (true) {
            if (i3 >= devicesDefFormat.length) {
                break;
            }
            DispDevice dispDevice = new DispDevice();
            dispDevice.mType = DisplayManager.getDisplayTypeFromFormat(Integer.valueOf(devicesDefFormat[i3], 16).intValue());
            dispDevice.mHotplugSupport = getHotplugSupport(dispDevice.mType);
            dispDevice.mRevertPlugStateType = getRevertHotplugType(dispDevice.mType);
            Log.d(TAG, "device[" + i3 + "]: type=" + dispDevice.mType + ", hotPlugSupport=" + dispDevice.mHotplugSupport + ", RPST=" + dispDevice.mRevertPlugStateType);
            this.mDispDevices.add(i3, dispDevice);
            i3++;
        }
        DispDevice dispDeviceByType = getDispDeviceByType(this.mDm.getDisplayOutputType(1));
        if (this.mDispDevices.contains(dispDeviceByType)) {
            int iIndexOf = this.mDispDevices.indexOf(dispDeviceByType);
            Log.d(TAG, "mainDispDevice[" + dispDeviceByType.mType + "," + iIndexOf + "]");
            i = iIndexOf;
            i2 = 1;
        } else {
            Log.e(TAG, "main: initOutputState maybe failed, fixme!!!");
            i = 0;
        }
        int displayOutputType = this.mDm.getDisplayOutputType(2);
        DispDevice dispDeviceByType2 = getDispDeviceByType(displayOutputType);
        if (this.mDispDevices.contains(dispDeviceByType2)) {
            i2++;
            Log.d(TAG, "extDispDevice[" + dispDeviceByType2.mType + "," + this.mDispDevices.indexOf(dispDeviceByType2) + "]");
        } else {
            Log.d(TAG, "no external display for the type[" + displayOutputType + "]");
        }
        Log.d(TAG, "currentDispNum=" + i2 + ", mainPriority=" + i);
        if (1 == i2) {
            if (1 == i) {
                this.mDispOutputState = this.mMainDispToDev1PlugIn;
                return;
            } else {
                this.mDispOutputState = this.mMainDispToDev0PlugIn;
                return;
            }
        }
        if (2 == i2) {
            this.mDispOutputState = this.mDualDisplayOutput;
        } else {
            Log.d(TAG, "currentDispNum is " + String.valueOf(i2));
            this.mDispOutputState = null;
        }
    }

    public DispOutputState getMainDispToDev0PlugIn() {
        return this.mMainDispToDev0PlugIn;
    }

    public DispOutputState getMainDispToDev0PlugInExt() {
        return this.mMainDispToDev0PlugInExt;
    }

    public DispOutputState getMainDispToDev0PlugOut() {
        return this.mMainDispToDev0PlugOut;
    }

    public DispOutputState getMainDispToDev1PlugIn() {
        return this.mMainDispToDev1PlugIn;
    }

    public DispOutputState getMainDispToDev1PlugOut() {
        return this.mMainDispToDev1PlugOut;
    }

    public DispOutputState getDualDisplayOutput() {
        return this.mDualDisplayOutput;
    }

    public void setOutputState(DispOutputState dispOutputState) {
        Log.d(TAG, "setOutputState: " + dispOutputState);
        this.mDispOutputState = dispOutputState;
    }

    public int setDisplayOutput(int i, int i2) {
        Log.d(TAG, "setDispOutput: disp=" + i + ", dispFormat=" + i2);
        return this.mDm.setDisplayOutput(i, i2);
    }

    private DispDevice getDispDeviceByType(int i) {
        for (int i2 = 0; i2 < this.mDispDevices.size(); i2++) {
            DispDevice dispDevice = this.mDispDevices.get(i2);
            if (i == dispDevice.mType) {
                return dispDevice;
            }
        }
        return null;
    }

    private int getDispTypeByPriority(int i) {
        if (this.mDispDevices.size() <= i) {
            return 0;
        }
        return this.mDispDevices.get(i).mType;
    }

    private synchronized void dispDevicePlugChanged(DispDevice dispDevice, boolean z) {
        int dispTypeByPriority = dispDevice.mType;
        int iIndexOf = this.mDispDevices.indexOf(dispDevice);
        if (!z && iIndexOf == 0) {
            dispTypeByPriority = getDispTypeByPriority(1);
        }
        int iMakeDisplayFormat = this.mDm.makeDisplayFormat(dispTypeByPriority, 255);
        Log.d(TAG, "plug=" + z + ", device[" + iIndexOf + "," + dispDevice.mType + ", " + iMakeDisplayFormat + "]");
        DispOutputState dispOutputState = this.mDispOutputState;
        if (dispOutputState != null) {
            dispOutputState.devicePlugChanged(iMakeDisplayFormat, iIndexOf, z);
        }
    }

    public synchronized void notifyDisplayDevicePlugedChanged(int i, boolean z) {
        DispDevice dispDeviceByType = getDispDeviceByType(i);
        hotplugTips(i, z);
        if (dispDeviceByType == null) {
            return;
        }
        if (dispDeviceByType.mHotplugSupport) {
            if (dispDeviceByType.mRevertPlugStateType != 0 && true == z) {
                dispDevicePlugChanged(getDispDeviceByType(dispDeviceByType.mRevertPlugStateType), false);
            }
            dispDevicePlugChanged(dispDeviceByType, z);
            if (dispDeviceByType.mRevertPlugStateType != 0 && !z) {
                dispDevicePlugChanged(getDispDeviceByType(dispDeviceByType.mRevertPlugStateType), true);
            }
        }
    }

    private int getRevertHotplugType(int i) {
        if (i == 2) {
            return SystemProperties.getInt("persist.sys.cvbs_rvthpd", 0);
        }
        if (i == 4) {
            return SystemProperties.getInt("persist.sys.hdmi_rvthpd", 0);
        }
        if (i != 8) {
            return 0;
        }
        return SystemProperties.getInt("persist.sys.vga_rvthpd", 0);
    }

    private boolean getHotplugSupport(int i) {
        int i2;
        if (i == 2) {
            i2 = SystemProperties.getInt("persist.sys.cvbs_hpd", 0);
        } else if (i == 4) {
            i2 = SystemProperties.getInt("persist.sys.hdmi_hpd", 0);
        } else {
            if (i != 8) {
                return false;
            }
            i2 = SystemProperties.getInt("persist.sys.vga_hpd", 0);
        }
        return 1 == i2;
    }

    private String[] getDevicesDefFormat() {
        ArrayList arrayList = new ArrayList();
        String str = SystemProperties.get("persist.sys.disp_dev0", "0");
        Log.d(TAG, "prop_name[persist.sys.disp_dev0]: " + str);
        int i = 0;
        while ("0" != str) {
            arrayList.add(str);
            i++;
            String str2 = "persist.sys.disp_dev" + i;
            String str3 = SystemProperties.get(str2, "0");
            Log.d(TAG, "prop_name[" + str2 + "]: " + str3);
            str = str3;
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    private void hotplugTips(int i, boolean z) {
        final String str;
        if (this.mBootCompleted) {
            String str2 = z ? " has been plugged in" : " has been plugged out";
            if (i == 1) {
                str = "LCD" + str2;
            } else if (i == 2) {
                str = "CVBS" + str2;
            } else if (i == 4) {
                str = "HDMI" + str2;
            } else if (i == 8) {
                str = "VGA" + str2;
            } else {
                str = "unknow" + str2;
            }
            this.mHandler.post(new Runnable() { // from class: android.hardware.display.DisplayManagerPolicy2.2
                @Override // java.lang.Runnable
                public void run() {
                    Toast.makeText(DisplayManagerPolicy2.this.mContext, str, 1).show();
                }
            });
        }
    }

    public int getDisplay2EnhanceMode(int i) {
        int i2 = -1;
        if (!this.mCanSetDisp2Enhance) {
            return -1;
        }
        try {
            FileWriter fileWriter = new FileWriter(this.mDisp2EnhanceIdFile);
            fileWriter.write(String.valueOf(i));
            fileWriter.close();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(this.mDisp2EnhanceModeFile));
            i2 = Integer.parseInt(bufferedReader.readLine());
            bufferedReader.close();
            return i2;
        } catch (IOException e) {
            e.printStackTrace();
            return i2;
        }
    }

    public int setDisplay2EnhanceMode(int i, int i2) {
        if (!this.mCanSetDisp2Enhance) {
            return -1;
        }
        try {
            FileWriter fileWriter = new FileWriter(this.mDisp2EnhanceIdFile);
            fileWriter.write(String.valueOf(i));
            fileWriter.close();
            FileWriter fileWriter2 = new FileWriter(this.mDisp2EnhanceModeFile);
            fileWriter2.write(String.valueOf(i2));
            fileWriter2.close();
            SystemProperties.set("persist.sys.disp_enhance_mode", String.valueOf(i2));
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
