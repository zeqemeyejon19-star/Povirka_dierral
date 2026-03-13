package org.apache.poi.openxml4j.opc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;

/* JADX INFO: loaded from: classes.dex */
public final class PackagePartCollection implements Serializable {
    private static final long serialVersionUID = 2515031135957635517L;
    private HashSet<String> registerPartNameStr = new HashSet<>();
    private final HashMap<PackagePartName, PackagePart> packagePartLookup = new HashMap<>();

    public PackagePart put(PackagePartName partName, PackagePart part) {
        String[] segments = partName.getURI().toASCIIString().split(PackagingURIHelper.FORWARD_SLASH_STRING);
        StringBuilder concatSeg = new StringBuilder();
        for (String seg : segments) {
            if (!seg.equals("")) {
                concatSeg.append(PackagingURIHelper.FORWARD_SLASH_CHAR);
            }
            concatSeg.append(seg);
            if (this.registerPartNameStr.contains(concatSeg.toString())) {
                throw new InvalidOperationException("You can't add a part with a part name derived from another part ! [M1.11]");
            }
        }
        this.registerPartNameStr.add(partName.getName());
        return this.packagePartLookup.put(partName, part);
    }

    public PackagePart remove(PackagePartName key) {
        this.registerPartNameStr.remove(key.getName());
        return this.packagePartLookup.remove(key);
    }

    public Collection<PackagePart> sortedValues() {
        ArrayList<PackagePart> packageParts = new ArrayList<>(this.packagePartLookup.values());
        Collections.sort(packageParts);
        return packageParts;
    }

    public boolean containsKey(PackagePartName partName) {
        return this.packagePartLookup.containsKey(partName);
    }

    public PackagePart get(PackagePartName partName) {
        return this.packagePartLookup.get(partName);
    }

    public int size() {
        return this.packagePartLookup.size();
    }
}
