package rekkyn.tank;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.FixtureDef;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

public class Particle extends Entity {
    
    public int age;
    
    public Color colour;
    public float alpha = 0.25F;
    public int ageDecay = 30;
    
    public Particle(float x, float y, GameWorld world) {
        super(x, y, world);
        colour = Colours.getDark();
        colour.a = alpha;
    }
    
    @Override
    public void init() {
        super.init();
        
        CircleShape shape = new CircleShape();
        shape.setRadius(0.125F);
        FixtureDef def = new FixtureDef();
        def.shape = shape;
        def.density = 0.5F;
        def.filter.maskBits = 0;
        body.createFixture(def).setUserData(this);
    }
    
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        super.update(container, game, delta);
        age++;
        if (age > ageDecay) remove();
        
        colour.a = -alpha * age / ageDecay + alpha;
    }
    
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        super.render(container, game, g);
        g.setColor(colour);
        g.fillOval(x - 0.125F, -y - 0.125F, 0.25F, 0.25F);
    }
    
    @Override
    public Object[] getSpecificData() {
        return null;
    }
    
    @Override
    public void setSpecificData(Object[] data) {}
    
}
