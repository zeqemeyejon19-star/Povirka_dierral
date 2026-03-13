package org.apache.poi.openxml4j.opc.internal;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.openxml4j.opc.ContentTypes;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageProperties;
import org.apache.poi.openxml4j.util.Nullable;
import org.apache.poi.util.LocaleUtil;

/* JADX INFO: loaded from: classes.dex */
public final class PackagePropertiesPart extends PackagePart implements PackageProperties {
    public static final String NAMESPACE_CP_URI = "http://schemas.openxmlformats.org/package/2006/metadata/core-properties";
    public static final String NAMESPACE_DCTERMS_URI = "http://purl.org/dc/terms/";
    public static final String NAMESPACE_DC_URI = "http://purl.org/dc/elements/1.1/";
    private final Pattern TIME_ZONE_PAT;
    private final String[] TZ_DATE_FORMATS;
    protected Nullable<String> category;
    protected Nullable<String> contentStatus;
    protected Nullable<String> contentType;
    protected Nullable<Date> created;
    protected Nullable<String> creator;
    protected Nullable<String> description;
    protected Nullable<String> identifier;
    protected Nullable<String> keywords;
    protected Nullable<String> language;
    protected Nullable<String> lastModifiedBy;
    protected Nullable<Date> lastPrinted;
    protected Nullable<Date> modified;
    protected Nullable<String> revision;
    protected Nullable<String> subject;
    protected Nullable<String> title;
    protected Nullable<String> version;
    private static final String DEFAULT_DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String[] DATE_FORMATS = {DEFAULT_DATEFORMAT, "yyyy-MM-dd'T'HH:mm:ss.SS'Z'", "yyyy-MM-dd"};

