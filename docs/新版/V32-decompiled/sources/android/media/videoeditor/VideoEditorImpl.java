package android.media.videoeditor;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.media.videoeditor.VideoEditor;
import android.os.Debug;
import android.os.Environment;
import android.os.SystemProperties;
import android.util.Log;
import android.util.Xml;
import android.view.Surface;
import android.view.SurfaceHolder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class VideoEditorImpl implements VideoEditor {
    private static final String ATTR_AFTER_MEDIA_ITEM_ID = "after_media_item";
    private static final String ATTR_ASPECT_RATIO = "aspect_ratio";
    private static final String ATTR_AUDIO_WAVEFORM_FILENAME = "waveform";
    private static final String ATTR_BEFORE_MEDIA_ITEM_ID = "before_media_item";
    private static final String ATTR_BEGIN_TIME = "begin_time";
    private static final String ATTR_BEHAVIOR = "behavior";
    private static final String ATTR_BLENDING = "blending";
    private static final String ATTR_COLOR_EFFECT_TYPE = "color_type";
    private static final String ATTR_COLOR_EFFECT_VALUE = "color_value";
    private static final String ATTR_DIRECTION = "direction";
    private static final String ATTR_DUCKED_TRACK_VOLUME = "ducking_volume";
    private static final String ATTR_DUCK_ENABLED = "ducking_enabled";
    private static final String ATTR_DUCK_THRESHOLD = "ducking_threshold";
    private static final String ATTR_DURATION = "duration";
    private static final String ATTR_END_RECT_BOTTOM = "end_b";
    private static final String ATTR_END_RECT_LEFT = "end_l";
    private static final String ATTR_END_RECT_RIGHT = "end_r";
    private static final String ATTR_END_RECT_TOP = "end_t";
    private static final String ATTR_END_TIME = "end_time";
    private static final String ATTR_FILENAME = "filename";
    private static final String ATTR_GENERATED_IMAGE_CLIP = "generated_image_clip";
    private static final String ATTR_GENERATED_TRANSITION_CLIP = "generated_transition_clip";
    private static final String ATTR_ID = "id";
    private static final String ATTR_INVERT = "invert";
    private static final String ATTR_IS_IMAGE_CLIP_GENERATED = "is_image_clip_generated";
    private static final String ATTR_IS_TRANSITION_GENERATED = "is_transition_generated";
    private static final String ATTR_LOOP = "loop";
    private static final String ATTR_MASK = "mask";
    private static final String ATTR_MUTED = "muted";
    private static final String ATTR_OVERLAY_FRAME_HEIGHT = "overlay_frame_height";
    private static final String ATTR_OVERLAY_FRAME_WIDTH = "overlay_frame_width";
    private static final String ATTR_OVERLAY_RESIZED_RGB_FRAME_HEIGHT = "resized_RGBframe_height";
    private static final String ATTR_OVERLAY_RESIZED_RGB_FRAME_WIDTH = "resized_RGBframe_width";
    private static final String ATTR_OVERLAY_RGB_FILENAME = "overlay_rgb_filename";
    private static final String ATTR_REGENERATE_PCM = "regeneratePCMFlag";
    private static final String ATTR_RENDERING_MODE = "rendering_mode";
    private static final String ATTR_START_RECT_BOTTOM = "start_b";
    private static final String ATTR_START_RECT_LEFT = "start_l";
    private static final String ATTR_START_RECT_RIGHT = "start_r";
    private static final String ATTR_START_RECT_TOP = "start_t";
    private static final String ATTR_START_TIME = "start_time";
    private static final String ATTR_TYPE = "type";
    private static final String ATTR_VOLUME = "volume";
    private static final int ENGINE_ACCESS_MAX_TIMEOUT_MS = 500;
    private static final String PROJECT_FILENAME = "videoeditor.xml";
    private static final String TAG = "VideoEditorImpl";
    private static final String TAG_AUDIO_TRACK = "audio_track";
    private static final String TAG_AUDIO_TRACKS = "audio_tracks";
    private static final String TAG_EFFECT = "effect";
    private static final String TAG_EFFECTS = "effects";
    private static final String TAG_MEDIA_ITEM = "media_item";
    private static final String TAG_MEDIA_ITEMS = "media_items";
    private static final String TAG_OVERLAY = "overlay";
    private static final String TAG_OVERLAYS = "overlays";
    private static final String TAG_OVERLAY_USER_ATTRIBUTES = "overlay_user_attributes";
    private static final String TAG_PROJECT = "project";
    private static final String TAG_TRANSITION = "transition";
    private static final String TAG_TRANSITIONS = "transitions";
    private int mAspectRatio;
    private long mDurationMs;
    private final Semaphore mLock;
    private MediaArtistNativeHelper mMANativeHelper;
    private final boolean mMallocDebug;
    private final String mProjectPath;
    private final List<MediaItem> mMediaItems = new ArrayList();
    private final List<AudioTrack> mAudioTracks = new ArrayList();
    private final List<Transition> mTransitions = new ArrayList();
    private boolean mPreviewInProgress = false;

    public VideoEditorImpl(String str) throws IOException {
        if (SystemProperties.get("libc.debug.malloc").equals("1")) {
            this.mMallocDebug = true;
            try {
                dumpHeap("HeapAtStart");
            } catch (Exception unused) {
                Log.e(TAG, "dumpHeap returned error in constructor");
            }
        } else {
            this.mMallocDebug = false;
        }
        Semaphore semaphore = new Semaphore(1, true);
        this.mLock = semaphore;
        this.mMANativeHelper = new MediaArtistNativeHelper(str, semaphore, this);
        this.mProjectPath = str;
        if (new File(str, PROJECT_FILENAME).exists()) {
            try {
                load();
                return;
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException(e.toString());
            }
        }
        this.mAspectRatio = 2;
        this.mDurationMs = 0L;
    }

    MediaArtistNativeHelper getNativeContext() {
        return this.mMANativeHelper;
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized void addAudioTrack(AudioTrack audioTrack) {
        try {
            if (audioTrack == null) {
                throw new IllegalArgumentException("Audio Track is null");
            }
            if (this.mAudioTracks.size() == 1) {
                throw new IllegalArgumentException("No more tracks can be added");
            }
            this.mMANativeHelper.setGeneratePreview(true);
            this.mAudioTracks.add(audioTrack);
            if (new File(String.format(this.mProjectPath + "/AudioPcm" + audioTrack.getId() + ".pcm", new Object[0])).exists()) {
                this.mMANativeHelper.setAudioflag(false);
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized void addMediaItem(MediaItem mediaItem) {
        try {
            if (mediaItem == null) {
                throw new IllegalArgumentException("Media item is null");
            }
            if (this.mMediaItems.contains(mediaItem)) {
                throw new IllegalArgumentException("Media item already exists: " + mediaItem.getId());
            }
            this.mMANativeHelper.setGeneratePreview(true);
            int size = this.mMediaItems.size();
            if (size > 0) {
                removeTransitionAfter(size - 1);
            }
            this.mMediaItems.add(mediaItem);
            computeTimelineDuration();
            if (this.mMediaItems.size() == 1) {
                generateProjectThumbnail();
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized void addTransition(Transition transition) {
        try {
            if (transition == null) {
                throw new IllegalArgumentException("Null Transition");
            }
            MediaItem beforeMediaItem = transition.getBeforeMediaItem();
            MediaItem afterMediaItem = transition.getAfterMediaItem();
            List<MediaItem> list = this.mMediaItems;
            if (list == null) {
                throw new IllegalArgumentException("No media items are added");
            }
            if (afterMediaItem != null && beforeMediaItem != null) {
                int iIndexOf = list.indexOf(afterMediaItem);
                int iIndexOf2 = this.mMediaItems.indexOf(beforeMediaItem);
                if (iIndexOf == -1 || iIndexOf2 == -1) {
                    throw new IllegalArgumentException("Either of the mediaItem is not found in the list");
                }
                if (iIndexOf != iIndexOf2 - 1) {
                    throw new IllegalArgumentException("MediaItems are not in sequence");
                }
            }
            this.mMANativeHelper.setGeneratePreview(true);
            this.mTransitions.add(transition);
            if (afterMediaItem != null) {
                if (afterMediaItem.getEndTransition() != null) {
                    afterMediaItem.getEndTransition().invalidate();
                    this.mTransitions.remove(afterMediaItem.getEndTransition());
                }
                afterMediaItem.setEndTransition(transition);
            }
            if (beforeMediaItem != null) {
                if (beforeMediaItem.getBeginTransition() != null) {
                    beforeMediaItem.getBeginTransition().invalidate();
                    this.mTransitions.remove(beforeMediaItem.getBeginTransition());
                }
                beforeMediaItem.setBeginTransition(transition);
            }
            computeTimelineDuration();
        } catch (Throwable th) {
            throw th;
        }
    }

    @Override // android.media.videoeditor.VideoEditor
    public void cancelExport(String str) {
        MediaArtistNativeHelper mediaArtistNativeHelper = this.mMANativeHelper;
        if (mediaArtistNativeHelper == null || str == null) {
            return;
        }
        mediaArtistNativeHelper.stop(str);
    }

    /* JADX WARN: Removed duplicated region for block: B:58:0x0103  */
    @Override // android.media.videoeditor.VideoEditor
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void export(java.lang.String r14, int r15, int r16, int r17, int r18, android.media.videoeditor.VideoEditor.ExportProgressListener r19) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 342
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.videoeditor.VideoEditorImpl.export(java.lang.String, int, int, int, int, android.media.videoeditor.VideoEditor$ExportProgressListener):void");
    }

    @Override // android.media.videoeditor.VideoEditor
    public void export(String str, int i, int i2, VideoEditor.ExportProgressListener exportProgressListener) throws Throwable {
        export(str, i, i2, 2, 2, exportProgressListener);
    }

    @Override // android.media.videoeditor.VideoEditor
    public void generatePreview(VideoEditor.MediaProcessingProgressListener mediaProcessingProgressListener) {
        try {
            try {
                lock();
            } catch (InterruptedException unused) {
                Log.e(TAG, "Sem acquire NOT successful in previewStoryBoard");
                if (0 == 0) {
                    return;
                }
            }
            if (this.mMANativeHelper == null) {
                throw new IllegalStateException("The video editor is not initialized");
            }
            if (this.mMediaItems.size() > 0 || this.mAudioTracks.size() > 0) {
                this.mMANativeHelper.previewStoryBoard(this.mMediaItems, this.mTransitions, this.mAudioTracks, mediaProcessingProgressListener);
            }
            unlock();
        } catch (Throwable th) {
            if (0 != 0) {
                unlock();
            }
            throw th;
        }
    }

    @Override // android.media.videoeditor.VideoEditor
    public List<AudioTrack> getAllAudioTracks() {
        return this.mAudioTracks;
    }

    @Override // android.media.videoeditor.VideoEditor
    public List<MediaItem> getAllMediaItems() {
        return this.mMediaItems;
    }

    @Override // android.media.videoeditor.VideoEditor
    public List<Transition> getAllTransitions() {
        return this.mTransitions;
    }

    @Override // android.media.videoeditor.VideoEditor
    public int getAspectRatio() {
        return this.mAspectRatio;
    }

    @Override // android.media.videoeditor.VideoEditor
    public AudioTrack getAudioTrack(String str) {
        for (AudioTrack audioTrack : this.mAudioTracks) {
            if (audioTrack.getId().equals(str)) {
                return audioTrack;
            }
        }
        return null;
    }

    @Override // android.media.videoeditor.VideoEditor
    public long getDuration() {
        computeTimelineDuration();
        return this.mDurationMs;
    }

    void updateTimelineDuration() {
        computeTimelineDuration();
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized MediaItem getMediaItem(String str) {
        for (MediaItem mediaItem : this.mMediaItems) {
            if (mediaItem.getId().equals(str)) {
                return mediaItem;
            }
        }
        return null;
    }

    @Override // android.media.videoeditor.VideoEditor
    public String getPath() {
        return this.mProjectPath;
    }

    @Override // android.media.videoeditor.VideoEditor
    public Transition getTransition(String str) {
        for (Transition transition : this.mTransitions) {
            if (transition.getId().equals(str)) {
                return transition;
            }
        }
        return null;
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized void insertAudioTrack(AudioTrack audioTrack, String str) {
        if (this.mAudioTracks.size() == 1) {
            throw new IllegalArgumentException("No more tracks can be added");
        }
        if (str == null) {
            this.mMANativeHelper.setGeneratePreview(true);
            this.mAudioTracks.add(0, audioTrack);
            return;
        }
        int size = this.mAudioTracks.size();
        for (int i = 0; i < size; i++) {
            if (this.mAudioTracks.get(i).getId().equals(str)) {
                this.mMANativeHelper.setGeneratePreview(true);
                this.mAudioTracks.add(i + 1, audioTrack);
                return;
            }
        }
        throw new IllegalArgumentException("AudioTrack not found: " + str);
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized void insertMediaItem(MediaItem mediaItem, String str) {
        if (this.mMediaItems.contains(mediaItem)) {
            throw new IllegalArgumentException("Media item already exists: " + mediaItem.getId());
        }
        if (str == null) {
            this.mMANativeHelper.setGeneratePreview(true);
            if (this.mMediaItems.size() > 0) {
                removeTransitionBefore(0);
            }
            this.mMediaItems.add(0, mediaItem);
            computeTimelineDuration();
            generateProjectThumbnail();
            return;
        }
        int size = this.mMediaItems.size();
        for (int i = 0; i < size; i++) {
            if (this.mMediaItems.get(i).getId().equals(str)) {
                this.mMANativeHelper.setGeneratePreview(true);
                removeTransitionAfter(i);
                this.mMediaItems.add(i + 1, mediaItem);
                computeTimelineDuration();
                return;
            }
        }
        throw new IllegalArgumentException("MediaItem not found: " + str);
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized void moveAudioTrack(String str, String str2) {
        throw new IllegalStateException("Not supported");
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized void moveMediaItem(String str, String str2) {
        MediaItem mediaItemRemoveMediaItem = removeMediaItem(str, true);
        if (mediaItemRemoveMediaItem == null) {
            throw new IllegalArgumentException("Target MediaItem not found: " + str);
        }
        if (str2 == null) {
            if (this.mMediaItems.size() > 0) {
                this.mMANativeHelper.setGeneratePreview(true);
                removeTransitionBefore(0);
                this.mMediaItems.add(0, mediaItemRemoveMediaItem);
                computeTimelineDuration();
                generateProjectThumbnail();
                return;
            }
            throw new IllegalStateException("Cannot move media item (it is the only item)");
        }
        int size = this.mMediaItems.size();
        for (int i = 0; i < size; i++) {
            if (this.mMediaItems.get(i).getId().equals(str2)) {
                this.mMANativeHelper.setGeneratePreview(true);
                removeTransitionAfter(i);
                this.mMediaItems.add(i + 1, mediaItemRemoveMediaItem);
                computeTimelineDuration();
                return;
            }
        }
        throw new IllegalArgumentException("MediaItem not found: " + str2);
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0038 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:27:? A[RETURN, SYNTHETIC] */
    @Override // android.media.videoeditor.VideoEditor
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void release() {
        /*
            r4 = this;
            java.lang.String r0 = "VideoEditorImpl"
            r4.stopPreview()
            r1 = 0
            r4.lock()     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2b
            r1 = 1
            android.media.videoeditor.MediaArtistNativeHelper r2 = r4.mMANativeHelper     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2b
            if (r2 == 0) goto L25
            java.util.List<android.media.videoeditor.MediaItem> r2 = r4.mMediaItems     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2b
            r2.clear()     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2b
            java.util.List<android.media.videoeditor.AudioTrack> r2 = r4.mAudioTracks     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2b
            r2.clear()     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2b
            java.util.List<android.media.videoeditor.Transition> r2 = r4.mTransitions     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2b
            r2.clear()     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2b
            android.media.videoeditor.MediaArtistNativeHelper r2 = r4.mMANativeHelper     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2b
            r2.releaseNativeHelper()     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2b
            r2 = 0
            r4.mMANativeHelper = r2     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2b
        L25:
            r4.unlock()
            goto L34
        L29:
            r0 = move-exception
            goto L44
        L2b:
            r2 = move-exception
            java.lang.String r3 = "Sem acquire NOT successful in export"
            android.util.Log.e(r0, r3, r2)     // Catch: java.lang.Throwable -> L29
            if (r1 == 0) goto L34
            goto L25
        L34:
            boolean r1 = r4.mMallocDebug
            if (r1 == 0) goto L43
            java.lang.String r1 = "HeapAtEnd"
            dumpHeap(r1)     // Catch: java.lang.Exception -> L3e
            goto L43
        L3e:
            java.lang.String r1 = "dumpHeap returned error in release"
            android.util.Log.e(r0, r1)
        L43:
            return
        L44:
            if (r1 == 0) goto L49
            r4.unlock()
        L49:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.videoeditor.VideoEditorImpl.release():void");
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized void removeAllMediaItems() {
        this.mMANativeHelper.setGeneratePreview(true);
        this.mMediaItems.clear();
        Iterator<Transition> it = this.mTransitions.iterator();
        while (it.hasNext()) {
            it.next().invalidate();
        }
        this.mTransitions.clear();
        this.mDurationMs = 0L;
        if (new File(this.mProjectPath + "/" + VideoEditor.THUMBNAIL_FILENAME).exists()) {
            new File(this.mProjectPath + "/" + VideoEditor.THUMBNAIL_FILENAME).delete();
        }
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized AudioTrack removeAudioTrack(String str) {
        AudioTrack audioTrack;
        audioTrack = getAudioTrack(str);
        if (audioTrack != null) {
            this.mMANativeHelper.setGeneratePreview(true);
            this.mAudioTracks.remove(audioTrack);
            audioTrack.invalidate();
            this.mMANativeHelper.invalidatePcmFile();
            this.mMANativeHelper.setAudioflag(true);
        } else {
            throw new IllegalArgumentException(" No more audio tracks");
        }
        return audioTrack;
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized MediaItem removeMediaItem(String str) {
        MediaItem mediaItem;
        String id = this.mMediaItems.get(0).getId();
        mediaItem = getMediaItem(str);
        if (mediaItem != null) {
            this.mMANativeHelper.setGeneratePreview(true);
            this.mMediaItems.remove(mediaItem);
            if (mediaItem instanceof MediaImageItem) {
                ((MediaImageItem) mediaItem).invalidate();
            }
            List<Overlay> allOverlays = mediaItem.getAllOverlays();
            if (allOverlays.size() > 0) {
                for (Overlay overlay : allOverlays) {
                    if (overlay instanceof OverlayFrame) {
                        ((OverlayFrame) overlay).invalidate();
                    }
                }
            }
            removeAdjacentTransitions(mediaItem);
            computeTimelineDuration();
        }
        if (id.equals(str)) {
            generateProjectThumbnail();
        }
        if (mediaItem instanceof MediaVideoItem) {
            ((MediaVideoItem) mediaItem).invalidate();
        }
        return mediaItem;
    }

    private synchronized MediaItem removeMediaItem(String str, boolean z) {
        MediaItem mediaItem;
        String id = this.mMediaItems.get(0).getId();
        mediaItem = getMediaItem(str);
        if (mediaItem != null) {
            this.mMANativeHelper.setGeneratePreview(true);
            this.mMediaItems.remove(mediaItem);
            removeAdjacentTransitions(mediaItem);
            computeTimelineDuration();
        }
        if (id.equals(str)) {
            generateProjectThumbnail();
        }
        return mediaItem;
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized Transition removeTransition(String str) {
        Transition transition;
        transition = getTransition(str);
        if (transition == null) {
            throw new IllegalStateException("Transition not found: " + str);
        }
        this.mMANativeHelper.setGeneratePreview(true);
        MediaItem afterMediaItem = transition.getAfterMediaItem();
        if (afterMediaItem != null) {
            afterMediaItem.setEndTransition(null);
        }
        MediaItem beforeMediaItem = transition.getBeforeMediaItem();
        if (beforeMediaItem != null) {
            beforeMediaItem.setBeginTransition(null);
        }
        this.mTransitions.remove(transition);
        transition.invalidate();
        computeTimelineDuration();
        return transition;
    }

    @Override // android.media.videoeditor.VideoEditor
    public long renderPreviewFrame(SurfaceHolder surfaceHolder, long j, VideoEditor.OverlayData overlayData) throws Throwable {
        boolean zLock;
        if (surfaceHolder == null) {
            throw new IllegalArgumentException("Surface Holder is null");
        }
        Surface surface = surfaceHolder.getSurface();
        if (surface == null) {
            throw new IllegalArgumentException("Surface could not be retrieved from Surface holder");
        }
        if (!surface.isValid()) {
            throw new IllegalStateException("Surface is not valid");
        }
        long jRenderPreviewFrame = 0;
        if (j < 0) {
            throw new IllegalArgumentException("requested time not correct");
        }
        if (j > this.mDurationMs) {
            throw new IllegalArgumentException("requested time more than duration");
        }
        boolean z = false;
        try {
            try {
                zLock = lock(500L);
            } catch (Throwable th) {
                th = th;
            }
        } catch (InterruptedException unused) {
        }
        try {
            if (!zLock) {
                throw new IllegalStateException("Timeout waiting for semaphore");
            }
            if (this.mMANativeHelper == null) {
                throw new IllegalStateException("The video editor is not initialized");
            }
            if (this.mMediaItems.size() > 0) {
                Rect surfaceFrame = surfaceHolder.getSurfaceFrame();
                jRenderPreviewFrame = this.mMANativeHelper.renderPreviewFrame(surface, j, surfaceFrame.width(), surfaceFrame.height(), overlayData);
            }
            if (zLock) {
                unlock();
            }
            return jRenderPreviewFrame;
        } catch (InterruptedException unused2) {
            Log.w(TAG, "The thread was interrupted", new Throwable());
            throw new IllegalStateException("The thread was interrupted");
        } catch (Throwable th2) {
            th = th2;
            z = zLock;
            if (z) {
                unlock();
            }
            throw th;
        }
    }

    private void load() throws XmlPullParserException, IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(this.mProjectPath, PROJECT_FILENAME));
        try {
            List<String> arrayList = new ArrayList<>();
            XmlPullParser xmlPullParserNewPullParser = Xml.newPullParser();
            xmlPullParserNewPullParser.setInput(fileInputStream, "UTF-8");
            boolean z = false;
            MediaItem mediaItem = null;
            Overlay overlay = null;
            for (int eventType = xmlPullParserNewPullParser.getEventType(); eventType != 1; eventType = xmlPullParserNewPullParser.next()) {
                if (eventType == 2) {
                    String name = xmlPullParserNewPullParser.getName();
                    if (TAG_PROJECT.equals(name)) {
                        this.mAspectRatio = Integer.parseInt(xmlPullParserNewPullParser.getAttributeValue("", ATTR_ASPECT_RATIO));
                        this.mMANativeHelper.setAudioflag(Boolean.parseBoolean(xmlPullParserNewPullParser.getAttributeValue("", ATTR_REGENERATE_PCM)));
                    } else if (TAG_MEDIA_ITEM.equals(name)) {
                        String attributeValue = xmlPullParserNewPullParser.getAttributeValue("", "id");
                        try {
                            mediaItem = parseMediaItem(xmlPullParserNewPullParser);
                            this.mMediaItems.add(mediaItem);
                        } catch (Exception e) {
                            Log.w(TAG, "Cannot load media item: " + attributeValue, e);
                            if (this.mMediaItems.size() == 0) {
                                z = true;
                            }
                            arrayList.add(attributeValue);
                            mediaItem = null;
                        }
                    } else if (TAG_TRANSITION.equals(name)) {
                        try {
                            Transition transition = parseTransition(xmlPullParserNewPullParser, arrayList);
                            if (transition != null) {
                                this.mTransitions.add(transition);
                            }
                        } catch (Exception e2) {
                            Log.w(TAG, "Cannot load transition", e2);
                        }
                    } else if (TAG_OVERLAY.equals(name)) {
                        if (mediaItem != null) {
                            try {
                                overlay = parseOverlay(xmlPullParserNewPullParser, mediaItem);
                                mediaItem.addOverlay(overlay);
                            } catch (Exception e3) {
                                Log.w(TAG, "Cannot load overlay", e3);
                            }
                        }
                    } else if (TAG_OVERLAY_USER_ATTRIBUTES.equals(name)) {
                        if (overlay != null) {
                            int attributeCount = xmlPullParserNewPullParser.getAttributeCount();
                            for (int i = 0; i < attributeCount; i++) {
                                overlay.setUserAttribute(xmlPullParserNewPullParser.getAttributeName(i), xmlPullParserNewPullParser.getAttributeValue(i));
                            }
                        }
                    } else if (TAG_EFFECT.equals(name)) {
                        if (mediaItem != null) {
                            try {
                                Effect effect = parseEffect(xmlPullParserNewPullParser, mediaItem);
                                mediaItem.addEffect(effect);
                                if (effect instanceof EffectKenBurns) {
                                    if (Boolean.parseBoolean(xmlPullParserNewPullParser.getAttributeValue("", ATTR_IS_IMAGE_CLIP_GENERATED))) {
                                        String attributeValue2 = xmlPullParserNewPullParser.getAttributeValue("", ATTR_GENERATED_IMAGE_CLIP);
                                        if (new File(attributeValue2).exists()) {
                                            ((MediaImageItem) mediaItem).setGeneratedImageClip(attributeValue2);
                                            ((MediaImageItem) mediaItem).setRegenerateClip(false);
                                        } else {
                                            ((MediaImageItem) mediaItem).setGeneratedImageClip(null);
                                            ((MediaImageItem) mediaItem).setRegenerateClip(true);
                                        }
                                    } else {
                                        ((MediaImageItem) mediaItem).setGeneratedImageClip(null);
                                        ((MediaImageItem) mediaItem).setRegenerateClip(true);
                                    }
                                }
                            } catch (Exception e4) {
                                Log.w(TAG, "Cannot load effect", e4);
                            }
                        }
                    } else if (TAG_AUDIO_TRACK.equals(name)) {
                        try {
                            addAudioTrack(parseAudioTrack(xmlPullParserNewPullParser));
                        } catch (Exception e5) {
                            Log.w(TAG, "Cannot load audio track", e5);
                        }
                    }
                } else if (eventType == 3) {
                    String name2 = xmlPullParserNewPullParser.getName();
                    if (TAG_MEDIA_ITEM.equals(name2)) {
                        mediaItem = null;
                    } else if (TAG_OVERLAY.equals(name2)) {
                        overlay = null;
                    }
                }
            }
            computeTimelineDuration();
            if (z) {
                generateProjectThumbnail();
            }
        } finally {
            fileInputStream.close();
        }
    }

    private MediaItem parseMediaItem(XmlPullParser xmlPullParser) throws IOException {
        String attributeValue = xmlPullParser.getAttributeValue("", "id");
        String attributeValue2 = xmlPullParser.getAttributeValue("", "type");
        String attributeValue3 = xmlPullParser.getAttributeValue("", ATTR_FILENAME);
        int i = Integer.parseInt(xmlPullParser.getAttributeValue("", ATTR_RENDERING_MODE));
        if (MediaImageItem.class.getSimpleName().equals(attributeValue2)) {
            return new MediaImageItem(this, attributeValue, attributeValue3, Long.parseLong(xmlPullParser.getAttributeValue("", "duration")), i);
        }
        if (MediaVideoItem.class.getSimpleName().equals(attributeValue2)) {
            MediaVideoItem mediaVideoItem = new MediaVideoItem(this, attributeValue, attributeValue3, i, Long.parseLong(xmlPullParser.getAttributeValue("", ATTR_BEGIN_TIME)), Long.parseLong(xmlPullParser.getAttributeValue("", ATTR_END_TIME)), Integer.parseInt(xmlPullParser.getAttributeValue("", "volume")), Boolean.parseBoolean(xmlPullParser.getAttributeValue("", ATTR_MUTED)), xmlPullParser.getAttributeValue("", ATTR_AUDIO_WAVEFORM_FILENAME));
            MediaVideoItem mediaVideoItem2 = mediaVideoItem;
            mediaVideoItem2.setExtractBoundaries(Long.parseLong(xmlPullParser.getAttributeValue("", ATTR_BEGIN_TIME)), Long.parseLong(xmlPullParser.getAttributeValue("", ATTR_END_TIME)));
            mediaVideoItem2.setVolume(Integer.parseInt(xmlPullParser.getAttributeValue("", "volume")));
            return mediaVideoItem;
        }
        throw new IllegalArgumentException("Unknown media item type: " + attributeValue2);
    }

    private Transition parseTransition(XmlPullParser xmlPullParser, List<String> list) {
        MediaItem mediaItem;
        MediaItem mediaItem2;
        MediaItem mediaItem3;
        Transition transitionFadeBlack;
        String attributeValue = xmlPullParser.getAttributeValue("", "id");
        String attributeValue2 = xmlPullParser.getAttributeValue("", "type");
        long j = Long.parseLong(xmlPullParser.getAttributeValue("", "duration"));
        int i = Integer.parseInt(xmlPullParser.getAttributeValue("", ATTR_BEHAVIOR));
        String attributeValue3 = xmlPullParser.getAttributeValue("", ATTR_BEFORE_MEDIA_ITEM_ID);
        if (attributeValue3 == null) {
            mediaItem = null;
        } else {
            if (list.contains(attributeValue3)) {
                return null;
            }
            mediaItem = getMediaItem(attributeValue3);
        }
        String attributeValue4 = xmlPullParser.getAttributeValue("", ATTR_AFTER_MEDIA_ITEM_ID);
        if (attributeValue4 == null) {
            mediaItem2 = null;
        } else {
            if (list.contains(attributeValue4)) {
                return null;
            }
            mediaItem2 = getMediaItem(attributeValue4);
        }
        if (TransitionAlpha.class.getSimpleName().equals(attributeValue2)) {
            mediaItem3 = mediaItem;
            transitionFadeBlack = new TransitionAlpha(attributeValue, mediaItem2, mediaItem, j, i, xmlPullParser.getAttributeValue("", ATTR_MASK), Integer.parseInt(xmlPullParser.getAttributeValue("", ATTR_BLENDING)), Boolean.getBoolean(xmlPullParser.getAttributeValue("", ATTR_INVERT)));
        } else {
            mediaItem3 = mediaItem;
            if (TransitionCrossfade.class.getSimpleName().equals(attributeValue2)) {
                transitionFadeBlack = new TransitionCrossfade(attributeValue, mediaItem2, mediaItem3, j, i);
            } else if (TransitionSliding.class.getSimpleName().equals(attributeValue2)) {
                transitionFadeBlack = new TransitionSliding(attributeValue, mediaItem2, mediaItem3, j, i, Integer.parseInt(xmlPullParser.getAttributeValue("", ATTR_DIRECTION)));
            } else if (TransitionFadeBlack.class.getSimpleName().equals(attributeValue2)) {
                transitionFadeBlack = new TransitionFadeBlack(attributeValue, mediaItem2, mediaItem3, j, i);
            } else {
                throw new IllegalArgumentException("Invalid transition type: " + attributeValue2);
            }
        }
        Transition transition = transitionFadeBlack;
        if (Boolean.parseBoolean(xmlPullParser.getAttributeValue("", ATTR_IS_TRANSITION_GENERATED))) {
            String attributeValue5 = xmlPullParser.getAttributeValue("", ATTR_GENERATED_TRANSITION_CLIP);
            if (new File(attributeValue5).exists()) {
                transition.setFilename(attributeValue5);
            } else {
                transition.setFilename(null);
            }
        }
        MediaItem mediaItem4 = mediaItem3;
        if (mediaItem4 != null) {
            mediaItem4.setBeginTransition(transition);
        }
        if (mediaItem2 != null) {
            mediaItem2.setEndTransition(transition);
        }
        return transition;
    }

    private Overlay parseOverlay(XmlPullParser xmlPullParser, MediaItem mediaItem) {
        String attributeValue = xmlPullParser.getAttributeValue("", "id");
        String attributeValue2 = xmlPullParser.getAttributeValue("", "type");
        long j = Long.parseLong(xmlPullParser.getAttributeValue("", "duration"));
        long j2 = Long.parseLong(xmlPullParser.getAttributeValue("", ATTR_BEGIN_TIME));
        if (OverlayFrame.class.getSimpleName().equals(attributeValue2)) {
            OverlayFrame overlayFrame = new OverlayFrame(mediaItem, attributeValue, xmlPullParser.getAttributeValue("", ATTR_FILENAME), j2, j);
            String attributeValue3 = xmlPullParser.getAttributeValue("", ATTR_OVERLAY_RGB_FILENAME);
            if (attributeValue3 != null) {
                OverlayFrame overlayFrame2 = overlayFrame;
                overlayFrame2.setFilename(attributeValue3);
                int i = Integer.parseInt(xmlPullParser.getAttributeValue("", ATTR_OVERLAY_FRAME_WIDTH));
                int i2 = Integer.parseInt(xmlPullParser.getAttributeValue("", ATTR_OVERLAY_FRAME_HEIGHT));
                overlayFrame2.setOverlayFrameWidth(i);
                overlayFrame2.setOverlayFrameHeight(i2);
                overlayFrame2.setResizedRGBSize(Integer.parseInt(xmlPullParser.getAttributeValue("", ATTR_OVERLAY_RESIZED_RGB_FRAME_WIDTH)), Integer.parseInt(xmlPullParser.getAttributeValue("", ATTR_OVERLAY_RESIZED_RGB_FRAME_HEIGHT)));
            }
            return overlayFrame;
        }
        throw new IllegalArgumentException("Invalid overlay type: " + attributeValue2);
    }

    private Effect parseEffect(XmlPullParser xmlPullParser, MediaItem mediaItem) {
        String attributeValue = xmlPullParser.getAttributeValue("", "id");
        String attributeValue2 = xmlPullParser.getAttributeValue("", "type");
        long j = Long.parseLong(xmlPullParser.getAttributeValue("", "duration"));
        long j2 = Long.parseLong(xmlPullParser.getAttributeValue("", ATTR_BEGIN_TIME));
        if (EffectColor.class.getSimpleName().equals(attributeValue2)) {
            int i = Integer.parseInt(xmlPullParser.getAttributeValue("", "color_type"));
            return new EffectColor(mediaItem, attributeValue, j2, j, i, (i == 1 || i == 2) ? Integer.parseInt(xmlPullParser.getAttributeValue("", ATTR_COLOR_EFFECT_VALUE)) : 0);
        }
        if (EffectKenBurns.class.getSimpleName().equals(attributeValue2)) {
            return new EffectKenBurns(mediaItem, attributeValue, new Rect(Integer.parseInt(xmlPullParser.getAttributeValue("", ATTR_START_RECT_LEFT)), Integer.parseInt(xmlPullParser.getAttributeValue("", ATTR_START_RECT_TOP)), Integer.parseInt(xmlPullParser.getAttributeValue("", ATTR_START_RECT_RIGHT)), Integer.parseInt(xmlPullParser.getAttributeValue("", ATTR_START_RECT_BOTTOM))), new Rect(Integer.parseInt(xmlPullParser.getAttributeValue("", ATTR_END_RECT_LEFT)), Integer.parseInt(xmlPullParser.getAttributeValue("", ATTR_END_RECT_TOP)), Integer.parseInt(xmlPullParser.getAttributeValue("", ATTR_END_RECT_RIGHT)), Integer.parseInt(xmlPullParser.getAttributeValue("", ATTR_END_RECT_BOTTOM))), j2, j);
        }
        throw new IllegalArgumentException("Invalid effect type: " + attributeValue2);
    }

    private AudioTrack parseAudioTrack(XmlPullParser xmlPullParser) throws IOException {
        return new AudioTrack(this, xmlPullParser.getAttributeValue("", "id"), xmlPullParser.getAttributeValue("", ATTR_FILENAME), Long.parseLong(xmlPullParser.getAttributeValue("", ATTR_START_TIME)), Long.parseLong(xmlPullParser.getAttributeValue("", ATTR_BEGIN_TIME)), Long.parseLong(xmlPullParser.getAttributeValue("", ATTR_END_TIME)), Boolean.parseBoolean(xmlPullParser.getAttributeValue("", ATTR_LOOP)), Integer.parseInt(xmlPullParser.getAttributeValue("", "volume")), Boolean.parseBoolean(xmlPullParser.getAttributeValue("", ATTR_MUTED)), Boolean.parseBoolean(xmlPullParser.getAttributeValue("", ATTR_DUCK_ENABLED)), Integer.parseInt(xmlPullParser.getAttributeValue("", ATTR_DUCK_THRESHOLD)), Integer.parseInt(xmlPullParser.getAttributeValue("", ATTR_DUCKED_TRACK_VOLUME)), xmlPullParser.getAttributeValue("", ATTR_AUDIO_WAVEFORM_FILENAME));
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x019d  */
    @Override // android.media.videoeditor.VideoEditor
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void save() throws java.io.IOException {
        /*
            Method dump skipped, instruction units count: 1203
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.videoeditor.VideoEditorImpl.save():void");
    }

    @Override // android.media.videoeditor.VideoEditor
    public void setAspectRatio(int i) {
        this.mAspectRatio = i;
        this.mMANativeHelper.setGeneratePreview(true);
        Iterator<Transition> it = this.mTransitions.iterator();
        while (it.hasNext()) {
            it.next().invalidate();
        }
        Iterator<MediaItem> it2 = this.mMediaItems.iterator();
        while (it2.hasNext()) {
            Iterator<Overlay> it3 = it2.next().getAllOverlays().iterator();
            while (it3.hasNext()) {
                ((OverlayFrame) it3.next()).invalidateGeneratedFiles();
            }
        }
    }

    @Override // android.media.videoeditor.VideoEditor
    public void startPreview(SurfaceHolder surfaceHolder, long j, long j2, boolean z, int i, VideoEditor.PreviewProgressListener previewProgressListener) {
        if (surfaceHolder == null) {
            throw new IllegalArgumentException();
        }
        Surface surface = surfaceHolder.getSurface();
        if (surface == null) {
            throw new IllegalArgumentException("Surface could not be retrieved from surface holder");
        }
        if (!surface.isValid()) {
            throw new IllegalStateException("Surface is not valid");
        }
        if (previewProgressListener == null) {
            throw new IllegalArgumentException();
        }
        if (j >= this.mDurationMs) {
            throw new IllegalArgumentException("Requested time not correct");
        }
        if (j < 0) {
            throw new IllegalArgumentException("Requested time not correct");
        }
        if (!this.mPreviewInProgress) {
            try {
                if (!lock(500L)) {
                    throw new IllegalStateException("Timeout waiting for semaphore");
                }
                if (this.mMANativeHelper == null) {
                    throw new IllegalStateException("The video editor is not initialized");
                }
                if (this.mMediaItems.size() > 0) {
                    this.mPreviewInProgress = true;
                    this.mMANativeHelper.previewStoryBoard(this.mMediaItems, this.mTransitions, this.mAudioTracks, null);
                    this.mMANativeHelper.doPreview(surface, j, j2, z, i, previewProgressListener);
                    return;
                }
                return;
            } catch (InterruptedException unused) {
                Log.w(TAG, "The thread was interrupted", new Throwable());
                throw new IllegalStateException("The thread was interrupted");
            }
        }
        throw new IllegalStateException("Preview already in progress");
    }

    @Override // android.media.videoeditor.VideoEditor
    public long stopPreview() {
        if (!this.mPreviewInProgress) {
            return 0L;
        }
        try {
            return this.mMANativeHelper.stopPreview();
        } finally {
            this.mPreviewInProgress = false;
            unlock();
        }
    }

    private void removeAdjacentTransitions(MediaItem mediaItem) {
        Transition beginTransition = mediaItem.getBeginTransition();
        if (beginTransition != null) {
            if (beginTransition.getAfterMediaItem() != null) {
                beginTransition.getAfterMediaItem().setEndTransition(null);
            }
            beginTransition.invalidate();
            this.mTransitions.remove(beginTransition);
        }
        Transition endTransition = mediaItem.getEndTransition();
        if (endTransition != null) {
            if (endTransition.getBeforeMediaItem() != null) {
                endTransition.getBeforeMediaItem().setBeginTransition(null);
            }
            endTransition.invalidate();
            this.mTransitions.remove(endTransition);
        }
        mediaItem.setBeginTransition(null);
        mediaItem.setEndTransition(null);
    }

    private void removeTransitionBefore(int i) {
        MediaItem mediaItem = this.mMediaItems.get(i);
        Iterator<Transition> it = this.mTransitions.iterator();
        while (it.hasNext()) {
            Transition next = it.next();
            if (next.getBeforeMediaItem() == mediaItem) {
                this.mMANativeHelper.setGeneratePreview(true);
                it.remove();
                next.invalidate();
                mediaItem.setBeginTransition(null);
                if (i > 0) {
                    this.mMediaItems.get(i - 1).setEndTransition(null);
                    return;
                }
                return;
            }
        }
    }

    private void removeTransitionAfter(int i) {
        MediaItem mediaItem = this.mMediaItems.get(i);
        Iterator<Transition> it = this.mTransitions.iterator();
        while (it.hasNext()) {
            Transition next = it.next();
            if (next.getAfterMediaItem() == mediaItem) {
                this.mMANativeHelper.setGeneratePreview(true);
                it.remove();
                next.invalidate();
                mediaItem.setEndTransition(null);
                if (i < this.mMediaItems.size() - 1) {
                    this.mMediaItems.get(i + 1).setBeginTransition(null);
                    return;
                }
                return;
            }
        }
    }

    private void computeTimelineDuration() {
        this.mDurationMs = 0L;
        int size = this.mMediaItems.size();
        for (int i = 0; i < size; i++) {
            MediaItem mediaItem = this.mMediaItems.get(i);
            this.mDurationMs += mediaItem.getTimelineDuration();
            if (mediaItem.getEndTransition() != null && i < size - 1) {
                this.mDurationMs -= mediaItem.getEndTransition().getDuration();
            }
        }
    }

    private void generateProjectThumbnail() {
        Bitmap thumbnail;
        if (new File(this.mProjectPath + "/" + VideoEditor.THUMBNAIL_FILENAME).exists()) {
            new File(this.mProjectPath + "/" + VideoEditor.THUMBNAIL_FILENAME).delete();
        }
        if (this.mMediaItems.size() > 0) {
            MediaItem mediaItem = this.mMediaItems.get(0);
            int width = (mediaItem.getWidth() * 480) / mediaItem.getHeight();
            String filename = mediaItem.getFilename();
            if (mediaItem instanceof MediaVideoItem) {
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(filename);
                Bitmap frameAtTime = mediaMetadataRetriever.getFrameAtTime();
                mediaMetadataRetriever.release();
                if (frameAtTime == null) {
                    throw new IllegalArgumentException("Thumbnail extraction from " + filename + " failed");
                }
                thumbnail = Bitmap.createScaledBitmap(frameAtTime, width, 480, true);
            } else {
                try {
                    thumbnail = mediaItem.getThumbnail(width, 480, 500L);
                } catch (IOException unused) {
                    throw new IllegalArgumentException("IO Error creating project thumbnail");
                } catch (IllegalArgumentException unused2) {
                    throw new IllegalArgumentException("Project thumbnail extraction from " + filename + " failed");
                }
            }
            try {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(this.mProjectPath + "/" + VideoEditor.THUMBNAIL_FILENAME);
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException unused3) {
                    throw new IllegalArgumentException("Error creating project thumbnail");
                }
            } finally {
                thumbnail.recycle();
            }
        }
    }

    @Override // android.media.videoeditor.VideoEditor
    public void clearSurface(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalArgumentException("Invalid surface holder");
        }
        Surface surface = surfaceHolder.getSurface();
        if (surface == null) {
            throw new IllegalArgumentException("Surface could not be retrieved from surface holder");
        }
        if (!surface.isValid()) {
            throw new IllegalStateException("Surface is not valid");
        }
        MediaArtistNativeHelper mediaArtistNativeHelper = this.mMANativeHelper;
        if (mediaArtistNativeHelper != null) {
            mediaArtistNativeHelper.clearPreviewSurface(surface);
        } else {
            Log.w(TAG, "Native helper was not ready!");
        }
    }

    private void lock() throws InterruptedException {
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "lock: grabbing semaphore", new Throwable());
        }
        this.mLock.acquire();
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "lock: grabbed semaphore");
        }
    }

    private boolean lock(long j) throws InterruptedException {
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "lock: grabbing semaphore with timeout " + j, new Throwable());
        }
        boolean zTryAcquire = this.mLock.tryAcquire(j, TimeUnit.MILLISECONDS);
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "lock: grabbed semaphore status " + zTryAcquire);
        }
        return zTryAcquire;
    }

    private void unlock() {
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "unlock: releasing semaphore");
        }
        this.mLock.release();
    }

    private static void dumpHeap(String str) throws Exception {
        System.gc();
        System.runFinalization();
        Thread.sleep(1000L);
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String string = Environment.getExternalStorageDirectory().toString();
            if (new File(string + "/" + str + ".dump").exists()) {
                new File(string + "/" + str + ".dump").delete();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(string + "/" + str + ".dump");
            Debug.dumpNativeHeap(fileOutputStream.getFD());
            fileOutputStream.close();
        }
    }
}
