package org.apache.poi.hssf.dev;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.formula.ptg.ExpPtg;
import org.apache.poi.ss.formula.ptg.FuncPtg;
import org.apache.poi.ss.formula.ptg.Ptg;

/* JADX INFO: loaded from: classes.dex */
public class FormulaViewer {
    private String file;
    private boolean list = false;

    public void run() throws IOException {
        NPOIFSFileSystem fs = new NPOIFSFileSystem(new File(this.file), true);
        try {
            InputStream is = BiffViewer.getPOIFSInputStream(fs);
            try {
                List<Record> records = RecordFactory.createRecords(is);
                for (Record record : records) {
                    if (record.getSid() == 6) {
                        if (this.list) {
                            listFormula((FormulaRecord) record);
                        } else {
                            parseFormulaRecord((FormulaRecord) record);
                        }
                    }
                }
            } finally {
                is.close();
            }
        } finally {
            fs.close();
        }
    }

    private void listFormula(FormulaRecord record) {
        String numArg;
        Ptg[] tokens = record.getParsedExpression();
        int numptgs = tokens.length;
        Ptg token = tokens[numptgs - 1];
        if (token instanceof FuncPtg) {
            numArg = String.valueOf(numptgs - 1);
        } else {
            numArg = String.valueOf(-1);
        }
        StringBuilder buf = new StringBuilder();
        if (token instanceof ExpPtg) {
            return;
        }
        buf.append(token.toFormulaString());
        buf.append("~");
        byte ptgClass = token.getPtgClass();
        if (ptgClass == 0) {
            buf.append("REF");
        } else if (ptgClass == 32) {
            buf.append("VALUE");
        } else if (ptgClass == 64) {
            buf.append("ARRAY");
        } else {
            throwInvalidRVAToken(token);
        }
        buf.append("~");
        if (numptgs > 1) {
            Ptg token2 = tokens[numptgs - 2];
            byte ptgClass2 = token2.getPtgClass();
            if (ptgClass2 == 0) {
                buf.append("REF");
            } else if (ptgClass2 == 32) {
                buf.append("VALUE");
            } else if (ptgClass2 == 64) {
                buf.append("ARRAY");
            } else {
                throwInvalidRVAToken(token2);
            }
        } else {
            buf.append("VALUE");
        }
        buf.append("~");
        buf.append(numArg);
        System.out.println(buf);
    }

    public void parseFormulaRecord(FormulaRecord record) {
        System.out.println("==============================");
        System.out.print("row = " + record.getRow());
        System.out.println(", col = " + ((int) record.getColumn()));
        System.out.println("value = " + record.getValue());
        System.out.print("xf = " + ((int) record.getXFIndex()));
        System.out.print(", number of ptgs = " + record.getParsedExpression().length);
        System.out.println(", options = " + ((int) record.getOptions()));
        System.out.println("RPN List = " + formulaString(record));
        System.out.println("Formula text = " + composeFormula(record));
    }

    private String formulaString(FormulaRecord record) {
        StringBuilder buf = new StringBuilder();
        Ptg[] tokens = record.getParsedExpression();
        for (Ptg token : tokens) {
            buf.append(token.toFormulaString());
            byte ptgClass = token.getPtgClass();
            if (ptgClass == 0) {
                buf.append("(R)");
            } else if (ptgClass == 32) {
                buf.append("(V)");
            } else if (ptgClass == 64) {
                buf.append("(A)");
            } else {
                throwInvalidRVAToken(token);
            }
            buf.append(' ');
        }
        return buf.toString();
    }

    private static void throwInvalidRVAToken(Ptg token) {
        throw new IllegalStateException("Invalid RVA type (" + ((int) token.getPtgClass()) + "). This should never happen.");
    }

    private static String composeFormula(FormulaRecord record) {
        return HSSFFormulaParser.toFormulaString((HSSFWorkbook) null, record.getParsedExpression());
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setList(boolean list) {
        this.list = list;
    }

    public static void main(String[] args) throws IOException {
        if (args == null || args.length > 2 || args[0].equals("--help")) {
            System.out.println("FormulaViewer .8 proof that the devil lies in the details (or just in BIFF8 files in general)");
            System.out.println("usage: Give me a big fat file name");
        } else {
            if (args[0].equals("--listFunctions")) {
                FormulaViewer viewer = new FormulaViewer();
                viewer.setFile(args[1]);
                viewer.setList(true);
                viewer.run();
                return;
            }
            FormulaViewer viewer2 = new FormulaViewer();
            viewer2.setFile(args[0]);
            viewer2.run();
        }
    }
}
