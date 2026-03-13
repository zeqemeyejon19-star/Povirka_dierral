package org.apache.poi.sl.draw.geom;

import java.io.InputStream;
import java.util.LinkedHashMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.poi.sl.draw.binding.CTCustomGeometry2D;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.StaxHelper;

/* JADX INFO: loaded from: classes.dex */
public class PresetGeometries extends LinkedHashMap<String, CustomGeometry> {
    protected static final String BINDING_PACKAGE = "org.apache.poi.sl.draw.binding";
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) PresetGeometries.class);
    protected static PresetGeometries _inst;

    protected PresetGeometries() {
    }

    public void init(InputStream is) throws XMLStreamException, JAXBException {
        EventFilter startElementFilter;
        XMLInputFactory staxFactory;
        EventFilter startElementFilter2 = new EventFilter() { // from class: org.apache.poi.sl.draw.geom.PresetGeometries.1
            public boolean accept(XMLEvent event) {
                return event.isStartElement();
            }
        };
        XMLInputFactory staxFactory2 = StaxHelper.newXMLInputFactory();
        XMLEventReader staxReader = staxFactory2.createXMLEventReader(is);
        XMLEventReader staxFiltRd = staxFactory2.createFilteredReader(staxReader, startElementFilter2);
        staxFiltRd.nextEvent();
        JAXBContext jaxbContext = JAXBContext.newInstance(BINDING_PACKAGE);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        long cntElem = 0;
        while (staxFiltRd.peek() != null) {
            StartElement evRoot = staxFiltRd.peek();
            String name = evRoot.getName().getLocalPart();
            JAXBElement<CTCustomGeometry2D> el = unmarshaller.unmarshal(staxReader, CTCustomGeometry2D.class);
            CTCustomGeometry2D cus = (CTCustomGeometry2D) el.getValue();
            cntElem++;
            if (!containsKey(name)) {
                startElementFilter = startElementFilter2;
                staxFactory = staxFactory2;
            } else {
                startElementFilter = startElementFilter2;
                staxFactory = staxFactory2;
                LOG.log(5, "Duplicate definition of " + name);
            }
            put(name, new CustomGeometry(cus));
            startElementFilter2 = startElementFilter;
            staxFactory2 = staxFactory;
        }
    }

    public static CustomGeometry convertCustomGeometry(XMLStreamReader staxReader) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(BINDING_PACKAGE);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<CTCustomGeometry2D> el = unmarshaller.unmarshal(staxReader, CTCustomGeometry2D.class);
            return new CustomGeometry((CTCustomGeometry2D) el.getValue());
        } catch (JAXBException e) {
            LOG.log(7, "Unable to parse single custom geometry", e);
            return null;
        }
    }

    public static synchronized PresetGeometries getInstance() {
        if (_inst == null) {
            PresetGeometries lInst = new PresetGeometries();
            try {
                InputStream is = PresetGeometries.class.getResourceAsStream("presetShapeDefinitions.xml");
                try {
                    lInst.init(is);
                    _inst = lInst;
                } finally {
                    is.close();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return _inst;
    }
}
