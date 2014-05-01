package rekkyn.tank;

import java.util.Scanner;

import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import rekkyn.tank.network.client.GameClient;
import rekkyn.tank.network.server.GameServer;

public class Menu extends BasicGameState {
    
    Scanner s = new Scanner(System.in);
    
    public Menu() throws SlickException {}
    
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {}
    
    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        System.out.println("Enter 0 for server.\nEnter 1 for client.\nEnter 3 for editor.");

        int i = s.nextInt();
        if (i == 3) {
            game.enterState(Game.EDITOR);
            return;
        }
        if (i == 0) {
            System.out.println("Please enter your username:");
            String name = s.next();
            GameServer server = new GameServer(name, ((Game) game).world);
        } else if (i == 1) {
            System.out.println("Please enter your username:");
            String name = s.next();
            GameClient client = new GameClient(name, ((Game) game).world);
        }
        game.enterState(Game.WORLD);
    }
    
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {}
    
    @Override
    public int getID() {
        return Game.MENU;
    }
    
}
