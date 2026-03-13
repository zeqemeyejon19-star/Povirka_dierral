package org.apache.poi.util;

import androidx.appcompat.widget.ActivityChooserView;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import org.apache.poi.EmptyFileException;
import org.apache.poi.POIDocument;
import org.apache.poi.ss.usermodel.Workbook;

/* JADX INFO: loaded from: classes.dex */
public final class IOUtils {
    private static final int SKIP_BUFFER_SIZE = 2048;
    private static byte[] SKIP_BYTE_BUFFER;
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) IOUtils.class);

    private IOUtils() {
    }

    public static byte[] peekFirst8Bytes(InputStream stream) throws EmptyFileException, IOException {
        return peekFirstNBytes(stream, 8);
    }

    public static byte[] peekFirstNBytes(InputStream stream, int limit) throws EmptyFileException, IOException {
        stream.mark(limit);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(limit);
        copy(new BoundedInputStream(stream, limit), bos);
        int readBytes = bos.size();
        if (readBytes == 0) {
            throw new EmptyFileException();
        }
        if (readBytes < limit) {
            bos.write(new byte[limit - readBytes]);
        }
        byte[] peekedBytes = bos.toByteArray();
        if (stream instanceof PushbackInputStream) {
            PushbackInputStream pin = (PushbackInputStream) stream;
            pin.unread(peekedBytes, 0, readBytes);
        } else {
            stream.reset();
        }
        return peekedBytes;
    }

    public static byte[] toByteArray(InputStream stream) throws IOException {
        return toByteArray(stream, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
    }

    public static byte[] toByteArray(InputStream stream, int length) throws IOException {
        int readBytes;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(length == Integer.MAX_VALUE ? 4096 : length);
        byte[] buffer = new byte[4096];
        int totalBytes = 0;
        do {
            readBytes = stream.read(buffer, 0, Math.min(buffer.length, length - totalBytes));
            totalBytes += Math.max(readBytes, 0);
            if (readBytes > 0) {
                baos.write(buffer, 0, readBytes);
            }
            if (totalBytes >= length) {
                break;
            }
        } while (readBytes > -1);
        if (length != Integer.MAX_VALUE && totalBytes < length) {
            throw new IOException("unexpected EOF");
        }
        return baos.toByteArray();
    }

    public static byte[] toByteArray(ByteBuffer buffer, int length) {
        if (buffer.hasArray() && buffer.arrayOffset() == 0) {
            return buffer.array();
        }
        byte[] data = new byte[length];
        buffer.get(data);
        return data;
    }

    public static int readFully(InputStream in, byte[] b) throws IOException {
        return readFully(in, b, 0, b.length);
    }

    public static int readFully(InputStream in, byte[] b, int off, int len) throws IOException {
        int total = 0;
        do {
            int got = in.read(b, off + total, len - total);
            if (got < 0) {
                if (total == 0) {
                    return -1;
                }
                return total;
            }
            total += got;
        } while (total != len);
        return total;
    }

    public static int readFully(ReadableByteChannel channel, ByteBuffer b) throws IOException {
        int total = 0;
        do {
            int got = channel.read(b);
            if (got < 0) {
                if (total == 0) {
                    return -1;
                }
                return total;
            }
            total += got;
            if (total == b.capacity()) {
                break;
            }
        } while (b.position() != b.capacity());
        return total;
    }

    public static void write(POIDocument doc, OutputStream out) throws IOException {
        try {
            doc.write(out);
        } finally {
            closeQuietly(out);
        }
    }

    public static void write(Workbook doc, OutputStream out) throws IOException {
        try {
            doc.write(out);
        } finally {
            closeQuietly(out);
        }
    }

    public static void writeAndClose(POIDocument doc, OutputStream out) throws IOException {
        try {
            write(doc, out);
        } finally {
            closeQuietly(doc);
        }
    }

    public static void writeAndClose(POIDocument doc, File out) throws IOException {
        try {
            doc.write(out);
        } finally {
            closeQuietly(doc);
        }
    }

    public static void writeAndClose(POIDocument doc) throws IOException {
        try {
            doc.write();
        } finally {
            closeQuietly(doc);
        }
    }

    public static void writeAndClose(Workbook doc, OutputStream out) throws IOException {
        try {
            doc.write(out);
        } finally {
            closeQuietly(doc);
        }
    }

    public static void copy(InputStream inp, OutputStream out) throws IOException {
        byte[] buff = new byte[4096];
        while (true) {
            int count = inp.read(buff);
            if (count != -1) {
                if (count < -1) {
                    throw new RecordFormatException("Can't have read < -1 bytes");
                }
                if (count > 0) {
                    out.write(buff, 0, count);
                }
            } else {
                return;
            }
        }
    }

    public static long calculateChecksum(byte[] data) {
        Checksum sum = new CRC32();
        sum.update(data, 0, data.length);
        return sum.getValue();
    }

    public static long calculateChecksum(InputStream stream) throws IOException {
        Checksum sum = new CRC32();
        byte[] buf = new byte[4096];
        while (true) {
            int count = stream.read(buf);
            if (count != -1) {
                if (count > 0) {
                    sum.update(buf, 0, count);
                }
            } else {
                return sum.getValue();
            }
        }
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception exc) {
            logger.log(7, "Unable to close resource: " + exc, exc);
        }
    }

    public static long skipFully(InputStream input, long toSkip) throws IOException {
        if (toSkip < 0) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        if (toSkip == 0) {
            return 0L;
        }
        if (SKIP_BYTE_BUFFER == null) {
            SKIP_BYTE_BUFFER = new byte[2048];
        }
        long remain = toSkip;
        while (remain > 0) {
            long n = input.read(SKIP_BYTE_BUFFER, 0, (int) Math.min(remain, 2048L));
            if (n < 0) {
                break;
            }
            remain -= n;
        }
        if (toSkip == remain) {
            return -1L;
        }
        return toSkip - remain;
    }

    public static byte[] safelyAllocate(long length, int maxLength) {
        if (length < 0) {
            throw new RecordFormatException("Can't allocate an array of length < 0");
        }
        if (length > 2147483647L) {
            throw new RecordFormatException("Can't allocate an array > 2147483647");
        }
        if (length > maxLength) {
            throw new RecordFormatException("Not allowed to allocate an array > " + maxLength + " for this record type.If the file is not corrupt, please open an issue on bugzilla to request increasing the maximum allowable size for this record type");
        }
        return new byte[(int) length];
    }
}
