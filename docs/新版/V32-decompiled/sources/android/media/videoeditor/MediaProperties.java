package android.media.videoeditor;

import android.util.Pair;

/* JADX INFO: loaded from: classes.dex */
public class MediaProperties {
    public static final int ACODEC_AAC_LC = 2;
    public static final int ACODEC_AAC_PLUS = 3;
    public static final int ACODEC_AMRNB = 1;
    public static final int ACODEC_AMRWB = 8;
    public static final int ACODEC_ENHANCED_AAC_PLUS = 4;
    public static final int ACODEC_EVRC = 6;
    public static final int ACODEC_MP3 = 5;
    public static final int ACODEC_NO_AUDIO = 0;
    public static final int ACODEC_OGG = 9;
    private static final int[] ASPECT_RATIOS = {1, 2, 3, 4, 5};
    public static final int ASPECT_RATIO_11_9 = 5;
    private static final Pair<Integer, Integer>[] ASPECT_RATIO_11_9_RESOLUTIONS;
    public static final int ASPECT_RATIO_16_9 = 2;
    private static final Pair<Integer, Integer>[] ASPECT_RATIO_16_9_RESOLUTIONS;
    public static final int ASPECT_RATIO_3_2 = 1;
    private static final Pair<Integer, Integer>[] ASPECT_RATIO_3_2_RESOLUTIONS;
    public static final int ASPECT_RATIO_4_3 = 3;
    private static final Pair<Integer, Integer>[] ASPECT_RATIO_4_3_RESOLUTIONS;
    public static final int ASPECT_RATIO_5_3 = 4;
    private static final Pair<Integer, Integer>[] ASPECT_RATIO_5_3_RESOLUTIONS;
    public static final int ASPECT_RATIO_UNDEFINED = 0;
    public static final int AUDIO_MAX_TRACK_COUNT = 1;
    public static final int AUDIO_MAX_VOLUME_PERCENT = 100;
    public static final int BITRATE_128K = 128000;
    public static final int BITRATE_192K = 192000;
    public static final int BITRATE_256K = 256000;
    public static final int BITRATE_28K = 28000;
    public static final int BITRATE_2M = 2000000;
    public static final int BITRATE_384K = 384000;
    public static final int BITRATE_40K = 40000;
    public static final int BITRATE_512K = 512000;
    public static final int BITRATE_5M = 5000000;
    public static final int BITRATE_64K = 64000;
    public static final int BITRATE_800K = 800000;
    public static final int BITRATE_8M = 8000000;
    public static final int BITRATE_96K = 96000;
    public static final int DEFAULT_CHANNEL_COUNT = 2;
    public static final int DEFAULT_SAMPLING_FREQUENCY = 32000;
    public static final int FILE_3GP = 0;
    public static final int FILE_AMR = 2;
    public static final int FILE_JPEG = 5;
    public static final int FILE_M4V = 10;
    public static final int FILE_MP3 = 3;
    public static final int FILE_MP4 = 1;
    public static final int FILE_PNG = 8;
    public static final int FILE_UNSUPPORTED = 255;
    public static final int HEIGHT_1080 = 1080;
    public static final int HEIGHT_144 = 144;
    public static final int HEIGHT_288 = 288;
    public static final int HEIGHT_360 = 360;
    public static final int HEIGHT_480 = 480;
    public static final int HEIGHT_720 = 720;
    public static final int SAMPLES_PER_FRAME_AAC = 1024;
    public static final int SAMPLES_PER_FRAME_AMRNB = 160;
    public static final int SAMPLES_PER_FRAME_AMRWB = 320;
    public static final int SAMPLES_PER_FRAME_MP3 = 1152;
    private static final int[] SUPPORTED_ACODECS;
    private static final int[] SUPPORTED_BITRATES;
    private static final int[] SUPPORTED_VCODECS;
    private static final int[] SUPPORTED_VIDEO_FILE_FORMATS;
    public static final int UNDEFINED_VIDEO_PROFILE = 255;
    public static final int VCODEC_H263 = 1;
    public static final int VCODEC_H264 = 2;
    public static final int VCODEC_MPEG4 = 3;

