package rekkyn.tank;

import java.util.*;
import java.util.Map.Entry;

import org.jbox2d.common.Vec2;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import rekkyn.tank.Colours.ColourSets;
import rekkyn.tank.skeleton.*;

public class Editor extends BasicGameState {
    
    float accumulator = 0.0F;
    public long tickCount = 0;
    public final float TIMESTEP = 50F / 3F;
    
    public Skeleton skeleton = new Skeleton(null);
    Camera camera = new Camera();
    
    HashMap<Segment, Object[]> cooldowns = new HashMap<Segment, Object[]>();
    int cooldown = 20;
    
    public int selected = 1;
    
    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        skeleton.addSegment(1, 1).addSegment(2, 2).addSegment(0, 2).addSegment(1, 2);
        skeleton.getSegment(0, 2).addMotor(true);
        skeleton.getSegment(2, 2).addElement(new Mouth(), 1).addElement(new Mouth(), 7);
        camera.zoom = 30;
    }
    
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        
        if (delta > 25) delta = 25;
        accumulator += delta;
        
        while (accumulator >= TIMESTEP) {
            if (container.hasFocus()) {
                tick(container, game, delta);
            }
            accumulator -= TIMESTEP;
        }
    }
    
    public void tick(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        Input input = container.getInput();
        
        if (input.isKeyDown(Input.KEY_RIGHT)) {
            camera.x += 4 / camera.zoom;
        }
        if (input.isKeyDown(Input.KEY_LEFT)) {
            camera.x -= 4 / camera.zoom;
        }
        if (input.isKeyDown(Input.KEY_UP)) {
            camera.y += 4 / camera.zoom;
        }
        if (input.isKeyDown(Input.KEY_DOWN)) {
            camera.y -= 4 / camera.zoom;
        }
        if (input.isKeyDown(Input.KEY_EQUALS)) {
            camera.zoom *= 1.01;
        }
        if (input.isKeyDown(Input.KEY_MINUS)) {
            camera.zoom *= 0.99;
        }
        camera.update();
        
        if (input.isKeyPressed(Input.KEY_T)) {
            TestWorld testWorld = (TestWorld) game.getState(Game.TESTWORLD);
            testWorld.addTestCreatrue(skeleton);
            game.enterState(Game.TESTWORLD);
        }
        
        if (input.isKeyPressed(Input.KEY_1)) {
            selected = 1;
        }
        if (input.isKeyPressed(Input.KEY_2)) {
            selected = 2;
        }
        if (input.isKeyPressed(Input.KEY_3)) {
            selected = 3;
        }
        
        Iterator<Entry<Segment, Object[]>> it = cooldowns.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = it.next();
            Integer cool = (Integer) ((Object[]) pairs.getValue())[0];
            Boolean added = (Boolean) ((Object[]) pairs.getValue())[1];
            if (added) {
                if (cool > 0) {
                    cooldowns.put((Segment) pairs.getKey(), new Object[] { cool - 1, added });
                } else {
                    skeleton.addSegment((Segment) pairs.getKey());
                    it.remove();
                }
            } else {
                if (cool < cooldown) {
                    cooldowns.put((Segment) pairs.getKey(), new Object[] { cool + 1, added });
                } else {
                    it.remove();
                }
            }
        }
    }
    
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        Input input = container.getInput();
        
        g.setAntiAlias(true);
        Colours.setColourSet(ColourSets.SNAZZY);
        
        g.setColor(Colours.getBackground());
        g.fillRect(0, 0, Game.width, Game.height);
        
        g.pushTransform();
        g.scale(camera.zoom, camera.zoom);
        g.translate(-camera.x + Game.width / camera.zoom / 2, camera.y + Game.height / camera.zoom / 2);
        
        g.pushTransform();
        float root2 = (float) Math.sqrt(2);
        g.translate(root2 / 2, 0);
        Color c = Colours.getDark();
        c.a = 0.5F;
        g.setColor(c);
        int gridSize = 50;
        float f = root2 * gridSize / 2;
        for (int i = -gridSize - 1; i <= gridSize; i++) {
            g.drawLine(-f + (i - 1) / root2, -f - (i + 1) / root2, f + i / root2, f - i / root2);
        }
        for (int i = -gridSize - 1; i <= gridSize; i++) {
            g.drawLine(-f + (i - 1) / root2, f + (i + 1) / root2, f + i / root2, -f + i / root2);
        }
        g.popTransform();
        
        g.rotate(0, 0, -45);
        
        g.pushTransform();
        float dist = (float) (1 / Math.sqrt(8));
        g.translate(0, dist);
        
        // shadows
        for (Segment s : skeleton.segments) {
            g.setColor(Colours.getShadow());
            g.fillRect(s.x - 0.5F, -s.y - 0.5F, 1, 1);
        }
        
        drawCompletingSegments(g);
        g.popTransform();
        
        // body
        for (Segment s : skeleton.segments) {
            if (s instanceof Heart) {
                g.setColor(Colours.getAccent());
            } else {
                g.setColor(Colours.getBody());
            }
            g.fillRect(s.x - 0.5F, -s.y - 0.5F, 1, 1);
            g.pushTransform();
            g.translate(s.x, -s.y);
            s.render(container, game, g);
            g.popTransform();
        }
        
        // draw half completed segments
        g.setColor(Colours.getBody());
        drawCompletingSegments(g);
        
        
        
        
        // not rendering stuff
        Vec2 mouse = Util.rotateVec(mousePos(container), (float) (-Math.PI / 4));
        int mouseX = Math.round(mouse.x);
        int mouseY = Math.round(mouse.y);
        boolean[] neighbours = getNeighbours(mouseX, mouseY);
        
        boolean overSegment = false;
        for (Segment s : skeleton.segments) {
            if (s.x == mouseX && s.y == mouseY) {
                overSegment = true;
            }
        }
        
        if (selected == 1) {
            // draw outline
            if (overSegment) {
                g.setColor(Colours.getAccent());
                g.setLineWidth(2);
                g.drawRect(mouseX - 0.5F, -mouseY - 0.5F, 1, 1);
                if (mouseX != mouseY) g.drawRect(mouseY - 0.5F, -mouseX - 0.5F, 1, 1);
            }
            
            // remove segment
            if (overSegment && input.isMousePressed(Input.MOUSE_RIGHT_BUTTON) && !(skeleton.getSegment(mouseX, mouseY) instanceof Heart)) {
                cooldowns.put(new Segment(mouseX, mouseY, skeleton), new Object[] { 0, false });
                if (mouseX != mouseY) cooldowns.put(new Segment(mouseY, mouseX, skeleton), new Object[] { 0, false });
                skeleton.removeSegment(mouseX, mouseY);
            }
            
            // draw posible segment
            if (!overSegment
                    && (neighbours[0] || neighbours[1] || neighbours[2] || neighbours[3] || neighbours[4] || neighbours[5] || neighbours[6] || neighbours[7])) {
                Color col = Colours.getBody();
                col.a = 0.5F;
                g.setColor(col);
                g.fillRect(mouseX - 0.5F, -mouseY - 0.5F, 1, 1);
                if (mouseX != mouseY)
                    g.fillRect(mouseY - 0.5F, -mouseX - 0.5F, 1, 1);
                
                if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
                    cooldowns.put(new Segment(mouseX, mouseY, skeleton), new Object[] { cooldown, true });
                    if (mouseX != mouseY) cooldowns.put(new Segment(mouseY, mouseX, skeleton), new Object[] { cooldown, true });
                }
            }
        } else if (selected == 2) {
            Motor motor = new Motor();
            if (overSegment && !(skeleton.getSegment(mouseX, mouseY) instanceof Heart)) {
                if (skeleton.getSegment(mouseX, mouseY).elements[8] instanceof BlankElement) {
                    g.pushTransform();
                    g.translate(mouseX, -mouseY);
                    motor.render(container, game, g);
                    g.popTransform();
                    
                    if (mouseX != mouseY) {
                        g.pushTransform();
                        g.translate(mouseY, -mouseX);
                        motor.render(container, game, g);
                        g.popTransform();
                    }
                    
                    if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
                        skeleton.getSegment(mouseX, mouseY).addElement(motor, 8);
                        if (mouseX != mouseY) skeleton.getSegment(mouseY, mouseX).addElement(new Motor(), 8);
                    }
                } else {
                    g.setColor(Colours.getAccent());
                    g.setLineWidth(2);
                    g.drawRect(mouseX - 0.25F, -mouseY - 0.25F, 0.5F, 0.5F);
                    if (mouseX != mouseY) g.drawRect(mouseY - 0.25F, -mouseX - 0.25F, 0.5F, 0.5F);
                    if (input.isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {
                        skeleton.getSegment(mouseX, mouseY).removeElement(8);
                        if (mouseX != mouseY) skeleton.getSegment(mouseY, mouseX).removeElement(8);
                    }
                }
            } else {
                g.pushTransform();
                g.translate(mouse.x, -mouse.y);
                motor.render(container, game, g);
                g.popTransform();
            }
        } else if (overSegment && selected == 3) {
            float segX = skeleton.getSegment(mouseX, mouseY).x;
            float segY = skeleton.getSegment(mouseX, mouseY).y;
            float angle = (float) Math.atan2(mouse.y - segY, mouse.x - segX);
            int pos = 0;
            
            if (angle > Math.PI / 8 && angle < 3 * Math.PI / 8) {
                pos = 0;
            }
            if (angle < 5 * Math.PI / 8 && angle > 3 * Math.PI / 8) {
                pos = 7;
            }
            if (angle > 5 * Math.PI / 8 && angle < 7 * Math.PI / 8) {
                pos = 6;
            }
            if (angle < Math.PI / 8 && angle > -Math.PI / 8) {
                pos = 1;
            }
            if (angle > -3 * Math.PI / 8 && angle < -Math.PI / 8) {
                pos = 2;
            }
            if (angle < -3 * Math.PI / 8 && angle > -5 * Math.PI / 8) {
                pos = 3;
            }
            if (angle > -7 * Math.PI / 8 && angle < -5 * Math.PI / 8) {
                pos = 4;
            }
            if (angle > 7 * Math.PI / 8 || angle < -7 * Math.PI / 8) {
                pos = 5;
            }
            
            g.pushTransform();
            g.translate(segX, -segY);
            g.setColor(Colours.getAccent());
            g.setLineWidth(2);
            if (pos == 0 || pos == 1) {
                g.drawLine(0.5F, -0.5F, 0.5F, 0);
                g.drawLine(0.25F, -0.25F, 0.25F, 0);
            }
            if (pos == 2 || pos == 1) {
                g.drawLine(0.5F, 0.5F, 0.5F, 0);
                g.drawLine(0.25F, 0.25F, 0.25F, 0);
            }
            if (pos == 2 || pos == 3) {
                g.drawLine(0.5F, 0.5F, 0, 0.5F);
                g.drawLine(0.25F, 0.25F, 0, 0.25F);
            }
            if (pos == 4 || pos == 3) {
                g.drawLine(-0.5F, 0.5F, 0, 0.5F);
                g.drawLine(-0.25F, 0.25F, 0, 0.25F);
            }
            if (pos == 4 || pos == 5) {
                g.drawLine(-0.5F, 0.5F, -0.5F, 0);
                g.drawLine(-0.25F, 0.25F, -0.25F, 0);
            }
            if (pos == 6 || pos == 5) {
                g.drawLine(-0.5F, -0.5F, -0.5F, 0);
                g.drawLine(-0.25F, -0.25F, -0.25F, 0);
            }
            if (pos == 6 || pos == 7) {
                g.drawLine(-0.5F, -0.5F, 0, -0.5F);
                g.drawLine(-0.25F, -0.25F, 0, -0.25F);
            }
            if (pos == 0 || pos == 7) {
                g.drawLine(0.5F, -0.5F, 0, -0.5F);
                g.drawLine(0.25F, -0.25F, 0, -0.25F);
            }
            
            if (pos == 0 || pos == 2) {
                g.drawLine(0.25F, 0, 0.5F, 0);
            }
            if (pos == 1 || pos == 3) {
                g.drawLine(0.25F, 0.25F, 0.5F, 0.5F);
            }
            if (pos == 2 || pos == 4) {
                g.drawLine(0, 0.25F, 0, 0.5F);
            }
            if (pos == 3 || pos == 5) {
                g.drawLine(-0.25F, 0.25F, -0.5F, 0.5F);
            }
            if (pos == 4 || pos == 6) {
                g.drawLine(-0.25F, 0, -0.5F, 0);
            }
            if (pos == 5 || pos == 7) {
                g.drawLine(-0.25F, -0.25F, -0.5F, -0.5F);
            }
            if (pos == 6 || pos == 0) {
                g.drawLine(0, -0.25F, 0, -0.5F);
            }
            if (pos == 7 || pos == 1) {
                g.drawLine(0.25F, -0.25F, 0.5F, -0.5F);
            }
            
            g.popTransform();
            
            if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
                skeleton.getSegment(mouseX, mouseY).addElement(new Mouth(), pos);
                if (!(mouseX == mouseY && (pos == 0 || pos == 4)))
                    skeleton.getSegment(mouseY, mouseX).addElement(new Mouth(), 8 - pos < 8 ? 8 - pos : 0);
            }
            if (input.isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {
                skeleton.getSegment(mouseX, mouseY).removeElement(pos);
                if (!(mouseX == mouseY && (pos == 0 || pos == 4)))
                    skeleton.getSegment(mouseY, mouseX).removeElement(8 - pos < 8 ? 8 - pos : 0);
            }
        }
        
        
        g.popTransform();
    }
    
    public void drawCompletingSegments(Graphics g) {
        Iterator<Entry<Segment, Object[]>> it1 = cooldowns.entrySet().iterator();
        while (it1.hasNext()) {
            Map.Entry pairs = it1.next();
            Segment s = (Segment) pairs.getKey();
            Integer cool = (Integer) ((Object[]) pairs.getValue())[0];
            boolean[] bool = getNeighbours(s.x, s.y);
            if (bool[1]) {
                g.fillRect(s.x - 0.5F, -s.y - 0.5F, (float) -cool / cooldown + 1, 1);
            }
            if (bool[3]) {
                g.fillRect(s.x - 0.5F, -s.y - 0.5F, 1, (float) -cool / cooldown + 1);
            }
            if (bool[5]) {
                g.fillRect(s.x + (float) cool / cooldown - 0.5F, -s.y - 0.5F, (float) -cool / cooldown + 1, 1);
            }
            if (bool[7]) {
                g.fillRect(s.x - 0.5F, -s.y + (float) cool / cooldown - 0.5F, 1, (float) -cool / cooldown + 1);
            }
            
            if (bool[0] && !bool[1] && !bool[7]) {
                g.fillRect(s.x - 0.5F, -s.y - 0.5F, (float) -cool / cooldown + 1, 1);
                g.fillRect(s.x - 0.5F, -s.y + (float) cool / cooldown - 0.5F, 1, (float) -cool / cooldown + 1);
                
            }
            if (bool[2] && !bool[3] && !bool[1]) {
                g.fillRect(s.x - 0.5F, -s.y - 0.5F, (float) -cool / cooldown + 1, 1);
                g.fillRect(s.x - 0.5F, -s.y - 0.5F, 1, (float) -cool / cooldown + 1);
            }
            if (bool[4] && !bool[5] && !bool[3]) {
                g.fillRect(s.x + (float) cool / cooldown - 0.5F, -s.y - 0.5F, (float) -cool / cooldown + 1, 1);
                g.fillRect(s.x - 0.5F, -s.y - 0.5F, 1, (float) -cool / cooldown + 1);
            }
            if (bool[6] && !bool[7] && !bool[5]) {
                g.fillRect(s.x + (float) cool / cooldown - 0.5F, -s.y - 0.5F, (float) -cool / cooldown + 1, 1);
                g.fillRect(s.x - 0.5F, -s.y + (float) cool / cooldown - 0.5F, 1, (float) -cool / cooldown + 1);
            }
        }
    }
    
    public Vec2 mousePos(GameContainer container) {
        Input input = container.getInput();
        return new Vec2(camera.x - Game.width / 2 / camera.zoom + input.getMouseX() / camera.zoom, camera.y + Game.height / 2 / camera.zoom
                - input.getMouseY() / camera.zoom);
    }
    
    public boolean[] getNeighbours(int x, int y) {
        boolean[] bool = new boolean[] { false, false, false, false, false, false, false, false };
        for (Segment s : skeleton.segments) {
            if (s.x + 1 == x && s.y + 1 == y) bool[0] = true;
            if (s.x + 1 == x && s.y == y) bool[1] = true;
            if (s.x + 1 == x && s.y - 1 == y) bool[2] = true;
            if (s.x == x && s.y - 1 == y) bool[3] = true;
            if (s.x - 1 == x && s.y - 1 == y) bool[4] = true;
            if (s.x - 1 == x && s.y == y) bool[5] = true;
            if (s.x - 1 == x && s.y + 1 == y) bool[6] = true;
            if (s.x == x && s.y + 1 == y) bool[7] = true;
        }
        return bool;
    }
    
    @Override
    public int getID() {
        return Game.EDITOR;
    }
    
}
