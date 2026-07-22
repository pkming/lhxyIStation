package android.media;

/* JADX INFO: loaded from: classes.dex */
public interface VolumeController {
    void postHasNewRemotePlaybackInfo();

    void postRemoteSliderVisibility(boolean z);

    void postRemoteVolumeChanged(int i, int i2);
}
