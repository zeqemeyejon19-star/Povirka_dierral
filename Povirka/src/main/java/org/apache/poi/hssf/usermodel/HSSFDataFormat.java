package org.apache.poi.hssf.usermodel;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.FormatRecord;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormat;

/* JADX INFO: loaded from: classes.dex */
public final class HSSFDataFormat implements DataFormat {
    private static final String[] _builtinFormats = BuiltinFormats.getAll();
    private final Vector<String> _formats = new Vector<>();
    private boolean _movedBuiltins = false;
    private final InternalWorkbook _workbook;

    HSSFDataFormat(InternalWorkbook workbook) {
        this._workbook = workbook;
        for (FormatRecord r : workbook.getFormats()) {
            ensureFormatsSize(r.getIndexCode());
            this._formats.set(r.getIndexCode(), r.getFormatString());
        }
    }

    public static List<String> getBuiltinFormats() {
        return Arrays.asList(_builtinFormats);
    }

    public static short getBuiltinFormat(String format) {
        return (short) BuiltinFormats.getBuiltinFormat(format);
    }

    @Override // org.apache.poi.ss.usermodel.DataFormat
    public short getFormat(String pFormat) {
        String format;
        if (pFormat.equalsIgnoreCase("TEXT")) {
            format = "@";
        } else {
            format = pFormat;
        }
        if (!this._movedBuiltins) {
            int i = 0;
            while (true) {
                String[] strArr = _builtinFormats;
                if (i >= strArr.length) {
                    break;
                }
                ensureFormatsSize(i);
                if (this._formats.get(i) == null) {
                    this._formats.set(i, strArr[i]);
                }
                i++;
            }
            this._movedBuiltins = true;
        }
        for (int i2 = 0; i2 < this._formats.size(); i2++) {
            if (format.equals(this._formats.get(i2))) {
                return (short) i2;
            }
        }
        short index = this._workbook.getFormat(format, true);
        ensureFormatsSize(index);
        this._formats.set(index, format);
        return index;
    }

    @Override // org.apache.poi.ss.usermodel.DataFormat
    public String getFormat(short index) {
        if (this._movedBuiltins) {
            return this._formats.get(index);
        }
        if (index == -1) {
            return null;
        }
        String fmt = this._formats.size() > index ? this._formats.get(index) : null;
        String[] strArr = _builtinFormats;
        if (strArr.length <= index || strArr[index] == null || fmt != null) {
            return fmt;
        }
        return strArr[index];
    }

    public static String getBuiltinFormat(short index) {
        return BuiltinFormats.getBuiltinFormat(index);
    }

    public static int getNumberOfBuiltinBuiltinFormats() {
        return _builtinFormats.length;
    }

    private void ensureFormatsSize(int index) {
        if (this._formats.size() <= index) {
            this._formats.setSize(index + 1);
        }
    }
}
