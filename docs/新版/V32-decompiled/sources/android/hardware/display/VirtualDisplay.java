package android.hardware.display;

import android.os.IBinder;
import android.view.Display;

/* JADX INFO: loaded from: classes.dex */
public final class VirtualDisplay {
    private final Display mDisplay;
    private final DisplayManagerGlobal mGlobal;
    private IBinder mToken;

    VirtualDisplay(DisplayManagerGlobal displayManagerGlobal, Display display, IBinder iBinder) {
        this.mGlobal = displayManagerGlobal;
        this.mDisplay = display;
        this.mToken = iBinder;
    }

    public Display getDisplay() {
        return this.mDisplay;
    }

    public void release() {
        IBinder iBinder = this.mToken;
        if (iBinder != null) {
            this.mGlobal.releaseVirtualDisplay(iBinder);
            this.mToken = null;
        }
    }

    public String toString() {
        return "VirtualDisplay{display=" + this.mDisplay + ", token=" + this.mToken + "}";
    }
}
