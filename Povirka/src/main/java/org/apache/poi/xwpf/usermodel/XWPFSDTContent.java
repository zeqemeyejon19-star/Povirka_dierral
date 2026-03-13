package org.apache.poi.xwpf.usermodel;

import java.util.ArrayList;
import java.util.List;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtContentBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtContentRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;

/* JADX INFO: loaded from: classes.dex */
public class XWPFSDTContent implements ISDTContent {
    private List<XWPFParagraph> paragraphs = new ArrayList();
    private List<XWPFTable> tables = new ArrayList();
    private List<XWPFRun> runs = new ArrayList();
    private List<XWPFSDT> contentControls = new ArrayList();
    private List<ISDTContents> bodyElements = new ArrayList();

    public XWPFSDTContent(CTSdtContentRun sdtRun, IBody part, IRunBody parent) {
        CTR[] arr$ = sdtRun.getRArray();
        for (CTR ctr : arr$) {
            XWPFRun run = new XWPFRun(ctr, parent);
            this.runs.add(run);
            this.bodyElements.add(run);
        }
    }

    public XWPFSDTContent(CTSdtContentBlock block, IBody part, IRunBody parent) {
        XmlCursor cursor = block.newCursor();
        cursor.selectPath("./*");
        while (cursor.toNextSelection()) {
            CTR object = cursor.getObject();
            if (object instanceof CTP) {
                XWPFParagraph p = new XWPFParagraph((CTP) object, part);
                this.bodyElements.add(p);
                this.paragraphs.add(p);
            } else if (object instanceof CTTbl) {
                XWPFTable t = new XWPFTable((CTTbl) object, part);
                this.bodyElements.add(t);
                this.tables.add(t);
            } else if (object instanceof CTSdtBlock) {
                XWPFSDT c = new XWPFSDT((CTSdtBlock) object, part);
                this.bodyElements.add(c);
                this.contentControls.add(c);
            } else if (object instanceof CTR) {
                XWPFRun run = new XWPFRun(object, parent);
                this.runs.add(run);
                this.bodyElements.add(run);
            }
        }
        cursor.dispose();
    }

    @Override // org.apache.poi.xwpf.usermodel.ISDTContent
    public String getText() {
        StringBuilder text = new StringBuilder();
        boolean addNewLine = false;
        for (int i = 0; i < this.bodyElements.size(); i++) {
            Object o = this.bodyElements.get(i);
            if (o instanceof XWPFParagraph) {
                appendParagraph((XWPFParagraph) o, text);
                addNewLine = true;
            } else if (o instanceof XWPFTable) {
                appendTable((XWPFTable) o, text);
                addNewLine = true;
            } else if (o instanceof XWPFSDT) {
                text.append(((XWPFSDT) o).getContent().getText());
                addNewLine = true;
            } else if (o instanceof XWPFRun) {
                text.append((XWPFRun) o);
                addNewLine = false;
            }
            if (addNewLine && i < this.bodyElements.size() - 1) {
                text.append("\n");
            }
        }
        return text.toString();
    }

    private void appendTable(XWPFTable table, StringBuilder text) {
        for (XWPFTableRow row : table.getRows()) {
            List<ICell> cells = row.getTableICells();
            for (int i = 0; i < cells.size(); i++) {
                ICell cell = cells.get(i);
                if (cell instanceof XWPFTableCell) {
                    text.append(((XWPFTableCell) cell).getTextRecursively());
                } else if (cell instanceof XWPFSDTCell) {
                    text.append(((XWPFSDTCell) cell).getContent().getText());
                }
                if (i < cells.size() - 1) {
                    text.append("\t");
                }
            }
            text.append('\n');
        }
    }

    private void appendParagraph(XWPFParagraph paragraph, StringBuilder text) {
        for (IRunElement run : paragraph.getRuns()) {
            text.append(run);
        }
    }

    @Override // org.apache.poi.xwpf.usermodel.ISDTContent
    public String toString() {
        return getText();
    }
}
