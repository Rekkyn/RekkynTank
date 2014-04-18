package rekkyn.tank;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;

import rekkyn.tank.Skeleton.Segment;

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
            Vec2 relPos = globalPos.sub(new Vec2(s.creature.x, s.creature.y));
            float angle = (float) (Math.PI / 4 - s.creature.angle);
            Vec2 rotated = new Vec2((float) (relPos.x * Math.cos(angle) - relPos.y * Math.sin(angle)),
                    (float) (relPos.x * Math.sin(angle) + relPos.y * Math.cos(angle)));
            Vec2 segmemtPos = rotated.sub(new Vec2(s.x, s.y));
            s.contact = segmemtPos;
            System.out.println(segmemtPos);
        }
        
        System.out.println("----------------------------------");
        
    }
}

