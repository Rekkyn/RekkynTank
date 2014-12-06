package rekkyn.tank;

import java.util.Random;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

import rekkyn.tank.network.NetworkManager.EntityData;
import rekkyn.tank.network.NetworkManager.EntityType;

public abstract class Entity {
    
    public int id;
    
    public float x, y;
    /** The angle of the entity in radians */
    public float angle;
    public float prevAngle;
    public float prevX, prevY;
    public Vec2 velocity = new Vec2(0, 0);
    public boolean removed;
    public long ticksExisted = 0;
    Input input;
    Random rand = new Random();
    
    public Body body;
    public BodyDef def;
    public BodyType bodyType = BodyType.DYNAMIC;
    
    public GameWorld world;
    public static World physicsWorld;
    
    public EntityData sentData;
    public boolean shouldSend = true;
    
    public boolean init = false;
    
    public Entity(float x, float y, GameWorld world) {
        this.x = x;
        this.y = y;
        this.world = world;
    }
    
    public void remove() {
        removed = true;
    }
    
    public void init() {
        physicsWorld = world.physicsWorld;
        def = new BodyDef();
        def.position.set(x, y);
        def.angle = angle;
        def.linearVelocity = velocity;
        def.type = bodyType;
        body = world.physicsWorld.createBody(def);
        body.setLinearDamping(1.75F);
        body.setAngularDamping(2.75F);
        
        init = true;
    }
    
    public void update() {
        ticksExisted++;
        
        prevX = x;
        prevY = y;
        prevAngle = angle;
        x = body.getPosition().x;
        y = body.getPosition().y;
        angle = body.getAngle();
        velocity = body.getLinearVelocity();
    }
    
    public void prerender(Graphics g) {
        g.pushTransform();
        g.scale(world.camera.zoom, world.camera.zoom);
        g.translate(-world.camera.x + Game.width / world.camera.zoom / 2, world.camera.y + Game.height / world.camera.zoom / 2);
        g.rotate(x, -y, (float) Math.toDegrees(-angle));
        g.rotate(x, -y, world.partialTicks * (float) Math.toDegrees(-angle + prevAngle));
    }
    
    public void render(Graphics g) {}
    
    public void renderBackground(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {}
    
    public void postrender(Graphics g) {
        g.popTransform();
    }
    
    public EntityData getData() {
        EntityData data = new EntityData();
        if (this instanceof Creature) {
            data.type = EntityType.CREATURE;
        } else if (this instanceof Wall) {
            data.type = EntityType.WALL;
        } else if (this instanceof Food) {
            data.type = EntityType.FOOD;
        } else if (this instanceof Particle) {
            data.type = EntityType.PARTICLE;
        }
        
        data.id = id;
        data.x = x;
        data.y = y;
        data.angle = angle;
        data.velocity = velocity;
        data.removed = removed;
        data.specificData = getSpecificData();
        return data;
    }
    
    public void setData(EntityData data) {
        id = data.id;
        x = data.x;
        y = data.y;
        angle = data.angle;
        body.setTransform(new Vec2(x, y), angle);
        velocity = data.velocity;
        body.setLinearVelocity(velocity);
        removed = data.removed;
        setSpecificData(data.specificData);
        
        body.setTransform(new Vec2(x, y), angle);
        body.setLinearVelocity(velocity);
    }
    
    public abstract Object[] getSpecificData();
    
    public abstract void setSpecificData(Object[] data);
    
}
