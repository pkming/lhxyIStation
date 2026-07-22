package android.view;

import android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.AudioService;
import android.media.AudioSystem;
import android.media.ToneGenerator;
import android.media.VolumeController;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class VolumePanel extends Handler implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, VolumeController {
    private static final int BEEP_DURATION = 150;
    private static final int FREE_DELAY = 10000;
    private static boolean LOGD = false;
    private static final int MAX_VOLUME = 100;
    private static final int MSG_DISPLAY_SAFE_VOLUME_WARNING = 11;
    private static final int MSG_FREE_RESOURCES = 1;
    private static final int MSG_MUTE_CHANGED = 7;
    private static final int MSG_PLAY_SOUND = 2;
    private static final int MSG_REMOTE_VOLUME_CHANGED = 8;
    private static final int MSG_REMOTE_VOLUME_UPDATE_IF_SHOWN = 9;
    private static final int MSG_RINGER_MODE_CHANGED = 6;
    private static final int MSG_SLIDER_VISIBILITY_CHANGED = 10;
    private static final int MSG_STOP_SOUNDS = 3;
    private static final int MSG_TIMEOUT = 5;
    private static final int MSG_VIBRATE = 4;
    private static final int MSG_VOLUME_CHANGED = 0;
    public static final int PLAY_SOUND_DELAY = 300;
    private static final int STREAM_MASTER = -100;
    private static final String TAG = "VolumePanel";
    private static final int TIMEOUT_DELAY = 3000;
    public static final int VIBRATE_DELAY = 300;
    private static final int VIBRATE_DURATION = 300;
    private static AlertDialog sConfirmSafeVolumeDialog;
    private int mActiveStreamType = -1;
    private AudioManager mAudioManager;
    protected AudioService mAudioService;
    protected Context mContext;
    private final Dialog mDialog;
    private final View mDivider;
    private final View mMoreButton;
    private final ViewGroup mPanel;
    private final boolean mPlayMasterStreamTones;
    private boolean mRingIsSilent;
    private boolean mShowCombinedVolumes;
    private final ViewGroup mSliderGroup;
    private HashMap<Integer, StreamControl> mStreamControls;
    private ToneGenerator[] mToneGenerators;
    private Vibrator mVibrator;
    private final View mView;
    private boolean mVoiceCapable;
    private static final StreamResources[] STREAMS = {StreamResources.BluetoothSCOStream, StreamResources.RingerStream, StreamResources.VoiceStream, StreamResources.MediaStream, StreamResources.FMStream, StreamResources.NotificationStream, StreamResources.AlarmStream, StreamResources.MasterStream, StreamResources.RemoteStream};
    private static Object sConfirmSafeVolumeLock = new Object();

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    private enum StreamResources {
        BluetoothSCOStream(6, 17040424, 17302174, 17302174, false),
        RingerStream(2, 17040425, 17302182, 17302184, false),
        VoiceStream(0, 17040426, 17302180, 17302180, false),
        AlarmStream(4, 17040421, 17302172, 17302173, false),
        MediaStream(3, 17040427, 17302188, 17302190, true),
        FMStream(10, 17040427, 17302188, 17302190, true),
        NotificationStream(5, 17040428, 17302176, 17302178, true),
        MasterStream(-100, 17040427, 17302188, 17302190, false),
        RemoteStream(-200, 17040427, 17302316, 17302304, false);

        int descRes;
        int iconMuteRes;
        int iconRes;
        boolean show;
        int streamType;

        StreamResources(int i, int i2, int i3, int i4, boolean z) {
            this.streamType = i;
            this.descRes = i2;
            this.iconRes = i3;
            this.iconMuteRes = i4;
            this.show = z;
        }
    }

    private class StreamControl {
        ViewGroup group;
        ImageView icon;
        int iconMuteRes;
        int iconRes;
        SeekBar seekbarView;
        int streamType;

        private StreamControl() {
        }
    }

    private static class WarningDialogReceiver extends BroadcastReceiver implements DialogInterface.OnDismissListener {
        private final Context mContext;
        private final Dialog mDialog;
        private final VolumePanel mVolumePanel;

        WarningDialogReceiver(Context context, Dialog dialog, VolumePanel volumePanel) {
            this.mContext = context;
            this.mDialog = dialog;
            this.mVolumePanel = volumePanel;
            context.registerReceiver(this, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) throws Throwable {
            this.mDialog.cancel();
            cleanUp();
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialogInterface) throws Throwable {
            this.mContext.unregisterReceiver(this);
            cleanUp();
        }

        private void cleanUp() throws Throwable {
            synchronized (VolumePanel.sConfirmSafeVolumeLock) {
                AlertDialog unused = VolumePanel.sConfirmSafeVolumeDialog = null;
            }
            this.mVolumePanel.updateStates();
            this.mVolumePanel.forceTimeout();
        }
    }

    public VolumePanel(Context context, AudioService audioService) {
        this.mContext = context;
        this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.mAudioService = audioService;
        boolean z = context.getResources().getBoolean(17891345);
        if (z) {
            int i = 0;
            while (true) {
                StreamResources[] streamResourcesArr = STREAMS;
                if (i >= streamResourcesArr.length) {
                    break;
                }
                StreamResources streamResources = streamResourcesArr[i];
                streamResources.show = streamResources.streamType == -100;
                i++;
            }
        }
        View viewInflate = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(17367225, (ViewGroup) null);
        this.mView = viewInflate;
        viewInflate.setOnTouchListener(new View.OnTouchListener() { // from class: android.view.VolumePanel.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                VolumePanel.this.resetTimeout();
                return false;
            }
        });
        this.mPanel = (ViewGroup) viewInflate.findViewById(16909155);
        this.mSliderGroup = (ViewGroup) viewInflate.findViewById(16909156);
        ImageView imageView = (ImageView) viewInflate.findViewById(16909158);
        this.mMoreButton = imageView;
        ImageView imageView2 = (ImageView) viewInflate.findViewById(16909157);
        this.mDivider = imageView2;
        Dialog dialog = new Dialog(context, 16974602) { // from class: android.view.VolumePanel.2
            @Override // android.app.Dialog
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (!isShowing() || motionEvent.getAction() != 4 || VolumePanel.sConfirmSafeVolumeDialog != null) {
                    return false;
                }
                VolumePanel.this.forceTimeout();
                return true;
            }
        };
        this.mDialog = dialog;
        dialog.setTitle("Volume control");
        dialog.setContentView(viewInflate);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: android.view.VolumePanel.3
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                VolumePanel.this.mActiveStreamType = -1;
                VolumePanel.this.mAudioManager.forceVolumeControlStream(VolumePanel.this.mActiveStreamType);
            }
        });
        Window window = dialog.getWindow();
        window.setGravity(48);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.token = null;
        attributes.y = this.mContext.getResources().getDimensionPixelOffset(17104984);
        attributes.type = 2020;
        attributes.width = -2;
        attributes.height = -2;
        attributes.privateFlags |= 32;
        window.setAttributes(attributes);
        window.addFlags(262184);
        this.mToneGenerators = new ToneGenerator[AudioSystem.getNumStreamTypes()];
        this.mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        boolean z2 = context.getResources().getBoolean(17891385);
        this.mVoiceCapable = z2;
        boolean z3 = (z2 || z) ? false : true;
        this.mShowCombinedVolumes = z3;
        if (!z3) {
            imageView.setVisibility(8);
            imageView2.setVisibility(8);
        } else {
            imageView.setOnClickListener(this);
        }
        this.mPlayMasterStreamTones = context.getResources().getBoolean(17891345) && this.mContext.getResources().getBoolean(17891346);
        listenToRingerMode();
    }

    public void setLayoutDirection(int i) {
        this.mPanel.setLayoutDirection(i);
        updateStates();
    }

    private void listenToRingerMode() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: android.view.VolumePanel.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if (AudioManager.RINGER_MODE_CHANGED_ACTION.equals(intent.getAction())) {
                    VolumePanel.this.removeMessages(6);
                    VolumePanel volumePanel = VolumePanel.this;
                    volumePanel.sendMessage(volumePanel.obtainMessage(6));
                }
            }
        }, intentFilter);
    }

    private boolean isMuted(int i) {
        if (i == -100) {
            return this.mAudioManager.isMasterMute();
        }
        if (i == -200) {
            return this.mAudioService.getRemoteStreamVolume() <= 0;
        }
        return this.mAudioManager.isStreamMute(i);
    }

    private int getStreamMaxVolume(int i) {
        if (i == -100) {
            return this.mAudioManager.getMasterMaxVolume();
        }
        if (i == -200) {
            return this.mAudioService.getRemoteStreamMaxVolume();
        }
        return this.mAudioManager.getStreamMaxVolume(i);
    }

    private int getStreamVolume(int i) {
        if (i == -100) {
            return this.mAudioManager.getMasterVolume();
        }
        if (i == -200) {
            return this.mAudioService.getRemoteStreamVolume();
        }
        return this.mAudioManager.getStreamVolume(i);
    }

    private void setStreamVolume(int i, int i2, int i3) {
        if (i == -100) {
            this.mAudioManager.setMasterVolume(i2, i3);
        } else if (i == -200) {
            this.mAudioService.setRemoteStreamVolume(i2);
        } else {
            this.mAudioManager.setStreamVolume(i, i2, i3);
        }
    }

    private void createSliders() throws Throwable {
        LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mStreamControls = new HashMap<>(STREAMS.length);
        Resources resources = this.mContext.getResources();
        int i = 0;
        while (true) {
            StreamResources[] streamResourcesArr = STREAMS;
            if (i >= streamResourcesArr.length) {
                return;
            }
            StreamResources streamResources = streamResourcesArr[i];
            int i2 = streamResources.streamType;
            if (this.mVoiceCapable && streamResources == StreamResources.NotificationStream) {
                streamResources = StreamResources.RingerStream;
            }
            StreamControl streamControl = new StreamControl();
            streamControl.streamType = i2;
            streamControl.group = (ViewGroup) layoutInflater.inflate(17367226, (ViewGroup) null);
            streamControl.group.setTag(streamControl);
            streamControl.icon = (ImageView) streamControl.group.findViewById(16909159);
            streamControl.icon.setTag(streamControl);
            streamControl.icon.setContentDescription(resources.getString(streamResources.descRes));
            streamControl.iconRes = streamResources.iconRes;
            streamControl.iconMuteRes = streamResources.iconMuteRes;
            streamControl.icon.setImageResource(streamControl.iconRes);
            streamControl.seekbarView = (SeekBar) streamControl.group.findViewById(16909056);
            streamControl.seekbarView.setMax(getStreamMaxVolume(i2) + ((i2 == 6 || i2 == 0) ? 1 : 0));
            streamControl.seekbarView.setOnSeekBarChangeListener(this);
            streamControl.seekbarView.setTag(streamControl);
            this.mStreamControls.put(Integer.valueOf(i2), streamControl);
            i++;
        }
    }

    private void reorderSliders(int i) throws Throwable {
        this.mSliderGroup.removeAllViews();
        StreamControl streamControl = this.mStreamControls.get(Integer.valueOf(i));
        if (streamControl == null) {
            Log.e(TAG, "Missing stream type! - " + i);
            this.mActiveStreamType = -1;
        } else {
            this.mSliderGroup.addView(streamControl.group);
            this.mActiveStreamType = i;
            streamControl.group.setVisibility(0);
            updateSlider(streamControl);
        }
        addOtherVolumes();
    }

    private void addOtherVolumes() throws Throwable {
        if (!this.mShowCombinedVolumes) {
            return;
        }
        int i = 0;
        while (true) {
            StreamResources[] streamResourcesArr = STREAMS;
            if (i >= streamResourcesArr.length) {
                return;
            }
            int i2 = streamResourcesArr[i].streamType;
            if (streamResourcesArr[i].show && i2 != this.mActiveStreamType) {
                StreamControl streamControl = this.mStreamControls.get(Integer.valueOf(i2));
                this.mSliderGroup.addView(streamControl.group);
                updateSlider(streamControl);
            }
            i++;
        }
    }

    private void updateSlider(StreamControl streamControl) throws Throwable {
        streamControl.seekbarView.setProgress(getStreamVolume(streamControl.streamType));
        boolean zIsMuted = isMuted(streamControl.streamType);
        streamControl.icon.setImageDrawable(null);
        streamControl.icon.setImageResource(zIsMuted ? streamControl.iconMuteRes : streamControl.iconRes);
        if ((streamControl.streamType == 2 || streamControl.streamType == 5) && this.mAudioManager.getRingerMode() == 1) {
            streamControl.icon.setImageResource(17302186);
        }
        if (streamControl.streamType == -200) {
            streamControl.seekbarView.setEnabled(true);
        } else if ((streamControl.streamType != this.mAudioManager.getMasterStreamType() && zIsMuted) || sConfirmSafeVolumeDialog != null) {
            streamControl.seekbarView.setEnabled(false);
        } else {
            streamControl.seekbarView.setEnabled(true);
        }
    }

    private boolean isExpanded() {
        return this.mMoreButton.getVisibility() != 0;
    }

    private void expand() {
        int childCount = this.mSliderGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            this.mSliderGroup.getChildAt(i).setVisibility(0);
        }
        this.mMoreButton.setVisibility(4);
        this.mDivider.setVisibility(4);
    }

    private void collapse() {
        this.mMoreButton.setVisibility(0);
        this.mDivider.setVisibility(0);
        int childCount = this.mSliderGroup.getChildCount();
        for (int i = 1; i < childCount; i++) {
            this.mSliderGroup.getChildAt(i).setVisibility(8);
        }
    }

    public void updateStates() throws Throwable {
        int childCount = this.mSliderGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            updateSlider((StreamControl) this.mSliderGroup.getChildAt(i).getTag());
        }
    }

    public void postVolumeChanged(int i, int i2) {
        if (hasMessages(0)) {
            removeMessages(0);
        }
        synchronized (this) {
            if (this.mStreamControls == null) {
                createSliders();
            }
        }
        removeMessages(1);
        obtainMessage(0, i, i2).sendToTarget();
    }

    @Override // android.media.VolumeController
    public void postRemoteVolumeChanged(int i, int i2) {
        if (hasMessages(8)) {
            return;
        }
        synchronized (this) {
            if (this.mStreamControls == null) {
                createSliders();
            }
        }
        removeMessages(1);
        obtainMessage(8, i, i2).sendToTarget();
    }

    @Override // android.media.VolumeController
    public void postRemoteSliderVisibility(boolean z) {
        obtainMessage(10, -200, z ? 1 : 0).sendToTarget();
    }

    @Override // android.media.VolumeController
    public void postHasNewRemotePlaybackInfo() {
        if (hasMessages(9)) {
            return;
        }
        obtainMessage(9).sendToTarget();
    }

    public void postMasterVolumeChanged(int i) {
        postVolumeChanged(-100, i);
    }

    public void postMuteChanged(int i, int i2) {
        if (hasMessages(0)) {
            return;
        }
        synchronized (this) {
            if (this.mStreamControls == null) {
                createSliders();
            }
        }
        removeMessages(1);
        obtainMessage(7, i, i2).sendToTarget();
    }

    public void postMasterMuteChanged(int i) {
        postMuteChanged(-100, i);
    }

    public void postDisplaySafeVolumeWarning(int i) {
        if (hasMessages(11)) {
            return;
        }
        obtainMessage(11, i, 0).sendToTarget();
    }

    protected void onVolumeChanged(int i, int i2) {
        if (LOGD) {
            Log.d(TAG, "onVolumeChanged(streamType: " + i + ", flags: " + i2 + ")");
        }
        if ((i2 & 1) != 0) {
            synchronized (this) {
                if (this.mActiveStreamType != i) {
                    reorderSliders(i);
                }
                onShowVolumeChanged(i, i2);
            }
        }
        if ((i2 & 4) != 0 && !this.mRingIsSilent) {
            removeMessages(2);
            sendMessageDelayed(obtainMessage(2, i, i2), 300L);
        }
        if ((i2 & 8) != 0) {
            removeMessages(2);
            removeMessages(4);
            onStopSounds();
        }
        removeMessages(1);
        sendMessageDelayed(obtainMessage(1), 10000L);
        resetTimeout();
    }

    protected void onMuteChanged(int i, int i2) throws Throwable {
        if (LOGD) {
            Log.d(TAG, "onMuteChanged(streamType: " + i + ", flags: " + i2 + ")");
        }
        StreamControl streamControl = this.mStreamControls.get(Integer.valueOf(i));
        if (streamControl != null) {
            streamControl.icon.setImageResource(isMuted(streamControl.streamType) ? streamControl.iconMuteRes : streamControl.iconRes);
        }
        onVolumeChanged(i, i2);
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x0084  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onShowVolumeChanged(int r9, int r10) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 312
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.VolumePanel.onShowVolumeChanged(int, int):void");
    }

    protected void onPlaySound(int i, int i2) {
        if (hasMessages(3)) {
            removeMessages(3);
            onStopSounds();
        }
        synchronized (this) {
            ToneGenerator orCreateToneGenerator = getOrCreateToneGenerator(i);
            if (orCreateToneGenerator != null) {
                orCreateToneGenerator.startTone(24);
                sendMessageDelayed(obtainMessage(3), 150L);
            }
        }
    }

    protected void onStopSounds() {
        synchronized (this) {
            for (int numStreamTypes = AudioSystem.getNumStreamTypes() - 1; numStreamTypes >= 0; numStreamTypes--) {
                ToneGenerator toneGenerator = this.mToneGenerators[numStreamTypes];
                if (toneGenerator != null) {
                    toneGenerator.stopTone();
                }
            }
        }
    }

    protected void onVibrate() {
        if (this.mAudioManager.getRingerMode() != 1) {
            return;
        }
        this.mVibrator.vibrate(300L);
    }

    protected void onRemoteVolumeChanged(int i, int i2) {
        if (LOGD) {
            Log.d(TAG, "onRemoteVolumeChanged(stream:" + i + ", flags: " + i2 + ")");
        }
        if ((i2 & 1) != 0 || this.mDialog.isShowing()) {
            synchronized (this) {
                if (this.mActiveStreamType != -200) {
                    reorderSliders(-200);
                }
                onShowVolumeChanged(-200, i2);
            }
        } else if (LOGD) {
            Log.d(TAG, "not calling onShowVolumeChanged(), no FLAG_SHOW_UI or no UI");
        }
        if ((i2 & 4) != 0 && !this.mRingIsSilent) {
            removeMessages(2);
            sendMessageDelayed(obtainMessage(2, i, i2), 300L);
        }
        if ((i2 & 8) != 0) {
            removeMessages(2);
            removeMessages(4);
            onStopSounds();
        }
        removeMessages(1);
        sendMessageDelayed(obtainMessage(1), 10000L);
        resetTimeout();
    }

    protected void onRemoteVolumeUpdateIfShown() throws Throwable {
        if (LOGD) {
            Log.d(TAG, "onRemoteVolumeUpdateIfShown()");
        }
        if (this.mDialog.isShowing() && this.mActiveStreamType == -200 && this.mStreamControls != null) {
            onShowVolumeChanged(-200, 0);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x0042, code lost:
    
        r0.show = r5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x0044, code lost:
    
        if (r5 != false) goto L21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0048, code lost:
    
        if (r3.mActiveStreamType != r4) goto L21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x004a, code lost:
    
        r3.mActiveStreamType = -1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected synchronized void onSliderVisibilityChanged(int r4, int r5) {
        /*
            r3 = this;
            monitor-enter(r3)
            boolean r0 = android.view.VolumePanel.LOGD     // Catch: java.lang.Throwable -> L53
            if (r0 == 0) goto L2e
            java.lang.String r0 = "VolumePanel"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L53
            r1.<init>()     // Catch: java.lang.Throwable -> L53
            java.lang.String r2 = "onSliderVisibilityChanged(stream="
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Throwable -> L53
            java.lang.StringBuilder r1 = r1.append(r4)     // Catch: java.lang.Throwable -> L53
            java.lang.String r2 = ", visi="
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Throwable -> L53
            java.lang.StringBuilder r1 = r1.append(r5)     // Catch: java.lang.Throwable -> L53
            java.lang.String r2 = ")"
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Throwable -> L53
            java.lang.String r1 = r1.toString()     // Catch: java.lang.Throwable -> L53
            android.util.Log.d(r0, r1)     // Catch: java.lang.Throwable -> L53
        L2e:
            r0 = 1
            if (r5 != r0) goto L33
            r5 = r0
            goto L34
        L33:
            r5 = 0
        L34:
            android.view.VolumePanel$StreamResources[] r1 = android.view.VolumePanel.STREAMS     // Catch: java.lang.Throwable -> L53
            int r1 = r1.length     // Catch: java.lang.Throwable -> L53
            int r1 = r1 - r0
        L38:
            if (r1 < 0) goto L51
            android.view.VolumePanel$StreamResources[] r0 = android.view.VolumePanel.STREAMS     // Catch: java.lang.Throwable -> L53
            r0 = r0[r1]     // Catch: java.lang.Throwable -> L53
            int r2 = r0.streamType     // Catch: java.lang.Throwable -> L53
            if (r2 != r4) goto L4e
            r0.show = r5     // Catch: java.lang.Throwable -> L53
            if (r5 != 0) goto L51
            int r5 = r3.mActiveStreamType     // Catch: java.lang.Throwable -> L53
            if (r5 != r4) goto L51
            r4 = -1
            r3.mActiveStreamType = r4     // Catch: java.lang.Throwable -> L53
            goto L51
        L4e:
            int r1 = r1 + (-1)
            goto L38
        L51:
            monitor-exit(r3)
            return
        L53:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.VolumePanel.onSliderVisibilityChanged(int, int):void");
    }

    protected void onDisplaySafeVolumeWarning(int i) throws Throwable {
        if ((i & 1) != 0 || this.mDialog.isShowing()) {
            synchronized (sConfirmSafeVolumeLock) {
                if (sConfirmSafeVolumeDialog != null) {
                    return;
                }
                sConfirmSafeVolumeDialog = new AlertDialog.Builder(this.mContext).setMessage(17040768).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // from class: android.view.VolumePanel.6
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i2) {
                        VolumePanel.this.mAudioService.disableSafeMediaVolume();
                    }
                }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // from class: android.view.VolumePanel.5
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i2) {
                        Intent intent = new Intent();
                        intent.setAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
                        VolumePanel.this.mContext.sendBroadcastAsUser(intent, new UserHandle(-2));
                    }
                }).setIconAttribute(R.attr.alertDialogIcon).create();
                sConfirmSafeVolumeDialog.setOnDismissListener(new WarningDialogReceiver(this.mContext, sConfirmSafeVolumeDialog, this));
                sConfirmSafeVolumeDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
                sConfirmSafeVolumeDialog.show();
                updateStates();
            }
        }
        resetTimeout();
    }

    private ToneGenerator getOrCreateToneGenerator(int i) {
        ToneGenerator toneGenerator;
        if (i == -100) {
            if (!this.mPlayMasterStreamTones) {
                return null;
            }
            i = 1;
        }
        synchronized (this) {
            ToneGenerator[] toneGeneratorArr = this.mToneGenerators;
            if (toneGeneratorArr[i] == null) {
                try {
                    toneGeneratorArr[i] = new ToneGenerator(i, 100);
                } catch (RuntimeException e) {
                    if (LOGD) {
                        Log.d(TAG, "ToneGenerator constructor failed with RuntimeException: " + e);
                    }
                }
                toneGenerator = this.mToneGenerators[i];
            } else {
                toneGenerator = this.mToneGenerators[i];
            }
        }
        return toneGenerator;
    }

    private void setMusicIcon(int i, int i2) throws Throwable {
        StreamControl streamControl = this.mStreamControls.get(3);
        if (streamControl != null) {
            streamControl.iconRes = i;
            streamControl.iconMuteRes = i2;
            streamControl.icon.setImageResource(isMuted(streamControl.streamType) ? streamControl.iconMuteRes : streamControl.iconRes);
        }
    }

    protected void onFreeResources() {
        synchronized (this) {
            for (int length = this.mToneGenerators.length - 1; length >= 0; length--) {
                ToneGenerator[] toneGeneratorArr = this.mToneGenerators;
                if (toneGeneratorArr[length] != null) {
                    toneGeneratorArr[length].release();
                }
                this.mToneGenerators[length] = null;
            }
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) throws Throwable {
        switch (message.what) {
            case 0:
                onVolumeChanged(message.arg1, message.arg2);
                return;
            case 1:
                onFreeResources();
                return;
            case 2:
                onPlaySound(message.arg1, message.arg2);
                return;
            case 3:
                onStopSounds();
                return;
            case 4:
                onVibrate();
                return;
            case 5:
                if (this.mDialog.isShowing()) {
                    this.mDialog.dismiss();
                    this.mActiveStreamType = -1;
                }
                synchronized (sConfirmSafeVolumeLock) {
                    AlertDialog alertDialog = sConfirmSafeVolumeDialog;
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }
                    break;
                }
                return;
            case 6:
                if (this.mDialog.isShowing()) {
                    updateStates();
                    return;
                }
                return;
            case 7:
                onMuteChanged(message.arg1, message.arg2);
                return;
            case 8:
                onRemoteVolumeChanged(message.arg1, message.arg2);
                return;
            case 9:
                onRemoteVolumeUpdateIfShown();
                return;
            case 10:
                onSliderVisibilityChanged(message.arg1, message.arg2);
                return;
            case 11:
                onDisplaySafeVolumeWarning(message.arg1);
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetTimeout() {
        removeMessages(5);
        sendMessageDelayed(obtainMessage(5), 3000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void forceTimeout() {
        removeMessages(5);
        sendMessage(obtainMessage(5));
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        Object tag = seekBar.getTag();
        if (z && (tag instanceof StreamControl)) {
            StreamControl streamControl = (StreamControl) tag;
            if (getStreamVolume(streamControl.streamType) != i) {
                setStreamVolume(streamControl.streamType, i, 0);
            }
        }
        resetTimeout();
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
        Object tag = seekBar.getTag();
        if ((tag instanceof StreamControl) && ((StreamControl) tag).streamType == -200) {
            seekBar.setProgress(getStreamVolume(-200));
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view == this.mMoreButton) {
            expand();
        }
        resetTimeout();
    }
}
