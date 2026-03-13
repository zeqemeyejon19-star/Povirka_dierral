package org.apache.poi.wp.usermodel;

/* JADX INFO: loaded from: classes.dex */
public interface Paragraph {
    int getFirstLineIndent();

    int getFontAlignment();

    int getIndentFromLeft();

    int getIndentFromRight();

    boolean isWordWrapped();

    void setFirstLineIndent(int i);

    void setFontAlignment(int i);

    void setIndentFromLeft(int i);

    void setIndentFromRight(int i);

    void setWordWrapped(boolean z);
}
