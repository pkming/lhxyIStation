package org.apache.tools.ant.taskdefs.optional.extension;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.apache.tools.ant.util.StringUtils;

/* JADX INFO: loaded from: classes3.dex */
public final class Extension {
    private String extensionName;
    private String implementationURL;
    private String implementationVendor;
    private String implementationVendorID;
    private org.apache.tools.ant.util.DeweyDecimal implementationVersion;
    private String specificationVendor;
    private org.apache.tools.ant.util.DeweyDecimal specificationVersion;
    public static final Attributes.Name EXTENSION_LIST = new Attributes.Name("Extension-List");
    public static final Attributes.Name OPTIONAL_EXTENSION_LIST = new Attributes.Name("Optional-Extension-List");
    public static final Attributes.Name EXTENSION_NAME = new Attributes.Name("Extension-Name");
    public static final Attributes.Name SPECIFICATION_VERSION = Attributes.Name.SPECIFICATION_VERSION;
    public static final Attributes.Name SPECIFICATION_VENDOR = Attributes.Name.SPECIFICATION_VENDOR;
    public static final Attributes.Name IMPLEMENTATION_VERSION = Attributes.Name.IMPLEMENTATION_VERSION;
    public static final Attributes.Name IMPLEMENTATION_VENDOR = Attributes.Name.IMPLEMENTATION_VENDOR;
    public static final Attributes.Name IMPLEMENTATION_URL = new Attributes.Name("Implementation-URL");
    public static final Attributes.Name IMPLEMENTATION_VENDOR_ID = new Attributes.Name("Implementation-Vendor-Id");
    public static final Compatibility COMPATIBLE = new Compatibility("COMPATIBLE");
    public static final Compatibility REQUIRE_SPECIFICATION_UPGRADE = new Compatibility("REQUIRE_SPECIFICATION_UPGRADE");
    public static final Compatibility REQUIRE_VENDOR_SWITCH = new Compatibility("REQUIRE_VENDOR_SWITCH");
    public static final Compatibility REQUIRE_IMPLEMENTATION_UPGRADE = new Compatibility("REQUIRE_IMPLEMENTATION_UPGRADE");
    public static final Compatibility INCOMPATIBLE = new Compatibility("INCOMPATIBLE");

    public static Extension[] getAvailable(Manifest manifest) {
        Extension extension;
        if (manifest == null) {
            return new Extension[0];
        }
        ArrayList arrayList = new ArrayList();
        Attributes mainAttributes = manifest.getMainAttributes();
        if (mainAttributes != null && (extension = getExtension("", mainAttributes)) != null) {
            arrayList.add(extension);
        }
        Map<String, Attributes> entries = manifest.getEntries();
        Iterator<String> it = entries.keySet().iterator();
        while (it.hasNext()) {
            Extension extension2 = getExtension("", entries.get(it.next()));
            if (extension2 != null) {
                arrayList.add(extension2);
            }
        }
        return (Extension[]) arrayList.toArray(new Extension[arrayList.size()]);
    }

    public static Extension[] getRequired(Manifest manifest) {
        return getListed(manifest, Attributes.Name.EXTENSION_LIST);
    }

    public static Extension[] getOptions(Manifest manifest) {
        return getListed(manifest, OPTIONAL_EXTENSION_LIST);
    }

    public static void addExtension(Extension extension, Attributes attributes) {
        addExtension(extension, "", attributes);
    }

