package rekkyn.tank.skeleton;

import rekkyn.tank.Colours;
import rekkyn.tank.Food;
import rekkyn.tank.skeleton.Skeleton.ElementType;

public class Mouth extends Element {
    
    public Mouth() {
        type = ElementType.EDGE;
        colour = Colours.getAccent();
    }
    
    @Override
    public void contact(Object o) {
        super.contact(o);
        if (o instanceof Food) {
            ((Food) o).remove();
        }
    }
}
