package android.hardware.input;

import android.content.Context;
import android.hardware.input.IInputDevicesChangedListener;
import android.hardware.input.IInputManager;
import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.InputDevice;
import android.view.InputEvent;
import com.android.internal.util.ArrayUtils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class InputManager {
    public static final String ACTION_QUERY_KEYBOARD_LAYOUTS = "android.hardware.input.action.QUERY_KEYBOARD_LAYOUTS";
    private static final boolean DEBUG = false;
    public static final int DEFAULT_POINTER_SPEED = 0;
    public static final int INJECT_INPUT_EVENT_MODE_ASYNC = 0;
    public static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH = 2;
    public static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT = 1;
    public static final int MAX_POINTER_SPEED = 7;
    public static final String META_DATA_KEYBOARD_LAYOUTS = "android.hardware.input.metadata.KEYBOARD_LAYOUTS";
    public static final int MIN_POINTER_SPEED = -7;
    private static final int MSG_DEVICE_ADDED = 1;
    private static final int MSG_DEVICE_CHANGED = 3;
    private static final int MSG_DEVICE_REMOVED = 2;
    private static final String TAG = "InputManager";
    private static InputManager sInstance;
    private final IInputManager mIm;
    private SparseArray<InputDevice> mInputDevices;
    private InputDevicesChangedListener mInputDevicesChangedListener;
    private final Object mInputDevicesLock = new Object();
    private final ArrayList<InputDeviceListenerDelegate> mInputDeviceListeners = new ArrayList<>();

    public interface InputDeviceListener {
        void onInputDeviceAdded(int i);

        void onInputDeviceChanged(int i);

        void onInputDeviceRemoved(int i);
    }

    private InputManager(IInputManager iInputManager) {
        this.mIm = iInputManager;
    }

    public static InputManager getInstance() {
        InputManager inputManager;
        synchronized (InputManager.class) {
            if (sInstance == null) {
                sInstance = new InputManager(IInputManager.Stub.asInterface(ServiceManager.getService(Context.INPUT_SERVICE)));
            }
            inputManager = sInstance;
        }
        return inputManager;
    }

    public InputDevice getInputDevice(int i) {
        synchronized (this.mInputDevicesLock) {
            populateInputDevicesLocked();
            int iIndexOfKey = this.mInputDevices.indexOfKey(i);
            if (iIndexOfKey < 0) {
                return null;
            }
            InputDevice inputDeviceValueAt = this.mInputDevices.valueAt(iIndexOfKey);
            if (inputDeviceValueAt == null) {
                try {
                    inputDeviceValueAt = this.mIm.getInputDevice(i);
                    if (inputDeviceValueAt != null) {
                        this.mInputDevices.setValueAt(iIndexOfKey, inputDeviceValueAt);
                    }
                } catch (RemoteException e) {
                    throw new RuntimeException("Could not get input device information.", e);
                }
            }
            return inputDeviceValueAt;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x0039 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:33:0x003b A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.view.InputDevice getInputDeviceByDescriptor(java.lang.String r7) {
        /*
            r6 = this;
            if (r7 == 0) goto L44
            java.lang.Object r0 = r6.mInputDevicesLock
            monitor-enter(r0)
            r6.populateInputDevicesLocked()     // Catch: java.lang.Throwable -> L41
            android.util.SparseArray<android.view.InputDevice> r1 = r6.mInputDevices     // Catch: java.lang.Throwable -> L41
            int r1 = r1.size()     // Catch: java.lang.Throwable -> L41
            r2 = 0
        Lf:
            if (r2 >= r1) goto L3e
            android.util.SparseArray<android.view.InputDevice> r3 = r6.mInputDevices     // Catch: java.lang.Throwable -> L41
            java.lang.Object r3 = r3.valueAt(r2)     // Catch: java.lang.Throwable -> L41
            android.view.InputDevice r3 = (android.view.InputDevice) r3     // Catch: java.lang.Throwable -> L41
            if (r3 != 0) goto L2f
            android.util.SparseArray<android.view.InputDevice> r4 = r6.mInputDevices     // Catch: java.lang.Throwable -> L41
            int r4 = r4.keyAt(r2)     // Catch: java.lang.Throwable -> L41
            android.hardware.input.IInputManager r5 = r6.mIm     // Catch: android.os.RemoteException -> L27 java.lang.Throwable -> L41
            android.view.InputDevice r3 = r5.getInputDevice(r4)     // Catch: android.os.RemoteException -> L27 java.lang.Throwable -> L41
        L27:
            if (r3 != 0) goto L2a
            goto L3b
        L2a:
            android.util.SparseArray<android.view.InputDevice> r4 = r6.mInputDevices     // Catch: java.lang.Throwable -> L41
            r4.setValueAt(r2, r3)     // Catch: java.lang.Throwable -> L41
        L2f:
            java.lang.String r4 = r3.getDescriptor()     // Catch: java.lang.Throwable -> L41
            boolean r4 = r7.equals(r4)     // Catch: java.lang.Throwable -> L41
            if (r4 == 0) goto L3b
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L41
            return r3
        L3b:
            int r2 = r2 + 1
            goto Lf
        L3e:
            r7 = 0
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L41
            return r7
        L41:
            r7 = move-exception
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L41
            throw r7
        L44:
            java.lang.IllegalArgumentException r7 = new java.lang.IllegalArgumentException
            java.lang.String r0 = "descriptor must not be null."
            r7.<init>(r0)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: android.hardware.input.InputManager.getInputDeviceByDescriptor(java.lang.String):android.view.InputDevice");
    }

    public int[] getInputDeviceIds() {
        int[] iArr;
        synchronized (this.mInputDevicesLock) {
            populateInputDevicesLocked();
            int size = this.mInputDevices.size();
            iArr = new int[size];
            for (int i = 0; i < size; i++) {
                iArr[i] = this.mInputDevices.keyAt(i);
            }
        }
        return iArr;
    }

    public void registerInputDeviceListener(InputDeviceListener inputDeviceListener, Handler handler) {
        if (inputDeviceListener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        synchronized (this.mInputDevicesLock) {
            if (findInputDeviceListenerLocked(inputDeviceListener) < 0) {
                this.mInputDeviceListeners.add(new InputDeviceListenerDelegate(inputDeviceListener, handler));
            }
        }
    }

    public void unregisterInputDeviceListener(InputDeviceListener inputDeviceListener) {
        if (inputDeviceListener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        synchronized (this.mInputDevicesLock) {
            int iFindInputDeviceListenerLocked = findInputDeviceListenerLocked(inputDeviceListener);
            if (iFindInputDeviceListenerLocked >= 0) {
                this.mInputDeviceListeners.get(iFindInputDeviceListenerLocked).removeCallbacksAndMessages(null);
                this.mInputDeviceListeners.remove(iFindInputDeviceListenerLocked);
            }
        }
    }

    private int findInputDeviceListenerLocked(InputDeviceListener inputDeviceListener) {
        int size = this.mInputDeviceListeners.size();
        for (int i = 0; i < size; i++) {
            if (this.mInputDeviceListeners.get(i).mListener == inputDeviceListener) {
                return i;
            }
        }
        return -1;
    }

    public KeyboardLayout[] getKeyboardLayouts() {
        try {
            return this.mIm.getKeyboardLayouts();
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get list of keyboard layout informations.", e);
            return new KeyboardLayout[0];
        }
    }

    public KeyboardLayout getKeyboardLayout(String str) {
        if (str == null) {
            throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
        }
        try {
            return this.mIm.getKeyboardLayout(str);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get keyboard layout information.", e);
            return null;
        }
    }

    public String getCurrentKeyboardLayoutForInputDevice(String str) {
        if (str == null) {
            throw new IllegalArgumentException("inputDeviceDescriptor must not be null");
        }
        try {
            return this.mIm.getCurrentKeyboardLayoutForInputDevice(str);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get current keyboard layout for input device.", e);
            return null;
        }
    }

    public void setCurrentKeyboardLayoutForInputDevice(String str, String str2) {
        if (str == null) {
            throw new IllegalArgumentException("inputDeviceDescriptor must not be null");
        }
        if (str2 == null) {
            throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
        }
        try {
            this.mIm.setCurrentKeyboardLayoutForInputDevice(str, str2);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not set current keyboard layout for input device.", e);
        }
    }

    public String[] getKeyboardLayoutsForInputDevice(String str) {
        if (str == null) {
            throw new IllegalArgumentException("inputDeviceDescriptor must not be null");
        }
        try {
            return this.mIm.getKeyboardLayoutsForInputDevice(str);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get keyboard layouts for input device.", e);
            return (String[]) ArrayUtils.emptyArray(String.class);
        }
    }

    public void addKeyboardLayoutForInputDevice(String str, String str2) {
        if (str == null) {
            throw new IllegalArgumentException("inputDeviceDescriptor must not be null");
        }
        if (str2 == null) {
            throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
        }
        try {
            this.mIm.addKeyboardLayoutForInputDevice(str, str2);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not add keyboard layout for input device.", e);
        }
    }

    public void removeKeyboardLayoutForInputDevice(String str, String str2) {
        if (str == null) {
            throw new IllegalArgumentException("inputDeviceDescriptor must not be null");
        }
        if (str2 == null) {
            throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
        }
        try {
            this.mIm.removeKeyboardLayoutForInputDevice(str, str2);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not remove keyboard layout for input device.", e);
        }
    }

    public int getPointerSpeed(Context context) {
        try {
            return Settings.System.getInt(context.getContentResolver(), Settings.System.POINTER_SPEED);
        } catch (Settings.SettingNotFoundException unused) {
            return 0;
        }
    }

    public void setPointerSpeed(Context context, int i) {
        if (i < -7 || i > 7) {
            throw new IllegalArgumentException("speed out of range");
        }
        Settings.System.putInt(context.getContentResolver(), Settings.System.POINTER_SPEED, i);
    }

    public void tryPointerSpeed(int i) {
        if (i < -7 || i > 7) {
            throw new IllegalArgumentException("speed out of range");
        }
        try {
            this.mIm.tryPointerSpeed(i);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not set temporary pointer speed.", e);
        }
    }

    public boolean[] deviceHasKeys(int[] iArr) {
        return deviceHasKeys(-1, iArr);
    }

    public boolean[] deviceHasKeys(int i, int[] iArr) {
        boolean[] zArr = new boolean[iArr.length];
        try {
            this.mIm.hasKeys(i, -256, iArr, zArr);
        } catch (RemoteException unused) {
        }
        return zArr;
    }

    public boolean injectInputEvent(InputEvent inputEvent, int i) {
        if (inputEvent == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        if (i != 0 && i != 2 && i != 1) {
            throw new IllegalArgumentException("mode is invalid");
        }
        try {
            return this.mIm.injectInputEvent(inputEvent, i);
        } catch (RemoteException unused) {
            return false;
        }
    }

    private void populateInputDevicesLocked() {
        if (this.mInputDevicesChangedListener == null) {
            InputDevicesChangedListener inputDevicesChangedListener = new InputDevicesChangedListener();
            try {
                this.mIm.registerInputDevicesChangedListener(inputDevicesChangedListener);
                this.mInputDevicesChangedListener = inputDevicesChangedListener;
            } catch (RemoteException e) {
                throw new RuntimeException("Could not get register input device changed listener", e);
            }
        }
        if (this.mInputDevices == null) {
            try {
                int[] inputDeviceIds = this.mIm.getInputDeviceIds();
                this.mInputDevices = new SparseArray<>();
                for (int i : inputDeviceIds) {
                    this.mInputDevices.put(i, null);
                }
            } catch (RemoteException e2) {
                throw new RuntimeException("Could not get input device ids.", e2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onInputDevicesChanged(int[] iArr) {
        synchronized (this.mInputDevicesLock) {
            int size = this.mInputDevices.size();
            while (true) {
                size--;
                if (size <= 0) {
                    break;
                }
                int iKeyAt = this.mInputDevices.keyAt(size);
                if (!containsDeviceId(iArr, iKeyAt)) {
                    this.mInputDevices.removeAt(size);
                    sendMessageToInputDeviceListenersLocked(2, iKeyAt);
                }
            }
            for (int i = 0; i < iArr.length; i += 2) {
                int i2 = iArr[i];
                int iIndexOfKey = this.mInputDevices.indexOfKey(i2);
                if (iIndexOfKey >= 0) {
                    InputDevice inputDeviceValueAt = this.mInputDevices.valueAt(iIndexOfKey);
                    if (inputDeviceValueAt != null) {
                        if (inputDeviceValueAt.getGeneration() != iArr[i + 1]) {
                            this.mInputDevices.setValueAt(iIndexOfKey, null);
                            sendMessageToInputDeviceListenersLocked(3, i2);
                        }
                    }
                } else {
                    this.mInputDevices.put(i2, null);
                    sendMessageToInputDeviceListenersLocked(1, i2);
                }
            }
        }
    }

    private void sendMessageToInputDeviceListenersLocked(int i, int i2) {
        int size = this.mInputDeviceListeners.size();
        for (int i3 = 0; i3 < size; i3++) {
            InputDeviceListenerDelegate inputDeviceListenerDelegate = this.mInputDeviceListeners.get(i3);
            inputDeviceListenerDelegate.sendMessage(inputDeviceListenerDelegate.obtainMessage(i, i2, 0));
        }
    }

    private static boolean containsDeviceId(int[] iArr, int i) {
        for (int i2 = 0; i2 < iArr.length; i2 += 2) {
            if (iArr[i2] == i) {
                return true;
            }
        }
        return false;
    }

    public Vibrator getInputDeviceVibrator(int i) {
        return new InputDeviceVibrator(i);
    }

    private final class InputDevicesChangedListener extends IInputDevicesChangedListener.Stub {
        private InputDevicesChangedListener() {
        }

        @Override // android.hardware.input.IInputDevicesChangedListener
        public void onInputDevicesChanged(int[] iArr) throws RemoteException {
            InputManager.this.onInputDevicesChanged(iArr);
        }
    }

    private static final class InputDeviceListenerDelegate extends Handler {
        public final InputDeviceListener mListener;

        public InputDeviceListenerDelegate(InputDeviceListener inputDeviceListener, Handler handler) {
            super(handler != null ? handler.getLooper() : Looper.myLooper());
            this.mListener = inputDeviceListener;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                this.mListener.onInputDeviceAdded(message.arg1);
            } else if (i == 2) {
                this.mListener.onInputDeviceRemoved(message.arg1);
            } else {
                if (i != 3) {
                    return;
                }
                this.mListener.onInputDeviceChanged(message.arg1);
            }
        }
    }

    private final class InputDeviceVibrator extends Vibrator {
        private final int mDeviceId;
        private final Binder mToken = new Binder();

        @Override // android.os.Vibrator
        public boolean hasVibrator() {
            return true;
        }

        public InputDeviceVibrator(int i) {
            this.mDeviceId = i;
        }

        @Override // android.os.Vibrator
        public void vibrate(long j) {
            vibrate(new long[]{0, j}, -1);
        }

        @Override // android.os.Vibrator
        public void vibrate(long[] jArr, int i) {
            if (i < jArr.length) {
                try {
                    InputManager.this.mIm.vibrate(this.mDeviceId, jArr, i, this.mToken);
                    return;
                } catch (RemoteException e) {
                    Log.w(InputManager.TAG, "Failed to vibrate.", e);
                    return;
                }
            }
            throw new ArrayIndexOutOfBoundsException();
        }

        @Override // android.os.Vibrator
        public void vibrate(int i, String str, long j) {
            vibrate(j);
        }

        @Override // android.os.Vibrator
        public void vibrate(int i, String str, long[] jArr, int i2) {
            vibrate(jArr, i2);
        }

        @Override // android.os.Vibrator
        public void cancel() {
            try {
                InputManager.this.mIm.cancelVibrate(this.mDeviceId, this.mToken);
            } catch (RemoteException e) {
                Log.w(InputManager.TAG, "Failed to cancel vibration.", e);
            }
        }
    }
}
