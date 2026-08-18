package android.media;

import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.media.IAudioFocusDispatcher;
import android.media.IAudioRoutesObserver;
import android.media.IRemoteControlClient;
import android.media.IRemoteControlDisplay;
import android.media.IRemoteVolumeObserver;
import android.media.IRingtonePlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.KeyEvent;

/* JADX INFO: loaded from: classes.dex */
public interface IAudioService extends IInterface {
    int abandonAudioFocus(IAudioFocusDispatcher iAudioFocusDispatcher, String str) throws RemoteException;

    void adjustLocalOrRemoteStreamVolume(int i, int i2, String str) throws RemoteException;

    void adjustMasterVolume(int i, int i2, String str) throws RemoteException;

    void adjustStreamVolume(int i, int i2, int i3, String str) throws RemoteException;

    void adjustSuggestedStreamVolume(int i, int i2, int i3, String str) throws RemoteException;

    void adjustVolume(int i, int i2, String str) throws RemoteException;

    void avrcpSupportsAbsoluteVolume(String str, boolean z) throws RemoteException;

    void dispatchMediaKeyEvent(KeyEvent keyEvent) throws RemoteException;

    void dispatchMediaKeyEventUnderWakelock(KeyEvent keyEvent) throws RemoteException;

    void forceVolumeControlStream(int i, IBinder iBinder) throws RemoteException;

    int getCurrentAudioFocus() throws RemoteException;

    int getLastAudibleMasterVolume() throws RemoteException;

    int getLastAudibleStreamVolume(int i) throws RemoteException;

    int getMasterMaxVolume() throws RemoteException;

    int getMasterStreamType() throws RemoteException;

    int getMasterVolume() throws RemoteException;

    int getMode() throws RemoteException;

    int getRemoteStreamMaxVolume() throws RemoteException;

    int getRemoteStreamVolume() throws RemoteException;

    int getRingerMode() throws RemoteException;

    IRingtonePlayer getRingtonePlayer() throws RemoteException;

    int getStreamMaxVolume(int i) throws RemoteException;

    int getStreamVolume(int i) throws RemoteException;

    int getVibrateSetting(int i) throws RemoteException;

    boolean isBluetoothA2dpOn() throws RemoteException;

    boolean isBluetoothScoOn() throws RemoteException;

    boolean isCameraSoundForced() throws RemoteException;

    boolean isLocalOrRemoteMusicActive() throws RemoteException;

    boolean isMasterMute() throws RemoteException;

    boolean isSpeakerphoneOn() throws RemoteException;

    boolean isStreamMute(int i) throws RemoteException;

    boolean loadSoundEffects() throws RemoteException;

    void playSoundEffect(int i) throws RemoteException;

    void playSoundEffectVolume(int i, float f) throws RemoteException;

    void registerMediaButtonEventReceiverForCalls(ComponentName componentName) throws RemoteException;

    void registerMediaButtonIntent(PendingIntent pendingIntent, ComponentName componentName, IBinder iBinder) throws RemoteException;

    int registerRemoteControlClient(PendingIntent pendingIntent, IRemoteControlClient iRemoteControlClient, String str) throws RemoteException;

    boolean registerRemoteControlDisplay(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2) throws RemoteException;

    boolean registerRemoteController(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2, ComponentName componentName) throws RemoteException;

    void registerRemoteVolumeObserverForRcc(int i, IRemoteVolumeObserver iRemoteVolumeObserver) throws RemoteException;

    void reloadAudioSettings() throws RemoteException;

    void remoteControlDisplayUsesBitmapSize(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2) throws RemoteException;

    void remoteControlDisplayWantsPlaybackPositionSync(IRemoteControlDisplay iRemoteControlDisplay, boolean z) throws RemoteException;

    int requestAudioFocus(int i, int i2, IBinder iBinder, IAudioFocusDispatcher iAudioFocusDispatcher, String str, String str2) throws RemoteException;

