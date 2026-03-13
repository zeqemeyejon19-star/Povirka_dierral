package org.apache.poi.ss.formula;

import java.util.Stack;
import org.apache.poi.ss.formula.ptg.AbstractFunctionPtg;
import org.apache.poi.ss.formula.ptg.AttrPtg;
import org.apache.poi.ss.formula.ptg.MemAreaPtg;
import org.apache.poi.ss.formula.ptg.MemErrPtg;
import org.apache.poi.ss.formula.ptg.MemFuncPtg;
import org.apache.poi.ss.formula.ptg.OperationPtg;
import org.apache.poi.ss.formula.ptg.ParenthesisPtg;
import org.apache.poi.ss.formula.ptg.Ptg;

/* JADX INFO: loaded from: classes.dex */
public class FormulaRenderer {
    /* JADX WARN: Multi-variable type inference failed */
    public static String toFormulaString(FormulaRenderingWorkbook book, Ptg[] ptgArr) {
        if (ptgArr == 0 || ptgArr.length == 0) {
            throw new IllegalArgumentException("ptgs must not be null");
        }
        Stack<String> stack = new Stack<>();
        for (AbstractFunctionPtg abstractFunctionPtg : ptgArr) {
            if (!(abstractFunctionPtg instanceof MemAreaPtg) && !(abstractFunctionPtg instanceof MemFuncPtg) && !(abstractFunctionPtg instanceof MemErrPtg)) {
                if (abstractFunctionPtg instanceof ParenthesisPtg) {
                    String contents = stack.pop();
                    stack.push("(" + contents + ")");
                } else if (abstractFunctionPtg instanceof AttrPtg) {
                    AttrPtg attrPtg = (AttrPtg) abstractFunctionPtg;
                    if (!attrPtg.isOptimizedIf() && !attrPtg.isOptimizedChoose() && !attrPtg.isSkip() && !attrPtg.isSpace() && !attrPtg.isSemiVolatile()) {
                        if (attrPtg.isSum()) {
                            String[] operands = getOperands(stack, attrPtg.getNumberOfOperands());
                            stack.push(attrPtg.toFormulaString(operands));
                        } else {
                            throw new RuntimeException("Unexpected tAttr: " + attrPtg);
                        }
                    }
                } else if (abstractFunctionPtg instanceof WorkbookDependentFormula) {
                    WorkbookDependentFormula optg = (WorkbookDependentFormula) abstractFunctionPtg;
                    stack.push(optg.toFormulaString(book));
                } else if (!(abstractFunctionPtg instanceof OperationPtg)) {
                    stack.push(abstractFunctionPtg.toFormulaString());
                } else {
                    AbstractFunctionPtg o = abstractFunctionPtg;
                    String[] operands2 = getOperands(stack, o.getNumberOfOperands());
                    stack.push(o.toFormulaString(operands2));
                }
            }
        }
        if (stack.isEmpty()) {
            throw new IllegalStateException("Stack underflow");
        }
        String result = stack.pop();
        if (!stack.isEmpty()) {
            throw new IllegalStateException("too much stuff left on the stack");
        }
        return result;
    }

    private static String[] getOperands(Stack<String> stack, int nOperands) {
        String[] operands = new String[nOperands];
        for (int j = nOperands - 1; j >= 0; j--) {
            if (stack.isEmpty()) {
                String msg = "Too few arguments supplied to operation. Expected (" + nOperands + ") operands but got (" + ((nOperands - j) - 1) + ")";
                throw new IllegalStateException(msg);
            }
            operands[j] = stack.pop();
        }
        return operands;
    }
}
