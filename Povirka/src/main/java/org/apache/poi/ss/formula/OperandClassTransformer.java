package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.ptg.AbstractFunctionPtg;
import org.apache.poi.ss.formula.ptg.AttrPtg;
import org.apache.poi.ss.formula.ptg.ControlPtg;
import org.apache.poi.ss.formula.ptg.FuncVarPtg;
import org.apache.poi.ss.formula.ptg.IntersectionPtg;
import org.apache.poi.ss.formula.ptg.MemAreaPtg;
import org.apache.poi.ss.formula.ptg.MemFuncPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RangePtg;
import org.apache.poi.ss.formula.ptg.UnionPtg;
import org.apache.poi.ss.formula.ptg.ValueOperatorPtg;

/* JADX INFO: loaded from: classes.dex */
final class OperandClassTransformer {
    private final FormulaType _formulaType;

    public OperandClassTransformer(FormulaType formulaType) {
        this._formulaType = formulaType;
    }

    /* JADX INFO: renamed from: org.apache.poi.ss.formula.OperandClassTransformer$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$formula$FormulaType;

        static {
            int[] iArr = new int[FormulaType.values().length];
            $SwitchMap$org$apache$poi$ss$formula$FormulaType = iArr;
            try {
                iArr[FormulaType.CELL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$formula$FormulaType[FormulaType.ARRAY.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$formula$FormulaType[FormulaType.NAMEDRANGE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$formula$FormulaType[FormulaType.DATAVALIDATION_LIST.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public void transformFormula(ParseNode rootNode) {
        byte rootNodeOperandClass;
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$formula$FormulaType[this._formulaType.ordinal()];
        if (i == 1) {
            rootNodeOperandClass = 32;
        } else if (i == 2) {
            rootNodeOperandClass = Ptg.CLASS_ARRAY;
        } else if (i == 3 || i == 4) {
            rootNodeOperandClass = 0;
        } else {
            throw new RuntimeException("Incomplete code - formula type (" + this._formulaType + ") not supported yet");
        }
        transformNode(rootNode, rootNodeOperandClass, false);
    }

    private void transformNode(ParseNode node, byte desiredOperandClass, boolean callerForceArrayFlag) {
        Ptg token = node.getToken();
        ParseNode[] children = node.getChildren();
        boolean isSimpleValueFunc = isSimpleValueFunction(token);
        if (isSimpleValueFunc) {
            boolean localForceArray = desiredOperandClass == 64;
            for (ParseNode parseNode : children) {
                transformNode(parseNode, desiredOperandClass, localForceArray);
            }
            setSimpleValueFuncClass((AbstractFunctionPtg) token, desiredOperandClass, callerForceArrayFlag);
            return;
        }
        boolean localForceArray2 = isSingleArgSum(token);
        if (localForceArray2) {
            token = FuncVarPtg.SUM;
        }
        if ((token instanceof ValueOperatorPtg) || (token instanceof ControlPtg) || (token instanceof MemFuncPtg) || (token instanceof MemAreaPtg) || (token instanceof UnionPtg) || (token instanceof IntersectionPtg)) {
            byte localDesiredOperandClass = desiredOperandClass == 0 ? (byte) 32 : desiredOperandClass;
            for (ParseNode parseNode2 : children) {
                transformNode(parseNode2, localDesiredOperandClass, callerForceArrayFlag);
            }
            return;
        }
        if (token instanceof AbstractFunctionPtg) {
            transformFunctionNode((AbstractFunctionPtg) token, children, desiredOperandClass, callerForceArrayFlag);
            return;
        }
        if (children.length > 0) {
            if (token != RangePtg.instance) {
                throw new IllegalStateException("Node should not have any children");
            }
        } else {
            if (token.isBaseToken()) {
                return;
            }
            token.setClass(transformClass(token.getPtgClass(), desiredOperandClass, callerForceArrayFlag));
        }
    }

    private static boolean isSingleArgSum(Ptg token) {
        if (token instanceof AttrPtg) {
            AttrPtg attrPtg = (AttrPtg) token;
            return attrPtg.isSum();
        }
        return false;
    }

    private static boolean isSimpleValueFunction(Ptg token) {
        if (!(token instanceof AbstractFunctionPtg)) {
            return false;
        }
        AbstractFunctionPtg aptg = (AbstractFunctionPtg) token;
        if (aptg.getDefaultOperandClass() != 32) {
            return false;
        }
        int numberOfOperands = aptg.getNumberOfOperands();
        for (int i = numberOfOperands - 1; i >= 0; i--) {
            if (aptg.getParameterClass(i) != 32) {
                return false;
            }
        }
        return true;
    }

    private byte transformClass(byte currentOperandClass, byte desiredOperandClass, boolean callerForceArrayFlag) {
        if (desiredOperandClass == 0) {
            if (!callerForceArrayFlag) {
                return currentOperandClass;
            }
            return (byte) 0;
        }
        if (desiredOperandClass != 32) {
            if (desiredOperandClass != 64) {
                throw new IllegalStateException("Unexpected operand class (" + ((int) desiredOperandClass) + ")");
            }
        } else if (!callerForceArrayFlag) {
            return (byte) 32;
        }
        return Ptg.CLASS_ARRAY;
    }

    private void transformFunctionNode(AbstractFunctionPtg afp, ParseNode[] children, byte desiredOperandClass, boolean callerForceArrayFlag) {
        boolean localForceArrayFlag;
        byte defaultReturnOperandClass = afp.getDefaultOperandClass();
        if (callerForceArrayFlag) {
            if (defaultReturnOperandClass == 0) {
                if (desiredOperandClass == 0) {
                    afp.setClass((byte) 0);
                } else {
                    afp.setClass(Ptg.CLASS_ARRAY);
                }
                localForceArrayFlag = false;
            } else if (defaultReturnOperandClass == 32) {
                afp.setClass(Ptg.CLASS_ARRAY);
                localForceArrayFlag = true;
            } else if (defaultReturnOperandClass == 64) {
                afp.setClass(Ptg.CLASS_ARRAY);
                localForceArrayFlag = false;
            } else {
                throw new IllegalStateException("Unexpected operand class (" + ((int) defaultReturnOperandClass) + ")");
            }
        } else if (defaultReturnOperandClass == desiredOperandClass) {
            localForceArrayFlag = false;
            afp.setClass(defaultReturnOperandClass);
        } else if (desiredOperandClass == 0) {
            if (defaultReturnOperandClass == 32) {
                afp.setClass((byte) 32);
            } else if (defaultReturnOperandClass == 64) {
                afp.setClass(Ptg.CLASS_ARRAY);
            } else {
                throw new IllegalStateException("Unexpected operand class (" + ((int) defaultReturnOperandClass) + ")");
            }
            localForceArrayFlag = false;
        } else if (desiredOperandClass == 32) {
            afp.setClass((byte) 32);
            localForceArrayFlag = false;
        } else if (desiredOperandClass == 64) {
            if (defaultReturnOperandClass == 0) {
                afp.setClass((byte) 0);
            } else if (defaultReturnOperandClass == 32) {
                afp.setClass(Ptg.CLASS_ARRAY);
            } else {
                throw new IllegalStateException("Unexpected operand class (" + ((int) defaultReturnOperandClass) + ")");
            }
            localForceArrayFlag = defaultReturnOperandClass == 32;
        } else {
            throw new IllegalStateException("Unexpected operand class (" + ((int) desiredOperandClass) + ")");
        }
        for (int i = 0; i < children.length; i++) {
            ParseNode child = children[i];
            byte paramOperandClass = afp.getParameterClass(i);
            transformNode(child, paramOperandClass, localForceArrayFlag);
        }
    }

    private void setSimpleValueFuncClass(AbstractFunctionPtg afp, byte desiredOperandClass, boolean callerForceArrayFlag) {
        if (callerForceArrayFlag || desiredOperandClass == 64) {
            afp.setClass(Ptg.CLASS_ARRAY);
        } else {
            afp.setClass((byte) 32);
        }
    }
}
