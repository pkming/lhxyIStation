package android.net;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.IConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import com.android.internal.net.VpnConfig;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class VpnService extends Service {
    public static final String SERVICE_INTERFACE = "android.net.VpnService";

    /* JADX INFO: Access modifiers changed from: private */
    public static IConnectivityManager getService() {
        return IConnectivityManager.Stub.asInterface(ServiceManager.getService(Context.CONNECTIVITY_SERVICE));
    }

    public static Intent prepare(Context context) {
        try {
            if (getService().prepareVpn(context.getPackageName(), null)) {
                return null;
            }
        } catch (RemoteException unused) {
        }
        return VpnConfig.getIntentForConfirmation();
    }

    public boolean protect(int i) {
        ParcelFileDescriptor parcelFileDescriptorFromFd = null;
        try {
            parcelFileDescriptorFromFd = ParcelFileDescriptor.fromFd(i);
            boolean zProtectVpn = getService().protectVpn(parcelFileDescriptorFromFd);
            try {
                parcelFileDescriptorFromFd.close();
            } catch (Exception unused) {
            }
            return zProtectVpn;
        } catch (Exception unused2) {
            try {
                parcelFileDescriptorFromFd.close();
            } catch (Exception unused3) {
            }
            return false;
        } catch (Throwable th) {
            try {
                parcelFileDescriptorFromFd.close();
            } catch (Exception unused4) {
            }
            throw th;
        }
    }

    public boolean protect(Socket socket) {
        return protect(socket.getFileDescriptor$().getInt$());
    }

    public boolean protect(DatagramSocket datagramSocket) {
        return protect(datagramSocket.getFileDescriptor$().getInt$());
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (intent == null || !SERVICE_INTERFACE.equals(intent.getAction())) {
            return null;
        }
        return new Callback();
    }

    public void onRevoke() {
        stopSelf();
    }

    private class Callback extends Binder {
        private Callback() {
        }

        @Override // android.os.Binder
        protected boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i != 16777215) {
                return false;
            }
            VpnService.this.onRevoke();
            return true;
        }
    }

    public class Builder {
        private final List<LinkAddress> mAddresses;
        private final VpnConfig mConfig;
        private final List<RouteInfo> mRoutes;

        public Builder() {
            VpnConfig vpnConfig = new VpnConfig();
            this.mConfig = vpnConfig;
            this.mAddresses = new ArrayList();
            this.mRoutes = new ArrayList();
            vpnConfig.user = VpnService.this.getClass().getName();
        }

        public Builder setSession(String str) {
            this.mConfig.session = str;
            return this;
        }

        public Builder setConfigureIntent(PendingIntent pendingIntent) {
            this.mConfig.configureIntent = pendingIntent;
            return this;
        }

        public Builder setMtu(int i) {
            if (i <= 0) {
                throw new IllegalArgumentException("Bad mtu");
            }
            this.mConfig.mtu = i;
            return this;
        }

        private void check(InetAddress inetAddress, int i) {
            if (inetAddress.isLoopbackAddress()) {
                throw new IllegalArgumentException("Bad address");
            }
            if (inetAddress instanceof Inet4Address) {
                if (i < 0 || i > 32) {
                    throw new IllegalArgumentException("Bad prefixLength");
                }
            } else {
                if (!(inetAddress instanceof Inet6Address)) {
                    throw new IllegalArgumentException("Unsupported family");
                }
                if (i < 0 || i > 128) {
                    throw new IllegalArgumentException("Bad prefixLength");
                }
            }
        }

        public Builder addAddress(InetAddress inetAddress, int i) {
            check(inetAddress, i);
            if (inetAddress.isAnyLocalAddress()) {
                throw new IllegalArgumentException("Bad address");
            }
            this.mAddresses.add(new LinkAddress(inetAddress, i));
            return this;
        }

        public Builder addAddress(String str, int i) {
            return addAddress(InetAddress.parseNumericAddress(str), i);
        }

        public Builder addRoute(InetAddress inetAddress, int i) {
            check(inetAddress, i);
            int i2 = i / 8;
            byte[] address = inetAddress.getAddress();
            if (i2 < address.length) {
                address[i2] = (byte) (address[i2] << (i % 8));
                while (i2 < address.length) {
                    if (address[i2] != 0) {
                        throw new IllegalArgumentException("Bad address");
                    }
                    i2++;
                }
            }
            this.mRoutes.add(new RouteInfo(new LinkAddress(inetAddress, i), null));
            return this;
        }

        public Builder addRoute(String str, int i) {
            return addRoute(InetAddress.parseNumericAddress(str), i);
        }

        public Builder addDnsServer(InetAddress inetAddress) {
            if (inetAddress.isLoopbackAddress() || inetAddress.isAnyLocalAddress()) {
                throw new IllegalArgumentException("Bad address");
            }
            if (this.mConfig.dnsServers == null) {
                this.mConfig.dnsServers = new ArrayList();
            }
            this.mConfig.dnsServers.add(inetAddress.getHostAddress());
            return this;
        }

        public Builder addDnsServer(String str) {
            return addDnsServer(InetAddress.parseNumericAddress(str));
        }

        public Builder addSearchDomain(String str) {
            if (this.mConfig.searchDomains == null) {
                this.mConfig.searchDomains = new ArrayList();
            }
            this.mConfig.searchDomains.add(str);
            return this;
        }

        public ParcelFileDescriptor establish() {
            this.mConfig.addresses = this.mAddresses;
            this.mConfig.routes = this.mRoutes;
            try {
                return VpnService.getService().establishVpn(this.mConfig);
            } catch (RemoteException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
