package rekkyn.tank;

public class Camera {
    public float zoom = 20;
    public float x = 0;
    public float y = 0;
    public Entity following;
    
    public void setFollowing(Entity e) {
        following = e;
    }
    
    public void update() {
        if (following != null) {
            x = following.body.getWorldCenter().x;
            y = following.body.getWorldCenter().y;
        }
    }
}
