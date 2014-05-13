package rekkyn.tank.network;

import rekkyn.tank.skeleton.Skeleton;

public class User {
    public String name;
    public Skeleton skeleton;
    
    public User() {}
    
    public User(String name, Skeleton skeleton) {
        this.name = name;
        this.skeleton = skeleton;
    }
}
