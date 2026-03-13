package org.apache.poi.ss.formula;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.constant.ErrorConstant;
import org.apache.poi.ss.formula.function.FunctionMetadata;
import org.apache.poi.ss.formula.function.FunctionMetadataRegistry;
import org.apache.poi.ss.formula.ptg.AbstractFunctionPtg;
import org.apache.poi.ss.formula.ptg.AddPtg;
import org.apache.poi.ss.formula.ptg.Area3DPxg;
import org.apache.poi.ss.formula.ptg.AreaPtg;
import org.apache.poi.ss.formula.ptg.ArrayPtg;
import org.apache.poi.ss.formula.ptg.AttrPtg;
import org.apache.poi.ss.formula.ptg.BoolPtg;
import org.apache.poi.ss.formula.ptg.ConcatPtg;
import org.apache.poi.ss.formula.ptg.DividePtg;
import org.apache.poi.ss.formula.ptg.EqualPtg;
import org.apache.poi.ss.formula.ptg.ErrPtg;
import org.apache.poi.ss.formula.ptg.FuncPtg;
import org.apache.poi.ss.formula.ptg.FuncVarPtg;
import org.apache.poi.ss.formula.ptg.GreaterEqualPtg;
import org.apache.poi.ss.formula.ptg.GreaterThanPtg;
import org.apache.poi.ss.formula.ptg.IntPtg;
import org.apache.poi.ss.formula.ptg.IntersectionPtg;
import org.apache.poi.ss.formula.ptg.LessEqualPtg;
import org.apache.poi.ss.formula.ptg.LessThanPtg;
import org.apache.poi.ss.formula.ptg.MemAreaPtg;
import org.apache.poi.ss.formula.ptg.MemFuncPtg;
import org.apache.poi.ss.formula.ptg.MissingArgPtg;
import org.apache.poi.ss.formula.ptg.MultiplyPtg;
import org.apache.poi.ss.formula.ptg.NamePtg;
import org.apache.poi.ss.formula.ptg.NameXPtg;
import org.apache.poi.ss.formula.ptg.NameXPxg;
import org.apache.poi.ss.formula.ptg.NotEqualPtg;
import org.apache.poi.ss.formula.ptg.NumberPtg;
import org.apache.poi.ss.formula.ptg.OperandPtg;
import org.apache.poi.ss.formula.ptg.OperationPtg;
import org.apache.poi.ss.formula.ptg.ParenthesisPtg;
import org.apache.poi.ss.formula.ptg.PercentPtg;
import org.apache.poi.ss.formula.ptg.PowerPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RangePtg;
import org.apache.poi.ss.formula.ptg.RefPtg;
import org.apache.poi.ss.formula.ptg.StringPtg;
import org.apache.poi.ss.formula.ptg.SubtractPtg;
import org.apache.poi.ss.formula.ptg.UnaryMinusPtg;
import org.apache.poi.ss.formula.ptg.UnaryPlusPtg;
import org.apache.poi.ss.formula.ptg.UnionPtg;
import org.apache.poi.ss.formula.ptg.ValueOperatorPtg;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.Internal;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
@Internal
public final class FormulaParser {
    private static final char CR = '\r';
    private static final char LF = '\n';
    private static final char TAB = '\t';
    private static final String specAll = "All";
    private static final String specData = "Data";
    private static final String specHeaders = "Headers";
    private static final String specThisRow = "This Row";
    private static final String specTotals = "Totals";
    private final FormulaParsingWorkbook _book;
    private final int _formulaLength;
    private final String _formulaString;
    private boolean _inIntersection;
    private int _pointer = 0;
    private ParseNode _rootNode;
    private final int _rowIndex;
    private final int _sheetIndex;
    private final SpreadsheetVersion _ssVersion;
    private int look;
    private static final POILogger log = POILogFactory.getLogger((Class<?>) FormulaParser.class);
    private static final Pattern CELL_REF_PATTERN = Pattern.compile("(\\$?[A-Za-z]+)?(\\$?[0-9]+)?");

    private FormulaParser(String formula, FormulaParsingWorkbook book, int sheetIndex, int rowIndex) {
        this._formulaString = formula;
        this._book = book;
        this._ssVersion = book == null ? SpreadsheetVersion.EXCEL97 : book.getSpreadsheetVersion();
        this._formulaLength = formula.length();
        this._sheetIndex = sheetIndex;
        this._rowIndex = rowIndex;
    }

    public static Ptg[] parse(String formula, FormulaParsingWorkbook workbook, FormulaType formulaType, int sheetIndex, int rowIndex) {
        FormulaParser fp = new FormulaParser(formula, workbook, sheetIndex, rowIndex);
        fp.parse();
        return fp.getRPNPtg(formulaType);
    }

    public static Ptg[] parse(String formula, FormulaParsingWorkbook workbook, FormulaType formulaType, int sheetIndex) {
        return parse(formula, workbook, formulaType, sheetIndex, -1);
    }

    public static Area3DPxg parseStructuredReference(String tableText, FormulaParsingWorkbook workbook, int rowIndex) {
        Ptg[] arr = parse(tableText, workbook, FormulaType.CELL, -1, rowIndex);
        if (arr.length != 1 || !(arr[0] instanceof Area3DPxg)) {
            throw new IllegalStateException("Illegal structured reference");
        }
        return (Area3DPxg) arr[0];
    }

