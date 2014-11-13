package rekkyn.tank.AI;

import org.jgapcustomised.Chromosome;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import com.anji.integration.Activator;
import com.ojcoleman.ahni.evaluation.HyperNEATFitnessFunction;
import com.ojcoleman.ahni.hyperneat.Properties;

public class PlanetTest extends HyperNEATFitnessFunction {
    
    public static final String TIME = "planet.time";
    public static final String TRIALS = "planet.trials";
    public static final String RANDOM = "planet.random";
    public static final String MIN = "planet.min";
    public static final String DEBUG = "planet.debug";
    public int time;
    public int trials;
    public boolean random;
    public boolean min;
    public boolean debug;
    
    @Override
    public void init(Properties props) {
        super.init(props);
        time = props.getIntProperty(TIME, 300);
        trials = props.getIntProperty(TRIALS, 5);
        random = props.getBooleanProperty(RANDOM, false);
        min = props.getBooleanProperty(MIN, false);
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
        double fitness = 0;
        for (int i = 0; i < trials; i++) {
            PlanetWorld world = new PlanetWorld(substrate, time, i, trials, random, debug);
            try {
                world.init(null, null);
            } catch (SlickException e) {
                e.printStackTrace();
            }
            
            long gotTime = 0;
            for (int j = 0; j < time; j++) {
                world.tick(null, null);
                if (world.distance() < 2 && gotTime == 0) gotTime = world.tickCount;
            }
            
            if (world.distance() < 2) {
                fitness += 1D - 0.5D / time * gotTime;
            } else {
                if (min) {
                    fitness += 0.5D - 0.5D / world.initialDist * world.minDist;
                } else {
                    fitness += Math.pow(0.96, world.distance() + 16.9797);
                }
            }
            
            if (logImage) {
                AIGame game = new AIGame("Planets", substrate, time, i, trials, random, debug, false);
                
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
        }
        
        genotype.setPerformanceValue(fitness / trials);
        return fitness / trials;
    }
}
