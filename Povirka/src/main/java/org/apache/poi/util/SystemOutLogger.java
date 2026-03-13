package org.apache.poi.util;

/* JADX INFO: loaded from: classes.dex */
public class SystemOutLogger extends POILogger {
    private String _cat;

    @Override // org.apache.poi.util.POILogger
    public void initialize(String cat) {
        this._cat = cat;
    }

    @Override // org.apache.poi.util.POILogger
    protected void _log(int level, Object obj1) {
        _log(level, obj1, null);
    }

    @Override // org.apache.poi.util.POILogger
    protected void _log(int level, Object obj1, Throwable exception) {
        if (check(level)) {
            System.out.println("[" + this._cat + "]" + LEVEL_STRINGS_SHORT[Math.min(LEVEL_STRINGS_SHORT.length - 1, level)] + " " + obj1);
            if (exception != null) {
                exception.printStackTrace(System.out);
            }
        }
    }

    @Override // org.apache.poi.util.POILogger
    public boolean check(int level) {
        int currentLevel;
        try {
            currentLevel = Integer.parseInt(System.getProperty("poi.log.level", "5"));
        } catch (SecurityException e) {
            currentLevel = 1;
        }
        return level >= currentLevel;
    }
}
