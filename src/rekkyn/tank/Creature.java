package rekkyn.tank;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

import rekkyn.tank.skeleton.*;

public class Creature extends Entity {
    
    public Skeleton skeleton = new Skeleton(this);
    
    public Creature(float x, float y, GameWorld world) {
        super(x, y, world);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void init() {
        super.init();
        
        /*if (skeleton.segments.size() <= 1) {
            skeleton.addSegment(0, 2).addSegment(1, 1);
            skeleton.getSegment(2, 0).addMotor(true);
            skeleton.getSegment(1, 1).addElement(new Mouth(), 1).addElement(new Mouth(), 7);
        }*/
        
        /*skeleton.addSegment(0, 2).addSegment(1, -1).addSegment(1, 1)
        .addSegment(-1, 2);
        skeleton.getSegment(2, 0).addMotor(true);
        skeleton.getSegment(1, 1).addElement(new Mouth(), 1).addElement(new Mouth(), 7);*/
        
        skeleton.addSegment(1, 1).addSegment(0, 2).addSegment(2, 1).addSegment(2, 2);
        skeleton.getSegment(2, 0).addMotor(true);
        skeleton.getSegment(2, 2).addElement(new Mouth(), 1).addElement(new Mouth(), 7);
        
        for (Segment s : skeleton.segments) {
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(0.5F, 0.5F, getPosOnBody(s.x, s.y), (float) Math.toRadians(45));
            body.createFixture(shape, 1).setUserData(s);
        }
        
        body.setBullet(true);
        body.setUserData(this);
    }
    
    @Override
    public void update() {
        super.update();
        for (Segment s : skeleton.segments) {
            for (Element e : s.elements) {
                if (e != null) {
                    e.update(this);
                }
            }
        }
    }
    
    public Vec2 getPosOnBody(int x, int y) {
        float rotX = (float) (x * Math.cos(-Math.PI / 4) - y * Math.sin(-Math.PI / 4));
        float rotY = (float) (x * Math.sin(-Math.PI / 4) + y * Math.cos(-Math.PI / 4));
        
        return new Vec2(rotX, rotY);
    }
    
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        super.render(container, game, g);
        g.pushTransform();
        g.rotate(x, -y, 45);
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
            s.render(container, game, g);
            g.popTransform();
        }
        g.popTransform();
    }
    
    @Override
    public void renderBackground(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        super.renderBackground(container, game, g);
        g.pushTransform();
        g.rotate(x, -y, 45);
        float dist = (float) (1 / Math.sqrt(8));
        g.translate((float) (Math.cos(angle) * dist), (float) (Math.sin(angle) * dist));
        
        for (Segment s : skeleton.segments) {
            float drawX = x + s.x;
            float drawY = -y - s.y;
            g.setColor(Colours.getShadow());
            g.fillRect(drawX - 0.5F, drawY - 0.5F, 1, 1);
        }
        g.popTransform();
    }
    
    @Override
    public Object[] getSpecificData() {
        return new Object[] { skeleton };
    }
    
    @Override
    public void setSpecificData(Object[] data) {
        skeleton = (Skeleton) data[0];
        skeleton.creature = this;
    }
    
}