    public static int getSupportedAudioTrackCount() {
        return 1;
    }

    public static int getSupportedMaxVolume() {
        return 100;
    }

    static {
        Integer numValueOf = Integer.valueOf(HEIGHT_720);
        ASPECT_RATIO_3_2_RESOLUTIONS = new Pair[]{new Pair<>(numValueOf, 480), new Pair<>(1080, numValueOf)};
        ASPECT_RATIO_4_3_RESOLUTIONS = new Pair[]{new Pair<>(640, 480), new Pair<>(960, numValueOf)};
        ASPECT_RATIO_5_3_RESOLUTIONS = new Pair[]{new Pair<>(800, 480)};
        ASPECT_RATIO_11_9_RESOLUTIONS = new Pair[]{new Pair<>(176, 144), new Pair<>(352, Integer.valueOf(HEIGHT_288))};
        ASPECT_RATIO_16_9_RESOLUTIONS = new Pair[]{new Pair<>(848, 480), new Pair<>(1280, numValueOf), new Pair<>(1920, 1080)};
        SUPPORTED_BITRATES = new int[]{BITRATE_28K, BITRATE_40K, 64000, 96000, 128000, 192000, 256000, 384000, 512000, 800000, 2000000, 5000000, 8000000};
        SUPPORTED_VCODECS = new int[]{2, 1, 3};
        SUPPORTED_ACODECS = new int[]{2, 1, 8};
        SUPPORTED_VIDEO_FILE_FORMATS = new int[]{0, 1, 10};
    }

    public final class H264Profile {
        public static final int H264ProfileBaseline = 1;
        public static final int H264ProfileExtended = 4;
        public static final int H264ProfileHigh = 8;
        public static final int H264ProfileHigh10 = 16;
        public static final int H264ProfileHigh422 = 32;
        public static final int H264ProfileHigh444 = 64;
        public static final int H264ProfileMain = 2;
        public static final int H264ProfileUnknown = Integer.MAX_VALUE;

        public H264Profile() {
        }
    }

    public final class H264Level {
        public static final int H264Level1 = 1;
        public static final int H264Level11 = 4;
        public static final int H264Level12 = 8;
        public static final int H264Level13 = 16;
        public static final int H264Level1b = 2;
        public static final int H264Level2 = 32;
        public static final int H264Level21 = 64;
        public static final int H264Level22 = 128;
        public static final int H264Level3 = 256;
        public static final int H264Level31 = 512;
        public static final int H264Level32 = 1024;
        public static final int H264Level4 = 2048;
        public static final int H264Level41 = 4096;
        public static final int H264Level42 = 8192;
        public static final int H264Level5 = 16384;
        public static final int H264Level51 = 32768;
        public static final int H264LevelUnknown = Integer.MAX_VALUE;

        public H264Level() {
        }
    }

    public final class H263Profile {
        public static final int H263ProfileBackwardCompatible = 4;
        public static final int H263ProfileBaseline = 1;
        public static final int H263ProfileH320Coding = 2;
        public static final int H263ProfileHighCompression = 32;
        public static final int H263ProfileHighLatency = 256;
        public static final int H263ProfileISWV2 = 8;
        public static final int H263ProfileISWV3 = 16;
        public static final int H263ProfileInterlace = 128;
        public static final int H263ProfileInternet = 64;
        public static final int H263ProfileUnknown = Integer.MAX_VALUE;

        public H263Profile() {
        }
    }

    public final class H263Level {
        public static final int H263Level10 = 1;
        public static final int H263Level20 = 2;
        public static final int H263Level30 = 4;
        public static final int H263Level40 = 8;
        public static final int H263Level45 = 16;
        public static final int H263Level50 = 32;
        public static final int H263Level60 = 64;
        public static final int H263Level70 = 128;
        public static final int H263LevelUnknown = Integer.MAX_VALUE;

