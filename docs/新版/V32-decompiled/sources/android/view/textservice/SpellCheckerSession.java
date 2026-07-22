package android.view.textservice;

import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.textservice.ISpellCheckerSession;
import com.android.internal.textservice.ISpellCheckerSessionListener;
import com.android.internal.textservice.ITextServicesManager;
import com.android.internal.textservice.ITextServicesSessionListener;
import java.util.LinkedList;
import java.util.Queue;

/* JADX INFO: loaded from: classes.dex */
public class SpellCheckerSession {
    private static final boolean DBG = false;
    private static final int MSG_ON_GET_SUGGESTION_MULTIPLE = 1;
    private static final int MSG_ON_GET_SUGGESTION_MULTIPLE_FOR_SENTENCE = 2;
    public static final String SERVICE_META_DATA = "android.view.textservice.scs";
    private static final String TAG = "SpellCheckerSession";
    private final Handler mHandler;
    private final InternalListener mInternalListener;
    private boolean mIsUsed;
    private final SpellCheckerInfo mSpellCheckerInfo;
    private SpellCheckerSessionListener mSpellCheckerSessionListener;
    private final SpellCheckerSessionListenerImpl mSpellCheckerSessionListenerImpl;
    private final SpellCheckerSubtype mSubtype;
    private final ITextServicesManager mTextServicesManager;

    public interface SpellCheckerSessionListener {
        void onGetSentenceSuggestions(SentenceSuggestionsInfo[] sentenceSuggestionsInfoArr);

        void onGetSuggestions(SuggestionsInfo[] suggestionsInfoArr);
    }

