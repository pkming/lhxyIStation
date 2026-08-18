package android.view.textservice;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.textservice.SpellCheckerSession;
import com.android.internal.textservice.ITextServicesManager;
import java.util.Locale;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class TextServicesManager {
    private static final boolean DBG = false;
    private static final String TAG = "TextServicesManager";
    private static TextServicesManager sInstance;
    private static ITextServicesManager sService;

    private TextServicesManager() {
        if (sService == null) {
            sService = ITextServicesManager.Stub.asInterface(ServiceManager.getService(Context.TEXT_SERVICES_MANAGER_SERVICE));
        }
    }

    public static TextServicesManager getInstance() {
        synchronized (TextServicesManager.class) {
            TextServicesManager textServicesManager = sInstance;
            if (textServicesManager != null) {
                return textServicesManager;
            }
            TextServicesManager textServicesManager2 = new TextServicesManager();
            sInstance = textServicesManager2;
            return textServicesManager2;
        }
    }

    public SpellCheckerSession newSpellCheckerSession(Bundle bundle, Locale locale, SpellCheckerSession.SpellCheckerSessionListener spellCheckerSessionListener, boolean z) {
        SpellCheckerSubtype currentSpellCheckerSubtype;
        Objects.requireNonNull(spellCheckerSessionListener);
        if (!z && locale == null) {
            throw new IllegalArgumentException("Locale should not be null if you don't refer settings.");
        }
        if (z && !isSpellCheckerEnabled()) {
            return null;
        }
        try {
            SpellCheckerInfo currentSpellChecker = sService.getCurrentSpellChecker((String) null);
            if (currentSpellChecker == null) {
                return null;
            }
            int i = 0;
            if (z) {
                currentSpellCheckerSubtype = getCurrentSpellCheckerSubtype(true);
                if (currentSpellCheckerSubtype == null) {
                    return null;
                }
                if (locale != null) {
                    String locale2 = currentSpellCheckerSubtype.getLocale();
                    String string = locale.toString();
                    if (locale2.length() < 2 || string.length() < 2 || !locale2.substring(0, 2).equals(string.substring(0, 2))) {
                        return null;
                    }
                }
            } else {
                String string2 = locale.toString();
                currentSpellCheckerSubtype = null;
                while (true) {
                    if (i >= currentSpellChecker.getSubtypeCount()) {
                        break;
                    }
                    SpellCheckerSubtype subtypeAt = currentSpellChecker.getSubtypeAt(i);
                    String locale3 = subtypeAt.getLocale();
                    if (locale3.equals(string2)) {
                        currentSpellCheckerSubtype = subtypeAt;
                        break;
                    }
                    if (string2.length() >= 2 && locale3.length() >= 2 && string2.startsWith(locale3)) {
                        currentSpellCheckerSubtype = subtypeAt;
                    }
                    i++;
                }
            }
            if (currentSpellCheckerSubtype == null) {
                return null;
            }
            SpellCheckerSession spellCheckerSession = new SpellCheckerSession(currentSpellChecker, sService, spellCheckerSessionListener, currentSpellCheckerSubtype);
            sService.getSpellCheckerService(currentSpellChecker.getId(), currentSpellCheckerSubtype.getLocale(), spellCheckerSession.getTextServicesSessionListener(), spellCheckerSession.getSpellCheckerSessionListener(), bundle);
            return spellCheckerSession;
        } catch (RemoteException unused) {
            return null;
        }
    }

    public SpellCheckerInfo[] getEnabledSpellCheckers() {
        try {
            return sService.getEnabledSpellCheckers();
        } catch (RemoteException e) {
            Log.e(TAG, "Error in getEnabledSpellCheckers: " + e);
            return null;
        }
    }

    public SpellCheckerInfo getCurrentSpellChecker() {
        try {
            return sService.getCurrentSpellChecker((String) null);
        } catch (RemoteException unused) {
            return null;
        }
    }

    public void setCurrentSpellChecker(SpellCheckerInfo spellCheckerInfo) {
        try {
            if (spellCheckerInfo == null) {
                throw new NullPointerException("SpellCheckerInfo is null.");
            }
            sService.setCurrentSpellChecker((String) null, spellCheckerInfo.getId());
        } catch (RemoteException e) {
            Log.e(TAG, "Error in setCurrentSpellChecker: " + e);
        }
    }

    public SpellCheckerSubtype getCurrentSpellCheckerSubtype(boolean z) {
        try {
            ITextServicesManager iTextServicesManager = sService;
            if (iTextServicesManager == null) {
                Log.e(TAG, "sService is null.");
                return null;
            }
            return iTextServicesManager.getCurrentSpellCheckerSubtype((String) null, z);
        } catch (RemoteException e) {
            Log.e(TAG, "Error in getCurrentSpellCheckerSubtype: " + e);
            return null;
        }
    }

    public void setSpellCheckerSubtype(SpellCheckerSubtype spellCheckerSubtype) {
        int iHashCode;
        if (spellCheckerSubtype == null) {
            iHashCode = 0;
        } else {
            try {
                iHashCode = spellCheckerSubtype.hashCode();
            } catch (RemoteException e) {
                Log.e(TAG, "Error in setSpellCheckerSubtype:" + e);
                return;
            }
        }
        sService.setCurrentSpellCheckerSubtype((String) null, iHashCode);
    }

    public void setSpellCheckerEnabled(boolean z) {
        try {
            sService.setSpellCheckerEnabled(z);
        } catch (RemoteException e) {
            Log.e(TAG, "Error in setSpellCheckerEnabled:" + e);
        }
    }

    public boolean isSpellCheckerEnabled() {
        try {
            return sService.isSpellCheckerEnabled();
        } catch (RemoteException e) {
            Log.e(TAG, "Error in isSpellCheckerEnabled:" + e);
            return false;
        }
    }
}
