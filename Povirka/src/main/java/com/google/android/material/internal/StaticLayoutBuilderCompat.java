package com.google.android.material.internal;

import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import androidx.appcompat.widget.ActivityChooserView;
import androidx.core.util.Preconditions;
import java.lang.reflect.Constructor;

/* JADX INFO: loaded from: classes.dex */
final class StaticLayoutBuilderCompat {
    private static final String TEXT_DIRS_CLASS = "android.text.TextDirectionHeuristics";
    private static final String TEXT_DIR_CLASS = "android.text.TextDirectionHeuristic";
    private static final String TEXT_DIR_CLASS_LTR = "LTR";
    private static final String TEXT_DIR_CLASS_RTL = "RTL";
    private static Constructor<StaticLayout> constructor;
    private static boolean initialized;
    private static Object textDirection;
    private int end;
    private boolean isRtl;
    private final TextPaint paint;
    private CharSequence source;
    private final int width;
    private int start = 0;
    private Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
    private int maxLines = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
    private boolean includePad = true;
    private TextUtils.TruncateAt ellipsize = null;

    private StaticLayoutBuilderCompat(CharSequence source, TextPaint paint, int width) {
        this.source = source;
        this.paint = paint;
        this.width = width;
        this.end = source.length();
    }

    public static StaticLayoutBuilderCompat obtain(CharSequence source, TextPaint paint, int width) {
        return new StaticLayoutBuilderCompat(source, paint, width);
    }

    public StaticLayoutBuilderCompat setAlignment(Layout.Alignment alignment) {
        this.alignment = alignment;
        return this;
    }

    public StaticLayoutBuilderCompat setIncludePad(boolean includePad) {
        this.includePad = includePad;
        return this;
    }

    public StaticLayoutBuilderCompat setStart(int start) {
        this.start = start;
        return this;
    }

    public StaticLayoutBuilderCompat setEnd(int end) {
        this.end = end;
        return this;
    }

    public StaticLayoutBuilderCompat setMaxLines(int maxLines) {
        this.maxLines = maxLines;
        return this;
    }

    public StaticLayoutBuilderCompat setEllipsize(TextUtils.TruncateAt ellipsize) {
        this.ellipsize = ellipsize;
        return this;
    }

    public StaticLayout build() throws StaticLayoutBuilderCompatException {
        if (this.source == null) {
            this.source = "";
        }
        int availableWidth = Math.max(0, this.width);
        CharSequence textToDraw = this.source;
        if (this.maxLines == 1) {
            textToDraw = TextUtils.ellipsize(this.source, this.paint, availableWidth, this.ellipsize);
        }
        this.end = Math.min(textToDraw.length(), this.end);
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.isRtl) {
                this.alignment = Layout.Alignment.ALIGN_OPPOSITE;
            }
            StaticLayout.Builder builder = StaticLayout.Builder.obtain(textToDraw, this.start, this.end, this.paint, availableWidth);
            builder.setAlignment(this.alignment);
            builder.setIncludePad(this.includePad);
            TextDirectionHeuristic textDirectionHeuristic = this.isRtl ? TextDirectionHeuristics.RTL : TextDirectionHeuristics.LTR;
            builder.setTextDirection(textDirectionHeuristic);
            TextUtils.TruncateAt truncateAt = this.ellipsize;
            if (truncateAt != null) {
                builder.setEllipsize(truncateAt);
            }
            builder.setMaxLines(this.maxLines);
            return builder.build();
        }
        createConstructorWithReflection();
        try {
            return (StaticLayout) ((Constructor) Preconditions.checkNotNull(constructor)).newInstance(textToDraw, Integer.valueOf(this.start), Integer.valueOf(this.end), this.paint, Integer.valueOf(availableWidth), this.alignment, Preconditions.checkNotNull(textDirection), Float.valueOf(1.0f), Float.valueOf(0.0f), Boolean.valueOf(this.includePad), null, Integer.valueOf(availableWidth), Integer.valueOf(this.maxLines));
        } catch (Exception cause) {
            throw new StaticLayoutBuilderCompatException(cause);
        }
    }

    private void createConstructorWithReflection() throws StaticLayoutBuilderCompatException {
        Class<?> textDirClass;
        if (initialized) {
            return;
        }
        try {
            boolean useRtl = this.isRtl && Build.VERSION.SDK_INT >= 23;
            if (Build.VERSION.SDK_INT >= 18) {
                textDirClass = TextDirectionHeuristic.class;
                textDirection = useRtl ? TextDirectionHeuristics.RTL : TextDirectionHeuristics.LTR;
            } else {
                ClassLoader loader = StaticLayoutBuilderCompat.class.getClassLoader();
                String textDirClassName = this.isRtl ? TEXT_DIR_CLASS_RTL : TEXT_DIR_CLASS_LTR;
                Class<?> textDirClass2 = loader.loadClass(TEXT_DIR_CLASS);
                Class<?> textDirsClass = loader.loadClass(TEXT_DIRS_CLASS);
                textDirection = textDirsClass.getField(textDirClassName).get(textDirsClass);
                textDirClass = textDirClass2;
            }
            Class<?>[] signature = {CharSequence.class, Integer.TYPE, Integer.TYPE, TextPaint.class, Integer.TYPE, Layout.Alignment.class, textDirClass, Float.TYPE, Float.TYPE, Boolean.TYPE, TextUtils.TruncateAt.class, Integer.TYPE, Integer.TYPE};
            Constructor<StaticLayout> declaredConstructor = StaticLayout.class.getDeclaredConstructor(signature);
            constructor = declaredConstructor;
            declaredConstructor.setAccessible(true);
            initialized = true;
        } catch (Exception cause) {
            throw new StaticLayoutBuilderCompatException(cause);
        }
    }

    public StaticLayoutBuilderCompat setIsRtl(boolean isRtl) {
        this.isRtl = isRtl;
        return this;
    }

    static class StaticLayoutBuilderCompatException extends Exception {
        StaticLayoutBuilderCompatException(Throwable cause) {
            super("Error thrown initializing StaticLayout " + cause.getMessage(), cause);
        }
    }
}
