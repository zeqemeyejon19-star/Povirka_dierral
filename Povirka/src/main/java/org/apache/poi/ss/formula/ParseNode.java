package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.ptg.ArrayPtg;
import org.apache.poi.ss.formula.ptg.AttrPtg;
import org.apache.poi.ss.formula.ptg.FuncVarPtg;
import org.apache.poi.ss.formula.ptg.MemAreaPtg;
import org.apache.poi.ss.formula.ptg.MemFuncPtg;
import org.apache.poi.ss.formula.ptg.Ptg;

/* JADX INFO: loaded from: classes.dex */
final class ParseNode {
    public static final ParseNode[] EMPTY_ARRAY = new ParseNode[0];
    private final ParseNode[] _children;
    private boolean _isIf;
    private final Ptg _token;
    private final int _tokenCount;

    public ParseNode(Ptg token, ParseNode[] children) {
        if (token == null) {
            throw new IllegalArgumentException("token must not be null");
        }
        this._token = token;
        this._children = (ParseNode[]) children.clone();
        this._isIf = isIf(token);
        int tokenCount = 1;
        for (ParseNode parseNode : children) {
            tokenCount += parseNode.getTokenCount();
        }
        this._tokenCount = this._isIf ? tokenCount + children.length : tokenCount;
    }

    public ParseNode(Ptg token) {
        this(token, EMPTY_ARRAY);
    }

    public ParseNode(Ptg token, ParseNode child0) {
        this(token, new ParseNode[]{child0});
    }

    public ParseNode(Ptg token, ParseNode child0, ParseNode child1) {
        this(token, new ParseNode[]{child0, child1});
    }

    private int getTokenCount() {
        return this._tokenCount;
    }

    public int getEncodedSize() {
        Ptg ptg = this._token;
        int result = ptg instanceof ArrayPtg ? 8 : ptg.getSize();
        int i = 0;
        while (true) {
            ParseNode[] parseNodeArr = this._children;
            if (i < parseNodeArr.length) {
                result += parseNodeArr[i].getEncodedSize();
                i++;
            } else {
                return result;
            }
        }
    }

    public static Ptg[] toTokenArray(ParseNode rootNode) {
        TokenCollector temp = new TokenCollector(rootNode.getTokenCount());
        rootNode.collectPtgs(temp);
        return temp.getResult();
    }

    private void collectPtgs(TokenCollector temp) {
        if (isIf(this._token)) {
            collectIfPtgs(temp);
            return;
        }
        Ptg ptg = this._token;
        boolean isPreFixOperator = (ptg instanceof MemFuncPtg) || (ptg instanceof MemAreaPtg);
        if (isPreFixOperator) {
            temp.add(ptg);
        }
        for (int i = 0; i < getChildren().length; i++) {
            getChildren()[i].collectPtgs(temp);
        }
        if (!isPreFixOperator) {
            temp.add(this._token);
        }
    }

    private void collectIfPtgs(TokenCollector temp) {
        getChildren()[0].collectPtgs(temp);
        int ifAttrIndex = temp.createPlaceholder();
        getChildren()[1].collectPtgs(temp);
        int skipAfterTrueParamIndex = temp.createPlaceholder();
        int trueParamSize = temp.sumTokenSizes(ifAttrIndex + 1, skipAfterTrueParamIndex);
        AttrPtg attrIf = AttrPtg.createIf(trueParamSize + 4);
        if (getChildren().length > 2) {
            getChildren()[2].collectPtgs(temp);
            int skipAfterFalseParamIndex = temp.createPlaceholder();
            int falseParamSize = temp.sumTokenSizes(skipAfterTrueParamIndex + 1, skipAfterFalseParamIndex);
            AttrPtg attrSkipAfterTrue = AttrPtg.createSkip(((falseParamSize + 4) + 4) - 1);
            AttrPtg attrSkipAfterFalse = AttrPtg.createSkip(3);
            temp.setPlaceholder(ifAttrIndex, attrIf);
            temp.setPlaceholder(skipAfterTrueParamIndex, attrSkipAfterTrue);
            temp.setPlaceholder(skipAfterFalseParamIndex, attrSkipAfterFalse);
        } else {
            AttrPtg attrSkipAfterTrue2 = AttrPtg.createSkip(3);
            temp.setPlaceholder(ifAttrIndex, attrIf);
            temp.setPlaceholder(skipAfterTrueParamIndex, attrSkipAfterTrue2);
        }
        temp.add(this._token);
    }

    private static boolean isIf(Ptg token) {
        if (token instanceof FuncVarPtg) {
            FuncVarPtg func = (FuncVarPtg) token;
            if ("IF".equals(func.getName())) {
                return true;
            }
            return false;
        }
        return false;
    }

    public Ptg getToken() {
        return this._token;
    }

    public ParseNode[] getChildren() {
        return this._children;
    }

    private static final class TokenCollector {
        private int _offset = 0;
        private final Ptg[] _ptgs;

        public TokenCollector(int tokenCount) {
            this._ptgs = new Ptg[tokenCount];
        }

        public int sumTokenSizes(int fromIx, int toIx) {
            int result = 0;
            for (int i = fromIx; i < toIx; i++) {
                result += this._ptgs[i].getSize();
            }
            return result;
        }

        public int createPlaceholder() {
            int i = this._offset;
            this._offset = i + 1;
            return i;
        }

        public void add(Ptg token) {
            if (token == null) {
                throw new IllegalArgumentException("token must not be null");
            }
            Ptg[] ptgArr = this._ptgs;
            int i = this._offset;
            ptgArr[i] = token;
            this._offset = i + 1;
        }

        public void setPlaceholder(int index, Ptg token) {
            Ptg[] ptgArr = this._ptgs;
            if (ptgArr[index] != null) {
                throw new IllegalStateException("Invalid placeholder index (" + index + ")");
            }
            ptgArr[index] = token;
        }

        public Ptg[] getResult() {
            return this._ptgs;
        }
    }
}
