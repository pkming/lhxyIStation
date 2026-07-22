package android.bluetooth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothGattService {
    public static final int SERVICE_TYPE_PRIMARY = 0;
    public static final int SERVICE_TYPE_SECONDARY = 1;
    private boolean mAdvertisePreferred;
    protected List<BluetoothGattCharacteristic> mCharacteristics;
    protected BluetoothDevice mDevice;
    protected int mHandles;
    protected List<BluetoothGattService> mIncludedServices;
    protected int mInstanceId;
    protected int mServiceType;
    protected UUID mUuid;

    public BluetoothGattService(UUID uuid, int i) {
        this.mHandles = 0;
        this.mDevice = null;
        this.mUuid = uuid;
        this.mInstanceId = 0;
        this.mServiceType = i;
        this.mCharacteristics = new ArrayList();
        this.mIncludedServices = new ArrayList();
    }

    BluetoothGattService(BluetoothDevice bluetoothDevice, UUID uuid, int i, int i2) {
        this.mHandles = 0;
        this.mDevice = bluetoothDevice;
        this.mUuid = uuid;
        this.mInstanceId = i;
        this.mServiceType = i2;
        this.mCharacteristics = new ArrayList();
        this.mIncludedServices = new ArrayList();
    }

    BluetoothDevice getDevice() {
        return this.mDevice;
    }

    public boolean addService(BluetoothGattService bluetoothGattService) {
        this.mIncludedServices.add(bluetoothGattService);
        return true;
    }

    public boolean addCharacteristic(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        this.mCharacteristics.add(bluetoothGattCharacteristic);
        bluetoothGattCharacteristic.setService(this);
        return true;
    }

    BluetoothGattCharacteristic getCharacteristic(UUID uuid, int i) {
        for (BluetoothGattCharacteristic bluetoothGattCharacteristic : this.mCharacteristics) {
            if (uuid.equals(bluetoothGattCharacteristic.getUuid()) && bluetoothGattCharacteristic.getInstanceId() == i) {
                return bluetoothGattCharacteristic;
            }
        }
        return null;
    }

    public void setInstanceId(int i) {
        this.mInstanceId = i;
    }

    int getHandles() {
        return this.mHandles;
    }

    public void setHandles(int i) {
        this.mHandles = i;
    }

    void addIncludedService(BluetoothGattService bluetoothGattService) {
        this.mIncludedServices.add(bluetoothGattService);
    }

    public UUID getUuid() {
        return this.mUuid;
    }

    public int getInstanceId() {
        return this.mInstanceId;
    }

    public int getType() {
        return this.mServiceType;
    }

    public List<BluetoothGattService> getIncludedServices() {
        return this.mIncludedServices;
    }

    public List<BluetoothGattCharacteristic> getCharacteristics() {
        return this.mCharacteristics;
    }

    public BluetoothGattCharacteristic getCharacteristic(UUID uuid) {
        for (BluetoothGattCharacteristic bluetoothGattCharacteristic : this.mCharacteristics) {
            if (uuid.equals(bluetoothGattCharacteristic.getUuid())) {
                return bluetoothGattCharacteristic;
            }
        }
        return null;
    }

    public boolean isAdvertisePreferred() {
        return this.mAdvertisePreferred;
    }

    public void setAdvertisePreferred(boolean z) {
        this.mAdvertisePreferred = z;
    }
}
