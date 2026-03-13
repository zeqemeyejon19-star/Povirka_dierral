package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.util.Removal;
import org.apache.poi.xssf.model.StylesTable;

/* JADX INFO: loaded from: classes.dex */
public class XSSFDataFormat implements DataFormat {
    private final StylesTable stylesSource;

    protected XSSFDataFormat(StylesTable stylesSource) {
        this.stylesSource = stylesSource;
    }

    @Override // org.apache.poi.ss.usermodel.DataFormat
    public short getFormat(String format) {
        int idx = BuiltinFormats.getBuiltinFormat(format);
        if (idx == -1) {
            idx = this.stylesSource.putNumberFormat(format);
        }
        return (short) idx;
    }

    @Override // org.apache.poi.ss.usermodel.DataFormat
    public String getFormat(short index) {
        String fmt = this.stylesSource.getNumberFormatAt(index);
        return fmt == null ? BuiltinFormats.getBuiltinFormat(index) : fmt;
    }

    @Removal(version = "3.18")
    public String getFormat(int index) {
        return getFormat((short) index);
    }

    public void putFormat(short index, String format) {
        this.stylesSource.putNumberFormat(index, format);
    }
}
