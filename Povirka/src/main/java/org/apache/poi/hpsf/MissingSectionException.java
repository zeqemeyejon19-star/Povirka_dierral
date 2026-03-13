package org.apache.poi.hpsf;

/* JADX INFO: loaded from: classes.dex */
public class MissingSectionException extends HPSFRuntimeException {
    public MissingSectionException() {
    }

    public MissingSectionException(String msg) {
        super(msg);
    }

    public MissingSectionException(Throwable reason) {
        super(reason);
    }

    public MissingSectionException(String msg, Throwable reason) {
        super(msg, reason);
    }
}
