package org.apache.poi.sl.usermodel;

import com.poverka.httpFileClient.activity.MainActivity;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public enum AutoNumberingScheme {
    alphaLcParenBoth(8, 1),
    alphaUcParenBoth(10, 2),
    alphaLcParenRight(9, 3),
    alphaUcParenRight(11, 4),
    alphaLcPeriod(0, 5),
    alphaUcPeriod(1, 6),
    arabicParenBoth(12, 7),
    arabicParenRight(2, 8),
    arabicPeriod(3, 9),
    arabicPlain(13, 10),
    romanLcParenBoth(4, 11),
    romanUcParenBoth(14, 12),
    romanLcParenRight(5, 13),
    romanUcParenRight(15, 14),
    romanLcPeriod(6, 15),
    romanUcPeriod(7, 16),
    circleNumDbPlain(18, 17),
    circleNumWdBlackPlain(20, 18),
    circleNumWdWhitePlain(19, 19),
    arabicDbPeriod(29, 20),
    arabicDbPlain(28, 21),
    ea1ChsPeriod(17, 22),
    ea1ChsPlain(16, 23),
    ea1ChtPeriod(21, 24),
    ea1ChtPlain(20, 25),
    ea1JpnChsDbPeriod(38, 26),
    ea1JpnKorPlain(26, 27),
    ea1JpnKorPeriod(27, 28),
    arabic1Minus(23, 29),
    arabic2Minus(24, 30),
    hebrew2Minus(25, 31),
    thaiAlphaPeriod(30, 32),
    thaiAlphaParenRight(31, 33),
    thaiAlphaParenBoth(32, 34),
    thaiNumPeriod(33, 35),
    thaiNumParenRight(34, 36),
    thaiNumParenBoth(35, 37),
    hindiAlphaPeriod(36, 38),
    hindiNumPeriod(37, 39),
    hindiNumParenRight(39, 40),
    hindiAlpha1Period(39, 41);

    private static final String ALPHA_LIST = "abcdefghijklmnopqrstuvwxyz";
    private static final String ARABIC_LIST = "0123456789";
    private static final String CIRCLE_DB_LIST = "❶❷❸❹❺❻❼❽❾";
    private static final String WINGDINGS_BLACK_LIST = "\u008b\u008c\u008d\u008e\u008f\u0090\u0091\u0092\u0093\u0094";
    private static final String WINGDINGS_WHITE_LIST = "\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089";
    public final int nativeId;
    public final int ooxmlId;

    AutoNumberingScheme(int nativeId, int ooxmlId) {
        this.nativeId = nativeId;
        this.ooxmlId = ooxmlId;
    }

    public static AutoNumberingScheme forNativeID(int nativeId) {
        AutoNumberingScheme[] arr$ = values();
        for (AutoNumberingScheme ans : arr$) {
            if (ans.nativeId == nativeId) {
                return ans;
            }
        }
        return null;
    }

    public static AutoNumberingScheme forOoxmlID(int ooxmlId) {
        AutoNumberingScheme[] arr$ = values();
        for (AutoNumberingScheme ans : arr$) {
            if (ans.ooxmlId == ooxmlId) {
                return ans;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: org.apache.poi.sl.usermodel.AutoNumberingScheme$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme;

        static {
            int[] iArr = new int[AutoNumberingScheme.values().length];
            $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme = iArr;
            try {
                iArr[AutoNumberingScheme.alphaLcPeriod.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.alphaUcPeriod.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.arabicParenRight.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.arabicPeriod.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.romanLcParenBoth.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.romanLcParenRight.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.romanLcPeriod.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.romanUcPeriod.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.alphaLcParenBoth.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.alphaLcParenRight.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.alphaUcParenBoth.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.alphaUcParenRight.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.arabicParenBoth.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.arabicPlain.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.romanUcParenBoth.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.romanUcParenRight.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.ea1ChsPlain.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.ea1ChsPeriod.ordinal()] = 18;
            } catch (NoSuchFieldError e18) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.circleNumDbPlain.ordinal()] = 19;
            } catch (NoSuchFieldError e19) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.circleNumWdWhitePlain.ordinal()] = 20;
            } catch (NoSuchFieldError e20) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.circleNumWdBlackPlain.ordinal()] = 21;
            } catch (NoSuchFieldError e21) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.ea1ChtPlain.ordinal()] = 22;
            } catch (NoSuchFieldError e22) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.ea1ChtPeriod.ordinal()] = 23;
            } catch (NoSuchFieldError e23) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.arabic1Minus.ordinal()] = 24;
            } catch (NoSuchFieldError e24) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.arabic2Minus.ordinal()] = 25;
            } catch (NoSuchFieldError e25) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.hebrew2Minus.ordinal()] = 26;
            } catch (NoSuchFieldError e26) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.ea1JpnKorPlain.ordinal()] = 27;
            } catch (NoSuchFieldError e27) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.ea1JpnKorPeriod.ordinal()] = 28;
            } catch (NoSuchFieldError e28) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.arabicDbPlain.ordinal()] = 29;
            } catch (NoSuchFieldError e29) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.arabicDbPeriod.ordinal()] = 30;
            } catch (NoSuchFieldError e30) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.thaiAlphaPeriod.ordinal()] = 31;
            } catch (NoSuchFieldError e31) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.thaiAlphaParenRight.ordinal()] = 32;
            } catch (NoSuchFieldError e32) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.thaiAlphaParenBoth.ordinal()] = 33;
            } catch (NoSuchFieldError e33) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.thaiNumPeriod.ordinal()] = 34;
            } catch (NoSuchFieldError e34) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.thaiNumParenRight.ordinal()] = 35;
            } catch (NoSuchFieldError e35) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.thaiNumParenBoth.ordinal()] = 36;
            } catch (NoSuchFieldError e36) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.hindiAlphaPeriod.ordinal()] = 37;
            } catch (NoSuchFieldError e37) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.hindiNumPeriod.ordinal()] = 38;
            } catch (NoSuchFieldError e38) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.ea1JpnChsDbPeriod.ordinal()] = 39;
            } catch (NoSuchFieldError e39) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.hindiNumParenRight.ordinal()] = 40;
            } catch (NoSuchFieldError e40) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[AutoNumberingScheme.hindiAlpha1Period.ordinal()] = 41;
            } catch (NoSuchFieldError e41) {
            }
        }
    }

    public String getDescription() {
        switch (AnonymousClass1.$SwitchMap$org$apache$poi$sl$usermodel$AutoNumberingScheme[ordinal()]) {
        }
        return "Hindi alphabetic character followed by a period.";
    }

    public String format(int value) {
        String index = formatIndex(value);
        String cased = formatCase(index);
        String seperated = formatSeperator(cased);
        return seperated;
    }

    private String formatSeperator(String cased) {
        String name = name().toLowerCase(Locale.ROOT);
        return name.contains("plain") ? cased : name.contains("parenright") ? cased + ")" : name.contains("parenboth") ? "(" + cased + ")" : name.contains("period") ? cased + "." : name.contains("minus") ? cased + "-" : cased;
    }

    private String formatCase(String index) {
        String name = name().toLowerCase(Locale.ROOT);
        return name.contains("lc") ? index.toLowerCase(Locale.ROOT) : name.contains("uc") ? index.toUpperCase(Locale.ROOT) : index;
    }

    private String formatIndex(int value) {
        String name = name().toLowerCase(Locale.ROOT);
        if (name.startsWith("roman")) {
            return formatRomanIndex(value);
        }
        if (name.startsWith("arabic") && !name.contains("db")) {
            return getIndexedList(value, ARABIC_LIST, false);
        }
        if (name.startsWith("alpha")) {
            return getIndexedList(value, ALPHA_LIST, true);
        }
        if (name.contains("WdWhite")) {
            return value == 10 ? "\u008a" : getIndexedList(value, WINGDINGS_WHITE_LIST, false);
        }
        if (name.contains("WdBlack")) {
            return value == 10 ? "\u0095" : getIndexedList(value, WINGDINGS_BLACK_LIST, false);
        }
        if (name.contains("NumDb")) {
            return value == 10 ? "❿" : getIndexedList(value, CIRCLE_DB_LIST, true);
        }
        return "?";
    }

    private static String getIndexedList(int val, String list, boolean oneBased) {
        StringBuilder sb = new StringBuilder();
        addIndexedChar(val, list, oneBased, sb);
        return sb.toString();
    }

    private static void addIndexedChar(int val, String list, boolean oneBased, StringBuilder sb) {
        if (oneBased) {
            val--;
        }
        int len = list.length();
        if (val >= len) {
            addIndexedChar(val / len, list, oneBased, sb);
        }
        sb.append(list.charAt(val % len));
    }

    private String formatRomanIndex(int value) {
        int[] VALUES = {1000, 900, MainActivity.REQUEST_DELAY, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] ROMAN = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        String[][] conciseList = {new String[]{"XLV", "VL"}, new String[]{"XCV", "VC"}, new String[]{"CDL", "LD"}, new String[]{"CML", "LM"}, new String[]{"CMVC", "LMVL"}, new String[]{"CDXC", "LDXL"}, new String[]{"CDVC", "LDVL"}, new String[]{"CMXC", "LMXL"}, new String[]{"XCIX", "VCIV"}, new String[]{"XLIX", "VLIV"}, new String[]{"XLIX", "IL"}, new String[]{"XCIX", "IC"}, new String[]{"CDXC", "XD"}, new String[]{"CDVC", "XDV"}, new String[]{"CDIC", "XDIX"}, new String[]{"LMVL", "XMV"}, new String[]{"CMIC", "XMIX"}, new String[]{"CMXC", "XM"}, new String[]{"XDV", "VD"}, new String[]{"XDIX", "VDIV"}, new String[]{"XMV", "VM"}, new String[]{"XMIX", "VMIV"}, new String[]{"VDIV", "ID"}, new String[]{"VMIV", "IM"}};
        StringBuilder sb = new StringBuilder();
        int value2 = value;
        for (int i = 0; i < 13; i++) {
            while (value2 >= VALUES[i]) {
                value2 -= VALUES[i];
                sb.append(ROMAN[i]);
            }
        }
        String result = sb.toString();
        for (String[] cc : conciseList) {
            result = result.replace(cc[0], cc[1]);
        }
        return result;
    }
}
