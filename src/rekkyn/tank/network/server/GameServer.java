package rekkyn.tank.network.server;

import java.io.IOException;

import rekkyn.tank.network.NetworkManager;

import com.esotericsoftware.kryonet.Server;

public class GameServer {
    public Server server;
    
    public static ServerListener listener;
    
    public GameServer() {
        System.out.println("SERVAH");
        server = new Server();
        
        NetworkManager.register(server);
        
        listener = new ServerListener();
        server.addListener(listener);
        
        try {
            server.bind(NetworkManager.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.start();
    }
}
