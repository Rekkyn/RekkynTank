package rekkyn.tank;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

public class Skeleton {
    
    public Creature creature;
    
    public List<Segment> segments = new ArrayList<Segment>();
    
    public Skeleton(Creature creature) {
        this.creature = creature;
        segments.add(new Heart(0, 0, creature));
    }
    
    public Skeleton addSegment(int x, int y) {
        for (Segment s : segments) {
            if (s.x == x && s.y == y || s.x == y && s.y == x) return this;
        }
        
        segments.add(new Segment(x, y, creature));
        if (x != y) {
            segments.add(new Segment(y, x, creature));
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
        public Creature creature;
        
        public Vec2 contact;
        
        public Segment(int x, int y, Creature creature) {
            this.x = x;
            this.y = y;
            this.creature = creature;
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
                Segment s = getSegment(y, x);
                s.addElement(new Motor(s), 8);
            }
            return this;
        }
        
        public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
            g.setColor(Color.yellow);
            if (contact != null)
 g.fillOval(contact.x - 0.0625F, -contact.y - 0.0625F, 0.125F, 0.125F);
        }
    }
    
    public class Heart extends Segment {
        
        public Heart(int x, int y, Creature creature) {
            super(x, y, creature);
        }
    }
    
    public class Element {
        public ElementType type;
        public Segment segment;
        
        public Element(Segment s) {
            segment = s;
        }
        
        public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {}
        
        public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {}
        
    }
    
    public class Motor extends Element {
        
        public float power = 0;
        
        public Motor(Segment s) {
            super(s);
            type = ElementType.CENTRE;
        }
        
        @Override
        public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
            creature.body.applyForce(new Vec2((float) (Math.cos(creature.angle) * power), (float) (Math.sin(creature.angle) * power)),
                    creature.body.getWorldPoint(creature.getPosOnBody(segment.x, segment.y)));
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
    
    public class Mouth extends Element {
        
        public Mouth(Segment s) {
            super(s);
            type = ElementType.EDGE;
        }
        
    }
    
    public enum ElementType {
        EDGE,
        CENTRE
    }
}