package android.speech.tts;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

/* JADX INFO: loaded from: classes.dex */
public class TtsEngines {
    private static final boolean DBG = false;
    private static final String LOCALE_DELIMITER = "-";
    private static final String TAG = "TtsEngines";
    private static final String XML_TAG_NAME = "tts-engine";
    private final Context mContext;

    public TtsEngines(Context context) {
        this.mContext = context;
    }

    public String getDefaultEngine() {
        String string = Settings.Secure.getString(this.mContext.getContentResolver(), Settings.Secure.TTS_DEFAULT_SYNTH);
        return isEngineInstalled(string) ? string : getHighestRankedEngineName();
    }

    public String getHighestRankedEngineName() {
        List<TextToSpeech.EngineInfo> engines = getEngines();
        if (engines.size() <= 0 || !engines.get(0).system) {
            return null;
        }
        return engines.get(0).name;
    }

    public TextToSpeech.EngineInfo getEngineInfo(String str) {
        PackageManager packageManager = this.mContext.getPackageManager();
        Intent intent = new Intent(TextToSpeech.Engine.INTENT_ACTION_TTS_SERVICE);
        intent.setPackage(str);
        List<ResolveInfo> listQueryIntentServices = packageManager.queryIntentServices(intent, 65536);
        if (listQueryIntentServices == null || listQueryIntentServices.size() != 1) {
            return null;
        }
        return getEngineInfo(listQueryIntentServices.get(0), packageManager);
    }

    public List<TextToSpeech.EngineInfo> getEngines() {
        PackageManager packageManager = this.mContext.getPackageManager();
        List<ResolveInfo> listQueryIntentServices = packageManager.queryIntentServices(new Intent(TextToSpeech.Engine.INTENT_ACTION_TTS_SERVICE), 65536);
        if (listQueryIntentServices == null) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList(listQueryIntentServices.size());
        Iterator<ResolveInfo> it = listQueryIntentServices.iterator();
        while (it.hasNext()) {
            TextToSpeech.EngineInfo engineInfo = getEngineInfo(it.next(), packageManager);
            if (engineInfo != null) {
                arrayList.add(engineInfo);
            }
        }
        Collections.sort(arrayList, EngineInfoComparator.INSTANCE);
        return arrayList;
    }

    private boolean isSystemEngine(ServiceInfo serviceInfo) {
        ApplicationInfo applicationInfo = serviceInfo.applicationInfo;
        return (applicationInfo == null || (applicationInfo.flags & 1) == 0) ? false : true;
    }

    public boolean isEngineInstalled(String str) {
        return (str == null || getEngineInfo(str) == null) ? false : true;
    }

    public Intent getSettingsIntent(String str) {
        ServiceInfo serviceInfo;
        String str2;
        PackageManager packageManager = this.mContext.getPackageManager();
        Intent intent = new Intent(TextToSpeech.Engine.INTENT_ACTION_TTS_SERVICE);
        intent.setPackage(str);
        List<ResolveInfo> listQueryIntentServices = packageManager.queryIntentServices(intent, 65664);
        if (listQueryIntentServices == null || listQueryIntentServices.size() != 1 || (serviceInfo = listQueryIntentServices.get(0).serviceInfo) == null || (str2 = settingsActivityFromServiceInfo(serviceInfo, packageManager)) == null) {
            return null;
        }
        Intent intent2 = new Intent();
        intent2.setClassName(str, str2);
        return intent2;
    }

