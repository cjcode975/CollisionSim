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
public class Ellipse extends Boundary{
    
    double ra, rb;
    
    Vector crossing_point;
    
    public Ellipse(double width, double height){
        ra = width/2.0;
        rb = height/2.0;
        
        bounds[0] = -ra;
        bounds[1] = ra;
        bounds[2] = -rb;
        bounds[3] = rb;
    }

    @Override
    public Vector Normal(Vector loc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean OutOfBounds(Vector loc, double radius) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double Rewind_Time(Vector loc, Vector vel, double radius) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void Draw() {
        StdDraw.ellipse(0, 0, ra, rb);
    }
    
}
