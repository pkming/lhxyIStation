package android.nfc.cardemulation;

import android.app.ActivityThread;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.IPackageManager;
import android.nfc.INfcCardEmulation;
import android.nfc.NfcAdapter;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import java.util.HashMap;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final class CardEmulation {
    public static final String ACTION_CHANGE_DEFAULT = "android.nfc.cardemulation.action.ACTION_CHANGE_DEFAULT";
    public static final String CATEGORY_OTHER = "other";
    public static final String CATEGORY_PAYMENT = "payment";
    public static final String EXTRA_CATEGORY = "category";
    public static final String EXTRA_SERVICE_COMPONENT = "component";
    public static final int SELECTION_MODE_ALWAYS_ASK = 1;
    public static final int SELECTION_MODE_ASK_IF_CONFLICT = 2;
    public static final int SELECTION_MODE_PREFER_DEFAULT = 0;
    static final String TAG = "CardEmulation";
    static HashMap<Context, CardEmulation> sCardEmus = new HashMap<>();
    static boolean sIsInitialized = false;
    static INfcCardEmulation sService;
    final Context mContext;

    private CardEmulation(Context context, INfcCardEmulation iNfcCardEmulation) {
        this.mContext = context.getApplicationContext();
        sService = iNfcCardEmulation;
    }

    public static synchronized CardEmulation getInstance(NfcAdapter nfcAdapter) {
        CardEmulation cardEmulation;
        try {
            if (nfcAdapter == null) {
                throw new NullPointerException("NfcAdapter is null");
            }
            Context context = nfcAdapter.getContext();
            if (context == null) {
                Log.e(TAG, "NfcAdapter context is null.");
                throw new UnsupportedOperationException();
            }
            if (!sIsInitialized) {
                IPackageManager packageManager = ActivityThread.getPackageManager();
                if (packageManager == null) {
                    Log.e(TAG, "Cannot get PackageManager");
                    throw new UnsupportedOperationException();
                }
                try {
                    if (!packageManager.hasSystemFeature("android.hardware.nfc.hce")) {
                        Log.e(TAG, "This device does not support card emulation");
                        throw new UnsupportedOperationException();
                    }
                    sIsInitialized = true;
                } catch (RemoteException unused) {
                    Log.e(TAG, "PackageManager query failed.");
                    throw new UnsupportedOperationException();
                }
            }
            cardEmulation = sCardEmus.get(context);
            if (cardEmulation == null) {
                cardEmulation = new CardEmulation(context, nfcAdapter.getCardEmulationService());
                sCardEmus.put(context, cardEmulation);
            }
        } catch (Throwable th) {
            throw th;
        }
        return cardEmulation;
    }

    public boolean isDefaultServiceForCategory(ComponentName componentName, String str) {
        try {
            return sService.isDefaultServiceForCategory(UserHandle.myUserId(), componentName, str);
        } catch (RemoteException unused) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return iNfcCardEmulation.isDefaultServiceForCategory(UserHandle.myUserId(), componentName, str);
            } catch (RemoteException unused2) {
                Log.e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
        }
    }

    public boolean isDefaultServiceForAid(ComponentName componentName, String str) {
        try {
            return sService.isDefaultServiceForAid(UserHandle.myUserId(), componentName, str);
        } catch (RemoteException unused) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return iNfcCardEmulation.isDefaultServiceForAid(UserHandle.myUserId(), componentName, str);
            } catch (RemoteException unused2) {
                Log.e(TAG, "Failed to reach CardEmulationService.");
                return false;
            }
        }
    }

    public int getSelectionModeForCategory(String str) {
        if (CATEGORY_PAYMENT.equals(str)) {
            return Settings.Secure.getString(this.mContext.getContentResolver(), Settings.Secure.NFC_PAYMENT_DEFAULT_COMPONENT) != null ? 0 : 1;
        }
        return 2;
    }

    public boolean setDefaultServiceForCategory(ComponentName componentName, String str) {
        try {
            return sService.setDefaultServiceForCategory(UserHandle.myUserId(), componentName, str);
        } catch (RemoteException unused) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return iNfcCardEmulation.setDefaultServiceForCategory(UserHandle.myUserId(), componentName, str);
            } catch (RemoteException unused2) {
                Log.e(TAG, "Failed to reach CardEmulationService.");
                return false;
            }
        }
    }

    public boolean setDefaultForNextTap(ComponentName componentName) {
        try {
            return sService.setDefaultForNextTap(UserHandle.myUserId(), componentName);
        } catch (RemoteException unused) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return iNfcCardEmulation.setDefaultForNextTap(UserHandle.myUserId(), componentName);
            } catch (RemoteException unused2) {
                Log.e(TAG, "Failed to reach CardEmulationService.");
                return false;
            }
        }
    }

    public List<ApduServiceInfo> getServices(String str) {
        try {
            return sService.getServices(UserHandle.myUserId(), str);
        } catch (RemoteException unused) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.e(TAG, "Failed to recover CardEmulationService.");
                return null;
            }
            try {
                return iNfcCardEmulation.getServices(UserHandle.myUserId(), str);
            } catch (RemoteException unused2) {
                Log.e(TAG, "Failed to reach CardEmulationService.");
                return null;
            }
        }
    }

    void recoverService() {
        sService = NfcAdapter.getDefaultAdapter(this.mContext).getCardEmulationService();
    }
}
