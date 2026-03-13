package org.apache.poi.xwpf.usermodel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLException;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocDefaults;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLanguage;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrDefault;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPrDefault;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.StylesDocument;

/* JADX INFO: loaded from: classes.dex */
public class XWPFStyles extends POIXMLDocumentPart {
    private CTStyles ctStyles;
    private XWPFDefaultParagraphStyle defaultParaStyle;
    private XWPFDefaultRunStyle defaultRunStyle;
    private XWPFLatentStyles latentStyles;
    private List<XWPFStyle> listStyle;

    public XWPFStyles(PackagePart part) throws OpenXML4JException, IOException {
        super(part);
        this.listStyle = new ArrayList();
    }

    public XWPFStyles() {
        this.listStyle = new ArrayList();
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void onDocumentRead() throws Throwable {
        StylesDocument stylesDoc;
        InputStream is = getPackagePart().getInputStream();
        try {
            try {
                stylesDoc = StylesDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            } catch (XmlException e) {
                e = e;
            } catch (Throwable th) {
                th = th;
                is.close();
                throw th;
            }
            try {
                setStyles(stylesDoc.getStyles());
                this.latentStyles = new XWPFLatentStyles(this.ctStyles.getLatentStyles(), this);
                is.close();
            } catch (XmlException e2) {
                e = e2;
                throw new POIXMLException("Unable to read styles", e);
            }
        } catch (Throwable th2) {
            th = th2;
            is.close();
            throw th;
        }
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void commit() throws IOException {
        if (this.ctStyles == null) {
            throw new IllegalStateException("Unable to write out styles that were never read in!");
        }
        XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTStyles.type.getName().getNamespaceURI(), "styles"));
        PackagePart part = getPackagePart();
        OutputStream out = part.getOutputStream();
        this.ctStyles.save(out, xmlOptions);
        out.close();
    }

    protected void ensureDocDefaults() {
        if (!this.ctStyles.isSetDocDefaults()) {
            this.ctStyles.addNewDocDefaults();
        }
        CTDocDefaults docDefaults = this.ctStyles.getDocDefaults();
        if (!docDefaults.isSetPPrDefault()) {
            docDefaults.addNewPPrDefault();
        }
        if (!docDefaults.isSetRPrDefault()) {
            docDefaults.addNewRPrDefault();
        }
        CTPPrDefault pprd = docDefaults.getPPrDefault();
        CTRPrDefault rprd = docDefaults.getRPrDefault();
        if (!pprd.isSetPPr()) {
            pprd.addNewPPr();
        }
        if (!rprd.isSetRPr()) {
            rprd.addNewRPr();
        }
        this.defaultRunStyle = new XWPFDefaultRunStyle(rprd.getRPr());
        this.defaultParaStyle = new XWPFDefaultParagraphStyle(pprd.getPPr());
    }

    public void setStyles(CTStyles styles) {
        this.ctStyles = styles;
        CTStyle[] arr$ = styles.getStyleArray();
        for (CTStyle style : arr$) {
            this.listStyle.add(new XWPFStyle(style, this));
        }
        if (this.ctStyles.isSetDocDefaults()) {
            CTDocDefaults docDefaults = this.ctStyles.getDocDefaults();
            if (docDefaults.isSetRPrDefault() && docDefaults.getRPrDefault().isSetRPr()) {
                this.defaultRunStyle = new XWPFDefaultRunStyle(docDefaults.getRPrDefault().getRPr());
            }
            if (docDefaults.isSetPPrDefault() && docDefaults.getPPrDefault().isSetPPr()) {
                this.defaultParaStyle = new XWPFDefaultParagraphStyle(docDefaults.getPPrDefault().getPPr());
            }
        }
    }

    public boolean styleExist(String styleID) {
        for (XWPFStyle style : this.listStyle) {
            if (style.getStyleId().equals(styleID)) {
                return true;
            }
        }
        return false;
    }

    public void addStyle(XWPFStyle style) {
        this.listStyle.add(style);
        this.ctStyles.addNewStyle();
        int pos = this.ctStyles.sizeOfStyleArray() - 1;
        this.ctStyles.setStyleArray(pos, style.getCTStyle());
    }

    public XWPFStyle getStyle(String styleID) {
        for (XWPFStyle style : this.listStyle) {
            if (style.getStyleId().equals(styleID)) {
                return style;
            }
        }
        return null;
    }

    public int getNumberOfStyles() {
        return this.listStyle.size();
    }

    public List<XWPFStyle> getUsedStyleList(XWPFStyle style) {
        List<XWPFStyle> usedStyleList = new ArrayList<>();
        usedStyleList.add(style);
        return getUsedStyleList(style, usedStyleList);
    }

    private List<XWPFStyle> getUsedStyleList(XWPFStyle style, List<XWPFStyle> usedStyleList) {
        String basisStyleID = style.getBasisStyleID();
        XWPFStyle basisStyle = getStyle(basisStyleID);
        if (basisStyle != null && !usedStyleList.contains(basisStyle)) {
            usedStyleList.add(basisStyle);
            getUsedStyleList(basisStyle, usedStyleList);
        }
        String linkStyleID = style.getLinkStyleID();
        XWPFStyle linkStyle = getStyle(linkStyleID);
        if (linkStyle != null && !usedStyleList.contains(linkStyle)) {
            usedStyleList.add(linkStyle);
            getUsedStyleList(linkStyle, usedStyleList);
        }
        String nextStyleID = style.getNextStyleID();
        XWPFStyle nextStyle = getStyle(nextStyleID);
        if (nextStyle != null && !usedStyleList.contains(nextStyle)) {
            usedStyleList.add(linkStyle);
            getUsedStyleList(linkStyle, usedStyleList);
        }
        return usedStyleList;
    }

    protected CTLanguage getCTLanguage() {
        ensureDocDefaults();
        if (this.defaultRunStyle.getRPr().isSetLang()) {
            CTLanguage lang = this.defaultRunStyle.getRPr().getLang();
            return lang;
        }
        CTLanguage lang2 = this.defaultRunStyle.getRPr().addNewLang();
        return lang2;
    }

    public void setSpellingLanguage(String strSpellingLanguage) {
        CTLanguage lang = getCTLanguage();
        lang.setVal(strSpellingLanguage);
        lang.setBidi(strSpellingLanguage);
    }

    public void setEastAsia(String strEastAsia) {
        CTLanguage lang = getCTLanguage();
        lang.setEastAsia(strEastAsia);
    }

    public void setDefaultFonts(CTFonts fonts) {
        ensureDocDefaults();
        CTRPr runProps = this.defaultRunStyle.getRPr();
        runProps.setRFonts(fonts);
    }

    public XWPFStyle getStyleWithSameName(XWPFStyle style) {
        for (XWPFStyle ownStyle : this.listStyle) {
            if (ownStyle.hasSameName(style)) {
                return ownStyle;
            }
        }
        return null;
    }

    public XWPFDefaultRunStyle getDefaultRunStyle() {
        ensureDocDefaults();
        return this.defaultRunStyle;
    }

    public XWPFDefaultParagraphStyle getDefaultParagraphStyle() {
        ensureDocDefaults();
        return this.defaultParaStyle;
    }

    public XWPFLatentStyles getLatentStyles() {
        return this.latentStyles;
    }
}
