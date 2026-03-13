package org.apache.poi.sl.draw;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import org.apache.poi.sl.usermodel.GroupShape;
import org.apache.poi.sl.usermodel.StrokeStyle;
import org.apache.poi.sl.usermodel.TableCell;
import org.apache.poi.sl.usermodel.TableShape;
import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.util.Internal;

/* JADX INFO: loaded from: classes.dex */
public class DrawTableShape extends DrawShape {

    @Internal
    public static final int borderSize = 2;

    public DrawTableShape(TableShape<?, ?> shape) {
        super(shape);
    }

    protected Drawable getGroupShape(Graphics2D graphics) {
        if (this.shape instanceof GroupShape) {
            DrawFactory df = DrawFactory.getInstance(graphics);
            return df.getDrawable((GroupShape<?, ?>) this.shape);
        }
        return null;
    }

    @Override // org.apache.poi.sl.draw.DrawShape, org.apache.poi.sl.draw.Drawable
    public void applyTransform(Graphics2D graphics) {
        Drawable d = getGroupShape(graphics);
        if (d != null) {
            d.applyTransform(graphics);
        } else {
            super.applyTransform(graphics);
        }
    }

    @Override // org.apache.poi.sl.draw.DrawShape, org.apache.poi.sl.draw.Drawable
    public void draw(Graphics2D graphics) {
        Drawable d;
        TableShape<?, ?> ts;
        Drawable d2 = getGroupShape(graphics);
        if (d2 != null) {
            d2.draw(graphics);
            return;
        }
        TableShape<?, ?> ts2 = getShape();
        DrawPaint drawPaint = DrawFactory.getInstance(graphics).getPaint(ts2);
        int rows = ts2.getNumberOfRows();
        int cols = ts2.getNumberOfColumns();
        Shape shape = null;
        for (int row = 0; row < rows; row++) {
            int col = 0;
            while (col < cols) {
                TableCell<S, P> cell = ts2.getCell(row, col);
                if (cell != 0 && !cell.isMerged()) {
                    Paint fillPaint = drawPaint.getPaint(graphics, cell.getFillStyle().getPaint());
                    graphics.setPaint(fillPaint);
                    Rectangle2D cellAnc = cell.getAnchor();
                    graphics.fill(cellAnc);
                    TableCell.BorderEdge[] arr$ = TableCell.BorderEdge.values();
                    int len$ = arr$.length;
                    int i$ = 0;
                    while (i$ < len$) {
                        TableCell.BorderEdge edge = arr$[i$];
                        StrokeStyle stroke = cell.getBorderStyle(edge);
                        if (stroke == null) {
                            d = d2;
                            ts = ts2;
                        } else {
                            d = d2;
                            graphics.setStroke(getStroke(stroke));
                            Paint linePaint = drawPaint.getPaint(graphics, stroke.getPaint());
                            graphics.setPaint(linePaint);
                            double x = cellAnc.getX();
                            double y = cellAnc.getY();
                            double w = cellAnc.getWidth();
                            double h = cellAnc.getHeight();
                            int i = AnonymousClass1.$SwitchMap$org$apache$poi$sl$usermodel$TableCell$BorderEdge[edge.ordinal()];
                            ts = ts2;
                            shape = i != 2 ? i != 3 ? i != 4 ? new Line2D.Double(x - 2.0d, y + h, x + w + 2.0d, y + h) : new Line2D.Double(x - 2.0d, y, x + w + 2.0d, y) : new Line2D.Double(x + w, y, x + w, y + h + 2.0d) : new Line2D.Double(x, y, x, y + h + 2.0d);
                            graphics.draw(shape);
                        }
                        i$++;
                        d2 = d;
                        ts2 = ts;
                    }
                }
                col++;
                d2 = d2;
                ts2 = ts2;
            }
        }
        drawContent(graphics);
    }

