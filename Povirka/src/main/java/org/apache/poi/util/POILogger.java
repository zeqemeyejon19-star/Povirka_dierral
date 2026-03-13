package org.apache.poi.util;

/* JADX INFO: loaded from: classes.dex */
@Internal
public abstract class POILogger {
    public static final int DEBUG = 1;
    public static final int ERROR = 7;
    public static final int FATAL = 9;
    public static final int INFO = 3;
    public static final int WARN = 5;
    protected static final String[] LEVEL_STRINGS_SHORT = {"?", "D", "?", "I", "?", "W", "?", "E", "?", "F", "?"};
    protected static final String[] LEVEL_STRINGS = {"?0?", "DEBUG", "?2?", "INFO", "?4?", "WARN", "?6?", "ERROR", "?8?", "FATAL", "?10+?"};

    protected abstract void _log(int i, Object obj);

    protected abstract void _log(int i, Object obj, Throwable th);

    public abstract boolean check(int i);

    public abstract void initialize(String str);

    POILogger() {
    }

    public void log(int level, Object... objs) {
        if (check(level)) {
            StringBuilder sb = new StringBuilder(32);
            Throwable lastEx = null;
            for (int i = 0; i < objs.length; i++) {
                if (i == objs.length - 1 && (objs[i] instanceof Throwable)) {
                    lastEx = (Throwable) objs[i];
                } else {
                    sb.append(objs[i]);
                }
            }
            String msg = sb.toString().replaceAll("[\r\n]+", " ");
            if (lastEx == null) {
                _log(level, msg);
            } else {
                _log(level, msg, lastEx);
            }
        }
    }
}
