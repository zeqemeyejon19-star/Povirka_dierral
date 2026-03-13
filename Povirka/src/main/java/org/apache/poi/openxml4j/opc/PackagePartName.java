package org.apache.poi.openxml4j.opc;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JRuntimeException;

/* JADX INFO: loaded from: classes.dex */
public final class PackagePartName implements Comparable<PackagePartName> {
    private boolean isRelationship;
    private URI partNameURI;
    private static String[] RFC3986_PCHAR_SUB_DELIMS = {"!", "$", "&", "'", "(", ")", "*", "+", ",", ";", "="};
    private static String[] RFC3986_PCHAR_UNRESERVED_SUP = {"-", ".", "_", "~"};
    private static String[] RFC3986_PCHAR_AUTHORIZED_SUP = {":", "@"};

    PackagePartName(URI uri, boolean checkConformance) throws InvalidFormatException {
        if (checkConformance) {
            throwExceptionIfInvalidPartUri(uri);
        } else if (!PackagingURIHelper.PACKAGE_ROOT_URI.equals(uri)) {
            throw new OpenXML4JRuntimeException("OCP conformance must be check for ALL part name except special cases : ['/']");
        }
        this.partNameURI = uri;
        this.isRelationship = isRelationshipPartURI(uri);
    }

    PackagePartName(String partName, boolean checkConformance) throws InvalidFormatException {
        try {
            URI partURI = new URI(partName);
            if (checkConformance) {
                throwExceptionIfInvalidPartUri(partURI);
            } else if (!PackagingURIHelper.PACKAGE_ROOT_URI.equals(partURI)) {
                throw new OpenXML4JRuntimeException("OCP conformance must be check for ALL part name except special cases : ['/']");
            }
            this.partNameURI = partURI;
            this.isRelationship = isRelationshipPartURI(partURI);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("partName argmument is not a valid OPC part name !");
        }
    }

    private boolean isRelationshipPartURI(URI partUri) {
        if (partUri == null) {
            throw new IllegalArgumentException("partUri");
        }
        return partUri.getPath().matches("^.*/" + PackagingURIHelper.RELATIONSHIP_PART_SEGMENT_NAME + "/.*\\" + PackagingURIHelper.RELATIONSHIP_PART_EXTENSION_NAME + "$");
    }

    public boolean isRelationshipPartURI() {
        return this.isRelationship;
    }

    private static void throwExceptionIfInvalidPartUri(URI partUri) throws InvalidFormatException {
        if (partUri == null) {
            throw new IllegalArgumentException("partUri");
        }
        throwExceptionIfEmptyURI(partUri);
        throwExceptionIfAbsoluteUri(partUri);
        throwExceptionIfPartNameNotStartsWithForwardSlashChar(partUri);
        throwExceptionIfPartNameEndsWithForwardSlashChar(partUri);
        throwExceptionIfPartNameHaveInvalidSegments(partUri);
    }

    private static void throwExceptionIfEmptyURI(URI partURI) throws InvalidFormatException {
        if (partURI == null) {
            throw new IllegalArgumentException("partURI");
        }
        String uriPath = partURI.getPath();
        if (uriPath.length() == 0 || (uriPath.length() == 1 && uriPath.charAt(0) == PackagingURIHelper.FORWARD_SLASH_CHAR)) {
            throw new InvalidFormatException("A part name shall not be empty [M1.1]: " + partURI.getPath());
        }
    }

    private static void throwExceptionIfPartNameHaveInvalidSegments(URI partUri) throws InvalidFormatException {
        if (partUri == null) {
            throw new IllegalArgumentException("partUri");
        }
        String[] segments = partUri.toASCIIString().split("/");
        if (segments.length <= 1 || !segments[0].equals("")) {
            throw new InvalidFormatException("A part name shall not have empty segments [M1.3]: " + partUri.getPath());
        }
        for (int i = 1; i < segments.length; i++) {
            String seg = segments[i];
            if (seg == null || "".equals(seg)) {
                throw new InvalidFormatException("A part name shall not have empty segments [M1.3]: " + partUri.getPath());
            }
            if (seg.endsWith(".")) {
                throw new InvalidFormatException("A segment shall not end with a dot ('.') character [M1.9]: " + partUri.getPath());
            }
            if ("".equals(seg.replaceAll("\\\\.", ""))) {
                throw new InvalidFormatException("A segment shall include at least one non-dot character. [M1.10]: " + partUri.getPath());
            }
            checkPCharCompliance(seg);
        }
    }

