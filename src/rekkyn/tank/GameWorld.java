package rekkyn.tank;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import rekkyn.tank.Colours.ColourSets;
import rekkyn.tank.network.NetworkManager.AddEntity;
import rekkyn.tank.network.NetworkManager.EntityData;
import rekkyn.tank.network.NetworkManager.EntityType;
import rekkyn.tank.network.NetworkManager.SendInput;
import rekkyn.tank.network.*;
import rekkyn.tank.network.client.GameClient;
import rekkyn.tank.network.server.GameServer;
import rekkyn.tank.skeleton.Motor;

public class GameWorld extends BasicGameState {
    
    float accumulator = 0.0F;
    public long tickCount = 0;
    public float partialTicks;
    public final float TIMESTEP = 50F / 3F;
    
    public HashMap<Integer, Entity> entities = new HashMap<Integer, Entity>();
    
    public HashMap<User, Creature> players = new HashMap<User, Creature>();
    
    public LinkedBlockingQueue process = new LinkedBlockingQueue();
    
    public Random rand = new Random();
    
    public World physicsWorld = new World(new Vec2(0, 0));
    
    public GameServer server;
    public GameClient client;
    
    public GameWorld() {
    }
    
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        
        if (delta > 25) delta = 25;
        accumulator += delta;
        
        while (accumulator >= TIMESTEP) {
            // if (container.hasFocus()) {
            tick(container, game, delta);
            // }
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
        
        Object o;
        while ((o = process.poll()) != null) {
            process(o, container);
        }
        
        // SERVER THINGS
        if (server != null) {
            Iterator it = entities.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                Entity ent = (Entity) pairs.getValue();
                
                EntityData data = ent.getData();
                if (!data.equals(ent.sentData)) {
                    server.server.sendToAllTCP(data);
                    ent.sentData = data;
                }
            }
            
            Creature player = players.get(server.host);
            
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
            
            if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
                add(new Wall(mousePos(container).x, mousePos(container).y, 1, 1, this));
            }
        }
        
        // CLIENT THINGS
        if (client != null) {
            int[] keys = new int[] { Input.KEY_W, Input.KEY_S, Input.KEY_R, Input.KEY_F };
            
            SendInput sendInput = new SendInput();
            if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
                sendInput.mousePressed[Input.MOUSE_LEFT_BUTTON] = true;
                sendInput.mousePos = mousePos(container);
            }
            for (int key : keys) {
                if (input.isKeyDown(key)) {
                    sendInput.down[key] = true;
                }
            }
            client.client.sendTCP(sendInput);
        }
        
        physicsWorld.step(TIMESTEP / 1000, 40, 20);
        
        Iterator it = entities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            
            Entity e = (Entity) pairs.getValue();
            
            e.update(container, game, delta);
            
            if (e.removed) {
                physicsWorld.destroyBody(e.body);
                entities.remove(pairs.getKey());
            }
        }
    }
    
    private void process(Object o, GameContainer container) {
        if (o instanceof AddEntity) {
            EntityData data = ((AddEntity) o).data;
            
            Entity e = null;
            if (data.type == EntityType.CREATURE) {
                e = new Creature(data.x, data.y, this);
            } else if (data.type == EntityType.WALL) {
                e = new Wall(data.x, data.y, this);
            }
            
            add(e);
            e.setData(data);
        } else if (o instanceof EntityData) {
            EntityData data = (EntityData) o;
            entities.get(data.id).setData(data);
        } else if (o instanceof SendInput) {
            processInput((SendInput) o, container);
        } else if (o instanceof Entity) {
            add((Entity) o);
        }
    }
    
    private void processInput(SendInput sendInput, GameContainer container) {
        
        if (sendInput.mousePressed[Input.MOUSE_LEFT_BUTTON]) {
            add(new Wall(sendInput.mousePos.x, sendInput.mousePos.y, 1, 1, this));
        }
        
        User user = sendInput.user;
        Creature player = players.get(user);
        
        Motor ml = (Motor) player.skeleton.getSegment(0, 2).elements[8];
        Motor mr = (Motor) player.skeleton.getSegment(2, 0).elements[8];
        float power = 50;
        if (sendInput.down[Input.KEY_W]) {
            ml.power = power;
        } else if (sendInput.down[Input.KEY_S]) {
            ml.power = -power;
        } else {
            ml.power = 0;
        }
        
        if (sendInput.down[Input.KEY_R]) {
            mr.power = power;
        } else if (sendInput.down[Input.KEY_F]) {
            mr.power = -power;
        } else {
            mr.power = 0;
        }
        
    }
    
    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        physicsWorld.setContinuousPhysics(true);
    }
    
    public void initServer() {
        if (server != null) {
            for (int lel = 0; lel < 0; lel++) {
                add(new Wall(rand.nextFloat() * 20, rand.nextFloat() * 20, rand.nextFloat() * 5, rand.nextFloat() * 5, this));
            }
        }
    }
    
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.setAntiAlias(true);
        
        Colours.setColourSet(ColourSets.SNAZZY);
        
        g.setColor(Colours.getBackground());
        g.fillRect(0, 0, Game.width, Game.height);
        
        Iterator it = entities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            
            Entity e = (Entity) pairs.getValue();
            
            e.prerender(g);
            e.renderBackground(container, game, g);
            e.postrender(g);
        }
        Iterator it2 = entities.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry pairs = (Map.Entry) it2.next();
            
            Entity e = (Entity) pairs.getValue();
            
            e.prerender(g);
            e.render(container, game, g);
            e.postrender(g);
        }
        
        g.setColor(new Color(27, 50, 95));
        Font.draw("FPS: " + Game.appgc.getFPS(), 20, 10, 2, g);
    }
    
    public void add(Entity entity) {
        entity.removed = false;
        int id;
        if (entity.id == 0) {
            id = getNextID();
        } else {
            id = entity.id;
        }
        entity.id = id;
        entity.init();
        entities.put(id, entity);
        
        if (server != null) {
            server.server.sendToAllTCP(server.addEntity(entity));
        }
    }
    
    public int getNextID() {
        int nextID = 1;
        while (nextID != -1) {
            if (entities.get(nextID) == null) return nextID;
            nextID++;
        }
        return -1;
    }
    
    public HashMap<Integer, Entity> getEntities() {
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
    
    public void addPlayer(User user) {
        if (server != null) {
            Creature player = new Creature(rand.nextFloat() * 50 - 25, rand.nextFloat() * 50 - 25, this);
            player.angle = (float) (rand.nextFloat() * 2F * Math.PI);
            try {
                process.put(player);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            players.put(user, player);
        }
    }
    
}