    /* JADX INFO: renamed from: org.apache.poi.sl.draw.DrawTableShape$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$sl$usermodel$TableCell$BorderEdge;

        static {
            int[] iArr = new int[TableCell.BorderEdge.values().length];
            $SwitchMap$org$apache$poi$sl$usermodel$TableCell$BorderEdge = iArr;
            try {
                iArr[TableCell.BorderEdge.bottom.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$TableCell$BorderEdge[TableCell.BorderEdge.left.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$TableCell$BorderEdge[TableCell.BorderEdge.right.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$TableCell$BorderEdge[TableCell.BorderEdge.top.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    @Override // org.apache.poi.sl.draw.DrawShape, org.apache.poi.sl.draw.Drawable
    public void drawContent(Graphics2D graphics) {
        Drawable d = getGroupShape(graphics);
        if (d != null) {
            d.drawContent(graphics);
            return;
        }
        TableShape<?, ?> ts = getShape();
        DrawFactory df = DrawFactory.getInstance(graphics);
        int rows = ts.getNumberOfRows();
        int cols = ts.getNumberOfColumns();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                TextShape<?, ?> cell = ts.getCell(row, col);
                if (cell != null) {
                    DrawTextShape dts = df.getDrawable(cell);
                    dts.drawContent(graphics);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.poi.sl.draw.DrawShape
    public TableShape<?, ?> getShape() {
        return (TableShape) this.shape;
    }

    public void setAllBorders(Object... args) {
        TableShape<?, ?> table = getShape();
        int rows = table.getNumberOfRows();
        int cols = table.getNumberOfColumns();
        TableCell.BorderEdge[] edges = {TableCell.BorderEdge.top, TableCell.BorderEdge.left, null, null};
        int row = 0;
        while (row < rows) {
            int col = 0;
            while (col < cols) {
                edges[2] = col == cols + (-1) ? TableCell.BorderEdge.right : null;
                edges[3] = row == rows + (-1) ? TableCell.BorderEdge.bottom : null;
                setEdges(table.getCell(row, col), edges, args);
                col++;
            }
            row++;
        }
    }

    public void setOutsideBorders(Object... args) {
        if (args.length == 0) {
            return;
        }
        TableShape<?, ?> table = getShape();
        int rows = table.getNumberOfRows();
        int cols = table.getNumberOfColumns();
        TableCell.BorderEdge[] edges = new TableCell.BorderEdge[4];
        int row = 0;
        while (row < rows) {
            int col = 0;
            while (col < cols) {
                TableCell.BorderEdge borderEdge = null;
                edges[0] = col == 0 ? TableCell.BorderEdge.left : null;
                edges[1] = col == cols + (-1) ? TableCell.BorderEdge.right : null;
                edges[2] = row == 0 ? TableCell.BorderEdge.top : null;
                if (row == rows - 1) {
                    borderEdge = TableCell.BorderEdge.bottom;
                }
                edges[3] = borderEdge;
                setEdges(table.getCell(row, col), edges, args);
                col++;
            }
            row++;
        }
    }

    public void setInsideBorders(Object... args) {
        if (args.length == 0) {
            return;
        }
        TableShape<?, ?> table = getShape();
        int rows = table.getNumberOfRows();
        int cols = table.getNumberOfColumns();
        TableCell.BorderEdge[] edges = new TableCell.BorderEdge[2];
        for (int row = 0; row < rows; row++) {
            int col = 0;
            while (col < cols) {
                TableCell.BorderEdge borderEdge = null;
                edges[0] = (col <= 0 || col >= cols + (-1)) ? null : TableCell.BorderEdge.right;
                if (row > 0 && row < rows - 1) {
                    borderEdge = TableCell.BorderEdge.bottom;
                }
                edges[1] = borderEdge;
                setEdges(table.getCell(row, col), edges, args);
                col++;
            }
        }
    }

    private static void setEdges(TableCell<?, ?> cell, TableCell.BorderEdge[] edges, Object... args) {
        if (cell == null) {
            return;
        }
        for (TableCell.BorderEdge be : edges) {
            if (be != null) {
                if (args.length == 0) {
                    cell.removeBorder(be);
                } else {
                    for (Object o : args) {
                        if (o instanceof Double) {
                            cell.setBorderWidth(be, ((Double) o).doubleValue());
                        } else if (o instanceof Color) {
                            cell.setBorderColor(be, (Color) o);
                        } else if (o instanceof StrokeStyle.LineDash) {
                            cell.setBorderDash(be, (StrokeStyle.LineDash) o);
                        } else if (o instanceof StrokeStyle.LineCompound) {
                            cell.setBorderCompound(be, (StrokeStyle.LineCompound) o);
                        }
                    }
                }
            }
        }
    }
}
