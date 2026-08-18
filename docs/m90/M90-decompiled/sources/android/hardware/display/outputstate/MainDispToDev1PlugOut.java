package android.hardware.display.outputstate;

import android.hardware.display.DisplayManagerPolicy2;

/* JADX INFO: loaded from: classes.dex */
public class MainDispToDev1PlugOut implements DispOutputState {
    DisplayManagerPolicy2 mDMP;

    public MainDispToDev1PlugOut(DisplayManagerPolicy2 displayManagerPolicy2) {
        this.mDMP = displayManagerPolicy2;
    }

    @Override // android.hardware.display.outputstate.DispOutputState
    public void devicePlugChanged(int i, int i2, boolean z) {
        if (true == z) {
            if (i2 == 0) {
                this.mDMP.setDisplayOutput(1, i);
                DisplayManagerPolicy2 displayManagerPolicy2 = this.mDMP;
                displayManagerPolicy2.setOutputState(displayManagerPolicy2.getMainDispToDev0PlugIn());
            } else if (1 == i2) {
                DisplayManagerPolicy2 displayManagerPolicy22 = this.mDMP;
                displayManagerPolicy22.setOutputState(displayManagerPolicy22.getMainDispToDev1PlugIn());
            }
        }
    }
}
