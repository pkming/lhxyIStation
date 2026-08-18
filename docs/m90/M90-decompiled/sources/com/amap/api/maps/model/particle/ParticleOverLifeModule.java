package com.amap.api.maps.model.particle;

/* JADX INFO: loaded from: classes2.dex */
public class ParticleOverLifeModule {
    private static final int TYPE_COLOR = 3;
    private static final int TYPE_ROTATE = 1;
    private static final int TYPE_SCALE = 2;
    private static final int TYPE_VEL = 0;
    private ColorGenerate colorGenerate;
    private Object colorGenerateObject;
    private RotationOverLife rotateOverLife;
    private Object rotateOverLifeObject;
    private SizeOverLife sizeOverLife;
    private Object sizeOverLifeObject;
    private Object velocityOverLifeObject;
    private VelocityGenerate overLife = null;
    private VelocityGenerate velocityOverLife = null;
    private boolean isNeedReloadVelocityGenerate = false;
    private boolean isNeedReloadRotationOverLife = false;
    private boolean isNeedReloadSizeOverLife = false;
    private boolean isNeedReloadColorGenerate = false;

    public void setVelocityOverLife(VelocityGenerate velocityGenerate) {
        this.overLife = velocityGenerate;
        this.velocityOverLife = velocityGenerate;
        this.velocityOverLifeObject = velocityGenerate;
        this.isNeedReloadVelocityGenerate = true;
    }

    public void setRotateOverLife(RotationOverLife rotationOverLife) {
        this.rotateOverLife = rotationOverLife;
        this.rotateOverLifeObject = rotationOverLife;
        this.isNeedReloadRotationOverLife = true;
    }

    public void setSizeOverLife(SizeOverLife sizeOverLife) {
        this.sizeOverLife = sizeOverLife;
        this.sizeOverLifeObject = sizeOverLife;
        this.isNeedReloadSizeOverLife = true;
    }

    public void setColorGenerate(ColorGenerate colorGenerate) {
        this.colorGenerate = colorGenerate;
        this.colorGenerateObject = colorGenerate;
        this.isNeedReloadColorGenerate = true;
    }
}
