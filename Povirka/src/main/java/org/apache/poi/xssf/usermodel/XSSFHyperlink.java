package org.apache.poi.xssf.usermodel;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHyperlink;

/* JADX INFO: loaded from: classes.dex */
public class XSSFHyperlink implements Hyperlink {
    private final CTHyperlink _ctHyperlink;
    private final PackageRelationship _externalRel;
    private String _location;
    private final HyperlinkType _type;

    protected XSSFHyperlink(HyperlinkType type) {
        this._type = type;
        this._ctHyperlink = CTHyperlink.Factory.newInstance();
        this._externalRel = null;
    }

    protected XSSFHyperlink(CTHyperlink ctHyperlink, PackageRelationship hyperlinkRel) {
        this._ctHyperlink = ctHyperlink;
        this._externalRel = hyperlinkRel;
        if (hyperlinkRel == null) {
            if (ctHyperlink.getLocation() != null) {
                this._type = HyperlinkType.DOCUMENT;
                this._location = ctHyperlink.getLocation();
                return;
            } else {
                if (ctHyperlink.getId() != null) {
                    throw new IllegalStateException("The hyperlink for cell " + ctHyperlink.getRef() + " references relation " + ctHyperlink.getId() + ", but that didn't exist!");
                }
                this._type = HyperlinkType.DOCUMENT;
                return;
            }
        }
        URI target = hyperlinkRel.getTargetURI();
        this._location = target.toString();
        if (ctHyperlink.getLocation() != null) {
            this._location += "#" + ctHyperlink.getLocation();
        }
        if (this._location.startsWith("http://") || this._location.startsWith("https://") || this._location.startsWith("ftp://")) {
            this._type = HyperlinkType.URL;
        } else if (this._location.startsWith("mailto:")) {
            this._type = HyperlinkType.EMAIL;
        } else {
            this._type = HyperlinkType.FILE;
        }
    }

    @Internal
    public XSSFHyperlink(Hyperlink other) {
        if (other instanceof XSSFHyperlink) {
            XSSFHyperlink xlink = (XSSFHyperlink) other;
            this._type = xlink.getTypeEnum();
            this._location = xlink._location;
            this._externalRel = xlink._externalRel;
            this._ctHyperlink = xlink._ctHyperlink.copy();
            return;
        }
        this._type = other.getTypeEnum();
        this._location = other.getAddress();
        this._externalRel = null;
        this._ctHyperlink = CTHyperlink.Factory.newInstance();
        setCellReference(new CellReference(other.getFirstRow(), other.getFirstColumn()));
    }

    @Internal
    public CTHyperlink getCTHyperlink() {
        return this._ctHyperlink;
    }

    public boolean needsRelationToo() {
        return this._type != HyperlinkType.DOCUMENT;
    }

    protected void generateRelationIfNeeded(PackagePart sheetPart) {
        if (this._externalRel == null && needsRelationToo()) {
            PackageRelationship rel = sheetPart.addExternalRelationship(this._location, XSSFRelation.SHEET_HYPERLINKS.getRelation());
            this._ctHyperlink.setId(rel.getId());
        }
    }

    @Override // org.apache.poi.common.usermodel.Hyperlink
    public int getType() {
        return this._type.getCode();
    }

    @Override // org.apache.poi.common.usermodel.Hyperlink
    public HyperlinkType getTypeEnum() {
        return this._type;
    }

    public String getCellRef() {
        return this._ctHyperlink.getRef();
    }

    @Override // org.apache.poi.common.usermodel.Hyperlink
    public String getAddress() {
        return this._location;
    }

    @Override // org.apache.poi.common.usermodel.Hyperlink
    public String getLabel() {
        return this._ctHyperlink.getDisplay();
    }

    public String getLocation() {
        return this._ctHyperlink.getLocation();
    }

    @Override // org.apache.poi.common.usermodel.Hyperlink
    public void setLabel(String label) {
        this._ctHyperlink.setDisplay(label);
    }

    public void setLocation(String location) {
        this._ctHyperlink.setLocation(location);
    }

    @Override // org.apache.poi.common.usermodel.Hyperlink
    public void setAddress(String address) {
        validate(address);
        this._location = address;
        if (this._type == HyperlinkType.DOCUMENT) {
            setLocation(address);
        }
    }

    /* JADX INFO: renamed from: org.apache.poi.xssf.usermodel.XSSFHyperlink$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$common$usermodel$HyperlinkType;

        static {
            int[] iArr = new int[HyperlinkType.values().length];
            $SwitchMap$org$apache$poi$common$usermodel$HyperlinkType = iArr;
            try {
                iArr[HyperlinkType.EMAIL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$common$usermodel$HyperlinkType[HyperlinkType.FILE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$common$usermodel$HyperlinkType[HyperlinkType.URL.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$common$usermodel$HyperlinkType[HyperlinkType.DOCUMENT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private void validate(String address) {
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$common$usermodel$HyperlinkType[this._type.ordinal()];
        if (i != 1 && i != 2 && i != 3) {
            if (i != 4) {
                throw new IllegalStateException("Invalid Hyperlink type: " + this._type);
            }
        } else {
            try {
                new URI(address);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Address of hyperlink must be a valid URI", e);
            }
        }
    }

    @Internal
    public void setCellReference(String ref) {
        this._ctHyperlink.setRef(ref);
    }

    @Internal
    public void setCellReference(CellReference ref) {
        setCellReference(ref.formatAsString());
    }

    private CellReference buildCellReference() {
        String ref = this._ctHyperlink.getRef();
        if (ref == null) {
            ref = "A1";
        }
        return new CellReference(ref);
    }

    @Override // org.apache.poi.ss.usermodel.Hyperlink
    public int getFirstColumn() {
        return buildCellReference().getCol();
    }

    @Override // org.apache.poi.ss.usermodel.Hyperlink
    public int getLastColumn() {
        return buildCellReference().getCol();
    }

    @Override // org.apache.poi.ss.usermodel.Hyperlink
    public int getFirstRow() {
        return buildCellReference().getRow();
    }

    @Override // org.apache.poi.ss.usermodel.Hyperlink
    public int getLastRow() {
        return buildCellReference().getRow();
    }

    @Override // org.apache.poi.ss.usermodel.Hyperlink
    public void setFirstColumn(int col) {
        setCellReference(new CellReference(getFirstRow(), col));
    }

    @Override // org.apache.poi.ss.usermodel.Hyperlink
    public void setLastColumn(int col) {
        setFirstColumn(col);
    }

    @Override // org.apache.poi.ss.usermodel.Hyperlink
    public void setFirstRow(int row) {
        setCellReference(new CellReference(row, getFirstColumn()));
    }

    @Override // org.apache.poi.ss.usermodel.Hyperlink
    public void setLastRow(int row) {
        setFirstRow(row);
    }

    public String getTooltip() {
        return this._ctHyperlink.getTooltip();
    }

    public void setTooltip(String text) {
        this._ctHyperlink.setTooltip(text);
    }
}
