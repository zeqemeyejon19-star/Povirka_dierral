package org.apache.poi.xdgf.usermodel;

import com.microsoft.schemas.office.visio.x2012.main.PageType;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.apache.poi.POIXMLException;
import org.apache.poi.util.Internal;
import org.apache.poi.xdgf.geom.Dimension2dDouble;

/* JADX INFO: loaded from: classes.dex */
public class XDGFPage {
    protected XDGFPageContents _content;
    private PageType _page;
    protected XDGFSheet _pageSheet;
    protected XDGFPages _pages;

    public XDGFPage(PageType page, XDGFPageContents content, XDGFDocument document, XDGFPages pages) {
        this._pageSheet = null;
        this._page = page;
        this._content = content;
        this._pages = pages;
        content.setPage(this);
        if (page.isSetPageSheet()) {
            this._pageSheet = new XDGFPageSheet(page.getPageSheet(), document);
        }
    }

    @Internal
    protected PageType getXmlObject() {
        return this._page;
    }

    public long getID() {
        return this._page.getID();
    }

    public String getName() {
        return this._page.getName();
    }

    public XDGFPageContents getContent() {
        return this._content;
    }

    public XDGFSheet getPageSheet() {
        return this._pageSheet;
    }

    public long getPageNumber() {
        return this._pages.getPageList().indexOf(this) + 1;
    }

    public Dimension2dDouble getPageSize() {
        XDGFCell w = this._pageSheet.getCell("PageWidth");
        XDGFCell h = this._pageSheet.getCell("PageHeight");
        if (w == null || h == null) {
            throw new POIXMLException("Cannot determine page size");
        }
        return new Dimension2dDouble(Double.parseDouble(w.getValue()), Double.parseDouble(h.getValue()));
    }

    public Point2D.Double getPageOffset() {
        XDGFCell xoffcell = this._pageSheet.getCell("XRulerOrigin");
        XDGFCell yoffcell = this._pageSheet.getCell("YRulerOrigin");
        double xoffset = 0.0d;
        double yoffset = 0.0d;
        if (xoffcell != null) {
            xoffset = Double.parseDouble(xoffcell.getValue());
        }
        if (xoffcell != null) {
            yoffset = Double.parseDouble(yoffcell.getValue());
        }
        return new Point2D.Double(xoffset, yoffset);
    }

    public Rectangle2D getBoundingBox() {
        Dimension2dDouble sz = getPageSize();
        Point2D.Double offset = getPageOffset();
        return new Rectangle2D.Double(-offset.getX(), -offset.getY(), sz.getWidth(), sz.getHeight());
    }
}
