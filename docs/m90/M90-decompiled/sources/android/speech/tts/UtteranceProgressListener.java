package android.speech.tts;

import android.speech.tts.TextToSpeech;

/* JADX INFO: loaded from: classes.dex */
public abstract class UtteranceProgressListener {
    public abstract void onDone(String str);

    public abstract void onError(String str);

    public abstract void onStart(String str);

    static UtteranceProgressListener from(final TextToSpeech.OnUtteranceCompletedListener onUtteranceCompletedListener) {
        return new UtteranceProgressListener() { // from class: android.speech.tts.UtteranceProgressListener.1
            @Override // android.speech.tts.UtteranceProgressListener
            public void onStart(String str) {
            }

            @Override // android.speech.tts.UtteranceProgressListener
            public synchronized void onDone(String str) {
                onUtteranceCompletedListener.onUtteranceCompleted(str);
            }

            @Override // android.speech.tts.UtteranceProgressListener
            public void onError(String str) {
                onUtteranceCompletedListener.onUtteranceCompleted(str);
            }
        };
    }
}
