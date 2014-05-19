package rekkyn.tank;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.FixtureDef;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class Particle extends Entity {
    
    public int age;
    
    public Color colour;
    public float alpha = 0.25F;
    public int ageDecay;
    public float radius;
    
    public Particle(float x, float y, Color colour, int age, float radius, GameWorld world) {
        super(x, y, world);
        this.colour = colour;
        colour.a = alpha;
        ageDecay = age;
        this.radius = radius;
        shouldSend = false;
    }
    
    @Override
    public void init() {
        super.init();
        
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        FixtureDef def = new FixtureDef();
        def.shape = shape;
        def.density = 0.5F;
        def.filter.maskBits = 0;
        body.createFixture(def).setUserData(this);
    }
    
    @Override
    public void update() {
        super.update();
        age++;
        if (age > ageDecay) remove();
        
        colour.a = -alpha * age / ageDecay + alpha;
    }
    
    @Override
    public void render(Graphics g) {
        super.render(g);
        g.setColor(colour);
        g.fillOval(x - radius, -y - radius, 2 * radius, 2 * radius);
    }
    
    @Override
    public Object[] getSpecificData() {
        return null;
    }
    
    @Override
    public void setSpecificData(Object[] data) {}
    
}
