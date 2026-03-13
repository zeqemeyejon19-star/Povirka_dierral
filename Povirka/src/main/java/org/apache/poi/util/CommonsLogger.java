package org.apache.poi.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: classes.dex */
public class CommonsLogger extends POILogger {
    private static final LogFactory _creator = LogFactory.getFactory();
    private Log log = null;

    @Override // org.apache.poi.util.POILogger
    public void initialize(String cat) {
        this.log = _creator.getInstance(cat);
    }

    @Override // org.apache.poi.util.POILogger
    protected void _log(int level, Object obj1) {
        if (level == 9) {
            if (this.log.isFatalEnabled()) {
                this.log.fatal(obj1);
                return;
            }
            return;
        }
        if (level == 7) {
            if (this.log.isErrorEnabled()) {
                this.log.error(obj1);
                return;
            }
            return;
        }
        if (level == 5) {
            if (this.log.isWarnEnabled()) {
                this.log.warn(obj1);
            }
        } else if (level == 3) {
            if (this.log.isInfoEnabled()) {
                this.log.info(obj1);
            }
        } else if (level == 1) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(obj1);
            }
        } else if (this.log.isTraceEnabled()) {
            this.log.trace(obj1);
        }
    }

    @Override // org.apache.poi.util.POILogger
    protected void _log(int level, Object obj1, Throwable exception) {
        if (level == 9) {
            if (this.log.isFatalEnabled()) {
                if (obj1 != null) {
                    this.log.fatal(obj1, exception);
                    return;
                } else {
                    this.log.fatal(exception);
                    return;
                }
            }
            return;
        }
        if (level == 7) {
            if (this.log.isErrorEnabled()) {
                if (obj1 != null) {
                    this.log.error(obj1, exception);
                    return;
                } else {
                    this.log.error(exception);
                    return;
                }
            }
            return;
        }
        if (level == 5) {
            if (this.log.isWarnEnabled()) {
                if (obj1 != null) {
                    this.log.warn(obj1, exception);
                    return;
                } else {
                    this.log.warn(exception);
                    return;
                }
            }
            return;
        }
        if (level == 3) {
            if (this.log.isInfoEnabled()) {
                if (obj1 != null) {
                    this.log.info(obj1, exception);
                    return;
                } else {
                    this.log.info(exception);
                    return;
                }
            }
            return;
        }
        if (level == 1) {
            if (this.log.isDebugEnabled()) {
                if (obj1 != null) {
                    this.log.debug(obj1, exception);
                    return;
                } else {
                    this.log.debug(exception);
                    return;
                }
            }
            return;
        }
        if (this.log.isTraceEnabled()) {
            if (obj1 != null) {
                this.log.trace(obj1, exception);
            } else {
                this.log.trace(exception);
            }
        }
    }

    @Override // org.apache.poi.util.POILogger
    public boolean check(int level) {
        if (level == 9) {
            if (this.log.isFatalEnabled()) {
                return true;
            }
            return false;
        }
        if (level == 7) {
            if (this.log.isErrorEnabled()) {
                return true;
            }
            return false;
        }
        if (level == 5) {
            if (this.log.isWarnEnabled()) {
                return true;
            }
            return false;
        }
        if (level == 3) {
            if (this.log.isInfoEnabled()) {
                return true;
            }
            return false;
        }
        if (level == 1 && this.log.isDebugEnabled()) {
            return true;
        }
        return false;
    }
}