    /* JADX WARN: Not initialized variable reg: 4, insn: 0x00fc: MOVE (r3 I:??[OBJECT, ARRAY]) = (r4 I:??[OBJECT, ARRAY]), block:B:51:0x00fc */
    /* JADX WARN: Removed duplicated region for block: B:53:0x00ff  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.String settingsActivityFromServiceInfo(android.content.pm.ServiceInfo r8, android.content.pm.PackageManager r9) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 259
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.speech.tts.TtsEngines.settingsActivityFromServiceInfo(android.content.pm.ServiceInfo, android.content.pm.PackageManager):java.lang.String");
    }

    private TextToSpeech.EngineInfo getEngineInfo(ResolveInfo resolveInfo, PackageManager packageManager) {
        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        if (serviceInfo == null) {
            return null;
        }
        TextToSpeech.EngineInfo engineInfo = new TextToSpeech.EngineInfo();
        engineInfo.name = serviceInfo.packageName;
        CharSequence charSequenceLoadLabel = serviceInfo.loadLabel(packageManager);
        engineInfo.label = TextUtils.isEmpty(charSequenceLoadLabel) ? engineInfo.name : charSequenceLoadLabel.toString();
        engineInfo.icon = serviceInfo.getIconResource();
        engineInfo.priority = resolveInfo.priority;
        engineInfo.system = isSystemEngine(serviceInfo);
        return engineInfo;
    }

    private static class EngineInfoComparator implements Comparator<TextToSpeech.EngineInfo> {
        static EngineInfoComparator INSTANCE = new EngineInfoComparator();

        private EngineInfoComparator() {
        }

        @Override // java.util.Comparator
        public int compare(TextToSpeech.EngineInfo engineInfo, TextToSpeech.EngineInfo engineInfo2) {
            if (engineInfo.system && !engineInfo2.system) {
                return -1;
            }
            if (!engineInfo2.system || engineInfo.system) {
                return engineInfo2.priority - engineInfo.priority;
            }
            return 1;
        }
    }

    public String getLocalePrefForEngine(String str) {
        String enginePrefFromList = parseEnginePrefFromList(Settings.Secure.getString(this.mContext.getContentResolver(), Settings.Secure.TTS_DEFAULT_LOCALE), str);
        return TextUtils.isEmpty(enginePrefFromList) ? getV1Locale() : enginePrefFromList;
    }

    public static String[] parseLocalePref(String str) {
        String[] strArr = {"", "", ""};
        if (!TextUtils.isEmpty(str)) {
            String[] strArrSplit = str.split(LOCALE_DELIMITER);
            System.arraycopy(strArrSplit, 0, strArr, 0, strArrSplit.length);
        }
        return strArr;
    }

    private String getV1Locale() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        String string = Settings.Secure.getString(contentResolver, Settings.Secure.TTS_DEFAULT_LANG);
        String string2 = Settings.Secure.getString(contentResolver, Settings.Secure.TTS_DEFAULT_COUNTRY);
        String string3 = Settings.Secure.getString(contentResolver, Settings.Secure.TTS_DEFAULT_VARIANT);
        if (TextUtils.isEmpty(string)) {
            return getDefaultLocale();
        }
        if (TextUtils.isEmpty(string2)) {
            return string;
        }
        String str = string + LOCALE_DELIMITER + string2;
        return !TextUtils.isEmpty(string3) ? str + LOCALE_DELIMITER + string3 : str;
    }

    public String getDefaultLocale() {
        Locale locale = Locale.getDefault();
        try {
            String iSO3Language = locale.getISO3Language();
            if (TextUtils.isEmpty(iSO3Language)) {
                Log.w(TAG, "Default locale is empty.");
                return "";
            }
            if (TextUtils.isEmpty(locale.getISO3Country())) {
                return iSO3Language;
            }
            String str = iSO3Language + LOCALE_DELIMITER + locale.getISO3Country();
            return !TextUtils.isEmpty(locale.getVariant()) ? str + LOCALE_DELIMITER + locale.getVariant() : str;
        } catch (MissingResourceException unused) {
            return "eng-usa";
        }
    }

    private static String parseEnginePrefFromList(String str, String str2) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        for (String str3 : str.split(",")) {
            int iIndexOf = str3.indexOf(58);
            if (iIndexOf > 0 && str2.equals(str3.substring(0, iIndexOf))) {
                return str3.substring(iIndexOf + 1);
            }
        }
        return null;
    }

    public synchronized void updateLocalePrefForEngine(String str, String str2) {
        Settings.Secure.putString(this.mContext.getContentResolver(), Settings.Secure.TTS_DEFAULT_LOCALE, updateValueInCommaSeparatedList(Settings.Secure.getString(this.mContext.getContentResolver(), Settings.Secure.TTS_DEFAULT_LOCALE), str, str2).toString());
    }

    private String updateValueInCommaSeparatedList(String str, String str2, String str3) {
        StringBuilder sb = new StringBuilder();
        if (TextUtils.isEmpty(str)) {
            sb.append(str2).append(':').append(str3);
        } else {
            boolean z = true;
            boolean z2 = false;
            for (String str4 : str.split(",")) {
                int iIndexOf = str4.indexOf(58);
                if (iIndexOf > 0) {
                    if (str2.equals(str4.substring(0, iIndexOf))) {
                        if (z) {
                            z = false;
                        } else {
                            sb.append(PhoneNumberUtils.PAUSE);
                        }
                        sb.append(str2).append(':').append(str3);
                        z2 = true;
                    } else {
                        if (z) {
                            z = false;
                        } else {
                            sb.append(PhoneNumberUtils.PAUSE);
                        }
                        sb.append(str4);
                    }
                }
            }
            if (!z2) {
                sb.append(PhoneNumberUtils.PAUSE);
                sb.append(str2).append(':').append(str3);
            }
        }
        return sb.toString();
    }
}
