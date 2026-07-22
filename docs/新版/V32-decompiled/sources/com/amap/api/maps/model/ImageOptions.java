package com.amap.api.maps.model;

/* JADX INFO: loaded from: classes2.dex */
public class ImageOptions {
    public int color;
    public String content;
    public int fontSize;
    public float radius;
    public double[] rgba;
    public int type;

    public enum ShapeType {
        DEFAULT(0),
        CIRCLE(1),
        TEXT(2);

        private int index;

        ShapeType(int i) {
            this.index = i;
        }

        public final int value() {
            return this.index;
        }
    }
}
