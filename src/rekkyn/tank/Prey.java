package rekkyn.tank;

import org.jbox2d.common.Vec2;

public class Prey extends Food {
    
    public float direction = 0;
    public int turnSpeed = 0;
    
    public Prey(float x, float y, GameWorld world) {
        super(x, y, world);
    }
    
    @Override
    public void update() {
        super.update();
        turnSpeed += rand.nextInt(3) - 1;
        int max = 5;
        if (turnSpeed > max) turnSpeed = max;
        if (turnSpeed < -max) turnSpeed = -max;
        direction += turnSpeed / Math.PI / 16;
        body.applyForceToCenter(Util.rotateVec(new Vec2(3, 0), direction));
        // System.out.println(turnSpeed);
    }
    
}
