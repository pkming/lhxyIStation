package com.amap.api.services.route;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class TimeInfo implements Parcelable {
    public static final Parcelable.Creator<TimeInfo> CREATOR = new Parcelable.Creator<TimeInfo>() { // from class: com.amap.api.services.route.TimeInfo.1
        @Override // android.os.Parcelable.Creator
        public final /* bridge */ /* synthetic */ TimeInfo[] newArray(int i) {
            return null;
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ TimeInfo createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        private static TimeInfo a(Parcel parcel) {
            return new TimeInfo(parcel);
        }
    };
    private long a;
    private List<TimeInfosElement> b;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public long getStartTime() {
        return this.a;
    }

    public void setStartTime(long j) {
        this.a = j;
    }

    public List<TimeInfosElement> getElements() {
        return this.b;
    }

    public void setElements(List<TimeInfosElement> list) {
        this.b = list;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.a);
        parcel.writeTypedList(this.b);
    }

    public TimeInfo(Parcel parcel) {
        this.b = new ArrayList();
        this.a = parcel.readInt();
        this.b = parcel.createTypedArrayList(TimeInfosElement.CREATOR);
    }

    public TimeInfo() {
        this.b = new ArrayList();
    }
}
