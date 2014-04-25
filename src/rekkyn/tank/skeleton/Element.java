package rekkyn.tank.skeleton;

import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

import rekkyn.tank.skeleton.Skeleton.ElementType;

public class Element {
    public ElementType type;
    public Segment segment;
    public Color colour;
    
    public Element(Segment s) {
        segment = s;
    }
    
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {}
    
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {}
    
    public void contact(Object o) {}
    
    @Deprecated
    public Element() {}
}
