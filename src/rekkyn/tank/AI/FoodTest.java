package rekkyn.tank.AI;

import org.jgapcustomised.Chromosome;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import com.anji.integration.Activator;
import com.ojcoleman.ahni.evaluation.HyperNEATFitnessFunction;
import com.ojcoleman.ahni.hyperneat.Properties;

public class FoodTest extends HyperNEATFitnessFunction {
    
    public static final String TIME = "food.time";
    public static final String TRIALS = "food.trials";
    public int time;
    public int trials;
    
    @Override
    public void init(Properties props) {
        super.init(props);
        time = props.getIntProperty(TIME, 300);
        trials = props.getIntProperty(TRIALS, 5);
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
            AIWorld world = new AIWorld(substrate, time);
            try {
                world.init(null, null);
            } catch (SlickException e) {
                e.printStackTrace();
            }
            
            for (int j = 0; j < time; j++) {
                world.tick(null, null);
                if (world.gotFood) break;
            }
            
            if (world.gotFood) {
                fitness += 1D - 0.5D / time * world.tickCount;
            } else {
                fitness += 0.5D - 0.5D / world.initialDist * world.minDist;
            }
            
            if (logImage) {
                AIGame game = new AIGame("Food Test", substrate);
                
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
