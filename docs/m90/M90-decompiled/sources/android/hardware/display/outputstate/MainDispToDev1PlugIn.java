package android.hardware.display.outputstate;

import android.hardware.display.DisplayManagerPolicy2;
import android.os.SystemProperties;

/* JADX INFO: loaded from: classes.dex */
public class MainDispToDev1PlugIn implements DispOutputState {
    DisplayManagerPolicy2 mDMP;
    int mDispPolicy = Integer.valueOf(SystemProperties.get("persist.sys.disp_policy", "0"), 16).intValue();

    public MainDispToDev1PlugIn(DisplayManagerPolicy2 displayManagerPolicy2) {
        this.mDMP = displayManagerPolicy2;
    }

    @Override // android.hardware.display.outputstate.DispOutputState
    public void devicePlugChanged(int i, int i2, boolean z) {
        if (!z && 1 == i2) {
            DisplayManagerPolicy2 displayManagerPolicy2 = this.mDMP;
            displayManagerPolicy2.setOutputState(displayManagerPolicy2.getMainDispToDev1PlugOut());
            return;
        }
        if (true == z && i2 == 0) {
            if (1 == this.mDispPolicy) {
                this.mDMP.setDisplayOutput(1, i);
                DisplayManagerPolicy2 displayManagerPolicy22 = this.mDMP;
                displayManagerPolicy22.setOutputState(displayManagerPolicy22.getMainDispToDev0PlugInExt());
            } else if (this.mDMP.setDisplayOutput(2, i) == 0) {
                DisplayManagerPolicy2 displayManagerPolicy23 = this.mDMP;
                displayManagerPolicy23.setOutputState(displayManagerPolicy23.getDualDisplayOutput());
            } else {
                DisplayManagerPolicy2 displayManagerPolicy24 = this.mDMP;
                displayManagerPolicy24.setOutputState(displayManagerPolicy24.getMainDispToDev0PlugInExt());
            }
        }
    }
}
