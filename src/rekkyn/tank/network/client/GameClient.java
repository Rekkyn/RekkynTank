package rekkyn.tank.network.client;

import rekkyn.tank.GameWorld;
import rekkyn.tank.network.NetworkManager;
import rekkyn.tank.network.NetworkManager.Login;

import com.esotericsoftware.kryonet.Client;

public class GameClient {
    public Client client;
    
    public static ClientListener listener;
    
    public GameClient(String name, GameWorld world) {
        world.client = this;
        client = new Client(8192, 64 * 1024);
        client.start();
        
        NetworkManager.register(client);
        
        listener = new ClientListener(world);
        client.addListener(listener);
        
        try {
            client.connect(5000, "24.207.67.56", NetworkManager.port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Login login = new Login();
        login.name = name;
        client.sendTCP(login);
        
    }
}
