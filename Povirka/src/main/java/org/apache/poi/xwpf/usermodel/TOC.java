package org.apache.poi.xwpf.usermodel;

import java.math.BigInteger;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LocaleUtil;
import org.apache.xmlbeans.impl.xb.xmlschema.SpaceAttribute;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtContentBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtEndPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabStop;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabs;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabTlc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTheme;

/* JADX INFO: loaded from: classes.dex */
public class TOC {
    CTSdtBlock block;

    public TOC() {
        this(CTSdtBlock.Factory.newInstance());
    }

    public TOC(CTSdtBlock block) {
        this.block = block;
        CTSdtPr sdtPr = block.addNewSdtPr();
        CTDecimalNumber id = sdtPr.addNewId();
        id.setVal(new BigInteger("4844945"));
        sdtPr.addNewDocPartObj().addNewDocPartGallery().setVal("Table of contents");
        CTSdtEndPr sdtEndPr = block.addNewSdtEndPr();
        CTRPr rPr = sdtEndPr.addNewRPr();
        CTFonts fonts = rPr.addNewRFonts();
        fonts.setAsciiTheme(STTheme.MINOR_H_ANSI);
        fonts.setEastAsiaTheme(STTheme.MINOR_H_ANSI);
        fonts.setHAnsiTheme(STTheme.MINOR_H_ANSI);
        fonts.setCstheme(STTheme.MINOR_BIDI);
        rPr.addNewB().setVal(STOnOff.OFF);
        rPr.addNewBCs().setVal(STOnOff.OFF);
        rPr.addNewColor().setVal("auto");
        rPr.addNewSz().setVal(new BigInteger("24"));
        rPr.addNewSzCs().setVal(new BigInteger("24"));
        CTSdtContentBlock content = block.addNewSdtContent();
        CTP p = content.addNewP();
        p.setRsidR("00EF7E24".getBytes(LocaleUtil.CHARSET_1252));
        p.setRsidRDefault("00EF7E24".getBytes(LocaleUtil.CHARSET_1252));
        p.addNewPPr().addNewPStyle().setVal("TOCHeading");
        p.addNewR().addNewT().setStringValue("Table of Contents");
    }

    @Internal
    public CTSdtBlock getBlock() {
        return this.block;
    }

    public void addRow(int level, String title, int page, String bookmarkRef) {
        CTSdtContentBlock contentBlock = this.block.getSdtContent();
        CTP p = contentBlock.addNewP();
        p.setRsidR("00EF7E24".getBytes(LocaleUtil.CHARSET_1252));
        p.setRsidRDefault("00EF7E24".getBytes(LocaleUtil.CHARSET_1252));
        CTPPr pPr = p.addNewPPr();
        pPr.addNewPStyle().setVal("TOC" + level);
        CTTabs tabs = pPr.addNewTabs();
        CTTabStop tab = tabs.addNewTab();
        tab.setVal(STTabJc.RIGHT);
        tab.setLeader(STTabTlc.DOT);
        tab.setPos(new BigInteger("8290"));
        pPr.addNewRPr().addNewNoProof();
        CTR run = p.addNewR();
        run.addNewRPr().addNewNoProof();
        run.addNewT().setStringValue(title);
        CTR run2 = p.addNewR();
        run2.addNewRPr().addNewNoProof();
        run2.addNewTab();
        CTR run3 = p.addNewR();
        run3.addNewRPr().addNewNoProof();
        run3.addNewFldChar().setFldCharType(STFldCharType.BEGIN);
        CTR run4 = p.addNewR();
        run4.addNewRPr().addNewNoProof();
        CTText text = run4.addNewInstrText();
        text.setSpace(SpaceAttribute.Space.PRESERVE);
        text.setStringValue(" PAGEREF _Toc" + bookmarkRef + " \\h ");
        p.addNewR().addNewRPr().addNewNoProof();
        CTR run5 = p.addNewR();
        run5.addNewRPr().addNewNoProof();
        run5.addNewFldChar().setFldCharType(STFldCharType.SEPARATE);
        CTR run6 = p.addNewR();
        run6.addNewRPr().addNewNoProof();
        run6.addNewT().setStringValue(Integer.toString(page));
        CTR run7 = p.addNewR();
        run7.addNewRPr().addNewNoProof();
        run7.addNewFldChar().setFldCharType(STFldCharType.END);
    }
}
