package android.media.effect.effects;

import android.filterpacks.imageproc.CropRectFilter;
import android.media.effect.EffectContext;
import android.media.effect.SizeChangeEffect;

/* JADX INFO: loaded from: classes.dex */
public class CropEffect extends SizeChangeEffect {
    public CropEffect(EffectContext effectContext, String str) {
        super(effectContext, str, CropRectFilter.class, "image", "image", new Object[0]);
    }
}
