package com.amap.api.maps.model;

/* JADX INFO: loaded from: classes2.dex */
public class AMapCameraInfo {
    private float aspectRatio;
    private float fov;
    private float positionX;
    private float positionY;
    private float positionZ;
    private float rotate;

    public AMapCameraInfo(float f, float f2, float f3, float f4, float f5, float f6) {
        this.fov = 0.0f;
        this.aspectRatio = 1.0f;
        this.rotate = 0.0f;
        this.positionX = 0.0f;
        this.positionY = 0.0f;
        this.positionZ = 0.0f;
        this.fov = f;
        this.aspectRatio = f2;
        this.rotate = f3;
        this.positionX = f4;
        this.positionY = f5;
        this.positionZ = f6;
    }

    public float getFov() {
        return this.fov;
    }

    public float getAspectRatio() {
        return this.aspectRatio;
    }

    public float getRotate() {
        return this.rotate;
    }

    public float getX() {
        return this.positionX;
    }

    public float getY() {
        return this.positionY;
    }

    public float getZ() {
        return this.positionZ;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[fov:").append(this.fov).append(" ");
        sb.append("aspectRatio:").append(this.aspectRatio).append(" ");
        sb.append("rotate:").append(this.rotate).append(" ");
        sb.append("pos_x:").append(this.positionX).append(" ");
        sb.append("pos_y:").append(this.positionY).append(" ");
        sb.append("pos_z:").append(this.positionZ).append("]");
        return sb.toString();
    }
}
