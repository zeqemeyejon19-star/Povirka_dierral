package org.apache.poi.ss.usermodel;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public enum FormulaError {
    _NO_ERROR(-1, "(no error)"),
    NULL(0, "#NULL!"),
    DIV0(7, "#DIV/0!"),
    VALUE(15, "#VALUE!"),
    REF(23, "#REF!"),
    NAME(29, "#NAME?"),
    NUM(36, "#NUM!"),
    NA(42, "#N/A"),
    CIRCULAR_REF(-60, "~CIRCULAR~REF~"),
    FUNCTION_NOT_IMPLEMENTED(-30, "~FUNCTION~NOT~IMPLEMENTED~");

    private final int longType;
    private final String repr;
    private final byte type;
    private static final Map<String, FormulaError> smap = new HashMap();
    private static final Map<Byte, FormulaError> bmap = new HashMap();
    private static final Map<Integer, FormulaError> imap = new HashMap();

    static {
        FormulaError[] arr$ = values();
        for (FormulaError error : arr$) {
            bmap.put(Byte.valueOf(error.getCode()), error);
            imap.put(Integer.valueOf(error.getLongCode()), error);
            smap.put(error.getString(), error);
        }
    }

    FormulaError(int type, String repr) {
        this.type = (byte) type;
        this.longType = type;
        this.repr = repr;
    }

    public byte getCode() {
        return this.type;
    }

    public int getLongCode() {
        return this.longType;
    }

    public String getString() {
        return this.repr;
    }

    public static final boolean isValidCode(int errorCode) {
        FormulaError[] arr$ = values();
        for (FormulaError error : arr$) {
            if (error.getCode() == errorCode || error.getLongCode() == errorCode) {
                return true;
            }
        }
        return false;
    }

    public static FormulaError forInt(byte type) throws IllegalArgumentException {
        FormulaError err = bmap.get(Byte.valueOf(type));
        if (err == null) {
            throw new IllegalArgumentException("Unknown error type: " + ((int) type));
        }
        return err;
    }

    public static FormulaError forInt(int type) throws IllegalArgumentException {
        FormulaError err = imap.get(Integer.valueOf(type));
        if (err == null) {
            err = bmap.get(Byte.valueOf((byte) type));
        }
        if (err == null) {
            throw new IllegalArgumentException("Unknown error type: " + type);
        }
        return err;
    }

    public static FormulaError forString(String code) throws IllegalArgumentException {
        FormulaError err = smap.get(code);
        if (err == null) {
            throw new IllegalArgumentException("Unknown error code: " + code);
        }
        return err;
    }
}
