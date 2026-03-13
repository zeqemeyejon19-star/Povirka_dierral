package org.apache.poi.xwpf.usermodel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumbering;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.NumberingDocument;

/* JADX INFO: loaded from: classes.dex */
public class XWPFNumbering extends POIXMLDocumentPart {
    protected List<XWPFAbstractNum> abstractNums;
    private CTNumbering ctNumbering;
    boolean isNew;
    protected List<XWPFNum> nums;

    public XWPFNumbering(PackagePart part) throws OpenXML4JException, IOException {
        super(part);
        this.abstractNums = new ArrayList();
        this.nums = new ArrayList();
        this.isNew = true;
    }

    public XWPFNumbering() {
        this.abstractNums = new ArrayList();
        this.nums = new ArrayList();
        this.abstractNums = new ArrayList();
        this.nums = new ArrayList();
        this.isNew = true;
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void onDocumentRead() throws IOException {
        InputStream is = getPackagePart().getInputStream();
        try {
            try {
                NumberingDocument numberingDoc = NumberingDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                CTNumbering numbering = numberingDoc.getNumbering();
                this.ctNumbering = numbering;
                CTNum[] arr$ = numbering.getNumArray();
                for (CTNum ctNum : arr$) {
                    this.nums.add(new XWPFNum(ctNum, this));
                }
                CTAbstractNum[] arr$2 = this.ctNumbering.getAbstractNumArray();
                for (CTAbstractNum ctAbstractNum : arr$2) {
                    this.abstractNums.add(new XWPFAbstractNum(ctAbstractNum, this));
                }
                this.isNew = false;
            } catch (XmlException e) {
                throw new POIXMLException();
            }
        } finally {
            is.close();
        }
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void commit() throws IOException {
        XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTNumbering.type.getName().getNamespaceURI(), "numbering"));
        PackagePart part = getPackagePart();
        OutputStream out = part.getOutputStream();
        this.ctNumbering.save(out, xmlOptions);
        out.close();
    }

    public void setNumbering(CTNumbering numbering) {
        this.ctNumbering = numbering;
    }

    public boolean numExist(BigInteger numID) {
        for (XWPFNum num : this.nums) {
            if (num.getCTNum().getNumId().equals(numID)) {
                return true;
            }
        }
        return false;
    }

    public BigInteger addNum(XWPFNum num) {
        this.ctNumbering.addNewNum();
        int pos = this.ctNumbering.sizeOfNumArray() - 1;
        this.ctNumbering.setNumArray(pos, num.getCTNum());
        this.nums.add(num);
        return num.getCTNum().getNumId();
    }

    public BigInteger addNum(BigInteger abstractNumID) {
        CTNum ctNum = this.ctNumbering.addNewNum();
        ctNum.addNewAbstractNumId();
        ctNum.getAbstractNumId().setVal(abstractNumID);
        ctNum.setNumId(BigInteger.valueOf(this.nums.size() + 1));
        XWPFNum num = new XWPFNum(ctNum, this);
        this.nums.add(num);
        return ctNum.getNumId();
    }

    public void addNum(BigInteger abstractNumID, BigInteger numID) {
        CTNum ctNum = this.ctNumbering.addNewNum();
        ctNum.addNewAbstractNumId();
        ctNum.getAbstractNumId().setVal(abstractNumID);
        ctNum.setNumId(numID);
        XWPFNum num = new XWPFNum(ctNum, this);
        this.nums.add(num);
    }

    public XWPFNum getNum(BigInteger numID) {
        for (XWPFNum num : this.nums) {
            if (num.getCTNum().getNumId().equals(numID)) {
                return num;
            }
        }
        return null;
    }

    public XWPFAbstractNum getAbstractNum(BigInteger abstractNumID) {
        for (XWPFAbstractNum abstractNum : this.abstractNums) {
            if (abstractNum.getAbstractNum().getAbstractNumId().equals(abstractNumID)) {
                return abstractNum;
            }
        }
        return null;
    }

    public BigInteger getIdOfAbstractNum(XWPFAbstractNum abstractNum) {
        CTAbstractNum copy = abstractNum.getCTAbstractNum().copy();
        XWPFAbstractNum newAbstractNum = new XWPFAbstractNum(copy, this);
        for (int i = 0; i < this.abstractNums.size(); i++) {
            newAbstractNum.getCTAbstractNum().setAbstractNumId(BigInteger.valueOf(i));
            newAbstractNum.setNumbering(this);
            if (newAbstractNum.getCTAbstractNum().valueEquals(this.abstractNums.get(i).getCTAbstractNum())) {
                return newAbstractNum.getCTAbstractNum().getAbstractNumId();
            }
        }
        return null;
    }

    public BigInteger addAbstractNum(XWPFAbstractNum abstractNum) {
        int pos = this.abstractNums.size();
        if (abstractNum.getAbstractNum() != null) {
            this.ctNumbering.addNewAbstractNum().set(abstractNum.getAbstractNum());
        } else {
            this.ctNumbering.addNewAbstractNum();
            abstractNum.getAbstractNum().setAbstractNumId(BigInteger.valueOf(pos));
            this.ctNumbering.setAbstractNumArray(pos, abstractNum.getAbstractNum());
        }
        this.abstractNums.add(abstractNum);
        return abstractNum.getCTAbstractNum().getAbstractNumId();
    }

    public boolean removeAbstractNum(BigInteger abstractNumID) {
        if (abstractNumID.byteValue() < this.abstractNums.size()) {
            this.ctNumbering.removeAbstractNum(abstractNumID.byteValue());
            this.abstractNums.remove(abstractNumID.byteValue());
            return true;
        }
        return false;
    }

    public BigInteger getAbstractNumID(BigInteger numID) {
        XWPFNum num = getNum(numID);
        if (num == null || num.getCTNum() == null || num.getCTNum().getAbstractNumId() == null) {
            return null;
        }
        return num.getCTNum().getAbstractNumId().getVal();
    }
}
