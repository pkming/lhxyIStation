package org.apache.tools.ant.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Vector;
import org.apache.tools.ant.taskdefs.condition.Os;

/* JADX INFO: loaded from: classes3.dex */
public final class JavaEnvUtils {
    public static final String JAVA_1_0 = "1.0";
    public static final String JAVA_1_1 = "1.1";
    public static final String JAVA_1_2 = "1.2";
    public static final String JAVA_1_3 = "1.3";
    public static final String JAVA_1_4 = "1.4";
    public static final String JAVA_1_5 = "1.5";
    public static final String JAVA_1_6 = "1.6";
    public static final String JAVA_1_7 = "1.7";
    public static final String JAVA_1_8 = "1.8";
    public static final String JAVA_1_9 = "1.9";
    public static final int VERSION_1_0 = 10;
    public static final int VERSION_1_1 = 11;
    public static final int VERSION_1_2 = 12;
    public static final int VERSION_1_3 = 13;
    public static final int VERSION_1_4 = 14;
    public static final int VERSION_1_5 = 15;
    public static final int VERSION_1_6 = 16;
    public static final int VERSION_1_7 = 17;
    public static final int VERSION_1_8 = 18;
    public static final int VERSION_1_9 = 19;
    private static boolean classpathDetected;
    private static boolean gijDetected;
    private static boolean harmonyDetected;
    private static String javaVersion;
    private static int javaVersionNumber;
    private static Vector<String> jrePackages;
    private static boolean kaffeDetected;
    private static final boolean IS_DOS = Os.isFamily(Os.FAMILY_DOS);
    private static final boolean IS_NETWARE = Os.isName(Os.FAMILY_NETWARE);
    private static final boolean IS_AIX = Os.isName("aix");
    private static final String JAVA_HOME = System.getProperty("java.home");
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();

    private JavaEnvUtils() {
    }

    static {
        try {
            javaVersion = "1.0";
            javaVersionNumber = 10;
            Class.forName("java.lang.Void");
            javaVersion = "1.1";
            javaVersionNumber++;
            Class.forName("java.lang.ThreadLocal");
            javaVersion = "1.2";
            javaVersionNumber++;
            Class.forName("java.lang.StrictMath");
            javaVersion = "1.3";
            javaVersionNumber++;
            Class.forName("java.lang.CharSequence");
            javaVersion = "1.4";
            javaVersionNumber++;
            Class.forName("java.net.Proxy");
            javaVersion = JAVA_1_5;
            javaVersionNumber++;
            Class.forName("java.net.CookieStore");
            javaVersion = JAVA_1_6;
            javaVersionNumber++;
            Class.forName("java.nio.file.FileSystem");
            javaVersion = JAVA_1_7;
            javaVersionNumber++;
            Class.forName("java.lang.reflect.Executable");
            javaVersion = JAVA_1_8;
            javaVersionNumber++;
            checkForJava9();
            javaVersion = JAVA_1_9;
            javaVersionNumber++;
        } catch (Throwable unused) {
        }
        kaffeDetected = false;
        try {
            Class.forName("kaffe.util.NotImplemented");
            kaffeDetected = true;
        } catch (Throwable unused2) {
        }
        classpathDetected = false;
        try {
            Class.forName("gnu.classpath.Configuration");
            classpathDetected = true;
        } catch (Throwable unused3) {
        }
        gijDetected = false;
        try {
            Class.forName("gnu.gcj.Core");
            gijDetected = true;
        } catch (Throwable unused4) {
        }
        harmonyDetected = false;
        try {
            Class.forName("org.apache.harmony.luni.util.Base64");
            harmonyDetected = true;
        } catch (Throwable unused5) {
        }
    }

    public static String getJavaVersion() {
        return javaVersion;
    }

    private static void checkForJava9() throws Exception {
        Class.forName("javax.lang.model.SourceVersion").getDeclaredField("RELEASE_9");
    }

    public static int getJavaVersionNumber() {
        return javaVersionNumber;
    }

    public static boolean isJavaVersion(String str) {
        return javaVersion.equals(str);
    }

    public static boolean isAtLeastJavaVersion(String str) {
        return javaVersion.compareTo(str) >= 0;
    }

    public static boolean isKaffe() {
        return kaffeDetected;
    }

    public static boolean isClasspathBased() {
        return classpathDetected;
    }

    public static boolean isGij() {
        return gijDetected;
    }

    public static boolean isApacheHarmony() {
        return harmonyDetected;
    }

    public static String getJreExecutable(String str) {
        if (IS_NETWARE) {
            return str;
        }
        File fileFindInDir = IS_AIX ? findInDir(JAVA_HOME + "/sh", str) : null;
        if (fileFindInDir == null) {
            fileFindInDir = findInDir(JAVA_HOME + "/bin", str);
        }
        if (fileFindInDir != null) {
            return fileFindInDir.getAbsolutePath();
        }
        return addExtension(str);
    }

