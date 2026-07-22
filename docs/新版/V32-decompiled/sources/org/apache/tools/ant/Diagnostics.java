package org.apache.tools.ant;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TimeZone;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import org.apache.tools.ant.launch.Launcher;
import org.apache.tools.ant.util.JAXPUtils;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.util.ProxySetup;

/* JADX INFO: loaded from: classes3.dex */
public final class Diagnostics {
    private static final int BIG_DRIFT_LIMIT = 10000;
    protected static final String ERROR_PROPERTY_ACCESS_BLOCKED = "Access to this property blocked by a security manager";
    private static final int JAVA_1_5_NUMBER = 15;
    private static final int KILOBYTE = 1024;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int SECONDS_PER_MILLISECOND = 1000;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int TEST_FILE_SIZE = 32;

    private static void ignoreThrowable(Throwable th) {
    }

    public static boolean isOptionalAvailable() {
        return true;
    }

    public static void validateVersion() throws BuildException {
    }

    private Diagnostics() {
    }

    public static File[] listLibraries() {
        String property = System.getProperty(MagicNames.ANT_HOME);
        if (property == null) {
            return null;
        }
        return listJarFiles(new File(property, "lib"));
    }

    private static File[] listJarFiles(File file) {
        return file.listFiles(new FilenameFilter() { // from class: org.apache.tools.ant.Diagnostics.1
            @Override // java.io.FilenameFilter
            public boolean accept(File file2, String str) {
                return str.endsWith(".jar");
            }
        });
    }

    public static void main(String[] strArr) {
        doReport(System.out);
    }

    private static String getImplementationVersion(Class<?> cls) {
        return cls.getPackage().getImplementationVersion();
    }

    private static URL getClassLocation(Class<?> cls) {
        if (cls.getProtectionDomain().getCodeSource() == null) {
            return null;
        }
        return cls.getProtectionDomain().getCodeSource().getLocation();
    }

    private static String getXMLParserName() {
        SAXParser sAXParser = getSAXParser();
        return sAXParser == null ? "Could not create an XML Parser" : sAXParser.getClass().getName();
    }

    private static String getXSLTProcessorName() {
        Transformer xSLTProcessor = getXSLTProcessor();
        return xSLTProcessor == null ? "Could not create an XSLT Processor" : xSLTProcessor.getClass().getName();
    }

    private static SAXParser getSAXParser() {
        SAXParserFactory sAXParserFactoryNewInstance = SAXParserFactory.newInstance();
        if (sAXParserFactoryNewInstance == null) {
            return null;
        }
        try {
            return sAXParserFactoryNewInstance.newSAXParser();
        } catch (Exception e) {
            ignoreThrowable(e);
            return null;
        }
    }

    private static Transformer getXSLTProcessor() {
        TransformerFactory transformerFactoryNewInstance = TransformerFactory.newInstance();
        if (transformerFactoryNewInstance == null) {
            return null;
        }
        try {
            return transformerFactoryNewInstance.newTransformer();
        } catch (Exception e) {
            ignoreThrowable(e);
            return null;
        }
    }

    private static String getXMLParserLocation() {
        URL classLocation;
        SAXParser sAXParser = getSAXParser();
        if (sAXParser == null || (classLocation = getClassLocation(sAXParser.getClass())) == null) {
            return null;
        }
        return classLocation.toString();
    }

    private static String getNamespaceParserName() {
        try {
            return JAXPUtils.getNamespaceXMLReader().getClass().getName();
        } catch (BuildException e) {
            ignoreThrowable(e);
            return null;
        }
    }

    private static String getNamespaceParserLocation() {
        try {
            URL classLocation = getClassLocation(JAXPUtils.getNamespaceXMLReader().getClass());
            if (classLocation != null) {
                return classLocation.toString();
            }
            return null;
        } catch (BuildException e) {
            ignoreThrowable(e);
            return null;
        }
    }

    private static String getXSLTProcessorLocation() {
        URL classLocation;
        Transformer xSLTProcessor = getXSLTProcessor();
        if (xSLTProcessor == null || (classLocation = getClassLocation(xSLTProcessor.getClass())) == null) {
            return null;
        }
        return classLocation.toString();
    }

    public static void doReport(PrintStream printStream) {
        doReport(printStream, 2);
    }

