package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.PrintSetupRecord;
import org.apache.poi.ss.usermodel.PrintSetup;

/* JADX INFO: loaded from: classes.dex */
public class HSSFPrintSetup implements PrintSetup {
    PrintSetupRecord printSetupRecord;

    protected HSSFPrintSetup(PrintSetupRecord printSetupRecord) {
        this.printSetupRecord = printSetupRecord;
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public void setPaperSize(short size) {
        this.printSetupRecord.setPaperSize(size);
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public void setScale(short scale) {
        this.printSetupRecord.setScale(scale);
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public void setPageStart(short start) {
        this.printSetupRecord.setPageStart(start);
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public void setFitWidth(short width) {
        this.printSetupRecord.setFitWidth(width);
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public void setFitHeight(short height) {
        this.printSetupRecord.setFitHeight(height);
    }

    public void setOptions(short options) {
        this.printSetupRecord.setOptions(options);
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public void setLeftToRight(boolean ltor) {
        this.printSetupRecord.setLeftToRight(ltor);
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public void setLandscape(boolean ls) {
        this.printSetupRecord.setLandscape(!ls);
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public void setValidSettings(boolean valid) {
        this.printSetupRecord.setValidSettings(valid);
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public void setNoColor(boolean mono) {
        this.printSetupRecord.setNoColor(mono);
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public void setDraft(boolean d) {
        this.printSetupRecord.setDraft(d);
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public void setNotes(boolean printnotes) {
        this.printSetupRecord.setNotes(printnotes);
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public void setNoOrientation(boolean orientation) {
        this.printSetupRecord.setNoOrientation(orientation);
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public void setUsePage(boolean page) {
        this.printSetupRecord.setUsePage(page);
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public void setHResolution(short resolution) {
        this.printSetupRecord.setHResolution(resolution);
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public void setVResolution(short resolution) {
        this.printSetupRecord.setVResolution(resolution);
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public void setHeaderMargin(double headermargin) {
        this.printSetupRecord.setHeaderMargin(headermargin);
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public void setFooterMargin(double footermargin) {
        this.printSetupRecord.setFooterMargin(footermargin);
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public void setCopies(short copies) {
        this.printSetupRecord.setCopies(copies);
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public short getPaperSize() {
        return this.printSetupRecord.getPaperSize();
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public short getScale() {
        return this.printSetupRecord.getScale();
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public short getPageStart() {
        return this.printSetupRecord.getPageStart();
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public short getFitWidth() {
        return this.printSetupRecord.getFitWidth();
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public short getFitHeight() {
        return this.printSetupRecord.getFitHeight();
    }

    public short getOptions() {
        return this.printSetupRecord.getOptions();
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public boolean getLeftToRight() {
        return this.printSetupRecord.getLeftToRight();
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public boolean getLandscape() {
        return !this.printSetupRecord.getLandscape();
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public boolean getValidSettings() {
        return this.printSetupRecord.getValidSettings();
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public boolean getNoColor() {
        return this.printSetupRecord.getNoColor();
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public boolean getDraft() {
        return this.printSetupRecord.getDraft();
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public boolean getNotes() {
        return this.printSetupRecord.getNotes();
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public boolean getNoOrientation() {
        return this.printSetupRecord.getNoOrientation();
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public boolean getUsePage() {
        return this.printSetupRecord.getUsePage();
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public short getHResolution() {
        return this.printSetupRecord.getHResolution();
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public short getVResolution() {
        return this.printSetupRecord.getVResolution();
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public double getHeaderMargin() {
        return this.printSetupRecord.getHeaderMargin();
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public double getFooterMargin() {
        return this.printSetupRecord.getFooterMargin();
    }

    @Override // org.apache.poi.ss.usermodel.PrintSetup
    public short getCopies() {
        return this.printSetupRecord.getCopies();
    }
}