    int setBluetoothA2dpDeviceConnectionState(BluetoothDevice bluetoothDevice, int i, int i2) throws RemoteException;

    void setBluetoothA2dpOn(boolean z) throws RemoteException;

    void setBluetoothScoOn(boolean z) throws RemoteException;

    void setMasterMute(boolean z, int i, IBinder iBinder) throws RemoteException;

    void setMasterVolume(int i, int i2, String str) throws RemoteException;

    void setMode(int i, IBinder iBinder) throws RemoteException;

    void setPlaybackInfoForRcc(int i, int i2, int i3) throws RemoteException;

    void setPlaybackStateForRcc(int i, int i2, long j, float f) throws RemoteException;

    void setRemoteControlClientPlaybackPosition(int i, long j) throws RemoteException;

    void setRemoteStreamVolume(int i) throws RemoteException;

    void setRingerMode(int i) throws RemoteException;

    void setRingtonePlayer(IRingtonePlayer iRingtonePlayer) throws RemoteException;

    void setSpeakerphoneOn(boolean z) throws RemoteException;

    void setStreamMute(int i, boolean z, IBinder iBinder) throws RemoteException;

    void setStreamSolo(int i, boolean z, IBinder iBinder) throws RemoteException;

    void setStreamVolume(int i, int i2, int i3, String str) throws RemoteException;

    void setVibrateSetting(int i, int i2) throws RemoteException;

    void setWiredDeviceConnectionState(int i, int i2, String str) throws RemoteException;

    boolean shouldVibrate(int i) throws RemoteException;

    void startBluetoothSco(IBinder iBinder, int i) throws RemoteException;

    AudioRoutesInfo startWatchingRoutes(IAudioRoutesObserver iAudioRoutesObserver) throws RemoteException;

    void stopBluetoothSco(IBinder iBinder) throws RemoteException;

    void unloadSoundEffects() throws RemoteException;

    void unregisterAudioFocusClient(String str) throws RemoteException;

    void unregisterMediaButtonEventReceiverForCalls() throws RemoteException;

    void unregisterMediaButtonIntent(PendingIntent pendingIntent) throws RemoteException;

    void unregisterRemoteControlClient(PendingIntent pendingIntent, IRemoteControlClient iRemoteControlClient) throws RemoteException;

    void unregisterRemoteControlDisplay(IRemoteControlDisplay iRemoteControlDisplay) throws RemoteException;

    void updateRemoteControlClientMetadata(int i, int i2, Rating rating) throws RemoteException;

