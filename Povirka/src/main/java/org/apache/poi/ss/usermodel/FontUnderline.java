package org.apache.poi.ss.usermodel;

/* JADX INFO: loaded from: classes.dex */
public enum FontUnderline {
    SINGLE(1),
    DOUBLE(2),
    SINGLE_ACCOUNTING(3),
    DOUBLE_ACCOUNTING(4),
    NONE(5);

    private static FontUnderline[] _table = new FontUnderline[6];
    private int value;

    static {
        FontUnderline[] arr$ = values();
        for (FontUnderline c : arr$) {
            _table[c.getValue()] = c;
        }
    }

    FontUnderline(int val) {
        this.value = val;
    }

    public int getValue() {
        return this.value;
    }

    /* JADX INFO: renamed from: org.apache.poi.ss.usermodel.FontUnderline$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$usermodel$FontUnderline;

        static {
            int[] iArr = new int[FontUnderline.values().length];
            $SwitchMap$org$apache$poi$ss$usermodel$FontUnderline = iArr;
            try {
                iArr[FontUnderline.DOUBLE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$FontUnderline[FontUnderline.DOUBLE_ACCOUNTING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$FontUnderline[FontUnderline.SINGLE_ACCOUNTING.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$FontUnderline[FontUnderline.NONE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$FontUnderline[FontUnderline.SINGLE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public byte getByteValue() {
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$FontUnderline[ordinal()];
        if (i == 1) {
            return (byte) 2;
        }
        if (i == 2) {
            return (byte) 34;
        }
        if (i != 3) {
            return i != 4 ? (byte) 1 : (byte) 0;
        }
        return (byte) 33;
    }

    public static FontUnderline valueOf(int value) {
        return _table[value];
    }

    public static FontUnderline valueOf(byte value) {
        if (value == 1) {
            FontUnderline val = SINGLE;
            return val;
        }
        if (value == 2) {
            FontUnderline val2 = DOUBLE;
            return val2;
        }
        if (value == 33) {
            FontUnderline val3 = SINGLE_ACCOUNTING;
            return val3;
        }
        if (value != 34) {
            FontUnderline val4 = NONE;
            return val4;
        }
        FontUnderline val5 = DOUBLE_ACCOUNTING;
        return val5;
    }
}
