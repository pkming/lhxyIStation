package android.speech.tts;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.ConditionVariable;
import android.speech.tts.TextToSpeechService;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
class AudioPlaybackQueueItem extends PlaybackQueueItem {
    private static final String TAG = "TTS.AudioQueueItem";
    private final Context mContext;
    private final ConditionVariable mDone;
    private volatile boolean mFinished;
    private MediaPlayer mPlayer;
    private final int mStreamType;
    private final Uri mUri;

    AudioPlaybackQueueItem(TextToSpeechService.UtteranceProgressDispatcher utteranceProgressDispatcher, Object obj, Context context, Uri uri, int i) {
        super(utteranceProgressDispatcher, obj);
        this.mContext = context;
        this.mUri = uri;
        this.mStreamType = i;
        this.mDone = new ConditionVariable();
        this.mPlayer = null;
        this.mFinished = false;
    }

    @Override // android.speech.tts.PlaybackQueueItem, java.lang.Runnable
    public void run() {
        TextToSpeechService.UtteranceProgressDispatcher dispatcher = getDispatcher();
        dispatcher.dispatchOnStart();
        MediaPlayer mediaPlayerCreate = MediaPlayer.create(this.mContext, this.mUri);
        this.mPlayer = mediaPlayerCreate;
        if (mediaPlayerCreate == null) {
            dispatcher.dispatchOnError();
            return;
        }
        try {
            mediaPlayerCreate.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: android.speech.tts.AudioPlaybackQueueItem.1
                @Override // android.media.MediaPlayer.OnErrorListener
                public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                    Log.w(AudioPlaybackQueueItem.TAG, "Audio playback error: " + i + ", " + i2);
                    AudioPlaybackQueueItem.this.mDone.open();
                    return true;
                }
            });
            this.mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: android.speech.tts.AudioPlaybackQueueItem.2
                @Override // android.media.MediaPlayer.OnCompletionListener
                public void onCompletion(MediaPlayer mediaPlayer) {
                    AudioPlaybackQueueItem.this.mFinished = true;
                    AudioPlaybackQueueItem.this.mDone.open();
                }
            });
            this.mPlayer.setAudioStreamType(this.mStreamType);
            this.mPlayer.start();
            this.mDone.block();
            finish();
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "MediaPlayer failed", e);
            this.mDone.open();
        }
        if (this.mFinished) {
            dispatcher.dispatchOnDone();
        } else {
            dispatcher.dispatchOnError();
        }
    }

    private void finish() {
        try {
            this.mPlayer.stop();
        } catch (IllegalStateException unused) {
        }
        this.mPlayer.release();
    }

    @Override // android.speech.tts.PlaybackQueueItem
    void stop(boolean z) {
        this.mDone.open();
    }
}