    public static void doReport(PrintStream printStream, int i) {
        printStream.println("------- Ant diagnostics report -------");
        printStream.println(Main.getAntVersion());
        header(printStream, "Implementation Version");
        printStream.println("core tasks     : " + getImplementationVersion(Main.class) + " in " + getClassLocation(Main.class));
        header(printStream, "ANT PROPERTIES");
        doReportAntProperties(printStream);
        header(printStream, "ANT_HOME/lib jar listing");
        doReportAntHomeLibraries(printStream);
        header(printStream, "USER_HOME/.ant/lib jar listing");
        doReportUserHomeLibraries(printStream);
        header(printStream, "Tasks availability");
        doReportTasksAvailability(printStream);
        header(printStream, "org.apache.env.Which diagnostics");
        doReportWhich(printStream);
        header(printStream, "XML Parser information");
        doReportParserInfo(printStream);
        header(printStream, "XSLT Processor information");
        doReportXSLTProcessorInfo(printStream);
        header(printStream, "System properties");
        doReportSystemProperties(printStream);
        header(printStream, "Temp dir");
        doReportTempDir(printStream);
        header(printStream, "Locale information");
        doReportLocale(printStream);
        header(printStream, "Proxy information");
        doReportProxy(printStream);
        printStream.println();
    }

    private static void header(PrintStream printStream, String str) {
        printStream.println();
        printStream.println("-------------------------------------------");
        printStream.print(" ");
        printStream.println(str);
        printStream.println("-------------------------------------------");
    }

    private static void doReportSystemProperties(PrintStream printStream) {
        try {
            Enumeration<?> enumerationPropertyNames = System.getProperties().propertyNames();
            while (enumerationPropertyNames.hasMoreElements()) {
                String str = (String) enumerationPropertyNames.nextElement();
                printStream.println(str + " : " + getProperty(str));
            }
        } catch (SecurityException e) {
            ignoreThrowable(e);
            printStream.println("Access to System.getProperties() blocked by a security manager");
        }
    }

    private static String getProperty(String str) {
        try {
            return System.getProperty(str);
        } catch (SecurityException unused) {
            return ERROR_PROPERTY_ACCESS_BLOCKED;
        }
    }

    private static void doReportAntProperties(PrintStream printStream) {
        Project project = new Project();
        project.initProperties();
        printStream.println("ant.version: " + project.getProperty(MagicNames.ANT_VERSION));
        printStream.println("ant.java.version: " + project.getProperty(MagicNames.ANT_JAVA_VERSION));
        printStream.println("Is this the Apache Harmony VM? " + (JavaEnvUtils.isApacheHarmony() ? "yes" : "no"));
        printStream.println("Is this the Kaffe VM? " + (JavaEnvUtils.isKaffe() ? "yes" : "no"));
        printStream.println("Is this gij/gcj? " + (JavaEnvUtils.isGij() ? "yes" : "no"));
        printStream.println("ant.core.lib: " + project.getProperty(MagicNames.ANT_LIB));
        printStream.println("ant.home: " + project.getProperty(MagicNames.ANT_HOME));
    }

    private static void doReportAntHomeLibraries(PrintStream printStream) {
        printStream.println("ant.home: " + System.getProperty(MagicNames.ANT_HOME));
        printLibraries(listLibraries(), printStream);
    }

    private static void doReportUserHomeLibraries(PrintStream printStream) {
        String property = System.getProperty("user.home");
        printStream.println("user.home: " + property);
        printLibraries(listJarFiles(new File(property, Launcher.USER_LIBDIR)), printStream);
    }

