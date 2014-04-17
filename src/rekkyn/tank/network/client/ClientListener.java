package rekkyn.tank.network.client;

import rekkyn.tank.Game;
import rekkyn.tank.network.NetworkManager.LoginResult;

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
        if (o instanceof LoginResult) {
            LoginResult lr = (LoginResult) o;
            if (lr.result) {
                System.out.println("[CLIENT] Connected successfully.");
            } else {
                System.out.println("[CLIENT] Connection failed: " + lr.reason);
            }
        }
    }
}
