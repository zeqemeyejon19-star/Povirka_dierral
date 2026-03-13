package org.apache.poi.ss.formula.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.poi.ss.formula.ptg.Ptg;

/* JADX INFO: loaded from: classes.dex */
final class FunctionMetadataReader {
    private static final String[] DIGIT_ENDING_FUNCTION_NAMES;
    private static final Set<String> DIGIT_ENDING_FUNCTION_NAMES_SET;
    private static final String ELLIPSIS = "...";
    private static final String METADATA_FILE_NAME = "functionMetadata.txt";
    private static final Pattern TAB_DELIM_PATTERN = Pattern.compile("\t");
    private static final Pattern SPACE_DELIM_PATTERN = Pattern.compile(" ");
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    FunctionMetadataReader() {
    }

    static {
        String[] strArr = {"LOG10", "ATAN2", "DAYS360", "SUMXMY2", "SUMX2MY2", "SUMX2PY2"};
        DIGIT_ENDING_FUNCTION_NAMES = strArr;
        DIGIT_ENDING_FUNCTION_NAMES_SET = new HashSet(Arrays.asList(strArr));
    }

    public static FunctionMetadataRegistry createRegistry() {
        try {
            InputStream is = FunctionMetadataReader.class.getResourceAsStream(METADATA_FILE_NAME);
            if (is == null) {
                throw new RuntimeException("resource 'functionMetadata.txt' not found");
            }
            try {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    try {
                        FunctionDataBuilder fdb = new FunctionDataBuilder(400);
                        while (true) {
                            String line = br.readLine();
                            if (line != null) {
                                if (line.length() >= 1 && line.charAt(0) != '#') {
                                    String trimLine = line.trim();
                                    if (trimLine.length() >= 1) {
                                        processLine(fdb, line);
                                    }
                                }
                            } else {
                                FunctionMetadataRegistry functionMetadataRegistryBuild = fdb.build();
                                is.close();
                                return functionMetadataRegistryBuild;
                            }
                        }
                    } finally {
                        br.close();
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            } catch (Throwable th) {
                is.close();
                throw th;
            }
        } catch (IOException e2) {
            throw new RuntimeException(e2);
        }
    }

    private static void processLine(FunctionDataBuilder fdb, String line) {
        String[] parts = TAB_DELIM_PATTERN.split(line, -2);
        if (parts.length != 8) {
            throw new RuntimeException("Bad line format '" + line + "' - expected 8 data fields");
        }
        int functionIndex = parseInt(parts[0]);
        String functionName = parts[1];
        int minParams = parseInt(parts[2]);
        int maxParams = parseInt(parts[3]);
        byte returnClassCode = parseReturnTypeCode(parts[4]);
        byte[] parameterClassCodes = parseOperandTypeCodes(parts[5]);
        boolean hasNote = parts[7].length() > 0;
        validateFunctionName(functionName);
        fdb.add(functionIndex, functionName, minParams, maxParams, returnClassCode, parameterClassCodes, hasNote);
    }

    private static byte parseReturnTypeCode(String code) {
        if (code.length() == 0) {
            return (byte) 0;
        }
        return parseOperandTypeCode(code);
    }

    private static byte[] parseOperandTypeCodes(String codes) {
        if (codes.length() < 1) {
            return EMPTY_BYTE_ARRAY;
        }
        if (isDash(codes)) {
            return EMPTY_BYTE_ARRAY;
        }
        String[] array = SPACE_DELIM_PATTERN.split(codes);
        int nItems = array.length;
        if (ELLIPSIS.equals(array[nItems - 1])) {
            nItems--;
        }
        byte[] result = new byte[nItems];
        for (int i = 0; i < nItems; i++) {
            result[i] = parseOperandTypeCode(array[i]);
        }
        return result;
    }

    private static boolean isDash(String codes) {
        return codes.length() == 1 && codes.charAt(0) == '-';
    }

    private static byte parseOperandTypeCode(String code) {
        if (code.length() != 1) {
            throw new RuntimeException("Bad operand type code format '" + code + "' expected single char");
        }
        char cCharAt = code.charAt(0);
        if (cCharAt == 'A') {
            return Ptg.CLASS_ARRAY;
        }
        if (cCharAt == 'R') {
            return (byte) 0;
        }
        if (cCharAt == 'V') {
            return (byte) 32;
        }
        throw new IllegalArgumentException("Unexpected operand type code '" + code + "' (" + ((int) code.charAt(0)) + ")");
    }

    private static void validateFunctionName(String functionName) {
        int len = functionName.length();
        int ix = len - 1;
        if (!Character.isDigit(functionName.charAt(ix))) {
            return;
        }
        while (ix >= 0 && Character.isDigit(functionName.charAt(ix))) {
            ix--;
        }
        if (DIGIT_ENDING_FUNCTION_NAMES_SET.contains(functionName)) {
        } else {
            throw new RuntimeException("Invalid function name '" + functionName + "' (is footnote number incorrectly appended)");
        }
    }

    private static int parseInt(String valStr) {
        try {
            return Integer.parseInt(valStr);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Value '" + valStr + "' could not be parsed as an integer");
        }
    }
}
