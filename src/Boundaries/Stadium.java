package Boundaries;

import Physics.Formulae;
import Physics.Vector;
import std.StdDraw;

/**
 * Class describing the boundary for the stadium billiard, a rectangle capped by 
 * a semicircle on the horizontal sides.
 * 
 * @author cjcode975
 */
public class Stadium extends Boundary{
    
    private double rad, wid;
    
    private boolean cc, cbw, ctw;
    
    private Vector cl, cr;
    
    public Stadium(double radius, double box_width){
        rad = radius;
        
        wid = box_width/2.0;
        
        double centrepoints[] = {-wid,0};
        cl = new Vector(centrepoints);
        centrepoints[0] = wid;
        cr = new Vector(centrepoints);
        
        bounds[0] = -wid-rad;
        bounds[1] = wid+rad;
        bounds[2] = -rad;
        bounds[3] = rad;
    }

    @Override
    public Vector Normal(Vector loc) {
        if(cbw){
            double temp[] = {0,1};
            return new Vector(temp);
        }
        else if(ctw){
            double temp[] = {0,-1};
            return new Vector(temp);
        }
        return loc.sub(loc.get(0)<0?cl:cr).unit().scale(-1);
    }

    @Override
    public boolean OutOfBounds(Vector loc, double radius) {
        cc = false;
        cbw = false;
        ctw = false;
        if(Math.abs(loc.get(0))<wid){ //collide with the straight walls
            cbw = rad-Math.abs(loc.get(1))<radius & loc.get(1)<0;
            ctw = rad-Math.abs(loc.get(1))<radius & loc.get(1)>0;
        }
        else if(loc.sub(loc.get(0)<0?cl:cr).magnitude()>rad-radius){ //collide with one of the circular sections
            cc = true;
        }
        return cc | cbw | ctw;
    }

    @Override
    public double Rewind_Time(Vector loc, Vector vel, double radius) {
        //Collide with the circular parts
        if(cc){
            Vector loc_prime = loc.sub(loc.get(0)<0?cl:cr);
            return Formulae.whenCirclesIntersected(loc_prime, new Vector(2), vel, new Vector(2), -radius, rad);
        }       
        
        //Collide with the straight walls
        return (radius-rad+Math.abs(loc.get(1)))/Math.abs(vel.get(1));
    }

    @Override
    public void Draw() {
        StdDraw.arc(-wid, 0, rad, 90, 270);
        StdDraw.arc(wid, 0, rad, -90, 90);
        StdDraw.line(-wid, rad, wid, rad);
        StdDraw.line(-wid, -rad, wid, -rad);
    }
    
    /**
     * Describe the boundary as a String
     * @return 
     */
    public String toString(){
        return "Stadium, "+rad+", "+(2*wid);
    }
}
