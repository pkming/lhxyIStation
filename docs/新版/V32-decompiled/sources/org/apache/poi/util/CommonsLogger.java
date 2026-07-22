package org.apache.poi.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: classes3.dex */
public class CommonsLogger extends POILogger {
    private static LogFactory _creator = LogFactory.getFactory();
    private Log log = null;

    @Override // org.apache.poi.util.POILogger
    public void initialize(String str) {
        this.log = _creator.getInstance(str);
    }

    @Override // org.apache.poi.util.POILogger
    public void log(int i, Object obj) {
        if (i == 9) {
            if (this.log.isFatalEnabled()) {
                this.log.fatal(obj);
                return;
            }
            return;
        }
        if (i == 7) {
            if (this.log.isErrorEnabled()) {
                this.log.error(obj);
                return;
            }
            return;
        }
        if (i == 5) {
            if (this.log.isWarnEnabled()) {
                this.log.warn(obj);
            }
        } else if (i == 3) {
            if (this.log.isInfoEnabled()) {
                this.log.info(obj);
            }
        } else if (i == 1) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(obj);
            }
        } else if (this.log.isTraceEnabled()) {
            this.log.trace(obj);
        }
    }

    @Override // org.apache.poi.util.POILogger
    public boolean check(int i) {
        return i == 9 ? this.log.isFatalEnabled() : i == 7 ? this.log.isErrorEnabled() : i == 5 ? this.log.isWarnEnabled() : i == 3 ? this.log.isInfoEnabled() : i == 1 && this.log.isDebugEnabled();
    }
}
