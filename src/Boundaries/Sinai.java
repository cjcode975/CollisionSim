/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Boundaries;

import Physics.Formulae;
import Physics.Vector;
import java.util.ArrayList;
import std.StdDraw;

/**
 * Class describing the boundary for a Sinai billiard - square billiard with a 
 * central hole
 * @author cjcode975
 */
public class Sinai extends Boundary{
    
    //Dimensions - radius of hole, half-side-length of box
    private double rad, size;
        
    //Keep track of where the collision is happening
    private boolean collide_with_circle, coll_x, coll_y;
    
    public Sinai(double radius, double width){
        if(radius<=0 || width<=0){
            throw new IllegalArgumentException("All dimensions must be greater than zero");
        }
        if(radius >= width/2.0){
            throw new IllegalArgumentException("Central hole must be smaller than the box");
        }
        
        rad = radius;
        size = width/2;
        
        bounds[0] = -size;
        bounds[1] = size;
        bounds[2] = -size;
        bounds[3] = size;
    }

    @Override
    public Vector Normal(Vector loc) {
        double normal[] = new double[2];
        if(collide_with_circle){
            return loc.unit();
        }
        else if(coll_x){
            normal[0] = loc.get(0)<0 ? 1 : -1;
        }
        else if(coll_y){
            normal[1] = loc.get(0)<0 ? 1 : -1;
        }
        return new Vector(normal);
    }

    @Override
    public boolean OutOfBounds(Vector loc, double radius) {
        if(loc.dot(loc)<=(rad+radius)*(rad+radius)){
            collide_with_circle = true;
            return true;
        }
        else if(size-Math.abs(loc.get(0))<=radius){
            coll_x = true;
            coll_y = false;
            collide_with_circle = false;
            return true;            
        }
        else if(size-Math.abs(loc.get(1))<=radius){
            coll_x = false;
            coll_y = true;
            collide_with_circle = false;
            return true;
        }
        
        return false;
    }

    @Override
    public double Rewind_Time(Vector loc, Vector vel, double radius) {
        if(collide_with_circle){
            return Formulae.whenCirclesIntersected(loc, new Vector(2), vel, new Vector(2), radius, rad);
        }
        
        if(size-Math.abs(loc.get(0))<=radius){
            return (radius-size+Math.abs(loc.get(0)))/Math.abs(vel.get(0));
        }
        
        return (radius-size+Math.abs(loc.get(1)))/Math.abs(vel.get(1));
    }

    @Override
    public void Draw() {
        StdDraw.circle(0, 0, rad);
        StdDraw.square(0, 0, size);
    }
    
}