    public PackagePropertiesPart(OPCPackage pack, PackagePartName partName) throws InvalidFormatException {
        super(pack, partName, ContentTypes.CORE_PROPERTIES_PART);
        this.TZ_DATE_FORMATS = new String[]{"yyyy-MM-dd'T'HH:mm:ssz", "yyyy-MM-dd'T'HH:mm:ss.Sz", "yyyy-MM-dd'T'HH:mm:ss.SSz", "yyyy-MM-dd'T'HH:mm:ss.SSSz"};
        this.TIME_ZONE_PAT = Pattern.compile("([-+]\\d\\d):?(\\d\\d)");
        this.category = new Nullable<>();
        this.contentStatus = new Nullable<>();
        this.contentType = new Nullable<>();
        this.created = new Nullable<>();
        this.creator = new Nullable<>();
        this.description = new Nullable<>();
        this.identifier = new Nullable<>();
        this.keywords = new Nullable<>();
        this.language = new Nullable<>();
        this.lastModifiedBy = new Nullable<>();
        this.lastPrinted = new Nullable<>();
        this.modified = new Nullable<>();
        this.revision = new Nullable<>();
        this.subject = new Nullable<>();
        this.title = new Nullable<>();
        this.version = new Nullable<>();
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public Nullable<String> getCategoryProperty() {
        return this.category;
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public Nullable<String> getContentStatusProperty() {
        return this.contentStatus;
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public Nullable<String> getContentTypeProperty() {
        return this.contentType;
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public Nullable<Date> getCreatedProperty() {
        return this.created;
    }

    public String getCreatedPropertyString() {
        return getDateValue(this.created);
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public Nullable<String> getCreatorProperty() {
        return this.creator;
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public Nullable<String> getDescriptionProperty() {
        return this.description;
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public Nullable<String> getIdentifierProperty() {
        return this.identifier;
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public Nullable<String> getKeywordsProperty() {
        return this.keywords;
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public Nullable<String> getLanguageProperty() {
        return this.language;
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public Nullable<String> getLastModifiedByProperty() {
        return this.lastModifiedBy;
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public Nullable<Date> getLastPrintedProperty() {
        return this.lastPrinted;
    }

    public String getLastPrintedPropertyString() {
        return getDateValue(this.lastPrinted);
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public Nullable<Date> getModifiedProperty() {
        return this.modified;
    }

    public String getModifiedPropertyString() {
        if (this.modified.hasValue()) {
            return getDateValue(this.modified);
        }
        return getDateValue(new Nullable<>(new Date()));
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public Nullable<String> getRevisionProperty() {
        return this.revision;
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public Nullable<String> getSubjectProperty() {
        return this.subject;
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public Nullable<String> getTitleProperty() {
        return this.title;
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public Nullable<String> getVersionProperty() {
        return this.version;
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public void setCategoryProperty(String category) {
        this.category = setStringValue(category);
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public void setContentStatusProperty(String contentStatus) {
        this.contentStatus = setStringValue(contentStatus);
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public void setContentTypeProperty(String contentType) {
        this.contentType = setStringValue(contentType);
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public void setCreatedProperty(String created) {
        try {
            this.created = setDateValue(created);
        } catch (InvalidFormatException e) {
            throw new IllegalArgumentException("Date for created could not be parsed: " + created, e);
        }
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public void setCreatedProperty(Nullable<Date> created) {
        if (created.hasValue()) {
            this.created = created;
        }
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public void setCreatorProperty(String creator) {
        this.creator = setStringValue(creator);
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public void setDescriptionProperty(String description) {
        this.description = setStringValue(description);
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public void setIdentifierProperty(String identifier) {
        this.identifier = setStringValue(identifier);
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public void setKeywordsProperty(String keywords) {
        this.keywords = setStringValue(keywords);
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public void setLanguageProperty(String language) {
        this.language = setStringValue(language);
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public void setLastModifiedByProperty(String lastModifiedBy) {
        this.lastModifiedBy = setStringValue(lastModifiedBy);
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public void setLastPrintedProperty(String lastPrinted) {
        try {
            this.lastPrinted = setDateValue(lastPrinted);
        } catch (InvalidFormatException e) {
            throw new IllegalArgumentException("lastPrinted  : " + e.getLocalizedMessage(), e);
        }
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public void setLastPrintedProperty(Nullable<Date> lastPrinted) {
        if (lastPrinted.hasValue()) {
            this.lastPrinted = lastPrinted;
        }
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public void setModifiedProperty(String modified) {
        try {
            this.modified = setDateValue(modified);
        } catch (InvalidFormatException e) {
            throw new IllegalArgumentException("modified  : " + e.getLocalizedMessage(), e);
        }
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public void setModifiedProperty(Nullable<Date> modified) {
        if (modified.hasValue()) {
            this.modified = modified;
        }
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public void setRevisionProperty(String revision) {
        this.revision = setStringValue(revision);
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public void setSubjectProperty(String subject) {
        this.subject = setStringValue(subject);
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public void setTitleProperty(String title) {
        this.title = setStringValue(title);
    }

    @Override // org.apache.poi.openxml4j.opc.PackageProperties
    public void setVersionProperty(String version) {
        this.version = setStringValue(version);
    }

    private Nullable<String> setStringValue(String s) {
        if (s == null || s.equals("")) {
            return new Nullable<>();
        }
        return new Nullable<>(s);
    }

    private Nullable<Date> setDateValue(String dateStr) throws InvalidFormatException {
        if (dateStr == null || dateStr.equals("")) {
            return new Nullable<>();
        }
        Matcher m = this.TIME_ZONE_PAT.matcher(dateStr);
        if (m.find()) {
            String dateTzStr = dateStr.substring(0, m.start()) + m.group(1) + m.group(2);
            String[] arr$ = this.TZ_DATE_FORMATS;
            for (String fStr : arr$) {
                SimpleDateFormat df = new SimpleDateFormat(fStr, Locale.ROOT);
                df.setTimeZone(LocaleUtil.TIMEZONE_UTC);
                Date d = df.parse(dateTzStr, new ParsePosition(0));
                if (d != null) {
                    return new Nullable<>(d);
                }
            }
        }
        String dateTzStr2 = dateStr.endsWith("Z") ? dateStr : dateStr + "Z";
        String[] arr$2 = DATE_FORMATS;
        for (String fStr2 : arr$2) {
            SimpleDateFormat df2 = new SimpleDateFormat(fStr2, Locale.ROOT);
            df2.setTimeZone(LocaleUtil.TIMEZONE_UTC);
            Date d2 = df2.parse(dateTzStr2, new ParsePosition(0));
            if (d2 != null) {
                return new Nullable<>(d2);
            }
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        String[] arr$3 = this.TZ_DATE_FORMATS;
        int len$ = arr$3.length;
        int i$ = 0;
        while (i$ < len$) {
            String fStr3 = arr$3[i$];
            int i2 = i + 1;
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(fStr3);
            i$++;
            i = i2;
        }
        String[] arr$4 = DATE_FORMATS;
        for (String fStr4 : arr$4) {
            sb.append(", ").append(fStr4);
        }
        throw new InvalidFormatException("Date " + dateStr + " not well formatted, expected format in: " + ((Object) sb));
    }

    private String getDateValue(Nullable<Date> d) {
        Date date;
        if (d == null || (date = d.getValue()) == null) {
            return "";
        }
        SimpleDateFormat df = new SimpleDateFormat(DEFAULT_DATEFORMAT, Locale.ROOT);
        df.setTimeZone(LocaleUtil.TIMEZONE_UTC);
        return df.format(date);
    }

    @Override // org.apache.poi.openxml4j.opc.PackagePart
    protected InputStream getInputStreamImpl() {
        throw new InvalidOperationException("Operation not authorized. This part may only be manipulated using the getters and setters on PackagePropertiesPart");
    }

    @Override // org.apache.poi.openxml4j.opc.PackagePart
    protected OutputStream getOutputStreamImpl() {
        throw new InvalidOperationException("Can't use output stream to set properties !");
    }

    @Override // org.apache.poi.openxml4j.opc.PackagePart
    public boolean save(OutputStream zos) {
        throw new InvalidOperationException("Operation not authorized. This part may only be manipulated using the getters and setters on PackagePropertiesPart");
    }

    @Override // org.apache.poi.openxml4j.opc.PackagePart
    public boolean load(InputStream ios) {
        throw new InvalidOperationException("Operation not authorized. This part may only be manipulated using the getters and setters on PackagePropertiesPart");
    }

    @Override // org.apache.poi.openxml4j.opc.PackagePart
    public void close() {
    }

    @Override // org.apache.poi.openxml4j.opc.PackagePart
    public void flush() {
    }
}