        public H263Level() {
        }
    }

    public final class MPEG4Profile {
        public static final int MPEG4ProfileAdvancedCoding = 4096;
        public static final int MPEG4ProfileAdvancedCore = 8192;
        public static final int MPEG4ProfileAdvancedRealTime = 1024;
        public static final int MPEG4ProfileAdvancedScalable = 16384;
        public static final int MPEG4ProfileAdvancedSimple = 32768;
        public static final int MPEG4ProfileBasicAnimated = 256;
        public static final int MPEG4ProfileCore = 4;
        public static final int MPEG4ProfileCoreScalable = 2048;
        public static final int MPEG4ProfileHybrid = 512;
        public static final int MPEG4ProfileMain = 8;
        public static final int MPEG4ProfileNbit = 16;
        public static final int MPEG4ProfileScalableTexture = 32;
        public static final int MPEG4ProfileSimple = 1;
        public static final int MPEG4ProfileSimpleFBA = 128;
        public static final int MPEG4ProfileSimpleFace = 64;
        public static final int MPEG4ProfileSimpleScalable = 2;
        public static final int MPEG4ProfileUnknown = Integer.MAX_VALUE;

        public MPEG4Profile() {
        }
    }

    public final class MPEG4Level {
        public static final int MPEG4Level0 = 1;
        public static final int MPEG4Level0b = 2;
        public static final int MPEG4Level1 = 4;
        public static final int MPEG4Level2 = 8;
        public static final int MPEG4Level3 = 16;
        public static final int MPEG4Level4 = 32;
        public static final int MPEG4Level4a = 64;
        public static final int MPEG4Level5 = 128;
        public static final int MPEG4LevelUnknown = Integer.MAX_VALUE;

        public MPEG4Level() {
        }
    }

    private MediaProperties() {
    }

    public static int[] getAllSupportedAspectRatios() {
        return ASPECT_RATIOS;
    }

    public static Pair<Integer, Integer>[] getSupportedResolutions(int i) {
        Pair<Integer, Integer>[] pairArr;
        if (i == 1) {
            pairArr = ASPECT_RATIO_3_2_RESOLUTIONS;
        } else if (i == 2) {
            pairArr = ASPECT_RATIO_16_9_RESOLUTIONS;
        } else if (i == 3) {
            pairArr = ASPECT_RATIO_4_3_RESOLUTIONS;
        } else if (i == 4) {
            pairArr = ASPECT_RATIO_5_3_RESOLUTIONS;
        } else if (i == 5) {
            pairArr = ASPECT_RATIO_11_9_RESOLUTIONS;
        } else {
            throw new IllegalArgumentException("Unknown aspect ratio: " + i);
        }
        VideoEditorProfile videoEditorProfile = VideoEditorProfile.get();
        if (videoEditorProfile == null) {
            throw new RuntimeException("Can't get the video editor profile");
        }
        int i2 = videoEditorProfile.maxOutputVideoFrameWidth;
        int i3 = videoEditorProfile.maxOutputVideoFrameHeight;
        Pair[] pairArr2 = new Pair[pairArr.length];
        int i4 = 0;
        for (int i5 = 0; i5 < pairArr.length; i5++) {
            if (pairArr[i5].first.intValue() <= i2 && pairArr[i5].second.intValue() <= i3) {
                pairArr2[i4] = pairArr[i5];
                i4++;
            }
        }
        Pair<Integer, Integer>[] pairArr3 = new Pair[i4];
        System.arraycopy(pairArr2, 0, pairArr3, 0, i4);
        return pairArr3;
    }

    public static int[] getSupportedVideoCodecs() {
        return SUPPORTED_VCODECS;
    }

    public static int[] getSupportedAudioCodecs() {
        return SUPPORTED_ACODECS;
    }

    public static int[] getSupportedVideoFileFormat() {
        return SUPPORTED_VIDEO_FILE_FORMATS;
    }

    public static int[] getSupportedVideoBitrates() {
        return SUPPORTED_BITRATES;
    }
}
