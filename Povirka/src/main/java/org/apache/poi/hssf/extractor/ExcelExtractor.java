package org.apache.poi.hssf.extractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Locale;
import org.apache.poi.POIOLE2TextExtractor;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HeaderFooter;
import org.apache.poi.ss.usermodel.Row;

/* JADX INFO: loaded from: classes.dex */
public class ExcelExtractor extends POIOLE2TextExtractor implements org.apache.poi.ss.extractor.ExcelExtractor {
    private final HSSFDataFormatter _formatter;
    private boolean _includeBlankCells;
    private boolean _includeCellComments;
    private boolean _includeHeadersFooters;
    private boolean _includeSheetNames;
    private boolean _shouldEvaluateFormulas;
    private final HSSFWorkbook _wb;

    public ExcelExtractor(HSSFWorkbook wb) {
        super(wb);
        this._includeSheetNames = true;
        this._shouldEvaluateFormulas = true;
        this._includeCellComments = false;
        this._includeBlankCells = false;
        this._includeHeadersFooters = true;
        this._wb = wb;
        this._formatter = new HSSFDataFormatter();
    }

    public ExcelExtractor(POIFSFileSystem fs) throws IOException {
        this(fs.getRoot());
    }

    public ExcelExtractor(DirectoryNode dir) throws IOException {
        this(new HSSFWorkbook(dir, true));
    }

    private static final class CommandParseException extends Exception {
        public CommandParseException(String msg) {
            super(msg);
        }
    }

    private static final class CommandArgs {
        private final boolean _evaluateFormulas;
        private final boolean _headersFooters;
        private final File _inputFile;
        private final boolean _requestHelp;
        private final boolean _showBlankCells;
        private final boolean _showCellComments;
        private final boolean _showSheetNames;

