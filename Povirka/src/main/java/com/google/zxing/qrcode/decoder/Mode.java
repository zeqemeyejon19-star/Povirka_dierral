package com.google.zxing.qrcode.decoder;

/* JADX INFO: loaded from: classes.dex */
public enum Mode {
    TERMINATOR(new int[]{0, 0, 0}, 0),
    NUMERIC(new int[]{10, 12, 14}, 1),
    ALPHANUMERIC(new int[]{9, 11, 13}, 2),
    STRUCTURED_APPEND(new int[]{0, 0, 0}, 3),
    BYTE(new int[]{8, 16, 16}, 4),
    ECI(new int[]{0, 0, 0}, 7),
    KANJI(new int[]{8, 10, 12}, 8),
    FNC1_FIRST_POSITION(new int[]{0, 0, 0}, 5),
    FNC1_SECOND_POSITION(new int[]{0, 0, 0}, 9),
    HANZI(new int[]{8, 10, 12}, 13);

    private final int bits;
    private final int[] characterCountBitsForVersions;

    Mode(int[] characterCountBitsForVersions, int bits) {
        this.characterCountBitsForVersions = characterCountBitsForVersions;
        this.bits = bits;
    }

    public static Mode forBits(int bits) {
        if (bits == 0) {
            return TERMINATOR;
        }
        if (bits == 1) {
            return NUMERIC;
        }
        if (bits == 2) {
            return ALPHANUMERIC;
        }
        if (bits == 3) {
            return STRUCTURED_APPEND;
        }
        if (bits == 4) {
            return BYTE;
        }
        if (bits == 5) {
            return FNC1_FIRST_POSITION;
        }
        if (bits == 7) {
            return ECI;
        }
        if (bits == 8) {
            return KANJI;
        }
        if (bits == 9) {
            return FNC1_SECOND_POSITION;
        }
        if (bits == 13) {
            return HANZI;
        }
        throw new IllegalArgumentException();
    }

    public int getCharacterCountBits(Version version) {
        int offset;
        int number = version.getVersionNumber();
        if (number > 9) {
            if (number <= 26) {
                offset = 1;
            } else {
                offset = 2;
            }
        } else {
            offset = 0;
        }
        return this.characterCountBitsForVersions[offset];
    }

    public int getBits() {
        return this.bits;
    }
}
