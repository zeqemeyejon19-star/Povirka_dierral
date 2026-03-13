package org.apache.poi.xssf.usermodel;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.DifferentialStyleProvider;
import org.apache.poi.ss.usermodel.TableStyle;
import org.apache.poi.ss.usermodel.TableStyleType;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDxf;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDxfs;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyle;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleElement;

/* JADX INFO: loaded from: classes.dex */
public class XSSFTableStyle implements TableStyle {
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) XSSFTableStyle.class);
    private final Map<TableStyleType, DifferentialStyleProvider> elementMap = new EnumMap(TableStyleType.class);
    private final int index;
    private final String name;

    public XSSFTableStyle(int index, CTDxfs dxfs, CTTableStyle tableStyle, IndexedColorMap colorMap) {
        CTDxf dxf;
        this.name = tableStyle.getName();
        this.index = index;
        List<CTDxf> dxfList = new ArrayList<>();
        XmlCursor cur = dxfs.newCursor();
        cur.selectPath("declare namespace x='http://schemas.openxmlformats.org/spreadsheetml/2006/main' .//x:dxf | .//dxf");
        while (cur.toNextSelection()) {
            CTDxf object = cur.getObject();
            String parentName = object.getDomNode().getParentNode().getNodeName();
            if (parentName.equals("mc:Fallback") || parentName.equals("x:dxfs") || parentName.contentEquals("dxfs")) {
                try {
                    if (object instanceof CTDxf) {
                        dxf = object;
                    } else {
                        dxf = CTDxf.Factory.parse(object.newXMLStreamReader(), new XmlOptions().setDocumentType(CTDxf.type));
                    }
                    if (dxf != null) {
                        try {
                            dxfList.add(dxf);
                        } catch (XmlException e) {
                            e = e;
                            logger.log(5, "Error parsing XSSFTableStyle", e);
                        }
                    }
                } catch (XmlException e2) {
                    e = e2;
                }
            }
        }
        for (CTTableStyleElement element : tableStyle.getTableStyleElementList()) {
            TableStyleType type = TableStyleType.valueOf(element.getType().toString());
            DifferentialStyleProvider dstyle = null;
            if (element.isSetDxfId()) {
                int idx = (int) element.getDxfId();
                CTDxf dxf2 = dxfList.get(idx);
                int stripeSize = element.isSetSize() ? (int) element.getSize() : 0;
                if (dxf2 != null) {
                    dstyle = new XSSFDxfStyleProvider(dxf2, stripeSize, colorMap);
                }
            }
            this.elementMap.put(type, dstyle);
        }
    }

    @Override // org.apache.poi.ss.usermodel.TableStyle
    public String getName() {
        return this.name;
    }

    @Override // org.apache.poi.ss.usermodel.TableStyle
    public int getIndex() {
        return this.index;
    }

    @Override // org.apache.poi.ss.usermodel.TableStyle
    public boolean isBuiltin() {
        return false;
    }

    @Override // org.apache.poi.ss.usermodel.TableStyle
    public DifferentialStyleProvider getStyle(TableStyleType type) {
        return this.elementMap.get(type);
    }
}
