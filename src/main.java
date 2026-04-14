import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;

public class main {

    public static void main(String[] args) {
        if (args.length == 0) {
            StdOut.println("Usage: java main <filename>");
            return;
        }

        In in = new In(args[0]);

        double width = in.readDouble();
        double height = in.readDouble();

        ArrayList<Point> pointList = new ArrayList<>();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            pointList.add(new Point(x, y));
        }

        int n = pointList.size();
        Point[] points = pointList.toArray(new Point[0]);

        // Nearest Neighbor Heuristic
        StdOut.println("=== Usando Nearest Neighbor Heuristic ===");
        tour t1 = new tour();
        for (int i = 0; i < n; i++) {
            t1.insertNearest(points[i]);
            // Removed incremental printing for large files
        }
        StdOut.println("Final: " + n + " points");
        StdOut.println("Tour length = " + t1.length());
        StdOut.println();

        // Smallest Increase Heuristic
        StdOut.println("=== Usando Smallest Increase Heuristic ===");
        tour t2 = new tour();
        for (int i = 0; i < n; i++) {
            t2.insertSmallest(points[i]);
            // Removed incremental printing for large files
        }
        StdOut.println("Final: " + n + " points");
        StdOut.println("Tour length = " + t2.length());

        // Visualize both tours together
        TSPVisualizer.drawCompare(t1, t2, width, height);
    }
}