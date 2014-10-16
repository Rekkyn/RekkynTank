package rekkyn.tank.skeleton;

import rekkyn.tank.Colours;
import rekkyn.tank.skeleton.Skeleton.ElementType;

public class Eye extends Element {
    
    public int numCells;
    public int raysPerCell;
    
    public Eye() {
        type = ElementType.EDGE;
        colour = Colours.getDark();
        
    }
    
}
