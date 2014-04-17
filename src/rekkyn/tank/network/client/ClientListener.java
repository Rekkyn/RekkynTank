package rekkyn.tank.network.client;

import rekkyn.tank.Game;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ClientListener extends Listener {
    
    public Game game;
    
    public ClientListener() {
        // game = game;
    }
    
    @Override
    public void connected(Connection c) {}
    
    @Override
    public void disconnected(Connection c) {}
    
    @Override
    public void received(Connection c, Object o) {

    }
}
