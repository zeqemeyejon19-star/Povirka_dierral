package org.apache.poi.ss.usermodel;

/* JADX INFO: loaded from: classes.dex */
public interface DifferentialStyleProvider {
    BorderFormatting getBorderFormatting();

    FontFormatting getFontFormatting();

    ExcelNumberFormat getNumberFormat();

    PatternFormatting getPatternFormatting();

    int getStripeSize();
}
