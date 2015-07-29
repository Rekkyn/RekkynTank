package rekkyn.tank.AI;

import org.jgapcustomised.Chromosome;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import com.anji.integration.Activator;
import com.ojcoleman.ahni.evaluation.HyperNEATFitnessFunction;
import com.ojcoleman.ahni.hyperneat.Properties;
import com.ojcoleman.ahni.util.Point;

public class FoodTest extends HyperNEATFitnessFunction {
    
    public static final String TIME = "food.time";
    public static final String TRIALS = "food.trials";
    public static final String RANDOM = "food.random";
    public static final String MIN = "food.min";
    public static final String DEBUG = "food.debug";
    public static final String CHAIN = "food.chain";
    public int time;
    public int trials;
    public boolean random;
    public boolean min;
    public boolean debug;
    public boolean chain;
    
    @Override
    public void init(Properties props) {
        super.init(props);
        time = props.getIntProperty(TIME, 300);
        trials = props.getIntProperty(TRIALS, 5);
        random = props.getBooleanProperty(RANDOM, false);
        min = props.getBooleanProperty(MIN, true);
        debug = props.getBooleanProperty(DEBUG, true);
        chain = props.getBooleanProperty(CHAIN, true);
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
            AIWorld world = new AIWorld(substrate, time, i, trials, random, debug, chain);
            try {
                world.init(null, null);
            } catch (SlickException e) {
                e.printStackTrace();
            }
            
            for (int j = 0; j < time; j++) {
                world.tick(null, null);
                if (world.gotFood) {
                    if (chain) {
                        fitness += 1;
                        world.addFood();
                    } else {
                        break;
                    }
                }
            }
            
            if (chain) {
                double initialDist = world.initialDist;
                double distance = world.distance(world.creature, world.food);
                if (distance > initialDist) distance = initialDist;
                fitness += 1 - distance / initialDist;
                fitness /= 10;
            } else {
                if (world.gotFood) {
                    fitness += 1D - 0.5D / time * world.tickCount;
                } else {
                    if (min) {
                        fitness += 0.5D - 0.5D / world.initialDist * world.minDist;
                    } else {
                        double distance = world.distance(world.creature, world.food);
                        fitness += Math.pow(0.954842, distance + 15);
                    }
                }
            }
            
            if (logImage) {
                AIGame game = new AIGame("Food Test", substrate, time, i, trials, random, debug, chain);
                
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
    
    @Override
    public int[] getLayerDimensions(int layer, int totalLayerCount) {
        if (layer == 0) // Input layer.
            return new int[] { 7, 1 };
        else if (layer == totalLayerCount - 1) { // Output layer.
            return new int[] { 2, 1 };
        }
        return null;
    }
    
    @Override
    public Point[] getNeuronPositions(int layer, int totalLayerCount) {
        // Coordinates are given in unit ranges and translated to whatever range
        // is specified by the
        // experiment properties.
        Point[] positions = null;
        if (layer == 0) { // Input layer.
            positions = new Point[7];
            positions[0] = new Point(0, 1, 0);
            positions[1] = new Point(0, 0.8, 0);
            positions[2] = new Point(0, 0.6, 0);
            positions[3] = new Point(0, 0.4, 0);
            positions[4] = new Point(0, 0.2, 0);
            positions[5] = new Point(0.25, 0.5, 0);
            positions[6] = new Point(0.75, 0.5, 0);
        } else if (layer == totalLayerCount - 1) { // Output layer.
            positions = new Point[2];
            positions[0] = new Point(0.25, 0.5, 1);
            positions[1] = new Point(0.75, 0.5, 1);
        }
        return positions;
    }
}