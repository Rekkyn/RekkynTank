package rekkyn.tank;

import java.util.*;

public class Skeleton {
    
    public HashMap<Point, Segment> segments = new HashMap<Point, Segment>();
    
    public Skeleton() {
        segments.put(new Point(0, 0), new Heart());
    }
    
    public Skeleton addSegment(int x, int y) {
        Point p = new Point(x, y);
        Iterator it = segments.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            if (p.equals(pairs.getKey())) {
                return this;
            }
        }
        
        segments.put(p, new Segment());
        return this;
    }
    
    public class Segment {
    }
    
    public class Heart extends Segment {
        
    }
    
    public class Point {
        int x, y;
        
        public Point(int x, int y) {
            if (x >= y) {
                this.x = x;
                this.y = y;
            } else {
                this.x = y;
                this.y = x;
            }
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Point) {
                if (x == ((Point) obj).x && y == ((Point) obj).y) return true;
            }
            return false;
        }
    }
}