package org.apache.poi.poifs.filesystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.hpsf.ClassID;
import org.apache.poi.poifs.dev.POIFSViewable;
import org.apache.poi.poifs.property.DirectoryProperty;
import org.apache.poi.poifs.property.DocumentProperty;
import org.apache.poi.poifs.property.Property;

/* JADX INFO: loaded from: classes.dex */
public class DirectoryNode extends EntryNode implements DirectoryEntry, POIFSViewable, Iterable<Entry> {
    private Map<String, Entry> _byname;
    private ArrayList<Entry> _entries;
    private NPOIFSFileSystem _nfilesystem;
    private OPOIFSFileSystem _ofilesystem;
    private POIFSDocumentPath _path;

    DirectoryNode(DirectoryProperty property, OPOIFSFileSystem filesystem, DirectoryNode parent) {
        this(property, parent, filesystem, (NPOIFSFileSystem) null);
    }

    DirectoryNode(DirectoryProperty property, NPOIFSFileSystem nfilesystem, DirectoryNode parent) {
        this(property, parent, (OPOIFSFileSystem) null, nfilesystem);
    }

    private DirectoryNode(DirectoryProperty property, DirectoryNode parent, OPOIFSFileSystem ofilesystem, NPOIFSFileSystem nfilesystem) {
        Entry childNode;
        super(property, parent);
        this._ofilesystem = ofilesystem;
        this._nfilesystem = nfilesystem;
        if (parent == null) {
            this._path = new POIFSDocumentPath();
        } else {
            this._path = new POIFSDocumentPath(parent._path, new String[]{property.getName()});
        }
        this._byname = new HashMap();
        this._entries = new ArrayList<>();
        Iterator<Property> iter = property.getChildren();
        while (iter.hasNext()) {
            Property child = iter.next();
            if (child.isDirectory()) {
                DirectoryProperty childDir = (DirectoryProperty) child;
                if (this._ofilesystem != null) {
                    childNode = new DirectoryNode(childDir, this._ofilesystem, this);
                } else {
                    childNode = new DirectoryNode(childDir, this._nfilesystem, this);
                }
            } else {
                childNode = new DocumentNode((DocumentProperty) child, this);
            }
            this._entries.add(childNode);
            this._byname.put(childNode.getName(), childNode);
        }
    }

    public POIFSDocumentPath getPath() {
        return this._path;
    }

    public NPOIFSFileSystem getFileSystem() {
        return this._nfilesystem;
    }

    public OPOIFSFileSystem getOFileSystem() {
        return this._ofilesystem;
    }

    public NPOIFSFileSystem getNFileSystem() {
        return this._nfilesystem;
    }

    public DocumentInputStream createDocumentInputStream(String documentName) throws IOException {
        return createDocumentInputStream(getEntry(documentName));
    }

    public DocumentInputStream createDocumentInputStream(Entry document) throws IOException {
        if (!document.isDocumentEntry()) {
            throw new IOException("Entry '" + document.getName() + "' is not a DocumentEntry");
        }
        DocumentEntry entry = (DocumentEntry) document;
        return new DocumentInputStream(entry);
    }

    DocumentEntry createDocument(OPOIFSDocument document) throws IOException {
        DocumentProperty property = document.getDocumentProperty();
        DocumentNode rval = new DocumentNode(property, this);
        ((DirectoryProperty) getProperty()).addChild(property);
        this._ofilesystem.addDocument(document);
        this._entries.add(rval);
        this._byname.put(property.getName(), rval);
        return rval;
    }

    DocumentEntry createDocument(NPOIFSDocument document) throws IOException {
        DocumentProperty property = document.getDocumentProperty();
        DocumentNode rval = new DocumentNode(property, this);
        ((DirectoryProperty) getProperty()).addChild(property);
        this._nfilesystem.addDocument(document);
        this._entries.add(rval);
        this._byname.put(property.getName(), rval);
        return rval;
    }

    boolean changeName(String oldName, String newName) {
        boolean rval = false;
        EntryNode child = (EntryNode) this._byname.get(oldName);
        if (child != null && (rval = ((DirectoryProperty) getProperty()).changeName(child.getProperty(), newName))) {
            this._byname.remove(oldName);
            this._byname.put(child.getProperty().getName(), child);
        }
        return rval;
    }

