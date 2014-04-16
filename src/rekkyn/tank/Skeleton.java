package rekkyn.tank;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

public class Skeleton {
    
    public List<Segment> segments = new ArrayList<Segment>();
    
    public Skeleton() {
        segments.add(new Heart(0, 0));
    }
    
    public Skeleton addSegment(int x, int y) {
        for (Segment s : segments) {
            if (s.x == x && s.y == y || s.x == y && s.y == x) return this;
        }
        
        segments.add(new Segment(x, y));
        if (x != y) {
            segments.add(new Segment(y, x));
        }
        return this;
    }
    
    public Segment getSegment(int x, int y) {
        
        for (Segment s : segments) {
            if (s.x == x && s.y == y) {
                return s;
            }
        }
        System.err.println("Segment not found.");
        return null;
    }
    
    public class Segment {
        
        public int x, y;
        public Element[] elements = new Element[9];
        
        public Segment(int x, int y) {
            this.x = x;
            this.y = y;
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
            addElement(new Motor(), 8);
            if (mirror) {
                getSegment(y, x).addElement(new Motor(), 8);
            }
            return this;
        }
    }
    
    public class Heart extends Segment {
        
        public Heart(int x, int y) {
            super(x, y);
        }
    }
    
    public class Element {
        public ElementType type;
        
        public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {}
    }
    
    public class Motor extends Element {
        public Motor() {
            type = ElementType.CENTRE;
        }
        
        @Override
        public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
            g.setColor(Color.black);
            g.fillRect(-0.25F, -0.25F, 0.5F, 0.5F);
        }
        
        @Override
        public String toString() {
            return "motor";
        }
    }
    
    public enum ElementType {
        EDGE,
        CENTRE
    }
}