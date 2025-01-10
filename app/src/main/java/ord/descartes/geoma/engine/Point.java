package ord.descartes.geoma.engine;

public class Point implements Cloneable {
    private double x;
    private double y;

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            Point p = (Point) obj;
            return x == p.getX() && y == p.getY();
        } return false;
    }

    public double distance(Point p) {
        return Math.sqrt(Math.pow(x - p.getX(), 2) + Math.pow(y - p.getY(), 2));
    }

    public Point minus(double scalar) {
        return new Point(
                x - scalar,
                y - scalar
        );
    }

    public Point times(double scalar) {
        return new Point(
                x * scalar,
                y * scalar
        );
    }

    public Point minus(Point other) {
        return new Point(
                x - other.x,
                y - other.y
        );
    }

    public Point abs(){
        return new Point(Math.abs(x), Math.abs(y));
    }

    public Point plus(double scalar) {
        return new Point(
                x + scalar,
                y + scalar
        );
    }

    public Point divide(double scalar) {
        return new Point(
                x / scalar,
                y / scalar
        );
    }

    public Point plus(Point other) {
        return new Point(
                x + other.x,
                y + other.y
        );
    }

    public boolean isGreaterX(Point point) {
        return this.x > point.x;
    }

    public boolean isGreaterY(Point point) {
        return this.y > point.y;
    }

    public boolean isLessX(Point point) {
        return this.x < point.x;
    }

    public boolean isLessY(Point point) {
        return this.y < point.y;
    }

    @Override
    public Point clone() {
        try {
            return (Point) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
