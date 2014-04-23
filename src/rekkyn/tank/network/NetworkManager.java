package rekkyn.tank.network;

import java.util.ArrayList;

import org.jbox2d.common.Vec2;

import rekkyn.tank.Creature;
import rekkyn.tank.skeleton.*;
import rekkyn.tank.skeleton.Skeleton.ElementType;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class NetworkManager {
    
    public static final int port = 54555;
    
    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.setReferences(true);
        kryo.register(Login.class);
        kryo.register(LoginResult.class);
        kryo.register(User.class);
        kryo.register(AddUser.class);
        kryo.register(AddEntity.class);
        kryo.register(EntityData.class);
        kryo.register(Object[].class);
        kryo.register(Skeleton.class);
        kryo.register(Creature.class);
        kryo.register(ArrayList.class);
        kryo.register(Heart.class);
        kryo.register(Element[].class);
        kryo.register(Element.class);
        kryo.register(Segment.class);
        kryo.register(Motor.class);
        kryo.register(ElementType.class);
        kryo.register(EntityType.class);
        kryo.register(Vec2.class);
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
    
    public static class AddEntity {
        public EntityData data;
    }
    
    
    public static class EntityData {
        public EntityType type;
        
        public int id;
        
        public float x, y;
        public float angle;
        public Vec2 velocity;
        public boolean removed;
        public long ticksExisted;
        
        public Object[] data;
    }
    
    public enum EntityType {
        CREATURE,
        WALL;
    }
    
}
