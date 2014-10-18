package rekkyn.tank.AI;

import java.util.Iterator;
import java.util.Map;

import org.jbox2d.common.Vec2;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

import rekkyn.tank.*;
import rekkyn.tank.Game;
import rekkyn.tank.skeleton.Mouth;
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
    public int trialTime;
    public int maxTrials;
    public boolean random;
    public boolean debug;
    public boolean chain;
    
    float relX, relY;
    float velX, velY;
    float angV;
    float lPow, rPow;
    
    public AIWorld(Activator substrate, int time, int trial, int maxTrials, boolean random, boolean debug, boolean chain) {
        this.substrate = substrate;
        this.time = time;
        this.trial = trial;
        this.maxTrials = maxTrials;
        this.random = random;
        this.debug = debug;
        this.chain = chain;
    }
    
    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        super.init(container, game);
        Skeleton skeleton = new Skeleton();
        skeleton.addSegment(0, 1).addSegment(0, 2).addSegment(0, 3);
        skeleton.getSegment(0, 2).addMotor(true);
        skeleton.addElement(new Mouth(), 0, 3, 1);
        creature = new Creature(0, 0, this, skeleton);
        creature.angle = (float) (Math.PI / 2);
        add(creature);
        camera.setFollowing(creature);
        
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
    
    public void addFood() {
        float angle = (float) (rand.nextFloat() * 2 * Math.PI);
        food = new Food((float) Math.cos(angle) * 15 + creature.x, (float) Math.sin(angle) * 15 + creature.y, this);
        add(food);
        gotFood = false;
        initialDist = distance(creature, food);
        minDist = initialDist;
        trialTime = 0;
    }
    
    @Override
    public void tick(GameContainer container, StateBasedGame game) {
        tickCount++;
        trialTime++;
        
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
        
        Vec2 relPos = Util.rotateVec(creature.body.getLocalPoint(food.body.getPosition()).add(new Vec2((float) -Math.sqrt(12.5), 0)),
                (float) (Math.PI / 2));
        
        relX = (float) (2 / (1 + Math.exp(-0.2 * relPos.x)) - 1);
        relY = (float) (2 / (1 + Math.exp(-0.2 * relPos.y)) - 1);
        velX = Util.rotateVec(creature.body.m_linearVelocity, (float) (Math.PI / 2 - creature.angle)).x / 10;
        velY = Util.rotateVec(creature.body.m_linearVelocity, (float) (Math.PI / 2 - creature.angle)).y / 10;
        angV = creature.body.m_angularVelocity / 8;
        lPow = creature.leftPower;
        rPow = creature.rightPower;
        
        if (substrate != null) {
            double[] inputs = new double[7];
            inputs[0] = relX;
            inputs[1] = relY;
            inputs[2] = velX;
            inputs[3] = velY;
            inputs[4] = angV;
            inputs[5] = lPow;
            inputs[6] = rPow;
            
            double[] outputs = substrate.next(inputs);
            
            creature.setMotors((float) outputs[0] * 2 - 1, (float) outputs[1] * 2 - 1);
        }
        
        if (distance(creature, food) < minDist) minDist = distance(creature, food);
        
        if (container != null && tickCount > time) {
            container.exit();
        }
        
        if (container != null && chain && gotFood) {
            addFood();
        }
    }
    
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        super.render(container, game, g);
        if (debug) {
            g.setColor(Color.red);
            g.drawRect(55, 50, relX * 20, 10);
            g.drawRect(50, 55, 10, -relY * 20);
            g.drawRect(155, 50, velX * 20, 10);
            g.drawRect(150, 55, 10, -velY * 20);
            g.drawRect(250, 50, angV * 20, 10);
            
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
