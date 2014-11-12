package rekkyn.tank.AI;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.anji.integration.Activator;

public class AIGame extends StateBasedGame {
    
    Activator substrate;
    int time;
    int trial;
    int maxTrails;
    boolean random;
    boolean debug;
    boolean chain;
    
    public AIGame(String name) {
        super(name);
    }
    
    public AIGame(String name, Activator substrate, int time, int trial, int maxTrials, boolean random, boolean debug, boolean chain) {
        this(name);
        this.substrate = substrate;
        this.time = time;
        this.trial = trial;
        maxTrails = maxTrials;
        this.random = random;
        this.debug = debug;
        this.chain = chain;
    }
    
    public AIGame(String name, Activator substrate, int time) {
        this(name);
        this.substrate = substrate;
        this.time = time;
    }
    
    @Override
    public void initStatesList(GameContainer container) throws SlickException {
        // addState(new AIWorld(substrate, time, trial, maxTrails, random,
        // debug, chain));
        // addState(new RetrieveWorld(substrate, time));
        addState(new RoamWorld(substrate, time, debug));
    }
    
}
