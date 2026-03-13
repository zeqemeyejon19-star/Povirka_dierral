package org.apache.poi.ss.formula.functions;

/* JADX INFO: loaded from: classes.dex */
public class BaseNumberUtils {
    public static double convertToDecimal(String value, int base, int maxNumberOfPlaces) throws IllegalArgumentException {
        long digit;
        if (value == null || value.length() == 0) {
            return 0.0d;
        }
        long stringLength = value.length();
        if (stringLength > maxNumberOfPlaces) {
            throw new IllegalArgumentException();
        }
        double decimalValue = 0.0d;
        long signedDigit = 0;
        boolean hasSignedDigit = true;
        char[] characters = value.toCharArray();
        char[] arr$ = characters;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            char character = arr$[i$];
            if ('0' <= character && character <= '9') {
                digit = character - '0';
            } else if ('A' <= character && character <= 'Z') {
                digit = (character - 'A') + 10;
            } else if ('a' <= character && character <= 'z') {
                digit = (character - 'a') + 10;
            } else {
                digit = base;
            }
            char[] characters2 = characters;
            char[] arr$2 = arr$;
            if (digit < base) {
                if (hasSignedDigit) {
                    hasSignedDigit = false;
                    signedDigit = digit;
                }
                decimalValue = (((double) base) * decimalValue) + digit;
                i$++;
                arr$ = arr$2;
                signedDigit = signedDigit;
                characters = characters2;
            } else {
                throw new IllegalArgumentException("character not allowed");
            }
        }
        boolean isNegative = !hasSignedDigit && stringLength == ((long) maxNumberOfPlaces) && signedDigit >= ((long) (base / 2));
        if (isNegative) {
            return getTwoComplement(base, maxNumberOfPlaces, decimalValue) * (-1.0d);
        }
        return decimalValue;
    }

    private static double getTwoComplement(double base, double maxNumberOfPlaces, double decimalValue) {
        return Math.pow(base, maxNumberOfPlaces) - decimalValue;
    }
}
