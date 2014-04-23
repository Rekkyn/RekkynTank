package rekkyn.tank;

import org.jbox2d.collision.shapes.PolygonShape;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

public class Wall extends Entity {
    
    private float width;
    private float height;
    
    public Wall(float x, float y, float width, float height) {
        super(x, y);
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
    public void renderBackground(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        super.renderBackground(container, game, g);
        g.pushTransform();
        float dist = (float) (1 / Math.sqrt(8));
        g.translate((float) (Math.cos(angle + Math.PI / 4) * dist), (float) (Math.sin(angle + Math.PI / 4) * dist));
        g.setColor(Colours.getShadow());
        g.fillRect(x - width / 2, -y - height / 2, width, height);
        g.popTransform();
    }
    
}
