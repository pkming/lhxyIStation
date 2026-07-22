package android.hardware;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.view.IRotationWatcher;
import android.view.IWindowManager;
import java.util.HashMap;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
final class LegacySensorManager {
    private static boolean sInitialized;
    private static int sRotation;
    private static IWindowManager sWindowManager;
    private final HashMap<SensorListener, LegacyListener> mLegacyListenersMap = new HashMap<>();
    private final SensorManager mSensorManager;

    public LegacySensorManager(SensorManager sensorManager) {
        this.mSensorManager = sensorManager;
        synchronized (SensorManager.class) {
            if (!sInitialized) {
                IWindowManager iWindowManagerAsInterface = IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE));
                sWindowManager = iWindowManagerAsInterface;
                if (iWindowManagerAsInterface != null) {
                    try {
                        sRotation = iWindowManagerAsInterface.watchRotation(new IRotationWatcher.Stub() { // from class: android.hardware.LegacySensorManager.1
                            @Override // android.view.IRotationWatcher
                            public void onRotationChanged(int i) {
                                LegacySensorManager.onRotationChanged(i);
                            }
                        });
                    } catch (RemoteException unused) {
                    }
                }
            }
        }
    }

    public int getSensors() {
        Iterator<Sensor> it = this.mSensorManager.getFullSensorList().iterator();
        int i = 0;
        while (it.hasNext()) {
            int type = it.next().getType();
            if (type == 1) {
                i |= 2;
            } else if (type == 2) {
                i |= 8;
            } else if (type == 3) {
                i |= 129;
            }
        }
        return i;
    }

    public boolean registerListener(SensorListener sensorListener, int i, int i2) {
        if (sensorListener == null) {
            return false;
        }
        return registerLegacyListener(4, 7, sensorListener, i, i2) || (registerLegacyListener(1, 3, sensorListener, i, i2) || (registerLegacyListener(128, 3, sensorListener, i, i2) || (registerLegacyListener(8, 2, sensorListener, i, i2) || (registerLegacyListener(2, 1, sensorListener, i, i2)))));
    }

    private boolean registerLegacyListener(int i, int i2, SensorListener sensorListener, int i3, int i4) {
        Sensor defaultSensor;
        boolean zRegisterListener;
        if ((i3 & i) == 0 || (defaultSensor = this.mSensorManager.getDefaultSensor(i2)) == null) {
            return false;
        }
        synchronized (this.mLegacyListenersMap) {
            LegacyListener legacyListener = this.mLegacyListenersMap.get(sensorListener);
            if (legacyListener == null) {
                legacyListener = new LegacyListener(sensorListener);
                this.mLegacyListenersMap.put(sensorListener, legacyListener);
            }
            zRegisterListener = legacyListener.registerSensor(i) ? this.mSensorManager.registerListener(legacyListener, defaultSensor, i4) : true;
        }
        return zRegisterListener;
    }

    public void unregisterListener(SensorListener sensorListener, int i) {
        if (sensorListener == null) {
            return;
        }
        unregisterLegacyListener(2, 1, sensorListener, i);
        unregisterLegacyListener(8, 2, sensorListener, i);
        unregisterLegacyListener(128, 3, sensorListener, i);
        unregisterLegacyListener(1, 3, sensorListener, i);
        unregisterLegacyListener(4, 7, sensorListener, i);
    }

    private void unregisterLegacyListener(int i, int i2, SensorListener sensorListener, int i3) {
        Sensor defaultSensor;
        if ((i3 & i) == 0 || (defaultSensor = this.mSensorManager.getDefaultSensor(i2)) == null) {
            return;
        }
        synchronized (this.mLegacyListenersMap) {
            LegacyListener legacyListener = this.mLegacyListenersMap.get(sensorListener);
            if (legacyListener != null && legacyListener.unregisterSensor(i)) {
                this.mSensorManager.unregisterListener(legacyListener, defaultSensor);
                if (!legacyListener.hasSensors()) {
                    this.mLegacyListenersMap.remove(sensorListener);
                }
            }
        }
    }

    static void onRotationChanged(int i) {
        synchronized (SensorManager.class) {
            sRotation = i;
        }
    }

    static int getRotation() {
        int i;
        synchronized (SensorManager.class) {
            i = sRotation;
        }
        return i;
    }

    private static final class LegacyListener implements SensorEventListener {
        private SensorListener mTarget;
        private float[] mValues = new float[6];
        private final LmsFilter mYawfilter = new LmsFilter();
        private int mSensors = 0;

        private static int getLegacySensorType(int i) {
            if (i == 1) {
                return 2;
            }
            if (i == 2) {
                return 8;
            }
            if (i != 3) {
                return i != 7 ? 0 : 4;
            }
            return 128;
        }

        private static boolean hasOrientationSensor(int i) {
            return (i & 129) != 0;
        }

        LegacyListener(SensorListener sensorListener) {
            this.mTarget = sensorListener;
        }

        boolean registerSensor(int i) {
            int i2 = this.mSensors;
            if ((i2 & i) != 0) {
                return false;
            }
            boolean zHasOrientationSensor = hasOrientationSensor(i2);
            this.mSensors |= i;
            return (zHasOrientationSensor && hasOrientationSensor(i)) ? false : true;
        }

        boolean unregisterSensor(int i) {
            int i2 = this.mSensors;
            if ((i2 & i) == 0) {
                return false;
            }
            this.mSensors = i2 & (~i);
            return (hasOrientationSensor(i) && hasOrientationSensor(this.mSensors)) ? false : true;
        }

        boolean hasSensors() {
            return this.mSensors != 0;
        }

        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int i) {
            try {
                this.mTarget.onAccuracyChanged(getLegacySensorType(sensor.getType()), i);
            } catch (AbstractMethodError unused) {
            }
        }

        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent sensorEvent) {
            float[] fArr = this.mValues;
            fArr[0] = sensorEvent.values[0];
            fArr[1] = sensorEvent.values[1];
            fArr[2] = sensorEvent.values[2];
            int type = sensorEvent.sensor.getType();
            int legacySensorType = getLegacySensorType(type);
            mapSensorDataToWindow(legacySensorType, fArr, LegacySensorManager.getRotation());
            if (type == 3) {
                if ((this.mSensors & 128) != 0) {
                    this.mTarget.onSensorChanged(128, fArr);
                }
                if ((this.mSensors & 1) != 0) {
                    fArr[0] = this.mYawfilter.filter(sensorEvent.timestamp, fArr[0]);
                    this.mTarget.onSensorChanged(1, fArr);
                    return;
                }
                return;
            }
            this.mTarget.onSensorChanged(legacySensorType, fArr);
        }

        /* JADX WARN: Removed duplicated region for block: B:19:0x0040  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private void mapSensorDataToWindow(int r10, float[] r11, int r12) {
            /*
                r9 = this;
                r0 = 0
                r1 = r11[r0]
                r2 = 1
                r3 = r11[r2]
                r4 = 2
                r5 = r11[r4]
                r6 = 128(0x80, float:1.794E-43)
                r7 = 8
                if (r10 == r2) goto L1b
                if (r10 == r4) goto L19
                if (r10 == r7) goto L16
                if (r10 == r6) goto L1b
                goto L1c
            L16:
                float r1 = -r1
                float r3 = -r3
                goto L1c
            L19:
                float r1 = -r1
                float r3 = -r3
            L1b:
                float r5 = -r5
            L1c:
                r11[r0] = r1
                r11[r2] = r3
                r11[r4] = r5
                r8 = 3
                r11[r8] = r1
                r8 = 4
                r11[r8] = r3
                r8 = 5
                r11[r8] = r5
                r8 = r12 & 1
                if (r8 == 0) goto L53
                if (r10 == r2) goto L40
                if (r10 == r4) goto L38
                if (r10 == r7) goto L38
                if (r10 == r6) goto L40
                goto L53
            L38:
                float r3 = -r3
                r11[r0] = r3
                r11[r2] = r1
                r11[r4] = r5
                goto L53
            L40:
                r8 = 1132920832(0x43870000, float:270.0)
                int r8 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
                if (r8 >= 0) goto L49
                r8 = 90
                goto L4b
            L49:
                r8 = -270(0xfffffffffffffef2, float:NaN)
            L4b:
                float r8 = (float) r8
                float r1 = r1 + r8
                r11[r0] = r1
                r11[r2] = r5
                r11[r4] = r3
            L53:
                r12 = r12 & r4
                if (r12 == 0) goto L7f
                r12 = r11[r0]
                r1 = r11[r2]
                r3 = r11[r4]
                if (r10 == r2) goto L6e
                if (r10 == r4) goto L65
                if (r10 == r7) goto L65
                if (r10 == r6) goto L6e
                goto L7f
            L65:
                float r10 = -r12
                r11[r0] = r10
                float r10 = -r1
                r11[r2] = r10
                r11[r4] = r3
                goto L7f
            L6e:
                r10 = 1127481344(0x43340000, float:180.0)
                int r5 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
                if (r5 < 0) goto L76
                float r12 = r12 - r10
                goto L77
            L76:
                float r12 = r12 + r10
            L77:
                r11[r0] = r12
                float r10 = -r1
                r11[r2] = r10
                float r10 = -r3
                r11[r4] = r10
            L7f:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: android.hardware.LegacySensorManager.LegacyListener.mapSensorDataToWindow(int, float[], int):void");
        }
    }

    private static final class LmsFilter {
        private static final int COUNT = 12;
        private static final float PREDICTION_RATIO = 0.33333334f;
        private static final float PREDICTION_TIME = 0.08f;
        private static final int SENSORS_RATE_MS = 20;
        private float[] mV = new float[24];
        private long[] mT = new long[24];
        private int mIndex = 12;

        public float filter(long j, float f) {
            float[] fArr = this.mV;
            int i = this.mIndex;
            float f2 = fArr[i];
            float f3 = f - f2 > 180.0f ? f - 360.0f : f2 - f > 180.0f ? f + 360.0f : f;
            int i2 = i + 1;
            this.mIndex = i2;
            if (i2 >= 24) {
                this.mIndex = 12;
            }
            int i3 = this.mIndex;
            fArr[i3] = f3;
            long[] jArr = this.mT;
            jArr[i3] = j;
            fArr[i3 - 12] = f3;
            jArr[i3 - 12] = j;
            float f4 = 0.0f;
            float f5 = 0.0f;
            float f6 = 0.0f;
            float f7 = 0.0f;
            float f8 = 0.0f;
            for (int i4 = 0; i4 < 11; i4++) {
                int i5 = (this.mIndex - 1) - i4;
                float f9 = this.mV[i5];
                long[] jArr2 = this.mT;
                int i6 = i5 + 1;
                float f10 = (((jArr2[i5] / 2) + (jArr2[i6] / 2)) - j) * 1.0E-9f;
                float f11 = (jArr2[i5] - jArr2[i6]) * 1.0E-9f;
                float f12 = f11 * f11;
                f4 += f9 * f12;
                float f13 = f10 * f12;
                f5 += f10 * f13;
                f6 += f13;
                f7 += f9 * f13;
                f8 += f12;
            }
            float f14 = ((f4 * f5) + (f7 * f6)) / ((f5 * f8) + (f6 * f6));
            float fCeil = (f14 + ((((f8 * f14) - f4) / f6) * PREDICTION_TIME)) * 0.0027777778f;
            if ((fCeil >= 0.0f ? fCeil : -fCeil) >= 0.5f) {
                fCeil = (fCeil - ((float) Math.ceil(0.5f + fCeil))) + 1.0f;
            }
            if (fCeil < 0.0f) {
                fCeil += 1.0f;
            }
            return fCeil * 360.0f;
        }
    }
}
