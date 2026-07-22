package android.media;

import android.app.backup.FullBackup;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.MediaTimeProvider;
import android.media.SubtitleController;
import android.net.Proxy;
import android.net.ProxyProperties;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PowerManager;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.softwinner.ISOMountManager;
import com.softwinner.utils.Config;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class MediaPlayer implements SubtitleController.Listener {
    public static final int ANAGLAGH_COLOR = 3;
    public static final int ANAGLAGH_HALF_COLOR = 4;
    public static final int ANAGLAGH_OPTIMIZED = 5;
    public static final int ANAGLAGH_RED_BLUE = 0;
    public static final int ANAGLAGH_RED_CYAN = 2;
    public static final int ANAGLAGH_RED_GREEN = 1;
    public static final int ANAGLAGH_YELLOW_BLUE = 6;
    public static final boolean APPLY_METADATA_FILTER = true;
    public static final int AUDIO_CHANNEL_MUTE_ALL = 3;
    public static final int AUDIO_CHANNEL_MUTE_LEFT = 1;
    public static final int AUDIO_CHANNEL_MUTE_NONE = 0;
    public static final int AUDIO_CHANNEL_MUTE_RIGHT = 2;
    public static int AUDIO_DATA_MODE_HDMI_RAW = 0;
    public static int AUDIO_DATA_MODE_PCM = 0;
    public static int AUDIO_DATA_MODE_SPDIF_RAW = 0;
    public static final boolean BYPASS_METADATA_FILTER = false;
    public static final String CHARSET_BIG5 = "Big5";
    public static final String CHARSET_BIG5_HKSCS = "Big5-HKSCS";
    public static final String CHARSET_BOCU_1 = "BOCU-1";
    public static final String CHARSET_CESU_8 = "CESU-8";
    public static final String CHARSET_CP864 = "cp864";
    public static final String CHARSET_EUC_JP = "EUC-JP";
    public static final String CHARSET_EUC_KR = "EUC-KR";
    public static final String CHARSET_GB18030 = "GB18030";
    public static final String CHARSET_GBK = "GBK";
    public static final String CHARSET_HZ_GB_2312 = "HZ-GB-2312";
    public static final String CHARSET_ISO_2022_CN = "ISO-2022-CN";
    public static final String CHARSET_ISO_2022_CN_EXT = "ISO-2022-CN-EXT";
    public static final String CHARSET_ISO_2022_JP = "ISO-2022-JP";
    public static final String CHARSET_ISO_2022_KR = "ISO-2022-KR";
    public static final String CHARSET_ISO_8859_1 = "ISO-8859-1";
    public static final String CHARSET_ISO_8859_10 = "ISO-8859-10";
    public static final String CHARSET_ISO_8859_13 = "ISO-8859-13";
    public static final String CHARSET_ISO_8859_14 = "ISO-8859-14";
    public static final String CHARSET_ISO_8859_15 = "ISO-8859-15";
    public static final String CHARSET_ISO_8859_16 = "ISO-8859-16";
    public static final String CHARSET_ISO_8859_2 = "ISO-8859-2";
    public static final String CHARSET_ISO_8859_3 = "ISO-8859-3";
    public static final String CHARSET_ISO_8859_4 = "ISO-8859-4";
    public static final String CHARSET_ISO_8859_5 = "ISO-8859-5";
    public static final String CHARSET_ISO_8859_6 = "ISO-8859-6";
    public static final String CHARSET_ISO_8859_7 = "ISO-8859-7";
    public static final String CHARSET_ISO_8859_8 = "ISO-8859-8";
    public static final String CHARSET_ISO_8859_9 = "ISO-8859-9";
    public static final String CHARSET_KOI8_R = "KOI8-R";
    public static final String CHARSET_KOI8_U = "KOI8-U";
    public static final String CHARSET_MACINTOSH = "macintosh";
    public static final String CHARSET_SCSU = "SCSU";
    public static final String CHARSET_SHIFT_JIS = "Shift_JIS";
    public static final String CHARSET_TIS_620 = "TIS-620";
    public static final String CHARSET_UNKNOWN = "UNKNOWN";
    public static final String CHARSET_US_ASCII = "US-ASCII";
    public static final String CHARSET_UTF_16 = "UTF-16";
    public static final String CHARSET_UTF_16BE = "UTF-16BE";
    public static final String CHARSET_UTF_16LE = "UTF-16LE";
    public static final String CHARSET_UTF_32 = "UTF-32";
    public static final String CHARSET_UTF_32BE = "UTF-32BE";
    public static final String CHARSET_UTF_32LE = "UTF-32LE";
    public static final String CHARSET_UTF_7 = "UTF-7";
    public static final String CHARSET_UTF_8 = "UTF-8";
    public static final String CHARSET_WINDOWS_1250 = "windows-1250";
    public static final String CHARSET_WINDOWS_1251 = "windows-1251";
    public static final String CHARSET_WINDOWS_1252 = "windows-1252";
    public static final String CHARSET_WINDOWS_1253 = "windows-1253";
    public static final String CHARSET_WINDOWS_1254 = "windows-1254";
    public static final String CHARSET_WINDOWS_1255 = "windows-1255";
    public static final String CHARSET_WINDOWS_1256 = "windows-1256";
    public static final String CHARSET_WINDOWS_1257 = "windows-1257";
    public static final String CHARSET_WINDOWS_1258 = "windows-1258";
    public static final String CHARSET_X_DOCOMO_SHIFT_JIS_2007 = "x-docomo-shift_jis-2007";
    public static final String CHARSET_X_GSM_03_38_2000 = "x-gsm-03.38-2000";
    public static final String CHARSET_X_IBM_1383_P110_1999 = "x-ibm-1383_P110-1999";
    public static final String CHARSET_X_IMAP_MAILBOX_NAME = "x-IMAP-mailbox-name";
    public static final String CHARSET_X_ISCII_BE = "x-iscii-be";
    public static final String CHARSET_X_ISCII_DE = "x-iscii-de";
    public static final String CHARSET_X_ISCII_GU = "x-iscii-gu";
    public static final String CHARSET_X_ISCII_KA = "x-iscii-ka";
    public static final String CHARSET_X_ISCII_MA = "x-iscii-ma";
    public static final String CHARSET_X_ISCII_OR = "x-iscii-or";
    public static final String CHARSET_X_ISCII_PA = "x-iscii-pa";
    public static final String CHARSET_X_ISCII_TA = "x-iscii-ta";
    public static final String CHARSET_X_ISCII_TE = "x-iscii-te";
    public static final String CHARSET_X_ISO_8859_11_2001 = "x-iso-8859_11-2001";
    public static final String CHARSET_X_JAVAUNICODE = "x-JavaUnicode";
    public static final String CHARSET_X_KDDI_SHIFT_JIS_2007 = "x-kddi-shift_jis-2007";
    public static final String CHARSET_X_MAC_CYRILLIC = "x-mac-cyrillic";
    public static final String CHARSET_X_SOFTBANK_SHIFT_JIS_2007 = "x-softbank-shift_jis-2007";
    public static final String CHARSET_X_UNICODEBIG = "x-UnicodeBig";
    public static final String CHARSET_X_UTF16_OPPOSITEENDIAN = "x-UTF16_OppositeEndian";
    public static final String CHARSET_X_UTF16_PLATFORMENDIAN = "x-UTF16_PlatformEndian";
    public static final String CHARSET_X_UTF32_OPPOSITEENDIAN = "x-UTF32_OppositeEndian";
    public static final String CHARSET_X_UTF32_PLATFORMENDIAN = "x-UTF32_PlatformEndian";
    public static final String CHARSET_X_UTF_16LE_BOM = "x-UTF-16LE-BOM";
    public static final int DISPLAY_3D_MODE_2D = 0;
    public static final int DISPLAY_3D_MODE_3D = 1;
    public static final int DISPLAY_3D_MODE_ANAGLAGH = 3;
    public static final int DISPLAY_3D_MODE_HALF_PICTURE = 2;
    private static final String DLNA_SOURCE_DETECTOR = "com.softwinner.dlnasourcedetector";
    private static final String IMEDIA_PLAYER = "android.media.IMediaPlayer";
    private static final int INVOKE_ID_ADD_EXTERNAL_SOURCE = 2;
    private static final int INVOKE_ID_ADD_EXTERNAL_SOURCE_FD = 3;
    private static final int INVOKE_ID_DESELECT_TRACK = 5;
    private static final int INVOKE_ID_GET_3D_MODE = 129;
    private static final int INVOKE_ID_GET_TRACK_INFO = 1;
    private static final int INVOKE_ID_SELECT_TRACK = 4;
    private static final int INVOKE_ID_SET_3D_MODE = 128;
    private static final int INVOKE_ID_SET_VIDEO_SCALE_MODE = 6;
    public static final int MASTER_SCREEN = 0;
    private static final int MEDIA_BUFFERING_UPDATE = 3;
    private static final int MEDIA_ERROR = 100;
    public static final int MEDIA_ERROR_IO = -1004;
    public static final int MEDIA_ERROR_MALFORMED = -1007;
    public static final int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;
    public static final int MEDIA_ERROR_OUT_OF_MEMORY = 900;
    public static final int MEDIA_ERROR_SERVER_DIED = 100;
    public static final int MEDIA_ERROR_TIMED_OUT = -110;
    public static final int MEDIA_ERROR_UNKNOWN = 1;
    public static final int MEDIA_ERROR_UNSUPPORTED = -1010;
    private static final int MEDIA_INFO = 200;
    public static final int MEDIA_INFO_AWEXTEND_INDICATE_3D_DOUBLE_STREAM = 4096;
    public static final int MEDIA_INFO_BAD_INTERLEAVING = 800;
    public static final int MEDIA_INFO_BUFFERING_END = 702;
    public static final int MEDIA_INFO_BUFFERING_START = 701;
    public static final int MEDIA_INFO_DOWNLOAD_END = 10087;
    public static final int MEDIA_INFO_DOWNLOAD_ERROR = 10088;
    public static final int MEDIA_INFO_DOWNLOAD_START = 10086;
    public static final int MEDIA_INFO_EXTERNAL_METADATA_UPDATE = 803;
    public static final int MEDIA_INFO_METADATA_UPDATE = 802;
    public static final int MEDIA_INFO_NOT_SEEKABLE = 801;
    public static final int MEDIA_INFO_STARTED_AS_NEXT = 2;
    public static final int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902;
    public static final int MEDIA_INFO_TIMED_TEXT_ERROR = 900;
    public static final int MEDIA_INFO_UNKNOWN = 1;
    public static final int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901;
    public static final int MEDIA_INFO_VIDEO_RENDERING_START = 3;
    public static final int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700;
    private static final String[] MEDIA_MIMETYPE;
    public static final String MEDIA_MIMETYPE_TEXT_AQT = "text/aqt";
    public static final String MEDIA_MIMETYPE_TEXT_ASC = "text/asc";
    public static final String MEDIA_MIMETYPE_TEXT_ASS = "text/ass";
    public static final String MEDIA_MIMETYPE_TEXT_DKS = "text/dks";
    public static final String MEDIA_MIMETYPE_TEXT_IDXSUB = "application/idx-sub";
    public static final String MEDIA_MIMETYPE_TEXT_JS = "text/js";
    public static final String MEDIA_MIMETYPE_TEXT_JSS = "text/jss";
    public static final String MEDIA_MIMETYPE_TEXT_LRC = "text/lrc";
    public static final String MEDIA_MIMETYPE_TEXT_MPL = "text/mpl";
    public static final String MEDIA_MIMETYPE_TEXT_OVR = "text/ovr";
    public static final String MEDIA_MIMETYPE_TEXT_PAN = "text/pan";
    public static final String MEDIA_MIMETYPE_TEXT_PJS = "text/pjs";
    public static final String MEDIA_MIMETYPE_TEXT_PSB = "text/psb";
    public static final String MEDIA_MIMETYPE_TEXT_RT = "text/rt";
    public static final String MEDIA_MIMETYPE_TEXT_RTF = "text/rtf";
    public static final String MEDIA_MIMETYPE_TEXT_S2K = "text/s2k";
    public static final String MEDIA_MIMETYPE_TEXT_SBT = "text/sbt";
    public static final String MEDIA_MIMETYPE_TEXT_SCR = "text/scr";
    public static final String MEDIA_MIMETYPE_TEXT_SMI = "text/smi";
    public static final String MEDIA_MIMETYPE_TEXT_SON = "text/son";
    public static final String MEDIA_MIMETYPE_TEXT_SSA = "text/ssa";
    public static final String MEDIA_MIMETYPE_TEXT_SST = "text/sst";
    public static final String MEDIA_MIMETYPE_TEXT_SSTS = "text/ssts";
    public static final String MEDIA_MIMETYPE_TEXT_STL = "text/stl";
    public static final String MEDIA_MIMETYPE_TEXT_SUB = "application/sub";
    public static final String MEDIA_MIMETYPE_TEXT_SUBRIP = "application/x-subrip";
    public static final String MEDIA_MIMETYPE_TEXT_TTS = "text/tts";
    public static final String MEDIA_MIMETYPE_TEXT_TXT = "text/txt";
    public static final String MEDIA_MIMETYPE_TEXT_VKT = "text/vkt";
    public static final String MEDIA_MIMETYPE_TEXT_VSF = "text/vsf";
    public static final String MEDIA_MIMETYPE_TEXT_VTT = "text/vtt";
    public static final String MEDIA_MIMETYPE_TEXT_ZEG = "text/zeg";
    private static final int MEDIA_NOP = 0;
    private static final int MEDIA_PAUSED = 7;
    private static final int MEDIA_PLAYBACK_COMPLETE = 2;
    private static final int MEDIA_PREPARED = 1;
    private static final int MEDIA_SEEK_COMPLETE = 4;
    private static final int MEDIA_SET_VIDEO_SIZE = 5;
    private static final int MEDIA_SKIPPED = 9;
    private static final int MEDIA_SOURCE_DETECTED = 234;
    private static final int MEDIA_STARTED = 6;
    private static final int MEDIA_STOPPED = 8;
    private static final int MEDIA_SUBTITLE_DATA = 201;
    private static final int MEDIA_TIMED_TEXT = 99;
    public static final boolean METADATA_ALL = false;
    public static final boolean METADATA_UPDATE_ONLY = true;
    public static final int PICTURE_3D_MODE_COLUME_INTERLEAVE = 5;
    public static final int PICTURE_3D_MODE_DOUBLE_STREAM = 1;
    public static final int PICTURE_3D_MODE_LINE_INTERLEAVE = 4;
    public static final int PICTURE_3D_MODE_NONE = 0;
    public static final int PICTURE_3D_MODE_SIDE_BY_SIDE = 2;
    public static final int PICTURE_3D_MODE_TOP_TO_BOTTOM = 3;
    public static final int SLAVE_SCREEN = 1;
    public static final int SUBTITLE_TYPE_BITMAP = 1;
    public static final int SUBTITLE_TYPE_TEXT = 0;
    private static final String[] SUB_EXTS;
    private static final String TAG = "MediaPlayer";
    public static final int VIDEO_SCALING_MODE_SCALE_TO_FIT = 1;
    public static final int VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING = 2;
    private static int mRawDataMode;
    private DlnaSourceDetector mDlnaSourceDetector;
    private EventHandler mEventHandler;
    private SubtitleTrack[] mInbandSubtitleTracks;
    private ISOManager mIso;
    private int mListenerContext;
    private int mNativeContext;
    private int mNativeSurfaceTexture;
    private OnBufferingUpdateListener mOnBufferingUpdateListener;
    private OnCompletionListener mOnCompletionListener;
    private OnErrorListener mOnErrorListener;
    private OnInfoListener mOnInfoListener;
    private OnPreparedListener mOnPreparedListener;
    private OnSeekCompleteListener mOnSeekCompleteListener;
    private OnSubtitleDataListener mOnSubtitleDataListener;
    private OnTimedTextListener mOnTimedTextListener;
    private OnVideoSizeChangedListener mOnVideoSizeChangedListener;
    private Vector<InputStream> mOpenSubtitleSources;
    private Vector<SubtitleTrack> mOutOfBandSubtitleTracks;
    private boolean mScreenOnWhilePlaying;
    private boolean mStayAwake;
    private SubtitleController mSubtitleController;
    private SurfaceHolder mSurfaceHolder;
    private TimeProvider mTimeProvider;
    private PowerManager.WakeLock mWakeLock = null;
    private ArrayList<String> mSrtList = new ArrayList<>();
    private ArrayList<String> mMediaTypeList = new ArrayList<>();
    private int mSelectedSubtitleTrackIndex = -1;
    private OnSubtitleDataListener mSubtitleDataListener = new OnSubtitleDataListener() { // from class: android.media.MediaPlayer.1
        @Override // android.media.MediaPlayer.OnSubtitleDataListener
        public void onSubtitleData(MediaPlayer mediaPlayer, SubtitleData subtitleData) {
            SubtitleTrack subtitleTrack;
            int trackIndex = subtitleData.getTrackIndex();
            if (trackIndex < MediaPlayer.this.mInbandSubtitleTracks.length && (subtitleTrack = MediaPlayer.this.mInbandSubtitleTracks[trackIndex]) != null) {
                try {
                    long startTimeUs = subtitleData.getStartTimeUs() + 1;
                    subtitleTrack.onData(new String(subtitleData.getData(), "UTF-8"), true, startTimeUs);
                    subtitleTrack.setRunDiscardTimeMs(startTimeUs, (subtitleData.getStartTimeUs() + subtitleData.getDurationUs()) / 1000);
                } catch (UnsupportedEncodingException e) {
                    Log.w(MediaPlayer.TAG, "subtitle data for track " + trackIndex + " is not UTF-8 encoded: " + e);
                }
            }
        }
    };
    private Context mProxyContext = null;
    private ProxyReceiver mProxyReceiver = null;

    public interface DlnaSourceDetector {
        void onSourceDetected(String str);
    }

    public interface OnBufferingUpdateListener {
        void onBufferingUpdate(MediaPlayer mediaPlayer, int i);
    }

    public interface OnCompletionListener {
        void onCompletion(MediaPlayer mediaPlayer);
    }

    public interface OnErrorListener {
        boolean onError(MediaPlayer mediaPlayer, int i, int i2);
    }

    public interface OnInfoListener {
        boolean onInfo(MediaPlayer mediaPlayer, int i, int i2);
    }

    public interface OnPreparedListener {
        void onPrepared(MediaPlayer mediaPlayer);
    }

    public interface OnSeekCompleteListener {
        void onSeekComplete(MediaPlayer mediaPlayer);
    }

    public interface OnSubtitleDataListener {
        void onSubtitleData(MediaPlayer mediaPlayer, SubtitleData subtitleData);
    }

    public interface OnTimedTextListener {
        void onTimedText(MediaPlayer mediaPlayer, TimedText timedText);
    }

    public interface OnVideoSizeChangedListener {
        void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i2);
    }

    private native void _pause() throws IllegalStateException;

    private native void _release();

    private native void _reset();

    private native void _setDataSource(FileDescriptor fileDescriptor, long j, long j2) throws IllegalStateException, IOException, IllegalArgumentException;

    private native void _setDataSource(String str, String[] strArr, String[] strArr2) throws IllegalStateException, IOException, SecurityException, IllegalArgumentException;

    private static native int _setRawDataMode(int i);

    private native void _setVideoSurface(Surface surface);

    private native void _start() throws IllegalStateException;

    private native void _stop() throws IllegalStateException;

    /* JADX INFO: Access modifiers changed from: private */
    public static final String getAudioMimeType(int i) {
        switch (i) {
            case -1:
                return "audio/ ";
            case 0:
                return "unknown";
            case 1:
                return "audio/mp1";
            case 2:
                return "audio/mp2";
            case 3:
                return "audio/mp3";
            case 4:
                return "audio/mpeg_aac_lc";
            case 5:
                return "audio/ac3";
            case 6:
                return "audio/dts";
            case 7:
                return "audio/lpcm_v";
            case 8:
                return "audio/lpcm_a";
            case 9:
                return "audio/adpcm";
            case 10:
                return "audio/pcm";
            case 11:
                return "audio/wma_standard";
            case 12:
                return "audio/flac";
            case 13:
                return "audio/ape";
            case 14:
                return "audio/ogg";
            case 15:
                return "audio/raac";
            case 16:
                return "audio/cook";
            case 17:
                return "audio/sipr";
            case 18:
                return "audio/atrc";
            case 19:
                return "audio/amr";
            case 20:
                return "audio/ra";
            case 21:
                return "audio/ppcm";
            case 22:
                return "audio/wma_loss";
            case 23:
                return "audio/wma_pro";
            case 24:
                return "audio/mp3_pro";
            case 25:
                return "audio/alac";
            default:
                return null;
        }
    }

    private native void getParameter(int i, Parcel parcel);

    /* JADX INFO: Access modifiers changed from: private */
    public static final String getSubtitleMimeType(int i) {
        if (i == 0) {
            return "subtitle/unknown";
        }
        switch (i) {
            case 256:
                return MEDIA_MIMETYPE_TEXT_SUB;
            case 257:
                return MEDIA_MIMETYPE_TEXT_IDXSUB;
            case 258:
                return "text/pgs";
            case 259:
                return "text/divx";
            default:
                switch (i) {
                    case 512:
                    case 513:
                        return MEDIA_MIMETYPE_TEXT_TXT;
                    case 514:
                        return MEDIA_MIMETYPE_TEXT_SSA;
                    case 515:
                        return MEDIA_MIMETYPE_TEXT_SMI;
                    case 516:
                        return "text/srt";
                    default:
                        return null;
                }
        }
    }

    private boolean isVideoScalingModeSupported(int i) {
        return i == 1 || i == 2;
    }

    private final native void native_finalize();

    private final native boolean native_getMetadata(boolean z, boolean z2, Parcel parcel);

    private static final native void native_init();

    private final native int native_invoke(Parcel parcel, Parcel parcel2);

    public static native int native_pullBatteryData(Parcel parcel);

    private final native int native_setMetadataFilter(Parcel parcel);

    private final native int native_setRetransmitEndpoint(String str, int i);

    private final native void native_setup(Object obj);

    private native void updateProxyConfig(ProxyProperties proxyProperties);

    public native void attachAuxEffect(int i);

    public native void enableScaleMode(boolean z, int i, int i2);

    public native int getAudioSessionId();

    public native boolean getBdFolderPlayMode();

    public native int getChannelMuteMode();

    public native int getCurrentPosition();

    public native int getDuration();

    public native String getSubCharset();

    public native int getSubDelay();

    public native int getVideoHeight();

    public native int getVideoWidth();

    public native boolean isLooping();

    public native boolean isPlaying();

    public native void prepare() throws IllegalStateException, IOException;

    public native void prepareAsync() throws IllegalStateException;

    public native int releaseSurfaceByHand();

    public native void seekTo(int i) throws IllegalStateException;

    public native void setAudioSessionId(int i) throws IllegalStateException, IllegalArgumentException;

    public native void setAudioStreamType(int i);

    public native void setAuxEffectSendLevel(float f);

    public native int setBdFolderPlayMode(boolean z);

    public native int setChannelMuteMode(int i);

    public native void setLooping(boolean z);

    public native void setNextMediaPlayer(MediaPlayer mediaPlayer);

    public native boolean setParameter(int i, Parcel parcel);

    public native int setSubCharset(String str);

    public native int setSubDelay(int i);

    public native void setVolume(float f, float f2);

    static {
        System.loadLibrary("media_jni");
        native_init();
        SUB_EXTS = new String[]{".idx", ".sub", ".srt", ".smi", ".rt", ".txt", ".ssa", ".aqt", ".jss", ".js", ".ass", ".vsf", ".tts", ".stl", ".zeg", ".ovr", ".dks", ".lrc", ".pan", ".sbt", ".vkt", ".pjs", ".mpl", ".scr", ".psb", ".asc", ".rtf", ".s2k", ".sst", ".son", ".ssts", ".sami"};
        MEDIA_MIMETYPE = new String[]{MEDIA_MIMETYPE_TEXT_IDXSUB, MEDIA_MIMETYPE_TEXT_SUB, MEDIA_MIMETYPE_TEXT_SUBRIP, MEDIA_MIMETYPE_TEXT_SMI, MEDIA_MIMETYPE_TEXT_RT, MEDIA_MIMETYPE_TEXT_TXT, MEDIA_MIMETYPE_TEXT_SSA, MEDIA_MIMETYPE_TEXT_AQT, MEDIA_MIMETYPE_TEXT_JSS, MEDIA_MIMETYPE_TEXT_JS, MEDIA_MIMETYPE_TEXT_ASS, MEDIA_MIMETYPE_TEXT_VSF, MEDIA_MIMETYPE_TEXT_TTS, MEDIA_MIMETYPE_TEXT_STL, MEDIA_MIMETYPE_TEXT_ZEG, MEDIA_MIMETYPE_TEXT_OVR, MEDIA_MIMETYPE_TEXT_DKS, MEDIA_MIMETYPE_TEXT_LRC, MEDIA_MIMETYPE_TEXT_PAN, MEDIA_MIMETYPE_TEXT_SBT, MEDIA_MIMETYPE_TEXT_VKT, MEDIA_MIMETYPE_TEXT_PJS, MEDIA_MIMETYPE_TEXT_MPL, MEDIA_MIMETYPE_TEXT_SCR, MEDIA_MIMETYPE_TEXT_PSB, MEDIA_MIMETYPE_TEXT_ASC, MEDIA_MIMETYPE_TEXT_RTF, MEDIA_MIMETYPE_TEXT_S2K, MEDIA_MIMETYPE_TEXT_SST, MEDIA_MIMETYPE_TEXT_SON, MEDIA_MIMETYPE_TEXT_SSTS, MEDIA_MIMETYPE_TEXT_SMI};
        AUDIO_DATA_MODE_PCM = 1;
        AUDIO_DATA_MODE_HDMI_RAW = 2;
        AUDIO_DATA_MODE_SPDIF_RAW = 3;
        mRawDataMode = 1;
    }

    public MediaPlayer() {
        Looper looperMyLooper = Looper.myLooper();
        if (looperMyLooper != null) {
            this.mEventHandler = new EventHandler(this, looperMyLooper);
        } else {
            Looper mainLooper = Looper.getMainLooper();
            if (mainLooper != null) {
                this.mEventHandler = new EventHandler(this, mainLooper);
            } else {
                this.mEventHandler = null;
            }
        }
        this.mTimeProvider = new TimeProvider(this);
        this.mOutOfBandSubtitleTracks = new Vector<>();
        this.mOpenSubtitleSources = new Vector<>();
        this.mInbandSubtitleTracks = new SubtitleTrack[0];
        native_setup(new WeakReference(this));
    }

    public Parcel newRequest() {
        Parcel parcelObtain = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IMEDIA_PLAYER);
        return parcelObtain;
    }

    public void invoke(Parcel parcel, Parcel parcel2) {
        int iNative_invoke = native_invoke(parcel, parcel2);
        parcel2.setDataPosition(0);
        if (iNative_invoke != 0) {
            throw new RuntimeException("failure code: " + iNative_invoke);
        }
    }

    public void setDisplay(SurfaceHolder surfaceHolder) {
        this.mSurfaceHolder = surfaceHolder;
        _setVideoSurface(surfaceHolder != null ? surfaceHolder.getSurface() : null);
        updateSurfaceScreenOn();
    }

    public void setSurface(Surface surface) {
        if (this.mScreenOnWhilePlaying && surface != null) {
            Log.w(TAG, "setScreenOnWhilePlaying(true) is ineffective for Surface");
        }
        this.mSurfaceHolder = null;
        _setVideoSurface(surface);
        updateSurfaceScreenOn();
    }

    public void setVideoScalingMode(int i) {
        if (!isVideoScalingModeSupported(i)) {
            throw new IllegalArgumentException("Scaling mode " + i + " is not supported");
        }
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            parcelObtain.writeInterfaceToken(IMEDIA_PLAYER);
            parcelObtain.writeInt(6);
            parcelObtain.writeInt(i);
            invoke(parcelObtain, parcelObtain2);
        } finally {
            parcelObtain.recycle();
            parcelObtain2.recycle();
        }
    }

    public static MediaPlayer create(Context context, Uri uri) {
        return create(context, uri, null);
    }

    public static MediaPlayer create(Context context, Uri uri, SurfaceHolder surfaceHolder) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context, uri);
            if (surfaceHolder != null) {
                mediaPlayer.setDisplay(surfaceHolder);
            }
            mediaPlayer.prepare();
            return mediaPlayer;
        } catch (IOException e) {
            Log.d(TAG, "create failed:", e);
            return null;
        } catch (IllegalArgumentException e2) {
            Log.d(TAG, "create failed:", e2);
            return null;
        } catch (SecurityException e3) {
            Log.d(TAG, "create failed:", e3);
            return null;
        }
    }

    public static MediaPlayer create(Context context, int i) {
        try {
            AssetFileDescriptor assetFileDescriptorOpenRawResourceFd = context.getResources().openRawResourceFd(i);
            if (assetFileDescriptorOpenRawResourceFd == null) {
                return null;
            }
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(assetFileDescriptorOpenRawResourceFd.getFileDescriptor(), assetFileDescriptorOpenRawResourceFd.getStartOffset(), assetFileDescriptorOpenRawResourceFd.getLength());
            assetFileDescriptorOpenRawResourceFd.close();
            mediaPlayer.prepare();
            return mediaPlayer;
        } catch (IOException e) {
            Log.d(TAG, "create failed:", e);
            return null;
        } catch (IllegalArgumentException e2) {
            Log.d(TAG, "create failed:", e2);
            return null;
        } catch (SecurityException e3) {
            Log.d(TAG, "create failed:", e3);
            return null;
        }
    }

    public void setDataSource(Context context, Uri uri) throws IllegalStateException, IOException, SecurityException, IllegalArgumentException {
        setDataSource(context, uri, (Map<String, String>) null);
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x0061  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void setDataSource(android.content.Context r9, android.net.Uri r10, java.util.Map<java.lang.String, java.lang.String> r11) throws java.lang.IllegalStateException, java.io.IOException, java.lang.SecurityException, java.lang.IllegalArgumentException {
        /*
            r8 = this;
            r8.disableProxyListener()
            android.media.MediaPlayer$ISOManager r0 = new android.media.MediaPlayer$ISOManager
            r0.<init>(r9)
            r8.mIso = r0
            java.lang.String r0 = r10.getScheme()
            if (r0 == 0) goto L86
            java.lang.String r1 = "file"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L19
            goto L86
        L19:
            r1 = 0
            android.content.ContentResolver r2 = r9.getContentResolver()     // Catch: java.lang.Throwable -> L55 java.io.IOException -> L5c java.lang.SecurityException -> L5f
            java.lang.String r3 = "r"
            android.content.res.AssetFileDescriptor r1 = r2.openAssetFileDescriptor(r10, r3)     // Catch: java.lang.Throwable -> L55 java.io.IOException -> L5c java.lang.SecurityException -> L5f
            if (r1 != 0) goto L2d
            if (r1 == 0) goto L2c
            r1.close()
        L2c:
            return
        L2d:
            long r2 = r1.getDeclaredLength()     // Catch: java.lang.Throwable -> L55 java.io.IOException -> L5c java.lang.SecurityException -> L5f
            r4 = 0
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 >= 0) goto L3f
            java.io.FileDescriptor r2 = r1.getFileDescriptor()     // Catch: java.lang.Throwable -> L55 java.io.IOException -> L5c java.lang.SecurityException -> L5f
            r8.setDataSource(r2)     // Catch: java.lang.Throwable -> L55 java.io.IOException -> L5c java.lang.SecurityException -> L5f
            goto L4f
        L3f:
            java.io.FileDescriptor r3 = r1.getFileDescriptor()     // Catch: java.lang.Throwable -> L55 java.io.IOException -> L5c java.lang.SecurityException -> L5f
            long r4 = r1.getStartOffset()     // Catch: java.lang.Throwable -> L55 java.io.IOException -> L5c java.lang.SecurityException -> L5f
            long r6 = r1.getDeclaredLength()     // Catch: java.lang.Throwable -> L55 java.io.IOException -> L5c java.lang.SecurityException -> L5f
            r2 = r8
            r2.setDataSource(r3, r4, r6)     // Catch: java.lang.Throwable -> L55 java.io.IOException -> L5c java.lang.SecurityException -> L5f
        L4f:
            if (r1 == 0) goto L54
            r1.close()
        L54:
            return
        L55:
            r9 = move-exception
            if (r1 == 0) goto L5b
            r1.close()
        L5b:
            throw r9
        L5c:
            if (r1 == 0) goto L64
            goto L61
        L5f:
            if (r1 == 0) goto L64
        L61:
            r1.close()
        L64:
            java.lang.String r1 = "MediaPlayer"
            java.lang.String r2 = "Couldn't open file on client side, trying server side"
            android.util.Log.d(r1, r2)
            java.lang.String r10 = r10.toString()
            r8.setDataSource(r10, r11)
            java.lang.String r10 = "http"
            boolean r10 = r0.equalsIgnoreCase(r10)
            if (r10 != 0) goto L82
            java.lang.String r10 = "https"
            boolean r10 = r0.equalsIgnoreCase(r10)
            if (r10 == 0) goto L85
        L82:
            r8.setupProxyListener(r9)
        L85:
            return
        L86:
            java.lang.String r9 = r10.getPath()
            r8.setDataSource(r9)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MediaPlayer.setDataSource(android.content.Context, android.net.Uri, java.util.Map):void");
    }

    public void setDataSource(String str) throws IllegalStateException, IOException, SecurityException, IllegalArgumentException {
        setDataSource(str, (String[]) null, (String[]) null);
    }

    public void setDataSource(String str, Map<String, String> map) throws IllegalStateException, IOException, SecurityException, IllegalArgumentException {
        String[] strArr;
        String[] strArr2 = null;
        if (map != null) {
            strArr2 = new String[map.size()];
            strArr = new String[map.size()];
            int i = 0;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                strArr2[i] = entry.getKey();
                strArr[i] = entry.getValue();
                i++;
            }
        } else {
            strArr = null;
        }
        setDataSource(str, strArr2, strArr);
    }

    private void setDataSource(String str, String[] strArr, String[] strArr2) throws IllegalStateException, IOException, SecurityException, IllegalArgumentException {
        disableProxyListener();
        Uri uri = Uri.parse(str);
        if ("file".equals(uri.getScheme())) {
            str = uri.getPath();
        }
        if (str != null && str.contains("/BDMV/STREAM")) {
            Log.d(TAG, "It is BD video directoty, make the path !!!");
            setBdFolderPlayMode(true);
            str = "bdmv://" + str.substring(0, str.indexOf("/BDMV/STREAM"));
        } else if (str != null && str.contains(".iso") && !str.contains("bdmv")) {
            Log.d(TAG, "It is BD video iso, make the path !!!");
            setBdFolderPlayMode(true);
            ISOManager iSOManager = this.mIso;
            if (iSOManager != null) {
                str = "bdmv://" + iSOManager.getVirtualCDRomPath(str);
            } else {
                Log.d(TAG, "There is no ISOManager, check it!!!");
                return;
            }
        }
        if (str == null) {
            return;
        }
        File file = new File(str);
        if (file.exists() && !getBdFolderPlayMode()) {
            FileInputStream fileInputStream = new FileInputStream(file);
            setDataSource(fileInputStream.getFD());
            fileInputStream.close();
        } else {
            _setDataSource(str, strArr, strArr2);
        }
        if (Config.getTargetPlatform(1) == 8) {
            searchSubTitle(str);
            ArrayList<String> arrayList = this.mSrtList;
            if (arrayList == null || arrayList.size() <= 0) {
                return;
            }
            for (int i = 0; i < this.mSrtList.size(); i++) {
                addTimedTextSource(this.mSrtList.get(i), this.mMediaTypeList.get(i));
                Log.d("fuqiang", "here j = " + i + ", mSrtList.get(j) = " + this.mSrtList.get(i) + ", mMediaTypeList.get(j) = " + this.mMediaTypeList.get(i));
            }
        }
    }

    public void setDataSource(FileDescriptor fileDescriptor) throws IllegalStateException, IOException, IllegalArgumentException {
        setDataSource(fileDescriptor, 0L, 576460752303423487L);
    }

    public void setDataSource(FileDescriptor fileDescriptor, long j, long j2) throws IllegalStateException, IOException, IllegalArgumentException {
        disableProxyListener();
        _setDataSource(fileDescriptor, j, j2);
    }

    public void start() throws IllegalStateException {
        stayAwake(true);
        _start();
    }

    public void stop() throws IllegalStateException {
        stayAwake(false);
        _stop();
    }

    public void pause() throws IllegalStateException {
        stayAwake(false);
        _pause();
    }

    public void setWakeMode(Context context, int i) {
        boolean z;
        PowerManager.WakeLock wakeLock = this.mWakeLock;
        if (wakeLock != null) {
            if (wakeLock.isHeld()) {
                z = true;
                this.mWakeLock.release();
            } else {
                z = false;
            }
            this.mWakeLock = null;
        } else {
            z = false;
        }
        PowerManager.WakeLock wakeLockNewWakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(i | 536870912, MediaPlayer.class.getName());
        this.mWakeLock = wakeLockNewWakeLock;
        wakeLockNewWakeLock.setReferenceCounted(false);
        if (z) {
            this.mWakeLock.acquire();
        }
    }

    public void setScreenOnWhilePlaying(boolean z) {
        if (this.mScreenOnWhilePlaying != z) {
            if (z && this.mSurfaceHolder == null) {
                Log.w(TAG, "setScreenOnWhilePlaying(true) is ineffective without a SurfaceHolder");
            }
            this.mScreenOnWhilePlaying = z;
            updateSurfaceScreenOn();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stayAwake(boolean z) {
        PowerManager.WakeLock wakeLock = this.mWakeLock;
        if (wakeLock != null) {
            if (z && !wakeLock.isHeld()) {
                this.mWakeLock.acquire();
            } else if (!z && this.mWakeLock.isHeld()) {
                this.mWakeLock.release();
            }
        }
        this.mStayAwake = z;
        updateSurfaceScreenOn();
    }

    private void updateSurfaceScreenOn() {
        SurfaceHolder surfaceHolder = this.mSurfaceHolder;
        if (surfaceHolder != null) {
            surfaceHolder.setKeepScreenOn(this.mScreenOnWhilePlaying && this.mStayAwake);
        }
    }

    public Metadata getMetadata(boolean z, boolean z2) {
        Parcel parcelObtain = Parcel.obtain();
        Metadata metadata = new Metadata();
        if (!native_getMetadata(z, z2, parcelObtain)) {
            parcelObtain.recycle();
            return null;
        }
        if (metadata.parse(parcelObtain)) {
            return metadata;
        }
        parcelObtain.recycle();
        return null;
    }

    public int setMetadataFilter(Set<Integer> set, Set<Integer> set2) {
        Parcel parcelNewRequest = newRequest();
        int iDataSize = parcelNewRequest.dataSize() + ((set.size() + 1 + 1 + set2.size()) * 4);
        if (parcelNewRequest.dataCapacity() < iDataSize) {
            parcelNewRequest.setDataCapacity(iDataSize);
        }
        parcelNewRequest.writeInt(set.size());
        Iterator<Integer> it = set.iterator();
        while (it.hasNext()) {
            parcelNewRequest.writeInt(it.next().intValue());
        }
        parcelNewRequest.writeInt(set2.size());
        Iterator<Integer> it2 = set2.iterator();
        while (it2.hasNext()) {
            parcelNewRequest.writeInt(it2.next().intValue());
        }
        return native_setMetadataFilter(parcelNewRequest);
    }

    public void release() {
        stayAwake(false);
        updateSurfaceScreenOn();
        this.mOnPreparedListener = null;
        this.mOnBufferingUpdateListener = null;
        this.mOnCompletionListener = null;
        this.mOnSeekCompleteListener = null;
        this.mOnErrorListener = null;
        this.mOnInfoListener = null;
        this.mOnVideoSizeChangedListener = null;
        this.mOnTimedTextListener = null;
        TimeProvider timeProvider = this.mTimeProvider;
        if (timeProvider != null) {
            timeProvider.close();
            this.mTimeProvider = null;
        }
        this.mOnSubtitleDataListener = null;
        _release();
        if (this.mIso != null) {
            Log.d(TAG, "release iso");
            this.mIso.clear();
        }
    }

    public void reset() {
        this.mSelectedSubtitleTrackIndex = -1;
        synchronized (this.mOpenSubtitleSources) {
            Iterator<InputStream> it = this.mOpenSubtitleSources.iterator();
            while (it.hasNext()) {
                try {
                    it.next().close();
                } catch (IOException unused) {
                }
            }
            this.mOpenSubtitleSources.clear();
        }
        this.mOutOfBandSubtitleTracks.clear();
        this.mInbandSubtitleTracks = new SubtitleTrack[0];
        SubtitleController subtitleController = this.mSubtitleController;
        if (subtitleController != null) {
            subtitleController.reset();
        }
        TimeProvider timeProvider = this.mTimeProvider;
        if (timeProvider != null) {
            timeProvider.close();
            this.mTimeProvider = null;
        }
        _reset();
        EventHandler eventHandler = this.mEventHandler;
        if (eventHandler != null) {
            eventHandler.removeCallbacksAndMessages(null);
        }
        disableProxyListener();
        stayAwake(false);
        Log.d(TAG, "MediaPlayer reset finished.");
    }

    public void setVolume(float f) {
        setVolume(f, f);
    }

    public static class TrackInfo implements Parcelable {
        static final Parcelable.Creator<TrackInfo> CREATOR = new Parcelable.Creator<TrackInfo>() { // from class: android.media.MediaPlayer.TrackInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public TrackInfo createFromParcel(Parcel parcel) {
                return new TrackInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public TrackInfo[] newArray(int i) {
                return new TrackInfo[i];
            }
        };
        public static final int MEDIA_TRACK_TYPE_AUDIO = 2;
        public static final int MEDIA_TRACK_TYPE_SUBTITLE = 4;
        public static final int MEDIA_TRACK_TYPE_TIMEDTEXT = 3;
        public static final int MEDIA_TRACK_TYPE_UNKNOWN = 0;
        public static final int MEDIA_TRACK_TYPE_VIDEO = 1;
        final MediaFormat mFormat;
        final int mTrackType;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public int getTrackType() {
            return this.mTrackType;
        }

        public String getLanguage() {
            String string = this.mFormat.getString("language");
            return string == null ? "und" : string;
        }

        public MediaFormat getFormat() {
            int i = this.mTrackType;
            if (i == 3 || i == 4 || i == 2 || i == 1) {
                return this.mFormat;
            }
            return null;
        }

        TrackInfo(Parcel parcel) {
            int i = parcel.readInt();
            this.mTrackType = i;
            String string = parcel.readString();
            if (i == 3) {
                MediaFormat mediaFormatCreateSubtitleFormat = MediaFormat.createSubtitleFormat(MediaPlayer.getSubtitleMimeType(parcel.readInt()), string);
                this.mFormat = mediaFormatCreateSubtitleFormat;
                mediaFormatCreateSubtitleFormat.setInteger("source-type", parcel.readInt());
                return;
            }
            if (i == 4) {
                MediaFormat mediaFormatCreateSubtitleFormat2 = MediaFormat.createSubtitleFormat(MediaPlayer.MEDIA_MIMETYPE_TEXT_VTT, string);
                this.mFormat = mediaFormatCreateSubtitleFormat2;
                mediaFormatCreateSubtitleFormat2.setInteger(MediaFormat.KEY_IS_AUTOSELECT, parcel.readInt());
                mediaFormatCreateSubtitleFormat2.setInteger(MediaFormat.KEY_IS_DEFAULT, parcel.readInt());
                mediaFormatCreateSubtitleFormat2.setInteger(MediaFormat.KEY_IS_FORCED_SUBTITLE, parcel.readInt());
                return;
            }
            if (i == 2) {
                int i2 = parcel.readInt();
                int i3 = parcel.readInt();
                int i4 = parcel.readInt();
                MediaFormat mediaFormatCreateAudioFormat = MediaFormat.createAudioFormat(MediaPlayer.getAudioMimeType(parcel.readInt()), i3, i2);
                this.mFormat = mediaFormatCreateAudioFormat;
                mediaFormatCreateAudioFormat.setString("language", string);
                mediaFormatCreateAudioFormat.setInteger(MediaFormat.KEY_BIT_RATE, i4);
                return;
            }
            if (i == 1) {
                MediaFormat mediaFormat = new MediaFormat();
                this.mFormat = mediaFormat;
                mediaFormat.setInteger("is-doubleStream", parcel.readInt());
                mediaFormat.setInteger("CodecFormat", parcel.readInt());
                return;
            }
            MediaFormat mediaFormat2 = new MediaFormat();
            this.mFormat = mediaFormat2;
            mediaFormat2.setString("language", string);
        }

        TrackInfo(int i, MediaFormat mediaFormat) {
            this.mTrackType = i;
            this.mFormat = mediaFormat;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mTrackType);
            parcel.writeString(getLanguage());
            if (this.mTrackType == 4) {
                parcel.writeInt(this.mFormat.getInteger(MediaFormat.KEY_IS_AUTOSELECT));
                parcel.writeInt(this.mFormat.getInteger(MediaFormat.KEY_IS_DEFAULT));
                parcel.writeInt(this.mFormat.getInteger(MediaFormat.KEY_IS_FORCED_SUBTITLE));
            }
        }
    }

    public TrackInfo[] getTrackInfo() throws IllegalStateException {
        TrackInfo[] inbandTrackInfo = getInbandTrackInfo();
        TrackInfo[] trackInfoArr = new TrackInfo[inbandTrackInfo.length + this.mOutOfBandSubtitleTracks.size()];
        System.arraycopy(inbandTrackInfo, 0, trackInfoArr, 0, inbandTrackInfo.length);
        int length = inbandTrackInfo.length;
        Iterator<SubtitleTrack> it = this.mOutOfBandSubtitleTracks.iterator();
        while (it.hasNext()) {
            trackInfoArr[length] = new TrackInfo(4, it.next().getFormat());
            length++;
        }
        return trackInfoArr;
    }

    private TrackInfo[] getInbandTrackInfo() throws IllegalStateException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            parcelObtain.writeInterfaceToken(IMEDIA_PLAYER);
            parcelObtain.writeInt(1);
            invoke(parcelObtain, parcelObtain2);
            return (TrackInfo[]) parcelObtain2.createTypedArray(TrackInfo.CREATOR);
        } finally {
            parcelObtain.recycle();
            parcelObtain2.recycle();
        }
    }

    private static boolean availableMimeTypeForExternalSource(String str) {
        return str.equals(new String(MEDIA_MIMETYPE_TEXT_SUBRIP)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_IDXSUB)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_SUB)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_SMI)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_RT)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_TXT)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_SSA)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_AQT)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_JSS)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_JS)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_ASS)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_VSF)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_TTS)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_STL)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_ZEG)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_OVR)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_DKS)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_LRC)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_PAN)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_SBT)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_VKT)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_PJS)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_MPL)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_SCR)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_PSB)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_ASC)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_RTF)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_S2K)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_SST)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_SON)) || str.equals(new String(MEDIA_MIMETYPE_TEXT_SSTS));
    }

    public void setSubtitleAnchor(SubtitleController subtitleController, SubtitleController.Anchor anchor) {
        this.mSubtitleController = subtitleController;
        subtitleController.setAnchor(anchor);
    }

    @Override // android.media.SubtitleController.Listener
    public void onSubtitleTrackSelected(SubtitleTrack subtitleTrack) {
        int i = this.mSelectedSubtitleTrackIndex;
        int i2 = 0;
        if (i >= 0) {
            try {
                selectOrDeselectInbandTrack(i, false);
            } catch (IllegalStateException unused) {
            }
            this.mSelectedSubtitleTrackIndex = -1;
        }
        setOnSubtitleDataListener(null);
        if (subtitleTrack == null) {
            return;
        }
        while (true) {
            SubtitleTrack[] subtitleTrackArr = this.mInbandSubtitleTracks;
            if (i2 >= subtitleTrackArr.length) {
                return;
            }
            if (subtitleTrackArr[i2] == subtitleTrack) {
                Log.v(TAG, "Selecting subtitle track " + i2);
                this.mSelectedSubtitleTrackIndex = i2;
                try {
                    selectOrDeselectInbandTrack(i2, true);
                } catch (IllegalStateException unused2) {
                }
                setOnSubtitleDataListener(this.mSubtitleDataListener);
                return;
            }
            i2++;
        }
    }

    public void addSubtitleSource(final InputStream inputStream, final MediaFormat mediaFormat) throws IllegalStateException {
        synchronized (this.mOpenSubtitleSources) {
            this.mOpenSubtitleSources.add(inputStream);
        }
        final HandlerThread handlerThread = new HandlerThread("SubtitleReadThread", 9);
        handlerThread.start();
        new Handler(handlerThread.getLooper()).post(new Runnable() { // from class: android.media.MediaPlayer.2
            private int addTrack() {
                SubtitleTrack subtitleTrackAddTrack;
                if (inputStream == null || MediaPlayer.this.mSubtitleController == null || (subtitleTrackAddTrack = MediaPlayer.this.mSubtitleController.addTrack(mediaFormat)) == null) {
                    return MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE;
                }
                Scanner scanner = new Scanner(inputStream, "UTF-8");
                String next = scanner.useDelimiter("\\A").next();
                synchronized (MediaPlayer.this.mOpenSubtitleSources) {
                    MediaPlayer.this.mOpenSubtitleSources.remove(inputStream);
                }
                scanner.close();
                MediaPlayer.this.mOutOfBandSubtitleTracks.add(subtitleTrackAddTrack);
                subtitleTrackAddTrack.onData(next, true, -1L);
                return MediaPlayer.MEDIA_INFO_EXTERNAL_METADATA_UPDATE;
            }

            @Override // java.lang.Runnable
            public void run() {
                int iAddTrack = addTrack();
                if (MediaPlayer.this.mEventHandler != null) {
                    MediaPlayer.this.mEventHandler.sendMessage(MediaPlayer.this.mEventHandler.obtainMessage(200, iAddTrack, 0, null));
                }
                handlerThread.getLooper().quitSafely();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scanInternalSubtitleTracks() {
        if (this.mSubtitleController == null) {
            Log.e(TAG, "Should have subtitle controller already set");
            return;
        }
        TrackInfo[] inbandTrackInfo = getInbandTrackInfo();
        SubtitleTrack[] subtitleTrackArr = new SubtitleTrack[inbandTrackInfo.length];
        for (int i = 0; i < inbandTrackInfo.length; i++) {
            if (inbandTrackInfo[i].getTrackType() == 4) {
                SubtitleTrack[] subtitleTrackArr2 = this.mInbandSubtitleTracks;
                if (i < subtitleTrackArr2.length) {
                    subtitleTrackArr[i] = subtitleTrackArr2[i];
                } else {
                    subtitleTrackArr[i] = this.mSubtitleController.addTrack(inbandTrackInfo[i].getFormat());
                }
            }
        }
        this.mInbandSubtitleTracks = subtitleTrackArr;
        this.mSubtitleController.selectDefaultTrack();
    }

    public void addTimedTextSource(String str, String str2) throws IllegalStateException, IOException, IllegalArgumentException {
        if (!availableMimeTypeForExternalSource(str2)) {
            throw new IllegalArgumentException("Illegal mimeType for timed text source: " + str2);
        }
        File file = new File(str);
        if (file.exists()) {
            FileInputStream fileInputStream = new FileInputStream(file);
            addTimedTextSource(fileInputStream.getFD(), str2);
            fileInputStream.close();
            return;
        }
        throw new IOException(str);
    }

    public void addTimedTextSource(Context context, Uri uri, String str) throws IllegalStateException, IOException, IllegalArgumentException {
        String scheme = uri.getScheme();
        if (scheme == null || scheme.equals("file")) {
            addTimedTextSource(uri.getPath(), str);
            return;
        }
        AutoCloseable autoCloseable = null;
        try {
            AssetFileDescriptor assetFileDescriptorOpenAssetFileDescriptor = context.getContentResolver().openAssetFileDescriptor(uri, FullBackup.ROOT_TREE_TOKEN);
            if (assetFileDescriptorOpenAssetFileDescriptor == null) {
                if (assetFileDescriptorOpenAssetFileDescriptor != null) {
                    assetFileDescriptorOpenAssetFileDescriptor.close();
                }
            } else {
                addTimedTextSource(assetFileDescriptorOpenAssetFileDescriptor.getFileDescriptor(), str);
                if (assetFileDescriptorOpenAssetFileDescriptor != null) {
                    assetFileDescriptorOpenAssetFileDescriptor.close();
                }
            }
        } catch (IOException unused) {
            if (0 == 0) {
                return;
            }
            autoCloseable.close();
        } catch (SecurityException unused2) {
            if (0 == 0) {
                return;
            }
            autoCloseable.close();
        } catch (Throwable th) {
            if (0 != 0) {
                autoCloseable.close();
            }
            throw th;
        }
    }

    public void addTimedTextSource(FileDescriptor fileDescriptor, String str) throws IllegalStateException, IllegalArgumentException {
        addTimedTextSource(fileDescriptor, 0L, 576460752303423487L, str);
    }

    public void addTimedTextSource(FileDescriptor fileDescriptor, long j, long j2, String str) throws IllegalStateException, IllegalArgumentException {
        if (!availableMimeTypeForExternalSource(str)) {
            throw new IllegalArgumentException("Illegal mimeType for timed text source: " + str);
        }
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            parcelObtain.writeInterfaceToken(IMEDIA_PLAYER);
            parcelObtain.writeInt(3);
            parcelObtain.writeFileDescriptor(fileDescriptor);
            parcelObtain.writeLong(j);
            parcelObtain.writeLong(j2);
            parcelObtain.writeString(str);
            invoke(parcelObtain, parcelObtain2);
        } finally {
            parcelObtain.recycle();
            parcelObtain2.recycle();
        }
    }

    public void selectTrack(int i) throws IllegalStateException {
        selectOrDeselectTrack(i, true);
    }

    public void deselectTrack(int i) throws IllegalStateException {
        selectOrDeselectTrack(i, false);
    }

    private void selectOrDeselectTrack(int i, boolean z) throws IllegalStateException {
        SubtitleTrack subtitleTrack;
        SubtitleTrack[] subtitleTrackArr = this.mInbandSubtitleTracks;
        if (i < subtitleTrackArr.length) {
            subtitleTrack = subtitleTrackArr[i];
        } else {
            subtitleTrack = i < subtitleTrackArr.length + this.mOutOfBandSubtitleTracks.size() ? this.mOutOfBandSubtitleTracks.get(i - this.mInbandSubtitleTracks.length) : null;
        }
        SubtitleController subtitleController = this.mSubtitleController;
        if (subtitleController == null || subtitleTrack == null) {
            selectOrDeselectInbandTrack(i, z);
            return;
        }
        if (z) {
            subtitleController.selectTrack(subtitleTrack);
        } else if (subtitleController.getSelectedTrack() == subtitleTrack) {
            this.mSubtitleController.selectTrack(null);
        } else {
            Log.w(TAG, "trying to deselect track that was not selected");
        }
    }

    private void selectOrDeselectInbandTrack(int i, boolean z) throws IllegalStateException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            parcelObtain.writeInterfaceToken(IMEDIA_PLAYER);
            parcelObtain.writeInt(z ? 4 : 5);
            parcelObtain.writeInt(i);
            invoke(parcelObtain, parcelObtain2);
        } finally {
            parcelObtain.recycle();
            parcelObtain2.recycle();
        }
    }

    public void setRetransmitEndpoint(InetSocketAddress inetSocketAddress) throws IllegalStateException, IllegalArgumentException {
        String hostAddress;
        int port;
        if (inetSocketAddress != null) {
            hostAddress = inetSocketAddress.getAddress().getHostAddress();
            port = inetSocketAddress.getPort();
        } else {
            hostAddress = null;
            port = 0;
        }
        int iNative_setRetransmitEndpoint = native_setRetransmitEndpoint(hostAddress, port);
        if (iNative_setRetransmitEndpoint != 0) {
            throw new IllegalArgumentException("Illegal re-transmit endpoint; native ret " + iNative_setRetransmitEndpoint);
        }
    }

    protected void finalize() {
        native_finalize();
    }

    public MediaTimeProvider getMediaTimeProvider() {
        if (this.mTimeProvider == null) {
            this.mTimeProvider = new TimeProvider(this);
        }
        return this.mTimeProvider;
    }

    private class EventHandler extends Handler {
        private MediaPlayer mMediaPlayer;

        public EventHandler(MediaPlayer mediaPlayer, Looper looper) {
            super(looper);
            this.mMediaPlayer = mediaPlayer;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (this.mMediaPlayer.mNativeContext == 0) {
                Log.w(MediaPlayer.TAG, "mediaplayer went away with unhandled events");
                return;
            }
            int i = message.what;
            if (i == 99) {
                if (MediaPlayer.this.mOnTimedTextListener == null) {
                    return;
                }
                if (message.obj == null) {
                    MediaPlayer.this.mOnTimedTextListener.onTimedText(this.mMediaPlayer, null);
                    return;
                } else {
                    if (message.obj instanceof Parcel) {
                        Parcel parcel = (Parcel) message.obj;
                        TimedText timedText = new TimedText(parcel);
                        parcel.recycle();
                        MediaPlayer.this.mOnTimedTextListener.onTimedText(this.mMediaPlayer, timedText);
                        return;
                    }
                    return;
                }
            }
            if (i == 100) {
                Log.e(MediaPlayer.TAG, "Error (" + message.arg1 + "," + message.arg2 + ")");
                boolean zOnError = MediaPlayer.this.mOnErrorListener != null ? MediaPlayer.this.mOnErrorListener.onError(this.mMediaPlayer, message.arg1, message.arg2) : false;
                if (MediaPlayer.this.mOnCompletionListener != null && !zOnError) {
                    MediaPlayer.this.mOnCompletionListener.onCompletion(this.mMediaPlayer);
                }
                MediaPlayer.this.stayAwake(false);
                return;
            }
            if (i == 200) {
                int i2 = message.arg1;
                if (i2 == 700) {
                    Log.i(MediaPlayer.TAG, "Info (" + message.arg1 + "," + message.arg2 + ")");
                } else {
                    if (i2 == 802) {
                        MediaPlayer.this.scanInternalSubtitleTracks();
                    } else if (i2 == 803) {
                    }
                    message.arg1 = MediaPlayer.MEDIA_INFO_METADATA_UPDATE;
                    MediaPlayer.this.mSubtitleController.selectDefaultTrack();
                }
                if (MediaPlayer.this.mOnInfoListener != null) {
                    MediaPlayer.this.mOnInfoListener.onInfo(this.mMediaPlayer, message.arg1, message.arg2);
                    return;
                }
                return;
            }
            if (i == 201) {
                if (MediaPlayer.this.mOnSubtitleDataListener != null && (message.obj instanceof Parcel)) {
                    Parcel parcel2 = (Parcel) message.obj;
                    SubtitleData subtitleData = new SubtitleData(parcel2);
                    parcel2.recycle();
                    MediaPlayer.this.mOnSubtitleDataListener.onSubtitleData(this.mMediaPlayer, subtitleData);
                    return;
                }
                return;
            }
            if (i != 234) {
                switch (i) {
                    case 0:
                        return;
                    case 1:
                        MediaPlayer.this.scanInternalSubtitleTracks();
                        if (MediaPlayer.this.mOnPreparedListener != null) {
                            MediaPlayer.this.mOnPreparedListener.onPrepared(this.mMediaPlayer);
                            return;
                        }
                        return;
                    case 2:
                        if (MediaPlayer.this.mOnCompletionListener != null) {
                            MediaPlayer.this.mOnCompletionListener.onCompletion(this.mMediaPlayer);
                        }
                        MediaPlayer.this.stayAwake(false);
                        return;
                    case 3:
                        if (MediaPlayer.this.mOnBufferingUpdateListener != null) {
                            MediaPlayer.this.mOnBufferingUpdateListener.onBufferingUpdate(this.mMediaPlayer, message.arg1);
                            return;
                        }
                        return;
                    case 4:
                        if (MediaPlayer.this.mOnSeekCompleteListener != null) {
                            MediaPlayer.this.mOnSeekCompleteListener.onSeekComplete(this.mMediaPlayer);
                        }
                        break;
                    case 5:
                        if (MediaPlayer.this.mOnVideoSizeChangedListener != null) {
                            MediaPlayer.this.mOnVideoSizeChangedListener.onVideoSizeChanged(this.mMediaPlayer, message.arg1, message.arg2);
                            return;
                        }
                        return;
                    case 6:
                    case 7:
                        if (MediaPlayer.this.mTimeProvider != null) {
                            MediaPlayer.this.mTimeProvider.onPaused(message.what == 7);
                            return;
                        }
                        return;
                    case 8:
                        if (MediaPlayer.this.mTimeProvider != null) {
                            MediaPlayer.this.mTimeProvider.onStopped();
                            return;
                        }
                        return;
                    case 9:
                        break;
                    default:
                        Log.e(MediaPlayer.TAG, "Unknown message type " + message.what);
                        return;
                }
                if (MediaPlayer.this.mTimeProvider != null) {
                    MediaPlayer.this.mTimeProvider.onSeekComplete(this.mMediaPlayer);
                    return;
                }
                return;
            }
            if (MediaPlayer.this.mDlnaSourceDetector == null || message.obj == null || !(message.obj instanceof Parcel)) {
                return;
            }
            String string = ((Parcel) message.obj).readString();
            Log.d(MediaPlayer.TAG, "######MEDIA_SOURCE_DETECTED! url = " + string);
            MediaPlayer.this.mDlnaSourceDetector.onSourceDetected(string);
        }
    }

    private static void postEventFromNative(Object obj, int i, int i2, int i3, Object obj2) {
        MediaPlayer mediaPlayer = (MediaPlayer) ((WeakReference) obj).get();
        if (mediaPlayer == null) {
            return;
        }
        if (i == 200 && i2 == 2) {
            mediaPlayer.start();
        }
        EventHandler eventHandler = mediaPlayer.mEventHandler;
        if (eventHandler != null) {
            mediaPlayer.mEventHandler.sendMessage(eventHandler.obtainMessage(i, i2, i3, obj2));
        }
    }

    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        this.mOnPreparedListener = onPreparedListener;
    }

    public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
        this.mOnCompletionListener = onCompletionListener;
    }

    public void setOnBufferingUpdateListener(OnBufferingUpdateListener onBufferingUpdateListener) {
        this.mOnBufferingUpdateListener = onBufferingUpdateListener;
    }

    public void setOnSeekCompleteListener(OnSeekCompleteListener onSeekCompleteListener) {
        this.mOnSeekCompleteListener = onSeekCompleteListener;
    }

    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener) {
        this.mOnVideoSizeChangedListener = onVideoSizeChangedListener;
    }

    public void setOnTimedTextListener(OnTimedTextListener onTimedTextListener) {
        this.mOnTimedTextListener = onTimedTextListener;
    }

    public void setOnSubtitleDataListener(OnSubtitleDataListener onSubtitleDataListener) {
        this.mOnSubtitleDataListener = onSubtitleDataListener;
    }

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.mOnErrorListener = onErrorListener;
    }

    public void setOnInfoListener(OnInfoListener onInfoListener) {
        this.mOnInfoListener = onInfoListener;
    }

    private void setupProxyListener(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Proxy.PROXY_CHANGE_ACTION);
        this.mProxyReceiver = new ProxyReceiver();
        this.mProxyContext = context;
        Intent intentRegisterReceiver = context.getApplicationContext().registerReceiver(this.mProxyReceiver, intentFilter);
        if (intentRegisterReceiver != null) {
            handleProxyBroadcast(intentRegisterReceiver);
        }
    }

    private void disableProxyListener() {
        if (this.mProxyReceiver == null) {
            return;
        }
        Context applicationContext = this.mProxyContext.getApplicationContext();
        if (applicationContext != null) {
            applicationContext.unregisterReceiver(this.mProxyReceiver);
        }
        this.mProxyReceiver = null;
        this.mProxyContext = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleProxyBroadcast(Intent intent) {
        ProxyProperties proxyProperties = (ProxyProperties) intent.getExtra(Proxy.EXTRA_PROXY_INFO);
        if (proxyProperties == null || proxyProperties.getHost() == null) {
            updateProxyConfig(null);
        } else {
            updateProxyConfig(proxyProperties);
        }
    }

    private class ProxyReceiver extends BroadcastReceiver {
        private ProxyReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Proxy.PROXY_CHANGE_ACTION)) {
                MediaPlayer.this.handleProxyBroadcast(intent);
            }
        }
    }

    static class TimeProvider implements OnSeekCompleteListener, MediaTimeProvider {
        private static final long MAX_EARLY_CALLBACK_US = 1000;
        private static final long MAX_NS_WITHOUT_POSITION_CHECK = 5000000000L;
        private static final int NOTIFY = 1;
        private static final int NOTIFY_SEEK = 3;
        private static final int NOTIFY_STOP = 2;
        private static final int NOTIFY_TIME = 0;
        private static final int REFRESH_AND_NOTIFY_TIME = 1;
        private static final String TAG = "MTP";
        private static final long TIME_ADJUSTMENT_RATE = 2;
        private Handler mEventHandler;
        private HandlerThread mHandlerThread;
        private long mLastNanoTime;
        private long mLastReportedTime;
        private long mLastTimeUs;
        private MediaTimeProvider.OnMediaTimeListener[] mListeners;
        private MediaPlayer mPlayer;
        private boolean mRefresh;
        private long mTimeAdjustment;
        private long[] mTimes;
        private boolean mPaused = true;
        private boolean mStopped = true;
        private boolean mPausing = false;
        private boolean mSeeking = false;
        public boolean DEBUG = false;

        public TimeProvider(MediaPlayer mediaPlayer) {
            this.mLastTimeUs = 0L;
            this.mRefresh = false;
            this.mPlayer = mediaPlayer;
            try {
                getCurrentTimeUs(true, false);
            } catch (IllegalStateException unused) {
                this.mRefresh = true;
            }
            Looper looperMyLooper = Looper.myLooper();
            if (looperMyLooper == null && (looperMyLooper = Looper.getMainLooper()) == null) {
                HandlerThread handlerThread = new HandlerThread("MediaPlayerMTPEventThread", -2);
                this.mHandlerThread = handlerThread;
                handlerThread.start();
                looperMyLooper = this.mHandlerThread.getLooper();
            }
            this.mEventHandler = new EventHandler(looperMyLooper);
            this.mListeners = new MediaTimeProvider.OnMediaTimeListener[0];
            this.mTimes = new long[0];
            this.mLastTimeUs = 0L;
            this.mTimeAdjustment = 0L;
        }

        private void scheduleNotification(int i, long j) {
            if (this.mSeeking && (i == 0 || i == 1)) {
                return;
            }
            if (this.DEBUG) {
                Log.v(TAG, "scheduleNotification " + i + " in " + j);
            }
            this.mEventHandler.removeMessages(1);
            this.mEventHandler.sendMessageDelayed(this.mEventHandler.obtainMessage(1, i, 0), (int) (j / 1000));
        }

        public void close() {
            this.mEventHandler.removeMessages(1);
            HandlerThread handlerThread = this.mHandlerThread;
            if (handlerThread != null) {
                handlerThread.quitSafely();
                this.mHandlerThread = null;
            }
        }

        protected void finalize() {
            HandlerThread handlerThread = this.mHandlerThread;
            if (handlerThread != null) {
                handlerThread.quitSafely();
            }
        }

        public void onPaused(boolean z) {
            synchronized (this) {
                if (this.DEBUG) {
                    Log.d(TAG, "onPaused: " + z);
                }
                if (this.mStopped) {
                    this.mStopped = false;
                    this.mSeeking = true;
                    scheduleNotification(3, 0L);
                } else {
                    this.mPausing = z;
                    this.mSeeking = false;
                    scheduleNotification(1, 0L);
                }
            }
        }

        public void onStopped() {
            synchronized (this) {
                if (this.DEBUG) {
                    Log.d(TAG, "onStopped");
                }
                this.mPaused = true;
                this.mStopped = true;
                this.mSeeking = false;
                scheduleNotification(2, 0L);
            }
        }

        @Override // android.media.MediaPlayer.OnSeekCompleteListener
        public void onSeekComplete(MediaPlayer mediaPlayer) {
            synchronized (this) {
                this.mStopped = false;
                this.mSeeking = true;
                scheduleNotification(3, 0L);
            }
        }

        public void onNewPlayer() {
            if (this.mRefresh) {
                synchronized (this) {
                    this.mStopped = false;
                    this.mSeeking = true;
                    scheduleNotification(3, 0L);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized void notifySeek() {
            this.mSeeking = false;
            try {
                long currentTimeUs = getCurrentTimeUs(true, false);
                if (this.DEBUG) {
                    Log.d(TAG, "onSeekComplete at " + currentTimeUs);
                }
                for (MediaTimeProvider.OnMediaTimeListener onMediaTimeListener : this.mListeners) {
                    if (onMediaTimeListener == null) {
                        break;
                    }
                    onMediaTimeListener.onSeek(currentTimeUs);
                }
            } catch (IllegalStateException unused) {
                if (this.DEBUG) {
                    Log.d(TAG, "onSeekComplete but no player");
                }
                this.mPausing = true;
                notifyTimedEvent(false);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized void notifyStop() {
            for (MediaTimeProvider.OnMediaTimeListener onMediaTimeListener : this.mListeners) {
                if (onMediaTimeListener == null) {
                    break;
                }
                onMediaTimeListener.onStop();
            }
        }

        private int registerListener(MediaTimeProvider.OnMediaTimeListener onMediaTimeListener) {
            MediaTimeProvider.OnMediaTimeListener[] onMediaTimeListenerArr;
            int i = 0;
            while (true) {
                onMediaTimeListenerArr = this.mListeners;
                if (i >= onMediaTimeListenerArr.length || onMediaTimeListenerArr[i] == onMediaTimeListener || onMediaTimeListenerArr[i] == null) {
                    break;
                }
                i++;
            }
            if (i >= onMediaTimeListenerArr.length) {
                int i2 = i + 1;
                MediaTimeProvider.OnMediaTimeListener[] onMediaTimeListenerArr2 = new MediaTimeProvider.OnMediaTimeListener[i2];
                long[] jArr = new long[i2];
                System.arraycopy(onMediaTimeListenerArr, 0, onMediaTimeListenerArr2, 0, onMediaTimeListenerArr.length);
                long[] jArr2 = this.mTimes;
                System.arraycopy(jArr2, 0, jArr, 0, jArr2.length);
                this.mListeners = onMediaTimeListenerArr2;
                this.mTimes = jArr;
            }
            MediaTimeProvider.OnMediaTimeListener[] onMediaTimeListenerArr3 = this.mListeners;
            if (onMediaTimeListenerArr3[i] == null) {
                onMediaTimeListenerArr3[i] = onMediaTimeListener;
                this.mTimes[i] = -1;
            }
            return i;
        }

        @Override // android.media.MediaTimeProvider
        public void notifyAt(long j, MediaTimeProvider.OnMediaTimeListener onMediaTimeListener) {
            synchronized (this) {
                if (this.DEBUG) {
                    Log.d(TAG, "notifyAt " + j);
                }
                this.mTimes[registerListener(onMediaTimeListener)] = j;
                scheduleNotification(0, 0L);
            }
        }

        @Override // android.media.MediaTimeProvider
        public void scheduleUpdate(MediaTimeProvider.OnMediaTimeListener onMediaTimeListener) {
            synchronized (this) {
                if (this.DEBUG) {
                    Log.d(TAG, "scheduleUpdate");
                }
                int iRegisterListener = registerListener(onMediaTimeListener);
                if (this.mStopped) {
                    scheduleNotification(2, 0L);
                } else {
                    this.mTimes[iRegisterListener] = 0;
                    scheduleNotification(0, 0L);
                }
            }
        }

        @Override // android.media.MediaTimeProvider
        public void cancelNotifications(MediaTimeProvider.OnMediaTimeListener onMediaTimeListener) {
            synchronized (this) {
                int i = 0;
                while (true) {
                    MediaTimeProvider.OnMediaTimeListener[] onMediaTimeListenerArr = this.mListeners;
                    if (i >= onMediaTimeListenerArr.length) {
                        break;
                    }
                    if (onMediaTimeListenerArr[i] == onMediaTimeListener) {
                        int i2 = i + 1;
                        System.arraycopy(onMediaTimeListenerArr, i2, onMediaTimeListenerArr, i, (onMediaTimeListenerArr.length - i) - 1);
                        long[] jArr = this.mTimes;
                        System.arraycopy(jArr, i2, jArr, i, (jArr.length - i) - 1);
                        this.mListeners[r5.length - 1] = null;
                        this.mTimes[r5.length - 1] = -1;
                        break;
                    }
                    if (onMediaTimeListenerArr[i] == null) {
                        break;
                    } else {
                        i++;
                    }
                }
                scheduleNotification(0, 0L);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized void notifyTimedEvent(boolean z) {
            long currentTimeUs;
            try {
                currentTimeUs = getCurrentTimeUs(z, true);
            } catch (IllegalStateException unused) {
                this.mRefresh = true;
                this.mPausing = true;
                currentTimeUs = getCurrentTimeUs(z, true);
            }
            if (this.mSeeking) {
                return;
            }
            if (this.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("notifyTimedEvent(").append(this.mLastTimeUs).append(" -> ").append(currentTimeUs).append(") from {");
                boolean z2 = true;
                for (long j : this.mTimes) {
                    if (j != -1) {
                        if (!z2) {
                            sb.append(", ");
                        }
                        sb.append(j);
                        z2 = false;
                    }
                }
                sb.append("}");
                Log.d(TAG, sb.toString());
            }
            Vector vector = new Vector();
            long j2 = currentTimeUs;
            int i = 0;
            while (true) {
                long[] jArr = this.mTimes;
                if (i >= jArr.length) {
                    break;
                }
                MediaTimeProvider.OnMediaTimeListener[] onMediaTimeListenerArr = this.mListeners;
                if (onMediaTimeListenerArr[i] == null) {
                    break;
                }
                if (jArr[i] > -1) {
                    if (jArr[i] <= 1000 + currentTimeUs) {
                        vector.add(onMediaTimeListenerArr[i]);
                        if (this.DEBUG) {
                            Log.d(TAG, Environment.MEDIA_REMOVED);
                        }
                        this.mTimes[i] = -1;
                    } else if (j2 == currentTimeUs || jArr[i] < j2) {
                        j2 = jArr[i];
                    }
                }
                i++;
            }
            if (j2 > currentTimeUs && !this.mPaused) {
                if (this.DEBUG) {
                    Log.d(TAG, "scheduling for " + j2 + " and " + currentTimeUs);
                }
                scheduleNotification(0, j2 - currentTimeUs);
            } else {
                this.mEventHandler.removeMessages(1);
            }
            Iterator it = vector.iterator();
            while (it.hasNext()) {
                ((MediaTimeProvider.OnMediaTimeListener) it.next()).onTimedEvent(currentTimeUs);
            }
        }

        private long getEstimatedTime(long j, boolean z) {
            if (this.mPaused) {
                this.mLastReportedTime = this.mLastTimeUs + this.mTimeAdjustment;
            } else {
                long j2 = (j - this.mLastNanoTime) / 1000;
                long j3 = this.mLastTimeUs + j2;
                this.mLastReportedTime = j3;
                long j4 = this.mTimeAdjustment;
                if (j4 > 0) {
                    long j5 = j4 - (j2 / 2);
                    if (j5 <= 0) {
                        this.mTimeAdjustment = 0L;
                    } else {
                        this.mLastReportedTime = j3 + j5;
                    }
                }
            }
            return this.mLastReportedTime;
        }

        /* JADX WARN: Removed duplicated region for block: B:33:0x0085 A[Catch: all -> 0x00bc, TryCatch #0 {, blocks: (B:3:0x0001, B:6:0x0007, B:7:0x0009, B:9:0x000b, B:11:0x0011, B:34:0x0087, B:35:0x008b, B:15:0x001f, B:19:0x0035, B:21:0x003b, B:25:0x004d, B:26:0x0064, B:28:0x006a, B:30:0x0072, B:32:0x007c, B:33:0x0085, B:38:0x008e, B:40:0x0092, B:42:0x009d, B:43:0x00b7, B:44:0x00b9, B:46:0x00bb), top: B:50:0x0001, inners: #1 }] */
        @Override // android.media.MediaTimeProvider
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public long getCurrentTimeUs(boolean r11, boolean r12) throws java.lang.IllegalStateException {
            /*
                r10 = this;
                monitor-enter(r10)
                boolean r0 = r10.mPaused     // Catch: java.lang.Throwable -> Lbc
                if (r0 == 0) goto Lb
                if (r11 != 0) goto Lb
                long r11 = r10.mLastReportedTime     // Catch: java.lang.Throwable -> Lbc
                monitor-exit(r10)     // Catch: java.lang.Throwable -> Lbc
                return r11
            Lb:
                long r0 = java.lang.System.nanoTime()     // Catch: java.lang.Throwable -> Lbc
                if (r11 != 0) goto L1d
                long r2 = r10.mLastNanoTime     // Catch: java.lang.Throwable -> Lbc
                r4 = 5000000000(0x12a05f200, double:2.470328229E-314)
                long r2 = r2 + r4
                int r11 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r11 < 0) goto L87
            L1d:
                r11 = 1
                r2 = 0
                android.media.MediaPlayer r3 = r10.mPlayer     // Catch: java.lang.IllegalStateException -> L8d java.lang.Throwable -> Lbc
                int r3 = r3.getCurrentPosition()     // Catch: java.lang.IllegalStateException -> L8d java.lang.Throwable -> Lbc
                int r3 = r3 * 1000
                long r3 = (long) r3     // Catch: java.lang.IllegalStateException -> L8d java.lang.Throwable -> Lbc
                r10.mLastTimeUs = r3     // Catch: java.lang.IllegalStateException -> L8d java.lang.Throwable -> Lbc
                android.media.MediaPlayer r3 = r10.mPlayer     // Catch: java.lang.IllegalStateException -> L8d java.lang.Throwable -> Lbc
                boolean r3 = r3.isPlaying()     // Catch: java.lang.IllegalStateException -> L8d java.lang.Throwable -> Lbc
                if (r3 != 0) goto L34
                r3 = r11
                goto L35
            L34:
                r3 = r2
            L35:
                r10.mPaused = r3     // Catch: java.lang.IllegalStateException -> L8d java.lang.Throwable -> Lbc
                boolean r3 = r10.DEBUG     // Catch: java.lang.IllegalStateException -> L8d java.lang.Throwable -> Lbc
                if (r3 == 0) goto L64
                java.lang.String r3 = "MTP"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.IllegalStateException -> L8d java.lang.Throwable -> Lbc
                r4.<init>()     // Catch: java.lang.IllegalStateException -> L8d java.lang.Throwable -> Lbc
                boolean r5 = r10.mPaused     // Catch: java.lang.IllegalStateException -> L8d java.lang.Throwable -> Lbc
                if (r5 == 0) goto L4a
                java.lang.String r5 = "paused"
                goto L4d
            L4a:
                java.lang.String r5 = "playing"
            L4d:
                java.lang.StringBuilder r4 = r4.append(r5)     // Catch: java.lang.IllegalStateException -> L8d java.lang.Throwable -> Lbc
                java.lang.String r5 = " at "
                java.lang.StringBuilder r4 = r4.append(r5)     // Catch: java.lang.IllegalStateException -> L8d java.lang.Throwable -> Lbc
                long r5 = r10.mLastTimeUs     // Catch: java.lang.IllegalStateException -> L8d java.lang.Throwable -> Lbc
                java.lang.StringBuilder r4 = r4.append(r5)     // Catch: java.lang.IllegalStateException -> L8d java.lang.Throwable -> Lbc
                java.lang.String r4 = r4.toString()     // Catch: java.lang.IllegalStateException -> L8d java.lang.Throwable -> Lbc
                android.util.Log.v(r3, r4)     // Catch: java.lang.IllegalStateException -> L8d java.lang.Throwable -> Lbc
            L64:
                r10.mLastNanoTime = r0     // Catch: java.lang.Throwable -> Lbc
                r3 = 0
                if (r12 == 0) goto L85
                long r5 = r10.mLastTimeUs     // Catch: java.lang.Throwable -> Lbc
                long r7 = r10.mLastReportedTime     // Catch: java.lang.Throwable -> Lbc
                int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
                if (r9 >= 0) goto L85
                long r7 = r7 - r5
                r10.mTimeAdjustment = r7     // Catch: java.lang.Throwable -> Lbc
                r5 = 1000000(0xf4240, double:4.940656E-318)
                int r5 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
                if (r5 <= 0) goto L87
                r10.mStopped = r2     // Catch: java.lang.Throwable -> Lbc
                r10.mSeeking = r11     // Catch: java.lang.Throwable -> Lbc
                r11 = 3
                r10.scheduleNotification(r11, r3)     // Catch: java.lang.Throwable -> Lbc
                goto L87
            L85:
                r10.mTimeAdjustment = r3     // Catch: java.lang.Throwable -> Lbc
            L87:
                long r11 = r10.getEstimatedTime(r0, r12)     // Catch: java.lang.Throwable -> Lbc
                monitor-exit(r10)     // Catch: java.lang.Throwable -> Lbc
                return r11
            L8d:
                r3 = move-exception
                boolean r4 = r10.mPausing     // Catch: java.lang.Throwable -> Lbc
                if (r4 == 0) goto Lbb
                r10.mPausing = r2     // Catch: java.lang.Throwable -> Lbc
                r10.getEstimatedTime(r0, r12)     // Catch: java.lang.Throwable -> Lbc
                r10.mPaused = r11     // Catch: java.lang.Throwable -> Lbc
                boolean r11 = r10.DEBUG     // Catch: java.lang.Throwable -> Lbc
                if (r11 == 0) goto Lb7
                java.lang.String r11 = "MTP"
                java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lbc
                r12.<init>()     // Catch: java.lang.Throwable -> Lbc
                java.lang.String r0 = "illegal state, but pausing: estimating at "
                java.lang.StringBuilder r12 = r12.append(r0)     // Catch: java.lang.Throwable -> Lbc
                long r0 = r10.mLastReportedTime     // Catch: java.lang.Throwable -> Lbc
                java.lang.StringBuilder r12 = r12.append(r0)     // Catch: java.lang.Throwable -> Lbc
                java.lang.String r12 = r12.toString()     // Catch: java.lang.Throwable -> Lbc
                android.util.Log.d(r11, r12)     // Catch: java.lang.Throwable -> Lbc
            Lb7:
                long r11 = r10.mLastReportedTime     // Catch: java.lang.Throwable -> Lbc
                monitor-exit(r10)     // Catch: java.lang.Throwable -> Lbc
                return r11
            Lbb:
                throw r3     // Catch: java.lang.Throwable -> Lbc
            Lbc:
                r11 = move-exception
                monitor-exit(r10)     // Catch: java.lang.Throwable -> Lbc
                throw r11
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.MediaPlayer.TimeProvider.getCurrentTimeUs(boolean, boolean):long");
        }

        private class EventHandler extends Handler {
            public EventHandler(Looper looper) {
                super(looper);
            }

            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what == 1) {
                    int i = message.arg1;
                    if (i == 0) {
                        TimeProvider.this.notifyTimedEvent(false);
                        return;
                    }
                    if (i == 1) {
                        TimeProvider.this.notifyTimedEvent(true);
                    } else if (i == 2) {
                        TimeProvider.this.notifyStop();
                    } else {
                        if (i != 3) {
                            return;
                        }
                        TimeProvider.this.notifySeek();
                    }
                }
            }
        }
    }

    public static class SubInfo {
        public String charset;
        public byte[] name;
        public int type;

        public SubInfo(byte[] bArr, String str, int i) {
            this.name = bArr;
            this.charset = str;
            this.type = i;
        }
    }

    public static class TrackInfoVendor {
        public String charset;
        public byte[] name;

        public TrackInfoVendor(byte[] bArr, String str) {
            this.name = bArr;
            this.charset = str;
        }
    }

    public void setDlnaSourceDetector(DlnaSourceDetector dlnaSourceDetector) {
        this.mDlnaSourceDetector = dlnaSourceDetector;
        try {
            setDataSource(DLNA_SOURCE_DETECTOR);
        } catch (Exception e) {
            Log.e(TAG, "Fail to set DlnaSourceDetector..");
            e.printStackTrace();
        }
    }

    public static int setRawDataMode(int i) {
        mRawDataMode = i;
        return _setRawDataMode(i);
    }

    public static int getRawDataMode() {
        return mRawDataMode;
    }

    public class ISOManager {
        private String TAG = ISOManager.class.getSimpleName();
        private ArrayList<ISOMountManager.MountInfo> cdromList;
        private File cdromRoot;
        private Context mContext;

        public ISOManager(Context context) {
            this.cdromRoot = null;
            this.cdromList = null;
            this.mContext = context;
            this.cdromRoot = context.getDir("CDROM", 0);
            this.cdromList = new ArrayList<>();
        }

        public String getVirtualCDRomPath(String str) {
            for (ISOMountManager.MountInfo mountInfo : this.cdromList) {
                if (mountInfo.getISOPath().equals(str)) {
                    return mountInfo.getMountPath();
                }
            }
            String strCreateVirtualCDRomPathIfNeed = createVirtualCDRomPathIfNeed(str);
            ISOMountManager.umount(strCreateVirtualCDRomPathIfNeed);
            if (ISOMountManager.mount(strCreateVirtualCDRomPathIfNeed, str) != 0) {
                return null;
            }
            this.cdromList.add(new ISOMountManager.MountInfo(strCreateVirtualCDRomPathIfNeed, str));
            return strCreateVirtualCDRomPathIfNeed;
        }

        private String createVirtualCDRomPathIfNeed(String str) {
            File file = new File(this.cdromRoot, new File(str).getName());
            Log.d(this.TAG, "createVirtualCDRomPathIfNeed()  cdromPath is " + file.getAbsolutePath());
            if (!file.exists()) {
                try {
                    file.mkdir();
                } catch (Exception unused) {
                    Log.e(this.TAG, "createVirtualCDRomPathIfNeed()  create path fail!");
                    return null;
                }
            }
            return file.getAbsolutePath();
        }

        public boolean isVirtualCDRom(String str) {
            Iterator<ISOMountManager.MountInfo> it = this.cdromList.iterator();
            while (it.hasNext()) {
                if (it.next().getMountPath().equals(str)) {
                    return true;
                }
            }
            return false;
        }

        public void clear() {
            Iterator<ISOMountManager.MountInfo> it = this.cdromList.iterator();
            while (it.hasNext()) {
                ISOMountManager.umount(it.next().getMountPath());
            }
            this.cdromList.clear();
        }

        public String getIsoFile(String str) {
            for (ISOMountManager.MountInfo mountInfo : this.cdromList) {
                if (mountInfo.getMountPath().equals(str)) {
                    return mountInfo.getISOPath();
                }
            }
            return null;
        }

        private class IsoMountInfo {
            public String isoFilePath;
            public String mountPointPath;

            private IsoMountInfo() {
            }
        }
    }

    public boolean setParameter(int i, String str) {
        Parcel parcelObtain = Parcel.obtain();
        parcelObtain.writeString(str);
        boolean parameter = setParameter(i, parcelObtain);
        parcelObtain.recycle();
        return parameter;
    }

    public boolean setParameter(int i, int i2) {
        Parcel parcelObtain = Parcel.obtain();
        parcelObtain.writeInt(i2);
        boolean parameter = setParameter(i, parcelObtain);
        parcelObtain.recycle();
        return parameter;
    }

    public Parcel getParcelParameter(int i) {
        Parcel parcelObtain = Parcel.obtain();
        getParameter(i, parcelObtain);
        return parcelObtain;
    }

    public String getStringParameter(int i) {
        Parcel parcelObtain = Parcel.obtain();
        getParameter(i, parcelObtain);
        String string = parcelObtain.readString();
        parcelObtain.recycle();
        return string;
    }

    private void searchSubTitle(String str) {
        if (str == null || str.length() == 0) {
            return;
        }
        int iLastIndexOf = str.lastIndexOf(".");
        int iLastIndexOf2 = str.lastIndexOf("/");
        if (iLastIndexOf2 <= 0 || iLastIndexOf <= 0 || iLastIndexOf <= iLastIndexOf2) {
            return;
        }
        int i = 0;
        String strSubstring = str.substring(0, iLastIndexOf2);
        String strSubstring2 = str.substring(iLastIndexOf2 + 1, iLastIndexOf);
        File file = new File(strSubstring);
        if (file.isDirectory()) {
            File[] fileArrListFiles = file.listFiles();
            int length = fileArrListFiles.length;
            int i2 = 0;
            while (i2 < length) {
                File file2 = fileArrListFiles[i2];
                if (file2.exists()) {
                    String name = file2.getName();
                    int iIndexOf = name.indexOf(".");
                    int iLastIndexOf3 = name.lastIndexOf(".");
                    int length2 = name.length();
                    if (iIndexOf > 0 && iLastIndexOf3 > 0 && length2 > 0) {
                        while (iIndexOf > 0) {
                            String strSubstring3 = name.substring(i, iIndexOf);
                            String strSubstring4 = name.substring(iLastIndexOf3, length2);
                            if (strSubstring3.equals(strSubstring2)) {
                                int i3 = i;
                                while (true) {
                                    String[] strArr = SUB_EXTS;
                                    if (i3 < strArr.length) {
                                        if (strSubstring4.toLowerCase().equals(strArr[i3])) {
                                            this.mSrtList.add(strSubstring + "/" + name);
                                            this.mMediaTypeList.add(MEDIA_MIMETYPE[i3]);
                                            Log.d("fuqiang", "get it! filename = " + strSubstring + "/" + name);
                                        }
                                        i3++;
                                    }
                                }
                            }
                            iIndexOf = name.indexOf(".", iIndexOf + 1);
                            i = 0;
                        }
                    }
                }
                i2++;
                i = 0;
            }
        }
    }
}
