package rekkyn.tank;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

import rekkyn.tank.skeleton.*;

public class Creature extends Entity {
    
    public Skeleton skeleton = new Skeleton(this);
    
    public List<Element> updatedElements = new ArrayList<Element>();
    
    public Creature(float x, float y, GameWorld world, Skeleton skeleton) {
        super(x, y, world);
        if (skeleton != null) {
            this.skeleton = skeleton;
            skeleton.creature = this;
        }
    }
    
    public Creature(float x, float y, GameWorld world) {
        this(x, y, world, null);
    }
    
    @Override
    public void init() {
        super.init();
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
                if (e != null && !updatedElements.contains(e)) {
                    e.update(this);
                    updatedElements.add(e);
                }
            }
        }
        updatedElements.clear();
    }
    
    public Vec2 getPosOnBody(int x, int y) {
        return Util.rotateVec(new Vec2(x, y), (float) (-Math.PI / 4));
    }
    
    public void setMotors(float leftPower, float rightPower) {
        for (Segment s : skeleton.segments) {
            Motor m;
            if (s.elements[8] instanceof Motor) {
                m = (Motor) s.elements[8];
            } else
                continue;
            
            if (s.y >= s.x) {
                m.power = leftPower;
            }
            
            if (s.x >= s.y) {
                m.power = rightPower;
            }
        }
        
    }
    
    @Override
    public void render(Graphics g) {
        super.render(g);
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
            s.render(g);
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
