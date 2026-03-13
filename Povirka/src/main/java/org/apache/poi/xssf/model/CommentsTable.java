package org.apache.poi.xssf.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTComment;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCommentList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTComments;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CommentsDocument;

/* JADX INFO: loaded from: classes.dex */
@Internal
public class CommentsTable extends POIXMLDocumentPart {
    public static final String DEFAULT_AUTHOR = "";
    public static final int DEFAULT_AUTHOR_ID = 0;
    private Map<CellAddress, CTComment> commentRefs;
    private CTComments comments;

    public CommentsTable() {
        CTComments cTCommentsNewInstance = CTComments.Factory.newInstance();
        this.comments = cTCommentsNewInstance;
        cTCommentsNewInstance.addNewCommentList();
        this.comments.addNewAuthors().addAuthor("");
    }

    public CommentsTable(PackagePart part) throws IOException {
        super(part);
        readFrom(part.getInputStream());
    }

    public void readFrom(InputStream is) throws IOException {
        try {
            CommentsDocument doc = CommentsDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.comments = doc.getComments();
        } catch (XmlException e) {
            throw new IOException(e.getLocalizedMessage());
        }
    }

    public void writeTo(OutputStream out) throws IOException {
        CommentsDocument doc = CommentsDocument.Factory.newInstance();
        doc.setComments(this.comments);
        doc.save(out, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void commit() throws IOException {
        PackagePart part = getPackagePart();
        OutputStream out = part.getOutputStream();
        writeTo(out);
        out.close();
    }

    public void referenceUpdated(CellAddress oldReference, CTComment comment) {
        Map<CellAddress, CTComment> map = this.commentRefs;
        if (map != null) {
            map.remove(oldReference);
            this.commentRefs.put(new CellAddress(comment.getRef()), comment);
        }
    }

    public int getNumberOfComments() {
        return this.comments.getCommentList().sizeOfCommentArray();
    }

    public int getNumberOfAuthors() {
        return this.comments.getAuthors().sizeOfAuthorArray();
    }

    public String getAuthor(long authorId) {
        return this.comments.getAuthors().getAuthorArray((int) authorId);
    }

    public int findAuthor(String author) {
        String[] authorArray = this.comments.getAuthors().getAuthorArray();
        for (int i = 0; i < authorArray.length; i++) {
            if (authorArray[i].equals(author)) {
                return i;
            }
        }
        int i2 = addNewAuthor(author);
        return i2;
    }

    public XSSFComment findCellComment(CellAddress cellAddress) {
        CTComment ct = getCTComment(cellAddress);
        if (ct == null) {
            return null;
        }
        return new XSSFComment(this, ct, null);
    }

    @Internal
    public CTComment getCTComment(CellAddress cellRef) {
        prepareCTCommentCache();
        return this.commentRefs.get(cellRef);
    }

    public Map<CellAddress, XSSFComment> getCellComments() {
        prepareCTCommentCache();
        TreeMap<CellAddress, XSSFComment> map = new TreeMap<>();
        for (Map.Entry<CellAddress, CTComment> e : this.commentRefs.entrySet()) {
            map.put(e.getKey(), new XSSFComment(this, e.getValue(), null));
        }
        return map;
    }

    private void prepareCTCommentCache() {
        if (this.commentRefs == null) {
            this.commentRefs = new HashMap();
            CTComment[] arr$ = this.comments.getCommentList().getCommentArray();
            for (CTComment comment : arr$) {
                this.commentRefs.put(new CellAddress(comment.getRef()), comment);
            }
        }
    }

    @Internal
    public CTComment newComment(CellAddress ref) {
        CTComment ct = this.comments.getCommentList().addNewComment();
        ct.setRef(ref.formatAsString());
        ct.setAuthorId(0L);
        Map<CellAddress, CTComment> map = this.commentRefs;
        if (map != null) {
            map.put(ref, ct);
        }
        return ct;
    }

    public boolean removeComment(CellAddress cellRef) {
        String stringRef = cellRef.formatAsString();
        CTCommentList lst = this.comments.getCommentList();
        if (lst != null) {
            CTComment[] commentArray = lst.getCommentArray();
            for (int i = 0; i < commentArray.length; i++) {
                CTComment comment = commentArray[i];
                if (stringRef.equals(comment.getRef())) {
                    lst.removeComment(i);
                    Map<CellAddress, CTComment> map = this.commentRefs;
                    if (map != null) {
                        map.remove(cellRef);
                        return true;
                    }
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private int addNewAuthor(String author) {
        int index = this.comments.getAuthors().sizeOfAuthorArray();
        this.comments.getAuthors().insertAuthor(index, author);
        return index;
    }

    @Internal
    public CTComments getCTComments() {
        return this.comments;
    }
}
