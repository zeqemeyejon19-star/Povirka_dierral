package org.apache.poi.hssf.usermodel.helpers;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.helpers.RowShifter;
import org.apache.poi.util.Internal;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public final class HSSFRowShifter extends RowShifter {
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) HSSFRowShifter.class);

    public HSSFRowShifter(HSSFSheet sh) {
        super(sh);
    }

    @Override // org.apache.poi.ss.usermodel.helpers.RowShifter
    @NotImplemented
    public void updateNamedRanges(FormulaShifter shifter) {
        throw new NotImplementedException("HSSFRowShifter.updateNamedRanges");
    }

    @Override // org.apache.poi.ss.usermodel.helpers.RowShifter
    @NotImplemented
    public void updateFormulas(FormulaShifter shifter) {
        throw new NotImplementedException("updateFormulas");
    }

    @Override // org.apache.poi.ss.usermodel.helpers.RowShifter
    @Internal
    @NotImplemented
    public void updateRowFormulas(Row row, FormulaShifter shifter) {
        throw new NotImplementedException("updateRowFormulas");
    }

    @Override // org.apache.poi.ss.usermodel.helpers.RowShifter
    @NotImplemented
    public void updateConditionalFormatting(FormulaShifter shifter) {
        throw new NotImplementedException("updateConditionalFormatting");
    }

    @Override // org.apache.poi.ss.usermodel.helpers.RowShifter
    @NotImplemented
    public void updateHyperlinks(FormulaShifter shifter) {
        throw new NotImplementedException("updateHyperlinks");
    }
}
