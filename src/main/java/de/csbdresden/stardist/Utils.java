package de.csbdresden.stardist;

import de.lighti.clipper.Path;
import de.lighti.clipper.Point.LongPoint;
import plugins.kernel.roi.roi2d.ROI2DPoint;
import plugins.kernel.roi.roi2d.ROI2DPolygon;
import plugins.kernel.roi.roi2d.ROI2DRectangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Utils {

    public static ROI2DPolygon toROI2DPolygon(Path poly, float S) {
        int n = poly.size();
        float x;
        float y;
        List<java.awt.geom.Point2D> points = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            LongPoint p = poly.get(i);
            x = (0.5f + p.getX() / S);
            y = (0.5f + p.getY() / S);
            java.awt.geom.Point2D.Float point = new java.awt.geom.Point2D.Float(x, y);
            points.add(point);
        }
        return new ROI2DPolygon(points);
    }
    
    public static ROI2DPoint toPointRoi(Point2D o, float S) {
        return new ROI2DPoint(0.5f + o.x / S, 0.5f + o.y / S);
    }

    public static ROI2DRectangle toBoxRoi(Box2D bbox, float S) {
        double xmin = 0.5 + bbox.xmin / S;
        double xmax = 0.5 + bbox.xmax / S;
        double ymin = 0.5 + bbox.ymin / S;
        double ymax = 0.5 + bbox.ymax / S;
        return new ROI2DRectangle(xmin, ymin, xmax - xmin, ymax - ymin);
    }

    static double[] rayAngles(int n) {
        double[] angles = new double[n];
        double st = (2*Math.PI)/n;
        for (int i = 0; i < n; i++) angles[i] = st*i;
        return angles;
    }

    static List<Integer> argsortDescending(final List<Float> list) {
        Integer[] indices = new Integer[list.size()];
        for (int i = 0; i < indices.length; i++) indices[i] = i;
        Arrays.sort(indices, new Comparator<Integer>() {
            @Override
            public int compare(Integer i, Integer j) {
                return -Float.compare(list.get(i), list.get(j));
            }
        });
        return Arrays.asList(indices);
    }
}
