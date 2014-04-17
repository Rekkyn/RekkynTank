package rekkyn.tank.network.client;

import rekkyn.tank.network.*;
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
        
        Login login2 = new Login();
        User user2 = new User(name);
        login2.user = user2;
        login2.name = user2.name;
        client.sendTCP(login2); // server does not recieve this
        
        Login login = new Login();
        User user = new User(name);
        // login.user = user;
        login.name = user.name;
        client.sendTCP(login); // server receives this only when it is sent before the Login containing the User
        
    }
}
