package com.adamheinrich.luxfer;

import com.jhlabs.image.PerspectiveFilter;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;

/**
 *
 * @author adam
 */
public class Cell {

    private Color color;
    private Color colorDesired;
    private float colorStep[] = new float[3];

    Point2D[] points;

    int transformedPointsX[] = new int[4];
    int transformedPointsY[] = new int[4];

    public Cell(int x, int y, int width, int height, Color color) {
        this.color = color;
        this.colorDesired = color;

        points = new Point2D[]{
            new Point2D.Double(x, y),
            new Point2D.Double(x + width, y),
            new Point2D.Double(x + width, y + height),
            new Point2D.Double(x, y + height)
        };
    }

    public void transform(PerspectiveFilter filter, double matrixWidth, double matrixHeight) {
        Point2D dstPt = new Point2D.Double();

        for (int i = 0; i < 4; i++) {
            Point2D srcPt = new Point2D.Double(points[i].getX()/matrixWidth, points[i].getY()/matrixHeight);
            dstPt = filter.getPoint2D(srcPt, dstPt);

            transformedPointsX[i] = (int) Math.round(dstPt.getX());
            transformedPointsY[i] = (int) Math.round(dstPt.getY());
        }
    }

    public void setDesiredColor(Color colorDesired, int animationSteps) {
        this.colorDesired = colorDesired;

        float rgbFrom[] = new float[3];
        float rgbTo[] = new float[3];

        color.getRGBColorComponents(rgbFrom);
        colorDesired.getRGBColorComponents(rgbTo);

        for (int i = 0; i < 3; i++) {
            colorStep[i] = (rgbTo[i] - rgbFrom[i]) / animationSteps;
        }
    }

    public void colorAnimationStep(boolean lastStep) {
        if (lastStep) {
            color = colorDesired;
        } else {
            float[] rgb = new float[3];
            color.getRGBColorComponents(rgb);

            for (int i = 0; i < 3; i++) {
                rgb[i] += colorStep[i];

                if (rgb[i] > 1) {
                    rgb[i] = 1;
                } else if (rgb[i] < 0) {
                    rgb[i] = 0;
                }
            }

            color = new Color(rgb[0], rgb[1], rgb[2]);
        }
    }

    public void draw(Graphics g, boolean fill) {
        if (fill) {
            g.setColor(color);
            g.fillPolygon(transformedPointsX, transformedPointsY, 4);
        } else {
            g.setColor(Color.RED);
            g.drawPolygon(transformedPointsX, transformedPointsY, 4);
        }
    }
}
