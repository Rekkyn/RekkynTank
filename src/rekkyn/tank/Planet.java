package rekkyn.tank;

import java.util.Iterator;
import java.util.Map;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class Planet extends Entity {
    
    public float radius;
    public float density = 12F;
    
    float dampRange = 150F;
    
    public Vec2 point = new Vec2(90, 0);
    
    public Planet(float x, float y, GameWorld world) {
        this(x, y, 20, world);
    }
    
    public Planet(float x, float y, float radius, GameWorld world) {
        super(x, y, world);
        // bodyType = BodyType.STATIC;
        this.radius = radius;
    }
    
    @Override
    public void init() {
        super.init();
        
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        body.createFixture(shape, density);
    }
    
    @Override
    public void update() {
        super.update();
        Iterator it = world.entities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            
            Entity e = (Entity) pairs.getValue();
            
            if (!e.equals(this)) {
                Vec2 dist = body.getWorldCenter().sub(e.body.getWorldCenter());
                float magnitude = body.getMass() * e.body.getMass() / dist.lengthSquared();
                float distFromSurface = dist.length() - radius;
                dist.normalize();
                Vec2 force = dist.mul(magnitude);
                e.body.applyForceToCenter(force);
                
                float initialDamp = 1.75F;
                float initialAngDamp = 2.75F;
                
                float damp = (float) (initialDamp * Math.pow(1 - distFromSurface / dampRange, 3));
                float angDamp = (float) (initialAngDamp * Math.pow(1 - distFromSurface / dampRange, 3));
                e.body.setLinearDamping(damp);
                e.body.setAngularDamping(angDamp);
            }
        }
    }
    
    public Vec2 surfaceCoords(Vec2 v) {
        Vec2 dist = v.sub(body.getWorldCenter());
        float angle = (float) Math.toDegrees(Math.atan2(dist.x, dist.y));
        return new Vec2(angle, dist.length() - radius);
    }
    
    public Vec2 worldCoords(Vec2 v) {
        float angle = (float) Math.toRadians(v.x);
        float length = radius + v.y;
        return new Vec2((float) (length * Math.sin(angle)) + x, (float) (length * Math.cos(angle)) + y);
    }
    
    @Override
    public void render(Graphics g) {
        super.render(g);
        g.setColor(Colours.getBody());
        g.fillOval(x - radius, -y - radius, 2 * radius, 2 * radius);
        g.setColor(Colours.getDark());
        g.drawOval(x - radius - dampRange, -y - radius - dampRange, 2 * (radius + dampRange), 2 * (radius + dampRange));
        
        Vec2 pos = worldCoords(point);
        Color col = Util.copyColor(Colours.getAccent());
        col.a = 0.25F;
        g.setColor(col);
        g.fillOval(pos.x - 10, -pos.y - 10, 20, 20);
    }
    
    @Override
    public Object[] getSpecificData() {
        return null;
    }
    
    @Override
    public void setSpecificData(Object[] data) {}
    
}
