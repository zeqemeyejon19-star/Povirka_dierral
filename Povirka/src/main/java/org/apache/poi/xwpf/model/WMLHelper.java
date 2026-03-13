package org.apache.poi.xwpf.model;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;

/* JADX INFO: loaded from: classes.dex */
public final class WMLHelper {
    public static boolean convertSTOnOffToBoolean(STOnOff.Enum value) {
        if (value == STOnOff.TRUE || value == STOnOff.ON || value == STOnOff.X_1) {
            return true;
        }
        return false;
    }

    public static STOnOff.Enum convertBooleanToSTOnOff(boolean value) {
        return value ? STOnOff.TRUE : STOnOff.FALSE;
    }
}
