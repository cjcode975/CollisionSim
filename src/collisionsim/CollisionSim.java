/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collisionsim;

import Boundaries.Circle;
import Boundaries.Rectangle;

/**
 *
 * @author My Laptop
 */
public class CollisionSim {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Rectangle rect = new Rectangle(20,20);
        Circle circ = new Circle(12);
        Ball_Sim bb = new Ball_Sim(50,circ);
        bb.Set_Radii(0.25);
        bb.Set_Mass(true, 1);
        bb.Set_Locations();
        bb.Set_Speed(1.5);
        bb.Set_DT(0.05);
        bb.Simulate(1000);
    }
    
}
