package org.apache.poi.xslf.util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import org.apache.poi.openxml4j.opc.ContentTypes;
import org.apache.poi.sl.draw.DrawFactory;
import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.sl.usermodel.SlideShowFactory;

/* JADX INFO: loaded from: classes.dex */
public class PPTX2PNG {
    static void usage(String error) {
        String msg = "Usage: PPTX2PNG [options] <ppt or pptx file>\n" + (error == null ? "" : "Error: " + error + "\n") + "Options:\n    -scale <float>   scale factor\n    -slide <integer> 1-based index of a slide to render\n    -format <type>   png,gif,jpg (,null for testing)    -outdir <dir>    output directory, defaults to origin of the ppt/pptx file    -quiet           do not write to console (for normal processing)";
        System.out.println(msg);
    }

    public static void main(String[] args) throws Exception {
        File outdir;
        String slidenumStr;
        boolean quiet;
        List list;
        Set<Integer> slidenum;
        Integer slideNo;
        Dimension pgsize;
        String title;
        Iterator<Integer> it;
        String str;
        if (args.length == 0) {
            usage(null);
            return;
        }
        boolean quiet2 = false;
        int i = 0;
        String slidenumStr2 = "-1";
        File outdir2 = null;
        String format = ContentTypes.EXTENSION_PNG;
        File file = null;
        float scale = 1.0f;
        while (i < args.length) {
            if (!args[i].startsWith("-")) {
                file = new File(args[i]);
            } else if ("-scale".equals(args[i])) {
                i++;
                scale = Float.parseFloat(args[i]);
            } else if ("-slide".equals(args[i])) {
                i++;
                slidenumStr2 = args[i];
            } else if ("-format".equals(args[i])) {
                i++;
                format = args[i];
            } else if ("-outdir".equals(args[i])) {
                i++;
                outdir2 = new File(args[i]);
            } else if ("-quiet".equals(args[i])) {
                quiet2 = true;
            }
            i++;
        }
        if (file == null || !file.exists()) {
            usage("File not specified or it doesn't exist");
            return;
        }
        if (format == null || !format.matches("^(png|gif|jpg|null)$")) {
            usage("Invalid format given");
            return;
        }
        if (outdir2 == null) {
            File outdir3 = file.getParentFile();
            outdir = outdir3;
        } else {
            outdir = outdir2;
        }
        if (!"null".equals(format) && (outdir == null || !outdir.exists() || !outdir.isDirectory())) {
            usage("Output directory doesn't exist");
            return;
        }
        if (scale < 0.0f) {
            usage("Invalid scale given");
            return;
        }
        if (!quiet2) {
            System.out.println("Processing " + file);
        }
        SlideShow<?, ?> ss = SlideShowFactory.create(file, null, true);
        try {
            List slides = ss.getSlides();
            Set<Integer> slidenum2 = slideIndexes(slides.size(), slidenumStr2);
            if (!slidenum2.isEmpty()) {
                Dimension pgsize2 = ss.getPageSize();
                int width = (int) (pgsize2.width * scale);
                int height = (int) (pgsize2.height * scale);
                Iterator<Integer> it2 = slidenum2.iterator();
                while (it2.hasNext()) {
                    Integer slideNo2 = it2.next();
                    Slide<?, ?> slide = (Slide) slides.get(slideNo2.intValue());
                    String title2 = slide.getTitle();
                    if (quiet2) {
                        slidenumStr = slidenumStr2;
                        quiet = quiet2;
                        list = slides;
                        slidenum = slidenum2;
                        slideNo = slideNo2;
                        pgsize = pgsize2;
                        title = title2;
                        it = it2;
                    } else {
                        slidenumStr = slidenumStr2;
                        try {
                            PrintStream printStream = System.out;
                            list = slides;
                            slidenum = slidenum2;
                            slideNo = slideNo2;
                            StringBuilder sbAppend = new StringBuilder().append("Rendering slide ").append(slideNo);
                            pgsize = pgsize2;
                            title = title2;
                            if (title == null) {
                                quiet = quiet2;
                                it = it2;
                                str = "";
                            } else {
                                it = it2;
                                quiet = quiet2;
                                str = ": " + title;
                            }
                            printStream.println(sbAppend.append(str).toString());
                        } catch (Throwable th) {
                            th = th;
                        }
                    }
                    BufferedImage img = new BufferedImage(width, height, 2);
                    Graphics2D graphics = img.createGraphics();
                    DrawFactory.getInstance(graphics).fixFonts(graphics);
                    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                    int width2 = width;
                    int height2 = height;
                    graphics.scale(scale, scale);
                    slide.draw(graphics);
                    if (!"null".equals(format)) {
                        try {
                            String outname = file.getName().replaceFirst(".pptx?", "");
                            File outfile = new File(outdir, String.format(Locale.ROOT, "%1$s-%2$04d.%3$s", outname, slideNo, format));
                            ImageIO.write(img, format, outfile);
                        } catch (Throwable th2) {
                            th = th2;
                        }
                    }
                    graphics.dispose();
                    img.flush();
                    width = width2;
                    pgsize2 = pgsize;
                    slidenumStr2 = slidenumStr;
                    it2 = it;
                    slides = list;
                    slidenum2 = slidenum;
                    quiet2 = quiet;
                    height = height2;
                }
                boolean quiet3 = quiet2;
                ss.close();
                if (quiet3) {
                    return;
                }
                System.out.println("Done");
                return;
            }
            try {
                usage("slidenum must be either -1 (for all) or within range: [1.." + slides.size() + "] for " + file);
                ss.close();
                return;
            } catch (Throwable th3) {
                th = th3;
            }
        } catch (Throwable th4) {
            th = th4;
        }
        ss.close();
        throw th;
    }

    private static Set<Integer> slideIndexes(int slideCount, String range) {
        Set<Integer> slideIdx = new TreeSet<>();
        if ("-1".equals(range)) {
            for (int i = 0; i < slideCount; i++) {
                slideIdx.add(Integer.valueOf(i));
            }
        } else {
            String[] arr$ = range.split(",");
            for (String subrange : arr$) {
                String[] idx = subrange.split("-");
                int length = idx.length;
                if (length == 1) {
                    int subidx = Integer.parseInt(idx[0]);
                    if (subrange.contains("-")) {
                        int startIdx = subrange.startsWith("-") ? 0 : subidx;
                        int endIdx = subrange.endsWith("-") ? slideCount : Math.min(subidx, slideCount);
                        for (int i2 = Math.max(startIdx, 1); i2 < endIdx; i2++) {
                            slideIdx.add(Integer.valueOf(i2 - 1));
                        }
                    } else {
                        slideIdx.add(Integer.valueOf(Math.max(subidx, 1) - 1));
                    }
                } else if (length == 2) {
                    int startIdx2 = Math.min(Integer.parseInt(idx[0]), slideCount);
                    int endIdx2 = Math.min(Integer.parseInt(idx[1]), slideCount);
                    for (int i3 = Math.max(startIdx2, 1); i3 < endIdx2; i3++) {
                        slideIdx.add(Integer.valueOf(i3 - 1));
                    }
                }
            }
        }
        return slideIdx;
    }
}
