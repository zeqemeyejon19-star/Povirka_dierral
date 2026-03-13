package org.apache.poi.xwpf.extractor;

import java.io.IOException;
import java.util.List;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.model.XWPFCommentsDecorator;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.ICell;
import org.apache.poi.xwpf.usermodel.IRunElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHyperlink;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRelation;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFSDT;
import org.apache.poi.xwpf.usermodel.XWPFSDTCell;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;

/* JADX INFO: loaded from: classes.dex */
public class XWPFWordExtractor extends POIXMLTextExtractor {
    public static final XWPFRelation[] SUPPORTED_TYPES = {XWPFRelation.DOCUMENT, XWPFRelation.TEMPLATE, XWPFRelation.MACRO_DOCUMENT, XWPFRelation.MACRO_TEMPLATE_DOCUMENT};
    private boolean concatenatePhoneticRuns;
    private XWPFDocument document;
    private boolean fetchHyperlinks;

    public XWPFWordExtractor(OPCPackage container) throws XmlException, OpenXML4JException, IOException {
        this(new XWPFDocument(container));
    }

    public XWPFWordExtractor(XWPFDocument document) {
        super(document);
        this.fetchHyperlinks = false;
        this.concatenatePhoneticRuns = true;
        this.document = document;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Use:");
            System.err.println("  XWPFWordExtractor <filename.docx>");
            System.exit(1);
        }
        POIXMLTextExtractor extractor = new XWPFWordExtractor(POIXMLDocument.openPackage(args[0]));
        System.out.println(extractor.getText());
        extractor.close();
    }

    public void setFetchHyperlinks(boolean fetch) {
        this.fetchHyperlinks = fetch;
    }

    public void setConcatenatePhoneticRuns(boolean concatenatePhoneticRuns) {
        this.concatenatePhoneticRuns = concatenatePhoneticRuns;
    }

    @Override // org.apache.poi.POITextExtractor
    public String getText() {
        StringBuffer text = new StringBuffer();
        XWPFHeaderFooterPolicy hfPolicy = this.document.getHeaderFooterPolicy();
        extractHeaders(text, hfPolicy);
        for (IBodyElement e : this.document.getBodyElements()) {
            appendBodyElementText(text, e);
            text.append('\n');
        }
        extractFooters(text, hfPolicy);
        return text.toString();
    }

    public void appendBodyElementText(StringBuffer text, IBodyElement e) {
        if (e instanceof XWPFParagraph) {
            appendParagraphText(text, (XWPFParagraph) e);
        } else if (e instanceof XWPFTable) {
            appendTableText(text, (XWPFTable) e);
        } else if (e instanceof XWPFSDT) {
            text.append(((XWPFSDT) e).getContent().getText());
        }
    }

    public void appendParagraphText(StringBuffer text, XWPFParagraph paragraph) {
        XWPFHyperlink link;
        CTSectPr ctSectPr = null;
        if (paragraph.getCTP().getPPr() != null) {
            ctSectPr = paragraph.getCTP().getPPr().getSectPr();
        }
        XWPFHeaderFooterPolicy headerFooterPolicy = null;
        if (ctSectPr != null) {
            headerFooterPolicy = new XWPFHeaderFooterPolicy(this.document, ctSectPr);
            extractHeaders(text, headerFooterPolicy);
        }
        for (IRunElement run : paragraph.getRuns()) {
            if (!this.concatenatePhoneticRuns && (run instanceof XWPFRun)) {
                text.append(((XWPFRun) run).text());
            } else {
                text.append(run);
            }
            if ((run instanceof XWPFHyperlinkRun) && this.fetchHyperlinks && (link = ((XWPFHyperlinkRun) run).getHyperlink(this.document)) != null) {
                text.append(" <").append(link.getURL()).append(">");
            }
        }
        XWPFCommentsDecorator decorator = new XWPFCommentsDecorator(paragraph, null);
        String commentText = decorator.getCommentText();
        if (commentText.length() > 0) {
            text.append(commentText).append('\n');
        }
        String footnameText = paragraph.getFootnoteText();
        if (footnameText != null && footnameText.length() > 0) {
            text.append(footnameText).append('\n');
        }
        if (ctSectPr != null) {
            extractFooters(text, headerFooterPolicy);
        }
    }

    private void appendTableText(StringBuffer text, XWPFTable table) {
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

    private void extractFooters(StringBuffer text, XWPFHeaderFooterPolicy hfPolicy) {
        if (hfPolicy == null) {
            return;
        }
        if (hfPolicy.getFirstPageFooter() != null) {
            text.append(hfPolicy.getFirstPageFooter().getText());
        }
        if (hfPolicy.getEvenPageFooter() != null) {
            text.append(hfPolicy.getEvenPageFooter().getText());
        }
        if (hfPolicy.getDefaultFooter() != null) {
            text.append(hfPolicy.getDefaultFooter().getText());
        }
    }

    private void extractHeaders(StringBuffer text, XWPFHeaderFooterPolicy hfPolicy) {
        if (hfPolicy == null) {
            return;
        }
        if (hfPolicy.getFirstPageHeader() != null) {
            text.append(hfPolicy.getFirstPageHeader().getText());
        }
        if (hfPolicy.getEvenPageHeader() != null) {
            text.append(hfPolicy.getEvenPageHeader().getText());
        }
        if (hfPolicy.getDefaultHeader() != null) {
            text.append(hfPolicy.getDefaultHeader().getText());
        }
    }
}
