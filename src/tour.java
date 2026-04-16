public class tour {
    private Node head;
    private int size;

    private static class Node {
        Point p;
        Node next;

        Node(Point p) {
            this.p = p;
            this.next = null;
        }
    }

    public tour() {
        head = null;
        size = 0;
    }

    public void insertNearest(Point p) {
        if (size == 0) {
            head = new Node(p);
            head.next = head;
            size = 1;
            return;
        }

        Node closest = head;
        double minDist = head.p.distanceTo(p);
        Node current = head.next;
        for (int i = 1; i < size; i++) {
            double dist = current.p.distanceTo(p);
            if (dist < minDist) {
                minDist = dist;
                closest = current;
            }
            current = current.next;
        }

        Node newNode = new Node(p);
        newNode.next = closest.next;
        closest.next = newNode;
        size++;
    }

    public void insertSmallest(Point p) {
        if (size == 0) {
            head = new Node(p);
            head.next = head;
            size = 1;
            return;
        }

        insertAtSmallestIncrease(p);
    }

    private void insertAtSmallestIncrease(Point p) {
        Node bestPrev = null;
        double minIncrease = Double.MAX_VALUE;

        Node prev = head;
        for (int i = 0; i < size; i++) {
            Node next = prev.next;
            double increase = prev.p.distanceTo(p) + p.distanceTo(next.p) - prev.p.distanceTo(next.p);
            if (increase < minIncrease) {
                minIncrease = increase;
                bestPrev = prev;
            }
            prev = next;
        }

        Node newNode = new Node(p);
        newNode.next = bestPrev.next;
        bestPrev.next = newNode;
        size++;
    }

    public double length() {
        if (size <= 1) return 0.0;
        double total = 0.0;
        Node current = head;
        for (int i = 0; i < size; i++) {
            total += current.p.distanceTo(current.next.p);
            current = current.next;
        }
        return total;
    }

    public int size() {
        return size;
    }

    public String toString() {
        if (size == 0) return "";
        StringBuilder sb = new StringBuilder();
        Node current = head;
        for (int i = 0; i < size; i++) {
            sb.append(current.p.toString());
            if (i < size - 1) sb.append(" -> ");
            current = current.next;
        }
        return sb.toString();
    }

    // For TSPVisualizer to iterate
    public Iterable<Point> points() {
        return new Iterable<Point>() {
            public java.util.Iterator<Point> iterator() {
                return new java.util.Iterator<Point>() {
                    private Node current = head;
                    private int count = 0;

                    public boolean hasNext() {
                        return count < size;
                    }

                    public Point next() {
                        Point p = current.p;
                        current = current.next;
                        count++;
                        return p;
                    }
                };
            }
        };
    }
}
