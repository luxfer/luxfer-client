package com.adamheinrich.luxfer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import javax.swing.JComponent;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.KeyEventDispatcher;
import java.awt.Toolkit;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class JLuxfer extends JComponent {

    private Config config;

    private boolean editMode = false;
    private int currentEditedPoint = -1;
    private boolean drawEditMesh = true;
    
    private boolean mirrorX = false;
    private boolean mirrorY = false;

    private int animationStepsCount = 10;
    private int animationStep = 0;
    private Timer animationTimer;

    private boolean canvasInitialized = false;

    private int cellWidth;
    private int cellHeight;
    private int cellPadX;
    private int cellPadY;

    private CellMatrix matrix;

    Point2D cornerPoints[];
    
    private Requester requester;
    private String url;

    public JLuxfer() {
        MouseAdapter ma = new MyListener();
        addMouseListener(ma);
        addMouseMotionListener(ma);

        config = new Config("luxfer.conf");

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_RELEASED) {
                    if (e.getKeyCode() == 27) { // ESC
                        System.exit(0);
                    } else {
                        char c = Character.toLowerCase(e.getKeyChar());

                        if (c == 'd') {
                            System.out.println("Deleting config file...");
                            config.delete();
                            loadConfig();
                            editMode = false;
                            repaint();
                        }

                        if (c == 'r') {
                            System.out.println("Reloading config file...");
                            loadConfig();
                            repaint();
                        }

                        if (c == 'r') {
                            animationStep = 0;
                            updateColors();
                            animationTimer.start();
                        }

                        if (c >= '1' && c <= '4') {
                            editMode = true;
                            currentEditedPoint = c - '1';
                            repaint();
                        }

                        if (c == 'e') {
                            currentEditedPoint = -1;
                            editMode = !editMode;

                            if (!editMode) {
                                matrix.setCornerPoints(cornerPoints);
                                System.out.println("Saving configuration.");
                                saveConfig();
                            }

                            repaint();
                        }

                        if (editMode) {
                            if (c == 'x') {
                                mirrorX = !mirrorX;
                                repaint();
                            } else if (c == 'y') {
                                mirrorY = !mirrorY;
                                repaint();
                            } else if (c == 'm') {
                                drawEditMesh = !drawEditMesh;
                                repaint();
                            }
                        }
                    }
                }
                return false;
            }
        });

        // Hide cursor
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
        setCursor(blankCursor);

        animationTimer = new Timer(100, new AnimationTimerListener());
        
        requester = new Requester();
        requester.init(this);
    }
    
    public CellMatrix getMatrix() {
        return matrix;
    }

    private void loadConfig() {
        config.load();
        
        url = config.getString("url", Requester.DEFAULT_URL);
        
        requester.stop();
        requester.setUrl(url);
        requester.start();

        mirrorX = config.getBoolean("mirrorX", mirrorX);
        mirrorY = config.getBoolean("mirrorY", mirrorY);
        drawEditMesh = config.getBoolean("drawEditMesh", drawEditMesh);

        int colsCount = config.getInteger("colsCount", 5);
        int rowsCount = config.getInteger("rowsCount", 10);

        cellWidth = config.getInteger("cellWidth", 20);
        cellHeight = config.getInteger("cellHeight", 20);
        cellPadX = config.getInteger("cellPadX", 1);
        cellPadY = config.getInteger("cellPadY", 1);

        matrix = new CellMatrix(rowsCount, colsCount, cellWidth, cellHeight, cellPadX, cellPadY);

        animationTimer.setDelay(config.getInteger("animationDelay", 40));
        animationStepsCount = config.getInteger("animationSteps", 10);

        Point2D defaultCornerPoints[] = new Point2D[]{
            new Point2D.Double(0, 0),
            new Point2D.Double(getWidth(), 0),
            new Point2D.Double(getWidth(), getHeight()),
            new Point2D.Double(0, getHeight())
        };

        cornerPoints = config.getPoints("cornerPoints", 4, defaultCornerPoints);
        matrix.setCornerPoints(cornerPoints);
        
        requester.init(this);
    }
    
    public int getAnimationStepsCount() {
        return animationStepsCount;
    }

    private void saveConfig() {
        config.setString("url", url);
        
        config.setBoolean("mirrorX", mirrorX);
        config.setBoolean("mirrorY", mirrorY);
        config.setBoolean("drawEditMesh", drawEditMesh);

        config.setInteger("colsCount", matrix.getColumnCount());
        config.setInteger("rowsCount", matrix.getRowCount());

        config.setInteger("cellWidth", cellWidth);
        config.setInteger("cellHeight", cellHeight);
        config.setInteger("cellPadX", cellPadY);
        config.setInteger("cellPadY", cellPadX);

        config.setInteger("animationSteps", animationTimer.getDelay());
        config.setInteger("animationSteps", animationStepsCount);

        config.setPoints("cornerPoints", cornerPoints);

        config.save();
    }

    public void updateColors() {        
        animationStep = 0;
        animationTimer.start();
        
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        int w = getWidth();
        int h = getHeight();

        if (!canvasInitialized && w > 0) {
            canvasInitialized = true;
            loadConfig();
        }

        if (canvasInitialized) {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, w, h);

            if (editMode) {
                int r = 40;

                g2.setStroke(new BasicStroke(2.0f));

                if (drawEditMesh) {
                    matrix.draw(g2, false);
                }
                
                g2.setStroke(new BasicStroke(6.0f));

                for (int i = 0; i < cornerPoints.length; i++) {

                    if (i == currentEditedPoint) {
                        g2.setColor(Color.GREEN);
                    } else {
                        g2.setColor(Color.RED);
                    }

                    int x = (int) cornerPoints[i].getX();
                    int y = (int) cornerPoints[i].getY();

                    g2.drawOval(x - r, y - r, r * 2, r * 2);

                    g2.setColor(Color.WHITE);

                    x -= r * 1 / 2;
                    y += r * 1 / 2;

                    g2.setFont(g2.getFont().deriveFont(r * 1.5f));
                    g2.drawString("" + (i + 1), x, y);
                }
            } else {
                matrix.draw(g2, true);
            }
        }
    }

    private class AnimationTimerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            animationStep++;

            if (animationStep < animationStepsCount) {
                matrix.colorAnimationStep(false);
            } else {
                matrix.colorAnimationStep(true);
                animationTimer.stop();
            }

            repaint();
        }

    }

    public class MyListener extends MouseAdapter {

        @Override
        public void mouseMoved(MouseEvent me) {
            if (currentEditedPoint != -1) {
                int x = mirrorX ? getWidth() - me.getX() : me.getX();
                int y = mirrorY ? getHeight() - me.getY() : me.getY();
                
                cornerPoints[currentEditedPoint] = new Point2D.Double(x, y);
                matrix.setCornerPoints(cornerPoints);
            }

            repaint();
        }

        @Override
        public void mouseClicked(MouseEvent me) {

            if (me.getButton() == MouseEvent.BUTTON3) {
                if (editMode) {
                    System.out.println("Saving configuration.");
                    saveConfig();
                    editMode = false;

                    matrix.setCornerPoints(cornerPoints);
                } else {
                    editMode = true;
                }

            } else if (me.getButton() == MouseEvent.BUTTON1) {
                if (currentEditedPoint > -1) {
                    currentEditedPoint = -1;
                }
            }

            repaint();
        }
    }
}
