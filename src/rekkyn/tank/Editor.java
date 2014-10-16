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
    public boolean symmetry = true;
    
    public Map<int[], Boolean> connected = new HashMap<int[], Boolean>();
    
    public Editor(Skeleton skeleton) {
        this.skeleton = skeleton;
    }
    
    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        camera.zoom = 30;
        checkConnections();
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
        if (input.isKeyPressed(Input.KEY_ESCAPE)) {
            game.enterState(Game.MENU);
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
        if (input.isKeyPressed(Input.KEY_4)) {
            selected = 4;
        }
        
        if (input.isKeyPressed(Input.KEY_M)) {
            symmetry = !symmetry;
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
        checkConnections();
    }
    
    @Override
    public void mouseWheelMoved(int change) {
        camera.zoom *= 1F + 0.002 * change / 120F;
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
        
        if (camera.zoom > 40) {
            Color c1 = Colours.getDark();
            c1.a = 0.2F / 30 * (camera.zoom - 40);
            g.setColor(c1);
            for (float i = -gridSize - 1; i <= gridSize; i += 0.5F) {
                g.drawLine(-f + (i - 1) / root2, -f - (i + 1) / root2, f + i / root2, f - i / root2);
            }
            for (float i = -gridSize - 1; i <= gridSize; i += 0.5F) {
                g.drawLine(-f + (i - 1) / root2, f + (i + 1) / root2, f + i / root2, -f + i / root2);
            }
        }
        g.popTransform();
        
        g.rotate(0, 0, -45);
        
        g.pushTransform();
        float dist = (float) (1 / Math.sqrt(8));
        g.translate(0, dist);
        
        // shadows
        for (Segment s : skeleton.segments) {
            g.setColor(Colours.getShadow());
            if (getConnectedAt(s.x, s.y)) { // not implemented at the moment
                g.fillRect(s.x - 0.5F, -s.y - 0.5F, 1, 1);
            }
        }
        
        drawCompletingSegments(g);
        g.popTransform();
        
        // body
        for (Segment s : skeleton.segments) {
            Color col;
            if (s instanceof Heart) {
                col = Colours.getAccent();
            } else {
                col = Colours.getBody();
            }
            if (!getConnectedAt(s.x, s.y)) { // not implemented at the moment
                col.a = 0.25F;
            }
            g.setColor(col);
            g.fillRect(s.x - 0.5F, -s.y - 0.5F, 1, 1);
            g.pushTransform();
            g.translate(s.x, -s.y);
            s.render(g);
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
        
        Segment segment = skeleton.getSegment(mouseX, mouseY);
        
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
                if (mouseX != mouseY && symmetry) g.drawRect(mouseY - 0.5F, -mouseX - 0.5F, 1, 1);
            }
            
            // remove segment
            if (overSegment && input.isMousePressed(Input.MOUSE_RIGHT_BUTTON) && !(segment instanceof Heart)) {
                cooldowns.put(new Segment(mouseX, mouseY, skeleton), new Object[] { 0, false });
                if (mouseX != mouseY && symmetry) cooldowns.put(new Segment(mouseY, mouseX, skeleton), new Object[] { 0, false });
                skeleton.removeSegment(mouseX, mouseY);
            }
            
            // draw posible segment
            if (!overSegment
                    && (neighbours[0] || neighbours[1] || neighbours[2] || neighbours[3] || neighbours[4] || neighbours[5] || neighbours[6] || neighbours[7])) {
                Color col = Colours.getBody();
                col.a = 0.5F;
                g.setColor(col);
                g.fillRect(mouseX - 0.5F, -mouseY - 0.5F, 1, 1);
                if (mouseX != mouseY && symmetry) g.fillRect(mouseY - 0.5F, -mouseX - 0.5F, 1, 1);
                
                if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
                    cooldowns.put(new Segment(mouseX, mouseY, skeleton), new Object[] { cooldown, true });
                    if (mouseX != mouseY && symmetry)
                        cooldowns.put(new Segment(mouseY, mouseX, skeleton), new Object[] { cooldown, true });
                }
            }
        } else if (selected == 2) {
            Motor motor = new Motor();
            if (overSegment && !(segment instanceof Heart)) {
                if (segment.elements[8] instanceof BlankElement) {
                    g.pushTransform();
                    g.translate(mouseX, -mouseY);
                    motor.render(g);
                    g.popTransform();
                    
                    if (mouseX != mouseY && symmetry) {
                        g.pushTransform();
                        g.translate(mouseY, -mouseX);
                        motor.render(g);
                        g.popTransform();
                    }
                    
                    if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
                        segment.addMotor(symmetry);
                    }
                } else {
                    g.setColor(Colours.getAccent());
                    g.setLineWidth(2);
                    g.drawRect(mouseX - 0.25F, -mouseY - 0.25F, 0.5F, 0.5F);
                    if (mouseX != mouseY && symmetry) g.drawRect(mouseY - 0.25F, -mouseX - 0.25F, 0.5F, 0.5F);
                    if (input.isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {
                        segment.removeElement(8);
                        if (mouseX != mouseY && symmetry) skeleton.getSegment(mouseY, mouseX).removeElement(8);
                    }
                }
            } else {
                g.pushTransform();
                g.translate(mouse.x, -mouse.y);
                motor.render(g);
                g.popTransform();
            }
        } else if (overSegment && !(segment instanceof Heart) && (selected == 3 || selected == 4)) {
            float angle = (float) Math.atan2(mouse.y - mouseY, mouse.x - mouseX);
            int pos = 0;
            
            final float angle1 = (float) Math.atan(0.5F);
            final float angle2 = (float) Math.atan(2F);
            final float angle3 = (float) (Math.PI - Math.atan(2F));
            final float angle4 = (float) (Math.PI - Math.atan(0.5F));
            final float angle5 = (float) Math.atan(-0.5F);
            final float angle6 = (float) Math.atan(-2F);
            final float angle7 = (float) (Math.atan(2F) - Math.PI);
            final float angle8 = (float) (Math.atan(0.5F) - Math.PI);
            
            if (angle > angle1 && angle < angle2) {
                pos = 0;
            }
            if (angle < angle3 && angle > angle2) {
                pos = 7;
            }
            if (angle > angle3 && angle < angle4) {
                pos = 6;
            }
            if (angle < angle1 && angle > angle5) {
                pos = 1;
            }
            if (angle > angle6 && angle < angle5) {
                pos = 2;
            }
            if (angle < angle6 && angle > angle7) {
                pos = 3;
            }
            if (angle > angle8 && angle < angle7) {
                pos = 4;
            }
            if (angle > angle4 || angle < angle8) {
                pos = 5;
            }
            
            boolean mirror = skeleton.shouldMirror(mouseX, mouseY, pos);
            int[][] locations = skeleton.getLocationsToAdd(mouseX, mouseY, pos, angle);
            
            Element e = null;
            if (selected == 3) e = new Mouth();
            if (selected == 4) e = new Eye();
            
            if (locations != null) {
                Color col = Util.copyColor(e.colour);
                col.a = 0.5F;
                g.setColor(col);
                g.pushTransform();
                g.translate(locations[0][0], -locations[0][1]);
                skeleton.getSegment(locations[0][0], locations[0][1]).renderElement(locations[0][2], g);
                g.popTransform();
                g.pushTransform();
                g.translate(locations[1][0], -locations[1][1]);
                skeleton.getSegment(locations[1][0], locations[1][1]).renderElement(locations[1][2], g);
                g.popTransform();
                
                if (mirror && symmetry) {
                    g.pushTransform();
                    g.translate(locations[0][1], -locations[0][0]);
                    skeleton.getSegment(locations[0][1], locations[0][0]).renderElement(7 - locations[0][2], g);
                    g.popTransform();
                    g.pushTransform();
                    g.translate(locations[1][1], -locations[1][0]);
                    skeleton.getSegment(locations[1][1], locations[1][0]).renderElement(7 - locations[1][2], g);
                    g.popTransform();
                }
                
                if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
                    skeleton.addElementAtLocation(e, locations, mirror && symmetry);
                }
            }
            
            if (input.isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {
                skeleton.removeElement(mouseX, mouseY, pos, angle, mirror && symmetry);
            }
        }
        g.popTransform();
    }
    
    private void checkConnections() {
        connected.clear();
        connected.put(new int[] { 0, 0 }, true);
        
        boolean go;
        do {
            go = false;
            for (Segment s : skeleton.segments) {
                boolean skip = false;
                Iterator<Entry<int[], Boolean>> it = connected.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<int[], Boolean> pairs = it.next();
                    int[] i = pairs.getKey();
                    if (s.x == i[0] && s.y == i[1]) {
                        skip = pairs.getValue();
                    }
                }
                if (skip) continue;
                
                int[][] pos = new int[][] { { s.x + 1, s.y }, { s.x + 1, s.y + 1 }, { s.x, s.y + 1 }, { s.x - 1, s.y + 1 },
                        { s.x - 1, s.y }, { s.x - 1, s.y - 1 }, { s.x, s.y - 1 }, { s.x + 1, s.y - 1 } };
                boolean isConnected = false;
                for (int i = 0; i < 8; i++) {
                    int x = pos[i][0];
                    int y = pos[i][1];
                    if (skeleton.getSegment(x, y) != null && getConnectedAt(x, y)) {
                        isConnected = true;
                        go = true;
                        break;
                    }
                }
                Iterator<Entry<int[], Boolean>> it1 = connected.entrySet().iterator();
                while (it1.hasNext()) {
                    Entry<int[], Boolean> pairs = it1.next();
                    int[] i = pairs.getKey();
                    if (s.x == i[0] && s.y == i[1]) it1.remove();
                }
                connected.put(new int[] { s.x, s.y }, isConnected);
            }
        } while (go);
        
        Iterator<Entry<int[], Boolean>> it1 = connected.entrySet().iterator();
        while (it1.hasNext()) {
            Entry<int[], Boolean> pairs = it1.next();
            int[] i = pairs.getKey();
            if (!pairs.getValue()) {
                skeleton.removeSegment(i[0], i[1]);
            }
        }
    }
    
    private boolean getConnectedAt(int x, int y) {
        Iterator<Entry<int[], Boolean>> it = connected.entrySet().iterator();
        while (it.hasNext()) {
            Entry<int[], Boolean> pairs = it.next();
            int[] i = pairs.getKey();
            if (x == i[0] && y == i[1]) return pairs.getValue();
        }
        return false;
    }
    
    public void drawCompletingSegments(Graphics g) {
        Iterator<Entry<Segment, Object[]>> it1 = cooldowns.entrySet().iterator();
        while (it1.hasNext()) {
            Entry<Segment, Object[]> pairs = it1.next();
            Segment s = pairs.getKey();
            Integer cool = (Integer) pairs.getValue()[0];
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
