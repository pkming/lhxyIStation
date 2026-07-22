package android.speech.tts;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.ParcelFileDescriptor;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.provider.Settings;
import android.speech.tts.ITextToSpeechService;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public abstract class TextToSpeechService extends Service {
    private static final boolean DBG = false;
    private static final String SYNTH_THREAD_NAME = "SynthThread";
    private static final String TAG = "TextToSpeechService";
    private AudioPlaybackHandler mAudioPlaybackHandler;
    private final ITextToSpeechService.Stub mBinder = new ITextToSpeechService.Stub() { // from class: android.speech.tts.TextToSpeechService.1
        @Override // android.speech.tts.ITextToSpeechService
        public int speak(IBinder iBinder, String str, int i, Bundle bundle) {
            if (checkNonNull(iBinder, str, bundle)) {
                return TextToSpeechService.this.mSynthHandler.enqueueSpeechItem(i, TextToSpeechService.this.new SynthesisSpeechItem(iBinder, Binder.getCallingUid(), Binder.getCallingPid(), bundle, str));
            }
            return -1;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public int synthesizeToFileDescriptor(IBinder iBinder, String str, ParcelFileDescriptor parcelFileDescriptor, Bundle bundle) {
            if (checkNonNull(iBinder, str, parcelFileDescriptor, bundle)) {
                return TextToSpeechService.this.mSynthHandler.enqueueSpeechItem(1, TextToSpeechService.this.new SynthesisToFileOutputStreamSpeechItem(iBinder, Binder.getCallingUid(), Binder.getCallingPid(), bundle, str, new ParcelFileDescriptor.AutoCloseOutputStream(ParcelFileDescriptor.adoptFd(parcelFileDescriptor.detachFd()))));
            }
            return -1;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public int playAudio(IBinder iBinder, Uri uri, int i, Bundle bundle) {
            if (checkNonNull(iBinder, uri, bundle)) {
                return TextToSpeechService.this.mSynthHandler.enqueueSpeechItem(i, TextToSpeechService.this.new AudioSpeechItem(iBinder, Binder.getCallingUid(), Binder.getCallingPid(), bundle, uri));
            }
            return -1;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public int playSilence(IBinder iBinder, long j, int i, Bundle bundle) {
            if (checkNonNull(iBinder, bundle)) {
                return TextToSpeechService.this.mSynthHandler.enqueueSpeechItem(i, TextToSpeechService.this.new SilenceSpeechItem(iBinder, Binder.getCallingUid(), Binder.getCallingPid(), bundle, j));
            }
            return -1;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public boolean isSpeaking() {
            return TextToSpeechService.this.mSynthHandler.isSpeaking() || TextToSpeechService.this.mAudioPlaybackHandler.isSpeaking();
        }

        @Override // android.speech.tts.ITextToSpeechService
        public int stop(IBinder iBinder) {
            if (checkNonNull(iBinder)) {
                return TextToSpeechService.this.mSynthHandler.stopForApp(iBinder);
            }
            return -1;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public String[] getLanguage() {
            return TextToSpeechService.this.onGetLanguage();
        }

        @Override // android.speech.tts.ITextToSpeechService
        public String[] getClientDefaultLanguage() {
            return TextToSpeechService.this.getSettingsLocale();
        }

        @Override // android.speech.tts.ITextToSpeechService
        public int isLanguageAvailable(String str, String str2, String str3) {
            if (checkNonNull(str)) {
                return TextToSpeechService.this.onIsLanguageAvailable(str, str2, str3);
            }
            return -1;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public String[] getFeaturesForLanguage(String str, String str2, String str3) {
            Set<String> setOnGetFeaturesForLanguage = TextToSpeechService.this.onGetFeaturesForLanguage(str, str2, str3);
            if (setOnGetFeaturesForLanguage == null) {
                return new String[0];
            }
            String[] strArr = new String[setOnGetFeaturesForLanguage.size()];
            setOnGetFeaturesForLanguage.toArray(strArr);
            return strArr;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public int loadLanguage(IBinder iBinder, String str, String str2, String str3) {
            if (!checkNonNull(str)) {
                return -1;
            }
            int iOnIsLanguageAvailable = TextToSpeechService.this.onIsLanguageAvailable(str, str2, str3);
            if ((iOnIsLanguageAvailable == 0 || iOnIsLanguageAvailable == 1 || iOnIsLanguageAvailable == 2) && TextToSpeechService.this.mSynthHandler.enqueueSpeechItem(1, TextToSpeechService.this.new LoadLanguageItem(iBinder, Binder.getCallingUid(), Binder.getCallingPid(), null, str, str2, str3)) != 0) {
                return -1;
            }
            return iOnIsLanguageAvailable;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public void setCallback(IBinder iBinder, ITextToSpeechCallback iTextToSpeechCallback) {
            if (checkNonNull(iBinder)) {
                TextToSpeechService.this.mCallbacks.setCallback(iBinder, iTextToSpeechCallback);
            }
        }

        private String intern(String str) {
            return str.intern();
        }

        private boolean checkNonNull(Object... objArr) {
            for (Object obj : objArr) {
                if (obj == null) {
                    return false;
                }
            }
            return true;
        }
    };
    private CallbackMap mCallbacks;
    private TtsEngines mEngineHelper;
    private String mPackageName;
    private SynthHandler mSynthHandler;

    interface UtteranceProgressDispatcher {
        void dispatchOnDone();

        void dispatchOnError();

        void dispatchOnStart();
    }

    protected Set<String> onGetFeaturesForLanguage(String str, String str2, String str3) {
        return null;
    }

    protected abstract String[] onGetLanguage();

    protected abstract int onIsLanguageAvailable(String str, String str2, String str3);

    protected abstract int onLoadLanguage(String str, String str2, String str3);

    protected abstract void onStop();

    protected abstract void onSynthesizeText(SynthesisRequest synthesisRequest, SynthesisCallback synthesisCallback);

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        SynthThread synthThread = new SynthThread();
        synthThread.start();
        this.mSynthHandler = new SynthHandler(synthThread.getLooper());
        AudioPlaybackHandler audioPlaybackHandler = new AudioPlaybackHandler();
        this.mAudioPlaybackHandler = audioPlaybackHandler;
        audioPlaybackHandler.start();
        this.mEngineHelper = new TtsEngines(this);
        this.mCallbacks = new CallbackMap();
        this.mPackageName = getApplicationInfo().packageName;
        String[] settingsLocale = getSettingsLocale();
        onLoadLanguage(settingsLocale[0], settingsLocale[1], settingsLocale[2]);
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.mSynthHandler.quit();
        this.mAudioPlaybackHandler.quit();
        this.mCallbacks.kill();
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDefaultSpeechRate() {
        return getSecureSettingInt(Settings.Secure.TTS_DEFAULT_RATE, 100);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String[] getSettingsLocale() {
        return TtsEngines.parseLocalePref(this.mEngineHelper.getLocalePrefForEngine(this.mPackageName));
    }

    private int getSecureSettingInt(String str, int i) {
        return Settings.Secure.getInt(getContentResolver(), str, i);
    }

    private class SynthThread extends HandlerThread implements MessageQueue.IdleHandler {
        private boolean mFirstIdle;

        public SynthThread() {
            super(TextToSpeechService.SYNTH_THREAD_NAME, 0);
            this.mFirstIdle = true;
        }

        @Override // android.os.HandlerThread
        protected void onLooperPrepared() {
            getLooper().getQueue().addIdleHandler(this);
        }

        @Override // android.os.MessageQueue.IdleHandler
        public boolean queueIdle() {
            if (this.mFirstIdle) {
                this.mFirstIdle = false;
                return true;
            }
            broadcastTtsQueueProcessingCompleted();
            return true;
        }

        private void broadcastTtsQueueProcessingCompleted() {
            TextToSpeechService.this.sendBroadcast(new Intent(TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED));
        }
    }

    private class SynthHandler extends Handler {
        private SpeechItem mCurrentSpeechItem;

        public SynthHandler(Looper looper) {
            super(looper);
            this.mCurrentSpeechItem = null;
        }

        private synchronized SpeechItem getCurrentSpeechItem() {
            return this.mCurrentSpeechItem;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized SpeechItem setCurrentSpeechItem(SpeechItem speechItem) {
            SpeechItem speechItem2;
            speechItem2 = this.mCurrentSpeechItem;
            this.mCurrentSpeechItem = speechItem;
            return speechItem2;
        }

        private synchronized SpeechItem maybeRemoveCurrentSpeechItem(Object obj) {
            SpeechItem speechItem = this.mCurrentSpeechItem;
            if (speechItem == null || speechItem.getCallerIdentity() != obj) {
                return null;
            }
            SpeechItem speechItem2 = this.mCurrentSpeechItem;
            this.mCurrentSpeechItem = null;
            return speechItem2;
        }

        public boolean isSpeaking() {
            return getCurrentSpeechItem() != null;
        }

        public void quit() {
            getLooper().quit();
            SpeechItem currentSpeechItem = setCurrentSpeechItem(null);
            if (currentSpeechItem != null) {
                currentSpeechItem.stop();
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        public int enqueueSpeechItem(int i, final SpeechItem speechItem) {
            UtteranceProgressDispatcher utteranceProgressDispatcher = speechItem instanceof UtteranceProgressDispatcher ? (UtteranceProgressDispatcher) speechItem : null;
            if (!speechItem.isValid()) {
                if (utteranceProgressDispatcher != null) {
                    utteranceProgressDispatcher.dispatchOnError();
                }
                return -1;
            }
            if (i == 0) {
                stopForApp(speechItem.getCallerIdentity());
            } else if (i == 2) {
                stopAll();
            }
            Message messageObtain = Message.obtain(this, new Runnable() { // from class: android.speech.tts.TextToSpeechService.SynthHandler.1
                @Override // java.lang.Runnable
                public void run() {
                    SynthHandler.this.setCurrentSpeechItem(speechItem);
                    speechItem.play();
                    SynthHandler.this.setCurrentSpeechItem(null);
                }
            });
            messageObtain.obj = speechItem.getCallerIdentity();
            if (sendMessage(messageObtain)) {
                return 0;
            }
            Log.w(TextToSpeechService.TAG, "SynthThread has quit");
            if (utteranceProgressDispatcher != null) {
                utteranceProgressDispatcher.dispatchOnError();
            }
            return -1;
        }

        public int stopForApp(Object obj) {
            if (obj == null) {
                return -1;
            }
            removeCallbacksAndMessages(obj);
            SpeechItem speechItemMaybeRemoveCurrentSpeechItem = maybeRemoveCurrentSpeechItem(obj);
            if (speechItemMaybeRemoveCurrentSpeechItem != null) {
                speechItemMaybeRemoveCurrentSpeechItem.stop();
            }
            TextToSpeechService.this.mAudioPlaybackHandler.stopForApp(obj);
            return 0;
        }

        public int stopAll() {
            SpeechItem currentSpeechItem = setCurrentSpeechItem(null);
            if (currentSpeechItem != null) {
                currentSpeechItem.stop();
            }
            removeCallbacksAndMessages(null);
            TextToSpeechService.this.mAudioPlaybackHandler.stop();
            return 0;
        }
    }

    private abstract class SpeechItem {
        private final Object mCallerIdentity;
        private final int mCallerPid;
        private final int mCallerUid;
        protected final Bundle mParams;
        private boolean mStarted = false;
        private boolean mStopped = false;

        public abstract boolean isValid();

        protected abstract int playImpl();

        protected abstract void stopImpl();

        public SpeechItem(Object obj, int i, int i2, Bundle bundle) {
            this.mCallerIdentity = obj;
            this.mParams = bundle;
            this.mCallerUid = i;
            this.mCallerPid = i2;
        }

        public Object getCallerIdentity() {
            return this.mCallerIdentity;
        }

        public int getCallerUid() {
            return this.mCallerUid;
        }

        public int getCallerPid() {
            return this.mCallerPid;
        }

        public int play() {
            synchronized (this) {
                if (this.mStarted) {
                    throw new IllegalStateException("play() called twice");
                }
                this.mStarted = true;
            }
            return playImpl();
        }

        public void stop() {
            synchronized (this) {
                if (this.mStopped) {
                    throw new IllegalStateException("stop() called twice");
                }
                this.mStopped = true;
            }
            stopImpl();
        }

        protected synchronized boolean isStopped() {
            return this.mStopped;
        }
    }

    private abstract class UtteranceSpeechItem extends SpeechItem implements UtteranceProgressDispatcher {
        public UtteranceSpeechItem(Object obj, int i, int i2, Bundle bundle) {
            super(obj, i, i2, bundle);
        }

        @Override // android.speech.tts.TextToSpeechService.UtteranceProgressDispatcher
        public void dispatchOnDone() {
            String utteranceId = getUtteranceId();
            if (utteranceId != null) {
                TextToSpeechService.this.mCallbacks.dispatchOnDone(getCallerIdentity(), utteranceId);
            }
        }

        @Override // android.speech.tts.TextToSpeechService.UtteranceProgressDispatcher
        public void dispatchOnStart() {
            String utteranceId = getUtteranceId();
            if (utteranceId != null) {
                TextToSpeechService.this.mCallbacks.dispatchOnStart(getCallerIdentity(), utteranceId);
            }
        }

        @Override // android.speech.tts.TextToSpeechService.UtteranceProgressDispatcher
        public void dispatchOnError() {
            String utteranceId = getUtteranceId();
            if (utteranceId != null) {
                TextToSpeechService.this.mCallbacks.dispatchOnError(getCallerIdentity(), utteranceId);
            }
        }

        public int getStreamType() {
            return getIntParam(TextToSpeech.Engine.KEY_PARAM_STREAM, 3);
        }

        public float getVolume() {
            return getFloatParam("volume", 1.0f);
        }

        public float getPan() {
            return getFloatParam(TextToSpeech.Engine.KEY_PARAM_PAN, 0.0f);
        }

        public String getUtteranceId() {
            return getStringParam(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, null);
        }

        protected String getStringParam(String str, String str2) {
            return this.mParams == null ? str2 : this.mParams.getString(str, str2);
        }

        protected int getIntParam(String str, int i) {
            return this.mParams == null ? i : this.mParams.getInt(str, i);
        }

        protected float getFloatParam(String str, float f) {
            return this.mParams == null ? f : this.mParams.getFloat(str, f);
        }
    }

    class SynthesisSpeechItem extends UtteranceSpeechItem {
        private final int mCallerUid;
        private final String[] mDefaultLocale;
        private final EventLogger mEventLogger;
        private AbstractSynthesisCallback mSynthesisCallback;
        private final SynthesisRequest mSynthesisRequest;
        private final String mText;

        public SynthesisSpeechItem(Object obj, int i, int i2, Bundle bundle, String str) {
            super(obj, i, i2, bundle);
            this.mText = str;
            this.mCallerUid = i;
            SynthesisRequest synthesisRequest = new SynthesisRequest(str, this.mParams);
            this.mSynthesisRequest = synthesisRequest;
            this.mDefaultLocale = TextToSpeechService.this.getSettingsLocale();
            setRequestParams(synthesisRequest);
            this.mEventLogger = new EventLogger(synthesisRequest, i, i2, TextToSpeechService.this.mPackageName);
        }

        public String getText() {
            return this.mText;
        }

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        public boolean isValid() {
            String str = this.mText;
            if (str == null) {
                Log.e(TextToSpeechService.TAG, "null synthesis text");
                return false;
            }
            if (str.length() < TextToSpeech.getMaxSpeechInputLength()) {
                return true;
            }
            Log.w(TextToSpeechService.TAG, "Text too long: " + this.mText.length() + " chars");
            return false;
        }

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        protected int playImpl() {
            this.mEventLogger.onRequestProcessingStart();
            synchronized (this) {
                if (isStopped()) {
                    return -1;
                }
                AbstractSynthesisCallback abstractSynthesisCallbackCreateSynthesisCallback = createSynthesisCallback();
                this.mSynthesisCallback = abstractSynthesisCallbackCreateSynthesisCallback;
                TextToSpeechService.this.onSynthesizeText(this.mSynthesisRequest, abstractSynthesisCallbackCreateSynthesisCallback);
                return abstractSynthesisCallbackCreateSynthesisCallback.isDone() ? 0 : -1;
            }
        }

        protected AbstractSynthesisCallback createSynthesisCallback() {
            return new PlaybackSynthesisCallback(getStreamType(), getVolume(), getPan(), TextToSpeechService.this.mAudioPlaybackHandler, this, getCallerIdentity(), this.mEventLogger);
        }

        private void setRequestParams(SynthesisRequest synthesisRequest) {
            synthesisRequest.setLanguage(getLanguage(), getCountry(), getVariant());
            synthesisRequest.setSpeechRate(getSpeechRate());
            synthesisRequest.setCallerUid(this.mCallerUid);
            synthesisRequest.setPitch(getPitch());
        }

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        protected void stopImpl() {
            AbstractSynthesisCallback abstractSynthesisCallback;
            synchronized (this) {
                abstractSynthesisCallback = this.mSynthesisCallback;
            }
            if (abstractSynthesisCallback != null) {
                abstractSynthesisCallback.stop();
                TextToSpeechService.this.onStop();
            }
        }

        public String getLanguage() {
            return getStringParam("language", this.mDefaultLocale[0]);
        }

        private boolean hasLanguage() {
            return !TextUtils.isEmpty(getStringParam("language", null));
        }

        private String getCountry() {
            return !hasLanguage() ? this.mDefaultLocale[1] : getStringParam("country", "");
        }

        private String getVariant() {
            return !hasLanguage() ? this.mDefaultLocale[2] : getStringParam(TextToSpeech.Engine.KEY_PARAM_VARIANT, "");
        }

        private int getSpeechRate() {
            return getIntParam(TextToSpeech.Engine.KEY_PARAM_RATE, TextToSpeechService.this.getDefaultSpeechRate());
        }

        private int getPitch() {
            return getIntParam(TextToSpeech.Engine.KEY_PARAM_PITCH, 100);
        }
    }

    private class SynthesisToFileOutputStreamSpeechItem extends SynthesisSpeechItem {
        private final FileOutputStream mFileOutputStream;

        public SynthesisToFileOutputStreamSpeechItem(Object obj, int i, int i2, Bundle bundle, String str, FileOutputStream fileOutputStream) {
            super(obj, i, i2, bundle, str);
            this.mFileOutputStream = fileOutputStream;
        }

        @Override // android.speech.tts.TextToSpeechService.SynthesisSpeechItem
        protected AbstractSynthesisCallback createSynthesisCallback() {
            return new FileSynthesisCallback(this.mFileOutputStream.getChannel());
        }

        @Override // android.speech.tts.TextToSpeechService.SynthesisSpeechItem, android.speech.tts.TextToSpeechService.SpeechItem
        protected int playImpl() {
            dispatchOnStart();
            int iPlayImpl = super.playImpl();
            if (iPlayImpl == 0) {
                dispatchOnDone();
            } else {
                dispatchOnError();
            }
            try {
                this.mFileOutputStream.close();
            } catch (IOException e) {
                Log.w(TextToSpeechService.TAG, "Failed to close output file", e);
            }
            return iPlayImpl;
        }
    }

    private class AudioSpeechItem extends UtteranceSpeechItem {
        private final AudioPlaybackQueueItem mItem;

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        public boolean isValid() {
            return true;
        }

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        protected void stopImpl() {
        }

        public AudioSpeechItem(Object obj, int i, int i2, Bundle bundle, Uri uri) {
            super(obj, i, i2, bundle);
            this.mItem = new AudioPlaybackQueueItem(this, getCallerIdentity(), TextToSpeechService.this, uri, getStreamType());
        }

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        protected int playImpl() {
            TextToSpeechService.this.mAudioPlaybackHandler.enqueue(this.mItem);
            return 0;
        }
    }

    private class SilenceSpeechItem extends UtteranceSpeechItem {
        private final long mDuration;

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        public boolean isValid() {
            return true;
        }

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        protected void stopImpl() {
        }

        public SilenceSpeechItem(Object obj, int i, int i2, Bundle bundle, long j) {
            super(obj, i, i2, bundle);
            this.mDuration = j;
        }

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        protected int playImpl() {
            TextToSpeechService.this.mAudioPlaybackHandler.enqueue(new SilencePlaybackQueueItem(this, getCallerIdentity(), this.mDuration));
            return 0;
        }
    }

    private class LoadLanguageItem extends SpeechItem {
        private final String mCountry;
        private final String mLanguage;
        private final String mVariant;

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        public boolean isValid() {
            return true;
        }

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        protected void stopImpl() {
        }

        public LoadLanguageItem(Object obj, int i, int i2, Bundle bundle, String str, String str2, String str3) {
            super(obj, i, i2, bundle);
            this.mLanguage = str;
            this.mCountry = str2;
            this.mVariant = str3;
        }

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        protected int playImpl() {
            int iOnLoadLanguage = TextToSpeechService.this.onLoadLanguage(this.mLanguage, this.mCountry, this.mVariant);
            return (iOnLoadLanguage == 0 || iOnLoadLanguage == 1 || iOnLoadLanguage == 2) ? 0 : -1;
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (TextToSpeech.Engine.INTENT_ACTION_TTS_SERVICE.equals(intent.getAction())) {
            return this.mBinder;
        }
        return null;
    }

    private class CallbackMap extends RemoteCallbackList<ITextToSpeechCallback> {
        private final HashMap<IBinder, ITextToSpeechCallback> mCallerToCallback;

        private CallbackMap() {
            this.mCallerToCallback = new HashMap<>();
        }

        public void setCallback(IBinder iBinder, ITextToSpeechCallback iTextToSpeechCallback) {
            ITextToSpeechCallback iTextToSpeechCallbackRemove;
            synchronized (this.mCallerToCallback) {
                if (iTextToSpeechCallback != null) {
                    register(iTextToSpeechCallback, iBinder);
                    iTextToSpeechCallbackRemove = this.mCallerToCallback.put(iBinder, iTextToSpeechCallback);
                } else {
                    iTextToSpeechCallbackRemove = this.mCallerToCallback.remove(iBinder);
                }
                if (iTextToSpeechCallbackRemove != null && iTextToSpeechCallbackRemove != iTextToSpeechCallback) {
                    unregister(iTextToSpeechCallbackRemove);
                }
            }
        }

        public void dispatchOnDone(Object obj, String str) {
            ITextToSpeechCallback callbackFor = getCallbackFor(obj);
            if (callbackFor == null) {
                return;
            }
            try {
                callbackFor.onDone(str);
            } catch (RemoteException e) {
                Log.e(TextToSpeechService.TAG, "Callback onDone failed: " + e);
            }
        }

        public void dispatchOnStart(Object obj, String str) {
            ITextToSpeechCallback callbackFor = getCallbackFor(obj);
            if (callbackFor == null) {
                return;
            }
            try {
                callbackFor.onStart(str);
            } catch (RemoteException e) {
                Log.e(TextToSpeechService.TAG, "Callback onStart failed: " + e);
            }
        }

        public void dispatchOnError(Object obj, String str) {
            ITextToSpeechCallback callbackFor = getCallbackFor(obj);
            if (callbackFor == null) {
                return;
            }
            try {
                callbackFor.onError(str);
            } catch (RemoteException e) {
                Log.e(TextToSpeechService.TAG, "Callback onError failed: " + e);
            }
        }

        @Override // android.os.RemoteCallbackList
        public void onCallbackDied(ITextToSpeechCallback iTextToSpeechCallback, Object obj) {
            IBinder iBinder = (IBinder) obj;
            synchronized (this.mCallerToCallback) {
                this.mCallerToCallback.remove(iBinder);
            }
            TextToSpeechService.this.mSynthHandler.stopForApp(iBinder);
        }

        @Override // android.os.RemoteCallbackList
        public void kill() {
            synchronized (this.mCallerToCallback) {
                this.mCallerToCallback.clear();
                super.kill();
            }
        }

        private ITextToSpeechCallback getCallbackFor(Object obj) {
            ITextToSpeechCallback iTextToSpeechCallback;
            IBinder iBinder = (IBinder) obj;
            synchronized (this.mCallerToCallback) {
                iTextToSpeechCallback = this.mCallerToCallback.get(iBinder);
            }
            return iTextToSpeechCallback;
        }
    }
}
