package android.media.effect.effects;

import android.filterpacks.imageproc.BrightnessFilter;
import android.media.effect.EffectContext;
import android.media.effect.SingleFilterEffect;

/* JADX INFO: loaded from: classes.dex */
public class BrightnessEffect extends SingleFilterEffect {
    public BrightnessEffect(EffectContext effectContext, String str) {
        super(effectContext, str, BrightnessFilter.class, "image", "image", new Object[0]);
    }
}
