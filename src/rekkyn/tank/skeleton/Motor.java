package rekkyn.tank.skeleton;

import org.jbox2d.common.Vec2;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import rekkyn.tank.*;
import rekkyn.tank.skeleton.Skeleton.ElementType;

public class Motor extends Element {
    
    public float power = 0;
    public float changeAmount = 7;
    public Segment segment;
    
    public Motor() {
        type = ElementType.CENTRE;
        colour = new Color(0, 0, 0);
        colour.a = 0.75F;
    }
    
    public Motor(Segment s) {
        segment = s;
        type = ElementType.CENTRE;
        colour = new Color(0, 0, 0);
        colour.a = 0.75F;
    }
    
    @Override
    public void update(Creature c) {
        Vec2 force = new Vec2((float) (Math.cos(c.angle) * power), (float) (Math.sin(c.angle) * power));
        
        Vec2 pos = c.body.getWorldPoint(c.getPosOnBody(segment.x, segment.y));
        c.body.applyForce(force, pos);
        
        if (power != 0 && c.world.rand.nextInt(5) == 0) {
            Particle p = c.world.spawnParticle(pos.x, pos.y, Colours.getDark(), 60, 0.125F);
            p.init();
            p.body.setLinearVelocity(c.body.getLinearVelocityFromWorldPoint(pos));
            p.body.applyForceToCenter(force.mul(-0.1F));
        }
        
        colour.r = Math.abs(Colours.getDark().r * power / 70);
        colour.g = Math.abs(Colours.getDark().g * power / 70);
        colour.b = Math.abs(Colours.getDark().b * power / 70);
    }
    
    @Override
    public void render(Graphics g) {
        g.setColor(colour);
        g.fillRect(-0.25F, -0.25F, 0.5F, 0.5F);
    }
    
    @Override
    public String toString() {
        return "motor";
    }
    
}