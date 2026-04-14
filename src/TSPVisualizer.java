import edu.princeton.cs.algs4.StdDraw;
import java.awt.Color;
import java.text.DecimalFormat;

public class TSPVisualizer {

    private static final DecimalFormat LENGTH_FORMAT = new DecimalFormat("#0.00");

    public static void drawCompare(tour nearest, tour smallest, double width, double height) {
        double margin = 20;
        double bottomSpace = 80;
        double topSpace = 40;
        double totalWidth = width * 2 + margin * 3;
        double totalHeight = height + bottomSpace + topSpace;
        double leftXOffset = margin;
        double rightXOffset = width + margin * 2;
        double graphYOffset = bottomSpace;

        StdDraw.setCanvasSize(1200, 700);
        StdDraw.setXscale(0, totalWidth);
        StdDraw.setYscale(0, totalHeight);
        StdDraw.clear(StdDraw.WHITE);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenRadius(0.005);

        // Draw panel backgrounds
        StdDraw.setPenRadius(0.0);
        StdDraw.setPenColor(new Color(245, 245, 245));
        StdDraw.filledRectangle(leftXOffset + width / 2, graphYOffset + height / 2, width / 2, height / 2);
        StdDraw.filledRectangle(rightXOffset + width / 2, graphYOffset + height / 2, width / 2, height / 2);

        // Draw panel titles
        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(leftXOffset + width / 2, totalHeight - topSpace / 2, "Nearest Neighbor");
        StdDraw.text(rightXOffset + width / 2, totalHeight - topSpace / 2, "Smallest Increase");

        // Draw panel separator line
        StdDraw.setPenRadius(0.002);
        StdDraw.line(rightXOffset - margin / 2, 0, rightXOffset - margin / 2, totalHeight);

        // Draw nearest neighbor tour in left panel
        drawTour(nearest, StdDraw.BOOK_RED, leftXOffset, graphYOffset);

        // Draw smallest increase tour in right panel
        drawTour(smallest, StdDraw.BOOK_BLUE, rightXOffset, graphYOffset);

        // Legend
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.textLeft(leftXOffset, totalHeight - topSpace, "Nearest Neighbor = red line, red points");
        StdDraw.textLeft(rightXOffset, totalHeight - topSpace, "Smallest Increase = blue line, blue points");

        // Draw tour length info at bottom of each panel
        drawRouteInfo(nearest, leftXOffset, bottomSpace - 20, "Nearest");
        drawRouteInfo(smallest, rightXOffset, bottomSpace - 20, "Smallest");

        StdDraw.show();
    }

    private static void drawTour(tour t, Color color, double xOffset, double yOffset) {
        StdDraw.setPenRadius(0.002);
        StdDraw.setPenColor(color);

        java.util.Iterator<Point> it = t.points().iterator();
        if (it.hasNext()) {
            Point first = it.next();
            Point prev = first;
            drawPoint(prev, xOffset, yOffset, color);
            while (it.hasNext()) {
                Point curr = it.next();
                StdDraw.setPenColor(color);
                StdDraw.line(prev.getX() + xOffset, prev.getY() + yOffset,
                             curr.getX() + xOffset, curr.getY() + yOffset);
                drawPoint(curr, xOffset, yOffset, color);
                prev = curr;
            }
            StdDraw.setPenColor(color);
            StdDraw.line(prev.getX() + xOffset, prev.getY() + yOffset,
                         first.getX() + xOffset, first.getY() + yOffset);
        }
    }

    private static void drawPoint(Point p, double xOffset, double yOffset, Color color) {
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(color);
        StdDraw.filledCircle(p.getX() + xOffset, p.getY() + yOffset, 4);
    }

    private static void drawRouteInfo(tour t, double xOffset, double textMargin, String label) {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        double lineY = textMargin;
        StdDraw.textLeft(xOffset, lineY, label + " tour length = " + LENGTH_FORMAT.format(t.length()));
    }
}

