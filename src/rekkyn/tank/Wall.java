package rekkyn.tank;

import org.jbox2d.collision.shapes.PolygonShape;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

public class Wall extends Entity {
    
    private float width;
    private float height;
    
    public Wall(float x, float y, float width, float height, GameWorld world) {
        super(x, y, world);
        this.width = width;
        this.height = height;
        
    }
    
    @Override
    public void init() {
        super.init();
        
        PolygonShape ps = new PolygonShape();
        ps.setAsBox(width / 2, height / 2);
        body.createFixture(ps, 1).setFriction(0.3F);
        body.setGravityScale(0);
    }
    
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        super.render(container, game, g);
        g.fillRect(x - width / 2, -y - height / 2, width, height);
    }
    
    @Override
    public Object[] getData() {
        return new Object[] { width, height };
    }
    
}
