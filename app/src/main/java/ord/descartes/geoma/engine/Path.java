package ord.descartes.geoma.engine;

import java.util.ArrayList;
import java.util.List;

public class Path extends Entity {
    List<Point> points = new ArrayList<>();

    protected boolean isSolution = false;

    public Path(List<Point> points, boolean isSolution) {
        this.points = points;
        this.isSolution = isSolution;
    }

    public Path() {
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public boolean isSolution() {
        return isSolution;
    }

    public void setSolution(boolean solution) {
        isSolution = solution;
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public double getLength() {
        double length = 0;

        for(int i = 0 ; i < points.size()-1 ; i++) {
            length += points.get(i).distance(points.get(i+1));
        }

        return length;
    }


    @Override
    public Point getTopLeft() {
        Point topLeft = points.isEmpty() ? null : points.get(0).clone();
        for (Point point : points.stream().skip(1).toArray(Point[]::new)) {
            if (point.isLessX(topLeft))
                topLeft.setX(point.getX());
            if (point.isLessY(topLeft))
                topLeft.setY(point.getY());
        }
        return topLeft;
    }

    @Override
    public Point getBottomRight() {
        Point bottomRight = points.isEmpty() ? null : points.get(0).clone();

        for (Point point : points) {
            if (point.isGreaterX(bottomRight))
                bottomRight.setX(point.getX());
            if (point.isGreaterY(bottomRight))
                bottomRight.setY(point.getY());
        }
        return bottomRight;
    }

//    @Override
//    public void draw(Graphics2D g) {
//        g.setColor(isSolution ? Color.blue : new Color(10,10,10));
//        for(int i = 0; i < points.size() - 1; i++) {
//            Point start = points.get(i);
//            Point end = points.get(i + 1);
//            g.drawLine((int)start.getX(),(int)start.getY(),(int)end.getX(),(int)end.getY());
//        }
//    }
}
