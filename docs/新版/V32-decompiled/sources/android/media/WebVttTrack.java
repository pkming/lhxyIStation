package android.media;

import android.media.SubtitleTrack;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/* JADX INFO: compiled from: WebVttRenderer.java */
/* JADX INFO: loaded from: classes.dex */
class WebVttTrack extends SubtitleTrack implements WebVttCueListener {
    private static final String TAG = "WebVttTrack";
    private Long mCurrentRunID;
    private final UnstyledTextExtractor mExtractor;
    private final WebVttParser mParser;
    private final Map<String, TextTrackRegion> mRegions;
    private final WebVttRenderingWidget mRenderingWidget;
    private final Vector<Long> mTimestamps;
    private final Tokenizer mTokenizer;

    WebVttTrack(WebVttRenderingWidget webVttRenderingWidget, MediaFormat mediaFormat) {
        super(mediaFormat);
        this.mParser = new WebVttParser(this);
        UnstyledTextExtractor unstyledTextExtractor = new UnstyledTextExtractor();
        this.mExtractor = unstyledTextExtractor;
        this.mTokenizer = new Tokenizer(unstyledTextExtractor);
        this.mTimestamps = new Vector<>();
        this.mRegions = new HashMap();
        this.mRenderingWidget = webVttRenderingWidget;
    }

    @Override // android.media.SubtitleTrack
    public WebVttRenderingWidget getRenderingWidget() {
        return this.mRenderingWidget;
    }

    @Override // android.media.SubtitleTrack
    public void onData(String str, boolean z, long j) {
        synchronized (this.mParser) {
            Long l = this.mCurrentRunID;
            if (l != null && j != l.longValue()) {
                throw new IllegalStateException("Run #" + this.mCurrentRunID + " in progress.  Cannot process run #" + j);
            }
            this.mCurrentRunID = Long.valueOf(j);
            this.mParser.parse(str);
            if (z) {
                finishedRun(j);
                this.mParser.eos();
                this.mRegions.clear();
                this.mCurrentRunID = null;
            }
        }
    }

    @Override // android.media.WebVttCueListener
    public void onCueParsed(TextTrackCue textTrackCue) {
        synchronized (this.mParser) {
            if (textTrackCue.mRegionId.length() != 0) {
                textTrackCue.mRegion = this.mRegions.get(textTrackCue.mRegionId);
            }
            if (this.DEBUG) {
                Log.v(TAG, "adding cue " + textTrackCue);
            }
            this.mTokenizer.reset();
            for (String str : textTrackCue.mStrings) {
                this.mTokenizer.tokenize(str);
            }
            textTrackCue.mLines = this.mExtractor.getText();
            if (this.DEBUG) {
                Log.v(TAG, textTrackCue.appendLinesToBuilder(textTrackCue.appendStringsToBuilder(new StringBuilder()).append(" simplified to: ")).toString());
            }
            for (TextTrackCueSpan[] textTrackCueSpanArr : textTrackCue.mLines) {
                for (TextTrackCueSpan textTrackCueSpan : textTrackCueSpanArr) {
                    if (textTrackCueSpan.mTimestampMs > textTrackCue.mStartTimeMs && textTrackCueSpan.mTimestampMs < textTrackCue.mEndTimeMs && !this.mTimestamps.contains(Long.valueOf(textTrackCueSpan.mTimestampMs))) {
                        this.mTimestamps.add(Long.valueOf(textTrackCueSpan.mTimestampMs));
                    }
                }
            }
            if (this.mTimestamps.size() > 0) {
                textTrackCue.mInnerTimesMs = new long[this.mTimestamps.size()];
                for (int i = 0; i < this.mTimestamps.size(); i++) {
                    textTrackCue.mInnerTimesMs[i] = this.mTimestamps.get(i).longValue();
                }
                this.mTimestamps.clear();
            } else {
                textTrackCue.mInnerTimesMs = null;
            }
            textTrackCue.mRunID = this.mCurrentRunID.longValue();
        }
        addCue(textTrackCue);
    }

    @Override // android.media.WebVttCueListener
    public void onRegionParsed(TextTrackRegion textTrackRegion) {
        synchronized (this.mParser) {
            this.mRegions.put(textTrackRegion.mId, textTrackRegion);
        }
    }

    @Override // android.media.SubtitleTrack
    public void updateView(Vector<SubtitleTrack.Cue> vector) {
        if (this.mVisible) {
            if (this.DEBUG && this.mTimeProvider != null) {
                try {
                    Log.d(TAG, "at " + (this.mTimeProvider.getCurrentTimeUs(false, true) / 1000) + " ms the active cues are:");
                } catch (IllegalStateException unused) {
                    Log.d(TAG, "at (illegal state) the active cues are:");
                }
            }
            this.mRenderingWidget.setActiveCues(vector);
        }
    }
}
