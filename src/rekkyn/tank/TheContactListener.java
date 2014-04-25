package rekkyn.tank;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;

import rekkyn.tank.skeleton.Segment;

public class TheContactListener implements ContactListener {
    
    @Override
    public void beginContact(Contact contact) {}
    
    @Override
    public void endContact(Contact contact) {}
    
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}
    
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        
        
        Object bodyDataA = contact.getFixtureA().getUserData();
        Object bodyDataB = contact.getFixtureB().getUserData();
        
        if (bodyDataA instanceof Segment) {
            segmentCollision((Segment) bodyDataA, bodyDataB, contact);
        }
        
        if (bodyDataB instanceof Segment) {
            segmentCollision((Segment) bodyDataB, bodyDataA, contact);
        }
    }
    
    public void segmentCollision(Segment s, Object o, Contact contact) {
        WorldManifold manifold = new WorldManifold();
        contact.getWorldManifold(manifold);
        
        for (int i = 0; i < contact.getManifold().pointCount; i++) {
            Vec2 globalPos = new Vec2(manifold.points[i].x, manifold.points[i].y);
            Vec2 relPos = globalPos.sub(new Vec2(s.skeleton.creature.x, s.skeleton.creature.y));
            float rotateAngle = (float) (Math.PI / 4 - s.skeleton.creature.angle);
            Vec2 rotated = new Vec2((float) (relPos.x * Math.cos(rotateAngle) - relPos.y * Math.sin(rotateAngle)), (float) (relPos.x
                    * Math.sin(rotateAngle) + relPos.y * Math.cos(rotateAngle)));
            Vec2 segmemtPos = rotated.sub(new Vec2(s.x, s.y));
            
            float angle = (float) Math.atan2(segmemtPos.y, segmemtPos.x);
            
            s.elements[elementFromAngle(angle)].contact(o);
        }
    }
    
    public int elementFromAngle(float angle) {
        if (angle > 0 && angle < Math.PI / 4) return 0;
        if (angle < Math.PI / 2 && angle > Math.PI / 4) return 7;
        if (angle > Math.PI / 2 && angle < 3 * Math.PI / 4) return 6;
        if (angle < Math.PI && angle > 3 * Math.PI / 4) return 5;
        
        if (angle < 0 && angle > -Math.PI / 4) return 1;
        if (angle > -Math.PI / 2 && angle < -Math.PI / 4) return 2;
        if (angle < -Math.PI / 2 && angle > -3 * Math.PI / 4) return 3;
        if (angle > -Math.PI && angle < -3 * Math.PI / 4) return 4;
        
        return -1;
    }
}

