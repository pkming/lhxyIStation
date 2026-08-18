package com.amap.api.services.core;

import android.os.Parcel;
import android.os.Parcelable;
import com.amap.api.services.poisearch.Business;
import com.amap.api.services.poisearch.IndoorDataV2;
import com.amap.api.services.poisearch.Photo;
import com.amap.api.services.poisearch.PoiNavi;
import com.amap.api.services.poisearch.SubPoiItemV2;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class PoiItemV2 implements Parcelable {
    public static final Parcelable.Creator<PoiItem> CREATOR = new Parcelable.Creator<PoiItem>() { // from class: com.amap.api.services.core.PoiItemV2.1
        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ PoiItem createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ PoiItem[] newArray(int i) {
            return a(i);
        }

        private static PoiItem a(Parcel parcel) {
            return new PoiItem(parcel);
        }

        private static PoiItem[] a(int i) {
            return new PoiItem[i];
        }
    };
    private String a;
    private String b;
    private String c;
    private String d;
    private final LatLonPoint e;
    private final String f;
    private final String g;
    private String h;
    private String i;
    private String j;
    private String k;
    private String l;
    private List<SubPoiItemV2> m;
    private Business n;
    private IndoorDataV2 o;
    private PoiNavi p;
    private List<Photo> q;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public PoiItemV2(String str, LatLonPoint latLonPoint, String str2, String str3) {
        this.d = "";
        this.m = new ArrayList();
        this.q = new ArrayList();
        this.a = str;
        this.e = latLonPoint;
        this.f = str2;
        this.g = str3;
    }

    public String getAdName() {
        return this.k;
    }

    public void setAdName(String str) {
        this.k = str;
    }

    public String getCityName() {
        return this.j;
    }

    public void setCityName(String str) {
        this.j = str;
    }

    public String getProvinceName() {
        return this.i;
    }

    public void setProvinceName(String str) {
        this.i = str;
    }

    public String getTypeDes() {
        return this.d;
    }

    public void setTypeDes(String str) {
        this.d = str;
    }

    public String getAdCode() {
        return this.b;
    }

    public void setAdCode(String str) {
        this.b = str;
    }

    public String getPoiId() {
        return this.a;
    }

    public String getTitle() {
        return this.f;
    }

    public String getSnippet() {
        return this.g;
    }

    public LatLonPoint getLatLonPoint() {
        return this.e;
    }

    public String getCityCode() {
        return this.c;
    }

    public void setCityCode(String str) {
        this.c = str;
    }

    public void setProvinceCode(String str) {
        this.l = str;
    }

    public String getProvinceCode() {
        return this.l;
    }

    public String getTypeCode() {
        return this.h;
    }

    public void setTypeCode(String str) {
        this.h = str;
    }

    public void setSubPois(List<SubPoiItemV2> list) {
        this.m = list;
    }

    public List<SubPoiItemV2> getSubPois() {
        return this.m;
    }

    public void setBusiness(Business business) {
        this.n = business;
    }

    public Business getBusiness() {
        return this.n;
    }

    public void setIndoorData(IndoorDataV2 indoorDataV2) {
        this.o = indoorDataV2;
    }

    public IndoorDataV2 getIndoorData() {
        return this.o;
    }

    public void setPoiNavi(PoiNavi poiNavi) {
        this.p = poiNavi;
    }

    public PoiNavi getPoiNavi() {
        return this.p;
    }

    public List<Photo> getPhotos() {
        return this.q;
    }

    public void setPhotos(List<Photo> list) {
        this.q = list;
    }

    protected PoiItemV2(Parcel parcel) {
        this.d = "";
        this.m = new ArrayList();
        this.q = new ArrayList();
        this.a = parcel.readString();
        this.b = parcel.readString();
        this.d = parcel.readString();
        this.e = (LatLonPoint) parcel.readValue(LatLonPoint.class.getClassLoader());
        this.f = parcel.readString();
        this.g = parcel.readString();
        this.c = parcel.readString();
        this.i = parcel.readString();
        this.j = parcel.readString();
        this.k = parcel.readString();
        this.l = parcel.readString();
        this.h = parcel.readString();
        this.m = parcel.readArrayList(SubPoiItemV2.class.getClassLoader());
        this.n = (Business) parcel.readValue(Business.class.getClassLoader());
        this.o = (IndoorDataV2) parcel.readValue(IndoorDataV2.class.getClassLoader());
        this.p = (PoiNavi) parcel.readValue(PoiNavi.class.getClassLoader());
        this.q = parcel.createTypedArrayList(Photo.CREATOR);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.a);
        parcel.writeString(this.b);
        parcel.writeString(this.d);
        parcel.writeValue(this.e);
        parcel.writeString(this.f);
        parcel.writeString(this.g);
        parcel.writeString(this.c);
        parcel.writeString(this.i);
        parcel.writeString(this.j);
        parcel.writeString(this.k);
        parcel.writeString(this.l);
        parcel.writeString(this.h);
        parcel.writeList(this.m);
        parcel.writeValue(this.n);
        parcel.writeValue(this.o);
        parcel.writeValue(this.p);
        parcel.writeTypedList(this.q);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PoiItemV2 poiItemV2 = (PoiItemV2) obj;
        String str = this.a;
        if (str == null) {
            if (poiItemV2.a != null) {
                return false;
            }
        } else if (!str.equals(poiItemV2.a)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        String str = this.a;
        return (str == null ? 0 : str.hashCode()) + 31;
    }

    public String toString() {
        return this.f;
    }
}
