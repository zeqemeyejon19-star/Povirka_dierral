package org.apache.poi.xwpf.usermodel;

/* JADX INFO: loaded from: classes.dex */
public class PositionInParagraph {
    private int posChar;
    private int posRun;
    private int posText;

    public PositionInParagraph() {
        this.posRun = 0;
        this.posText = 0;
        this.posChar = 0;
    }

    public PositionInParagraph(int posRun, int posText, int posChar) {
        this.posRun = 0;
        this.posText = 0;
        this.posChar = 0;
        this.posRun = posRun;
        this.posChar = posChar;
        this.posText = posText;
    }

    public int getRun() {
        return this.posRun;
    }

    public void setRun(int beginRun) {
        this.posRun = beginRun;
    }

    public int getText() {
        return this.posText;
    }

    public void setText(int beginText) {
        this.posText = beginText;
    }

    public int getChar() {
        return this.posChar;
    }

    public void setChar(int beginChar) {
        this.posChar = beginChar;
    }
}
