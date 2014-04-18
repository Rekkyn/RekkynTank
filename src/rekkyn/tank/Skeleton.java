package rekkyn.tank;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Polygon;
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
            
            if (location == 0) {
                if (elements[0] != null || elements[7] != null) {
                    System.err.println("Tried to put an element in a taken spot;");
                    return this;
                }
                
                elements[0] = elements[7] = e;
                return this;
                
            } else if (location == 8) {
                if (elements[8] != null) {
                    System.err.println("Tried to put an element in a taken spot;");
                    return this;
                }
                
                elements[8] = e;
                return this;
                
            } else {
                if (elements[location] != null || elements[location - 1] != null) {
                    System.err.println("Tried to put an element in a taken spot;");
                    return this;
                }
                
                elements[location] = elements[location - 1] = e;
            }
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
            
            g.setColor(Color.cyan);
            if (elements[0] != null) {
                Polygon p0 = new Polygon();
                p0.addPoint(0.5F, -0.5F);
                p0.addPoint(0.5F, 0);
                p0.addPoint(0.25F, 0);
                p0.addPoint(0.25F, -0.25F);
                g.fill(p0);
            }
            if (elements[1] != null) {
                Polygon p1 = new Polygon();
                p1.addPoint(0.5F, 0.5F);
                p1.addPoint(0.5F, 0);
                p1.addPoint(0.25F, 0);
                p1.addPoint(0.25F, 0.25F);
                g.fill(p1);
            }
            if (elements[2] != null) {
                Polygon p2 = new Polygon();
                p2.addPoint(0, 0.5F);
                p2.addPoint(0, 0.25F);
                p2.addPoint(0.25F, 0.25F);
                p2.addPoint(0.5F, 0.5F);
                g.fill(p2);
            }
            if (elements[3] != null) {
                Polygon p3 = new Polygon();
                p3.addPoint(0, 0.5F);
                p3.addPoint(0, 0.25F);
                p3.addPoint(-0.25F, 0.25F);
                p3.addPoint(-0.5F, 0.5F);
                g.fill(p3);
            }
            if (elements[4] != null) {
                Polygon p4 = new Polygon();
                p4.addPoint(-0.5F, 0.5F);
                p4.addPoint(-0.5F, 0);
                p4.addPoint(-0.25F, 0);
                p4.addPoint(-0.25F, 0.25F);
                g.fill(p4);
            }
            if (elements[5] != null) {
                Polygon p5 = new Polygon();
                p5.addPoint(-0.5F, -0.5F);
                p5.addPoint(-0.5F, 0);
                p5.addPoint(-0.25F, 0);
                p5.addPoint(-0.25F, -0.25F);
                g.fill(p5);
            }
            if (elements[6] != null) {
                Polygon p6 = new Polygon();
                p6.addPoint(0, -0.5F);
                p6.addPoint(0, -0.25F);
                p6.addPoint(-0.25F, -0.25F);
                p6.addPoint(-0.5F, -0.5F);
                g.fill(p6);
            }
            if (elements[7] != null) {
                Polygon p7 = new Polygon();
                p7.addPoint(0, -0.5F);
                p7.addPoint(0, -0.25F);
                p7.addPoint(0.25F, -0.25F);
                p7.addPoint(0.5F, -0.5F);
                g.fill(p7);
            }
            if (elements[8] != null) {
                g.fillRect(-0.25F, -0.25F, 0.5F, 0.5F);
            }
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
        public Color colour;
        
        /** Only use this in the addElement method. */
        @Deprecated
        public Element() {}
        
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
        public String toString() {
            return "motor";
        }
    }
    
    public class Mouth extends Element {
        
        /** Only use this in the addElement method. */
        @Deprecated
        public Mouth() {}
        
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