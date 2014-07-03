package rekkyn.tank.AI;

import java.util.Iterator;
import java.util.Map;

import org.jbox2d.common.Vec2;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

import rekkyn.tank.*;
import rekkyn.tank.skeleton.Skeleton;

import com.anji.integration.Activator;

public class RetrieveWorld extends GameWorld {
    
    Activator substrate;
    Creature creature;
    Food food;
    Particle target;
    
    public boolean gotFood = false;
    public int time;
    
    public RetrieveWorld(Activator substrate, int time) {
        this.substrate = substrate;
        this.time = time;
    }
    
    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        super.init(container, game);
        creature = new Creature(0, 0, this, Skeleton.defaultSkeleton());
        creature.angle = (float) (Math.PI / 2);
        add(creature);
        
        float angle = (float) (rand.nextFloat() * 2 * Math.PI);
        
        food = new Food((float) (Math.cos(angle) * 15), (float) (Math.sin(angle) * 15), this);
        add(food);
        
        target = new Particle((float) (Math.cos(angle + Math.PI) * 15), (float) (Math.sin(angle + Math.PI) * 15), new Color(0, 1F, 0),
                Integer.MAX_VALUE, 1, this);
        add(target);
    }
    
    @Override
    public void tick(GameContainer container, StateBasedGame game) {
        tickCount++;
        
        Object o;
        while ((o = process.poll()) != null) {
            process(o);
        }
        
        if (container != null) {
            Input input = container.getInput();
            /*float leftPower;
            float rightPower;
            if (input.isKeyDown(Input.KEY_W)) {
                leftPower = 1;
            } else if (input.isKeyDown(Input.KEY_S)) {
                leftPower = -1;
            } else {
                leftPower = 0;
            }
            
            if (input.isKeyDown(Input.KEY_R)) {
                rightPower = 1;
            } else if (input.isKeyDown(Input.KEY_F)) {
                rightPower = -1;
            } else {
                rightPower = 0;
            }
            
            creature.setMotors(leftPower, rightPower);*/
            
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
            
        }
        
        physicsWorld.step(TIMESTEP / 1000, 40, 20);
        
        Iterator it = entities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            
            Entity e = (Entity) pairs.getValue();
            
            e.update();
            
            if (e.removed) {
                if (e == food) gotFood = true;
                physicsWorld.destroyBody(e.body);
                it.remove();
            }
        }
        
        if (substrate != null) {
            double[] inputs = new double[9];
            inputs[0] = Util.rotateVec(creature.body.getLocalPoint(food.body.getPosition()).add(new Vec2((float) -Math.sqrt(12.5), 0)),
                    (float) (Math.PI / 2)).x / 10;
            inputs[1] = Util.rotateVec(creature.body.getLocalPoint(food.body.getPosition()).add(new Vec2((float) -Math.sqrt(12.5), 0)),
                    (float) (Math.PI / 2)).y / 10;
            
            inputs[2] = Util.rotateVec(creature.body.getLocalPoint(target.body.getPosition()).add(new Vec2((float) -Math.sqrt(12.5), 0)),
                    (float) (Math.PI / 2)).x / 10;
            inputs[3] = Util.rotateVec(creature.body.getLocalPoint(target.body.getPosition()).add(new Vec2((float) -Math.sqrt(12.5), 0)),
                    (float) (Math.PI / 2)).y / 10;
            
            inputs[4] = Util.rotateVec(creature.body.m_linearVelocity, (float) (Math.PI / 2 - creature.angle)).x / 10;
            inputs[5] = Util.rotateVec(creature.body.m_linearVelocity, (float) (Math.PI / 2 - creature.angle)).y / 10;
            inputs[6] = creature.body.m_angularVelocity / 5;
            inputs[7] = creature.leftPower;
            inputs[8] = creature.rightPower;
            
            double[] outputs = substrate.next(inputs);
            
            creature.setMotors((float) outputs[0] * 2 - 1, (float) outputs[1] * 2 - 1);
        }
        
        if (container != null && tickCount > time) {
            container.exit();
        }
    }
    
    public double distance(Entity e1, Entity e2) {
        return Math.sqrt((e1.x - e2.x) * (e1.x - e2.x) + (e1.y - e2.y) * (e1.y - e2.y));
    }
    
    @Override
    public int getID() {
        return 55;
    }
}
