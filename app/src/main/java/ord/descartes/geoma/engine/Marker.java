package ord.descartes.geoma.engine;


/**
 * Indicates the location of a navigator
 */
public class Marker extends Location implements Cloneable {
    public Marker(Point origin) {
        super(origin);
    }

//    @Override
//    public void draw(Graphics2D g) {
//        final int size1 = 4, size2 = 16;
//        final Point start1 = origin.minus((double) size1 / 2), start2 = origin.minus((double) size2 / 2);
//        Color c = new Color(Color.blue.getRed(), Color.blue.getGreen(), Color.blue.getBlue(), 100);
//        g.setColor(c);
//        g.fillArc((int) start2.getX(), (int) start2.getY(), size2, size2, 0, 360);
//        g.setColor(Color.blue);
//        g.fillArc((int) start1.getX(), (int) start1.getY(), size1, size1, 0, 360);
//    }

    @Override
    public Marker clone() {
        try {
            Marker marker = (Marker) super.clone();
            marker.origin = origin.clone();
            return marker;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
