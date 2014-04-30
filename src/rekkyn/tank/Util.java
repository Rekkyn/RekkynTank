package rekkyn.tank;

import org.jbox2d.common.Vec2;

public class Util {
    
    public static Vec2 rotateVec(Vec2 v, float angle) {
        return new Vec2((float) (v.x * Math.cos(angle) - v.y * Math.sin(angle)), (float) (v.x * Math.sin(angle) + v.y * Math.cos(angle)));
    }
}
