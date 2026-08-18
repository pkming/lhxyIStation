package android.speech.tts;

import android.speech.tts.TextToSpeechService;

/* JADX INFO: loaded from: classes.dex */
abstract class PlaybackQueueItem implements Runnable {
    private final Object mCallerIdentity;
    private final TextToSpeechService.UtteranceProgressDispatcher mDispatcher;

    @Override // java.lang.Runnable
    public abstract void run();

    abstract void stop(boolean z);

    PlaybackQueueItem(TextToSpeechService.UtteranceProgressDispatcher utteranceProgressDispatcher, Object obj) {
        this.mDispatcher = utteranceProgressDispatcher;
        this.mCallerIdentity = obj;
    }

    Object getCallerIdentity() {
        return this.mCallerIdentity;
    }

    protected TextToSpeechService.UtteranceProgressDispatcher getDispatcher() {
        return this.mDispatcher;
    }
}
