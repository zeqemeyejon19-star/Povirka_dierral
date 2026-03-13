package org.apache.poi.hpsf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import org.apache.poi.util.CodePageUtil;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Removal;

/* JADX INFO: loaded from: classes.dex */
public class VariantSupport extends Variant {
    private static boolean logUnsupportedTypes;
    private static List<Long> unsupportedMessage;
    public static final int[] SUPPORTED_TYPES = {0, 2, 3, 20, 5, 64, 30, 31, 71, 11};
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) VariantSupport.class);
    private static final byte[] paddingBytes = new byte[3];

    public static void setLogUnsupportedTypes(boolean logUnsupportedTypes2) {
        logUnsupportedTypes = logUnsupportedTypes2;
    }

    public static boolean isLogUnsupportedTypes() {
        return logUnsupportedTypes;
    }

    protected static void writeUnsupportedTypeMessage(UnsupportedVariantTypeException ex) {
        if (isLogUnsupportedTypes()) {
            if (unsupportedMessage == null) {
                unsupportedMessage = new LinkedList();
            }
            Long vt = Long.valueOf(ex.getVariantType());
            if (!unsupportedMessage.contains(vt)) {
                logger.log(7, ex.getMessage());
                unsupportedMessage.add(vt);
            }
        }
    }

    public boolean isSupportedType(int variantType) {
        int[] arr$ = SUPPORTED_TYPES;
        for (int st : arr$) {
            if (variantType == st) {
                return true;
            }
        }
        return false;
    }

    public static Object read(byte[] src, int offset, int length, long type, int codepage) throws ReadingNotSupportedException, UnsupportedEncodingException {
        LittleEndianByteArrayInputStream lei = new LittleEndianByteArrayInputStream(src, offset);
        return read(lei, length, type, codepage);
    }

    public static Object read(LittleEndianByteArrayInputStream lei, int length, long type, int codepage) throws ReadingNotSupportedException, UnsupportedEncodingException {
        int offset = lei.getReadIndex();
        TypedPropertyValue typedPropertyValue = new TypedPropertyValue((int) type, null);
        try {
            typedPropertyValue.readValue(lei);
            int i = (int) type;
            if (i != 0) {
                if (i == 11) {
                    VariantBool bool = (VariantBool) typedPropertyValue.getValue();
                    return Boolean.valueOf(bool.getValue());
                }
                if (i == 64) {
                    Filetime filetime = (Filetime) typedPropertyValue.getValue();
                    return filetime.getJavaValue();
                }
                if (i == 71) {
                    ClipboardData clipboardData = (ClipboardData) typedPropertyValue.getValue();
                    return clipboardData.toByteArray();
                }
                if (i == 2) {
                    return Integer.valueOf(((Short) typedPropertyValue.getValue()).intValue());
                }
                if (i != 3 && i != 4 && i != 5) {
                    if (i == 30) {
                        CodePageString cpString = (CodePageString) typedPropertyValue.getValue();
                        return cpString.getJavaValue(codepage);
                    }
                    if (i == 31) {
                        UnicodeString uniString = (UnicodeString) typedPropertyValue.getValue();
                        return uniString.toJavaString();
                    }
                    switch (i) {
                        case 16:
                        case 17:
                        case 18:
                        case 19:
                        case 20:
                        case 21:
                            break;
                        default:
                            int unpadded = lei.getReadIndex() - offset;
                            lei.setReadIndex(offset);
                            byte[] v = new byte[unpadded];
                            lei.readFully(v, 0, unpadded);
                            throw new ReadingNotSupportedException(type, v);
                    }
                }
            }
            return typedPropertyValue.getValue();
        } catch (UnsupportedOperationException e) {
            int propLength = Math.min(length, lei.available());
            byte[] v2 = new byte[propLength];
            lei.readFully(v2, 0, propLength);
            throw new ReadingNotSupportedException(type, v2);
        }
    }

    @Removal(version = "3.18")
    @Deprecated
    public static String codepageToEncoding(int codepage) throws UnsupportedEncodingException {
        return CodePageUtil.codepageToEncoding(codepage);
    }

    public static int write(OutputStream out, long type, Object value, int codepage) throws IOException, WritingNotSupportedException {
        int length = -1;
        int i = (int) type;
        if (i == 0) {
            LittleEndian.putUInt(0L, out);
            length = 4;
        } else if (i != 11) {
            if (i == 64) {
                Filetime filetimeValue = value instanceof java.util.Date ? new Filetime((java.util.Date) value) : new Filetime();
                length = filetimeValue.write(out);
            } else if (i != 71) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            if (i != 5) {
                                if (i != 30) {
                                    if (i == 31) {
                                        if (value instanceof String) {
                                            UnicodeString uniString = new UnicodeString();
                                            uniString.setJavaValue((String) value);
                                            length = uniString.write(out);
                                        }
                                    } else {
                                        switch (i) {
                                            case 18:
                                                if (value instanceof Number) {
                                                    LittleEndian.putUShort(((Number) value).intValue(), out);
                                                    length = 2;
                                                }
                                                break;
                                            case 19:
                                                if (value instanceof Number) {
                                                    LittleEndian.putUInt(((Number) value).longValue(), out);
                                                    length = 4;
                                                }
                                                break;
                                            case 20:
                                                if (value instanceof Number) {
                                                    LittleEndian.putLong(((Number) value).longValue(), out);
                                                    length = 8;
                                                }
                                                break;
                                            case 21:
                                                if (value instanceof Number) {
                                                    BigInteger bi = value instanceof BigInteger ? (BigInteger) value : BigInteger.valueOf(((Number) value).longValue());
                                                    if (bi.bitLength() > 64) {
                                                        throw new WritingNotSupportedException(type, value);
                                                    }
                                                    byte[] biBytesBE = bi.toByteArray();
                                                    byte[] biBytesLE = new byte[8];
                                                    int i2 = biBytesBE.length;
                                                    for (byte b : biBytesBE) {
                                                        if (i2 <= 8) {
                                                            biBytesLE[i2 - 1] = b;
                                                        }
                                                        i2--;
                                                    }
                                                    out.write(biBytesLE);
                                                    length = 8;
                                                }
                                                break;
                                        }
                                    }
                                } else if (value instanceof String) {
                                    CodePageString codePageString = new CodePageString();
                                    codePageString.setJavaValue((String) value, codepage);
                                    length = codePageString.write(out);
                                }
                            } else if (value instanceof Number) {
                                LittleEndian.putDouble(((Number) value).doubleValue(), out);
                                length = 8;
                            }
                        } else if (value instanceof Number) {
                            int floatBits = Float.floatToIntBits(((Number) value).floatValue());
                            LittleEndian.putInt(floatBits, out);
                            length = 4;
                        }
                    } else if (value instanceof Number) {
                        LittleEndian.putInt(((Number) value).intValue(), out);
                        length = 4;
                    }
                } else if (value instanceof Number) {
                    LittleEndian.putShort(out, ((Number) value).shortValue());
                    length = 2;
                }
            } else if (value instanceof byte[]) {
                byte[] cf = (byte[]) value;
                out.write(cf);
                length = cf.length;
            }
        } else if (value instanceof Boolean) {
            int bb = ((Boolean) value).booleanValue() ? 255 : 0;
            out.write(bb);
            out.write(bb);
            length = 2;
        }
        if (length == -1) {
            if (value instanceof byte[]) {
                byte[] b2 = (byte[]) value;
                out.write(b2);
                length = b2.length;
                writeUnsupportedTypeMessage(new WritingNotSupportedException(type, value));
            } else {
                throw new WritingNotSupportedException(type, value);
            }
        }
        int padding = (4 - (length & 3)) & 3;
        out.write(paddingBytes, 0, padding);
        return length + padding;
    }
}
