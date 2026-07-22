package android.hardware;

/* JADX INFO: loaded from: classes.dex */
public class SensorEvent {
    public int accuracy;
    public final float[] originalValues;
    public Sensor sensor;
    public long timestamp;
    public final float[] values;

    SensorEvent(int i) {
        this.values = new float[i];
        this.originalValues = new float[i];
    }
}
