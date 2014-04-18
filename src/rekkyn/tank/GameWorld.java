package rekkyn.tank;

import java.util.*;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import rekkyn.tank.Colours.ColourSets;
import rekkyn.tank.Skeleton.Motor;

public class GameWorld extends BasicGameState {
    
    float accumulator = 0.0F;
    public Creature player = new Creature(0, 0);
    public static long tickCount = 0;
    public static float partialTicks;
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
        
        Motor ml = (Motor) player.skeleton.getSegment(0, 2).elements[8];
        Motor mr = (Motor) player.skeleton.getSegment(2, 0).elements[8];
        float power = 50;
        if (input.isKeyDown(Input.KEY_W)) {
            ml.power = power;
        } else if (input.isKeyDown(Input.KEY_S)) {
            ml.power = -power;
        } else {
            ml.power = 0;
        }
        
        if (input.isKeyDown(Input.KEY_R)) {
            mr.power = power;
        } else if (input.isKeyDown(Input.KEY_F)) {
            mr.power = -power;
        } else {
            mr.power = 0;
        }
        
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
        TheContactListener listener = new TheContactListener();
        physicsWorld.setContactListener(listener);
        physicsWorld.setContinuousPhysics(true);
        
        add(player);
        Creature c1 = new Creature(0, 5);
        c1.angle = (float) Math.toRadians(45);
        add(c1);
        Creature c11 = new Creature(0, 20);
        c11.angle = (float) Math.toRadians(-45);
        add(c11);
        
        for (int lol = 0; lol < 50; lol++) {
            add(new Food(rand.nextFloat() * 50 - 25, rand.nextFloat() * 50 - 25));
        }
        
        add(new Wall(0, -20, 50, 2));
    }
    
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.setAntiAlias(true);
        
        Colours.setColourSet(ColourSets.SNAZZY);
        
        g.setColor(Colours.getBackground());
        g.fillRect(0, 0, Game.width, Game.height);
        
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            
            e.prerender(g);
            e.renderBackground(container, game, g);
            e.postrender(g);
        }
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
