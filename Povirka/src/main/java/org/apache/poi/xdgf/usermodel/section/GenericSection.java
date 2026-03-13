package org.apache.poi.xdgf.usermodel.section;

import com.microsoft.schemas.office.visio.x2012.main.SectionType;
import org.apache.poi.xdgf.usermodel.XDGFSheet;

/* JADX INFO: loaded from: classes.dex */
public class GenericSection extends XDGFSection {
    public GenericSection(SectionType section, XDGFSheet containingSheet) {
        super(section, containingSheet);
    }

    @Override // org.apache.poi.xdgf.usermodel.section.XDGFSection
    public void setupMaster(XDGFSection section) {
    }
}
