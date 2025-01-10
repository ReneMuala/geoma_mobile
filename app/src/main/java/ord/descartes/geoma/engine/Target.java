package ord.descartes.geoma.engine;


public class Target extends Location implements Cloneable {
    public Target(Point origin) {
        super(origin);
    }

//    @Override
//    public void draw(Graphics2D g) {
//        final int size1 = 4, size2 = 16;
//        final Point start1 = origin.minus((double) size1 / 2), start2 = origin.minus((double) size2 / 2);
//        Color base = Color.red;
//        Color c = new Color(base.getRed(), base.getGreen(), base.getBlue(), 100);
//        g.setColor(c);
//        g.fillArc((int) start2.getX(), (int) start2.getY(), size2, size2, 0, 360);
//        g.setColor(base);
//        g.fillArc((int) start1.getX(), (int) start1.getY(), size1, size1, 0, 360);
//    }

    @Override
    public Target clone() {
        try {
            Target clone = (Target) super.clone();
            clone.origin = origin.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
