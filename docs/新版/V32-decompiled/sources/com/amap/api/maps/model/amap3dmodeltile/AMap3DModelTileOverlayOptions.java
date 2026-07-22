package com.amap.api.maps.model.amap3dmodeltile;

import android.os.Parcel;
import android.os.Parcelable;
import com.amap.api.maps.model.BaseOptions;
import com.amap.api.maps.model.TileProvider;
import com.autonavi.base.ae.gmap.bean.TileProviderInner;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class AMap3DModelTileOverlayOptions extends BaseOptions implements Parcelable {
    public static final Parcelable.Creator<AMap3DModelTileOverlayOptions> CREATOR = new Parcelable.Creator<AMap3DModelTileOverlayOptions>() { // from class: com.amap.api.maps.model.amap3dmodeltile.AMap3DModelTileOverlayOptions.1
        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ AMap3DModelTileOverlayOptions createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ AMap3DModelTileOverlayOptions[] newArray(int i) {
            return a(i);
        }

        private static AMap3DModelTileOverlayOptions a(Parcel parcel) {
            TileProvider tileProvider = (TileProvider) parcel.readValue(TileProvider.class.getClassLoader());
            ArrayList arrayList = parcel.readArrayList(AMap3DTileBuildingColor.class.getClassLoader());
            ArrayList arrayList2 = parcel.readArrayList(AMap3DTileBuildingMaterialOptions.class.getClassLoader());
            AMap3DModelTileOverlayOptions aMap3DModelTileOverlayOptions = new AMap3DModelTileOverlayOptions();
            if (tileProvider != null) {
                aMap3DModelTileOverlayOptions.setTileProvider(tileProvider);
            }
            boolean[] zArr = new boolean[1];
            parcel.readBooleanArray(zArr);
            aMap3DModelTileOverlayOptions.setVisible(zArr[0]);
            aMap3DModelTileOverlayOptions.setZIndex(parcel.readFloat());
            aMap3DModelTileOverlayOptions.setCustomBuildingColors(arrayList);
            aMap3DModelTileOverlayOptions.setCustomBuildingMaterialOptions(arrayList2);
            return aMap3DModelTileOverlayOptions;
        }

        private static AMap3DModelTileOverlayOptions[] a(int i) {
            return new AMap3DModelTileOverlayOptions[i];
        }
    };
    private TileProvider tileProvider;
    private TileProviderInner tileProviderInner;
    private float zIndex = 0.0f;
    private boolean visible = true;
    private List<AMap3DTileBuildingColor> customBuildingColors = new ArrayList();
    private List<AMap3DTileBuildingMaterialOptions> customBuildingMaterialOptions = new ArrayList();

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public AMap3DModelTileOverlayOptions() {
        this.type = "AMap3DModelTileOverlayOptions";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeValue(this.tileProviderInner);
        parcel.writeBooleanArray(new boolean[]{this.visible});
        parcel.writeFloat(this.zIndex);
        parcel.writeList(this.customBuildingColors);
        parcel.writeList(this.customBuildingMaterialOptions);
    }

    public void setTileProvider(TileProvider tileProvider) {
        this.tileProvider = tileProvider;
        this.tileProviderInner = new TileProviderInner(this.tileProvider);
    }

    public TileProviderInner getTileProviderInner() {
        return this.tileProviderInner;
    }

    public void setCustomBuildingColors(List<AMap3DTileBuildingColor> list) {
        this.customBuildingColors = list;
    }

    public List<AMap3DTileBuildingColor> getCustomBuildingColors() {
        return this.customBuildingColors;
    }

    public void setCustomBuildingMaterialOptions(List<AMap3DTileBuildingMaterialOptions> list) {
        this.customBuildingMaterialOptions = list;
    }

    public List<AMap3DTileBuildingMaterialOptions> getCustomBuildingMaterialOptions() {
        return this.customBuildingMaterialOptions;
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
}
