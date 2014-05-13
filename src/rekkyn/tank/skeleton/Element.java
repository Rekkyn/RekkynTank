package rekkyn.tank.skeleton;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import rekkyn.tank.Creature;
import rekkyn.tank.GameWorld;
import rekkyn.tank.skeleton.Skeleton.ElementType;

import com.esotericsoftware.kryo.serializers.FieldSerializer.Optional;

public class Element {
    public ElementType type;
    
    public List<int[]> locations = new ArrayList<int[]>();
    
    @Optional(value = "")
    public Color colour;
    
    public Element(List<int[]> locations) {
        this.locations = locations;
    }
    
    public void render(Graphics g) {}
    
    public void update(Creature c) {}
    
    public void contact(Object o, GameWorld world) {}
    
    public Element() {}
}
