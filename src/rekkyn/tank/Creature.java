package rekkyn.tank;

import java.util.Iterator;
import java.util.Map;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

import rekkyn.tank.Skeleton.Heart;
import rekkyn.tank.Skeleton.Point;
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
        
        Iterator it = skeleton.segments.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            Point p = (Point) pairs.getKey();
            Segment s = (Segment) pairs.getValue();
            float drawX = (float) (p.x * Math.cos(Math.PI / 4) - p.y * Math.sin(Math.PI / 4));
            float drawY = (float) (p.x * Math.sin(Math.PI / 4) + p.y * Math.cos(Math.PI / 4));
            
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(0.5F, 0.5F, new Vec2(drawX, drawY), (float) Math.toRadians(45));
            body.createFixture(shape, 1).setFriction(0.2F);
            
            if (p.x != p.y) {
                drawX = (float) (p.y * Math.cos(Math.PI / 4) - p.y * Math.sin(Math.PI / 4));
                drawY = (float) (p.x * Math.sin(Math.PI / 4) + p.x * Math.cos(Math.PI / 4));
                
                PolygonShape shape1 = new PolygonShape();
                shape1.setAsBox(0.5F, 0.5F, new Vec2(drawX, drawY), (float) Math.toRadians(45));
                body.createFixture(shape1, 1).setFriction(0.2F);
            }
        }
        
        
        body.setBullet(true);
        body.setUserData(this);
    }
    
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        super.render(container, game, g);
        g.pushTransform();
        g.rotate(x, -y, -45);
        Iterator it = skeleton.segments.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            Point p = (Point) pairs.getKey();
            Segment s = (Segment) pairs.getValue();
            float drawX = x + p.x;
            float drawY = -y - p.y;
            if (s instanceof Heart) {
                g.setColor(new Color(225, 112, 51));
            } else {
                g.setColor(new Color(27, 50, 95));
            }
            g.fillRect(drawX - 0.5F, drawY - 0.5F, 1, 1);
            if (p.x != p.y) {
                drawX = x + p.y;
                drawY = -y - p.x;
                g.fillRect(drawX - 0.5F, drawY - 0.5F, 1, 1);
            }
        }
        g.popTransform();
    }
}
