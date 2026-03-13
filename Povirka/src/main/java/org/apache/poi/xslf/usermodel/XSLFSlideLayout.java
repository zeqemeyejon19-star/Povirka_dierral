package org.apache.poi.xslf.usermodel;

import java.io.IOException;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.sl.usermodel.MasterSheet;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.util.Internal;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackground;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPlaceholder;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideLayout;
import org.openxmlformats.schemas.presentationml.x2006.main.SldLayoutDocument;

/* JADX INFO: loaded from: classes.dex */
public class XSLFSlideLayout extends XSLFSheet implements MasterSheet<XSLFShape, XSLFTextParagraph> {
    private CTSlideLayout _layout;
    private XSLFSlideMaster _master;

    XSLFSlideLayout() {
        this._layout = CTSlideLayout.Factory.newInstance();
    }

    public XSLFSlideLayout(PackagePart part) throws XmlException, IOException {
        super(part);
        SldLayoutDocument doc = SldLayoutDocument.Factory.parse(getPackagePart().getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        CTSlideLayout sldLayout = doc.getSldLayout();
        this._layout = sldLayout;
        setCommonSlideData(sldLayout.getCSld());
    }

    public String getName() {
        return this._layout.getCSld().getName();
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSheet
    @Internal
    public CTSlideLayout getXmlObject() {
        return this._layout;
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSheet
    protected String getRootElementName() {
        return "sldLayout";
    }

    public XSLFSlideMaster getSlideMaster() {
        if (this._master == null) {
            for (POIXMLDocumentPart p : getRelations()) {
                if (p instanceof XSLFSlideMaster) {
                    this._master = (XSLFSlideMaster) p;
                }
            }
        }
        XSLFSlideMaster xSLFSlideMaster = this._master;
        if (xSLFSlideMaster == null) {
            throw new IllegalStateException("SlideMaster was not found for " + this);
        }
        return xSLFSlideMaster;
    }

    @Override // org.apache.poi.sl.usermodel.Sheet
    public XSLFSlideMaster getMasterSheet() {
        return getSlideMaster();
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSheet
    public XSLFTheme getTheme() {
        return getSlideMaster().getTheme();
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSheet, org.apache.poi.sl.usermodel.Sheet
    public boolean getFollowMasterGraphics() {
        return this._layout.getShowMasterSp();
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSheet
    protected boolean canDraw(XSLFShape shape) {
        if (shape instanceof XSLFSimpleShape) {
            XSLFSimpleShape txt = (XSLFSimpleShape) shape;
            CTPlaceholder ph = txt.getCTPlaceholder();
            if (ph != null) {
                return false;
            }
            return true;
        }
        return true;
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSheet, org.apache.poi.sl.usermodel.Sheet
    public XSLFBackground getBackground() {
        CTBackground bg = this._layout.getCSld().getBg();
        if (bg != null) {
            return new XSLFBackground(bg, this);
        }
        return getMasterSheet().getBackground();
    }

    public void copyLayout(XSLFSlide slide) {
        XSLFTextShape tsh;
        Placeholder ph;
        int i;
        for (XSLFShape sh : getShapes()) {
            if ((sh instanceof XSLFTextShape) && (ph = (tsh = (XSLFTextShape) sh).getTextType()) != null && (i = AnonymousClass1.$SwitchMap$org$apache$poi$sl$usermodel$Placeholder[ph.ordinal()]) != 1 && i != 2 && i != 3) {
                slide.getSpTree().addNewSp().set(tsh.getXmlObject().copy());
            }
        }
    }

    /* JADX INFO: renamed from: org.apache.poi.xslf.usermodel.XSLFSlideLayout$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$sl$usermodel$Placeholder;

        static {
            int[] iArr = new int[Placeholder.values().length];
            $SwitchMap$org$apache$poi$sl$usermodel$Placeholder = iArr;
            try {
                iArr[Placeholder.DATETIME.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$Placeholder[Placeholder.SLIDE_NUMBER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$Placeholder[Placeholder.FOOTER.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public SlideLayout getType() {
        int ordinal = this._layout.getType().intValue() - 1;
        return SlideLayout.values()[ordinal];
    }
}
