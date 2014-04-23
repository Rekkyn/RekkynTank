package rekkyn.tank;

import java.util.Scanner;

import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import rekkyn.tank.network.client.GameClient;
import rekkyn.tank.network.server.GameServer;

public class Menu extends BasicGameState {
    
    Scanner s = new Scanner(System.in);
    
    public Menu() throws SlickException {
    }
    
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
    }
    
    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        int i = s.nextInt();
        if (i == 0) {
            GameServer server = new GameServer(((Game) game).world);
        } else if (i == 1) {
            GameClient client = new GameClient("Rekkyn", ((Game) game).world);
        }
        game.enterState(Game.WORLD);
    }
    
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        
        
    }
    
    @Override
    public int getID() {
        return Game.MENU;
    }
    
}
