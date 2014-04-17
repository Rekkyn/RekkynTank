package rekkyn.tank.network.server;

import rekkyn.tank.network.NetworkManager.Login;
import rekkyn.tank.network.NetworkManager.LoginResult;

import com.esotericsoftware.kryonet.*;

public class ServerListener extends Listener {
    
    public Server server;
    
    public ServerListener(Server server) {
        this.server = server;
    }
    
    @Override
    public void connected(Connection c) {}
    
    @Override
    public void disconnected(Connection c) {}
    
    @Override
    public void received(Connection c, Object o) {
        System.out.println("recieved");
        if (o instanceof Login) {
            System.out.println("[SERVER] " + ((Login) o).name + " logged in.");
            LoginResult lr = new LoginResult();
            lr.result = true;
            c.sendTCP(lr);
        }
    }
}
