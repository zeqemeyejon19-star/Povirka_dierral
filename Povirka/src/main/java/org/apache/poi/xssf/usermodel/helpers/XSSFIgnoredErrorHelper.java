package org.apache.poi.xssf.usermodel.helpers;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.poi.ss.usermodel.IgnoredErrorType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIgnoredError;

/* JADX INFO: loaded from: classes.dex */
public class XSSFIgnoredErrorHelper {

    /* JADX INFO: renamed from: org.apache.poi.xssf.usermodel.helpers.XSSFIgnoredErrorHelper$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$usermodel$IgnoredErrorType;

        static {
            int[] iArr = new int[IgnoredErrorType.values().length];
            $SwitchMap$org$apache$poi$ss$usermodel$IgnoredErrorType = iArr;
            try {
                iArr[IgnoredErrorType.CALCULATED_COLUMN.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$IgnoredErrorType[IgnoredErrorType.EMPTY_CELL_REFERENCE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$IgnoredErrorType[IgnoredErrorType.EVALUATION_ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$IgnoredErrorType[IgnoredErrorType.FORMULA.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$IgnoredErrorType[IgnoredErrorType.FORMULA_RANGE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$IgnoredErrorType[IgnoredErrorType.LIST_DATA_VALIDATION.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$IgnoredErrorType[IgnoredErrorType.NUMBER_STORED_AS_TEXT.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$IgnoredErrorType[IgnoredErrorType.TWO_DIGIT_TEXT_YEAR.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$IgnoredErrorType[IgnoredErrorType.UNLOCKED_FORMULA.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
        }
    }

    public static boolean isSet(IgnoredErrorType errorType, CTIgnoredError error) {
        switch (AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$IgnoredErrorType[errorType.ordinal()]) {
            case 1:
                return error.isSetCalculatedColumn();
            case 2:
                return error.isSetEmptyCellReference();
            case 3:
                return error.isSetEvalError();
            case 4:
                return error.isSetFormula();
            case 5:
                return error.isSetFormulaRange();
            case 6:
                return error.isSetListDataValidation();
            case 7:
                return error.isSetNumberStoredAsText();
            case 8:
                return error.isSetTwoDigitTextYear();
            case 9:
                return error.isSetUnlockedFormula();
            default:
                throw new IllegalStateException();
        }
    }

    public static void set(IgnoredErrorType errorType, CTIgnoredError error) {
        switch (AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$IgnoredErrorType[errorType.ordinal()]) {
            case 1:
                error.setCalculatedColumn(true);
                return;
            case 2:
                error.setEmptyCellReference(true);
                return;
            case 3:
                error.setEvalError(true);
                return;
            case 4:
                error.setFormula(true);
                return;
            case 5:
                error.setFormulaRange(true);
                return;
            case 6:
                error.setListDataValidation(true);
                return;
            case 7:
                error.setNumberStoredAsText(true);
                return;
            case 8:
                error.setTwoDigitTextYear(true);
                return;
            case 9:
                error.setUnlockedFormula(true);
                return;
            default:
                throw new IllegalStateException();
        }
    }

    public static void addIgnoredErrors(CTIgnoredError err, String ref, IgnoredErrorType... ignoredErrorTypes) {
        err.setSqref(Arrays.asList(ref));
        for (IgnoredErrorType errType : ignoredErrorTypes) {
            set(errType, err);
        }
    }

    public static Set<IgnoredErrorType> getErrorTypes(CTIgnoredError err) {
        Set<IgnoredErrorType> result = new LinkedHashSet<>();
        IgnoredErrorType[] arr$ = IgnoredErrorType.values();
        for (IgnoredErrorType errType : arr$) {
            if (isSet(errType, err)) {
                result.add(errType);
            }
        }
        return result;
    }
}
