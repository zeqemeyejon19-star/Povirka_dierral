package org.apache.poi.ss.usermodel;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ConditionType {
    public final byte id;
    public final String type;
    private static Map<Integer, ConditionType> lookup = new HashMap();
    public static final ConditionType CELL_VALUE_IS = new ConditionType(1, "cellIs");
    public static final ConditionType FORMULA = new ConditionType(2, "expression");
    public static final ConditionType COLOR_SCALE = new ConditionType(3, "colorScale");
    public static final ConditionType DATA_BAR = new ConditionType(4, "dataBar");
    public static final ConditionType FILTER = new ConditionType(5, null);
    public static final ConditionType ICON_SET = new ConditionType(6, "iconSet");

    public String toString() {
        return ((int) this.id) + " - " + this.type;
    }

    public static ConditionType forId(byte id) {
        return forId((int) id);
    }

    public static ConditionType forId(int id) {
        return lookup.get(Integer.valueOf(id));
    }

    private ConditionType(int id, String type) {
        this.id = (byte) id;
        this.type = type;
        lookup.put(Integer.valueOf(id), this);
    }
}
