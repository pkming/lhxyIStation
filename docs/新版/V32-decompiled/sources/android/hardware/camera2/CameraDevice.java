package android.hardware.camera2;

import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.view.Surface;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface CameraDevice extends AutoCloseable {
    public static final int TEMPLATE_MANUAL = 6;
    public static final int TEMPLATE_PREVIEW = 1;
    public static final int TEMPLATE_RECORD = 3;
    public static final int TEMPLATE_STILL_CAPTURE = 2;
    public static final int TEMPLATE_VIDEO_SNAPSHOT = 4;
    public static final int TEMPLATE_ZERO_SHUTTER_LAG = 5;

    public static abstract class CaptureListener {
        public void onCaptureCompleted(CameraDevice cameraDevice, CaptureRequest captureRequest, CaptureResult captureResult) {
        }

        public void onCaptureFailed(CameraDevice cameraDevice, CaptureRequest captureRequest, CaptureFailure captureFailure) {
        }

        public void onCapturePartial(CameraDevice cameraDevice, CaptureRequest captureRequest, CaptureResult captureResult) {
        }

        public void onCaptureSequenceCompleted(CameraDevice cameraDevice, int i, int i2) {
        }

        public void onCaptureStarted(CameraDevice cameraDevice, CaptureRequest captureRequest, long j) {
        }
    }

    public static abstract class StateListener {
        public static final int ERROR_CAMERA_DEVICE = 4;
        public static final int ERROR_CAMERA_DISABLED = 3;
        public static final int ERROR_CAMERA_IN_USE = 1;
        public static final int ERROR_CAMERA_SERVICE = 5;
        public static final int ERROR_MAX_CAMERAS_IN_USE = 2;

        public void onActive(CameraDevice cameraDevice) {
        }

        public void onBusy(CameraDevice cameraDevice) {
        }

        public void onClosed(CameraDevice cameraDevice) {
        }

        public abstract void onDisconnected(CameraDevice cameraDevice);

        public abstract void onError(CameraDevice cameraDevice, int i);

        public void onIdle(CameraDevice cameraDevice) {
        }

        public abstract void onOpened(CameraDevice cameraDevice);

        public void onUnconfigured(CameraDevice cameraDevice) {
        }
    }

    int capture(CaptureRequest captureRequest, CaptureListener captureListener, Handler handler) throws CameraAccessException;

    int captureBurst(List<CaptureRequest> list, CaptureListener captureListener, Handler handler) throws CameraAccessException;

    @Override // java.lang.AutoCloseable
    void close();

    void configureOutputs(List<Surface> list) throws CameraAccessException;

    CaptureRequest.Builder createCaptureRequest(int i) throws CameraAccessException;

    void flush() throws CameraAccessException;

    String getId();

    int setRepeatingBurst(List<CaptureRequest> list, CaptureListener captureListener, Handler handler) throws CameraAccessException;

    int setRepeatingRequest(CaptureRequest captureRequest, CaptureListener captureListener, Handler handler) throws CameraAccessException;

    void stopRepeating() throws CameraAccessException;

    void waitUntilIdle() throws CameraAccessException;
}
