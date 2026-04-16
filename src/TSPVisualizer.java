import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class TSPVisualizer {

    private static final DecimalFormat LENGTH_FORMAT = new DecimalFormat("#0.00");
    private static final Color APP_BACKGROUND = new Color(244, 242, 236);
    private static final Color PANEL_BACKGROUND = new Color(255, 252, 247);
    private static final Color PANEL_BORDER = new Color(214, 210, 201);
    private static final Color TITLE_COLOR = new Color(48, 44, 38);
    private static final Color SUBTITLE_COLOR = new Color(98, 93, 86);
    private static final Color NEAREST_EDGE_COLOR = new Color(79, 123, 88, 145);
    private static final Color SMALLEST_EDGE_COLOR = new Color(60, 96, 156, 145);
    private static final Color POINT_COLOR = new Color(214, 36, 28);
    private static final Color POINT_OUTLINE_COLOR = new Color(255, 248, 240, 230);
    private static final int LARGE_INSTANCE_THRESHOLD = 4000;

    public static void drawCompare(tour nearest, tour smallest, double width, double height) {
        SwingUtilities.invokeLater(() -> {
            if (nearest.size() >= LARGE_INSTANCE_THRESHOLD || smallest.size() >= LARGE_INSTANCE_THRESHOLD) {
                createSingleTourFrame("Nearest Insertion", "Arraste para mover, role o mouse para zoom, clique duplo para resetar.",
                        nearest, width, height, NEAREST_EDGE_COLOR, POINT_COLOR, 980, 760);
                createSingleTourFrame("Smallest Increase", "Arraste para mover, role o mouse para zoom, clique duplo para resetar.",
                        smallest, width, height, SMALLEST_EDGE_COLOR, POINT_COLOR, 980, 760);
            } else {
                createCombinedFrame(nearest, smallest, width, height);
            }
        });
    }

    private static void createCombinedFrame(tour nearest, tour smallest, double width, double height) {
        JFrame frame = new JFrame("Comparacao de Tours");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(APP_BACKGROUND);
        frame.setLayout(new java.awt.GridLayout(1, 2, 16, 0));

        TourPanel nearestPanel = new TourPanel(nearest, width, height, "Nearest Insertion",
                "Insere apos o ponto mais proximo do tour atual.", NEAREST_EDGE_COLOR, POINT_COLOR);
        TourPanel smallestPanel = new TourPanel(smallest, width, height, "Smallest Increase",
                "Escolhe a posicao com menor aumento no comprimento.", SMALLEST_EDGE_COLOR, POINT_COLOR);

        frame.add(wrapPanel(nearestPanel));
        frame.add(wrapPanel(smallestPanel));
        frame.getRootPane().setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        frame.setSize(1500, 820);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void createSingleTourFrame(String title, String subtitle, tour tourData,
                                              double width, double height, Color edgeColor, Color pointColor,
                                              int frameWidth, int frameHeight) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(APP_BACKGROUND);
        frame.add(wrapPanel(new TourPanel(tourData, width, height, title, subtitle, edgeColor, pointColor)));
        frame.getRootPane().setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        frame.setSize(frameWidth, frameHeight);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    private static JPanel wrapPanel(TourPanel panel) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(APP_BACKGROUND);
        container.add(panel, BorderLayout.CENTER);
        return container;
    }

    private static final class TourPanel extends JPanel {
        private final List<Point> points;
        private final double worldWidth;
        private final double worldHeight;
        private final String title;
        private final String subtitle;
        private final Color edgeColor;
        private final Color pointColor;
        private double zoom;
        private double offsetX;
        private double offsetY;
        private java.awt.Point lastMousePosition;

        TourPanel(tour tourData, double width, double height, String title, String subtitle,
                  Color edgeColor, Color pointColor) {
            this.points = copyPoints(tourData);
            this.worldWidth = width;
            this.worldHeight = height;
            this.title = title;
            this.subtitle = subtitle;
            this.edgeColor = edgeColor;
            this.pointColor = pointColor;
            this.zoom = 1.0;
            this.offsetX = 0.0;
            this.offsetY = 0.0;

            setBackground(PANEL_BACKGROUND);
            setPreferredSize(new Dimension(720, 720));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PANEL_BORDER, 1),
                    BorderFactory.createEmptyBorder(14, 14, 14, 14)));

            MouseAdapter navigation = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    lastMousePosition = e.getPoint();
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    lastMousePosition = null;
                    setCursor(Cursor.getDefaultCursor());
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (lastMousePosition == null) {
                        return;
                    }

                    int drawWidth = getWidth() - 28;
                    int drawHeight = getHeight() - 110;
                    if (drawWidth <= 0 || drawHeight <= 0) {
                        return;
                    }

                    double scale = baseScale(drawWidth, drawHeight) * zoom;
                    offsetX += (e.getX() - lastMousePosition.x) / scale;
                    offsetY -= (e.getY() - lastMousePosition.y) / scale;
                    lastMousePosition = e.getPoint();
                    repaint();
                }

                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    int drawWidth = getWidth() - 28;
                    int drawHeight = getHeight() - 110;
                    if (drawWidth <= 0 || drawHeight <= 0) {
                        return;
                    }

                    double oldZoom = zoom;
                    double factor = e.getPreciseWheelRotation() < 0 ? 1.15 : 1.0 / 1.15;
                    zoom = clamp(zoom * factor, 1.0, 60.0);

                    double mouseWorldX = screenToWorldX(e.getX(), drawWidth, oldZoom);
                    double mouseWorldY = screenToWorldY(e.getY(), drawHeight, oldZoom);
                    offsetX = mouseWorldX - (e.getX() - 14 - drawWidth / 2.0) / (baseScale(drawWidth, drawHeight) * zoom);
                    offsetY = mouseWorldY + (e.getY() - 72 - drawHeight / 2.0) / (baseScale(drawWidth, drawHeight) * zoom);
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        zoom = 1.0;
                        offsetX = 0.0;
                        offsetY = 0.0;
                        repaint();
                    }
                }
            };

            addMouseListener(navigation);
            addMouseMotionListener(navigation);
            addMouseWheelListener(navigation);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int innerX = 14;
            int titleY = 24;
            int subtitleY = 44;
            int infoY = getHeight() - 16;
            int drawY = 58;
            int drawWidth = getWidth() - 28;
            int drawHeight = getHeight() - 110;

            g2.setColor(TITLE_COLOR);
            g2.setFont(new Font("SansSerif", Font.BOLD, 20));
            g2.drawString(title, innerX, titleY);

            g2.setColor(SUBTITLE_COLOR);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.drawString(subtitle, innerX, subtitleY);
            g2.drawString("Pontos: " + points.size() + "  |  Comprimento: " + LENGTH_FORMAT.format(length()), innerX, infoY - 18);
            g2.drawString("Zoom: " + LENGTH_FORMAT.format(zoom) + "x  |  Scroll para zoom, arraste para mover", innerX, infoY);

            g2.setColor(new Color(250, 248, 243));
            g2.fillRoundRect(innerX, drawY, drawWidth, drawHeight, 18, 18);
            g2.setColor(PANEL_BORDER);
            g2.drawRoundRect(innerX, drawY, drawWidth, drawHeight, 18, 18);

            g2.clipRect(innerX, drawY, drawWidth, drawHeight);
            drawTour(g2, innerX, drawY, drawWidth, drawHeight);
            g2.dispose();
        }

        private void drawTour(Graphics2D g2, int left, int top, int drawWidth, int drawHeight) {
            if (points.isEmpty()) {
                return;
            }

            double scale = baseScale(drawWidth, drawHeight) * zoom;
            double centerX = left + drawWidth / 2.0;
            double centerY = top + drawHeight / 2.0;
            double viewCenterX = worldWidth / 2.0 + offsetX;
            double viewCenterY = worldHeight / 2.0 + offsetY;

            float strokeWidth = points.size() > 5000 ? 1.0f : points.size() > 1000 ? 1.3f : 2.0f;
            Stroke previousStroke = g2.getStroke();
            g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(edgeColor);

            Point first = points.get(0);
            Point prev = first;
            for (int i = 1; i < points.size(); i++) {
                Point curr = points.get(i);
                g2.draw(new Line2D.Double(
                        toScreenX(prev.getX(), centerX, viewCenterX, scale),
                        toScreenY(prev.getY(), centerY, viewCenterY, scale),
                        toScreenX(curr.getX(), centerX, viewCenterX, scale),
                        toScreenY(curr.getY(), centerY, viewCenterY, scale)));
                prev = curr;
            }
            g2.draw(new Line2D.Double(
                    toScreenX(prev.getX(), centerX, viewCenterX, scale),
                    toScreenY(prev.getY(), centerY, viewCenterY, scale),
                    toScreenX(first.getX(), centerX, viewCenterX, scale),
                    toScreenY(first.getY(), centerY, viewCenterY, scale)));

            double pointRadius = points.size() > 10000 ? 1.1 : points.size() > 4000 ? 1.35 : points.size() > 1000 ? 1.8 : points.size() > 200 ? 2.8 : 3.9;
            double outlineRadius = pointRadius + (points.size() > 4000 ? 0.5 : 1.1);

            for (Point point : points) {
                double screenX = toScreenX(point.getX(), centerX, viewCenterX, scale);
                double screenY = toScreenY(point.getY(), centerY, viewCenterY, scale);
                g2.setColor(POINT_OUTLINE_COLOR);
                g2.fill(new Ellipse2D.Double(screenX - outlineRadius, screenY - outlineRadius,
                        outlineRadius * 2, outlineRadius * 2));
                g2.setColor(pointColor);
                g2.fill(new Ellipse2D.Double(screenX - pointRadius, screenY - pointRadius,
                        pointRadius * 2, pointRadius * 2));
            }

            g2.setStroke(previousStroke);
        }

        private double length() {
            if (points.size() <= 1) {
                return 0.0;
            }

            double total = 0.0;
            Point previous = points.get(0);
            for (int i = 1; i < points.size(); i++) {
                Point current = points.get(i);
                total += previous.distanceTo(current);
                previous = current;
            }
            total += previous.distanceTo(points.get(0));
            return total;
        }

        private double baseScale(int drawWidth, int drawHeight) {
            return Math.min(drawWidth / worldWidth, drawHeight / worldHeight) * 0.94;
        }

        private double toScreenX(double worldX, double centerX, double viewCenterX, double scale) {
            return centerX + (worldX - viewCenterX) * scale;
        }

        private double toScreenY(double worldY, double centerY, double viewCenterY, double scale) {
            return centerY - (worldY - viewCenterY) * scale;
        }

        private double screenToWorldX(int screenX, int drawWidth, double zoomValue) {
            double centerX = 14 + drawWidth / 2.0;
            double scale = baseScale(drawWidth, getHeight() - 110) * zoomValue;
            return worldWidth / 2.0 + offsetX + (screenX - centerX) / scale;
        }

        private double screenToWorldY(int screenY, int drawHeight, double zoomValue) {
            double centerY = 72 + drawHeight / 2.0;
            double scale = baseScale(getWidth() - 28, drawHeight) * zoomValue;
            return worldHeight / 2.0 + offsetY - (screenY - centerY) / scale;
        }
    }

    private static List<Point> copyPoints(tour t) {
        List<Point> copied = new ArrayList<>();
        for (Point point : t.points()) {
            copied.add(point);
        }
        return copied;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}

