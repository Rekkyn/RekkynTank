package rekkyn.tank;

import org.newdawn.slick.Color;

public class Colours {
    
    private static ColourSets current = ColourSets.SNAZZY;
    
    public static void setColourSet(ColourSets set) {
        current = set;
    }
    
    public static Color getBody() {
        return new Color(current.body.r, current.body.g, current.body.b);
    }
    
    public static Color getAccent() {
        return new Color(current.accent.r, current.accent.g, current.accent.b);
    }
    
    public static Color getBackground() {
        return new Color(current.background.r, current.background.g, current.background.b);
    }
    
    public static Color getShadow() {
        return new Color(current.shadow.r, current.shadow.g, current.shadow.b, current.shadow.a);
    }
    
    public static Color getDark() {
        return new Color(current.dark.r, current.dark.g, current.dark.b);
    }
    
    public enum ColourSets {
        SNAZZY(new Color(27, 50, 95), new Color(225, 112, 51), new Color(233, 242, 249), new Color(0, 0, 0, 0.25F), new Color(58, 137, 201)),
        NORMAL(new Color(Color.black), new Color(Color.blue), new Color(Color.white), new Color(0, 0, 0, 0.25F), new Color(58, 137, 201));
        
        private final Color body;
        private final Color accent;
        private final Color background;
        private final Color shadow;
        private final Color dark;
        
        ColourSets(Color body, Color accent, Color background, Color shadow, Color dark) {
            this.body = body;
            this.accent = accent;
            this.background = background;
            this.shadow = shadow;
            this.dark = dark;
        }
    }
}
