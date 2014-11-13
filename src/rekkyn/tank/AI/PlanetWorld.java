package rekkyn.tank.AI;

import java.util.Iterator;
import java.util.Map;

import org.jbox2d.common.Vec2;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

import rekkyn.tank.*;
import rekkyn.tank.Game;
import rekkyn.tank.skeleton.Skeleton;

import com.anji.integration.Activator;

public class PlanetWorld extends GameWorld {
    
    Activator substrate;
    Creature creature;
    Planet planet;
    
    public double initialDist = 0;
    public double minDist = 0;
    public int time;
    public int trial;
    public int trialTime;
    public int maxTrials;
    public boolean random;
    public boolean debug;
    
    float relX, relY;
    float velX, velY;
    float angV;
    float lPow, rPow;
    
    public PlanetWorld(Activator substrate, int time, int trial, int maxTrials, boolean random, boolean debug) {
        this.substrate = substrate;
        this.time = time;
        this.trial = trial;
        this.maxTrials = maxTrials;
        this.random = random;
        this.debug = debug;
    }
    
    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        super.init(container, game);
        Skeleton skeleton = new Skeleton();
        skeleton.addSegment(0, 1).addSegment(1, 1).addSegment(-1, 0).addSegment(-1, -1).addSegment(-2, -1).addSegment(-3, -1);
        skeleton.getSegment(-1, 0).addMotor(true);
        skeleton.getSegment(-2, -1).addMotor(true);
        skeleton.getSegment(-3, -1).addMotor(true);
        creature = new Creature(0, 30, this, skeleton);
        creature.angle = (float) (Math.PI / 2);
        add(creature);
        
        planet = new Planet(0, 0, 100, this);
        add(planet);
        
        initialDist = distance();
        minDist = distance();
        
        camera.x = 43;
        camera.y = 28;
        camera.zoom = 2.4F;
        
        // physicsWorld.setGravity(new Vec2(0, -15));
        // camera.setFollowing(creature);
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
            if (substrate == null) {
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
            }
            
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
                physicsWorld.destroyBody(e.body);
                it.remove();
            }
        }
        
        Vec2 surface = planet.surfaceCoords(creature.body.getWorldCenter());
        Vec2 relPos = new Vec2(planet.point.x - surface.x, surface.y);
        Vec2 dist = creature.body.getWorldCenter().sub(planet.body.getWorldCenter());
        float angle = (float) Math.atan2(dist.y, dist.x);
        
        relX = (float) (2 / (1 + Math.exp(-0.07 * relPos.x)) - 1);
        relY = (float) (2 / (1 + Math.exp(-0.03 * relPos.y)) - 1);
        velX = Vec2.dot(creature.velocity, new Vec2(-dist.y, dist.x)) / dist.length();
        velY = Vec2.dot(creature.velocity, dist) / dist.length();
        velX = (float) (2 / (1 + Math.exp(-0.06 * velX)) - 1);
        velY = (float) (2 / (1 + Math.exp(-0.05 * velX)) - 1);
        angV = creature.body.m_angularVelocity / 50;
        lPow = creature.leftPower;
        rPow = creature.rightPower;
        
        float relAngle = (float) ((creature.body.getAngle() - angle) / Math.PI);
        while (relAngle > 1)
            relAngle -= 2;
        while (relAngle < -1)
            relAngle += 2;
        
        if (substrate != null) {
            double[] inputs = new double[8];
            inputs[0] = relX;
            inputs[1] = relY;
            inputs[2] = velX;
            inputs[3] = velY;
            inputs[4] = angV;
            inputs[5] = lPow;
            inputs[6] = rPow;
            inputs[7] = relAngle;
            
            double[] outputs = substrate.next(inputs);
            
            creature.setMotors((float) outputs[0] * 2 - 1, (float) outputs[1] * 2 - 1);
        }
        
        if (distance() < minDist) minDist = distance();
        
        if (container != null && tickCount > time) {
            container.exit();
        }
        
    }
    
    public double distance() {
        return creature.body.getWorldCenter().sub(planet.worldCoords(planet.point)).length();
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
    
    public double distance(Entity e, Vec2 p) {
        Vec2 e1T = e.body.getPosition();
        return Math.sqrt((e1T.x - p.x) * (e1T.x - p.x) + (e1T.y - p.y) * (e1T.y - p.y));
    }
    
    @Override
    public int getID() {
        return Game.AIWORLD;
    }
    
}
