package rekkyn.tank.skeleton;

import java.util.ArrayList;
import java.util.List;

import rekkyn.tank.Creature;

import com.esotericsoftware.kryo.serializers.FieldSerializer.Optional;

public class Skeleton {
    
    @Optional(value = "lol")
    public Creature creature;
    
    public List<Segment> segments = new ArrayList<Segment>();
    
    public Skeleton(Creature creature) {
        this.creature = creature;
        segments.add(new Heart(0, 0, this));
    }
    
    public Skeleton addSegment(int x, int y) {
        for (Segment s : segments) {
            if (s.x == x && s.y == y || s.x == y && s.y == x) return this;
        }
        
        segments.add(new Segment(x, y, this));
        if (x != y) {
            segments.add(new Segment(y, x, this));
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
    
    
    public static enum ElementType {
        EDGE,
        CENTRE
    }

    @Deprecated
    public Skeleton() {}
}