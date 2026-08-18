package android.webkit;

/* JADX INFO: loaded from: classes.dex */
public class JsResult {
    private final ResultReceiver mReceiver;
    private boolean mResult;

    public interface ResultReceiver {
        void onJsResultComplete(JsResult jsResult);
    }

    public final void cancel() {
        this.mResult = false;
        wakeUp();
    }

    public final void confirm() {
        this.mResult = true;
        wakeUp();
    }

    public JsResult(ResultReceiver resultReceiver) {
        this.mReceiver = resultReceiver;
    }

    public final boolean getResult() {
        return this.mResult;
    }

    private final void wakeUp() {
        this.mReceiver.onJsResultComplete(this);
    }
}
