package rekkyn.tank.AI;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.anji.integration.Activator;

public class AIGame extends StateBasedGame {
    
    Activator substrate;
    
    public AIGame(String name) {
        super(name);
    }
    
    public AIGame(String name, Activator substrate) {
        this(name);
        this.substrate = substrate;
    }
    
    @Override
    public void initStatesList(GameContainer container) throws SlickException {
        addState(new AIWorld(substrate));
    }
    
}
