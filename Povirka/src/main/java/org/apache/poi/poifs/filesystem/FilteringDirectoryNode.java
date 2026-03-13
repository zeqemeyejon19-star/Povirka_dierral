package org.apache.poi.poifs.filesystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.hpsf.ClassID;

/* JADX INFO: loaded from: classes.dex */
public class FilteringDirectoryNode implements DirectoryEntry {
    private DirectoryEntry directory;
    private Set<String> excludes = new HashSet();
    private Map<String, List<String>> childExcludes = new HashMap();

    public FilteringDirectoryNode(DirectoryEntry directory, Collection<String> excludes) {
        this.directory = directory;
        for (String excl : excludes) {
            int splitAt = excl.indexOf(47);
            if (splitAt == -1) {
                this.excludes.add(excl);
            } else {
                String child = excl.substring(0, splitAt);
                String childExcl = excl.substring(splitAt + 1);
                if (!this.childExcludes.containsKey(child)) {
                    this.childExcludes.put(child, new ArrayList());
                }
                this.childExcludes.get(child).add(childExcl);
            }
        }
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public DirectoryEntry createDirectory(String name) throws IOException {
        return this.directory.createDirectory(name);
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public DocumentEntry createDocument(String name, InputStream stream) throws IOException {
        return this.directory.createDocument(name, stream);
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public DocumentEntry createDocument(String name, int size, POIFSWriterListener writer) throws IOException {
        return this.directory.createDocument(name, size, writer);
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public Iterator<Entry> getEntries() {
        return new FilteringIterator();
    }

    @Override // java.lang.Iterable
    public Iterator<Entry> iterator() {
        return getEntries();
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public int getEntryCount() {
        int size = this.directory.getEntryCount();
        for (String excl : this.excludes) {
            if (this.directory.hasEntry(excl)) {
                size--;
            }
        }
        return size;
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public Set<String> getEntryNames() {
        Set<String> names = new HashSet<>();
        for (String name : this.directory.getEntryNames()) {
            if (!this.excludes.contains(name)) {
                names.add(name);
            }
        }
        return names;
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public boolean isEmpty() {
        return getEntryCount() == 0;
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public boolean hasEntry(String name) {
        if (this.excludes.contains(name)) {
            return false;
        }
        return this.directory.hasEntry(name);
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public Entry getEntry(String name) throws FileNotFoundException {
        if (this.excludes.contains(name)) {
            throw new FileNotFoundException(name);
        }
        Entry entry = this.directory.getEntry(name);
        return wrapEntry(entry);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Entry wrapEntry(Entry entry) {
        String name = entry.getName();
        if (this.childExcludes.containsKey(name) && (entry instanceof DirectoryEntry)) {
            return new FilteringDirectoryNode((DirectoryEntry) entry, this.childExcludes.get(name));
        }
        return entry;
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public ClassID getStorageClsid() {
        return this.directory.getStorageClsid();
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public void setStorageClsid(ClassID clsidStorage) {
        this.directory.setStorageClsid(clsidStorage);
    }

    @Override // org.apache.poi.poifs.filesystem.Entry
    public boolean delete() {
        return this.directory.delete();
    }

    @Override // org.apache.poi.poifs.filesystem.Entry
    public boolean renameTo(String newName) {
        return this.directory.renameTo(newName);
    }

    @Override // org.apache.poi.poifs.filesystem.Entry
    public String getName() {
        return this.directory.getName();
    }

    @Override // org.apache.poi.poifs.filesystem.Entry
    public DirectoryEntry getParent() {
        return this.directory.getParent();
    }

    @Override // org.apache.poi.poifs.filesystem.Entry
    public boolean isDirectoryEntry() {
        return true;
    }

    @Override // org.apache.poi.poifs.filesystem.Entry
    public boolean isDocumentEntry() {
        return false;
    }

    private class FilteringIterator implements Iterator<Entry> {
        private Entry next;
        private Iterator<Entry> parent;

        private FilteringIterator() {
            this.parent = FilteringDirectoryNode.this.directory.getEntries();
            locateNext();
        }

        private void locateNext() {
            this.next = null;
            while (this.parent.hasNext() && this.next == null) {
                Entry e = this.parent.next();
                if (!FilteringDirectoryNode.this.excludes.contains(e.getName())) {
                    this.next = FilteringDirectoryNode.this.wrapEntry(e);
                }
            }
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.next != null;
        }

        @Override // java.util.Iterator
        public Entry next() {
            Entry e = this.next;
            locateNext();
            return e;
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException("Remove not supported");
        }
    }
}
