package org.apache.tools.ant.taskdefs.optional.extension;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Reference;

/* JADX INFO: loaded from: classes3.dex */
public class ExtensionAdapter extends DataType {
    private String extensionName;
    private String implementationURL;
    private String implementationVendor;
    private String implementationVendorID;
    private org.apache.tools.ant.util.DeweyDecimal implementationVersion;
    private String specificationVendor;
    private org.apache.tools.ant.util.DeweyDecimal specificationVersion;

    public void setExtensionName(String str) {
        verifyNotAReference();
        this.extensionName = str;
    }

    public void setSpecificationVersion(String str) {
        verifyNotAReference();
        this.specificationVersion = new org.apache.tools.ant.util.DeweyDecimal(str);
    }

    public void setSpecificationVendor(String str) {
        verifyNotAReference();
        this.specificationVendor = str;
    }

    public void setImplementationVendorId(String str) {
        verifyNotAReference();
        this.implementationVendorID = str;
    }

    public void setImplementationVendor(String str) {
        verifyNotAReference();
        this.implementationVendor = str;
    }

    public void setImplementationVersion(String str) {
        verifyNotAReference();
        this.implementationVersion = new org.apache.tools.ant.util.DeweyDecimal(str);
    }

    public void setImplementationUrl(String str) {
        verifyNotAReference();
        this.implementationURL = str;
    }

    @Override // org.apache.tools.ant.types.DataType
    public void setRefid(Reference reference) throws BuildException {
        if (this.extensionName != null || this.specificationVersion != null || this.specificationVendor != null || this.implementationVersion != null || this.implementationVendorID != null || this.implementationVendor != null || this.implementationURL != null) {
            throw tooManyAttributes();
        }
        super.setRefid(reference);
    }

    private void verifyNotAReference() throws BuildException {
        if (isReference()) {
            throw tooManyAttributes();
        }
    }

    Extension toExtension() throws BuildException {
        if (isReference()) {
            return ((ExtensionAdapter) getCheckedRef()).toExtension();
        }
        dieOnCircularReference();
        if (this.extensionName == null) {
            throw new BuildException("Extension is missing name.");
        }
        org.apache.tools.ant.util.DeweyDecimal deweyDecimal = this.specificationVersion;
        String string = deweyDecimal != null ? deweyDecimal.toString() : null;
        org.apache.tools.ant.util.DeweyDecimal deweyDecimal2 = this.implementationVersion;
        return new Extension(this.extensionName, string, this.specificationVendor, deweyDecimal2 != null ? deweyDecimal2.toString() : null, this.implementationVendor, this.implementationVendorID, this.implementationURL);
    }

    @Override // org.apache.tools.ant.types.DataType
    public String toString() {
        return "{" + toExtension().toString() + "}";
    }
}
