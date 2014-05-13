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
    
    public Skeleton addSegment(Segment s) {
        for (Segment other : segments) {
            if (other.x == s.x && other.y == s.y) return this;
        }
        segments.add(s);
        return this;
    }
    
    public void removeSegment(int x, int y) {
        segments.remove(getSegment(x, y));
        if (x != y) segments.remove(getSegment(y, x));
    }
    
    public void removeSegment(Segment s) {
        segments.remove(getSegment(s.x, s.y));
    }
    
    public Segment getSegment(int x, int y) {
        
        for (Segment s : segments) {
            if (s.x == x && s.y == y) {
                return s;
            }
        }
        // System.err.println("Segment not found.");
        return null;
    }
    
    public Skeleton addElement(Element e, int[][] locations, boolean mirror) {
        Segment seg1 = getSegment(locations[0][0], locations[0][1]);
        Segment seg2 = getSegment(locations[1][0], locations[1][1]);
        int pos1 = locations[0][2];
        int pos2 = locations[1][2];
        if (seg1 instanceof Heart || seg2 instanceof Heart) {
            System.err.println("You can't add elements to hearts.");
            return this;
        }
        
        
        if (!(seg1.elements[pos1] instanceof BlankElement) || !(seg2.elements[pos2] instanceof BlankElement)) {
            System.err.println("Tried to put an element in a taken spot;");
            return this;
        }
        
        int[] leftOfPos1 = getLocationToLeft(locations[0][0], locations[0][1], locations[0][2]);
        int[] leftOfPos2 = getLocationToLeft(locations[1][0], locations[1][1], locations[1][2]);
        
        /*boolean sameRight = elements[right].getClass().equals(e.getClass());
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
        }*/
        
        getSegment(locations[0][0], locations[0][1]).elements[locations[0][2]] = e;
        getSegment(locations[1][0], locations[1][1]).elements[locations[1][2]] = e;
        e.locations.add(locations[0]);
        e.locations.add(locations[1]);
        
        if (mirror) {
            Class<? extends Element> eClass = e.getClass();
            Element e2 = null;
            try {
                e2 = eClass.newInstance();
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            }
            
            addElement(e2, new int[][] { { locations[0][1], locations[0][0], 7 - locations[0][2] },
                    { locations[1][1], locations[1][0], 7 - locations[1][2] } }, false);
        }
        return this;
    }
    
    /** @param segX
     * @param segY
     * @param pos Directional position.
     * @param angle
     * @return */
    public int[][] getLocationsToAdd(int segX, int segY, int pos, float angle) {
        int[][] result = null;
        
        int left = pos - 1;
        while (left < 0)
            left += 8;
        
        if (pos == 1 && getSegment(segX + 1, segY) == null) {
            result = new int[][] { { segX, segY, 0 }, { segX, segY, 1 } };
        } else if (pos == 3 && getSegment(segX, segY - 1) == null) {
            result = new int[][] { { segX, segY, 2 }, { segX, segY, 3 } };
        } else if (pos == 5 && getSegment(segX - 1, segY) == null) {
            result = new int[][] { { segX, segY, 4 }, { segX, segY, 5 } };
        } else if (pos == 7 && getSegment(segX, segY + 1) == null) {
            result = new int[][] { { segX, segY, 6 }, { segX, segY, 7 } };
        } else if (pos % 2 == 0) {
            double[] angles = new double[] { Math.PI / 4, 0, -Math.PI / 4, 0, -3 * Math.PI / 4, 0, 3 * Math.PI / 4 };
            System.out.println(angle);
            if (angle < angles[pos] && onEdge(segX, segY, pos)) {
                result = new int[][] { { segX, segY, pos }, getLocationToLeft(segX, segY, pos) };
            } else if (onEdge(segX, segY, left)) {
                result = new int[][] { { segX, segY, left }, getLocationToRight(segX, segY, left) };
            }
        }
        return result;
    }
    
    /** @param segX
     * @param segY
     * @param pos Slot position.
     * @return */
    public int[] getLocationToLeft(int segX, int segY, int pos) {
        if ((pos + 1) % 2 == 0) {
            int left = pos - 1;
            while (left < 0)
                left += 8;
            return new int[] { segX, segY, left };
        } else if (pos == 0) {
            if (getSegment(segX + 1, segY + 1) != null)
                return new int[] { segX + 1, segY + 1, 3 };
            else if (getSegment(segX, segY + 1) != null)
                return new int[] { segX, segY + 1, 1 };
            else
                return new int[] { segX, segY, 7 };
        } else if (pos == 2) {
            if (getSegment(segX + 1, segY - 1) != null)
                return new int[] { segX + 1, segY - 1, 5 };
            else if (getSegment(segX + 1, segY) != null)
                return new int[] { segX + 1, segY, 3 };
            else
                return new int[] { segX, segY, 1 };
        } else if (pos == 4) {
            if (getSegment(segX - 1, segY - 1) != null)
                return new int[] { segX - 1, segY - 1, 7 };
            else if (getSegment(segX, segY - 1) != null)
                return new int[] { segX, segY - 1, 5 };
            else
                return new int[] { segX, segY, 3 };
        } else if (pos == 6) {
            if (getSegment(segX - 1, segY + 1) != null)
                return new int[] { segX - 1, segY + 1, 1 };
            else if (getSegment(segX - 1, segY) != null)
                return new int[] { segX - 1, segY, 7 };
            else
                return new int[] { segX, segY, 5 };
        }
        return null;
    }
    
    /** @param segX
     * @param segY
     * @param pos Slot position.
     * @return */
    public int[] getLocationToRight(int segX, int segY, int pos) {
        if (pos % 2 == 0) {
            int right = pos + 1;
            while (right > 7)
                right -= 8;
            return new int[] { segX, segY, right };
        } else if (pos == 1) {
            if (getSegment(segX + 1, segY - 1) != null)
                return new int[] { segX + 1, segY - 1, 6 };
            else if (getSegment(segX, segY - 1) != null)
                return new int[] { segX, segY - 1, 0 };
            else
                return new int[] { segX, segY, 2 };
        } else if (pos == 3) {
            if (getSegment(segX - 1, segY - 1) != null)
                return new int[] { segX - 1, segY - 1, 0 };
            else if (getSegment(segX - 1, segY) != null)
                return new int[] { segX - 1, segY, 2 };
            else
                return new int[] { segX, segY, 4 };
        } else if (pos == 5) {
            if (getSegment(segX - 1, segY + 1) != null)
                return new int[] { segX - 1, segY + 1, 2 };
            else if (getSegment(segX, segY + 1) != null)
                return new int[] { segX, segY + 1, 4 };
            else
                return new int[] { segX, segY, 6 };
        } else if (pos == 7) {
            if (getSegment(segX + 1, segY + 1) != null)
                return new int[] { segX + 1, segY + 1, 4 };
            else if (getSegment(segX + 1, segY) != null)
                return new int[] { segX + 1, segY, 6 };
            else
                return new int[] { segX, segY, 0 };
        }
        return null;
    }
    
    /** @param segX
     * @param segY
     * @param pos Slot position.
     * @return */
    public boolean onEdge(int segX, int segY, int pos) {
        if ((pos == 0 || pos == 1) && getSegment(segX + 1, segY) != null) {
            return false;
        }
        if ((pos == 2 || pos == 3) && getSegment(segX, segY - 1) != null) {
            return false;
        }
        if ((pos == 4 || pos == 5) && getSegment(segX - 1, segY) != null) {
            return false;
        }
        if ((pos == 6 || pos == 7) && getSegment(segX, segY + 1) != null) {
            return false;
        }
        return true;
    }
    
    public static enum ElementType {
        EDGE,
        CENTRE
    }
    
    @Deprecated
    public Skeleton() {}
    
}