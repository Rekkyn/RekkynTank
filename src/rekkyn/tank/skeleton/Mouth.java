package rekkyn.tank.skeleton;

import rekkyn.tank.Food;
import rekkyn.tank.skeleton.Skeleton.ElementType;

public class Mouth extends Element {
    
    /** Only use this in the addElement method. */
    @Deprecated
    public Mouth() {}
    
    public Mouth(Segment s) {
        super(s);
        type = ElementType.EDGE;
    }
    
    @Override
    public void contact(Object o) {
        super.contact(o);
        if (o instanceof Food) {
            ((Food) o).remove();
        }
    }
}
