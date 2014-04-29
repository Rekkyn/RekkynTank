package rekkyn.tank.skeleton;

import org.jbox2d.common.Vec2;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

import rekkyn.tank.Particle;
import rekkyn.tank.skeleton.Skeleton.ElementType;

public class Motor extends Element {
    
    public float power = 0;
    
    public Motor(Segment s) {
        super(s);
        type = ElementType.CENTRE;
        colour = Color.black;
    }
    
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        Vec2 force = new Vec2((float) (Math.cos(segment.skeleton.creature.angle) * power),
                (float) (Math.sin(segment.skeleton.creature.angle) * power));
        
        Vec2 pos = segment.skeleton.creature.body.getWorldPoint(segment.skeleton.creature.getPosOnBody(segment.x, segment.y));
        segment.skeleton.creature.body.applyForce(force, pos);
        
        if (power != 0 && segment.skeleton.creature.world.rand.nextInt(5) == 0) {
            Particle p = new Particle(pos.x, pos.y, segment.skeleton.creature.world);
            p.init();
            try {
                segment.skeleton.creature.world.process.put(p);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            p.body.setLinearVelocity(segment.skeleton.creature.velocity);
            p.body.applyForceToCenter(force.mul(-0.1F));
        }
    }
    
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.setColor(Color.black);
        g.fillRect(-0.25F, -0.25F, 0.5F, 0.5F);
    }
    
    @Override
    public String toString() {
        return "motor";
    }
    
    @Deprecated
    public Motor() {
        colour = Color.black;
    }
}