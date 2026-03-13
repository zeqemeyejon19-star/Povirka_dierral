package org.apache.poi.xssf.usermodel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.usermodel.Chart;
import org.apache.poi.ss.usermodel.charts.AxisPosition;
import org.apache.poi.ss.usermodel.charts.ChartAxis;
import org.apache.poi.ss.usermodel.charts.ChartAxisFactory;
import org.apache.poi.ss.usermodel.charts.ChartData;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Removal;
import org.apache.poi.xssf.usermodel.charts.XSSFCategoryAxis;
import org.apache.poi.xssf.usermodel.charts.XSSFChartAxis;
import org.apache.poi.xssf.usermodel.charts.XSSFChartDataFactory;
import org.apache.poi.xssf.usermodel.charts.XSSFChartLegend;
import org.apache.poi.xssf.usermodel.charts.XSSFDateAxis;
import org.apache.poi.xssf.usermodel.charts.XSSFManualLayout;
import org.apache.poi.xssf.usermodel.charts.XSSFValueAxis;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCatAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartSpace;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDateAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPageMargins;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPrintSettings;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrRef;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTitle;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTValAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.ChartSpaceDocument;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextField;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/* JADX INFO: loaded from: classes.dex */
public final class XSSFChart extends POIXMLDocumentPart implements Chart, ChartAxisFactory {
    List<XSSFChartAxis> axis;
    private CTChart chart;
    private CTChartSpace chartSpace;
    private XSSFGraphicFrame frame;

    protected XSSFChart() {
        this.axis = new ArrayList();
        createChart();
    }

    protected XSSFChart(PackagePart part) throws XmlException, IOException {
        super(part);
        this.axis = new ArrayList();
        CTChartSpace chartSpace = ChartSpaceDocument.Factory.parse(part.getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS).getChartSpace();
        this.chartSpace = chartSpace;
        this.chart = chartSpace.getChart();
    }

    private void createChart() {
        CTChartSpace cTChartSpaceNewInstance = CTChartSpace.Factory.newInstance();
        this.chartSpace = cTChartSpaceNewInstance;
        CTChart cTChartAddNewChart = cTChartSpaceNewInstance.addNewChart();
        this.chart = cTChartAddNewChart;
        CTPlotArea plotArea = cTChartAddNewChart.addNewPlotArea();
        plotArea.addNewLayout();
        this.chart.addNewPlotVisOnly().setVal(true);
        CTPrintSettings printSettings = this.chartSpace.addNewPrintSettings();
        printSettings.addNewHeaderFooter();
        CTPageMargins pageMargins = printSettings.addNewPageMargins();
        pageMargins.setB(0.75d);
        pageMargins.setL(0.7d);
        pageMargins.setR(0.7d);
        pageMargins.setT(0.75d);
        pageMargins.setHeader(0.3d);
        pageMargins.setFooter(0.3d);
        printSettings.addNewPageSetup();
    }

    @Internal
    public CTChartSpace getCTChartSpace() {
        return this.chartSpace;
    }

