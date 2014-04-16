package rekkyn.tank;

import org.newdawn.slick.Color;

public class Colours {
    
    private static ColourSets current = ColourSets.SNAZZY;
    
    public static void setColourSet(ColourSets set) {
        current = set;
    }
    
    public static Color getBody() {
        return current.body;
    }
    
    public static Color getAccent() {
        return current.accent;
    }
    
    public static Color getBackground() {
        return current.background;
    }
    
    public static Color getShadow() {
        return current.shadow;
    }
    
    public enum ColourSets {
        SNAZZY(new Color(27, 50, 95), new Color(225, 112, 51), new Color(233, 242, 249), new Color(0, 0, 0, 0.25F)),
        NORMAL(new Color(Color.black), new Color(Color.blue), new Color(Color.white), new Color(0, 0, 0, 0.25F));
        
        private final Color body;
        private final Color accent;
        private final Color background;
        private final Color shadow;
        
        ColourSets(Color body, Color accent, Color background, Color shadow) {
            this.body = body;
            this.accent = accent;
            this.background = background;
            this.shadow = shadow;
        }
    }
}
