package rekkyn.tank;

import java.util.*;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class GameWorld extends BasicGameState {
    
    float accumulator = 0.0F;
    public static long tickCount = 0;
    public static float partialTicks;
    public static int timespeeds = 0;
    public static final float TIMESTEP = 50F / 3F;
    
    public static List<Entity> entities = new ArrayList<Entity>();
    
    public static Random rand = new Random();
    
    public static World physicsWorld = new World(new Vec2(0, 0));
    
    public GameWorld() {
    }
    
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        
        if (delta > 25) delta = 25;
        accumulator += delta;
        
        while (accumulator >= TIMESTEP) {
            if (container.hasFocus()) {
                tick(container, game, delta);
            }
            accumulator -= TIMESTEP;
        }
        partialTicks = accumulator / TIMESTEP;
    }
    
    public void tick(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        Input input = container.getInput();
        tickCount++;
        
        if (input.isKeyDown(Input.KEY_RIGHT)) {
            Camera.x += 4 / Camera.zoom;
        }
        if (input.isKeyDown(Input.KEY_LEFT)) {
            Camera.x -= 4 / Camera.zoom;
        }
        if (input.isKeyDown(Input.KEY_UP)) {
            Camera.y += 4 / Camera.zoom;
        }
        if (input.isKeyDown(Input.KEY_DOWN)) {
            Camera.y -= 4 / Camera.zoom;
        }
        if (input.isKeyDown(Input.KEY_EQUALS)) {
            Camera.zoom *= 1.01;
        }
        if (input.isKeyDown(Input.KEY_MINUS)) {
            Camera.zoom *= 0.99;
        }
        Camera.update();
        
        physicsWorld.step(TIMESTEP / 1000, 40, 20);
        
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            
            e.update(container, game, delta);
            
            if (e.removed) {
                physicsWorld.destroyBody(e.body);
                entities.remove(i--);
            }
        }
    }
    
    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        physicsWorld.setContinuousPhysics(true);
    }
    
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.setAntiAlias(true);
        
        g.setColor(new Color(233, 242, 249));
        g.fillRect(0, 0, Game.width, Game.height);
        
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            
            e.prerender(g);
            e.render(container, game, g);
            e.postrender(g);
        }
        
        g.setColor(new Color(27, 50, 95));
        Font.draw("FPS: " + Game.appgc.getFPS(), 20, 10, 2, g);
    }
    
    public static void add(Entity entity) {
        entity.removed = false;
        entities.add(entity);
        entity.init();
    }
    
    public static List<Entity> getEntities() {
        return entities;
    }
    
    public static Vec2 mousePos(GameContainer container) {
        Input input = container.getInput();
        return new Vec2(Camera.x - Game.width / 2 / Camera.zoom + input.getMouseX() / Camera.zoom, Camera.y + Game.height / 2 / Camera.zoom
                - input.getMouseY() / Camera.zoom);
    }
    
    @Override
    public int getID() {
        return Game.WORLD;
    }
    
}