    private static void checkPCharCompliance(String segment) throws InvalidFormatException {
        int length = segment.length();
        int i = 0;
        while (i < length) {
            char c = segment.charAt(i);
            boolean errorFlag = true;
            if ((c >= 'A' && c <= 'Z') || ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9'))) {
                errorFlag = false;
            } else {
                int j = 0;
                while (true) {
                    String[] strArr = RFC3986_PCHAR_UNRESERVED_SUP;
                    if (j >= strArr.length) {
                        break;
                    }
                    if (c != strArr[j].charAt(0)) {
                        j++;
                    } else {
                        errorFlag = false;
                        break;
                    }
                }
                int j2 = 0;
                while (errorFlag) {
                    String[] strArr2 = RFC3986_PCHAR_AUTHORIZED_SUP;
                    if (j2 >= strArr2.length) {
                        break;
                    }
                    if (c == strArr2[j2].charAt(0)) {
                        errorFlag = false;
                    }
                    j2++;
                }
                int j3 = 0;
                while (errorFlag) {
                    String[] strArr3 = RFC3986_PCHAR_SUB_DELIMS;
                    if (j3 >= strArr3.length) {
                        break;
                    }
                    if (c == strArr3[j3].charAt(0)) {
                        errorFlag = false;
                    }
                    j3++;
                }
            }
            if (errorFlag && c == '%') {
                if (length - i < 2) {
                    throw new InvalidFormatException("The segment " + segment + " contain invalid encoded character !");
                }
                errorFlag = false;
                char decodedChar = (char) Integer.parseInt(segment.substring(i + 1, i + 3), 16);
                i += 2;
                if (decodedChar == '/' || decodedChar == '\\') {
                    throw new InvalidFormatException("A segment shall not contain percent-encoded forward slash ('/'), or backward slash ('') characters. [M1.7]");
                }
                if ((decodedChar >= 'A' && decodedChar <= 'Z') || ((decodedChar >= 'a' && decodedChar <= 'z') || (decodedChar >= '0' && decodedChar <= '9'))) {
                    errorFlag = true;
                }
                int j4 = 0;
                while (true) {
                    if (errorFlag) {
                        break;
                    }
                    String[] strArr4 = RFC3986_PCHAR_UNRESERVED_SUP;
                    if (j4 >= strArr4.length) {
                        break;
                    }
                    if (c != strArr4[j4].charAt(0)) {
                        j4++;
                    } else {
                        errorFlag = true;
                        break;
                    }
                }
                if (errorFlag) {
                    throw new InvalidFormatException("A segment shall not contain percent-encoded unreserved characters. [M1.8]");
                }
            }
            if (!errorFlag) {
                i++;
            } else {
                throw new InvalidFormatException("A segment shall not hold any characters other than pchar characters. [M1.6]");
            }
        }
    }

    private static void throwExceptionIfPartNameNotStartsWithForwardSlashChar(URI partUri) throws InvalidFormatException {
        String uriPath = partUri.getPath();
        if (uriPath.length() > 0 && uriPath.charAt(0) != PackagingURIHelper.FORWARD_SLASH_CHAR) {
            throw new InvalidFormatException("A part name shall start with a forward slash ('/') character [M1.4]: " + partUri.getPath());
        }
    }

    private static void throwExceptionIfPartNameEndsWithForwardSlashChar(URI partUri) throws InvalidFormatException {
        String uriPath = partUri.getPath();
        if (uriPath.length() > 0 && uriPath.charAt(uriPath.length() - 1) == PackagingURIHelper.FORWARD_SLASH_CHAR) {
            throw new InvalidFormatException("A part name shall not have a forward slash as the last character [M1.5]: " + partUri.getPath());
        }
    }

    private static void throwExceptionIfAbsoluteUri(URI partUri) throws InvalidFormatException {
        if (partUri.isAbsolute()) {
            throw new InvalidFormatException("Absolute URI forbidden: " + partUri);
        }
    }

    @Override // java.lang.Comparable
    public int compareTo(PackagePartName other) {
        return compare(this, other);
    }

    public String getExtension() {
        int i;
        String fragment = this.partNameURI.getPath();
        if (fragment.length() > 0 && (i = fragment.lastIndexOf(".")) > -1) {
            return fragment.substring(i + 1);
        }
        return "";
    }

    public String getName() {
        return this.partNameURI.toASCIIString();
    }

    public boolean equals(Object other) {
        if (other instanceof PackagePartName) {
            return this.partNameURI.toASCIIString().toLowerCase(Locale.ROOT).equals(((PackagePartName) other).partNameURI.toASCIIString().toLowerCase(Locale.ROOT));
        }
        return false;
    }

    public int hashCode() {
        return this.partNameURI.toASCIIString().toLowerCase(Locale.ROOT).hashCode();
    }

    public String toString() {
        return getName();
    }

    public URI getURI() {
        return this.partNameURI;
    }

    public static int compare(PackagePartName obj1, PackagePartName obj2) {
        if (obj1 == null) {
            return obj2 == null ? 0 : -1;
        }
        if (obj2 == null) {
            return 1;
        }
        return compare(obj1.getURI().toASCIIString().toLowerCase(Locale.ROOT), obj2.getURI().toASCIIString().toLowerCase(Locale.ROOT));
    }

    public static int compare(String str1, String str2) {
        if (str1 == null) {
            return str2 == null ? 0 : -1;
        }
        if (str2 == null) {
            return 1;
        }
        int len1 = str1.length();
        int len2 = str2.length();
        int idx1 = 0;
        int idx2 = 0;
        while (idx1 < len1 && idx2 < len2) {
            int idx12 = idx1 + 1;
            char c1 = str1.charAt(idx1);
            int idx22 = idx2 + 1;
            char c2 = str2.charAt(idx2);
            if (Character.isDigit(c1) && Character.isDigit(c2)) {
                int beg1 = idx12 - 1;
                while (idx12 < len1 && Character.isDigit(str1.charAt(idx12))) {
                    idx12++;
                }
                int beg2 = idx22 - 1;
                while (idx22 < len2 && Character.isDigit(str2.charAt(idx22))) {
                    idx22++;
                }
                int cmp = new BigInteger(str1.substring(beg1, idx12)).compareTo(new BigInteger(str2.substring(beg2, idx22)));
                if (cmp != 0) {
                    return cmp;
                }
                idx1 = idx12;
                idx2 = idx22;
            } else if (c1 == c2) {
                idx1 = idx12;
                idx2 = idx22;
            } else {
                return c1 - c2;
            }
        }
        int idx13 = len1 - len2;
        return idx13;
    }
}