        /* JADX WARN: Code restructure failed: missing block: B:44:0x00f9, code lost:
        
            r13._requestHelp = r2;
            r13._inputFile = r1;
            r13._showSheetNames = r3;
            r13._evaluateFormulas = r4;
            r13._showCellComments = r5;
            r13._showBlankCells = r6;
            r13._headersFooters = r7;
         */
        /* JADX WARN: Code restructure failed: missing block: B:45:0x0107, code lost:
        
            return;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public CommandArgs(java.lang.String[] r14) throws org.apache.poi.hssf.extractor.ExcelExtractor.CommandParseException {
            /*
                Method dump skipped, instruction units count: 264
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.hssf.extractor.ExcelExtractor.CommandArgs.<init>(java.lang.String[]):void");
        }

        private static boolean parseBoolArg(String[] args, int i) throws CommandParseException {
            if (i >= args.length) {
                throw new CommandParseException("Expected value after '" + args[i - 1] + "'");
            }
            String value = args[i].toUpperCase(Locale.ROOT);
            if ("Y".equals(value) || "YES".equals(value) || "ON".equals(value) || "TRUE".equals(value)) {
                return true;
            }
            if ("N".equals(value) || "NO".equals(value) || "OFF".equals(value) || "FALSE".equals(value)) {
                return false;
            }
            throw new CommandParseException("Invalid value '" + args[i] + "' for '" + args[i - 1] + "'. Expected 'Y' or 'N'");
        }

        public boolean isRequestHelp() {
            return this._requestHelp;
        }

        public File getInputFile() {
            return this._inputFile;
        }

        public boolean shouldShowSheetNames() {
            return this._showSheetNames;
        }

        public boolean shouldEvaluateFormulas() {
            return this._evaluateFormulas;
        }

        public boolean shouldShowCellComments() {
            return this._showCellComments;
        }

        public boolean shouldShowBlankCells() {
            return this._showBlankCells;
        }

        public boolean shouldIncludeHeadersFooters() {
            return this._headersFooters;
        }
    }

    private static void printUsageMessage(PrintStream ps) {
        ps.println("Use:");
        ps.println("    " + ExcelExtractor.class.getName() + " [<flag> <value> [<flag> <value> [...]]] [-i <filename.xls>]");
        ps.println("       -i <filename.xls> specifies input file (default is to use stdin)");
        ps.println("       Flags can be set on or off by using the values 'Y' or 'N'.");
        ps.println("       Following are available flags and their default values:");
        ps.println("       --show-sheet-names  Y");
        ps.println("       --evaluate-formulas Y");
        ps.println("       --show-comments     N");
        ps.println("       --show-blanks       Y");
        ps.println("       --headers-footers   Y");
    }

    public static void main(String[] args) throws IOException {
        InputStream is;
        try {
            CommandArgs cmdArgs = new CommandArgs(args);
            if (cmdArgs.isRequestHelp()) {
                printUsageMessage(System.out);
                return;
            }
            if (cmdArgs.getInputFile() == null) {
                is = System.in;
            } else {
                is = new FileInputStream(cmdArgs.getInputFile());
            }
            HSSFWorkbook wb = new HSSFWorkbook(is);
            is.close();
            ExcelExtractor extractor = new ExcelExtractor(wb);
            extractor.setIncludeSheetNames(cmdArgs.shouldShowSheetNames());
            extractor.setFormulasNotResults(true ^ cmdArgs.shouldEvaluateFormulas());
            extractor.setIncludeCellComments(cmdArgs.shouldShowCellComments());
            extractor.setIncludeBlankCells(cmdArgs.shouldShowBlankCells());
            extractor.setIncludeHeadersFooters(cmdArgs.shouldIncludeHeadersFooters());
            System.out.println(extractor.getText());
            extractor.close();
            wb.close();
        } catch (CommandParseException e) {
            System.err.println(e.getMessage());
            printUsageMessage(System.err);
            System.exit(1);
        }
    }

    @Override // org.apache.poi.ss.extractor.ExcelExtractor
    public void setIncludeSheetNames(boolean includeSheetNames) {
        this._includeSheetNames = includeSheetNames;
    }

    @Override // org.apache.poi.ss.extractor.ExcelExtractor
    public void setFormulasNotResults(boolean formulasNotResults) {
        this._shouldEvaluateFormulas = !formulasNotResults;
    }

    @Override // org.apache.poi.ss.extractor.ExcelExtractor
    public void setIncludeCellComments(boolean includeCellComments) {
        this._includeCellComments = includeCellComments;
    }

    public void setIncludeBlankCells(boolean includeBlankCells) {
        this._includeBlankCells = includeBlankCells;
    }

    @Override // org.apache.poi.ss.extractor.ExcelExtractor
    public void setIncludeHeadersFooters(boolean includeHeadersFooters) {
        this._includeHeadersFooters = includeHeadersFooters;
    }

    @Override // org.apache.poi.POITextExtractor
    public String getText() {
        int firstRow;
        int lastRow;
        int lastRow2;
        HSSFRow row;
        String name;
        StringBuffer text = new StringBuffer();
        this._wb.setMissingCellPolicy(Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        HSSFComment hSSFComment = null;
        double nVal = 0.0d;
        HSSFCellStyle hSSFCellStyle = null;
        String commentText = null;
        for (int i = 0; i < this._wb.getNumberOfSheets(); i++) {
            HSSFSheet sheet = this._wb.getSheetAt(i);
            if (sheet != null) {
                if (this._includeSheetNames && (name = this._wb.getSheetName(i)) != null) {
                    text.append(name);
                    text.append("\n");
                }
                if (this._includeHeadersFooters) {
                    text.append(_extractHeaderFooter(sheet.getHeader()));
                }
                int firstRow2 = sheet.getFirstRowNum();
                int lastRow3 = sheet.getLastRowNum();
                int j = firstRow2;
                while (j <= lastRow3) {
                    HSSFRow row2 = sheet.getRow(j);
                    if (row2 == null) {
                        firstRow = firstRow2;
                        lastRow = lastRow3;
                    } else {
                        int firstCell = row2.getFirstCellNum();
                        HSSFComment hSSFComment2 = hSSFComment;
                        int lastCell = row2.getLastCellNum();
                        HSSFCellStyle style = hSSFCellStyle;
                        if (this._includeBlankCells) {
                            firstCell = 0;
                        }
                        int k = firstCell;
                        while (k < lastCell) {
                            int firstRow3 = firstRow2;
                            HSSFCell cell = row2.getCell(k);
                            boolean outputContents = true;
                            if (cell == null) {
                                lastRow2 = lastRow3;
                                outputContents = this._includeBlankCells;
                                row = row2;
                            } else {
                                lastRow2 = lastRow3;
                                int i2 = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cell.getCellTypeEnum().ordinal()];
                                row = row2;
                                if (i2 == 1) {
                                    text.append(cell.getRichStringCellValue().getString());
                                } else if (i2 == 2) {
                                    text.append(this._formatter.formatCellValue(cell));
                                } else if (i2 == 3) {
                                    text.append(cell.getBooleanCellValue());
                                } else if (i2 == 4) {
                                    text.append(ErrorEval.getText(cell.getErrorCellValue()));
                                } else {
                                    if (i2 != 5) {
                                        throw new RuntimeException("Unexpected cell type (" + cell.getCellTypeEnum() + ")");
                                    }
                                    if (this._shouldEvaluateFormulas) {
                                        int i3 = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cell.getCachedFormulaResultTypeEnum().ordinal()];
                                        if (i3 == 1) {
                                            HSSFRichTextString str = cell.getRichStringCellValue();
                                            if (str != null && str.length() > 0) {
                                                text.append(str);
                                            }
                                        } else if (i3 == 2) {
                                            HSSFCellStyle style2 = cell.getCellStyle();
                                            double nVal2 = cell.getNumericCellValue();
                                            short df = style2.getDataFormat();
                                            String dfs = style2.getDataFormatString();
                                            text.append(this._formatter.formatRawCellContents(nVal2, df, dfs));
                                            style = style2;
                                            nVal = nVal2;
                                            commentText = dfs;
                                        } else if (i3 == 3) {
                                            text.append(cell.getBooleanCellValue());
                                            commentText = commentText;
                                        } else {
                                            if (i3 != 4) {
                                                throw new IllegalStateException("Unexpected cell cached formula result type: " + cell.getCachedFormulaResultTypeEnum());
                                            }
                                            text.append(ErrorEval.getText(cell.getErrorCellValue()));
                                            commentText = commentText;
                                            style = style;
                                        }
                                    } else {
                                        text.append(cell.getCellFormula());
                                    }
                                }
                                HSSFComment comment = cell.getCellComment();
                                if (!this._includeCellComments || comment == null) {
                                    commentText = commentText;
                                    nVal = nVal;
                                    hSSFComment2 = comment;
                                } else {
                                    String str2 = commentText;
                                    String commentText2 = comment.getString().getString().replace('\n', ' ');
                                    text.append(" Comment by " + comment.getAuthor() + ": " + commentText2);
                                    style = commentText2;
                                    commentText = str2;
                                    nVal = nVal;
                                    hSSFComment2 = comment;
                                }
                            }
                            if (outputContents && k < lastCell - 1) {
                                text.append("\t");
                            }
                            k++;
                            firstRow2 = firstRow3;
                            lastRow3 = lastRow2;
                            row2 = row;
                        }
                        firstRow = firstRow2;
                        lastRow = lastRow3;
                        text.append("\n");
                        hSSFComment = hSSFComment2;
                        hSSFCellStyle = style;
                    }
                    j++;
                    firstRow2 = firstRow;
                    lastRow3 = lastRow;
                }
                HSSFComment hSSFComment3 = hSSFComment;
                HSSFCellStyle hSSFCellStyle2 = hSSFCellStyle;
                if (this._includeHeadersFooters) {
                    text.append(_extractHeaderFooter(sheet.getFooter()));
                }
                hSSFComment = hSSFComment3;
                hSSFCellStyle = hSSFCellStyle2;
            }
        }
        return text.toString();
    }

    /* JADX INFO: renamed from: org.apache.poi.hssf.extractor.ExcelExtractor$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$usermodel$CellType;

        static {
            int[] iArr = new int[CellType.values().length];
            $SwitchMap$org$apache$poi$ss$usermodel$CellType = iArr;
            try {
                iArr[CellType.STRING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.NUMERIC.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.BOOLEAN.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.ERROR.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.FORMULA.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public static String _extractHeaderFooter(HeaderFooter hf) {
        StringBuffer text = new StringBuffer();
        if (hf.getLeft() != null) {
            text.append(hf.getLeft());
        }
        if (hf.getCenter() != null) {
            if (text.length() > 0) {
                text.append("\t");
            }
            text.append(hf.getCenter());
        }
        if (hf.getRight() != null) {
            if (text.length() > 0) {
                text.append("\t");
            }
            text.append(hf.getRight());
        }
        if (text.length() > 0) {
            text.append("\n");
        }
        return text.toString();
    }
}
