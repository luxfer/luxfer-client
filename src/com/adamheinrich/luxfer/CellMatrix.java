package com.adamheinrich.luxfer;

import com.jhlabs.image.PerspectiveFilter;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.geom.Point2D;

public class CellMatrix {

    private int rowCount;

    private int columnCount;

    private Cell cells[][];

    private Point2D cornerPoints[] = new Point2D[4];

    private int width;

    private int height;

    public CellMatrix(int rowCount, int columnCount, int cellWidth, int cellHeight, int cellPaddingX, int cellPaddingY) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;

        cells = new Cell[rowCount][columnCount];

        int x = 0;
        int y = 0;
        int xMax = 0;
        int yMax = 0;

        boolean dark = true;

        for (int i = 0; i < rowCount; i++) {
            x = 0;

            for (int j = 0; j < columnCount; j++) {
                Color color = dark ? Color.BLUE : Color.YELLOW;
                cells[i][j] = new Cell(x, y, cellWidth, cellHeight, color);

                if (x+cellWidth > xMax) {
                    xMax = x+cellWidth;
                }

                if (y+cellHeight > yMax) {
                    yMax = y+cellHeight;
                }

                x += cellWidth + cellPaddingX;
                dark = !dark;
            }

            if (columnCount % 2 == 0) {
                dark = !dark;
            }
            y += cellHeight + cellPaddingY;
        }

        this.width = xMax;
        this.height = yMax;
        
        //setCornerPoints(cornerPointsOriginal);
    }

    public void setCornerPoints(Point2D[] cornerPoints) {
        PerspectiveFilter filter = new PerspectiveFilter();

        filter.setCorners((float) cornerPoints[0].getX(),
                (float) cornerPoints[0].getY(),
                (float) cornerPoints[1].getX(),
                (float) cornerPoints[1].getY(),
                (float) cornerPoints[2].getX(),
                (float) cornerPoints[2].getY(),
                (float) cornerPoints[3].getX(),
                (float) cornerPoints[3].getY()
        );
        
        //filter.setClip(true);
        
        this.cornerPoints = new Point2D[cornerPoints.length];
        for (int i = 0; i < cornerPoints.length; i++) {
            this.cornerPoints[i] = (Point2D)cornerPoints[i].clone();
        }

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                cells[i][j].transform(filter, width, height);
            }
        }
    }

    public void draw(Graphics g, boolean fill) {
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                cells[i][j].draw(g, fill);
            }
        }
    }
    
    
    public void colorAnimationStep(boolean lastStep) {
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                cells[i][j].colorAnimationStep(lastStep);
            }
        }
    }
    
    public int getRowCount() {
        return rowCount;
    }
    
    public int getColumnCount() {
        return columnCount;
    }
    
    public Cell getCell(int row, int col) {
        return cells[row][col];
    }
}
