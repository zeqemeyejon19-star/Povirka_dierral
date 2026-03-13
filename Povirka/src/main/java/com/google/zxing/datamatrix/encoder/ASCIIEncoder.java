package com.google.zxing.datamatrix.encoder;

/* JADX INFO: loaded from: classes.dex */
final class ASCIIEncoder implements Encoder {
    ASCIIEncoder() {
    }

    @Override // com.google.zxing.datamatrix.encoder.Encoder
    public int getEncodingMode() {
        return 0;
    }

    @Override // com.google.zxing.datamatrix.encoder.Encoder
    public void encode(EncoderContext context) {
        if (HighLevelEncoder.determineConsecutiveDigitCount(context.getMessage(), context.pos) >= 2) {
            context.writeCodeword(encodeASCIIDigits(context.getMessage().charAt(context.pos), context.getMessage().charAt(context.pos + 1)));
            context.pos += 2;
            return;
        }
        char c = context.getCurrentChar();
        int newMode = HighLevelEncoder.lookAheadTest(context.getMessage(), context.pos, getEncodingMode());
        if (newMode != getEncodingMode()) {
            if (newMode != 1) {
                if (newMode != 2) {
                    if (newMode != 3) {
                        if (newMode != 4) {
                            if (newMode == 5) {
                                context.writeCodeword((char) 231);
                                context.signalEncoderChange(5);
                                return;
                            }
                            throw new IllegalStateException("Illegal mode: " + newMode);
                        }
                        context.writeCodeword((char) 240);
                        context.signalEncoderChange(4);
                        return;
                    }
                    context.writeCodeword((char) 238);
                    context.signalEncoderChange(3);
                    return;
                }
                context.writeCodeword((char) 239);
                context.signalEncoderChange(2);
                return;
            }
            context.writeCodeword((char) 230);
            context.signalEncoderChange(1);
            return;
        }
        if (HighLevelEncoder.isExtendedASCII(c)) {
            context.writeCodeword((char) 235);
            context.writeCodeword((char) ((c - 128) + 1));
            context.pos++;
        } else {
            context.writeCodeword((char) (c + 1));
            context.pos++;
        }
    }

    private static char encodeASCIIDigits(char digit1, char digit2) {
        if (HighLevelEncoder.isDigit(digit1) && HighLevelEncoder.isDigit(digit2)) {
            return (char) (((digit1 - '0') * 10) + (digit2 - '0') + 130);
        }
        throw new IllegalArgumentException("not digits: " + digit1 + digit2);
    }
}
