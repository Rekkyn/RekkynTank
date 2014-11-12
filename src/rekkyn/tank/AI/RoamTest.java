package rekkyn.tank.AI;

import org.jgapcustomised.Chromosome;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import com.anji.integration.Activator;
import com.ojcoleman.ahni.evaluation.HyperNEATFitnessFunction;
import com.ojcoleman.ahni.hyperneat.Properties;

public class RoamTest extends HyperNEATFitnessFunction {
    
    public static final String TIME = "roam.time";
    public static final String DEBUG = "roam.debug";
    public int time;
    public boolean debug;
    
    @Override
    public void init(Properties props) {
        super.init(props);
        time = props.getIntProperty(TIME, 300);
        debug = props.getBooleanProperty(DEBUG, true);
    }
    
    @Override
    protected double evaluate(Chromosome genotype, Activator activator, int threadIndex) {
        return _evaluate(genotype, activator, null, false, false);
    }
    
    @Override
    public void evaluate(Chromosome genotype, Activator substrate, String baseFileName, boolean logText, boolean logImage) {
        _evaluate(genotype, substrate, baseFileName, logText, logImage);
    }
    
    public double _evaluate(Chromosome genotype, Activator substrate, String baseFileName, boolean logText, boolean logImage) {
        RoamWorld world = new RoamWorld(substrate, time, debug);
        try {
            world.init(null, null);
        } catch (SlickException e) {
            e.printStackTrace();
        }
        
        float avVel = 0;
        for (int j = 0; j < time; j++) {
            world.tick(null, null);
            avVel += (world.creature.velocity.length() - avVel) / world.tickCount;
            if (world.outOfBounds) {
                avVel = 0.0001F;
                break;
            }
        }
        
        if (logImage) {
            AIGame game = new AIGame("Food Test", substrate, time, 0, 0, false, debug, false);
            
            try {
                AppGameContainer appgc = new AppGameContainer(game);
                appgc.setDisplayMode(800, 600, false);
                appgc.setShowFPS(false);
                appgc.setAlwaysRender(true);
                appgc.setForceExit(false);
                appgc.start();
                
            } catch (SlickException e) {
                e.printStackTrace();
            }
            
        }
        
        genotype.setPerformanceValue(avVel / 40F);
        return avVel / 40F;
    }
}
