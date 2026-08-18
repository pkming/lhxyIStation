package com.amap.api.maps.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.autonavi.base.ae.gmap.bean.TileProviderInner;

/* JADX INFO: loaded from: classes2.dex */
public class MVTTileOverlayOptions extends BaseOptions implements Parcelable {
    public static final Parcelable.Creator<MVTTileOverlayOptions> CREATOR = new Parcelable.Creator<MVTTileOverlayOptions>() { // from class: com.amap.api.maps.model.MVTTileOverlayOptions.1
        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ MVTTileOverlayOptions createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ MVTTileOverlayOptions[] newArray(int i) {
            return a(i);
        }

        private static MVTTileOverlayOptions a(Parcel parcel) {
            TileProvider tileProvider = (TileProvider) parcel.readValue(TileProvider.class.getClassLoader());
            MVTTileOverlayOptions mVTTileOverlayOptions = new MVTTileOverlayOptions(parcel.readString(), parcel.readString(), parcel.readString());
            if (tileProvider != null) {
                mVTTileOverlayOptions.setTileProvider(tileProvider);
            }
            return mVTTileOverlayOptions;
        }

        private static MVTTileOverlayOptions[] a(int i) {
            return new MVTTileOverlayOptions[i];
        }
    };
    private String layerId;
    private TileProvider tileProvider;
    private TileProviderInner tileProviderInner;
    private boolean visible;
    private float zIndex;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public MVTTileOverlayOptions(String str, String str2, String str3) {
        this.type = "MVTTileOverlayOptions";
        this.tileProvider = new MVTTileProvider(str, str2, str3);
        this.tileProviderInner = new TileProviderInner(this.tileProvider);
        this.layerId = str3;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeValue(this.tileProviderInner);
    }

    public void setTileProvider(TileProvider tileProvider) {
        this.tileProvider = tileProvider;
    }

    public TileProviderInner getTileProviderInner() {
        return this.tileProviderInner;
    }

    public void setZIndex(float f) {
        this.zIndex = f;
    }

    public float getZIndex() {
        return this.zIndex;
    }

    public void setVisible(boolean z) {
        this.visible = z;
    }

    public boolean visible() {
        return this.visible;
    }

    public static class Builder {
        private String id;
        private String key;
        private String url;
        private float zIndex = 0.0f;
        private boolean visible = true;

        public Builder url(String str) {
            this.url = str;
            return this;
        }

        public Builder key(String str) {
            this.key = str;
            return this;
        }

        public Builder id(String str) {
            this.id = str;
            return this;
        }

        public Builder zIndex(float f) {
            this.zIndex = f;
            return this;
        }

        public Builder visible(boolean z) {
            this.visible = z;
            return this;
        }

        public MVTTileOverlayOptions build() {
            MVTTileOverlayOptions mVTTileOverlayOptions = new MVTTileOverlayOptions(this.url, this.key, this.id);
            mVTTileOverlayOptions.setZIndex(this.zIndex);
            mVTTileOverlayOptions.setVisible(this.visible);
            return mVTTileOverlayOptions;
        }
    }
}
