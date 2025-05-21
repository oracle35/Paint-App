import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
//me facilita la vida ponerlo asi pero si lo gustas cambiar por cada uno de los imports no tengo tema mi compu si jajajaja

public class Sprint4 {
    public static void main(String[] args) {
        new Sprint4();
    }

    enum Tool {
        PENCIL, RECTANGLE, OVAL, ARC, ERASER
    }

    // Colores 
    private static final Color[] COLOR_PALETTE = {
        Color.BLACK, Color.DARK_GRAY, Color.GRAY, 
        Color.WHITE, Color.RED, Color.GREEN, Color.BLUE,
        Color.YELLOW, Color.MAGENTA,Color.ORANGE, Color.CYAN
    };
    //Formas
    class ColoredShape {
        Shape shape;
        Color color;
        Stroke stroke;
        
        public ColoredShape(Shape shape, Color color, Stroke stroke) {
            this.shape = shape;
            this.color = color;
            this.stroke = stroke;
        }
    }
    // Panel de dibujo
    class DrawingPanel extends JPanel {
        private List<ColoredShape> shapes = new ArrayList<>();
        private Point startPoint;
        private Shape previewShape = null;
        private Tool currentTool = Tool.PENCIL;
        private Color currentColor = Color.BLACK;

        public DrawingPanel() {
            setBackground(Color.WHITE);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    startPoint = e.getPoint();
                    previewShape = null;
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (previewShape != null) {
                        addShape(previewShape);
                        previewShape = null;
                        repaint();
                    }
                }
            });
            // Sensor de movimiento del mouse
            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    Point end = e.getPoint();
                    switch (currentTool) {
                        case PENCIL:
                            Shape line = new Line2D.Double(startPoint, end);
                            addShape(line);
                            startPoint = end;
                            break;
                        case RECTANGLE:
                            previewShape = crearRectangulo(startPoint, end);
                            break;
                        case OVAL:
                            previewShape = crearOvalo(startPoint, end);
                            break;
                        case ARC:
                            previewShape = crearArco(startPoint, end);
                            break;
                        case ERASER:
                            Shape eraserLine = new Line2D.Double(startPoint, end);
                            addShape(eraserLine, Color.WHITE, new BasicStroke(10));
                            startPoint = end;
                            break;
                    }
                    repaint();
                }
            });
        }

        private void addShape(Shape shape) {
            addShape(shape, currentColor, new BasicStroke(2));
        }
    
        private void addShape(Shape shape, Color color, Stroke stroke) {
            shapes.add(new ColoredShape(shape, color, stroke));
        }
        //Crear rectangulo
        private Shape crearRectangulo(Point p1, Point p2) {
            int x = Math.min(p1.x, p2.x);
            int y = Math.min(p1.y, p2.y);
            int w = Math.abs(p2.x - p1.x);
            int h = Math.abs(p2.y - p1.y);
            return new Rectangle2D.Float(x, y, w, h);
        }
        //Crear ovalo
        private Shape crearOvalo(Point p1, Point p2) {
            int x = Math.min(p1.x, p2.x);
            int y = Math.min(p1.y, p2.y);
            int w = Math.abs(p2.x - p1.x);
            int h = Math.abs(p2.y - p1.y);
            return new Ellipse2D.Float(x, y, w, h);
        }
        //Crear arco
        private Shape crearArco(Point p1, Point p2) {
            int x = Math.min(p1.x, p2.x);
            int y = Math.min(p1.y, p2.y);
            int w = Math.abs(p2.x - p1.x);
            int h = Math.abs(p2.y - p1.y);
            return new Arc2D.Float(x, y, w, h, 0, 180, Arc2D.OPEN);
        }

        public void setCurrentTool(Tool tool) {
            this.currentTool = tool;
        }

        public void setCurrentColor(Color color) {
            this.currentColor = color;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            for (ColoredShape cs : shapes) {
                g2.setColor(cs.color);
                g2.setStroke(cs.stroke);
                g2.draw(cs.shape);
            }

            if (previewShape != null) {
                g2.setColor(currentColor);
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f,
                                              new float[]{5f, 5f}, 0f)); // dashed preview
                g2.draw(previewShape);
            }
        }
    }

    public Sprint4() {
        JFrame frame = new JFrame("Java Paint App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        DrawingPanel drawingPanel = new DrawingPanel();
        frame.add(drawingPanel, BorderLayout.CENTER);

        JPanel toolPanel = new JPanel();
        ButtonGroup toolGroup = new ButtonGroup();

        // Botones de herramientas
        JToggleButton pencilBtn = new JToggleButton("Pencil", true);
        JToggleButton rectBtn = new JToggleButton("Rectangle");
        JToggleButton ovalBtn = new JToggleButton("Oval");
        JToggleButton arcBtn = new JToggleButton("Arc");
        JToggleButton eraserBtn = new JToggleButton("Eraser");
        
        toolGroup.add(pencilBtn); toolPanel.add(pencilBtn);
        toolGroup.add(rectBtn); toolPanel.add(rectBtn);
        toolGroup.add(ovalBtn); toolPanel.add(ovalBtn);
        toolGroup.add(arcBtn); toolPanel.add(arcBtn);
        toolGroup.add(eraserBtn); toolPanel.add(eraserBtn);

        pencilBtn.addActionListener(e -> drawingPanel.setCurrentTool(Tool.PENCIL));
        rectBtn.addActionListener(e -> drawingPanel.setCurrentTool(Tool.RECTANGLE));
        ovalBtn.addActionListener(e -> drawingPanel.setCurrentTool(Tool.OVAL));
        arcBtn.addActionListener(e -> drawingPanel.setCurrentTool(Tool.ARC));
        eraserBtn.addActionListener(e -> drawingPanel.setCurrentTool(Tool.ERASER));

        // Panel de colores
        for (Color color : COLOR_PALETTE) {
            JPanel colorPanel = new JPanel();
            colorPanel.setBackground(color);
            colorPanel.setPreferredSize(new Dimension(24, 24));
            colorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            colorPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            colorPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    drawingPanel.setCurrentColor(color);
                }
            });
            toolPanel.add(colorPanel);
        }

        frame.add(toolPanel, BorderLayout.NORTH);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