    public static void addExtension(Extension extension, String str, Attributes attributes) {
        attributes.putValue(str + EXTENSION_NAME, extension.getExtensionName());
        String specificationVendor = extension.getSpecificationVendor();
        if (specificationVendor != null) {
            attributes.putValue(str + SPECIFICATION_VENDOR, specificationVendor);
        }
        org.apache.tools.ant.util.DeweyDecimal specificationVersion = extension.getSpecificationVersion();
        if (specificationVersion != null) {
            attributes.putValue(str + SPECIFICATION_VERSION, specificationVersion.toString());
        }
        String implementationVendorID = extension.getImplementationVendorID();
        if (implementationVendorID != null) {
            attributes.putValue(str + IMPLEMENTATION_VENDOR_ID, implementationVendorID);
        }
        String implementationVendor = extension.getImplementationVendor();
        if (implementationVendor != null) {
            attributes.putValue(str + IMPLEMENTATION_VENDOR, implementationVendor);
        }
        org.apache.tools.ant.util.DeweyDecimal implementationVersion = extension.getImplementationVersion();
        if (implementationVersion != null) {
            attributes.putValue(str + IMPLEMENTATION_VERSION, implementationVersion.toString());
        }
        String implementationURL = extension.getImplementationURL();
        if (implementationURL != null) {
            attributes.putValue(str + IMPLEMENTATION_URL, implementationURL);
        }
    }

    public Extension(String str, String str2, String str3, String str4, String str5, String str6, String str7) {
        this.extensionName = str;
        this.specificationVendor = str3;
        if (str2 != null) {
            try {
                this.specificationVersion = new org.apache.tools.ant.util.DeweyDecimal(str2);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Bad specification version format '" + str2 + "' in '" + str + "'. (Reason: " + e + ")");
            }
        }
        this.implementationURL = str7;
        this.implementationVendor = str5;
        this.implementationVendorID = str6;
        if (str4 != null) {
            try {
                this.implementationVersion = new org.apache.tools.ant.util.DeweyDecimal(str4);
            } catch (NumberFormatException e2) {
                throw new IllegalArgumentException("Bad implementation version format '" + str4 + "' in '" + str + "'. (Reason: " + e2 + ")");
            }
        }
        Objects.requireNonNull(this.extensionName, "extensionName property is null");
    }

    public String getExtensionName() {
        return this.extensionName;
    }

    public String getSpecificationVendor() {
        return this.specificationVendor;
    }

    public org.apache.tools.ant.util.DeweyDecimal getSpecificationVersion() {
        return this.specificationVersion;
    }

    public String getImplementationURL() {
        return this.implementationURL;
    }

    public String getImplementationVendor() {
        return this.implementationVendor;
    }

    public String getImplementationVendorID() {
        return this.implementationVendorID;
    }

    public org.apache.tools.ant.util.DeweyDecimal getImplementationVersion() {
        return this.implementationVersion;
    }

    public Compatibility getCompatibilityWith(Extension extension) {
        org.apache.tools.ant.util.DeweyDecimal deweyDecimal;
        String str;
        org.apache.tools.ant.util.DeweyDecimal deweyDecimal2;
        if (!this.extensionName.equals(extension.getExtensionName())) {
            return INCOMPATIBLE;
        }
        org.apache.tools.ant.util.DeweyDecimal specificationVersion = extension.getSpecificationVersion();
        if (specificationVersion != null && ((deweyDecimal2 = this.specificationVersion) == null || !isCompatible(deweyDecimal2, specificationVersion))) {
            return REQUIRE_SPECIFICATION_UPGRADE;
        }
        String implementationVendorID = extension.getImplementationVendorID();
        if (implementationVendorID != null && ((str = this.implementationVendorID) == null || !str.equals(implementationVendorID))) {
            return REQUIRE_VENDOR_SWITCH;
        }
        org.apache.tools.ant.util.DeweyDecimal implementationVersion = extension.getImplementationVersion();
        if (implementationVersion != null && ((deweyDecimal = this.implementationVersion) == null || !isCompatible(deweyDecimal, implementationVersion))) {
            return REQUIRE_IMPLEMENTATION_UPGRADE;
        }
        return COMPATIBLE;
    }

