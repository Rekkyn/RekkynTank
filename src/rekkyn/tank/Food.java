package rekkyn.tank;

import org.jbox2d.collision.shapes.CircleShape;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

public class Food extends Entity {
    
    public Food(float x, float y) {
        super(x, y);
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
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        super.render(container, game, g);
        g.setColor(Colours.getAccent());
        g.fillOval(x - 0.25F, -y - 0.25F, 0.5F, 0.5F);
    }
}
