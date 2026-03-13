package org.apache.poi.sl.draw;

import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

/* JADX INFO: loaded from: classes.dex */
public class DrawTextFragment implements Drawable {
    final TextLayout layout;
    final AttributedString str;
    double x;
    double y;

    public DrawTextFragment(TextLayout layout, AttributedString str) {
        this.layout = layout;
        this.str = str;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override // org.apache.poi.sl.draw.Drawable
    public void draw(Graphics2D graphics) {
        if (this.str == null) {
            return;
        }
        double yBaseline = this.y + ((double) this.layout.getAscent());
        Integer textMode = (Integer) graphics.getRenderingHint(Drawable.TEXT_RENDERING_MODE);
        if (textMode != null && textMode.intValue() == 2) {
            this.layout.draw(graphics, (float) this.x, (float) yBaseline);
        } else {
            graphics.drawString(this.str.getIterator(), (float) this.x, (float) yBaseline);
        }
    }

    @Override // org.apache.poi.sl.draw.Drawable
    public void applyTransform(Graphics2D graphics) {
    }

    @Override // org.apache.poi.sl.draw.Drawable
    public void drawContent(Graphics2D graphics) {
    }

    public TextLayout getLayout() {
        return this.layout;
    }

    public AttributedString getAttributedString() {
        return this.str;
    }

    public float getHeight() {
        double h = this.layout.getAscent() + this.layout.getDescent() + getLeading();
        return (float) h;
    }

    public float getLeading() {
        double l = this.layout.getLeading();
        if (l == 0.0d) {
            l = ((double) (this.layout.getAscent() + this.layout.getDescent())) * 0.15d;
        }
        return (float) l;
    }

    public float getWidth() {
        return this.layout.getAdvance();
    }

    public String getString() {
        AttributedString attributedString = this.str;
        if (attributedString == null) {
            return "";
        }
        AttributedCharacterIterator it = attributedString.getIterator();
        StringBuilder buf = new StringBuilder();
        for (char c = it.first(); c != 65535; c = it.next()) {
            buf.append(c);
        }
        return buf.toString();
    }

    public String toString() {
        return "[" + getClass().getSimpleName() + "] " + getString();
    }
}
