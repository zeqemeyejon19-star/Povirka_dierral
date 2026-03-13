package org.apache.poi.hssf.usermodel;

import java.util.HashSet;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.record.FontRecord;
import org.apache.poi.hssf.record.common.UnicodeString;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

/* JADX INFO: loaded from: classes.dex */
public class HSSFOptimiser {
    public static void optimiseFonts(HSSFWorkbook workbook) {
        short[] newPos = new short[workbook.getWorkbook().getNumberOfFontRecords() + 1];
        boolean[] zapRecords = new boolean[newPos.length];
        for (int i = 0; i < newPos.length; i++) {
            newPos[i] = (short) i;
            zapRecords[i] = false;
        }
        int i2 = newPos.length;
        FontRecord[] frecs = new FontRecord[i2];
        for (int i3 = 0; i3 < newPos.length; i3++) {
            if (i3 != 4) {
                frecs[i3] = workbook.getWorkbook().getFontRecordAt(i3);
            }
        }
        for (int i4 = 5; i4 < newPos.length; i4++) {
            int earlierDuplicate = -1;
            for (int j = 0; j < i4 && earlierDuplicate == -1; j++) {
                if (j != 4) {
                    FontRecord frCheck = workbook.getWorkbook().getFontRecordAt(j);
                    if (frCheck.sameProperties(frecs[i4])) {
                        earlierDuplicate = j;
                    }
                }
            }
            if (earlierDuplicate != -1) {
                newPos[i4] = (short) earlierDuplicate;
                zapRecords[i4] = true;
            }
        }
        for (int i5 = 5; i5 < newPos.length; i5++) {
            short preDeletePos = newPos[i5];
            short newPosition = preDeletePos;
            for (int j2 = 0; j2 < preDeletePos; j2++) {
                if (zapRecords[j2]) {
                    newPosition = (short) (newPosition - 1);
                }
            }
            newPos[i5] = newPosition;
        }
        for (int i6 = 5; i6 < newPos.length; i6++) {
            if (zapRecords[i6]) {
                workbook.getWorkbook().removeFontRecord(frecs[i6]);
            }
        }
        workbook.resetFontCache();
        for (int i7 = 0; i7 < workbook.getWorkbook().getNumExFormats(); i7++) {
            ExtendedFormatRecord xfr = workbook.getWorkbook().getExFormatAt(i7);
            xfr.setFontIndex(newPos[xfr.getFontIndex()]);
        }
        HashSet<UnicodeString> doneUnicodeStrings = new HashSet<>();
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            HSSFSheet s = workbook.getSheetAt(sheetNum);
            for (Row row : s) {
                for (Cell cell : row) {
                    if (cell.getCellTypeEnum() == CellType.STRING) {
                        HSSFRichTextString rtr = (HSSFRichTextString) cell.getRichStringCellValue();
                        UnicodeString u = rtr.getRawUnicodeString();
                        if (!doneUnicodeStrings.contains(u)) {
                            for (short i8 = 5; i8 < newPos.length; i8 = (short) (i8 + 1)) {
                                if (i8 != newPos[i8]) {
                                    u.swapFontUse(i8, newPos[i8]);
                                }
                            }
                            doneUnicodeStrings.add(u);
                        }
                    }
                }
            }
        }
    }

    public static void optimiseCellStyles(HSSFWorkbook workbook) {
        short[] newPos = new short[workbook.getWorkbook().getNumExFormats()];
        boolean[] isUsed = new boolean[newPos.length];
        boolean[] zapRecords = new boolean[newPos.length];
        for (int i = 0; i < newPos.length; i++) {
            isUsed[i] = false;
            newPos[i] = (short) i;
            zapRecords[i] = false;
        }
        int i2 = newPos.length;
        ExtendedFormatRecord[] xfrs = new ExtendedFormatRecord[i2];
        for (int i3 = 0; i3 < newPos.length; i3++) {
            xfrs[i3] = workbook.getWorkbook().getExFormatAt(i3);
        }
        for (int i4 = 21; i4 < newPos.length; i4++) {
            int earlierDuplicate = -1;
            for (int j = 0; j < i4 && earlierDuplicate == -1; j++) {
                ExtendedFormatRecord xfCheck = workbook.getWorkbook().getExFormatAt(j);
                if (xfCheck.equals(xfrs[i4])) {
                    earlierDuplicate = j;
                }
            }
            if (earlierDuplicate != -1) {
                newPos[i4] = (short) earlierDuplicate;
                zapRecords[i4] = true;
            }
            if (earlierDuplicate != -1) {
                isUsed[earlierDuplicate] = true;
            }
        }
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            HSSFSheet s = workbook.getSheetAt(sheetNum);
            for (Row row : s) {
                for (Cell cellI : row) {
                    short oldXf = ((HSSFCell) cellI).getCellValueRecord().getXFIndex();
                    isUsed[oldXf] = true;
                }
            }
        }
        for (int i5 = 21; i5 < isUsed.length; i5++) {
            if (!isUsed[i5]) {
                zapRecords[i5] = true;
                newPos[i5] = 0;
            }
        }
        for (int i6 = 21; i6 < newPos.length; i6++) {
            short preDeletePos = newPos[i6];
            short newPosition = preDeletePos;
            for (int j2 = 0; j2 < preDeletePos; j2++) {
                if (zapRecords[j2]) {
                    newPosition = (short) (newPosition - 1);
                }
            }
            newPos[i6] = newPosition;
        }
        int max = newPos.length;
        int removed = 0;
        int i7 = 21;
        while (i7 < max) {
            if (zapRecords[i7 + removed]) {
                workbook.getWorkbook().removeExFormatRecord(i7);
                i7--;
                max--;
                removed++;
            }
            i7++;
        }
        for (int sheetNum2 = 0; sheetNum2 < workbook.getNumberOfSheets(); sheetNum2++) {
            HSSFSheet s2 = workbook.getSheetAt(sheetNum2);
            for (Row row2 : s2) {
                for (Cell cellI2 : row2) {
                    HSSFCell cell = (HSSFCell) cellI2;
                    short oldXf2 = cell.getCellValueRecord().getXFIndex();
                    HSSFCellStyle newStyle = workbook.getCellStyleAt((int) newPos[oldXf2]);
                    cell.setCellStyle(newStyle);
                }
            }
        }
    }
}
