package rekkyn.tank.skeleton;

import org.jbox2d.common.Vec2;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

import rekkyn.tank.skeleton.Skeleton.ElementType;

public class Motor extends Element {
    
    public float power = 0;
    
    public Motor(Segment s) {
        super(s);
        type = ElementType.CENTRE;
    }
    
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        segment.skeleton.creature.body.applyForce(
                new Vec2((float) (Math.cos(segment.skeleton.creature.angle) * power),
                        (float) (Math.sin(segment.skeleton.creature.angle) * power)), segment.skeleton.creature.body
                        .getWorldPoint(segment.skeleton.creature.getPosOnBody(segment.x, segment.y)));
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
    public Motor() {}
}