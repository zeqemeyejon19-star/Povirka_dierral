package org.apache.poi.xwpf.model;

import org.apache.poi.xwpf.usermodel.XWPFComment;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange;

/* JADX INFO: loaded from: classes.dex */
public class XWPFCommentsDecorator extends XWPFParagraphDecorator {
    private StringBuffer commentText;

    public XWPFCommentsDecorator(XWPFParagraphDecorator nextDecorator) {
        this(nextDecorator.paragraph, nextDecorator);
    }

    public XWPFCommentsDecorator(XWPFParagraph paragraph, XWPFParagraphDecorator nextDecorator) {
        super(paragraph, nextDecorator);
        this.commentText = new StringBuffer();
        CTMarkupRange[] arr$ = paragraph.getCTP().getCommentRangeStartArray();
        for (CTMarkupRange anchor : arr$) {
            XWPFComment comment = paragraph.getDocument().getCommentByID(anchor.getId().toString());
            if (comment != null) {
                this.commentText.append("\tComment by " + comment.getAuthor() + ": " + comment.getText());
            }
        }
    }

    public String getCommentText() {
        return this.commentText.toString();
    }

    @Override // org.apache.poi.xwpf.model.XWPFParagraphDecorator
    public String getText() {
        return super.getText() + ((Object) this.commentText);
    }
}
