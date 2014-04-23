package rekkyn.tank.network.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rekkyn.tank.*;
import rekkyn.tank.network.*;
import rekkyn.tank.network.NetworkManager.AddEntity;
import rekkyn.tank.network.NetworkManager.EntityData;
import rekkyn.tank.network.NetworkManager.EntityType;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

public class GameServer {
    public Server server;
    
    public static ServerListener listener;
    
    public List<User> users = new ArrayList<User>();
    
    public GameServer(GameWorld world) {
        world.server = this;
        server = new Server() {
            @Override
            protected Connection newConnection() {
                return new UserConnection();
            }
        };
        
        NetworkManager.register(server);
        
        listener = new ServerListener(server, this, world);
        server.addListener(listener);
        
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
        EntityData data = new EntityData();
        if (e instanceof Creature) {
            data.type = EntityType.CREATURE;
        } else if (e instanceof Wall) {
            data.type = EntityType.WALL;
        }
        
        data.id = e.id;
        data.x = e.x;
        data.y = e.y;
        data.angle = e.angle;
        data.velocity = e.velocity;
        data.removed = e.removed;
        data.data = e.getData();
        addentity.data = data;
        return addentity;
    }
}
