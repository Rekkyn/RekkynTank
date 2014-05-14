package rekkyn.tank.skeleton;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Polygon;

public class Segment {
    
    public int x, y;
    public Element[] elements = new Element[9];
    public Skeleton skeleton;
    
    public Segment(int x, int y, Skeleton skeleton) {
        this.x = x;
        this.y = y;
        this.skeleton = skeleton;
        for (int i = 0; i < elements.length; i++) {
            elements[i] = new BlankElement();
        }
    }
    
    public Segment addMotor(boolean mirror) {
        skeleton.addElement(new Motor(this), x, y, 8);
        if (x != y && mirror) {
            Segment s = skeleton.getSegment(y, x);
            skeleton.addElement(new Motor(s), s.x, s.y, 8);
        }
        return this;
    }
    
    public Segment removeElement(int pos) {
        elements[pos] = new BlankElement();
        return this;
    }
    
    public void render(Graphics g) {
        for (int i = 0; i <= 8; i++) {
            if (!(elements[i] instanceof BlankElement)) {
                g.setColor(elements[i].colour);
                renderElement(i, g);
            }
        }
    }
    
    public void renderElement(int pos, Graphics g) {
        Polygon p = new Polygon();
        switch (pos) {
            case 0:
                p.addPoint(0.5F, -0.5F);
                p.addPoint(0.5F, 0);
                p.addPoint(0.25F, 0);
                p.addPoint(0.25F, -0.25F);
                break;
            case 1:
                p.addPoint(0.5F, 0.5F);
                p.addPoint(0.5F, 0);
                p.addPoint(0.25F, 0);
                p.addPoint(0.25F, 0.25F);
                break;
            case 2:
                p.addPoint(0, 0.5F);
                p.addPoint(0, 0.25F);
                p.addPoint(0.25F, 0.25F);
                p.addPoint(0.5F, 0.5F);
                break;
            case 3:
                p.addPoint(0, 0.5F);
                p.addPoint(0, 0.25F);
                p.addPoint(-0.25F, 0.25F);
                p.addPoint(-0.5F, 0.5F);
                break;
            case 4:
                p.addPoint(-0.5F, 0.5F);
                p.addPoint(-0.5F, 0);
                p.addPoint(-0.25F, 0);
                p.addPoint(-0.25F, 0.25F);
                break;
            case 5:
                p.addPoint(-0.5F, -0.5F);
                p.addPoint(-0.5F, 0);
                p.addPoint(-0.25F, 0);
                p.addPoint(-0.25F, -0.25F);
                break;
            case 6:
                p.addPoint(0, -0.5F);
                p.addPoint(0, -0.25F);
                p.addPoint(-0.25F, -0.25F);
                p.addPoint(-0.5F, -0.5F);
                break;
            case 7:
                p.addPoint(0, -0.5F);
                p.addPoint(0, -0.25F);
                p.addPoint(0.25F, -0.25F);
                p.addPoint(0.5F, -0.5F);
                break;
            case 8:
                elements[8].render(g);
        }
        g.fill(p);
    }
    
    @Deprecated
    public Segment() {}
}