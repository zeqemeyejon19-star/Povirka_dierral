package org.apache.poi.poifs.property;

/* JADX INFO: loaded from: classes.dex */
public interface Child {
    Child getNextChild();

    Child getPreviousChild();

    void setNextChild(Child child);

    void setPreviousChild(Child child);
}
