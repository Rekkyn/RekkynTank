package rekkyn.tank;

import org.jbox2d.collision.shapes.CircleShape;
import org.newdawn.slick.Graphics;

public class Food extends Entity {
    
    public Food(float x, float y, GameWorld world) {
        super(x, y, world);
    }
    
    @Override
    public void init() {
        super.init();
        
        CircleShape shape = new CircleShape();
        shape.setRadius(0.25F);
        body.createFixture(shape, 1).setUserData(this);
        
        body.setBullet(true);
    }
    
    @Override
    public void render(Graphics g) {
        super.render(g);
        g.setColor(Colours.getAccent());
        g.fillOval(x - 0.25F, -y - 0.25F, 0.5F, 0.5F);
    }
    
    @Override
    public Object[] getSpecificData() {
        return null;
    }
    
    @Override
    public void setSpecificData(Object[] data) {}
}