    public static abstract class Stub extends Binder implements IAudioService {
        private static final String DESCRIPTOR = "android.media.IAudioService";
        static final int TRANSACTION_abandonAudioFocus = 41;
        static final int TRANSACTION_adjustLocalOrRemoteStreamVolume = 3;
        static final int TRANSACTION_adjustMasterVolume = 6;
        static final int TRANSACTION_adjustStreamVolume = 5;
        static final int TRANSACTION_adjustSuggestedStreamVolume = 4;
        static final int TRANSACTION_adjustVolume = 1;
        static final int TRANSACTION_avrcpSupportsAbsoluteVolume = 33;
        static final int TRANSACTION_dispatchMediaKeyEvent = 44;
        static final int TRANSACTION_dispatchMediaKeyEventUnderWakelock = 45;
        static final int TRANSACTION_forceVolumeControlStream = 66;
        static final int TRANSACTION_getCurrentAudioFocus = 43;
        static final int TRANSACTION_getLastAudibleMasterVolume = 20;
        static final int TRANSACTION_getLastAudibleStreamVolume = 19;
        static final int TRANSACTION_getMasterMaxVolume = 18;
        static final int TRANSACTION_getMasterStreamType = 69;
        static final int TRANSACTION_getMasterVolume = 16;
        static final int TRANSACTION_getMode = 27;
        static final int TRANSACTION_getRemoteStreamMaxVolume = 61;
        static final int TRANSACTION_getRemoteStreamVolume = 62;
        static final int TRANSACTION_getRingerMode = 22;
        static final int TRANSACTION_getRingtonePlayer = 68;
        static final int TRANSACTION_getStreamMaxVolume = 17;
        static final int TRANSACTION_getStreamVolume = 15;
        static final int TRANSACTION_getVibrateSetting = 24;
        static final int TRANSACTION_isBluetoothA2dpOn = 39;
        static final int TRANSACTION_isBluetoothScoOn = 37;
        static final int TRANSACTION_isCameraSoundForced = 73;
        static final int TRANSACTION_isLocalOrRemoteMusicActive = 2;
        static final int TRANSACTION_isMasterMute = 14;
        static final int TRANSACTION_isSpeakerphoneOn = 35;
        static final int TRANSACTION_isStreamMute = 12;
        static final int TRANSACTION_loadSoundEffects = 30;
        static final int TRANSACTION_playSoundEffect = 28;
        static final int TRANSACTION_playSoundEffectVolume = 29;
        static final int TRANSACTION_registerMediaButtonEventReceiverForCalls = 48;
        static final int TRANSACTION_registerMediaButtonIntent = 46;
        static final int TRANSACTION_registerRemoteControlClient = 57;
        static final int TRANSACTION_registerRemoteControlDisplay = 50;
        static final int TRANSACTION_registerRemoteController = 51;
        static final int TRANSACTION_registerRemoteVolumeObserverForRcc = 63;
        static final int TRANSACTION_reloadAudioSettings = 32;
        static final int TRANSACTION_remoteControlDisplayUsesBitmapSize = 53;
        static final int TRANSACTION_remoteControlDisplayWantsPlaybackPositionSync = 54;
        static final int TRANSACTION_requestAudioFocus = 40;
        static final int TRANSACTION_setBluetoothA2dpDeviceConnectionState = 71;
        static final int TRANSACTION_setBluetoothA2dpOn = 38;
        static final int TRANSACTION_setBluetoothScoOn = 36;
        static final int TRANSACTION_setMasterMute = 13;
        static final int TRANSACTION_setMasterVolume = 9;
        static final int TRANSACTION_setMode = 26;
        static final int TRANSACTION_setPlaybackInfoForRcc = 59;
        static final int TRANSACTION_setPlaybackStateForRcc = 60;
        static final int TRANSACTION_setRemoteControlClientPlaybackPosition = 55;
        static final int TRANSACTION_setRemoteStreamVolume = 8;
        static final int TRANSACTION_setRingerMode = 21;
        static final int TRANSACTION_setRingtonePlayer = 67;
        static final int TRANSACTION_setSpeakerphoneOn = 34;
        static final int TRANSACTION_setStreamMute = 11;
        static final int TRANSACTION_setStreamSolo = 10;
        static final int TRANSACTION_setStreamVolume = 7;
        static final int TRANSACTION_setVibrateSetting = 23;
        static final int TRANSACTION_setWiredDeviceConnectionState = 70;
        static final int TRANSACTION_shouldVibrate = 25;
        static final int TRANSACTION_startBluetoothSco = 64;
        static final int TRANSACTION_startWatchingRoutes = 72;
        static final int TRANSACTION_stopBluetoothSco = 65;
        static final int TRANSACTION_unloadSoundEffects = 31;
        static final int TRANSACTION_unregisterAudioFocusClient = 42;
        static final int TRANSACTION_unregisterMediaButtonEventReceiverForCalls = 49;
        static final int TRANSACTION_unregisterMediaButtonIntent = 47;
        static final int TRANSACTION_unregisterRemoteControlClient = 58;
        static final int TRANSACTION_unregisterRemoteControlDisplay = 52;
        static final int TRANSACTION_updateRemoteControlClientMetadata = 56;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAudioService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IAudioService)) {
                return (IAudioService) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    adjustVolume(parcel.readInt(), parcel.readInt(), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsLocalOrRemoteMusicActive = isLocalOrRemoteMusicActive();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsLocalOrRemoteMusicActive ? 1 : 0);
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    adjustLocalOrRemoteStreamVolume(parcel.readInt(), parcel.readInt(), parcel.readString());
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    adjustSuggestedStreamVolume(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    adjustStreamVolume(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    adjustMasterVolume(parcel.readInt(), parcel.readInt(), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    setStreamVolume(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    setRemoteStreamVolume(parcel.readInt());
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    setMasterVolume(parcel.readInt(), parcel.readInt(), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    setStreamSolo(parcel.readInt(), parcel.readInt() != 0, parcel.readStrongBinder());
                    parcel2.writeNoException();
                    return true;
                case 11:
                    parcel.enforceInterface(DESCRIPTOR);
                    setStreamMute(parcel.readInt(), parcel.readInt() != 0, parcel.readStrongBinder());
                    parcel2.writeNoException();
                    return true;
                case 12:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsStreamMute = isStreamMute(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsStreamMute ? 1 : 0);
                    return true;
                case 13:
                    parcel.enforceInterface(DESCRIPTOR);
                    setMasterMute(parcel.readInt() != 0, parcel.readInt(), parcel.readStrongBinder());
                    parcel2.writeNoException();
                    return true;
                case 14:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsMasterMute = isMasterMute();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsMasterMute ? 1 : 0);
                    return true;
                case 15:
                    parcel.enforceInterface(DESCRIPTOR);
                    int streamVolume = getStreamVolume(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(streamVolume);
                    return true;
                case 16:
                    parcel.enforceInterface(DESCRIPTOR);
                    int masterVolume = getMasterVolume();
                    parcel2.writeNoException();
                    parcel2.writeInt(masterVolume);
                    return true;
                case 17:
                    parcel.enforceInterface(DESCRIPTOR);
                    int streamMaxVolume = getStreamMaxVolume(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(streamMaxVolume);
                    return true;
                case 18:
                    parcel.enforceInterface(DESCRIPTOR);
                    int masterMaxVolume = getMasterMaxVolume();
                    parcel2.writeNoException();
                    parcel2.writeInt(masterMaxVolume);
                    return true;
                case 19:
                    parcel.enforceInterface(DESCRIPTOR);
                    int lastAudibleStreamVolume = getLastAudibleStreamVolume(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(lastAudibleStreamVolume);
                    return true;
                case 20:
                    parcel.enforceInterface(DESCRIPTOR);
                    int lastAudibleMasterVolume = getLastAudibleMasterVolume();
                    parcel2.writeNoException();
                    parcel2.writeInt(lastAudibleMasterVolume);
                    return true;
                case 21:
                    parcel.enforceInterface(DESCRIPTOR);
                    setRingerMode(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 22:
                    parcel.enforceInterface(DESCRIPTOR);
                    int ringerMode = getRingerMode();
                    parcel2.writeNoException();
                    parcel2.writeInt(ringerMode);
                    return true;
                case 23:
                    parcel.enforceInterface(DESCRIPTOR);
                    setVibrateSetting(parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 24:
                    parcel.enforceInterface(DESCRIPTOR);
                    int vibrateSetting = getVibrateSetting(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(vibrateSetting);
                    return true;
                case 25:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zShouldVibrate = shouldVibrate(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zShouldVibrate ? 1 : 0);
                    return true;
                case 26:
                    parcel.enforceInterface(DESCRIPTOR);
                    setMode(parcel.readInt(), parcel.readStrongBinder());
                    parcel2.writeNoException();
                    return true;
                case 27:
                    parcel.enforceInterface(DESCRIPTOR);
                    int mode = getMode();
                    parcel2.writeNoException();
                    parcel2.writeInt(mode);
                    return true;
                case 28:
                    parcel.enforceInterface(DESCRIPTOR);
                    playSoundEffect(parcel.readInt());
                    return true;
                case 29:
                    parcel.enforceInterface(DESCRIPTOR);
                    playSoundEffectVolume(parcel.readInt(), parcel.readFloat());
                    return true;
                case 30:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zLoadSoundEffects = loadSoundEffects();
                    parcel2.writeNoException();
                    parcel2.writeInt(zLoadSoundEffects ? 1 : 0);
                    return true;
                case 31:
                    parcel.enforceInterface(DESCRIPTOR);
                    unloadSoundEffects();
                    return true;
                case 32:
                    parcel.enforceInterface(DESCRIPTOR);
                    reloadAudioSettings();
                    return true;
                case 33:
                    parcel.enforceInterface(DESCRIPTOR);
                    avrcpSupportsAbsoluteVolume(parcel.readString(), parcel.readInt() != 0);
                    return true;
                case 34:
                    parcel.enforceInterface(DESCRIPTOR);
                    setSpeakerphoneOn(parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 35:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsSpeakerphoneOn = isSpeakerphoneOn();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsSpeakerphoneOn ? 1 : 0);
                    return true;
                case 36:
                    parcel.enforceInterface(DESCRIPTOR);
                    setBluetoothScoOn(parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 37:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsBluetoothScoOn = isBluetoothScoOn();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsBluetoothScoOn ? 1 : 0);
                    return true;
                case 38:
                    parcel.enforceInterface(DESCRIPTOR);
                    setBluetoothA2dpOn(parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 39:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsBluetoothA2dpOn = isBluetoothA2dpOn();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsBluetoothA2dpOn ? 1 : 0);
                    return true;
                case 40:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iRequestAudioFocus = requestAudioFocus(parcel.readInt(), parcel.readInt(), parcel.readStrongBinder(), IAudioFocusDispatcher.Stub.asInterface(parcel.readStrongBinder()), parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(iRequestAudioFocus);
                    return true;
                case 41:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iAbandonAudioFocus = abandonAudioFocus(IAudioFocusDispatcher.Stub.asInterface(parcel.readStrongBinder()), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(iAbandonAudioFocus);
                    return true;
                case 42:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterAudioFocusClient(parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case 43:
                    parcel.enforceInterface(DESCRIPTOR);
                    int currentAudioFocus = getCurrentAudioFocus();
                    parcel2.writeNoException();
                    parcel2.writeInt(currentAudioFocus);
                    return true;
                case 44:
                    parcel.enforceInterface(DESCRIPTOR);
                    dispatchMediaKeyEvent(parcel.readInt() != 0 ? KeyEvent.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 45:
                    parcel.enforceInterface(DESCRIPTOR);
                    dispatchMediaKeyEventUnderWakelock(parcel.readInt() != 0 ? KeyEvent.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 46:
                    parcel.enforceInterface(DESCRIPTOR);
                    registerMediaButtonIntent(parcel.readInt() != 0 ? PendingIntent.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0 ? ComponentName.CREATOR.createFromParcel(parcel) : null, parcel.readStrongBinder());
                    parcel2.writeNoException();
                    return true;
                case 47:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterMediaButtonIntent(parcel.readInt() != 0 ? PendingIntent.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 48:
                    parcel.enforceInterface(DESCRIPTOR);
                    registerMediaButtonEventReceiverForCalls(parcel.readInt() != 0 ? ComponentName.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 49:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterMediaButtonEventReceiverForCalls();
                    return true;
                case 50:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zRegisterRemoteControlDisplay = registerRemoteControlDisplay(IRemoteControlDisplay.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zRegisterRemoteControlDisplay ? 1 : 0);
                    return true;
                case 51:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zRegisterRemoteController = registerRemoteController(IRemoteControlDisplay.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt(), parcel.readInt(), parcel.readInt() != 0 ? ComponentName.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeInt(zRegisterRemoteController ? 1 : 0);
                    return true;
                case 52:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterRemoteControlDisplay(IRemoteControlDisplay.Stub.asInterface(parcel.readStrongBinder()));
                    return true;
                case 53:
                    parcel.enforceInterface(DESCRIPTOR);
                    remoteControlDisplayUsesBitmapSize(IRemoteControlDisplay.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt(), parcel.readInt());
                    return true;
                case 54:
                    parcel.enforceInterface(DESCRIPTOR);
                    remoteControlDisplayWantsPlaybackPositionSync(IRemoteControlDisplay.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt() != 0);
                    return true;
                case 55:
                    parcel.enforceInterface(DESCRIPTOR);
                    setRemoteControlClientPlaybackPosition(parcel.readInt(), parcel.readLong());
                    parcel2.writeNoException();
                    return true;
                case 56:
                    parcel.enforceInterface(DESCRIPTOR);
                    updateRemoteControlClientMetadata(parcel.readInt(), parcel.readInt(), parcel.readInt() != 0 ? Rating.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 57:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iRegisterRemoteControlClient = registerRemoteControlClient(parcel.readInt() != 0 ? PendingIntent.CREATOR.createFromParcel(parcel) : null, IRemoteControlClient.Stub.asInterface(parcel.readStrongBinder()), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(iRegisterRemoteControlClient);
                    return true;
                case 58:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterRemoteControlClient(parcel.readInt() != 0 ? PendingIntent.CREATOR.createFromParcel(parcel) : null, IRemoteControlClient.Stub.asInterface(parcel.readStrongBinder()));
                    return true;
                case 59:
                    parcel.enforceInterface(DESCRIPTOR);
                    setPlaybackInfoForRcc(parcel.readInt(), parcel.readInt(), parcel.readInt());
                    return true;
                case 60:
                    parcel.enforceInterface(DESCRIPTOR);
                    setPlaybackStateForRcc(parcel.readInt(), parcel.readInt(), parcel.readLong(), parcel.readFloat());
                    parcel2.writeNoException();
                    return true;
                case 61:
                    parcel.enforceInterface(DESCRIPTOR);
                    int remoteStreamMaxVolume = getRemoteStreamMaxVolume();
                    parcel2.writeNoException();
                    parcel2.writeInt(remoteStreamMaxVolume);
                    return true;
                case 62:
                    parcel.enforceInterface(DESCRIPTOR);
                    int remoteStreamVolume = getRemoteStreamVolume();
                    parcel2.writeNoException();
                    parcel2.writeInt(remoteStreamVolume);
                    return true;
                case 63:
                    parcel.enforceInterface(DESCRIPTOR);
                    registerRemoteVolumeObserverForRcc(parcel.readInt(), IRemoteVolumeObserver.Stub.asInterface(parcel.readStrongBinder()));
                    return true;
                case 64:
                    parcel.enforceInterface(DESCRIPTOR);
                    startBluetoothSco(parcel.readStrongBinder(), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 65:
                    parcel.enforceInterface(DESCRIPTOR);
                    stopBluetoothSco(parcel.readStrongBinder());
                    parcel2.writeNoException();
                    return true;
                case 66:
                    parcel.enforceInterface(DESCRIPTOR);
                    forceVolumeControlStream(parcel.readInt(), parcel.readStrongBinder());
                    parcel2.writeNoException();
                    return true;
                case 67:
                    parcel.enforceInterface(DESCRIPTOR);
                    setRingtonePlayer(IRingtonePlayer.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 68:
                    parcel.enforceInterface(DESCRIPTOR);
                    IRingtonePlayer ringtonePlayer = getRingtonePlayer();
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(ringtonePlayer != null ? ringtonePlayer.asBinder() : null);
                    return true;
                case 69:
                    parcel.enforceInterface(DESCRIPTOR);
                    int masterStreamType = getMasterStreamType();
                    parcel2.writeNoException();
                    parcel2.writeInt(masterStreamType);
                    return true;
                case 70:
                    parcel.enforceInterface(DESCRIPTOR);
                    setWiredDeviceConnectionState(parcel.readInt(), parcel.readInt(), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case 71:
                    parcel.enforceInterface(DESCRIPTOR);
                    int bluetoothA2dpDeviceConnectionState = setBluetoothA2dpDeviceConnectionState(parcel.readInt() != 0 ? BluetoothDevice.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(bluetoothA2dpDeviceConnectionState);
                    return true;
                case 72:
                    parcel.enforceInterface(DESCRIPTOR);
                    AudioRoutesInfo audioRoutesInfoStartWatchingRoutes = startWatchingRoutes(IAudioRoutesObserver.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    if (audioRoutesInfoStartWatchingRoutes != null) {
                        parcel2.writeInt(1);
                        audioRoutesInfoStartWatchingRoutes.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 73:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsCameraSoundForced = isCameraSoundForced();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsCameraSoundForced ? 1 : 0);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        private static class Proxy implements IAudioService {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.media.IAudioService
            public void adjustVolume(int i, int i2, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isLocalOrRemoteMusicActive() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void adjustLocalOrRemoteStreamVolume(int i, int i2, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(3, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void adjustSuggestedStreamVolume(int i, int i2, int i3, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void adjustStreamVolume(int i, int i2, int i3, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(5, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void adjustMasterVolume(int i, int i2, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(6, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setStreamVolume(int i, int i2, int i3, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(7, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setRemoteStreamVolume(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(8, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setMasterVolume(int i, int i2, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(9, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setStreamSolo(int i, boolean z, IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(z ? 1 : 0);
                    parcelObtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(10, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setStreamMute(int i, boolean z, IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(z ? 1 : 0);
                    parcelObtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(11, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isStreamMute(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(12, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setMasterMute(boolean z, int i, IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(13, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isMasterMute() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getStreamVolume(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(15, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getMasterVolume() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(16, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getStreamMaxVolume(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(17, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getMasterMaxVolume() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(18, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getLastAudibleStreamVolume(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(19, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getLastAudibleMasterVolume() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(20, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setRingerMode(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(21, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getRingerMode() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(22, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setVibrateSetting(int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(23, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getVibrateSetting(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(24, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean shouldVibrate(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(25, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setMode(int i, IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(26, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getMode() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(27, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void playSoundEffect(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(28, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void playSoundEffectVolume(int i, float f) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeFloat(f);
                    this.mRemote.transact(29, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean loadSoundEffects() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(30, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unloadSoundEffects() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(31, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void reloadAudioSettings() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(32, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void avrcpSupportsAbsoluteVolume(String str, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(33, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setSpeakerphoneOn(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(34, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isSpeakerphoneOn() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(35, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setBluetoothScoOn(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(36, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isBluetoothScoOn() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(37, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setBluetoothA2dpOn(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(38, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isBluetoothA2dpOn() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(39, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int requestAudioFocus(int i, int i2, IBinder iBinder, IAudioFocusDispatcher iAudioFocusDispatcher, String str, String str2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeStrongBinder(iBinder);
                    parcelObtain.writeStrongBinder(iAudioFocusDispatcher != null ? iAudioFocusDispatcher.asBinder() : null);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    this.mRemote.transact(40, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int abandonAudioFocus(IAudioFocusDispatcher iAudioFocusDispatcher, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iAudioFocusDispatcher != null ? iAudioFocusDispatcher.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(41, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unregisterAudioFocusClient(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(42, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getCurrentAudioFocus() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(43, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void dispatchMediaKeyEvent(KeyEvent keyEvent) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (keyEvent != null) {
                        parcelObtain.writeInt(1);
                        keyEvent.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(44, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void dispatchMediaKeyEventUnderWakelock(KeyEvent keyEvent) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (keyEvent != null) {
                        parcelObtain.writeInt(1);
                        keyEvent.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(45, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void registerMediaButtonIntent(PendingIntent pendingIntent, ComponentName componentName, IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (pendingIntent != null) {
                        parcelObtain.writeInt(1);
                        pendingIntent.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (componentName != null) {
                        parcelObtain.writeInt(1);
                        componentName.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(46, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unregisterMediaButtonIntent(PendingIntent pendingIntent) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (pendingIntent != null) {
                        parcelObtain.writeInt(1);
                        pendingIntent.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(47, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void registerMediaButtonEventReceiverForCalls(ComponentName componentName) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (componentName != null) {
                        parcelObtain.writeInt(1);
                        componentName.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(48, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unregisterMediaButtonEventReceiverForCalls() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(49, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean registerRemoteControlDisplay(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iRemoteControlDisplay != null ? iRemoteControlDisplay.asBinder() : null);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(50, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean registerRemoteController(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2, ComponentName componentName) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iRemoteControlDisplay != null ? iRemoteControlDisplay.asBinder() : null);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    if (componentName != null) {
                        parcelObtain.writeInt(1);
                        componentName.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(51, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unregisterRemoteControlDisplay(IRemoteControlDisplay iRemoteControlDisplay) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iRemoteControlDisplay != null ? iRemoteControlDisplay.asBinder() : null);
                    this.mRemote.transact(52, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void remoteControlDisplayUsesBitmapSize(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iRemoteControlDisplay != null ? iRemoteControlDisplay.asBinder() : null);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(53, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void remoteControlDisplayWantsPlaybackPositionSync(IRemoteControlDisplay iRemoteControlDisplay, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iRemoteControlDisplay != null ? iRemoteControlDisplay.asBinder() : null);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(54, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setRemoteControlClientPlaybackPosition(int i, long j) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeLong(j);
                    this.mRemote.transact(55, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void updateRemoteControlClientMetadata(int i, int i2, Rating rating) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    if (rating != null) {
                        parcelObtain.writeInt(1);
                        rating.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(56, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int registerRemoteControlClient(PendingIntent pendingIntent, IRemoteControlClient iRemoteControlClient, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (pendingIntent != null) {
                        parcelObtain.writeInt(1);
                        pendingIntent.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(iRemoteControlClient != null ? iRemoteControlClient.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(57, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unregisterRemoteControlClient(PendingIntent pendingIntent, IRemoteControlClient iRemoteControlClient) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (pendingIntent != null) {
                        parcelObtain.writeInt(1);
                        pendingIntent.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(iRemoteControlClient != null ? iRemoteControlClient.asBinder() : null);
                    this.mRemote.transact(58, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setPlaybackInfoForRcc(int i, int i2, int i3) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    this.mRemote.transact(59, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setPlaybackStateForRcc(int i, int i2, long j, float f) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeLong(j);
                    parcelObtain.writeFloat(f);
                    this.mRemote.transact(60, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getRemoteStreamMaxVolume() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(61, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getRemoteStreamVolume() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(62, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void registerRemoteVolumeObserverForRcc(int i, IRemoteVolumeObserver iRemoteVolumeObserver) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeStrongBinder(iRemoteVolumeObserver != null ? iRemoteVolumeObserver.asBinder() : null);
                    this.mRemote.transact(63, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void startBluetoothSco(IBinder iBinder, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(64, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void stopBluetoothSco(IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(65, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void forceVolumeControlStream(int i, IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(66, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setRingtonePlayer(IRingtonePlayer iRingtonePlayer) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iRingtonePlayer != null ? iRingtonePlayer.asBinder() : null);
                    this.mRemote.transact(67, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public IRingtonePlayer getRingtonePlayer() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(68, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return IRingtonePlayer.Stub.asInterface(parcelObtain2.readStrongBinder());
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getMasterStreamType() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(69, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setWiredDeviceConnectionState(int i, int i2, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(70, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int setBluetoothA2dpDeviceConnectionState(BluetoothDevice bluetoothDevice, int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bluetoothDevice != null) {
                        parcelObtain.writeInt(1);
                        bluetoothDevice.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(71, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public AudioRoutesInfo startWatchingRoutes(IAudioRoutesObserver iAudioRoutesObserver) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iAudioRoutesObserver != null ? iAudioRoutesObserver.asBinder() : null);
                    this.mRemote.transact(72, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? AudioRoutesInfo.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isCameraSoundForced() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(73, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }
    }
}
