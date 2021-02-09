
package Boundaries;

import Physics.Vector;
import std.StdDraw;

/**
 * Class describing the rectangular boundary problem for a billiard
 * @author cjcode975
 */
public class Rectangle extends Boundary{
    
    private boolean clw, crw, cbw, ctw;
    
    /**
     * Initialise a boundary rectangle that has dimension width x height
     * @param width width of the box
     * @param height height of the box
     */
    public Rectangle(double width, double height){
        bounds[1] = width;
        bounds[3] = height;
    }

    @Override
    public double Rewind_Time(Vector loc, Vector vel, double radius){
        if(loc.get(0)<radius){ //left wall
            return -1*(radius-loc.get(0))/vel.get(0);
        }
        else if(loc.get(0)>bounds[1]-radius){ //right wall
            return (radius-bounds[1]+loc.get(0))/vel.get(0);
        }
        if(loc.get(1)<radius){ //bottom wall
            return -1*(radius-loc.get(1))/vel.get(1);
        }
        else{ //top wall
            return (radius-bounds[3]+loc.get(1))/vel.get(1);
        }
    }    
    
    @Override
    public Vector Normal(Vector loc) {
        double normal[] = new double[2];
        
        if(clw){
            normal[0] = 1;
        }
        else if(crw){
            normal[0]=-1;
        }
        
        if(cbw){
            normal[1] = 1;            
        }
        else if(ctw){
            normal[1] = -1;
        }
        return (new Vector(normal)).unit();
    }
    
    @Override
    public boolean OutOfBounds(Vector loc, double radius) {
        clw = loc.get(0)<radius;
        crw = (bounds[1]-loc.get(0))<radius;
        cbw = loc.get(1)<radius;
        ctw = (bounds[3]-loc.get(1))<radius;
        return clw | crw | cbw | ctw;
    }

    @Override
    public void Draw() {
        StdDraw.rectangle(bounds[1]/2.0, bounds[3]/2.0, bounds[1]/2.0, bounds[3]/2.0);
    }

    
}
