
package Boundaries;

import Physics.Formulae;
import Physics.Vector;
import std.StdDraw;

/**
 * Class to describe the 'mushroom' billiard boundary
 * TO DO: Fix corner maths
 * @author cjcode975
 */
public class Mushroom extends Boundary{
    
    double rad, wid, height;
    
    boolean hit_circ, hit_circ_base, hit_base, hit_sides;
    
    public Mushroom(double radius, double stalk_width, double stalk_height){
        if(stalk_width>=2*radius){
            throw new IllegalArgumentException("Stalk must be narrower than the cap");
        }
        if(radius<=0 || stalk_width<=0 || stalk_height<=0){
            throw new IllegalArgumentException("All dimensions must be greater than zero");
        }
        
        rad = radius;
        wid = stalk_width/2.0;
        height = stalk_height;
        
        bounds[0] = -rad;
        bounds[1] = rad;
        bounds[2] = -stalk_height;
        bounds[3] = rad;
    }

    @Override
    public Vector Normal(Vector loc) {
        double normal[] = new double[2];
        if(hit_circ_base || hit_base){ 
            normal[1] = 1;
            return new Vector(normal);
        }
        else if(hit_sides){
            normal[0] = loc.get(0)<0?1:-1;
            return new Vector(normal);
        }
        return loc.unit().scale(-1);
    }

    @Override
    public boolean OutOfBounds(Vector loc, double radius) {
        hit_circ = false;
        hit_circ_base = false;
        hit_sides = false;
        hit_base = false;
        
        if(loc.get(1)>0){
            if(Math.abs(loc.get(0))>=wid && loc.get(1)<radius){ // hit the base of the cap
                hit_circ_base = true;
            }
            else if(loc.dot(loc)>(rad-radius)*(rad-radius)){ //hit the top of the cap
                hit_circ = true;
            }
        }        
        else{
            hit_sides = wid-Math.abs(loc.get(0))<radius; //hit the side of the stalk
            hit_base = height+loc.get(1)<radius; //hit the base of the stalk
        }
        
        return hit_circ | hit_circ_base | hit_sides | hit_base;
    }

    @Override
    public double Rewind_Time(Vector loc, Vector vel, double radius) {
        if(hit_circ){
            return Formulae.whenCirclesIntersected(loc, new Vector(2), vel, new Vector(2), -radius, rad);
        }
        else if(hit_sides){
            return (radius-wid+Math.abs(loc.get(0)))/Math.abs(vel.get(0));
        }
        else if(hit_circ_base){
            return (radius-loc.get(1))/Math.abs(vel.get(1));
        }
        return -1*(radius-height-loc.get(1))/vel.get(1);
        
    }

    @Override
    public void Draw() {
        StdDraw.arc(0, 0, rad, 0, 180);
        StdDraw.line(-rad, 0, -wid, 0);
        StdDraw.line(-wid, 0, -wid, -height);
        StdDraw.line(-wid, -height, wid, -height);
        StdDraw.line(wid, 0, wid, -height);
        StdDraw.line(rad, 0, wid, 0);
    }
    
}