    boolean deleteEntry(EntryNode entry) {
        boolean rval = ((DirectoryProperty) getProperty()).deleteChild(entry.getProperty());
        if (rval) {
            this._entries.remove(entry);
            this._byname.remove(entry.getName());
            OPOIFSFileSystem oPOIFSFileSystem = this._ofilesystem;
            if (oPOIFSFileSystem != null) {
                oPOIFSFileSystem.remove(entry);
            } else {
                try {
                    this._nfilesystem.remove(entry);
                } catch (IOException e) {
                }
            }
        }
        return rval;
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public Iterator<Entry> getEntries() {
        return this._entries.iterator();
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public Set<String> getEntryNames() {
        return this._byname.keySet();
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public boolean isEmpty() {
        return this._entries.isEmpty();
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public int getEntryCount() {
        return this._entries.size();
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public boolean hasEntry(String name) {
        return name != null && this._byname.containsKey(name);
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public Entry getEntry(String name) throws FileNotFoundException {
        Entry rval = null;
        if (name != null) {
            Entry rval2 = this._byname.get(name);
            rval = rval2;
        }
        if (rval == null) {
            throw new FileNotFoundException("no such entry: \"" + name + "\", had: " + this._byname.keySet());
        }
        return rval;
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public DocumentEntry createDocument(String name, InputStream stream) throws IOException {
        if (this._nfilesystem != null) {
            return createDocument(new NPOIFSDocument(name, this._nfilesystem, stream));
        }
        return createDocument(new OPOIFSDocument(name, stream));
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public DocumentEntry createDocument(String name, int size, POIFSWriterListener writer) throws IOException {
        if (this._nfilesystem != null) {
            return createDocument(new NPOIFSDocument(name, size, this._nfilesystem, writer));
        }
        return createDocument(new OPOIFSDocument(name, size, this._path, writer));
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public DirectoryEntry createDirectory(String name) throws IOException {
        DirectoryNode rval;
        DirectoryProperty property = new DirectoryProperty(name);
        if (this._ofilesystem != null) {
            rval = new DirectoryNode(property, this._ofilesystem, this);
            this._ofilesystem.addDirectory(property);
        } else {
            rval = new DirectoryNode(property, this._nfilesystem, this);
            this._nfilesystem.addDirectory(property);
        }
        ((DirectoryProperty) getProperty()).addChild(property);
        this._entries.add(rval);
        this._byname.put(name, rval);
        return rval;
    }

    public DocumentEntry createOrUpdateDocument(String name, InputStream stream) throws IOException {
        if (!hasEntry(name)) {
            return createDocument(name, stream);
        }
        DocumentNode existing = (DocumentNode) getEntry(name);
        if (this._nfilesystem != null) {
            NPOIFSDocument nDoc = new NPOIFSDocument(existing);
            nDoc.replaceContents(stream);
            return existing;
        }
        deleteEntry(existing);
        return createDocument(name, stream);
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public ClassID getStorageClsid() {
        return getProperty().getStorageClsid();
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public void setStorageClsid(ClassID clsidStorage) {
        getProperty().setStorageClsid(clsidStorage);
    }

    @Override // org.apache.poi.poifs.filesystem.EntryNode, org.apache.poi.poifs.filesystem.Entry
    public boolean isDirectoryEntry() {
        return true;
    }

    @Override // org.apache.poi.poifs.filesystem.EntryNode
    protected boolean isDeleteOK() {
        return isEmpty();
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public Object[] getViewableArray() {
        return new Object[0];
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public Iterator<Object> getViewableIterator() {
        List<Object> components = new ArrayList<>();
        components.add(getProperty());
        Iterator<Entry> iter = this._entries.iterator();
        while (iter.hasNext()) {
            components.add(iter.next());
        }
        return components.iterator();
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public boolean preferArray() {
        return false;
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public String getShortDescription() {
        return getName();
    }

    @Override // java.lang.Iterable
    public Iterator<Entry> iterator() {
        return getEntries();
    }
}
