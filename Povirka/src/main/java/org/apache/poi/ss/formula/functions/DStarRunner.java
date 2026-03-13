package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.StringValueEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.util.NumberComparer;

/* JADX INFO: loaded from: classes.dex */
public final class DStarRunner implements Function3Arg {
    private final DStarAlgorithmEnum algoType;

    public enum DStarAlgorithmEnum {
        DGET,
        DMIN
    }

    private enum operator {
        largerThan,
        largerEqualThan,
        smallerThan,
        smallerEqualThan,
        equal
    }

    public DStarRunner(DStarAlgorithmEnum algorithm) {
        this.algoType = algorithm;
    }

    @Override // org.apache.poi.ss.formula.functions.Function
    public final ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        if (args.length == 3) {
            return evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2]);
        }
        return ErrorEval.VALUE_INVALID;
    }

    @Override // org.apache.poi.ss.formula.functions.Function3Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval database, ValueEval filterColumn, ValueEval conditionDatabase) {
        IDStarAlgorithm algorithm;
        if (!(database instanceof AreaEval) || !(conditionDatabase instanceof AreaEval)) {
            return ErrorEval.VALUE_INVALID;
        }
        AreaEval db = (AreaEval) database;
        AreaEval cdb = (AreaEval) conditionDatabase;
        try {
            try {
                int fc = getColumnForName(OperandResolver.getSingleValue(filterColumn, srcRowIndex, srcColumnIndex), db);
                if (fc == -1) {
                    return ErrorEval.VALUE_INVALID;
                }
                int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$formula$functions$DStarRunner$DStarAlgorithmEnum[this.algoType.ordinal()];
                if (i == 1) {
                    algorithm = new DGet();
                } else if (i == 2) {
                    algorithm = new DMin();
                } else {
                    throw new IllegalStateException("Unexpected algorithm type " + this.algoType + " encountered.");
                }
                int height = db.getHeight();
                for (int row = 1; row < height; row++) {
                    try {
                        boolean matches = fullfillsConditions(db, row, cdb);
                        if (matches) {
                            ValueEval currentValueEval = resolveReference(db, row, fc);
                            boolean shouldContinue = algorithm.processMatch(currentValueEval);
                            if (!shouldContinue) {
                                break;
                            }
                        }
                    } catch (EvaluationException e) {
                        return ErrorEval.VALUE_INVALID;
                    }
                }
                return algorithm.getResult();
            } catch (EvaluationException e2) {
                return ErrorEval.VALUE_INVALID;
            }
        } catch (EvaluationException e3) {
            return e3.getErrorEval();
        }
    }

    private static int getColumnForName(ValueEval nameValueEval, AreaEval db) throws EvaluationException {
        String name = OperandResolver.coerceValueToString(nameValueEval);
        return getColumnForString(db, name);
    }

    private static int getColumnForString(AreaEval db, String name) throws EvaluationException {
        int width = db.getWidth();
        for (int column = 0; column < width; column++) {
            ValueEval columnNameValueEval = resolveReference(db, 0, column);
            if (!(columnNameValueEval instanceof BlankEval) && !(columnNameValueEval instanceof ErrorEval)) {
                String columnName = OperandResolver.coerceValueToString(columnNameValueEval);
                if (name.equals(columnName)) {
                    int resultColumn = column;
                    return resultColumn;
                }
            }
        }
        return -1;
    }

    private static boolean fullfillsConditions(AreaEval db, int row, AreaEval cdb) throws EvaluationException {
        int height = cdb.getHeight();
        for (int conditionRow = 1; conditionRow < height; conditionRow++) {
            boolean matches = true;
            int width = cdb.getWidth();
            int column = 0;
            while (true) {
                if (column >= width) {
                    break;
                }
                boolean columnCondition = true;
                ValueEval condition = resolveReference(cdb, conditionRow, column);
                if (!(condition instanceof BlankEval)) {
                    ValueEval targetHeader = resolveReference(cdb, 0, column);
                    if (!(targetHeader instanceof StringValueEval)) {
                        throw new EvaluationException(ErrorEval.VALUE_INVALID);
                    }
                    if (getColumnForName(targetHeader, db) == -1) {
                        columnCondition = false;
                    }
                    if (columnCondition) {
                        ValueEval value = resolveReference(db, row, getColumnForName(targetHeader, db));
                        if (!testNormalCondition(value, condition)) {
                            matches = false;
                            break;
                        }
                    } else {
                        if (OperandResolver.coerceValueToString(condition).isEmpty()) {
                            throw new EvaluationException(ErrorEval.VALUE_INVALID);
                        }
                        throw new NotImplementedException("D* function with formula conditions");
                    }
                }
                column++;
            }
            if (matches) {
                return true;
            }
        }
        return false;
    }

    private static boolean testNormalCondition(ValueEval value, ValueEval condition) throws EvaluationException {
        String valueString;
        boolean itsANumber;
        if (condition instanceof StringEval) {
            String conditionString = ((StringEval) condition).getStringValue();
            if (conditionString.startsWith("<")) {
                String number = conditionString.substring(1);
                if (number.startsWith("=")) {
                    return testNumericCondition(value, operator.smallerEqualThan, number.substring(1));
                }
                return testNumericCondition(value, operator.smallerThan, number);
            }
            if (conditionString.startsWith(">")) {
                String number2 = conditionString.substring(1);
                if (number2.startsWith("=")) {
                    return testNumericCondition(value, operator.largerEqualThan, number2.substring(1));
                }
                return testNumericCondition(value, operator.largerThan, number2);
            }
            if (conditionString.startsWith("=")) {
                String stringOrNumber = conditionString.substring(1);
                if (stringOrNumber.isEmpty()) {
                    return value instanceof BlankEval;
                }
                try {
                    Integer.parseInt(stringOrNumber);
                    itsANumber = true;
                } catch (NumberFormatException e) {
                    try {
                        Double.parseDouble(stringOrNumber);
                        itsANumber = true;
                    } catch (NumberFormatException e2) {
                        itsANumber = false;
                    }
                }
                if (itsANumber) {
                    return testNumericCondition(value, operator.equal, stringOrNumber);
                }
                valueString = value instanceof BlankEval ? "" : OperandResolver.coerceValueToString(value);
                return stringOrNumber.equals(valueString);
            }
            if (conditionString.isEmpty()) {
                return value instanceof StringEval;
            }
            valueString = value instanceof BlankEval ? "" : OperandResolver.coerceValueToString(value);
            return valueString.startsWith(conditionString);
        }
        if (!(condition instanceof NumericValueEval)) {
            return (condition instanceof ErrorEval) && (value instanceof ErrorEval) && ((ErrorEval) condition).getErrorCode() == ((ErrorEval) value).getErrorCode();
        }
        double conditionNumber = ((NumericValueEval) condition).getNumberValue();
        Double valueNumber = getNumberFromValueEval(value);
        return valueNumber != null && conditionNumber == valueNumber.doubleValue();
    }

    private static boolean testNumericCondition(ValueEval valueEval, operator op, String condition) throws EvaluationException {
        double conditionValue;
        if (!(valueEval instanceof NumericValueEval)) {
            return false;
        }
        double value = ((NumericValueEval) valueEval).getNumberValue();
        try {
            int intValue = Integer.parseInt(condition);
            conditionValue = intValue;
        } catch (NumberFormatException e) {
            try {
                conditionValue = Double.parseDouble(condition);
            } catch (NumberFormatException e2) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
        }
        int result = NumberComparer.compare(value, conditionValue);
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$formula$functions$DStarRunner$operator[op.ordinal()];
        return i != 1 ? i != 2 ? i != 3 ? i != 4 ? i == 5 && result == 0 : result <= 0 : result < 0 : result >= 0 : result > 0;
    }

    /* JADX INFO: renamed from: org.apache.poi.ss.formula.functions.DStarRunner$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$formula$functions$DStarRunner$DStarAlgorithmEnum;
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$formula$functions$DStarRunner$operator;

        static {
            int[] iArr = new int[operator.values().length];
            $SwitchMap$org$apache$poi$ss$formula$functions$DStarRunner$operator = iArr;
            try {
                iArr[operator.largerThan.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$formula$functions$DStarRunner$operator[operator.largerEqualThan.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$formula$functions$DStarRunner$operator[operator.smallerThan.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$formula$functions$DStarRunner$operator[operator.smallerEqualThan.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$formula$functions$DStarRunner$operator[operator.equal.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            int[] iArr2 = new int[DStarAlgorithmEnum.values().length];
            $SwitchMap$org$apache$poi$ss$formula$functions$DStarRunner$DStarAlgorithmEnum = iArr2;
            try {
                iArr2[DStarAlgorithmEnum.DGET.ordinal()] = 1;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$formula$functions$DStarRunner$DStarAlgorithmEnum[DStarAlgorithmEnum.DMIN.ordinal()] = 2;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    private static Double getNumberFromValueEval(ValueEval value) {
        if (value instanceof NumericValueEval) {
            return Double.valueOf(((NumericValueEval) value).getNumberValue());
        }
        if (!(value instanceof StringValueEval)) {
            return null;
        }
        String stringValue = ((StringValueEval) value).getStringValue();
        try {
            return Double.valueOf(Double.parseDouble(stringValue));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static ValueEval resolveReference(AreaEval db, int dbRow, int dbCol) {
        try {
            return OperandResolver.getSingleValue(db.getValue(dbRow, dbCol), db.getFirstRow() + dbRow, db.getFirstColumn() + dbCol);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
}
