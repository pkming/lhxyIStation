package android.speech.tts;

import android.os.ConditionVariable;
import android.speech.tts.TextToSpeechService;

/* JADX INFO: loaded from: classes.dex */
class SilencePlaybackQueueItem extends PlaybackQueueItem {
    private final ConditionVariable mCondVar;
    private final long mSilenceDurationMs;

    SilencePlaybackQueueItem(TextToSpeechService.UtteranceProgressDispatcher utteranceProgressDispatcher, Object obj, long j) {
        super(utteranceProgressDispatcher, obj);
        this.mCondVar = new ConditionVariable();
        this.mSilenceDurationMs = j;
    }

    @Override // android.speech.tts.PlaybackQueueItem, java.lang.Runnable
    public void run() {
        getDispatcher().dispatchOnStart();
        long j = this.mSilenceDurationMs;
        if (j > 0) {
            this.mCondVar.block(j);
        }
        getDispatcher().dispatchOnDone();
    }

    @Override // android.speech.tts.PlaybackQueueItem
    void stop(boolean z) {
        this.mCondVar.open();
    }
}
