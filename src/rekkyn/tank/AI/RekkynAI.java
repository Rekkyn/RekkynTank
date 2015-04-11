package rekkyn.tank.AI;

public class RekkynAI implements CustomAI {
    
    float relX, relY;
    float velX, velY;
    float angV;
    float lPow, rPow;
    float relAngle;
    
    @Override
    public float[] getOutput(float[] inputs) {
        
        relX = inputs[0];
        relY = inputs[1];
        velX = inputs[2];
        velY = inputs[3];
        angV = inputs[4];
        lPow = inputs[5];
        rPow = inputs[6];
        relAngle = inputs[7];
        
        /*float height = 80;
        
        float power = (float) (-(1 - powerToHoverAtHeight(0)) / height * relY + 1 - powerToHoverAtHeight(0));
        power *= 100 * 100 / Math.pow(relY + 100, 2);
        
        power += powerToHoverAtHeight(relY);
        
        return new float[] { power, power };*/
        
        float power = 1;
        
        if (relY < 200)
            power = 1;
        else if (velY > 0)
            power = -velY / 20;
        else if (velY < 0) power = -velY / 20;
        
        return new float[] { power, power };
    }
    
    public double powerToHoverAtHeight(float h) {
        return -1.845706169e-9 * Math.pow(h, 4) + 5.624403569e-8 * Math.pow(h, 3) + 8.563650837e-5 * Math.pow(h, 2) - 1.346165547e-2 * h
                + 8.427390074e-1;
    }
}
