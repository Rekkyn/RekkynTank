package rekkyn.tank.network.server;

import rekkyn.tank.Game;
import rekkyn.tank.network.NetworkManager.Login;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ServerListener extends Listener {
    
    public Game game;
    
    public ServerListener() {
        // game = game;
    }
    
    @Override
    public void connected(Connection c) {}
    
    @Override
    public void disconnected(Connection c) {}
    
    @Override
    public void received(Connection c, Object o) {
        if (o instanceof Login) {
            System.out.println("[SERVER] " + ((Login) o).name + " logged in.");
        }
    }
}
