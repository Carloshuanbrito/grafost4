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
        double[] rawValues = in.readAllDoubles();
        int offset = 0;
        int n;

        if (rawValues.length % 2 != 0) {
            n = (int) rawValues[0];
            offset = 1;
        } else {
            n = rawValues.length / 2;
        }

        ArrayList<Point> pointList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int index = offset + i * 2;
            if (index + 1 >= rawValues.length) {
                StdOut.println("Arquivo invalido: quantidade de coordenadas insuficiente.");
                return;
            }

            double x = rawValues[index];
            double y = rawValues[index + 1];
            pointList.add(new Point(x, y));
        }

        if (offset == 1 && rawValues.length != 1 + 2 * n) {
            StdOut.println("Arquivo invalido: quantidade de pontos lida difere do cabecalho.");
            return;
        }

        Point[] points = pointList.toArray(new Point[0]);

        // Nearest Insertion Heuristic
        StdOut.println("=== Usando Nearest Insertion Heuristic ===");
        tour t1 = new tour();
        for (int i = 0; i < n; i++) {
            t1.insertNearest(points[i]);
        }
        StdOut.println("Final: " + n + " points");
        StdOut.println("Tour length = " + t1.length());
        StdOut.println();

        // Smallest Increase Heuristic
        StdOut.println("=== Usando Smallest Increase Heuristic ===");
        tour t2 = new tour();
        for (Point point : pointList) {
            t2.insertSmallest(point);
        }
        StdOut.println("Final: " + n + " points");
        StdOut.println("Tour length = " + t2.length());

        // Visualize both tours together
        TSPVisualizer.drawCompare(t1, t2, width, height);
    }
}
