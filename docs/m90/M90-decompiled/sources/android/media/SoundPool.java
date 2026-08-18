package android.media;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.SystemProperties;
import android.util.AndroidRuntimeException;
import android.util.Log;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class SoundPool {
    private final SoundPoolDelegate mImpl;

    public interface OnLoadCompleteListener {
        void onLoadComplete(SoundPool soundPool, int i, int i2);
    }

    interface SoundPoolDelegate {
        void autoPause();

        void autoResume();

        int load(Context context, int i, int i2);

        int load(AssetFileDescriptor assetFileDescriptor, int i);

        int load(FileDescriptor fileDescriptor, long j, long j2, int i);

        int load(String str, int i);

        void pause(int i);

        int play(int i, float f, float f2, int i2, int i3, float f3);

        void release();

        void resume(int i);

        void setLoop(int i, int i2);

        void setOnLoadCompleteListener(OnLoadCompleteListener onLoadCompleteListener);

        void setPriority(int i, int i2);

        void setRate(int i, float f);

        void setVolume(int i, float f);

        void setVolume(int i, float f, float f2);

        void stop(int i);

        boolean unload(int i);
    }

    static class SoundPoolStub implements SoundPoolDelegate {
        @Override // android.media.SoundPool.SoundPoolDelegate
        public final void autoPause() {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final void autoResume() {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public int load(Context context, int i, int i2) {
            return 0;
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public int load(AssetFileDescriptor assetFileDescriptor, int i) {
            return 0;
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public int load(FileDescriptor fileDescriptor, long j, long j2, int i) {
            return 0;
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public int load(String str, int i) {
            return 0;
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final void pause(int i) {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final int play(int i, float f, float f2, int i2, int i3, float f3) {
            return 0;
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final void release() {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final void resume(int i) {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final void setLoop(int i, int i2) {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public void setOnLoadCompleteListener(OnLoadCompleteListener onLoadCompleteListener) {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final void setPriority(int i, int i2) {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final void setRate(int i, float f) {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public void setVolume(int i, float f) {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final void setVolume(int i, float f, float f2) {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final void stop(int i) {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final boolean unload(int i) {
            return true;
        }
    }

    public SoundPool(int i, int i2, int i3) {
        if (SystemProperties.getBoolean("config.disable_media", false)) {
            this.mImpl = new SoundPoolStub();
        } else {
            this.mImpl = new SoundPoolImpl(this, i, i2, i3);
        }
    }

    public int load(String str, int i) {
        return this.mImpl.load(str, i);
    }

    public int load(Context context, int i, int i2) {
        return this.mImpl.load(context, i, i2);
    }

    public int load(AssetFileDescriptor assetFileDescriptor, int i) {
        return this.mImpl.load(assetFileDescriptor, i);
    }

    public int load(FileDescriptor fileDescriptor, long j, long j2, int i) {
        return this.mImpl.load(fileDescriptor, j, j2, i);
    }

    public final boolean unload(int i) {
        return this.mImpl.unload(i);
    }

    public final int play(int i, float f, float f2, int i2, int i3, float f3) {
        return this.mImpl.play(i, f, f2, i2, i3, f3);
    }

    public final void pause(int i) {
        this.mImpl.pause(i);
    }

    public final void resume(int i) {
        this.mImpl.resume(i);
    }

    public final void autoPause() {
        this.mImpl.autoPause();
    }

    public final void autoResume() {
        this.mImpl.autoResume();
    }

    public final void stop(int i) {
        this.mImpl.stop(i);
    }

    public final void setVolume(int i, float f, float f2) {
        this.mImpl.setVolume(i, f, f2);
    }

    public void setVolume(int i, float f) {
        setVolume(i, f, f);
    }

    public final void setPriority(int i, int i2) {
        this.mImpl.setPriority(i, i2);
    }

    public final void setLoop(int i, int i2) {
        this.mImpl.setLoop(i, i2);
    }

    public final void setRate(int i, float f) {
        this.mImpl.setRate(i, f);
    }

    public void setOnLoadCompleteListener(OnLoadCompleteListener onLoadCompleteListener) {
        this.mImpl.setOnLoadCompleteListener(onLoadCompleteListener);
    }

    public final void release() {
        this.mImpl.release();
    }

    static class SoundPoolImpl implements SoundPoolDelegate {
        private static final boolean DEBUG = false;
        private static final int SAMPLE_LOADED = 1;
        private static final String TAG = "SoundPool";
        private EventHandler mEventHandler;
        private final Object mLock;
        private int mNativeContext;
        private OnLoadCompleteListener mOnLoadCompleteListener;
        private SoundPool mProxy;

        private final native int _load(FileDescriptor fileDescriptor, long j, long j2, int i);

        private final native int _load(String str, int i);

        private final native int native_setup(Object obj, int i, int i2, int i3);

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native void autoPause();

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native void autoResume();

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native void pause(int i);

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native int play(int i, float f, float f2, int i2, int i3, float f3);

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native void release();

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native void resume(int i);

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native void setLoop(int i, int i2);

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native void setPriority(int i, int i2);

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native void setRate(int i, float f);

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native void setVolume(int i, float f, float f2);

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native void stop(int i);

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native boolean unload(int i);

        static {
            System.loadLibrary("soundpool");
        }

        public SoundPoolImpl(SoundPool soundPool, int i, int i2, int i3) {
            if (native_setup(new WeakReference(this), i, i2, i3) != 0) {
                throw new RuntimeException("Native setup failed");
            }
            this.mLock = new Object();
            this.mProxy = soundPool;
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public int load(String str, int i) {
            if (str.startsWith("http:")) {
                return _load(str, i);
            }
            int i_load = 0;
            try {
                File file = new File(str);
                ParcelFileDescriptor parcelFileDescriptorOpen = ParcelFileDescriptor.open(file, 268435456);
                if (parcelFileDescriptorOpen == null) {
                    return 0;
                }
                i_load = _load(parcelFileDescriptorOpen.getFileDescriptor(), 0L, file.length(), i);
                parcelFileDescriptorOpen.close();
                return i_load;
            } catch (IOException unused) {
                Log.e(TAG, "error loading " + str);
                return i_load;
            }
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public int load(Context context, int i, int i2) {
            AssetFileDescriptor assetFileDescriptorOpenRawResourceFd = context.getResources().openRawResourceFd(i);
            if (assetFileDescriptorOpenRawResourceFd == null) {
                return 0;
            }
            int i_load = _load(assetFileDescriptorOpenRawResourceFd.getFileDescriptor(), assetFileDescriptorOpenRawResourceFd.getStartOffset(), assetFileDescriptorOpenRawResourceFd.getLength(), i2);
            try {
                assetFileDescriptorOpenRawResourceFd.close();
                return i_load;
            } catch (IOException unused) {
                return i_load;
            }
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public int load(AssetFileDescriptor assetFileDescriptor, int i) {
            if (assetFileDescriptor == null) {
                return 0;
            }
            long length = assetFileDescriptor.getLength();
            if (length < 0) {
                throw new AndroidRuntimeException("no length for fd");
            }
            return _load(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), length, i);
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public int load(FileDescriptor fileDescriptor, long j, long j2, int i) {
            return _load(fileDescriptor, j, j2, i);
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public void setVolume(int i, float f) {
            setVolume(i, f, f);
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public void setOnLoadCompleteListener(OnLoadCompleteListener onLoadCompleteListener) {
            synchronized (this.mLock) {
                if (onLoadCompleteListener != null) {
                    Looper looperMyLooper = Looper.myLooper();
                    if (looperMyLooper != null) {
                        this.mEventHandler = new EventHandler(this.mProxy, looperMyLooper);
                    } else {
                        Looper mainLooper = Looper.getMainLooper();
                        if (mainLooper != null) {
                            this.mEventHandler = new EventHandler(this.mProxy, mainLooper);
                        } else {
                            this.mEventHandler = null;
                        }
                    }
                } else {
                    this.mEventHandler = null;
                }
                this.mOnLoadCompleteListener = onLoadCompleteListener;
            }
        }

        private class EventHandler extends Handler {
            private SoundPool mSoundPool;

            public EventHandler(SoundPool soundPool, Looper looper) {
                super(looper);
                this.mSoundPool = soundPool;
            }

            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what == 1) {
                    synchronized (SoundPoolImpl.this.mLock) {
                        if (SoundPoolImpl.this.mOnLoadCompleteListener != null) {
                            SoundPoolImpl.this.mOnLoadCompleteListener.onLoadComplete(this.mSoundPool, message.arg1, message.arg2);
                        }
                    }
                    return;
                }
                Log.e(SoundPoolImpl.TAG, "Unknown message type " + message.what);
            }
        }

        private static void postEventFromNative(Object obj, int i, int i2, int i3, Object obj2) {
            EventHandler eventHandler;
            SoundPoolImpl soundPoolImpl = (SoundPoolImpl) ((WeakReference) obj).get();
            if (soundPoolImpl == null || (eventHandler = soundPoolImpl.mEventHandler) == null) {
                return;
            }
            soundPoolImpl.mEventHandler.sendMessage(eventHandler.obtainMessage(i, i2, i3, obj2));
        }

        protected void finalize() {
            release();
        }
    }
}
