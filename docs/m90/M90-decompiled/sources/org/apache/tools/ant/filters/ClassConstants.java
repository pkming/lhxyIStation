package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public final class ClassConstants extends BaseFilterReader implements ChainableReader {
    private static final String JAVA_CLASS_HELPER = "org.apache.tools.ant.filters.util.JavaClassHelper";
    private String queuedData;

    public ClassConstants() {
        this.queuedData = null;
    }

    public ClassConstants(Reader reader) {
        super(reader);
        this.queuedData = null;
    }

    @Override // java.io.FilterReader, java.io.Reader
    public int read() throws IOException {
        String str = this.queuedData;
        if (str != null && str.length() == 0) {
            this.queuedData = null;
        }
        String str2 = this.queuedData;
        int iCharAt = -1;
        if (str2 != null) {
            iCharAt = str2.charAt(0);
            String strSubstring = this.queuedData.substring(1);
            this.queuedData = strSubstring;
            if (strSubstring.length() == 0) {
                this.queuedData = null;
            }
        } else {
            String fully = readFully();
            if (fully != null && fully.length() != 0) {
                byte[] bytes = fully.getBytes("ISO-8859-1");
                try {
                    Class<?> cls = Class.forName(JAVA_CLASS_HELPER);
                    if (cls != null) {
                        StringBuffer stringBuffer = (StringBuffer) cls.getMethod("getConstants", byte[].class).invoke(null, bytes);
                        if (stringBuffer.length() > 0) {
                            this.queuedData = stringBuffer.toString();
                            return read();
                        }
                    }
                } catch (NoClassDefFoundError e) {
                    throw e;
                } catch (RuntimeException e2) {
                    throw e2;
                } catch (InvocationTargetException e3) {
                    Throwable targetException = e3.getTargetException();
                    if (targetException instanceof NoClassDefFoundError) {
                        throw ((NoClassDefFoundError) targetException);
                    }
                    if (targetException instanceof RuntimeException) {
                        throw ((RuntimeException) targetException);
                    }
                    throw new BuildException(targetException);
                } catch (Exception e4) {
                    throw new BuildException(e4);
                }
            }
        }
        return iCharAt;
    }

    @Override // org.apache.tools.ant.filters.ChainableReader
    public Reader chain(Reader reader) {
        return new ClassConstants(reader);
    }
}
