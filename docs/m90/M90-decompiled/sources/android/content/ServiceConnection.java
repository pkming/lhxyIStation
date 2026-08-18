package android.content;

import android.os.IBinder;

/* JADX INFO: loaded from: classes.dex */
public interface ServiceConnection {
    void onServiceConnected(ComponentName componentName, IBinder iBinder);

    void onServiceDisconnected(ComponentName componentName);
}
