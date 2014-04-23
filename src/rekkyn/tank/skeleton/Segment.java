package rekkyn.tank.skeleton;

import rekkyn.tank.skeleton.Skeleton.ElementType;

public class Segment {
    
    public int x, y;
    public Element[] elements = new Element[9];
    public Skeleton skeleton;
    
    public Segment(int x, int y, Skeleton skeleton) {
        this.x = x;
        this.y = y;
        this.skeleton = skeleton;
    }
    
    public Segment addElement(Element e, int location) {
        if (this instanceof Heart) {
            System.err.println("You can't add elements to hearts.");
            return this;
        }
        if (e.type == ElementType.CENTRE && location != 8 || e.type == ElementType.EDGE && location == 8) {
            System.err.println("Invalid placement for element: " + e.toString());
            return this;
        }
        if (elements[location] != null) {
            System.err.println("Tried to put an element in a taken spot;");
            return this;
        }
        elements[location] = e;
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
    
    @Deprecated
    public Segment() {}
}