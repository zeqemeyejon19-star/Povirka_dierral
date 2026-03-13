package org.apache.poi.xssf;

import org.apache.poi.UnsupportedFileFormatException;

/* JADX INFO: loaded from: classes.dex */
public class XLSBUnsupportedException extends UnsupportedFileFormatException {
    public static final String MESSAGE = ".XLSB Binary Workbooks are not supported";
    private static final long serialVersionUID = 7849681804154571175L;

    public XLSBUnsupportedException() {
        super(MESSAGE);
    }
}
