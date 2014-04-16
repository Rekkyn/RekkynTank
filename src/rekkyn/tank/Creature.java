package rekkyn.tank;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

import rekkyn.tank.Skeleton.Element;
import rekkyn.tank.Skeleton.Heart;
import rekkyn.tank.Skeleton.Segment;

public class Creature extends Entity {
    
    public Skeleton skeleton = new Skeleton();
    
    public Creature(float x, float y) {
        super(x, y);
    }
    
    @Override
    public void init() {
        super.init();
        
        skeleton.addSegment(1, 1).addSegment(2, 2).addSegment(0, 2).addSegment(2, 1);
        skeleton.getSegment(2, 0).addMotor(true);
        
        for (Segment s : skeleton.segments) {
            float drawX = (float) (s.x * Math.cos(Math.PI / 4) - s.y * Math.sin(Math.PI / 4));
            float drawY = (float) (s.x * Math.sin(Math.PI / 4) + s.y * Math.cos(Math.PI / 4));
            
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(0.5F, 0.5F, new Vec2(drawX, drawY), (float) Math.toRadians(45));
            body.createFixture(shape, 1).setFriction(0.2F);
        }
        
        body.setBullet(true);
        body.setUserData(this);
    }
    
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        super.render(container, game, g);
        g.pushTransform();
        g.rotate(x, -y, -45);
        for (Segment s : skeleton.segments) {
            float drawX = x + s.x;
            float drawY = -y - s.y;
            if (s instanceof Heart) {
                g.setColor(Colours.getAccent());
            } else {
                g.setColor(Colours.getBody());
            }
            g.fillRect(drawX - 0.5F, drawY - 0.5F, 1, 1);
            g.pushTransform();
            g.translate(x + s.x, -y - s.y);
            for (Element e : s.elements) {
                if (e != null) {
                    e.render(container, game, g);
                }
            }
            g.popTransform();
        }
        g.popTransform();
    }
    
    @Override
    public void renderBackground(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        super.render(container, game, g);
        g.pushTransform();
        g.rotate(x, -y, -45);
        float dist = (float) (1 / Math.sqrt(8));
        g.translate((float) -(Math.sin(angle) * dist), (float) (Math.cos(angle) * dist));
        
        for (Segment s : skeleton.segments) {
            float drawX = x + s.x;
            float drawY = -y - s.y;
            g.setColor(Colours.getShadow());
            g.fillRect(drawX - 0.5F, drawY - 0.5F, 1, 1);
        }
        g.popTransform();
    }
    
}
