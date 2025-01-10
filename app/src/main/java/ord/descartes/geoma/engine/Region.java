package ord.descartes.geoma.engine;


public class Region extends Path {
    static Region fromPath(Path path) {
        Region region = new Region();
        region.setPoints(path.getPoints());
        region.setName(path.getName());
        return region;
    }
//    @Override
//    public void draw(Graphics2D g) {
//        g.setColor(isSolution ? Color.blue : Color.red);
//        for(int i = 0; i < points.size() - 1; i++) {
//            Point start = points.get(i);
//            Point end = points.get(i + 1);
//            g.drawLine((int)start.getX(),(int)start.getY(),(int)end.getX(),(int)end.getY());
//        }
//        final var tl = getTopLeft();
//        final var br = getBottomRight();
//        Point center = br.minus(tl).divide(2).plus(tl);
//        g.setFont(new Font("Monospace", Font.BOLD, 8));
//        g.drawString(getName(), (int)center.getX() - getName().length() * 8 / 2, (int)center.getY()+4);
//    }
}
