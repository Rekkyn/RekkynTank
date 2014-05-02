package rekkyn.tank;

import java.util.Iterator;
import java.util.Map;

import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

import rekkyn.tank.skeleton.*;


public class TestWorld extends GameWorld {
    
    Creature c;
    
    @Override
    public int getID() {
        return Game.TESTWORLD;
    }
    
    @Override
    public void tick(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        Input input = container.getInput();
        tickCount++;
        
        if (input.isKeyDown(Input.KEY_RIGHT)) {
            camera.x += 4 / camera.zoom;
        }
        if (input.isKeyDown(Input.KEY_LEFT)) {
            camera.x -= 4 / camera.zoom;
        }
        if (input.isKeyDown(Input.KEY_UP)) {
            camera.y += 4 / camera.zoom;
        }
        if (input.isKeyDown(Input.KEY_DOWN)) {
            camera.y -= 4 / camera.zoom;
        }
        if (input.isKeyDown(Input.KEY_EQUALS)) {
            camera.zoom *= 1.01;
        }
        if (input.isKeyDown(Input.KEY_MINUS)) {
            camera.zoom *= 0.99;
        }
        camera.update();
        
        Object o;
        while ((o = process.poll()) != null) {
            process(o, container);
        }

        if (input.isKeyPressed(Input.KEY_T)) {
            
            Iterator it = entities.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                ((Entity) pairs.getValue()).remove();
            }
            
            game.enterState(Game.EDITOR);
        }
        
        for (Segment s : c.skeleton.segments) {
            Motor m;
            if (s.elements[8] instanceof Motor) {
                m = (Motor) s.elements[8];
            } else
                continue;
            
            if (s.y >= s.x) {
                if (input.isKeyDown(Input.KEY_W)) {
                    m.power = power;
                    continue;
                } else if (input.isKeyDown(Input.KEY_S)) {
                    m.power = -power;
                    continue;
                } else {
                    m.power = 0;
                }
            }
            
            if (s.x >= s.y) {
                if (input.isKeyDown(Input.KEY_R)) {
                    m.power = power;
                } else if (input.isKeyDown(Input.KEY_F)) {
                    m.power = -power;
                } else {
                    m.power = 0;
                }
            }
        }
        
        if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
            add(new Wall(mousePos(container).x, mousePos(container).y, 1, 1, this));
        }
        
        
        physicsWorld.step(TIMESTEP / 1000, 40, 20);
        
        Iterator it = entities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            
            Entity e = (Entity) pairs.getValue();
            
            e.update();
            
            if (e.removed) {
                physicsWorld.destroyBody(e.body);
                it.remove();
            }
        }
    }
    
    public void addTestCreatrue(Skeleton skeleton) {
        c = new Creature(0, 0, this, skeleton);
        c.angle = (float) (Math.PI / 2);
        add(c);
    }
}
