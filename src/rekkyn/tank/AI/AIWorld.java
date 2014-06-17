package rekkyn.tank.AI;

import java.util.Iterator;
import java.util.Map;

import org.jbox2d.common.Vec2;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import rekkyn.tank.*;
import rekkyn.tank.skeleton.Skeleton;

import com.anji.integration.Activator;

public class AIWorld extends GameWorld {
    
    Activator substrate;
    Creature creature;
    Food food;
    
    public double initialDist = 0;
    public double minDist = 0;
    public boolean gotFood = false;
    public int time;
    public int trial;
    public int maxTrials;
    public boolean random;
    
    public AIWorld(Activator substrate, int time, int trial, int maxTrials, boolean random) {
        this.substrate = substrate;
        this.time = time;
        this.trial = trial;
        this.maxTrials = maxTrials;
        this.random = random;
    }
    
    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        super.init(container, game);
        creature = new Creature(0, 0, this, Skeleton.defaultSkeleton());
        creature.angle = (float) (Math.PI / 2);
        add(creature);
        
        float angle = 0;
        if (random) {
            angle = (float) (rand.nextFloat() * 2 * Math.PI);
        } else {
            angle = (float) (trial * (2 * Math.PI / maxTrials));
        }
        
        food = new Food((float) Math.cos(angle) * 15, (float) Math.sin(angle) * 15, this);
        add(food);
        
        initialDist = distance(creature, food);
        minDist = initialDist;
    }
    
    @Override
    public void tick(GameContainer container, StateBasedGame game) {
        tickCount++;
        
        Object o;
        while ((o = process.poll()) != null) {
            process(o);
        }
        
        /*if (container != null) {
            Input input = container.getInput();
            float leftPower;
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
            
            creature.setMotors(leftPower, rightPower);
        }*/
        
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
            double[] inputs = new double[7];
            inputs[0] = Util.rotateVec(creature.body.getLocalPoint(food.body.getPosition()).add(new Vec2((float) -Math.sqrt(12.5), 0)),
                    (float) (Math.PI / 2)).x / 10;
            inputs[1] = Util.rotateVec(creature.body.getLocalPoint(food.body.getPosition()).add(new Vec2((float) -Math.sqrt(12.5), 0)),
                    (float) (Math.PI / 2)).y / 10;
            inputs[2] = Util.rotateVec(creature.body.m_linearVelocity, (float) (Math.PI / 2 - creature.angle)).x / 10;
            inputs[3] = Util.rotateVec(creature.body.m_linearVelocity, (float) (Math.PI / 2 - creature.angle)).y / 10;
            inputs[4] = creature.body.m_angularVelocity / 5;
            inputs[5] = creature.leftPower;
            inputs[6] = creature.rightPower;
            
            double[] outputs = substrate.next(inputs);
            
            creature.setMotors((float) outputs[0] * 2 - 1, (float) outputs[1] * 2 - 1);
        }
        
        if (distance(creature, food) < minDist) minDist = distance(creature, food);
        
        if (container != null && tickCount > time) {
            container.exit();
        }
    }
    
    public double distance(Entity e1, Entity e2) {
        Vec2 e1T = e1.body.getWorldPoint(new Vec2((float) Math.sqrt(12.5), 0));
        return Math.sqrt((e1T.x - e2.x) * (e1T.x - e2.x) + (e1T.y - e2.y) * (e1T.y - e2.y));
    }
    
    @Override
    public int getID() {
        return Game.AIWORLD;
    }
}