    private static void printLibraries(File[] fileArr, PrintStream printStream) {
        if (fileArr == null) {
            printStream.println("No such directory.");
            return;
        }
        for (int i = 0; i < fileArr.length; i++) {
            printStream.println(fileArr[i].getName() + " (" + fileArr[i].length() + " bytes)");
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0 */
    /* JADX WARN: Type inference failed for: r0v1, types: [java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r0v2, types: [java.lang.reflect.InvocationTargetException] */
    /* JADX WARN: Type inference failed for: r0v3, types: [java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r0v4, types: [java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r0v5 */
    private static void doReportWhich(PrintStream printStream) {
        ?? e = 0;
        e = 0;
        try {
            Class.forName("org.apache.env.Which").getMethod("main", String[].class).invoke(null, new String[0]);
        } catch (ClassNotFoundException unused) {
            printStream.println("Not available.");
            printStream.println("Download it at http://xml.apache.org/commons/");
        } catch (InvocationTargetException e2) {
            e = e2;
            if (e.getTargetException() != null) {
                e = e.getTargetException();
            }
        } catch (Throwable th) {
            e = th;
        }
        if (e != 0) {
            printStream.println("Error while running org.apache.env.Which");
            e.printStackTrace();
        }
    }

    private static void doReportTasksAvailability(PrintStream printStream) {
        InputStream resourceAsStream = Main.class.getResourceAsStream(MagicNames.TASKDEF_PROPERTIES_RESOURCE);
        if (resourceAsStream == null) {
            printStream.println("None available");
            return;
        }
        Properties properties = new Properties();
        try {
            properties.load(resourceAsStream);
            Enumeration enumerationKeys = properties.keys();
            while (enumerationKeys.hasMoreElements()) {
                String str = (String) enumerationKeys.nextElement();
                try {
                    try {
                        Class.forName(properties.getProperty(str));
                        properties.remove(str);
                    } catch (NoClassDefFoundError e) {
                        printStream.println(str + " : Missing dependency " + e.getMessage().replace('/', '.'));
                    }
                } catch (ClassNotFoundException unused) {
                    printStream.println(str + " : Not Available (the implementation class is not present)");
                } catch (LinkageError unused2) {
                    printStream.println(str + " : Initialization error");
                }
            }
            if (properties.size() == 0) {
                printStream.println("All defined tasks are available");
            } else {
                printStream.println("A task being missing/unavailable should only matter if you are trying to use it");
            }
        } catch (IOException e2) {
            printStream.println(e2.getMessage());
        }
    }

    private static void doReportParserInfo(PrintStream printStream) {
        printParserInfo(printStream, "XML Parser", getXMLParserName(), getXMLParserLocation());
        printParserInfo(printStream, "Namespace-aware parser", getNamespaceParserName(), getNamespaceParserLocation());
    }

    private static void doReportXSLTProcessorInfo(PrintStream printStream) {
        printParserInfo(printStream, "XSLT Processor", getXSLTProcessorName(), getXSLTProcessorLocation());
    }

    private static void printParserInfo(PrintStream printStream, String str, String str2, String str3) {
        if (str2 == null) {
            str2 = "unknown";
        }
        if (str3 == null) {
            str3 = "unknown";
        }
        printStream.println(str + " : " + str2);
        printStream.println(str + " Location: " + str3);
    }

    /* JADX WARN: Not initialized variable reg: 5, insn: 0x0137: MOVE (r1 I:??[OBJECT, ARRAY]) = (r5 I:??[OBJECT, ARRAY]), block:B:65:0x0136 */
    /* JADX WARN: Removed duplicated region for block: B:80:0x018e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static void doReportTempDir(java.io.PrintStream r12) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 408
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.Diagnostics.doReportTempDir(java.io.PrintStream):void");
    }

    private static void doReportLocale(PrintStream printStream) {
        Calendar calendar = Calendar.getInstance();
        TimeZone timeZone = calendar.getTimeZone();
        printStream.println("Timezone " + timeZone.getDisplayName() + " offset=" + timeZone.getOffset(calendar.get(0), calendar.get(1), calendar.get(2), calendar.get(5), calendar.get(7), (((((calendar.get(11) * 60) + calendar.get(12)) * 60) + calendar.get(13)) * 1000) + calendar.get(14)));
    }

    private static void printProperty(PrintStream printStream, String str) {
        String property = getProperty(str);
        if (property != null) {
            printStream.print(str);
            printStream.print(" = ");
            printStream.print('\"');
            printStream.print(property);
            printStream.println('\"');
        }
    }

    private static void doReportProxy(PrintStream printStream) {
        printProperty(printStream, ProxySetup.HTTP_PROXY_HOST);
        printProperty(printStream, ProxySetup.HTTP_PROXY_PORT);
        printProperty(printStream, ProxySetup.HTTP_PROXY_USERNAME);
        printProperty(printStream, ProxySetup.HTTP_PROXY_PASSWORD);
        printProperty(printStream, ProxySetup.HTTP_NON_PROXY_HOSTS);
        printProperty(printStream, ProxySetup.HTTPS_PROXY_HOST);
        printProperty(printStream, ProxySetup.HTTPS_PROXY_PORT);
        printProperty(printStream, ProxySetup.HTTPS_NON_PROXY_HOSTS);
        printProperty(printStream, ProxySetup.FTP_PROXY_HOST);
        printProperty(printStream, ProxySetup.FTP_PROXY_PORT);
        printProperty(printStream, ProxySetup.FTP_NON_PROXY_HOSTS);
        printProperty(printStream, ProxySetup.SOCKS_PROXY_HOST);
        printProperty(printStream, ProxySetup.SOCKS_PROXY_PORT);
        printProperty(printStream, ProxySetup.SOCKS_PROXY_USERNAME);
        printProperty(printStream, ProxySetup.SOCKS_PROXY_PASSWORD);
        if (JavaEnvUtils.getJavaVersionNumber() < 15) {
            return;
        }
        printProperty(printStream, ProxySetup.USE_SYSTEM_PROXIES);
        try {
            Object objNewInstance = Class.forName("org.apache.tools.ant.util.java15.ProxyDiagnostics").newInstance();
            printStream.println("Java1.5+ proxy settings:");
            printStream.println(objNewInstance.toString());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoClassDefFoundError unused) {
        }
    }
}
