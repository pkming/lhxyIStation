package android.media;

import android.media.SubtitleTrack;
import com.unisound.common.r;
import java.util.Arrays;

/* JADX INFO: compiled from: WebVttRenderer.java */
/* JADX INFO: loaded from: classes.dex */
class TextTrackCue extends SubtitleTrack.Cue {
    static final int ALIGNMENT_END = 202;
    static final int ALIGNMENT_LEFT = 203;
    static final int ALIGNMENT_MIDDLE = 200;
    static final int ALIGNMENT_RIGHT = 204;
    static final int ALIGNMENT_START = 201;
    private static final String TAG = "TTCue";
    static final int WRITING_DIRECTION_HORIZONTAL = 100;
    static final int WRITING_DIRECTION_VERTICAL_LR = 102;
    static final int WRITING_DIRECTION_VERTICAL_RL = 101;
    boolean mAutoLinePosition;
    String[] mStrings;
    String mId = "";
    boolean mPauseOnExit = false;
    int mWritingDirection = 100;
    String mRegionId = "";
    boolean mSnapToLines = true;
    Integer mLinePosition = null;
    int mTextPosition = 50;
    int mSize = 100;
    int mAlignment = 200;
    TextTrackCueSpan[][] mLines = (TextTrackCueSpan[][]) null;
    TextTrackRegion mRegion = null;

    TextTrackCue() {
    }

    public boolean equals(Object obj) {
        boolean z;
        if (!(obj instanceof TextTrackCue)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        try {
            TextTrackCue textTrackCue = (TextTrackCue) obj;
            boolean z2 = this.mId.equals(textTrackCue.mId) && this.mPauseOnExit == textTrackCue.mPauseOnExit && this.mWritingDirection == textTrackCue.mWritingDirection && this.mRegionId.equals(textTrackCue.mRegionId) && this.mSnapToLines == textTrackCue.mSnapToLines && (z = this.mAutoLinePosition) == textTrackCue.mAutoLinePosition && (z || this.mLinePosition == textTrackCue.mLinePosition) && this.mTextPosition == textTrackCue.mTextPosition && this.mSize == textTrackCue.mSize && this.mAlignment == textTrackCue.mAlignment && this.mLines.length == textTrackCue.mLines.length;
            if (z2) {
                int i = 0;
                while (true) {
                    TextTrackCueSpan[][] textTrackCueSpanArr = this.mLines;
                    if (i >= textTrackCueSpanArr.length) {
                        break;
                    }
                    if (!Arrays.equals(textTrackCueSpanArr[i], textTrackCue.mLines[i])) {
                        return false;
                    }
                    i++;
                }
            }
            return z2;
        } catch (IncompatibleClassChangeError unused) {
            return false;
        }
    }

    public StringBuilder appendStringsToBuilder(StringBuilder sb) {
        if (this.mStrings == null) {
            sb.append("null");
        } else {
            sb.append("[");
            String[] strArr = this.mStrings;
            int length = strArr.length;
            boolean z = true;
            int i = 0;
            while (i < length) {
                String str = strArr[i];
                if (!z) {
                    sb.append(", ");
                }
                if (str == null) {
                    sb.append("null");
                } else {
                    sb.append("\"");
                    sb.append(str);
                    sb.append("\"");
                }
                i++;
                z = false;
            }
            sb.append("]");
        }
        return sb;
    }

    public StringBuilder appendLinesToBuilder(StringBuilder sb) {
        if (this.mLines == null) {
            sb.append("null");
        } else {
            sb.append("[");
            TextTrackCueSpan[][] textTrackCueSpanArr = this.mLines;
            int length = textTrackCueSpanArr.length;
            int i = 0;
            boolean z = true;
            while (i < length) {
                TextTrackCueSpan[] textTrackCueSpanArr2 = textTrackCueSpanArr[i];
                if (!z) {
                    sb.append(", ");
                }
                if (textTrackCueSpanArr2 == null) {
                    sb.append("null");
                } else {
                    sb.append("\"");
                    long j = -1;
                    int length2 = textTrackCueSpanArr2.length;
                    int i2 = 0;
                    boolean z2 = true;
                    while (i2 < length2) {
                        TextTrackCueSpan textTrackCueSpan = textTrackCueSpanArr2[i2];
                        if (!z2) {
                            sb.append(" ");
                        }
                        if (textTrackCueSpan.mTimestampMs != j) {
                            sb.append("<").append(WebVttParser.timeToString(textTrackCueSpan.mTimestampMs)).append(">");
                            j = textTrackCueSpan.mTimestampMs;
                        }
                        sb.append(textTrackCueSpan.mText);
                        i2++;
                        z2 = false;
                    }
                    sb.append("\"");
                }
                i++;
                z = false;
            }
            sb.append("]");
        }
        return sb;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        StringBuilder sbAppend = sb.append(WebVttParser.timeToString(this.mStartTimeMs)).append(" --> ").append(WebVttParser.timeToString(this.mEndTimeMs)).append(" {id:\"").append(this.mId).append("\", pauseOnExit:").append(this.mPauseOnExit).append(", direction:");
        int i = this.mWritingDirection;
        String str = "INVALID";
        StringBuilder sbAppend2 = sbAppend.append(i == 100 ? "horizontal" : i == 102 ? "vertical_lr" : i == 101 ? "vertical_rl" : "INVALID").append(", regionId:\"").append(this.mRegionId).append("\", snapToLines:").append(this.mSnapToLines).append(", linePosition:").append(this.mAutoLinePosition ? "auto" : this.mLinePosition).append(", textPosition:").append(this.mTextPosition).append(", size:").append(this.mSize).append(", alignment:");
        int i2 = this.mAlignment;
        if (i2 == 202) {
            str = "end";
        } else if (i2 == 203) {
            str = "left";
        } else if (i2 == 200) {
            str = "middle";
        } else if (i2 == 204) {
            str = "right";
        } else if (i2 == 201) {
            str = r.w;
        }
        sbAppend2.append(str).append(", text:");
        appendStringsToBuilder(sb).append("}");
        return sb.toString();
    }

    public int hashCode() {
        return toString().hashCode();
    }

    @Override // android.media.SubtitleTrack.Cue
    public void onTime(long j) {
        for (TextTrackCueSpan[] textTrackCueSpanArr : this.mLines) {
            for (TextTrackCueSpan textTrackCueSpan : textTrackCueSpanArr) {
                textTrackCueSpan.mEnabled = j >= textTrackCueSpan.mTimestampMs;
            }
        }
    }
}
