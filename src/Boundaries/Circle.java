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
 *
 * @author My Laptop
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
        double c = loc.dot(loc) - (rad-radius)*(rad-radius);
        double b = -2*loc.dot(vel);
        double a = vel.dot(vel);
        
        ArrayList<Double> troots = Formulae.QuadraticRealRoots(a, b, c);
        double tcorrec = 0;
        if(troots.size()==1){
            tcorrec = troots.get(0);
        }
        else{
            if(troots.get(0)>0 && troots.get(1)>0){
                tcorrec = Math.min(troots.get(0), troots.get(1));
            }
            else{
                tcorrec = Math.max(troots.get(0), troots.get(1));
            }
        }
        if(tcorrec<0){
            throw new IllegalStateException("Correction time calculated as negative");
        }
        
        return tcorrec;
    }
    
    @Override
    public Vector Normal(Vector loc, double radius) {        
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