    private void GetChar() {
        if (IsWhite(this.look)) {
            if (this.look == 32) {
                this._inIntersection = true;
            }
        } else {
            this._inIntersection = false;
        }
        int i = this._pointer;
        int i2 = this._formulaLength;
        if (i > i2) {
            throw new RuntimeException("too far");
        }
        if (i < i2) {
            this.look = this._formulaString.codePointAt(i);
        } else {
            this.look = 0;
            this._inIntersection = false;
        }
        this._pointer += Character.charCount(this.look);
    }

    private void resetPointer(int ptr) {
        this._pointer = ptr;
        if (ptr <= this._formulaLength) {
            this.look = this._formulaString.codePointAt(ptr - Character.charCount(this.look));
        } else {
            this.look = 0;
        }
    }

    private RuntimeException expected(String s) {
        String msg;
        if (this.look == 61 && this._formulaString.substring(0, this._pointer - 1).trim().length() < 1) {
            msg = "The specified formula '" + this._formulaString + "' starts with an equals sign which is not allowed.";
        } else {
            msg = new StringBuilder("Parse error near char ").append(this._pointer - 1).append(" '").appendCodePoint(this.look).append("'").append(" in specified formula '").append(this._formulaString).append("'. Expected ").append(s).toString();
        }
        return new FormulaParseException(msg);
    }

    private static boolean IsAlpha(int c) {
        return Character.isLetter(c) || c == 36 || c == 95;
    }

    private static boolean IsDigit(int c) {
        return Character.isDigit(c);
    }

    private static boolean IsWhite(int c) {
        return c == 32 || c == 9 || c == 13 || c == 10;
    }

    private void SkipWhite() {
        while (IsWhite(this.look)) {
            GetChar();
        }
    }

    private void Match(int x) {
        if (this.look != x) {
            throw expected(new StringBuilder().append("'").appendCodePoint(x).append("'").toString());
        }
        GetChar();
    }

    private String GetNum() {
        StringBuilder value = new StringBuilder();
        while (IsDigit(this.look)) {
            value.appendCodePoint(this.look);
            GetChar();
        }
        if (value.length() == 0) {
            return null;
        }
        return value.toString();
    }

    private ParseNode parseRangeExpression() {
        ParseNode result = parseRangeable();
        boolean hasRange = false;
        while (this.look == 58) {
            int pos = this._pointer;
            GetChar();
            ParseNode nextPart = parseRangeable();
            checkValidRangeOperand("LHS", pos, result);
            checkValidRangeOperand("RHS", pos, nextPart);
            ParseNode[] children = {result, nextPart};
            result = new ParseNode(RangePtg.instance, children);
            hasRange = true;
        }
        if (hasRange) {
            return augmentWithMemPtg(result);
        }
        return result;
    }

    private static ParseNode augmentWithMemPtg(ParseNode root) {
        Ptg memPtg;
        if (needsMemFunc(root)) {
            memPtg = new MemFuncPtg(root.getEncodedSize());
        } else {
            memPtg = new MemAreaPtg(root.getEncodedSize());
        }
        return new ParseNode(memPtg, root);
    }

    private static boolean needsMemFunc(ParseNode root) {
        Ptg token = root.getToken();
        if ((token instanceof AbstractFunctionPtg) || (token instanceof ExternSheetReferenceToken) || (token instanceof NamePtg) || (token instanceof NameXPtg)) {
            return true;
        }
        if (!(token instanceof OperationPtg) && !(token instanceof ParenthesisPtg)) {
            return !(token instanceof OperandPtg) && (token instanceof OperationPtg);
        }
        ParseNode[] arr$ = root.getChildren();
        for (ParseNode child : arr$) {
            if (needsMemFunc(child)) {
                return true;
            }
        }
        return false;
    }

    private static void checkValidRangeOperand(String sideName, int currentParsePosition, ParseNode pn) {
        if (!isValidRangeOperand(pn)) {
            throw new FormulaParseException("The " + sideName + " of the range operator ':' at position " + currentParsePosition + " is not a proper reference.");
        }
    }

    private static boolean isValidRangeOperand(ParseNode a) {
        Ptg tkn = a.getToken();
        if (tkn instanceof OperandPtg) {
            return true;
        }
        if (tkn instanceof AbstractFunctionPtg) {
            AbstractFunctionPtg afp = (AbstractFunctionPtg) tkn;
            byte returnClass = afp.getDefaultOperandClass();
            return returnClass == 0;
        }
        if (tkn instanceof ValueOperatorPtg) {
            return false;
        }
        if (tkn instanceof OperationPtg) {
            return true;
        }
        if (tkn instanceof ParenthesisPtg) {
            return isValidRangeOperand(a.getChildren()[0]);
        }
        return tkn == ErrPtg.REF_INVALID;
    }

