package org.apache.poi.hssf.model;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.record.Record;

/* JADX INFO: loaded from: classes.dex */
public final class WorkbookRecordList {
    private List<Record> records = new ArrayList();
    private int protpos = 0;
    private int bspos = 0;
    private int tabpos = 0;
    private int fontpos = 0;
    private int xfpos = 0;
    private int backuppos = 0;
    private int namepos = 0;
    private int supbookpos = 0;
    private int externsheetPos = 0;
    private int palettepos = -1;

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public int size() {
        return this.records.size();
    }

    public Record get(int i) {
        return this.records.get(i);
    }

    public void add(int pos, Record r) {
        this.records.add(pos, r);
        updateRecordPos(pos, true);
    }

    public List<Record> getRecords() {
        return this.records;
    }

    public void remove(Object record) {
        int i = 0;
        for (Record r : this.records) {
            if (r == record) {
                remove(i);
                return;
            }
            i++;
        }
    }

    public void remove(int pos) {
        this.records.remove(pos);
        updateRecordPos(pos, false);
    }

    public int getProtpos() {
        return this.protpos;
    }

    public void setProtpos(int protpos) {
        this.protpos = protpos;
    }

    public int getBspos() {
        return this.bspos;
    }

    public void setBspos(int bspos) {
        this.bspos = bspos;
    }

    public int getTabpos() {
        return this.tabpos;
    }

    public void setTabpos(int tabpos) {
        this.tabpos = tabpos;
    }

    public int getFontpos() {
        return this.fontpos;
    }

    public void setFontpos(int fontpos) {
        this.fontpos = fontpos;
    }

    public int getXfpos() {
        return this.xfpos;
    }

    public void setXfpos(int xfpos) {
        this.xfpos = xfpos;
    }

    public int getBackuppos() {
        return this.backuppos;
    }

    public void setBackuppos(int backuppos) {
        this.backuppos = backuppos;
    }

    public int getPalettepos() {
        return this.palettepos;
    }

    public void setPalettepos(int palettepos) {
        this.palettepos = palettepos;
    }

    public int getNamepos() {
        return this.namepos;
    }

    public int getSupbookpos() {
        return this.supbookpos;
    }

    public void setNamepos(int namepos) {
        this.namepos = namepos;
    }

    public void setSupbookpos(int supbookpos) {
        this.supbookpos = supbookpos;
    }

    public int getExternsheetPos() {
        return this.externsheetPos;
    }

    public void setExternsheetPos(int externsheetPos) {
        this.externsheetPos = externsheetPos;
    }

    private void updateRecordPos(int pos, boolean add) {
        int delta = add ? 1 : -1;
        int p = getProtpos();
        if (p >= pos) {
            setProtpos(p + delta);
        }
        int p2 = getBspos();
        if (p2 >= pos) {
            setBspos(p2 + delta);
        }
        int p3 = getTabpos();
        if (p3 >= pos) {
            setTabpos(p3 + delta);
        }
        int p4 = getFontpos();
        if (p4 >= pos) {
            setFontpos(p4 + delta);
        }
        int p5 = getXfpos();
        if (p5 >= pos) {
            setXfpos(p5 + delta);
        }
        int p6 = getBackuppos();
        if (p6 >= pos) {
            setBackuppos(p6 + delta);
        }
        int p7 = getNamepos();
        if (p7 >= pos) {
            setNamepos(p7 + delta);
        }
        int p8 = getSupbookpos();
        if (p8 >= pos) {
            setSupbookpos(p8 + delta);
        }
        int p9 = getPalettepos();
        if (p9 != -1 && p9 >= pos) {
            setPalettepos(p9 + delta);
        }
        int p10 = getExternsheetPos();
        if (p10 >= pos) {
            setExternsheetPos(p10 + delta);
        }
    }
}