    public SpellCheckerSession(SpellCheckerInfo spellCheckerInfo, ITextServicesManager iTextServicesManager, SpellCheckerSessionListener spellCheckerSessionListener, SpellCheckerSubtype spellCheckerSubtype) {
        Handler handler = new Handler() { // from class: android.view.textservice.SpellCheckerSession.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 1) {
                    SpellCheckerSession.this.handleOnGetSuggestionsMultiple((SuggestionsInfo[]) message.obj);
                } else {
                    if (i != 2) {
                        return;
                    }
                    SpellCheckerSession.this.handleOnGetSentenceSuggestionsMultiple((SentenceSuggestionsInfo[]) message.obj);
                }
            }
        };
        this.mHandler = handler;
        if (spellCheckerInfo == null || spellCheckerSessionListener == null || iTextServicesManager == null) {
            throw null;
        }
        this.mSpellCheckerInfo = spellCheckerInfo;
        SpellCheckerSessionListenerImpl spellCheckerSessionListenerImpl = new SpellCheckerSessionListenerImpl(handler);
        this.mSpellCheckerSessionListenerImpl = spellCheckerSessionListenerImpl;
        this.mInternalListener = new InternalListener(spellCheckerSessionListenerImpl);
        this.mTextServicesManager = iTextServicesManager;
        this.mIsUsed = true;
        this.mSpellCheckerSessionListener = spellCheckerSessionListener;
        this.mSubtype = spellCheckerSubtype;
    }

    public boolean isSessionDisconnected() {
        return this.mSpellCheckerSessionListenerImpl.isDisconnected();
    }

    public SpellCheckerInfo getSpellChecker() {
        return this.mSpellCheckerInfo;
    }

    public void cancel() {
        this.mSpellCheckerSessionListenerImpl.cancel();
    }

    public void close() {
        this.mIsUsed = false;
        try {
            this.mSpellCheckerSessionListenerImpl.close();
            this.mTextServicesManager.finishSpellCheckerService(this.mSpellCheckerSessionListenerImpl);
        } catch (RemoteException unused) {
        }
    }

    public void getSentenceSuggestions(TextInfo[] textInfoArr, int i) {
        this.mSpellCheckerSessionListenerImpl.getSentenceSuggestionsMultiple(textInfoArr, i);
    }

    @Deprecated
    public void getSuggestions(TextInfo textInfo, int i) {
        getSuggestions(new TextInfo[]{textInfo}, i, false);
    }

    @Deprecated
    public void getSuggestions(TextInfo[] textInfoArr, int i, boolean z) {
        this.mSpellCheckerSessionListenerImpl.getSuggestionsMultiple(textInfoArr, i, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnGetSuggestionsMultiple(SuggestionsInfo[] suggestionsInfoArr) {
        this.mSpellCheckerSessionListener.onGetSuggestions(suggestionsInfoArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnGetSentenceSuggestionsMultiple(SentenceSuggestionsInfo[] sentenceSuggestionsInfoArr) {
        this.mSpellCheckerSessionListener.onGetSentenceSuggestions(sentenceSuggestionsInfoArr);
    }

    private static class SpellCheckerSessionListenerImpl extends ISpellCheckerSessionListener.Stub {
        private static final int TASK_CANCEL = 1;
        private static final int TASK_CLOSE = 3;
        private static final int TASK_GET_SUGGESTIONS_MULTIPLE = 2;
        private static final int TASK_GET_SUGGESTIONS_MULTIPLE_FOR_SENTENCE = 4;
        private Handler mAsyncHandler;
        private Handler mHandler;
        private ISpellCheckerSession mISpellCheckerSession;
        private HandlerThread mThread;
        private final Queue<SpellCheckerParams> mPendingTasks = new LinkedList();
        private boolean mOpened = false;

        public SpellCheckerSessionListenerImpl(Handler handler) {
            this.mHandler = handler;
        }

        private static class SpellCheckerParams {
            public final boolean mSequentialWords;
            public ISpellCheckerSession mSession;
            public final int mSuggestionsLimit;
            public final TextInfo[] mTextInfos;
            public final int mWhat;

            public SpellCheckerParams(int i, TextInfo[] textInfoArr, int i2, boolean z) {
                this.mWhat = i;
                this.mTextInfos = textInfoArr;
                this.mSuggestionsLimit = i2;
                this.mSequentialWords = z;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void processTask(ISpellCheckerSession iSpellCheckerSession, SpellCheckerParams spellCheckerParams, boolean z) {
            if (z || this.mAsyncHandler == null) {
                int i = spellCheckerParams.mWhat;
                if (i == 1) {
                    try {
                        iSpellCheckerSession.onCancel();
                    } catch (RemoteException e) {
                        Log.e(SpellCheckerSession.TAG, "Failed to cancel " + e);
                    }
                } else if (i == 2) {
                    try {
                        iSpellCheckerSession.onGetSuggestionsMultiple(spellCheckerParams.mTextInfos, spellCheckerParams.mSuggestionsLimit, spellCheckerParams.mSequentialWords);
                    } catch (RemoteException e2) {
                        Log.e(SpellCheckerSession.TAG, "Failed to get suggestions " + e2);
                    }
                } else if (i == 3) {
                    try {
                        iSpellCheckerSession.onClose();
                    } catch (RemoteException e3) {
                        Log.e(SpellCheckerSession.TAG, "Failed to close " + e3);
                    }
                } else if (i == 4) {
                    try {
                        iSpellCheckerSession.onGetSentenceSuggestionsMultiple(spellCheckerParams.mTextInfos, spellCheckerParams.mSuggestionsLimit);
                    } catch (RemoteException e4) {
                        Log.e(SpellCheckerSession.TAG, "Failed to get suggestions " + e4);
                    }
                }
            } else {
                spellCheckerParams.mSession = iSpellCheckerSession;
                Handler handler = this.mAsyncHandler;
                handler.sendMessage(Message.obtain(handler, 1, spellCheckerParams));
            }
            if (spellCheckerParams.mWhat == 3) {
                synchronized (this) {
                    this.mISpellCheckerSession = null;
                    this.mHandler = null;
                    HandlerThread handlerThread = this.mThread;
                    if (handlerThread != null) {
                        handlerThread.quit();
                    }
                    this.mThread = null;
                    this.mAsyncHandler = null;
                }
            }
        }

        public synchronized void onServiceConnected(ISpellCheckerSession iSpellCheckerSession) {
            synchronized (this) {
                this.mISpellCheckerSession = iSpellCheckerSession;
                if ((iSpellCheckerSession.asBinder() instanceof Binder) && this.mThread == null) {
                    HandlerThread handlerThread = new HandlerThread("SpellCheckerSession", 10);
                    this.mThread = handlerThread;
                    handlerThread.start();
                    this.mAsyncHandler = new Handler(this.mThread.getLooper()) { // from class: android.view.textservice.SpellCheckerSession.SpellCheckerSessionListenerImpl.1
                        @Override // android.os.Handler
                        public void handleMessage(Message message) {
                            SpellCheckerParams spellCheckerParams = (SpellCheckerParams) message.obj;
                            SpellCheckerSessionListenerImpl.this.processTask(spellCheckerParams.mSession, spellCheckerParams, true);
                        }
                    };
                }
                this.mOpened = true;
            }
            while (!this.mPendingTasks.isEmpty()) {
                processTask(iSpellCheckerSession, this.mPendingTasks.poll(), false);
            }
        }

        public void cancel() {
            processOrEnqueueTask(new SpellCheckerParams(1, null, 0, false));
        }

        public void getSuggestionsMultiple(TextInfo[] textInfoArr, int i, boolean z) {
            processOrEnqueueTask(new SpellCheckerParams(2, textInfoArr, i, z));
        }

        public void getSentenceSuggestionsMultiple(TextInfo[] textInfoArr, int i) {
            processOrEnqueueTask(new SpellCheckerParams(4, textInfoArr, i, false));
        }

        public void close() {
            processOrEnqueueTask(new SpellCheckerParams(3, null, 0, false));
        }

        public boolean isDisconnected() {
            return this.mOpened && this.mISpellCheckerSession == null;
        }

        private void processOrEnqueueTask(SpellCheckerParams spellCheckerParams) {
            synchronized (this) {
                ISpellCheckerSession iSpellCheckerSession = this.mISpellCheckerSession;
                if (iSpellCheckerSession == null) {
                    SpellCheckerParams spellCheckerParams2 = null;
                    if (spellCheckerParams.mWhat == 1) {
                        while (!this.mPendingTasks.isEmpty()) {
                            SpellCheckerParams spellCheckerParamsPoll = this.mPendingTasks.poll();
                            if (spellCheckerParamsPoll.mWhat == 3) {
                                spellCheckerParams2 = spellCheckerParamsPoll;
                            }
                        }
                    }
                    this.mPendingTasks.offer(spellCheckerParams);
                    if (spellCheckerParams2 != null) {
                        this.mPendingTasks.offer(spellCheckerParams2);
                    }
                    return;
                }
                processTask(iSpellCheckerSession, spellCheckerParams, false);
            }
        }

        public void onGetSuggestions(SuggestionsInfo[] suggestionsInfoArr) {
            synchronized (this) {
                Handler handler = this.mHandler;
                if (handler != null) {
                    handler.sendMessage(Message.obtain(handler, 1, suggestionsInfoArr));
                }
            }
        }

        public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] sentenceSuggestionsInfoArr) {
            Handler handler = this.mHandler;
            handler.sendMessage(Message.obtain(handler, 2, sentenceSuggestionsInfoArr));
        }
    }

    private static class InternalListener extends ITextServicesSessionListener.Stub {
        private final SpellCheckerSessionListenerImpl mParentSpellCheckerSessionListenerImpl;

        public InternalListener(SpellCheckerSessionListenerImpl spellCheckerSessionListenerImpl) {
            this.mParentSpellCheckerSessionListenerImpl = spellCheckerSessionListenerImpl;
        }

        public void onServiceConnected(ISpellCheckerSession iSpellCheckerSession) {
            this.mParentSpellCheckerSessionListenerImpl.onServiceConnected(iSpellCheckerSession);
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();
        if (this.mIsUsed) {
            Log.e(TAG, "SpellCheckerSession was not finished properly.You should call finishShession() when you finished to use a spell checker.");
            close();
        }
    }

    public ITextServicesSessionListener getTextServicesSessionListener() {
        return this.mInternalListener;
    }

    public ISpellCheckerSessionListener getSpellCheckerSessionListener() {
        return this.mSpellCheckerSessionListenerImpl;
    }
}
