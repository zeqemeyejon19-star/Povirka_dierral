package org.apache.poi.poifs.filesystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.util.Internal;

/* JADX INFO: loaded from: classes.dex */
@Internal
public class EntryUtils {
    @Internal
    public static void copyNodeRecursively(Entry entry, DirectoryEntry target) throws IOException {
        if (entry.isDirectoryEntry()) {
            DirectoryEntry dirEntry = (DirectoryEntry) entry;
            DirectoryEntry newTarget = target.createDirectory(entry.getName());
            newTarget.setStorageClsid(dirEntry.getStorageClsid());
            Iterator<Entry> entries = dirEntry.getEntries();
            while (entries.hasNext()) {
                copyNodeRecursively(entries.next(), newTarget);
            }
            return;
        }
        DocumentEntry dentry = (DocumentEntry) entry;
        DocumentInputStream dstream = new DocumentInputStream(dentry);
        target.createDocument(dentry.getName(), dstream);
        dstream.close();
    }

    public static void copyNodes(DirectoryEntry sourceRoot, DirectoryEntry targetRoot) throws IOException {
        for (Entry entry : sourceRoot) {
            copyNodeRecursively(entry, targetRoot);
        }
    }

    public static void copyNodes(FilteringDirectoryNode filteredSource, FilteringDirectoryNode filteredTarget) throws IOException {
        copyNodes((DirectoryEntry) filteredSource, (DirectoryEntry) filteredTarget);
    }

    public static void copyNodes(OPOIFSFileSystem source, OPOIFSFileSystem target) throws IOException {
        copyNodes(source.getRoot(), target.getRoot());
    }

    public static void copyNodes(NPOIFSFileSystem source, NPOIFSFileSystem target) throws IOException {
        copyNodes(source.getRoot(), target.getRoot());
    }

    public static void copyNodes(OPOIFSFileSystem source, OPOIFSFileSystem target, List<String> excepts) throws IOException {
        copyNodes(new FilteringDirectoryNode(source.getRoot(), excepts), new FilteringDirectoryNode(target.getRoot(), excepts));
    }

    public static void copyNodes(NPOIFSFileSystem source, NPOIFSFileSystem target, List<String> excepts) throws IOException {
        copyNodes(new FilteringDirectoryNode(source.getRoot(), excepts), new FilteringDirectoryNode(target.getRoot(), excepts));
    }

    public static boolean areDirectoriesIdentical(DirectoryEntry dirA, DirectoryEntry dirB) {
        boolean match;
        int size;
        if (!dirA.getName().equals(dirB.getName()) || dirA.getEntryCount() != dirB.getEntryCount()) {
            return false;
        }
        Map<String, Integer> aSizes = new HashMap<>();
        for (Entry a : dirA) {
            String aName = a.getName();
            if (a.isDirectoryEntry()) {
                aSizes.put(aName, -12345);
            } else {
                aSizes.put(aName, Integer.valueOf(((DocumentNode) a).getSize()));
            }
        }
        for (Entry b : dirB) {
            String bName = b.getName();
            if (!aSizes.containsKey(bName)) {
                return false;
            }
            if (b.isDirectoryEntry()) {
                size = -12345;
            } else {
                size = ((DocumentNode) b).getSize();
            }
            if (size != aSizes.get(bName).intValue()) {
                return false;
            }
            aSizes.remove(bName);
        }
        if (!aSizes.isEmpty()) {
            return false;
        }
        for (Entry a2 : dirA) {
            try {
                Entry b2 = dirB.getEntry(a2.getName());
                if (a2.isDirectoryEntry()) {
                    match = areDirectoriesIdentical((DirectoryEntry) a2, (DirectoryEntry) b2);
                } else {
                    match = areDocumentsIdentical((DocumentEntry) a2, (DocumentEntry) b2);
                }
                if (!match) {
                    return false;
                }
            } catch (FileNotFoundException e) {
                return false;
            } catch (IOException e2) {
                return false;
            }
        }
        return true;
    }

    public static boolean areDocumentsIdentical(DocumentEntry docA, DocumentEntry docB) throws IOException {
        if (!docA.getName().equals(docB.getName()) || docA.getSize() != docB.getSize()) {
            return false;
        }
        boolean matches = true;
        DocumentInputStream inpA = null;
        DocumentInputStream inpB = null;
        try {
            inpA = new DocumentInputStream(docA);
            inpB = new DocumentInputStream(docB);
            while (true) {
                int readA = inpA.read();
                int readB = inpB.read();
                if (readA != readB) {
                    matches = false;
                    break;
                }
                if (readA == -1 || readB == -1) {
                    break;
                }
            }
            inpA.close();
            inpB.close();
            return matches;
        } catch (Throwable th) {
            if (inpA != null) {
                inpA.close();
            }
            if (inpB != null) {
                inpB.close();
            }
            throw th;
        }
    }
}
