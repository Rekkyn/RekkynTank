package rekkyn.tank.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class NetworkManager {
    
    public static final int port = 54555;
    
    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(Login.class);
        kryo.register(LoginResult.class);
        kryo.register(User.class);
        kryo.register(AddUser.class);
    }
    
    public static class Login {
        public String name;
    }
    
    public static class LoginResult {
        public boolean result;
        public String reason;
    }
    
    public static class AddUser {
        public User user;
    }
    
}
