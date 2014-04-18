package rekkyn.tank.network.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rekkyn.tank.network.NetworkManager;
import rekkyn.tank.network.User;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

public class GameServer {
    public Server server;
    
    public static ServerListener listener;
    
    public List<User> users = new ArrayList<User>();
    
    public GameServer() {
        server = new Server() {
            @Override
            protected Connection newConnection() {
                // By providing our own connection implementation, we can store
                // per
                // connection state without a connection ID to state look up.
                return new UserConnection();
            }
        };
        
        NetworkManager.register(server);
        
        listener = new ServerListener(server);
        server.addListener(listener);
        
        try {
            server.bind(NetworkManager.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.start();
    }
    
    public class UserConnection extends Connection {
        public User user;
    }
}
