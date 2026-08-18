package android.hardware;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import dalvik.system.CloseGuard;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class SystemSensorManager extends SensorManager {
    private static Context mContext = null;
    private static boolean sSensorModuleInitialized = false;
    private final Looper mMainLooper;
    private final int mTargetSdkLevel;
    private static final Object sSensorModuleLock = new Object();
    private static final ArrayList<Sensor> sFullSensorsList = new ArrayList<>();
    private static final SparseArray<Sensor> sHandleToSensor = new SparseArray<>();
    private final HashMap<SensorEventListener, SensorEventQueue> mSensorListeners = new HashMap<>();
    private final HashMap<TriggerEventListener, TriggerEventQueue> mTriggerListeners = new HashMap<>();

    private static native void nativeClassInit();

    private static native int nativeGetNextSensor(Sensor sensor, int i);

    public SystemSensorManager(Context context, Looper looper) {
        this.mMainLooper = looper;
        this.mTargetSdkLevel = context.getApplicationInfo().targetSdkVersion;
        mContext = context;
        synchronized (sSensorModuleLock) {
            if (!sSensorModuleInitialized) {
                sSensorModuleInitialized = true;
                nativeClassInit();
                ArrayList<Sensor> arrayList = sFullSensorsList;
                int iNativeGetNextSensor = 0;
                do {
                    Sensor sensor = new Sensor();
                    iNativeGetNextSensor = nativeGetNextSensor(sensor, iNativeGetNextSensor);
                    if (iNativeGetNextSensor >= 0) {
                        arrayList.add(sensor);
                        sHandleToSensor.append(sensor.getHandle(), sensor);
                    }
                } while (iNativeGetNextSensor > 0);
            }
        }
    }

    @Override // android.hardware.SensorManager
    protected List<Sensor> getFullSensorList() {
        return sFullSensorsList;
    }

    @Override // android.hardware.SensorManager
    protected boolean registerListenerImpl(SensorEventListener sensorEventListener, Sensor sensor, int i, Handler handler, int i2, int i3) {
        if (sensorEventListener == null || sensor == null) {
            Log.e("SensorManager", "sensor or listener is null");
            return false;
        }
        if (Sensor.getReportingMode(sensor) == Sensor.REPORTING_MODE_ONE_SHOT) {
            Log.e("SensorManager", "Trigger Sensors should use the requestTriggerSensor.");
            return false;
        }
        if (i2 < 0 || i < 0) {
            Log.e("SensorManager", "maxBatchReportLatencyUs and delayUs should be non-negative");
            return false;
        }
        synchronized (this.mSensorListeners) {
            SensorEventQueue sensorEventQueue = this.mSensorListeners.get(sensorEventListener);
            if (sensorEventQueue == null) {
                SensorEventQueue sensorEventQueue2 = new SensorEventQueue(sensorEventListener, handler != null ? handler.getLooper() : this.mMainLooper, this);
                if (!sensorEventQueue2.addSensor(sensor, i, i2, i3)) {
                    sensorEventQueue2.dispose();
                    return false;
                }
                this.mSensorListeners.put(sensorEventListener, sensorEventQueue2);
                return true;
            }
            return sensorEventQueue.addSensor(sensor, i, i2, i3);
        }
    }

    @Override // android.hardware.SensorManager
    protected void unregisterListenerImpl(SensorEventListener sensorEventListener, Sensor sensor) {
        boolean zRemoveSensor;
        if (sensor == null || Sensor.getReportingMode(sensor) != Sensor.REPORTING_MODE_ONE_SHOT) {
            synchronized (this.mSensorListeners) {
                SensorEventQueue sensorEventQueue = this.mSensorListeners.get(sensorEventListener);
                if (sensorEventQueue != null) {
                    if (sensor == null) {
                        zRemoveSensor = sensorEventQueue.removeAllSensors();
                    } else {
                        zRemoveSensor = sensorEventQueue.removeSensor(sensor, true);
                    }
                    if (zRemoveSensor && !sensorEventQueue.hasSensors()) {
                        this.mSensorListeners.remove(sensorEventListener);
                        sensorEventQueue.dispose();
                    }
                }
            }
        }
    }

    @Override // android.hardware.SensorManager
    protected boolean requestTriggerSensorImpl(TriggerEventListener triggerEventListener, Sensor sensor) {
        if (sensor == null) {
            throw new IllegalArgumentException("sensor cannot be null");
        }
        if (Sensor.getReportingMode(sensor) != Sensor.REPORTING_MODE_ONE_SHOT) {
            return false;
        }
        synchronized (this.mTriggerListeners) {
            TriggerEventQueue triggerEventQueue = this.mTriggerListeners.get(triggerEventListener);
            if (triggerEventQueue == null) {
                TriggerEventQueue triggerEventQueue2 = new TriggerEventQueue(triggerEventListener, this.mMainLooper, this);
                if (!triggerEventQueue2.addSensor(sensor, 0, 0, 0)) {
                    triggerEventQueue2.dispose();
                    return false;
                }
                this.mTriggerListeners.put(triggerEventListener, triggerEventQueue2);
                return true;
            }
            return triggerEventQueue.addSensor(sensor, 0, 0, 0);
        }
    }

    @Override // android.hardware.SensorManager
    protected boolean cancelTriggerSensorImpl(TriggerEventListener triggerEventListener, Sensor sensor, boolean z) {
        boolean zRemoveSensor;
        if (sensor != null && Sensor.getReportingMode(sensor) != Sensor.REPORTING_MODE_ONE_SHOT) {
            return false;
        }
        synchronized (this.mTriggerListeners) {
            TriggerEventQueue triggerEventQueue = this.mTriggerListeners.get(triggerEventListener);
            if (triggerEventQueue == null) {
                return false;
            }
            if (sensor == null) {
                zRemoveSensor = triggerEventQueue.removeAllSensors();
            } else {
                zRemoveSensor = triggerEventQueue.removeSensor(sensor, z);
            }
            if (zRemoveSensor && !triggerEventQueue.hasSensors()) {
                this.mTriggerListeners.remove(triggerEventListener);
                triggerEventQueue.dispose();
            }
            return zRemoveSensor;
        }
    }

    @Override // android.hardware.SensorManager
    protected boolean flushImpl(SensorEventListener sensorEventListener) {
        if (sensorEventListener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        synchronized (this.mSensorListeners) {
            SensorEventQueue sensorEventQueue = this.mSensorListeners.get(sensorEventListener);
            if (sensorEventQueue == null) {
                return false;
            }
            return sensorEventQueue.flush() == 0;
        }
    }

    private static abstract class BaseEventQueue {
        private final CloseGuard mCloseGuard;
        protected final SystemSensorManager mManager;
        private final float[] mScratch;
        private int nSensorEventQueue;
        private final SparseBooleanArray mActiveSensors = new SparseBooleanArray();
        protected final SparseIntArray mSensorAccuracies = new SparseIntArray();
        protected final SparseBooleanArray mFirstEvent = new SparseBooleanArray();

        private static native void nativeDestroySensorEventQueue(int i);

        private static native int nativeDisableSensor(int i, int i2);

        private static native int nativeEnableSensor(int i, int i2, int i3, int i4, int i5);

        private static native int nativeFlushSensor(int i);

        private native int nativeInitBaseEventQueue(BaseEventQueue baseEventQueue, MessageQueue messageQueue, float[] fArr);

        protected abstract void addSensorEvent(Sensor sensor);

        protected abstract void dispatchFlushCompleteEvent(int i);

        protected abstract void dispatchSensorEvent(int i, float[] fArr, int i2, long j);

        protected abstract void removeSensorEvent(Sensor sensor);

        BaseEventQueue(Looper looper, SystemSensorManager systemSensorManager) {
            CloseGuard closeGuard = CloseGuard.get();
            this.mCloseGuard = closeGuard;
            float[] fArr = new float[16];
            this.mScratch = fArr;
            this.nSensorEventQueue = nativeInitBaseEventQueue(this, looper.getQueue(), fArr);
            closeGuard.open("dispose");
            this.mManager = systemSensorManager;
        }

        public void dispose() {
            dispose(false);
        }

        public boolean addSensor(Sensor sensor, int i, int i2, int i3) {
            int handle = sensor.getHandle();
            if (this.mActiveSensors.get(handle)) {
                return false;
            }
            this.mActiveSensors.put(handle, true);
            addSensorEvent(sensor);
            if (enableSensor(sensor, i, i2, i3) == 0 || (i2 != 0 && (i2 <= 0 || enableSensor(sensor, i, 0, 0) == 0))) {
                return true;
            }
            removeSensor(sensor, false);
            return false;
        }

        public boolean removeAllSensors() {
            for (int i = 0; i < this.mActiveSensors.size(); i++) {
                if (this.mActiveSensors.valueAt(i)) {
                    int iKeyAt = this.mActiveSensors.keyAt(i);
                    Sensor sensor = (Sensor) SystemSensorManager.sHandleToSensor.get(iKeyAt);
                    if (sensor != null) {
                        disableSensor(sensor);
                        this.mActiveSensors.put(iKeyAt, false);
                        removeSensorEvent(sensor);
                    }
                }
            }
            return true;
        }

        public boolean removeSensor(Sensor sensor, boolean z) {
            if (!this.mActiveSensors.get(sensor.getHandle())) {
                return false;
            }
            if (z) {
                disableSensor(sensor);
            }
            this.mActiveSensors.put(sensor.getHandle(), false);
            removeSensorEvent(sensor);
            return true;
        }

        public int flush() {
            int i = this.nSensorEventQueue;
            if (i == 0) {
                throw null;
            }
            return nativeFlushSensor(i);
        }

        public boolean hasSensors() {
            return this.mActiveSensors.indexOfValue(true) >= 0;
        }

        protected void finalize() throws Throwable {
            try {
                dispose(true);
            } finally {
                super.finalize();
            }
        }

        private void dispose(boolean z) {
            CloseGuard closeGuard = this.mCloseGuard;
            if (closeGuard != null) {
                if (z) {
                    closeGuard.warnIfOpen();
                }
                this.mCloseGuard.close();
            }
            int i = this.nSensorEventQueue;
            if (i != 0) {
                nativeDestroySensorEventQueue(i);
                this.nSensorEventQueue = 0;
            }
        }

        private int enableSensor(Sensor sensor, int i, int i2, int i3) {
            int i4 = this.nSensorEventQueue;
            if (i4 == 0) {
                throw null;
            }
            Objects.requireNonNull(sensor);
            return nativeEnableSensor(i4, sensor.getHandle(), i, i2, i3);
        }

        private int disableSensor(Sensor sensor) {
            int i = this.nSensorEventQueue;
            if (i == 0) {
                throw null;
            }
            Objects.requireNonNull(sensor);
            return nativeDisableSensor(i, sensor.getHandle());
        }
    }

    static final class SensorEventQueue extends BaseEventQueue {
        private final SensorEventListener mListener;
        private final SparseArray<SensorEvent> mSensorsEvents;

        public SensorEventQueue(SensorEventListener sensorEventListener, Looper looper, SystemSensorManager systemSensorManager) {
            super(looper, systemSensorManager);
            this.mSensorsEvents = new SparseArray<>();
            this.mListener = sensorEventListener;
        }

        @Override // android.hardware.SystemSensorManager.BaseEventQueue
        public void addSensorEvent(Sensor sensor) {
            SensorEvent sensorEvent = new SensorEvent(Sensor.getMaxLengthValuesArray(sensor, this.mManager.mTargetSdkLevel));
            synchronized (this.mSensorsEvents) {
                this.mSensorsEvents.put(sensor.getHandle(), sensorEvent);
            }
        }

        @Override // android.hardware.SystemSensorManager.BaseEventQueue
        public void removeSensorEvent(Sensor sensor) {
            synchronized (this.mSensorsEvents) {
                this.mSensorsEvents.delete(sensor.getHandle());
            }
        }

        @Override // android.hardware.SystemSensorManager.BaseEventQueue
        protected void dispatchSensorEvent(int i, float[] fArr, int i2, long j) {
            SensorEvent sensorEvent;
            Sensor sensor = (Sensor) SystemSensorManager.sHandleToSensor.get(i);
            synchronized (this.mSensorsEvents) {
                sensorEvent = this.mSensorsEvents.get(i);
            }
            if (sensorEvent == null) {
                return;
            }
            float[] fArr2 = sensorEvent.values;
            System.arraycopy(fArr, 0, sensorEvent.values, 0, sensorEvent.values.length);
            System.arraycopy(fArr, 0, sensorEvent.originalValues, 0, sensorEvent.values.length);
            int type = sensor.getType();
            String string = Settings.System.getString(SystemSensorManager.mContext.getContentResolver(), Settings.System.ACCELEROMETER_COORDINATE);
            if (string != null && string.equals("special") && (type == 1 || type == 9)) {
                new DisplayMetrics();
                DisplayMetrics displayMetrics = SystemSensorManager.mContext.getResources().getDisplayMetrics();
                if (displayMetrics.widthPixels > displayMetrics.heightPixels) {
                    fArr2[0] = fArr[1];
                    fArr2[1] = -fArr[0];
                    fArr2[2] = fArr[2];
                } else {
                    fArr2[0] = -fArr[1];
                    fArr2[1] = fArr[0];
                    fArr2[2] = fArr[2];
                }
            }
            sensorEvent.timestamp = j;
            sensorEvent.accuracy = i2;
            sensorEvent.sensor = sensor;
            int type2 = sensorEvent.sensor.getType();
            if (type2 == 2 || type2 == 3) {
                int i3 = this.mSensorAccuracies.get(i);
                if (sensorEvent.accuracy >= 0 && i3 != sensorEvent.accuracy) {
                    this.mSensorAccuracies.put(i, sensorEvent.accuracy);
                    this.mListener.onAccuracyChanged(sensorEvent.sensor, sensorEvent.accuracy);
                }
            } else if (!this.mFirstEvent.get(i)) {
                this.mFirstEvent.put(i, true);
                this.mListener.onAccuracyChanged(sensorEvent.sensor, 3);
            }
            this.mListener.onSensorChanged(sensorEvent);
        }

        @Override // android.hardware.SystemSensorManager.BaseEventQueue
        protected void dispatchFlushCompleteEvent(int i) {
            if (this.mListener instanceof SensorEventListener2) {
                ((SensorEventListener2) this.mListener).onFlushCompleted((Sensor) SystemSensorManager.sHandleToSensor.get(i));
            }
        }
    }

    static final class TriggerEventQueue extends BaseEventQueue {
        private final TriggerEventListener mListener;
        private final SparseArray<TriggerEvent> mTriggerEvents;

        @Override // android.hardware.SystemSensorManager.BaseEventQueue
        protected void dispatchFlushCompleteEvent(int i) {
        }

        public TriggerEventQueue(TriggerEventListener triggerEventListener, Looper looper, SystemSensorManager systemSensorManager) {
            super(looper, systemSensorManager);
            this.mTriggerEvents = new SparseArray<>();
            this.mListener = triggerEventListener;
        }

        @Override // android.hardware.SystemSensorManager.BaseEventQueue
        public void addSensorEvent(Sensor sensor) {
            TriggerEvent triggerEvent = new TriggerEvent(Sensor.getMaxLengthValuesArray(sensor, this.mManager.mTargetSdkLevel));
            synchronized (this.mTriggerEvents) {
                this.mTriggerEvents.put(sensor.getHandle(), triggerEvent);
            }
        }

        @Override // android.hardware.SystemSensorManager.BaseEventQueue
        public void removeSensorEvent(Sensor sensor) {
            synchronized (this.mTriggerEvents) {
                this.mTriggerEvents.delete(sensor.getHandle());
            }
        }

        @Override // android.hardware.SystemSensorManager.BaseEventQueue
        protected void dispatchSensorEvent(int i, float[] fArr, int i2, long j) {
            TriggerEvent triggerEvent;
            Sensor sensor = (Sensor) SystemSensorManager.sHandleToSensor.get(i);
            synchronized (this.mTriggerEvents) {
                triggerEvent = this.mTriggerEvents.get(i);
            }
            if (triggerEvent == null) {
                Log.e("SensorManager", "Error: Trigger Event is null for Sensor: " + sensor);
                return;
            }
            System.arraycopy(fArr, 0, triggerEvent.values, 0, triggerEvent.values.length);
            triggerEvent.timestamp = j;
            triggerEvent.sensor = sensor;
            this.mManager.cancelTriggerSensorImpl(this.mListener, sensor, false);
            this.mListener.onTrigger(triggerEvent);
        }
    }
}
