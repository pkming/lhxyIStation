package android.hardware.usb;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class UsbManager {
    public static final String ACTION_USB_ACCESSORY_ATTACHED = "android.hardware.usb.action.USB_ACCESSORY_ATTACHED";
    public static final String ACTION_USB_ACCESSORY_DETACHED = "android.hardware.usb.action.USB_ACCESSORY_DETACHED";
    public static final String ACTION_USB_DEVICE_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    public static final String ACTION_USB_DEVICE_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    public static final String ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE";
    public static final String EXTRA_ACCESSORY = "accessory";
    public static final String EXTRA_DEVICE = "device";
    public static final String EXTRA_PERMISSION_GRANTED = "permission";
    private static final String TAG = "UsbManager";
    public static final String USB_CONFIGURED = "configured";
    public static final String USB_CONNECTED = "connected";
    public static final String USB_FUNCTION_ACCESSORY = "accessory";
    public static final String USB_FUNCTION_ADB = "adb";
    public static final String USB_FUNCTION_AUDIO_SOURCE = "audio_source";
    public static final String USB_FUNCTION_MASS_STORAGE = "mass_storage";
    public static final String USB_FUNCTION_MTP = "mtp";
    public static final String USB_FUNCTION_PTP = "ptp";
    public static final String USB_FUNCTION_RNDIS = "rndis";
    private final Context mContext;
    private final IUsbManager mService;

    public UsbManager(Context context, IUsbManager iUsbManager) {
        this.mContext = context;
        this.mService = iUsbManager;
    }

    public HashMap<String, UsbDevice> getDeviceList() {
        Bundle bundle = new Bundle();
        try {
            this.mService.getDeviceList(bundle);
            HashMap<String, UsbDevice> map = new HashMap<>();
            for (String str : bundle.keySet()) {
                map.put(str, (UsbDevice) bundle.get(str));
            }
            return map;
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in getDeviceList", e);
            return null;
        }
    }

    public UsbDeviceConnection openDevice(UsbDevice usbDevice) {
        try {
            String deviceName = usbDevice.getDeviceName();
            ParcelFileDescriptor parcelFileDescriptorOpenDevice = this.mService.openDevice(deviceName);
            if (parcelFileDescriptorOpenDevice == null) {
                return null;
            }
            UsbDeviceConnection usbDeviceConnection = new UsbDeviceConnection(usbDevice);
            boolean zOpen = usbDeviceConnection.open(deviceName, parcelFileDescriptorOpenDevice);
            parcelFileDescriptorOpenDevice.close();
            if (zOpen) {
                return usbDeviceConnection;
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "exception in UsbManager.openDevice", e);
            return null;
        }
    }

    public UsbAccessory[] getAccessoryList() {
        try {
            UsbAccessory currentAccessory = this.mService.getCurrentAccessory();
            if (currentAccessory == null) {
                return null;
            }
            return new UsbAccessory[]{currentAccessory};
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in getAccessoryList", e);
            return null;
        }
    }

    public ParcelFileDescriptor openAccessory(UsbAccessory usbAccessory) {
        try {
            return this.mService.openAccessory(usbAccessory);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in openAccessory", e);
            return null;
        }
    }

    public boolean hasPermission(UsbDevice usbDevice) {
        try {
            return this.mService.hasDevicePermission(usbDevice);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in hasPermission", e);
            return false;
        }
    }

    public boolean hasPermission(UsbAccessory usbAccessory) {
        try {
            return this.mService.hasAccessoryPermission(usbAccessory);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in hasPermission", e);
            return false;
        }
    }

    public void requestPermission(UsbDevice usbDevice, PendingIntent pendingIntent) {
        try {
            this.mService.requestDevicePermission(usbDevice, this.mContext.getPackageName(), pendingIntent);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in requestPermission", e);
        }
    }

    public void requestPermission(UsbAccessory usbAccessory, PendingIntent pendingIntent) {
        try {
            this.mService.requestAccessoryPermission(usbAccessory, this.mContext.getPackageName(), pendingIntent);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in requestPermission", e);
        }
    }

    private static boolean propertyContainsFunction(String str, String str2) {
        String str3 = SystemProperties.get(str, "");
        int iIndexOf = str3.indexOf(str2);
        if (iIndexOf < 0) {
            return false;
        }
        if (iIndexOf > 0 && str3.charAt(iIndexOf - 1) != ',') {
            return false;
        }
        int length = iIndexOf + str2.length();
        return length >= str3.length() || str3.charAt(length) == ',';
    }

    public boolean isFunctionEnabled(String str) {
        return propertyContainsFunction("sys.usb.config", str);
    }

    public String getDefaultFunction() {
        String str = SystemProperties.get("persist.sys.usb.config", "");
        int iIndexOf = str.indexOf(44);
        return iIndexOf > 0 ? str.substring(0, iIndexOf) : str;
    }

    public void setCurrentFunction(String str, boolean z) {
        try {
            this.mService.setCurrentFunction(str, z);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in setCurrentFunction", e);
        }
    }

    public void setMassStorageBackingFile(String str) {
        try {
            this.mService.setMassStorageBackingFile(str);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in setDefaultFunction", e);
        }
    }
}
