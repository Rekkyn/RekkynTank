package rekkyn.tank;

import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

import rekkyn.tank.AI.AIWorld;
import rekkyn.tank.skeleton.Skeleton;

public class Game extends StateBasedGame {
    
    static AppGameContainer appgc;
    public static final String NAME = "Rekkyn Tank";
    public static final int WORLD = 0;
    public static final int MENU = 1;
    public static final int EDITOR = 2;
    public static final int TESTWORLD = 3;
    public static final int AIWORLD = 4;
    public static int width = 800;
    public static int height = 600;
    
    public GameWorld world = new GameWorld();
    
    public Skeleton skeleton = Skeleton.defaultSkeleton();
    
    public Game(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        
        try {
            appgc = new AppGameContainer(new Game(NAME));
            // width = appgc.getScreenWidth();
            // height = appgc.getScreenHeight();
            appgc.setDisplayMode(width, height, false);
            appgc.setShowFPS(false);
            appgc.setAlwaysRender(true);
            appgc.start();
            
        } catch (SlickException e) {
            e.printStackTrace();
        }
        
    }
    
    @Override
    public void initStatesList(GameContainer container) throws SlickException {
        addState(new Menu());
        addState(world);
        addState(new Editor(skeleton));
        addState(new TestWorld());
        addState(new AIWorld(null, 300, 0, 1, true, false, false));
    }
    
    public static Image scaleImage(Image image, int scale) {
        if (image != null) {
            image.setFilter(Image.FILTER_NEAREST);
            return image.getScaledCopy(scale);
        }
        return image;
    }
    
}
