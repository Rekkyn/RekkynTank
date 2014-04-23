package rekkyn.tank.network.server;

import java.util.*;

import rekkyn.tank.Entity;
import rekkyn.tank.GameWorld;
import rekkyn.tank.network.NetworkManager.AddUser;
import rekkyn.tank.network.NetworkManager.Login;
import rekkyn.tank.network.NetworkManager.LoginResult;
import rekkyn.tank.network.NetworkManager.RemoveUser;
import rekkyn.tank.network.NetworkManager.SendInput;
import rekkyn.tank.network.*;
import rekkyn.tank.network.server.GameServer.UserConnection;

import com.esotericsoftware.kryonet.*;

public class ServerListener extends Listener {
    
    public Server server;
    GameServer gameServer;
    public GameWorld world;
    
    HashSet<User> loggedIn = new HashSet<User>();
    
    public ServerListener(Server server, GameServer gameServer, GameWorld world) {
        this.server = server;
        this.gameServer = gameServer;
        this.world = world;
    }
    
    @Override
    public void connected(Connection c) {}
    
    @Override
    public void disconnected(Connection c) {
        UserConnection connection = (UserConnection) c;
        User user = connection.user;
        logOut(connection, user);
    }
    
    @Override
    public void received(Connection c, Object o) {
        UserConnection connection = (UserConnection) c;
        User user = connection.user;
        if (o instanceof Login) {
            
            LoginResult lr = new LoginResult();
            lr.result = true;
            
            // Ignore if already logged in.
            if (user != null) {
                lr.result = false;
                lr.reason = "You are already logged in.";
                c.sendTCP(lr);
                return;
            }
            
            String name = ((Login) o).name;
            
            // Reject if already logged in.
            for (User other : loggedIn) {
                if (other.name.equals(name)) {
                    lr.result = false;
                    lr.reason = "There is already a user with your name.";
                    c.sendTCP(lr);
                    c.close();
                    return;
                }
            }
            
            user = new User(name);
            
            loggedIn(connection, user);
            c.sendTCP(lr);
            return;
        } else if (o instanceof SendInput) {
            System.out.println("SEEEND IIINPUT");
            SendInput sendInput = (SendInput) o;
            sendInput.user = user;
            try {
                world.inputs.put(sendInput);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void loggedIn(UserConnection c, User user) {
        c.user = user;
        
        // Add existing users to new logged in connection.
        for (User other : loggedIn) {
            AddUser addUser = new AddUser();
            addUser.user = other;
            c.sendTCP(addUser);
        }
        
        loggedIn.add(user);
        
        // Add logged in character to all connections.
        AddUser addUser = new AddUser();
        addUser.user = user;
        server.sendToAllTCP(addUser);
        world.addPlayer(user);
        
        System.out.println("[SERVER] " + user.name + " logged in.");
        
        Iterator it = world.entities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            
            Entity e = (Entity) pairs.getValue();
            c.sendTCP(gameServer.addEntity(e));
        }
    }
    
    private void logOut(UserConnection c, User user) {
        
        loggedIn.remove(user);
        
        RemoveUser removeUser = new RemoveUser();
        removeUser.user = user;
        server.sendToAllTCP(removeUser);
        
        System.out.println("[SERVER] " + user.name + " disconnected.");
    }
}
