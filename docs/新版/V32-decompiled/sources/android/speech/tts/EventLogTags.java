package android.speech.tts;

import android.util.EventLog;

/* JADX INFO: loaded from: classes.dex */
public class EventLogTags {
    public static final int TTS_SPEAK_FAILURE = 76002;
    public static final int TTS_SPEAK_SUCCESS = 76001;

    private EventLogTags() {
    }

    public static void writeTtsSpeakSuccess(String str, int i, int i2, int i3, String str2, int i4, int i5, long j, long j2, long j3) {
        EventLog.writeEvent(TTS_SPEAK_SUCCESS, str, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), str2, Integer.valueOf(i4), Integer.valueOf(i5), Long.valueOf(j), Long.valueOf(j2), Long.valueOf(j3));
    }

    public static void writeTtsSpeakFailure(String str, int i, int i2, int i3, String str2, int i4, int i5) {
        EventLog.writeEvent(TTS_SPEAK_FAILURE, str, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), str2, Integer.valueOf(i4), Integer.valueOf(i5));
    }
}
