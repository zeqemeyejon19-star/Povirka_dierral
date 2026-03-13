package org.apache.poi.xssf.usermodel.helpers;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Locale;
import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.util.Internal;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

/* JADX INFO: loaded from: classes.dex */
@Internal(since = "3.15 beta 3")
public final class XSSFPasswordHelper {
    private XSSFPasswordHelper() {
    }

    public static void setPassword(XmlObject xobj, String password, HashAlgorithm hashAlgo, String prefix) {
        XmlCursor cur = xobj.newCursor();
        if (password == null) {
            cur.removeAttribute(getAttrName(prefix, "password"));
            cur.removeAttribute(getAttrName(prefix, "algorithmName"));
            cur.removeAttribute(getAttrName(prefix, "hashValue"));
            cur.removeAttribute(getAttrName(prefix, "saltValue"));
            cur.removeAttribute(getAttrName(prefix, "spinCount"));
            return;
        }
        cur.toFirstContentToken();
        if (hashAlgo == null) {
            int hash = CryptoFunctions.createXorVerifier1(password);
            cur.insertAttributeWithValue(getAttrName(prefix, "password"), String.format(Locale.ROOT, "%04X", Integer.valueOf(hash)).toUpperCase(Locale.ROOT));
        } else {
            SecureRandom random = new SecureRandom();
            byte[] salt = random.generateSeed(16);
            byte[] hash2 = CryptoFunctions.hashPassword(password, hashAlgo, salt, 100000, false);
            cur.insertAttributeWithValue(getAttrName(prefix, "algorithmName"), hashAlgo.jceId);
            cur.insertAttributeWithValue(getAttrName(prefix, "hashValue"), DatatypeConverter.printBase64Binary(hash2));
            cur.insertAttributeWithValue(getAttrName(prefix, "saltValue"), DatatypeConverter.printBase64Binary(salt));
            cur.insertAttributeWithValue(getAttrName(prefix, "spinCount"), "100000");
        }
        cur.dispose();
    }

    public static boolean validatePassword(XmlObject xobj, String password, String prefix) {
        if (password == null) {
            return false;
        }
        XmlCursor cur = xobj.newCursor();
        String xorHashVal = cur.getAttributeText(getAttrName(prefix, "password"));
        String algoName = cur.getAttributeText(getAttrName(prefix, "algorithmName"));
        String hashVal = cur.getAttributeText(getAttrName(prefix, "hashValue"));
        String saltVal = cur.getAttributeText(getAttrName(prefix, "saltValue"));
        String spinCount = cur.getAttributeText(getAttrName(prefix, "spinCount"));
        cur.dispose();
        if (xorHashVal != null) {
            int hash1 = Integer.parseInt(xorHashVal, 16);
            int hash2 = CryptoFunctions.createXorVerifier1(password);
            return hash1 == hash2;
        }
        if (hashVal == null || algoName == null || saltVal == null || spinCount == null) {
            return false;
        }
        byte[] hash12 = DatatypeConverter.parseBase64Binary(hashVal);
        HashAlgorithm hashAlgo = HashAlgorithm.fromString(algoName);
        byte[] salt = DatatypeConverter.parseBase64Binary(saltVal);
        int spinCnt = Integer.parseInt(spinCount);
        byte[] hash22 = CryptoFunctions.hashPassword(password, hashAlgo, salt, spinCnt, false);
        return Arrays.equals(hash12, hash22);
    }

    private static QName getAttrName(String prefix, String name) {
        if (prefix == null || "".equals(prefix)) {
            return new QName(name);
        }
        return new QName(prefix + Character.toUpperCase(name.charAt(0)) + name.substring(1));
    }
}
