package rekkyn.tank;

import org.jbox2d.collision.shapes.PolygonShape;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

public class Wall extends Entity {
    
    public float width;
    public float height;
    
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
        g.setColor(Colours.getBody());
        g.fillRect(x - width / 2, -y - height / 2, width, height);
    }
    
    @Override
    public Object[] getSpecificData() {
        return new Object[] { width, height };
    }
    
    @Override
    public void setSpecificData(Object[] data) {
        width = (Float) data[0];
        height = (Float) data[1];
    }
    
}
