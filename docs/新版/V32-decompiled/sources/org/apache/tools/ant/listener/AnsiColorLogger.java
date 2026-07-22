package org.apache.tools.ant.listener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;
import org.apache.tools.ant.DefaultLogger;

/* JADX INFO: loaded from: classes3.dex */
public class AnsiColorLogger extends DefaultLogger {
    private static final int ATTR_DIM = 2;
    private static final String END_COLOR = "\u001b[m";
    private static final int FG_BLUE = 34;
    private static final int FG_CYAN = 36;
    private static final int FG_GREEN = 32;
    private static final int FG_MAGENTA = 35;
    private static final int FG_RED = 31;
    private static final String PREFIX = "\u001b[";
    private static final char SEPARATOR = ';';
    private static final String SUFFIX = "m";
    private String errColor = "\u001b[2;31m";
    private String warnColor = "\u001b[2;35m";
    private String infoColor = "\u001b[2;36m";
    private String verboseColor = "\u001b[2;32m";
    private String debugColor = "\u001b[2;34m";
    private boolean colorsSet = false;

    private void setColors() {
        String property = System.getProperty("ant.logger.defaults");
        InputStream resourceAsStream = null;
        try {
            Properties properties = new Properties();
            if (property != null) {
                resourceAsStream = new FileInputStream(property);
            } else {
                resourceAsStream = getClass().getResourceAsStream("/org/apache/tools/ant/listener/defaults.properties");
            }
            if (resourceAsStream != null) {
                properties.load(resourceAsStream);
            }
            String property2 = properties.getProperty("AnsiColorLogger.ERROR_COLOR");
            String property3 = properties.getProperty("AnsiColorLogger.WARNING_COLOR");
            String property4 = properties.getProperty("AnsiColorLogger.INFO_COLOR");
            String property5 = properties.getProperty("AnsiColorLogger.VERBOSE_COLOR");
            String property6 = properties.getProperty("AnsiColorLogger.DEBUG_COLOR");
            if (property2 != null) {
                this.errColor = PREFIX + property2 + SUFFIX;
            }
            if (property3 != null) {
                this.warnColor = PREFIX + property3 + SUFFIX;
            }
            if (property4 != null) {
                this.infoColor = PREFIX + property4 + SUFFIX;
            }
            if (property5 != null) {
                this.verboseColor = PREFIX + property5 + SUFFIX;
            }
            if (property6 != null) {
                this.debugColor = PREFIX + property6 + SUFFIX;
            }
            if (resourceAsStream == null) {
                return;
            }
        } catch (IOException unused) {
            if (resourceAsStream == null) {
                return;
            }
        } catch (Throwable th) {
            if (resourceAsStream != null) {
                try {
                    resourceAsStream.close();
                } catch (IOException unused2) {
                }
            }
            throw th;
        }
        try {
            resourceAsStream.close();
        } catch (IOException unused3) {
        }
    }

    @Override // org.apache.tools.ant.DefaultLogger
    protected void printMessage(String str, PrintStream printStream, int i) {
        if (str == null || printStream == null) {
            return;
        }
        if (!this.colorsSet) {
            setColors();
            this.colorsSet = true;
        }
        StringBuffer stringBuffer = new StringBuffer(str);
        if (i == 0) {
            stringBuffer.insert(0, this.errColor);
            stringBuffer.append(END_COLOR);
        } else if (i == 1) {
            stringBuffer.insert(0, this.warnColor);
            stringBuffer.append(END_COLOR);
        } else if (i == 2) {
            stringBuffer.insert(0, this.infoColor);
            stringBuffer.append(END_COLOR);
        } else if (i == 3) {
            stringBuffer.insert(0, this.verboseColor);
            stringBuffer.append(END_COLOR);
        } else {
            stringBuffer.insert(0, this.debugColor);
            stringBuffer.append(END_COLOR);
        }
        printStream.println(stringBuffer.toString());
    }
}
