package android.hardware.display.outputstate;

import android.hardware.display.DisplayManagerPolicy2;

/* JADX INFO: loaded from: classes.dex */
public class DualDisplayOutput implements DispOutputState {
    DisplayManagerPolicy2 mDMP;

    public DualDisplayOutput(DisplayManagerPolicy2 displayManagerPolicy2) {
        this.mDMP = displayManagerPolicy2;
    }

    @Override // android.hardware.display.outputstate.DispOutputState
    public void devicePlugChanged(int i, int i2, boolean z) {
        if (i2 == 0) {
            this.mDMP.setDisplayOutput(1, i);
            this.mDMP.setDisplayOutput(2, 0);
            DisplayManagerPolicy2 displayManagerPolicy2 = this.mDMP;
            displayManagerPolicy2.setOutputState(displayManagerPolicy2.getMainDispToDev1PlugIn());
            return;
        }
        if (1 == i2) {
            this.mDMP.setDisplayOutput(2, 0);
            DisplayManagerPolicy2 displayManagerPolicy22 = this.mDMP;
            displayManagerPolicy22.setOutputState(displayManagerPolicy22.getMainDispToDev0PlugIn());
        }
    }
}
