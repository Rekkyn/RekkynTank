package rekkyn.tank.skeleton;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Polygon;

import rekkyn.tank.skeleton.Skeleton.ElementType;

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
    
    public Segment addElement(Element e, int location) {
        e.segment = this;
        
        if (this instanceof Heart) {
            System.err.println("You can't add elements to hearts.");
            return this;
        }
        
        if (e.type == ElementType.CENTRE && location != 8 || e.type == ElementType.EDGE && location == 8) {
            System.err.println("Invalid placement for element: " + e.toString());
            return this;
        }
        
        if (location == 8) {
            if (!(elements[8] instanceof BlankElement)) {
                System.err.println("Tried to put an element in a taken spot;");
                return this;
            }
            
            elements[8] = e;
            return this;
            
        } else {
            int left = location - 1;
            while (left < 0)
                left += 8;
            int leftTwo = location - 2;
            while (leftTwo < 0)
                leftTwo += 8;
            int right = location + 1;
            while (right > 7)
                right -= 8;
            
            if (!(elements[location] instanceof BlankElement) || !(elements[left] instanceof BlankElement)) {
                System.err.println("Tried to put an element in a taken spot;");
                return this;
            }
            
            boolean sameRight = elements[right].getClass().equals(e.getClass());
            boolean sameLeft = elements[leftTwo].getClass().equals(e.getClass());
            
            if (sameRight && sameLeft) {
                e = elements[right];
                if (!(elements[right] == elements[leftTwo])) {
                    removeElement(leftTwo);
                    addElement(e, leftTwo);
                }
            } else {
                if (sameRight) {
                    e = elements[right];
                }
                if (sameLeft) {
                    e = elements[leftTwo];
                }
            }
            
            elements[location] = elements[left] = e;
            e.locations.add(new Integer[] { x, y, location });
            e.locations.add(new Integer[] { x, y, left });
        }
        return this;
    }
    
    public Segment removeElement(int pos) {
        if (!(elements[pos] instanceof BlankElement)) {
            for (Integer[] locations : elements[pos].locations) {
                skeleton.getSegment(locations[0], locations[1]).elements[locations[2]] = new BlankElement();
            }
        }
        int left = pos - 1;
        while (left < 0)
            left += 8;
        
        if (!(elements[left] instanceof BlankElement)) {
            for (Integer[] locations : elements[left].locations) {
                skeleton.getSegment(locations[0], locations[1]).elements[locations[2]] = new BlankElement();
            }
        }
        return this;
    }
    
    public Segment addMotor(boolean mirror) {
        addElement(new Motor(this), 8);
        if (mirror && x != y) {
            Segment s = skeleton.getSegment(y, x);
            s.addElement(new Motor(s), 8);
        }
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