package ord.descartes.geoma.engine;



public class Location extends Entity {
    protected Point origin;

    public Point getOrigin() {
        return origin;
    }

    public Location() {
    }

    public Location(Point origin) {
        this.origin = origin;
    }

    public void setOrigin(Point origin) {
        this.origin = origin;
    }

    @Override
    public Point getTopLeft() {
        return origin;
    }

    @Override
    public Point getBottomRight() {
        return origin;
    }

//    @Override
//    public void draw(Graphics2D g) {
//        final int size1 = 4,size2 = 8;
//        final Point start1 = origin.minus((double) size1 /2), start2 = origin.minus((double) size2 /2);
//        g.setColor(Color.white);
//        g.fillArc((int)start2.getX(), (int)start2.getY(), size2, size2, 0, 360);
//        g.setColor(Color.blue);
//        g.fillArc((int)start1.getX(), (int)start1.getY(), size1, size1, 0, 360);
//    }
}
