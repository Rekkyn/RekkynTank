package rekkyn.tank.skeleton;

import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

import rekkyn.tank.skeleton.Skeleton.ElementType;

public class Element {
    public ElementType type;
    public Segment segment;
    
    public Element(Segment s) {
        segment = s;
    }
    
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {}
    
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {}
    
    @Deprecated
    public Element() {}
}
