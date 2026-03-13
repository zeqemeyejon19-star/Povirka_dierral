package org.apache.poi.xwpf.model;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;

/* JADX INFO: loaded from: classes.dex */
public abstract class XWPFParagraphDecorator {
    protected XWPFParagraphDecorator nextDecorator;
    protected XWPFParagraph paragraph;

    public XWPFParagraphDecorator(XWPFParagraph paragraph) {
        this(paragraph, null);
    }

    public XWPFParagraphDecorator(XWPFParagraph paragraph, XWPFParagraphDecorator nextDecorator) {
        this.paragraph = paragraph;
        this.nextDecorator = nextDecorator;
    }

    public String getText() {
        XWPFParagraphDecorator xWPFParagraphDecorator = this.nextDecorator;
        if (xWPFParagraphDecorator != null) {
            return xWPFParagraphDecorator.getText();
        }
        return this.paragraph.getText();
    }
}
