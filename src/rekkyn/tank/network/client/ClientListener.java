package rekkyn.tank.network.client;

import rekkyn.tank.*;
import rekkyn.tank.network.NetworkManager.AddEntity;
import rekkyn.tank.network.NetworkManager.EntityData;
import rekkyn.tank.network.NetworkManager.EntityType;
import rekkyn.tank.network.NetworkManager.LoginResult;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ClientListener extends Listener {
    
    public GameWorld world;
    
    public ClientListener(GameWorld world) {
        this.world = world;
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
        } else if (o instanceof AddEntity) {
            EntityData data = ((AddEntity) o).data;
            
            Entity e = null;
            
            if (data.type == EntityType.CREATURE) {
                e = new Creature(data.x, data.y, world);
            } else if (data.type == EntityType.WALL) {
                e = new Wall(data.x, data.y, world);
            }
            
            e.setData(data);
            
            world.add(e);

            System.out.println("[CLIENT] Add entity yo.");
        }
    }
}
