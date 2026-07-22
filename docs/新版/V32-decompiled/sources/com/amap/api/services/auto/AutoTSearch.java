package com.amap.api.services.auto;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.amap.api.col.p0003sl.hh;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.interfaces.IAutoTSearch;

/* JADX INFO: loaded from: classes2.dex */
public class AutoTSearch {
    private IAutoTSearch a;

    public interface OnChargeStationListener {
        void onChargeStationSearched(AutoTChargeStationResult autoTChargeStationResult, int i);
    }

    public AutoTSearch(Context context) throws AMapException {
        if (this.a == null) {
            try {
                this.a = new hh(context);
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof AMapException) {
                    throw ((AMapException) e);
                }
            }
        }
    }

    public void setQuery(Query query) {
        IAutoTSearch iAutoTSearch = this.a;
        if (iAutoTSearch == null) {
            return;
        }
        iAutoTSearch.setQuery(query);
    }

    public AutoTChargeStationResult searchChargeStation() throws AMapException {
        IAutoTSearch iAutoTSearch = this.a;
        if (iAutoTSearch == null) {
            return null;
        }
        return iAutoTSearch.searchChargeStation();
    }

    public void searchChargeStationAsync() throws AMapException {
        IAutoTSearch iAutoTSearch = this.a;
        if (iAutoTSearch == null) {
            return;
        }
        iAutoTSearch.searchChargeStationAsync();
    }

    public void setChargeStationListener(OnChargeStationListener onChargeStationListener) {
        IAutoTSearch iAutoTSearch = this.a;
        if (iAutoTSearch == null || onChargeStationListener == null) {
            return;
        }
        iAutoTSearch.setChargeStationListener(onChargeStationListener);
    }

    public static class FilterBox implements Parcelable, Cloneable {
        public static final Parcelable.Creator<FilterBox> CREATOR = new Parcelable.Creator<FilterBox>() { // from class: com.amap.api.services.auto.AutoTSearch.FilterBox.1
            @Override // android.os.Parcelable.Creator
            public final /* synthetic */ FilterBox createFromParcel(Parcel parcel) {
                return a(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public final /* synthetic */ FilterBox[] newArray(int i) {
                return a(i);
            }

            private static FilterBox a(Parcel parcel) {
                return new FilterBox(parcel);
            }

            private static FilterBox[] a(int i) {
                return new FilterBox[i];
            }
        };
        private String a;
        private String b;
        private String c;
        private String d;
        private String e;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public FilterBox() {
        }

        public String getRetainState() {
            return this.a;
        }

        public void setRetainState(String str) {
            this.a = str;
        }

        public String getCheckedLevel() {
            return this.b;
        }

        public void setCheckedLevel(String str) {
            this.b = str;
        }

        public String getClassifyV2Data() {
            return this.c;
        }

        public void setClassifyV2Data(String str) {
            this.c = str;
        }

        public String getClassifyV2Level2Data() {
            return this.d;
        }

        public void setClassifyV2Level2Data(String str) {
            this.d = str;
        }

        public String getClassifyV2Level3Data() {
            return this.e;
        }

        public void setClassifyV2Level3Data(String str) {
            this.e = str;
        }

        protected FilterBox(Parcel parcel) {
            this.a = parcel.readString();
            this.b = parcel.readString();
            this.c = parcel.readString();
            this.d = parcel.readString();
            this.e = parcel.readString();
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.a);
            parcel.writeString(this.b);
            parcel.writeString(this.c);
            parcel.writeString(this.d);
            parcel.writeString(this.e);
        }

        /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
        public FilterBox m45clone() {
            try {
                super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            FilterBox filterBox = new FilterBox();
            filterBox.setRetainState(this.a);
            filterBox.setCheckedLevel(this.b);
            filterBox.setClassifyV2Data(this.c);
            filterBox.setClassifyV2Level2Data(this.d);
            filterBox.setClassifyV2Level3Data(this.e);
            return filterBox;
        }
    }

    public static class Query implements Parcelable, Cloneable {
        public static final Parcelable.Creator<Query> CREATOR = new Parcelable.Creator<Query>() { // from class: com.amap.api.services.auto.AutoTSearch.Query.1
            @Override // android.os.Parcelable.Creator
            public final /* synthetic */ Query createFromParcel(Parcel parcel) {
                return a(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public final /* synthetic */ Query[] newArray(int i) {
                return a(i);
            }

            private static Query a(Parcel parcel) {
                return new Query(parcel);
            }

            private static Query[] a(int i) {
                return new Query[i];
            }
        };
        private String a;
        private String b;
        private String c;
        private String d;
        private String e;
        private int f;
        private int g;
        private boolean h;
        private String i;
        private int j;
        private LatLonPoint k;
        private String l;
        private String m;
        private FilterBox n;
        private String o;
        private String p;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public Query() {
            this.h = false;
        }

        protected Query(Parcel parcel) {
            this.h = false;
            this.a = parcel.readString();
            this.b = parcel.readString();
            this.c = parcel.readString();
            this.d = parcel.readString();
            this.e = parcel.readString();
            this.f = parcel.readInt();
            this.g = parcel.readInt();
            this.h = parcel.readByte() != 0;
            this.i = parcel.readString();
            this.j = parcel.readInt();
            this.k = (LatLonPoint) parcel.readParcelable(LatLonPoint.class.getClassLoader());
            this.l = parcel.readString();
            this.m = parcel.readString();
            this.n = (FilterBox) parcel.readParcelable(FilterBox.class.getClassLoader());
            this.o = parcel.readString();
            this.p = parcel.readString();
        }

        public String getAdCode() {
            return this.a;
        }

        public void setAdCode(String str) {
            this.a = str;
        }

        public String getCity() {
            return this.b;
        }

        public void setCity(String str) {
            this.b = str;
        }

        public String getDataType() {
            return this.c;
        }

        public void setDataType(String str) {
            this.c = str;
        }

        public String getGeoObj() {
            return this.d;
        }

        public void setGeoObj(String str) {
            this.d = str;
        }

        public String getKeywords() {
            return this.e;
        }

        public void setKeywords(String str) {
            this.e = str;
        }

        public int getPageNum() {
            return this.f;
        }

        public void setPageNum(int i) {
            this.f = i;
        }

        public int getPageSize() {
            return this.g;
        }

        public void setPageSize(int i) {
            this.g = i;
        }

        public boolean isQii() {
            return this.h;
        }

        public void setQii(boolean z) {
            this.h = z;
        }

        public String getQueryType() {
            return this.i;
        }

        public void setQueryType(String str) {
            this.i = str;
        }

        public int getRange() {
            return this.j;
        }

        public void setRange(int i) {
            this.j = i;
        }

        public LatLonPoint getLatLonPoint() {
            return this.k;
        }

        public void setLatLonPoint(LatLonPoint latLonPoint) {
            this.k = latLonPoint;
        }

        public String getUserLoc() {
            return this.l;
        }

        public void setUserLoc(String str) {
            this.l = str;
        }

        public String getUserCity() {
            return this.m;
        }

        public void setUserCity(String str) {
            this.m = str;
        }

        public FilterBox getFilterBox() {
            return this.n;
        }

        public void setFilterBox(FilterBox filterBox) {
            this.n = filterBox;
        }

        public String getAccessKey() {
            return this.o;
        }

        public void setAccessKey(String str) {
            this.o = str;
        }

        public String getSecretKey() {
            return this.p;
        }

        public void setSecretKey(String str) {
            this.p = str;
        }

        /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
        public Query m46clone() {
            try {
                super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            Query query = new Query();
            query.setAdCode(this.a);
            query.setCity(this.b);
            query.setDataType(this.c);
            query.setGeoObj(this.d);
            query.setKeywords(this.e);
            query.setPageNum(this.f);
            query.setPageSize(this.g);
            query.setQii(this.h);
            query.setQueryType(this.i);
            query.setRange(this.j);
            query.setLatLonPoint(this.k);
            query.setUserLoc(this.l);
            query.setUserCity(this.m);
            query.setAccessKey(this.o);
            query.setSecretKey(this.p);
            query.setFilterBox(this.n);
            return query;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.a);
            parcel.writeString(this.b);
            parcel.writeString(this.c);
            parcel.writeString(this.d);
            parcel.writeString(this.e);
            parcel.writeInt(this.f);
            parcel.writeInt(this.g);
            parcel.writeByte(this.h ? (byte) 1 : (byte) 0);
            parcel.writeString(this.i);
            parcel.writeInt(this.j);
            parcel.writeParcelable(this.k, i);
            parcel.writeString(this.l);
            parcel.writeString(this.m);
            parcel.writeParcelable(this.n, i);
            parcel.writeString(this.o);
            parcel.writeString(this.p);
        }
    }
}
