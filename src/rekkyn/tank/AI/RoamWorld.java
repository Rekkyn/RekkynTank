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

public class RoamWorld extends GameWorld {
    
    Activator substrate;
    Creature creature;
    
    public int time;
    public boolean debug;
    
    public boolean outOfBounds = false;
    public float boundaryStart = 1;
    public float boundaryEnd = 50;
    public float boundary = boundaryStart;
    
    float relX, relY;
    float velX, velY;
    float angV;
    float lPow, rPow;
    
    public RoamWorld(Activator substrate, int time, boolean debug) {
        this.substrate = substrate;
        this.time = time;
        this.debug = debug;
    }
    
    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        super.init(container, game);
        Skeleton skeleton = new Skeleton();
        skeleton.addSegment(0, 1).addSegment(1, 1).addSegment(-1, 1).addSegment(-1, 0).addSegment(-1, -1);
        skeleton.getSegment(-1, 1).addMotor(true);
        skeleton.getSegment(-1, 0).addMotor(true);
        skeleton.getSegment(-1, -1).addMotor(true);
        skeleton.getSegment(0, 1).addMotor(true);
        skeleton.getSegment(1, 1).addMotor(true);
        creature = new Creature(0, 0, this, skeleton);
        creature.angle = (float) (Math.PI / 2);
        add(creature);
        
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
                physicsWorld.destroyBody(e.body);
                it.remove();
            }
        }
        
        boundary = (boundaryEnd - boundaryStart) / time * tickCount + boundaryStart;
        
        if (distance(creature, new Vec2(0, 0)) > boundary) outOfBounds = true;
        
        Vec2 relPos = Util.rotateVec(creature.body.getLocalPoint(new Vec2(0, 0)), (float) (Math.PI / 2));
        
        relX = (float) (2 / (1 + Math.exp(-0.2 * relPos.x)) - 1);
        relY = (float) (2 / (1 + Math.exp(-0.2 * relPos.y)) - 1);
        velX = Util.rotateVec(creature.body.m_linearVelocity, (float) (Math.PI / 2 - creature.angle)).x / 10;
        velY = Util.rotateVec(creature.body.m_linearVelocity, (float) (Math.PI / 2 - creature.angle)).y / 10;
        angV = creature.body.m_angularVelocity / 8;
        lPow = creature.leftPower;
        rPow = creature.rightPower;
        
        if (substrate != null) {
            double[] inputs = new double[8];
            inputs[0] = relX;
            inputs[1] = relY;
            inputs[2] = velX;
            inputs[3] = velY;
            inputs[4] = angV;
            inputs[5] = lPow;
            inputs[6] = rPow;
            inputs[7] = boundaryStart > boundaryEnd ? boundary / boundaryStart : boundary / boundaryEnd;
            
            double[] outputs = substrate.next(inputs);
            
            creature.setMotors((float) outputs[0] * 2 - 1, (float) outputs[1] * 2 - 1);
        }
        
        if (container != null && (tickCount > time || outOfBounds)) {
            container.exit();
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
        g.pushTransform();
        g.scale(camera.zoom, camera.zoom);
        g.translate(-camera.x + Game.width / camera.zoom / 2, camera.y + Game.height / camera.zoom / 2);
        g.drawOval(-boundary, -boundary, 2 * boundary, 2 * boundary);
        g.popTransform();
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
