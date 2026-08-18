package com.amap.api.maps.model;

/* JADX INFO: loaded from: classes2.dex */
public class CustomMapStyleOptions {
    private String styleDataPath = null;
    private byte[] styleData = null;
    private String styleTexturePath = null;
    private byte[] styleTextureData = null;
    private String styleId = null;
    private boolean enable = true;
    private byte[] styleExtraData = null;
    private String styleExtraPath = null;
    private String styleDataOverseaPath = null;
    private byte[] styleDataOversea = null;
    private String styleResDataPath = null;
    private byte[] styleResData = null;

    public CustomMapStyleOptions setStyleResData(byte[] bArr) {
        return this;
    }

    public CustomMapStyleOptions setStyleResDataPath(String str) {
        return this;
    }

    public String getStyleDataPath() {
        return this.styleDataPath;
    }

    public CustomMapStyleOptions setStyleDataPath(String str) {
        this.styleDataPath = str;
        return this;
    }

    public String getStyleTexturePath() {
        return this.styleTexturePath;
    }

    public CustomMapStyleOptions setStyleTexturePath(String str) {
        this.styleTexturePath = str;
        return this;
    }

    public byte[] getStyleData() {
        return this.styleData;
    }

    public CustomMapStyleOptions setStyleData(byte[] bArr) {
        this.styleData = bArr;
        return this;
    }

    public byte[] getStyleTextureData() {
        return this.styleTextureData;
    }

    public CustomMapStyleOptions setStyleTextureData(byte[] bArr) {
        this.styleTextureData = bArr;
        return this;
    }

    public String getStyleId() {
        return this.styleId;
    }

    public CustomMapStyleOptions setStyleId(String str) {
        this.styleId = str;
        return this;
    }

    public boolean isEnable() {
        return this.enable;
    }

    public CustomMapStyleOptions setEnable(boolean z) {
        this.enable = z;
        return this;
    }

    public byte[] getStyleExtraData() {
        return this.styleExtraData;
    }

    public CustomMapStyleOptions setStyleExtraData(byte[] bArr) {
        this.styleExtraData = bArr;
        return this;
    }

    public String getStyleExtraPath() {
        return this.styleExtraPath;
    }

    public CustomMapStyleOptions setStyleExtraPath(String str) {
        this.styleExtraPath = str;
        return this;
    }

    public String getStyleDataOverseaPath() {
        return this.styleDataOverseaPath;
    }

    public CustomMapStyleOptions setStyleDataOverseaPath(String str) {
        this.styleDataOverseaPath = str;
        return this;
    }

    public byte[] getStyleDataOversea() {
        return this.styleDataOversea;
    }

    public CustomMapStyleOptions setStyleDataOversea(byte[] bArr) {
        this.styleDataOversea = bArr;
        return this;
    }

    public String getStyleResDataPath() {
        return this.styleResDataPath;
    }

    public byte[] getStyleResData() {
        return this.styleResData;
    }
}
