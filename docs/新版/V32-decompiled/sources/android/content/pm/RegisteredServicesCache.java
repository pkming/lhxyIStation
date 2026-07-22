package android.content.pm;

import android.accounts.GrantCredentialsPermissionActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Environment;
import android.os.Handler;
import android.os.UserHandle;
import android.util.AtomicFile;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;
import androidx.core.app.NotificationCompat;
import com.android.internal.util.FastXmlSerializer;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public abstract class RegisteredServicesCache<V> {
    private static final boolean DEBUG = false;
    private static final String TAG = "PackageManager";
    private final String mAttributesName;
    public final Context mContext;
    private final BroadcastReceiver mExternalReceiver;
    private Handler mHandler;
    private final String mInterfaceName;
    private RegisteredServicesCacheListener<V> mListener;
    private final String mMetaDataName;
    private final BroadcastReceiver mPackageReceiver;
    private final AtomicFile mPersistentServicesFile;
    private boolean mPersistentServicesFileDidNotExist;
    private final XmlSerializerAndParser<V> mSerializerAndParser;
    private final Object mServicesLock = new Object();
    private final SparseArray<UserServices<V>> mUserServices = new SparseArray<>(2);

    public abstract V parseServiceAttributes(Resources resources, String str, AttributeSet attributeSet);

    private static class UserServices<V> {
        public final Map<V, Integer> persistentServices;
        public Map<V, ServiceInfo<V>> services;

        private UserServices() {
            this.persistentServices = Maps.newHashMap();
            this.services = null;
        }
    }

    private UserServices<V> findOrCreateUserLocked(int i) {
        UserServices<V> userServices = this.mUserServices.get(i);
        if (userServices != null) {
            return userServices;
        }
        UserServices<V> userServices2 = new UserServices<>();
        this.mUserServices.put(i, userServices2);
        return userServices2;
    }

    public RegisteredServicesCache(Context context, String str, String str2, String str3, XmlSerializerAndParser<V> xmlSerializerAndParser) throws Throwable {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: android.content.pm.RegisteredServicesCache.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) throws Throwable {
                int intExtra = intent.getIntExtra(Intent.EXTRA_UID, -1);
                if (intExtra != -1) {
                    RegisteredServicesCache.this.generateServicesMap(UserHandle.getUserId(intExtra));
                }
            }
        };
        this.mPackageReceiver = broadcastReceiver;
        BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() { // from class: android.content.pm.RegisteredServicesCache.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) throws Throwable {
                RegisteredServicesCache.this.generateServicesMap(0);
            }
        };
        this.mExternalReceiver = broadcastReceiver2;
        this.mContext = context;
        this.mInterfaceName = str;
        this.mMetaDataName = str2;
        this.mAttributesName = str3;
        this.mSerializerAndParser = xmlSerializerAndParser;
        this.mPersistentServicesFile = new AtomicFile(new File(new File(new File(Environment.getDataDirectory(), "system"), "registered_services"), str + ".xml"));
        readPersistentServicesLocked();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        context.registerReceiverAsUser(broadcastReceiver, UserHandle.ALL, intentFilter, null, null);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        intentFilter2.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        context.registerReceiver(broadcastReceiver2, intentFilter2);
    }

    public void invalidateCache(int i) {
        synchronized (this.mServicesLock) {
            findOrCreateUserLocked(i).services = null;
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, int i) {
        synchronized (this.mServicesLock) {
            UserServices<V> userServicesFindOrCreateUserLocked = findOrCreateUserLocked(i);
            if (userServicesFindOrCreateUserLocked.services != null) {
                printWriter.println("RegisteredServicesCache: " + userServicesFindOrCreateUserLocked.services.size() + " services");
                Iterator<ServiceInfo<V>> it = userServicesFindOrCreateUserLocked.services.values().iterator();
                while (it.hasNext()) {
                    printWriter.println("  " + it.next());
                }
            } else {
                printWriter.println("RegisteredServicesCache: services not loaded");
            }
        }
    }

    public RegisteredServicesCacheListener<V> getListener() {
        RegisteredServicesCacheListener<V> registeredServicesCacheListener;
        synchronized (this) {
            registeredServicesCacheListener = this.mListener;
        }
        return registeredServicesCacheListener;
    }

    public void setListener(RegisteredServicesCacheListener<V> registeredServicesCacheListener, Handler handler) {
        if (handler == null) {
            handler = new Handler(this.mContext.getMainLooper());
        }
        synchronized (this) {
            this.mHandler = handler;
            this.mListener = registeredServicesCacheListener;
        }
    }

    private void notifyListener(final V v, final int i, final boolean z) {
        final RegisteredServicesCacheListener<V> registeredServicesCacheListener;
        Handler handler;
        synchronized (this) {
            registeredServicesCacheListener = this.mListener;
            handler = this.mHandler;
        }
        if (registeredServicesCacheListener == null) {
            return;
        }
        handler.post(new Runnable() { // from class: android.content.pm.RegisteredServicesCache.3
            /* JADX WARN: Multi-variable type inference failed */
            /* JADX WARN: Type inference fix 'apply assigned field type' failed
            java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$PrimitiveArg
            	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
            	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
            	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
             */
            @Override // java.lang.Runnable
            public void run() {
                registeredServicesCacheListener.onServiceChanged(v, i, z);
            }
        });
    }

    public static class ServiceInfo<V> {
        public final ComponentName componentName;
        public final V type;
        public final int uid;

        public ServiceInfo(V v, ComponentName componentName, int i) {
            this.type = v;
            this.componentName = componentName;
            this.uid = i;
        }

        public String toString() {
            return "ServiceInfo: " + this.type + ", " + this.componentName + ", uid " + this.uid;
        }
    }

    public ServiceInfo<V> getServiceInfo(V v, int i) {
        ServiceInfo<V> serviceInfo;
        synchronized (this.mServicesLock) {
            UserServices<V> userServicesFindOrCreateUserLocked = findOrCreateUserLocked(i);
            if (userServicesFindOrCreateUserLocked.services == null) {
                generateServicesMap(i);
            }
            serviceInfo = userServicesFindOrCreateUserLocked.services.get(v);
        }
        return serviceInfo;
    }

    public Collection<ServiceInfo<V>> getAllServices(int i) {
        Collection<ServiceInfo<V>> collectionUnmodifiableCollection;
        synchronized (this.mServicesLock) {
            UserServices<V> userServicesFindOrCreateUserLocked = findOrCreateUserLocked(i);
            if (userServicesFindOrCreateUserLocked.services == null) {
                generateServicesMap(i);
            }
            collectionUnmodifiableCollection = Collections.unmodifiableCollection(new ArrayList(userServicesFindOrCreateUserLocked.services.values()));
        }
        return collectionUnmodifiableCollection;
    }

    private boolean inSystemImage(int i) {
        for (String str : this.mContext.getPackageManager().getPackagesForUid(i)) {
            try {
                if ((this.mContext.getPackageManager().getPackageInfo(str, 0).applicationInfo.flags & 1) != 0) {
                    return true;
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public void generateServicesMap(int i) throws Throwable {
        PackageManager packageManager = this.mContext.getPackageManager();
        ArrayList<ServiceInfo<V>> arrayList = new ArrayList();
        for (ResolveInfo resolveInfo : packageManager.queryIntentServicesAsUser(new Intent(this.mInterfaceName), 128, i)) {
            try {
                ServiceInfo serviceInfo = parseServiceInfo(resolveInfo);
                if (serviceInfo == null) {
                    Log.w(TAG, "Unable to load service info " + resolveInfo.toString());
                } else {
                    arrayList.add(serviceInfo);
                }
            } catch (IOException e) {
                Log.w(TAG, "Unable to load service info " + resolveInfo.toString(), e);
            } catch (XmlPullParserException e2) {
                Log.w(TAG, "Unable to load service info " + resolveInfo.toString(), e2);
            }
        }
        synchronized (this.mServicesLock) {
            UserServices userServicesFindOrCreateUserLocked = findOrCreateUserLocked(i);
            boolean z = userServicesFindOrCreateUserLocked.services == null;
            if (z) {
                userServicesFindOrCreateUserLocked.services = Maps.newHashMap();
            } else {
                userServicesFindOrCreateUserLocked.services.clear();
            }
            boolean z2 = false;
            for (ServiceInfo<V> serviceInfo2 : arrayList) {
                Integer num = userServicesFindOrCreateUserLocked.persistentServices.get(serviceInfo2.type);
                if (num == null) {
                    userServicesFindOrCreateUserLocked.services.put(serviceInfo2.type, serviceInfo2);
                    userServicesFindOrCreateUserLocked.persistentServices.put(serviceInfo2.type, Integer.valueOf(serviceInfo2.uid));
                    if (!this.mPersistentServicesFileDidNotExist || !z) {
                        notifyListener(serviceInfo2.type, i, false);
                    }
                } else if (num.intValue() == serviceInfo2.uid) {
                    userServicesFindOrCreateUserLocked.services.put(serviceInfo2.type, serviceInfo2);
                } else if (inSystemImage(serviceInfo2.uid) || !containsTypeAndUid(arrayList, serviceInfo2.type, num.intValue())) {
                    userServicesFindOrCreateUserLocked.services.put(serviceInfo2.type, serviceInfo2);
                    userServicesFindOrCreateUserLocked.persistentServices.put(serviceInfo2.type, Integer.valueOf(serviceInfo2.uid));
                    notifyListener(serviceInfo2.type, i, false);
                }
                z2 = true;
            }
            ArrayList arrayListNewArrayList = Lists.newArrayList();
            for (V v : userServicesFindOrCreateUserLocked.persistentServices.keySet()) {
                if (!containsType(arrayList, v)) {
                    arrayListNewArrayList.add(v);
                }
            }
            for (Object obj : arrayListNewArrayList) {
                userServicesFindOrCreateUserLocked.persistentServices.remove(obj);
                notifyListener(obj, i, true);
                z2 = true;
            }
            if (z2) {
                writePersistentServicesLocked();
            }
        }
    }

    private boolean containsType(ArrayList<ServiceInfo<V>> arrayList, V v) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            if (arrayList.get(i).type.equals(v)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsTypeAndUid(ArrayList<ServiceInfo<V>> arrayList, V v, int i) {
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            ServiceInfo<V> serviceInfo = arrayList.get(i2);
            if (serviceInfo.type.equals(v) && serviceInfo.uid == i) {
                return true;
            }
        }
        return false;
    }

    private ServiceInfo<V> parseServiceInfo(ResolveInfo resolveInfo) throws Throwable {
        XmlResourceParser xmlResourceParserLoadXmlMetaData;
        int next;
        android.content.pm.ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        ComponentName componentName = new ComponentName(serviceInfo.packageName, serviceInfo.name);
        PackageManager packageManager = this.mContext.getPackageManager();
        XmlResourceParser xmlResourceParser = null;
        try {
            try {
                xmlResourceParserLoadXmlMetaData = serviceInfo.loadXmlMetaData(packageManager, this.mMetaDataName);
            } catch (Throwable th) {
                th = th;
            }
        } catch (PackageManager.NameNotFoundException unused) {
        }
        try {
            if (xmlResourceParserLoadXmlMetaData == null) {
                throw new XmlPullParserException("No " + this.mMetaDataName + " meta-data");
            }
            AttributeSet attributeSetAsAttributeSet = Xml.asAttributeSet(xmlResourceParserLoadXmlMetaData);
            do {
                next = xmlResourceParserLoadXmlMetaData.next();
                if (next == 1) {
                    break;
                }
            } while (next != 2);
            if (!this.mAttributesName.equals(xmlResourceParserLoadXmlMetaData.getName())) {
                throw new XmlPullParserException("Meta-data does not start with " + this.mAttributesName + " tag");
            }
            V serviceAttributes = parseServiceAttributes(packageManager.getResourcesForApplication(serviceInfo.applicationInfo), serviceInfo.packageName, attributeSetAsAttributeSet);
            if (serviceAttributes == null) {
                if (xmlResourceParserLoadXmlMetaData != null) {
                    xmlResourceParserLoadXmlMetaData.close();
                }
                return null;
            }
            ServiceInfo<V> serviceInfo2 = new ServiceInfo<>(serviceAttributes, componentName, resolveInfo.serviceInfo.applicationInfo.uid);
            if (xmlResourceParserLoadXmlMetaData != null) {
                xmlResourceParserLoadXmlMetaData.close();
            }
            return serviceInfo2;
        } catch (PackageManager.NameNotFoundException unused2) {
            throw new XmlPullParserException("Unable to load resources for pacakge " + serviceInfo.packageName);
        } catch (Throwable th2) {
            th = th2;
            xmlResourceParser = xmlResourceParserLoadXmlMetaData;
            if (xmlResourceParser != null) {
                xmlResourceParser.close();
            }
            throw th;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x0086 A[Catch: Exception -> 0x0092, all -> 0x00a8, TRY_LEAVE, TryCatch #3 {all -> 0x00a8, blocks: (B:14:0x0026, B:18:0x0036, B:19:0x003b, B:21:0x0048, B:23:0x004e, B:25:0x0054, B:27:0x0061, B:30:0x006a, B:31:0x0086, B:42:0x009d), top: B:54:0x000b }] */
    /* JADX WARN: Removed duplicated region for block: B:55:0x00ab A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:65:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void readPersistentServicesLocked() throws java.lang.Throwable {
        /*
            r9 = this;
            android.util.SparseArray<android.content.pm.RegisteredServicesCache$UserServices<V>> r0 = r9.mUserServices
            r0.clear()
            android.content.pm.XmlSerializerAndParser<V> r0 = r9.mSerializerAndParser
            if (r0 != 0) goto La
            return
        La:
            r0 = 0
            android.util.AtomicFile r1 = r9.mPersistentServicesFile     // Catch: java.lang.Throwable -> L94 java.lang.Exception -> L99
            java.io.File r1 = r1.getBaseFile()     // Catch: java.lang.Throwable -> L94 java.lang.Exception -> L99
            boolean r1 = r1.exists()     // Catch: java.lang.Throwable -> L94 java.lang.Exception -> L99
            r2 = 1
            if (r1 != 0) goto L1a
            r1 = r2
            goto L1b
        L1a:
            r1 = 0
        L1b:
            r9.mPersistentServicesFileDidNotExist = r1     // Catch: java.lang.Throwable -> L94 java.lang.Exception -> L99
            if (r1 == 0) goto L20
            return
        L20:
            android.util.AtomicFile r1 = r9.mPersistentServicesFile     // Catch: java.lang.Throwable -> L94 java.lang.Exception -> L99
            java.io.FileInputStream r1 = r1.openRead()     // Catch: java.lang.Throwable -> L94 java.lang.Exception -> L99
            org.xmlpull.v1.XmlPullParser r3 = android.util.Xml.newPullParser()     // Catch: java.lang.Exception -> L92 java.lang.Throwable -> La8
            r3.setInput(r1, r0)     // Catch: java.lang.Exception -> L92 java.lang.Throwable -> La8
            int r4 = r3.getEventType()     // Catch: java.lang.Exception -> L92 java.lang.Throwable -> La8
        L31:
            r5 = 2
            if (r4 == r5) goto L3b
            if (r4 == r2) goto L3b
            int r4 = r3.next()     // Catch: java.lang.Exception -> L92 java.lang.Throwable -> La8
            goto L31
        L3b:
            java.lang.String r4 = r3.getName()     // Catch: java.lang.Exception -> L92 java.lang.Throwable -> La8
            java.lang.String r6 = "services"
            boolean r4 = r6.equals(r4)     // Catch: java.lang.Exception -> L92 java.lang.Throwable -> La8
            if (r4 == 0) goto L8c
            int r4 = r3.next()     // Catch: java.lang.Exception -> L92 java.lang.Throwable -> La8
        L4c:
            if (r4 != r5) goto L86
            int r4 = r3.getDepth()     // Catch: java.lang.Exception -> L92 java.lang.Throwable -> La8
            if (r4 != r5) goto L86
            java.lang.String r4 = r3.getName()     // Catch: java.lang.Exception -> L92 java.lang.Throwable -> La8
            java.lang.String r6 = "service"
            boolean r4 = r6.equals(r4)     // Catch: java.lang.Exception -> L92 java.lang.Throwable -> La8
            if (r4 == 0) goto L86
            android.content.pm.XmlSerializerAndParser<V> r4 = r9.mSerializerAndParser     // Catch: java.lang.Exception -> L92 java.lang.Throwable -> La8
            java.lang.Object r4 = r4.createFromXml(r3)     // Catch: java.lang.Exception -> L92 java.lang.Throwable -> La8
            if (r4 != 0) goto L6a
            goto L8c
        L6a:
            java.lang.String r6 = "uid"
            java.lang.String r6 = r3.getAttributeValue(r0, r6)     // Catch: java.lang.Exception -> L92 java.lang.Throwable -> La8
            int r6 = java.lang.Integer.parseInt(r6)     // Catch: java.lang.Exception -> L92 java.lang.Throwable -> La8
            int r7 = android.os.UserHandle.getUserId(r6)     // Catch: java.lang.Exception -> L92 java.lang.Throwable -> La8
            android.content.pm.RegisteredServicesCache$UserServices r7 = r9.findOrCreateUserLocked(r7)     // Catch: java.lang.Exception -> L92 java.lang.Throwable -> La8
            java.util.Map<V, java.lang.Integer> r7 = r7.persistentServices     // Catch: java.lang.Exception -> L92 java.lang.Throwable -> La8
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch: java.lang.Exception -> L92 java.lang.Throwable -> La8
            r7.put(r4, r6)     // Catch: java.lang.Exception -> L92 java.lang.Throwable -> La8
        L86:
            int r4 = r3.next()     // Catch: java.lang.Exception -> L92 java.lang.Throwable -> La8
            if (r4 != r2) goto L4c
        L8c:
            if (r1 == 0) goto La7
        L8e:
            r1.close()     // Catch: java.io.IOException -> La7
            goto La7
        L92:
            r0 = move-exception
            goto L9d
        L94:
            r1 = move-exception
            r8 = r1
            r1 = r0
            r0 = r8
            goto La9
        L99:
            r1 = move-exception
            r8 = r1
            r1 = r0
            r0 = r8
        L9d:
            java.lang.String r2 = "PackageManager"
            java.lang.String r3 = "Error reading persistent services, starting from scratch"
            android.util.Log.w(r2, r3, r0)     // Catch: java.lang.Throwable -> La8
            if (r1 == 0) goto La7
            goto L8e
        La7:
            return
        La8:
            r0 = move-exception
        La9:
            if (r1 == 0) goto Lae
            r1.close()     // Catch: java.io.IOException -> Lae
        Lae:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.RegisteredServicesCache.readPersistentServicesLocked():void");
    }

    private void writePersistentServicesLocked() {
        FileOutputStream fileOutputStreamStartWrite;
        if (this.mSerializerAndParser == null) {
            return;
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStreamStartWrite = this.mPersistentServicesFile.startWrite();
        } catch (IOException e) {
            e = e;
        }
        try {
            FastXmlSerializer fastXmlSerializer = new FastXmlSerializer();
            fastXmlSerializer.setOutput(fileOutputStreamStartWrite, "utf-8");
            fastXmlSerializer.startDocument(null, true);
            fastXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            fastXmlSerializer.startTag(null, "services");
            for (int i = 0; i < this.mUserServices.size(); i++) {
                for (Map.Entry<V, Integer> entry : this.mUserServices.valueAt(i).persistentServices.entrySet()) {
                    fastXmlSerializer.startTag(null, NotificationCompat.CATEGORY_SERVICE);
                    fastXmlSerializer.attribute(null, GrantCredentialsPermissionActivity.EXTRAS_REQUESTING_UID, Integer.toString(entry.getValue().intValue()));
                    this.mSerializerAndParser.writeAsXml(entry.getKey(), fastXmlSerializer);
                    fastXmlSerializer.endTag(null, NotificationCompat.CATEGORY_SERVICE);
                }
            }
            fastXmlSerializer.endTag(null, "services");
            fastXmlSerializer.endDocument();
            this.mPersistentServicesFile.finishWrite(fileOutputStreamStartWrite);
        } catch (IOException e2) {
            e = e2;
            fileOutputStream = fileOutputStreamStartWrite;
            Log.w(TAG, "Error writing accounts", e);
            if (fileOutputStream != null) {
                this.mPersistentServicesFile.failWrite(fileOutputStream);
            }
        }
    }
}
