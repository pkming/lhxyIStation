package android.hardware.display;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.Surface;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class DisplayManager {
    public static final String ACTION_WIFI_DISPLAY_STATUS_CHANGED = "android.hardware.display.action.WIFI_DISPLAY_STATUS_CHANGED";
    private static final boolean DEBUG = false;
    public static final int DISPLAY_2D_DUAL_STREAM = 5;
    public static final int DISPLAY_2D_LEFT = 1;
    public static final int DISPLAY_2D_ORIGINAL = 0;
    public static final int DISPLAY_2D_TOP = 2;
    public static final int DISPLAY_3D_DUAL_STREAM = 6;
    public static final int DISPLAY_3D_LEFT_RIGHT_HDMI = 3;
    public static final int DISPLAY_3D_TOP_BOTTOM_HDMI = 4;
    public static final String DISPLAY_CATEGORY_PRESENTATION = "android.hardware.display.category.PRESENTATION";
    private static final int DISPLAY_CMD_GETBRIGHT = 38;
    private static final int DISPLAY_CMD_GETCONTRAST = 37;
    private static final int DISPLAY_CMD_GETHUE = 36;
    private static final int DISPLAY_CMD_GETMARGIN_H = 40;
    private static final int DISPLAY_CMD_GETMARGIN_W = 39;
    private static final int DISPLAY_CMD_GETOUTPUTMODE = 34;
    private static final int DISPLAY_CMD_GETOUTPUTTYPE = 33;
    private static final int DISPLAY_CMD_GETSATURATION = 35;
    private static final int DISPLAY_CMD_GETSUPPORT3DMODE = 32;
    private static final int DISPLAY_CMD_IS_SUPPORT_HDMI_MODE = 25;
    private static final int DISPLAY_CMD_SET3DLAYEROFFSET = 12;
    private static final int DISPLAY_CMD_SET3DMODE = 1;
    private static final int DISPLAY_CMD_SETBRIGHT = 11;
    private static final int DISPLAY_CMD_SETCONTRAST = 10;
    private static final int DISPLAY_CMD_SETHUE = 9;
    private static final int DISPLAY_CMD_SETMARGIN = 7;
    private static final int DISPLAY_CMD_SETOUTPUTMODE = 6;
    private static final int DISPLAY_CMD_SETSATURATION = 8;
    public static final int DISPLAY_OUTPUT_MODE_MASK = 255;
    public static final int DISPLAY_OUTPUT_TYPE_HDMI = 4;
    public static final int DISPLAY_OUTPUT_TYPE_LCD = 1;
    public static final int DISPLAY_OUTPUT_TYPE_MASK = 65280;
    public static final int DISPLAY_OUTPUT_TYPE_NONE = 0;
    public static final int DISPLAY_OUTPUT_TYPE_TV = 2;
    public static final int DISPLAY_OUTPUT_TYPE_VGA = 8;
    public static final int DISPLAY_TVFORMAT_1080I_50HZ = 6;
    public static final int DISPLAY_TVFORMAT_1080I_60HZ = 7;
    public static final int DISPLAY_TVFORMAT_1080P_24HZ = 8;
    public static final int DISPLAY_TVFORMAT_1080P_50HZ = 9;
    public static final int DISPLAY_TVFORMAT_1080P_60HZ = 10;
    public static final int DISPLAY_TVFORMAT_3840_2160P_24HZ = 30;
    public static final int DISPLAY_TVFORMAT_3840_2160P_25HZ = 29;
    public static final int DISPLAY_TVFORMAT_3840_2160P_30HZ = 28;
    public static final int DISPLAY_TVFORMAT_480I = 0;
    public static final int DISPLAY_TVFORMAT_480P = 2;
    public static final int DISPLAY_TVFORMAT_576I = 1;
    public static final int DISPLAY_TVFORMAT_576P = 3;
    public static final int DISPLAY_TVFORMAT_720P_50HZ = 4;
    public static final int DISPLAY_TVFORMAT_720P_60HZ = 5;
    public static final int DISPLAY_TVFORMAT_NTSC = 14;
    public static final int DISPLAY_TVFORMAT_NTSC_CVBS_SVIDEO = 16;
    public static final int DISPLAY_TVFORMAT_NTSC_SVIDEO = 15;
    public static final int DISPLAY_TVFORMAT_PAL = 11;
    public static final int DISPLAY_TVFORMAT_PAL_CVBS_SVIDEO = 13;
    public static final int DISPLAY_TVFORMAT_PAL_M = 17;
    public static final int DISPLAY_TVFORMAT_PAL_M_CVBS_SVIDEO = 19;
    public static final int DISPLAY_TVFORMAT_PAL_M_SVIDEO = 18;
    public static final int DISPLAY_TVFORMAT_PAL_NC = 20;
    public static final int DISPLAY_TVFORMAT_PAL_NC_CVBS_SVIDEO = 22;
    public static final int DISPLAY_TVFORMAT_PAL_NC_SVIDEO = 21;
    public static final int DISPLAY_TVFORMAT_PAL_SVIDEO = 12;
    public static final int DISPLAY_VGA_FORMAT_1024x768P_60HZ = 2;
    public static final int DISPLAY_VGA_FORMAT_1280x768P_60HZ = 3;
    public static final int DISPLAY_VGA_FORMAT_1280x800P_60HZ = 4;
    public static final int DISPLAY_VGA_FORMAT_1366x768P_60HZ = 5;
    public static final int DISPLAY_VGA_FORMAT_1440x900P_60HZ = 6;
    public static final int DISPLAY_VGA_FORMAT_1920x1080P_60HZ = 7;
    public static final int DISPLAY_VGA_FORMAT_1920x1200P_60HZ = 8;
    public static final int DISPLAY_VGA_FORMAT_640x480P_60HZ = 0;
    public static final int DISPLAY_VGA_FORMAT_800x600P_60HZ = 1;
    public static final String EXTRA_HDMISTATUS = "hdmistatus";
    public static final String EXTRA_WIFI_DISPLAY_STATUS = "android.hardware.display.extra.WIFI_DISPLAY_STATUS";
    private static final String TAG = "DisplayManager";
    public static final int VIRTUAL_DISPLAY_FLAG_PRESENTATION = 2;
    public static final int VIRTUAL_DISPLAY_FLAG_PUBLIC = 1;
    public static final int VIRTUAL_DISPLAY_FLAG_SECURE = 4;
    private final Context mContext;
    private final Object mLock = new Object();
    private final SparseArray<Display> mDisplays = new SparseArray<>();
    private final ArrayList<Display> mTempDisplays = new ArrayList<>();
    private final String RSL_FILENAME = "/mnt/Reserve0/disp_rsl.fex";
    private final String MARGIN_FILENAME = "/mnt/Reserve0/disp_margin.fex";
    private final DisplayManagerGlobal mGlobal = DisplayManagerGlobal.getInstance();

    public interface DisplayListener {
        void onDisplayAdded(int i);

        void onDisplayChanged(int i);

        void onDisplayRemoved(int i);
    }

    public static int getDisplayModeFromFormat(int i) {
        return i & 255;
    }

    public static int getDisplayTypeFromFormat(int i) {
        return (i & 65280) >> 8;
    }

    public int makeDisplayFormat(int i, int i2) {
        return ((i << 8) & 65280) | (i2 & 255);
    }

    public DisplayManager(Context context) {
        this.mContext = context;
    }

    public Display getDisplay(int i) {
        Display orCreateDisplayLocked;
        synchronized (this.mLock) {
            orCreateDisplayLocked = getOrCreateDisplayLocked(i, false);
        }
        return orCreateDisplayLocked;
    }

    public Display[] getDisplays() {
        return getDisplays(null);
    }

    public Display[] getDisplays(String str) {
        Display[] displayArr;
        int[] displayIds = this.mGlobal.getDisplayIds();
        synchronized (this.mLock) {
            try {
                if (str == null) {
                    addAllDisplaysLocked(this.mTempDisplays, displayIds);
                } else if (str.equals("android.hardware.display.category.PRESENTATION")) {
                    addPresentationDisplaysLocked(this.mTempDisplays, displayIds, 3);
                    addPresentationDisplaysLocked(this.mTempDisplays, displayIds, 2);
                    addPresentationDisplaysLocked(this.mTempDisplays, displayIds, 4);
                    addPresentationDisplaysLocked(this.mTempDisplays, displayIds, 5);
                }
                ArrayList<Display> arrayList = this.mTempDisplays;
                displayArr = (Display[]) arrayList.toArray(new Display[arrayList.size()]);
            } finally {
                this.mTempDisplays.clear();
            }
        }
        return displayArr;
    }

    private void addAllDisplaysLocked(ArrayList<Display> arrayList, int[] iArr) {
        for (int i : iArr) {
            Display orCreateDisplayLocked = getOrCreateDisplayLocked(i, true);
            if (orCreateDisplayLocked != null) {
                arrayList.add(orCreateDisplayLocked);
            }
        }
    }

    private void addPresentationDisplaysLocked(ArrayList<Display> arrayList, int[] iArr, int i) {
        for (int i2 : iArr) {
            Display orCreateDisplayLocked = getOrCreateDisplayLocked(i2, true);
            if (orCreateDisplayLocked != null && (orCreateDisplayLocked.getFlags() & 8) != 0 && orCreateDisplayLocked.getType() == i) {
                arrayList.add(orCreateDisplayLocked);
            }
        }
    }

    private Display getOrCreateDisplayLocked(int i, boolean z) {
        Display display = this.mDisplays.get(i);
        if (display == null) {
            Display compatibleDisplay = this.mGlobal.getCompatibleDisplay(i, this.mContext.getDisplayAdjustments(i));
            if (compatibleDisplay == null) {
                return compatibleDisplay;
            }
            this.mDisplays.put(i, compatibleDisplay);
            return compatibleDisplay;
        }
        if (z || display.isValid()) {
            return display;
        }
        return null;
    }

    public void registerDisplayListener(DisplayListener displayListener, Handler handler) {
        this.mGlobal.registerDisplayListener(displayListener, handler);
    }

    public void unregisterDisplayListener(DisplayListener displayListener) {
        this.mGlobal.unregisterDisplayListener(displayListener);
    }

    public void startWifiDisplayScan() {
        this.mGlobal.startWifiDisplayScan();
    }

    public void stopWifiDisplayScan() {
        this.mGlobal.stopWifiDisplayScan();
    }

    public void connectWifiDisplay(String str) {
        this.mGlobal.connectWifiDisplay(str);
    }

    public void pauseWifiDisplay() {
        this.mGlobal.pauseWifiDisplay();
    }

    public void resumeWifiDisplay() {
        this.mGlobal.resumeWifiDisplay();
    }

    public void disconnectWifiDisplay() {
        this.mGlobal.disconnectWifiDisplay();
    }

    public void renameWifiDisplay(String str, String str2) {
        this.mGlobal.renameWifiDisplay(str, str2);
    }

    public void forgetWifiDisplay(String str) {
        this.mGlobal.forgetWifiDisplay(str);
    }

    public WifiDisplayStatus getWifiDisplayStatus() {
        return this.mGlobal.getWifiDisplayStatus();
    }

    public VirtualDisplay createVirtualDisplay(String str, int i, int i2, int i3, Surface surface, int i4) {
        return this.mGlobal.createVirtualDisplay(this.mContext, str, i, i2, i3, surface, i4);
    }

    public int getDisplaySupport3DMode(int i) {
        return this.mGlobal.getDisplayParameter(i, 32, 0, 0);
    }

    public int getDisplayOutputType(int i) {
        return this.mGlobal.getDisplayParameter(i, 33, 0, 0);
    }

    public int getDisplayOutputMode(int i) {
        return this.mGlobal.getDisplayParameter(i, 34, 0, 0);
    }

    public int getDisplaySaturation(int i) {
        return this.mGlobal.getDisplayParameter(i, 35, 0, 0);
    }

    public int getDisplayhue(int i) {
        return this.mGlobal.getDisplayParameter(i, 36, 0, 0);
    }

    public int getDisplayContrast(int i) {
        return this.mGlobal.getDisplayParameter(i, 37, 0, 0);
    }

    public int getDisplayBright(int i) {
        return this.mGlobal.getDisplayParameter(i, 38, 0, 0);
    }

    public int getDisplayPercent(int i) {
        int displayParameter = this.mGlobal.getDisplayParameter(i, 39, 0, 0);
        int displayParameter2 = this.mGlobal.getDisplayParameter(i, 40, 0, 0);
        return displayParameter > displayParameter2 ? displayParameter : displayParameter2;
    }

    public int[] getDisplayMargin(int i) {
        return new int[]{this.mGlobal.getDisplayParameter(i, 39, 0, 0), this.mGlobal.getDisplayParameter(i, 40, 0, 0)};
    }

    private int saveDisplayMargin(int i, int i2, int i3) {
        File file = new File("/mnt/Reserve0/disp_margin.fex");
        if (!file.exists()) {
            Log.w(TAG, "file: /mnt/Reserve0/disp_margin.fex is not exists");
            try {
                file.createNewFile();
            } catch (IOException unused) {
                Log.d(TAG, "file: creat file failed");
            }
        }
        String str = new String(Integer.toHexString(i2) + "\n" + Integer.toHexString(i3) + "\n");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(str.getBytes());
            fileOutputStream.flush();
            fileOutputStream.getFD().sync();
            fileOutputStream.close();
            return 0;
        } catch (IOException unused2) {
            return 0;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x0067  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x006f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int saveDisplayResolution(int r9, int r10, int r11) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 225
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.hardware.display.DisplayManager.saveDisplayResolution(int, int, int):int");
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v0, types: [java.lang.String[]] */
    /* JADX WARN: Type inference failed for: r3v1 */
    /* JADX WARN: Type inference failed for: r3v10 */
    /* JADX WARN: Type inference failed for: r3v12 */
    /* JADX WARN: Type inference failed for: r3v13 */
    /* JADX WARN: Type inference failed for: r3v14, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r3v15, types: [int] */
    /* JADX WARN: Type inference failed for: r3v16 */
    /* JADX WARN: Type inference failed for: r3v17 */
    /* JADX WARN: Type inference failed for: r3v18 */
    /* JADX WARN: Type inference failed for: r3v19 */
    /* JADX WARN: Type inference failed for: r3v2 */
    /* JADX WARN: Type inference failed for: r3v20 */
    /* JADX WARN: Type inference failed for: r3v3, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r3v4, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r3v5 */
    /* JADX WARN: Type inference failed for: r3v6 */
    /* JADX WARN: Type inference failed for: r3v7 */
    /* JADX WARN: Type inference failed for: r3v8 */
    /* JADX WARN: Type inference failed for: r3v9 */
    /* JADX WARN: Type inference failed for: r4v0 */
    /* JADX WARN: Type inference failed for: r4v1 */
    /* JADX WARN: Type inference failed for: r4v10 */
    /* JADX WARN: Type inference failed for: r4v11 */
    /* JADX WARN: Type inference failed for: r4v12 */
    /* JADX WARN: Type inference failed for: r4v13 */
    /* JADX WARN: Type inference failed for: r4v14 */
    /* JADX WARN: Type inference failed for: r4v15 */
    /* JADX WARN: Type inference failed for: r4v2 */
    /* JADX WARN: Type inference failed for: r4v3 */
    /* JADX WARN: Type inference failed for: r4v4 */
    /* JADX WARN: Type inference failed for: r4v5 */
    /* JADX WARN: Type inference failed for: r4v6 */
    /* JADX WARN: Type inference failed for: r4v7 */
    /* JADX WARN: Type inference failed for: r4v8, types: [int] */
    /* JADX WARN: Type inference failed for: r4v9 */
    public int getDisplayResolution(int i) throws Throwable {
        File file = new File("/mnt/Reserve0/disp_rsl.fex");
        ?? r2 = new String[3];
        ?? r4 = 0;
        r4 = 0;
        r4 = 0;
        r4 = 0;
        r4 = 0;
        r4 = 0;
        if (!file.exists()) {
            Log.w(TAG, "file: /mnt/Reserve0/disp_rsl.fex is not exists");
            try {
                file.createNewFile();
            } catch (IOException unused) {
                Log.d(TAG, "file: creat file failed");
            }
        } else {
            ?? r3 = 0;
            ?? r32 = 0;
            r3 = 0;
            try {
                try {
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                        int i2 = 0;
                        while (true) {
                            if (i2 >= 3) {
                                break;
                            }
                            try {
                                r2[i2] = bufferedReader.readLine();
                                r32 = r2[i2];
                                if (r32 != 0) {
                                    r32 = Integer.parseInt(r2[i2], 16);
                                    if (getDisplayTypeFromFormat(r32) == i) {
                                        r4 = r32;
                                        break;
                                    }
                                }
                                i2++;
                                r32 = r32;
                            } catch (IOException e) {
                                e = e;
                                r3 = bufferedReader;
                                e.printStackTrace();
                                if (r3 != 0) {
                                    r3.close();
                                    r3 = r3;
                                    r4 = r4;
                                }
                                return r4;
                            } catch (Throwable th) {
                                th = th;
                                r3 = bufferedReader;
                                if (r3 != 0) {
                                    try {
                                        r3.close();
                                    } catch (IOException unused2) {
                                    }
                                }
                                throw th;
                            }
                        }
                        bufferedReader.close();
                        bufferedReader.close();
                        r3 = r32;
                        r4 = r4;
                    } catch (IOException unused3) {
                    }
                } catch (IOException e2) {
                    e = e2;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        }
        return r4;
    }

    public int setDisplay3DMode(int i, int i2) {
        return this.mGlobal.setDisplayParameter(i, 1, i2, 0, 0);
    }

    public int getDisplayOutput(int i) {
        return makeDisplayFormat(getDisplayOutputType(i), getDisplayOutputMode(i));
    }

    public int setDisplayOutput(int i, int i2) {
        return setDisplayOutputMode(i, (65280 & i2) >> 8, i2 & 255);
    }

    public int setDisplayOutputMode(int i, int i2, int i3) {
        return this.mGlobal.setDisplayParameter(i, 6, i2, i3, 0);
    }

    public boolean isSupportHdmiMode(int i, int i2) {
        return this.mGlobal.getDisplayParameter(i, 25, i2, 0) == 1;
    }

    public int setDisplayPercent(int i, int i2) {
        int displayParameter = this.mGlobal.setDisplayParameter(i, 7, i2, i2, 0);
        saveDisplayMargin(i, i2, i2);
        return displayParameter;
    }

    public int setDisplayMargin(int i, int i2, int i3) {
        int displayParameter = this.mGlobal.setDisplayParameter(i, 7, i2, i3, 0);
        saveDisplayMargin(i, i2, i3);
        return displayParameter;
    }

    public int setDisplaySaturation(int i, int i2) {
        return this.mGlobal.setDisplayParameter(i, 8, i2, 0, 0);
    }

    public int setDisplayHue(int i, int i2) {
        return this.mGlobal.setDisplayParameter(i, 9, i2, 0, 0);
    }

    public int setDisplayContrast(int i, int i2) {
        return this.mGlobal.setDisplayParameter(i, 10, i2, 0, 0);
    }

    public int setDisplayBright(int i, int i2) {
        return this.mGlobal.setDisplayParameter(i, 11, i2, 0, 0);
    }

    public int setDisplay3DLayerOffset(int i, int i2) {
        return this.mGlobal.setDisplayParameter(i, 12, i2, 0, 0);
    }
}
