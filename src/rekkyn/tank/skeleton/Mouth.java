package rekkyn.tank.skeleton;

import org.jbox2d.common.Vec2;

import rekkyn.tank.*;
import rekkyn.tank.skeleton.Skeleton.ElementType;

public class Mouth extends Element {
    
    public int cooldown = 0;
    public int cooldownLength = 70;
    
    public Mouth() {
        type = ElementType.EDGE;
        colour = Colours.getAccent();
    }
    
    @Override
    public void update(Creature c) {
        if (cooldown > 0) cooldown--;
        colour.a = -(float) cooldown / cooldownLength + 1;
    }
    
    @Override
    public void contact(Object o, GameWorld world) {
        if (o instanceof Food && cooldown == 0) {
            Food f = (Food) o;
            for (int i = 0; i < 5; i++) {
                Particle p = world.spawnParticle(f.x, f.y, Colours.getAccent(), 30, 0.125F);
                p.velocity = new Vec2(world.rand.nextFloat() * 4 - 2, world.rand.nextFloat() * 4 - 2);
            }
            f.remove();
            cooldown = cooldownLength;
        }
    }
}
