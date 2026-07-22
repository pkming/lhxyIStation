package android.hardware.display.outputstate;

import android.hardware.display.DisplayManagerPolicy2;

/* JADX INFO: loaded from: classes.dex */
public class MainDispToDev0PlugInExt implements DispOutputState {
    DisplayManagerPolicy2 mDMP;

    public MainDispToDev0PlugInExt(DisplayManagerPolicy2 displayManagerPolicy2) {
        this.mDMP = displayManagerPolicy2;
    }

    @Override // android.hardware.display.outputstate.DispOutputState
    public void devicePlugChanged(int i, int i2, boolean z) {
        if (z) {
            return;
        }
        if (i2 != 0) {
            if (1 == i2) {
                this.mDMP.setDisplayOutput(2, 0);
                DisplayManagerPolicy2 displayManagerPolicy2 = this.mDMP;
                displayManagerPolicy2.setOutputState(displayManagerPolicy2.getMainDispToDev0PlugIn());
                return;
            }
            return;
        }
        if (this.mDMP.setDisplayOutput(1, i) == 0) {
            DisplayManagerPolicy2 displayManagerPolicy22 = this.mDMP;
            displayManagerPolicy22.setOutputState(displayManagerPolicy22.getMainDispToDev1PlugIn());
        } else {
            DisplayManagerPolicy2 displayManagerPolicy23 = this.mDMP;
            displayManagerPolicy23.setOutputState(displayManagerPolicy23.getMainDispToDev0PlugOut());
        }
    }
}
