package android.media.effect.effects;

import android.filterpacks.imageproc.RotateFilter;
import android.media.effect.EffectContext;
import android.media.effect.SizeChangeEffect;

/* JADX INFO: loaded from: classes.dex */
public class RotateEffect extends SizeChangeEffect {
    public RotateEffect(EffectContext effectContext, String str) {
        super(effectContext, str, RotateFilter.class, "image", "image", new Object[0]);
    }
}