    @Internal
    public CTChart getCTChart() {
        return this.chart;
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void commit() throws IOException {
        XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTChartSpace.type.getName().getNamespaceURI(), "chartSpace", "c"));
        PackagePart part = getPackagePart();
        OutputStream out = part.getOutputStream();
        this.chartSpace.save(out, xmlOptions);
        out.close();
    }

    public XSSFGraphicFrame getGraphicFrame() {
        return this.frame;
    }

    protected void setGraphicFrame(XSSFGraphicFrame frame) {
        this.frame = frame;
    }

    @Override // org.apache.poi.ss.usermodel.Chart
    public XSSFChartDataFactory getChartDataFactory() {
        return XSSFChartDataFactory.getInstance();
    }

    @Override // org.apache.poi.ss.usermodel.Chart
    public XSSFChart getChartAxisFactory() {
        return this;
    }

    @Override // org.apache.poi.ss.usermodel.Chart
    public void plot(ChartData data, ChartAxis... chartAxis) {
        data.fillChart(this, chartAxis);
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxisFactory
    public XSSFValueAxis createValueAxis(AxisPosition pos) {
        long id = this.axis.size() + 1;
        XSSFValueAxis valueAxis = new XSSFValueAxis(this, id, pos);
        if (this.axis.size() == 1) {
            ChartAxis ax = this.axis.get(0);
            ax.crossAxis(valueAxis);
            valueAxis.crossAxis(ax);
        }
        this.axis.add(valueAxis);
        return valueAxis;
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxisFactory
    public XSSFCategoryAxis createCategoryAxis(AxisPosition pos) {
        long id = this.axis.size() + 1;
        XSSFCategoryAxis categoryAxis = new XSSFCategoryAxis(this, id, pos);
        if (this.axis.size() == 1) {
            ChartAxis ax = this.axis.get(0);
            ax.crossAxis(categoryAxis);
            categoryAxis.crossAxis(ax);
        }
        this.axis.add(categoryAxis);
        return categoryAxis;
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxisFactory
    public XSSFDateAxis createDateAxis(AxisPosition pos) {
        long id = this.axis.size() + 1;
        XSSFDateAxis dateAxis = new XSSFDateAxis(this, id, pos);
        if (this.axis.size() == 1) {
            ChartAxis ax = this.axis.get(0);
            ax.crossAxis(dateAxis);
            dateAxis.crossAxis(ax);
        }
        this.axis.add(dateAxis);
        return dateAxis;
    }

    @Override // org.apache.poi.ss.usermodel.Chart
    public List<? extends XSSFChartAxis> getAxis() {
        if (this.axis.isEmpty() && hasAxis()) {
            parseAxis();
        }
        return this.axis;
    }

    @Override // org.apache.poi.ss.usermodel.charts.ManuallyPositionable
    public XSSFManualLayout getManualLayout() {
        return new XSSFManualLayout(this);
    }

    public boolean isPlotOnlyVisibleCells() {
        return this.chart.getPlotVisOnly().getVal();
    }

    public void setPlotOnlyVisibleCells(boolean plotVisOnly) {
        this.chart.getPlotVisOnly().setVal(plotVisOnly);
    }

    @Removal(version = "4.0")
    @Deprecated
    public XSSFRichTextString getTitle() {
        return getTitleText();
    }

    public XSSFRichTextString getTitleText() {
        if (!this.chart.isSetTitle()) {
            return null;
        }
        CTTitle title = this.chart.getTitle();
        StringBuffer text = new StringBuffer();
        XmlObject[] t = title.selectPath("declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main' .//a:t");
        for (XmlObject xmlObject : t) {
            NodeList kids = xmlObject.getDomNode().getChildNodes();
            int count = kids.getLength();
            for (int n = 0; n < count; n++) {
                Node kid = kids.item(n);
                if (kid instanceof Text) {
                    text.append(kid.getNodeValue());
                }
            }
        }
        return new XSSFRichTextString(text.toString());
    }

    @Removal(version = "4.0")
    @Deprecated
    public void setTitle(String newTitle) {
    }

    public void setTitleText(String newTitle) {
        CTTitle ctTitle;
        CTTx tx;
        CTTextBody rich;
        CTTextParagraph para;
        if (this.chart.isSetTitle()) {
            ctTitle = this.chart.getTitle();
        } else {
            ctTitle = this.chart.addNewTitle();
        }
        if (ctTitle.isSetTx()) {
            tx = ctTitle.getTx();
        } else {
            tx = ctTitle.addNewTx();
        }
        if (tx.isSetStrRef()) {
            tx.unsetStrRef();
        }
        if (tx.isSetRich()) {
            rich = tx.getRich();
        } else {
            rich = tx.addNewRich();
            rich.addNewBodyPr();
        }
        if (rich.sizeOfPArray() > 0) {
            para = rich.getPArray(0);
        } else {
            para = rich.addNewP();
        }
        if (para.sizeOfRArray() > 0) {
            CTRegularTextRun run = para.getRArray(0);
            run.setT(newTitle);
        } else if (para.sizeOfFldArray() > 0) {
            CTTextField fld = para.getFldArray(0);
            fld.setT(newTitle);
        } else {
            CTRegularTextRun run2 = para.addNewR();
            run2.setT(newTitle);
        }
    }

    public String getTitleFormula() {
        if (!this.chart.isSetTitle()) {
            return null;
        }
        CTTitle title = this.chart.getTitle();
        if (!title.isSetTx()) {
            return null;
        }
        CTTx tx = title.getTx();
        if (tx.isSetStrRef()) {
            return tx.getStrRef().getF();
        }
        return null;
    }

    public void setTitleFormula(String formula) {
        CTTitle ctTitle;
        CTTx tx;
        CTStrRef strRef;
        if (this.chart.isSetTitle()) {
            ctTitle = this.chart.getTitle();
        } else {
            ctTitle = this.chart.addNewTitle();
        }
        if (ctTitle.isSetTx()) {
            tx = ctTitle.getTx();
        } else {
            tx = ctTitle.addNewTx();
        }
        if (tx.isSetRich()) {
            tx.unsetRich();
        }
        if (tx.isSetStrRef()) {
            strRef = tx.getStrRef();
        } else {
            strRef = tx.addNewStrRef();
        }
        strRef.setF(formula);
    }

    @Override // org.apache.poi.ss.usermodel.Chart
    public XSSFChartLegend getOrCreateLegend() {
        return new XSSFChartLegend(this);
    }

    @Override // org.apache.poi.ss.usermodel.Chart
    public void deleteLegend() {
        if (this.chart.isSetLegend()) {
            this.chart.unsetLegend();
        }
    }

    private boolean hasAxis() {
        CTPlotArea ctPlotArea = this.chart.getPlotArea();
        int totalAxisCount = ctPlotArea.sizeOfValAxArray() + ctPlotArea.sizeOfCatAxArray() + ctPlotArea.sizeOfDateAxArray() + ctPlotArea.sizeOfSerAxArray();
        return totalAxisCount > 0;
    }

    private void parseAxis() {
        parseCategoryAxis();
        parseDateAxis();
        parseValueAxis();
    }

    private void parseCategoryAxis() {
        CTCatAx[] arr$ = this.chart.getPlotArea().getCatAxArray();
        for (CTCatAx catAx : arr$) {
            this.axis.add(new XSSFCategoryAxis(this, catAx));
        }
    }

    private void parseDateAxis() {
        CTDateAx[] arr$ = this.chart.getPlotArea().getDateAxArray();
        for (CTDateAx dateAx : arr$) {
            this.axis.add(new XSSFDateAxis(this, dateAx));
        }
    }

    private void parseValueAxis() {
        CTValAx[] arr$ = this.chart.getPlotArea().getValAxArray();
        for (CTValAx valAx : arr$) {
            this.axis.add(new XSSFValueAxis(this, valAx));
        }
    }
}
