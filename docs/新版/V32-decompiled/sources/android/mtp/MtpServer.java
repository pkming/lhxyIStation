package android.mtp;

/* JADX INFO: loaded from: classes.dex */
public class MtpServer implements Runnable {
    private int mNativeContext;

    private final native void native_add_storage(MtpStorage mtpStorage);

    private final native void native_cleanup();

    private final native void native_remove_storage(int i);

    private final native void native_run();

    private final native void native_send_object_added(int i);

    private final native void native_send_object_removed(int i);

    private final native void native_setup(MtpDatabase mtpDatabase, boolean z);

    static {
        System.loadLibrary("media_jni");
    }

    public MtpServer(MtpDatabase mtpDatabase, boolean z) {
        native_setup(mtpDatabase, z);
    }

    public void start() {
        new Thread(this, "MtpServer").start();
    }

    @Override // java.lang.Runnable
    public void run() {
        native_run();
        native_cleanup();
    }

    public void sendObjectAdded(int i) {
        native_send_object_added(i);
    }

    public void sendObjectRemoved(int i) {
        native_send_object_removed(i);
    }

    public void addStorage(MtpStorage mtpStorage) {
        native_add_storage(mtpStorage);
    }

    public void removeStorage(MtpStorage mtpStorage) {
        native_remove_storage(mtpStorage.getStorageId());
    }
}
