package org.apache.poi.xdgf.usermodel.section;

import com.microsoft.schemas.office.visio.x2012.main.CellType;
import com.microsoft.schemas.office.visio.x2012.main.SectionType;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.POIXMLException;
import org.apache.poi.util.Internal;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import org.apache.poi.xdgf.usermodel.XDGFSheet;
import org.apache.poi.xdgf.util.ObjectFactory;

/* JADX INFO: loaded from: classes.dex */
public abstract class XDGFSection {
    static final ObjectFactory<XDGFSection, SectionType> _sectionTypes;
    protected Map<String, XDGFCell> _cells = new HashMap();
    protected XDGFSheet _containingSheet;
    protected SectionType _section;

    public abstract void setupMaster(XDGFSection xDGFSection);

    static {
        ObjectFactory<XDGFSection, SectionType> objectFactory = new ObjectFactory<>();
        _sectionTypes = objectFactory;
        try {
            objectFactory.put("LineGradient", GenericSection.class, SectionType.class, XDGFSheet.class);
            objectFactory.put("FillGradient", GenericSection.class, SectionType.class, XDGFSheet.class);
            objectFactory.put("Character", CharacterSection.class, SectionType.class, XDGFSheet.class);
            objectFactory.put("Paragraph", GenericSection.class, SectionType.class, XDGFSheet.class);
            objectFactory.put("Tabs", GenericSection.class, SectionType.class, XDGFSheet.class);
            objectFactory.put("Scratch", GenericSection.class, SectionType.class, XDGFSheet.class);
            objectFactory.put("Connection", GenericSection.class, SectionType.class, XDGFSheet.class);
            objectFactory.put("ConnectionABCD", GenericSection.class, SectionType.class, XDGFSheet.class);
            objectFactory.put("Field", GenericSection.class, SectionType.class, XDGFSheet.class);
            objectFactory.put("Control", GenericSection.class, SectionType.class, XDGFSheet.class);
            objectFactory.put("Geometry", GeometrySection.class, SectionType.class, XDGFSheet.class);
            objectFactory.put("Actions", GenericSection.class, SectionType.class, XDGFSheet.class);
            objectFactory.put("Layer", GenericSection.class, SectionType.class, XDGFSheet.class);
            objectFactory.put("User", GenericSection.class, SectionType.class, XDGFSheet.class);
            objectFactory.put("Property", GenericSection.class, SectionType.class, XDGFSheet.class);
            objectFactory.put("Hyperlink", GenericSection.class, SectionType.class, XDGFSheet.class);
            objectFactory.put("Reviewer", GenericSection.class, SectionType.class, XDGFSheet.class);
            objectFactory.put("Annotation", GenericSection.class, SectionType.class, XDGFSheet.class);
            objectFactory.put("ActionTag", GenericSection.class, SectionType.class, XDGFSheet.class);
        } catch (NoSuchMethodException e) {
            throw new POIXMLException("Internal error");
        } catch (SecurityException e2) {
            throw new POIXMLException("Internal error");
        }
    }

    public static XDGFSection load(SectionType section, XDGFSheet containingSheet) {
        return _sectionTypes.load(section.getN(), section, containingSheet);
    }

    public XDGFSection(SectionType section, XDGFSheet containingSheet) {
        this._section = section;
        this._containingSheet = containingSheet;
        CellType[] arr$ = section.getCellArray();
        for (CellType cell : arr$) {
            this._cells.put(cell.getN(), new XDGFCell(cell));
        }
    }

    @Internal
    public SectionType getXmlObject() {
        return this._section;
    }

    public String toString() {
        return "<Section type=" + this._section.getN() + " from " + this._containingSheet + ">";
    }
}
