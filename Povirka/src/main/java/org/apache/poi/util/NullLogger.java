package org.apache.poi.util;

/* JADX INFO: loaded from: classes.dex */
@Internal
public class NullLogger extends POILogger {
    @Override // org.apache.poi.util.POILogger
    public void initialize(String cat) {
    }

    @Override // org.apache.poi.util.POILogger
    protected void _log(int level, Object obj1) {
    }

    @Override // org.apache.poi.util.POILogger
    protected void _log(int level, Object obj1, Throwable exception) {
    }

    @Override // org.apache.poi.util.POILogger
    public void log(int level, Object... objs) {
    }

    @Override // org.apache.poi.util.POILogger
    public boolean check(int level) {
        return false;
    }
}