    public boolean isCompatibleWith(Extension extension) {
        return COMPATIBLE == getCompatibilityWith(extension);
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer(EXTENSION_NAME.toString());
        stringBuffer.append(": ");
        stringBuffer.append(this.extensionName);
        stringBuffer.append(StringUtils.LINE_SEP);
        if (this.specificationVersion != null) {
            stringBuffer.append(SPECIFICATION_VERSION);
            stringBuffer.append(": ");
            stringBuffer.append(this.specificationVersion);
            stringBuffer.append(StringUtils.LINE_SEP);
        }
        if (this.specificationVendor != null) {
            stringBuffer.append(SPECIFICATION_VENDOR);
            stringBuffer.append(": ");
            stringBuffer.append(this.specificationVendor);
            stringBuffer.append(StringUtils.LINE_SEP);
        }
        if (this.implementationVersion != null) {
            stringBuffer.append(IMPLEMENTATION_VERSION);
            stringBuffer.append(": ");
            stringBuffer.append(this.implementationVersion);
            stringBuffer.append(StringUtils.LINE_SEP);
        }
        if (this.implementationVendorID != null) {
            stringBuffer.append(IMPLEMENTATION_VENDOR_ID);
            stringBuffer.append(": ");
            stringBuffer.append(this.implementationVendorID);
            stringBuffer.append(StringUtils.LINE_SEP);
        }
        if (this.implementationVendor != null) {
            stringBuffer.append(IMPLEMENTATION_VENDOR);
            stringBuffer.append(": ");
            stringBuffer.append(this.implementationVendor);
            stringBuffer.append(StringUtils.LINE_SEP);
        }
        if (this.implementationURL != null) {
            stringBuffer.append(IMPLEMENTATION_URL);
            stringBuffer.append(": ");
            stringBuffer.append(this.implementationURL);
            stringBuffer.append(StringUtils.LINE_SEP);
        }
        return stringBuffer.toString();
    }

    private boolean isCompatible(org.apache.tools.ant.util.DeweyDecimal deweyDecimal, org.apache.tools.ant.util.DeweyDecimal deweyDecimal2) {
        return deweyDecimal.isGreaterThanOrEqual(deweyDecimal2);
    }

    private static Extension[] getListed(Manifest manifest, Attributes.Name name) {
        ArrayList arrayList = new ArrayList();
        Attributes mainAttributes = manifest.getMainAttributes();
        if (mainAttributes != null) {
            getExtension(mainAttributes, arrayList, name);
        }
        Map<String, Attributes> entries = manifest.getEntries();
        Iterator<String> it = entries.keySet().iterator();
        while (it.hasNext()) {
            getExtension(entries.get(it.next()), arrayList, name);
        }
        return (Extension[]) arrayList.toArray(new Extension[arrayList.size()]);
    }

    private static void getExtension(Attributes attributes, ArrayList arrayList, Attributes.Name name) {
        String value = attributes.getValue(name);
        if (value == null) {
            return;
        }
        for (String str : split(value, " ")) {
            Extension extension = getExtension(str + "-", attributes);
            if (extension != null) {
                arrayList.add(extension);
            }
        }
    }

    private static String[] split(String str, String str2) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, str2);
        int iCountTokens = stringTokenizer.countTokens();
        String[] strArr = new String[iCountTokens];
        for (int i = 0; i < iCountTokens; i++) {
            strArr[i] = stringTokenizer.nextToken();
        }
        return strArr;
    }

    private static Extension getExtension(String str, Attributes attributes) {
        String trimmedString = getTrimmedString(attributes.getValue(str + EXTENSION_NAME));
        if (trimmedString == null) {
            return null;
        }
        return new Extension(trimmedString, getTrimmedString(attributes.getValue(str + SPECIFICATION_VERSION)), getTrimmedString(attributes.getValue(str + SPECIFICATION_VENDOR)), getTrimmedString(attributes.getValue(str + IMPLEMENTATION_VERSION)), getTrimmedString(attributes.getValue(str + IMPLEMENTATION_VENDOR)), getTrimmedString(attributes.getValue(str + IMPLEMENTATION_VENDOR_ID)), getTrimmedString(attributes.getValue(str + IMPLEMENTATION_URL)));
    }

    private static String getTrimmedString(String str) {
        if (str == null) {
            return null;
        }
        return str.trim();
    }
}
