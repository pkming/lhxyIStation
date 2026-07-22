package android.preference;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;
import com.android.internal.R;

/* JADX INFO: loaded from: classes.dex */
public class VolumePreference extends SeekBarDialogPreference implements PreferenceManager.OnActivityStopListener, View.OnKeyListener {
    private static final String TAG = "VolumePreference";
    private SeekBarVolumizer mSeekBarVolumizer;
    private int mStreamType;

    public static class VolumeStore {
        public int volume = -1;
        public int originalVolume = -1;
    }

    public VolumePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.VolumePreference, 0, 0);
        this.mStreamType = typedArrayObtainStyledAttributes.getInt(0, 0);
        typedArrayObtainStyledAttributes.recycle();
    }

    public void setStreamType(int i) {
        this.mStreamType = i;
    }

    @Override // android.preference.SeekBarDialogPreference, android.preference.DialogPreference
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        this.mSeekBarVolumizer = new SeekBarVolumizer(this, getContext(), (SeekBar) view.findViewById(16909056), this.mStreamType);
        getPreferenceManager().registerOnActivityStopListener(this);
        view.setOnKeyListener(this);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    @Override // android.preference.Preference
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (this.mSeekBarVolumizer == null) {
            return true;
        }
        boolean z = keyEvent.getAction() == 0;
        if (i == 24) {
            if (z) {
                this.mSeekBarVolumizer.changeVolumeBy(1);
            }
            return true;
        }
        if (i == 25) {
            if (z) {
                this.mSeekBarVolumizer.changeVolumeBy(-1);
            }
            return true;
        }
        if (i != 164) {
            return false;
        }
        if (z) {
            this.mSeekBarVolumizer.muteVolume();
        }
        return true;
    }

    @Override // android.preference.DialogPreference
    protected void onDialogClosed(boolean z) {
        SeekBarVolumizer seekBarVolumizer;
        super.onDialogClosed(z);
        if (!z && (seekBarVolumizer = this.mSeekBarVolumizer) != null) {
            seekBarVolumizer.revertVolume();
        }
        cleanup();
    }

    @Override // android.preference.PreferenceManager.OnActivityStopListener
    public void onActivityStop() {
        SeekBarVolumizer seekBarVolumizer = this.mSeekBarVolumizer;
        if (seekBarVolumizer != null) {
            seekBarVolumizer.postStopSample();
        }
    }

    private void cleanup() {
        getPreferenceManager().unregisterOnActivityStopListener(this);
        if (this.mSeekBarVolumizer != null) {
            Dialog dialog = getDialog();
            if (dialog != null && dialog.isShowing()) {
                View viewFindViewById = dialog.getWindow().getDecorView().findViewById(16909056);
                if (viewFindViewById != null) {
                    viewFindViewById.setOnKeyListener(null);
                }
                this.mSeekBarVolumizer.revertVolume();
            }
            this.mSeekBarVolumizer.stop();
            this.mSeekBarVolumizer = null;
        }
    }

    protected void onSampleStarting(SeekBarVolumizer seekBarVolumizer) {
        SeekBarVolumizer seekBarVolumizer2 = this.mSeekBarVolumizer;
        if (seekBarVolumizer2 == null || seekBarVolumizer == seekBarVolumizer2) {
            return;
        }
        seekBarVolumizer2.stopSample();
    }

    @Override // android.preference.DialogPreference, android.preference.Preference
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelableOnSaveInstanceState = super.onSaveInstanceState();
        if (isPersistent()) {
            return parcelableOnSaveInstanceState;
        }
        SavedState savedState = new SavedState(parcelableOnSaveInstanceState);
        SeekBarVolumizer seekBarVolumizer = this.mSeekBarVolumizer;
        if (seekBarVolumizer != null) {
            seekBarVolumizer.onSaveInstanceState(savedState.getVolumeStore());
        }
        return savedState;
    }

    @Override // android.preference.DialogPreference, android.preference.Preference
    protected void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable == null || !parcelable.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        SeekBarVolumizer seekBarVolumizer = this.mSeekBarVolumizer;
        if (seekBarVolumizer != null) {
            seekBarVolumizer.onRestoreInstanceState(savedState.getVolumeStore());
        }
    }

    private static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.preference.VolumePreference.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        VolumeStore mVolumeStore;

        public SavedState(Parcel parcel) {
            super(parcel);
            VolumeStore volumeStore = new VolumeStore();
            this.mVolumeStore = volumeStore;
            volumeStore.volume = parcel.readInt();
            this.mVolumeStore.originalVolume = parcel.readInt();
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.mVolumeStore.volume);
            parcel.writeInt(this.mVolumeStore.originalVolume);
        }

        VolumeStore getVolumeStore() {
            return this.mVolumeStore;
        }

        public SavedState(Parcelable parcelable) {
            super(parcelable);
            this.mVolumeStore = new VolumeStore();
        }
    }

    public class SeekBarVolumizer implements SeekBar.OnSeekBarChangeListener, Handler.Callback {
        private static final int CHECK_RINGTONE_PLAYBACK_DELAY_MS = 1000;
        private static final int MSG_SET_STREAM_VOLUME = 0;
        private static final int MSG_START_SAMPLE = 1;
        private static final int MSG_STOP_SAMPLE = 2;
        private AudioManager mAudioManager;
        private Context mContext;
        private Handler mHandler;
        private int mLastProgress;
        private int mOriginalStreamVolume;
        private Ringtone mRingtone;
        private SeekBar mSeekBar;
        private int mStreamType;
        private int mVolumeBeforeMute;
        private ContentObserver mVolumeObserver;

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public SeekBarVolumizer(VolumePreference volumePreference, Context context, SeekBar seekBar, int i) {
            this(context, seekBar, i, null);
        }

        public SeekBarVolumizer(Context context, SeekBar seekBar, int i, Uri uri) {
            this.mLastProgress = -1;
            this.mVolumeBeforeMute = -1;
            this.mVolumeObserver = new ContentObserver(this.mHandler) { // from class: android.preference.VolumePreference.SeekBarVolumizer.1
                @Override // android.database.ContentObserver
                public void onChange(boolean z) {
                    super.onChange(z);
                    if (SeekBarVolumizer.this.mSeekBar == null || SeekBarVolumizer.this.mAudioManager == null) {
                        return;
                    }
                    SeekBarVolumizer.this.mSeekBar.setProgress(SeekBarVolumizer.this.mAudioManager.getStreamVolume(SeekBarVolumizer.this.mStreamType));
                }
            };
            this.mContext = context;
            this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            this.mStreamType = i;
            this.mSeekBar = seekBar;
            HandlerThread handlerThread = new HandlerThread("VolumePreference.CallbackHandler");
            handlerThread.start();
            this.mHandler = new Handler(handlerThread.getLooper(), this);
            initSeekBar(seekBar, uri);
        }

        private void initSeekBar(SeekBar seekBar, Uri uri) {
            seekBar.setMax(this.mAudioManager.getStreamMaxVolume(this.mStreamType));
            int streamVolume = this.mAudioManager.getStreamVolume(this.mStreamType);
            this.mOriginalStreamVolume = streamVolume;
            seekBar.setProgress(streamVolume);
            seekBar.setOnSeekBarChangeListener(this);
            this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.VOLUME_SETTINGS[this.mStreamType]), false, this.mVolumeObserver);
            if (uri == null) {
                int i = this.mStreamType;
                if (i == 2) {
                    uri = Settings.System.DEFAULT_RINGTONE_URI;
                } else if (i == 5) {
                    uri = Settings.System.DEFAULT_NOTIFICATION_URI;
                } else {
                    uri = Settings.System.DEFAULT_ALARM_ALERT_URI;
                }
            }
            Ringtone ringtone = RingtoneManager.getRingtone(this.mContext, uri);
            this.mRingtone = ringtone;
            if (ringtone != null) {
                ringtone.setStreamType(this.mStreamType);
            }
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                this.mAudioManager.setStreamVolume(this.mStreamType, this.mLastProgress, 0);
            } else if (i == 1) {
                onStartSample();
            } else if (i == 2) {
                onStopSample();
            } else {
                Log.e(VolumePreference.TAG, "invalid SeekBarVolumizer message: " + message.what);
            }
            return true;
        }

        private void postStartSample() {
            this.mHandler.removeMessages(1);
            Handler handler = this.mHandler;
            handler.sendMessageDelayed(handler.obtainMessage(1), isSamplePlaying() ? 1000L : 0L);
        }

        private void onStartSample() {
            if (isSamplePlaying()) {
                return;
            }
            VolumePreference.this.onSampleStarting(this);
            Ringtone ringtone = this.mRingtone;
            if (ringtone != null) {
                ringtone.play();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void postStopSample() {
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(2);
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(2));
        }

        private void onStopSample() {
            Ringtone ringtone = this.mRingtone;
            if (ringtone != null) {
                ringtone.stop();
            }
        }

        public void stop() {
            postStopSample();
            this.mContext.getContentResolver().unregisterContentObserver(this.mVolumeObserver);
            this.mSeekBar.setOnSeekBarChangeListener(null);
        }

        public void revertVolume() {
            this.mAudioManager.setStreamVolume(this.mStreamType, this.mOriginalStreamVolume, 0);
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            if (z) {
                postSetVolume(i);
            }
        }

        void postSetVolume(int i) {
            this.mLastProgress = i;
            this.mHandler.removeMessages(0);
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(0));
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(SeekBar seekBar) {
            postStartSample();
        }

        public boolean isSamplePlaying() {
            Ringtone ringtone = this.mRingtone;
            return ringtone != null && ringtone.isPlaying();
        }

        public void startSample() {
            postStartSample();
        }

        public void stopSample() {
            postStopSample();
        }

        public SeekBar getSeekBar() {
            return this.mSeekBar;
        }

        public void changeVolumeBy(int i) {
            this.mSeekBar.incrementProgressBy(i);
            postSetVolume(this.mSeekBar.getProgress());
            postStartSample();
            this.mVolumeBeforeMute = -1;
        }

        public void muteVolume() {
            int i = this.mVolumeBeforeMute;
            if (i != -1) {
                this.mSeekBar.setProgress(i);
                postSetVolume(this.mVolumeBeforeMute);
                postStartSample();
                this.mVolumeBeforeMute = -1;
                return;
            }
            this.mVolumeBeforeMute = this.mSeekBar.getProgress();
            this.mSeekBar.setProgress(0);
            postStopSample();
            postSetVolume(0);
        }

        public void onSaveInstanceState(VolumeStore volumeStore) {
            int i = this.mLastProgress;
            if (i >= 0) {
                volumeStore.volume = i;
                volumeStore.originalVolume = this.mOriginalStreamVolume;
            }
        }

        public void onRestoreInstanceState(VolumeStore volumeStore) {
            if (volumeStore.volume != -1) {
                this.mOriginalStreamVolume = volumeStore.originalVolume;
                int i = volumeStore.volume;
                this.mLastProgress = i;
                postSetVolume(i);
            }
        }
    }
}
