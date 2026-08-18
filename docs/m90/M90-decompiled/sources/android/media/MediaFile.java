package android.media;

import android.content.ClipDescription;
import android.media.DecoderCapabilities;
import android.mtp.MtpConstants;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class MediaFile {
    public static final int FILE_TYPE_3GPP = 23;
    public static final int FILE_TYPE_3GPP2 = 24;
    public static final int FILE_TYPE_AAC = 8;
    public static final int FILE_TYPE_AMR = 4;
    public static final int FILE_TYPE_ASF = 26;
    public static final int FILE_TYPE_AVI = 29;
    public static final int FILE_TYPE_AWB = 5;
    public static final int FILE_TYPE_BMP = 34;
    private static final int FILE_TYPE_CEDARA = 300;
    public static final int FILE_TYPE_CEDARV = 201;
    public static final int FILE_TYPE_FL = 51;
    public static final int FILE_TYPE_FLAC = 10;
    public static final int FILE_TYPE_GIF = 32;
    public static final int FILE_TYPE_HTML = 101;
    public static final int FILE_TYPE_HTTPLIVE = 44;
    public static final int FILE_TYPE_IMY = 13;
    public static final int FILE_TYPE_JPEG = 31;
    public static final int FILE_TYPE_M3U = 41;
    public static final int FILE_TYPE_M4A = 2;
    public static final int FILE_TYPE_M4V = 22;
    public static final int FILE_TYPE_MID = 11;
    public static final int FILE_TYPE_MKA = 9;
    public static final int FILE_TYPE_MKV = 27;
    public static final int FILE_TYPE_MP2PS = 200;
    public static final int FILE_TYPE_MP2TS = 28;
    public static final int FILE_TYPE_MP3 = 1;
    public static final int FILE_TYPE_MP4 = 21;
    public static final int FILE_TYPE_MS_EXCEL = 105;
    public static final int FILE_TYPE_MS_POWERPOINT = 106;
    public static final int FILE_TYPE_MS_WORD = 104;
    public static final int FILE_TYPE_OGG = 7;
    public static final int FILE_TYPE_PDF = 102;
    public static final int FILE_TYPE_PLS = 42;
    public static final int FILE_TYPE_PNG = 33;
    public static final int FILE_TYPE_SMF = 12;
    public static final int FILE_TYPE_TEXT = 100;
    public static final int FILE_TYPE_WAV = 3;
    public static final int FILE_TYPE_WBMP = 35;
    public static final int FILE_TYPE_WEBM = 30;
    public static final int FILE_TYPE_WEBP = 36;
    public static final int FILE_TYPE_WMA = 6;
    public static final int FILE_TYPE_WMV = 25;
    public static final int FILE_TYPE_WPL = 43;
    public static final int FILE_TYPE_XML = 103;
    public static final int FILE_TYPE_ZIP = 107;
    private static final int FIRST_AUDIO_FILE_TYPE = 1;
    private static final int FIRST_DRM_FILE_TYPE = 51;
    private static final int FIRST_IMAGE_FILE_TYPE = 31;
    private static final int FIRST_MIDI_FILE_TYPE = 11;
    private static final int FIRST_PLAYLIST_FILE_TYPE = 41;
    private static final int FIRST_VIDEO_FILE_TYPE = 21;
    private static final int FIRST_VIDEO_FILE_TYPE2 = 200;
    private static final int LAST_AUDIO_FILE_TYPE = 10;
    private static final int LAST_DRM_FILE_TYPE = 51;
    private static final int LAST_IMAGE_FILE_TYPE = 36;
    private static final int LAST_MIDI_FILE_TYPE = 13;
    private static final int LAST_PLAYLIST_FILE_TYPE = 44;
    private static final int LAST_VIDEO_FILE_TYPE = 30;
    private static final int LAST_VIDEO_FILE_TYPE2 = 201;
    private static final HashMap<String, MediaFileType> sFileTypeMap = new HashMap<>();
    private static final HashMap<String, Integer> sMimeTypeMap = new HashMap<>();
    private static final HashMap<String, Integer> sFileTypeToFormatMap = new HashMap<>();
    private static final HashMap<String, Integer> sMimeTypeToFormatMap = new HashMap<>();
    private static final HashMap<Integer, String> sFormatToMimeTypeMap = new HashMap<>();

    public static boolean isAudioFileType(int i) {
        if (i < 1 || i > 10) {
            return (i >= 11 && i <= 13) || i == 300;
        }
        return true;
    }

    public static boolean isDrmFileType(int i) {
        return i >= 51 && i <= 51;
    }

    public static boolean isImageFileType(int i) {
        return i >= 31 && i <= 36;
    }

    public static boolean isPlayListFileType(int i) {
        return i >= 41 && i <= 44;
    }

    public static boolean isVideoFileType(int i) {
        return (i >= 21 && i <= 30) || (i >= 200 && i <= 201);
    }

    public static class MediaFileType {
        public final int fileType;
        public final String mimeType;

        MediaFileType(int i, String str) {
            this.fileType = i;
            this.mimeType = str;
        }
    }

    static {
        addFileType("MP3", 1, "audio/mpeg", 12297);
        addFileType("MPGA", 1, "audio/mpeg", 12297);
        addFileType("M4A", 2, "audio/mp4", 12299);
        addFileType("WAV", 3, "audio/x-wav", 12296);
        addFileType("AMR", 4, "audio/amr");
        addFileType("AWB", 5, "audio/amr-wb");
        addFileType("WMA", 6, "audio/x-ms-wma", MtpConstants.FORMAT_WMA);
        addFileType("OGG", 7, "audio/ogg", MtpConstants.FORMAT_OGG);
        addFileType("OGG", 7, "application/ogg", MtpConstants.FORMAT_OGG);
        addFileType("OGA", 7, "application/ogg", MtpConstants.FORMAT_OGG);
        addFileType("AAC", 8, "audio/aac", MtpConstants.FORMAT_AAC);
        addFileType("AAC", 8, "audio/aac-adts", MtpConstants.FORMAT_AAC);
        addFileType("MKA", 9, "audio/x-matroska");
        addFileType("APE", 300, "audio/cedara");
        addFileType("AC3", 300, "audio/cedara");
        addFileType("DTS", 300, "audio/cedara");
        addFileType("OMG", 300, "audio/cedara");
        addFileType("M4R", 300, "audio/cedara");
        addFileType("MP1", 300, "audio/cedara");
        addFileType("MP2", 300, "audio/cedara");
        addFileType("MID", 11, "audio/midi");
        addFileType("MIDI", 11, "audio/midi");
        addFileType("XMF", 11, "audio/midi");
        addFileType("RTTTL", 11, "audio/midi");
        addFileType("SMF", 12, "audio/sp-midi");
        addFileType("IMY", 13, "audio/imelody");
        addFileType("RTX", 11, "audio/midi");
        addFileType("OTA", 11, "audio/midi");
        addFileType("MXMF", 11, "audio/midi");
        addFileType("MPEG", 21, "video/mpeg", 12299);
        addFileType("MPG", 21, "video/mpeg", 12299);
        addFileType("MP4", 21, "video/mp4", 12299);
        addFileType("M4V", 22, "video/mp4", 12299);
        addFileType("3GP", 23, "video/3gpp", MtpConstants.FORMAT_3GP_CONTAINER);
        addFileType("3GPP", 23, "video/3gpp", MtpConstants.FORMAT_3GP_CONTAINER);
        addFileType("3G2", 24, "video/3gpp2", MtpConstants.FORMAT_3GP_CONTAINER);
        addFileType("3GPP2", 24, "video/3gpp2", MtpConstants.FORMAT_3GP_CONTAINER);
        addFileType("MKV", 27, "video/x-matroska");
        addFileType("WEBM", 30, "video/webm");
        addFileType("TS", 28, "video/mp2ts");
        addFileType("TP", 28, "video/mp2ts");
        addFileType("M2TS", 28, "video/mp2ts");
        addFileType("AVI", 29, "video/avi");
        addFileType("RMVB", 201, "video/cedarx");
        addFileType("RM", 201, "video/cedarx");
        addFileType("AVI", 201, "video/cedarx");
        addFileType("MOV", 201, "video/cedarx");
        addFileType("FLV", 201, "video/cedarx");
        addFileType("F4V", 201, "video/cedarx");
        addFileType("VOB", 201, "video/cedarx");
        addFileType("PMP", 201, "video/cedarx");
        addFileType("3DM", 201, "video/cedarx");
        addFileType("3DV", 201, "video/cedarx");
        addFileType("WMV", 25, "video/x-ms-wmv", MtpConstants.FORMAT_WMV);
        addFileType("ASF", 26, "video/x-ms-asf");
        addFileType("JPG", 31, "image/jpeg", MtpConstants.FORMAT_EXIF_JPEG);
        addFileType("JPEG", 31, "image/jpeg", MtpConstants.FORMAT_EXIF_JPEG);
        addFileType("GIF", 32, "image/gif", MtpConstants.FORMAT_GIF);
        addFileType("PNG", 33, "image/png", MtpConstants.FORMAT_PNG);
        addFileType("BMP", 34, "image/x-ms-bmp", MtpConstants.FORMAT_BMP);
        addFileType("WBMP", 35, "image/vnd.wap.wbmp");
        addFileType("WEBP", 36, "image/webp");
        addFileType("M3U", 41, "audio/x-mpegurl", MtpConstants.FORMAT_M3U_PLAYLIST);
        addFileType("M3U", 41, "application/x-mpegurl", MtpConstants.FORMAT_M3U_PLAYLIST);
        addFileType("PLS", 42, "audio/x-scpls", MtpConstants.FORMAT_PLS_PLAYLIST);
        addFileType("WPL", 43, "application/vnd.ms-wpl", MtpConstants.FORMAT_WPL_PLAYLIST);
        addFileType("M3U8", 44, "application/vnd.apple.mpegurl");
        addFileType("M3U8", 44, "audio/mpegurl");
        addFileType("M3U8", 44, "audio/x-mpegurl");
        addFileType("FL", 51, "application/x-android-drm-fl");
        addFileType("TXT", 100, ClipDescription.MIMETYPE_TEXT_PLAIN, 12292);
        addFileType("HTM", 101, ClipDescription.MIMETYPE_TEXT_HTML, 12293);
        addFileType("HTML", 101, ClipDescription.MIMETYPE_TEXT_HTML, 12293);
        addFileType("PDF", 102, "application/pdf");
        addFileType("DOC", 104, "application/msword", MtpConstants.FORMAT_MS_WORD_DOCUMENT);
        addFileType("XLS", 105, "application/vnd.ms-excel", MtpConstants.FORMAT_MS_EXCEL_SPREADSHEET);
        addFileType("PPT", 106, "application/mspowerpoint", MtpConstants.FORMAT_MS_POWERPOINT_PRESENTATION);
        addFileType("FLAC", 10, "audio/flac", MtpConstants.FORMAT_FLAC);
        addFileType("ZIP", 107, "application/zip");
        addFileType("MPG", 200, "video/mp2p");
        addFileType("MPEG", 200, "video/mp2p");
    }

    static void addFileType(String str, int i, String str2) {
        sFileTypeMap.put(str, new MediaFileType(i, str2));
        sMimeTypeMap.put(str2, Integer.valueOf(i));
    }

    static void addFileType(String str, int i, String str2, int i2) {
        addFileType(str, i, str2);
        sFileTypeToFormatMap.put(str, Integer.valueOf(i2));
        sMimeTypeToFormatMap.put(str2, Integer.valueOf(i2));
        sFormatToMimeTypeMap.put(Integer.valueOf(i2), str2);
    }

    private static boolean isWMAEnabled() {
        List<DecoderCapabilities.AudioDecoder> audioDecoders = DecoderCapabilities.getAudioDecoders();
        int size = audioDecoders.size();
        for (int i = 0; i < size; i++) {
            if (audioDecoders.get(i) == DecoderCapabilities.AudioDecoder.AUDIO_DECODER_WMA) {
                return true;
            }
        }
        return false;
    }

    private static boolean isWMVEnabled() {
        List<DecoderCapabilities.VideoDecoder> videoDecoders = DecoderCapabilities.getVideoDecoders();
        int size = videoDecoders.size();
        for (int i = 0; i < size; i++) {
            if (videoDecoders.get(i) == DecoderCapabilities.VideoDecoder.VIDEO_DECODER_WMV) {
                return true;
            }
        }
        return false;
    }

    public static MediaFileType getFileType(String str) {
        int iLastIndexOf = str.lastIndexOf(".");
        if (iLastIndexOf < 0) {
            return null;
        }
        return sFileTypeMap.get(str.substring(iLastIndexOf + 1).toUpperCase(Locale.ROOT));
    }

    public static boolean isMimeTypeMedia(String str) {
        int fileTypeForMimeType = getFileTypeForMimeType(str);
        return isAudioFileType(fileTypeForMimeType) || isVideoFileType(fileTypeForMimeType) || isImageFileType(fileTypeForMimeType) || isPlayListFileType(fileTypeForMimeType);
    }

    public static String getFileTitle(String str) {
        int i;
        int iLastIndexOf = str.lastIndexOf(47);
        if (iLastIndexOf >= 0 && (i = iLastIndexOf + 1) < str.length()) {
            str = str.substring(i);
        }
        int iLastIndexOf2 = str.lastIndexOf(46);
        return iLastIndexOf2 > 0 ? str.substring(0, iLastIndexOf2) : str;
    }

    public static int getFileTypeForMimeType(String str) {
        Integer num = sMimeTypeMap.get(str);
        if (num == null) {
            return 0;
        }
        return num.intValue();
    }

    public static String getMimeTypeForFile(String str) {
        MediaFileType fileType = getFileType(str);
        if (fileType == null) {
            return null;
        }
        return fileType.mimeType;
    }

    public static int getFormatCode(String str, String str2) {
        Integer num;
        if (str2 != null && (num = sMimeTypeToFormatMap.get(str2)) != null) {
            return num.intValue();
        }
        int iLastIndexOf = str.lastIndexOf(46);
        if (iLastIndexOf <= 0) {
            return 12288;
        }
        Integer num2 = sFileTypeToFormatMap.get(str.substring(iLastIndexOf + 1).toUpperCase(Locale.ROOT));
        if (num2 != null) {
            return num2.intValue();
        }
        return 12288;
    }

    public static String getMimeTypeForFormatCode(int i) {
        return sFormatToMimeTypeMap.get(Integer.valueOf(i));
    }
}