    public static String getJdkExecutable(String str) {
        if (IS_NETWARE) {
            return str;
        }
        File fileFindInDir = IS_AIX ? findInDir(JAVA_HOME + "/../sh", str) : null;
        if (fileFindInDir == null) {
            fileFindInDir = findInDir(JAVA_HOME + "/../bin", str);
        }
        if (fileFindInDir != null) {
            return fileFindInDir.getAbsolutePath();
        }
        return getJreExecutable(str);
    }

    private static String addExtension(String str) {
        return str + (IS_DOS ? ".exe" : "");
    }

    private static File findInDir(String str, String str2) {
        File fileNormalize = FILE_UTILS.normalize(str);
        if (!fileNormalize.exists()) {
            return null;
        }
        File file = new File(fileNormalize, addExtension(str2));
        if (file.exists()) {
            return file;
        }
        return null;
    }

    private static void buildJrePackages() {
        Vector<String> vector = new Vector<>();
        jrePackages = vector;
        switch (javaVersionNumber) {
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
                vector.addElement("com.sun.org.apache");
            case 14:
                if (javaVersionNumber == 14) {
                    jrePackages.addElement("org.apache.crimson");
                    jrePackages.addElement("org.apache.xalan");
                    jrePackages.addElement("org.apache.xml");
                    jrePackages.addElement("org.apache.xpath");
                }
                jrePackages.addElement("org.ietf.jgss");
                jrePackages.addElement("org.w3c.dom");
                jrePackages.addElement("org.xml.sax");
            case 13:
                jrePackages.addElement("org.omg");
                jrePackages.addElement("com.sun.corba");
                jrePackages.addElement("com.sun.jndi");
                jrePackages.addElement("com.sun.media");
                jrePackages.addElement("com.sun.naming");
                jrePackages.addElement("com.sun.org.omg");
                jrePackages.addElement("com.sun.rmi");
                jrePackages.addElement("sunw.io");
                jrePackages.addElement("sunw.util");
            case 12:
                jrePackages.addElement("com.sun.java");
                jrePackages.addElement("com.sun.image");
                break;
        }
        jrePackages.addElement("sun");
        jrePackages.addElement("java");
        jrePackages.addElement("javax");
    }

    public static Vector<String> getJrePackageTestCases() {
        Vector<String> vector = new Vector<>();
        vector.addElement("java.lang.Object");
        switch (javaVersionNumber) {
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
                vector.addElement("com.sun.org.apache.xerces.internal.jaxp.datatype.DatatypeFactoryImpl ");
            case 14:
                vector.addElement("sun.audio.AudioPlayer");
                if (javaVersionNumber == 14) {
                    vector.addElement("org.apache.crimson.parser.ContentModel");
                    vector.addElement("org.apache.xalan.processor.ProcessorImport");
                    vector.addElement("org.apache.xml.utils.URI");
                    vector.addElement("org.apache.xpath.XPathFactory");
                }
                vector.addElement("org.ietf.jgss.Oid");
                vector.addElement("org.w3c.dom.Attr");
                vector.addElement("org.xml.sax.XMLReader");
            case 13:
                vector.addElement("org.omg.CORBA.Any");
                vector.addElement("com.sun.corba.se.internal.corba.AnyImpl");
                vector.addElement("com.sun.jndi.ldap.LdapURL");
                vector.addElement("com.sun.media.sound.Printer");
                vector.addElement("com.sun.naming.internal.VersionHelper");
                vector.addElement("com.sun.org.omg.CORBA.Initializer");
                vector.addElement("sunw.io.Serializable");
                vector.addElement("sunw.util.EventListener");
            case 12:
                vector.addElement("javax.accessibility.Accessible");
                vector.addElement("sun.misc.BASE64Encoder");
                vector.addElement("com.sun.image.codec.jpeg.JPEGCodec");
                break;
        }
        vector.addElement("sun.reflect.SerializationConstructorAccessorImpl");
        vector.addElement("sun.net.www.http.HttpClient");
        vector.addElement("sun.audio.AudioPlayer");
        return vector;
    }

    public static Vector<String> getJrePackages() {
        if (jrePackages == null) {
            buildJrePackages();
        }
        return jrePackages;
    }

    public static File createVmsJavaOptionFile(String[] strArr) throws Throwable {
        File fileCreateTempFile = FILE_UTILS.createTempFile("ANT", ".JAVA_OPTS", null, false, true);
        BufferedWriter bufferedWriter = null;
        try {
            BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter(fileCreateTempFile));
            for (String str : strArr) {
                try {
                    bufferedWriter2.write(str);
                    bufferedWriter2.newLine();
                } catch (Throwable th) {
                    th = th;
                    bufferedWriter = bufferedWriter2;
                    FileUtils.close(bufferedWriter);
                    throw th;
                }
            }
            FileUtils.close(bufferedWriter2);
            return fileCreateTempFile;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static String getJavaHome() {
        return JAVA_HOME;
    }
}
