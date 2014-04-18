package rekkyn.tank.network.client;

import rekkyn.tank.network.NetworkManager;
import rekkyn.tank.network.NetworkManager.Login;

import com.esotericsoftware.kryonet.Client;

public class GameClient {
    public Client client;
    
    public static ClientListener listener;
    
    public GameClient(String name) {
        client = new Client();
        client.start();
        
        NetworkManager.register(client);
        
        listener = new ClientListener();
        client.addListener(listener);
        
        try {
            client.connect(5000, "127.0.0.1", NetworkManager.port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Login login = new Login();
        login.name = name;
        client.sendTCP(login);
        
    }
}
