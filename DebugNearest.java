public class DebugNearest {
    public static void main(String[] args) {
        edu.princeton.cs.algs4.In in = new edu.princeton.cs.algs4.In("Dados/tsp10.txt");
        double width = in.readDouble();
        double height = in.readDouble();
        double[] raw = in.readAllDoubles();
        int offset = (raw.length % 2 != 0) ? 1 : 0;
        int n = (offset == 1) ? (int) raw[0] : raw.length / 2;
        tour t = new tour();
        for (int i = 0; i < n; i++) {
            Point p = new Point(raw[offset + i * 2], raw[offset + i * 2 + 1]);
            t.insertNearest(p);
            System.out.println(t);
            System.out.println("Tour length = " + t.length());
            System.out.println();
        }
    }
}
