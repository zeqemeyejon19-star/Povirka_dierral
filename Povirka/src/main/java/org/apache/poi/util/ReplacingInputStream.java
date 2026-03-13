package org.apache.poi.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
@Internal
public class ReplacingInputStream extends FilterInputStream {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    final int[] buf;
    private int matchedIndex;
    private final byte[] pattern;
    private int replacedIndex;
    private final byte[] replacement;
    private State state;
    private int unbufferIndex;

    private enum State {
        NOT_MATCHED,
        MATCHING,
        REPLACING,
        UNBUFFER
    }

    /* JADX WARN: Illegal instructions before constructor call */
    public ReplacingInputStream(InputStream in, String pattern, String replacement) {
        Charset charset = UTF8;
        this(in, pattern.getBytes(charset), replacement == null ? null : replacement.getBytes(charset));
    }

    public ReplacingInputStream(InputStream in, byte[] pattern, byte[] replacement) {
        super(in);
        this.matchedIndex = 0;
        this.unbufferIndex = 0;
        this.replacedIndex = 0;
        this.state = State.NOT_MATCHED;
        if (pattern == null || pattern.length == 0) {
            throw new IllegalArgumentException("pattern length should be > 0");
        }
        this.pattern = pattern;
        this.replacement = replacement;
        this.buf = new int[pattern.length];
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        int c = read();
        if (c == -1) {
            return -1;
        }
        b[off] = (byte) c;
        int i = 1;
        while (i < len) {
            int c2 = read();
            if (c2 == -1) {
                break;
            }
            b[off + i] = (byte) c2;
            i++;
        }
        return i;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    /* JADX INFO: renamed from: org.apache.poi.util.ReplacingInputStream$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$util$ReplacingInputStream$State;

        static {
            int[] iArr = new int[State.values().length];
            $SwitchMap$org$apache$poi$util$ReplacingInputStream$State = iArr;
            try {
                iArr[State.NOT_MATCHED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$util$ReplacingInputStream$State[State.MATCHING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$util$ReplacingInputStream$State[State.REPLACING.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$util$ReplacingInputStream$State[State.UNBUFFER.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$util$ReplacingInputStream$State[this.state.ordinal()];
        if (i == 2) {
            int next = super.read();
            byte[] bArr = this.pattern;
            int i2 = this.matchedIndex;
            if (bArr[i2] == next) {
                int[] iArr = this.buf;
                int i3 = i2 + 1;
                this.matchedIndex = i3;
                iArr[i2] = next;
                if (i3 == bArr.length) {
                    byte[] bArr2 = this.replacement;
                    if (bArr2 == null || bArr2.length == 0) {
                        this.state = State.NOT_MATCHED;
                        this.matchedIndex = 0;
                    } else {
                        this.state = State.REPLACING;
                        this.replacedIndex = 0;
                    }
                }
            } else {
                int[] iArr2 = this.buf;
                this.matchedIndex = i2 + 1;
                iArr2[i2] = next;
                this.state = State.UNBUFFER;
                this.unbufferIndex = 0;
            }
            return read();
        }
        if (i == 3) {
            byte[] bArr3 = this.replacement;
            int i4 = this.replacedIndex;
            int i5 = i4 + 1;
            this.replacedIndex = i5;
            int next2 = bArr3[i4];
            if (i5 == bArr3.length) {
                this.state = State.NOT_MATCHED;
                this.replacedIndex = 0;
            }
            return next2;
        }
        if (i == 4) {
            int[] iArr3 = this.buf;
            int i6 = this.unbufferIndex;
            int i7 = i6 + 1;
            this.unbufferIndex = i7;
            int next3 = iArr3[i6];
            if (i7 == this.matchedIndex) {
                this.state = State.NOT_MATCHED;
                this.matchedIndex = 0;
            }
            return next3;
        }
        int next4 = super.read();
        if (this.pattern[0] != next4) {
            return next4;
        }
        Arrays.fill(this.buf, 0);
        this.matchedIndex = 0;
        int[] iArr4 = this.buf;
        this.matchedIndex = 0 + 1;
        iArr4[0] = next4;
        if (this.pattern.length == 1) {
            this.state = State.REPLACING;
            this.replacedIndex = 0;
        } else {
            this.state = State.MATCHING;
        }
        return read();
    }

    public String toString() {
        return this.state.name() + " " + this.matchedIndex + " " + this.replacedIndex + " " + this.unbufferIndex;
    }
}
