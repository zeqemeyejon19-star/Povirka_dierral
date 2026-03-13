package org.apache.poi.poifs.filesystem;

import java.io.File;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public class POIFSDocumentPath {
    private static final POILogger log = POILogFactory.getLogger((Class<?>) POIFSDocumentPath.class);
    private final String[] components;
    private int hashcode;

    public POIFSDocumentPath(String[] components) throws IllegalArgumentException {
        this.hashcode = 0;
        if (components == null) {
            this.components = new String[0];
            return;
        }
        this.components = new String[components.length];
        for (int j = 0; j < components.length; j++) {
            if (components[j] == null || components[j].length() == 0) {
                throw new IllegalArgumentException("components cannot contain null or empty strings");
            }
            this.components[j] = components[j];
        }
    }

    public POIFSDocumentPath() {
        this.hashcode = 0;
        this.components = new String[0];
    }

    public POIFSDocumentPath(POIFSDocumentPath path, String[] components) throws IllegalArgumentException {
        this.hashcode = 0;
        if (components == null) {
            this.components = new String[path.components.length];
        } else {
            this.components = new String[path.components.length + components.length];
        }
        int j = 0;
        while (true) {
            String[] strArr = path.components;
            if (j >= strArr.length) {
                break;
            }
            this.components[j] = strArr[j];
            j++;
        }
        if (components != null) {
            for (int j2 = 0; j2 < components.length; j2++) {
                if (components[j2] == null) {
                    throw new IllegalArgumentException("components cannot contain null");
                }
                if (components[j2].length() == 0) {
                    log.log(5, "Directory under " + path + " has an empty name, not all OLE2 readers will handle this file correctly!");
                }
                this.components[path.components.length + j2] = components[j2];
            }
        }
    }

    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        if (this == o) {
            return true;
        }
        POIFSDocumentPath path = (POIFSDocumentPath) o;
        if (path.components.length != this.components.length) {
            return false;
        }
        int j = 0;
        while (true) {
            String[] strArr = this.components;
            if (j >= strArr.length) {
                return true;
            }
            if (path.components[j].equals(strArr[j])) {
                j++;
            } else {
                return false;
            }
        }
    }

    public int hashCode() {
        if (this.hashcode == 0) {
            this.hashcode = computeHashCode();
        }
        return this.hashcode;
    }

    private int computeHashCode() {
        int code = 0;
        int j = 0;
        while (true) {
            String[] strArr = this.components;
            if (j < strArr.length) {
                code += strArr[j].hashCode();
                j++;
            } else {
                return code;
            }
        }
    }

    public int length() {
        return this.components.length;
    }

    public String getComponent(int n) throws ArrayIndexOutOfBoundsException {
        return this.components[n];
    }

    public POIFSDocumentPath getParent() {
        String[] strArr = this.components;
        int length = strArr.length - 1;
        if (length < 0) {
            return null;
        }
        String[] parentComponents = new String[length];
        System.arraycopy(strArr, 0, parentComponents, 0, length);
        POIFSDocumentPath parent = new POIFSDocumentPath(parentComponents);
        return parent;
    }

    public String getName() {
        String[] strArr = this.components;
        if (strArr.length == 0) {
            return "";
        }
        return strArr[strArr.length - 1];
    }

    public String toString() {
        StringBuffer b = new StringBuffer();
        int l = length();
        b.append(File.separatorChar);
        for (int i = 0; i < l; i++) {
            b.append(getComponent(i));
            if (i < l - 1) {
                b.append(File.separatorChar);
            }
        }
        return b.toString();
    }
}
