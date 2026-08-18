package android.speech.tts;

/* JADX INFO: loaded from: classes.dex */
abstract class AbstractSynthesisCallback implements SynthesisCallback {
    abstract boolean isDone();

    abstract void stop();

    AbstractSynthesisCallback() {
    }
}
