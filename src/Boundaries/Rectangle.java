
package Boundaries;

import Physics.Vector;
import std.StdDraw;

/**
 * Class describing the rectangular boundary problem for a billiard
 * @author cjcode975
 */
public class Rectangle extends Boundary{
    
    private boolean coll_x, coll_y;
    
    /**
     * Initialise a boundary rectangle that has dimension width x height
     * @param width width of the box
     * @param height height of the box
     */
    public Rectangle(double width, double height){
        bounds[0] = -width/2;
        bounds[1] = width/2;
        bounds[2] = -height/2;
        bounds[3] = height/2;
    }

    @Override
    public double Rewind_Time(Vector loc, Vector vel, double radius){
        if(coll_x){
            return (radius-bounds[1]+Math.abs(loc.get(0)))/Math.abs(vel.get(0));
        }
        
        return (radius-bounds[3]+Math.abs(loc.get(1)))/Math.abs(vel.get(1));
    }    
    
    @Override
    public Vector Normal(Vector loc) {
        double normal[] = new double[2];
        
        if(coll_x){
            normal[0] = loc.get(0)<0 ? 1 : -1;
        }
        
        if(coll_y){
            normal[1] = loc.get(1)<0 ? 1 : -1;
        }
        
        return (new Vector(normal)).unit();
    }
    
    @Override
    public boolean OutOfBounds(Vector loc, double radius) {
        coll_x = (bounds[1]-Math.abs(loc.get(0)))<radius;
        coll_y = (bounds[3]-Math.abs(loc.get(1)))<radius;
        return coll_x | coll_y;
    }

    @Override
    public void Draw() {
        StdDraw.rectangle(bounds[1]/2.0, bounds[3]/2.0, bounds[1]/2.0, bounds[3]/2.0);
    }

    public String toString(){
        return "Rectangle, "+(2*bounds[1])+", "+(2*bounds[3]);
    }
}
