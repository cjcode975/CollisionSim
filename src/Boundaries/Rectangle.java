/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Boundaries;

import Physics.Vector;
import std.StdDraw;

/**
 *
 * @author My Laptop
 */
public class Rectangle extends Boundary{
    
    /**
     * Initialise a boundary rectangle that has dimension width x height
     * @param width width of the box
     * @param height height of the box
     */
    public Rectangle(double width, double height){
        bounds[1] = width;
        bounds[3] = height;
    }

    /**
     *
     * @param loc
     * @param vel
     * @param radius
     * @return
     */
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
    
    /**
     * Get the Vector normal to the surface of the boundary at a point
     * @param loc position on boundary
     * @param radius
     * @return normal vector for the surface
     */
    @Override
    public Vector Normal(Vector loc, double radius) {
        double normal[] = new double[2];
        
        if(loc.get(0)<radius){
            normal[0] = 1;
        }
        else if(loc.get(0)>bounds[1]-radius){
            normal[0]=-1;
        }
        
        if(loc.get(1)<radius){
            normal[1] = 1;            
        }
        else if(loc.get(1)>bounds[3]-radius){
            normal[1] = -1;
        }
        return (new Vector(normal)).unit();
    }

    /**
     * Check if a ball is outside bounds or in contact with the boundary wall 
     * @param loc centre of the ball
     * @param radius radius of the ball
     * @return if the boundary is breached
     */
    @Override
    public boolean OutOfBounds(Vector loc, double radius) {
        if(loc.get(0)<radius || loc.get(0)>bounds[1]-radius || loc.get(1)<radius || loc.get(1)>bounds[3]-radius){
            return true;
        }
        return false;
    }

    /**
     * Draw the boundary
     */
    @Override
    public void Draw() {
        StdDraw.rectangle(bounds[1]/2.0, bounds[3]/2.0, bounds[1]/2.0, bounds[3]/2.0);
    }

    
}
