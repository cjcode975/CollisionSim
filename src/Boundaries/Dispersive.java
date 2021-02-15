
package Boundaries;

import Physics.Formulae;
import Physics.Vector;
import std.StdDraw;

/**
 * Class describing a dispersive billiard formed of the sub-diamond area enclosed 
 * by four circles centred at each corner of a square and with radii equal to 
 * half the side-length of the square. The outside of a 90o arc of a circle
 * makes up each side of the diamond.
 * 
 * @author cjcode975
 */
public class Dispersive extends Boundary {
    
    private double rad;    
    
    private Vector centrepoints[][] = new Vector[2][2];
    
    public Dispersive(double radius){
        rad = radius;
        bounds[0] = -rad;
        bounds[1] = rad;
        bounds[2] = -rad;
        bounds[3] = rad;
        
        for(int i=0; i<=1; i++){
            for(int j=0; j<=1; j++){
                double dist[] = {rad*(2*i-1),rad*(2*j-1)};
                centrepoints[i][j] = new Vector(dist);
            }
        }
    }

    @Override
    public Vector Normal(Vector loc) {        
        return loc.sub(centrepoints[loc.get(0)<0?0:1][loc.get(1)<0?0:1]).unit();
    }

    @Override
    public boolean OutOfBounds(Vector loc, double radius) {
        Vector y = loc.sub(centrepoints[loc.get(0)<0?0:1][loc.get(1)<0?0:1]);
        
        return y.dot(y)<(rad+radius)*(rad+radius);
    }

    @Override
    public double Rewind_Time(Vector loc, Vector vel, double radius) {
        return Formulae.whenCirclesIntersected(loc, centrepoints[loc.get(0)<0?0:1][loc.get(1)<0?0:1], vel, new Vector(2), radius, rad);
    }

    @Override
    public void Draw() {
        StdDraw.arc(rad, rad, rad, 180, 270);
        StdDraw.arc(rad, -rad, rad, 90, 180);
        StdDraw.arc(-rad, -rad, rad, 0, 90);
        StdDraw.arc(-rad, rad, rad, 270, 360);
    }
    
    /**
     * String description of boundary
     * @return boundary type name, radius
     */
    public String toString(){
        return "Dispersive, "+rad;
    }
    
}
