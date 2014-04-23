package rekkyn.tank.network.server;

import java.io.IOException;

import rekkyn.tank.Entity;
import rekkyn.tank.GameWorld;
import rekkyn.tank.network.*;
import rekkyn.tank.network.NetworkManager.AddEntity;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

public class GameServer {
    public Server server;
    public User host;
    public static ServerListener listener;
    
    public GameServer(String name, GameWorld world) {
        world.server = this;
        host = new User(name);
        
        server = new Server() {
            @Override
            protected Connection newConnection() {
                return new UserConnection();
            }
        };
        
        NetworkManager.register(server);
        
        listener = new ServerListener(server, this, world);
        server.addListener(listener);
        
        listener.loggedIn.add(host);
        world.addPlayer(host);
        
        try {
            server.bind(NetworkManager.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.start();
        
        world.initServer();
    }
    
    public class UserConnection extends Connection {
        public User user;
    }
    
    public void sendGameData(GameWorld gameWorld) {
        
    }
    
    public AddEntity addEntity(Entity e) {
        AddEntity addentity = new AddEntity();
        addentity.data = e.getData();
        e.sentData = addentity.data;
        
        return addentity;
    }
}
