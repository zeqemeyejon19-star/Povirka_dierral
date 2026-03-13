package org.apache.poi.xssf.usermodel;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.model.MapInfo;
import org.apache.poi.xssf.model.SingleXmlCells;
import org.apache.poi.xssf.usermodel.helpers.XSSFSingleXmlCell;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMap;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSchema;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: classes.dex */
public class XSSFMap {
    private CTMap ctMap;
    private MapInfo mapInfo;

    public XSSFMap(CTMap ctMap, MapInfo mapInfo) {
        this.ctMap = ctMap;
        this.mapInfo = mapInfo;
    }

    @Internal
    public CTMap getCtMap() {
        return this.ctMap;
    }

    @Internal
    public CTSchema getCTSchema() {
        String schemaId = this.ctMap.getSchemaID();
        return this.mapInfo.getCTSchemaById(schemaId);
    }

    public Node getSchema() {
        CTSchema schema = getCTSchema();
        Node xmlSchema = schema.getDomNode().getFirstChild();
        return xmlSchema;
    }

    public List<XSSFSingleXmlCell> getRelatedSingleXMLCell() {
        List<XSSFSingleXmlCell> relatedSimpleXmlCells = new ArrayList<>();
        int sheetNumber = this.mapInfo.getWorkbook().getNumberOfSheets();
        for (int i = 0; i < sheetNumber; i++) {
            XSSFSheet sheet = this.mapInfo.getWorkbook().getSheetAt(i);
            for (POIXMLDocumentPart p : sheet.getRelations()) {
                if (p instanceof SingleXmlCells) {
                    SingleXmlCells singleXMLCells = (SingleXmlCells) p;
                    for (XSSFSingleXmlCell cell : singleXMLCells.getAllSimpleXmlCell()) {
                        if (cell.getMapId() == this.ctMap.getID()) {
                            relatedSimpleXmlCells.add(cell);
                        }
                    }
                }
            }
        }
        return relatedSimpleXmlCells;
    }

    public List<XSSFTable> getRelatedTables() {
        List<XSSFTable> tables = new ArrayList<>();
        for (Sheet sheet : this.mapInfo.getWorkbook()) {
            for (POIXMLDocumentPart.RelationPart rp : ((XSSFSheet) sheet).getRelationParts()) {
                if (rp.getRelationship().getRelationshipType().equals(XSSFRelation.TABLE.getRelation())) {
                    XSSFTable table = (XSSFTable) rp.getDocumentPart();
                    if (table.mapsTo(this.ctMap.getID())) {
                        tables.add(table);
                    }
                }
            }
        }
        return tables;
    }
}
