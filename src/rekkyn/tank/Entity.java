package rekkyn.tank;

import java.util.Random;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.newdawn.slick.*;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;

public abstract class Entity {
    
    public float x, y;
    /** The angle of the entity in radians */
    public float angle;
    public float prevX, prevY;
    public Vec2 velocity;
    public boolean removed;
    public long ticksExisted = 0;
    Input input;
    Random rand = new Random();
    
    public Body body;
    public BodyDef def;
    
    public GameWorld world;
    public static World physicsWorld;
    
    public Entity(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public void remove() {
        removed = true;
    }
    
    public void init() {
        physicsWorld = GameWorld.physicsWorld;
        def = new BodyDef();
        def.position.set(x, y);
        def.angle = angle;
        def.type = BodyType.DYNAMIC;
        body = GameWorld.physicsWorld.createBody(def);
    }
    
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        ticksExisted++;
        GameState state = game.getCurrentState();
        if (!(state instanceof GameWorld)) return;
        world = (GameWorld) game.getCurrentState();
        
        prevX = x;
        prevY = y;
        x = body.getPosition().x;
        y = body.getPosition().y;
        angle = body.getAngle();
        velocity = body.getLinearVelocity();
    }
    
    public void prerender(Graphics g) {
        g.pushTransform();
        g.scale(Camera.zoom, Camera.zoom);
        g.translate(GameWorld.partialTicks * (x - prevX) - Camera.x + Game.width / Camera.zoom / 2, GameWorld.partialTicks * (prevY - y)
                + Camera.y + Game.height / Camera.zoom / 2);
        g.rotate(x, -y, (float) Math.toDegrees(-angle));
    }
    
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
    }
    
    public void renderBackground(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {}
    
    public void postrender(Graphics g) {
        g.popTransform();
    }
    
    public void onHit() {}
    
}
