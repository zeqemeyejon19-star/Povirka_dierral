package org.apache.poi.poifs.eventfilesystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.poi.poifs.filesystem.DocumentDescriptor;
import org.apache.poi.poifs.filesystem.POIFSDocumentPath;

/* JADX INFO: loaded from: classes.dex */
class POIFSReaderRegistry {
    private Set<POIFSReaderListener> omnivorousListeners = new HashSet();
    private Map<POIFSReaderListener, Set<DocumentDescriptor>> selectiveListeners = new HashMap();
    private Map<DocumentDescriptor, Set<POIFSReaderListener>> chosenDocumentDescriptors = new HashMap();

    POIFSReaderRegistry() {
    }

    void registerListener(POIFSReaderListener listener, POIFSDocumentPath path, String documentName) {
        if (!this.omnivorousListeners.contains(listener)) {
            Set<DocumentDescriptor> descriptors = this.selectiveListeners.get(listener);
            if (descriptors == null) {
                descriptors = new HashSet();
                this.selectiveListeners.put(listener, descriptors);
            }
            DocumentDescriptor descriptor = new DocumentDescriptor(path, documentName);
            if (descriptors.add(descriptor)) {
                Set<POIFSReaderListener> listeners = this.chosenDocumentDescriptors.get(descriptor);
                if (listeners == null) {
                    listeners = new HashSet();
                    this.chosenDocumentDescriptors.put(descriptor, listeners);
                }
                listeners.add(listener);
            }
        }
    }

    void registerListener(POIFSReaderListener listener) {
        if (!this.omnivorousListeners.contains(listener)) {
            removeSelectiveListener(listener);
            this.omnivorousListeners.add(listener);
        }
    }

    Iterator<POIFSReaderListener> getListeners(POIFSDocumentPath path, String name) {
        Set<POIFSReaderListener> rval = new HashSet<>(this.omnivorousListeners);
        Set<POIFSReaderListener> selectiveListenersInner = this.chosenDocumentDescriptors.get(new DocumentDescriptor(path, name));
        if (selectiveListenersInner != null) {
            rval.addAll(selectiveListenersInner);
        }
        return rval.iterator();
    }

    private void removeSelectiveListener(POIFSReaderListener listener) {
        Set<DocumentDescriptor> selectedDescriptors = this.selectiveListeners.remove(listener);
        if (selectedDescriptors != null) {
            Iterator<DocumentDescriptor> iter = selectedDescriptors.iterator();
            while (iter.hasNext()) {
                dropDocument(listener, iter.next());
            }
        }
    }

    private void dropDocument(POIFSReaderListener listener, DocumentDescriptor descriptor) {
        Set<POIFSReaderListener> listeners = this.chosenDocumentDescriptors.get(descriptor);
        listeners.remove(listener);
        if (listeners.size() == 0) {
            this.chosenDocumentDescriptors.remove(descriptor);
        }
    }
}
