package android.os;

import android.os.IDynamicPManager;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class DynamicPManager {
    public static final int CPU_MODE_NORMAL = 2;
    public static final int CPU_MODE_PERFORMENCE = 1;
    public static final String DPM_SERVICE = "DynamicPManager";
    private static final String TAG = "DynamicPManager";
    private IBinder binder;
    private IDynamicPManager service = IDynamicPManager.Stub.asInterface(ServiceManager.getService("DynamicPManager"));

    public void acquireCpuFreqLock(int i) {
        if (this.binder == null) {
            Log.d("DynamicPManager", "acquireCpuFreqLock");
            Binder binder = new Binder();
            this.binder = binder;
            try {
                this.service.acquireCpuFreqLock(binder, i);
            } catch (RemoteException unused) {
            }
        }
    }

    public void releaseCpuFreqLock() {
        IBinder iBinder = this.binder;
        if (iBinder != null) {
            try {
                this.service.releaseCpuFreqLock(iBinder);
                this.binder = null;
            } catch (RemoteException unused) {
            }
        }
    }
}
