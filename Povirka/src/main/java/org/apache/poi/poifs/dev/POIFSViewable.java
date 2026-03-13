package org.apache.poi.poifs.dev;

import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public interface POIFSViewable {
    String getShortDescription();

    Object[] getViewableArray();

    Iterator<Object> getViewableIterator();

    boolean preferArray();
}
