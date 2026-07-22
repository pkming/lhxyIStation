package org.apache.tools.ant.types.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes3.dex */
public class JavaConstantResource extends AbstractClasspathResource {
    @Override // org.apache.tools.ant.types.resources.AbstractClasspathResource
    protected InputStream openInputStream(ClassLoader classLoader) throws IOException {
        String name = getName();
        int iLastIndexOf = name.lastIndexOf(46);
        if (iLastIndexOf < 0) {
            throw new IOException("No class name in " + name);
        }
        String strSubstring = name.substring(0, iLastIndexOf);
        String strSubstring2 = name.substring(iLastIndexOf + 1, name.length());
        try {
            return new ByteArrayInputStream((classLoader != null ? Class.forName(strSubstring, true, classLoader) : Class.forName(strSubstring)).getField(strSubstring2).get(null).toString().getBytes("UTF-8"));
        } catch (ClassNotFoundException unused) {
            throw new IOException("Class not found:" + strSubstring);
        } catch (IllegalAccessException unused2) {
            throw new IOException("Illegal access to :" + strSubstring2 + " in " + strSubstring);
        } catch (NoSuchFieldException unused3) {
            throw new IOException("Field not found:" + strSubstring2 + " in " + strSubstring);
        } catch (NullPointerException unused4) {
            throw new IOException("Not a static field: " + strSubstring2 + " in " + strSubstring);
        }
    }
}
