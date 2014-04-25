package rekkyn.tank.network.client;

import rekkyn.tank.GameWorld;
import rekkyn.tank.network.NetworkManager.AddEntity;
import rekkyn.tank.network.NetworkManager.AddUser;
import rekkyn.tank.network.NetworkManager.EntityData;
import rekkyn.tank.network.NetworkManager.LoginResult;
import rekkyn.tank.network.NetworkManager.RemoveUser;

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
        } else if(o instanceof AddUser) {
            String name = ((AddUser) o).user.name;
            System.out.println("[CLIENT] " + name + " logged in.");
        } else if (o instanceof RemoveUser) {
            String name = ((RemoveUser) o).user.name;
            System.out.println("[CLIENT] " + name + " disconnected.");
        } else if (o instanceof AddEntity) {
            try {
                world.process.put(o);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        } else if (o instanceof EntityData) {
            try {
                world.process.put(o);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }
}
