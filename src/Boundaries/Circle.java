package Boundaries;

import Physics.Formulae;
import Physics.Vector;
import java.util.ArrayList;
import std.StdDraw;

/**
 * Class defining a circular boundary
 * @author cjcode975
 */
public class Circle extends Boundary{
    
    double rad;
    
    public Circle(double radius){
        bounds[0] = -radius;
        bounds[1] = radius;
        bounds[2] = -radius;
        bounds[3] = radius;
        
        rad = radius;
    }

    @Override
    public double Rewind_Time(Vector loc, Vector vel, double radius) {       
          return Formulae.whenCirclesIntersected(loc, new Vector(2), vel, new Vector(2), -radius, rad);
    }
    
    @Override
    public Vector Normal(Vector loc) {        
        return loc.unit().scale(-1);
    }

    @Override
    public boolean OutOfBounds(Vector loc, double radius) {
        return loc.magnitude()+radius > rad;
    }

    @Override
    public void Draw() {
        StdDraw.circle(0, 0, rad);
    }

    
}
