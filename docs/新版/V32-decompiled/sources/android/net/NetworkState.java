package android.net;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class NetworkState implements Parcelable {
    public static final Parcelable.Creator<NetworkState> CREATOR = new Parcelable.Creator<NetworkState>() { // from class: android.net.NetworkState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkState createFromParcel(Parcel parcel) {
            return new NetworkState(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkState[] newArray(int i) {
            return new NetworkState[i];
        }
    };
    public final LinkCapabilities linkCapabilities;
    public final LinkProperties linkProperties;
    public final String networkId;
    public final NetworkInfo networkInfo;
    public final String subscriberId;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public NetworkState(NetworkInfo networkInfo, LinkProperties linkProperties, LinkCapabilities linkCapabilities) {
        this(networkInfo, linkProperties, linkCapabilities, null, null);
    }

    public NetworkState(NetworkInfo networkInfo, LinkProperties linkProperties, LinkCapabilities linkCapabilities, String str, String str2) {
        this.networkInfo = networkInfo;
        this.linkProperties = linkProperties;
        this.linkCapabilities = linkCapabilities;
        this.subscriberId = str;
        this.networkId = str2;
    }

    public NetworkState(Parcel parcel) {
        this.networkInfo = (NetworkInfo) parcel.readParcelable(null);
        this.linkProperties = (LinkProperties) parcel.readParcelable(null);
        this.linkCapabilities = (LinkCapabilities) parcel.readParcelable(null);
        this.subscriberId = parcel.readString();
        this.networkId = parcel.readString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.networkInfo, i);
        parcel.writeParcelable(this.linkProperties, i);
        parcel.writeParcelable(this.linkCapabilities, i);
        parcel.writeString(this.subscriberId);
        parcel.writeString(this.networkId);
    }
}