    private ParseNode parseRangeable() {
        int i;
        SkipWhite();
        int savePointer = this._pointer;
        SheetIdentifier sheetIden = parseSheetName();
        if (sheetIden == null) {
            resetPointer(savePointer);
        } else {
            SkipWhite();
            savePointer = this._pointer;
        }
        SimpleRangePart part1 = parseSimpleRangePart();
        if (part1 == null) {
            if (sheetIden != null) {
                if (this.look == 35) {
                    return new ParseNode(ErrPtg.valueOf(parseErrorLiteral()));
                }
                String name = parseAsName();
                if (name.length() == 0) {
                    throw new FormulaParseException("Cell reference or Named Range expected after sheet name at index " + this._pointer + ".");
                }
                Ptg nameXPtg = this._book.getNameXPtg(name, sheetIden);
                if (nameXPtg == null) {
                    throw new FormulaParseException("Specified name '" + name + "' for sheet " + sheetIden.asFormulaString() + " not found");
                }
                return new ParseNode(nameXPtg);
            }
            return parseNonRange(savePointer);
        }
        boolean whiteAfterPart1 = IsWhite(this.look);
        if (whiteAfterPart1) {
            SkipWhite();
        }
        int i2 = this.look;
        if (i2 == 58) {
            int colonPos = this._pointer;
            GetChar();
            SkipWhite();
            SimpleRangePart part2 = parseSimpleRangePart();
            if (part2 != null && !part1.isCompatibleForArea(part2)) {
                part2 = null;
            }
            if (part2 == null) {
                resetPointer(colonPos);
                if (!part1.isCell()) {
                    String prefix = "";
                    if (sheetIden != null) {
                        prefix = "'" + sheetIden.getSheetIdentifier().getName() + '!';
                    }
                    throw new FormulaParseException(prefix + part1.getRep() + "' is not a proper reference.");
                }
            }
            return createAreaRefParseNode(sheetIden, part1, part2);
        }
        if (i2 == 46) {
            GetChar();
            int dotCount = 1;
            while (true) {
                i = this.look;
                if (i != 46) {
                    break;
                }
                dotCount++;
                GetChar();
            }
            boolean whiteBeforePart2 = IsWhite(i);
            SkipWhite();
            SimpleRangePart part22 = parseSimpleRangePart();
            String part1And2 = this._formulaString.substring(savePointer - 1, this._pointer - 1);
            if (part22 == null) {
                if (sheetIden != null) {
                    throw new FormulaParseException("Complete area reference expected after sheet name at index " + this._pointer + ".");
                }
                return parseNonRange(savePointer);
            }
            if (whiteAfterPart1 || whiteBeforePart2) {
                if (part1.isRowOrColumn() || part22.isRowOrColumn()) {
                    throw new FormulaParseException("Dotted range (full row or column) expression '" + part1And2 + "' must not contain whitespace.");
                }
                return createAreaRefParseNode(sheetIden, part1, part22);
            }
            if (dotCount == 1 && part1.isRow() && part22.isRow()) {
                return parseNonRange(savePointer);
            }
            if ((part1.isRowOrColumn() || part22.isRowOrColumn()) && dotCount != 2) {
                throw new FormulaParseException("Dotted range (full row or column) expression '" + part1And2 + "' must have exactly 2 dots.");
            }
            return createAreaRefParseNode(sheetIden, part1, part22);
        }
        if (part1.isCell() && isValidCellReference(part1.getRep())) {
            return createAreaRefParseNode(sheetIden, part1, null);
        }
        if (sheetIden != null) {
            throw new FormulaParseException("Second part of cell reference expected after sheet name at index " + this._pointer + ".");
        }
        return parseNonRange(savePointer);
    }

