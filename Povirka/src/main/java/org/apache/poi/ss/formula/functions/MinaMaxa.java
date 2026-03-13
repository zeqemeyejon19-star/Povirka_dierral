package org.apache.poi.ss.formula.functions;

/* JADX INFO: loaded from: classes.dex */
public abstract class MinaMaxa extends MultiOperandNumericFunction {
    public static final Function MAXA = new MinaMaxa() { // from class: org.apache.poi.ss.formula.functions.MinaMaxa.1
        @Override // org.apache.poi.ss.formula.functions.MultiOperandNumericFunction
        protected double evaluate(double[] values) {
            if (values.length > 0) {
                return MathX.max(values);
            }
            return 0.0d;
        }
    };
    public static final Function MINA = new MinaMaxa() { // from class: org.apache.poi.ss.formula.functions.MinaMaxa.2
        @Override // org.apache.poi.ss.formula.functions.MultiOperandNumericFunction
        protected double evaluate(double[] values) {
            if (values.length > 0) {
                return MathX.min(values);
            }
            return 0.0d;
        }
    };

    protected MinaMaxa() {
        super(true, true);
    }
}