    /* JADX WARN: Removed duplicated region for block: B:96:0x01fb  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.apache.poi.ss.formula.ParseNode parseStructuredReference(java.lang.String r32) {
        /*
            Method dump skipped, instruction units count: 953
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.ss.formula.FormulaParser.parseStructuredReference(java.lang.String):org.apache.poi.ss.formula.ParseNode");
    }

    private String parseAsColumnQuantifier() {
        if (this.look != 91) {
            return null;
        }
        GetChar();
        int i = this.look;
        if (i == 35) {
            return null;
        }
        if (i == 64) {
            GetChar();
        }
        StringBuilder name = new StringBuilder();
        while (true) {
            int i2 = this.look;
            if (i2 != 93) {
                name.appendCodePoint(i2);
                GetChar();
            } else {
                Match(93);
                return name.toString();
            }
        }
    }

    private String parseAsSpecialQuantifier() {
        if (this.look != 91) {
            return null;
        }
        GetChar();
        if (this.look != 35) {
            return null;
        }
        GetChar();
        String name = parseAsName();
        if (name.equals("This")) {
            name = name + ' ' + parseAsName();
        }
        Match(93);
        return name;
    }

    private ParseNode parseNonRange(int savePointer) {
        resetPointer(savePointer);
        if (Character.isDigit(this.look)) {
            return new ParseNode(parseNumber());
        }
        if (this.look == 34) {
            return new ParseNode(new StringPtg(parseStringLiteral()));
        }
        String name = parseAsName();
        int i = this.look;
        if (i == 40) {
            return function(name);
        }
        if (i == 91) {
            return parseStructuredReference(name);
        }
        if (name.equalsIgnoreCase("TRUE") || name.equalsIgnoreCase("FALSE")) {
            return new ParseNode(BoolPtg.valueOf(name.equalsIgnoreCase("TRUE")));
        }
        FormulaParsingWorkbook formulaParsingWorkbook = this._book;
        if (formulaParsingWorkbook == null) {
            throw new IllegalStateException("Need book to evaluate name '" + name + "'");
        }
        EvaluationName evalName = formulaParsingWorkbook.getName(name, this._sheetIndex);
        if (evalName == null) {
            throw new FormulaParseException("Specified named range '" + name + "' does not exist in the current workbook.");
        }
        if (evalName.isRange()) {
            return new ParseNode(evalName.createPtg());
        }
        throw new FormulaParseException("Specified name '" + name + "' is not a range as expected.");
    }

    private String parseAsName() {
        int i;
        StringBuilder sb = new StringBuilder();
        if (!Character.isLetter(this.look) && (i = this.look) != 95 && i != 92) {
            throw expected("number, string, defined name, or data table");
        }
        while (isValidDefinedNameChar(this.look)) {
            sb.appendCodePoint(this.look);
            GetChar();
        }
        SkipWhite();
        return sb.toString();
    }

    private static boolean isValidDefinedNameChar(int ch) {
        return Character.isLetterOrDigit(ch) || ch > 128 || ch == 46 || ch == 63 || ch == 92 || ch == 95;
    }

    private ParseNode createAreaRefParseNode(SheetIdentifier sheetIden, SimpleRangePart part1, SimpleRangePart part2) throws FormulaParseException {
        Ptg ptg;
        if (part2 == null) {
            CellReference cr = part1.getCellReference();
            if (sheetIden == null) {
                ptg = new RefPtg(cr);
            } else {
                ptg = this._book.get3DReferencePtg(cr, sheetIden);
            }
        } else {
            AreaReference areaRef = createAreaRef(part1, part2);
            if (sheetIden == null) {
                ptg = new AreaPtg(areaRef);
            } else {
                ptg = this._book.get3DReferencePtg(areaRef, sheetIden);
            }
        }
        return new ParseNode(ptg);
    }

    private AreaReference createAreaRef(SimpleRangePart part1, SimpleRangePart part2) {
        if (!part1.isCompatibleForArea(part2)) {
            throw new FormulaParseException("has incompatible parts: '" + part1.getRep() + "' and '" + part2.getRep() + "'.");
        }
        if (part1.isRow()) {
            return AreaReference.getWholeRow(this._ssVersion, part1.getRep(), part2.getRep());
        }
        if (part1.isColumn()) {
            return AreaReference.getWholeColumn(this._ssVersion, part1.getRep(), part2.getRep());
        }
        return new AreaReference(part1.getCellReference(), part2.getCellReference(), this._ssVersion);
    }

    private SimpleRangePart parseSimpleRangePart() {
        int ptr = this._pointer - 1;
        boolean hasDigits = false;
        boolean hasLetters = false;
        while (ptr < this._formulaLength) {
            char ch = this._formulaString.charAt(ptr);
            if (Character.isDigit(ch)) {
                hasDigits = true;
            } else if (Character.isLetter(ch)) {
                hasLetters = true;
            } else if (ch != '$' && ch != '_') {
                break;
            }
            ptr++;
        }
        int i = this._pointer;
        if (ptr <= i - 1) {
            return null;
        }
        String rep = this._formulaString.substring(i - 1, ptr);
        if (!CELL_REF_PATTERN.matcher(rep).matches()) {
            return null;
        }
        if (hasLetters && hasDigits) {
            if (!isValidCellReference(rep)) {
                return null;
            }
        } else if (hasLetters) {
            if (!CellReference.isColumnWithinRange(rep.replace("$", ""), this._ssVersion)) {
                return null;
            }
        } else {
            if (!hasDigits) {
                return null;
            }
            try {
                int i2 = Integer.parseInt(rep.replace("$", ""));
                if (i2 < 1 || i2 > this._ssVersion.getMaxRows()) {
                    return null;
                }
            } catch (NumberFormatException e) {
                return null;
            }
        }
        resetPointer(ptr + 1);
        return new SimpleRangePart(rep, hasLetters, hasDigits);
    }

    private static final class SimpleRangePart {
        private final String _rep;
        private final Type _type;

        private enum Type {
            CELL,
            ROW,
            COLUMN;

            public static Type get(boolean hasLetters, boolean hasDigits) {
                if (hasLetters) {
                    return hasDigits ? CELL : COLUMN;
                }
                if (!hasDigits) {
                    throw new IllegalArgumentException("must have either letters or numbers");
                }
                return ROW;
            }
        }

        public SimpleRangePart(String rep, boolean hasLetters, boolean hasNumbers) {
            this._rep = rep;
            this._type = Type.get(hasLetters, hasNumbers);
        }

        public boolean isCell() {
            return this._type == Type.CELL;
        }

        public boolean isRowOrColumn() {
            return this._type != Type.CELL;
        }

        public CellReference getCellReference() {
            if (this._type != Type.CELL) {
                throw new IllegalStateException("Not applicable to this type");
            }
            return new CellReference(this._rep);
        }

        public boolean isColumn() {
            return this._type == Type.COLUMN;
        }

        public boolean isRow() {
            return this._type == Type.ROW;
        }

        public String getRep() {
            return this._rep;
        }

        public boolean isCompatibleForArea(SimpleRangePart part2) {
            return this._type == part2._type;
        }

        public String toString() {
            return getClass().getName() + " [" + this._rep + "]";
        }
    }

    private String getBookName() {
        StringBuilder sb = new StringBuilder();
        GetChar();
        while (true) {
            int i = this.look;
            if (i != 93) {
                sb.appendCodePoint(i);
                GetChar();
            } else {
                GetChar();
                return sb.toString();
            }
        }
    }

    private SheetIdentifier parseSheetName() {
        String bookName;
        if (this.look == 91) {
            bookName = getBookName();
        } else {
            bookName = null;
        }
        int i = this.look;
        if (i == 39) {
            Match(39);
            if (this.look == 91) {
                bookName = getBookName();
            }
            StringBuilder sb = new StringBuilder();
            boolean done = this.look == 39;
            while (!done) {
                sb.appendCodePoint(this.look);
                GetChar();
                if (this.look == 39) {
                    Match(39);
                    done = this.look != 39;
                }
            }
            NameIdentifier iden = new NameIdentifier(sb.toString(), true);
            SkipWhite();
            int i2 = this.look;
            if (i2 == 33) {
                GetChar();
                return new SheetIdentifier(bookName, iden);
            }
            if (i2 == 58) {
                return parseSheetRange(bookName, iden);
            }
            return null;
        }
        if (i == 95 || Character.isLetter(i)) {
            StringBuilder sb2 = new StringBuilder();
            while (isUnquotedSheetNameChar(this.look)) {
                sb2.appendCodePoint(this.look);
                GetChar();
            }
            NameIdentifier iden2 = new NameIdentifier(sb2.toString(), false);
            SkipWhite();
            int i3 = this.look;
            if (i3 == 33) {
                GetChar();
                return new SheetIdentifier(bookName, iden2);
            }
            if (i3 == 58) {
                return parseSheetRange(bookName, iden2);
            }
            return null;
        }
        if (this.look != 33 || bookName == null) {
            return null;
        }
        GetChar();
        return new SheetIdentifier(bookName, null);
    }

    private SheetIdentifier parseSheetRange(String bookname, NameIdentifier sheet1Name) {
        GetChar();
        SheetIdentifier sheet2 = parseSheetName();
        if (sheet2 != null) {
            return new SheetRangeIdentifier(bookname, sheet1Name, sheet2.getSheetIdentifier());
        }
        return null;
    }

    private static boolean isUnquotedSheetNameChar(int ch) {
        return Character.isLetterOrDigit(ch) || ch > 128 || ch == 46 || ch == 95;
    }

    private boolean isValidCellReference(String str) {
        boolean result = CellReference.classifyCellReference(str, this._ssVersion) == CellReference.NameType.CELL;
        if (result) {
            boolean isFunc = FunctionMetadataRegistry.getFunctionByName(str.toUpperCase(Locale.ROOT)) != null;
            if (isFunc) {
                int savePointer = this._pointer;
                resetPointer(this._pointer + str.length());
                SkipWhite();
                boolean result2 = this.look != 40;
                resetPointer(savePointer);
                return result2;
            }
            return result;
        }
        return result;
    }

    private ParseNode function(String name) {
        Ptg nameToken = null;
        if (!AbstractFunctionPtg.isBuiltInFunctionName(name)) {
            FormulaParsingWorkbook formulaParsingWorkbook = this._book;
            if (formulaParsingWorkbook == null) {
                throw new IllegalStateException("Need book to evaluate name '" + name + "'");
            }
            EvaluationName hName = formulaParsingWorkbook.getName(name, this._sheetIndex);
            if (hName != null) {
                if (!hName.isFunctionName()) {
                    throw new FormulaParseException("Attempt to use name '" + name + "' as a function, but defined name in workbook does not refer to a function");
                }
                nameToken = hName.createPtg();
            } else {
                nameToken = this._book.getNameXPtg(name, null);
                if (nameToken == null) {
                    POILogger pOILogger = log;
                    if (pOILogger.check(5)) {
                        pOILogger.log(5, "FormulaParser.function: Name '" + name + "' is completely unknown in the current workbook.");
                    }
                    int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$SpreadsheetVersion[this._book.getSpreadsheetVersion().ordinal()];
                    if (i == 1) {
                        addName(name);
                        nameToken = this._book.getName(name, this._sheetIndex).createPtg();
                    } else if (i == 2) {
                        nameToken = new NameXPxg(name);
                    } else {
                        throw new IllegalStateException("Unexpected spreadsheet version: " + this._book.getSpreadsheetVersion().name());
                    }
                }
            }
        }
        Match(40);
        ParseNode[] args = Arguments();
        Match(41);
        return getFunction(name, nameToken, args);
    }

    /* JADX INFO: renamed from: org.apache.poi.ss.formula.FormulaParser$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$SpreadsheetVersion;

        static {
            int[] iArr = new int[SpreadsheetVersion.values().length];
            $SwitchMap$org$apache$poi$ss$SpreadsheetVersion = iArr;
            try {
                iArr[SpreadsheetVersion.EXCEL97.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$SpreadsheetVersion[SpreadsheetVersion.EXCEL2007.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    private void addName(String functionName) {
        Name name = this._book.createName();
        name.setFunction(true);
        name.setNameName(functionName);
        name.setSheetIndex(this._sheetIndex);
    }

    private ParseNode getFunction(String name, Ptg namePtg, ParseNode[] args) {
        AbstractFunctionPtg retval;
        FunctionMetadata fm = FunctionMetadataRegistry.getFunctionByName(name.toUpperCase(Locale.ROOT));
        int numArgs = args.length;
        if (fm == null) {
            if (namePtg == null) {
                throw new IllegalStateException("NamePtg must be supplied for external functions");
            }
            ParseNode[] allArgs = new ParseNode[numArgs + 1];
            allArgs[0] = new ParseNode(namePtg);
            System.arraycopy(args, 0, allArgs, 1, numArgs);
            return new ParseNode(FuncVarPtg.create(name, numArgs + 1), allArgs);
        }
        if (namePtg == null) {
            boolean isVarArgs = !fm.hasFixedArgsLength();
            int funcIx = fm.getIndex();
            if (funcIx == 4 && args.length == 1) {
                return new ParseNode(AttrPtg.getSumSingle(), args);
            }
            validateNumArgs(args.length, fm);
            if (isVarArgs) {
                retval = FuncVarPtg.create(name, numArgs);
            } else {
                retval = FuncPtg.create(funcIx);
            }
            return new ParseNode(retval, args);
        }
        throw new IllegalStateException("NamePtg no applicable to internal functions");
    }

    private void validateNumArgs(int numArgs, FunctionMetadata fm) {
        int maxArgs;
        String msg;
        FormulaParsingWorkbook formulaParsingWorkbook;
        String msg2;
        if (numArgs < fm.getMinParams()) {
            String msg3 = "Too few arguments to function '" + fm.getName() + "'. ";
            if (fm.hasFixedArgsLength()) {
                msg2 = msg3 + "Expected " + fm.getMinParams();
            } else {
                msg2 = msg3 + "At least " + fm.getMinParams() + " were expected";
            }
            throw new FormulaParseException(msg2 + " but got " + numArgs + ".");
        }
        if (fm.hasUnlimitedVarags() && (formulaParsingWorkbook = this._book) != null) {
            maxArgs = formulaParsingWorkbook.getSpreadsheetVersion().getMaxFunctionArgs();
        } else {
            maxArgs = fm.getMaxParams();
        }
        if (numArgs > maxArgs) {
            String msg4 = "Too many arguments to function '" + fm.getName() + "'. ";
            if (fm.hasFixedArgsLength()) {
                msg = msg4 + "Expected " + maxArgs;
            } else {
                msg = msg4 + "At most " + maxArgs + " were expected";
            }
            throw new FormulaParseException(msg + " but got " + numArgs + ".");
        }
    }

    private static boolean isArgumentDelimiter(int ch) {
        return ch == 44 || ch == 41;
    }

    private ParseNode[] Arguments() {
        List<ParseNode> temp = new ArrayList<>(2);
        SkipWhite();
        if (this.look == 41) {
            return ParseNode.EMPTY_ARRAY;
        }
        boolean missedPrevArg = true;
        while (true) {
            SkipWhite();
            if (isArgumentDelimiter(this.look)) {
                if (missedPrevArg) {
                    temp.add(new ParseNode(MissingArgPtg.instance));
                }
                if (this.look != 41) {
                    Match(44);
                    missedPrevArg = true;
                } else {
                    ParseNode[] result = new ParseNode[temp.size()];
                    temp.toArray(result);
                    return result;
                }
            } else {
                temp.add(comparisonExpression());
                missedPrevArg = false;
                SkipWhite();
                if (!isArgumentDelimiter(this.look)) {
                    throw expected("',' or ')'");
                }
            }
        }
    }

    private ParseNode powerFactor() {
        ParseNode result = percentFactor();
        while (true) {
            SkipWhite();
            if (this.look != 94) {
                return result;
            }
            Match(94);
            ParseNode other = percentFactor();
            result = new ParseNode(PowerPtg.instance, result, other);
        }
    }

    private ParseNode percentFactor() {
        ParseNode result = parseSimpleFactor();
        while (true) {
            SkipWhite();
            if (this.look != 37) {
                return result;
            }
            Match(37);
            result = new ParseNode(PercentPtg.instance, result);
        }
    }

    private ParseNode parseSimpleFactor() {
        int i;
        SkipWhite();
        int i2 = this.look;
        if (i2 == 34) {
            return new ParseNode(new StringPtg(parseStringLiteral()));
        }
        if (i2 == 35) {
            return new ParseNode(ErrPtg.valueOf(parseErrorLiteral()));
        }
        if (i2 == 40) {
            Match(40);
            ParseNode inside = unionExpression();
            Match(41);
            return new ParseNode(ParenthesisPtg.instance, inside);
        }
        if (i2 == 43) {
            Match(43);
            return parseUnary(true);
        }
        if (i2 == 45) {
            Match(45);
            return parseUnary(false);
        }
        if (i2 == 123) {
            Match(123);
            ParseNode arrayNode = parseArray();
            Match(125);
            return arrayNode;
        }
        if (IsAlpha(i2) || Character.isDigit(this.look) || (i = this.look) == 39 || i == 91 || i == 95 || i == 92) {
            return parseRangeExpression();
        }
        if (i == 46) {
            return new ParseNode(parseNumber());
        }
        throw expected("cell ref or constant literal");
    }

    private ParseNode parseUnary(boolean isPlus) {
        boolean numberFollows = IsDigit(this.look) || this.look == 46;
        ParseNode factor = powerFactor();
        if (numberFollows) {
            Ptg token = factor.getToken();
            if (token instanceof NumberPtg) {
                if (isPlus) {
                    return factor;
                }
                return new ParseNode(new NumberPtg(-((NumberPtg) token).getValue()));
            }
            if (token instanceof IntPtg) {
                if (isPlus) {
                    return factor;
                }
                int intVal = ((IntPtg) token).getValue();
                return new ParseNode(new NumberPtg(-intVal));
            }
        }
        return new ParseNode(isPlus ? UnaryPlusPtg.instance : UnaryMinusPtg.instance, factor);
    }

    private ParseNode parseArray() {
        List<Object[]> rowsData = new ArrayList<>();
        while (true) {
            Object[] singleRowData = parseArrayRow();
            rowsData.add(singleRowData);
            int i = this.look;
            if (i != 125) {
                if (i != 59) {
                    throw expected("'}' or ';'");
                }
                Match(59);
            } else {
                int nRows = rowsData.size();
                Object[][] values2d = new Object[nRows][];
                rowsData.toArray(values2d);
                int nColumns = values2d[0].length;
                checkRowLengths(values2d, nColumns);
                return new ParseNode(new ArrayPtg(values2d));
            }
        }
    }

    private void checkRowLengths(Object[][] values2d, int nColumns) {
        for (int i = 0; i < values2d.length; i++) {
            int rowLen = values2d[i].length;
            if (rowLen != nColumns) {
                throw new FormulaParseException("Array row " + i + " has length " + rowLen + " but row 0 has length " + nColumns);
            }
        }
    }

    private Object[] parseArrayRow() {
        int i;
        List<Object> temp = new ArrayList<>();
        while (true) {
            temp.add(parseArrayItem());
            SkipWhite();
            i = this.look;
            if (i != 44) {
                break;
            }
            Match(44);
        }
        if (i != 59 && i != 125) {
            throw expected("'}' or ','");
        }
        Object[] result = new Object[temp.size()];
        temp.toArray(result);
        return result;
    }

    private Object parseArrayItem() {
        SkipWhite();
        int i = this.look;
        if (i == 34) {
            return parseStringLiteral();
        }
        if (i == 35) {
            return ErrorConstant.valueOf(parseErrorLiteral());
        }
        if (i == 45) {
            Match(45);
            SkipWhite();
            return convertArrayNumber(parseNumber(), false);
        }
        if (i == 70 || i == 84 || i == 102 || i == 116) {
            return parseBooleanLiteral();
        }
        return convertArrayNumber(parseNumber(), true);
    }

    private Boolean parseBooleanLiteral() {
        String iden = parseUnquotedIdentifier();
        if ("TRUE".equalsIgnoreCase(iden)) {
            return Boolean.TRUE;
        }
        if ("FALSE".equalsIgnoreCase(iden)) {
            return Boolean.FALSE;
        }
        throw expected("'TRUE' or 'FALSE'");
    }

    private static Double convertArrayNumber(Ptg ptg, boolean isPositive) {
        double value;
        if (ptg instanceof IntPtg) {
            value = ((IntPtg) ptg).getValue();
        } else if (ptg instanceof NumberPtg) {
            value = ((NumberPtg) ptg).getValue();
        } else {
            throw new RuntimeException("Unexpected ptg (" + ptg.getClass().getName() + ")");
        }
        if (!isPositive) {
            value = -value;
        }
        return new Double(value);
    }

    private Ptg parseNumber() {
        String number2 = null;
        String exponent = null;
        String number1 = GetNum();
        if (this.look == 46) {
            GetChar();
            number2 = GetNum();
        }
        if (this.look == 69) {
            GetChar();
            String sign = "";
            int i = this.look;
            if (i == 43) {
                GetChar();
            } else if (i == 45) {
                GetChar();
                sign = "-";
            }
            String number = GetNum();
            if (number == null) {
                throw expected("Integer");
            }
            exponent = sign + number;
        }
        if (number1 == null && number2 == null) {
            throw expected("Integer");
        }
        return getNumberPtgFromString(number1, number2, exponent);
    }

    private int parseErrorLiteral() {
        Match(35);
        String part1 = parseUnquotedIdentifier().toUpperCase(Locale.ROOT);
        if (part1 == null) {
            throw expected("remainder of error constant literal");
        }
        char cCharAt = part1.charAt(0);
        if (cCharAt == 'D') {
            FormulaError fe = FormulaError.DIV0;
            if (part1.equals("DIV")) {
                Match(47);
                Match(48);
                Match(33);
                return fe.getCode();
            }
            throw expected(fe.getString());
        }
        if (cCharAt != 'N') {
            if (cCharAt == 'R') {
                FormulaError fe2 = FormulaError.REF;
                if (part1.equals(fe2.name())) {
                    Match(33);
                    return fe2.getCode();
                }
                throw expected(fe2.getString());
            }
            if (cCharAt == 'V') {
                FormulaError fe3 = FormulaError.VALUE;
                if (part1.equals(fe3.name())) {
                    Match(33);
                    return fe3.getCode();
                }
                throw expected(fe3.getString());
            }
            throw expected("#VALUE!, #REF!, #DIV/0!, #NAME?, #NUM!, #NULL! or #N/A");
        }
        FormulaError fe4 = FormulaError.NAME;
        if (part1.equals(fe4.name())) {
            Match(63);
            return fe4.getCode();
        }
        FormulaError fe5 = FormulaError.NUM;
        if (part1.equals(fe5.name())) {
            Match(33);
            return fe5.getCode();
        }
        FormulaError fe6 = FormulaError.NULL;
        if (part1.equals(fe6.name())) {
            Match(33);
            return fe6.getCode();
        }
        FormulaError fe7 = FormulaError.NA;
        if (part1.equals("N")) {
            Match(47);
            int i = this.look;
            if (i != 65 && i != 97) {
                throw expected(fe7.getString());
            }
            Match(i);
            return fe7.getCode();
        }
        throw expected("#NAME?, #NUM!, #NULL! or #N/A");
    }

    private String parseUnquotedIdentifier() {
        if (this.look == 39) {
            throw expected("unquoted identifier");
        }
        StringBuilder sb = new StringBuilder();
        while (true) {
            if (!Character.isLetterOrDigit(this.look) && this.look != 46) {
                break;
            }
            sb.appendCodePoint(this.look);
            GetChar();
        }
        if (sb.length() < 1) {
            return null;
        }
        return sb.toString();
    }

    private static Ptg getNumberPtgFromString(String number1, String number2, String exponent) {
        StringBuilder number = new StringBuilder();
        if (number2 == null) {
            number.append(number1);
            if (exponent != null) {
                number.append('E');
                number.append(exponent);
            }
            String numberStr = number.toString();
            try {
                int intVal = Integer.parseInt(numberStr);
                if (IntPtg.isInRange(intVal)) {
                    return new IntPtg(intVal);
                }
                return new NumberPtg(numberStr);
            } catch (NumberFormatException e) {
                return new NumberPtg(numberStr);
            }
        }
        if (number1 != null) {
            number.append(number1);
        }
        number.append('.');
        number.append(number2);
        if (exponent != null) {
            number.append('E');
            number.append(exponent);
        }
        return new NumberPtg(number.toString());
    }

    private String parseStringLiteral() {
        Match(34);
        StringBuilder token = new StringBuilder();
        while (true) {
            if (this.look == 34) {
                GetChar();
                if (this.look != 34) {
                    return token.toString();
                }
            }
            token.appendCodePoint(this.look);
            GetChar();
        }
    }

    private ParseNode Term() {
        Ptg operator;
        ParseNode result = powerFactor();
        while (true) {
            SkipWhite();
            int i = this.look;
            if (i == 42) {
                Match(42);
                operator = MultiplyPtg.instance;
            } else if (i == 47) {
                Match(47);
                operator = DividePtg.instance;
            } else {
                return result;
            }
            ParseNode other = powerFactor();
            result = new ParseNode(operator, result, other);
        }
    }

    private ParseNode unionExpression() {
        ParseNode result = intersectionExpression();
        boolean hasUnions = false;
        while (true) {
            SkipWhite();
            if (this.look != 44) {
                break;
            }
            GetChar();
            hasUnions = true;
            ParseNode other = intersectionExpression();
            result = new ParseNode(UnionPtg.instance, result, other);
        }
        if (hasUnions) {
            return augmentWithMemPtg(result);
        }
        return result;
    }

    private ParseNode intersectionExpression() {
        ParseNode result = comparisonExpression();
        boolean hasIntersections = false;
        while (true) {
            SkipWhite();
            if (!this._inIntersection) {
                break;
            }
            int savePointer = this._pointer;
            try {
                ParseNode other = comparisonExpression();
                result = new ParseNode(IntersectionPtg.instance, result, other);
                hasIntersections = true;
            } catch (FormulaParseException e) {
                resetPointer(savePointer);
            }
        }
        if (hasIntersections) {
            return augmentWithMemPtg(result);
        }
        return result;
    }

    private ParseNode comparisonExpression() {
        ParseNode result = concatExpression();
        while (true) {
            SkipWhite();
            switch (this.look) {
                case 60:
                case 61:
                case 62:
                    Ptg comparisonToken = getComparisonToken();
                    ParseNode other = concatExpression();
                    result = new ParseNode(comparisonToken, result, other);
                    break;
                default:
                    return result;
            }
        }
    }

    private Ptg getComparisonToken() {
        int i = this.look;
        if (i == 61) {
            Match(i);
            return EqualPtg.instance;
        }
        boolean isGreater = i == 62;
        Match(i);
        if (isGreater) {
            if (this.look == 61) {
                Match(61);
                return GreaterEqualPtg.instance;
            }
            return GreaterThanPtg.instance;
        }
        int i2 = this.look;
        if (i2 != 61) {
            if (i2 == 62) {
                Match(62);
                return NotEqualPtg.instance;
            }
            return LessThanPtg.instance;
        }
        Match(61);
        return LessEqualPtg.instance;
    }

    private ParseNode concatExpression() {
        ParseNode result = additiveExpression();
        while (true) {
            SkipWhite();
            if (this.look == 38) {
                Match(38);
                ParseNode other = additiveExpression();
                result = new ParseNode(ConcatPtg.instance, result, other);
            } else {
                return result;
            }
        }
    }

    private ParseNode additiveExpression() {
        Ptg operator;
        ParseNode result = Term();
        while (true) {
            SkipWhite();
            int i = this.look;
            if (i == 43) {
                Match(43);
                operator = AddPtg.instance;
            } else if (i == 45) {
                Match(45);
                operator = SubtractPtg.instance;
            } else {
                return result;
            }
            ParseNode other = Term();
            result = new ParseNode(operator, result, other);
        }
    }

    private void parse() {
        this._pointer = 0;
        GetChar();
        this._rootNode = unionExpression();
        if (this._pointer <= this._formulaLength) {
            String msg = "Unused input [" + this._formulaString.substring(this._pointer - 1) + "] after attempting to parse the formula [" + this._formulaString + "]";
            throw new FormulaParseException(msg);
        }
    }

    private Ptg[] getRPNPtg(FormulaType formulaType) {
        OperandClassTransformer oct = new OperandClassTransformer(formulaType);
        oct.transformFormula(this._rootNode);
        return ParseNode.toTokenArray(this._rootNode);
    }
}
